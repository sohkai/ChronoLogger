package com.bringitsf.chronologger;

import java.util.List;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

public class TabPagerAdapter extends FragmentPagerAdapter {
    
    public enum TabType {
        CURRENT,
        HISTORY,
        ADMIN
    }
    
    private List<TabType> tabs;
    
    public TabPagerAdapter(FragmentManager fm, List<TabType> tabTypes) {
        super(fm);
        tabs = tabTypes;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public Fragment getItem(int position) {
        return TabFragmentBase.newInstance(tabs.get(position));
    }
}