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
import android.os.Handler;
import android.os.RemoteException;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bringitsf.chronologger.TabPagerAdapter.TabType;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.BeaconManager.ErrorListener;
import com.estimote.sdk.BeaconManager.MonitoringListener;
import com.estimote.sdk.BeaconManager.RangingListener;
import com.estimote.sdk.BeaconManager.ServiceReadyCallback;
import com.estimote.sdk.Region;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getName();
    private static final int RANGING_DELAY = 5000;
    private static final int MENU_LOG_OUT_ID = 10;
    private static final int REQUEST_ENABLE_BT = 1000;

    private BeaconManager mBeaconManager;
    private FragmentPagerAdapter mAdapter;
    private TabPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        final ActionBar actionBar = getActionBar();
        
        VolleyHandler.init(this);
        ServerConnection.init(this);
        
        // Set up view pager
        mViewPager = (TabPager) findViewById(R.id.tabPager);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // Select correct tab in the action bar
                actionBar.setSelectedNavigationItem(position);
            }
        });
        mAdapter = new TabPagerAdapter(getFragmentManager(), Arrays.asList(TabType.CURRENT, TabType.HISTORY, TabType.ADMIN));
        mViewPager.setAdapter(mAdapter);
        
        // Set up tabbing in actionBar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        //FIXME: don't add admin tab is logged on user isn't admin
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_current).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_history).setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText(R.string.tab_admin).setTabListener(tabListener));

        // Set up estimote manager
        mBeaconManager = new com.estimote.sdk.BeaconManager(this);
        mBeaconManager.setRangingListener(new RangingListener() {
            public void onBeaconsDiscovered(final Region region, final List<Beacon> beacons) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerConnection.discoveredBeacons(region, beacons);
                    }
                });
            }
        });
        
        mBeaconManager.setMonitoringListener(new MonitoringListener() {
            public void onEnteredRegion(final Region region) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerConnection.enteredRegion(region);
                    }
                });
            }
            
            public void onExitedRegion(final Region region) {
                // Note that results are not delivered on UI thread.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ServerConnection.exitedRegion(region);
                    }
                });
            }
        });
        
        mBeaconManager.setErrorListener(new ErrorListener() {
            @Override
            public void onError(Integer errorId) {
                Log.e(TAG, "Estimote error: " + errorId);
            }
        });
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        ChronoLoggerApplication.sentToForeground();
        // Check if device supports Bluetooth Low Energy.
        if (!mBeaconManager.hasBluetooth()) {
            //FIXME: turn into dialog
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
        if (!mBeaconManager.isBluetoothEnabled()) {
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
        try {
            mBeaconManager.stopRanging(ServerConnection.defaultRegion);
            mBeaconManager.stopMonitoring(ServerConnection.defaultRegion);
        } catch (RemoteException remoteEx) {
            Log.e(TAG, "Could not stop ranging and monitoring for iBeacons during onDestroy: " + remoteEx.toString());
            remoteEx.printStackTrace();
        }
        mBeaconManager.disconnect();
        super.onDestroy();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                // Begin connection to our beacons
                connectBeacons();
            } else {
                Toast.makeText(getApplication(),  "Chrono Logger will not be able to actively log you without Bluetooth", Toast.LENGTH_LONG).show();
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
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
    }
    
    private void connectBeacons() {
        mBeaconManager.connect(new ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    mBeaconManager.startRanging(ServerConnection.defaultRegion);
                    
                    // Only do ranging requests for 5 seconds before going into passive monitoring mode
                    Handler rangingHandler = new Handler();
                    rangingHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mBeaconManager.stopRanging(ServerConnection.defaultRegion);
                                mBeaconManager.startMonitoring(ServerConnection.defaultRegion);
                            } catch(RemoteException remoteEx) {
                                Log.e(TAG, "Could not stop ranging and begin monitoring for iBeacons: " + remoteEx.toString());
                                remoteEx.printStackTrace();
                                Toast.makeText(ChronoLoggerApplication.getAppContext(), R.string.toast_ranging_failed, Toast.LENGTH_LONG).show();
                            }
                        };
                    }, RANGING_DELAY);
                } catch (RemoteException remoteEx) {
                    Log.e(TAG, "Could not start ranging for iBeacons: " + remoteEx.toString());
                    remoteEx.printStackTrace();
                    Toast.makeText(ChronoLoggerApplication.getAppContext(), R.string.toast_ranging_failed, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    
    private void logout() {
        //FIXME: async call to logout
        NavUtils.navigateUpFromSameTask(this);
    }
    
}
