package com.bringitsf.chronologger;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

// Guaranteed to have a single instance by Android OS
public class ChronoLoggerApplication extends Application {

    private static final String TAG = ChronoLoggerApplication.class.getName();
    private static Context mContext;
    private static boolean mIsForeground = false;
    private static int mAppVersion = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        
        if (Environment.DEBUG_MODE) {
            com.estimote.sdk.utils.L.enableDebugLogging(true);
        }
    }

    public static Context getAppContext() {
        return mContext;
    }

    // From https://code.google.com/p/gcm/source/browse/gcm-client/src/com/google/android/gcm/demo/app/DemoActivity.java
    public static int getAppVersion() {
        if (mAppVersion == 0) {
            try {
                PackageInfo packageInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                mAppVersion = packageInfo.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                // should never happen
                throw new RuntimeException("Could not get package name: " + e);
            }
        }
        return mAppVersion;
    }

    public static boolean isForeground() {
        return mIsForeground;
    }

    public static void sentToBackground() {
        mIsForeground = false;
    }

    public static void sentToForeground() {
        mIsForeground = true;
    }
    
}
