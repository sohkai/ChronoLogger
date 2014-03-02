package com.bringitsf.chronologger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

public class BeaconHandler {
    
    private static final String TAG = BeaconHandler.class.getName();
    private static final String CHECKIN_END_POINT = "/checkin/";
    private static final String EXIT_END_POINT = "/leave/";
    private static final double IN_RANGE_DISTANCE = 5.0;
    
    //FIXME: login request would ideally return status about the user has, ie. admin privileges, major, and minor ibeacon ids to
    // set up appropriate ranges.
    // For now, we'll use one of hockey's great numbers.
    private static final String DEFAULT_REGION_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";   // must normalize to lowercase
    private static final Integer DEFAULT_REGION_MAJOR = 99;
    private static final Integer DEFAULT_REGION_MINOR = null;   // to pick up both our estimote devices by default
    public static final Region defaultRegion = new Region("default", Utils.normalizeProximityUUID(DEFAULT_REGION_UUID), DEFAULT_REGION_MAJOR, DEFAULT_REGION_MINOR);
    
    private static HashMap<String, Region> mRegisteredRegions = new HashMap<String, Region>();
    private static List<Beacon> mInRangeBeacons = new ArrayList<Beacon>();
    
    public static void init(Context context) {
        mRegisteredRegions.put("default", defaultRegion);
    }
    
    public static void discoveredBeacons(Region region, List<Beacon> beacons) {
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            List<Beacon> exitedBeacons = findExitedBeacons(beacons);
            List<Beacon> discoveredBeacons = addDiscoveredBeacons(beacons);
            // Because Vlad would much rather I do these all one by one on my side and doesn't want to bother
            // dealing with a simple JSON array of beacons :(
            // We first exit from the beacons we lost
            if (!exitedBeacons.isEmpty()) {
                for (Beacon beacon : exitedBeacons) {
                    exitRegionToServer(beacon);
                }
            }
            // And then register to the ones we just found
            if (!discoveredBeacons.isEmpty()) {
                for (Beacon beacon : discoveredBeacons) {
                    checkInToServer(beacon);
                }
            }
        }
    }
    
    public static void enteredRegion(Region region) {
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            // If we entered a new region, we poll on this region
            BeaconManager.pollRanging(region);
        }
    }
    
    public static void exitedRegion(Region region) {
        List<Beacon> exitedBeacons = new ArrayList<Beacon>();
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            for (Beacon inRangeBeacon : mInRangeBeacons) {
                if (Utils.isBeaconInRegion(inRangeBeacon, region) && !isBeaconInRange(inRangeBeacon)) {
                    exitRegionToServer(inRangeBeacon);
                    exitedBeacons.add(inRangeBeacon);
                }
            }
            // Remove exited beacons
            for (Beacon beacon : exitedBeacons) {
                mInRangeBeacons.remove(beacon);
            }
            
            // Poll on this region to update it
            BeaconManager.pollRanging(region);
        }
    }
    
    private static boolean isBeaconInRange(Beacon beacon) {
        // Must be within 10 meters to register with us
        if (Utils.computeAccuracy(beacon) <= IN_RANGE_DISTANCE) {
            return true;
        }
        return false;
    }
    
    private static boolean isSameBeacon(Beacon beacon1, Beacon beacon2) {
        if (beacon1.getProximityUUID() == beacon2.getProximityUUID() &&
                beacon1.getMajor() == beacon2.getMajor() && beacon1.getMinor() == beacon2.getMinor()) {
            return true;
        }
        return false;
    }
    
    private static List<Beacon> addDiscoveredBeacons(List<Beacon> beacons) {
        List<Beacon> newBeacons = new ArrayList<Beacon>();
        for (Beacon newBeacon : beacons) {
            // Beacon must be in range first
            if (isBeaconInRange(newBeacon)) {
                // See if we haven't found it already and add it
                boolean found = false;
                for (Beacon oldBeacon : mInRangeBeacons) {
                    if (isSameBeacon(oldBeacon, newBeacon)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    newBeacons.add(newBeacon);
                    mInRangeBeacons.add(newBeacon);
                }
            }
        }
        return newBeacons;
    }
    
    private static List<Beacon> findExitedBeacons(List<Beacon> beacons) {
        List<Beacon> exitedBeacons = new ArrayList<Beacon>();
        for (Beacon oldBeacon : mInRangeBeacons) {
            // See which of our old beacons have exited the region and are not in the new discovered list anymore
            boolean exited = true;
            for (Beacon newBeacon : beacons) {
                if (isSameBeacon(oldBeacon, newBeacon)) {
                    if (isBeaconInRange(newBeacon)) {
                        // The old beacon is in the newly discovered region and still in range
                        exited = false;
                    }
                    break;
                }
            }
            if (exited) {
                exitedBeacons.add(oldBeacon);
            }
        }
        // Remove the exited Beacons from the in range list
        for (Beacon exitedBeacon : exitedBeacons) {
            mInRangeBeacons.remove(exitedBeacon);
        }
        return exitedBeacons;
    }
    
    private static void checkInToServer(Beacon beacon) {
        String checkInUrl = Environment.BASE_URL + CHECKIN_END_POINT + getUserEndPoint(beacon);
        Log.i(TAG, "Sending check in request for: " + checkInUrl);
        VolleyHandler.sendRequest(Request.Method.POST, checkInUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Successful check in request!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unsuccessful check in request :(!");
                Log.e(TAG, "Volley error: " + error.getMessage());
            }
        });
    }
    
    private static void exitRegionToServer(Beacon beacon) {
        String exitUrl = Environment.BASE_URL + EXIT_END_POINT + getUserEndPoint(beacon);
        Log.i(TAG, "Sending exit request for: " + exitUrl);
        VolleyHandler.sendRequest(Request.Method.POST, exitUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Successful exit request!");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unsuccessful exit request :(!");
                Log.e(TAG, "Volley error: " + error.getMessage());
            }
        });
    }
    
    private static String getUserEndPoint(Beacon beacon) {
        return DefaultSharedPrefs.getString(DefaultSharedPrefs.EXTRA_USER_EMAIL, "") + "/" + beacon.getProximityUUID() + beacon.getMajor() + beacon.getMinor();
    }
    
}
