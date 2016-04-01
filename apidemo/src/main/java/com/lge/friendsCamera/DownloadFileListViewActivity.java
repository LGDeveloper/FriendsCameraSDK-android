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

import com.lge.osclibrary.OSCParameterNameMapper;
import com.lge.vr.ViewerActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Show file lists of local storage
 */
public class DownloadFileListViewActivity extends AppCompatActivity {
    private final static String TAG = DownloadFileListViewActivity.class.getSimpleName();
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    //Array list for file information HashMap
    ArrayList<HashMap<String, String>> itemInfo = new ArrayList<HashMap<String, String>>();
    //Array list for thumbnail id
    ArrayList<Integer> itemBitmap = new ArrayList<>();
    private Context mContext;
    private CustomListAdapter adapter;
    private ListView mListView;
    private String mediaType; // Media type for this activity (IMAGE or VIDEO)
    /**
     * Item click listener for an item selected
     * Show dialog for selected row item
     */
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    final String imageFileUri = adapter.getInfo(position,
                            OSCParameterNameMapper.FileInfo.URL);

                    CharSequence[] list = new CharSequence[]{"Viewer"};
                    Utils.showListDialog(mContext, list, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0: {
                                    startViewer(mediaType, imageFileUri);
                                    break;
                                }
                                default:
                                    break;
                            }
                        }
                    });
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        initialize();
    }

    private void setupViews() {
        setContentView(R.layout.filelistview_layout);
        mListView = (ListView) findViewById(R.id.list_view);

        //Get media type from intent
        Intent intent = getIntent();
        mediaType = intent.getExtras().getString("type");

        if (mediaType.equals(IMAGE)) {
            getSupportActionBar().setTitle(R.string.download_image_gallery);
        } else {
            getSupportActionBar().setTitle(R.string.download_video_gallery);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new CustomListAdapter(this, itemInfo, itemBitmap);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);

        if (mediaType == null) {
            Log.d(TAG, "Error: Need intent value for media type");
            return;
        }

        //Set adapter type based on media type
        if (mediaType.equals(IMAGE)) {
            adapter.setType(CustomListAdapter.selectedGalleryType.DOWNLOAD_IMAGE);
        } else if (mediaType.equals(VIDEO)) {
            adapter.setType(CustomListAdapter.selectedGalleryType.DOWNLOAD_VIDEO);
        }
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(itemClickListener);

        //Get file list from android media storage
        readFileList(mediaType);
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Get the file list downloaded from friends camera
     *
     * @param mediaType gallery media type (image or video)
     */
    private void readFileList(String mediaType) {
        Log.v(TAG, "Context = " + mContext);
        Uri uri = null;
        if (mediaType.equals(IMAGE)) {
            uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        } else if (mediaType.equals(VIDEO)) {
            uri = android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        }

        if (uri == null) {
            Log.v(TAG, "ERROR: Media type error");
            return;
        } else {
            String[] projection = {MediaStore.MediaColumns._ID, MediaStore.MediaColumns.DATA
                    , MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE};

            Cursor cursor = mContext.getContentResolver().query(uri, projection, null, null, null);
            while (cursor != null && cursor.moveToNext()) {
                int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                String data = cursor.getString(column_index_data);
                int column_index_id = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID);
                int fileId = cursor.getInt(column_index_id);
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME));
                long fileSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE));

                //Get information of files in friendsCameraSample folder
                if (data.contains("friendsCameraSample")) {
                    HashMap<String, String> info = new HashMap<>();
                    info.put(OSCParameterNameMapper.FileInfo.NAME, displayName);
                    info.put(OSCParameterNameMapper.FileInfo.URL, data);
                    info.put(OSCParameterNameMapper.FileInfo.SIZE, String.valueOf(fileSize));
                    adapter.addItem(info, fileId);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     * Start 360 viewer
     *
     * @param mimetype media type (image or video)
     * @param fileUri  uri of the file to show in android media storage
     */
    private void startViewer(String mimetype, String fileUri) {
        Log.d(TAG, "path " + fileUri);
        Uri mFileUri = Uri.parse(fileUri);
        if (mFileUri == null) {
            Log.d(TAG, "Fail to parse String to Uri");
            return;
        }
        Intent intent = new Intent(mContext, ViewerActivity.class);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(mFileUri, mimetype + " ");
        startActivity(intent);
    }
}
