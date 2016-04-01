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

import com.lge.octopus.tentacles.wifi.client.WifiClient;
import com.lge.osclibrary.HTTP_SERVER_INFO;
import com.lge.osclibrary.HttpAsyncTask;
import com.lge.osclibrary.OSCCheckForUpdates;
import com.lge.osclibrary.OSCInfo;
import com.lge.osclibrary.OSCParameterNameMapper;
import com.lge.osclibrary.OSCState;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main Activity
 * Info, State, CheckForUpdates APIs are executed in this activity
 * Other APIs are executed in other activity
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final int MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    public Context mContext = this;
    private WifiReceiver receiver;
    private Button buttonConnect;
    private Button buttonCameraImage;
    private Button buttonCameraVideo;
    private Button buttonDownloadImage;
    private Button buttonDownloadVideo;
    private Button buttonInfo;
    private Button buttonState;
    private Button buttonCheckForUpdate;
    private Button buttonOptions;
    private Button buttonTakePicture;
    private Button buttonRecordVideo;
    private Button buttonCaptureInterval;
    private Button buttonSettings;
    private Button buttonPreview;
    private TextView connectStatus;
    private String fingerPrint;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "Main onCreate");
        super.onCreate(savedInstanceState);
        setupViews();
        initialize();
    }

    private void initialize() {
        receiver = new WifiReceiver();

        FriendsCameraApplication.setContext(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiClient.ACTION_WIFI_STATE);
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(receiver, filter);

        checkFileWritePermission();
        fingerPrint = "";
    }

    private void setupViews() {
        setContentView(R.layout.main_layout);

        connectStatus = (TextView) findViewById(R.id.text_state);

        //1. Connect Button
        buttonConnect = (Button) findViewById(R.id.button_connect);
        buttonConnect.setOnClickListener(this);

        // Set IP address and port number
        EditText editText = (EditText) findViewById(R.id.editTextIPAddr);
        String ip = editText.getText().toString();
        setIPPort(ip);

        //2. Get image list
        buttonCameraImage = (Button) findViewById(R.id.button_cameraimage);
        buttonCameraImage.setOnClickListener(this);

        //3. Get video list
        buttonCameraVideo = (Button) findViewById(R.id.button_cameravideo);
        buttonCameraVideo.setOnClickListener(this);

        //4. Image list for downloaded images(images in DCIM/LGC1Sample)
        //   Connect with viewer
        buttonDownloadImage = (Button) findViewById(R.id.button_downloadimage);
        buttonDownloadImage.setOnClickListener(this);

        //5. Video list for downloaded videos(videos in DCIM/LGC1Sample)
        //   Connect with viewer
        buttonDownloadVideo = (Button) findViewById(R.id.button_downloadvideo);
        buttonDownloadVideo.setOnClickListener(this);

        //6. Get camera info (info)
        buttonInfo = (Button) findViewById(R.id.button_info);
        buttonInfo.setOnClickListener(this);

        //7. Get camera state (state)
        buttonState = (Button) findViewById(R.id.button_state);
        buttonState.setOnClickListener(this);

        //8. Check for update
        buttonCheckForUpdate = (Button) findViewById(R.id.button_checkforupdate);
        buttonCheckForUpdate.setOnClickListener(this);

        //9. Set camera options (camera.setOption, camera.getOption)
        buttonOptions = (Button) findViewById(R.id.button_option);
        buttonOptions.setOnClickListener(this);

        //10. Take picture
        buttonTakePicture = (Button) findViewById(R.id.button_takePicture);
        buttonTakePicture.setOnClickListener(this);

        //11. Record video
        buttonRecordVideo = (Button) findViewById(R.id.button_recordVideo);
        buttonRecordVideo.setOnClickListener(this);

        //12. Capture Interval
        buttonCaptureInterval = (Button) findViewById(R.id.button_captureinterval);
        buttonCaptureInterval.setOnClickListener(this);

        //13. Camera Settings
        buttonSettings = (Button) findViewById(R.id.button_settings);
        buttonSettings.setOnClickListener(this);

        //14. Preview
        buttonPreview = (Button) findViewById(R.id.button_preview);
        buttonPreview.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent i;
        switch (view.getId()) {
            case R.id.button_connect:
                String currentConnection = connectStatus.getText().toString();
                if (currentConnection.equals(getResources().getString(R.string.wifi_status_connect))) {
                    //disconnect with camera
                    updateStateBasedOnWifiConnection(false);
                    WifiReceiver.disconnectWifi(mContext);
                } else {
                    //connect with camera
                    i = new Intent(mContext, ConnectionActivity.class);
                    startActivity(i);
                }
                break;
            case R.id.button_cameraimage:
                //   show an image info, get full image, delete image on camera for selected image
                i = new Intent(mContext, CameraFileListViewActivity.class);
                i.putExtra("type", "image");
                startActivity(i);
                break;
            case R.id.button_cameravideo:
                //   show an video info, get full video, delete video on camera for selected video
                i = new Intent(mContext, CameraFileListViewActivity.class);
                i.putExtra("type", "video");
                startActivity(i);
                break;
            case R.id.button_downloadimage:
                //   show an image info, get full image, delete image on phone for selected image
                i = new Intent(mContext, DownloadFileListViewActivity.class);
                i.putExtra("type", "image");
                startActivity(i);
                break;
            case R.id.button_downloadvideo:
                //   show an video info, get full video, delete video on phone for selected video
                i = new Intent(mContext, DownloadFileListViewActivity.class);
                i.putExtra("type", "video");
                startActivity(i);
                break;
            case R.id.button_info:
                getCameraInfo();
                break;
            case R.id.button_state:
                getCameraState();
                break;
            case R.id.button_checkforupdate:
                if (fingerPrint.equals("")) {
                    Utils.showAlertDialog(mContext, null,
                            "/osc/state has not been executed. Please click 'Camera State' button", null);
                } else {
                    getCameraCheckForUpdate();
                }
                break;
            case R.id.button_option:
                i = new Intent(mContext, OptionsActivity.class);
                startActivity(i);
                break;
            case R.id.button_takePicture:
                i = new Intent(mContext, TakePictureActivity.class);
                startActivity(i);
                break;
            case R.id.button_recordVideo:
                i = new Intent(mContext, RecordVideoActivity.class);
                startActivity(i);
                break;
            case R.id.button_captureinterval:
                i = new Intent(mContext, CaptureIntervalActivity.class);
                startActivity(i);
                break;
            case R.id.button_settings:
                i = new Intent(mContext, SettingsActivity.class);
                startActivity(i);
                break;
            case R.id.button_preview:
                i = new Intent(mContext, PreviewActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "Main onResume");
        FriendsCameraApplication.setContext(this);
        updateStateBasedOnWifiConnection(WifiReceiver.isConnected());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "Main onStop");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "Main onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "Main onDestroy");
        try {
            unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.d(TAG, "Fail to unregister receiver");
        }
    }

    /**
     * Set IP address and Port number
     */
    private void setIPPort(String ip) {
        String[] temp = ip.split(":");
        HTTP_SERVER_INFO.IP = temp[0];
        if (temp.length == 2) {
            HTTP_SERVER_INFO.PORT = temp[1];
        } else {
            HTTP_SERVER_INFO.PORT = "6624";
        }
    }

    /**
     * Get the basic information of the LG 360 CAM device
     * API : /osc/info
     */
    private void getCameraInfo() {
        final OSCInfo oscInfo = new OSCInfo();
        oscInfo.setListener(new OSCInfo.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                String title = getString(R.string.camera_info);
                Utils.showTextDialog(mContext, title, Utils.parseString(response));
            }
        });

        oscInfo.execute();
    }

    /**
     * Get the device information that change over time such as battery level, battery state, etc.
     * API : /osc/state
     */
    private void getCameraState() {
        final OSCState oscState = new OSCState();
        oscState.setListener(new OSCInfo.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                String title = getString(R.string.camera_state);
                if (type == OSCReturnType.SUCCESS) {
                    try {
                        JSONObject jObject = new JSONObject((String) response);
                        fingerPrint = jObject.getString(OSCParameterNameMapper.FINGERPRINT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Utils.showTextDialog(mContext, title, Utils.parseString(response));
            }
        });
        oscState.execute();
    }

    /**
     * Update the fingerprint to reflect the current camera state by comparing it with the
     * fingerprint held by the client.
     * API : /osc/checkForUpdate
     */
    private void getCameraCheckForUpdate() {
        final String title = getString(R.string.check_update);
        mProgressDialog = ProgressDialog.show(mContext, null, "Checking...", true, false);
        final OSCCheckForUpdates oscCheckForUpdates = new OSCCheckForUpdates(fingerPrint, 1);
        oscCheckForUpdates.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                if (type == OSCReturnType.SUCCESS) {
                    JSONObject jObject = null;
                    try {
                        mProgressDialog.cancel();
                        jObject = new JSONObject(response.toString());

                        String responseFingerprint = jObject.getString(OSCParameterNameMapper.LOCAL_FINGERPRINT);
                        if (fingerPrint.equals(responseFingerprint)) {
                            Utils.showAlertDialog(mContext, title, "State is same\n\n" + Utils.parseString(response), null);
                        } else {
                            Utils.showAlertDialog(mContext, title,
                                    "State is updated, Please check state by /osc/state\n\n" + Utils.parseString(response), null);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Utils.showTextDialog(mContext, title, Utils.parseString(response));
                }
            }
        });
        oscCheckForUpdates.execute();
    }

    /**
     * Update Main UI.
     * Buttons are enable when camera is connected
     */
    public void updateStateBasedOnWifiConnection(boolean state) {
        buttonCameraImage.setEnabled(state);
        buttonCameraVideo.setEnabled(state);
        buttonInfo.setEnabled(state);
        buttonState.setEnabled(state);
        buttonCheckForUpdate.setEnabled(state);
        buttonOptions.setEnabled(state);
        buttonTakePicture.setEnabled(state);
        buttonRecordVideo.setEnabled(state);
        buttonCaptureInterval.setEnabled(state);
        buttonSettings.setEnabled(state);
        buttonPreview.setEnabled(state);

        if (state) {
            connectStatus.setText(R.string.wifi_status_connect);
            buttonConnect.setText(R.string.button_disconnect);
        } else {
            connectStatus.setText(R.string.wifi_status_disconnect);
            buttonConnect.setText(R.string.button_connect);
        }
    }

    /**
     * Ask "write storage" permission to user
     */
    private void checkFileWritePermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission for write external storage is granted");
                } else {
                    Log.d(TAG, "Permission for write external storage is denied");
                }
        }
    }
}
