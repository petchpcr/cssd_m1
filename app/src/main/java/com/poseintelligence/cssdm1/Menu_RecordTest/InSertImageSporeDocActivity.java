package com.poseintelligence.cssdm1.Menu_RecordTest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.interfaces.ItemClickListener;
import com.denzcoskun.imageslider.models.SlideModel;
import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_Remark.RemarkActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelReceiveInDetail;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class InSertImageSporeDocActivity extends AppCompatActivity {
    private static final Object REQUEST_CODE_PERMISSIONS = 1101;
    WebView webView;
    ArrayAdapter<String> adapter_spinner_detail;
    ArrayAdapter<String> adapter_spinner_detail1;
    ArrayAdapter<String> adapter_spinner;
    private HashMap<String,String> array_spinner_id = new HashMap<>();
    private ArrayList<String> list_sp = new ArrayList<String>();
    int Spinner_data = 0;
    String DocNo_Save = "";
    String datapic1 = "0";
    String datapic2 = "0";
    TextView docno;
    TextView round;
    TextView program;
    TextView test;
    TextView testremark1;
    CheckBox check_code;
    CheckBox testno;
    CheckBox testyes;
    EditText code;
    EditText code1;
    EditText code2;
    EditText remark;
    EditText testremark;
    ImageView images1;
    ImageView images2;
    ImageView flip_camera1;
    ImageView flip_camera2;
    Button save_cancal;
    Button save_next;
    String DocNo;
    String WashMachineID;
    String WashRoundNumber;
    String SterileMachineID;
    String SterileRoundNumber;
    String TestProgramName;
    String IsActive;
    String ID = "";
    String EmpCode;
    String checkcode = "";
    String checkpass = "";
    String xSel = "";
    String xSel1 = "";
    String xSel2 = "";
    String Page = "";
    String count = "";
    String IsATP = "";
    String Username = "";
    String codeData_pic1;
    String codeData_pic2;
    String ID_Pic;
    String DocNo_pic;
    String PicNum;
    String PicText1;
    String PicText2;
    String Name_p;
    boolean pic = true;
    boolean pic2 = true;
    int statusPic = 0;
    int width = 800;
    int height = 800;
    Bitmap bitmap1 = null;
    Bitmap bitmap2 = null;
    private Spinner pg_spinner;
//    private Spinner pg_spinner1;
    private String B_ID = null;
    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    URL imageUrl1;
    URL imageUrl2;
    Uri image_uri1;
    Uri image_uri2;
    String NetWorkApp = "0";
    String countatp;
    TextView text1;
    TextView text2;
    TextView text3;
    int countatpint;
    ArrayList<String> liststatus = new ArrayList<String>();
    HashMap<String, String> Pro = new HashMap<String,String>();
    String image_str2="null";
    String image_str1="null";
    String CssdTest_std;

    android.hardware.Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_sert_image_spore_doc);

//        getSupportActionBar().hide();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if (checkSelfPermission(Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_DENIED){
                String [] permission = {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }
        }

        try {
            Picasso.setSingletonInstance(
                    new Picasso.Builder(this)
                            .build());
        } catch ( IllegalStateException e ) {

        }


        byIntent();

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
        CheckSaveATP();

        ShowATP();

        ShowDetail();

        ShowRound();
    }

    private void byIntent(){
        Intent intent = getIntent();
        DocNo = intent.getStringExtra("DocNo");
        WashMachineID = intent.getStringExtra("WashMachineID");
        WashRoundNumber = intent.getStringExtra("WashRoundNumber");
        TestProgramName = intent.getStringExtra("TestProgramName");
        SterileMachineID = intent.getStringExtra("SterileMachineID");
        SterileRoundNumber = intent.getStringExtra("SterileRoundNumber");
        IsActive = intent.getStringExtra("IsActive");
        xSel1 = intent.getStringExtra("ID");
        B_ID = intent.getStringExtra("B_ID");
        EmpCode = intent.getStringExtra("EmpCode");
        Page = intent.getStringExtra("page");
        Username = intent.getStringExtra("Username");
        CssdTest_std = intent.getStringExtra("CssdTest_std");
    }

    public void init() {

        pg_spinner = (Spinner) findViewById(R.id.pg_spinner);

        list_sp.add(TestProgramName);
        adapter_spinner = new ArrayAdapter<String>(InSertImageSporeDocActivity.this, android.R.layout.simple_spinner_dropdown_item, list_sp);
        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pg_spinner.setAdapter(adapter_spinner);

//        setSpinnerWash("1",TestProgramName);
//        setSpinnerSterile("2",TestProgramName);

        docno = (TextView)findViewById(R.id.docno);
        round = (TextView)findViewById(R.id.createdate);
        program = (TextView)findViewById(R.id.program);
        test = (TextView)findViewById(R.id.test);
        check_code = (CheckBox)findViewById(R.id.check_code);

        if (!IsATP.equals("")) {
            check_code.setChecked(true);
        }else {
            //check_code.setChecked(false);
        }

        if (Page.equals("1")){
            test.setVisibility(View.GONE);
            check_code.setVisibility(View.GONE);
            setSpinnerWash("2",TestProgramName);
        }else {
            test.setVisibility(View.VISIBLE);
            check_code.setVisibility(View.VISIBLE);
            setSpinnerWash("1",TestProgramName);
        }

        testno = (CheckBox)findViewById(R.id.testno);
        testyes = (CheckBox)findViewById(R.id.testyes);

        text1 = (TextView)findViewById(R.id.text1);
        text2 = (TextView)findViewById(R.id.text2);
        text3 = (TextView)findViewById(R.id.text3);

        code = (EditText)findViewById(R.id.code);
        code1 = (EditText)findViewById(R.id.code1);
        code2 = (EditText)findViewById(R.id.code2);

        code.setVisibility(View.GONE);
        code1.setVisibility(View.GONE);
        code2.setVisibility(View.GONE);

        text1.setVisibility(View.GONE);
        text2.setVisibility(View.GONE);
        text3.setVisibility(View.GONE);

        remark = (EditText)findViewById(R.id.remark);

        if (xSel1.equals("0")) {
            remark.setHint("หมายเหตุ :");
        }else if (xSel1.equals("1")) {
            remark.setHint("เครื่องมือ/เครื่องผ้า/Stock CSSD :");
        }else if (xSel1.equals("2")){
            remark.setHint("หมายเหตุ :");
        }else if (xSel1.equals("3")){
            remark.setHint("หมายเหตุ :");
        }else if (xSel1.equals("4")){
            remark.setHint("หมายเหตุ :");
        }else if (xSel1.equals("5")){
            remark.setHint("เครื่องมือ/แผนก :");
        }else if (xSel1.equals("6")){
            remark.setHint("แผนกต่าง ๆ :");
        }else if (xSel1.equals("7")){
            remark.setHint("ชุดเครื่องมือและจำนวน :");
        }

        testremark = (EditText) findViewById(R.id.testremark);
        testremark1 = (TextView) findViewById(R.id.testremark1);
        testremark.setVisibility(View.GONE);
        testremark1.setVisibility(View.GONE);
        images1 = (ImageView ) findViewById(R.id.images1);
        images2 = (ImageView ) findViewById(R.id.images2);
        save_cancal = (Button) findViewById(R.id.save_cancal);
        save_next = (Button) findViewById(R.id.save_next);

        if (!IsActive.equals("1")){
            docno.setText("เลขที่ : "+DocNo);
//            setSpinnerWash("1");
//            setSpinnerSterile("2");
            images1.setEnabled(true);
            images2.setEnabled(true);
        }else {
            docno.setText("เลขที่ : "+DocNo);
            ShowDetail();
//            images1.setEnabled(false);
//            images2.setEnabled(false);
        }

        testyes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (testyes.isChecked()) {
                    checkpass = "1";
                    testyes.setChecked(true);
                    testno.setChecked(false);
                }
            }
        });

        testno.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (testno.isChecked()) {
                    checkpass = "0";
                    testyes.setChecked(false);
                    testno.setChecked(true);
                }
            }
        });

        images1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BANKFEY",NetWorkApp);
                if (!IsActive.equals("1")){
                    Spinner_data = pg_spinner.getSelectedItemPosition();
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
                }else {
                    open_pic("1");
                }

            }
        });

        images2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner_data = pg_spinner.getSelectedItemPosition();
                if (!IsActive.equals("1")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                        if (    checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
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
                }else {
                    open_pic("2");
                }

            }
        });

        check_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Page.equals("1")) {
                    if (!IsATP.equals("1")) {
                        if (pg_spinner.getSelectedItem().equals("")){
                            check_code.setChecked(false);
                            Toast.makeText(InSertImageSporeDocActivity.this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
                            intent.putExtra("DocNo", DocNo);
                            intent.putExtra("WashMachineID", WashMachineID);
                            intent.putExtra("WashRoundNumber", WashRoundNumber);
                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
                            intent.putExtra("SterileMachineID", SterileMachineID);
                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
                            intent.putExtra("IsActive", IsActive);
                            intent.putExtra("xSel1", xSel2);
                            intent.putExtra("B_ID", B_ID);
                            intent.putExtra("EmpCode", EmpCode);
                            intent.putExtra("Page", Page);
                            intent.putExtra("DocNo", DocNo);
                            intent.putExtra("Username", Username);
                            intent.putExtra("CssdTest_std",CssdTest_std);
                            startActivity(intent);

                        }
                    } else {
                        check_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Log.d("FMDLHD",countatpint+"");
                                if (countatpint >= 1 && countatpint != 3){
                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
                                    quitDialog.setTitle(Cons.TITLE);
                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
                                    quitDialog.setMessage("ต้องการบันทึก ATP รอบที่"+countatpint+ "หรือไม่ ?");
                                    quitDialog.setCancelable(false);
                                    quitDialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
                                            intent.putExtra("DocNo", DocNo);
                                            intent.putExtra("WashMachineID", WashMachineID);
                                            intent.putExtra("WashRoundNumber", WashRoundNumber);
                                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
                                            intent.putExtra("SterileMachineID", SterileMachineID);
                                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
                                            intent.putExtra("IsActive", IsActive);
                                            intent.putExtra("xSel1", xSel2);
                                            intent.putExtra("B_ID", B_ID);
                                            intent.putExtra("EmpCode", EmpCode);
                                            Log.d("KFLHDL",EmpCode);
                                            intent.putExtra("Page", Page);
                                            intent.putExtra("DocNo", DocNo);
                                            intent.putExtra("Username", Username);
                                            intent.putExtra("CssdTest_std",CssdTest_std);
                                            startActivity(intent);
                                        }
                                    });
                                    quitDialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            check_code.setChecked(true);
                                        }
                                    });
                                    quitDialog.show();
                                }else{
                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
                                    quitDialog.setTitle(Cons.TITLE);
                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
                                    quitDialog.setMessage("ไม่สามารถบันทึกค่า ATP เกิน 2 รอบ");
                                    quitDialog.setCancelable(false);
                                    quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            check_code.setChecked(true);
                                        }
                                    });
                                    quitDialog.show();
                                }
                            }
                        });
                    }
                }
                else {
                    if (!IsATP.equals("1")) {
                        if (pg_spinner.getSelectedItem().equals("")){
                            check_code.setChecked(false);
                            Toast.makeText(InSertImageSporeDocActivity.this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
                        }else {
                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
                            intent.putExtra("DocNo", DocNo);
                            intent.putExtra("WashMachineID", WashMachineID);
                            intent.putExtra("WashRoundNumber", WashRoundNumber);
                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
                            intent.putExtra("SterileMachineID", SterileMachineID);
                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
                            intent.putExtra("IsActive", IsActive);
                            intent.putExtra("xSel1", xSel1);
                            intent.putExtra("B_ID", B_ID);
                            intent.putExtra("EmpCode", EmpCode);
                            intent.putExtra("Page", Page);
                            intent.putExtra("DocNo", DocNo);
                            intent.putExtra("Username", Username);
                            intent.putExtra("CssdTest_std",CssdTest_std);
                            startActivity(intent);
                        }
                    } else {
                        check_code.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (countatpint >= 1 && countatpint != 3){
                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
                                    quitDialog.setTitle(Cons.TITLE);
                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
                                    quitDialog.setMessage("ต้องการบันทึก ATP รอบที่ "+countatpint+" หรือไม่ ?");
                                    quitDialog.setCancelable(false);
                                    quitDialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
                                            intent.putExtra("DocNo", DocNo);
                                            intent.putExtra("WashMachineID", WashMachineID);
                                            intent.putExtra("WashRoundNumber", WashRoundNumber);
                                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
                                            intent.putExtra("SterileMachineID", SterileMachineID);
                                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
                                            intent.putExtra("IsActive", IsActive);
                                            intent.putExtra("xSel1", xSel1);
                                            intent.putExtra("B_ID", B_ID);
                                            intent.putExtra("EmpCode", EmpCode);
                                            intent.putExtra("Page", Page);
                                            intent.putExtra("DocNo", DocNo);
                                            intent.putExtra("Username", Username);
                                            intent.putExtra("CssdTest_std",CssdTest_std);
                                            startActivity(intent);
                                        }
                                    });
                                    quitDialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            check_code.setChecked(true);
                                        }
                                    });
                                    quitDialog.show();
                                }else{
                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
                                    quitDialog.setTitle(Cons.TITLE);
                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
                                    quitDialog.setMessage("ไม่สามารถบันทึกค่า ATP เกิน 2 รอบ");
                                    quitDialog.setCancelable(false);
                                    quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            check_code.setChecked(true);
                                        }
                                    });
                                    quitDialog.show();
                                }
                            }
                        });
                    }
                }
            }
        });

//        check_code.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean c) {
//                if (Page.equals("1")) {
//                    if (!IsATP.equals("1")) {
//                        if (pg_spinner.getSelectedItem().equals("")){
//                            check_code.setChecked(false);
//                            Toast.makeText(InSertImageSporeDocActivity.this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
//                        }else {
//                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
//                            intent.putExtra("DocNo", DocNo);
//                            intent.putExtra("WashMachineID", WashMachineID);
//                            intent.putExtra("WashRoundNumber", WashRoundNumber);
//                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
//                            intent.putExtra("SterileMachineID", SterileMachineID);
//                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
//                            intent.putExtra("IsActive", IsActive);
//                            intent.putExtra("xSel1", xSel2);
//                            intent.putExtra("B_ID", B_ID);
//                            intent.putExtra("EmpCode", EmpCode);
//                            intent.putExtra("Page", Page);
//                            intent.putExtra("DocNo", DocNo);
//                            intent.putExtra("Username", Username);
//                            intent.putExtra("CssdTest_std",CssdTest_std);
//                            startActivity(intent);
//
//                        }
//                    } else {
//                        check_code.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Log.d("FMDLHD",countatpint+"");
//                                if (countatpint >= 1 && countatpint != 3){
//                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
//                                    quitDialog.setTitle(Cons.TITLE);
//                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
//                                    quitDialog.setMessage("ต้องการบันทึก ATP รอบที่"+countatpint+ "หรือไม่ ?");
//                                    quitDialog.setCancelable(false);
//                                    quitDialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
//                                            intent.putExtra("DocNo", DocNo);
//                                            intent.putExtra("WashMachineID", WashMachineID);
//                                            intent.putExtra("WashRoundNumber", WashRoundNumber);
//                                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
//                                            intent.putExtra("SterileMachineID", SterileMachineID);
//                                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
//                                            intent.putExtra("IsActive", IsActive);
//                                            intent.putExtra("xSel1", xSel2);
//                                            intent.putExtra("B_ID", B_ID);
//                                            intent.putExtra("EmpCode", EmpCode);
//                                            Log.d("KFLHDL",EmpCode);
//                                            intent.putExtra("Page", Page);
//                                            intent.putExtra("DocNo", DocNo);
//                                            intent.putExtra("Username", Username);
//                                            intent.putExtra("CssdTest_std",CssdTest_std);
//                                            startActivity(intent);
//                                        }
//                                    });
//                                    quitDialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            check_code.setChecked(true);
//                                        }
//                                    });
//                                    quitDialog.show();
//                                }else{
//                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
//                                    quitDialog.setTitle(Cons.TITLE);
//                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
//                                    quitDialog.setMessage("ไม่สามารถบันทึกค่า ATP เกิน 2 รอบ");
//                                    quitDialog.setCancelable(false);
//                                    quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            check_code.setChecked(true);
//                                        }
//                                    });
//                                    quitDialog.show();
//                                }
//                            }
//                        });
//                    }
//                }
//                else {
//                    if (!IsATP.equals("1")) {
//                        if (pg_spinner.getSelectedItem().equals("")){
//                            check_code.setChecked(false);
//                            Toast.makeText(InSertImageSporeDocActivity.this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
//                        }else {
//                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
//                            intent.putExtra("DocNo", DocNo);
//                            intent.putExtra("WashMachineID", WashMachineID);
//                            intent.putExtra("WashRoundNumber", WashRoundNumber);
//                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
//                            intent.putExtra("SterileMachineID", SterileMachineID);
//                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
//                            intent.putExtra("IsActive", IsActive);
//                            intent.putExtra("xSel1", xSel1);
//                            intent.putExtra("B_ID", B_ID);
//                            intent.putExtra("EmpCode", EmpCode);
//                            intent.putExtra("Page", Page);
//                            intent.putExtra("DocNo", DocNo);
//                            intent.putExtra("Username", Username);
//                            intent.putExtra("CssdTest_std",CssdTest_std);
//                            startActivity(intent);
//                        }
//                    } else {
//                        check_code.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (countatpint >= 1 && countatpint != 3){
//                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
//                                    quitDialog.setTitle(Cons.TITLE);
//                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
//                                    quitDialog.setMessage("ต้องการบันทึก ATP รอบที่ "+countatpint+" หรือไม่ ?");
//                                    quitDialog.setCancelable(false);
//                                    quitDialog.setPositiveButton("ใช่", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            Intent intent = new Intent(InSertImageSporeDocActivity.this, ATPDocnoActivity.class);
//                                            intent.putExtra("DocNo", DocNo);
//                                            intent.putExtra("WashMachineID", WashMachineID);
//                                            intent.putExtra("WashRoundNumber", WashRoundNumber);
//                                            intent.putExtra("TestProgramName", String.valueOf(pg_spinner.getSelectedItem()));
//                                            intent.putExtra("SterileMachineID", SterileMachineID);
//                                            intent.putExtra("DoSterileRoundNumbercNo", SterileRoundNumber);
//                                            intent.putExtra("IsActive", IsActive);
//                                            intent.putExtra("xSel1", xSel1);
//                                            intent.putExtra("B_ID", B_ID);
//                                            intent.putExtra("EmpCode", EmpCode);
//                                            intent.putExtra("Page", Page);
//                                            intent.putExtra("DocNo", DocNo);
//                                            intent.putExtra("Username", Username);
//                                            intent.putExtra("CssdTest_std",CssdTest_std);
//                                            startActivity(intent);
//                                        }
//                                    });
//                                    quitDialog.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            check_code.setChecked(true);
//                                        }
//                                    });
//                                    quitDialog.show();
//                                }else{
//                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
//                                    quitDialog.setTitle(Cons.TITLE);
//                                    quitDialog.setIcon(R.drawable.pose_favicon_2x);
//                                    quitDialog.setMessage("ไม่สามารถบันทึกค่า ATP เกิน 2 รอบ");
//                                    quitDialog.setCancelable(false);
//                                    quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            check_code.setChecked(true);
//                                        }
//                                    });
//                                    quitDialog.show();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });

        pg_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (pg_spinner.getSelectedItem().equals("TOSI")){
                    testremark.setVisibility(View.VISIBLE);
                    testremark1.setVisibility(View.VISIBLE);
                }else {
                    testremark1.setVisibility(View.GONE);
                    testremark.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        if (!Page.equals("0")) {
//            pg_spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    if (pg_spinner.getSelectedItem().equals("TOSI")){
//                        testremark.setVisibility(View.VISIBLE);
//                        testremark1.setVisibility(View.VISIBLE);
//                    }else {
//                        testremark1.setVisibility(View.GONE);
//                        testremark.setVisibility(View.GONE);
//                    }
//                }
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//                }
//            });
//        }else {
//            pg_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                @Override
//                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                    pg_spinner1.setVisibility(View.GONE);
//                    if (pg_spinner.getSelectedItem().equals("TOSI")){
//                        testremark.setVisibility(View.VISIBLE);
//                        testremark1.setVisibility(View.VISIBLE);
//                    }else {
//                        testremark1.setVisibility(View.GONE);
//                        testremark.setVisibility(View.GONE);
//                    }
//                }
//
//                @Override
//                public void onNothingSelected(AdapterView<?> parent) {
//
//                }
//            });
//        }

        save_cancal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        save_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!IsActive.equals("1")) {
                    if (!pg_spinner.getSelectedItem().equals("") ) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("ยืนยัน");
                        builder.setMessage("ต้องการบันทึกข้อมูลใช่หรือไม่");
                        builder.setPositiveButton("ใช่",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (!code.getText().toString().equals("")) {
                                            code.getText().toString();
                                        } else {
                                            code.setText("0");
                                        }

                                        if (!code1.getText().toString().equals("")) {
                                            code1.getText().toString();
                                        } else {
                                            code1.setText("0");
                                        }

                                        if (!code2.getText().toString().equals("")) {
                                            code2.getText().toString();
                                        } else {
                                            code2.setText("0");
                                        }

                                        if (check_code.isChecked()) {
                                            checkcode = "1";
                                        } else {
                                            checkcode = "0";
                                        }

                                        if (pg_spinner.getSelectedItem().equals("")) {
                                            xSel = "-";
                                            xSel1 = "0";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("Spore Test 4 ชั่วโมง (SA)")) {
                                            xSel = "Spore Test 4 ชั่วโมง (SA)";
                                            xSel1 = "1";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("Bowie Dick Test")) {
                                            xSel = "Bowie Dick Test";
                                            xSel1 = "2";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("Leak Test")) {
                                            xSel = "Leak Test";
                                            xSel1 = "3";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("TOSI")) {
                                            xSel = "TOSI";
                                            xSel1 = "4";
                                            testremark.setVisibility(View.VISIBLE);
                                            testremark1.setVisibility(View.VISIBLE);
                                        } else if (pg_spinner.getSelectedItem().equals("Sono Check")) {
                                            xSel = "Sono Check";
                                            xSel1 = "5";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("Spore Test 4 ชั่วโมง (EO)")) {
                                            xSel = "Spore Test 4 ชั่วโมง (EO)";
                                            xSel1 = "6";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        } else if (pg_spinner.getSelectedItem().equals("Spore Test 24 นาที (SA)")) {
                                            xSel = "Spore Test 24 นาที (SA)";
                                            xSel1 = "7";
                                            testremark.setVisibility(View.GONE);
                                            testremark1.setVisibility(View.GONE);
                                        }

                                        Log.d("KFKLDD",datapic1+"");
                                        if (datapic1.equals("1")) {
                                            Log.d("KFKLDD",testno.isChecked()+"");
                                            Log.d("KFKLDD",testyes.isChecked()+"");
                                            if (testno.isChecked() || testyes.isChecked()) {
                                                String Remark = "";
                                                if (remark.getText().toString().equals("")) {
                                                    Remark = "";
                                                } else {
                                                    Remark = remark.getText().toString();
                                                }
                                                Log.d("tog_program_m1","SaveDoc = "+pg_spinner.getSelectedItem()+"---"+array_spinner_id.get(pg_spinner.getSelectedItem()));

                                                SaveDoc(DocNo, array_spinner_id.get(pg_spinner.getSelectedItem()), "", "", checkpass, Remark, testremark.getText().toString(),EmpCode);
//                                                SaveDoc(DocNo, String.valueOf(pg_spinner.getSelectedItemPosition()), "", "", checkpass, Remark, testremark.getText().toString(),EmpCode);
                                                uploadImage();
//                                                uploadImage_T2();
                                                uploadTextImage();

                                            } else {
                                                Toast.makeText(InSertImageSporeDocActivity.this, "เลือกผลการทดสอบ", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Toast.makeText(InSertImageSporeDocActivity.this, "เลือกรูปภาพก่อนบันทึกเอกสาร", Toast.LENGTH_SHORT).show();
                                        }
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
                        Toast.makeText(InSertImageSporeDocActivity.this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(InSertImageSporeDocActivity.this, "เอกสารนี้ถูกบันทึกแล้ว", Toast.LENGTH_SHORT).show();
                }
            }
        });

        flip_camera1 = (ImageView) findViewById(R.id.flip_camera1);
        flip_camera2 = (ImageView) findViewById(R.id.flip_camera2);

        if(((CssdProject) getApplication()).Project().equals("VCH")){
            flip_camera1.setVisibility(View.GONE);
            flip_camera2.setVisibility(View.GONE);
        }

        flip_camera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datapic1.equals("1")) {
                    if (images1.getRotation() == 0){
                        images1.setRotation(90);
                    }else if (images1.getRotation() == 90){
                        images1.setRotation(180);
                    }else if (images1.getRotation() == 180){
                        images1.setRotation(270);
                    }else if (images1.getRotation() == 270){
                        images1.setRotation(360);
                    }else {
                        images1.setRotation(90);
                    }
                }
            }
        });

        flip_camera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datapic1.equals("1")) {
                    if (images2.getRotation() == 0){
                        images2.setRotation(90);
                    }else if (images2.getRotation() == 90){
                        images2.setRotation(180);
                    }else if (images2.getRotation() == 180){
                        images2.setRotation(270);
                    }else if (images2.getRotation() == 270){
                        images2.setRotation(360);
                    }else {
                        images2.setRotation(90);
                    }
                }
            }
        });
    }


    private void openCamere() {
        if (!pg_spinner.getSelectedItem().equals("-")) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Image");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
            image_uri1 = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri1);
            startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
            pic = true;
        }else {
            Toast.makeText(this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
        }
    }

    private void openCamere1() {
        if (!pg_spinner.getSelectedItem().equals("-")) {
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
                Toast.makeText(this, "กรุณาถ่ายรูปแรกก่อน !!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "กรุณาเลือกโปรแกรมทดสอบ", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1001){
            if(pic == true){
                images1.setImageURI(image_uri1);
                pic = false;
                statusPic++;
                datapic1 = String.valueOf(data);
                if (datapic1.equals("null")){
                    datapic1 = "1";
                }else {
                    datapic1 = "0";
                }
            }else if (pic2 == true){
                images2.setImageURI(image_uri2);
                pic2 = false;
                statusPic++;
            }
            pg_spinner.setSelection(Spinner_data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public void SaveDoc(final String DocNo, final String ProGram, final String AtpTest, final String AtpCode, final String Test, final String Remark, final String testremark, final String EmpCode) {
        class SaveDoc extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
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
                    List<ModelReceiveInDetail> list = new ArrayList<>();
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("finish").equals("true")){
                            Toast.makeText(InSertImageSporeDocActivity.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                            //finish();
                        }else {
                            Toast.makeText(InSertImageSporeDocActivity.this, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            //finish();
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
                data.put("DocNo", params[0]);
                data.put("ProGram", params[1]);
                data.put("AtpTest", checkpass);
                data.put("Code", "0");
                data.put("Test", params[4]);
                data.put("Remark", params[5]);
                data.put("Page", Page);
                data.put("EmpCode", EmpCode);
                data.put("B_ID",B_ID);
                data.put("Point_test",testremark);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "savespore_doc.php", data);
                    Log.d("tog_savedoc","data = "+data);
                    Log.d("tog_savedoc","result = "+result);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }

        SaveDoc obj = new SaveDoc();
        obj.execute(DocNo,ProGram,AtpTest, String.valueOf(AtpCode),Test,Remark,testremark);
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    private void uploadImage(){
        class UploadImage extends AsyncTask<String,Void,String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {

                try {
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1 = ((BitmapDrawable) images1.getDrawable()).getBitmap();
                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
                    bitmap1 = rotateImage(bitmap1, images1.getRotation());
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //compress to which format you want.
                    byte [] byte_arr1 = stream1.toByteArray();
                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
                }catch (Exception e){

                }

                try {
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                    bitmap2 = ((BitmapDrawable) images2.getDrawable()).getBitmap();
                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, width,height, true);
                    bitmap2 = rotateImage(bitmap2, images2.getRotation());
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2); //compress to which format you want.
                    byte [] byte_arr2 = stream2.toByteArray();
                    image_str2 = Base64.encodeToString(byte_arr2, Base64.DEFAULT);
                }catch (Exception e){

                }

                HashMap<String,String> data = new HashMap<>();
                data.put("image1", image_str1);
                data.put("name1",DocNo+"_pic1");
                data.put("image2", image_str2);
                data.put("name2",DocNo+"_pic2");
                data.put("DocNo",DocNo);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                String result_Us = null;
                if (!images1.getDrawable().equals(null)){
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/UploadImageTestresult.php",data);
                }
                Log.d("tog_UploadImage","data = "+data+"");
                Log.d("tog_UploadImage","result = "+result+"");
                return  result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();
    }

//    private void uploadImage_T2(){
//        class uploadImage_T2 extends AsyncTask<String,Void,String> {
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//            }
//            @SuppressLint("WrongThread")
//            @Override
//            protected String doInBackground(String... params) {
//
//                try {
//                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
//                    bitmap1 = ((BitmapDrawable) images1.getDrawable()).getBitmap();
//                    bitmap1=Bitmap.createScaledBitmap(bitmap1, width,height, true);
//                    bitmap1 = rotateImage(bitmap1, images1.getRotation());
//                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //compress to which format you want.
//                    byte [] byte_arr1 = stream1.toByteArray();
//                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
//                }catch (Exception e){
//
//                }
//
//                try {
//                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
//                    bitmap2 = ((BitmapDrawable) images2.getDrawable()).getBitmap();
//                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, width,height, true);
//                    bitmap2 = rotateImage(bitmap2, images2.getRotation());
//                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2); //compress to which format you want.
//                    byte [] byte_arr2 = stream2.toByteArray();
//                    image_str2 = Base64.encodeToString(byte_arr2, Base64.DEFAULT);
//                }catch (Exception e){
//
//                }
//
//                HashMap<String,String> data = new HashMap<>();
//                data.put("image1", image_str1);
//                data.put("name1",DocNo+"_pic1");
//                data.put("image2", image_str2);
//                data.put("name2",DocNo+"_pic2");
//                data.put("DocNo",DocNo);
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                String result = null;
//                if (!images1.getDrawable().equals(null)){
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/UploadImageTestresult.php",data);
//                }
//                Log.d("tog_UpImage_T2","data = "+data+"");
//                Log.d("tog_UpImage_T2","result = "+result+"");
//                return  result;
//            }
//        }
//
//        uploadImage_T2 ui = new uploadImage_T2();
//        ui.execute();
//    }

    private void uploadTextImage(){
        class uploadTextImage extends AsyncTask<String,Void,String> {
            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
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
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("finish").equals("true2")){
                            Toast.makeText(InSertImageSporeDocActivity.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(InSertImageSporeDocActivity.this, "บันทึกไม่สำเร็จ", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()){
                        dialog.dismiss();
                    }
                }
            }

            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {

                try {
                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1 = (( BitmapDrawable ) images1.getDrawable()).getBitmap();
                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
                    bitmap1 = rotateImage(bitmap1, images1.getRotation());
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 50, stream1); //compress to which format you want.
                    byte [] byte_arr1 = stream1.toByteArray();
                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
                }catch (Exception e){

                }

                try {
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                    bitmap2 = ((BitmapDrawable) images2.getDrawable()).getBitmap();
                    bitmap2 = Bitmap.createScaledBitmap(bitmap2, width,height, true);
                    bitmap2 = rotateImage(bitmap2, images2.getRotation());
                    bitmap2.compress(Bitmap.CompressFormat.JPEG, 50, stream2); //compress to which format you want.
                    byte [] byte_arr2 = stream2.toByteArray();
                    image_str2 = Base64.encodeToString(byte_arr2, Base64.DEFAULT);
                }catch (Exception e){

                }

                String result = null;
                try {
                    HashMap<String,String> data = new HashMap<>();
                    data.put("image1", image_str1);
                    data.put("name1",DocNo+"_pic1");
                    data.put("image2", image_str2);
                    data.put("name2",DocNo+"_pic2");
                    data.put("DocNo",DocNo);
                    data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                    if (!images1.getDrawable().equals(null)){
                        result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/UploadImage_Text.php",data);
                    }

                    Log.d("tog_UploadImage_Text","data = "+data+"");
                    Log.d("tog_UploadImage_Text","result = "+result+"");
                }catch (Exception e){

                }
                return  result;
            }
        }
        uploadTextImage ui = new uploadTextImage();
        ui.execute();
    }

    public void setSpinnerWash(final String type,final String program) {
        class setSpinnerWash extends AsyncTask<String, Void, String> {
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
//                    array_spinner_id.clear();
                    //list_sp.clear();
                    if (!program.equals("-")){
                        array_spinner_id.put("-","0");
                        list_sp.add("-");
                    }

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        Log.d("tog_program_m1",c.getString("TestProgramName")+"----"+c.getString("ID"));
                        array_spinner_id.put(c.getString("TestProgramName"),c.getString("ID"));
                        if(!c.getString("TestProgramName").equals(program)){
                            list_sp.add(c.getString("TestProgramName"));
                        }
                    }

                    adapter_spinner = new ArrayAdapter<String>(InSertImageSporeDocActivity.this, android.R.layout.simple_spinner_dropdown_item, list_sp);
                    adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    pg_spinner.setAdapter(adapter_spinner);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("type",type);
                data.put("program",program);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_program_m1_cssd_test.php",data);
                Log.d("tog_program_m1","data = "+data);
                Log.d("tog_program_m1","result = "+result);
                return result;
            }
        }
        setSpinnerWash ru = new setSpinnerWash();
        ru.execute(type);
    }

//    public void setSpinnerSterile(final String type,final String program) {
//        class setSpinnerSterile extends AsyncTask<String, Void, String> {
//
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
//                    array_spinnersterile.clear();
//                    array_spinnersterile.add("");
//                    //list_sp1.clear();
//                    for (int i = 0; i < rs.length(); i++) {
//                        JSONObject c = rs.getJSONObject(i);
//                        list_sp1.add(c.getString("TestProgramName"));
//                    }
//                    if (!program.equals("-")){
//                        list_sp1.add("-");
//                    }
//                    adapter_spinner1 = new ArrayAdapter<String>(InSertImageSporeDocActivity.this, android.R.layout.simple_spinner_dropdown_item, list_sp1);
//                    adapter_spinner1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                    pg_spinner1.setAdapter(adapter_spinner1);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String, String>();
//                data.put("type",type);
//                data.put("program",program);
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_program_m1_cssd_test.php",data);
//
//                Log.d("tog_program_m1","data = "+data);
//                Log.d("tog_program_m1","result = "+result);
//                return result;
//            }
//        }
//        setSpinnerSterile ru = new setSpinnerSterile();
//        ru.execute(type);
//    }

    public void _gotoPage(Class gt){
        Intent intent = new Intent(getApplication(), gt);
        startActivity(intent);
        finish();
    }

    public void ShowDetail() {
        class ShowDetail extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
            // variable
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

                    for(int i=0;i<rs.length();i++) {
                        JSONObject c = rs.getJSONObject(i);

                        imageUrl1 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/uploads/"+c.getString("Pic1"));
                        imageUrl2 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/uploads/"+c.getString("Pic2"));

                        PicText1 = c.getString("img_pic1");
                        PicText2 = c.getString("img_pic2");

//                        Picasso.get().load(String.valueOf(imageUrl)).networkPolicy(NetworkPolicy.NO_CACHE)
//                                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                .into(images1);
//
//                        Picasso.get().load(String.valueOf(imageUrl1)).networkPolicy(NetworkPolicy.NO_CACHE)
//                                .memoryPolicy(MemoryPolicy.NO_CACHE)
//                                .into(images2);
//
                        try {
                            URL url = new URL(imageUrl1.toString());
                            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                            images1.setImageBitmap(bmp);
                        }catch(Exception e){
                            codeData_pic1 = "data:image/jpeg;base64,"+PicText1;
                            codeData_pic1 = codeData_pic1.replace("data:image/jpeg;base64,","");
                            byte[] code_pic1 = Base64.decode(codeData_pic1,Base64.DEFAULT);
                            Bitmap bitmap_pic1 = BitmapFactory.decodeByteArray(code_pic1,0,code_pic1.length);
                            images1.setImageBitmap(bitmap_pic1);
                        }

                        try {
                            URL url1 = new URL(imageUrl2.toString());
                            Bitmap bmp1 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
                            images2.setImageBitmap(bmp1);
                        }catch(Exception e){
                            codeData_pic2 = "data:image/jpeg;base64,"+PicText2;
                            codeData_pic2 = codeData_pic2.replace("data:image/jpeg;base64,","");
                            byte[] code_pic2 = Base64.decode(codeData_pic2,Base64.DEFAULT);
                            Bitmap bitmap_pic2 = BitmapFactory.decodeByteArray(code_pic2,0,code_pic2.length);
                            images2.setImageBitmap(bitmap_pic2);
                        }


                        remark.setText(c.getString("Remark"));

                        count = c.getString("ProgramTest");

                        testremark.setText(c.getString("Point_test"));

                        list_sp.add(c.getString("ProgramTest"));
                        adapter_spinner = new ArrayAdapter<String>(InSertImageSporeDocActivity.this, android.R.layout.simple_spinner_dropdown_item, list_sp);
                        adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        pg_spinner.setAdapter(adapter_spinner);

                        if (!c.getString("IsResultTest").equals("1")){
                            testno.setChecked(true);
                        }else {
                            testyes.setChecked(true);
                        }

                        testremark.setEnabled(false);
                        remark.setEnabled(false);
                        pg_spinner.setEnabled(false);
                        testyes.setEnabled(false);
                        testno.setEnabled(false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("B_ID",B_ID);
                data.put("DocNo",DocNo);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_detail_test.php", data);
                    Log.d("FDLKD",data+"");
                    Log.d("FDLKD",result);
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

    public void ShowRound() {
        class ShowRound extends AsyncTask<String, Void, String> {
            // variable
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

                        round.setText("เครื่อง/รอบ : "+c.getString("MachineID")+"/"+c.getString("RoundNumber"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("B_ID",B_ID);
                data.put("DocNo",DocNo);
                data.put("Page",Page);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_round_mac.php", data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("display_round_mac","data = "+data);
                Log.d("display_round_mac","result = "+result);
                return result;
            }
            // =========================================================================================
        }

        ShowRound obj = new ShowRound();
        obj.execute();
    }

    public void ShowATP() {
        class ShowATP extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
                this.dialog.dismiss();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                check_code.setChecked(false);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++) {
                        JSONObject c = rs.getJSONObject(i);

                        IsATP = c.getString("IsATP");

                        if (!IsATP.equals("0")){
                            check_code.setChecked(true);
                        }else {
                            check_code.setChecked(false);
                        }

                        switch (c.getInt("CountAtp"))
                        {
                            case 1 :
                                setShowATP(c.getString("ATP_Code"),code,text1);
                                break;
                            case 2 :
                                setShowATP(c.getString("ATP_Code"),code1,text2);
                                break;
                            case 3 :
                                setShowATP(c.getString("ATP_Code"),code2,text3);
                                break;
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("B_ID",B_ID);
                data.put("DocNo",DocNo);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_detail_test_1.php", data);
                    Log.d("tog_atp",result);
                    Log.d("tog_atp",data+"");
                }catch(Exception e){
                    e.printStackTrace();
                }

                return result;
            }
            // =========================================================================================
        }

        ShowATP obj = new ShowATP();
        obj.execute();
    }

    public void setShowATP(String ATP_Code,EditText code,TextView textAtp){

        code.setVisibility(View.VISIBLE);
        textAtp.setVisibility(View.VISIBLE);
        code.setText(ATP_Code);

        code.setEnabled(false);
    }

//    public void ShowATP1() {
//        class ShowATP1 extends AsyncTask<String, Void, String> {
//            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
//            // variable
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                this.dialog.setTitle(Cons.WAIT_FOR_PROCESS);
//                this.dialog.show();
//                this.dialog.dismiss();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for(int i=0;i<rs.length();i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        IsATP = c.getString("IsATP");
//
//                        if (!IsATP.equals("0")){
//                            check_code.setChecked(true);
//                            //check_code.setEnabled(false);
//                        }else {
//                            check_code.setChecked(false);
//                        }
//
//                        if (!c.getString("ATP_Code").equals(0)){
//                            code1.setVisibility(View.VISIBLE);
//                            text2.setVisibility(View.VISIBLE);
//                            code1.setText(c.getString("ATP_Code"));
//                        }else {
//                            code1.setVisibility(View.GONE);
//                            text2.setVisibility(View.GONE);
//                            code1.setText("");
//                        }
//                        code1.setEnabled(false);
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
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_detail_test_2.php", data);
//                    Log.d("BFGDH",result);
//                    Log.d("BFGDH",data+"");
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//            // =========================================================================================
//        }
//
//        ShowATP1 obj = new ShowATP1();
//        obj.execute();
//    }

//    public void ShowATP2() {
//        class ShowATP2 extends AsyncTask<String, Void, String> {
//            private ProgressDialog dialog = new ProgressDialog(InSertImageSporeDocActivity.this);
//            // variable
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                this.dialog.setTitle(Cons.WAIT_FOR_PROCESS);
//                this.dialog.show();
//                this.dialog.dismiss();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for(int i=0;i<rs.length();i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        IsATP = c.getString("IsATP");
//
//                        if (!IsATP.equals("0")){
//                            check_code.setChecked(true);
//                            //check_code.setEnabled(false);
//                        }else {
//                            check_code.setChecked(false);
//                        }
//
//                        if (!c.getString("ATP_Code").equals(0)){
//                            code2.setVisibility(View.VISIBLE);
//                            text3.setVisibility(View.VISIBLE);
//                            code2.setText(c.getString("ATP_Code"));
//                        }else {
//                            code2.setVisibility(View.GONE);
//                            text3.setVisibility(View.GONE);
//                            code2.setText("");
//                        }
//                        code2.setEnabled(false);
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
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_detail_test_3.php", data);
//                    Log.d("BFGDH",result);
//                    Log.d("BFGDH",data+"");
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//            // =========================================================================================
//        }
//
//        ShowATP2 obj = new ShowATP2();
//        obj.execute();
//    }

    public void CheckSaveATP() {
        class CheckSaveATP extends AsyncTask<String, Void, String> {
            // variable
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
                        countatp = c.getString("IsATP");
                        countatpint = Integer.parseInt(countatp);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("B_ID",B_ID);
                data.put("DocNo",DocNo);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_count_atp.php", data);
                    Log.d("BFGDH",result);
                    Log.d("BFGDH",data+"");
                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        CheckSaveATP obj = new CheckSaveATP();
        obj.execute();
    }

    public void open_pic(String numpic){
        Bitmap bitmap_pic1;
        try {
            URL url = new URL(imageUrl1.toString());
            bitmap_pic1 = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            images1.setImageBitmap(bitmap_pic1);
        }catch(Exception e){
            codeData_pic1 = "data:image/jpeg;base64,"+PicText1;
            codeData_pic1 = codeData_pic1.replace("data:image/jpeg;base64,","");
            byte[] code_pic1 = Base64.decode(codeData_pic1,Base64.DEFAULT);
            bitmap_pic1 = BitmapFactory.decodeByteArray(code_pic1,0,code_pic1.length);
            images1.setImageBitmap(bitmap_pic1);
        }
        Bitmap bitmap_pic2;
        try {
            URL url1 = new URL(imageUrl2.toString());
            bitmap_pic2 = BitmapFactory.decodeStream(url1.openConnection().getInputStream());
            images2.setImageBitmap(bitmap_pic2);
        }catch(Exception e){
            codeData_pic2 = "data:image/jpeg;base64,"+PicText2;
            codeData_pic2 = codeData_pic2.replace("data:image/jpeg;base64,","");
            byte[] code_pic2 = Base64.decode(codeData_pic2,Base64.DEFAULT);
            bitmap_pic2 = BitmapFactory.decodeByteArray(code_pic2,0,code_pic2.length);
            images2.setImageBitmap(bitmap_pic2);
        }


        AlertDialog.Builder quitDialog = new AlertDialog.Builder(InSertImageSporeDocActivity.this);
        final View customLayout = InSertImageSporeDocActivity.this.getLayoutInflater().inflate( R.layout.activity_dialog_pic_remark, null);
        quitDialog.setView(customLayout);

        ViewPager2 al_imageslide = (ViewPager2) customLayout.findViewById(R.id.imageslide);

        ImageView al_images = (ImageView) customLayout.findViewById(R.id.images);
        TextView text_remark = (TextView) customLayout.findViewById(R.id.text_remark);
        text_remark.setVisibility(View.GONE);

        if(numpic.equals("1")){
            al_images.setImageBitmap(bitmap_pic1);
            al_imageslide.setVisibility(View.GONE);
        }else {
            al_images.setImageBitmap(bitmap_pic2);
            al_imageslide.setVisibility(View.GONE);
        }

//        Log.d("image2",""+bitmap_pic1);

        AlertDialog alert = quitDialog.create();

//        al_imageslide.setItemClickListener(new ItemClickListener() {
//            @SuppressLint("ResourceType")
//            @Override
//            public void onItemSelected(int i) {
//                String TitlePic = slideModels.get(i).getTitle();
//                if (TitlePic.equals("รูปที่1")){
//                    if (DATA_MODEL.get(position).getPicture().equals("") || DATA_MODEL.get(position).getPicture().equals("null")){
//                        OpenCamera(DATA_MODEL.get(position).getID(),DATA_MODEL.get(position).getSensterileDocNo(),"1");
//                        alert.dismiss();
//                    }
//                }
//                else if (TitlePic.equals("รูปที่2")){
//                    if (DATA_MODEL.get(position).getPicture2().equals("") || DATA_MODEL.get(position).getPicture2().equals("null")){
//                        OpenCamera(DATA_MODEL.get(position).getID(),DATA_MODEL.get(position).getSensterileDocNo(),"2");
//                        alert.dismiss();
//                    }
//                }
//
//            }
//        });
        alert.show();
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

    protected Bitmap loadImage(String utl2) {
        // TODO Auto-generated method stub

        Log.v("utl2--", utl2);
        URL imageURL = null;

        Bitmap bitmap = null;
        try {
            imageURL = new URL(utl2);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) imageURL
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onBackPressed() {

        finish();
    }
}

