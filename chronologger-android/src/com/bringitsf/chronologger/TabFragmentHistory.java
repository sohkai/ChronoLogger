package com.bringitsf.chronologger;

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

public class TabFragmentHistory extends TabFragmentBase {
    
    private static final String TAG = TabFragmentHistory.class.getName();
    private ListView mHistoryList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tab_history, container, false);
        View historyListHeader = inflater.inflate(R.layout.history_list_header, null);
        mHistoryList = (ListView)fragmentView.findViewById(R.id.history_list);
        // Note that adding a header to the listview will add an item to the listview but not adapter
        mHistoryList.addHeaderView(historyListHeader, null, false);
        return fragmentView;
    }

    public TabFragmentHistory() {
        super();
    }

    public void onThisTabSelected() {
        String updateUrl = Environment.BASE_URL + UPDATE_END_POINT + DefaultSharedPrefs.getString(DefaultSharedPrefs.EXTRA_USER_EMAIL, "");
        Log.i(TAG, "Sending update request for: " + updateUrl);
        VolleyHandler.sendRequest(Request.Method.GET, updateUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.i(TAG, "Successful update request!");
                loadHistoryBeaconList(ViewBeacon.parseFromJSON(response));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Unsuccessful update request :(!");
                Log.e(TAG, "Volley error: " + error.getMessage());
            }
        });
    }
    
    private void loadHistoryBeaconList(List<ViewBeacon> viewBeacons) {
        if (getActivity() != null) {
            HistoryBeaconAdapter viewBeaconAdapter = new HistoryBeaconAdapter(getActivity(), viewBeacons.toArray(new ViewBeacon[viewBeacons.size()]));
            mHistoryList.setAdapter(viewBeaconAdapter);
        }
    }
    
    private static class HistoryBeaconAdapter extends ArrayAdapter<ViewBeacon> {
        private ViewBeacon mViewBeacons[];

        public HistoryBeaconAdapter(Context context, ViewBeacon[] viewBeacons) {
            super(context, R.layout.history_list_item, viewBeacons);
            mViewBeacons = viewBeacons;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View rowView = inflater.inflate(R.layout.history_list_item, parent, false);
            rowView.setClickable(false);
            TextView textLocation = (TextView)rowView.findViewById(R.id.history_location);
            TextView textTimeStart = (TextView)rowView.findViewById(R.id.history_time_start);
            textLocation.setText(mViewBeacons[position].locationName);
            String timeStart = mViewBeacons[position].timeEntered.toString();
            textTimeStart.setText(timeStart.substring(0, timeStart.length() - 5));
            if (mViewBeacons[position].timeLeft != null) {
                TextView textTimeEnd = (TextView)rowView.findViewById(R.id.history_time_end);
                String timeEnd = mViewBeacons[position].timeLeft.toString();
                textTimeEnd.setText(timeEnd.substring(0, timeEnd.length() - 5));
            }
            return rowView;
        }
    }

}
