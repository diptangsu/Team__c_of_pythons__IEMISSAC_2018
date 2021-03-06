/*
 * Created by Sujoy Datta. Copyright (c) 2018. All rights reserved.
 *
 * To the person who is reading this..
 * When you finally understand how this works, please do explain it to me too at sujoydatta26@gmail.com
 * P.S.: In case you are planning to use this without mentioning me, you will be met with mean judgemental looks and sarcastic comments.
 */

package com.example.deepd.pollutaware.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.deepd.pollutaware.R;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.guestLogin)
    public void guestLogin() {
        Intent i = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.facebookLoginButton)
    void facebookLogin() {
        Toast.makeText(this, "Not Available currently", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.googleLoginButton)
    void googleLogin() {
        Toast.makeText(this, "Not Available currently", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.twitterLoginButton)
    void twitterLogin() {
        Toast.makeText(this, "Not Available currently", Toast.LENGTH_SHORT).show();
    }
}
