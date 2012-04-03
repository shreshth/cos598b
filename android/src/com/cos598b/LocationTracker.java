package com.cos598b;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationTracker extends IntentService {
	/* 
	 * class for data points 
	 */
	public class DataPoint {
		private double lat;
		private double lng;
		private double lat_delta;
		private double lng_delta;
		private boolean wifi_found;
		private double timestamp;
		private int steps_till_wifi;
	
		public DataPoint(double lat, double lng, double lat_delta, double lng_delta, boolean wifi_found, double timestamp) {
			this.lat = lat;
			this.lng = lng;
			this.lat_delta = lat_delta;
			this.lng_delta = lng_delta;
			this.wifi_found = wifi_found;
			this.timestamp = timestamp;
		}
		
		public double getLat() { return this.lat; }
		public double getLng() { return this.lng; }
		public double getLatDelta() { return this.lat_delta; }
		public double getLngDelta() { return this.lng_delta; }
		public boolean getWifiFound() { return this.wifi_found; }
		public double getTimestamp() { return this.timestamp; }
		public void setStepsTillWifi(int n) { this.steps_till_wifi = n; }
	}
	
	/* 
	 * class variables 
	 */
	/* location model - Markov chain of 10 steps */
	private DataPoint[] loc_steps = new DataPoint[Consts.num_loc_steps]; // Markov chain for tracking movement
	private double lat_last;
	private double lng_last;
	
	/* location tracking */
	private LocationListener listener;
	private LocationListener listener2;
	private LocationManager lm;
	
	/* buffer for sending data to backend */
	private DataPoint[] loc_buf = new DataPoint[Consts.buf_size];
	
	/* handler for printing to Toast */
	Handler toastHandler; 
	
	/*
	 ************************************ FUNCTIONS **********************************
	 */
	
	/* 
	 * is WiFi connected 
	 */
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
	
	/* 
	 * is WiFi found 
	 */
	private boolean isFoundWiFi() {
		return isConnectedWiFi();
	}
	
	private boolean sendData(DataPoint[] point_send) {
		
		return true;
	}
	
	/* 
	 * send data to back-end 
	 */
	private void bufferData(DataPoint point_buf) {
		int i = 0;
		
		if (point_buf == null) return;
		if (point_buf.getLatDelta() == 0 && point_buf.getLngDelta() == 0) return; // corner case if first point ever is added and lat/lng_last were same as lat/lng_add
				
		// add to buffer in following order of priority
		// 1. add to any null spot in the buffer
		// 2. otherwise, add to oldest spot where WiFi wasn't found
		// 3. otherwise, add to oldest spot
		boolean found = false;
		int min_index_no_wifi = -1; // 2
		double min_time_no_wifi = System.currentTimeMillis();
		int min_index = 0; // 3
		double min_time = min_time_no_wifi;
		for (i = 0; i < Consts.buf_size; i++) { 
			if (loc_buf[i] == null) { loc_buf[i] = point_buf; found = true; break; } // 1
			if (!loc_buf[i].getWifiFound() && loc_buf[i].getTimestamp() < min_time_no_wifi) { // 2
				min_time_no_wifi = loc_buf[i].getTimestamp();
				min_index_no_wifi = i;
			}
			if (loc_buf[i].getTimestamp() < min_time) { // 3
				min_time = loc_buf[i].getTimestamp();
				min_index = i;
			}
			
		}
		if (!found && min_index_no_wifi != -1) { // 2
			loc_buf[min_index_no_wifi] = point_buf;
			found = true;
		}
		if (!found) { // 3
			loc_buf[min_index] = point_buf;
			found = true;
		}
		
		// try to empty out buffer, while WiFi is connected
		if (isConnectedWiFi()) {
			sendData(loc_buf);
		}
	}
	
	/* 
	 * add a data point for the given latitude and longitude
	 * new points added at 0th index
	 * old points removed from (num_loc_steps-1)th index
	 */
	private void addDataPoint(double lat_add, double lng_add) {	
		// store the data point to be removed
		DataPoint point_temp = loc_steps[Consts.num_loc_steps-1];
		int steps_till_wifi = Integer.MAX_VALUE;
		if (point_temp.getWifiFound()) steps_till_wifi = 0;
		
		// move stuff up
		for (int i = Consts.num_loc_steps-1; i > 0; i--) {
			loc_steps[i] = loc_steps[i-1];
			if (loc_steps[i-1] != null && point_temp != null) {
				if (loc_steps[i-1].getWifiFound() && steps_till_wifi == Integer.MAX_VALUE) steps_till_wifi = Consts.num_loc_steps - (i-1); // first point thereafter that Wifi was found
			}
		}
		// at this point, steps_till_wifi is the number of steps till wifi was found (or Integer.MAX_VALUE if not found)		
		if (point_temp != null) { point_temp.setStepsTillWifi(steps_till_wifi); }
		
		// add new point
		if (lat_add == lat_last && lng_add == lng_last && loc_steps[1] != null) { // in case no movement in the 5 second span, use last-to-last location (likely to be less accurate)
			DataPoint point_add = new DataPoint(lat_add, lng_add, lat_add - loc_steps[1].getLat(), lng_add - loc_steps[1].getLng(), isFoundWiFi(), System.currentTimeMillis());
			loc_steps[0] = point_add;
		}
		else {
			DataPoint point_add = new DataPoint(lat_add, lng_add, lat_add - lat_last, lng_add - lng_last, isFoundWiFi(), System.currentTimeMillis());
			loc_steps[0] = point_add;
		}
		
		// send data to back-end
		bufferData(point_temp);
	}
	
	/*
	 *************************** MAIN CLASS FUNCTIONS ********************************
	 */
	
	/*
	 * constructor
	 */
	public LocationTracker() {
		super("LocationTracker");
	}
	
	/*
	 * on creating the service
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		toastHandler.post(new DisplayToast("Hello service"));
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final long startTime = System.currentTimeMillis();
		
		// if GPS is disabled, ask user to turn it on // XXX : 1
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			gpsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(gpsIntent);
		}
		
		// dummy locations for debugging
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {toastHandler.post(new DisplayToast("Last GPS: " + location.getLatitude() + " " + location.getLongitude() + " " + location.getBearing()));}
		//Log.d("Last GPS", location.getLatitude() + " " + location.getLongitude() + " " + location.getBearing() + " " + location.getAccuracy());
		location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location != null) {toastHandler.post(new DisplayToast("Last network: " + location.getLatitude() + " " + location.getLongitude() + " " + location.getBearing()));}
		//Log.d("Last Network", location.getLatitude() + " " + location.getLongitude() + " " + location.getBearing() + " " + location.getAccuracy());
		
		// create new listener
		listener = new LocationListener() {			
			@Override
			public void onLocationChanged(Location location) {
				CharSequence text = "A: " + "Time: " + ((System.currentTimeMillis()-startTime)/1000) + " Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " Bearing: " + location.getBearing();
				toastHandler.post(new DisplayToast(text.toString()));
				lat_last = location.getLatitude();
				lng_last = location.getLongitude();
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
		// create another new listener (for the 5-second delay required for finding the direction)
		listener2 = new LocationListener() {
			@Override
			public void onLocationChanged(Location location) {
				CharSequence text = "B: " + "Time: " + ((System.currentTimeMillis()-startTime)/1000) + " Lat: " + location.getLatitude() + " Long: " + location.getLongitude() + " Bearing: " + location.getBearing();
				toastHandler.post(new DisplayToast(text.toString()));
				addDataPoint(location.getLatitude(), location.getLongitude());
			}
			
			public void onStatusChanged(String provider, int status, Bundle extras) {}

		    public void onProviderEnabled(String provider) {}

		    public void onProviderDisabled(String provider) {}
		
	    };
	    
	    // start the listeners 5 seconds apart	    
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener);
	    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener);
	    toastHandler.post(new DisplayToast("First listener started"));
	    long timeout = System.currentTimeMillis() + (Consts.time_diff * 1000);
	    while (System.currentTimeMillis() < timeout) { // wait time_diff seconds
	    	synchronized(this) {
	    		try { wait(timeout - System.currentTimeMillis()); } catch(Exception e) { }
	    	}
	    }
	    lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener2);
	    lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Consts.time_granularity*1000, Consts.dist_granularity, listener2);
	    toastHandler.post(new DisplayToast("Second listener started")); 
	    
	    // run for 100 seconds // DEBUG
	    timeout = System.currentTimeMillis() + (100*1000);
	    while(System.currentTimeMillis() < timeout) {
	    	synchronized(this) {
	    		try { wait(timeout - System.currentTimeMillis()); } catch(Exception e) { }
	    	}
	    }  
	}

	/* 
	 * on creating the service
	 */
	@Override
	public void onCreate()
	{
		super.onCreate();
		toastHandler = new Handler();
	}
	/*
	 * on destroying the service
	 */
	@Override
	public void onDestroy() {
		// stop listeners
		lm.removeUpdates(listener);
		lm.removeUpdates(listener2);
		lm = null;
		
		toastHandler.post(new DisplayToast("End" + "A" + 2));
		super.onDestroy();
	}
	
	/*
	 * display a toast from within non-UI thread
	 */
	private class DisplayToast implements Runnable { 
		String text;

		public DisplayToast(String text){
			this.text = text;
		}

		public void run(){
			Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
		}
	}
}