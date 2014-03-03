package com.bringitsf.chronologger;

import com.estimote.sdk.Beacon;

public class NotifyBeacon {
    
    private Beacon mBeacon;
    private boolean mCheckedIn = true;
    
    public NotifyBeacon(Beacon beacon) {
        mBeacon = beacon;
    }
    
    public Beacon getBeacon() {
        return mBeacon;
    }
    
    public boolean hasCheckedIn() {
        return mCheckedIn;
    }
    
    public void setCheckedIn(boolean checkedIn) {
        mCheckedIn = checkedIn;
    }
}