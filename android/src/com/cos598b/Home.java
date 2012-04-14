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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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

    // send data points to back-end
    private void sendPoints() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run(){
                while (DatabaseHelper.getNumRows(Home.this) > 0) {
                    Map<String, String> data = DatabaseHelper.popFew(Home.this);
                    // Create a new HttpClient and Post Header
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(Consts.SEND_POINTS_URL);
                    try {
                        // Add data
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                        nameValuePairs.add(new BasicNameValuePair("lat", data.get(DatabaseHelper.KEY_LAT)));
                        nameValuePairs.add(new BasicNameValuePair("lng", data.get(DatabaseHelper.KEY_LNG)));
                        nameValuePairs.add(new BasicNameValuePair("bearing", data.get(DatabaseHelper.KEY_BEARING)));
                        nameValuePairs.add(new BasicNameValuePair("timestamp", data.get(DatabaseHelper.KEY_TIMESTAMP)));
                        nameValuePairs.add(new BasicNameValuePair("time", data.get(DatabaseHelper.KEY_TIME_TILL_WIFI)));
                        nameValuePairs.add(new BasicNameValuePair("speed", data.get(DatabaseHelper.KEY_SPEED)));
                        nameValuePairs.add(new BasicNameValuePair("accuracy", data.get(DatabaseHelper.KEY_ACCURACY)));
                        nameValuePairs.add(new BasicNameValuePair("user_id", Utils.getDeviceID(Home.this)));
                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        // make attempts
                        int attempt = 0;
                        while (attempt < Consts.HTTP_MAX_ATTEMPTS) {
                            HttpResponse response = httpclient.execute(httppost);
                            if (response.getStatusLine().getStatusCode() == 200) {
                                break;
                            } else {
                                attempt = attempt + 1;
                            }
                        }
                    } catch (ClientProtocolException e) {
                        Log.d("Network error", e.toString());
                    } catch (IOException e) {
                        Log.d("Network error", e.toString());
                    }
                }
            }
        });
        thread.start();
    }

    // fetch predictions from back-end
    private void getPredictions() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // start markov service for collecting data points
        startService(new Intent(this, MarkovService.class));

        setContentView(R.layout.main);
        findViewById(R.id.send_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendPoints();
            }
        });
        findViewById(R.id.get_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getPredictions();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNumPoints();
    }

    // refresh the number of points collected
    private void refreshNumPoints() {
        int num_points = DatabaseHelper.getNumRows(this);
        TextView tv = (TextView) findViewById(R.id.num_rows);
        tv.setText(Integer.toString(num_points));
    }
}