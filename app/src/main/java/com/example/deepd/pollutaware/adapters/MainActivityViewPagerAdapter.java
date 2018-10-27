/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.adapters;

import com.example.deepd.pollutaware.fragments.FilterFragment;
import com.example.deepd.pollutaware.fragments.InfoFragment;
import com.example.deepd.pollutaware.fragments.MapsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainActivityViewPagerAdapter extends FragmentStatePagerAdapter {

    public MainActivityViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MapsFragment();
            case 1:
                return new FilterFragment();
            case 2:
                return new InfoFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Maps";
            case 1:
                return "Filter";
            case 2:
                return "Info";
            default:
                return null;
        }
    }
}
