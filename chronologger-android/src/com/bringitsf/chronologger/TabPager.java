package com.bringitsf.chronologger;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TabPager extends ViewPager {
    
    public TabPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setCurrentItem(int item) {
        TabPagerAdapter adapter = (TabPagerAdapter) getAdapter();
        TabFragmentBase fragment = (TabFragmentBase) adapter.instantiateItem(this, item);
        fragment.tabSelected();
        super.setCurrentItem(item, true);
    }

}
