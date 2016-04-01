/*
 * Copyright 2016 LG Electronics Inc. All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lge.friendsCamera;


import com.lge.osclibrary.HttpAsyncTask;
import com.lge.osclibrary.OSCCommandsExecute;
import com.lge.osclibrary.OSCParameterNameMapper;
import com.lge.vr.VrUtils;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * List adapter for CameraFileListViewActivity and DownloadFileListViewActivity
 * Show thumbnail, file name and file size in row view
 * Get thumbnail from android media storage or
 * Get thumbnail from friends camera (by camera.getFile api)
 */
public class CustomListAdapter extends ArrayAdapter<HashMap<String, String>> {
    private static final String TAG = CustomListAdapter.class.getSimpleName();

    private final Activity mContext;

    //Array list for file information HashMap
    private final ArrayList<HashMap<String, String>> mItemInfo;

    //Array list for thumbnail id
    private final ArrayList<Integer> mBitmapId;
    private final int mDummyCnt = 10; //The number for bounds of cancel the thumbnail request
    private int mCurrentPosition = 0; //The position of view which currently updates UI
    private Bitmap mDummyBitmap; //Dummy image  for thumbnail
    private selectedGalleryType galleryType;

    ;
    public CustomListAdapter(Activity context, ArrayList<HashMap<String, String>> itemInfo, ArrayList<Integer> thumbnailsId) {
        super(context, R.layout.customlistadapter_layout, itemInfo);
        mContext = context;
        mItemInfo = itemInfo;
        mBitmapId = thumbnailsId;

        try {
            //Set dummy image for thumbnail
            InputStream istr = mContext.getAssets().open("dummy.jpg");
            mDummyBitmap = VrUtils.scaleBitmap(BitmapFactory.decodeStream(istr), 300, 150);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set gallery type for adapter
     *
     * @param type Gallery Type;
     *             CAMERA IMAGE: Image in friends device
     *             CAMERA VIDEO: Video in friends device
     *             DOWNLOAD IMAGE: Image in local storage
     *             DOWNLOAD VIDEO: Video in local storage
     */
    public void setType(selectedGalleryType type) {
        galleryType = type;
    }

    /**
     * Set background of the row view
     *
     * @param position position of row view
     * @param rowView  row view which will set background
     * @param parent   parent of row view
     */
    private void setRowBackground(int position, View rowView, ViewGroup parent) {
        final ListView lv = (ListView) parent;
        int colorHighlight = ContextCompat.getColor(mContext, R.color.colorHighlight);
        int colorTransparent = ContextCompat.getColor(mContext, R.color.colorTransparent);

        if (lv.isItemChecked(position)) {
            rowView.setBackgroundColor(colorHighlight);
        } else {
            rowView.setBackgroundColor(colorTransparent);
        }
    }

    /**
     * Convert size unit from Bytes to MB
     *
     * @param sizeString size in Bytes
     * @return size in MB
     */
    private String convertBytesToMB(String sizeString) {
        double size = Float.parseFloat(sizeString);
        double mbSize = size / (1024 * 1024);

        return String.format("%.2f", mbSize) + "MB";
    }

    public View getView(final int position, View view, ViewGroup parent) {
        mCurrentPosition = position; //Set current position
        LayoutInflater inflater = mContext.getLayoutInflater();

        View rowView = view;
        ViewHolder holder;
        if (view == null) {
            rowView = inflater.inflate(R.layout.customlistadapter_layout, null, true);
            holder = new ViewHolder();
            holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
            holder.textViewName = (TextView) rowView.findViewById(R.id.itemname);
            holder.textViewSize = (TextView) rowView.findViewById(R.id.itemsize);

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        //Set background color of current row
        setRowBackground(position, rowView, parent);

        final ImageView rowImageView = holder.imageView;
        rowImageView.setImageBitmap(mDummyBitmap);

        if (position < mItemInfo.size()) {
            //Get item information of current position
            HashMap<String, String> info = mItemInfo.get(position);
            holder.textViewName.setText(info.get(OSCParameterNameMapper.FileInfo.NAME));

            String sizeString =
                    convertBytesToMB(info.get(OSCParameterNameMapper.FileInfo.SIZE));
            holder.textViewSize.setText(sizeString);
        }
        if (position < mBitmapId.size()) {
            //Get thumbnail bitmap data of current position

            HttpAsyncTask.OnCancelCallback cancelCallback = new HttpAsyncTask.OnCancelCallback() {
                //Return http request need to be canceled or not
                //if position of this call back is out of bound, then cancel the request
                //   bound =>  (current position - 10, current position + 10)
                @Override
                public boolean cancelBackground(Object object) {
                    int tmpPosition = position; // the position of requested item
                    if (object != null)
                        tmpPosition = (int) object;
                    Log.d(TAG, "$$mCurrentPos = " + mCurrentPosition + " temp = " + tmpPosition);
                    return !(((mCurrentPosition - mDummyCnt) < tmpPosition)
                            && (tmpPosition < (mCurrentPosition + mDummyCnt)));
                }
            };
            HttpAsyncTask.OnHttpListener thumbnailListener = new HttpAsyncTask.OnHttpListener() {
                //Set bitmap image as thumbnail of row view at the position
                @Override
                public void onResponse(OSCReturnType type, final Object response) {
                    Log.v(TAG, "type = " + type);
                    if (type == OSCReturnType.SUCCESS || response != null) {
                        if (((mCurrentPosition - mDummyCnt) < position)
                                && (position < (mCurrentPosition + mDummyCnt))) {
                            rowImageView.setImageBitmap((Bitmap) response);
                        }
                    }

                }
            };

            //Request thumbnail image
            getThumbnailImage(position, thumbnailListener, cancelCallback);
        }
        return rowView;
    }

    /**
     * Get thumbnail image for row view at the position
     * API: /osc/commands/execute (camera.getFile)
     *
     * @param position       position of the row view
     * @param listener       response listener for http request (getFile for thumbnail image)
     * @param cancelCallback cancel callback listener
     */
    private void getThumbnailImage(int position, HttpAsyncTask.OnHttpListener listener,
                                   HttpAsyncTask.OnCancelCallback cancelCallback) {
        //Download gallery
        //   get thumbnail image from android database
        if ((galleryType.equals(selectedGalleryType.DOWNLOAD_IMAGE)) || (galleryType.equals(selectedGalleryType.DOWNLOAD_VIDEO))) {
            if (mBitmapId.get(position) == -1) {

            } else {
                ThumbnailBuilder task = new ThumbnailBuilder(mContext, mBitmapId.get(position), position, galleryType);
                task.setOnCancelCallback(cancelCallback);
                task.setListener(listener);
                task.execute();
            }
        }
        //Camera gallery
        //  get thumbnail image from friends camera
        else if ((galleryType.equals(selectedGalleryType.CAMERA_IMAGE)) || (galleryType.equals(selectedGalleryType.CAMERA_VIDEO))) {
            //Set parameter in order to get thumbnail image (set maxSize parameter)
            JSONObject parameters = new JSONObject();
            try {
                Log.d(TAG, "CALL thumbnail getFile : " + galleryType);
                String uri = getInfo(position, OSCParameterNameMapper.FileInfo.URL);
                parameters.put(OSCParameterNameMapper.FILEURL, uri);
                parameters.put(OSCParameterNameMapper.MAXSIZE, 144);
                OSCCommandsExecute commandsExecute;

                //Set the data type for request based on gallery type (image or video)
                if (galleryType.equals(selectedGalleryType.CAMERA_IMAGE)) {
                    commandsExecute = new OSCCommandsExecute("camera.getFile", parameters,
                            OSCCommandsExecute.CommandType.IMAGE);
                } else if (galleryType.equals(selectedGalleryType.CAMERA_VIDEO)) {
                    commandsExecute = new OSCCommandsExecute("camera.getFile", parameters,
                            OSCCommandsExecute.CommandType.VIDEO);
                } else {
                    Log.d(TAG, "GalleryType should be CAMERA_IMAGE or CAMERA_VIDEO");
                    return;
                }

                commandsExecute.setOnCancelCallback(cancelCallback);
                commandsExecute.setListener(listener);
                commandsExecute.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add item (file) into array list
     *
     * @param info     file information
     * @param bitmapId thumbnail Id
     *                 This value is used to remember thumbnail id in android media storage
     *                 This value is not used when request thumbnail from friends camera (set -1)
     */
    public void addItem(HashMap<String, String> info, int bitmapId) {
        //Same file info saved at the same position in array lists
        mItemInfo.add(info);
        mBitmapId.add(bitmapId);
    }

    /**
     * Remove item (file) from array list
     *
     * @param positions positions of remove items
     */
    public void removeItems(int[] positions) {
        ArrayList<HashMap<String, String>> tempInfo = new ArrayList<>();

        for (int i = 0; i < positions.length; i++) {
            int position = positions[i];
            tempInfo.add(mItemInfo.get(position));
        }

        int totalItems = mBitmapId.size();
        for (int i = 0; i < tempInfo.size(); i++) {
            mBitmapId.remove(totalItems - (i + 1));
        }

        mItemInfo.removeAll(tempInfo);
    }

    /**
     * Remove all items from array list
     */
    public void removeAllItems() {
        if (!mBitmapId.isEmpty()) {
            mBitmapId.clear();
        }
        if (!mItemInfo.isEmpty()) {
            for (int i = 0; i < mItemInfo.size(); i++) {
                mItemInfo.get(i).clear();
            }
            mItemInfo.clear();
        }
    }

    /**
     * Return file information
     *
     * @param position position of row view
     * @param key      key value of file information (ex) name, size etc.
     * @return information of the key
     */
    public String getInfo(int position, String key) {
        HashMap<String, String> info = mItemInfo.get(position);

        if (info.containsKey(key)) {
            return info.get(key);
        }
        return null;
    }

    /**
     * The number of files
     *
     * @return The number of files
     */
    public int getSize() {
        return mItemInfo.size();
    }


    public enum selectedGalleryType {CAMERA_IMAGE, CAMERA_VIDEO, DOWNLOAD_IMAGE, DOWNLOAD_VIDEO}

    public class ViewHolder {
        public TextView textViewName;
        public TextView textViewSize;

        public ImageView imageView;
    }
}
