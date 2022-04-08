package com.ola.raven.Finder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ola.raven.Activity_Notify;
import com.ola.raven.ImageRequest.ImageDisplay;
import com.ola.raven.Login;
import com.ola.raven.Map.MapsActivity;
import com.ola.raven.R;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static android.support.v7.widget.AppCompatDrawableManager.get;
import static com.ola.raven.R.id.fab;
import static com.ola.raven.R.id.reboot;

public class FinderActivity extends AppCompatActivity implements View.OnClickListener{

    private Button location,ping,sms,reboot,capture,logout,stop;
    private TextView showloc;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference,userRef;
    private String uid,lat,lng,address,name;
    private StringBuilder stringBuilder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finder);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        location = (Button) findViewById(R.id.getlocation);
        location.setOnClickListener(this);
        ping = (Button)findViewById(R.id.ping);
        ping.setOnClickListener(this);
        sms = (Button) findViewById(R.id.sms);
        sms.setOnClickListener(this);
        reboot = (Button) findViewById(R.id.reboot);
        reboot.setOnClickListener(this);
        capture = (Button) findViewById(R.id.capture);
        capture.setOnClickListener(this);
        showloc = (TextView) findViewById(R.id.location);
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(this);
        stop = (Button) findViewById(R.id.stop);
        stop.setOnClickListener(this);

        if(firebaseAuth.getCurrentUser() != null){
            uid = firebaseAuth.getCurrentUser().getUid();
            userRef = databaseReference.child(uid);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(lng)){
                    Intent intent = new Intent(FinderActivity.this, MapsActivity.class);
                    intent.putExtra("lat",lat);
                    intent.putExtra("lng",lng);
                    intent.putExtra("name",name);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.getlocation:
                //getLocation
                Toast.makeText(this,"command sent", Toast.LENGTH_SHORT).show();
                getLocation();
                break;
            case R.id.ping:
                //Ping device
                userRef.child("command").setValue("ping");
                Toast.makeText(this,"command sent", Toast.LENGTH_LONG).show();
                break;
            case R.id.sms:
                //getsms
                userRef.child("command").setValue("sms");
                Toast.makeText(this,"command sent", Toast.LENGTH_LONG).show();
                break;
            case R.id.reboot:
                //Reboot
                userRef.child("command").setValue("reboot");
                Toast.makeText(this, "command sent", Toast.LENGTH_LONG).show();
                break;
            case R.id.capture:
                //capture
                //userRef.child("command").setValue("capture");
                startActivity(new Intent(FinderActivity.this, Activity_Notify.class));
                break;
            case R.id.logout:
                firebaseAuth.signOut();
                startActivity(new Intent(FinderActivity.this, Login.class));
                break;
            case R.id.stop:
                userRef.child("command").setValue("stop");
                Toast.makeText(this, "command sent", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void getLocation(){
        if(firebaseAuth.getCurrentUser() != null){
            userRef.child("command").setValue("location");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lat = dataSnapshot.child("latitude").getValue(String.class);
                    lng = dataSnapshot.child("longitude").getValue(String.class);
                    address = dataSnapshot.child("address").getValue(String.class);
                    name = dataSnapshot.child("name").getValue(String.class);
                    //
                    //if(!lat.equals("") && !lng.equals("")){
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Latitude: "+lat+"\n");
                    stringBuilder.append("Longitude: "+lng+"\n");
                    stringBuilder.append("Address "+address);


                    showloc.setText(stringBuilder.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
