package com.poseintelligence.cssdm1.Menu_MachineTest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class MachineTestDetailActivity extends AppCompatActivity {

    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Uri image_uri1;
    Uri image_uri2;

    boolean pic1 = false;
    boolean pic2 = false;
    String datapic1 = "0";
    String datapic2 = "0";

    ImageView backpage;
    CheckBox testyes;
    CheckBox testno;
    EditText note;
    ImageView images1;
    ImageView images2;

    String userid;
    String ID;
    String CreateDate;
    String LastUpdate;
    String MachineID;
    String MachineName;
    String IsResult;
    String Pic1;
    String Pic2;
    String Remark;
    boolean enableEdit;

    int test = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_test_detail);

        byIntent();

        init();

    }
    private void byIntent(){

        userid = ((CssdProject) getApplication()).getPm().getUserid()+"";

        Intent intent = getIntent();
        ID = intent.getStringExtra("ID");
        CreateDate = intent.getStringExtra("CreateDate");
        LastUpdate = intent.getStringExtra("LastUpdate");
        MachineID = intent.getStringExtra("MachineID");
        MachineName = intent.getStringExtra("MachineName");
        IsResult = intent.getStringExtra("IsResult");
        Pic1 = intent.getStringExtra("Pic1");
        Pic2 = intent.getStringExtra("Pic2");
        Remark = intent.getStringExtra("Remark");
    }

    public void onBackPressed() {
        backpage.callOnClick();
    }

    public void init() {

        ProgressDialog dialog = new ProgressDialog(MachineTestDetailActivity.this);
        dialog.setMessage(Cons.WAIT_FOR_PROCESS);
        dialog.show();

        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView mac_name = (TextView) findViewById(R.id.mac_name);
        TextView createdate = (TextView) findViewById(R.id.createdate);
        TextView lastdate = (TextView) findViewById(R.id.lastdate);

        testyes = (CheckBox) findViewById(R.id.testyes);
        testno = (CheckBox) findViewById(R.id.testno);

        images1 = (ImageView) findViewById(R.id.images1);
        images2 = (ImageView) findViewById(R.id.images2);

        Button save_next = (Button) findViewById(R.id.save_next);

        testyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testno.setChecked(false);
                test = 1;
            }
        });

        testno.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                testyes.setChecked(false);
                test = 0;
            }
        });

        mac_name.setText("เครื่อง : "+MachineName);
        createdate.setText("วันที่สร้าง : "+CreateDate);
        lastdate.setText("อัพเดทล่าสุด : "+LastUpdate);

        if(IsResult.equals("ผ่าน")){
            testyes.setChecked(true);
            test = 1;
        }else if(IsResult.equals("ไม่ผ่าน")){
            testno.setChecked(true);
            test = 0;
        }

        save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(test>=0){
                    if (datapic1.equals("1")) {
                        CheckSave(test);
                    }else{
                        Toast.makeText(MachineTestDetailActivity.this, "กรุณาถ่ายรูป", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(MachineTestDetailActivity.this, "ยังไม่ได้เลือกผลการทดสอบ", Toast.LENGTH_SHORT).show();
                }

            }
        });

        images1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else{
                        openCamere();
                    }
                }
                else {
                    openCamere();
                }
            }
        });

        images2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_DENIED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                                    PackageManager.PERMISSION_DENIED){
                        String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PERMISSION_CODE);
                    }
                    else{
                        openCamere1();
                    }
                }
                else {
                    openCamere1();
                }
            }
        });

        try {
            URL imageUrl1 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/uploads/"+Pic1);
            Bitmap bmp = BitmapFactory.decodeStream(imageUrl1.openConnection().getInputStream());
            images1.setImageBitmap(bmp);
            datapic1="1";
            Log.d("tog_datapic","load pic1 = "+Pic1);
            Log.d("tog_datapic","load datapic1 = "+datapic1);

            URL imageUrl2 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/uploads/"+Pic2);
            Bitmap bmp2 = BitmapFactory.decodeStream(imageUrl2.openConnection().getInputStream());
            images2.setImageBitmap(bmp2);
            datapic2="1";
            Log.d("tog_datapic","load pic2 = "+Pic2);
            Log.d("tog_datapic","load datapic2 = "+datapic2);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        note = (EditText)findViewById(R.id.remark);
        if(!Remark.equals("null")){
            note.setText(Remark);
        }

        dialog.dismiss();

    }


    private void openCamere() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Image");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri1);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
        pic1 = true;
    }

    private void openCamere1() {
        Log.d("tog_datapic","openCamere1 datapic1 = "+datapic1);
        if (datapic1.equals("1")) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            image_uri2 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri2);
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
            pic2 = true;
        } else {
            openCamere();
        }
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1001){
            if(pic1 == true){
                images1.setImageURI(image_uri1);
                pic1 = false;
                datapic1 = String.valueOf(data);
                if (datapic1.equals("null")){
                    datapic1 = "1";
                }else {
                    datapic1 = "0";
                }
                Log.d("tog_datapic","datapic1 = "+datapic1);
            }else if (pic2 == true){
                images2.setImageURI(image_uri2);
                pic2 = false;
                datapic2 = String.valueOf(data);
                if (datapic2.equals("null")){
                    datapic2 = "1";
                }else {
                    datapic2 = "0";
                }
                Log.d("tog_datapic","datapic2 = "+datapic2);
            }
        }

    }

    public void CheckSave(int isResult) {

        ProgressDialog dialog = new ProgressDialog(MachineTestDetailActivity.this);
        dialog.setMessage(Cons.WAIT_FOR_PROCESS);
        dialog.show();

        class CheckSave extends AsyncTask<String, Void, String> {

            int width = 800;
            int height = 800;
            Bitmap bitmap1;
            Bitmap bitmap2;
            String image_str1 = "null";
            String image_str2 = "null";
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (datapic1.equals("1")) {
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1 = ((BitmapDrawable) images1.getDrawable()).getBitmap();
                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
                    bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1);
                    byte [] byte_arr1 = stream1.toByteArray();
                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
                }

                if (datapic2.equals("1")) {
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                    bitmap2 = ((BitmapDrawable) images2.getDrawable()).getBitmap();
                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, width,height, true);
                    bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                    byte [] byte_arr2 = stream2.toByteArray();
                    image_str2 = Base64.encodeToString(byte_arr2, Base64.DEFAULT);
                }

            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                dialog.dismiss();
                if(result.equals("success")){
                    Toast.makeText(MachineTestDetailActivity.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                    finish();
                }else {
                    Toast.makeText(MachineTestDetailActivity.this, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("id",ID);
                data.put("machineID",MachineID);
                data.put("userID",userid);
                data.put("isResult",isResult+"");
                data.put("note",note.getText().toString());
                data.put("imageBase64Encode1",image_str1);
                data.put("imageBase64Encode2",image_str2);
                if(CreateDate.equals("-")){
                    data.put("firstTime","true");
                }else{
                    data.put("firstTime","false");
                }
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "check_machine/set_machine_data.php", data);
                    Log.d("tog_set_machine","result = "+result);
                    Log.d("tog_set_machine","data = "+data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        CheckSave obj = new CheckSave();
        obj.execute();
    }

}