package com.bringitsf.chronologger;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class LauncherActivity extends Activity {

    private static final String TAG = LauncherActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        
        if (Environment.DEBUG_MODE) {
            com.estimote.sdk.utils.L.enableDebugLogging(true);
        }
    }
    
}
