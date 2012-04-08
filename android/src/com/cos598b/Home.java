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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.TextView;


public class Home extends Activity {

    /* is WiFi connected */
    private boolean isConnectedWiFi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] ni = cm.getAllNetworkInfo();
        for (NetworkInfo n : ni) {
            if (n.getTypeName().equalsIgnoreCase("wifi") && n.isConnected()) {
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
            if (n.getTypeName().equalsIgnoreCase("mobile") && n.isConnected()) {
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
        // impossibru
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MarkovService.class);
        intent.putExtra("METHOD", "start");
        startService(new Intent(this, MarkovService.class));
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DatabaseHelper db = new DatabaseHelper(this);
        int num_points = db.getNumRows();

        TextView tv = (TextView) findViewById(R.id.num_rows);
        tv.setText(Integer.toString(num_points));
    }
}