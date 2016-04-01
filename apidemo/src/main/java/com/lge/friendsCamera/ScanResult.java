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

import com.lge.octopus.tentacles.ble.central.Central;

import android.os.Bundle;

public class ScanResult {

    Bundle mScanResult;

    String mDeviceName;
    String mBleAddress;
    String mSerial;
    boolean mIsOsc;
    int mWifiState;
    boolean mIsFactory;

    public ScanResult(Bundle scanResult) {
        mScanResult = scanResult;
        mDeviceName = mScanResult.getString(Central.LE_DEV_NAME, "");
        mBleAddress = mScanResult.getString(Central.LE_BLE_ADDRESS, "");
        mSerial = mScanResult.getString(Central.LE_DEVICE_SERIAL, "");
        mIsOsc = mScanResult.getBoolean(Central.LE_CAMERA_PROTOCOL_OSC, false);
        mWifiState = mScanResult.getInt(Central.LE_WIFI_STATE, Central.STATE.WIFI_OFF);
        mIsFactory = mScanResult.getBoolean(Central.LE_CAMERA_FACTORY_MODE, false);
    }

    public Bundle getScanResult() {
        return mScanResult;
    }

    public String getName() {
        return mDeviceName;
    }

    public String getBtAddress() {
        return mBleAddress;
    }

    public String getSerial() {
        return mSerial;
    }

    public boolean getIsOsc() {
        return mIsOsc;
    }

    public int getWifiState() {
        return mWifiState;
    }

    public boolean getIsFactory() {
        return mIsFactory;
    }

    @Override
    public String toString() {
        return "name:" + mDeviceName + ", btAddr:" + mBleAddress + ", wifiState:" + mWifiState;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScanResult)) {
            return false;
        }

        ScanResult scanResult = (ScanResult) o;

        if (!mDeviceName.equals(scanResult.mDeviceName)) {
            return false;
        }
        if (!mBleAddress.equals(scanResult.mBleAddress)) {
            return false;
        }
        if (!mSerial.equals(scanResult.mSerial)) {
            return false;
        }
        if (mWifiState != scanResult.mWifiState) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return mBleAddress.hashCode();
    }
}
