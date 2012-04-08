package com.cos598b;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduledAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MarkovService.onAlarm(context);
    }

}