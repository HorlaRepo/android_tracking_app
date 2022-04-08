package com.ola.raven.Service;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ola.raven.Activity_Notify;
import com.ola.raven.Broadcast.SmsReceiver;
import com.ola.raven.CameraService.APictureCapturingService;
import com.ola.raven.CameraService.PictureCapturingServiceImpl;
import com.ola.raven.Listeners.PictureCapturingListener;
import com.ola.raven.Location.GPSTracker;
import com.ola.raven.MainActivity;
import com.ola.raven.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static com.ola.raven.MainActivity.MY_PERMISSIONS_REQUEST_ACCESS_CODE;

/**
 * Created by Shizzy on 6/30/2017.
 */

public class AllServices extends Service implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback{
    private final Context context;
    MediaPlayer mediaPlayer;
    GPSTracker gps;
    double latitude;
    double longitude;
    FirebaseAuth auth;
    private DatabaseReference databaseReference,smsRef;
    private String uid;
    StorageReference firebaseStorage;
    //service
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError);
        firebaseStorage = FirebaseStorage.getInstance().getReference();
        mediaPlayer = MediaPlayer.create(this, R.raw.alarm);
        gps = new GPSTracker(this);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");
        smsRef = FirebaseDatabase.getInstance().getReference().child("");
        if(auth.getCurrentUser()!= null){
            uid = auth.getCurrentUser().getUid();
        }
    }

    public AllServices(Context context){
        this.context = context;
    }

    public AllServices(){
        context=null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final DatabaseReference ref = databaseReference.child(uid);
        //Toast.makeText(AllServices.this, "Service started "+uid, Toast.LENGTH_SHORT).show();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("command")){
                    if(dataSnapshot.child("command").getValue().equals("location")){
                        //getLocation
                        latitude = gps.getLatitude();
                        longitude = gps.getLongitude();
                        String address = gps.getAddress(latitude,longitude,AllServices.this);
                        ref.child("latitude").setValue(String.valueOf(latitude));
                        ref.child("longitude").setValue(String.valueOf(longitude));
                        ref.child("address").setValue(address);
                    } else if(dataSnapshot.child("command").getValue().equals("ping")){
                        //ping Device
                        try {
                            mediaPlayer = MediaPlayer.create(AllServices.this, R.raw.alarm);
                            mediaPlayer.start();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        mediaPlayer.setLooping(true);
                    } else if (dataSnapshot.child("command").getValue().equals("capture")){
                        //Intent intent1 = new Intent(AllServices.this,Dummy.class);
                        //intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        //startActivity(intent1);
                    } else if(dataSnapshot.child("command").getValue().equals("reboot")){
                        //reboot device
                        if(isRooted()){
                            shutdown();
                        }
                    } else if(dataSnapshot.child("command").getValue().equals("sms")){
                        //send sms
                        String phone = dataSnapshot.child("recovery").getValue(String.class);
                        sendSms(phone,"Your device is currently with the sender of this message");
                    }else if(dataSnapshot.child("command").getValue().equals("stop")){
                        if(mediaPlayer.isPlaying()){
                            mediaPlayer.stop();
                            mediaPlayer.setLooping(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    

    public void sendSms(String phone, String msg){
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(msg);
        sms.sendMultipartTextMessage(phone,null,parts,null,null);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

    @Override
    public void onCaptureDone(final String pictureUrl, final byte[] pictureData) {

    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            return;
        }
    }

    public static boolean findBinary(String binaryName) {
        boolean found = false;
        if (!found) {
            String[] places = { "/sbin/", "/system/bin/", "/system/xbin/",
                    "/data/local/xbin/", "/data/local/bin/",
                    "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/" };
            for (String where : places) {
                if (new File(where + binaryName).exists()) {
                    found = true;

                    break;
                }
            }
        }
        return found;
    }

    private static boolean isRooted() {
        return findBinary("su");
    }

    private void shutdown(){
        try {
            Process proc = Runtime.getRuntime()
                    .exec(new String[]{ "su", "-c", "reboot -p" });
            proc.waitFor();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private Thread.UncaughtExceptionHandler onRuntimeError= new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            //Try starting the service again
            startService();
            //May be a pending intent might work
        }
    };

    private void startService(){
        startService(new Intent(getBaseContext(), AllServices.class));
    }

}
