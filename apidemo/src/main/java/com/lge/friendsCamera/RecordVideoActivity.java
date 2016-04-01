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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Record video, check recording status and live snapshot during recording
 * Before start recording video, 'captureMode' should be set as 'video'
 */
public class RecordVideoActivity extends AppCompatActivity implements View.OnClickListener {
    private final static String TAG = RecordVideoActivity.class.getSimpleName();
    private static final String START = "camera.startCapture";
    private static final String RESUME = "camera._resumeRecording";
    private static final String PAUSE = "camera._pauseRecording";
    private static final String STOP = "camera.stopCapture";
    final String optionCaptureMode = "captureMode";
    private Context mContext;
    private recordState currentRecordState;
    private Button buttonRecording;
    private Button buttonStop;
    private Button buttonLiveSnapShot;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupViews();
        initialize();
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);

        //getOption
        getOptionCaptureMode();
        currentRecordState = recordState.STOP_RECORDING;
    }

    private void setupViews() {
        setContentView(R.layout.recordvideo_layout);

        getSupportActionBar().setTitle(R.string.recording);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button buttonRecordingStatus = (Button) findViewById(R.id.button_recordingStatus);
        buttonRecordingStatus.setOnClickListener(this);

        buttonRecording = (Button) findViewById(R.id.button_startVideo);
        buttonRecording.setOnClickListener(this);

        buttonStop = (Button) findViewById(R.id.button_stopVideo);
        buttonStop.setOnClickListener(this);

        buttonLiveSnapShot = (Button) findViewById(R.id.button_liveSnapShot);
        buttonLiveSnapShot.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_recordingStatus:
                getRecordingStatus();
                break;
            case R.id.button_startVideo:
                startVideo();
                break;
            case R.id.button_stopVideo:
                stopVideo();
                break;
            case R.id.button_liveSnapShot:
                liveSnapShot();
                break;
        }
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
     * Captures an image during recordings, saving lat/long coordinates to EXIF
     * This api is enable only during recording and half mode (180 degree picture)
     * API : /osc/commands/execute (camera._liveSnapshot)
     */
    private void liveSnapShot() {
        mProgressDialog = ProgressDialog.show(mContext, "", "Processing...", true, false);
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera._liveSnapshot", null);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                String state = Utils.getCommandState(response);
                if (state != null && state.equals(OSCParameterNameMapper.STATE_INPROGRESS)) {
                    String commandId = Utils.getCommandId(response);
                    checkCommandsStatus(commandId);
                } else {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.cancel();
                    Utils.showTextDialog(mContext, "Response  ", Utils.parseString(response));
                }
            }
        });
        commandsExecute.execute();
    }

    /**
     * start/resume/pause recording video
     */
    private void startVideo() {
        mProgressDialog = ProgressDialog.show(mContext, "", "Waiting..", true, false);
        if (currentRecordState == recordState.STOP_RECORDING) {
            changeRecordingStatus(START);
        } else if (currentRecordState == recordState.PAUSE_RECORDING) {
            changeRecordingStatus(RESUME);
        } else {  //is recording
            changeRecordingStatus(PAUSE);
        }
    }

    /**
     * stop recording video
     */
    private void stopVideo() {
        //Stop Recording
        mProgressDialog = ProgressDialog.show(mContext, "", "Waiting..", true, false);
        changeRecordingStatus(STOP);
    }

    /**
     * get recording status
     * API : /osc/commands/execute (camera._getRecordingStatus)
     */
    private void getRecordingStatus() {
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera._getRecordingStatus", null);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
            }
        });
        commandsExecute.execute();
    }

    /**
     * get captureMode option
     * API: /osc/commands/execute (camera.getOptions)
     */
    private void getOptionCaptureMode() {
        JSONObject parameters = new JSONObject();

        try {
            JSONArray optionParameter = new JSONArray();
            optionParameter.put(optionCaptureMode);

            parameters.put(OSCParameterNameMapper.Options.OPTIONNAMES, optionParameter);
            OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera.getOptions", parameters);

            commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                @Override
                public void onResponse(OSCReturnType type, Object response) {
                    try {

                        if (type == OSCReturnType.SUCCESS) {
                            //If the getOption request get response successfully,
                            //check whether the mode is video or not
                            JSONObject jObject = new JSONObject((String) response);

                            JSONObject results = jObject.getJSONObject(OSCParameterNameMapper.RESULTS);
                            JSONObject options = results.getJSONObject(OSCParameterNameMapper.Options.OPTIONS);
                            String captureMode = options.getString(optionCaptureMode);

                            if (!captureMode.equals("video")) {
                                //Ask user whether change captureMode as 'video' or not
                                //  yes = Send request to captureMode as video(camera.setOption)
                                //  no = finish this activity
                                DialogInterface.OnClickListener okListener =
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                mProgressDialog = ProgressDialog.show
                                                        (mContext, "", "Setting..", true, false);
                                                setCaptureModeVideo();
                                            }
                                        };
                                DialogInterface.OnClickListener cancelListener =
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((RecordVideoActivity) mContext).finish();
                                            }
                                        };

                                Utils.showSelectDialog(
                                        mContext, "Note: ",
                                        "Do you want to change captureMode to 'video'?",
                                        okListener, cancelListener);
                            }
                        } else {
                            Utils.showTextDialog(mContext, getString(R.string.response),
                                    Utils.parseString(response));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            commandsExecute.execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * change captureMode to video
     * API: /osc/commands/execute (camera.setOptions)
     */
    private void setCaptureModeVideo() {
        JSONObject setParam = new JSONObject();
        JSONObject optionParam = new JSONObject();

        try {
            setParam.put("captureMode", "video");
            optionParam.put("options", setParam);

            OSCCommandsExecute commandsExecute =
                    new OSCCommandsExecute("camera.setOptions", optionParam);
            commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                @Override
                public void onResponse(OSCReturnType type, Object response) {
                    if (mProgressDialog != null)
                        mProgressDialog.cancel();

                    if (type == OSCReturnType.SUCCESS) {
                        Toast.makeText(mContext, "Set captureMode to 'video' successfully",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
                    }
                }
            });
            commandsExecute.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(TAG, "RecordVideo onResume");
        if (!WifiReceiver.isConnected()) {
            ((RecordVideoActivity) mContext).finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currentRecordState != recordState.STOP_RECORDING) {
            changeRecordingStatus(STOP);
        }
        Log.v(TAG, "RecordVideo onDestroy");
    }

    /**
     * Change recording status
     * API : /osc/commands/execute
     * (camera.startCapture, camera._resumeRecording, camera._pauseRecording )
     *
     * @param command START / RESUME / PAUSE / STOP
     */
    private void changeRecordingStatus(final String command) {
        OSCCommandsExecute commandsExecute = new OSCCommandsExecute(command, null);
        commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, Object response) {
                Log.v(TAG, "command :: " + command);
                if (type == OSCReturnType.SUCCESS) {
                    String state = Utils.getCommandState(response);
                    Log.v(TAG, "state = " + state);
                    if (state != null) {
                        if (state.equals(OSCParameterNameMapper.STATE_INPROGRESS)) {
                            String commandId = Utils.getCommandId(response);
                            checkCommandsStatus(commandId);
                            return;
                        } else {
                            // state == done
                            setRecordingState(command);
                            /*if (mProgressDialog.isShowing())
                                mProgressDialog.cancel();*/
                        }
                    }
                }
                Utils.showTextDialog(mContext, getString(R.string.response),
                        Utils.parseString(response));
                if (mProgressDialog.isShowing())
                    mProgressDialog.cancel();

            }
        });
        commandsExecute.execute();
    }

    /**
     * Check the status for previous inProgress commands.
     * Determine whether start/resume/pause recording and liveSnapshot have completed.
     *
     * @param commandId command Id of previous request
     *                  API : /osc/commands/status
     */
    private void checkCommandsStatus(final String commandId) {
        final OSCCommandsStatus commandsStatus = new OSCCommandsStatus(commandId);
        commandsStatus.setListener(new HttpAsyncTask.OnHttpListener() {
            @Override
            public void onResponse(OSCReturnType type, final Object response) {
                if (type == OSCReturnType.SUCCESS) {
                    String state = Utils.getCommandState((String) response);
                    Log.v(TAG, "state = " + state);
                    if (state != null && state.equals(OSCParameterNameMapper.STATE_INPROGRESS)) {
                        checkCommandsStatus(commandId);
                    } else {
                        if (mProgressDialog.isShowing())
                            mProgressDialog.cancel();
                        Utils.showTextDialog(mContext, getString(R.string.response),
                                Utils.parseString(response));
                        updateUIBasedOnResponse(response);
                    }
                } else {
                    Utils.showTextDialog(mContext, getString(R.string.response),
                            Utils.parseString(response));
                }
            }
        });
        commandsStatus.execute();
    }

    private void updateUIBasedOnResponse(Object response) {
        String commandName = Utils.getCommandName(response);
        if (!commandName.equals("camera._liveSnapshot")) {
            setRecordingState(commandName);
        }
    }

    /**
     * set recording state
     *
     * @param command START / RESUME / PAUSE / STOP
     */
    private void setRecordingState(String command) {
        //change recording status and UI button
        Log.v(TAG, "Change recording state from " + command);
        if (command.equals(START)) {
            currentRecordState = recordState.IS_RECORDING;
        } else if (command.equals(RESUME)) {
            currentRecordState = recordState.IS_RECORDING;
        } else if (command.equals(PAUSE)) {
            currentRecordState = recordState.PAUSE_RECORDING;
        } else { //camera._stopRecording
            currentRecordState = recordState.STOP_RECORDING;
        }
        setRecordingButton();
    }

    /**
     * set recording button
     */
    private void setRecordingButton() {
        if (currentRecordState == recordState.STOP_RECORDING) {
            buttonRecording.setText(R.string.start_recording);
        } else if (currentRecordState == recordState.PAUSE_RECORDING) {
            buttonRecording.setText(R.string.resume_recording);
        } else {   //IS_RECORDING
            buttonRecording.setText(R.string.pause_recording);
        }
    }

    enum recordState {STOP_RECORDING, IS_RECORDING, PAUSE_RECORDING}
}
