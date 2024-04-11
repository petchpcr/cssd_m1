package com.poseintelligence.cssdm1.Menu_Remark;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDisplayDocRemarkAdapter;
import com.poseintelligence.cssdm1.core.CustomExceptionHandler;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDisplayDocRemark;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RemarkActivity extends AppCompatActivity {
    ImageView images1,backpage;
    EditText text_scan;
    CheckBox check_date;
    TextView txt_date;
    ListView rq_listdoc;
    Button button_search;
    String DocDate = "";

    Uri image_uri1;
    private String B_ID = null;

    String ID_Pic;
    String DocNo_pic;
    String PicNum;
    String Name_p;

    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;

    private List<ModelDisplayDocRemark> Model_RQ = null;
    private Calendar myCalendar = Calendar.getInstance();

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

        if(!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    this.getApplicationContext()));
        }

        byIntent();

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (check_date.isChecked()){
            txt_date.setEnabled(false);
            getlistdata("All","");
        }else {
            txt_date.setEnabled(true);
            getlistdata(convertdate(DocDate),"");
        }
    }

    @Override
    public void onBackPressed() {
        backpage.callOnClick();
    }

    private void byIntent(){
        Intent intent = getIntent();
        B_ID = intent.getStringExtra("B_ID");
    }

    public void init() {
        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(RemarkActivity.this, MainMenu.class);
                startActivity(intent1);
                finish();
            }
        });

        images1 = (ImageView) findViewById(R.id.images1);
        text_scan = (EditText) findViewById(R.id.text_scan);
        text_scan.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction()==KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    getlistdata(convertdate(DocDate),text_scan.getText().toString());
                    text_scan.setText("");

                    return true;
                }
                return false;
            }
        });

        text_scan.requestFocus();

        check_date = (CheckBox) findViewById(R.id.check_date);
        check_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (check_date.isChecked()){
                    txt_date.setEnabled(false);
                    getlistdata("All","");
                }else {
                    txt_date.setEnabled(true);
                    getlistdata(convertdate(DocDate),"");
                }
            }
        });

        button_search = (Button) findViewById(R.id.button_search);
        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!check_date.isChecked()){
                    getlistdata(convertdate(DocDate),"");
                }
            }
        });

        rq_listdoc = (ListView) findViewById(R.id.rq_listdoc);
        txt_date = (TextView) findViewById(R.id.txt_date);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }
        };

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RemarkActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(isNullOrEmpty(DocDate)) {
            updateDate();
        }else {
            txt_date.setText(DocDate);
            getlistdata(convertdate(DocDate),"");
        }
    }

    public String convertdate(String date){
        String A_date[] = date.split("/");
        date="";
        for(int i=A_date.length-1;i>=0;i--){
            if(i!=0){
                date+=A_date[i]+"-";
            }else{
                date+=A_date[i];
            }
        }
        return date;
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    private void updateDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_date.setText(sdf.format(myCalendar.getTime()));
        DocDate = txt_date.getText().toString();
        getlistdata(convertdate(DocDate),"");
    }

    public void getlistdata(final String Date,final String key) {
        class getlistdata extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(RemarkActivity.this);
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    List<ModelDisplayDocRemark> list = new ArrayList<>();
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        list.add(
                                get(
                                        c.getString("ID"),
                                        c.getString("SensterileDocNo"),
                                        c.getString("DepName2"),
                                        c.getString("itemname"),
                                        c.getString("UsageCode"),
                                        c.getString("NameType"),
                                        c.getString("Note"),
                                        c.getString("IsPicture"),
                                        c.getString("Picture"),
                                        c.getString("Picture2"),
                                        c.getString("Picture3"),
                                        c.getString("Pictruetext"),
                                        c.getString("Pictruetext2"),
                                        c.getString("Pictruetext3"),
                                        c.getString("MutiPic_Remark")
                                )
                        );
                    }
                    Model_RQ = list;
                    ArrayAdapter<ModelDisplayDocRemark> adapter;
                    adapter = new ListDisplayDocRemarkAdapter(RemarkActivity.this, Model_RQ);
                    rq_listdoc.setAdapter(adapter);
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
                if (check_date.isChecked()){
                    data.put("Date","All");
                }else {
                    data.put("Date",Date);
                }

                data.put("key",key);
//                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_docno_remark.php", data);

                    Log.d("tog_display1","getxUrl : "+((CssdProject) getApplication()).getxUrl() + "cssd_display_docno_remark.php");
                    Log.d("tog_display2","result : "+result);
                    Log.d("tog_display3","data : "+data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            private ModelDisplayDocRemark get(String ID, String SensterileDocNo, String DepName2, String itemname, String UsageCode, String NameType, String Note, String IsPicture, String Picture, String Picture2, String Picture3, String Pictruetext, String Pictruetext2, String Pictruetext3, String MutiPic_Remark){
                return new ModelDisplayDocRemark(ID, SensterileDocNo, DepName2, itemname, UsageCode, NameType, Note, IsPicture, Picture, Picture2, Picture3, Pictruetext, Pictruetext2, Pictruetext3, MutiPic_Remark);
            }
            // =========================================================================================
        }
        getlistdata obj = new getlistdata();
        obj.execute();
    }



    public void OpenCamera(String ID, String SendDocNo,String picnum){
        ID_Pic = ID;
        DocNo_pic = SendDocNo;
        PicNum = picnum;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
            else{
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Image");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
                image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri1);
                startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
            }
        }
    }

    public void DelPicture(String ID, String NamePic){
        ID_Pic = ID;
        Name_p = NamePic;
        DeletePic(ID_Pic,Name_p);
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == IMAGE_CAPTURE_CODE){
//            if(data==null){
                try {

//                    images1.setImageURI(image_uri1);

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), image_uri1);
                    images1.setImageBitmap(bitmap);
                    uploadImage(ID_Pic,DocNo_pic, String.valueOf(images1),PicNum);
                }catch (Exception e){

                }
//            }
        }
    }

    private void DeletePic(String ID,String Name_pic){
        class DeletePic extends AsyncTask<String,Void,String> {
            private ProgressDialog dialog = new ProgressDialog(RemarkActivity.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    List<ModelDisplayDocRemark> list = new ArrayList<>();
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("finish").equals("true1")){
                            getlistdata(convertdate(DocDate),"");
                        }
                    }
                    Model_RQ = list;
                    ArrayAdapter<ModelDisplayDocRemark> adapter;
                    adapter = new ListDisplayDocRemarkAdapter(RemarkActivity.this, Model_RQ);
                    rq_listdoc.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("ID",ID);
                data.put("name1",Name_pic);
                String result = null;
                result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/Delete_pic.php",data);
                Log.d("KFHKDDL",data+"");
                Log.d("KFHKDDL",result+"");
                return  result;
            }
        }
        DeletePic ui = new DeletePic();
        ui.execute();
    }

    private void uploadImage(String ID,String SendDocNo,String binary,String pinnum){
        class UploadImage extends AsyncTask<String,Void,String> {
            private ProgressDialog dialog = new ProgressDialog(RemarkActivity.this);
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
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("finish").equals("true1")){
//                            getlistdata(convertdate(DocDate),"");
                            if (check_date.isChecked()){
                                txt_date.setEnabled(false);
                                getlistdata("All","");
                            }else {
                                txt_date.setEnabled(true);
                                getlistdata(convertdate(DocDate),"");
                            }
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
            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                Bitmap bitmap1 = null;
                String image_str1 = "null";

                int width = 300;
                int height = 300;

                try {

                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1 = ((BitmapDrawable) images1.getDrawable()).getBitmap();
                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //compress to which format you want.
                    byte [] byte_arr1 = stream1.toByteArray();
                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
                }catch (Exception e){ }

                HashMap<String,String> data = new HashMap<>();
                data.put("image1", image_str1);
                if (pinnum.equals("1")){
                    data.put("name1",ID+"_"+SendDocNo+"_pic1");
                }else if (pinnum.equals("2")){
                    data.put("name1",ID+"_"+SendDocNo+"_pic2");
                }else if (pinnum.equals("3")){
                    data.put("name1",ID+"_"+SendDocNo+"_pic3");
                }
                data.put("ID",ID);
                data.put("picnum",pinnum);
                String result = null;
                result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/UploadImage_remark.php",data);
                Log.d("tog_upload","data = "+data);
                Log.d("tog_upload","result = "+result);
                return  result;
            }
        }
        UploadImage ui = new UploadImage();
        ui.execute();
    }

//    private void uploadImage_(String ID,String SendDocNo,String binary,String pinnum){
//        class UploadImage extends AsyncTask<String,Void,String> {
//            private ProgressDialog dialog = new ProgressDialog(RemarkActivity.this);
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//                    List<ModelDisplayDocRemark> list = new ArrayList<>();
//                    for(int i=0;i<rs.length();i++){
//                        JSONObject c = rs.getJSONObject(i);
//                        if (c.getString("finish").equals("true1")){
////                            getlistdata(convertdate(DocDate),"");
//                            if (check_date.isChecked()){
//                                txt_date.setEnabled(false);
//                                getlistdata("All","");
//                            }else {
//                                txt_date.setEnabled(true);
//                                getlistdata(convertdate(DocDate),"");
//                            }
//                        }
//                    }
//                    Model_RQ = list;
//                    ArrayAdapter<ModelDisplayDocRemark> adapter;
//                    adapter = new ListDisplayDocRemarkAdapter(RemarkActivity.this, Model_RQ);
//                    rq_listdoc.setAdapter(adapter);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }finally {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }
//                }
//
//            }
//            @SuppressLint("WrongThread")
//            @Override
//            protected String doInBackground(String... params) {
//                Bitmap bitmap1 = null;
//                String image_str1 = "null";
//
//                int width = 300;
//                int height = 300;
//
//                try {
//
//                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
//                    bitmap1 = ((BitmapDrawable) images1.getDrawable()).getBitmap();
//                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
//                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //compress to which format you want.
//                    byte [] byte_arr1 = stream1.toByteArray();
//                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
//                }catch (Exception e){ }
//
//                HashMap<String,String> data = new HashMap<>();
//                data.put("image1", image_str1);
//                if (pinnum.equals("1")){
//                    data.put("name1",ID+"_"+SendDocNo+"_pic1");
//                }else if (pinnum.equals("2")){
//                    data.put("name1",ID+"_"+SendDocNo+"_pic2");
//                }else if (pinnum.equals("3")){
//                    data.put("name1",ID+"_"+SendDocNo+"_pic3");
//                }
//                data.put("ID",ID);
//                data.put("picnum",pinnum);
//                String result = null;
//                result = httpConnect.sendPostRequest(((CssdProject) getApplication()).  () + "cssd_image/UploadImage_remark.php",data);
//                Log.d("tog_upload","data = "+data);
//                Log.d("tog_upload","result = "+result);
//                return  result;
//            }
//        }
//        UploadImage ui = new UploadImage();
//        ui.execute();
//    }
}