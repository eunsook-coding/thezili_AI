package com.thezili.ohmybaby;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class TakeCameraActivity extends Activity implements SurfaceHolder.Callback {

    //Note2 해상도에 맞춤.
    private static final int   IMAGE_WIDTH = 1280; // 찍을 넓이
    private static final int   IMAGE_HEIGHT = 720; // 찍을 높이

    @SuppressWarnings("deprecation")
    Camera camera;
    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Button button;
    ImageView imageview;
    String str;
    private int mWidth = 0;
    private int mHeight = 0;

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
        imageview = (ImageView) findViewById(R.id.imageview);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture(null, null, jpegCallback);
            }
        });

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        jpegCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                FileOutputStream outStream = null;
                try {
                    str = String.format("/sdcard/%d.jpg", System.currentTimeMillis());
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
            // 카메라에서 찍을 수 있는 모든 사이즈를 가지고 와서 그중에 하나를 선택한다.
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size optimalSize;
            optimalSize = getOptimalPreviewSize(sizes, IMAGE_WIDTH, IMAGE_HEIGHT);
            parameters.setPictureSize(optimalSize.width, optimalSize.height);
            camera.setParameters(parameters);

            camera.startPreview();
        }
        catch (Exception e) {
        }
    }

    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.05;
        double targetRatio = (double) width / height;
        if (sizes == null) {
            return null;
        }
        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        Log.i("optimal size", ""+optimalSize.width+" x "+optimalSize.height);
        return optimalSize;
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
            // 카메라에서 찍을 수 있는 모든 사이즈를 가지고 와서 그중에 하나를 선택한다.
            Camera.Parameters parameters = camera.getParameters();
            List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
            Camera.Size optimalSize;
            optimalSize = getOptimalPreviewSize(sizes, mWidth, mHeight);
            parameters.setPictureSize(optimalSize.width, optimalSize.height);
            camera.setParameters(parameters);

            camera.startPreview();
        }

        catch (Exception e) {
            System.err.println(e);
            return;
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        refreshCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.stopPreview();
        camera.release();
        camera = null;
    }
}