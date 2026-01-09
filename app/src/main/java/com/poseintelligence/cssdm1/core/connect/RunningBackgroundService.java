package com.poseintelligence.cssdm1.core.connect;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Login;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.WaitConnectDialog;

import java.util.HashMap;

public class RunningBackgroundService extends Service {

    public static WaitConnectDialog mActivity;

    Handler handler  = new Handler();
    Runnable runnable;

    public static boolean is_login = false;

    private IBinder mBinder = new MyBinder();

    int lost_connect_time = 0;
    boolean is_show_wait_connecting=false;

    @Override
    public void onCreate()
    {
        Log_d("tog_ccs","onCreate" );
        ((CssdProject) getApplication()).counting_token = 1;
        looper();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop_checking_connect();
        Log_d("tog_ccs","onDestroy" );
    }

    private void check_connecting(){
        Log_d("tog_wait_con","on show_wait_connecting");
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
//        Log_d("tog_wait_netInfo","netInfo = "+netInfo);
        if (netInfo == null){
            if(lost_connect_time>5){
                if(mActivity == null && !is_show_wait_connecting){
                    is_show_wait_connecting = true;
                    Log_d("tog_wait_con","mActivity = null");
                    Intent x = new Intent(this, WaitConnectDialog.class);
                    x.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(x);
                }
            }
            lost_connect_time++;

        }else{
            lost_connect_time=0;
            is_show_wait_connecting = false;
            if(mActivity != null){
                mActivity.fin();
                mActivity = null;
            }

            Log_d("tog_ccs","isNonActiveTime = "+((CssdProject) getApplication()).isNonActiveTime);
            if(((CssdProject) getApplication()).getST_LoginTimeOut()>=0 && is_login){
                if(((CssdProject) getApplication()).isNonActiveTime>=((CssdProject) getApplication()).getST_LoginTimeOut()){

                    Intent intent = new Intent(this,Login.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }

            Log_d("tog_http_F","isExpired_token= "+((CssdProject) getApplication()).isExpired_token());
            if(((CssdProject) getApplication()).isExpired_token()){
                ((CssdProject) getApplication()).setExpired_token(false);
                Intent intent = new Intent(this,Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }

//        Log_d("tog_http_F","expired_token check");
//        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
//        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
//        Log_d("topActivity", "CURRENT Activity ::" + taskInfo.get(0).topActivity.getClassName());

    }

    private void looper(){
        runnable = new Runnable() {
            @Override
            public void run() {
                check_connecting();
                handler.postDelayed(runnable, 1000);
//                handler.removeCallbacks(runnable);
                if(((CssdProject) getApplication()).getST_LoginTimeOut()>=0){
                    ((CssdProject) getApplication()).isNonActiveTime++;
                }

                if(is_login){
                    ((CssdProject) getApplication()).counting_token++;
                }else{
                    ((CssdProject) getApplication()).counting_token = 1;
                }

                Log_d("tog_http_t","counting_token= "+((CssdProject) getApplication()).counting_token);
                Log_d("tog_http_t","counting_token= "+((CssdProject) getApplication()).time_noti_token_expire);
//
                if(((CssdProject) getApplication()).counting_token==((CssdProject) getApplication()).time_noti_token_expire){
                    if(((CssdProject) getApplication()).repeat_noti_token_expire_time < ((CssdProject) getApplication()).repeat_noti_token_expire){
                        sendNotification(((CssdProject) getApplication()).repeat_noti_token_expire_time+1);
                        ((CssdProject) getApplication()).repeat_noti_token_expire_time++;
                        ((CssdProject) getApplication()).time_noti_token_expire = ((CssdProject) getApplication()).time_noti_token_expire + ((CssdProject) getApplication()).try_noti_token_expire;
                    }
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
        public RunningBackgroundService getService() {
            Log_d("tog_ccs","MyBinder" );

            return RunningBackgroundService.this;
        }
    }


    private void sendNotification(int notificationId) {
        long timestampMillis = System.currentTimeMillis();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CssdProject.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_r_grey) // Use a proper icon
                .setContentTitle("Token close expire")
                .setContentText("โทเคนของคุณกำลังจะหมดอายุ โปรดเข้าสู่ระบบอีกครั้ง")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setAutoCancel(true); // Dismisses the notification when tapped

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            // A unique ID for the notification (allows updates or dismissal later)
            manager.notify(notificationId, builder.build());
        }

        Toast.makeText(this, "โทเคนของคุณกำลังจะหมดอายุ\nโปรดเข้าสู่ระบบอีกครั้ง", Toast.LENGTH_LONG).show();

        HashMap<String, String> data = new HashMap<String, String>();
        data.put("UserID", CssdProject.getPm().getUserid()+"");
        data.put("Descriptions", "noti "+notificationId);
        data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

        HTTPConnect httpConnect = new HTTPConnect();
        try {
            httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_noti_log.php", data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void Log_d(String tag, String mass){
//        Log.d(tag, mass);
    }

}
