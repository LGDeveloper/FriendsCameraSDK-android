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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * get/set options of friends camera
 */
public class OptionsActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = OptionsActivity.class.getSimpleName();

    private Button get_button = null;
    private Button get_clear_button = null;
    private CheckBox get_captureMode = null;
    private CheckBox get_captureModeSupport = null;
    private CheckBox get_exposureProgram = null;
    private CheckBox get_exposureProgramSupport = null;
    private CheckBox get_iso = null;
    private CheckBox get_isoSupport = null;
    private CheckBox get_shutterSpeed = null;
    private CheckBox get_shutterSpeedSupport = null;
    private CheckBox get_aperture = null;
    private CheckBox get_apertureSupport = null;
    private CheckBox get_whiteBalance = null;
    private CheckBox get_whiteBalanceSupport = null;
    private CheckBox get_exposureCompensation = null;
    private CheckBox get_exposureCompensationSupport = null;
    private CheckBox get_fileFormat = null;
    private CheckBox get_fileFormatSupport = null;
    private CheckBox get_speedOptimized = null;
    private CheckBox get_previewFormat = null;
    private CheckBox get_previewFormatSupport = null;
    private CheckBox get_exposureDelay = null;
    private CheckBox get_exposureDelaySupport = null;
    private CheckBox get_captureInterval = null;
    private CheckBox get_captureIntervalSupport = null;
    private CheckBox get_captureNumber = null;
    private CheckBox get_captureNumberSupport = null;
    private CheckBox get_sleepDelay = null;
    private CheckBox get_sleepDelaySupport = null;
    private CheckBox get_totalSpace = null;
    private CheckBox get_remainingSpace = null;
    private CheckBox get_remainingPictures = null;
    private CheckBox get_remainingVideoMinutes = null;
    private CheckBox get_gpsInfo = null;
    private CheckBox get_dateTimeZone = null;
    private CheckBox get_hdr = null;
    private CheckBox get_hdrSupport = null;
    private CheckBox get_exposureBracket = null;
    private CheckBox get_exposureBracketSupport = null;
    private CheckBox get_gyro = null;
    private CheckBox get_gyroSupport = null;
    private CheckBox get_gps = null;
    private CheckBox get_gpsSupport = null;
    private CheckBox get_imageStabilization = null;
    private CheckBox get_imageStabilizationSupport = null;
    private CheckBox get_wifiPassword = null;
    private CheckBox get_delayProcessing = null;
    private CheckBox get_delayProcessingSupport = null;
    private CheckBox get_sceneMode = null;
    private CheckBox get_sceneModeSupport = null;
    private CheckBox get_timer = null;
    private CheckBox get_timerSupport = null;
    private CheckBox get_storage = null;
    private CheckBox get_batteryLevel = null;
    private CheckBox get_angle = null;
    private CheckBox get_angleSupport = null;
    private CheckBox get_audioChannel = null;
    private CheckBox get_audioChannelSupport = null;
    private CheckBox get_cameraId = null;
    private CheckBox get_cameraIdSupport = null;
    private CheckBox get_lgISO = null;
    private CheckBox get_lgISOSupport = null;
    private CheckBox get_lgShutterSpeed = null;
    private CheckBox get_lgShutterSpeedSupport = null;
    private CheckBox get_lgWhiteBalance = null;
    private CheckBox get_lgWhiteBalanceSupport = null;

    private Button set_button = null;
    private Button set_clear_button = null;

    private EditText set_captureMode = null;
    private EditText set_exposureProgram = null;
    private EditText set_iso = null;
    private EditText set_shutterSpeed = null;
    private EditText set_aperture = null;
    private EditText set_whiteBalance = null;
    private EditText set_exposureCompensation = null;
    private EditText set_fileFormat = null;
    private EditText set_speedOptimized = null;
    private EditText set_previewFormat = null;
    private EditText set_exposureDelay = null;
    private EditText set_captureInterval = null;
    private EditText set_captureNumber = null;
    private EditText set_sleepDelay = null;
    private EditText set_gpsInfo = null;
    private EditText set_dateTimeZone = null;
    private EditText set_hdr = null;
    private EditText set_exposureBracket = null;
    private EditText set_gyro = null;
    private EditText set_gps = null;
    private EditText set_imageStabilization = null;
    private EditText set_wifiPassword = null;
    private EditText set_delayProcessing = null;
    private EditText set_sceneMode = null;
    private EditText set_timer = null;
    private EditText set_angle = null;
    private EditText set_audioChannel = null;
    private EditText set_cameraId = null;
    private EditText set_lgISO = null;
    private EditText set_lgShutterSpeed = null;
    private EditText set_lgWhiteBalance = null;

    private ArrayList<String> getArrayList = new ArrayList<>();

    private Context mContext;

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

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);


    }

    private void setupViews() {
        setContentView(R.layout.options_layout);

        getSupportActionBar().setTitle(R.string.options);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        get_button = (Button) findViewById(R.id.get_button);
        get_clear_button = (Button) findViewById(R.id.get_clear_button);
        get_captureMode = (CheckBox) findViewById(R.id.get_captureMode);
        get_captureModeSupport = (CheckBox) findViewById(R.id.get_captureModeSupport);
        get_exposureProgram = (CheckBox) findViewById(R.id.get_exposureProgram);
        get_exposureProgramSupport = (CheckBox) findViewById(R.id.get_exposureProgramSupport);
        get_iso = (CheckBox) findViewById(R.id.get_iso);
        get_isoSupport = (CheckBox) findViewById(R.id.get_isoSupport);
        get_shutterSpeed = (CheckBox) findViewById(R.id.get_shutterSpeed);
        get_shutterSpeedSupport = (CheckBox) findViewById(R.id.get_shutterSpeedSupport);
        get_aperture = (CheckBox) findViewById(R.id.get_aperture);
        get_apertureSupport = (CheckBox) findViewById(R.id.get_apertureSupport);
        get_whiteBalance = (CheckBox) findViewById(R.id.get_whiteBalance);
        get_whiteBalanceSupport = (CheckBox) findViewById(R.id.get_whiteBalanceSupport);
        get_exposureCompensation = (CheckBox) findViewById(R.id.get_exposureCompensation);
        get_exposureCompensationSupport = (CheckBox) findViewById(R.id.get_exposureCompensationSupport);
        get_fileFormat = (CheckBox) findViewById(R.id.get_fileFormat);
        get_fileFormatSupport = (CheckBox) findViewById(R.id.get_fileFormatSupport);
        get_speedOptimized = (CheckBox) findViewById(R.id.get_speedOptimized);
        get_previewFormat = (CheckBox) findViewById(R.id.get_previewFormat);
        get_previewFormatSupport = (CheckBox) findViewById(R.id.get_previewFormatSupport);
        get_exposureDelay = (CheckBox) findViewById(R.id.get_exposureDelay);
        get_exposureDelaySupport = (CheckBox) findViewById(R.id.get_exposureDelaySupport);
        get_captureInterval = (CheckBox) findViewById(R.id.get_captureInterval);
        get_captureIntervalSupport = (CheckBox) findViewById(R.id.get_captureIntervalSupport);
        get_captureNumber = (CheckBox) findViewById(R.id.get_captureNumber);
        get_captureNumberSupport = (CheckBox) findViewById(R.id.get_captureNumberSupport);
        get_sleepDelay = (CheckBox) findViewById(R.id.get_sleepDelay);
        get_sleepDelaySupport = (CheckBox) findViewById(R.id.get_sleepDelaySupport);
        get_totalSpace = (CheckBox) findViewById(R.id.get_totalSpace);
        get_remainingSpace = (CheckBox) findViewById(R.id.get_remainingSpace);
        get_remainingPictures = (CheckBox) findViewById(R.id.get_remainingPictures);
        get_remainingVideoMinutes = (CheckBox) findViewById(R.id.get_remainingVideoMinutes);
        get_gpsInfo = (CheckBox) findViewById(R.id.get_gpsInfo);
        get_dateTimeZone = (CheckBox) findViewById(R.id.get_dateTimeZone);
        get_hdr = (CheckBox) findViewById(R.id.get_hdr);
        get_hdrSupport = (CheckBox) findViewById(R.id.get_hdrSupport);
        get_exposureBracket = (CheckBox) findViewById(R.id.get_exposureBracket);
        get_exposureBracketSupport = (CheckBox) findViewById(R.id.get_exposureBracketSupport);
        get_gyro = (CheckBox) findViewById(R.id.get_gyro);
        get_gyroSupport = (CheckBox) findViewById(R.id.get_gyroSupport);
        get_gps = (CheckBox) findViewById(R.id.get_gps);
        get_gpsSupport = (CheckBox) findViewById(R.id.get_gpsSupport);
        get_imageStabilization = (CheckBox) findViewById(R.id.get_imageStabilization);
        get_imageStabilizationSupport = (CheckBox) findViewById(R.id.get_imageStabilizationSupport);
        get_wifiPassword = (CheckBox) findViewById(R.id.get_wifiPassword);
        get_delayProcessing = (CheckBox) findViewById(R.id.get_delayProcessing);
        get_delayProcessingSupport = (CheckBox) findViewById(R.id.get_delayProcessingSupport);
        get_sceneMode = (CheckBox) findViewById(R.id.get_sceneMode);
        get_sceneModeSupport = (CheckBox) findViewById(R.id.get_sceneModeSupport);
        get_timer = (CheckBox) findViewById(R.id.get_timer);
        get_timerSupport = (CheckBox) findViewById(R.id.get_timerSupport);
        get_storage = (CheckBox) findViewById(R.id.get_storage);
        get_batteryLevel = (CheckBox) findViewById(R.id.get_batteryLevel);
        get_angle = (CheckBox) findViewById(R.id.get_angle);
        get_angleSupport = (CheckBox) findViewById(R.id.get_angleSupport);
        get_audioChannel = (CheckBox) findViewById(R.id.get_audioChannel);
        get_audioChannelSupport = (CheckBox) findViewById(R.id.get_audioChannelSupport);
        get_cameraId = (CheckBox) findViewById(R.id.get_cameraId);
        get_cameraIdSupport = (CheckBox) findViewById(R.id.get_cameraIdSupport);
        get_lgISO = (CheckBox) findViewById(R.id.get_lgISO);
        get_lgISOSupport = (CheckBox) findViewById(R.id.get_lgISOSupport);
        get_lgShutterSpeed = (CheckBox) findViewById(R.id.get_lgShutterSpeed);
        get_lgShutterSpeedSupport = (CheckBox) findViewById(R.id.get_lgShutterSpeedSupport);
        get_lgWhiteBalance = (CheckBox) findViewById(R.id.get_lgWhiteBalance);
        get_lgWhiteBalanceSupport = (CheckBox) findViewById(R.id.get_lgWhiteBalanceSupport);

        set_button = (Button) findViewById(R.id.set_button);
        set_clear_button = (Button) findViewById(R.id.set_clear_button);
        set_captureMode = (EditText) findViewById(R.id.set_captureMode);
        set_exposureProgram = (EditText) findViewById(R.id.set_exposureProgram);
        set_iso = (EditText) findViewById(R.id.set_iso);
        set_shutterSpeed = (EditText) findViewById(R.id.set_shutterSpeed);
        set_aperture = (EditText) findViewById(R.id.set_aperture);
        set_whiteBalance = (EditText) findViewById(R.id.set_whiteBalance);
        set_exposureCompensation = (EditText) findViewById(R.id.set_exposureCompensation);
        set_fileFormat = (EditText) findViewById(R.id.set_fileFormat);
        set_speedOptimized = (EditText) findViewById(R.id.set_speedOptimized);
        set_previewFormat = (EditText) findViewById(R.id.set_previewFormat);
        set_exposureDelay = (EditText) findViewById(R.id.set_exposureDelay);
        set_captureInterval = (EditText) findViewById(R.id.set_captureInterval);
        set_captureNumber = (EditText) findViewById(R.id.set_captureNumber);
        set_sleepDelay = (EditText) findViewById(R.id.set_sleepDelay);
        set_gpsInfo = (EditText) findViewById(R.id.set_gpsInfo);
        set_dateTimeZone = (EditText) findViewById(R.id.set_dateTimeZone);
        set_hdr = (EditText) findViewById(R.id.set_hdr);
        set_exposureBracket = (EditText) findViewById(R.id.set_exposureBracket);
        set_gyro = (EditText) findViewById(R.id.set_gyro);
        set_gps = (EditText) findViewById(R.id.set_gps);
        set_imageStabilization = (EditText) findViewById(R.id.set_imageStabilization);
        set_wifiPassword = (EditText) findViewById(R.id.set_wifiPassword);
        set_delayProcessing = (EditText) findViewById(R.id.set_delayProcessing);
        set_sceneMode = (EditText) findViewById(R.id.set_sceneMode);
        set_timer = (EditText) findViewById(R.id.set_timer);
        set_angle = (EditText) findViewById(R.id.set_angle);
        set_audioChannel = (EditText) findViewById(R.id.set_audioChannel);
        set_cameraId = (EditText) findViewById(R.id.set_cameraId);
        set_lgISO = (EditText) findViewById(R.id.set_lgISO);
        set_lgShutterSpeed = (EditText) findViewById(R.id.set_lgShutterSpeed);
        set_lgWhiteBalance = (EditText) findViewById(R.id.set_lgWhiteBalance);

        get_button.setOnClickListener(this);
        get_clear_button.setOnClickListener(this);
        get_captureMode.setOnClickListener(this);
        get_captureModeSupport.setOnClickListener(this);
        get_exposureProgram.setOnClickListener(this);
        get_exposureProgramSupport.setOnClickListener(this);
        get_iso.setOnClickListener(this);
        get_isoSupport.setOnClickListener(this);
        get_shutterSpeed.setOnClickListener(this);
        get_shutterSpeedSupport.setOnClickListener(this);
        get_aperture.setOnClickListener(this);
        get_apertureSupport.setOnClickListener(this);
        get_whiteBalance.setOnClickListener(this);
        get_whiteBalanceSupport.setOnClickListener(this);
        get_exposureCompensation.setOnClickListener(this);
        get_exposureCompensationSupport.setOnClickListener(this);
        get_fileFormat.setOnClickListener(this);
        get_fileFormatSupport.setOnClickListener(this);
        get_speedOptimized.setOnClickListener(this);
        get_previewFormat.setOnClickListener(this);
        get_previewFormatSupport.setOnClickListener(this);
        get_exposureDelay.setOnClickListener(this);
        get_exposureDelaySupport.setOnClickListener(this);
        get_captureInterval.setOnClickListener(this);
        get_captureIntervalSupport.setOnClickListener(this);
        get_captureNumber.setOnClickListener(this);
        get_captureNumberSupport.setOnClickListener(this);
        get_sleepDelay.setOnClickListener(this);
        get_sleepDelaySupport.setOnClickListener(this);
        get_totalSpace.setOnClickListener(this);
        get_remainingSpace.setOnClickListener(this);
        get_remainingPictures.setOnClickListener(this);
        get_remainingVideoMinutes.setOnClickListener(this);
        get_gpsInfo.setOnClickListener(this);
        get_dateTimeZone.setOnClickListener(this);
        get_hdr.setOnClickListener(this);
        get_hdrSupport.setOnClickListener(this);
        get_exposureBracket.setOnClickListener(this);
        get_exposureBracketSupport.setOnClickListener(this);
        get_gyro.setOnClickListener(this);
        get_gyroSupport.setOnClickListener(this);
        get_gps.setOnClickListener(this);
        get_gpsSupport.setOnClickListener(this);
        get_imageStabilization.setOnClickListener(this);
        get_imageStabilizationSupport.setOnClickListener(this);
        get_wifiPassword.setOnClickListener(this);
        get_delayProcessing.setOnClickListener(this);
        get_delayProcessingSupport.setOnClickListener(this);
        get_sceneMode.setOnClickListener(this);
        get_sceneModeSupport.setOnClickListener(this);
        get_timer.setOnClickListener(this);
        get_timerSupport.setOnClickListener(this);
        get_storage.setOnClickListener(this);
        get_batteryLevel.setOnClickListener(this);
        get_angle.setOnClickListener(this);
        get_angleSupport.setOnClickListener(this);
        get_audioChannel.setOnClickListener(this);
        get_audioChannelSupport.setOnClickListener(this);
        get_cameraId.setOnClickListener(this);
        get_cameraIdSupport.setOnClickListener(this);
        get_lgISO.setOnClickListener(this);
        get_lgISOSupport.setOnClickListener(this);
        get_lgShutterSpeed.setOnClickListener(this);
        get_lgShutterSpeedSupport.setOnClickListener(this);
        get_lgWhiteBalance.setOnClickListener(this);
        get_lgWhiteBalanceSupport.setOnClickListener(this);

        set_button.setOnClickListener(this);
        set_clear_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_button:
                getOptions();
                break;

            case R.id.get_clear_button:
                clearGetList();
                break;

            case R.id.get_captureMode:
                makeGetList(get_captureMode);
                break;

            case R.id.get_captureModeSupport:
                makeGetList(get_captureModeSupport);
                break;

            case R.id.get_exposureProgram:
                makeGetList(get_exposureProgram);
                break;

            case R.id.get_exposureProgramSupport:
                makeGetList(get_exposureProgramSupport);
                break;

            case R.id.get_iso:
                makeGetList(get_iso);
                break;

            case R.id.get_isoSupport:
                makeGetList(get_isoSupport);
                break;

            case R.id.get_shutterSpeed:
                makeGetList(get_shutterSpeed);
                break;

            case R.id.get_shutterSpeedSupport:
                makeGetList(get_shutterSpeedSupport);
                break;

            case R.id.get_aperture:
                makeGetList(get_aperture);
                break;

            case R.id.get_apertureSupport:
                makeGetList(get_apertureSupport);
                break;

            case R.id.get_whiteBalance:
                makeGetList(get_whiteBalance);
                break;

            case R.id.get_whiteBalanceSupport:
                makeGetList(get_whiteBalanceSupport);
                break;

            case R.id.get_exposureCompensation:
                makeGetList(get_exposureCompensation);
                break;

            case R.id.get_exposureCompensationSupport:
                makeGetList(get_exposureCompensationSupport);
                break;

            case R.id.get_fileFormat:
                makeGetList(get_fileFormat);
                break;

            case R.id.get_fileFormatSupport:
                makeGetList(get_fileFormatSupport);
                break;

            case R.id.get_speedOptimized:
                makeGetList(get_speedOptimized);
                break;

            case R.id.get_previewFormat:
                makeGetList(get_previewFormat);
                break;

            case R.id.get_previewFormatSupport:
                makeGetList(get_previewFormatSupport);
                break;

            case R.id.get_exposureDelay:
                makeGetList(get_exposureDelay);
                break;

            case R.id.get_exposureDelaySupport:
                makeGetList(get_exposureDelaySupport);
                break;

            case R.id.get_captureInterval:
                makeGetList(get_captureInterval);
                break;

            case R.id.get_captureIntervalSupport:
                makeGetList(get_captureIntervalSupport);
                break;

            case R.id.get_captureNumber:
                makeGetList(get_captureNumber);
                break;

            case R.id.get_captureNumberSupport:
                makeGetList(get_captureNumberSupport);
                break;

            case R.id.get_sleepDelay:
                makeGetList(get_sleepDelay);
                break;

            case R.id.get_sleepDelaySupport:
                makeGetList(get_sleepDelaySupport);
                break;

            case R.id.get_totalSpace:
                makeGetList(get_totalSpace);
                break;

            case R.id.get_remainingSpace:
                makeGetList(get_remainingSpace);
                break;

            case R.id.get_remainingPictures:
                makeGetList(get_remainingPictures);
                break;

            case R.id.get_remainingVideoMinutes:
                makeGetList(get_remainingVideoMinutes);
                break;

            case R.id.get_gpsInfo:
                makeGetList(get_gpsInfo);
                break;

            case R.id.get_dateTimeZone:
                makeGetList(get_dateTimeZone);
                break;

            case R.id.get_hdr:
                makeGetList(get_hdr);
                break;

            case R.id.get_hdrSupport:
                makeGetList(get_hdrSupport);
                break;

            case R.id.get_exposureBracket:
                makeGetList(get_exposureBracket);
                break;

            case R.id.get_exposureBracketSupport:
                makeGetList(get_exposureBracketSupport);
                break;

            case R.id.get_gyro:
                makeGetList(get_gyro);
                break;

            case R.id.get_gyroSupport:
                makeGetList(get_gyroSupport);
                break;

            case R.id.get_gps:
                makeGetList(get_gps);
                break;

            case R.id.get_gpsSupport:
                makeGetList(get_gpsSupport);
                break;

            case R.id.get_imageStabilization:
                makeGetList(get_imageStabilization);
                break;

            case R.id.get_imageStabilizationSupport:
                makeGetList(get_imageStabilizationSupport);
                break;

            case R.id.get_wifiPassword:
                makeGetList(get_wifiPassword);
                break;

            case R.id.get_delayProcessing:
                makeGetList(get_delayProcessing);
                break;

            case R.id.get_delayProcessingSupport:
                makeGetList(get_delayProcessingSupport);
                break;

            case R.id.get_sceneMode:
                makeGetList(get_sceneMode);
                break;

            case R.id.get_sceneModeSupport:
                makeGetList(get_sceneModeSupport);
                break;

            case R.id.get_timer:
                makeGetList(get_timer);
                break;

            case R.id.get_timerSupport:
                makeGetList(get_timerSupport);
                break;

            case R.id.get_storage:
                makeGetList(get_storage);
                break;

            case R.id.get_batteryLevel:
                makeGetList(get_batteryLevel);
                break;

            case R.id.get_angle:
                makeGetList(get_angle);
                break;

            case R.id.get_angleSupport:
                makeGetList(get_angleSupport);
                break;

            case R.id.get_audioChannel:
                makeGetList(get_audioChannel);
                break;

            case R.id.get_audioChannelSupport:
                makeGetList(get_audioChannelSupport);
                break;

            case R.id.get_cameraId:
                makeGetList(get_cameraId);
                break;

            case R.id.get_cameraIdSupport:
                makeGetList(get_cameraIdSupport);
                break;

            case R.id.get_lgISO:
                makeGetList(get_lgISO);
                break;

            case R.id.get_lgISOSupport:
                makeGetList(get_lgISOSupport);
                break;

            case R.id.get_lgShutterSpeed:
                makeGetList(get_lgShutterSpeed);
                break;

            case R.id.get_lgShutterSpeedSupport:
                makeGetList(get_lgShutterSpeedSupport);
                break;

            case R.id.get_lgWhiteBalance:
                makeGetList(get_lgWhiteBalance);
                break;

            case R.id.get_lgWhiteBalanceSupport:
                makeGetList(get_lgWhiteBalanceSupport);
                break;

            case R.id.set_button:
                setOptions();
                break;
            case R.id.set_clear_button:
                clearSetList();
                break;
        }
    }

    /**
     * Get the values of the options.
     * Returns current settings for requested properties.
     */
    private void getOptions() {
        JSONObject parameters = new JSONObject();
        JSONArray optionParam = new JSONArray(getArrayList);
        if (getArrayList.size() == 0) {
            Utils.showAlertDialog(mContext, "Note: ",
                    "Please select at least one option", null);
        } else {
            try {
                parameters.put("optionNames", optionParam);
                final OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera.getOptions", parameters);
                commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                    @Override
                    public void onResponse(OSCReturnType type, Object response) {
                        String message = Utils.parseString(response);
                        Utils.showTextDialog(mContext, getString(R.string.response), message);
                    }
                });
                commandsExecute.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Set values for specified properties.
     * For example, GPS on/off, date & time , ISO, shutter speed, sleep/power-off delay, and so on.
     */
    private void setOptions() {
        JSONObject setParam = new JSONObject();
        JSONObject optionParam = new JSONObject();

        String captureMode = set_captureMode.getText().toString();
        String exposureProgram = set_exposureProgram.getText().toString();
        String iso = set_iso.getText().toString();
        String shutterSpeed = set_shutterSpeed.getText().toString();
        String aperture = set_aperture.getText().toString();
        String whiteBalance = set_whiteBalance.getText().toString();
        String exposureCompensation = set_exposureCompensation.getText().toString();
        String fileFormat = set_fileFormat.getText().toString();
        String speedOptimized = set_speedOptimized.getText().toString();
        String previewFormat = set_previewFormat.getText().toString();
        String exposureDelay = set_exposureDelay.getText().toString();
        String captureInterval = set_captureInterval.getText().toString();
        String captureNumber = set_captureNumber.getText().toString();
        String sleepDelay = set_sleepDelay.getText().toString();
        String gpsInfo = set_gpsInfo.getText().toString();
        String dateTimeZone = set_dateTimeZone.getText().toString();
        String hdr = set_hdr.getText().toString();
        String exposureBracket = set_exposureBracket.getText().toString();
        String gyro = set_gyro.getText().toString();
        String gps = set_gps.getText().toString();
        String imageStabilization = set_imageStabilization.getText().toString();
        String wifiPassword = set_wifiPassword.getText().toString();
        String delayProcessing = set_delayProcessing.getText().toString();
        String sceneMode = set_sceneMode.getText().toString();
        String timer = set_timer.getText().toString();
        String angle = set_angle.getText().toString();
        String audioChannel = set_audioChannel.getText().toString();
        String cameraId = set_cameraId.getText().toString();
        String lgISO = set_lgISO.getText().toString();
        String lgShutterSpeed = set_lgShutterSpeed.getText().toString();
        String lgWhiteBalance = set_lgWhiteBalance.getText().toString();


        try {
            if (captureMode.getBytes().length > 0) {
                setParam.put(set_captureMode.getTag().toString(), captureMode);
            }
            if (exposureProgram.getBytes().length > 0) {
                setParam.put(set_exposureProgram.getTag().toString(), Utils.parseDouble(exposureProgram));
            }
            if (iso.getBytes().length > 0) {
                setParam.put(set_iso.getTag().toString(), Utils.parseDouble(iso));
            }
            if (shutterSpeed.getBytes().length > 0) {
                setParam.put(set_shutterSpeed.getTag().toString(), Utils.parseDouble(shutterSpeed));
            }
            if (aperture.getBytes().length > 0) {
                setParam.put(set_aperture.getTag().toString(), Utils.parseDouble(aperture));
            }
            if (whiteBalance.getBytes().length > 0) {
                setParam.put(set_whiteBalance.getTag().toString(), whiteBalance);
            }
            if (exposureCompensation.getBytes().length > 0) {
                setParam.put(set_exposureCompensation.getTag().toString(), Utils.parseDouble(exposureCompensation));
            }
            if (fileFormat.getBytes().length > 0) {
                setParam.put(set_fileFormat.getTag().toString(), new JSONObject(fileFormat));
            }
            if (speedOptimized.getBytes().length > 0) {
                setParam.put(set_speedOptimized.getTag().toString(), Utils.parseBoolean(speedOptimized));
            }
            if (previewFormat.getBytes().length > 0) {
                setParam.put(set_previewFormat.getTag().toString(), new JSONObject(previewFormat));
            }
            if (exposureDelay.getBytes().length > 0) {
                setParam.put(set_exposureDelay.getTag().toString(), Utils.parseDouble(exposureDelay));
            }
            if (captureInterval.getBytes().length > 0) {
                setParam.put(set_captureInterval.getTag().toString(), Utils.parseDouble(captureInterval));
            }
            if (captureNumber.getBytes().length > 0) {
                setParam.put(set_captureNumber.getTag().toString(), Utils.parseDouble(captureNumber));
            }
            if (sleepDelay.getBytes().length > 0) {
                setParam.put(set_sleepDelay.getTag().toString(), Utils.parseDouble(sleepDelay));
            }
            if (gpsInfo.getBytes().length > 0) {
                setParam.put(set_gpsInfo.getTag().toString(), new JSONObject(gpsInfo));
            }
            if (dateTimeZone.getBytes().length > 0) {
                setParam.put(set_dateTimeZone.getTag().toString(), dateTimeZone);
            }
            if (hdr.getBytes().length > 0) {
                setParam.put(set_hdr.getTag().toString(), hdr);
            }
            if (exposureBracket.getBytes().length > 0) {
                setParam.put(set_exposureBracket.getTag().toString(), new JSONObject(exposureBracket));
            }
            if (gyro.getBytes().length > 0) {
                setParam.put(set_gyro.getTag().toString(), Utils.parseBoolean(gyro));
            }
            if (gps.getBytes().length > 0) {
                setParam.put(set_gps.getTag().toString(), Utils.parseBoolean(gps));
            }
            if (imageStabilization.getBytes().length > 0) {
                setParam.put(set_imageStabilization.getTag().toString(), imageStabilization);
            }
            if (wifiPassword.getBytes().length > 0) {
                setParam.put(set_wifiPassword.getTag().toString(), wifiPassword);
            }
            if (delayProcessing.getBytes().length > 0) {
                setParam.put(set_delayProcessing.getTag().toString(), Utils.parseBoolean(delayProcessing));
            }
            if (sceneMode.getBytes().length > 0) {
                setParam.put(set_sceneMode.getTag().toString(), sceneMode);
            }
            if (timer.getBytes().length > 0) {
                setParam.put(set_timer.getTag().toString(), Utils.parseInt(timer));
            }
            if (angle.getBytes().length > 0) {
                setParam.put(set_angle.getTag().toString(), angle);
            }
            if (audioChannel.getBytes().length > 0) {
                setParam.put(set_audioChannel.getTag().toString(), audioChannel);
            }
            if (cameraId.getBytes().length > 0) {
                setParam.put(set_cameraId.getTag().toString(), cameraId);
            }
            if (lgISO.getBytes().length > 0) {
                setParam.put(set_lgISO.getTag().toString(), Utils.parseDouble(lgISO));
            }
            if (lgShutterSpeed.getBytes().length > 0) {
                setParam.put(set_lgShutterSpeed.getTag().toString(), lgShutterSpeed);
            }
            if (lgWhiteBalance.getBytes().length > 0) {
                setParam.put(set_lgWhiteBalance.getTag().toString(), Utils.parseDouble(lgWhiteBalance));
            }
            if (setParam.length() == 0) {
                Utils.showAlertDialog(mContext, "Note: ",
                        "Please input at least one option", null);
            } else {
                optionParam.put("options", setParam);

                final OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera.setOptions", optionParam);
                commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                    @Override
                    public void onResponse(OSCReturnType type, Object response) {

                        String message = Utils.parseString(response);
                        Utils.showTextDialog(mContext, getString(R.string.response), message);
                    }
                });
                commandsExecute.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make array list of get options
     * If ch is checked, then add array list.
     */
    private void makeGetList(CheckBox ch) {
        if (ch.isChecked()) {
            getArrayList.add(ch.getText().toString());
        } else {
            getArrayList.remove(ch.getText().toString());
        }
    }

    /**
     * Clear the list of get options
     */
    private void clearGetList() {
        get_captureMode.setChecked(false);
        get_captureModeSupport.setChecked(false);
        get_exposureProgram.setChecked(false);
        get_exposureProgramSupport.setChecked(false);
        get_iso.setChecked(false);
        get_isoSupport.setChecked(false);
        get_shutterSpeed.setChecked(false);
        get_shutterSpeedSupport.setChecked(false);
        get_aperture.setChecked(false);
        get_apertureSupport.setChecked(false);
        get_whiteBalance.setChecked(false);
        get_whiteBalanceSupport.setChecked(false);
        get_exposureCompensation.setChecked(false);
        get_exposureCompensationSupport.setChecked(false);
        get_fileFormat.setChecked(false);
        get_fileFormatSupport.setChecked(false);
        get_speedOptimized.setChecked(false);
        get_previewFormat.setChecked(false);
        get_previewFormatSupport.setChecked(false);
        get_exposureDelay.setChecked(false);
        get_exposureDelaySupport.setChecked(false);
        get_captureInterval.setChecked(false);
        get_captureIntervalSupport.setChecked(false);
        get_captureNumber.setChecked(false);
        get_captureNumberSupport.setChecked(false);
        get_sleepDelay.setChecked(false);
        get_sleepDelaySupport.setChecked(false);
        get_totalSpace.setChecked(false);
        get_remainingSpace.setChecked(false);
        get_remainingPictures.setChecked(false);
        get_remainingVideoMinutes.setChecked(false);
        get_gpsInfo.setChecked(false);
        get_dateTimeZone.setChecked(false);
        get_hdr.setChecked(false);
        get_hdrSupport.setChecked(false);
        get_exposureBracket.setChecked(false);
        get_exposureBracketSupport.setChecked(false);
        get_gyro.setChecked(false);
        get_gyroSupport.setChecked(false);
        get_gps.setChecked(false);
        get_gpsSupport.setChecked(false);
        get_imageStabilization.setChecked(false);
        get_imageStabilizationSupport.setChecked(false);
        get_wifiPassword.setChecked(false);
        get_delayProcessing.setChecked(false);
        get_delayProcessingSupport.setChecked(false);
        get_sceneMode.setChecked(false);
        get_sceneModeSupport.setChecked(false);
        get_timer.setChecked(false);
        get_timerSupport.setChecked(false);
        get_storage.setChecked(false);
        get_batteryLevel.setChecked(false);
        get_angle.setChecked(false);
        get_angleSupport.setChecked(false);
        get_audioChannel.setChecked(false);
        get_audioChannelSupport.setChecked(false);
        get_cameraId.setChecked(false);
        get_cameraIdSupport.setChecked(false);
        get_lgISO.setChecked(false);
        get_lgISOSupport.setChecked(false);
        get_lgShutterSpeed.setChecked(false);
        get_lgShutterSpeedSupport.setChecked(false);
        get_lgWhiteBalance.setChecked(false);
        get_lgWhiteBalanceSupport.setChecked(false);

        getArrayList.clear();
    }

    /**
     * Clear the list of set options
     */
    private void clearSetList() {
        set_captureMode.setText("");
        set_exposureProgram.setText("");
        set_iso.setText("");
        set_shutterSpeed.setText("");
        set_aperture.setText("");
        set_whiteBalance.setText("");
        set_exposureCompensation.setText("");
        set_fileFormat.setText("");
        set_speedOptimized.setText("");
        set_previewFormat.setText("");
        set_exposureDelay.setText("");
        set_captureInterval.setText("");
        set_captureNumber.setText("");
        set_sleepDelay.setText("");
        set_gpsInfo.setText("");
        set_dateTimeZone.setText("");
        set_hdr.setText("");
        set_exposureBracket.setText("");
        set_gyro.setText("");
        set_gps.setText("");
        set_imageStabilization.setText("");
        set_wifiPassword.setText("");
        set_delayProcessing.setText("");
        set_sceneMode.setText("");
        set_timer.setText("");
        set_angle.setText("");
        set_audioChannel.setText("");
        set_cameraId.setText("");
        set_lgISO.setText("");
        set_lgShutterSpeed.setText("");
        set_lgWhiteBalance.setText("");
    }
}