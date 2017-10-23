package com.thezili.ohmybaby;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by KangEunSook on 2017-10-07.
 */

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private TextView xText, yText, zText = null;
    private Sensor mSensor;
    private SensorManager SM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_view);

        xText = (TextView) findViewById(R.id.x_value);
        yText = (TextView) findViewById(R.id.y_value);
        zText = (TextView) findViewById(R.id.z_value);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        SM.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_FASTEST);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xText.setText("X : " + event.values[0]);
        yText.setText("Y : " + event.values[1]);
        zText.setText("Z : " + event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();

        SM.unregisterListener(this);
    }
}
