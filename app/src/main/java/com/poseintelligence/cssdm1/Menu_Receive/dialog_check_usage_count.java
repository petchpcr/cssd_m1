package com.poseintelligence.cssdm1.Menu_Receive;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.ModelUsageCount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.List;

public class dialog_check_usage_count extends Dialog {

    String UsageCode;
    String DocNo;
    String B_ID;
    String sel;
    String Cnt;
    String page;

    String condition1;
    String condition2;
    String condition3;
    String condition4;
    String condition5;
    String condition6;

    Context xContext;
    String p_DB;
    String getxUrl;

    TextView index1,index2,index3,index4,index5,index6;
    TextView qty1,qty2,qty3,qty4,qty5,qty6;
    Button back;
    LinearLayout P1,P2,P3,P4,P5,P6;

    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private List<ModelUsageCount> Model_RQ = null;

    public dialog_check_usage_count( Context context,HashMap<String, String> xData) {
        super(context);
        setContentView(R.layout.activity_dialog_check_usage_count);
        this.
//        byIntent();
        initialize(xData);
    }

//    private void byIntent() {
//        // Argument
//        Intent intent = getIntent();
//        UsageCode = intent.getStringExtra("UsageCode");
//        DocNo = intent.getStringExtra("DocNo");
//        B_ID = intent.getStringExtra("B_ID");
//        sel = intent.getStringExtra("sel");
//        Cnt = intent.getStringExtra("cnt");
//        page = intent.getStringExtra("page");
//        condition1 = intent.getStringExtra("condition1");
//        condition2 = intent.getStringExtra("condition2");
//        condition3 = intent.getStringExtra("condition3");
//        condition4 = intent.getStringExtra("condition4");
//        condition5 = intent.getStringExtra("condition5");
//        condition6 = intent.getStringExtra("condition6");
//    }

    public void initialize(HashMap<String, String> xData) {

        UsageCode = xData.get("UsageCode");
        DocNo = xData.get("DocNo");
        B_ID = xData.get("B_ID");
        sel = xData.get("sel");
        Cnt = xData.get("cnt");
        page = xData.get("page");
        condition1 = xData.get("condition1");
        condition2 = xData.get("condition2");
        condition3 = xData.get("condition3");
        condition4 = xData.get("condition4");
        condition5 = xData.get("condition5");
        condition6 = xData.get("condition6");

        qty1 = (TextView) findViewById(R.id.qty1);
        qty2 = (TextView) findViewById(R.id.qty2);
        qty3 = (TextView) findViewById(R.id.qty3);
        qty4 = (TextView) findViewById(R.id.qty4);
        qty5 = (TextView) findViewById(R.id.qty5);
        qty6 = (TextView) findViewById(R.id.qty6);

        qty1.setPaintFlags(qty1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        qty2.setPaintFlags(qty2.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        qty3.setPaintFlags(qty3.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        qty4.setPaintFlags(qty4.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        qty5.setPaintFlags(qty5.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        qty5.setPaintFlags(qty6.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        back = (Button) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (page.equals("0")){
                    finish();
                }else {
                    SetIsstatus();
                }
            }
        });

        P1 = (LinearLayout) findViewById(R.id.P1);
        P2 = (LinearLayout) findViewById(R.id.P2);
        P3 = (LinearLayout) findViewById(R.id.P3);
        P4 = (LinearLayout) findViewById(R.id.P4);
        P5 = (LinearLayout) findViewById(R.id.P5);
        P6 = (LinearLayout) findViewById(R.id.P6);

        index1 = (TextView) findViewById(R.id.index1);
        index2 = (TextView) findViewById(R.id.index2);
        index3 = (TextView) findViewById(R.id.index3);
        index4 = (TextView) findViewById(R.id.index4);
        index5 = (TextView) findViewById(R.id.index5);
        index6 = (TextView) findViewById(R.id.index6);

        int index = 1;

        P1.setVisibility(View.GONE);
        P2.setVisibility(View.GONE);
        P3.setVisibility(View.GONE);
        P4.setVisibility(View.GONE);
        P5.setVisibility(View.GONE);
        P6.setVisibility(View.GONE);

        if (!condition1.equals("0")){
            P1.setVisibility(View.VISIBLE);
            index1.setText(index+".");
            index++;
            qty1.setText(condition1);
        }

        if (!condition6.equals("0")){
            P6.setVisibility(View.VISIBLE);
            index6.setText(index+".");
            index++;
            qty6.setText(condition6);
        }

        if (!condition2.equals("0")){
            P2.setVisibility(View.VISIBLE);
            index2.setText(index+".");
            index++;
            qty2.setText(condition2);
        }

        if (!condition3.equals("0")){
            P3.setVisibility(View.VISIBLE);
            index3.setText(index+".");
            index++;
            qty3.setText(condition3);
        }

        if (!condition4.equals("0")){
            P4.setVisibility(View.VISIBLE);
            index4.setText(index+".");
            index++;
            qty4.setText(condition4);
        }

        if (!condition5.equals("0")){
            P5.setVisibility(View.VISIBLE);
            index5.setText(index+".");
            index++;
            qty5.setText(condition5);
        }

    }

    public void SetIsstatus() {
        class SetIsstatus extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(xContext);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage("กำลังประมวลผล...");
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("finish").equals("true")){
                            finish();
                        }else {
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("DOC_NO", DocNo);
                data.put("p_DB", p_DB);

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(getxUrl + "cssd_set_status_ems.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }

                return result;
            }
            // =========================================================================================
        }
        SetIsstatus obj = new SetIsstatus();
        obj.execute();
    }

    public void finish(){
        this.dismiss();
    };
}


