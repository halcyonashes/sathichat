package com.yohbmeetup.yohb.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.yohbmeetup.yohb.utils.Helper;
import com.yohbmeetup.yohb.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final Helper helper = new Helper(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, helper.getLoggedInUser() != null ? MainActivity.class : SignInActivity.class));
                finish();
            }
        }, 1500);
    }
}
