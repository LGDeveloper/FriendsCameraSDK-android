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

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

/**
 * get live preview from friends camera
 */
public class LivePreviewActivity extends AppCompatActivity {
    private final static String TAG = LivePreviewActivity.class.getSimpleName();
    private Context mContext;
    private ImageView imagePreView;
    private boolean keepSendRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpView();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, TAG + " onResume");
        keepSendRequest = true;
        if (!WifiReceiver.isConnected()) {
            ((LivePreviewActivity) mContext).finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        keepSendRequest = false;
    }

    private void setUpView() {
        setContentView(R.layout.livepreview_layout);

        getSupportActionBar().setTitle(R.string.preview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePreView = (ImageView) findViewById(R.id.imageView_preview);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);
        keepSendRequest = true;
        getLivePreview();
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
     * get live preview from friends camera
     * API : /osc/commands/execute (camera.getLivePreview)
     */
    private void getLivePreview() {
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera.getLivePreview", null,
                OSCCommandsExecute.CommandType.PREVIEW);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                if (type == OSCReturnType.SUCCESS) {
                    imagePreView.setImageBitmap((Bitmap) response);
                    if (keepSendRequest) {
                        getLivePreview();
                    }
                } else {
                    Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
                    keepSendRequest = false;
                }
            }
        });
        commandsExecute.execute();
    }
}
