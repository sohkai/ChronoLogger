package com.bringitsf.chronologger;

import java.util.Arrays;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bringitsf.chronologger.TabPagerAdapter.TabType;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager.ErrorListener;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.BeaconManager.ServiceReadyCallback;
import com.estimote.sdk.Region;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private static final int MENU_LOG_OUT_ID = 10;
    private static final int REQUEST_ENABLE_BT = 1000;

    private FragmentPagerAdapter mAdapter;
    private TabPager mTabPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final ActionBar actionBar = getActionBar();
        
        // Init various singletons
        VolleyHandler.init(this);
        BeaconManager.init(this);
        BeaconHandler.init(this);
        
        // Set up estimote manager
        BeaconManager.setRangingListener(new RangingListener() {
            public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeaconHandler.discoveredBeacons(region, beacons);
                    }
                });
            }
        });
        
        BeaconManager.getInstance().setMonitoringListener(new MonitoringListener() {
            public void onEnteredRegion(final Region region) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeaconHandler.enteredRegion(region);
                    }
                });
            }
            
            public void onExitedRegion(final Region region) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        BeaconHandler.exitedRegion(region);
                    }
                });
            }
        });
        
        BeaconManager.getInstance().setErrorListener(new ErrorListener() {
            @Override
            public void onError(Integer errorId) {
                Log.e(TAG, "Estimote error: " + errorId);
            }
        });
        
        // Set up view pager
        mTabPager = (TabPager) findViewById(R.id.tabPager);
        mTabPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Select correct tab in the action bar
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mAdapter = new TabPagerAdapter(getFragmentManager(), Arrays.asList(TabType.HOME, TabType.HISTORY, TabType.ADMIN));
        mTabPager.setAdapter(mAdapter);
        
        // Set up tabbing in actionBar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mTabPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        //FIXME: don't add admin tab if logged on user isn't admin
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_home).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_history).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_admin).setTabListener(tabListener));
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        ChronoLoggerApplication.sentToForeground();
        // Check if device supports Bluetooth Low Energy.
        if (!BeaconManager.getInstance().hasBluetooth()) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setMessage(R.string.dialog_device_not_support).setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    finish();
                }
                }).create();
           return;
        }
    
        // If Bluetooth is not enabled, let user enable it.
        if (!BeaconManager.getInstance().isBluetoothEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            // Begin connection to our beacons
            connectBeacons();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        DefaultSharedPrefs.putInt(DefaultSharedPrefs.SAVED_LAST_TAB, getActionBar().getSelectedNavigationIndex());
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        ChronoLoggerApplication.sentToBackground();
    }

    @Override
    protected void onDestroy() {
        BeaconManager.getInstance().disconnect();
        super.onDestroy();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Begin connection to our beacons
                connectBeacons();
            } else {
                Toast.makeText(getApplication(), R.string.toast_bluetooth_not_enabled, Toast.LENGTH_LONG).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add sign out menu item
        menu.add(Menu.NONE, MENU_LOG_OUT_ID, Menu.NONE, R.string.menu_sign_out);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
            case MENU_LOG_OUT_ID:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    
    private void connectBeacons() {
        BeaconManager.getInstance().connect(new ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                // Set the item to home and begin polling for beacons
                mTabPager.setCurrentItem(0);
            }
        });
    }
    
    private void logout() {
        //FIXME: no legit login system means no legit logout system. Yay web team!
        NavUtils.navigateUpFromSameTask(this);
    }
    
}
