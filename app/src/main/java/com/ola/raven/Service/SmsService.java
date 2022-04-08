package com.ola.raven.Service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ola.raven.Broadcast.SmsReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by hopeSMART on 8/3/2017.
 */

public class SmsService extends Service {
    SimpleDateFormat sf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private IntentFilter mIntentFilter;
    private SmsReceiver smsReceiver;
    private DatabaseReference smsRef;
    private String uid;
    private FirebaseAuth auth;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        smsReceiver = new SmsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, mIntentFilter);
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            uid = auth.getCurrentUser().getUid();
            smsRef = FirebaseDatabase.getInstance().getReference("Users").child(uid).child("sms");
        }



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String data=intent.getStringExtra("data");
        smsRef.child(sf.format(new Date())).setValue(data);
        Toast.makeText(this, "started...", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
}
