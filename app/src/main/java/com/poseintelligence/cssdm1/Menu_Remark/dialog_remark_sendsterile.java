package com.poseintelligence.cssdm1.Menu_Remark;

import static com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity.hideKeyboard;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class dialog_remark_sendsterile extends AppCompatActivity {
    LinearLayout R1,R2,R3;
    CheckBox check1,check2,check3,check4,check5,check6;
    EditText text_remark,userdep,qty_item;
    Button summit,cancle;
    ImageView close;

    String Itemname,Usagecode,DepID,DocNoSend,IsStatus,DocNo;
    String datacheck = "0",EmpCode,Type,Xqty,Qty_save,page;
    String IsSave = "0";

    boolean IsAdmin = false;

    private ReceiveActivity context;

    Intent intent;

    private String data = "";
    private String RETURN_VALUE = "";
    private String RETURN_ADMIN = "";
    private String RETURN_INCHARG = "";
    private String RETURN_USER = "";
    private String RETURN_EMCODE = "";
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_remark_sendsterile);
        byIntent();
        initialize();
        PutDataCheckBox1();
        PutDataCheckBox2();
        PutDataCheckBox3();
        PutDataCheckBox4();
        PutDataCheckBox5();
        PutDataCheckBox6();
    }

    public void byIntent() {
        intent = getIntent();
        IsAdmin = intent.getBooleanExtra("IsAdmin",false);
        IsStatus = intent.getStringExtra("IsStatus");
        Itemname = intent.getStringExtra("Itemname");
        Usagecode = intent.getStringExtra("Usagecode");
        DepID = intent.getStringExtra("DepID");
        DocNoSend = intent.getStringExtra("DocNoSend");
        EmpCode = intent.getStringExtra("EmpCode");
        Type = intent.getStringExtra("Type");
        DocNo = intent.getStringExtra("DocNo");
        Xqty = intent.getStringExtra("Qty");
        Qty_save = intent.getStringExtra("Qty_save");
        page = intent.getStringExtra("page");
    }

    public void initialize() {
        qty_item = (EditText ) findViewById(R.id.qty_item);
        if (Qty_save.equals("0")){
            qty_item.setText(Xqty);
        }else {
            qty_item.setText(Qty_save);
        }

        check1 = (CheckBox) findViewById(R.id.check1);
        check2 = (CheckBox) findViewById(R.id.check2);
        check3 = (CheckBox) findViewById(R.id.check3);
        check4 = (CheckBox) findViewById(R.id.check4);
        check5 = (CheckBox) findViewById(R.id.check5);
        check6 = (CheckBox) findViewById(R.id.check6);
        close = (ImageView) findViewById(R.id.close);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("usagecode",Usagecode);
                intent.putExtra("DocNoSend",DocNoSend);
                setResult(1005, intent);
                finish();
            }
        });

        if (Type.equals("1")){
            ShowDetail(DocNoSend,Itemname);
        }

        SaveRemarkDocNoStart(DocNoSend,Usagecode,Itemname);

        check1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check1.isChecked()) {
                    check1.setChecked(true);
                    check2.setChecked(false);
                    check3.setChecked(false);
                    check4.setChecked(false);
                    check5.setChecked(false);
                    check6.setChecked(false);
                }
            }
        });

        check2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check2.isChecked()) {
                    check2.setChecked(true);
                    check1.setChecked(false);
                    check3.setChecked(false);
                    check4.setChecked(false);
                    check5.setChecked(false);
                    check6.setChecked(false);
                }
            }
        });

        check3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check3.isChecked()) {
                    check3.setChecked(true);
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check4.setChecked(false);
                    check5.setChecked(false);
                    check6.setChecked(false);
                }
            }
        });

        check4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check4.isChecked()) {
                    check4.setChecked(true);
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                    check5.setChecked(false);
                    check6.setChecked(false);
                }
            }
        });

        check5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check5.isChecked()) {
                    check5.setChecked(true);
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                    check4.setChecked(false);
                    check6.setChecked(false);
                }
            }
        });

        check6.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (check6.isChecked()) {
                    check6.setChecked(true);
                    check1.setChecked(false);
                    check2.setChecked(false);
                    check3.setChecked(false);
                    check4.setChecked(false);
                    check5.setChecked(false);
                }
            }
        });

        R1 = (LinearLayout) findViewById(R.id.R1);
        R2 = (LinearLayout) findViewById(R.id.R2);
        R3 = (LinearLayout) findViewById(R.id.R3);

        text_remark = (EditText) findViewById(R.id.text_remark);

        userdep = (EditText) findViewById(R.id.userdep);

        summit = (Button) findViewById(R.id.summit);
        summit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!IsStatus.equals("1")){
                    if (check1.isChecked()){
                        datacheck = "1";
                    }else if (check2.isChecked()){
                        datacheck = "2";
                    }else if (check3.isChecked()){
                        datacheck = "3";
                    }else if (check4.isChecked()){
                        datacheck = "4";
                    }else if (check5.isChecked()){
                        datacheck = "5";
                    }else if (check6.isChecked()){
                        datacheck = "6";
                    }

                    if (qty_item.getText().toString().equals("0") || qty_item.getText().toString().equals("")){
                        Toast.makeText(dialog_remark_sendsterile.this, "ตัวเลขจำนวนไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
                    }else {
                        if (userdep.getText().toString().equals("")){
                            Toast.makeText(dialog_remark_sendsterile.this, "กรุณากรอกชื่อเจ้าหน้าที่", Toast.LENGTH_SHORT).show();
                        }else {
                            String Qty = qty_item.getText().toString();
                            if (Qty_save.equals("0")){
                                if (Integer.parseInt(Qty) > Integer.parseInt(Xqty)) {
                                    if (Qty_save.equals("0")){
                                        qty_item.setText(Xqty);
                                    }else {
                                        qty_item.setText(Qty_save);
                                    }
                                    qty_item.requestFocus();
                                    qty_item.setTextColor(Color.RED);
                                    Toast.makeText(dialog_remark_sendsterile.this, "ตัวเลขจำนวนมากกว่าที่กำหนด !!", Toast.LENGTH_SHORT).show();
                                }else {
                                    if (!datacheck.equals("0")){
                                        openQR_Save("admin_save");
                                        qty_item.setTextColor(Color.BLACK);
                                    }else {
                                        Toast.makeText(dialog_remark_sendsterile.this, "กรุณากรอกข้อมูลให้ครบถ้วน !!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else {
                                if (Integer.parseInt(Qty) > Integer.parseInt(Qty_save)) {
                                    if (Qty_save.equals("0")){
                                        qty_item.setText(Xqty);
                                    }else {
                                        qty_item.setText(Qty_save);
                                    }
                                    qty_item.requestFocus();
                                    qty_item.setTextColor(Color.RED);
                                    Toast.makeText(dialog_remark_sendsterile.this, "ตัวเลขจำนวนมากกว่าที่กำหนด !!", Toast.LENGTH_SHORT).show();
                                }else {
                                    if (!datacheck.equals("0")){
                                        openQR_Save("admin_save");
                                        qty_item.setTextColor(Color.BLACK);
                                    }else {
                                        Toast.makeText(dialog_remark_sendsterile.this, "กรุณากรอกข้อมูลให้ครบถ้วน !!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        }
                    }
                }else {
                    Toast.makeText(dialog_remark_sendsterile.this, "ไม่สามารถบันทึก Remark ได้เนื่องจากเอกสารถูกบันทึกไปแล้ว !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancle = (Button ) findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!IsStatus.equals("1")){
                    if (!userdep.getText().toString().equals("")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(dialog_remark_sendsterile.this);
                        builder.setCancelable(true);
                        builder.setTitle("ยืนยัน");
                        builder.setMessage("ยืนยันการยกเลิก Remark หรือไม่ !!");
                        builder.setPositiveButton("ยืนยัน",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        openQR("admin");
                                    }
                                });
                        builder.setNegativeButton("ไม่ยืนยัน", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.putExtra("usagecode",Usagecode);
                                intent.putExtra("DocNoSend",DocNoSend);
                                setResult(1005, intent);
                                finish();
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("usagecode",Usagecode);
                        intent.putExtra("DocNoSend",DocNoSend);
                        setResult(1005, intent);
                        finish();
                    }
                }else {
                    Toast.makeText(dialog_remark_sendsterile.this, "ไม่สามารถยกเลิก Remark ได้เนื่องจากเอกสารถูกบันทึกไปแล้ว !!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openQR(final String admin){
//        Intent i = new Intent(dialog_remark_sendsterile.this, CssdQrUser.class);
//        i.putExtra("page", page);
//        i.putExtra("data", admin);
//        startActivityForResult(i,1006);
    }

    private void openQR_Save(final String admin){
//        Intent i = new Intent(dialog_remark_sendsterile.this, CssdQrUser.class);
//        i.putExtra("data", admin);
//        i.putExtra("page", page);
//        startActivityForResult(i,1007);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;
        try {
            RETURN_VALUE = data.getStringExtra("RETURN_VALUE");
            RETURN_ADMIN = data.getStringExtra("RETURN_ADMIN");
            RETURN_INCHARG = data.getStringExtra("RETURN_INCHARG");
            RETURN_USER = data.getStringExtra("RETURN_USER");
            RETURN_EMCODE = data.getStringExtra("RETURN_EMCODE");

            if (resultCode == 1006) {
                if (RETURN_ADMIN.equals("1")){
                    CancelRemark(datacheck,text_remark.getText().toString(),DocNoSend,Usagecode,Itemname,DepID);
                }else {
                    if (RETURN_INCHARG.equals("1")){
                        CancelRemark(datacheck,text_remark.getText().toString(),DocNoSend,Usagecode,Itemname,DepID);
                    }else {
                        if (RETURN_USER.equals("1")){
                            CancelRemark(datacheck,text_remark.getText().toString(),DocNoSend,Usagecode,Itemname,DepID);
                        }else {
                            Toast.makeText(dialog_remark_sendsterile.this, "สิทธิ์ผู้ใช้งานไม่สามารถเข้าถึงส่วนนี้ได้ !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }else if (resultCode == 1007){
                if (RETURN_ADMIN.equals("1")){
                    if (DocNoSend.equals("")){
                        SaveRemarkDocNo(DocNoSend,Usagecode,Itemname);
                    }else {
                        CheckStatusDocNo(DocNoSend);
                    }
                }else {
                    if (RETURN_INCHARG.equals("1")){
                        if (DocNoSend.equals("")){
                            SaveRemarkDocNo(DocNoSend,Usagecode,Itemname);
                        }else {
                            CheckStatusDocNo(DocNoSend);
                        }
                    }else {
                        if (RETURN_USER.equals("1")){
                            if (DocNoSend.equals("")){
                                SaveRemarkDocNo(DocNoSend,Usagecode,Itemname);
                            }else {
                                CheckStatusDocNo(DocNoSend);
                            }
                        }else {
                            Toast.makeText(dialog_remark_sendsterile.this, "สิทธิ์ผู้ใช้งานไม่สามารถเข้าถึงส่วนนี้ได้ !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void SaveRemarkDocNo(final String senddocno, final String usagecode, final String itemname) {
        class SaveRemarkDocNo extends AsyncTask<String, Void, String> {
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
                        if (c.getString("finish").equals("1") || c.getString("finish").equals("0")){
                            SaveRemark(datacheck,text_remark.getText().toString(),DocNoSend,Usagecode,Itemname,DepID);
                        }else {
                            Toast.makeText(dialog_remark_sendsterile.this, "กรุณายกเลิก Remark เก่าก่อน !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("senddocno",senddocno);
                data.put("usagecode",usagecode);
                data.put("itemname",itemname);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_remark_send.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        SaveRemarkDocNo obj = new SaveRemarkDocNo();
        obj.execute();
    }

    public void SaveRemarkDocNoStart(final String senddocno, final String usagecode, final String itemname) {
        class SaveRemarkDocNoStart extends AsyncTask<String, Void, String> {
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
                        if (c.getString("finish").equals("1") || c.getString("finish").equals("0")){
                            userdep.setEnabled(true);
                            text_remark.setEnabled(true);
                            summit.setEnabled(true);
                            check1.setEnabled(true);
                            check2.setEnabled(true);
                            check3.setEnabled(true);
                            check4.setEnabled(true);
                            check5.setEnabled(true);
                            check6.setEnabled(true);
                        }else {
                            userdep.setEnabled(false);
                            text_remark.setEnabled(false);
                            summit.setEnabled(false);
                            check1.setEnabled(false);
                            check2.setEnabled(false);
                            check3.setEnabled(false);
                            check4.setEnabled(false);
                            check5.setEnabled(false);
                            check6.setEnabled(false);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("senddocno",senddocno);
                data.put("usagecode",usagecode);
                data.put("itemname",itemname);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_remark_send.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        SaveRemarkDocNoStart obj = new SaveRemarkDocNoStart();
        obj.execute();
    }

    public void SaveRemark(final String remarkselect, final String noteremark, final String senddocno, final String usagecode, final String itemname, final String depname) {
        class SaveRemark extends AsyncTask<String, Void, String> {
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
                        if (c.getString("finish").equals("true")){
                            Intent intent = new Intent();
                            intent.putExtra("usagecode",usagecode);
                            intent.putExtra("DocNoSend",DocNoSend);
                            setResult(1005, intent);
                            finish();
                            Toast.makeText(dialog_remark_sendsterile.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                            IsSave = "1";
                        }else {
                            Intent intent = new Intent();
                            intent.putExtra("usagecode",usagecode);
                            intent.putExtra("DocNoSend",DocNoSend);
                            setResult(1005, intent);
                            finish();
                            Toast.makeText(dialog_remark_sendsterile.this, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            IsSave = "0";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("remarkselect",remarkselect);
                data.put("noteremark",noteremark);
                data.put("senddocno",senddocno);
                data.put("usagecode",usagecode);
                data.put("itemname",itemname);
                data.put("depname",userdep.getText().toString());
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                if (RETURN_EMCODE != null){
                    data.put("EmpCode",RETURN_EMCODE);
                }else {
                    data.put("EmpCode","");
                }
                data.put("Type",Type);
                data.put("Qty_save",qty_item.getText().toString());
                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_save_remark_send.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        SaveRemark obj = new SaveRemark();
        obj.execute();
    }

    public void CancelRemark(final String remarkselect, final String noteremark, final String senddocno, final String usagecode, final String itemname, final String depname) {
        class CancelRemark extends AsyncTask<String, Void, String> {
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
                        if (c.getString("finish").equals("true")){
                            Intent intent = new Intent();
                            intent.putExtra("usagecode",usagecode);
                            intent.putExtra("DocNoSend",DocNoSend);
                            setResult(1005, intent);
                            finish();
                            Toast.makeText(dialog_remark_sendsterile.this, "ยกเลิก Remark สำเร็จ", Toast.LENGTH_SHORT).show();
                            IsSave = "1";
                        }else {
                            Intent intent = new Intent();
                            intent.putExtra("usagecode",usagecode);
                            intent.putExtra("DocNoSend",DocNoSend);
                            setResult(1005, intent);
                            finish();
                            Toast.makeText(dialog_remark_sendsterile.this, "ยกเลิก Remark ไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            IsSave = "0";
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("usagecode",usagecode);
                data.put("itemname",itemname);
                data.put("EmpCode",RETURN_EMCODE);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_delete_remark_send.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        CancelRemark obj = new CancelRemark();
        obj.execute();
    }

    public void ShowDetail(final String senddocno, final String itemname) {
        class ShowDetail extends AsyncTask<String, Void, String> {
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
                        userdep.setText(c.getString("DepRemark"));
                        text_remark.setText(c.getString("Note"));
                        if (IsStatus.equals("1")){
                            userdep.setEnabled(false);
                            text_remark.setEnabled(false);
                            summit.setEnabled(false);
                            check1.setEnabled(false);
                            check2.setEnabled(false);
                            check3.setEnabled(false);
                            check4.setEnabled(false);
                            check5.setEnabled(false);
                            check6.setEnabled(false);
                        }
                        if (c.getString("RemarkTypeID").equals("1")){
                            check1.setChecked(true);
                            check2.setChecked(false);
                            check3.setChecked(false);
                            check4.setChecked(false);
                            check5.setChecked(false);
                            check6.setChecked(false);
                        }else if (c.getString("RemarkTypeID").equals("2")){
                            check2.setChecked(true);
                            check1.setChecked(false);
                            check3.setChecked(false);
                            check4.setChecked(false);
                            check5.setChecked(false);
                            check6.setChecked(false);
                        }else if (c.getString("RemarkTypeID").equals("3")){
                            check3.setChecked(true);
                            check1.setChecked(false);
                            check2.setChecked(false);
                            check4.setChecked(false);
                            check5.setChecked(false);
                            check6.setChecked(false);
                        }else if (c.getString("RemarkTypeID").equals("4")){
                            check4.setChecked(true);
                            check1.setChecked(false);
                            check2.setChecked(false);
                            check3.setChecked(false);
                            check5.setChecked(false);
                            check6.setChecked(false);
                        }else if (c.getString("RemarkTypeID").equals("5")){
                            check5.setChecked(true);
                            check1.setChecked(false);
                            check2.setChecked(false);
                            check3.setChecked(false);
                            check4.setChecked(false);
                            check6.setChecked(false);
                        }else if (c.getString("RemarkTypeID").equals("6")){
                            check6.setChecked(true);
                            check1.setChecked(false);
                            check2.setChecked(false);
                            check3.setChecked(false);
                            check4.setChecked(false);
                            check5.setChecked(false);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("senddocno",senddocno);
                data.put("itemname",itemname);
                data.put("Type",Type);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_detail_remark.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        ShowDetail obj = new ShowDetail();
        obj.execute();
    }

    public void PutDataCheckBox1() {
        class PutDataCheckBox1 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check1.setText(c.getString("NameType"));
                        }else {
                            R1.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","1");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox1 obj = new PutDataCheckBox1();
        obj.execute();
    }

    public void PutDataCheckBox2() {
        class PutDataCheckBox2 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check2.setText(c.getString("NameType"));
                        }else {
                            check2.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","2");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox2 obj = new PutDataCheckBox2();
        obj.execute();
    }

    public void PutDataCheckBox3() {
        class PutDataCheckBox3 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check3.setText(c.getString("NameType"));
                        }else {
                            R2.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","3");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox3 obj = new PutDataCheckBox3();
        obj.execute();
    }

    public void PutDataCheckBox4() {
        class PutDataCheckBox4 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check4.setText(c.getString("NameType"));
                        }else {
                            check4.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","4");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox4 obj = new PutDataCheckBox4();
        obj.execute();
    }

    public void PutDataCheckBox5() {
        class PutDataCheckBox5 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check5.setText(c.getString("NameType"));
                        }else {
                            R3.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","5");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox5 obj = new PutDataCheckBox5();
        obj.execute();
    }

    public void PutDataCheckBox6() {
        class PutDataCheckBox6 extends AsyncTask<String, Void, String> {
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
                        if (!c.getString("NameType").equals("")){
                            check6.setText(c.getString("NameType"));
                        }else {
                            check6.setVisibility(View.GONE);
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("data","6");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_remark_type.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        PutDataCheckBox6 obj = new PutDataCheckBox6();
        obj.execute();
    }

    public void CheckStatusDocNo(final String senddocno) {
        class CheckStatusDocNo extends AsyncTask<String, Void, String> {
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
                        if (c.getString("IsStatus").equals("0")){
                            SaveRemarkDocNo(DocNoSend,Usagecode,Itemname);
                        }else {
                            Toast.makeText(dialog_remark_sendsterile.this, "ไม่สามารถแก้ไข Remark ได้เนื่องจากเอกสารถูกบันทึกแล้ว !!", Toast.LENGTH_SHORT).show();
                        }
                        hideKeyboard(dialog_remark_sendsterile.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("senddocno",senddocno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;
                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_statusdocno.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        CheckStatusDocNo obj = new CheckStatusDocNo();
        obj.execute();
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

}
