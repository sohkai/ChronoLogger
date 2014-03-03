package com.bringitsf.chronologger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TabFragmentAdmin extends TabFragmentBase {
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_admin, container, false);
    }

    public TabFragmentAdmin() {
        super();
    }
    
    public void onThisTabSelected() {
        
    }
    
}
