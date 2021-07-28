package com.tvinfo.requestpar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.partybuild.boxindex_cmcc.DeviceUNHelper;
import com.partybuild.boxindex_cmcc.DeviceUnCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.get).setOnClickListener(v -> new Thread(() -> {
            DeviceUNHelper.getDeviceUN(new DeviceUnCallBack() {
                @Override
                public void onSuccess(String deviceUn) {
                    v.post(() -> {
                        ((TextView) findViewById(R.id.info)).setText("DEVICE_UN : " + deviceUn);
                    });
                }

                @Override
                public void onFailed() {
                    v.post(() -> {
                        ((TextView) findViewById(R.id.info)).setText("DEVICE_UN : " + "失败");
                    });
                }
            });

        }).start());
    }

    @Override
    protected void onResume() {
        super.onResume();
        myRequetPermission();
    }

    private void myRequetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        } else {
            Toast.makeText(this, "您已经申请了权限!", Toast.LENGTH_SHORT).show();
        }
    }
}