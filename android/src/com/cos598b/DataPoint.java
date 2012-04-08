package com.cos598b;

/*
 * class for data points
 */
public class DataPoint {
    private double lat;
    private double lng;
    private float bearing;         // 0 - 360 degrees
    private boolean wifi_found;    // whether wifi was found at this point
    private double timestamp;      // ms
    private int time_till_wifi;    // seconds
    private boolean valid;         // whether the data point is valid or not (because we did not have gps or something)

    public DataPoint(double lat, double lng, float bearing, boolean wifi_found, double timestamp, int time_till_wifi) {
        this.lat = lat;
        this.lng = lng;
        this.bearing = bearing;
        this.wifi_found = wifi_found;
        this.timestamp = timestamp;
        this.time_till_wifi = time_till_wifi;
        this.valid = true;
    }

    // return an invalid datapoint
    public static DataPoint getInvalid() {
        DataPoint dp = new DataPoint(0,0,0,false,0,Integer.MAX_VALUE);
        dp.valid = false;
        return dp;
    }

    public double getLat() { return this.lat; }
    public double getLng() { return this.lng; }
    public double getBearing() { return this.bearing; }
    public boolean getWifiFound() { return this.wifi_found; }
    public double getTimestamp() { return this.timestamp; }
    public int getTimeTillWifi() { return this.time_till_wifi; }
    public void setTimeTillWifi(int n) { this.time_till_wifi = n; }
    public boolean isValid() { return this.valid; }
}