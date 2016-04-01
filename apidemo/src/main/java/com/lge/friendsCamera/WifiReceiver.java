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

import com.lge.octopus.ConnectionManager;
import com.lge.octopus.OctopusManager;
import com.lge.octopus.tentacles.wifi.client.WifiClient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Broadcast receiver to catch changing wifi status
 */
public class WifiReceiver extends BroadcastReceiver {
    private final static String TAG = WifiReceiver.class.getSimpleName();
    private static boolean isCamera = false;

    /**
     * disconnect Wi-Fi
     */
    public static void disconnectWifi(Context context) {
        ConnectionManager mConnectionManager = OctopusManager.getInstance(context).getConnectionManager();
        mConnectionManager.disconnect();
    }

    /**
     * Check whether friends camera is connected or not
     */
    public static boolean isConnected() {
        Log.d(TAG, "check is connected " + isCamera);
        return isCamera;
    }

    /**
     * Check wifi status when connection status is changed.
     * Change app status based on wifi connection with camera
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "RECEIVED SIGNAL");

        String strAction = intent.getAction();
        Log.d(TAG, "Action " + strAction);

        int result = intent.getIntExtra(WifiClient.EXTRA_RESULT, WifiClient.RESULT.DISCONNECTED);
        Log.d(TAG, " Result = " + result);

        if (WifiClient.ACTION_WIFI_STATE.equals(strAction)) {
            if (result == WifiClient.RESULT.CONNECTED) {
                isCamera = true;
            } else {
                isCamera = false;
            }
        }

        Context currentActivityContext = FriendsCameraApplication.getContext();
        String currentActivityName = currentActivityContext.getClass().getSimpleName();


        if (currentActivityName.equals(MainActivity.class.getSimpleName())) {
            if (isCamera) {
                setMainUI(currentActivityContext, true);
            } else {
                setMainUI(currentActivityContext, false);
            }
        } else {
            if (!isCamera) {
                //Disconnected and not Main activity
                boolean isException = false;
                String[] exceptionLists = new String[]{
                        DownloadFileListViewActivity.class.getSimpleName(),
                        ConnectionActivity.class.getSimpleName()};
                for (String exception : exceptionLists) {
                    if (currentActivityName.equals(exception)) {
                        isException = true;
                        break;
                    }
                }
                if (!isException) {
                    startMain(currentActivityContext);
                }
            }
        }
    }

    /**
     * set Main UI
     */
    private void setMainUI(Context context, boolean enable) {
        Log.d(TAG, "setMainUI = " + enable);
        ((MainActivity) context).updateStateBasedOnWifiConnection(enable);
    }

    /**
     * start Main Activity when friends camera is disconnected.
     */
    private void startMain(Context context) {
        Log.d(TAG, "startMain");
        Intent tempIntent = new Intent(context, MainActivity.class);
        tempIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(tempIntent);
        Toast.makeText(context, "Loose connection with camera. Go back to Main", Toast.LENGTH_SHORT).show();
    }
}

