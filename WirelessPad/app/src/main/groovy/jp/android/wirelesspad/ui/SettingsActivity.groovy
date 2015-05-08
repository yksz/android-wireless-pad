package jp.android.wirelesspad.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.app.ActionBarActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import groovy.transform.CompileStatic
import jp.android.wirelesspad.R

@CompileStatic
public class SettingsActivity extends ActionBarActivity {
    private static final int REQUEST_ENABLE_BLUETOOTH = 1

    private BluetoothAdapter mBluetoothAdapter
    private ArrayAdapter<String> mPairedDevicesAdapter
    private ArrayAdapter<String> mDiscoveredDevicesAdapter
    private BroadcastReceiver mReceiver

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        contentView = R.layout.activity_settings

        mBluetoothAdapter = BluetoothAdapter.defaultAdapter
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device does not support Bluetooth", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        if (!mBluetoothAdapter.isEnabled()) {
            def enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }

        mPairedDevicesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1)
        findPairedDevices()

        mDiscoveredDevicesAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mReceiver = createBluetoothDeviceFoundReceiver()
        registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND))
    }

    private void findPairedDevices() {
        def pairedDevices = mBluetoothAdapter.bondedDevices
        if (!pairedDevices.isEmpty()) {
            for (device in pairedDevices) {
                mPairedDevicesAdapter.add(device.name + "\n" + device.address)
            }
        }
        def deviceList = (ListView) findViewById(R.id.settings_device_list_view)
        deviceList.adapter = mPairedDevicesAdapter
    }

    private BroadcastReceiver createBluetoothDeviceFoundReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BluetoothDevice.ACTION_FOUND.equals(intent.action)) {
                    def device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    mDiscoveredDevicesAdapter.add(device.name + "\n" + device.address)
                }
                def deviceList = (ListView) findViewById(R.id.settings_device_list_view)
                deviceList.adapter = mDiscoveredDevicesAdapter
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {

            } else {
                finish()
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.itemId) {
            case R.id.settings_discover_menu:
                discoverDevices()
                return true
            default:
                return super.onOptionsItemSelected(item)
        }
    }

    private void discoverDevices() {
        def textView = (TextView) findViewById(R.id.settings_device_list_text)
        textView.text = "Discovered Device List"
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery()
        }
        mBluetoothAdapter.startDiscovery()
    }

    @Override
    protected void onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
    }
}
