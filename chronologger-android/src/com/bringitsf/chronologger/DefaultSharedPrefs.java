package com.bringitsf.chronologger;

import android.content.Context;
import android.content.SharedPreferences;

// Similar to flagship's LiSharedPrefsUtils, this is a convenience class to obtain
// the application's default shared preferences
public class DefaultSharedPrefs {

    private static final String CHRONOLOGGER_PREFS_NAME = "com.bringitsf.chronologger.RECRUITER_PREFS";

    // Shared preferences keys
    public static final String SAVED_LAST_TAB = "com.bringitsf.chronologger.SAVED_LAST_TAB";
    public static final String EXTRA_USER_EMAIL = "com.bringitsf.EXTRA_USER_EMAIL";

    public static String getString(String key, String defValue) {
        return getmPrefs().getString(key, defValue);
    }

    public static int getInt(String key, int defVal) {
        return getmPrefs().getInt(key, defVal);
    }

    public static long getLong(String key, long defVal) {
        return getmPrefs().getLong(key, defVal);
    }

    public static boolean getBoolean(String key, boolean defVal) {
        return getmPrefs().getBoolean(key, defVal);
    }

    public static void putInt(String key, int value) {
        getmPrefs().edit().putInt(key, value).apply();
    }

    public static void putLong(String key, long value) {
        getmPrefs().edit().putLong(key, value).apply();
    }

    public static void putString(String key, String value) {
        getmPrefs().edit().putString(key, value).apply();
    }

    public static void putBoolean(String key, boolean value) {
        getmPrefs().edit().putBoolean(key, value).apply();
    }

    public static void remove(String key) {
        getmPrefs().edit().remove(key).apply();
    }

    public static void clear() {
        getmPrefs().edit().clear().apply();
    }

    public static boolean contains(String key) {
        return getmPrefs().contains(key);
    }

    public static SharedPreferences getmPrefs() {
        return ChronoLoggerApplication.getAppContext().getSharedPreferences(CHRONOLOGGER_PREFS_NAME, Context.MODE_PRIVATE);
    }

}
