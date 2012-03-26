package com.cos598b;

import android.provider.Settings;

public class Consts {
	public static final int time_granularity = 60; // time granularity for location updates (in seconds)
	public static final int time_diff = 5;         // time difference between two GPS readings for direction calculation
	public static final int dist_granularity = 2;  // distance granularity for location updates (in metres)
	public static final int num_loc_steps = 10;    // how many steps of location data to store
	public static final int buf_size = 512;        // how many data points to store before needing to send to back-end
	public static final String device_id = Settings.Secure.ANDROID_ID; // unique ID for device
}
