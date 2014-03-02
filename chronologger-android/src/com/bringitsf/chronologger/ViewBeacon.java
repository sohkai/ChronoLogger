package com.bringitsf.chronologger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class ViewBeacon {
    
    private static final String TAG = ViewBeacon.class.getName();

    public String beaconId;
    public String locationName;
    public Date timeEntered;
    public Date timeLeft;
    
    public static List<ViewBeacon> parseFromJSON(JSONObject jsonObject) {
        List<ViewBeacon> mViewBeacons = new ArrayList<ViewBeacon>();
        try {
            JSONArray visitsArray = jsonObject.getJSONArray("visits");
            for (int ii = 0; ii < visitsArray.length(); ++ii) {
                try {
                    ViewBeacon beacon = new ViewBeacon(visitsArray.getJSONObject(ii));
                    mViewBeacons.add(beacon);
                } catch (JSONException jsonex) {
                    Log.e(TAG, "Could not parse JSON object into ViewBeacon: " + jsonex.toString());
                }
            }
        } catch (JSONException jsonex) {
            Log.e(TAG, "Could not get visits array from JSON object: " + jsonex.toString());
        }
        return mViewBeacons;
    }
    
    private ViewBeacon(JSONObject beaconJSON) throws JSONException {
        this.beaconId = beaconJSON.getString("beacon_string");
        this.locationName = beaconJSON.getString("location");
        this.timeEntered = new Date(beaconJSON.getLong("time_entered"));
        if (!beaconJSON.getString("time_left").isEmpty()) {
            this.timeLeft = new Date(beaconJSON.getLong("time_left"));
        }
    }

}
