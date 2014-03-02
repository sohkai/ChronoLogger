package com.bringitsf.chronologger;

import android.app.Fragment;
import android.util.Log;

import com.bringitsf.chronologger.TabPagerAdapter.TabType;

public abstract class TabFragmentBase extends Fragment {
    
    private static String TAG = TabFragmentBase.class.getName();
    
    public static TabFragmentBase newInstance(TabType tabType) {
        TabFragmentBase fragment;
        switch (tabType) {
            case CURRENT:
                fragment = new TabFragmentCurrent();
                break;
            case HISTORY:
                fragment = new TabFragmentHistory();
                break;
            case ADMIN:
                fragment = new TabFragmentAdmin();
                break;
            default:
                Log.e(TAG, "Tab type of " + tabType + " does not match known tabs. Defaulting to current tab.");
                // Select current page by default
                fragment = new TabFragmentCurrent();
        }
        return fragment;
    }
    
    public TabFragmentBase() {
        super();
    }
    
    public void tabSelected() {
    }
    
}
