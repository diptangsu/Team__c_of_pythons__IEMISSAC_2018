/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;

import com.example.deepd.pollutaware.R;
import com.example.deepd.pollutaware.fragments.FilterFragment;
import com.example.deepd.pollutaware.fragments.InfoFragment;
import com.example.deepd.pollutaware.fragments.MapsFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MainActivityViewPagerAdapter extends FragmentStatePagerAdapter {

    Drawable drawable;
    String title;

    private Context context;
    private String[] tab_names = {"Maps", "Filter", "Info"};

    public MainActivityViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
                drawable = context.getResources().getDrawable(R.mipmap.ic_maps);
                title = tab_names[position];
                break;
            case 1:
                drawable = context.getResources().getDrawable(R.mipmap.ic_filter);
                title = tab_names[position];
            case 2:
                drawable = context.getResources().getDrawable(R.mipmap.ic_info);
                title = tab_names[position];
                break;
            default:
                break;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" " + title);
        try {
            drawable.setBounds(5, 5, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable, DynamicDrawableSpan.ALIGN_BASELINE);
            spannableStringBuilder.setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return spannableStringBuilder;
    }
}
