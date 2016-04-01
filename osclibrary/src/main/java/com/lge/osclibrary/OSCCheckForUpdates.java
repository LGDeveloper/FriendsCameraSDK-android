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
package com.lge.osclibrary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * CheckForUpdates
 * API: /osc/checkForUpdates
 */
public class OSCCheckForUpdates extends HttpAsyncTask {
    private final static String TAG = "OSCCheckForUpdates";

    private static final String URL =
            HTTP_SERVER_INFO.IP + ":" + HTTP_SERVER_INFO.PORT + "/osc/checkForUpdates";

    private String fingerPrint;
    private int waitTimeout;

    /**
     * Constructor
     *
     * @param fingerPrint value to compare state
     */
    public OSCCheckForUpdates(String fingerPrint) {
        //Set url and http method as POST
        super(URL, HttpHeaderPropertyNameMapper.POST);
        this.fingerPrint = fingerPrint;
        this.waitTimeout = -1; // omitted
    }

    /**
     * Constructor
     *
     * @param fingerPrint value to compare state
     * @param waitTimeout value to set timeout
     */
    public OSCCheckForUpdates(String fingerPrint, int waitTimeout) {
        //Set url and http method as POST
        super(URL, HttpHeaderPropertyNameMapper.POST);
        this.fingerPrint = fingerPrint;
        this.waitTimeout = waitTimeout;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        //Set body for /osc/checkForUpdates API
        JSONObject data = new JSONObject();

        try {
            data.put(OSCParameterNameMapper.LOCAL_FINGERPRINT, fingerPrint);
            if (waitTimeout != -1)
                data.put(OSCParameterNameMapper.TIMEOUT, waitTimeout);
            setHttpRequestData(data.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.doInBackground(voids);
    }
}
