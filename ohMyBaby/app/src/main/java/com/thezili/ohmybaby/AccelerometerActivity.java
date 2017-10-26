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

    float[] gravity_data = new float[3];
    float[] accel_data = new float[3];
    float[] m_acc_data = new float[3];
    // 중력 데이터를 구하기 위해서 저속 통과 필터를 적용할 때 사용하는 비율 데이터.
    // t : 저속 통과 필터의 시정수. 시정수란 센서가 가속도의 63% 를 인지하는데 걸리는 시간
    // dT : 이벤트 전송율 혹은 이벤트 전송속도.
    // alpha = t / (t + Dt)
    final float alpha = (float)0.8;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.accelerometer_view);

        xText = (TextView) findViewById(R.id.x_value);
        yText = (TextView) findViewById(R.id.y_value);
        zText = (TextView) findViewById(R.id.z_value);

        SM = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    protected void onResume() {
        super.onResume();
        SM.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity_data[0] = alpha * gravity_data[0] + (1 - alpha) * event.values[0]; //먼저 중력데이터를 계산함
            gravity_data[1] = alpha * gravity_data[1] + (1 - alpha) * event.values[1];
            gravity_data[2] = alpha * gravity_data[2] + (1 - alpha) * event.values[2];
            accel_data[0] = event.values[0] - gravity_data[0]; // 순수 가속도센서값에 중력값을 빼줌
            accel_data[1] = event.values[1] - gravity_data[1]; // 아니면 약 9.81 어쩌고 하는값이 더해짐
            accel_data[2] = event.values[2] - gravity_data[2];

            xText.setText("x축 : " + accel_data[0]);
            yText.setText("y축 : " + accel_data[1]);
            zText.setText("z축 : " + accel_data[2]);
        }

        /*if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity_data[0] = alpha * gravity_data[0] + (1 - alpha) * event.values[0]; //먼저 중력데이터를 계산함
            gravity_data[1] = alpha * gravity_data[1] + (1 - alpha) * event.values[1];
            gravity_data[2] = alpha * gravity_data[2] + (1 - alpha) * event.values[2];
            accel_data[0] = event.values[0] - gravity_data[0]; // 순수 가속도센서값에 중력값을 빼줌
            accel_data[1] = event.values[1] - gravity_data[1]; // 아니면 약 9.81 어쩌고 하는값이 더해짐
            accel_data[2] = event.values[2] - gravity_data[2];

            tv1.setText("x축 : " + accel_data[0]);
            tv2.setText("y축 : " + accel_data[1]);
            tv3.setText("z축 : " + accel_data[2]);
        }*/

        //xText.setText("X : " + event.values[0]);
        //yText.setText("Y : " + event.values[1]);
        //zText.setText("Z : " + event.values[2]);
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
