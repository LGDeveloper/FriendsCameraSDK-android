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

import com.lge.osclibrary.FriendsCameraSettings;
import com.lge.osclibrary.HttpAsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

/**
 * get/set settings of friends camera
 */
public class SettingsActivity extends AppCompatActivity implements OnClickListener {
    private final static String TAG = SettingsActivity.class.getSimpleName();
    private Context mContext;

    private Button get_button = null;
    private Button get_clear_button = null;
    private CheckBox get_wifiPW = null;
    private CheckBox get_sound = null;
    private CheckBox get_batteryLevel = null;
    private CheckBox get_batteryState = null;
    private CheckBox get_plugType = null;
    private CheckBox get_sleepTime = null;
    private CheckBox get_totalCapacity = null;
    private CheckBox get_freeCapacity = null;

    private Button set_button = null;
    private Button set_clear_button = null;
    private EditText set_wifiPW = null;
    private EditText set_sound = null;
    private EditText set_sleepTime = null;

    private ArrayList<String> getArrayList = new ArrayList<>();

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
        setContentView(R.layout.settings_layout);

        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        get_button = (Button) findViewById(R.id.get_button);
        get_clear_button = (Button) findViewById(R.id.get_clear_button);
        get_wifiPW = (CheckBox) findViewById(R.id.get_wifiPW);
        get_sound = (CheckBox) findViewById(R.id.get_sound);
        get_batteryLevel = (CheckBox) findViewById(R.id.get_batteryLevel);
        get_batteryState = (CheckBox) findViewById(R.id.get_batteryState);
        get_plugType = (CheckBox) findViewById(R.id.get_plugType);
        get_sleepTime = (CheckBox) findViewById(R.id.get_sleepTime);
        get_totalCapacity = (CheckBox) findViewById(R.id.get_totalCapacity);
        get_freeCapacity = (CheckBox) findViewById(R.id.get_freeCapacity);

        set_button = (Button) findViewById(R.id.set_button);
        set_clear_button = (Button) findViewById(R.id.set_clear_button);
        set_wifiPW = (EditText) findViewById(R.id.set_wifiPW);
        set_sound = (EditText) findViewById(R.id.set_sound);
        set_sleepTime = (EditText) findViewById(R.id.set_sleepTime);

        get_button.setOnClickListener(this);
        get_clear_button.setOnClickListener(this);
        get_wifiPW.setOnClickListener(this);
        get_sound.setOnClickListener(this);
        get_batteryLevel.setOnClickListener(this);
        get_batteryState.setOnClickListener(this);
        get_plugType.setOnClickListener(this);
        get_sleepTime.setOnClickListener(this);
        get_totalCapacity.setOnClickListener(this);
        get_freeCapacity.setOnClickListener(this);
        set_button.setOnClickListener(this);
        set_clear_button.setOnClickListener(this);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.get_button:
                getSettings();
                break;
            case R.id.get_clear_button:
                clearGetList();
                break;
            case R.id.get_wifiPW:
                makeGetList(get_wifiPW);
                break;
            case R.id.get_sound:
                makeGetList(get_sound);
                break;
            case R.id.get_batteryLevel:
                makeGetList(get_batteryLevel);
                break;
            case R.id.get_batteryState:
                makeGetList(get_batteryState);
                break;
            case R.id.get_plugType:
                makeGetList(get_plugType);
                break;
            case R.id.get_sleepTime:
                makeGetList(get_sleepTime);
                break;
            case R.id.get_totalCapacity:
                makeGetList(get_totalCapacity);
                break;
            case R.id.get_freeCapacity:
                makeGetList(get_freeCapacity);
                break;

            case R.id.set_button:
                setSettings();
                break;
            case R.id.set_clear_button:
                clearSetList();
                break;
        }
    }

    /**
     * Make array list of get settings.
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
     * Get the values of the setting options.
     * Can get multiple option values by listing the key names of the options in a String Array.
     * API : /settings/get
     */
    private void getSettings() {
        JSONArray getParams = new JSONArray(getArrayList);
        if (getArrayList.size() == 0) {
            Utils.showAlertDialog(mContext, "Note: ",
                    "Please select at least one option", null);
        } else {
            final FriendsCameraSettings settings = new FriendsCameraSettings("get", getParams);
            settings.setListener(new HttpAsyncTask.OnHttpListener() {
                @Override
                public void onResponse(OSCReturnType type, Object response) {
                    Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
                }
            });
            settings.execute();
        }
    }

    /**
     * Set the values to the setting options.
     * Can set multiple option values by listing the JSON key-value pairs of the options.
     * API : /settings/set
     */
    private void setSettings() {
        JSONObject setParam = new JSONObject();
        JSONArray parameters = new JSONArray();

        String wifiPW = set_wifiPW.getText().toString();
        String sound = set_sound.getText().toString();
        String sleepTime = set_sleepTime.getText().toString();

        try {
            if (wifiPW.getBytes().length > 0) {
                setParam.put(set_wifiPW.getTag().toString(), wifiPW);
            }
            if (sound.getBytes().length > 0) {
                setParam.put(set_sound.getTag().toString(), Utils.parseInt(sound));
            }
            if (sleepTime.getBytes().length > 0) {
                setParam.put(set_sleepTime.getTag().toString(), Utils.parseInt(sleepTime));
            }

            if (setParam.length() == 0) {
                Utils.showAlertDialog(mContext, "Note: ",
                        "Please input at least one option", null);
            } else {
                parameters.put(setParam);
                final FriendsCameraSettings settings = new FriendsCameraSettings("set", parameters);
                settings.setListener(new HttpAsyncTask.OnHttpListener() {
                    @Override
                    public void onResponse(OSCReturnType type, Object response) {
                        Utils.showTextDialog(mContext, getString(R.string.response), Utils.parseString(response));
                    }
                });
                settings.execute();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear the list of get settings
     */
    private void clearGetList() {
        get_wifiPW.setChecked(false);
        get_sound.setChecked(false);
        get_batteryLevel.setChecked(false);
        get_batteryState.setChecked(false);
        get_plugType.setChecked(false);
        get_sleepTime.setChecked(false);
        get_totalCapacity.setChecked(false);
        get_freeCapacity.setChecked(false);
        getArrayList.clear();
    }

    /**
     * Clear the list of set settings
     */
    private void clearSetList() {
        set_wifiPW.setText("");
        set_sound.setText("");
        set_sleepTime.setText("");
    }
}
