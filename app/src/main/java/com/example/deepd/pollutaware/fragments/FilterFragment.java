/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class FilterFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private Spinner countrySpinner;
    private Spinner citySpinner;
    private Spinner areaSpinner;
    private TextView data;

    private final String URL_COUNTRIES = "https://api.openaq.org/v1/countries";
    private final String URL_CITIES = "https://api.openaq.org/v1/cities?country=";
    private final String URL_AREA = "https://api.openaq.org/v1/locations?";
    private final String URL_MEASUREMENTS = "https://api.openaq.org/v1/measurements?";
    private final String URL_PARAMETERS = "https://api.openaq.org/v1/parameters";

    private String citySelected, countrySelected, areaSelected;
    private final String TAG = "FilterFragment";

    private LineChart lineChart;

    private List<String> countryNames;
    private List<String> cityNames;
    private List<String> areaNames;
    private Map<String, String> countryCodes;
    private Map<String, String> parameters;
    private Map<String, ArrayList<Double>> pollutants;
    private List<Integer> colors;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container, false);

        countryNames = new ArrayList<>();
        cityNames = new ArrayList<>();
        areaNames = new ArrayList<>();
        countryCodes = new HashMap<>();
        parameters = new HashMap<>();
        pollutants = new HashMap<>();
        colors = new ArrayList<>();

        countrySpinner = view.findViewById(R.id.spinner_country);
        citySpinner = view.findViewById(R.id.spinner_city);
        areaSpinner = view.findViewById(R.id.spinner_area);
        data = view.findViewById(R.id.data);
        lineChart = view.findViewById(R.id.lineChart);


        populateCountrySpinner(URL_COUNTRIES);

        setItemSelectedListeners();
        loadParameters();
        initializeParameters();
        setColors();

        return view;
    }

    private void setColors() {
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.BLUE);
        colors.add(Color.CYAN);
        colors.add(Color.BLACK);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);
        colors.add(Color.LTGRAY);
    }

    private void loadParameters() {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PARAMETERS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectI = jsonArray.getJSONObject(i);
                        String id = jsonObjectI.getString("id");
                        String description = jsonObjectI.getString("description");

                        parameters.put(id, description);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Could not get Parameters", Toast.LENGTH_SHORT).show();
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
    }

    private void initializeParameters() {
        for (String parameter : parameters.keySet()) {
            pollutants.put(parameter, new ArrayList<Double>());
        }
    }

    private void setItemSelectedListeners() {
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String country = countrySpinner.getItemAtPosition(countrySpinner.getSelectedItemPosition()).toString();
                if (country.compareTo("--Select Country--") != 0) {
                    cityNames.clear();
                    areaNames.clear();

                    citySpinner.clearFocus();
                    areaSpinner.clearFocus();

                    String code = countryCodes.get(country);
                    countrySelected = code;

                    populateCitySpinner(URLify(URL_CITIES + code));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String city = citySpinner.getItemAtPosition(citySpinner.getSelectedItemPosition()).toString();
                if (city.compareTo("--Select City--") != 0) {
                    areaNames.clear();
                    areaSpinner.clearFocus();

                    citySelected = city;
                    String url = URL_AREA + "country=" + countrySelected + "&city=" + city;
                    Log.e(TAG, url);

                    populateAreaSpinner(URLify(url));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });

        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String area = areaSpinner.getItemAtPosition(areaSpinner.getSelectedItemPosition()).toString();
                if (area.compareTo("--Select Area--") != 0) {
                    Toast.makeText(getContext(), area, Toast.LENGTH_SHORT).show();
                    areaSelected = area;

                    initializeParameters();
                    display();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                // DO Nothing here
            }
        });
    }

    private void display() {
        String url = URL_MEASUREMENTS + "country=" + countrySelected + "&city=" + citySelected + "&location=" + areaSelected;
        url = URLify(url);
        Log.e(TAG, url);

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectI = jsonArray.getJSONObject(i);
                        String parameter, value;
                        parameter = jsonObjectI.getString("parameter");
                        value = jsonObjectI.getString("value");

                        ArrayList<Double> al = pollutants.get(parameter);
                        if (al != null)
                            al.add(Double.parseDouble(value));

                    }

                    StringBuilder sb = new StringBuilder();
                    List<ILineDataSet> dataSets = new ArrayList<>();
                    int i = 0;
                    for (String pollutant : pollutants.keySet()) {
                        ArrayList<Double> values = pollutants.get(pollutant);
                        List<Entry> entries = new ArrayList<>();

                        if (values != null) {
                            int k = 0;
                            for (Double value : values) {
                                entries.add(new Entry(new Float(value), k++));
                                sb.append(value);
                            }
                        }
                        LineDataSet pollutantSet = new LineDataSet(entries, pollutant);
                        pollutantSet.setColor(colors.get(i));
                        pollutantSet.setCircleColor(colors.get(i++));

                        dataSets.add(pollutantSet);
                    }
                    data.setText(sb);
                    lineChart.animateX(3000);
                    lineChart.setData(new LineData(dataSets));
                    lineChart.invalidate();
//                    ArrayList<LineDataSet> lines = new ArrayList<LineDataSet> ();
//                    String[] xAxis = new String[] {"1", "2", "3", "4", "5","6"};


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Could not get Areas", Toast.LENGTH_SHORT).show();
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
    }

    private String URLify(String url) {
        return url.replace(" ", "%20");
    }

    private void populateAreaSpinner(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    areaNames.add("--Select Area--");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectI = jsonArray.getJSONObject(i);
                        if (jsonObjectI.has("location")) {
                            String location = jsonObjectI.getString("location");
                            areaNames.add(location.trim());
                        }
                    }
                    Collections.sort(areaNames);
                    areaSpinner.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            areaNames));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Could not get Areas", Toast.LENGTH_SHORT).show();
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
    }

    private void populateCitySpinner(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    cityNames.add("--Select City--");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObjectI = jsonArray.getJSONObject(i);
                        if (jsonObjectI.has("city")) {
                            String city = jsonObjectI.getString("city");
                            cityNames.add(city.trim());
                        }
                    }
                    Collections.sort(cityNames);
                    citySpinner.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            cityNames));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Could not get Cities", Toast.LENGTH_SHORT).show();
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
    }

    private void populateCountrySpinner(String url) {
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getContext()));
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    countryNames.add("--Select Country--");
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                        if (jsonObject1.has("name")) {
                            String country = jsonObject1.getString("name");
                            String countryCode = jsonObject1.getString("code");
                            countryCodes.put(country, countryCode);
                            countryNames.add(country.trim());
                        }
                    }
                    countrySpinner.setAdapter(new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            countryNames));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Could not get Countries", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onRefresh() {
        populateCountrySpinner(URL_COUNTRIES);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
