/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.deepd.pollutaware.Managers.ConstantManagers;
import com.example.deepd.pollutaware.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    private int LOCATION_REQUEST_CODE = 1;

    private final String TAG = "SplashScreenActivity";

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String URL_CITY_LOCATIONS = "https://api.openaq.org/v1/cities?country=IN";
    private static final String URL_LOCATIONS_PER_CITY = "https://api.openaq.org/v1/locations?city=";

    private Set<String> locations;
    private Set<String> storedLocations;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            requestLocationPermission();
        }
    }

    private void locations() {
        sharedPreferences = getSharedPreferences(ConstantManagers.MY_PREFERENCES, Context.MODE_PRIVATE);
        locations = new HashSet<>();

//        fetchAllLocations();
        storedLocations = sharedPreferences.getStringSet(ConstantManagers.MY_PREFERENCES, null);
//        if (storedLocations == null)
            fetchAllLocations();
    }

    private void fetchAllLocations() {
        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_CITY_LOCATIONS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");

                    for(int i = 0; i < results.length(); i++) {
                        String city = results.getJSONObject(i).getString("city");
//                        Log.e("CITY", city);
                        fetchAllLocationsPerCity(city);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "Could not get Parameters", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "error listener", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void fetchAllLocationsPerCity(String city) {
        RequestQueue requestQueue = Volley.newRequestQueue(SplashActivity.this);
        String url = URLify(URL_LOCATIONS_PER_CITY + city);
//        Log.e("URL: ", url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray results = jsonObject.getJSONArray("results");

                    for (int i = 0; i < results.length(); i++) {
                        JSONObject jsonObjectI = results.getJSONObject(i);
                        if (jsonObjectI.has("coordinates")) {
                            JSONObject coordinates = jsonObjectI.getJSONObject("coordinates");
                            String lat, lon;
                            if (coordinates.has("latitude") && coordinates.has("longitude")) {
                                lat = coordinates.getString("latitude");
                                lon = coordinates.getString("longitude");
                                String location = lat + "," + lon;

                                locations.add(location);
                            }
                        }
                    }

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putStringSet(ConstantManagers.MY_PREFERENCES, locations);
                    editor.apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SplashActivity.this, "Could not get Parameters", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SplashActivity.this, "error listener", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Required")
                    .setMessage("Location Permission is needed to get data from your current location")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(SplashActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            dialogInterface.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        } else {
            Toast.makeText(this, "App Requires Location Permission to run", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    //    private boolean hasProfile() {
//        SharedPreferences sharedPreferences = getSharedPreferences(String.valueOf(R.string.sharedPref_file_name), Context.MODE_PRIVATE);
//        String username = sharedPreferences.getString(ConstantManagers.SHARED_PREF_USERNAME, null);
//        return username != null;
//    }

    private String URLify(String url) {
        return url.replace(" ", "%20");
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    ConstantManagers.longitude = location.getLongitude();
                    ConstantManagers.latitude = location.getLatitude();
                }
                changeActivity();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SplashActivity.this, "Location Fetching Failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changeActivity() {
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (isOnline()) {
                    locations();
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SplashActivity.this, "Turn on Internet", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        };
        handler.postDelayed(runnable, 1000);
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
