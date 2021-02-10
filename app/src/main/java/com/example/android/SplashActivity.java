package com.example.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.example.android.Category.MainCategoryActivity;

/**
 * Created by Yasmin Jalal - 2020.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainCategoryActivity.class);
                startActivity(intent);
                finish();

            }
        }, 2500);
    }
}
