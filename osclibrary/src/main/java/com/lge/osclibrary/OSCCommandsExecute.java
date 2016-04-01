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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Execute
 * API: /osc/commands/execute
 */
public class OSCCommandsExecute extends HttpAsyncTask {
    private final static String TAG = "OSCCommandsExecute";

    private static final String URL =
            HTTP_SERVER_INFO.IP + ":" + HTTP_SERVER_INFO.PORT + "/osc/commands/execute";

    private final String mCommand;
    private final JSONObject mParameters;
    private CommandType commandType;

    /**
     * Constructor (For return content type in JSON)
     *
     * @param command    command name to execute
     * @param parameters parameter for the command
     */
    public OSCCommandsExecute(String command, JSONObject parameters) {
        //Set url and http method as POST
        super(URL, HttpHeaderPropertyNameMapper.POST);
        Log.d(TAG, "2Params: COMMAND = " + command);
        mCommand = command;
        mParameters = parameters;
        commandType = CommandType.STRING;
    }

    /**
     * Constructor (For return content type in IMAGE, VIDEO, PREVIEW)
     *
     * @param command    command name
     * @param parameters parameter for command
     * @param type       expected response data type for command
     */
    public OSCCommandsExecute(String command, JSONObject parameters, CommandType type) {
        //Set url and http method as POST
        super(URL, HttpHeaderPropertyNameMapper.POST);
        Log.d(TAG, "3Params: COMMAND = " + command);
        mCommand = command;
        mParameters = parameters;
        commandType = type;
    }

    /**
     * Return location of downloaded files from friends device in local storage
     *
     * @return local directory uri for friends camera
     */
    public static String getFileLocation() {
        String dcimDirectory = Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DCIM).toString();

        return dcimDirectory + "/friendsCameraSample";
    }

    /**
     * Set commandType to image
     */
    public void setForImage() {
        commandType = CommandType.IMAGE;
    }

    /**
     * Set commandType to video
     */
    public void setForVideo() {
        commandType = CommandType.VIDEO;
    }

    /**
     * Set commandType to preview
     */
    public void setForPreview() {
        commandType = CommandType.PREVIEW;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        //Set body for /osc/commands/execute API
        JSONObject data = new JSONObject();
        try {
            data.put(OSCParameterNameMapper.NAME, mCommand);
            if (mParameters != null) {
                data.put(OSCParameterNameMapper.PARAMETERS, mParameters);
            }
        } catch (JSONException e) {
            Log.v(TAG, "Error: Json error for put data in function");
            e.printStackTrace();
        }
        setHttpRequestData(data.toString());
        return super.doInBackground(voids);
    }

    /**
     * Set http header property 'Accept' as expected response data type
     */
    @Override
    protected void updateProperties() {
        super.updateProperties();
        Log.d(TAG, "###Child_UpdateProperties");

        if (commandType == CommandType.IMAGE) {
            mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.ACCEPT,
                    HttpHeaderPropertyNameMapper.ACCEPT_IMAGE);
        } else if (commandType == CommandType.VIDEO) {
            if (mParameters.has(OSCParameterNameMapper.MAXSIZE)) {
                //thumbnail
                mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.ACCEPT,
                        HttpHeaderPropertyNameMapper.ACCEPT_IMAGE);
            } else {
                mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.ACCEPT,
                        HttpHeaderPropertyNameMapper.ACCEPT_VIDEO);
            }
        } else if (commandType == CommandType.PREVIEW) {
            mHttpRequestProperties.put(HttpHeaderPropertyNameMapper.ACCEPT,
                    HttpHeaderPropertyNameMapper.ACCEPT_PREVIEW);
        }
    }

    /**
     * Disable auto disconnect if it is livePreview command
     *
     * @return false (for camear.getLivePreview api) / true (for other commands)
     */
    @Override
    protected boolean isEnabledAutoDisconnect() {
        if (mCommand.contains("getLivePreview"))
            return false;
        else
            return super.isEnabledAutoDisconnect();
    }

    /**
     * Read response data and convert to particular data type depending on the command
     *
     * @param inputStream response stream data from server
     * @return response data
     */
    @Override
    protected Object parseResponse(InputStream inputStream) {
        Log.d(TAG, "----------------------------------- Child parse response ===== " + commandType);
        if (commandType == CommandType.IMAGE) {
            try {
                if (mParameters.has(OSCParameterNameMapper.MAXSIZE)) {
                    //For thumbnail, return  bitmap image
                    return BitmapFactory.decodeStream(inputStream);
                } else {
                    //For full image, save image in local storage
                    return saveBitmap(mParameters.getString(OSCParameterNameMapper.FILEURL),
                            BitmapFactory.decodeStream(inputStream));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (commandType == CommandType.VIDEO) {
            try {
                if (mParameters.has(OSCParameterNameMapper.MAXSIZE)) {
                    //For thumbnail, return bitmap image
                    return BitmapFactory.decodeStream(inputStream);
                } else {
                    //For full video, save video in local storage
                    return saveVideo(mParameters.getString(OSCParameterNameMapper.FILEURL),
                            inputStream);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (commandType == CommandType.PREVIEW) {
            //For preview(getLivePreview commands), return bitmap image
            return BitmapFactory.decodeStream(inputStream);
        }

        return super.parseResponse(inputStream);
    }

    /**
     * Save image data from friends camera in local storage
     *
     * @param uri    uri to save file
     * @param bitmap bitmap data to save file
     * @return uri of the saved file. if saving image fails, then return null
     */
    private String saveBitmap(String uri, Bitmap bitmap) {
        OutputStream outStream;

        String mTargetDirectory = getFileLocation();
        createFolder(mTargetDirectory);

        String[] parseUri = uri.split("/");
        //Get file name
        String name = parseUri[parseUri.length - 1];
        String fileUri = mTargetDirectory + File.separator + name;
        File file = new File(fileUri);

        if (bitmap == null) {
            Log.d(TAG, "ERROR: bitmap is empty");
            throw new RuntimeException("bitmap is null");
        }

        try {
            boolean res = file.createNewFile();
            if (!res) {
                Log.d(TAG, "ERROR: Fail to open file " + fileUri);
                return null;
            }

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return fileUri;
    }

    /**
     * Save video data from friends camera in local storage
     *
     * @param uri    uri to save file
     * @param stream input stream which contains video data
     * @return uri of save file. if saving video fails, then return null
     */
    private String saveVideo(String uri, InputStream stream) {
        String mTargetDirectory = getFileLocation();

        createFolder(mTargetDirectory);

        String[] parseUri = uri.split("/");
        //Get file name
        String name = parseUri[parseUri.length - 1];
        String fileUri = mTargetDirectory + File.separator + name;
        File videoFile = new File(fileUri);

        if (stream == null) {
            Log.d(TAG, "ERROR: Input Stream is empty");
            throw new RuntimeException("stream is null");
        }

        try {
            boolean res = videoFile.createNewFile();
            if (!res) {
                Log.d(TAG, "ERROR: Fail to open file " + fileUri);
                return null;
            }

            FileOutputStream out = new FileOutputStream(videoFile);

            byte buf[] = new byte[128];
            do {
                int numRead = stream.read(buf);
                if (numRead <= 0)
                    break;
                out.write(buf, 0, numRead);
            } while (true);

            stream.close();
            out.close();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileUri;
    }

    /**
     * Create folder if it doesn't exist
     *
     * @param folderPath path to create folder
     */
    private void createFolder(String folderPath) {
        String TAG_FOLDER = "CREATE_FOLDER: ";
        try {
            //check sdcard mount state
            String str = Environment.getExternalStorageState();
            if (str.equals(Environment.MEDIA_MOUNTED)) {
                Log.d(TAG, "sdcard mounted");

                File file = new File(folderPath);
                if (!file.exists()) {
                    boolean res = file.mkdirs();
                    if (!res) {
                        Log.d(TAG, "ERROR: Fail to make directory");
                        return;
                    }
                    //check result
                    if (file.exists())
                        Log.e(TAG, TAG_FOLDER + folderPath + " folder created");
                    else Log.e(TAG, TAG_FOLDER + folderPath + " folder not created");
                } else {
                    Log.d(TAG, TAG_FOLDER + folderPath + " is exist");
                }
            } else {
                Log.d(TAG, TAG_FOLDER + "sdcard unmount, use default image.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Expected response data type for OSCCommandsExecute APIs
    public enum CommandType {
        STRING, IMAGE, VIDEO, PREVIEW
    }
}
