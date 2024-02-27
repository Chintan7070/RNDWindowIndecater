package com.rndwindowindecater;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rndwindowindecater.services.IndecaterService;
import com.rndwindowindecater.utils.ConstantVal;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvTakePermission;
    private TextView tvStartService, tvWriteSetting,tvStopService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initIdes();
        clicks();
    }

    private void clicks() {
        tvTakePermission.setOnClickListener(this);
        tvStartService.setOnClickListener(this);
        tvWriteSetting.setOnClickListener(this);
        tvStopService.setOnClickListener(this);
    }

    private void initIdes() {
        tvTakePermission = findViewById(R.id.tvTakePermission);
        tvStartService = findViewById(R.id.tvStartService);
        tvWriteSetting = findViewById(R.id.tvWriteSetting);
        tvStopService = findViewById(R.id.tvStopService);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvTakePermission) {
           /* Settings.Secure.putString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES,
                    "com.rndwindowindecater.services.IndecaterService");*/
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        } else if (v.getId() == R.id.tvStartService) {
            Intent intent = new Intent(MainActivity.this, IndecaterService.class);
            startService(intent);

        } else if (v.getId() == R.id.tvWriteSetting) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ConstantVal.OVERLAYREQUEST_CODE);
            } else {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        } else if (v.getId() == R.id.tvStopService) {
            Intent intent = new Intent(MainActivity.this, IndecaterService.class);
            stopService(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ConstantVal.OVERLAYREQUEST_CODE) {
            /*if (Settings.canDrawOverlays(this)) {
            } else {
            }*/
            if (!Settings.System.canWrite(MainActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.fromParts("package", getPackageName(), null));
                startActivity(intent);
            }
        }
    }
}