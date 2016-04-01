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
import com.lge.osclibrary.OSCCommandsStatus;
import com.lge.osclibrary.OSCParameterNameMapper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

/**
 * Take picture by friends camera
 */
public class TakePictureActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = TakePictureActivity.class.getSimpleName();
    private Context mContext;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViews();
        initialize();
    }

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViews() {
        setContentView(R.layout.takepicture_layout);

        getSupportActionBar().setTitle(R.string.take_picture);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button button_manualMetaData = (Button) findViewById(R.id.button_manualMetaData);
        button_manualMetaData.setOnClickListener(this);

        final Button button_takePicture = (Button) findViewById(R.id.button_takePicture);
        button_takePicture.setOnClickListener(this);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_manualMetaData:
                getManualMetaData();
                break;
            case R.id.button_takePicture:
                takePicture();
                mProgressDialog = ProgressDialog.show(mContext, "", "Processing...", true, false);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "TakePicture onResume");
        if (!WifiReceiver.isConnected()) {
            ((TakePictureActivity) mContext).finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "TakePicture onDestroy");
    }

    /**
     * Get file metadata given its URL.
     * THe image header lists the Exif and XMP fields.
     */
    private void getManualMetaData() {
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera._manualMetaData", null);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                Utils.showTextDialog(mContext, getString(R.string.response),
                        Utils.parseString(response));
            }
        });
        commandsExecute.execute();
    }

    /**
     * Captures an equirectangular image, saving lat/long coordinates to EXIF
     * API : /osc/commands/execute (camera.takePicture)
     */
    private void takePicture() {
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera.takePicture", null);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, final Object response) {
                if (type == OSCReturnType.SUCCESS) {
                    String state = Utils.getCommandState((String) response);
                    if (state != null && state.equals(OSCParameterNameMapper.STATE_INPROGRESS)) {
                        String commandId = Utils.getCommandId((String) response);
                        checkCommandsStatus(commandId);
                        return;
                    }
                }
                if (mProgressDialog.isShowing())
                    mProgressDialog.cancel();
                Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));

            }
        });
        commandsExecute.execute();
    }

    /**
     * Check the status for previous inProgress commands.
     * Determine whether camera.takePicture has completed.
     *
     * @param commandId command Id of previous camera.takePicture
     *                  API : /osc/commands/status
     */
    private void checkCommandsStatus(final String commandId) {
        OSCCommandsStatus commandsStatus = new OSCCommandsStatus(commandId);
        commandsStatus.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, final Object response) {
                if (type == OSCReturnType.SUCCESS) {
                    String state = Utils.getCommandState((String) response);
                    if (state != null && state.equals(OSCParameterNameMapper.STATE_INPROGRESS)) {
                        checkCommandsStatus(commandId);
                        return;
                    }
                }
                Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
                if (mProgressDialog.isShowing())
                    mProgressDialog.cancel();

            }
        });
        commandsStatus.execute();
    }

}
