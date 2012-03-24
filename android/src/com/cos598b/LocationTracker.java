package com.cos598b;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import com.cos598b.Consts;

public class LocationTracker extends IntentService {
	private double[] lat = new double[Consts.num_loc_steps];
	private double[] lng = new double[Consts.num_loc_steps];
	private double[] lat_delta = new double[Consts.num_loc_steps];
	private double[] lng_delta = new double[Consts.num_loc_steps];
	private boolean[] wifi_found = new boolean[Consts.num_loc_steps];
	private double lat_last;
	private double lng_last;
	
	/* is WiFi connected */
	private boolean isConnectedWiFi() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] ni = cm.getAllNetworkInfo();
		for (NetworkInfo n : ni) {
			if ((n.getTypeName().equalsIgnoreCase("wifi")) && (n.isConnected())) {
				return true;
			}
		}
		return false;
	}
	
	/* is WiFi found */
	private boolean isFoundWiFi() {
		return isConnectedWiFi();
	}
	
	/* send data to back-end */
	private void sendData(double lat, double lng, double lat_delta, double lng_delta, int steps_till_wifi) {
		
	}
	
	/* 
	 * add a data point for the given latitude and longitude
	 * new points added at 0th index
	 * old points removed from (num_loc_steps-1)th index
	 */
	private void addDataPoint(double lat_add, double lng_add) {
		// store the data point to be removed
		double lat_temp = lat[Consts.num_loc_steps-1];
		double lng_temp = lng[Consts.num_loc_steps-1];
		double lat_delta_temp = lat_delta[Consts.num_loc_steps-1];
		double lng_delta_temp = lng_delta[Consts.num_loc_steps-1];
		boolean wifi_found_temp = wifi_found[Consts.num_loc_steps-1];
		int steps_till_wifi = Integer.MAX_VALUE;
		if (wifi_found_temp) steps_till_wifi = 0;
		
		// move stuff up
		for (int i = Consts.num_loc_steps-1; i > 0; i--) {
			lat[i] = lat[i-1];
			lng[i] = lng[i-1];
			lat_delta[i] = lat_delta[i-1];
			lng_delta[i] = lng_delta[i-1];
			wifi_found[i] = wifi_found[i-1];
			if (wifi_found[i-1] && steps_till_wifi == Integer.MAX_VALUE) steps_till_wifi = Consts.num_loc_steps - (i-1); // first point thereafter that Wifi was found
		}
		// at this point, steps_till_wifi is the number of steps till wifi was found (or Integer.MAX_VALUE if not found)		
		
		// add new point
		lat[0] = lat_add;
		lng[0] = lng_add;
		if (lat_add == lat_last && lng_add == lng_last) { // in case no movement in the 5 second span, use last-to-last location (likely to be less accurate)
			lat_delta[0] = lat[0] - lat[1];
			lat_delta[0] = lng[0] - lat[2];
		}
		else {
			lat_delta[0] = lat[0] - lat_last;
			lat_delta[0] = lng[0] - lng_last;
		}
		wifi_found[0] = isFoundWiFi();
		
		// send data to back-end
		sendData(lat_temp, lng_temp, lat_delta_temp, lng_delta_temp, steps_till_wifi);
	}
	
	public LocationTracker() {
		super("LocationTracker");
	}
	
	// on creating the service
	@Override
	protected void onHandleIntent(Intent intent) {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final long startTime = System.currentTimeMillis();
		
		/* 
		 * Advantages of using Listeners
		 * 1. If no movement, then don't cause updates (e.g. sitting in office)
		 * 2. If no GPS, then no updates (e.g. underground, inside) - hence, forces us to only consider locations 
		 *    where the 3G vs. WiFi is useful (i.e. outside, walking in between buildings etc.)
		 *    
		 * Assumption: If there is movement in the 60 second span, it will trigger updates in both listeners
		 * independently of whether there was movement in the 5 second span. NEED TO TEST THIS XXX
		 */
		// create new listener
		LocationListener listener = new LocationListener() {			
			public void onLocationChanged(Location location) {
				Log.d("A", "Time: " + ((System.currentTimeMillis()-startTime)/1000) + " Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
				lat_last = location.getLatitude();
				lng_last = location.getLongitude();
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
		// create another new listener (for the 5-second delay required for finding the direction)
		LocationListener listener2 = new LocationListener() {
			public void onLocationChanged(Location location) {
				Log.d("B", "Time: " + ((System.currentTimeMillis()-startTime)/1000) + "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
				addDataPoint(location.getLatitude(), location.getLongitude());
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
	    // start the listeners 5 seconds apart
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener);
	    long timeout = System.currentTimeMillis() + (Consts.time_diff * 1000);
	    while (System.currentTimeMillis() < timeout) { // wait time_diff seconds
	    	synchronized(this) {
	    		try { wait(timeout - System.currentTimeMillis()); } catch(Exception e) {}
	    	}
	    }
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener2);
	    
	    
	    // run for 100 seconds // DEBUG
	    timeout = System.currentTimeMillis() + (100*1000);
	    while(System.currentTimeMillis() < timeout) {}
	    
	    
	}
	
	// TESTING ONLY //DEBUG
	@Override
	public void onDestroy() {
		Log.d("Close", "Shutting service");
		super.onDestroy();
	}
}