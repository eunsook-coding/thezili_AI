package com.thezili.ohmybaby;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class IntroViewActivity extends Activity {
    private static final String TAG = "IntroActivity";

    private boolean mIsFinish = false;
    private Context mContext = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        setContentView(R.layout.intro_view);
        mContext = this;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                if(mContext == null || mIsFinish) {
                    Log.w(TAG, "is destroyed");
                    return;
                }

                Log.d(TAG, "onCreate()");
                Intent intent = new Intent(mContext, TakeCameraActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }
    
    @Override
    public void finish() {
    	Log.d(TAG, "finish");
    	mIsFinish = true;
    	super.finish();
    }

}
