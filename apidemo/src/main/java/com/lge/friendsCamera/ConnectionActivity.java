package com.lge.friendsCamera;

import com.lge.octopus.ConnectionManager;
import com.lge.octopus.OctopusManager;
import com.lge.octopus.tentacles.ble.central.Central;
import com.lge.octopus.tentacles.wifi.client.WifiClient;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Connect with Friends Camera
 */
public class ConnectionActivity extends AppCompatActivity {

    private final static String TAG = ConnectionActivity.class.getSimpleName();

    private final int REQUEST_ENABLE_BT = 1;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;

    private Context mContext;

    private TextView textScanState;
    private Button buttonStartScan;
    private Button buttonStopScan;
    private ListView listViewResult; // listview to display scan result


    private LeDeviceListAdapter mLeDeviceListAdapter;
    private WifiManager mWifiManager;
    private boolean isScanningCamera = false; // Check whether it is scanning or not

    private ScanResult selectedDevice; // selected friends camera
    private ConnectionManager mConnectionManager; // connection manager to use connection library

    private ProgressDialog mProgressDialog;
    private final BroadcastReceiver mUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "#######Action = " + action);

            if (action.startsWith(Central.PREFIX)) {
                // BLE
                processBle(intent);
            } else if (action.startsWith(WifiClient.PREFIX)) {
                // Wi-Fi
                processWifi(intent);
            } else {
                Log.e(TAG, "Error !! out of action");
            }
        }
    };
    /**
     * item click listener for an item selected.
     * try to connect with selected item(device)
     */
    private AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectedDevice = mLeDeviceListAdapter.getDevice(position);

                    if (selectedDevice == null) {
                        Log.e(TAG, "Error: no device for selected row");
                        return;
                    }

                    Log.d(TAG, "clicked = " + selectedDevice.getName() + " / " + selectedDevice.getBtAddress());
                    if (isScanningCamera) {
                        stopScanDevice();
                    }
                    connect();
                    mLeDeviceListAdapter.clear();
                }
            };

    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupView();
        initialize();
    }

    private void setupView() {
        setContentView(R.layout.connect_layout);

        getSupportActionBar().setTitle(R.string.camera_connect);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textScanState = (TextView) findViewById(R.id.text_scan);

        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "ble not supported", Toast.LENGTH_SHORT).show();
            buttonStartScan.setEnabled(false);
            buttonStopScan.setEnabled(false);
        } else {
            buttonStartScan = (Button) findViewById(R.id.button_startscan);
            buttonStartScan.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Ensures Bluetooth is available on the device and it is enabled. If not,
                    // displays a dialog requesting user permission to enable Bluetooth.
                    checkBleScanPermissionAndStartScan();
                }
            });

            buttonStopScan = (Button) findViewById(R.id.button_stopscan);
            buttonStopScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Stop scanning BLE devices
                    stopScanDevice();
                }
            });
            buttonStartScan.setEnabled(true);
            buttonStopScan.setEnabled(true);
        }

        listViewResult = (ListView) findViewById(R.id.listView_scanResult);
    }

    private void initialize() {
        mContext = this;
        FriendsCameraApplication.setContext(mContext);

        // initialize connectionManager
        mConnectionManager = OctopusManager.getInstance(this).getConnectionManager();

        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        updateScanStateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter();
        listViewResult.setAdapter(mLeDeviceListAdapter);
        listViewResult.setOnItemClickListener(itemClickListener);

        // register local broadcast receiver
        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mUpdateReceiver, getFilter());
    }

    /**
     * make Intent filter
     *
     * @return IntentFilter
     */
    private IntentFilter getFilter() {
        IntentFilter scanFilter = new IntentFilter();
        // filter about scan result
        scanFilter.addAction(Central.ACTION_LE_SCANRESULT);
        // filter about result of request gatt connection
        scanFilter.addAction(Central.ACTION_FRIENDS_LE_RESULT);

        // filter about result of connecting with friends camera
        scanFilter.addAction(WifiClient.ACTION_FRIENDS_WIFI_RESULT);
        // filter about state of Wi-Fi connection
        scanFilter.addAction(WifiClient.ACTION_WIFI_STATE);
        return scanFilter;
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "OnPause");
        if (mConnectionManager != null) {
            stopScanDevice();
        }
        mLeDeviceListAdapter.clear();
        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mUpdateReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "OnDestroy");
        super.onDestroy();
    }

    /**
     * handle BLE intent
     */
    private void processBle(Intent intent) {

        final String action = intent.getAction();

        // BLE scan result
        if (Central.ACTION_LE_SCANRESULT.equals(action)) {
            // get scanResult as  bundle
            Bundle scanResult = intent.getExtras();
            ScanResult foundDevice = new ScanResult(scanResult);
            boolean isNew = mLeDeviceListAdapter.addDevice(foundDevice);
            if (isNew) {
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            // result of request BLE gatt connection
        } else if (Central.ACTION_FRIENDS_LE_RESULT.equals(action)) {
            int result = intent.getIntExtra(Central.EXTRA_RESULT, Central.RESULT_FAIL);
            int state = intent.getIntExtra(Central.EXTRA_STATE, Central.LE_GATT_STATE.DISCONNECTED);
            String message = intent.getStringExtra(Central.EXTRA_DATA);

            if (result == Central.RESULT_FAIL) {
                // Fail to connect BLE device
                if (mProgressDialog != null)
                    mProgressDialog.cancel();
                Utils.showTextDialog(mContext, "Fail:", "Fail to connect Gatt server");

            } else if (state == Central.LE_GATT_STATE.TURN_ON_AP && message != null) {
                // Success to connect BLE device
                if (Central.CBOPCODE.toString[2].equalsIgnoreCase(message)) {
                    // Success to turn on hotspot of BLE device
                    mConnectionManager.connect(getSsid(selectedDevice), getDefaultPassphrase());
                }
            }
        }
    }

    /**
     * handle Wi-Fi intent
     */
    private void processWifi(Intent intent) {
        String action = intent.getAction();
        int result = intent.getIntExtra(WifiClient.EXTRA_RESULT, WifiClient.RESULT.DISCONNECTED);

        if (mProgressDialog != null)
            mProgressDialog.cancel();

        if (WifiClient.ACTION_WIFI_STATE.equals(action) && result == WifiClient.RESULT.CONNECTED) {
            // already connected with friends device's Wi-Fi
            Log.e(TAG, "WIFI_STATE: state = " + WifiClient.RESULT.toString[result]);
            finish();
        } else if (WifiClient.ACTION_FRIENDS_WIFI_RESULT.equals(action)) {
            // result of connecting with friends device
            processWifiResult(result);
        }
    }

    /**
     * handle result of connecting with friends device
     */
    private void processWifiResult(int result) {

        Log.e(TAG, "WIFI_RESULT: result = " + WifiClient.RESULT.toString[result]);

        switch (result) {
            case WifiClient.RESULT.CONNECTED: // success to connect Wi-Fi
                finish();
                break;

            case WifiClient.RESULT.DISCONNECTED: // disconnect Wi-Fi
            case WifiClient.RESULT.CONNECT_FAIL: // Fail to connect Wi-Fi
            case WifiClient.RESULT.TIME_EXPIRED: // Time expired
                Utils.showTextDialog(mContext, "FAIL", "Fail to connect WIFI");
                break;

            case WifiClient.RESULT.INCORRECT_PASSPHRASE: // incorrect password
                showWifiDialog();
                break;
        }
    }

    /**
     * Show dialog with EditText to input wifi password
     */
    private void showWifiDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);

        alert.setTitle(getSsid(selectedDevice));
        alert.setMessage("password");

        final EditText input = new EditText(mContext);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mProgressDialog = ProgressDialog.show(mContext, "", "Connecting...", true, false);
                String passwd = input.getText().toString();
                mConnectionManager.connect(getSsid(selectedDevice), passwd);
            }
        });
        alert.show();
    }

    /**
     * Update UI of scan state
     */
    private void updateScanStateUI() {
        Log.d(TAG, "update scan state = " + isScanningCamera);
        if (isScanningCamera) { // in the process of scanning
            textScanState.setText(R.string.scanning);
        } else {
            textScanState.setText(null);
        }
    }

    /**
     * Get ssid from selected device
     */
    private String getSsid(ScanResult device) {
        String ssid = null;

        String deviceName = device.getName();
        String deviceSerialNumber = device.getSerial();

        ssid = deviceName.replaceAll("\\s+", "") + "_" + deviceSerialNumber + ".OSC";

        return ssid;
    }

    /**
     * Ask permissions and start scan if all permissions are granted
     */
    private void checkBleScanPermissionAndStartScan() {
        Log.d(TAG, "PERMISSION_REQUEST: BLUETOOTH AND ACCESS_FINE_LOCATION");
        int loc_permissionCheck = ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION);

        //Enable wifi
        if (!mWifiManager.isWifiEnabled())
            mWifiManager.setWifiEnabled(true);

        //Enable bt
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Request bluetooth enable");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        //Get permission (ACCESS_FINE_LOCATION)
        if (loc_permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Request ACTION_FINE_LOCATION permission");
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }

        startScanDevice();
    }

    /**
     * Start scan BLE devices
     */
    private void startScanDevice() {
        mConnectionManager.StartScanFriends();
        isScanningCamera = true;
        updateScanStateUI();
    }

    /**
     * Stop scan BLE devices
     */
    private void stopScanDevice() {
        mConnectionManager.StopScanFriends();
        isScanningCamera = false;
        updateScanStateUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == REQUEST_ENABLE_BT) {
            Log.d(TAG, "BT ENABLE REQUEST");
            if (resultCode == Activity.RESULT_OK) {
                checkBleScanPermissionAndStartScan();
            } else {
                Log.d(TAG, "BT DENIED");
                return;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "Permission for ACCESS FINE LOCATION is granted");
                    startScanDevice();
                } else {
                    Log.d(TAG, "Permission for ACCESS FINE LOCATION  is denied");
                }
        }
    }

    /**
     * return default password.
     * If factory mode is true, the password is 00serialNumber.
     */
    private String getDefaultPassphrase() {
        return selectedDevice.getIsFactory() ? "00" + selectedDevice.getSerial() : null;
    }

    /**
     * connect to friends device.
     * If hot spot is already turned on, try connect to Wi-Fi directly.
     * If not, try to turn on hotspot.
     */
    private void connect() {
        mProgressDialog = ProgressDialog.show(mContext, "", "Connecting...", true, false);

        if (selectedDevice.getWifiState() == Central.STATE.WIFI_ON) {
            // hotspot is already turned on
            Log.d(TAG, "already hotspot on ");
            mConnectionManager.connect(getSsid(selectedDevice), getDefaultPassphrase());
        } else if (selectedDevice.getWifiState() == Central.STATE.WIFI_OFF) {
            // try to enable friends wifi ap
            Log.d(TAG, "Try to enable friends wifi ap");
            mConnectionManager.enableFriendWifiAP(selectedDevice.getBtAddress());
        } else { //Central.STATE.WIFI_BUSY
            // device is already connected
            if (mProgressDialog != null)
                mProgressDialog.cancel();
            Utils.showTextDialog(mContext, "FAIL:", "Device is already connected");
        }
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

    /**
     * Adapter for holding devices found through scanning.
     */
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<ScanResult> mLeDevices;

        private LayoutInflater mInflater;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<ScanResult>();
            mInflater = LayoutInflater.from(mContext);
        }

        public int getPosition(String address) {
            int position = -1;
            for (int i = 0; i < mLeDevices.size(); i++) {
                String addressInList = mLeDevices.get(i).getBtAddress();
                if (addressInList.equals(address)) {
                    position = i;
                    break;
                }
            }
            return position;
        }

        public boolean addDevice(ScanResult device) {
            if (device.getIsOsc()) {
                for (ScanResult res : mLeDevices) {
                    if (res.equals(device)) {
                        return false;
                    }
                }

                int devicePosition = getPosition(device.getBtAddress());
                if (devicePosition == -1) {
                    mLeDevices.add(device);
                } else {
                    mLeDevices.set(devicePosition, device);
                }

                return true;
            }
            return false;
        }

        public ScanResult getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflater.inflate(R.layout.ble_list_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            if (mLeDevices.size() > i) {
                ScanResult device = mLeDevices.get(i);

                String mSsid = getSsid(device);
                String bName = device.getName();

                if (bName != null && mSsid != null) {
                    viewHolder.deviceName.setText(bName);
                    viewHolder.deviceAddress.setText(mSsid);
                }
            }
            return view;
        }
    }
}
