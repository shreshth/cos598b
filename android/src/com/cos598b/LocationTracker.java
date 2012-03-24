package com.cos598b;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.cos598b.Consts;

public class LocationTracker extends IntentService {
	public LocationTracker() {
		super("LocationTracker");
	}
	
	// on creating the service
	@Override
	protected void onHandleIntent(Intent intent) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		// create new listener
		LocationListener listener = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.d("A", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
		// create another new listener
		LocationListener listener2 = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.d("B", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
	    /*
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time_granularity*1000, dist_granularity, listener);
	    long timeout = System.currentTimeMillis() + (time_diff * 1000);
	    while (System.currentTimeMillis() < timeout) { // wait time_diff seconds
	    	synchronized(this) {
	    		try { wait(timeout - System.currentTimeMillis()); } catch(Exception e) {}
	    	}
	    }
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, time_granularity*1000, dist_granularity, listener2);
	    */
	    for (int i = 0; i < 5; i++) {
	    	long timeout = System.currentTimeMillis() + (Consts.time_diff * 1000);
		    while (System.currentTimeMillis() < timeout) { // wait time_diff seconds
		    	synchronized(this) {
		    		try { wait(timeout - System.currentTimeMillis()); } catch(Exception e) {}
		    	}
		    }
		    Log.d("Hello", "Iteration " + i);
	    }
	    
	}
	
	// TESTING ONLY //DEBUG
	@Override
	public void onDestroy() {
		Log.d("Close", "Shutting service");
		super.onDestroy();
	}
}