package com.poseintelligence.cssdm1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;

public class RemarkActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    // Session Variable
    private String getUrl="";
    private String userid = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remark);
        getSupportActionBar().hide();

    }

    @Override
    public void onBackPressed() {

    }
}