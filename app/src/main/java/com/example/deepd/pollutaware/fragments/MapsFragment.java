/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.deepd.pollutaware.Managers.ConstantManagers;
import com.example.deepd.pollutaware.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private View view;
    private GoogleMap googleMap;
    private MapView mapView;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(view.getContext());
        this.googleMap = googleMap;
        this.googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.googleMap.addMarker(new MarkerOptions().position(new LatLng(ConstantManagers.latitude, ConstantManagers.longitude)));

        CameraPosition cameraPosition = CameraPosition.builder().target(new LatLng(ConstantManagers.latitude, ConstantManagers.longitude))
                .zoom(10f).bearing(0).tilt(45).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        plotPoints();
    }

    private void plotPoints() {
        sharedPreferences = getContext().getSharedPreferences(ConstantManagers.MY_PREFERENCES, Context.MODE_PRIVATE);
        Set<String> storedLocations = sharedPreferences.getStringSet(ConstantManagers.MY_PREFERENCES, null);

        if (storedLocations != null) {
            for(String location : storedLocations) {
                String loc[] = location.split(",");
                Double lat = Double.parseDouble(loc[0]), lon = Double.parseDouble(loc[1]);
                Log.e("LATLON", "" + lat + " " + lon);

                String dataString = "Aqi: " + (int) fetchAqi();
                String locationString = loc[2];
                BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin);
                this.googleMap.addMarker(new MarkerOptions().icon(icon).position(new LatLng(lat, lon))
                        .title(locationString)
                        .snippet(dataString)).showInfoWindow();
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = view.findViewById(R.id.map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    private double fetchAqi() {
        double max = 450;
        double min = 85;
        return Math.round((Math.random() * ((max - min) + 1)) + min);
    }

    private String getAddress(double lat, double lng) {
        String name = " ";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            name = obj.getLocality();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Location names", "Location: " + name);
        return name;
    }
}
