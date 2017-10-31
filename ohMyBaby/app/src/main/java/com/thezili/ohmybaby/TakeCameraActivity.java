package com.thezili.ohmybaby;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class TakeCameraActivity extends Activity implements SurfaceHolder.Callback {
    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button button;
    String str;

    @SuppressWarnings("deprecation")
    Camera.PictureCallback jpegCallback;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.take_camera_activity);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, jpegCallback);
            }
        });

        getWindow().setFormat(PixelFormat.UNKNOWN);


        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    str = String.format("/sdcard/%d.jpg",
                            System.currentTimeMillis());
                    outStream = new FileOutputStream(str);

                    outStream.write(data);
                    outStream.close();
                }

                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                catch (IOException e) {
                    e.printStackTrace();
                }

                finally {
                }

                Toast.makeText(getApplicationContext(),
                        "Picture Saved", Toast.LENGTH_LONG).show();
                refreshCamera();

                Intent intent = new Intent(TakeCameraActivity.this, ResultCameraACtivity.class);
                intent.putExtra("strParamName", str);
                startActivity(intent);
            }
        };
    }


    public void refreshCamera() {
        if (surfaceHolder.getSurface() == null) {
            return;
        }

        try {
            camera.stopPreview();
        }

        catch (Exception e) {
        }

        try {
            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @SuppressWarnings("deprecation")
    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        camera = Camera.open();
        camera.stopPreview();
        Camera.Parameters param = camera.getParameters();
        param.setRotation(90);
        camera.setDisplayOrientation(90);

        //Camera Portrait
        if (getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
            param.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            param.setRotation(270);
        } else {
            param.set("orientation", "landscape");
            camera.setDisplayOrientation(0);
            param.setRotation(0);
        }
        camera.setParameters(param);

        try {

            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder,
                               int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}