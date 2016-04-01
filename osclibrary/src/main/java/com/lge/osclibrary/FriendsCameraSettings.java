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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FriendsCameraSettings extends HttpAsyncTask {
    private final static String TAG = "Settings";

    private static final String URL =
            HTTP_SERVER_INFO.IP + ":" + HTTP_SERVER_INFO.PORT + "/settings/";

    private final JSONArray mParameters;

    /**
     * Constructor
     *
     * @param method     setting method (set / get)
     * @param parameters parameter for method
     */
    public FriendsCameraSettings(String method, JSONArray parameters) {
        //Set url and http method as POST
        super(URL + method, HttpHeaderPropertyNameMapper.POST);
        mParameters = parameters;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        //Set body for /settings/get or /settings/set
        JSONObject data = new JSONObject();
        try {
            if (mParameters != null) {
                data.put(OSCParameterNameMapper.PARAMETERS, mParameters);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        setHttpRequestData(data.toString());
        return super.doInBackground(voids);
    }
}
