package com.bringitsf.chronologger;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class TabFragmentAdmin extends TabFragmentBase {

    private ListView mAdminList;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_tab_admin, container, false);
        View adminListHeader = inflater.inflate(R.layout.admin_list_header, null);
        mAdminList = (ListView)fragmentView.findViewById(R.id.admin_list);
        // Note that adding a header to the listview will add an item to the listview but not adapter
        mAdminList.addHeaderView(adminListHeader, null, false);
        return fragmentView;
    }

    public TabFragmentAdmin() {
        super();
    }
    
    public void onThisTabSelected() {
        
    }
    
}
