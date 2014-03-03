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
    
    //FIXME: login request would ideally return status about the user has, ie. admin privileges, major, and minor ibeacon ids to
    // set up appropriate ranges.
    // For now, we'll use one of hockey's great numbers.
    private static final String DEFAULT_REGION_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";   // must normalize to lowercase
    private static final Integer DEFAULT_REGION_MAJOR = 99;
    private static final Integer DEFAULT_REGION_MINOR = null;   // to pick up both our estimote devices by default
    public static final Region defaultRegion = new Region("default", Utils.normalizeProximityUUID(DEFAULT_REGION_UUID), DEFAULT_REGION_MAJOR, DEFAULT_REGION_MINOR);
    
    private static HashMap<String, Region> mRegisteredRegions = new HashMap<String, Region>();
    private static HashMap<String, HashMap<Integer, HashMap<Integer, NotifyBeacon>>> mRegisteredBeacons = new HashMap<String, HashMap<Integer, HashMap<Integer, NotifyBeacon>>>();
    
    public static void init(Context context) {
        mRegisteredRegions.put("default", defaultRegion);
    }
    
    public static void discoveredBeacons(Region region, List<Beacon> beacons) {
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            List<Beacon> newBeacons = new ArrayList<Beacon>();
            for (Beacon beacon : beacons) {
                if (isBeaconInRange(beacon) && addDiscoveredBeacon(beacon)) {
                    newBeacons.add(beacon);
                }
            }
            // Because Vlad would much rather I do these all one by one on my side and doesn't want to bother
            // dealing with a simple JSON array of beacons :(
            if (!newBeacons.isEmpty()) {
                for (Beacon beacon : newBeacons) {
                    checkInToServer(beacon);
                }
            }
            
            //FIXME: deal with the fact that this should check for all the other beacons that are not in the new list
            // because they are obviously not in range and we should tell the server that
        }
    }
    
    public static void enteredRegion(Region region) {
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            beaconsEnteredInRegion(region);
        }
    }
    
    public static void exitedRegion(Region region) {
        if (mRegisteredRegions.containsKey(region.getIdentifier())) {
            beaconsExitedInRegion(region);
        }
    }
    
    private static boolean isBeaconInRange(Beacon beacon) {
        // Must be within 10 meters to register with us
        if (Utils.computeAccuracy(beacon) < 10.0) {
            return true;
        }
        return false;
    }
    
    private static boolean addDiscoveredBeacon(Beacon beacon) {
        String beaconUUID = beacon.getProximityUUID();
        int beaconMajor = beacon.getMajor();
        int beaconMinor = beacon.getMinor();
        if (mRegisteredBeacons.containsKey(beaconUUID)) {
            HashMap<Integer, HashMap<Integer, NotifyBeacon>> majorBeacons = mRegisteredBeacons.get(beaconUUID);
            if (majorBeacons.containsKey(beaconMajor)) {
                HashMap<Integer, NotifyBeacon> minorBeacons = majorBeacons.get(beaconMajor);
                if (minorBeacons.containsKey(beaconMinor)) {
                    NotifyBeacon curBeacon = minorBeacons.get(beaconMinor);
                    if (curBeacon.hasCheckedIn()) {
                        Log.i(TAG, "Already found and in range of this beacon: " + beaconUUID + " " + beaconMajor + " " + beaconMinor);
                        return false;
                    } else {
                        Log.i(TAG, "Already found but not range of this beacon: " + beaconUUID + " " + beaconMajor + " " + beaconMinor);
                        curBeacon.setCheckedIn(true);
                    }
                } else {
                    Log.i(TAG, "New minor beacon: " + beaconUUID + " " + beaconMajor + " " + beaconMinor);
                    minorBeacons.put(beaconMinor, new NotifyBeacon(beacon));
                }
            } else {
                Log.i(TAG, "New major set of beacons: " + beaconUUID + " " + beaconMajor);
                Log.i(TAG, "New beacon: " + beaconUUID + " " + beaconMajor + " " + beaconMinor);
                majorBeacons.put(beaconMajor, newBeaconMinor(beacon));
            }
        } else {
            Log.i(TAG, "New UUID set of beacons: " + beaconUUID);
            Log.i(TAG, "New beacon: " + beaconUUID + " " + beaconMajor + " " + beaconMinor);
            mRegisteredBeacons.put(beaconUUID, newBeaconMajor(beacon));
        }
        return true;
    }
    
    private static HashMap<Integer, NotifyBeacon> newBeaconMinor(Beacon beacon) {
        NotifyBeacon newBeacon = new NotifyBeacon(beacon);
        HashMap<Integer, NotifyBeacon> newMinorBeacons = new HashMap<Integer, NotifyBeacon>();
        newMinorBeacons.put(beacon.getMinor(), newBeacon);
        return newMinorBeacons;
    }
    
    private static HashMap<Integer, HashMap<Integer, NotifyBeacon>> newBeaconMajor(Beacon beacon) {
        HashMap<Integer, HashMap<Integer, NotifyBeacon>> newMajorBeacons = new HashMap<Integer, HashMap<Integer, NotifyBeacon>>();
        newMajorBeacons.put(beacon.getMajor(), newBeaconMinor(beacon));
        return newMajorBeacons;
    }
    
    public static void beaconsEnteredInRegion(final Region region) {
        findBeacon(region, new FoundBeaconListener() {
            @Override
            public void onFoundBeacon(NotifyBeacon notifyBeacon) {
                if (!notifyBeacon.hasCheckedIn() && isBeaconInRange(notifyBeacon.getBeacon())) {
                    checkInToServer(notifyBeacon.getBeacon());
                    notifyBeacon.setCheckedIn(true);
                } else if (notifyBeacon.hasCheckedIn()) {
                    Log.i(TAG, "Beacon already checked in: " + region.getProximityUUID() + " " +
                                    region.getMajor() + " " + region.getMinor());
                }
            }
            
            @Override
            public void onNoBeacon() {
                Log.e(TAG, "Could not find beacon for monitored region: " + region.getProximityUUID() + " " +
                                    region.getMajor() + " " + region.getMinor());
            }
        });
    }
    
    public static void beaconsExitedInRegion(final Region region) {
        findBeacon(region, new FoundBeaconListener() {
            @Override
            public void onFoundBeacon(NotifyBeacon notifyBeacon) {
                if (notifyBeacon.hasCheckedIn() && !isBeaconInRange(notifyBeacon.getBeacon())) {
                    exitRegionToServer(notifyBeacon.getBeacon());
                    notifyBeacon.setCheckedIn(false);
                } else if (notifyBeacon.hasCheckedIn()) {
                    Log.i(TAG, "Beacon already exited: " + region.getProximityUUID() + " " +
                                    region.getMajor() + " " + region.getMinor());
                }
            }
            
            @Override
            public void onNoBeacon() {
                Log.e(TAG, "Could not find beacon for monitored region: " + region.getProximityUUID() + " " +
                                    region.getMajor() + " " + region.getMinor());
            }
        });
    }
    
    private static void findBeacon(Region region, FoundBeaconListener listener) {
        String regionUUID = region.getProximityUUID();
        Integer regionMajor = region.getMajor();
        Integer regionMinor = region.getMinor();
        if (regionUUID != null && !regionUUID.isEmpty()) {
            HashMap<Integer, HashMap<Integer, NotifyBeacon>> majorBeacons = mRegisteredBeacons.get(regionUUID);
            if (regionMajor != null) {
                HashMap<Integer, NotifyBeacon> minorBeacons = majorBeacons.get(regionMajor);
                if (regionMinor != null) {
                    if (minorBeacons.containsKey(regionMinor)) {
                        // Found a matching beacon
                        listener.onFoundBeacon(minorBeacons.get(regionMinor));
                        return;
                    }
                }
            }
        }
        // Only reached if a beacon was not found for the region
        listener.onNoBeacon();
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
    
    private interface FoundBeaconListener {
        public void onFoundBeacon(NotifyBeacon notifyBeacon);
        public void onNoBeacon();
    }
    
}