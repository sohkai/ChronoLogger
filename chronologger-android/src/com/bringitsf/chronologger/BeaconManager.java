package com.bringitsf.chronologger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.Region;

// Enum singleton
public enum BeaconManager {
    INSTANCE;
    
    private static final String TAG = BeaconManager.class.getName();
    public static final int RANGING_DELAY = 5000;
    
    private static com.estimote.sdk.BeaconManager mBeaconManager;
    private static List<Beacon> mCurrentBeacons = new ArrayList<Beacon>();
    
    public static void init(Context context) {
        mBeaconManager = new com.estimote.sdk.BeaconManager(context);
    }
    
    // Only one we need to hook onto for now to get current beacons
    public static void setRangingListener(final RangingListener listener) {
        mBeaconManager.setRangingListener(new RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                mCurrentBeacons = beacons;
                listener.onBeaconsDiscovered(region, beacons);
            }
        });
    }
    
    public static com.estimote.sdk.BeaconManager getInstance() {
        return mBeaconManager;
    }
    
    public static List<Beacon> getCurrentBeacons() {
        return mCurrentBeacons;
    }
    
    public static void pollRanging(final Region region) {
        try {
            mBeaconManager.startRanging(region);
            
            // Only do ranging requests for 5 seconds before going into passive monitoring mode
            Handler rangingHandler = new Handler();
            rangingHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        mBeaconManager.stopRanging(region);
                        mBeaconManager.startMonitoring(region);
                    } catch(RemoteException remoteEx) {
                        Log.e(TAG, "Could not stop ranging and begin monitoring for iBeacons: " + remoteEx.toString());
                        remoteEx.printStackTrace();
                        Toast.makeText(ChronoLoggerApplication.getAppContext(), R.string.toast_ranging_failed, Toast.LENGTH_LONG).show();
                    }
                };
            }, RANGING_DELAY);
        } catch (RemoteException remoteEx) {
            Log.e(TAG, "Could not start ranging for iBeacons: " + remoteEx.toString());
            remoteEx.printStackTrace();
            Toast.makeText(ChronoLoggerApplication.getAppContext(), R.string.toast_ranging_failed, Toast.LENGTH_LONG).show();
        }
    }

}
