package com.poseintelligence.cssdm1.core.connect;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Login;
import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.WaitConnectDialog;

public class CheckConnectionService extends Service {

    public static WaitConnectDialog mActivity;

    Handler handler  = new Handler();
    Runnable runnable;


    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate()
    {
        Log.d("tog_ccs","onCreate" );
        check_connecting();
    }

    public void show_wait_connecting(){
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            if(mActivity == null){
                Intent x = new Intent(this, WaitConnectDialog.class);
                startActivity(x);
            }
        }else{
            if(mActivity != null){
                mActivity.fin();
                mActivity = null;
            }
        }
    }

    public void check_connecting(){
        runnable = new Runnable() {
            @Override
            public void run() {
                show_wait_connecting();

                handler.postDelayed(runnable, 1000);
//                handler.removeCallbacks(runnable);
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MyBinder extends Binder {
        public CheckConnectionService getService() {
            Log.d("tog_ccs","MyBinder" );

            return CheckConnectionService.this;
        }
    }
}
