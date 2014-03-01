package com.bringitsf.chronologger;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.estimote.sdk.BeaconManager;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private static final int REQUEST_ENABLE_BT = 1000;
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up estimote manager
        beaconManager = new BeaconManager(this);
        //FIXME: set ranging
        //FIXME: set monitor
    }

    @Override
    protected void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }
    
    @Override
    protected void onStart() {
        // Check if device supports Bluetooth Low Energy.
        if (!beaconManager.hasBluetooth()) {
            //FIXME: turn into dialog
            Toast.makeText(this, "Device does not support Bluetooth Low Energy.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    
        // If Bluetooth is not enabled, let user enable it.
        if (!beaconManager.isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            //FIXME
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                //FIXME
            } else {
                Toast.makeText(this,  "Chrono Logger will not be able to log your work location without Bluetooth", Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
