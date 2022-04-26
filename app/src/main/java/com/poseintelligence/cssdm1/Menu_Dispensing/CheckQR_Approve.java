package com.poseintelligence.cssdm1.Menu_Dispensing;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CheckQR_Approve extends Activity {

    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private Timer timer;

    EditText etxt_qr;
    Button bt_cancel;

    String DocNo;
    String xSel;
    String remark;
    String p_SterileMachineID;
    String MacNo;
    String ImportID;
    String ID;
    String type;
    String ItemStockID;
    String check = "false";

    Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_qrapprove);
        Intent intent = getIntent();
        DocNo = intent.getStringExtra("DocNo");
        xSel = intent.getStringExtra("xSel");
        remark = intent.getStringExtra("remark");
        p_SterileMachineID = intent.getStringExtra("p_SterileMachineID");
        MacNo = intent.getStringExtra("MacNo");
        ImportID = intent.getStringExtra("ImportID");
        ID = intent.getStringExtra("ID");
        ItemStockID = intent.getStringExtra("ItemStockID");
        type = intent.getStringExtra("type");

        Log.d("BANKTEST",remark+"");

        init();
    }

    public void init(){
        etxt_qr = (EditText) findViewById(R.id.etxt_qr);
        if(!etxt_qr.hasFocus()){
            etxt_qr.requestFocus();
        }
        bt_cancel = (Button) findViewById(R.id.bt_cancel);

        etxt_qr.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            if (xSel.equals("pay") || xSel.equals("payrow")){
                                if (remark.equals("closepayout")){
                                    Checkuser(etxt_qr.getText().toString(),DocNo,xSel);
                                }else {
                                    CheckuserPay(etxt_qr.getText().toString(),xSel,remark,DocNo);
                                }
                            }else if (xSel.equals("sterile")){
                                CheckuserSterile(etxt_qr.getText().toString(),xSel,remark,DocNo);
                            }else {
                                Checkuser(etxt_qr.getText().toString(),DocNo,xSel);
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        etxt_qr.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Checkuser("EM00001", DocNo, xSel);
                return true;
            }
        });

        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void startUserSession() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    public void Checkuser(String qr_code, String docno, String xsel) {

        class Checkuser extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("check").equals("true")){
                            check = c.getString("check");
                            setResult(1035, intent);
                            intent.putExtra("RETURN_DATA",check);
                            intent.putExtra("RETURN_xsel",xSel);
                            intent.putExtra("RETURN_DocNo",DocNo);
                            intent.putExtra("RETURN_MacNo",MacNo);
                            intent.putExtra("RETURN_Type",type);
                            intent.putExtra("RETURN_ID",c.getString("ID"));
                            finish();
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckQR_Approve.this);
                            builder.setCancelable(true);
                            builder.setTitle("แจ้งเตือน !!");
                            builder.setMessage("ไม่พบรหัสผู้ใช้");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            startUserSession();
                            etxt_qr.setText("");
                            etxt_qr.requestFocus();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("qr_code",params[0]);
                data.put("docno",params[1]);
                data.put("xsel",params[2]);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() +"chk_qr/check_qr.php",data);

                Log.d("Checkuser",data+"");
                Log.d("Checkuser",result+"");

                return  result;
            }
        }

        Checkuser ru = new Checkuser();
        ru.execute( qr_code,docno,xsel );
    }

    public void CheckuserPay(String qr_code, String xsel, String remark, String DocNo) {

        class CheckuserPay extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("check").equals("true")){

                            if (c.getString("IsAdmin").equals("1")){

                                if (xSel.equals("payrow")){
                                    setResult(1155, intent);
                                    intent.putExtra("RETURN_DATA",check);
                                    intent.putExtra("RETURN_xsel",xSel);
                                    intent.putExtra("RETURN_DocNo",DocNo);
                                    intent.putExtra("RETURN_MacNo",MacNo);
                                    intent.putExtra("RETURN_ID",c.getString("ID"));
                                    finish();
                                }else {
                                    setResult(1050, intent);
                                    intent.putExtra("RETURN_DATA",check);
                                    intent.putExtra("RETURN_xsel",xSel);
                                    intent.putExtra("RETURN_DocNo",DocNo);
                                    intent.putExtra("RETURN_MacNo",MacNo);
                                    intent.putExtra("RETURN_ID",c.getString("ID"));
                                    finish();
                                }

                            }else {
                                Toast.makeText(CheckQR_Approve.this, "ไม่ผ่าน", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckQR_Approve.this);
                            builder.setCancelable(true);
                            builder.setTitle("แจ้งเตือน !!");
                            builder.setMessage("ไม่พบรหัสผู้ใช้");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            startUserSession();
                            etxt_qr.setText("");
                            etxt_qr.requestFocus();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("qr_code",params[0]);
                data.put("xsel",params[1]);
                data.put("remark",params[2]);
                data.put("DocNo",params[3]);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() +"chk_qr/check_qr.php",data);

                return  result;
            }
        }

        CheckuserPay ru = new CheckuserPay();
        ru.execute(qr_code, xsel, remark, DocNo);
    }

    public void CheckuserSterile(String qr_code, String xsel, String remark, String DocNo) {

        class CheckuserSterile extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("check").equals("true")){

                            if (c.getString("IsAdmin").equals("1")){
                                if (remark.equals("ImportAll")){
                                    setResult(1060, intent);
                                    intent.putExtra("RETURN_DATA",check);
                                    intent.putExtra("RETURN_xsel",xSel);
                                    intent.putExtra("RETURN_DocNo",DocNo);
                                    intent.putExtra("RETURN_MacNo",MacNo);
                                    finish();
                                }else if (remark.equals("ExportAll")){
                                    setResult(1061, intent);
                                    intent.putExtra("RETURN_DATA",check);
                                    intent.putExtra("RETURN_xsel",xSel);
                                    intent.putExtra("RETURN_DocNo",DocNo);
                                    intent.putExtra("RETURN_MacNo",MacNo);
                                    finish();
                                }else {
                                    setResult(1062, intent);
                                    intent.putExtra("RETURN_DATA",check);
                                    intent.putExtra("RETURN_ImportID",ImportID);
                                    intent.putExtra("RETURN_ID",ID);
                                    intent.putExtra("RETURN_ItemStockID",ItemStockID);
                                    finish();
                                }
                            }else {
                                Toast.makeText(CheckQR_Approve.this, "รหัส Admin ไม่ถูกต้อง !!", Toast.LENGTH_SHORT).show();
                                finish();
                            }

                        }else{

                            AlertDialog.Builder builder = new AlertDialog.Builder(CheckQR_Approve.this);
                            builder.setCancelable(true);
                            builder.setTitle("แจ้งเตือน !!");
                            builder.setMessage("ไม่พบรหัสผู้ใช้");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                            startUserSession();
                            etxt_qr.setText("");
                            etxt_qr.requestFocus();

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("qr_code",params[0]);
                data.put("xsel",params[1]);
                data.put("remark",params[2]);
                data.put("DocNo",params[3]);
                data.put("p_SterileMachineID",p_SterileMachineID);
                data.put("ImportID",ImportID);
                data.put("ID",ID);
                data.put("ItemStockID",ItemStockID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() +"chk_qr/check_qr.php",data);

                return  result;
            }
        }

        CheckuserSterile ru = new CheckuserSterile();
        ru.execute(qr_code, xsel, remark, DocNo);
    }
}
