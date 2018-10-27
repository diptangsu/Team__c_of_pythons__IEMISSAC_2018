/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.example.deepd.pollutaware.LoginActivity;
import com.example.deepd.pollutaware.Managers.ConstantManagers;
import com.example.deepd.pollutaware.R;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isOnline()) {
//                    if (hasProfile()) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
//                    } else {
//                        Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
                } else {
                    //do
                    Toast.makeText(SplashActivity.this, "is not online", Toast.LENGTH_SHORT).show();
                }
            }
        };
        handler.postDelayed(runnable, 400);
    }

    private boolean hasProfile() {
        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPref_file_name), Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(ConstantManagers.SHARED_PREF_USERNAME, null);
        return username != null;
    }

    private boolean isOnline() {
        @SuppressLint("ServiceCast") ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            NetworkInfo networkInfo = null;
            if (connectivityManager != null) {
                networkInfo = connectivityManager.getActiveNetworkInfo();
            } else {
                Toast.makeText(this, "Connectivity Manager is null", Toast.LENGTH_SHORT).show();
            }
            return networkInfo != null && networkInfo.isConnectedOrConnecting();
        } catch (NullPointerException exception) {
            Log.i(TAG, "Exception: " + exception.getMessage());
            return false;
        }
    }
}
