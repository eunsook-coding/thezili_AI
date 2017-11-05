package com.thezili.ohmybaby;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by cwjun on 17. 11. 5.
 */

public class FinalResultActivity extends Activity {

    private static final String TAG = "FinalResultActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.final_result_activity);

        Log.d(TAG, ">> onCreate()");


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
