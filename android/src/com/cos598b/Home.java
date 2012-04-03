/************************************
 * TO DOS:
 * 1. Popup window for turning GPS on
 * 2. If user doesn't turn GPS on, then quit?
 * 3. If GPS not returning a location (e.g. indoors), then? In this case, it just ignores all this data.
 * 4. So far, only checking if Wifi is *connected*. Should check if any open Wifi is available?
 * 5. If there is movement in 60 seconds, but no movement in 5-second span, we can't detect direction. Currently, it uses the lat/long 
 *    from the previous 60 second span (less accurate since more motion in 60 seconds than 5). Is this okay, 
 *    or should we just discard that data?
 * 6. What if we go out of range of GPS and then get WiFi (e.g. enter a building and then get WiFi). In this
 * 	  case, we wouldn't get any location updates, and will not be capture the fact that WiFi was found.
 */

package com.cos598b;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class Home extends Activity {
	
	
	private boolean isAvailableWiFi() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] ni = cm.getAllNetworkInfo();
		for (NetworkInfo n : ni) {
			if (n.getTypeName().equalsIgnoreCase("wifi")) {
				return true;
			}
		}
		return false;
	}

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
	
	/* is mobile data connected? */
	private boolean isConnectedMobile() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] ni = cm.getAllNetworkInfo();
		for (NetworkInfo n : ni) {
			if ((n.getTypeName().equalsIgnoreCase("mobile")) && (n.isConnected())) {
				return true;
			}
		}
		return false;
	}
	
	/* stop WiFi, if enabled */
	private void disableWiFi() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (wm.isWifiEnabled()) { wm.setWifiEnabled(false); }
	}
	
	/* start WiFi, if disabled */
	private void enableWiFi() {
		WifiManager wm = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		if (!wm.isWifiEnabled()) { wm.setWifiEnabled(true); }
	}
	
	private void stop3G() {
		boolean isEnabled;
		Method dataConnSwitchmethod;
	    Class telephonyManagerClass;
	    Object ITelephonyStub;
	    Class ITelephonyClass;
	    
		TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);

		if(telephonyManager.getDataState() == TelephonyManager.DATA_CONNECTED){
			isEnabled = true;
		} else {
			isEnabled = false;  
		}   
		
		try {
			telephonyManagerClass = Class.forName(telephonyManager.getClass().getName());
			Method getITelephonyMethod = telephonyManagerClass.getDeclaredMethod("getITelephony");
			getITelephonyMethod.setAccessible(true);
			ITelephonyStub = getITelephonyMethod.invoke(telephonyManager);
			ITelephonyClass = Class.forName(ITelephonyStub.getClass().getName());

			if (isEnabled) {
				dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("disableDataConnectivity");
			} else {
				dataConnSwitchmethod = ITelephonyClass.getDeclaredMethod("enableDataConnectivity");   
			}
			dataConnSwitchmethod.setAccessible(true);
			dataConnSwitchmethod.invoke(ITelephonyStub);
		} catch (Exception e) {} 
		/*
		final ConnectivityManager conman = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			final Class conmanClass = Class.forName(conman.getClass().getName());
			final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
			iConnectivityManagerField.setAccessible(true);
			final Object iConnectivityManager = iConnectivityManagerField.get(conman);
			final Class iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());
			final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
			setMobileDataEnabledMethod.setAccessible(true);

			setMobileDataEnabledMethod.invoke(iConnectivityManager, true);
		} catch (Exception e) {}
		*/
	}

	/* Get best known location */
	private Location getLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		
		//lm.addGpsStatusListener(listener);
		// if GPS is disabled, ask user to turn it on // XXX : 1
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			Intent gpsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivity(gpsIntent);
		}
		// regardless of whether turned on or not, use best location tracker // XXX : 2		
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String best_provider = lm.getBestProvider(criteria, true);
		// Log.d("B", best_provider)); // DEBUG
		Location location;
		while ((location = lm.getLastKnownLocation(best_provider)) == null) {
			Log.d("C", "NULL");
		}
		return location;
		//return lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}
   
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);

		/*
		Location location = getLocation();
		Log.d("A", "Lat: " + location.getLatitude() + " Long: " + location.getLongitude());
		*/
		startService(new Intent(this, LocationTracker.class));
		tv.setText("Connected to wifbi");
		setContentView(tv);		
	}	
}