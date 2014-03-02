package com.bringitsf.chronologger;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TabPager extends ViewPager {
    
    public TabPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setCurrentItem(int item) {
        TabPagerAdapter adapter = (TabPagerAdapter) getAdapter();
        final TabFragmentBase fragment = (TabFragmentBase) adapter.instantiateItem(this, item);
        
        // Poll surroundings for updates
        BeaconManager.pollRanging(BeaconHandler.defaultRegion);
        // Run callback after poll is complete
        Handler rangingHandler = new Handler();
        rangingHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                fragment.onThisTabSelected();
            }
        }, BeaconManager.RANGING_DELAY);
        
        super.setCurrentItem(item, true);
    }

}
