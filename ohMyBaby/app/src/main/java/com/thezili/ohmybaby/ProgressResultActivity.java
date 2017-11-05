package com.thezili.ohmybaby;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by cwjun on 17. 11. 5.
 */

public class ProgressResultActivity extends Activity {

    private static final String TAG = "ProgressResultActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.progress_result_activity);

        Log.d(TAG, ">> onCreate()");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d(TAG, ">> onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG, ">> onDestroy()");
    }
}
