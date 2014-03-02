package com.bringitsf.chronologger;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class TabFragmentHome extends TabFragmentBase {
    
    private static final String TAG = TabFragmentHome.class.getName();
    private ListView mHomeList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tab_home, container, false);
        View homeListHeader = inflater.inflate(R.layout.home_list_header, null);
        mHomeList = (ListView)fragmentView.findViewById(R.id.home_list);
        // Note that adding a header to the listview will add an item to the listview but not adapter
        mHomeList.addHeaderView(homeListHeader, null, false);
        return fragmentView;
    }

    public TabFragmentHome() {
        super();
    }
    
    public void onThisTabSelected() {
        String updateUrl = Environment.BASE_URL + UPDATE_END_POINT + DefaultSharedPrefs.getString(DefaultSharedPrefs.EXTRA_USER_EMAIL, "");
        Log.i(TAG, "Sending update request for: " + updateUrl);
        VolleyHandler.sendRequest(Request.Method.GET, updateUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Successful update request!");
                loadCurrentBeaconList(ViewBeacon.parseFromJSON(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unsuccessful update request :(!");
                Log.e(TAG, "Volley error: " + error.getMessage());
            }
        });
    }
    
    private void loadCurrentBeaconList(List<ViewBeacon> viewBeacons) {
        List<ViewBeacon> currentBeacons = new ArrayList<ViewBeacon>();
        for (ViewBeacon viewBeacon : viewBeacons) {
            if (viewBeacon.timeLeft == null) {
                // Current beacons will not have a time left date yet
                currentBeacons.add(viewBeacon);
            }
        }
        HomeBeaconAdapter viewBeaconAdapter = new HomeBeaconAdapter(getActivity(), currentBeacons.toArray(new ViewBeacon[currentBeacons.size()]));
        mHomeList.setAdapter(viewBeaconAdapter);
    }
    
    private static class HomeBeaconAdapter extends ArrayAdapter<ViewBeacon> {
        private ViewBeacon mViewBeacons[];

        public HomeBeaconAdapter(Context context, ViewBeacon[] viewBeacons) {
            super(context, R.layout.home_list_item, viewBeacons);
            mViewBeacons = viewBeacons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rowView = inflater.inflate(R.layout.home_list_item, parent, false);
            TextView textLocation = (TextView)rowView.findViewById(R.id.home_location);
            TextView textTimeStart = (TextView)rowView.findViewById(R.id.home_time_start);
            textLocation.setText(mViewBeacons[position].locationName);
            textTimeStart.setText(mViewBeacons[position].timeEntered.toString());
            return rowView;
        }
    }
    
}
