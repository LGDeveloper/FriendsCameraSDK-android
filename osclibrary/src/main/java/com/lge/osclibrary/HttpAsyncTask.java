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

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Send http request and get response
 * Other classes for osc protocol extends this class
 */
public class HttpAsyncTask extends AsyncTask<Void, Void, Object> {
    private final static String TAG = "HttpAsyncTask";

    protected URL mURL;
    //Http header property
    protected HashMap<String, String> mHttpRequestProperties = new HashMap<>();
    //Cancel callback interface
    //  cancel http request
    protected OnCancelCallback mOnCancelCallback;
    private HttpURLConnection mHttpURLConnection = null;
    private String mHttpRequestMethod;
    private String mHttpRequestData;
    private boolean errorFlag = false; //Check response code is 200 or not
    private OnHttpListener mListener;

    /**
     * Constructor
     *
     * @param url               url of http request
     * @param httpRequestMethod GET / POST
     */
    public HttpAsyncTask(String url, String httpRequestMethod) {
        String urlString = "http://" + url;
        try {
            mURL = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        mHttpRequestMethod = httpRequestMethod;

        Log.v(TAG, urlString + ", " + httpRequestMethod);
    }

    public void setListener(OnHttpListener listener) {
        mListener = listener;
    }

    public void setOnCancelCallback(OnCancelCallback onCancelCallback) {
        mOnCancelCallback = onCancelCallback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * Check whether http request needs to send or not
     *
     * @return boolean; true / false
     */
    protected boolean isCanceled() {
        boolean isCancel = false;
        if (mOnCancelCallback != null) {
            isCancel = mOnCancelCallback.cancelBackground(null);
        }
        return isCancel;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        //Check this request is still available
        if (isCanceled())
            return false;
        //Update http request header
        updateProperties();
        //Send request and get response from server
        Object response = request();

        return response;
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        handleResponse(result);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected void onCancelled(Object result) {
        super.onCancelled(result);
    }

    /**
     * Callback with response data and request result (SUCCESS / FAIL)
     *
     * @param response response data from server
     */
    protected void handleResponse(Object response) {
        OnHttpListener.OSCReturnType type;
        if (mListener != null) {
            if (errorFlag) {
                type = OnHttpListener.OSCReturnType.FAIL;
            } else
                type = OnHttpListener.OSCReturnType.SUCCESS;
            mListener.onResponse(type, response);
        }
    }

    /**
     * Update http header property
     * Sub class can override this method to set particular http header property
     */
    protected void updateProperties() {
        Log.d(TAG, "###Parent_UpdateProperties");

        mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.HOST
                , HTTP_SERVER_INFO.IP + ":" + HTTP_SERVER_INFO.PORT);
        mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.ACCEPT,
                HttpHeaderPropertyNameMapper.ACCEPT_JSON);
        mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.X_XSRF, "1");

        //For POST request with body
        if (mHttpRequestMethod.equals(HttpHeaderPropertyNameMapper.POST)
                && (mHttpRequestData != null)) {
            mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.CONTENT_LENGTH,
                    String.valueOf(mHttpRequestData.length()));
            mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.CONTENT_TYPE,
                    HttpHeaderPropertyNameMapper.CONTENT_JSON);
        }
    }

    /**
     * Set http body with data
     *
     * @param data request data
     */
    protected void setHttpRequestData(String data) {
        mHttpRequestData = data;
    }

    /**
     * Send request to server and get response
     *
     * @return response from server
     */
    private Object request() {
        try {
            mHttpURLConnection = (HttpURLConnection) mURL.openConnection();

            for (HashMap.Entry<String, String> entry : mHttpRequestProperties.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                mHttpURLConnection.setRequestProperty(key, value);
            }

            //Send request
            mHttpURLConnection.setDoInput(true);
            mHttpURLConnection.setRequestMethod(mHttpRequestMethod);
            if (mHttpRequestMethod.equals(HttpHeaderPropertyNameMapper.POST)) {
                mHttpURLConnection.setDoOutput(true);
                if (mHttpRequestData != null) {
                    OutputStream os = mHttpURLConnection.getOutputStream();
                    os.write(mHttpRequestData.getBytes("UTF-8"));
                    os.flush();
                    os.close();
                    Log.v(TAG, "OutputData:" + ": " + mHttpRequestData);
                }
            }

            //Get request result
            int responseCode = mHttpURLConnection.getResponseCode();
            Log.v(TAG, "ResponseCode: " + responseCode);

            InputStream is;
            if (responseCode == 200) {
                //Normal response
                is = mHttpURLConnection.getInputStream();
                errorFlag = false;
            } else {
                //Error response
                is = mHttpURLConnection.getErrorStream();
                errorFlag = true;
            }
            Object response = parseResponse(is);

            return response;
        } catch (IOException e) {
            Log.v(TAG, "Error: Fail to handle response from input stream in function");
            e.printStackTrace();
        } finally {
            if (isEnabledAutoDisconnect() && (mHttpURLConnection != null)) {
                mHttpURLConnection.disconnect();
            }
        }
        return null;
    }

    /**
     * Enable or disable auto disconnect
     * Sub class can override this method to disable auto disconnect
     *
     * @return true (general case)
     */
    protected boolean isEnabledAutoDisconnect() {
        return true;
    }

    /**
     * Read response data and convert to string
     * Sub class can override this class to parse response as particular data type
     *
     * @param inputStream response stream data from server
     * @return response data
     */
    protected Object parseResponse(InputStream inputStream) {
        Log.v(TAG, "ParseResponse START");
        StringBuffer response = new StringBuffer();
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }

            inputStream.close();
            Log.v(TAG, "Response:" + ": " + response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "ParseResponse END");
        return response.toString();
    }

    //Listener for http response from server
    public interface OnHttpListener {
        void onResponse(OSCReturnType type, Object response);

        enum OSCReturnType {SUCCESS, FAIL}
    }

    public interface OnCancelCallback {
        boolean cancelBackground(Object object);
    }
}
