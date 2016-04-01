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
import com.lge.vr.VrUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.util.Log;

import java.io.IOException;

/**
 * Get thumbnail from android media storage
 */
public class ThumbnailBuilder extends HttpAsyncTask {
    private static final String TAG = ThumbnailBuilder.class.getSimpleName();

    final int mImageId; //thumbnail id
    final int mPosition; //position of thumbnail image
    final Context mContext;
    final CustomListAdapter.selectedGalleryType mType; //Gallery type
    Bitmap mBitmap;

    public ThumbnailBuilder(Context context, int bitmapId, int position, CustomListAdapter.selectedGalleryType type) {
        super(null, null);
        mContext = context;
        mImageId = bitmapId;
        mPosition = position;
        mType = type;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        boolean isCancel = false;
        if (mOnCancelCallback != null) {
            //Check this request does not need to execute
            isCancel = mOnCancelCallback.cancelBackground(mPosition);
        }
        if (!isCancel) {
            if (mType.equals(CustomListAdapter.selectedGalleryType.DOWNLOAD_IMAGE))
                mBitmap = MediaStore.Images.Thumbnails.getThumbnail(mContext.getContentResolver(),
                        mImageId, MediaStore.Images.Thumbnails.MINI_KIND, (BitmapFactory.Options) null);
            else if (mType.equals(CustomListAdapter.selectedGalleryType.DOWNLOAD_VIDEO))
                mBitmap = MediaStore.Video.Thumbnails.getThumbnail(mContext.getContentResolver(),
                        mImageId, MediaStore.Video.Thumbnails.MINI_KIND, (BitmapFactory.Options) null);
            else {
                Log.d(TAG, "GalleryType should be DOWNLOAD_IMAGE or DOWNLOAD_VIDEO");
                try {
                    mBitmap = VrUtils.scaleBitmap(BitmapFactory.decodeStream
                            (mContext.getAssets().open("dummy.jpg")), 300, 150);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return mBitmap;
    }
}