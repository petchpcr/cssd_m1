package com.poseintelligence.cssdm1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_WRITE_STORAGE = 112;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toSplashScreen();
    }

    public void toSplashScreen(){
        Intent it = new Intent(MainActivity.this, SplashScreen.class);
        startActivity(it);
        finish();

    }

}