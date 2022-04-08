package com.ola.raven.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

import com.ola.raven.Service.AllServices;

/**
 * Created by Shizzy on 6/30/2017.
 */

public class SimpleWakefulReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Intent service = new Intent(context, AllServices.class);
        // Start the service, keeping the device awake while it is launching.
        Log.i("SimpleWakefulReceiver", "Starting service @ " + SystemClock.elapsedRealtime());
        context.startService(service);
    }
}
