/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.example.deepd.pollutaware.Managers.ConstantManagers;
import com.example.deepd.pollutaware.R;
import com.example.deepd.pollutaware.activities.AboutUsActivity;
import com.example.deepd.pollutaware.activities.MainActivity;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import androidx.appcompat.widget.Toolbar;

public class DrawerUtils {

    public static void getDrawer(final Activity activity, Toolbar toolbar) {
        PrimaryDrawerItem aboutItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.title_dashboard);
        PrimaryDrawerItem settingsItem = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.title_about);

        AccountHeader header = new AccountHeaderBuilder()
                .withActivity(activity)
                .withHeaderBackground(R.drawable.gradient_background)
                .addProfiles(new ProfileDrawerItem().withName(ConstantManagers.userName).withIcon(R.drawable.ic_account))
                .build();

        Drawer drawer = new DrawerBuilder()
                .withActivity(activity)
                .withToolbar(toolbar)
                .withAccountHeader(header)
                .withActionBarDrawerToggle(true)
                .withActionBarDrawerToggleAnimated(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(aboutItem, settingsItem)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 1) {
                                intent = new Intent(activity, MainActivity.class);
                            } else if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(activity, AboutUsActivity.class);
                            }

                            if (intent != null) {
                                activity.startActivity(intent);
                                activity.finish();
                            }
                        }
                        return false;
                    }
                })
                .build();
    }
}
