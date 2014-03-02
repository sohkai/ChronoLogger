package com.bringitsf.chronologger;

import android.app.Fragment;
import android.util.Log;

import com.bringitsf.chronologger.TabPagerAdapter.TabType;

public abstract class TabFragmentBase extends Fragment {
    
    private static String TAG = TabFragmentBase.class.getName();
    protected static final String UPDATE_END_POINT = "/getvisits/";
    
    public static TabFragmentBase newInstance(TabType tabType) {
        TabFragmentBase fragment;
        switch (tabType) {
            case HOME:
                fragment = new TabFragmentHome();
                break;
            case HISTORY:
                fragment = new TabFragmentHistory();
                break;
            case ADMIN:
                fragment = new TabFragmentAdmin();
                break;
            default:
                Log.e(TAG, "Tab type of " + tabType + " does not match known tabs. Defaulting to home tab.");
                // Select home page by default
                fragment = new TabFragmentHome();
        }
        return fragment;
    }
    
    public TabFragmentBase() {
        super();
    }
    
    public void onThisTabSelected() {
    }
    
}
