package com.cos598b;


public class Consts {

    // ------------------------ Markov Constants ------------------------------- //

    // time granularity for location updates (in seconds)
    public static final int TIME_GRANULARITY = 60;

    // maximum wait for a gps location / wifi scan to return (in seconds)
    public static final int MAX_WAIT = 10;

    // total length of markov model in seconds
    public static final int MARKOV_TOTAL_SECONDS = 10*60;

    // how many steps of location data to store
    public static final int NUM_MARKOV_STEPS = MARKOV_TOTAL_SECONDS / TIME_GRANULARITY;

    // supported wireless SSID's
    public static final String[] SSID_WHITELIST = {"puwireless", "csvapornet"};

    // minimum wifi power level
    public static final int MIN_WIFI_POWER = -80;

    // ------------------------ HTPP Constants --------------------------------- //

    // Number of data points to send in one http request
    public static final int HTTP_BATCH_LIMIT = 10;

    // URL for sending data to backend
    public static final String SEND_POINTS_URL = "http://cos598b.appspot.com/add_data";

    // number of tries to make for an http request before giving up
    public static final int HTTP_MAX_ATTEMPTS = 3;

    // ------------------------ Test Constants --------------------------------- //

    // whether this is a test build
    public static final boolean TEST_MODE = true;
}
