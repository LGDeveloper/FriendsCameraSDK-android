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

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * get preview of friends camera
 */
public class PreviewActivity extends AppCompatActivity {
    private final static String TAG = PreviewActivity.class.getSimpleName();
    private Context mContext;
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_getlivepreview:
                    Intent i = new Intent(mContext, LivePreviewActivity.class);
                    startActivity(i);
                    break;
                case R.id.button_startpreview:
                    startPreview();
                    break;
                case R.id.button_stoppreview:
                    stopPreview();
                    break;

            }
        }
    };
    private Button buttonLivePreview;
    private Button buttonStartPreview;
    private Button buttonStopPreview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpView();
        initialize();
    }

    private void setUpView() {
        setContentView(R.layout.preview_layout);

        getSupportActionBar().setTitle(R.string.preview);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buttonLivePreview = (Button) findViewById(R.id.button_getlivepreview);
        buttonStartPreview = (Button) findViewById(R.id.button_startpreview);
        buttonStopPreview = (Button) findViewById(R.id.button_stoppreview);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);

        buttonLivePreview.setOnClickListener(clickListener);
        buttonStartPreview.setOnClickListener(clickListener);
        buttonStopPreview.setOnClickListener(clickListener);
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
     * start preview
     * API : /osc/commands/execute (camera._startPreview)
     */
    private void startPreview() {
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("sessionId", "LGFRIENDSCAMERA");
            parameter.put("_streamType", "UDP");
            OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera._startPreview", parameter);
            commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                @Override
                public void onResponse(OSCReturnType type, Object response) {
                    Utils.showTextDialog(mContext, getString(R.string.response),
                            Utils.parseString(response));
                }
            });
            commandsExecute.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * stop preview
     * API : /osc/commands/execute (camera._stopPreview)
     */
    private void stopPreview() {
        JSONObject parameter = new JSONObject();
        try {
            parameter.put("sessionId", "LGFRIENDSCAMERA");
            OSCCommandsExecute commandsExecute = new OSCCommandsExecute("camera._stopPreview", parameter);
            commandsExecute.setListener(new HttpAsyncTask.OnHttpListener() {
                @Override
                public void onResponse(OSCReturnType type, Object response) {
                    Utils.showTextDialog(mContext, getString(R.string.response),
                            Utils.parseString(response));
                }
            });
            commandsExecute.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
