package com.poseintelligence.cssdm1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

public class SplashScreen extends AppCompatActivity {
    final Handler handler_1 = new Handler();
    private Runnable runnable_1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
            return;
        }

        runnable_1 = new Runnable() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, 1000);
                        handler_1.postDelayed(this, 5000);
                    }else{
                        handler_1.removeCallbacks(runnable_1);
                        callActivity(Login.class, null, null);
                    }
                }

                Log.d("tog_permis","permission CAMERA = "+(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ));
                Log.d("tog_permis","permission WRITE_EXTERNAL_STORAGE = "+(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ));
            }
        };

        handler_1.postDelayed(runnable_1, 1000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            handler_1.removeCallbacks(runnable_1);
            callActivity(Login.class, null, null);
        }

        return true;
    }

    public void callActivity(Class c, String VersionName, String info){
        Intent it = new Intent(SplashScreen.this, c);
        it.putExtra("VersionName", VersionName);
        it.putExtra("info", info);
        startActivity(it);
        finish();
    }
}