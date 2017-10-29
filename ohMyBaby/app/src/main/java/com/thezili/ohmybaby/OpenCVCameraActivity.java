package com.thezili.ohmybaby;

/**
 * Created by KangEunSook on 2017-10-23.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OpenCVCameraActivity extends Activity implements CvCameraViewListener2, OnTouchListener {
    private static final String TAG = "OpenCVCameraActivity";

    private OpenCVCameraView mOpenCvCameraView;
    private List<Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private ProgressBar progressBar;
    private TextView txtPercentage;
    long totalSize = 0;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(OpenCVCameraActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public OpenCVCameraActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.opencv_view);

        mOpenCvCameraView = (OpenCVCameraView) findViewById(R.id.opencv_camera_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG,"onTouch event");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

        String currentDateandTime = sdf.format(new Date());

        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/ohMyBaby";
        File file = new File(filePath);
        file.mkdir();

        filePath += "/ohmybaby" + currentDateandTime + ".jpg";

        file = new File(filePath);
        try {
            file.createNewFile();
            Toast.makeText(this, "Save Success", Toast.LENGTH_SHORT).show();

        } catch (IOException ie) {
            Toast.makeText(this, "Save Fail", Toast.LENGTH_SHORT).show();
        }

        mOpenCvCameraView.takePicture(filePath);
        Toast.makeText(this, filePath + " saved", Toast.LENGTH_SHORT).show();

        String path = file.getPath();
        boolean uploadResult = sendUpload(path);
        if(!uploadResult) {
            Log.e(TAG, "Camera TakePicture(), Save filePath is NULL!!!");
        }

        Log.d(TAG, "Save File Path = " + path);

        return false;
    }

    private boolean sendUpload(String filePath) {
        if(filePath == null ||filePath.equals("") || filePath.isEmpty()) {
            Log.e(TAG, "sendUpload(), filePath is NULL!!!");
            return false;
        }
        Log.e(TAG, "sendUpload(), filePath = " + filePath);

        Intent uploadIntent = new Intent(OpenCVCameraActivity.this, UploadActivity.class);
        uploadIntent.putExtra("filePath", filePath);
        startActivity(uploadIntent);
        //new UploadFileToServer().execute(filePath);
//        new UploadFileToServer().uploadFile(filePath);

        return true;
    }

    private class UploadFileToServer extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return uploadFile(params[0]);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating progress bar value
            progressBar.setProgress(progress[1]);

            // updating percentage value
            txtPercentage.setText(String.valueOf(progress[1]) + "%");
        }

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
//            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile(String filePath) {
            String responseString = null;

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://182.221.33.99:9000/upload_image");

            //HttpResponse response;
            try {
                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
                        new AndroidMultiPartEntity.ProgressListener() {
                            @Override
                            public void transferred(long num) {
                                publishProgress((int) ((num / (float) totalSize) * 100));
                            }
                        });

                File sourceFile = new File(filePath);

                // Adding file data to http body
                entity.addPart("image", new FileBody(sourceFile));

                // Extra parameters if you want to pass to server
                /*entity.addPart("website",
                        new StringBody("www.thezili.com"));
                entity.addPart("email", new StringBody("thezilie.changwook@gmail.com"));*/

                totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                //response = httpclient.execute(request);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    //Log.d("Response of GET request", response.toString());
                    //Log.d("Response of POST request", responseString);
                } else {
                    responseString = "Error occurred! Http Status Code: "
                            + statusCode;
                }
            } catch (ClientProtocolException e) {
                responseString = e.toString();
            } catch (IOException e) {
                responseString = e.toString();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
//            showAlert(result);
            Toast.makeText(OpenCVCameraActivity.this, result, Toast.LENGTH_LONG).show();

            super.onPostExecute(result);
        }

    }
}
