package com.poseintelligence.cssdm1;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.poseintelligence.cssdm1.adapter.ListSendSterileAdapter;
import com.poseintelligence.cssdm1.adapter.ListSendSterileDetailAdapter;
import com.poseintelligence.cssdm1.adapter.ListSendSterileDetailSetAdapter;
import com.poseintelligence.cssdm1.core.audio.iAudio;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.data.Master;
import com.poseintelligence.cssdm1.model.pCustomer;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class ReceiveActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    private iAudio nMidia;
    // Session Variable
    private String getUrl="";
    final int mode = 1;
    private Intent intent;
    private String B_ID = null;
    private boolean IsAdmin;

    private String userid = "";
    private String EmpCode = "0";

    // ---------------------------------------------------------------------------------------------
    // Local Variable
    // ---------------------------------------------------------------------------------------------

    private int index = -1;
    private Calendar myCalendar = Calendar.getInstance();
    private String DocNo = "";
    private String IsStatus = "";
    //private String mode = "1";

    private String dept_id = "";
    private String dept_id_search = "";

    //private String usr_send_id = "";
    private String usr_send_old_id = "";
    //private String usr_receive_id = "";
    private String usr_receive_old_id = "";
    private String Usagecode = "";

    private boolean IsItemClick = false;
    private boolean B_IsSearch = false;
    private boolean IsSU;
    private HashMap<String, String> data_user_send_id = new HashMap<String,String>();
    private HashMap<String, String> data_user_receive_id = new HashMap<String,String>();
    private ArrayList<pCustomer> model_send_sterile_detail = new ArrayList<>();
    private ArrayList<pCustomer> model_send_sterile = new ArrayList<>();
    private HashMap<String, String> data_zone_id = new HashMap<String,String>();

    private ArrayList<String> ar_list_user_send_id = new ArrayList<String>();
    private ArrayList<String> ar_list_user_receive_id = new ArrayList<String>();
    private ArrayList<String> ar_list_dept_id = new ArrayList<String>();
    private ArrayList<String> ar_list_dept_name = new ArrayList<String>();
    private ArrayList<String> ar_list_zone = new ArrayList<String>();

    ArrayList<pCustomer> item_doc_detail_set = new ArrayList<pCustomer>();

    private ArrayAdapter<String> adapter_spinner;
    private boolean SS_IsShowToastDialog = true;

    // ---------------------------------------------------------------------------------------------
    // Widget
    // ---------------------------------------------------------------------------------------------

    private EditText edt_usage_code;
    private EditText edt_doc_no_search;

    private ImageView btn_form_search;
    private ImageView img_back_1;
    private ImageView img_back_2;
    private ImageView btn_search_doc;
    private ImageView Create_Doc;
    private ImageView btn_complete;
    private ImageView imageDocument;
    private ImageView img_re_sterile;
    private ImageView imageReport;
    private ImageView btn_doc;
    private ImageView btn_add_item;
    private ImageView btn_new;

    private Button btn_remove;
    private Button btn_approve;

    private TextView txt_title;
    private TextView txt_doc_no;
    private TextView txt_usr_receive;
    private TextView txt_doc_date;
    private TextView txt_doc_time;
    private TextView txt_count_list;
    private TextView txt_doc_date_search;
    private TextView txt_label_usr_send;
    private TextView txt_label_department;
    private TextView txt_label_zone;
    private TextView txt_basket_lable;
    private TextView lable_qty;
    private TextView lable_unit;

    private SearchableSpinner spn_department_form;
    private SearchableSpinner spn_usr_send;
    private SearchableSpinner spn_usr_receive;
    private SearchableSpinner spn_zone;
    private SearchableSpinner spn_department_search;

    private Switch switch_status;
    private Switch switch_non_select_department;
    private Switch switch_washdep;

    private ListView list_doc_detail_set;
    private ListView list_doc_detail;
    private ListView list_doc;

    private TabLayout tabs;

    private LinearLayout Block_1;
    private LinearLayout Block_2;

    // ---------------------------------------------------------------------------------------------
    // Config
    // ---------------------------------------------------------------------------------------------

    public boolean SS_IsCopyPayout = false;
    public boolean SS_IsShowSender = false;
    public boolean SS_IsReceiverDropdown = false;
    public boolean SS_IsApprove = false;
    public boolean SS_IsUsedReceiveTime = false;
    public boolean SS_IsNonSelectDepartment = false;
    public boolean SS_IsGroupPayout = false;
    public boolean SS_IsSortByUsedCount = false;
    public boolean SS_IsUsedZoneSterile = false;
    public boolean SS_IsUsedBasket = false;
    public boolean SS_IsUsedNotification = false;
    public boolean SS_IsUsedRemarks = false;
    public boolean SS_IsUsedSelfWashDepartment = false;
    public boolean SS_IsUsedClosePayout = false;
    public boolean SS_IsUsedChangeDepartment = false;

    public boolean MD_IsUsedSoundScanQR = false;

    public boolean WA_IsUsedWash = false;

    public boolean ST_IsUsedNotification = false;
    // ---------------------------------------------------------------------------------------------

    private TimePickerDialog picker;

    // ---------------------------------------------------------------------------------------------
    // basket value end
    // ---------------------------------------------------------------------------------------------

    final Handler handler_1 = new Handler();
    private Runnable runnable_1;

    String S_condition1 = "";
    String S_condition2 = "";
    String S_condition3 = "";
    String S_condition4 = "";
    String S_condition5 = "";
    String S_condition6 = "";
    String S_Qty;
    String S_RowId = "";

    private boolean DIALOG_ACTIVE = false;
    private boolean Switch_Mode = false;

    HashMap<String, String> Usage_Nowash = new HashMap<String,String>();
    public ArrayList<String> usage_nowash_id = new ArrayList<String>();

    int tab_index = 0;

    final DatePickerDialog.OnDateSetListener date1 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            onChangeDate();
        }

    };

    final DatePickerDialog.OnDateSetListener date2 = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            onChangeDateSearch();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        getSupportActionBar().hide();

        // Permission StrictMode
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        getSupportActionBar().hide();

        // -----------------------------------------------------------------------
        // Sound
        // -----------------------------------------------------------------------
        nMidia = new iAudio(this);
        // -----------------------------------------------------------------------

        byIntent();

        byWidget();

        byEvent();

        byConfig();

        // define

        defineUser();
        defineDepartment("x");

        if(SS_IsUsedZoneSterile){
//            defineZone();
        }


        edt_usage_code.requestFocus();

        runnable_1 = new Runnable() {

            @Override
            public void run() {
                B_IsSearch = true;

                handler_1.removeCallbacks(runnable_1);
            }
        };

        handler_1.postDelayed(runnable_1, 999);

        focus();
    }

    public void defineDepartment(final String str) {
        class DefineDepartment extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    ar_list_dept_id.clear();
                    ar_list_dept_id.add("เลือกแผนก");
                    ar_list_dept_name.add("เลือกแผนก");

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);
                        ar_list_dept_name.add(c.getString("xName"));
                        ar_list_dept_id.add(c.getString("xID"));
                    }

                    adapter_spinner = new ArrayAdapter<String>(ReceiveActivity.this, android.R.layout.simple_spinner_dropdown_item, ar_list_dept_name);
                    adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spn_department_form.setAdapter(adapter_spinner);
                    spn_department_search.setAdapter(adapter_spinner);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("B_ID",B_ID);
                Log.d("OOOO",getUrl + "cssd_select_department_by_send_sterile.php?"+ data);
                String result = httpConnect.sendPostRequest(getUrl + "cssd_select_department_by_send_sterile.php", data);
                Log.d("OOOO","Result : "+result);
                return result;
            }
        }
        DefineDepartment ru = new DefineDepartment();
        ru.execute();
    }

    public void ok()  {
        if(MD_IsUsedSoundScanQR) {
            nMidia.getAudio("okay");
        }
    }

    public void no()  {
        if(MD_IsUsedSoundScanQR) {
            nMidia.getAudio("no");
        }
    }

    public void byIntent() {
        getUrl = ((CssdProject) getApplication()).getxUrl();
        userid = ((CssdProject) getApplication()).getPm().getUserid()+"";
        IsAdmin = ((CssdProject) getApplication()).getPm().getIsAdmin();
        EmpCode = ((CssdProject) getApplication()).getPm().getEmCode()+"";
        B_ID = ((CssdProject) getApplication()).getPm().getBdCode()+"";
        IsSU = ((CssdProject) getApplication()).getPm().getIsSU();
    }

    public void byWidget() {
        IsStatus = "0";
        Block_1 = ( LinearLayout ) findViewById(R.id.Block_1);
        Block_2 = ( LinearLayout ) findViewById(R.id.Block_2);
        // ===============================
        // Block 1
        // ===============================
        edt_usage_code = ( EditText ) findViewById(R.id.edt_usage_code);
        edt_doc_no_search = ( EditText ) findViewById(R.id.edt_doc_no_search);

        txt_doc_date_search = ( TextView ) findViewById(R.id.txt_doc_date_search);

        spn_department_form = ( SearchableSpinner ) findViewById(R.id.spn_department_form);
        spn_department_search = ( SearchableSpinner ) findViewById(R.id.spn_department_search);

        switch_status = ( Switch ) findViewById(R.id.switch_status);
        switch_washdep = ( Switch ) findViewById(R.id.switch_washdep);

        Create_Doc = ( ImageView ) findViewById(R.id.Create_Doc);
        img_back_1 = ( ImageView ) findViewById(R.id.img_back_1);
        img_back_2 = ( ImageView ) findViewById(R.id.img_back_2);
        btn_search_doc = ( ImageView ) findViewById(R.id.btn_search_doc);
        btn_form_search = ( ImageView ) findViewById(R.id.btn_form_search);
        list_doc = ( ListView ) findViewById(R.id.list_doc);
        // ===============================
        // Block 2
        // ===============================
        txt_title = ( TextView ) findViewById(R.id.txt_title);
        txt_doc_date = ( TextView ) findViewById(R.id.txt_doc_date);
        txt_doc_time = ( TextView ) findViewById(R.id.txt_doc_time);
        txt_count_list = ( TextView ) findViewById(R.id.txt_count_list);
        btn_doc = ( ImageView ) findViewById(R.id.btn_doc);
        btn_add_item = ( ImageView ) findViewById(R.id.btn_add_item);
        btn_new = ( ImageView ) findViewById(R.id.btn_new);
        btn_complete = ( ImageView ) findViewById(R.id.btn_complete);
        spn_usr_receive = ( SearchableSpinner ) findViewById(R.id.spn_usr_receive);
        list_doc_detail = ( ListView ) findViewById(R.id.list_doc_detail);
        switch_non_select_department = ( Switch ) findViewById(R.id.switch_non_select_department);
    }

    public void byConfig() {

        // -----------------------------------------------------------------------------------------
        // Get Config
        // -----------------------------------------------------------------------------------------

        SS_IsGroupPayout = ((CssdProject) getApplication()).isSS_IsGroupPayout();
        SS_IsCopyPayout = ((CssdProject) getApplication()).isSS_IsCopyPayout();
        SS_IsShowSender = ((CssdProject) getApplication()).isSS_IsShowSender();
        SS_IsReceiverDropdown = ((CssdProject) getApplication()).isSS_IsReceiverDropdown();
        SS_IsApprove = ((CssdProject) getApplication()).isSS_IsApprove();
        SS_IsUsedReceiveTime = ((CssdProject) getApplication()).isSS_IsUsedReceiveTime();
        SS_IsNonSelectDepartment = ((CssdProject) getApplication()).isSS_IsNonSelectDepartment();
        SS_IsSortByUsedCount = ((CssdProject) getApplication()).isSS_IsSortByUsedCount();
        SS_IsUsedZoneSterile = ((CssdProject) getApplication()).isSS_IsUsedZoneSterile();
        SS_IsUsedBasket = ((CssdProject) getApplication()).isSS_IsUsedBasket();
        SS_IsUsedNotification = ((CssdProject) getApplication()).isSS_IsUsedNotification();
        SS_IsUsedRemarks = ((CssdProject) getApplication()).isSS_IsUsedRemarks();
        SS_IsUsedSelfWashDepartment = ((CssdProject) getApplication()).isSS_IsUsedSelfWashDepartment();
        SS_IsUsedClosePayout = ((CssdProject) getApplication()).isSS_IsUsedClosePayout();
        SS_IsUsedChangeDepartment = ((CssdProject) getApplication()).isSS_IsUsedChangeDepartment();

        MD_IsUsedSoundScanQR = ((CssdProject) getApplication()).isMD_IsUsedSoundScanQR();

        WA_IsUsedWash = ((CssdProject) getApplication()).isWA_IsUsedWash();

        ST_IsUsedNotification = ((CssdProject) getApplication()).isST_IsUsedNotification();
    }

    public void byEvent() {
        Block_1.setVisibility( View.VISIBLE );
        Block_2.setVisibility( View.GONE );
        txt_title.setText("บันทึกรับ");

        spn_department_search.setTitle("เลือก");
        spn_department_search.setPositiveButton("ปิด");
        spn_department_form.setTitle("เลือก");
        spn_department_form.setPositiveButton("ปิด");
        spn_usr_receive.setTitle("เลือกู้รับ(จ่ายกลาง)");
        spn_usr_receive.setPositiveButton("ปิด");

        onChangeDateSearch();
        // ===============================
        // Block 1
        // ===============================
        img_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(ReceiveActivity.this,MainMenu.class);
                startActivity(intent);
                finish();
            }
        });

        btn_search_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ReceiveActivity.this, date2, myCalendar .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        spn_department_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dept_id_search = ar_list_dept_id.get(position);

                displaySendSterile(false, null, 8);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spn_department_form.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(B_IsSearch) {

                    if (!IsItemClick) {

                        dept_id = ar_list_dept_id.get(position);

                        //clearDoc();
                    }

                    IsItemClick = false;

                    edt_usage_code.requestFocus();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btn_form_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method
                displaySendSterile(false, null, 2);
            }
        });

        switch_status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                displaySendSterile(false, null,3 );
            }
        });


        list_doc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {

                try {
                    Object o = list_doc.getItemAtPosition(position);

                    index = position;

                    pCustomer d = (pCustomer) o;

                    try {
                        spn_department_form.setEnabled(d.getIsWeb().equals("0"));
                    }catch (Exception e){

                    }

//                    lable_qty.setText("");
//                    lable_unit.setText("");

                    try {

                        String S_DocNo = d.getDocno().substring(0, 2);

                        // Show and Hide department
                        showDepartmentForm(S_DocNo.equals("SS"));

                        /*
                        if(S_DocNo.equals("NA")) {
                            spn_department_form.setVisibility(View.INVISIBLE);
                            txt_label_department.setVisibility(View.INVISIBLE);
                        }else{
                            spn_department_form.setVisibility(View.VISIBLE);
                            txt_label_department.setVisibility(View.VISIBLE);
                        }
                        */

                        if(!switch_non_select_department.isChecked() && S_DocNo.equals("NA")) {
                            switch_non_select_department.setChecked(true);
                            return;
                        }else if(S_DocNo.equals("NA")){

                        }else{
                            switch_non_select_department.setChecked(false);
                        }
                    }catch (Exception e){

                    }

                    //spn_department_form.setEnabled(false);

                    // Display Form
                    displaySendSterile(
                            d.getDocno(),
                            d.getDocdate(),
                            d.getTime(),
                            d.getDept(),
                            d.getUsr_receive(),
                            d.getUserReceive(),
                            d.getUserSend(),
                            d.getIsStatus() != null && d.getIsStatus().equals("2")
                    );

                    // Display List Detail
                    displaySendSterileDetail(d.getDocno());

                    CheckStatusDocNo(d.getDocno());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        Create_Doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method
                Block_1.setVisibility( View.GONE );
                Block_2.setVisibility( View.VISIBLE );

                IsStatus = "0";

                defineDocDate();

                dept_id = "";
                DocNo = "";
                txt_count_list.setText("จำนวน 0 รายการ");

//                if(SS_IsNonSelectDepartment){
//                    switch_non_select_department.setText("ไม่เลือกแผนก");
//                    switch_non_select_department.setChecked(true);
//                }else{
//
//                }
//
//                lable_qty.setText("");
//                lable_unit.setText("");
//
//                txt_usr_receive.setText("");
//                txt_usr_receive.setContentDescription(null);
//
                spn_department_form.setSelection(0);
//
//                spn_usr_send.setSelection(0);
                spn_usr_receive.setSelection(0);
//
                list_doc_detail.setAdapter(null);
//
//                if(SS_IsUsedZoneSterile) {
//                    spn_zone.setSelection(0);
//                }

//                displaySendSterile(false, null, 0);

            }
        });

        // ===============================
        // Block 2
        // ===============================
        img_back_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Block_1.setVisibility( View.VISIBLE );
                Block_2.setVisibility( View.GONE );
            }
        });

        txt_doc_date.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ReceiveActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btn_doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ReceiveActivity.this, date1, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        txt_doc_time.setInputType(InputType.TYPE_NULL);
        txt_doc_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int hour;
                int minutes;
                String time = txt_doc_time.getText().toString();

                if(time.equals("")){
                    final Calendar cldr = Calendar.getInstance();
                    hour = cldr.get(Calendar.HOUR_OF_DAY);
                    minutes = cldr.get(Calendar.MINUTE);
                }else{
                    hour = Integer.valueOf(time.substring(0,2)).intValue();
                    minutes = Integer.valueOf(time.substring(3,5)).intValue();
                }

                // time picker dialog
                picker = new TimePickerDialog(ReceiveActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                try {
                                    txt_doc_time.setText( (sHour < 10 ? ("0"+sHour) : sHour ) + ":" + (sMinute < 10 ? ("0"+sMinute) : sMinute));

                                    String d = txt_doc_date.getText().toString();

                                    updateSendSterile("DocDate", d.substring(6, 10) + "-" + d.substring(3, 5) + "-" + d.substring(0, 2) + " " + sHour + ":" + sMinute + ":00", DocNo);

                                }catch (Exception e){

                                }
                            }
                        }, hour, minutes, true);
                picker.show();
            }
        });

        // User Receive
        spn_usr_receive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(DocNo.equals("")){
                    Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เพิ่มรายการ กรุณาเพิ่มรายการ", Toast.LENGTH_SHORT).show();

                }else{

                    usr_receive_old_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());

                    if(usr_receive_old_id == null){
                        usr_receive_old_id ="";
                    }

                    spn_usr_receive.onTouch(v, event);
                }
                return true;
            }
        });

        spn_usr_receive.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(position!=0){

                    try {
                        String emp_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());

                        updateSendSterile(Integer.toString(Master.user_receive), emp_id, DocNo);

//                        if(!switch_non_select_department.isChecked() && model_send_sterile.size() > 0 && index > -1) {
//                            model_send_sterile.get(index).setUserReceive(emp_id);
//                        }

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch_washdep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (switch_washdep.isChecked()){
                    Switch_Mode = true;
                    displaySendSterile(false, null, 0);
                }else {
                    Switch_Mode = false;
                    displaySendSterile(false, null, 0);
                }
            }
        });

        edt_usage_code.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            String S_Code = edt_usage_code.getText().toString().toLowerCase();
                            Log.d("OOOO","S_Code:"+S_Code);
//                            Log.d("OOOO","IsSU:"+IsSU);
                            if(IsSU){
//                                Log.d("OOOO","IsSU1:"+IsSU);
                                addUsageForRemoveByQR(S_Code);
                            }else {
                                Log.d("OOOO","dept_id:"+dept_id);
//                                addSterileDetailByQR(S_Code, dept_id);

                                if (S_Code.equals("clear")) {
                                    Log.d("OOOO","Mode:1");
                                    clearByQR(S_Code);

                                }else if (S_Code.equals("save")) {
                                    Log.d("OOOO","Mode:2");
                                    edt_usage_code.setText("");
                                    edt_usage_code.requestFocus();

                                    int cnt = 0;
                                    for (String key : Usage_Nowash.keySet()) {
                                        if (Usage_Nowash.get(key) == "0")
                                            cnt++;
                                    }

                                    if (Switch_Mode){
                                        Log.d("OOOO","Switch_Mode:1");
                                        for (String key : Usage_Nowash.keySet()) {
                                            if (Usage_Nowash.get(key) == "0")
                                                usage_nowash_id.add(key);
                                        }

                                        if (!DocNo.equals("")) {

                                            if (!IsStatus.equals("0")) {

                                                Toast.makeText(ReceiveActivity.this, "เอกสารนี้ได้ถูกปิดแล้ว !!", Toast.LENGTH_SHORT).show();

                                            }else {
                                                String send_id = null;
                                                String receive_id = null;
                                                String zone_id = null;

                                                // Check Sender
                                                if(SS_IsShowSender){

                                                    if(!spn_usr_send.getSelectedItem().equals("")) {

                                                        send_id = data_user_send_id.get(spn_usr_send.getSelectedItem());

                                                    }else{

                                                        Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้ส่ง !! (แผนก)", Toast.LENGTH_SHORT).show();

                                                    }
                                                }else{

                                                    send_id = userid;

                                                }

                                                // Check Receiver
                                                if(SS_IsReceiverDropdown){

                                                    if(!spn_usr_receive.getSelectedItem().equals("")) {
                                                        receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());
                                                    }

                                                }else{

                                                    if (!txt_usr_receive.getText().toString().trim().equals("")) {
                                                        receive_id = txt_usr_receive.getContentDescription().toString();
                                                    }

                                                }

                                                // Check Zone
                                                if(SS_IsUsedZoneSterile){

                                                    if(!spn_zone.getSelectedItem().equals("")) {
                                                        zone_id = data_zone_id.get(spn_zone.getSelectedItem());
                                                    }

                                                }

                                                if(receive_id == null){
                                                    Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้รับ !! (จ่ายกลาง)", Toast.LENGTH_SHORT).show();
                                                }

                                                // Save Complete
                                                onSaveNowash(DocNo, send_id, receive_id, zone_id, String.valueOf(usage_nowash_id));

                                            }

                                        } else {
                                            Toast.makeText(ReceiveActivity.this, "ไม่มีเอกสารให้บันทึก", Toast.LENGTH_SHORT).show();
                                        }
                                    }else {
                                        Log.d("OOOO","Switch_Mode:2");
                                        if (!DocNo.equals("")) {

                                            // Check Status
                                            if (!IsStatus.equals("0")) {

                                                Toast.makeText(ReceiveActivity.this, "เอกสารนี้ได้ถูกปิดแล้ว !!", Toast.LENGTH_SHORT).show();

                                            }else {

                                                String send_id = null;
                                                String receive_id = null;
                                                String zone_id = null;

                                                // Check Sender
                                                if(SS_IsShowSender){

                                                    if(!spn_usr_send.getSelectedItem().equals("")) {

                                                        send_id = data_user_send_id.get(spn_usr_send.getSelectedItem());

                                                    }else{

                                                        Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้ส่ง !! (แผนก)", Toast.LENGTH_SHORT).show();

                                                    }
                                                }else{

                                                    send_id = userid;

                                                }

                                                // Check Receiver
                                                if(SS_IsReceiverDropdown){

                                                    if(!spn_usr_receive.getSelectedItem().equals("")) {
                                                        receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());
                                                    }

                                                }else{

                                                    if (!txt_usr_receive.getText().toString().trim().equals("")) {
                                                        receive_id = txt_usr_receive.getContentDescription().toString();
                                                    }

                                                }

                                                // Check Zone
                                                if(SS_IsUsedZoneSterile){

                                                    if(!spn_zone.getSelectedItem().equals("")) {
                                                        zone_id = data_zone_id.get(spn_zone.getSelectedItem());
                                                    }

                                                }

                                                if(receive_id == null){

                                                    Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้รับ !! (จ่ายกลาง)", Toast.LENGTH_SHORT).show();

                                                }

                                                // Save Complete
                                                onSave(DocNo, send_id, receive_id, zone_id);

                                            }

                                        } else {
                                            Toast.makeText(ReceiveActivity.this, "ไม่มีเอกสารให้บันทึก", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                }else if (SS_IsUsedZoneSterile && S_Code.length() > 4 && S_Code.substring(0, 4).toLowerCase().equals("zone")) {
                                    Log.d("OOOO","Mode:3");
                                    int index = 0;

                                    index = Integer.valueOf(S_Code.substring(4)).intValue();

//                                    spn_zone.setSelection(index);
//
//                                    setZone(String.valueOf(index));

                                } else if ( switch_non_select_department.isChecked()) {
                                    Log.d("OOOO","Mode:4");
                                    dept_id = "-1";
                                    String S_em = edt_usage_code.getText().toString().substring(0, 2);
                                    if (S_em.toUpperCase().equals("EM")){
                                        if (spn_usr_receive.getSelectedItemPosition() == 0){
                                            addEmpolyeeByQR(S_Code, dept_id, DocNo, "1");
                                        }else {
                                            addEmpolyeeByQR(S_Code, dept_id, DocNo, "0");
                                        }
                                    }else {
                                        addSterileDetailByQR(S_Code, dept_id);
                                    }


                                } else if ( !dept_id.equals("") && !dept_id.equals("0") ) {
                                    Log.d("OOOO","Mode:5");
                                    String S_em = edt_usage_code.getText().toString().substring(0, 2);
                                    if (S_em.toUpperCase().equals("EM")){
                                        if (spn_usr_receive.getSelectedItemPosition() == 0){
                                            addEmpolyeeByQR(S_Code, dept_id, DocNo, "1");
                                        }else {
                                            addEmpolyeeByQR(S_Code, dept_id, DocNo, "0");
                                        }
                                    }else {
                                        addSterileDetailByQR(S_Code, dept_id);
                                    }

                                } else {
                                    Log.d("OOOO","Mode:6");
                                    findDepartmentByQR(S_Code);
                                }
                            }

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }

        });

        btn_add_item.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if(IsStatus != null && !IsStatus.equals("0")){
                    Toast.makeText(ReceiveActivity.this, "ไม่สามารถเพิ่มรายการได้ !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO Auto-generated method
                if (switch_non_select_department.isChecked() || ( !dept_id.equals("") && !dept_id.equals("0") ) ) {
                    openSearchItemStock("1");
                } else {
                    Toast.makeText(ReceiveActivity.this, "กรุณาเลือกแผนก !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_new.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                IsStatus = "0";

                defineDocDate();

                dept_id = "";

                if(SS_IsNonSelectDepartment){
                    switch_non_select_department.setText("ไม่เลือกแผนก");
                    switch_non_select_department.setChecked(true);
                }else{

                }

                txt_title.setText("บันทึกรับ");
                txt_count_list.setText("จำนวน 0 รายการ");
//                lable_unit.setText("");
//
//                txt_usr_receive.setText("");
//                txt_usr_receive.setContentDescription(null);

                spn_department_form.setSelection(0);

//                spn_usr_send.setSelection(0);
                spn_usr_receive.setSelection(0);

                list_doc_detail.setAdapter(null);

                if(SS_IsUsedZoneSterile) {
                    spn_zone.setSelection(0);
                }

                displaySendSterile(false, null, 0);

            }
        });

        btn_complete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /*
                if(IsAdmin){
                    onRemoveSelectedSendSterile();
                    return;
                }
                */
                int cnt = 0;
                for (String key : Usage_Nowash.keySet()) {
                    if (Usage_Nowash.get(key) == "0")
                        cnt++;
                }

                if (Switch_Mode){
                    for (String key : Usage_Nowash.keySet()) {
                        if (Usage_Nowash.get(key) == "0")
                            usage_nowash_id.add(key);
                    }

                    if (!DocNo.equals("")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("ยืนยัน");
                        builder.setMessage("ยืนยันปิดใบบันทึกรับอุปกรณ์ ?");
                        builder.setPositiveButton("ตกลง",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Check Status
                                        if (!IsStatus.equals("0")) {
                                            Toast.makeText(ReceiveActivity.this, "เอกสารนี้ได้ถูกปิดแล้ว !!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String send_id = null;
                                        String receive_id = null;
                                        String zone_id = null;

                                        // Check Sender
                                        if(SS_IsShowSender){

                                            if(!spn_usr_send.getSelectedItem().equals("")) {
                                                send_id = data_user_send_id.get(spn_usr_send.getSelectedItem());
                                            }else{
                                                Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้ส่ง !! (แผนก)", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }else{
                                            send_id = userid;
                                        }

                                        // Check Receiver
//                                        if(SS_IsReceiverDropdown){
//                                            if(!spn_usr_receive.getSelectedItem().equals("")) {
//                                                receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());
//                                            }
//                                        }else{
//                                            if (!txt_usr_receive.getText().toString().trim().equals("")) {
//                                                receive_id = txt_usr_receive.getContentDescription().toString();
//                                            }
//                                        }

                                        // Check Zone
//                                        if(SS_IsUsedZoneSterile){
//                                            if(!spn_zone.getSelectedItem().equals("")) {
//                                                zone_id = data_zone_id.get(spn_zone.getSelectedItem());
//                                            }
//                                        }

                                        receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());

                                        if(receive_id == null){
                                            Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้รับ !! (จ่ายกลาง)", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Save Complete
                                        onSaveNowash(DocNo, send_id, receive_id, zone_id, String.valueOf(usage_nowash_id));

                                    }
                                });

                        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();

                        dialog.show();

                    } else {
                        Toast.makeText(ReceiveActivity.this, "ไม่มีเอกสารให้บันทึก", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (!DocNo.equals("")) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                        builder.setCancelable(true);
                        builder.setTitle("ยืนยัน");
                        builder.setMessage("ยืนยันปิดใบบันทึกรับอุปกรณ์ ?");
                        builder.setPositiveButton("ตกลง",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        // Check Status
                                        if (!IsStatus.equals("0")) {
                                            Toast.makeText(ReceiveActivity.this, "เอกสารนี้ได้ถูกปิดแล้ว !!", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        String send_id = null;
                                        String receive_id = null;
                                        String zone_id = null;

                                        // Check Sender
                                        if(SS_IsShowSender){

                                            if(!spn_usr_send.getSelectedItem().equals("")) {
                                                send_id = data_user_send_id.get(spn_usr_send.getSelectedItem());
                                            }else{
                                                Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้ส่ง !! (แผนก)", Toast.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }else{
                                            send_id = userid;
                                        }

                                        // Check Receiver
//                                        if(SS_IsReceiverDropdown){
//                                            if(!spn_usr_receive.getSelectedItem().equals("")) {
//                                                receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());
//                                            }
//                                        }else{
//                                            if (!txt_usr_receive.getText().toString().trim().equals("")) {
//                                                receive_id = txt_usr_receive.getContentDescription().toString();
//                                            }
//                                        }

                                        // Check Zone
//                                        if(SS_IsUsedZoneSterile){
//                                            if(!spn_zone.getSelectedItem().equals("")) {
//                                                zone_id = data_zone_id.get(spn_zone.getSelectedItem());
//                                            }
//                                        }

                                        receive_id = data_user_receive_id.get(spn_usr_receive.getSelectedItem());

                                        if(receive_id == null){
                                            Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกผู้รับ !! (จ่ายกลาง)", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        // Save Complete
                                        onSave(DocNo, send_id, receive_id, zone_id);

                                    }
                                });

                        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });

                        AlertDialog dialog = builder.create();

                        dialog.show();

                    } else {
                        Toast.makeText(ReceiveActivity.this, "ไม่มีเอกสารให้บันทึก", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void openSearchItemStock(String mode) {

        if(switch_non_select_department.isChecked()){
            dept_id = "-1";
        }
        ((CssdProject) getApplication()).setxActivity( this );
        Intent i = new Intent(ReceiveActivity.this, SearchItem_SendSterile.class);
        i.putExtra("xSel", mode);
        i.putExtra("ED_Dept", dept_id);
        i.putExtra("B_ID", B_ID);

        i.putExtra("p_docno", DocNo);
        i.putExtra("p_dept_id", dept_id);
        i.putExtra("p_user_code", userid);

        startActivityForResult(i, 100);
    }

    public void addUsageForRemoveByQR(final String p_qr) {

        if(!checkUsageInList(p_qr)){
            Toast.makeText(ReceiveActivity.this, "มีรายการนี้อยู่แล้ว !!", Toast.LENGTH_SHORT).show();
            no();
            return;
        }

        if(DocNo != null && !DocNo.equals("")) {
            AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceiveActivity.this);
            quitDialog.setTitle(Cons.TITLE);
            quitDialog.setIcon(R.drawable.pose_favicon_2x);
//            quitDialog.setMessage("ยืนยันเคลียร์เอกสารส่งล้าง เพื่อยิง usage code ที่ต้องการลบออกจากระบบ ?");
            final View customLayout = getLayoutInflater().inflate( R.layout.dialog_alert_1, null);
            TextView tView = customLayout.findViewById( R.id.tView);
            tView.setText( "ยืนยันเคลียร์เอกสารส่งล้าง เพื่อยิง usage code ที่ต้องการลบออกจากระบบ ?" );
            quitDialog.setView(customLayout);

            quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    DocNo = "";
                    dept_id = "";
                    index = -1;

                    // Widget
                    txt_doc_no.setText("");
                    txt_doc_time.setText("");
                    txt_count_list.setText("");
                    spn_department_form.setSelection(0);
                    spn_usr_send.setSelection(0);
                    spn_usr_receive.setSelection(0);

                    txt_usr_receive.setText("");
                    txt_count_list.setText("");

                    if(SS_IsUsedZoneSterile){
                        spn_zone.setSelection(0);
                    }

                    // List
                    item_doc_detail_set.clear();
                    if(tabs.getSelectedTabPosition()==0){
                        list_doc_detail_set.setAdapter(null);
                    }
                    list_doc_detail.setAdapter(null);
                    list_doc.setAdapter(null);

                    model_send_sterile_detail.clear();
                    model_send_sterile.clear();

                    addUsageForRemove(p_qr, false);
                }
            });

            quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            quitDialog.show();
        }else{
            addUsageForRemove(p_qr, true);
        }
    }

    public void addUsageForRemove(final String p_qr, final boolean IsUsedSound) {

        class AddUsageByQR extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            pCustomer p = new pCustomer();

                            p.setUsageCode(c.getString("UsageCode"));
                            p.setItemcode(c.getString("ItemCode"));
                            p.setItemname(c.getString("itemname"));
                            p.setXqty(c.getString("Qty"));
                            p.setIsSterile(c.getString("IsSterile"));
                            p.setXremark(c.getString("Remark"));
                            p.setDocno(c.getString("DocNo"));
                            p.setIsStatus(c.getString("IsStatus"));
                            p.setResteriletype(c.getString("ResterileType"));
                            p.setResterilename(c.getString("Resterilename"));
                            p.setDept("DeptID");
                            p.setUcode(userid);
                            p.setOccuranceID(c.getString("OccuranceQty"));
                            p.setItemID(c.getString("RowID"));
                            p.setSs_rowid(c.getString("SS_RowID"));
                            p.setPackdate(c.getString("Shelflife"));
                            p.setItemCount(c.getString("ItemCount"));

                            model_send_sterile_detail.add(p);

                            if(IsUsedSound)
                                ok();

                        }else{

                            if(IsUsedSound) {
                                no();
                            }

                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                            return;
                        }

                    }

                    list_doc_detail.setAdapter(new ListSendSterileDetailAdapter(ReceiveActivity.this, model_send_sterile_detail, false, mode, B_ID, WA_IsUsedWash, Switch_Mode));

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_qr", p_qr);

                String result = httpConnect.sendPostRequest(getUrl + "cssd_check_usage_for_remove_by_qr.php", data);

                return result;
            }
        }

        AddUsageByQR ru = new AddUsageByQR();

        ru.execute();
    }

    public void clearByQR(final String p_qr) {
        class ClearByQR extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                clearForm();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                ok();
                focus();
            }

            @Override
            protected String doInBackground(String... params) {
                //HashMap<String, String> data = new HashMap<String, String>();

                //data.put("p_id", p_qr);

                //String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getUrl() + "cssd_select_department_by_search.php", data);

                //return result;

                return p_qr;
            }
        }

        ClearByQR ru = new ClearByQR();

        ru.execute();
    }

    public void addEmpolyeeByQR(final String d_em, final String d_dep, final String DocNo, final String d_issend) {
        class addEmpolyeeByQR extends AsyncTask<String, Void, String> {
            // variable

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

                    for(int i=0;i<rs.length();i++) {

                        JSONObject c = rs.getJSONObject(i);

                        if (spn_usr_receive.getSelectedItemPosition() == 0){
                            spn_usr_receive.setSelection(Integer.parseInt(c.getString("ID")));
                            edt_usage_code.setText("");
                            edt_usage_code.requestFocus();
                        }else {
                            spn_usr_send.setSelection(Integer.parseInt(c.getString("ID")));
                            edt_usage_code.setText("");
                            edt_usage_code.requestFocus();
                        }

                    }

                } catch (JSONException e) {

                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("d_em", d_em);
                data.put("d_dep", d_dep);
                data.put("d_docno", DocNo);
                data.put("d_issend", d_issend);
                data.put("B_ID",B_ID);
                String result = null;

                result = httpConnect.sendPostRequest(getUrl + "cssd_update_send_sterile_empolyee.php", data);

                return result;
            }
        }

        addEmpolyeeByQR ru = new addEmpolyeeByQR();
        ru.execute();
    }

    public void addSterileDetailByQR(final String p_qr, final String p_dept_id) {

        class AddSterileDetailByQR extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {

                            ok();

                            if (DocNo == null || DocNo.equals("")) {

                                DocNo = c.getString("DocNo");

                                displaySendSterile(true, c.getString("DocNo"), 6);

                                displaySendSterileDetail(c.getString("DocNo"));

                            }else{
                                displaySendSterileDetail(DocNo);
                            }

                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                        }else if(c.getString("result").equals("B")){

                            final String RowId = c.getString("RowId");

                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("CSSD");
                            builder.setMessage(c.getString("Message"));

                            builder.setPositiveButton("ยกเลิก Usage Code",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onUpdateItemStock(RowId, true);
                                        }
                                    });


                            builder.setNeutralButton("Reset รอบใช้งานใหม่",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onUpdateItemStock(RowId, false);
                                        }
                                    });

                            builder.setNegativeButton("ปิด", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            AlertDialog dialog = builder.create();

                            dialog.show();

                            focus();

                        }else {
                            if (SS_IsUsedClosePayout){
                                if (c.getString("IsStatus").equals("3") && c.getString("Ispay").equals("1")){

                                    String payout_Docno = c.getString("DocNo_Payput");
                                    String text_Message = c.getString("Message");

                                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceiveActivity.this);
//                                    quitDialog.setTitle("รายการนี้อยู่ในสถานะจ่ายอุปกรณ์ให้แผนก กำลังรอปิดใบจ่าย คุณต้องการปิดเอกสารการจ่ายหรือไม่ ?");
                                    final View customLayout = getLayoutInflater().inflate( R.layout.dialog_alert_1, null);
                                    TextView tView = customLayout.findViewById( R.id.tView);
                                    tView.setText( "รายการนี้อยู่ในสถานะจ่ายอุปกรณ์ให้แผนก กำลังรอปิดใบจ่าย คุณต้องการปิดเอกสารการจ่ายหรือไม่ ?" );
                                    quitDialog.setView(customLayout);

                                    quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            updatePayoutDocno(payout_Docno,p_qr);
                                        }
                                    });

                                    quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(ReceiveActivity.this, text_Message, Toast.LENGTH_SHORT).show();
                                            no();
                                            focus();
                                        }
                                    });

                                    quitDialog.show();
                                }else {
                                    if (SS_IsUsedChangeDepartment){
                                        if (c.getString("IsStatus").equals("4") || c.getString("IsStatus").equals("5")){

                                            String text_Message = c.getString("Message");
                                            String S_DeptName = c.getString("DepName");

                                            AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceiveActivity.this);

                                            quitDialog.setTitle("รับอุปกรณ์ไม่ตรงแผนก !!");

                                            final View customLayout = getLayoutInflater().inflate( R.layout.dialog_alert_1, null);
                                            TextView tView = customLayout.findViewById( R.id.tView);
                                            tView.setText( "รหัสใช้งานนี้อยู่ในแผนก " + S_DeptName + " คุณต้องการเปลี่ยนแผนกหรือไม่ ?" );
                                            quitDialog.setView(customLayout);
                                            quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    updateDepartmentItem(p_qr);
                                                }
                                            });

                                            quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(ReceiveActivity.this, text_Message, Toast.LENGTH_SHORT).show();
                                                    no();
                                                    focus();
                                                }
                                            });

                                            quitDialog.show();

                                        }else {
                                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                                            no();
                                            focus();
                                        }
                                    }else {
                                        Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                                        no();
                                        focus();
                                    }
                                }
                            }else {
                                if (SS_IsUsedChangeDepartment){
                                    if (c.getString("IsStatus").equals("4") || c.getString("IsStatus").equals("5")){

                                        String text_Message = c.getString("Message");
                                        String S_DeptName = c.getString("DepName");

                                        AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceiveActivity.this);


                                        quitDialog.setTitle("รับอุปกรณ์ไม่ตรงแผนก !!");

                                        final View customLayout = getLayoutInflater().inflate( R.layout.dialog_alert_1, null);
                                        TextView tView = customLayout.findViewById( R.id.tView);
                                        tView.setText( "รหัสใช้งานนี้อยู่ในแผนก " + S_DeptName + " คุณต้องการเปลี่ยนแผนกหรือไม่ ?" );
                                        quitDialog.setView(customLayout);

                                        quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                updateDepartmentItem(p_qr);
                                            }
                                        });

                                        quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(ReceiveActivity.this, text_Message, Toast.LENGTH_SHORT).show();
                                                no();
                                                focus();
                                            }
                                        });

                                        quitDialog.show();

                                    }else {
                                        Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                                        no();
                                        focus();
                                    }
                                }else {
                                    Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                                    no();
                                    focus();
                                }
                            }
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if(((CssdProject) getApplication()).getCustomerId() == 201 || ((CssdProject) getApplication()).getCustomerId() == 211){
                    data.put("p_is_used_itemstock_department", "0");
                }

                if(DocNo != null && !DocNo.equals("")) {
                    data.put("p_docno", DocNo);
                }

                data.put("p_user_code", userid);
                data.put("p_qr", p_qr);
                data.put("p_dept_id", p_dept_id);
                data.put("p_bid",B_ID);
                Log.d("OOOO",""+getUrl + "cssd_add_send_sterile_detail_by_qr.php?"+data);
                String result = httpConnect.sendPostRequest(getUrl + "cssd_add_send_sterile_detail_by_qr.php", data);
                Log.d("OOOO",""+result);
                return result;
            }
        }

        AddSterileDetailByQR ru = new AddSterileDetailByQR();

        ru.execute();
    }

    public void findDepartmentByQR(final String p_qr) {
        class DepartmentByQR extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            spn_department_form.setSelection(getIndexSpinnerDepartment(c.getString("xID")));
                            ok();
                        }else{
                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                            no();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", p_qr);

                String result = httpConnect.sendPostRequest(getUrl + "cssd_select_department_by_search.php", data);
                Log.d("OOOO","result: "+result);
                return result;
            }
        }

        DepartmentByQR ru = new DepartmentByQR();

        ru.execute();
    }

    public void onSave(final String DocNo, final String send_id, final String receive_id, final String zone_id) {
        class Save extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceiveActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            displaySendSterile(false, null, 7);

                            list_doc_detail.setAdapter(null);

                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                        }else if (c.getString("result").equals("E")){

                            callDialog(c.getString("Message"));

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        Block_1.setVisibility(View.VISIBLE);
                        Block_2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("DocNo", DocNo);
                data.put("send_id", send_id);
                data.put("receive_id", receive_id);
                data.put("p_is_group_payout", SS_IsGroupPayout ? "1" : "0");
                data.put("p_is_copy_to_payout", SS_IsCopyPayout ? "1" : "0");
                data.put("p_is_approve", SS_IsApprove ? "1" : "0");
                data.put("p_is_usedwash", WA_IsUsedWash ? "1" : "0");
                data.put("p_is_usedself_washdepartment", SS_IsUsedSelfWashDepartment ? "1" : "0");

                data.put("B_ID", B_ID);

                if (!dept_id.equals("") && !dept_id.equals("0")) {
                    data.put("p_dept_id", dept_id);
                }

                if(SS_IsSortByUsedCount){
                    data.put("p_IsSortByUsedCount", "1");
                }

                if(SS_IsUsedZoneSterile){
                    data.put("p_zone_id", zone_id);
                }

                data.put("p_project_id", Integer.toString ( ((CssdProject) getApplication()).getCustomerId()) );

                String result = httpConnect.sendPostRequest(getUrl + "cssd_update_send_sterile_complete.php", data);

                return result;
            }
        }

        Save ru = new Save();

        ru.execute();
    }

    public void onSaveNowash(final String DocNo, final String send_id, final String receive_id, final String zone_id, final String Usage_nowash_id) {
        class onSaveNowash extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceiveActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCancelable(false);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            displaySendSterile(false, null, 7);

                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                            Usage_Nowash.clear();

                            usage_nowash_id.clear();

                        }else if (c.getString("result").equals("E")){

                            callDialog(c.getString("Message"));

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                        Block_1.setVisibility(View.VISIBLE);
                        Block_2.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("DocNo", DocNo);
                data.put("send_id", send_id);
                data.put("receive_id", receive_id);
                data.put("p_is_group_payout", SS_IsGroupPayout ? "1" : "0");
                data.put("p_is_copy_to_payout", SS_IsCopyPayout ? "1" : "0");
                data.put("p_is_approve", SS_IsApprove ? "1" : "0");
                data.put("p_is_usedwash", WA_IsUsedWash ? "1" : "0");
                data.put("p_usage_nowash_id", Usage_nowash_id);

                data.put("B_ID", B_ID);

                if (!dept_id.equals("") && !dept_id.equals("0")) {
                    data.put("p_dept_id", dept_id);
                }

                if(SS_IsSortByUsedCount){
                    data.put("p_IsSortByUsedCount", "1");
                }

                if(SS_IsUsedZoneSterile){
                    data.put("p_zone_id", zone_id);
                }

                data.put("p_project_id", Integer.toString ( ((CssdProject) getApplication()).getCustomerId()) );

                String result = httpConnect.sendPostRequest(getUrl + "cssd_update_send_sterile_complete.php", data);
                Log.d("OOOO","result:"+result);
                return result;
            }
        }

        onSaveNowash ru = new onSaveNowash();

        ru.execute();
    }

    private boolean checkUsageInList(final String p_qr){
        List<pCustomer> DATA_MODEL = model_send_sterile_detail;

        Iterator li = DATA_MODEL.iterator();

        while (li.hasNext()) {

            pCustomer m = (pCustomer) li.next();

            if (m.getUsageCode().equals(p_qr)) {
                return false;
            }
        }

        return true;
    }

    public void onUpdateItemStock(final String RowID, final boolean IsCancel) {

        class DeleteSterileDetail extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")){
                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", RowID);
                data.put("p_is_cancel", IsCancel ? "1" : "0");

                String result = httpConnect.sendPostRequest(getUrl + "cssd_update_item_stock.php", data);

                return result;
            }
        }

        DeleteSterileDetail ru = new DeleteSterileDetail();
        ru.execute();
    }

    public void updatePayoutDocno(final String docno, final String usagecode) {
        class updatePayoutDocno extends AsyncTask<String, Void, String> {
            private final ProgressDialog dialog = new ProgressDialog(ReceiveActivity.this);
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

                        addSterileDetailByQR(usagecode, dept_id);

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
                data.put("p_docno",docno);
                data.put("p_dept_id",dept_id);
                String result = null;
                try {

                    result = httpConnect.sendPostRequest(getUrl + "cssd_update_payout_by_send_sterile.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        updatePayoutDocno obj = new updatePayoutDocno();
        obj.execute();
    }

    public void updateDepartmentItem(final String usagecode) {
        class updateDepartmentItem extends AsyncTask<String, Void, String> {
            private final ProgressDialog dialog = new ProgressDialog(ReceiveActivity.this);
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

                        addSterileDetailByQR(usagecode, dept_id);

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
                data.put("p_usagecode",usagecode);
                data.put("p_dept_id",dept_id);
                String result = null;
                try {

                    result = httpConnect.sendPostRequest(getUrl + "cssd_update_dep_item_by_send_sterile.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        updateDepartmentItem obj = new updateDepartmentItem();
        obj.execute();
    }

    public void focus(){
        edt_usage_code.setText("");
        edt_usage_code.requestFocus();
    }

    public void clearForm() {
        // Var
        DocNo = "";
        dept_id = "";
        index = -1;

        // Widget
        txt_doc_no.setText("");
        txt_doc_time.setText("");
        txt_count_list.setText("");
        spn_department_form.setSelection(0);
        spn_usr_send.setSelection(0);
        spn_usr_receive.setSelection(0);
        txt_usr_receive.setText("");
        txt_count_list.setText("");

        if(SS_IsUsedZoneSterile){
            spn_zone.setSelection(0);
        }

        if(SS_IsNonSelectDepartment && switch_non_select_department.isChecked()) {
            spn_department_form.setEnabled(true);
            showDepartmentForm(false);
        }else{
            spn_department_form.setEnabled(true);
            showDepartmentForm(true);
        }

        // List
        item_doc_detail_set.clear();
        if(tabs.getSelectedTabPosition()==0){
            list_doc_detail_set.setAdapter(null);
        }
        //list_doc.setSelection(-1);

        defineDocDate();

        model_send_sterile_detail.clear();
        model_send_sterile.clear();

        focus();
    }

    public void defineUser() {
        class Define extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                JSONArray setRs = null;
                data_user_send_id.clear();
                data_user_receive_id.clear();

                ArrayList<String> listUSend = new ArrayList<String>();

                listUSend.clear();
                listUSend.add("เลือกผู้รับ");

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<setRs.length();i++){
                        JSONObject c = setRs.getJSONObject(i);

                        listUSend.add(c.getString("xName"));

                        ar_list_user_send_id.add(c.getString("xID"));
                        ar_list_user_receive_id.add(c.getString("xID"));

                        data_user_send_id.put(c.getString("xName"),c.getString("xID"));
                        data_user_receive_id.put(c.getString("xName"),c.getString("xID"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReceiveActivity.this,android.R.layout.simple_spinner_dropdown_item,listUSend);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

//                    spn_usr_send.setAdapter(adapter);
                    spn_usr_receive.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                String result = httpConnect.sendPostRequest(getUrl + "cssd_select_user.php",data);
                return  result;
            }
        }

        Define ru = new Define();

        ru.execute();
    }

    public void onLongClick(final String DocNo){
        checkCountSterileDetail(DocNo);
    }

    public void checkCountSterileDetail(final String p_docno) {

        class Check extends AsyncTask<String, Void, String> {
            // variable
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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(ReceiveActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("ยืนยัน");
                            builder.setMessage("ต้องการลบอกสารหรือไม่ ?");
                            builder.setPositiveButton("ตกลง",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            onRemoveSendSterile(p_docno);
                                        }
                                    });

                            builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                            AlertDialog dialog = builder.create();

                            dialog.show();


                        }else{
                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", p_docno);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_select_count_send_sterile_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        Check ru = new Check();

        ru.execute();
    }

    public void onRemoveSelectedSendSterile(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceiveActivity.this);
        quitDialog.setTitle(Cons.TITLE);
        quitDialog.setIcon(R.drawable.pose_favicon_2x);
        quitDialog.setMessage("ยืนยันเพื่อลบรายการส่งล้าง ?");

        quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                removeSelectedSendSterile();
            }
        });

        quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        quitDialog.show();
    }

    public void removeSelectedSendSterile() {

        String data = "";

        List<pCustomer> DATA_MODEL = model_send_sterile_detail;

        Iterator li = DATA_MODEL.iterator();

        while (li.hasNext()) {

            pCustomer m = (pCustomer) li.next();

            if (m.isIscheck()) {
                data += m.getSs_rowid() + ",";
            }
        }

        if(data.equals("")){
            Toast.makeText(ReceiveActivity.this, "ยังไม่ได้เลือกรายการ !!", Toast.LENGTH_SHORT).show();
            return;
        }

        final String p_data = data.substring(0, data.length() - 1);

        class Remove extends AsyncTask<String, Void, String> {
            // variable
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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            displaySendSterileDetail(DocNo);

                        }else{

                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_user", userid);
                data.put("p_data", p_data);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_delete_send_sterile_details.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        Remove ru = new Remove();

        ru.execute();
    }

    public void onRemoveSendSterile(final String p_docno) {

        class RemoveSendSterile extends AsyncTask<String, Void, String> {
            // variable
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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            displaySendSterile(false, null, 11);

                        }else{
                            Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_user", userid);
                data.put("p_docno", p_docno);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_remove_send_sterile.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        RemoveSendSterile ru = new RemoveSendSterile();

        ru.execute();
    }

    public void updateRemark(final String DocNo, final String remark, final String check) {
        class UpdateRemark extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("DocNo", DocNo);
                data.put("remark", remark);
                data.put("check", check);
                data.put("B_ID",B_ID);
                String result = null;

                result = httpConnect.sendPostRequest(getUrl + "cssd_update_send_sterile_remark.php", data);

                return result;
            }
        }

        UpdateRemark ru = new UpdateRemark();
        ru.execute();
    }

    private void onChangeDateSearch() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_doc_date_search.setText(sdf.format(myCalendar.getTime()));

//        if(B_IsSearch) {
//            displaySendSterile(false, null, 5);
//        }
    }

    private void onChangeDate() {
        try {
            String myFormat = "dd/MM/yyyy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            String d = sdf.format(myCalendar.getTime());
            Log.d("OOOO","Date: "+d);
            txt_doc_date.setText(d);

            String time = txt_doc_time.getText().toString();
            Log.d("OOOO","Time: "+d);
            if (time.equals("")) {
                final Calendar cldr = Calendar.getInstance();
                time = cldr.get(Calendar.HOUR_OF_DAY) + ":" + cldr.get(Calendar.MINUTE);
            }

//            updateSendSterile("DocDate", d.substring(6, 10) + "-" + d.substring(3, 5) + "-" + d.substring(0, 2) + " " + time + ":00", DocNo);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public int getIndexSpinnerDepartment(String deptPos) {
        int index = 0;

        for (int i = 0; ar_list_dept_id.size() > i; i++) {
            if (deptPos.equals(ar_list_dept_id.get(i))) {
                index = i;
            }
        }

        return index;
    }

    public void displaySendSterile() {

        final String Date = txt_doc_date_search.getText().toString();

        class DisplaySendSterile extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //clearForm();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        DocNo = c.getString("DocNo");
                        IsStatus = c.getString("IsStatus");

                        // Display Form
                        displaySendSterile(
                                DocNo,
                                c.getString("DocDate"),
                                c.getString("xtime"),
                                c.getString("DeptID"),
                                c.getString("usr_receive"),
                                c.getString("UserReceive"),
                                c.getString("UserSend"),
                                IsStatus != null && IsStatus.equals("2")
                        );

                        // Display List Detail
                        displaySendSterileDetail(DocNo);

                        return;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_is_non_select_department", "1");
                data.put("date", Date);
                data.put("p_bid", B_ID);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_display_send_sterile.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        DisplaySendSterile ru = new DisplaySendSterile();

        ru.execute();
    }

    private void displaySendSterile( String DocNo, String DocDate, String DocTime, String DeptId, String ReceiveName, String ReceiveId, String SendId, boolean IsComplete ){
        spn_department_form.setSelection(getIndexSpinnerDepartment(DeptId));

        txt_title.setText("บันทึกรับ  "+DocNo);
        txt_doc_date.setText(DocDate);
        txt_doc_time.setText(DocTime);

//        if(SS_IsReceiverDropdown) {
            if(ReceiveId != null) {
                spn_usr_receive.setSelection(ar_list_user_receive_id.indexOf(ReceiveId) + 1);
                usr_receive_old_id = ReceiveId;
            }
//        }

//        if(SendId != null) {
//            spn_usr_send.setSelection(ar_list_user_send_id.indexOf(SendId) + 1);
//        }
        dept_id = DeptId;

        Block_1.setVisibility( View.GONE );
        Block_2.setVisibility( View.VISIBLE );
    }

    public void displaySendSterile(final boolean IsDisplayForm, final String p_docno, int I_Result ) {

//        if(!B_IsSearch){
//            return;
//        }

        final String department_id = dept_id_search;
        final String Date = txt_doc_date_search.getText().toString();
        final String txt = edt_doc_no_search.getText().toString();
        final String status = switch_status.isChecked() ? "-1" : "0";

        class DisplaySendSterile extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    ArrayList<pCustomer> ar_data = new ArrayList<>();

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        pCustomer p = new pCustomer();

                        p.setDocno(c.getString("DocNo"));
                        p.setDocdate(c.getString("DocDate"));
                        p.setDept(c.getString("DeptID"));
                        p.setDeptname(c.getString("DepName2"));
                        p.setQty(c.getString("Qty"));
                        p.setNote(c.getString("Remark"));
                        p.setIsStatus(c.getString("IsStatus"));
                        p.setTime(c.getString("xtime"));
                        p.setUserReceive(c.getString("UserReceive"));
                        p.setUsr_receive(c.getString("usr_receive"));
                        p.setUserSend(c.getString("UserSend"));
                        p.setUsr_send(c.getString("usr_send"));

                        p.setIsWeb(c.getString("IsWeb"));
                        p.setSend_From(c.getString("Send_From"));

                        IsStatus = c.getString("IsStatus");

                        p.setQty1(c.getString("Qty1"));
                        p.setQty2(c.getString("Qty2"));

                        ar_data.add(p);
                    }

                    model_send_sterile = ar_data;

                    list_doc.setAdapter(new ListSendSterileAdapter(ReceiveActivity.this, model_send_sterile, B_ID));

                    if(IsDisplayForm){
                        // Display Form
//                        setSterileForm(p_docno);

                        //DocNo = c.getString("DocNo");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                }


            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if(IsSU) {
                    data.put("is_admin", "1");
                    data.put("department_id", department_id);
                    data.put("search", txt);
                }else if(IsDisplayForm){
                    data.put("search", p_docno);
                }else {
                    data.put("department_id", department_id);
                    data.put("search", txt);
                    data.put("status", status);
                    data.put("date", Date);
                }

                if(!SS_IsNonSelectDepartment){
                    data.put("p_is_doc", "1");
                }

                if (Switch_Mode){
                    data.put("p_wash_dep", "1");
                }else {
                    data.put("p_wash_dep", "0");
                }

                data.put("p_bid", B_ID);

                String result = null;

                try {
                    Log.d("OOOO",getUrl + "cssd_display_send_sterile.php?"+data);
                    result = httpConnect.sendPostRequest(getUrl + "cssd_display_send_sterile.php", data);
                    Log.d("OOOO",result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        DisplaySendSterile ru = new DisplaySendSterile();

        ru.execute();
    }

    public void displaySendSterileDetail(final String docno) {

        class DisplaySendSterileDetail extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                item_doc_detail_set.clear();
//                if(tabs.getSelectedTabPosition()==0){
//                    list_doc_detail_set.setAdapter(null);
//                }
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    model_send_sterile_detail.clear();

                    int count = 0;

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        pCustomer p = new pCustomer();

                        p.setRemarkSend(c.getString("RemarkSend"));
                        p.setUsageCode(c.getString("UsageCode"));
                        p.setItemcode(c.getString("ItemCode"));
                        p.setItemname(c.getString("itemname"));
                        p.setXqty(c.getString("Qty"));
                        p.setIsSterile(c.getString("IsSterile"));
                        p.setXremark(c.getString("Remark"));
                        p.setDocno(c.getString("DocNo"));
                        p.setIsStatus(c.getString("IsStatus"));
                        p.setResteriletype(c.getString("ResterileType"));
                        p.setResterilename(c.getString("Resterilename"));
                        p.setDept("DeptID");
                        p.setUcode(userid);
                        p.setOccuranceID(c.getString("OccuranceQty"));
                        p.setItemID(c.getString("RowID"));
                        p.setSs_rowid(c.getString("SS_RowID"));
                        p.setPackdate(c.getString("Shelflife"));
                        p.setItemCount(c.getString("ItemCount"));
                        p.setPhysicianID(c.getString("PhysicianID"));
                        p.setPhysicianName(c.getString("PhysicianName"));
                        p.setHnInfo(c.getString("HnInfo"));
                        p.setHnName(c.getString("HnName"));
                        p.setHNID(c.getString("HNID"));
                        p.setRemarkExpress(c.getString("RemarkExpress"));
                        p.setRemarkEms(c.getString("IsRemarkExpress"));
                        p.setIsDenger(c.getString("IsDenger"));
                        if(c.getString("basket").equals("null")){
                            p.setBasketname("");
                        }else {
                            p.setBasketname(c.getString("basket"));
                        }
                        model_send_sterile_detail.add(p);

                        IsStatus = c.getString("IsStatus");

                        count++;
                    }

                    DocNo = docno;

                    list_doc_detail.setAdapter(new ListSendSterileDetailAdapter(ReceiveActivity.this, model_send_sterile_detail, IsSU, mode, B_ID, WA_IsUsedWash, Switch_Mode));

                    txt_count_list.setText("จำนวน " + count + " รายการ");

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{

                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("DocNo", docno);
                data.put("B_ID", B_ID);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_display_send_sterile_detail.php", data);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        DisplaySendSterileDetail ru = new DisplaySendSterileDetail();

        ru.execute();
    }

    public void LoadImg(final String itemcode,final String sel,final String usagecode,final String itemname,final String type) {
        if (type.equals("remark")){
            Intent intent = new Intent(ReceiveActivity.this, dialog_Load_Img_Remark.class);
            intent.putExtra("itemcode", itemcode);
            intent.putExtra("usagecode", usagecode);
            intent.putExtra("itemname", itemname);
            intent.putExtra("sel",sel);
            intent.putExtra("type",type);
            startActivity(intent);
        }else {
            Intent intent = new Intent(ReceiveActivity.this, dialog_Load_Img.class);
            intent.putExtra("itemcode", itemcode);
            intent.putExtra("usagecode", usagecode);
            intent.putExtra("itemname", itemname);
            intent.putExtra("sel",sel);
            intent.putExtra("type",type);
            startActivity(intent);
        }
    }

    public void displaySterileDetailSet(final String UsageCode) {
        class DisplaySterileDetailSet extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                item_doc_detail_set.clear();
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);
                    final ArrayList<pCustomer> pCus = new ArrayList<pCustomer>();
                    int cnt = 0;
                    int Total = 0;
                    int Total1 = 0;
                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);
                        pCustomer p = new pCustomer();

                        p.setItemname(c.getString("itemname"));
                        p.setXqty(c.getString("Qty"));
                        p.setUsageCode(UsageCode);
                        p.setRemarkAdmin(c.getString("RemarkAdmin"));
                        p.setIsPicture(c.getString("IsPicture"));
                        p.setRemarkItemCode(c.getString("RemarkItemCode"));
                        p.setRemarkDocNo(c.getString("DocNo"));
                        p.setQtyItemDetail(c.getString("QtyItemDetail"));
                        p.setMutiPic_Remark(c.getString("MutiPic_Remark"));
                        pCus.add(p);

                        Total = Integer.parseInt(c.getString("Qty"));
                        Total1 = Integer.parseInt(c.getString("QtyItemDetail"));
                        if (Total == Total1){

                        }else {
                            cnt++;
                        }

                        item_doc_detail_set.add(p);
                    }
                    lable_qty.setVisibility(View.VISIBLE);
                    lable_qty.setText("[ " +cnt+ "  รายการ  / ");
                    getListDetailQty(UsageCode);
                    list_doc_detail_set.setAdapter(new ListSendSterileDetailSetAdapter(ReceiveActivity.this, item_doc_detail_set));
                    tabs.getTabAt(0).select();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_is_used_remarks", SS_IsUsedRemarks ? "1" : "0");
                data.put("UsageCode", UsageCode);
                data.put("DocNo", txt_doc_no.getText().toString());
                data.put("B_ID", B_ID);

                String result = null;

                try {

                    result = httpConnect.sendPostRequest(getUrl + "cssd_select_item_stock_by_send_sterile.php", data);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        DisplaySterileDetailSet ru = new DisplaySterileDetailSet();
        ru.execute();
    }

    public void getListDetailQty(final String UsageCode) {
        class getListDetailQty extends AsyncTask<String, Void, String> {
            // variable
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
                    for(int i=0;i<rs.length();i++) {
                        JSONObject c = rs.getJSONObject(i);

                        S_Qty = c.getString("Qty");

                        lable_unit.setVisibility(View.VISIBLE);

                        if (S_Qty.equals("null")) {
                            lable_unit.setText("" + "0" + " ชิ้น" + " ]");
                        }else {
                            lable_unit.setText("" + S_Qty + " ชิ้น" + " ]");
                        }

                    }

                } catch (JSONException e) {
                }
            }

            //class connect php RegisterUserClass important !!!!!!!
            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("UsageCode", UsageCode);
                data.put("DocNo",txt_doc_no.getText().toString());
                data.put("B_ID",B_ID);
                String result = null;

                result = httpConnect.sendPostRequest(getUrl + "getitemdetail_sendsterile.php", data);

                return result;
            }
        }
        getListDetailQty ru = new getListDetailQty();
        ru.execute(UsageCode);
    }

    public void onDeleteSterileDetail(final String ID) {

        class DeleteSterileDetail extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                try {
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        if(c.getString("result").equals("A")){

                            displaySendSterileDetail(DocNo);

                        }

                        Toast.makeText(ReceiveActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", ID);
                data.put("p_user_code", userid);

//                if(((CssdProject) getApplication()).getPm().getEmCode() == 201 || ((CssdProject) getApplication()).getPm().getEmCode() == 211){
//                    data.put("p_is_used_itemstock_department", "1");
//                }

                String result = httpConnect.sendPostRequest(getUrl + "cssd_delete_send_sterile_detail.php", data);

                return result;
            }
        }

        DeleteSterileDetail ru = new DeleteSterileDetail();
        ru.execute();
    }

    public void getUsagecode(final String Usage){
        Usagecode = Usage;
    }

    public void usageNowash (String RowID, String DelRowid){
        S_RowId = RowID;
        String x="0";
        if(Usage_Nowash.get(DelRowid)=="0"){
            x="1";
        }
        Usage_Nowash.put(DelRowid, x);
    }

    private void showDepartmentForm(boolean B_IsShowDepartmentForm){
        if (B_IsShowDepartmentForm) {
            spn_department_form.setVisibility(View.VISIBLE);
            txt_label_department.setVisibility(View.VISIBLE);
        } else {
            spn_department_form.setVisibility(View.INVISIBLE);
            txt_label_department.setVisibility(View.INVISIBLE);
        }
    }

    public void updateRemarkems(String DocNo, String remark, final String xsel) {
        class updateRemarkems extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try{
                    JSONObject jsonObj = new JSONObject(s);
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);
                    final ArrayList<pCustomer> pCus = new ArrayList<pCustomer>();

                    for(int i=0;i<setRs.length();i++) {
                        JSONObject c = setRs.getJSONObject(i);

                        //displaySendSterileDetail(DocNo);

                        Toast.makeText(ReceiveActivity.this, "บันทึกสำเร็จ", Toast.LENGTH_SHORT).show();
                    }

                }catch (JSONException e){
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("DocNo",params[0]);
                data.put("remark",params[1]);
                data.put("xsel",params[2]);
                data.put("B_ID",B_ID);

                String result =  httpConnect.sendPostRequest(getUrl + "updateremark_sendsterile_ems.php", data);

                return result;
            }
        }
        updateRemarkems ru = new updateRemarkems();
        ru.execute(DocNo,remark,xsel);
    }

    // ------------------------------------------------------------------
    // Call Dialog
    // ------------------------------------------------------------------

    private void callDialog(final String Message) {

        if (SS_IsShowToastDialog) {

            final Dialog dialog = new Dialog(ReceiveActivity.this, R.style.DialogCustomTheme);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            final TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
            final TextView txt_message = (TextView) dialog.findViewById(R.id.txt_message);
            final TextView txt_tmp = (TextView) dialog.findViewById(R.id.txt_tmp);
            final ImageView bt_cancel = (ImageView) dialog.findViewById(R.id.bt_cancel);

            txt_title.setText(Cons.TITLE);
            txt_message.setText(Message);

            bt_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    displaySendSterile(false, null, 10);
                    dialog.dismiss();
                }
            });

            dialog.show();

            txt_tmp.requestFocus();

        }else{
            Toast.makeText(ReceiveActivity.this, Message, Toast.LENGTH_SHORT).show();
        }
    }
    public void callDialog(final String Itemname ,final String type, final String Qty, final String Qty_save) {
        Intent intent = new Intent(ReceiveActivity.this, dialog_remark_sendsterile.class);
        intent.putExtra("Itemname", Itemname);
        intent.putExtra("Usagecode", Usagecode);
        intent.putExtra("DepID", dept_id);
        intent.putExtra("DocNoSend", DocNo);
        intent.putExtra("EmpCode",EmpCode);
        intent.putExtra("IsAdmin",IsAdmin);
        intent.putExtra("IsSU",IsSU);
        intent.putExtra("IsStatus",IsStatus);
        intent.putExtra("DocNo",txt_doc_no.getText().toString());
        intent.putExtra("Type",type);
        intent.putExtra("page","sendsterile");
        intent.putExtra("Qty",Qty);
        intent.putExtra("Qty_save",Qty_save);
        intent.putExtra("context", String.valueOf(ReceiveActivity.this));
        startActivityForResult(intent,1005);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }

        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void defineDocDate() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Log.d("OOOO","Date1: "+sdf.format(Calendar.getInstance().getTime()));
        txt_doc_date.setText(sdf.format(Calendar.getInstance().getTime()));
    }

    // =============================================================================================
    // -- Update Sterile
    // =============================================================================================

    public void updateSendSterile(final String field, final String value, final String doc_no) {

        if(doc_no == null)
            return;

        class Update extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_field", field);
                data.put("p_value", value);
                data.put("p_doc_no", doc_no);
                data.put("B_ID",B_ID);

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_send_sterile.php", data);

                return result;
            }

            // =========================================================================================
        }

        Update obj = new Update();
        obj.execute();
    }

    public void CheckStatusDocNo(final String p_docno) {
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
                            CheckNotification();
                            DIALOG_ACTIVE = true;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_docNo",p_docno);
                data.put("p_bid","1");
                String result = null;
                try {

                    result = httpConnect.sendPostRequest(getUrl + "cssd_check_status_docno.php", data);

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

    public void CheckNotification() {
        class CheckNotification extends AsyncTask<String, Void, String> {
            private final ProgressDialog dialog = new ProgressDialog(ReceiveActivity.this);
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
                    String UsageCode = "";
                    String Cnt = "";
                    for(int i=0;i<rs.length();i++) {
                        JSONObject c = rs.getJSONObject(i);
                        UsageCode = c.getString("UsageCode");
                        Cnt = c.getString("cnt");
                        S_condition1 = c.getString("condition1");
                        S_condition2 = c.getString("condition2");
                        S_condition3 = c.getString("condition3");
                        S_condition4 = c.getString("condition4");
                        S_condition5 = c.getString("condition5");
                        S_condition6 = c.getString("condition6");
                    }
                    if (DIALOG_ACTIVE == true){
                        if (!S_condition1.equals("0") || !S_condition2.equals("0") || !S_condition3.equals("0") || !S_condition4.equals("0") || !S_condition5.equals("0") || !S_condition6.equals("0")){
                            if (ST_IsUsedNotification || SS_IsUsedNotification){
                                Intent intent = new Intent(ReceiveActivity.this, dialog_check_usage_count.class);
                                intent.putExtra("UsageCode", UsageCode);
                                intent.putExtra("cnt", Cnt);
                                intent.putExtra("DocNo",DocNo);
                                intent.putExtra("B_ID",B_ID);
                                intent.putExtra("sel","1");
                                intent.putExtra("page","0");
                                intent.putExtra("condition1",S_condition1);
                                intent.putExtra("condition2",S_condition2);
                                intent.putExtra("condition3",S_condition3);
                                intent.putExtra("condition4",S_condition4);
                                intent.putExtra("condition5",S_condition5);
                                intent.putExtra("condition6",S_condition6);
                                startActivity(intent);
                                DIALOG_ACTIVE = false;
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

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("DocNo",DocNo);
                data.put("B_ID",B_ID);
                data.put("type","send");
                String result = null;
                try {

                    result = httpConnect.sendPostRequest(getUrl + "cssd_display_usage_count.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }
                return result;
            }
            // =========================================================================================
        }
        CheckNotification obj = new CheckNotification();
        obj.execute();
    }


    @Override
    public void onBackPressed() {

    }
}