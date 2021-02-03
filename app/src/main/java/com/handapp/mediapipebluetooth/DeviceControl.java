package com.handapp.mediapipebluetooth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.handapp.mediapipebluetooth.ui.main.DeviceControlFragment;

public class DeviceControl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_control_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, DeviceControlFragment.newInstance())
                    .commitNow();
        }
    }
}