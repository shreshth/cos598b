package com.cos598b;

import android.provider.Settings;

public class Consts {
    // time granularity for location updates (in seconds)
    public static final int time_granularity = 60;

    // maximum wait for a gps location / wifi scan to return (in seconds)
    public static final int max_wait = 10;

    // how many steps of location data to store
    public static final int num_loc_steps = 10;

    // supported wireless SSID's
    public static final String[] ssids = {"puwireless", "csvapornet"};

    // unique ID for device
    public static final String device_id = Settings.Secure.ANDROID_ID;

    // Number of data points to send in one http request
    public static final int http_batch_limit = 10;
}
