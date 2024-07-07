package com.arvind.emipro;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the main activity directly
        Intent intent = new Intent(SplashActivity.this, EmiActivity.class);
        startActivity(intent);
        finish();
    }
}
