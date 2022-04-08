package com.ola.raven;

import android.*;
import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ola.raven.CameraService.APictureCapturingService;
import com.ola.raven.CameraService.PictureCapturingServiceImpl;
import com.ola.raven.Listeners.PictureCapturingListener;
import com.ola.raven.RegComplete.RegComplete;
import com.ola.raven.Service.AllServices;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.RunnableFuture;

import static android.os.Build.VERSION_CODES.N;
import static com.google.android.gms.common.api.Status.sl;

public class MainActivity extends AppCompatActivity implements PictureCapturingListener, ActivityCompat.OnRequestPermissionsResultCallback {
    private ImageView imageView,display;
    private PopupMenu popup;
    private EditText name,password,mail,phone,recovery,poppass;
    private Button signUp,verify;
    private FirebaseAuth auth;
    private ProgressDialog progress;
    private DatabaseReference database;
    public static final int MY_PERMISSIONS_REQUEST_ACCESS_CODE = 1;

    //service
    private APictureCapturingService pictureService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //imageView = (ImageView) findViewById(R.id.pop);
        name = (EditText) findViewById(R.id.name);
        password = (EditText) findViewById(R.id.password);
        mail = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        recovery = (EditText) findViewById(R.id.recovery);
        signUp = (Button) findViewById(R.id.signup);
        display = (ImageView) findViewById(R.id.image);


        progress = new ProgressDialog(this);

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpUser();
            }
        });
        // pictureService = PictureCapturingServiceImpl.getInstance(MainActivity.this);
        /*imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showPopUp();
                showToast("Starting capture!");
                pictureService.startCapturing(MainActivity.this);
            }
        });*/

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference().child("Users");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    showToast("Starting capture!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //pictureService.startCapturing(MainActivity.this);
                        }
                    });
                }
            }
        });
        //t.start();
    }

    private void signUpUser() {
        final String n = name.getText().toString().trim();
        String email = mail.getText().toString().trim();
        final String pas = password.getText().toString().trim();
        final String phone_no = phone.getText().toString().trim();
        final String reco = recovery.getText().toString().trim();

        if(!TextUtils.isEmpty(n) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(pas) && !TextUtils.isEmpty(phone_no)
                && !TextUtils.isEmpty(reco)){
            progress.setMessage("Signing Up...");
            progress.setIndeterminate(true);
            progress.show();
            auth.createUserWithEmailAndPassword(email,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful() && auth.getCurrentUser() != null){
                        String user_id = auth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = database.child(user_id);
                        current_user_db.child("name").setValue(n);
                        current_user_db.child("phone").setValue(phone_no);
                        current_user_db.child("recovery").setValue(reco);
                        current_user_db.child("command").setValue("none");
                        current_user_db.child("password").setValue(pas);
                        progress.dismiss();
                        startActivity(new Intent(MainActivity.this, RegComplete.class));
                    }
                }
            });
        }
    }

    private void startService(){
        startService(new Intent(getBaseContext(), AllServices.class));
    }

    private void hold(final long time){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void showPopUp() {
        //Creating the instance of PopupMenu
        popup = new PopupMenu(MainActivity.this, imageView);
        //Inflating the Popup using xml file
        popup.getMenuInflater()
                .inflate(R.menu.menu_main, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.stolen:
                        Intent i = new Intent(MainActivity.this,Login.class);
                        i.putExtra("stolen",item.getTitle());
                        startActivity(i);
                        break;
                    case R.id.misplaced:
                        Intent is = new Intent(MainActivity.this,Login.class);
                        is.putExtra("misplaced",item.getTitle());
                        //startActivity(is);
                        showDialog();
                        break;
                }
                return true;
            }

        });
        popup.show();
    }

    private void showDialog(){
        final Dialog d = new Dialog(this,R.style.question_dialog);
        d.setCancelable(false);
        d.setTitle("Enter Raven Password");
        d.setContentView(R.layout.popup);
        poppass = (EditText) d.findViewById(R.id.popPassword);
        verify = (Button) d.findViewById(R.id.save);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                d.dismiss();
            }
        });
        d.show();
    }

    @Override
    public void onCaptureDone(final String pictureUrl, final byte[] pictureData) {
        if (pictureData != null && pictureUrl != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeByteArray(pictureData, 0, pictureData.length);
                    final int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                    final Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true);
                    if (pictureUrl.contains("0_pic.jpg")) {
                        display.setImageBitmap(scaled);
                    } else if (pictureUrl.contains("1_pic.jpg")) {
                        display.setImageBitmap(scaled);
                    }
                }
            });
            showToast("Picture saved to " + pictureUrl);
        }
    }

    @Override
    public void onDoneCapturingAllPhotos(TreeMap<String, byte[]> picturesTaken) {
        if (picturesTaken != null && !picturesTaken.isEmpty()) {
            showToast("Done capturing all photos!");
            return;
        }
        showToast("No camera detected!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_CODE: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    checkPermissions();
                }
            }
        }
    }

    /**
     * checking  permissions at Runtime.
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        final String[] requiredPermissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };
        final List<String> neededPermissions = new ArrayList<>();
        for (final String permission : requiredPermissions) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        if (!neededPermissions.isEmpty()) {
            requestPermissions(neededPermissions.toArray(new String[]{}),
                    MY_PERMISSIONS_REQUEST_ACCESS_CODE);
        }
    }


}

