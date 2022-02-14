package com.poseintelligence.cssdm1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;

import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.core.connect.CheckConnectionService;

public class WaitConnectDialog extends Activity {
    String Connecting = "กำลังทำการเชื่อมต่อ";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_connect_dialog);

        CheckConnectionService.mActivity = WaitConnectDialog.this;

        ProgressDialog wait_dialog = new ProgressDialog(WaitConnectDialog.this);
        wait_dialog.setCancelable(false);
        wait_dialog.setMessage(Connecting);
        wait_dialog.show();
    }

    public void fin(){
        this.finish();
    }
}