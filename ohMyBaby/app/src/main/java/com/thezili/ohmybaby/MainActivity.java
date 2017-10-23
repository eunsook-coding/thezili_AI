package com.thezili.ohmybaby;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button accelerometer_btn = null;
    private Button gyroscope_btn = null;
    private Button camera_btn = null;
    private Button opencv_btn = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_view);

        accelerometer_btn = (Button) findViewById(R.id.acce_start);
        gyroscope_btn = (Button) findViewById(R.id.gyro_start);
        camera_btn = (Button) findViewById(R.id.camera);
        opencv_btn = (Button) findViewById(R.id.opencv);

        accelerometer_btn.setOnClickListener(this);
        gyroscope_btn.setOnClickListener(this);
        camera_btn.setOnClickListener(this);
        opencv_btn.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.acce_start :
                Intent aIntent = new Intent(this, AccelerometerActivity.class);
                startActivity(aIntent);
                break;

            case R.id.gyro_start :
                Intent gIntent = new Intent(this, GyroscopeActivity.class);
                startActivity(gIntent);

                break;

            case R.id.camera :
                Intent cIntent = new Intent(this, CameraActivity.class);
                startActivity(cIntent);
                break;

            case R.id.opencv :
                Intent oIntent = new Intent(this, OpenCVActivity.class);
                startActivity(oIntent);
                break;


        }
    }
}
