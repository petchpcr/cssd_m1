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
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.WaitConnectDialog;

import java.util.List;

public class CheckConnectionService extends Service {

    public static WaitConnectDialog mActivity;

    Handler handler  = new Handler();
    Runnable runnable;

    public static boolean is_login = false;

    private IBinder mBinder = new MyBinder();

    @Override
    public void onCreate()
    {
        Log.d("tog_ccs","onCreate" );
        check_connecting();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop_checking_connect();
        Log.d("tog_ccs","onDestroy" );
    }

    private void show_wait_connecting(){
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null){
            if(mActivity == null){
                Intent x = new Intent(this, WaitConnectDialog.class);
                x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(x);
            }
        }else{
            if(mActivity != null){
                mActivity.fin();
                mActivity = null;
            }

//            Log.d("tog_ccs","isNonActiveTime = "+((CssdProject) getApplication()).isNonActiveTime);
            if(((CssdProject) getApplication()).getST_LoginTimeOut()>=0 && is_login){
                if(((CssdProject) getApplication()).isNonActiveTime>=((CssdProject) getApplication()).getST_LoginTimeOut()){

                    Intent intent = new Intent(this,Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

        }

//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        Log.d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());

    }

    private void check_connecting(){
        runnable = new Runnable() {
            @Override
            public void run() {
                show_wait_connecting();

                handler.postDelayed(runnable, 1000);
//                handler.removeCallbacks(runnable);

                if(((CssdProject) getApplication()).getST_LoginTimeOut()>=0){
                    ((CssdProject) getApplication()).isNonActiveTime++;
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void stop_checking_connect(){
        handler.removeCallbacks(runnable);
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
