package com.thezili.ohmybaby;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

public class GyroscopeActivity extends Activity {
    private TextView xText, yText, zText = null;
    private TextView pitchText, rollText, yawText = null;

    private Sensor mGyroscopeSensor = null;
    private Sensor mAccelrometerSensor = null;
    private SensorManager mSensorManager = null;

    private UserSensorListener mUserSensorListener;

    private double timestamp;
    private double dt;

    private double pitch;
    private double roll;
    private double yaw;

    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f/1000000000.0f;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gyroscope_view);

        Log.d("thezili", "GyroscopeActivity, onCreate()");

        xText = (TextView) findViewById(R.id.x_value);
        yText = (TextView) findViewById(R.id.y_value);
        zText = (TextView) findViewById(R.id.z_value);

        pitchText = (TextView) findViewById(R.id.pitch_value);
        rollText = (TextView) findViewById(R.id.roll_value);
        yawText = (TextView) findViewById(R.id.yaw_value);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mGyroscopeSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mUserSensorListener = new UserSensorListener();
        mSensorManager.registerListener(mUserSensorListener, mGyroscopeSensor, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(mUserSensorListener, mAccelrometerSensor, SensorManager.SENSOR_DELAY_UI);

    }


    public class UserSensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("thezili", "GyroscopeActivity, onSensorChanged()");

            xText.setText("X : " + event.values[0]);
            yText.setText("Y : " + event.values[1]);
            zText.setText("Z : " + event.values[2]);

            double gyroX = event.values[0];
            double gyroY = event.values[1];
            double gyroZ = event.values[2];

            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            if (dt - timestamp * NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                pitch = pitch + gyroY * dt;
                roll = roll + gyroX * dt;
                yaw = yaw + gyroZ * dt;


                pitchText.setText("Pitch : " + pitch);
                rollText.setText("Roll : " + roll);
                yawText.setText("Yaw : " + yaw);
            }
        }

        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){
            mSensorManager.unregisterListener(this);
        }

    }
}

