package com.poseintelligence.cssdm1.RecordTestMenu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;


public class ATPDocnoActivity extends AppCompatActivity {

    WebView webView;
    String DocNo;
    String WashMachineID;
    String WashRoundNumber;
    String SterileMachineID;
    String SterileRoundNumber;
    String TestProgramName;
    String IsActive;
    String ID = "";
    String EmpCode;
    String xSel1 = "";
    String Page = "";
    String checkpass1 = "";
//    String DocNoSave = "";
    CheckBox testyes;
    CheckBox testno;
    EditText testremark;
    EditText remark;
    Button save_cancal;
    Button save_next;
    private String B_ID = null;
    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    SharedPreferences mPrefs1;
    String NetWorkApp = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atpdocno);

        byIntent();

        mPrefs1 = getSharedPreferences("label", 0);

//        ShowDoc();

        init();

    }

    public void init() {

        testremark = ( EditText ) findViewById(R.id.testremark);

        testyes = ( CheckBox ) findViewById(R.id.testyes);
        testno = ( CheckBox ) findViewById(R.id.testno);

        testyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (testyes.isChecked()) {
                    checkpass1 = "1";
                    testyes.setChecked(true);
                    testno.setChecked(false);
                }
            }
        });

        testno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (testno.isChecked()) {
                    checkpass1 = "0";
                    testyes.setChecked(false);
                    testno.setChecked(true);
                }
            }
        });

        remark = ( EditText ) findViewById(R.id.remark);


        save_cancal = ( Button ) findViewById(R.id.save_cancal);
        save_next = ( Button ) findViewById(R.id.save_next);

        save_cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (testyes.isChecked() || testno.isChecked()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ATPDocnoActivity.this);
                    builder.setCancelable(true);
                    builder.setTitle("ยืนยัน");
                    builder.setMessage("ต้องการบันทึกข้อมูลใช่หรือไม่");
                    builder.setPositiveButton("ใช่",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String Remark;
                                    if (remark.getText().toString().equals("")) {
                                        Remark = "";
                                    } else {
                                        Remark = remark.getText().toString();
                                    }
                                    SaveDoc(DocNo, Remark, testremark.getText().toString());
                                }
                            });
                    builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }else {
                    Toast.makeText(ATPDocnoActivity.this, "เลือกผลการทดสอบ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void SaveDoc(final String DocNo,final String Remark,final String testremark) {
        class SaveDoc extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    for(int i=0;i<rs.length();i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("finish").equals("true")){
                            Toast.makeText(ATPDocnoActivity.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(ATPDocnoActivity.this, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("DocNo", DocNo);
                data.put("AtpTest", checkpass1);
                data.put("Code", testremark);
                data.put("Remark", Remark);
                data.put("B_ID",B_ID);
                data.put("EmpCode",EmpCode);
                data.put("TestProgramName",TestProgramName);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "savespore_doc1.php", data);
                    Log.d("BANKFEY",data+"");
                    Log.d("BANKFEY",result);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        SaveDoc obj = new SaveDoc();
        obj.execute();
    }

//    public void ShowDoc() {
//
//        class ShowDoc extends AsyncTask<String, Void, String> {
//
//            // variable
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//                    for(int i=0;i<rs.length();i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        DocNoSave = c.getString("DocNo");
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String,String>();
//                data.put("B_ID",B_ID);
//                data.put("DocNo",DocNo);
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                String result = null;
//
//                try {
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_doco_save.php", data);
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//            // =========================================================================================
//        }
//
//        ShowDoc obj = new ShowDoc();
//        obj.execute();
//    }

    private void byIntent(){
        Intent intent = getIntent();
        DocNo = intent.getStringExtra("DocNo");
        WashMachineID = intent.getStringExtra("WashMachineID");
        WashRoundNumber = intent.getStringExtra("WashRoundNumber");
        TestProgramName = intent.getStringExtra("TestProgramName");
        SterileMachineID = intent.getStringExtra("SterileMachineID");
        SterileRoundNumber = intent.getStringExtra("SterileRoundNumber");
        IsActive = intent.getStringExtra("IsActive");
        xSel1 = intent.getStringExtra("xSel1");
        B_ID = intent.getStringExtra("B_ID");
        EmpCode = intent.getStringExtra("EmpCode");
        Page = intent.getStringExtra("page");
    }

    public void _gotoPage(Class gt){
        Intent intent = new Intent(getApplication(), gt);
        intent.putExtra("DocNo", DocNo);
        intent.putExtra("WashMachineID", WashMachineID);
        intent.putExtra("WashRoundNumber", WashRoundNumber);
        intent.putExtra("TestProgramName", TestProgramName);
        intent.putExtra("SterileMachineID", SterileMachineID);
        intent.putExtra("SterileRoundNumber", SterileRoundNumber);
        intent.putExtra("IsActive", IsActive);
        intent.putExtra("xSel1", xSel1);
        intent.putExtra("B_ID", B_ID);
        intent.putExtra("EmpCode", EmpCode);
        intent.putExtra("Page", Page);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}


