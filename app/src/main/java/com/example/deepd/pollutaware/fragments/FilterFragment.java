/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.deepd.pollutaware.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FilterFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    SwipeRefreshLayout mSwipeRefreshLayout;
    Spinner countrySpinner;
    Spinner citySpinner;
    Spinner areaSpinner;
    String URL_COUNTRIES = "https://api.openaq.org/v1/countries";
    String URL_CITIES = "https://api.openaq.org/v1/cities?country=";
    ArrayList<String> countryName;
    ArrayList<String> cityName;
    //private OnFragmentInteractionListener mListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getActivity(), "onCreate", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_filter, container, false);
        countryName = new ArrayList<>();
        cityName = new ArrayList<>();
        countrySpinner = view.findViewById(R.id.spinner_country);
        citySpinner = view.findViewById(R.id.spinner_city);
        populateCountrySpinner(URL_COUNTRIES);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = countrySpinner.getItemAtPosition(countrySpinner.getSelectedItemPosition()).toString();
                Toast.makeText(getActivity(), country, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
        Toast.makeText(getActivity(), "View Created", Toast.LENGTH_SHORT).show();
        return view;
    }

    private void populateCountrySpinner(String url) {
        Toast.makeText(getActivity(), "Shuru", Toast.LENGTH_SHORT).show();
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int count = 0;
                try {
                    countryName.add("--Select Country--");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (jsonObject1.has("name")) {
                            String country = jsonObject1.getString("name");
                            countryName.add(country);
                        }
                    }
                    Toast.makeText(getContext(), "peyechi json", Toast.LENGTH_SHORT).show();

                    countrySpinner.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, countryName));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "error json a", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "error listener", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        });

        int socketTimeout = 30000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);

        Toast.makeText(getActivity(), "Shesh", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onRefresh() {
        populateCountrySpinner(URL_COUNTRIES);
        mSwipeRefreshLayout.setRefreshing(false);

    }

}
