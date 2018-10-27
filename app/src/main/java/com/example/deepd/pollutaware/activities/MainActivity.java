/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.activities;

import android.os.Bundle;

import com.example.deepd.pollutaware.R;
import com.example.deepd.pollutaware.Utilities.DrawerUtils;
import com.example.deepd.pollutaware.adapters.MainActivityViewPagerAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.mainActivityToolbar)
    Toolbar toolbar;
    @BindView(R.id.mainActivityViewPager)
    ViewPager viewPager;

    private MainActivityViewPagerAdapter mainActivityViewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        toolbar.setTitle("Dashboard");
        setSupportActionBar(toolbar);

        DrawerUtils.getDrawer(this, toolbar);
        mainActivityViewPagerAdapter = new MainActivityViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mainActivityViewPagerAdapter);
    }
}
