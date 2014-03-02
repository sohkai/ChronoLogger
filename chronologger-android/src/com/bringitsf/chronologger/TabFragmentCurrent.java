package com.bringitsf.chronologger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TabFragmentCurrent extends TabFragmentBase {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_current, container, false);
    }

    public TabFragmentCurrent() {
        super();
    }
    
}
