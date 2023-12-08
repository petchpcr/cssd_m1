package com.poseintelligence.cssdm1.Menu_Dispensing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDepartmentAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDetailItemAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDetailSubAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDocumentAdapter;
import com.poseintelligence.cssdm1.core.CustomExceptionHandler;
import com.poseintelligence.cssdm1.core.audio.iAudio;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.date.DateTime;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDepartment;
import com.poseintelligence.cssdm1.model.ModelPayout;
import com.poseintelligence.cssdm1.model.ModelPayoutDetailSub;
import com.poseintelligence.cssdm1.model.ModelPayoutDetails;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import com.poseintelligence.cssdm1.utils.SunmiPrintHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DispensingActivity extends AppCompatActivity {

    private String TAG_RESULTS = "result";
    private String TAG_RESULTS_API = "data";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    private iAudio nMidia;

    private List<ModelDepartment> Model_Department;
    private List<ModelPayout> Model_Pay;
    private List<ModelPayoutDetails> Model_Payout_Detail_item = new ArrayList<>();;
    private List<ModelPayoutDetailSub> Model_Payout_Detail_Sub;

    private ArrayList<String> ar_list_user_receive_id = new ArrayList<String>();
    private ArrayList<String> ar_list_zone_id = new ArrayList<String>();
    private ArrayList<String> ar_list_zone_name = new ArrayList<String>();

    private HashMap<String, String> data_user_receive_id = new HashMap<>();

    private ArrayAdapter<String> adapter_spinner;
    ArrayAdapter<ModelDepartment> list_department_adapter;

    // ---------------------------------------------------------------------------------------------
    // Config
    // ---------------------------------------------------------------------------------------------
    boolean have_printer = false;
    public boolean ST_SoundAndroidVersion9 = false;
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
    public boolean PA_IsNotificationPopupExpiringScan = false;

    public boolean SR_ReceiveFromDeposit = false;

    public boolean ST_IsUsedNotification = false;

    public boolean MD_IsUsedSoundScanQR = false;

    public boolean WA_IsUsedWash = false;

    private boolean PA_IsUsedZonePayout;
    private boolean PA_IsCreateReceiveDepartment;
    private boolean PA_IsShowToastDialog = true;
    private boolean PA_IsEditManualPayoutQty;
    private boolean PA_IsUsedApprover = false;
    private boolean PA_IsUsedRecipienter = false;
    private boolean PA_IsConfirmClosePayout = false;
    private boolean PA_IsUsedFIFO = false;
    private boolean PA_IsWastingPayout;

    private boolean B_IsNonSelectDocument = false;

    private String p_usage_code_pay = null;
    private String Docno_paylog = null;
    private String Dep_paylog = null;

    private boolean Is_imageCreate = false;
    private boolean Is_Zone = false;
    // ---------------------------------------------------------------------------------------------
    // Obj
    // ---------------------------------------------------------------------------------------------
    private LinearLayout Block_1;
    private LinearLayout Block_2;
    private LinearLayout Block_3;
    private LinearLayout Block_4;
    private LinearLayout Block_5;
    private RelativeLayout linear_layout_search;

    private ListView scan_log;
    private TextView scan_log_num;
    private ArrayAdapter<String> scan_log_adapter;

    private ImageView img_back_1;
    private ImageView img_back_2;
    private ImageView img_back_3;
    private ImageView imageCreate;
    private SearchableSpinner spn_zone;

    private TextView txt_search_department;
    private TextView title_2;
    private TextView title_3;
    private TextView txt_doc_type;
    TextView txt_p_approve_code;

    private ListView list_department;
    private ListView list_pay;
    private ListView list_payout_detail_item;

    private Button btn_search_department;

    private Switch switch_mode;
    private Switch switch_opt;

    private EditText txt_usage_code;
    private String mass_usage_code="";

    private LinearLayout spn_usr_receive_box;
    // ---------------------------------------------------------------------------------------------
    // Obj
    // ---------------------------------------------------------------------------------------------
    private String DocNo = null;
    private String RefDocNo = null;

    private String RefDocNoSend = null;
    private String DepID = null;
    private int DepIndex = -1;
    private String DepName = "-";
    private String DocDateTime = "";
    private String p_receive_code = null;
    private String p_approve_code = null;

    String user_name_pay;

    public String text_search_department = "";

    boolean s_expiring = false;

    private TextToSpeech tts;

    boolean PA_IsUsedPayOkSound = true;

    String d_id = "";

    SearchableSpinner spn_usr_receive;

    private TextView text_switch_department_or;
    private Switch switch_department_or;
    String Is_department_or = "0";

    ArrayList<String> xDataUserCode = new ArrayList<String>();

    public void speakText(String textContents) {
        tts.speak(textContents, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler_dept.removeCallbacks(runnable_dept);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensing);
        getSupportActionBar().hide();

        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof CustomExceptionHandler)) {
            Thread.setDefaultUncaughtExceptionHandler(new CustomExceptionHandler(
                    this.getApplicationContext()));
        }

        byWidget();

        byEvent();

        byConfig();

        initPrinterStyle();

        if (((CssdProject) getApplication()).Project().equals("VCH")) {
            getuserCode();
        }


        // -----------------------------------------------------------------------
        // Sound
        speakTextInit();
        nMidia = new iAudio(this);
        // -----------------------------------------------------------------------
        // -----------------------------------------------------------------------
        Block_1.setVisibility(View.VISIBLE);
        Block_2.setVisibility(View.GONE);
        Block_3.setVisibility(View.GONE);
        Block_4.setVisibility(View.GONE);
        spn_zone.setVisibility(Is_Zone ? View.VISIBLE : View.GONE);
        if (Is_Zone == false)
            displayDepartment("", -1, "");

        text_search_department = "";
    }

    private void speakTextInit() {
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int i) {
//                if (i == TextToSpeech.SUCCESS) {
//                    Log.d("tog_textToSpeech","TextToSpeech SUCCESS");
//
//                }
//
//                Toast.makeText(DispensingActivity.this, "TextToSpeech i = "+i, Toast.LENGTH_SHORT).show();
                Log.d("tog_textToSpeech", "TextToSpeech i = " + i);

                if (i == TextToSpeech.SUCCESS) {

                    Log.d("tog_textToSpeech", "SUCCESS");
                    int language = tts.setLanguage(Locale.US);
                    if (language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED) {
//                        Toast.makeText(DispensingActivity.this, "LANG MISSING DATA OR LANG NOT SUPPORTED", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(DispensingActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();

//                        speakText("SUCCESS");
                    }
                } else {

                    Log.d("tog_textToSpeech", "FAIL");
//                    Toast.makeText(DispensingActivity.this, "Your Device Is Missing TTS", Toast.LENGTH_SHORT).show();
                    ST_SoundAndroidVersion9 = false;
                }
            }

        });
    }

    private void initPrinterStyle() {
        Log.d("initPrinterStyle","initPrinterStyle");
        try {
            SunmiPrintHelper.getInstance().initPrinter();

        } catch (Exception e) {
            have_printer = false;
        }finally {

            have_printer = true;
            int s = SunmiPrintHelper.getInstance().sunmiPrinter;
            if (s==0){
                have_printer = false;
            }
        }

        Log.d("initPrinterStyle","have_printer = "+have_printer);
    }

    private void byWidget() {
        Block_1 = (LinearLayout) findViewById(R.id.Block1);
        Block_2 = (LinearLayout) findViewById(R.id.Block2);
        Block_3 = (LinearLayout) findViewById(R.id.Block3);
        Block_4 = (LinearLayout) findViewById(R.id.Block4);
        linear_layout_search = (RelativeLayout) findViewById(R.id.linear_layout_search);

        img_back_1 = (ImageView) findViewById(R.id.img_back_1);
        img_back_2 = (ImageView) findViewById(R.id.img_back_2);
        img_back_3 = (ImageView) findViewById(R.id.img_back_3);
        imageCreate = (ImageView) findViewById(R.id.imageCreate);

        spn_zone = (SearchableSpinner) findViewById(R.id.spn_zone);
        txt_search_department = (EditText) findViewById(R.id.txt_search_department);
        txt_usage_code = (EditText) findViewById(R.id.txt_usage_code);
        btn_search_department = (Button) findViewById(R.id.btn_search_department);
        list_department = (ListView) findViewById(R.id.list_department);
        list_pay = (ListView) findViewById(R.id.list_pay);

        switch_opt = (Switch) findViewById(R.id.switch_opt);
        switch_mode = (Switch) findViewById(R.id.switch_mode);

        switch_mode.setChecked(false);

        txt_doc_type = (TextView) findViewById(R.id.txt_doc_type);
        title_2 = (TextView) findViewById(R.id.title_2);
        title_3 = (TextView) findViewById(R.id.title_3);


        Block_5 = (LinearLayout) findViewById(R.id.Block5);
        LinearLayout scan_log_li = (LinearLayout) findViewById(R.id.scan_log_li);
        scan_log_num = (TextView) findViewById(R.id.scan_log_num);
        TextView textViewClose5 = (TextView) findViewById(R.id.textViewClose5);
        scan_log = (ListView) findViewById(R.id.scan_log);
        scan_log_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scan_log_listItems);
        scan_log.setAdapter(scan_log_adapter);

        textViewClose5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Block_5.setVisibility(View.GONE);
            }
        });
        scan_log_num.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String x = "'";
                for(int i = 0 ;i<scan_log_listItems.size();i++){
                    x=x+"','"+scan_log_listItems.get(i);
                }
                x=x+"'";

                f_checkExpiring(x);
            }
        });

        list_payout_detail_item = (ListView) findViewById(R.id.list_payout_detail_item);

        txt_p_approve_code = (TextView) findViewById(R.id.p_approve_code);
        txt_p_approve_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _getQRPay("payrow", "0");
            }
        });

        spn_usr_receive = (SearchableSpinner) findViewById(R.id.spn_usr_receive);
        spn_usr_receive.setTitle("เลือกผู้รับ");//lang
        spn_usr_receive.setPositiveButton("");//lang
        spn_usr_receive_box = (LinearLayout) findViewById(R.id.spn_usr_receive_box);

        switch_department_or = (Switch) findViewById(R.id.switch_department_or);
        text_switch_department_or = (TextView) findViewById(R.id.text_switch_department_or);

        if (!((CssdProject) getApplication()).Project().equals("SIPH")) {
            switch_department_or.setVisibility(View.GONE);
            text_switch_department_or.setVisibility(View.GONE);
        }
    }

    private void byEvent() {
        handler_dept.postDelayed(runnable_dept , 3000);

        // ===============================
        // Block 1
        // ===============================
        spn_zone.setTitle("เลือกโซน");
        spn_zone.setPositiveButton("ปิด");

        img_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(DispensingActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        });
        selectZone();
        spn_zone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                displayDepartment(null, -1, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btn_search_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(!switch_department.isChecked()) {
//                displayDepartment(txt_search_department.getText().toString(), -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//                }
                text_search_department = txt_search_department.getText().toString();
                list_department_adapter_notifyDataSetChanged();
            }
        });

        switch_department_or.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    Is_department_or = "1";
                    text_switch_department_or.setText("แผนก");
                } else {
                    Is_department_or = "0";
                    text_switch_department_or.setText("OR");
                }
                displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
            }
        });
        // ===============================
        // Block 2
        // ===============================
        img_back_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Block_1.setVisibility(View.VISIBLE);
                Block_2.setVisibility(View.GONE);
                Block_3.setVisibility(View.GONE);
                Block_4.setVisibility(View.GONE);
                Block_5.setVisibility(View.GONE);
                switch_opt.setVisibility(View.GONE);
                imageCreate.setVisibility(View.GONE);
                txt_search_department.setText("");
                displayDepartment(null, -1, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

                Log.d("tog_focus", "img_back_2");
            }
        });

        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                if (((CssdProject) getApplication()).Project().equals("SIPH")) {
                    if(isChecked){
                        switch_mode.setText("ย้อนหลัง");
                    }else{
                        switch_mode.setText("ปัจจุบัน");
                    }
                }else{
                    if (isChecked) {
                        switch_mode.setText("ทั้งหมด ");
                    } else {
                        switch_mode.setText("ค้างจ่าย ");
                    }
                }




                //displayPay(DepID, null);

                clearDocument();

            }
        });

        // ===============================
        // Block 3
        // ===============================
        img_back_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                switch_opt.setChecked(false);
                Block_1.setVisibility(View.GONE);
                Block_2.setVisibility(View.VISIBLE);
                Block_3.setVisibility(View.GONE);
                Block_4.setVisibility(View.VISIBLE);
                Block_5.setVisibility(View.GONE);
                switch_opt.setVisibility(View.GONE);
                imageCreate.setVisibility(View.GONE);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linear_layout_search.setLayoutParams(params);
                DocNo = null;
                RefDocNo = null;

                scan_log_listItems.clear();
                scan_log_adapter.notifyDataSetChanged();
            }
        });

        switch_opt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_opt.setText("ลบ ");
                } else {
                    switch_opt.setText("เพิ่ม ");
                }
            }
        });

        imageCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check DocNo
                if (DocNo == null) {
                    Toast.makeText(DispensingActivity.this, Cons.WARNING_SELECT_DOC, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (spn_usr_receive_box.getVisibility() == View.VISIBLE) {
                    if (spn_usr_receive.getSelectedItemPosition() == 0) {
                        Log.d("tog_spn_usr_receive", "spn_usr_receive = " + spn_usr_receive.getSelectedItemPosition());
                        Toast.makeText(DispensingActivity.this, "กรุณาเลือกผู้รับ!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }


                //Check Scan Qty
                boolean B_HasPay = false;

                int pay_balance = 0;

                int pay_qty = 0;

                int pay_qty_doc = 0;

                if (Model_Payout_Detail_item.size() > 0) {

                    Iterator li = Model_Payout_Detail_item.iterator();

                    while (li.hasNext()) {

                        ModelPayoutDetails m = (ModelPayoutDetails) li.next();

                        //System.out.println(m.getPay_Qty() + " = " + (!m.getPay_Qty().equals("")) + " && " + !m.getPay_Qty().equals("0"));

                        for (int i = 0; i < Model_Payout_Detail_item.size(); i++) {

                            if (m.getBalance().equals("0")) {
                                pay_qty = 1;
                                pay_balance = 1;
                            } else {
                                pay_qty = 0;
                                pay_balance = 0;
                            }
                            pay_qty_doc += Integer.parseInt(m.getPay_Qty());
                        }


                        if (!m.getPay_Qty().equals("") && !m.getPay_Qty().equals("0")) {
                            B_HasPay = true;
                            break;
                        }

                    }
                }

                if (!B_HasPay) {
                    Toast.makeText(DispensingActivity.this, "ไม่มีรายการจ่าย !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                callCloseDocument();

            }

        });

        txt_usage_code.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    speakText("Strat");
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String input = txt_usage_code.getText().toString();
                            txt_usage_code.setText("");

                            if (input.length() > 0) {

                                String S_Code = input.toLowerCase();
                                if (Block_1.getVisibility() == View.VISIBLE) {
                                    if (S_Code.length() < 6) {
                                        boolean x = true;
                                        for (int i = 0; i < Model_Department.size(); i++) {

                                            Log.d("top_dep", "ID = " + Model_Department.get(i));
                                            if (Model_Department.get(i).getID().equals(S_Code.substring(1))) {
                                                DepIndex = i;
                                                getQRPay("payrow", "0");
                                                x = false;
                                            }
                                        }
                                        if (x) {
                                            Toast.makeText(DispensingActivity.this, "ไม่พบแผนก !!", Toast.LENGTH_SHORT).show();
                                        }

                                        return false;
                                    }
                                }else {
                                    checkInput(input);
                                }
                            }

//                            if(!B_IsNonSelectDocument) {
//                                if (DepID != null) {
//                                    checkInput();
//                                } else {
//                                    findDepartmentByQR(input, true);
//                                }
//                            }
                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }


        });

//        txt_search_department.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                onTextChanged_txt_search_department();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        txt_search_department.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
//                    speakText("Strat");
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            if (txt_search_department.length() > 0) {
                                String S_Code = txt_search_department.getText().toString().toLowerCase();
                                Log.d("top_dep", "S_Code = " + S_Code);
                                if (S_Code.substring(0, 1).equals("d") || S_Code.substring(0, 1).equals("D")) {
                                    boolean x = true;
                                    for (int i = 0; i < Model_Department.size(); i++) {

                                        Log.d("top_dep", "ID = " + Model_Department.get(i).getID());
                                        if (Model_Department.get(i).getID().equals(S_Code.substring(1))) {
                                            DepIndex = i;
                                            getQRPay("payrow", "0");
                                            x = false;

                                        }
                                    }
                                    if (x) {
                                        Toast.makeText(DispensingActivity.this, "ไม่พบแผนก !!", Toast.LENGTH_SHORT).show();
                                    }

                                    txt_search_department.setText("");
                                    return false;
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

    }

//    int onTextChanged_txt_search_department_cnt = 0;
//    public void onTextChanged_txt_search_department(){
//
//        Log.d("tog_ld","onTextChanged_txt_search_department = "+onTextChanged_txt_search_department_cnt);
//        if(onTextChanged_txt_search_department_cnt==0){
//
//            Log.d("tog_ld","text_search_department = "+text_search_department);
//            Log.d("tog_ld","txt_search_department = "+txt_search_department.getText().toString());
//            if(text_search_department.equals(txt_search_department.getText().toString())){
//
//                text_search_department = txt_search_department.getText().toString();
//                list_department_adapter.notifyDataSetChanged();
//                onTextChanged_txt_search_department_cnt = 1;
//
//                handler.postDelayed(runnable, 3000);
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
    boolean Is_text_search_department =false;
    Handler handler_dept = new Handler(Looper.getMainLooper());
    Runnable runnable_dept  = new Runnable() {
        @Override
        public void run() {


            if(txt_search_department.getText().toString().length()>0){

//                Log.d("tog_handler_dept","tttt txt_search_department = "+txt_search_department.getText().toString());
                if(text_search_department.equals(txt_search_department.getText().toString())){
                    if(Is_text_search_department){
                        Is_text_search_department =false;
                        list_department_adapter_notifyDataSetChanged();
                    }
                }else{
                    Is_text_search_department = true;
                    text_search_department = txt_search_department.getText().toString();
                }
            }else{

//                Log.d("tog_handler_dept","ffff txt_search_department = "+txt_search_department.getText().toString());
                if(Is_text_search_department){
                    Is_text_search_department =false;
                    text_search_department = txt_search_department.getText().toString();
                    Log.d("tog_handler_dept","fff text_search_department = "+text_search_department);
                    list_department_adapter_notifyDataSetChanged();

                }
            }

            handler_dept.postDelayed(this, 400); // Optional, to repeat the task.
        }
    };

    public void list_department_adapter_notifyDataSetChanged(){
        String y = text_search_department.toLowerCase();

        List<ModelDepartment> list = new ArrayList<>();
        for(int i = 0;i<Model_Department.size();i++){
            String x = Model_Department.get(i).getDepName().toLowerCase();
            int s = x.indexOf(y);

            if(s>=0){
                list.add(Model_Department.get(i));
            }
        }

        list_department_adapter = new ListDepartmentAdapter(DispensingActivity.this, list, "#D6EAF8");
        list_department.setAdapter(list_department_adapter);
    }

//    public void findDepartmentByQR(final String p_qr, final boolean IsPlaySound) {
//        class DepartmentByQR extends AsyncTask<String, Void, String> {
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//            }
//
//            @Override
//            protected void onPostExecute(String s) {
//                super.onPostExecute(s);
//
//                try {
//
//                    JSONObject jsonObj = new JSONObject(s);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    List<ModelDepartment> list = new ArrayList<>();
//
//                    for(int i=0;i<rs.length();i++){
//                        JSONObject c = rs.getJSONObject(i);
//
//                        if(c.getString("result").equals("A")) {
//                            list.add(
//                                    new ModelDepartment(
//                                            c.getString("xID"),
//                                            c.getString("xDepName"),
//                                            c.getString("xDepName2"),
//                                            "0"
//                                    )
//                            );
//
//                            DepID = c.getString("xID");
//                            DepName = c.getString("xDepName");
//
//                        }else{
//                            Toast.makeText(CssdPayout.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//
//                    Model_Department = list;
//
//                    final ArrayAdapter<ModelDepartment> adapter = new ListDepartmentAdapter(CssdPayout.this, list, switch_department.isChecked() ? "#D6EAF8" : "#FFFFFF");
//                    list_department.setAdapter(adapter);
//
//                    if(list.size() > 0) {
//                        if(IsPlaySound)
////                            ok();
//
//                        // Select first index
//                        list_department.setSelection(0);
//                        ((ListDepartmentAdapter) adapter).setSelection(0);
//
//                        // Set Form
//                        DocNo = null;
//                        RefDocNo = null;
//                        switch_opt.setChecked(false);
//
//                        try {
//
//                            if(DepID == null){
//                                list_payout_detail_item.setAdapter(null);
//                                list_pay.setAdapter(null);
//                            }else {
//
////                                label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
//
//                                // Display document
//                                displayPay(DepID, null);
//
//                                list_payout_detail_item.setAdapter(null);
//
//                                hideKeyboard(DispensingActivity.this);
//
//                            }
//
//                        }catch (Exception e){
//
//                        }
//
//                    }else{
////                        no();
//                    }
//
//                    list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                            ((ListDepartmentAdapter) adapter).setSelection(position);
//
//                            DepID = ((TextView)((LinearLayout) view).getChildAt(0)).getText().toString();
//                            DepName = ((TextView)((LinearLayout) view).getChildAt(2)).getText().toString();
//                            DocNo = null;
//                            RefDocNo = null;
//                            switch_opt.setChecked(false);
//
//                            try {
//
//                                if(DepID == null){
//                                    list_payout_detail_item.setAdapter(null);
//                                    list_pay.setAdapter(null);
//                                }else {
//
//                                    label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
//
//                                    // Pay
//                                    displayPay(DepID, null);
//
//                                    list_payout_detail_item.setAdapter(null);
//
//                                    hideKeyboard(CssdPayout.this);
//
//                                }
//
//                            }catch (Exception e){
//
//                            }
//                        }
//                    });
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }finally {
//                    focus();
//                }
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String,String>();
//
//                data.put("p_used_in_payout", "p");
//                data.put("p_id", p_qr);
//
//                String result = null;
//
//                try {
//                    result = httpConnect.sendPostRequest(Url.URL + "cssd_select_department.php", data);
//                    Log.d("DOJCLD", String.valueOf(data));
//                    Log.d("DOJCLD", String.valueOf(result));
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//        }
//
//        DepartmentByQR ru = new DepartmentByQR();
//
//        ru.execute();
//    }

    public void byConfig() {
        // -----------------------------------------------------------------------------------------
        // Get Config
        // -----------------------------------------------------------------------------------------
        PA_IsNotificationPopupExpiringScan = ((CssdProject) getApplication()).isPA_IsNotificationPopupExpiringScan();
//        ST_SoundAndroidVersion9 = false;
        ST_SoundAndroidVersion9 = ((CssdProject) getApplication()).isST_SoundAndroidVersion9();
        Log.d("tog_LoadConfig", "PA_IsNotificationPopupExpiringScan = " + PA_IsNotificationPopupExpiringScan);
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

        SR_ReceiveFromDeposit = ((CssdProject) getApplication()).isSR_ReceiveFromDeposit();

        MD_IsUsedSoundScanQR = ((CssdProject) getApplication()).isMD_IsUsedSoundScanQR();

        WA_IsUsedWash = ((CssdProject) getApplication()).isWA_IsUsedWash();

        ST_IsUsedNotification = ((CssdProject) getApplication()).isST_IsUsedNotification();

        PA_IsUsedZonePayout = ((CssdProject) getApplication()).isPA_IsUsedZonePayout();
        PA_IsCreateReceiveDepartment = ((CssdProject) getApplication()).isPA_IsCreateReceiveDepartment();

        Log.d("tog_LoadConfig", "PA_IsCreateReceiveDepartment = " + PA_IsCreateReceiveDepartment);
        PA_IsEditManualPayoutQty = ((CssdProject) getApplication()).isPA_IsEditManualPayoutQty();
        PA_IsUsedApprover = ((CssdProject) getApplication()).isPA_IsUsedApprover();
        PA_IsUsedRecipienter = ((CssdProject) getApplication()).isPA_IsUsedRecipienter();
        PA_IsConfirmClosePayout = ((CssdProject) getApplication()).isPA_IsConfirmClosePayout();
        PA_IsUsedFIFO = ((CssdProject) getApplication()).isPA_IsUsedFIFO();
        PA_IsWastingPayout = ((CssdProject) getApplication()).isPA_IsWastingPayout();

        PA_IsUsedPayOkSound = ((CssdProject) getApplication()).isPA_IsUsedPayOkSound();

        Log.d("tog_LoadConfig", "PA_IsUsedPayOkSound = " + PA_IsUsedPayOkSound);
        //======================================================
        //  M1
        //======================================================
        Is_imageCreate = ((CssdProject) getApplication()).getcM1().get(0).getActive();
        Is_Zone = ((CssdProject) getApplication()).getcM1().get(7).getActive();
    }

    private void checkInput(String input) {

        Log.d("tog_flow", "checkInput");

//        B_IsNonSelectDocument = true;
        if (switch_opt.isChecked()) {
            removeItem(input);
        } else {
//            Log.d("OOOO","Length : "+input.length());
            checkDuplicate(input);
        }
    }

    public void checkDuplicate(final String usagecode) {
        Log.d("tog_flow", "checkDuplicate = " + usagecode);
        class checkDuplicate extends AsyncTask<String, Void, String> {

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

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getInt("Cnt") > 0) {

                            Log.d("tog_flow", "Cnt = 1");
                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("repeat_scan");
                                }
                            }

                        } else {

                            Log.d("tog_flow", "SR_ReceiveFromDeposit = " + SR_ReceiveFromDeposit);
                            if (SR_ReceiveFromDeposit) {
                                addItem(usagecode);
                            } else {
                                Log.d("tog_flow", "Cnt = 0");

//                                    if (PA_IsUsedFIFO) {
//                                        String arr[] = usagecode.split("-");
//                                        String itemcode = arr[0];
//                                        Log.d("tog_flow","checkFIFO ");
//                                        checkFIFO(itemcode, usagecode);
//                                    } else {
//                                        Log.d("tog_flow","checkExpiring ");
//                                        checkExpiring(usagecode);
//                                    }


                                if (((CssdProject) getApplication()).Project().equals("SIPH")){
                                    f_checkExpiring(usagecode);
                                }else{
                                    if (PA_IsUsedFIFO) {
                                        f_checkExpiring(usagecode);
                                    } else {
                                        f_checkCloseExpiring(usagecode);
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_usedcode", usagecode);
                data.put("p_docno", (DocNo == null ? "" : DocNo));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("xxx","p_itemcode : "+itemcode);
//                Log.d("xxx","p_usedcode : "+usagecode);
//                Log.d("xxx","p_docno : "+(DocNo==null?"":DocNo ));

                String result = null;

                try {
//                    Log.d("OOOO","result : "+((CssdProject) getApplication()).getxUrl() + "cssd_check_duplicate.php?"+data);
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_duplicate.php", data);
//                    Log.d("OOOO","result : "+result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_duplicate", "data = " + data);
                Log.d("tog_duplicate", "result = " + result);

                return result;
            }
        }


        if(false){
            checkDuplicate ru = new checkDuplicate();
            ru.execute();
        }else{
            boolean check_Duplicate = false;
            for(int i = 0 ; i < Model_Payout_Detail_item.size(); i++){

                if (Model_Payout_Detail_item.get(i).getUsageCode().equals(usagecode)){

                    check_Duplicate = true;

                }

            }

            if(check_Duplicate == false){
                if (((CssdProject) getApplication()).Project().equals("SIPH")){
                    f_checkExpiring(usagecode);
                }else{
                    if (PA_IsUsedFIFO) {
                        f_checkExpiring(usagecode);
                    } else {
                        f_checkCloseExpiring(usagecode);
                    }
                }
            }
        }


    }

    // **1
    public void checkFIFO(final String itemcode, final String usagecode) {
        class checkFIFO extends AsyncTask<String, Void, String> {

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

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("result").equals("A")) {
//                            Log.d("xxx",c.getString("Cnt"));
                            final String xUsageCode = c.getString("usedcode");

                            Log.d("tog_FIFO", "checkFIFO Cnt = " + c.getString("Cnt"));
                            if (c.getString("Cnt").equals("0")) {

                                checkExpiring(xUsageCode);

                            } else {
//                                Log.d("xxx","fifo...");
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("fifo");
                                    } else {
                                        nMidia.getAudio("fifo");
                                    }
                                }

                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                checkExpiring(xUsageCode);
                                                dialog.dismiss();
                                                // Do stuff if user accepts
                                            }
                                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Do stuff when user neglects.
                                            }
                                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                dialog.dismiss();
                                                // Do stuff when cancelled
                                            }
                                        }).create();
                                dialog.show();
                            }
                        }

                        if (c.getString("result").equals("R")) {

                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("repeat_scan");
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_itemcode", itemcode);
                data.put("p_usedcode", usagecode);
                data.put("p_docno", (DocNo == null ? "" : DocNo));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("xxx","p_itemcode : "+itemcode);
//                Log.d("xxx","p_usedcode : "+usagecode);
//                Log.d("xxx","p_docno : "+(DocNo==null?"":DocNo ));

                String result = null;

                try {
                    Log.d("xxx", "result : " + ((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php", data);
                    Log.d("xxx", "result : " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        checkFIFO ru = new checkFIFO();

        ru.execute();
    }

    public void checkExpiring(final String usagecode) {
        class checkExpiring extends AsyncTask<String, Void, String> {

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

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        Log.d("xxx", "result : " + c.getString("result"));
                        final String xUsageCode = c.getString("usedcode");
                        if (c.getString("result").equals("A")) {
                            Log.d("xxx", "result : " + c.getString("DateDiff"));

                            Log.d("tog_flow", "ST_SoundAndroidVersion9 : " + ST_SoundAndroidVersion9);
                            if (c.getInt("DateDiff") < 0) {
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        Log.d("tog_flow", "speakText : ");
                                        speakText("expire");
                                    } else {
                                        Log.d("tog_flow", "nMidia : ");
                                        nMidia.getAudio("expire");
                                    }
                                }
                            } else if (c.getInt("DateDiff") < 4) {
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("close expiring ");
                                    } else {
                                        nMidia.getAudio("expiring");
                                        nMidia.getAudio("within");
                                    }
                                }

                                if (c.getInt("DateDiff") == 0) {
                                    if (MD_IsUsedSoundScanQR) {
                                        if (ST_SoundAndroidVersion9) {
                                            speakText("today");
                                        } else {
                                            nMidia.getAudio("today");
                                        }
                                    }

                                } else {
                                    if (MD_IsUsedSoundScanQR) {
                                        if (ST_SoundAndroidVersion9) {
                                            speakText("in " + c.getString("DateDiff") + " day");
                                        } else {
                                            nMidia.getAudio(c.getString("DateDiff"));
                                            nMidia.getAudio("day");
                                        }
                                    }

                                }
                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addItem(xUsageCode);
                                                dialog.dismiss();
                                                // Do stuff if user accepts
                                            }
                                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Do stuff when user neglects.
                                            }
                                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                dialog.dismiss();
                                                // Do stuff when cancelled
                                            }
                                        }).create();

                                if (PA_IsNotificationPopupExpiringScan) {
                                    dialog.show();
                                } else {
                                    s_expiring = true;
                                    addItem(xUsageCode);
                                }

                            } else {
                                addItem(xUsageCode);
                            }
                        }

                        if (c.getString("result").equals("E")) {
                            if ((xUsageCode.length() == 6) || (xUsageCode.length() == 11)) {
                                addItem(xUsageCode);
                            } else {
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("no");
                                    } else {
                                        nMidia.getAudio("no_item_found");
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_usedcode", usagecode);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    Log.d("tog_exp", "URL = " + ((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php", data);
                    Log.d("tog_exp", "data = " + data);
                    Log.d("tog_exp", "result = " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        checkExpiring ru = new checkExpiring();

        ru.execute();
    }

    // **2

    public void CheckItem(final String p_usage_code) {

        class CheckItem extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

//                    Model_Payout_Detail_item = new ArrayList<>();

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    int Qty_pay = 0;
                    int Qty_Break = 0;

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            focus();

                            Qty_pay = Integer.parseInt(c.getString("Qty_topay"));
                            Qty_Break = Integer.parseInt(c.getString("Qty_Break"));

                            Log.d("BANKTEST",Qty_pay+"");
                            Log.d("BANKTEST",Qty_Break+"");

                            addItemError(p_usage_code,"0");

                            if (c.getString("Qty_pay").equals("0")){

                                SetAdapter(c.getString("ItemCode"),c.getString("Qty_pay"),c.getString("itemname"),c.getString("Stock"),c.getString("RefDocNo"),p_usage_code);

                            }else {
                                if (c.getString("IsWasting").equals("1")){

                                    displayPayoutDetail(DocNo, false);

                                }else {

                                    SetAdapter(c.getString("ItemCode"),c.getString("Qty_pay"),c.getString("itemname"),c.getString("Stock"),c.getString("RefDocNo"),p_usage_code);

                                }
                            }

                        }else if (c.getString("result").equals("E1")){

                            focus();

                            addItemError(p_usage_code,"0");

                        }else {

                            focus();

//                            addItem(p_usage_code,"0");
                            addItem(p_usage_code);

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_usage_code", p_usage_code.toUpperCase());
                if(DocNo != null) {
                    data.put("p_docno", DocNo);
                }
                data.put("p_qty", "1");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_DeptID", DepID);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_status_item_topay.php", data);

                Log.d("tog_detail","data = "+data);
                Log.d("tog_detail","result = "+result);

                return result;
            }
        }

        CheckItem ru = new CheckItem();

        ru.execute();
    }

    boolean test_2811 = false;
    String add_usage_test_2811 = "";
    public void f_checkExpiring(final String usagecode) {
        class checkExpiring extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                add_usage_test_2811 = "";
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        Log.d("xxx", "result : " + c.getString("result"));
                        final String xUsageCode = c.getString("usedcode");
                        if (c.getString("result").equals("A")) {

                            //test
                            if(test_2811){
                                if (c.getInt("DateDiff") <= 0) {
                                    for(int iii=0;iii<scan_log_listItems.size();iii++){
                                        if(scan_log_listItems.get(iii).equals(xUsageCode)){
                                            scan_log_listItems.set(iii,xUsageCode+" (Expire)");
                                            if (Block_5.getVisibility() == View.VISIBLE) {
                                                Log.d("scan_log_adapter", "notifyDataSetChanged : 1");
                                                scan_log_adapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                } else {
                                    add_usage_test_2811 = add_usage_test_2811+","+xUsageCode;
                                }
                            }else{
                                Log.d("xxx", "result : " + c.getString("DateDiff"));

                                Log.d("tog_flow", "DateDiff : " + c.getString("DateDiff"));
                                if (c.getInt("DateDiff") <= 0) {
                                    if (MD_IsUsedSoundScanQR) {
                                        if (ST_SoundAndroidVersion9) {
                                            Log.d("tog_flow", "speakText : ");
                                            speakText("expire");
                                        } else {
                                            Log.d("tog_flow", "nMidia : ");
                                            nMidia.getAudio("expire");
                                        }
                                    }

                                } else {

                                    if (PA_IsUsedFIFO) {
                                        String arr[] = usagecode.split("-");
                                        String itemcode = arr[0];
                                        Log.d("tog_flow", "checkFIFO ");
                                        f_checkFIFO(itemcode, usagecode);
                                    } else {
                                        Log.d("tog_flow", "checkExpiring ");

                                        if (((CssdProject) getApplication()).Project().equals("SIPH")) {
                                            addItem(xUsageCode);
                                        }else{
                                            f_checkCloseExpiring(usagecode);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }

                if(test_2811){
                    addItem_test_2811(add_usage_test_2811);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_usedcode", usagecode);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    Log.d("tog_exp", "URL = " + ((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php", data);
                    Log.d("tog_exp", "data = " + data);
                    Log.d("tog_exp", "result = " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        checkExpiring ru = new checkExpiring();

        ru.execute();
    }

    public void f_checkFIFO(final String itemcode, final String usagecode) {
        class checkFIFO extends AsyncTask<String, Void, String> {

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

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("result").equals("A")) {
//                            Log.d("xxx",c.getString("Cnt"));
                            final String xUsageCode = c.getString("usedcode");

                            Log.d("tog_FIFO", "checkFIFO Cnt = " + c.getString("Cnt"));
                            if (c.getString("Cnt").equals("0")) {

                                f_checkCloseExpiring(xUsageCode);

                            } else {
//                                Log.d("xxx","fifo...");
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("fifo");
                                    } else {
                                        nMidia.getAudio("fifo");
                                    }
                                }

                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addItem(xUsageCode);
                                                dialog.dismiss();
                                                // Do stuff if user accepts
                                            }
                                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Do stuff when user neglects.
                                            }
                                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                dialog.dismiss();
                                                // Do stuff when cancelled
                                            }
                                        }).create();
                                dialog.show();
                            }
                        }

                        if (c.getString("result").equals("R")) {
                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("repeat_scan");
                                }
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_itemcode", itemcode);
                data.put("p_usedcode", usagecode);
                data.put("p_docno", (DocNo == null ? "" : DocNo));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("xxx","p_itemcode : "+itemcode);
//                Log.d("xxx","p_usedcode : "+usagecode);
//                Log.d("xxx","p_docno : "+(DocNo==null?"":DocNo ));

                String result = null;

                try {
                    Log.d("xxx", "result : " + ((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php", data);
                    Log.d("xxx", "result : " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        checkFIFO ru = new checkFIFO();

        ru.execute();
    }

    public void f_checkCloseExpiring(final String usagecode) {
        class checkExpiring extends AsyncTask<String, Void, String> {

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

                    List<ModelDepartment> list = new ArrayList<>();

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        Log.d("xxx", "result : " + c.getString("result"));
                        final String xUsageCode = c.getString("usedcode");
                        if (c.getString("result").equals("A")) {
                            Log.d("xxx", "result : " + c.getString("DateDiff"));

                            Log.d("tog_flow", "ST_SoundAndroidVersion9 : " + ST_SoundAndroidVersion9);
                            if (c.getInt("DateDiff") < 4) {
                                if (MD_IsUsedSoundScanQR) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("close expiring ");
                                    } else {
                                        nMidia.getAudio("expiring");
                                        nMidia.getAudio("within");
                                    }
                                }

                                if (c.getInt("DateDiff") == 0) {
                                    if (MD_IsUsedSoundScanQR) {
                                        if (ST_SoundAndroidVersion9) {
                                            speakText("today");
                                        } else {
                                            nMidia.getAudio("today");
                                        }
                                    }

                                } else {
                                    if (MD_IsUsedSoundScanQR) {
                                        if (ST_SoundAndroidVersion9) {
                                            speakText("in " + c.getString("DateDiff") + " day");
                                        } else {
                                            nMidia.getAudio(c.getString("DateDiff"));
                                            nMidia.getAudio("day");
                                        }
                                    }

                                }
                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addItem(xUsageCode);
                                                dialog.dismiss();
                                                // Do stuff if user accepts
                                            }
                                        }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                // Do stuff when user neglects.
                                            }
                                        }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                                            @Override
                                            public void onCancel(DialogInterface dialog) {
                                                dialog.dismiss();
                                                // Do stuff when cancelled
                                            }
                                        }).create();

                                if (PA_IsNotificationPopupExpiringScan) {
                                    dialog.show();
                                } else {
                                    s_expiring = true;

                                    if (((CssdProject) getApplication()).Project().equals("VCH")) {
                                        addItem(xUsageCode);
                                    }else{
                                        CheckItem(usagecode);
                                    }
                                }

                            } else {
                                if (((CssdProject) getApplication()).Project().equals("VCH")) {
                                    addItem(xUsageCode);
                                }else{
                                    CheckItem(usagecode);
                                }

                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_usedcode", usagecode);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    Log.d("tog_exp", "URL = " + ((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php", data);
                    Log.d("tog_exp", "data = " + data);
                    Log.d("tog_exp", "result = " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        checkExpiring ru = new checkExpiring();

        ru.execute();
    }

    // ------------------------------------------------------------------
    // Check ItemCode
    // ------------------------------------------------------------------

    public void checkItemCode(final String p_item_code) {
        class Check extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            openPopupEnterQty(p_item_code, c.getString("itemname"), Integer.valueOf(c.getString("c")).intValue());

                            //callDialogAddItemCode(p_item_code, c.getString("itemname"), Integer.valueOf(c.getString("c")).intValue());
                        } else {
                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("no");
                                }
                            }
                            callDialog(c.getString("Message"));
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_item_code", p_item_code);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("OOOO",((CssdProject) getApplication()).getxUrl() + "cssd_check_item_stock_by_item_code.php?"+ data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_item_stock_by_item_code.php", data);
//                Log.d("OOOO","result: ");
                return result;
            }
        }

        Check c = new Check();

        c.execute();
    }

    public void openPopupEnterQty(String p_item_code, String p_item_name, int p_count) {
//        Intent intent = new Intent(CssdPayout.this, CssdEnterPayout.class);
//        intent.putExtra("p_item_code", p_item_code);
//        intent.putExtra("p_item_name", p_item_name);
//        intent.putExtra("p_count", p_count);
//        startActivityForResult(intent, 1000);
    }

    // ------------------------------------------------------------------
    // Add Payout
    // ------------------------------------------------------------------

    public void addItemError(final String p_usage_code, final String p_expiring) {

        Log.d("tog_flow","addItem");
        final boolean B_Is_Borrow = false; // switch_type.isChecked();

        class Add extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        String result = c.getString("result_code");
                        String msg = c.getString("Message");

                        System.out.println("result = " + c.getString("result"));
                        System.out.println("result_code = " + result);
                        //System.out.println("msg = " + msg);
//                        Toast.makeText(DispensingActivity.this, "result: "+c.getString("result"), Toast.LENGTH_SHORT).show();
                        if (c.getString("result").equals("A")) {
//                            Toast.makeText(DispensingActivity.this, "result: "+c.getString("PayQty"), Toast.LENGTH_SHORT).show();
                            Log.d("OOOO","PayQty: "+c.getString("PayQty"));
                            if( c.getInt("BQty") == c.getInt("PayQty") ) {
//                                nMidia.getAudio("pay_in_full" );

                                Log.d("tog_s_expiring", "s_expiring: " + s_expiring);
                                Log.d("tog_s_expiring", "PA_IsUsedPayOkSound: " + PA_IsUsedPayOkSound);
                                Log.d("tog_s_expiring", "ST_SoundAndroidVersion9: " + ST_SoundAndroidVersion9);
                                if (!s_expiring) {
                                    s_expiring = false;
                                    if (MD_IsUsedSoundScanQR) {
                                        if (PA_IsUsedPayOkSound) {
                                            if (ST_SoundAndroidVersion9) {
                                                speakText("complete");
                                            } else {
                                                nMidia.getAudio("okay");
                                            }
                                        }
                                    }
                                }

                            }else{
                                Log.d("tog_s_expiring","s_expiring: "+s_expiring);
                                if(!s_expiring){
                                    s_expiring = false;
                                    if (MD_IsUsedSoundScanQR) {
                                        if (PA_IsUsedPayOkSound) {
                                            if (ST_SoundAndroidVersion9) {
                                                speakText(c.getString("PayQty"));
                                            } else {
                                                nMidia.getAudio(c.getString("PayQty"));
                                            }
                                        }
                                    }

                                }
                            }

                            Log.d("tog_flow","addItem DocNo = "+DocNo);

                            if(DocNo == null){

                                Log.d("tog_flow","B_IsNonSelectDocument = "+B_IsNonSelectDocument);

                                DocNo = c.getString("DocNo");

                                if(B_IsNonSelectDocument){

                                    // Clear Department
                                    list_department.setAdapter(null);

                                    // Display Payout NA
                                    displayDocumentNA();

                                }else {

                                    // Display Department
//                                    if (!switch_department.isChecked()) {
//                                        displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//                                    }

                                    // Display Payout
                                    displayPay(DepID, DocNo,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
                                }

                                switch_opt.setChecked(false);
                                Block_1.setVisibility( View.GONE );
                                Block_2.setVisibility( View.GONE );
                                Block_3.setVisibility( View.VISIBLE );
                                Block_4.setVisibility( View.VISIBLE );
                                switch_opt.setVisibility( View.VISIBLE );
                                imageCreate.setVisibility( Is_imageCreate?View.VISIBLE:View.GONE );
                                if(Model_Pay.size()>0){
                                    title_3.setText(DocNo+" / "+Model_Pay.get(0).getDepName()+" (M)");
                                }else{
                                    title_3.setText(DocNo+" / "+DepName+" (M)");
                                }

                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params1.width = Is_imageCreate?255:505;
                                params1.setMargins(0, 0, 15, 0);
                                linear_layout_search.setLayoutParams(params1);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.width = 250;
                                imageCreate.setLayoutParams(params2);
                                // Display Payout Detail
                                displayPayoutDetail(DocNo, false);

                            }else if(result.equals("3")){

                                callDialog(msg);

                                return;

                            }else if(result.equals("6")){

                                callDialog(msg);

                                // Display Payout Detail
                                displayPayoutDetail(DocNo, false);

                                return;

                            }else if(result.equals("7")){

                                callQR("user_approve", msg);

                                // Display Payout Detail
                                displayPayoutDetail(DocNo, false);

                                return;

                            }else if(result.equals("8")){

                                callCloseDocument();

                                // Display Payout Detail
                                displayPayoutDetail(DocNo, false);

                            } else if(!result.equals("0")){

                                // 1, 4, 5

                                callDialog(msg);

                                clearAll(false);

                                // Display Payout Document
                                /*if(result.equals("5")){
                                    clearAll();
                                }else {
                                    displayPay(DepID, null);
                                }*/

                                // Clear
                                /*list_payout_detail_item.setAdapter(null);*/

                                return;
                            }else{
                                // Display Payout Detail
                                //displayPayoutDetail(DocNo, false);
                            }

                        }else{
                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("no");
                                }
                            }
//                            callDialog(msg);

                            if (c.getString("result").equals("E")){
                                callDialog(msg,"0");
                            }else {
                                callDialog(msg,"1");
                            }

                            p_usage_code_pay = p_usage_code.toUpperCase();
                            Docno_paylog = c.getString("DocNo");
                            Dep_paylog = c.getString("DepName");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if(B_IsNonSelectDocument){
                    data.put("p_is_non_department", "1");
                    data.put("p_DeptID", "-1");
                }else if(DepID != null) {
                    data.put("p_DeptID", DepID);
                }

                if(PA_IsUsedApprover){
                    data.put("p_IsUsedApprover", "1");
                }

                if(PA_IsUsedRecipienter){
                    data.put("p_IsUsedRecipienter", "1");
                }

                if(PA_IsConfirmClosePayout){
                    data.put("IsConfirmClosePayout", "1");
                }

                data.put("SS_IsGroupPayout", SS_IsGroupPayout ? "1" : "0");

                data.put("SR_ReceiveFromDeposit", SR_ReceiveFromDeposit ? "1" : "0");

                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");

                data.put("p_docno", DocNo == null ? "" : DocNo);
                data.put("p_is_manual", (RefDocNo == null || RefDocNo.trim().equals("")) ? "1" : "0");
                data.put("p_is_borrow", B_Is_Borrow ? "1" : "0");
                data.put("p_usage_code", p_usage_code.toUpperCase());
                data.put("p_qty", "1");
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid()+"");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_device", "M1");

                Log.d("OOOO",((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php?"+ data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php", data);
                Log.d("tog_add_pay_detail","data : "+data);
                Log.d("tog_add_pay_detail","result : "+result);
                return result;
            }
        }

        Add ru = new Add();

        ru.execute();
    }
    ArrayList<String> addItem_p_usage_code = new ArrayList<String>();
    int add_item_loop =0;
    public void addItem(final String p_usage_code) {


        Log.d("tog_add_pay_loop", "addItem --- p_usage_code : " + p_usage_code);
        Log.d("tog_flow", "addItem");
        final boolean B_Is_Borrow = false; // switch_type.isChecked();
        String p_is_borrow = B_Is_Borrow ? "1" : "0";

        class Add extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                add_item_loop++;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                add_item_loop--;

                Log.d("tog_add_pay_loop", "add_item_loop : " + add_item_loop);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        String result = c.getString("result_code");
                        String msg = c.getString("Message");

                        System.out.println("result = " + c.getString("result"));
                        System.out.println("result_code = " + result);
                        if (c.getString("result").equals("A")) {
                            Log.d("OOOO", "PayQty: " + c.getString("PayQty"));
                            if (c.getInt("BQty") == c.getInt("PayQty")) {

                                Log.d("tog_s_expiring", "s_expiring: " + s_expiring);
                                if (!s_expiring) {
                                    s_expiring = false;
                                    if (MD_IsUsedSoundScanQR) {
                                        if (PA_IsUsedPayOkSound) {
                                            if (ST_SoundAndroidVersion9) {
                                                speakText("complete");
                                            } else {
                                                nMidia.getAudio("okay");
                                            }
                                        }
                                    }
                                }

                            } else {
                                Log.d("tog_s_expiring", "s_expiring: " + s_expiring);
                                if (!s_expiring) {
                                    s_expiring = false;
                                    if (MD_IsUsedSoundScanQR) {
                                        if (PA_IsUsedPayOkSound) {
                                            if (ST_SoundAndroidVersion9) {
                                                speakText(c.getString("PayQty"));
                                            } else {
                                                nMidia.getAudio(c.getString("PayQty"));
                                            }
                                        }
                                    }
                                }
                            }

//                            for(int iii=0;iii<scan_log_listItems.size();iii++){
//                                if(scan_log_listItems.get(iii).equals(p_usage_code)){
//                                    scan_log_listItems.set(iii,p_usage_code+" (✓)");
//                                    if (Block_5.getVisibility() == View.VISIBLE) {
//                                        Log.d("scan_log_adapter", "notifyDataSetChanged : 2");
//                                        scan_log_adapter.notifyDataSetChanged();
//                                    }
//                                }
//                            }


                            Log.d("tog_flow", "addItem DocNo = " + DocNo);

                            if (DocNo == null) {

                                Log.d("tog_flow", "B_IsNonSelectDocument = " + B_IsNonSelectDocument);

                                DocNo = c.getString("DocNo");

                                if (B_IsNonSelectDocument) {

                                    // Clear Department
                                    list_department.setAdapter(null);

                                    // Display Payout NA
                                    displayDocumentNA();

                                } else {

                                    // Display Department
//                                    if (!switch_department.isChecked()) {
//                                        displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//                                    }

                                    // Display Payout
                                    displayPay(DepID, DocNo, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
                                }

                                switch_opt.setChecked(false);
                                Block_1.setVisibility(View.GONE);
                                Block_2.setVisibility(View.GONE);
                                Block_3.setVisibility(View.VISIBLE);
                                Block_4.setVisibility(View.VISIBLE);
                                switch_opt.setVisibility(View.VISIBLE);
                                imageCreate.setVisibility(Is_imageCreate ? View.VISIBLE : View.GONE);
                                if (Model_Pay.size() > 0) {
                                    title_3.setText(DocNo + " / " + Model_Pay.get(0).getDepName() + " (M)");
                                } else {
                                    title_3.setText(DocNo + " / " + DepName + " (M)");
                                }

                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params1.width = Is_imageCreate ? 255 : 505;
                                params1.setMargins(0, 0, 15, 0);
                                linear_layout_search.setLayoutParams(params1);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.width = 250;
                                imageCreate.setLayoutParams(params2);
                                // Display Payout Detail
//                                displayPayoutDetail(DocNo, false);

                                if(addItem_p_usage_code.size()>0){
                                    Log.d("tog_add_pay_loop", "NON displayPayoutDetail : " + addItem_p_usage_code.get(0));
                                    addItem(addItem_p_usage_code.get(0));
                                    addItem_p_usage_code.remove(0);
                                }else{
                                    if(add_item_loop ==0){
                                        Log.d("tog_add_pay_loop", "displayPayoutDetail : " + p_usage_code);
                                        displayPayoutDetail(DocNo, false);
                                    }
                                }

                            } else if (result.equals("3")) {

                                callDialog(msg);

                                return;

                            } else if (result.equals("6")) {

                                callDialog(msg);

                                // Display Payout Detail
                                if(addItem_p_usage_code.size()>0){
                                    Log.d("tog_add_pay_loop", "NON displayPayoutDetail : " + addItem_p_usage_code.get(0));
                                    addItem(addItem_p_usage_code.get(0));
                                    addItem_p_usage_code.remove(0);
                                }else{
                                    if(add_item_loop ==0){
                                        Log.d("tog_add_pay_loop", "displayPayoutDetail : " + p_usage_code);
                                        displayPayoutDetail(DocNo, false);
                                    }
                                }

                                return;

                            } else if (result.equals("7")) {

                                callQR("user_approve", msg);

                                // Display Payout Detail
                                if(addItem_p_usage_code.size()>0){
                                    Log.d("tog_add_pay_loop", "NON displayPayoutDetail : " + addItem_p_usage_code.get(0));
                                    addItem(addItem_p_usage_code.get(0));
                                    addItem_p_usage_code.remove(0);
                                }else{
                                    if(add_item_loop ==0){
                                        Log.d("tog_add_pay_loop", "displayPayoutDetail : " + p_usage_code);
                                        displayPayoutDetail(DocNo, false);
                                    }
                                }

                                return;

                            } else if (result.equals("8")) {

                                callCloseDocument();

                                // Display Payout Detail
                                if(addItem_p_usage_code.size()>0){
                                    Log.d("tog_add_pay_loop", "NON displayPayoutDetail : " + addItem_p_usage_code.get(0));
                                    addItem(addItem_p_usage_code.get(0));
                                    addItem_p_usage_code.remove(0);
                                }else{
                                    if(add_item_loop ==0){
                                        Log.d("tog_add_pay_loop", "displayPayoutDetail : " + p_usage_code);
                                        displayPayoutDetail(DocNo, false);
                                    }
                                }

                            } else if (!result.equals("0")) {

                                // 1, 4, 5

                                callDialog(msg);

                                clearAll(false);

                                // Display Payout Document
                                /*if(result.equals("5")){
                                    clearAll();
                                }else {
                                    displayPay(DepID, null);
                                }*/

                                // Clear
                                /*list_payout_detail_item.setAdapter(null);*/

                                return;
                            } else {
                                // Display Payout Detail
                                if(addItem_p_usage_code.size()>0){
                                    Log.d("tog_add_pay_loop", "NON displayPayoutDetail : " + addItem_p_usage_code.get(0));
                                    addItem(addItem_p_usage_code.get(0));
                                    addItem_p_usage_code.remove(0);
                                }else{
                                    if(add_item_loop ==0){
                                        Log.d("tog_add_pay_loop", "displayPayoutDetail : " + p_usage_code);
                                        displayPayoutDetail(DocNo, false);
                                    }
                                }
                            }

                        } else {
                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("no");
                                }
                            }
//                            callDialog(msg);

                            if (c.getString("result").equals("E")) {
                                callDialog(msg, "0");
                            } else {
                                callDialog(msg, "1");
                            }

                            p_usage_code_pay = p_usage_code.toUpperCase();
                            Docno_paylog = c.getString("DocNo");
                            Dep_paylog = c.getString("DepName");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if (B_IsNonSelectDocument) {
                    data.put("p_is_non_department", "1");
                    data.put("p_DeptID", "-1");
                } else if (DepID != null) {
                    data.put("p_DeptID", DepID);
                }

                if (PA_IsUsedApprover) {
                    data.put("p_IsUsedApprover", "1");
                }

                if (PA_IsUsedRecipienter) {
                    data.put("p_IsUsedRecipienter", "1");
                }

                if (PA_IsConfirmClosePayout) {
                    data.put("IsConfirmClosePayout", "1");
                }

                data.put("SS_IsGroupPayout", SS_IsGroupPayout ? "1" : "0");

                data.put("SR_ReceiveFromDeposit", SR_ReceiveFromDeposit ? "1" : "0");

                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");

                data.put("p_docno", DocNo == null ? "" : DocNo);
                data.put("p_is_manual", (RefDocNo == null || RefDocNo.trim().equals("")) ? "1" : "0");
                data.put("p_is_borrow", p_is_borrow);
                data.put("p_usage_code",p_usage_code.toUpperCase());
                data.put("p_qty", "1");
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_device", "M1");

                Log.d("OOOO", ((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php?" + data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php", data);
                Log.d("tog_add_pay_detail", "data : " + data);
                Log.d("tog_add_pay_detail", "result : " + result);

//                Log.d("tog_add_pay_loop", "p_usage_code :  --- "+p_usage_code);
//                Log.d("tog_add_pay_loop", "result : " + result);
                return result;
            }
        }

        if(add_item_loop ==0){
            Log.d("tog_add_pay_loop", add_item_loop+" data : 1 ---"+p_usage_code);
            Add AddItem = new Add();
            AddItem.execute();
        }else if(DocNo != null){
            Log.d("tog_add_pay_loop", add_item_loop+" data : 2 ---"+p_usage_code);
            Add AddItem = new Add();
            AddItem.execute();
        }else{
            Log.d("tog_add_pay_loop", add_item_loop+" data : 3 ---"+p_usage_code);
            addItem_p_usage_code.add(p_usage_code);
        }

    }

    public void addItem_test_2811(final String p_usage_code) {


        Log.d("tog_add_pay_loop", "addItem --- p_usage_code : " + p_usage_code);
        Log.d("tog_flow", "addItem");
        final boolean B_Is_Borrow = false; // switch_type.isChecked();
        String p_is_borrow = B_Is_Borrow ? "1" : "0";

        class Add extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                boolean is_dispaly = false;
                Log.d("tog_add_pay_loop", "add_item_loop : " + add_item_loop);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        String result = c.getString("result_code");
                        String msg = c.getString("Message");
                        String result_usage = c.getString("result_usage");

                        System.out.println("result = " + c.getString("result"));
                        System.out.println("result_code = " + result);
                        if (c.getString("result").equals("A")) {

                            if (DocNo == null) {

                                Log.d("tog_flow", "B_IsNonSelectDocument = " + B_IsNonSelectDocument);

                                DocNo = c.getString("DocNo");

                                if (B_IsNonSelectDocument) {

                                    // Clear Department
                                    list_department.setAdapter(null);

                                    // Display Payout NA
                                    displayDocumentNA();

                                } else {

                                    // Display Department
//                                    if (!switch_department.isChecked()) {
//                                        displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//                                    }

                                    // Display Payout
                                    displayPay(DepID, DocNo, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
                                }

                                switch_opt.setChecked(false);
                                Block_1.setVisibility(View.GONE);
                                Block_2.setVisibility(View.GONE);
                                Block_3.setVisibility(View.VISIBLE);
                                Block_4.setVisibility(View.VISIBLE);
                                switch_opt.setVisibility(View.VISIBLE);
                                imageCreate.setVisibility(Is_imageCreate ? View.VISIBLE : View.GONE);
                                if (Model_Pay.size() > 0) {
                                    title_3.setText(DocNo + " / " + Model_Pay.get(0).getDepName() + " (M)");
                                } else {
                                    title_3.setText(DocNo + " / " + DepName + " (M)");
                                }

                                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params1.width = Is_imageCreate ? 255 : 505;
                                params1.setMargins(0, 0, 15, 0);
                                linear_layout_search.setLayoutParams(params1);
                                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                params2.width = 250;
                                imageCreate.setLayoutParams(params2);
                                // Display Payout Detail
//                                displayPayoutDetail(DocNo, false);

                                is_dispaly = true;

                            } else if (result.equals("3")) {

                                callDialog(msg);

                            } else if (result.equals("6")) {

                                callDialog(msg);

                                is_dispaly = true;

                            } else if (result.equals("7")) {

                                callQR("user_approve", msg);

                                is_dispaly = true;

                                return;

                            } else if (result.equals("8")) {

                                callCloseDocument();

                                is_dispaly = true;

                            } else if (!result.equals("0")) {

                                callDialog(msg);

                                clearAll(false);

                            } else {
                                is_dispaly = true;
                            }

                            for(int iii=0;iii<scan_log_listItems.size();iii++){
                                if(scan_log_listItems.get(iii).equals(result_usage)){
                                    scan_log_listItems.set(iii,result_usage+" (✓)");
                                    scan_log_adapter.notifyDataSetChanged();
                                }
                            }

                        } else {
//                            callDialog(msg);

//                            if (c.getString("result").equals("E")) {
//                                callDialog(msg, "0");
//                            } else {
//                                callDialog(msg, "1");
//                            }

                            p_usage_code_pay = p_usage_code.toUpperCase();
                            Docno_paylog = c.getString("DocNo");
                            Dep_paylog = c.getString("DepName");

                            for(int iii=0;iii<scan_log_listItems.size();iii++){
                                if(scan_log_listItems.get(iii).equals(result_usage)){
                                    scan_log_listItems.set(iii,result_usage+" (X)"+msg);
                                    if (Block_5.getVisibility() == View.VISIBLE) {
                                        Log.d("scan_log_adapter", "notifyDataSetChanged : 3");
                                        scan_log_adapter.notifyDataSetChanged();
                                    }
                                }
                            }
                        }

                    }

                    if(is_dispaly){
                        displayPayoutDetail(DocNo, false);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if (B_IsNonSelectDocument) {
                    data.put("p_is_non_department", "1");
                    data.put("p_DeptID", "-1");
                } else if (DepID != null) {
                    data.put("p_DeptID", DepID);
                }

                if (PA_IsUsedApprover) {
                    data.put("p_IsUsedApprover", "1");
                }

                if (PA_IsUsedRecipienter) {
                    data.put("p_IsUsedRecipienter", "1");
                }

                if (PA_IsConfirmClosePayout) {
                    data.put("IsConfirmClosePayout", "1");
                }

                data.put("SS_IsGroupPayout", SS_IsGroupPayout ? "1" : "0");

                data.put("SR_ReceiveFromDeposit", SR_ReceiveFromDeposit ? "1" : "0");

                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");

                data.put("p_docno", DocNo == null ? "" : DocNo);
                data.put("p_is_manual", (RefDocNo == null || RefDocNo.trim().equals("")) ? "1" : "0");
                data.put("p_is_borrow", p_is_borrow);
                data.put("p_usage_code",p_usage_code.toUpperCase());
                data.put("p_qty", "1");
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_device", "M1");

                Log.d("OOOO", ((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php?" + data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage_list.php", data);
                Log.d("tog_add_pay_detail", "data : " + data);
                Log.d("tog_add_pay_detail", "result : " + result);

//                Log.d("tog_add_pay_loop", "p_usage_code :  --- "+p_usage_code);
//                Log.d("tog_add_pay_loop", "result : " + result);
                return result;
            }
        }

        Log.d("tog_add_pay_loop", add_item_loop+" data : 2 ---"+p_usage_code);
        Add AddItem = new Add();
        AddItem.execute();

    }

    private void clearAll(boolean isDisplayDepartment) {
        B_IsNonSelectDocument = false;

//        label_division.setText("เลือก: ");

        DocNo = null;
        RefDocNo = null;
        String DepID_Old = DepID;
        DepID = null;
        DepIndex = -1;
        DepName = "-";
        DocDateTime = "";

        p_receive_code = null;
        p_approve_code = null;

        list_payout_detail_item.setAdapter(null);
        list_pay.setAdapter(null);
        list_department.setAdapter(null);

        spn_usr_receive.setSelection(0);
//
//        // Display Department
//        if(switch_department.isChecked()) {
//            if(isDisplayDepartment) {
//                findDepartmentByQR(DepID_Old, false);
//            }
//        }else {
//            displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//        }

        displayDepartment(null, -1, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
        hideKeyboard(DispensingActivity.this);
    }


    // ------------------------------------------------------------------
    // Remove Item
    // ------------------------------------------------------------------

    public void removeItem(final String p_usage_code) {

        if (DocNo == null) {
            if (MD_IsUsedSoundScanQR) {
                if (ST_SoundAndroidVersion9) {
                    speakText("no");
                } else {
                    nMidia.getAudio("no");
                }
            }
            Toast.makeText(DispensingActivity.this, Cons.WARNING_SELECT_DOC, Toast.LENGTH_SHORT).show();
            focus();
            return;
        }

        class Remove extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
//                            nMidia.getAudio(c.getString("PayQty"));
//                            speakText(c.getString("PayQty"));
                            // Display Payout Detail
                            if (MD_IsUsedSoundScanQR) {
                                if (PA_IsUsedPayOkSound) {
                                    if (ST_SoundAndroidVersion9) {
                                        speakText("okay");
                                    } else {
                                        nMidia.getAudio("okay");
                                    }
                                }
                            }

                            displayPayoutDetail(DocNo, false);
                        } else {
                            callDialog(c.getString("Message"));

                            if (MD_IsUsedSoundScanQR) {
                                if (ST_SoundAndroidVersion9) {
                                    speakText("no");
                                } else {
                                    nMidia.getAudio("no");
                                }
                            }

                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", DocNo);
                data.put("p_usage_code", p_usage_code);
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("p_is_manual", (RefDocNo == null || RefDocNo.trim().equals("")) ? "1" : "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_remove_payout_detail_usage.php", data);

                Log.d("tog_remove", "data = " + data);
                Log.d("tog_remove", "result = " + result);
                return result;
            }
        }

        Remove ru = new Remove();

        ru.execute();
    }

    // ------------------------------------------------------------------
    // Open qr
    // ------------------------------------------------------------------

    private void callQR(String data, String decs) {
//        Intent i = new Intent(CssdPayout.this, CssdScanQr.class);
//        i.putExtra("data", data);
//        i.putExtra("decs", decs);
//        i.putExtra("B_ID", B_ID);
//        startActivityForResult(i, Master.getResult(data));
    }

    private void callCloseDocument() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(DispensingActivity.this);
        quitDialog.setTitle(Cons.TITLE);
        quitDialog.setIcon(R.drawable.pose_favicon_2x);
        quitDialog.setMessage(Cons.CONFIRM_CLOSE);

        quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (PA_IsUsedApprover) {
                    callQR("user_approve", "สแกนรหัสผู้อนุมัติจ่าย เพื่อปิดเอกสาร.");
                } else {
//                    updatePayout(DocNo, DepID);
                    if (((CssdProject) getApplication()).Project().equals("SIPH")||((CssdProject) getApplication()).Project().equals("RAMA") || ((CssdProject) getApplication()).Project().equals("BGH")) {
                        updatePayout(DocNo, DepID, "0");
                    } else if (((CssdProject) getApplication()).Project().equals("VCH")) {
                        updatePayout(DocNo, DepID, "3");
                    }

                }

            }
        });

        quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        quitDialog.show();
    }

    private void getQR(final String msg, final String type) {
        Intent i = new Intent(DispensingActivity.this, CheckQR_Approve.class);
        i.putExtra("xSel", "pay");
        i.putExtra("remark", msg);
        i.putExtra("DocNo", DocNo);
        i.putExtra("B_ID", 1);
        i.putExtra("type", type);
        startActivityForResult(i, 1050);
    }

    private void getQRPay(final String msg, final String type) {
        Log.d("tog_getQRPay", "d_id = " + txt_p_approve_code);

        if (d_id == "") {
            _getQRPay(msg, type);
        } else {
            select_dept(DepIndex);
        }
    }

    private void _getQRPay(final String msg, final String type) {
        Intent i = new Intent(DispensingActivity.this, CheckQR_Approve.class);
        i.putExtra("xSel", "payrow");
        i.putExtra("remark", msg);
        i.putExtra("DocNo", DocNo);
        i.putExtra("B_ID", "1");
        i.putExtra("type", type);

        Log.d("tog_flow", "getQRPay payrow 0 1155");
        startActivityForResult(i, 1155);
    }

    private void clearDocument() {

        DocNo = null;
        RefDocNo = null;

//        p_receive_code = null;
//        p_approve_code = null;

        list_payout_detail_item.setAdapter(null);

        spn_usr_receive.setSelection(0);

        if (B_IsNonSelectDocument) {
//            label_division.setText("เลือก : NA");
            displayDocumentNA();
        } else {
//            label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
            displayPay(DepID, null, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
        }

        hideKeyboard(DispensingActivity.this);
    }

    // ------------------------------------------------------------------
    // Update Payout
    // ------------------------------------------------------------------
    String udpo_p_dept_id = "";
    String udpo_type = "";

    public void updatePayout(final String p_docno, final String p_dept_id, final String type) {
        udpo_p_dept_id = p_dept_id;
        udpo_type = type;

        if (have_printer) {
            print_round(p_docno);
        } else {
            to_updatePayout(p_docno, p_dept_id, type);
        }
    }

    public void to_updatePayout(final String p_docno, final String p_dept_id, final String type) {

        if (!B_IsNonSelectDocument) {

            if (p_dept_id == null || p_dept_id.equals("") || p_dept_id.equals("0") || p_dept_id.equals("-1")) {

                return;
            }
        }

        class Update extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            DocNo = null;
                            RefDocNo = null;

                            list_payout_detail_item.setAdapter(null);
                            list_pay.setAdapter(null);
                            list_department.setAdapter(null);
                            Block_1.setVisibility(View.GONE);
                            Block_2.setVisibility(View.VISIBLE);
                            Block_3.setVisibility(View.GONE);
                            Block_4.setVisibility(View.VISIBLE);
                            switch_opt.setVisibility(View.GONE);
                            imageCreate.setVisibility(View.GONE);
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            linear_layout_search.setLayoutParams(params);
                            displayPay(DepID, null, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

                            spn_usr_receive.setSelection(0);

                            hideKeyboard(DispensingActivity.this);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    focus();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if (((CssdProject) getApplication()).getCustomerId() == 201) {
                    data.put("p_is_used_itemstock_department", "1");
                } else {
                    data.put("p_is_used_itemstock_department", "0");
                }

                if (p_dept_id != null) {
                    data.put("p_dept_id", p_dept_id);
                }

                if (PA_IsUsedRecipienter && p_approve_code != null) {
                    data.put("p_receive_code", p_receive_code);
                }

                if (PA_IsUsedApprover && p_approve_code != null) {
                    data.put("p_approve_code", p_approve_code);
                }

                data.put("p_userid", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("p_bid", ((CssdProject) getApplication()).getPm().getBdCode() + "");
                data.put("p_docno", DocNo);
                data.put("p_id", d_id);
                data.put("p_type", type);

                if (type.equals("1")) {
                    data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "0" : "0");
                } else if (type.equals("3")) {
                    data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");
                } else {
                    data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "0" : "0");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_Recipient", String.valueOf(spn_usr_receive.getSelectedItemPosition() - 1));

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout.php", data);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("tog_updatePayout", data + "");
                Log.d("tog_updatePayout", result + "");

                return result;
            }

            // -------------------------------------------------------------
        }

        Update obj = new Update();
        obj.execute();
    }

    public void print_round(String d_docno) {
        class print_round extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    if(rs.length() > 0){
                        JSONObject c = rs.getJSONObject(0);
                        getUserPay(d_docno, (c.getInt("Printno")) + "", "1");
                    }else{
                        getUserPay(d_docno, 1 + "", "1");
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }


            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_DocNo", d_docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_print_round_paydocno.php", data);

                return result;
            }
        }

        print_round ru = new print_round();

        ru.execute();
    }

    public void getuserCode() {
        class getuserCode extends AsyncTask<String, Void, String> {
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

                    xDataUserCode.clear();

                    for (int i = 0; i < rs.length(); i++) {

                        JSONObject c = rs.getJSONObject(i);

                        xDataUserCode.add(c.getString("xName"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(DispensingActivity.this, android.R.layout.simple_spinner_dropdown_item, xDataUserCode);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spn_usr_receive.setAdapter(adapter);

                    spn_usr_receive.setSelection(0);

                    spn_usr_receive_box.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "Itemstock/get_usageCode_spinner_itemstock_pay.php", data);
                Log.d("tog_getuserCode", result);
                return result;
            }
        }
        getuserCode ru = new getuserCode();
        ru.execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("onActivityResult", "onActivityResult = " + resultCode);
        try {

            if (resultCode == 1155) {

//                String d_round = data.getStringExtra("Printno");
//
//                Log.d("onActivityResult","d_round = "+d_round);
//
//                getUserPay(DocNo,d_round,"1");

                d_id = data.getStringExtra("RETURN_ID");
                Log.d("tog_getQRPay", "1155 d_id = " + d_id);
                txt_p_approve_code.setText("ชื่อผู้จ่าย : " + data.getStringExtra("RETURN_NAME"));
                select_dept(DepIndex);

            } else if (resultCode == 1035) {

                d_id = data.getStringExtra("RETURN_ID");

                Log.d("tog_updatePayout", "RETURN_Type = " + data.getStringExtra("RETURN_Type"));
                p_approve_code = data.getStringExtra("RETURN_VALUE");
                if (data.getStringExtra("RETURN_Type").equals("1")) {
                    updatePayout(DocNo, DepID, "3");
                } else {
                    updatePayout(DocNo, DepID, "0");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetPrintCountReportAndPrintCountSlip(String p_Type, String p_SubType, String p_Docno) {

        class SetPrintCountReportAndPrintCountSlip extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS_API);
//
//                    for (int i = 0; i < rs.length(); i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        if (c.getString("result").equals("A")) {
//
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_Type", p_Type);
                data.put("p_SubType", p_SubType);
                data.put("p_Docno", p_Docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_printcount_payout.php", data);

                Log.d("BANKTEST", data + "");
                Log.d("BANKTEST", result + "");

                return result;
            }
        }

        SetPrintCountReportAndPrintCountSlip ru = new SetPrintCountReportAndPrintCountSlip();
        ru.execute();
    }

    private void getUserPay(String p_Docno, String p_Round, String p_Type) {

        class getUserPay extends AsyncTask<String, Void, String> {

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        user_name_pay = "คุณ " + c.getString("FirstName") + " " + c.getString("LastName");


                        if (p_Type.equals("1")) {
                            printSlip(c.getString("PayDate"), c.getString("RefDocNo"), p_Round);
                            SetPrintCountReportAndPrintCountSlip("1", "0", p_Docno);
                            to_updatePayout(p_Docno, udpo_p_dept_id, udpo_type);
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_Docno", p_Docno);
                data.put("p_Round", p_Round);
                data.put("p_id", d_id);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_user_pay_round.php", data);

                Log.d("BANKTEST", data + "");
                Log.d("BANKTEST", result + "");

                return result;
            }
        }

        getUserPay ru = new getUserPay();
        ru.execute();
    }

    // ------------------------------------------------------------------
    // Select Zone
    // ------------------------------------------------------------------

    public void selectZone() {
        class selectZone extends AsyncTask<String, Void, String> {
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

                    ar_list_zone_id.clear();
                    ar_list_zone_id.add("");
                    ar_list_zone_name.add("");

                    for (int i = 0; i < setRs.length(); i++) {

                        JSONObject c = setRs.getJSONObject(i);
                        ar_list_zone_name.add(c.getString("xName"));
                        ar_list_zone_id.add(c.getString("xID"));

                    }

                    adapter_spinner = new ArrayAdapter<String>(DispensingActivity.this, android.R.layout.simple_spinner_dropdown_item, ar_list_zone_name);
                    adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spn_zone.setAdapter(adapter_spinner);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("B_ID", ((CssdProject) getApplication()).getPm().getBdCode() + "");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_zone.php", data);
                return result;
            }
        }

        selectZone ru = new selectZone();
        ru.execute();
    }

    // ------------------------------------------------------------------
    // Display Department
    // ------------------------------------------------------------------

    public void displayDepartment(final String pDepName, final int depIndex, final String p_zone) {

        Log.d("tog_RemovePayout", "displayDepartment");
        Log.d("tog_focus", "displayDepartment");
        final boolean is_borrow = false; // switch_type.isChecked();

        switch_mode.setChecked(false);

        class DisplayDepartment extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelDepartment> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        list.add(
                                new ModelDepartment(
                                        c.getString("xID"),
                                        c.getString("xDepName"),
                                        c.getString("xDepName2"),
                                        "0"
                                )
                        );

                    }

                    Model_Department = list;

                    Log.d("tog_list_department","Model_Department size = "+Model_Department.size());

                    list_department_adapter = new ListDepartmentAdapter(DispensingActivity.this, list, "#D6EAF8");
                    list_department.setAdapter(list_department_adapter);

                    list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ModelDepartment pos_dept = list_department_adapter.getItem(position);
                            for (int ii = 0; ii < Model_Department.size(); ii++) {
                                if (Model_Department.get(ii).getID().equals(pos_dept.getID())) {
                                    DepIndex = ii;
                                    break;
                                }
                            }
                            getQRPay("payrow", "0");
                        }
                    });


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    txt_search_department.requestFocus();
                    Log.d("tog_focus", "txt_search_department focus");
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_used_in_payout", "p");
                data.put("p_type", Is_department_or);
                if (pDepName != null && !pDepName.trim().equals("")) {
                    data.put("pDepName", pDepName);
                }

                data.put("p_is_borrow", is_borrow ? "1" : "0");

                if (Is_Zone) {
                    data.put("p_zone", p_zone);
                } else {
                    data.put("p_zone", "");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {

                    if (((CssdProject) getApplication()).Project().equals("SIPH")||((CssdProject) getApplication()).Project().equals("RAMA") || ((CssdProject) getApplication()).Project().equals("BGH")) {
                        result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_department.php", data);
                    } else if (((CssdProject) getApplication()).Project().equals("VCH")) {
                        result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_department_new.php", data);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                Log.d("tog_focus", "result = " + result);
                return result;
            }

        }

        DisplayDepartment obj = new DisplayDepartment();
        obj.execute();
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    public void select_dept(int position) {
        list_department.setSelection(position);
        ((ListDepartmentAdapter)list_department_adapter).setSelection(position);
        Log.d("tog_selectedPos","select_dept setSelection == "+ position);

        DepIndex = position;
        DepID = Model_Department.get(position).getID();
        DepName = Model_Department.get(position).getDepName2();
        DocNo = null;
        RefDocNo = null;
        DocDateTime = "";
        switch_opt.setChecked(false);

        title_2.setText(" แผนก: " + DepName);
        try {

            if (DepID == null) {
                list_payout_detail_item.setAdapter(null);
                list_pay.setAdapter(null);
            } else {

//                                    label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");

                // Pay
                displayPay(DepID, null, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

                list_payout_detail_item.setAdapter(null);

                hideKeyboard(DispensingActivity.this);

            }

        } catch (Exception e) {

        }
//                            title_2.setText( "  " );
        Block_1.setVisibility(View.GONE);
        Block_2.setVisibility(View.VISIBLE);
        Block_3.setVisibility(View.GONE);
        Block_4.setVisibility(View.VISIBLE);
        switch_opt.setVisibility(View.GONE);
        imageCreate.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        linear_layout_search.setLayoutParams(params);
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

    // ------------------------------------------------------------------
    // Display Payout (NA)
    // ------------------------------------------------------------------
    private void displayDocumentNA() {
        class DisplayPay extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayout> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            list.add(
                                    new ModelPayout(
                                            c.getString("Department_ID"),
                                            c.getString("DepName"),
                                            c.getString("DocNo"),
                                            c.getString("CreateDate"),
                                            c.getString("Qty"),
                                            c.getString("PayQty"),
                                            c.getString("Count_Qty"),
                                            c.getString("IsStatus"),
                                            c.getString("Payout_Status"),
                                            "",
                                            c.getString("RefDocNo"),
                                            c.getString("IsSpecial"),
                                            c.getString("IsWeb"),
                                            c.getString("DocDateTime"),
                                            c.getString("DocDateSend"),
                                            i
                                    )
                            );
                        }
                    }

                    Model_Pay = list;

                    // Set Adepter
                    final ArrayAdapter<ModelPayout> adapter = new ListPayoutDocumentAdapter(DispensingActivity.this, Model_Pay, false);
                    list_pay.setAdapter(adapter);

                    // OnItemLongClick
//                    list_pay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                        @Override
//                        public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long id) {
//
//                            ((ListPayoutDocumentAdapter) adapter).setSelection(pos);
//
//                            DocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
//                            RefDocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();
//                            DocDateTime = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(5)).getText().toString();
//
////                            callDialogMenu();
//
//                            displayPayoutDetail(DocNo, false);
//
//                            return true;
//                        }
//                    });

                    // OnClick
//                    list_pay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                            ((ListPayoutDocumentAdapter) adapter).setSelection(position);
//
//                            DocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
//                            RefDocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();
//                            switch_opt.setChecked(false);
//                            displayPayoutDetail(DocNo, true);
//                            Toast.makeText(DispensingActivity.this, "list_pay...!", Toast.LENGTH_SHORT).show();
//                            Block_1.setVisibility( View.GONE );
//                            Block_2.setVisibility( View.GONE );
//                            Block_3.setVisibility( View.VISIBLE );
//                            Block_4.setVisibility( View.VISIBLE );
//                            switch_opt.setVisibility( View.VISIBLE );
//                            imageCreate.setVisibility( View.VISIBLE );
//                        }
//                    });

                    // Set Select Index
                    Iterator li = Model_Pay.iterator();
                    int i = 0;

                    while (li.hasNext()) {

                        ModelPayout m = (ModelPayout) li.next();

                        //System.out.println(m.getCreateDate() + " == " + DateTime.getDate("dd-MM-yy"));

                        if (m.getCreateDate().equals(DateTime.getDate("dd-MM-yy"))) {
                            ((ListPayoutDocumentAdapter) adapter).setSelection(i);

                            DocNo = m.getDocNo();
                            RefDocNo = m.getRefDocNo();
                            DocDateTime = m.getDocDateTime();

                            displayPayoutDetail(DocNo, true);

                            break;
                        }

                        i++;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String result = null;

                data.put("p_is_non_department", "1");

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payouts.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d("tog_displayPay_NA", "data" + data);
                Log.d("tog_displayPay_NA", "result" + result);
                return result;
            }

            // -------------------------------------------------------------
        }

        DisplayPay obj = new DisplayPay();

        obj.execute();
    }

    public void focus() {
        txt_usage_code.setText("");
        txt_usage_code.requestFocus();

        Log.d("tog_focus", "focus()");
    }

    // ------------------------------------------------------------------
    // Display Payout
    // ------------------------------------------------------------------
    private void displayPay(final String p_department_id, final String p_docno, final String p_zone) {

        if (p_department_id == null && p_docno == null) {
            return;
        }

        final boolean IsShowAll = switch_mode.isChecked();

        class DisplayPay extends AsyncTask<String, Void, String> {

//            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
//                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayout> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            //System.out.println("RefDocNo = " + c.getString("RefDocNo") == null);

                            list.add(
                                    new ModelPayout(
                                            c.getString("Department_ID"),
                                            c.getString("DepName"),
                                            c.getString("DocNo"),
                                            c.getString("CreateDate"),
                                            c.getString("Qty"),
                                            c.getString("PayQty"),
                                            c.getString("Count_Qty"),
                                            c.getString("IsStatus"),
                                            c.getString("Payout_Status"),
                                            "",
                                            c.getString("RefDocNo"),
                                            c.getString("IsSpecial"),
                                            c.getString("IsWeb"),
                                            c.getString("DocDateTime"),
                                            c.getString("DocDateSend"),
                                            i
                                    )
                            );
                        }
                    }

                    Model_Pay = list;

                    // Set Adepter
                    final ArrayAdapter<ModelPayout> adapter = new ListPayoutDocumentAdapter(DispensingActivity.this, Model_Pay, false);
                    list_pay.setAdapter(adapter);

                    if (!WA_IsUsedWash) {
                        checkUsagecodeNotRetrun(DepID);
                    }

                    // OnClick
                    list_pay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Log.d("tog_listPay", "setOnItemClick");
                            ((ListPayoutDocumentAdapter) adapter).setSelection(position);

                            DocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
                            RefDocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();
                            RefDocNoSend = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(6)).getText().toString();
                            switch_opt.setChecked(false);

                            title_3.setText(DocNo + " / " + Model_Pay.get(position).getDepNameByPayItem());
                            displayPayoutDetail(DocNo, true);
                            Block_1.setVisibility(View.GONE);
                            Block_2.setVisibility(View.GONE);
                            Block_3.setVisibility(View.VISIBLE);
                            Block_4.setVisibility(View.VISIBLE);
                            switch_opt.setVisibility(View.VISIBLE);
                            imageCreate.setVisibility(Is_imageCreate ? View.VISIBLE : View.GONE);
                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.width = Is_imageCreate ? 255 : 505;
                            params1.setMargins(0, 0, 15, 0);
                            linear_layout_search.setLayoutParams(params1);
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params2.width = 250;
                            imageCreate.setLayoutParams(params2);

                            if (RefDocNo.equals("")) {
                                txt_doc_type.setVisibility(View.INVISIBLE);
                            } else {
                                txt_doc_type.setVisibility(View.VISIBLE);
                            }
                        }
                    });

                    list_pay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                            DocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
                            callDialogMenu(Model_Pay.get(i).getDocNo());
                            Log.d("tog_listPay", "setOnItemLong");

                            return true;
                        }
                    });

                    // Set Select Index
                    if (DocNo != null) {
                        Iterator li = Model_Pay.iterator();
                        int i = 0;

                        while (li.hasNext()) {

                            ModelPayout m = (ModelPayout) li.next();

                            if (m.getDocNo().equals(DocNo)) {
                                ((ListPayoutDocumentAdapter) adapter).setSelection(i);
                                break;
                            }

                            i++;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
//                    if (dialog.isShowing()) {
//                        dialog.dismiss();
//                    }

                    focus();

                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if (IsShowAll) {
                    data.put("p_show_all_docno", "1");
                } else {
                    data.put("p_show_accrued", "1");
                }

                if (p_department_id != null) {
                    data.put("p_department_id", p_department_id);
                } else if (p_docno != null) {
                    data.put("p_docno", p_docno);
                }

                data.put("p_is_borrow", "0"); //switch_type.isChecked() ? "1" : "0");

                if (Is_Zone) {
                    data.put("p_zone", p_zone);
                } else {
                    data.put("p_zone", "");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payouts.php", data);
                    Log.d("tog_displayPay", "data = " + data);
                    Log.d("tog_displayPay", "result = " + result);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // -------------------------------------------------------------
        }

        DisplayPay obj = new DisplayPay();
        obj.execute();
    }

    // ------------------------------------------------------------------
    // Check Qty Usagecode Borrow Not Retrun
    // ------------------------------------------------------------------

    public void checkUsagecodeNotRetrun(final String p_dept) {

        class checkUsagecodeNotRetrun extends AsyncTask<String, Void, String> {

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

                        String Str = "อุปกรณ์ที่ยืมและไม่ได้คืน" + " ( " + c.getString("d_usage_qty") + " )";
                        Toast.makeText(DispensingActivity.this, Str, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_dept", p_dept);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_count_usage_borrow_not_retrun.php", data);

                return result;
            }
        }

        checkUsagecodeNotRetrun ru = new checkUsagecodeNotRetrun();

        ru.execute();
    }


    // ------------------------------------------------------------------
    // Display Payout
    // ------------------------------------------------------------------

    public String getRefDocNo(final String RefDocNo) {
        return RefDocNo == null || RefDocNo.trim().equals("") ? "-" : RefDocNo;
    }

    public void displayPayoutDetail(final String p_docno, final boolean isShowDialog) {

        //System.out.println("p_docno 2 = " + p_docno);

//        title_2.setText( " แผนก: " + DepName +  " ใบจ่ายที่: " + p_docno + " อ้างอิง: " + getRefDocNo(RefDocNo) );

        DocNo = p_docno;

        class DisplayPayout extends AsyncTask<String, Void, String> {

//            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

//                if (isShowDialog) {
//                    this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
//                    this.dialog.show();
//                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayoutDetails> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            String RefDocNo = null;
                            if (!c.isNull("RefDocNo")) {
                                RefDocNo = c.getString("RefDocNo");
                            }
                            list.add(
                                    new ModelPayoutDetails(
                                            c.getString("ID"),
                                            c.getString("itemcode"),
                                            c.getString("itemname"),
                                            c.getString("ItemStockID"),
                                            c.getString("UsageCode"),

                                            c.getString("Pay_Qty"),
                                            c.getString("Stock_Qty"),
                                            c.getString("Qty"),
                                            c.getString("Balance_Qty"),
                                            RefDocNo,

                                            c.getString("IsWasting"),
                                            c.getString("IsReceiveNotSterile"),
                                            i
                                    )
                            );

//                            user_name_pay = "คุณ " + c.getString("FirstName") + " " + c.getString("LastName");
                        } else {
                            //System.out.println(c.getString("SQL"));
                        }
                    }

                } catch (JSONException e) {
                    list_payout_detail_item.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Payout_Detail_item = list;

                if (RefDocNo == null) {
                    RefDocNo = "";
                }

                if (RefDocNo.equals("")) {
                    txt_doc_type.setVisibility(View.INVISIBLE);
                } else {
                    txt_doc_type.setVisibility(View.VISIBLE);
                }

                ArrayAdapter<ModelPayoutDetails> adapter;
                adapter = new ListPayoutDetailItemAdapter(DispensingActivity.this, Model_Payout_Detail_item, PA_IsWastingPayout, RefDocNo);
                list_payout_detail_item.setAdapter(adapter);

//                if (isShowDialog && dialog.isShowing()) {
//                    dialog.dismiss();
//                }

                focus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_docno", p_docno);
                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail_by_item.php", data);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_dis_payout", "data = " + data);
                Log.d("tog_dis_payout", "result = " + result);

                return result;
            }


            // -------------------------------------------------------------
        }

        DisplayPayout obj = new DisplayPayout();
        obj.execute();
    }

//    public void displayPayoutDetail(final String p_docno, final boolean isShowDialog){
//
//        //System.out.println("p_docno 2 = " + p_docno);
//
////        title_2.setText( " แผนก: " + DepName +  " ใบจ่ายที่: " + p_docno + " อ้างอิง: " + getRefDocNo(RefDocNo) );
//
//        DocNo = p_docno;
//
//        class DisplayPayout extends AsyncTask<String, Void, String> {
//
//            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);
//
//            // variable
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//
//                if(isShowDialog) {
//                    this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
//                    this.dialog.show();
//                }
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                List<ModelPayoutDetails> list = new ArrayList<>();
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for(int i=0;i<rs.length();i++){
//                        JSONObject c = rs.getJSONObject(i);
//
//                        if(c.getString("result").equals("A")) {
//                            list.add(
//                                    new ModelPayoutDetails(
//                                            c.getString("ID"),
//                                            c.getString("itemcode"),
//                                            c.getString("itemname"),
//                                            c.getString("ItemStockID"),
//                                            c.getString("UsageCode"),
//
//                                            c.getString("Pay_Qty"),
//                                            c.getString("Stock_Qty"),
//                                            c.getString("Qty"),
//                                            c.getString("Balance_Qty"),
//                                            null,
//
//                                            c.getString("IsWasting"),
//                                            i
//                                    )
//                            );
//
////                            user_name_pay = "คุณ " + c.getString("FirstName") + " " + c.getString("LastName");
//                        }
//                    }
//
//                } catch (JSONException e) {
//                    list_payout_detail_item.setAdapter(null);
//                    e.printStackTrace();
//                }
//
//                Model_Payout_Detail_item = list;
//
////                ArrayAdapter<ModelPayoutDetailo> adapter;
////                adapter = new ListPayoutDetailItemOAdapter(DispensingActivity.this, Model_Payout_Detail_item);
////                list_payout_detail_item.setAdapter(adapter);
//
//                if (isShowDialog && dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//                focus();
//
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String,String>();
//                data.put("p_docno", p_docno);
//                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                String result = null;
//
//                try {
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail_by_item.php", data);
//                    Log.d("OOOO",data+"");
//                    Log.d("OOOO",result+"");
//
//                }catch(Exception e){
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//
//
//            // -------------------------------------------------------------
//        }
//
//        DisplayPayout obj = new DisplayPayout();
//        obj.execute();
//    }

    // ------------------------------------------------------------------
    // Update Payout Detail Qty
    // ------------------------------------------------------------------

    public void updateQty(final String p_id, final String p_qty) {

        //System.out.println("updateQty");

        class Update extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                hideKeyboard(DispensingActivity.this);

                focus();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", p_id);
                data.put("p_qty", p_qty);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_detail_qty.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // -------------------------------------------------------------
        }

        Update obj = new Update();
        obj.execute();
    }

    public void callDialogRemove(final String ID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DispensingActivity.this);
        builder.setCancelable(true);
        builder.setTitle("ยืนยัน");
        builder.setMessage("ต้องการลบรายการใช่หรือไม่ ?");
        builder.setPositiveButton("ใช่",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removePayoutDetail(ID);
                    }
                });
        builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog_ = builder.create();
        dialog_.show();
    }

    public void removePayoutDetail(final String p_id) {

        class Remove extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            // Display Payout Detail
                            displayPayoutDetail(DocNo, false);

                        } else {
                            callDialog(c.getString("Message"));
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", p_id);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_remove_payout_detail_id.php", data);

                return result;
            }
        }

        Remove ru = new Remove();

        ru.execute();
    }

    // ------------------------------------------------------------------
    // Display payout detail sub
    // ------------------------------------------------------------------
    public void openDialogPayoutDetailSub(final String id, final String code, final String name) {

        final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_payout_detail_sub);
        dialog.setCancelable(true);


        final GridView gridView = (GridView) dialog.findViewById(R.id.grid_payout_detail_sub);
        final TextView txt_caption = (TextView) dialog.findViewById(R.id.txt_caption);

        txt_caption.setText(code + " - " + name);

        ImageView button1 = (ImageView) dialog.findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*
                if(DocNo != null) {
                    displayPayoutDetail(DocNo);
                }
                */
                dialog.dismiss();
            }
        });

        // Display Sterile Log
        displayPayoutDetailSub(gridView, dialog, id);

        //dialog.show();
    }

    private void displayPayoutDetailSub(final GridView gridView, final Dialog dialog, final String id) {

        class DisplayPayoutDetailSub extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayoutDetailSub> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            list.add(
                                    new ModelPayoutDetailSub(
                                            c.getString("ID"),
                                            c.getString("ItemStockID"),
                                            c.getString("UsageCode"),
                                            i
                                    )
                            );
                        } else {
                            //System.out.println(c.getString("SQL"));
                        }
                    }

                } catch (JSONException e) {
                    gridView.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Payout_Detail_Sub = list;

                if (Model_Payout_Detail_Sub != null && Model_Payout_Detail_Sub.size() > 0) {

                    ArrayAdapter<ModelPayoutDetailSub> adapter;
                    adapter = new ListPayoutDetailSubAdapter(DispensingActivity.this, Model_Payout_Detail_Sub);
                    gridView.setAdapter(adapter);

                    dialog.show();

                    focus();
                } else {
                    Toast.makeText(DispensingActivity.this, Cons.WARNING_NOT_FOUND, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", id);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail_sub.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }


            // -------------------------------------------------------------
        }

        DisplayPayoutDetailSub obj = new DisplayPayoutDetailSub();
        obj.execute();
    }

    // ------------------------------------------------------------------
    // Display payout detail sub
    // ------------------------------------------------------------------
    public void openDialogPayoutDetailQty(final String id, final String code, final String name, final String qty, final String qty_enter_old) {

        //System.out.println("RefDocNo = " + RefDocNo);

        if ((RefDocNo == null || RefDocNo.trim().equals("") || RefDocNo.trim().equals("-")) && PA_IsEditManualPayoutQty) {

            final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.activity_cssd_enter_number);
            dialog.setCancelable(true);
            dialog.setTitle(Cons.TITLE);

            final TextView txt_item_name = (TextView) dialog.findViewById(R.id.txt_item_name);
            final TextView txt_caption = (TextView) dialog.findViewById(R.id.txt_caption);
            final EditText edt_qty = (EditText) dialog.findViewById(R.id.edt_qty);
            final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

            txt_caption.setText("ป้อนจำนวนจ่าย");
            txt_item_name.setText(code + " : " + name);
            edt_qty.setText(qty);

            btn_ok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    int I_Qty_Enter_New = 0;
                    int I_Qty_Enter_Old = 0;

                    try {
                        I_Qty_Enter_New = Integer.valueOf(edt_qty.getText().toString()).intValue();
                        I_Qty_Enter_Old = Integer.valueOf(qty_enter_old).intValue();

                        if (I_Qty_Enter_New < I_Qty_Enter_Old) {
                            Toast.makeText(DispensingActivity.this, "ป้อนจำนวนน้อยว่าที่เลือกไว้ !!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update
                        updatePayoutDetailQty(id, Integer.toString(I_Qty_Enter_New));

                    } catch (Exception e) {

                    } finally {
                        dialog.dismiss();
                    }

                }
            });

            dialog.show();

            edt_qty.selectAll();
        }

    }

    // ------------------------------------------------------------------
    // Update Payout Detail Qty
    // ------------------------------------------------------------------
    public void updatePayoutDetailQty(final String id, final String Qty_Enter_New) {

        class Update extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                //System.out.println(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            displayPayoutDetail(DocNo, false);
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", id);
                data.put("p_qty", Qty_Enter_New);
                data.put("p_is_edit_payout_detail_qty", "1");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_detail_qty.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // -------------------------------------------------------------
        }

        Update obj = new Update();
        obj.execute();
    }

    // ------------------------------------------------------------------
    // Call Dialog
    // ------------------------------------------------------------------

    final Handler handler_1 = new Handler();
    private Runnable runnable_1;

    private void callDialog(final String Message) {
        callDialog(Message, "0");
//        if (PA_IsShowToastDialog) {
//
//            final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);
//
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.dialog_warning);
//            dialog.setCancelable(false);
//
//            final TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
//            final TextView txt_message = (TextView) dialog.findViewById(R.id.txt_message);
//            final TextView txt_tmp = (TextView) dialog.findViewById(R.id.txt_tmp);
//            final ImageView bt_cancel = (ImageView) dialog.findViewById(R.id.bt_cancel);
//
//            txt_title.setText(Cons.TITLE);
//            txt_message.setText(Message);
//
//            bt_cancel.setOnClickListener(new View.OnClickListener() {
//                public void onClick(View v) {
//                    dialog.dismiss();
//                }
//            });
//
//            dialog.show();
//
//            txt_tmp.requestFocus();
//
//            runnable_1 = new Runnable() {
//
//                @Override
//                public void run() {
//                    dialog.dismiss();
//                    handler_1.removeCallbacks(runnable_1);
//                    handler_1.removeCallbacksAndMessages(null);
//                    focus();
//                }
//            };
//
//            handler_1.postDelayed(runnable_1, 2000);
//
//        }else{
//            Toast.makeText(DispensingActivity.this, Message, Toast.LENGTH_SHORT).show();
//        }

    }

    private void callDialog(final String Message, final String type) {


        if (PA_IsShowToastDialog) {

            final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);

            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_warning);
            dialog.setCancelable(false);

            final TextView txt_title = (TextView) dialog.findViewById(R.id.txt_title);
            final TextView txt_message = (TextView) dialog.findViewById(R.id.txt_message);
            final EditText edt_message = (EditText) dialog.findViewById(R.id.edt_message);
            final TextView txt_tmp = (TextView) dialog.findViewById(R.id.txt_tmp);
            final Button bt_cancel = (Button) dialog.findViewById(R.id.bt_cancel);
            final Button bt_submit_pay = (Button) dialog.findViewById(R.id.bt_submit_pay);
            final Button bt_cancel_pay = (Button) dialog.findViewById(R.id.bt_cancel_pay);

            if (Message.equals("หมายเหตุ")) {
                txt_message.setVisibility(View.GONE);
                edt_message.setVisibility(View.VISIBLE);

                //txt_title.setText("เอกสารนี้มีรายการค้างจ่าย กรุณาระบุหมายเหตุเพื่อปิดเอกสาร");
            } else {
                txt_message.setVisibility(View.VISIBLE);
                txt_message.setText(Message);

                txt_title.setText(Cons.TITLE);
            }

            if (!type.equals("1")) {
                bt_cancel.setVisibility(View.VISIBLE);
                bt_submit_pay.setVisibility(View.GONE);
                bt_cancel_pay.setVisibility(View.GONE);
            } else {
                bt_cancel.setVisibility(View.GONE);
                bt_submit_pay.setVisibility(View.VISIBLE);
                bt_cancel_pay.setVisibility(View.VISIBLE);
            }

            bt_cancel.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            bt_submit_pay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (Message.equals("หมายเหตุ")) {
                        if (edt_message.getText().toString().equals("")) {
                            Toast.makeText(DispensingActivity.this, "กรุณากรอกหมายเหตุ !!", Toast.LENGTH_SHORT).show();
                        } else {
//                            dialog_wait_scan("closepayout", "0");
                            getQR("closepayout", "0");
                            dialog.dismiss();
                        }
                    } else {
                        Delete_Pay_Detail(DocNo, p_usage_code_pay);
                        dialog.dismiss();
                    }
                }
            });

            bt_cancel_pay.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    if (Message.equals("หมายเหตุ")){
//                        dialog_wait_scan("closepayout", "1");
//                        dialog.dismiss();
//                    }else {
//                        Delete_Pay_Detail(DocNo,p_usage_code_pay);
//                        dialog.dismiss();
//                    }

                    dialog.dismiss();
                }
            });

            dialog.show();

            txt_tmp.requestFocus();

            if (!type.equals("1")) {

                if (!Message.equals("ไม่ตรงแผนก !")) {
                    runnable_1 = new Runnable() {

                        @Override
                        public void run() {
                            dialog.dismiss();
                            handler_1.removeCallbacks(runnable_1);
                            handler_1.removeCallbacksAndMessages(null);
                            focus();
                        }
                    };

                    handler_1.postDelayed(runnable_1, 2000);
                }

            }

        } else {
            Toast.makeText(DispensingActivity.this, Message, Toast.LENGTH_SHORT).show();
        }

    }

    public void Delete_Pay_Detail(final String DocNo, final String Usagecode) {

        class Delete_Pay_Detail extends AsyncTask<String, Void, String> {
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
                        addItem(Usagecode);
//                        addItem(Usagecode,"0");
                    }

                } catch (JSONException e) {
                }
            }

            //class connect php RegisterUserClass important !!!!!!!
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_docno", params[0]);
                data.put("p_usagecode", params[1]);
                data.put("p_bid", "1");
                data.put("p_docno_paylog", Docno_paylog);
                data.put("p_dep_paylog", Dep_paylog);
                data.put("p_docno_paylog_save", DocNo);
                data.put("p_dep_paylog_save", DepID);
                data.put("p_user_code", ((CssdProject) getApplication()).getCustomerId() + "");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_delete_detail_payout_on_change.php", data);

                return result;
            }
        }
        Delete_Pay_Detail ru = new Delete_Pay_Detail();
        ru.execute(DocNo, Usagecode);
    }

    private void printSlip(String DocDate, String RefDocno, String d_round) {

        try {
            String showDepName = DepIndex == -1 || Model_Department.size() == 0 ? "" : Model_Department.get(DepIndex).getDepName();

            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().setAlign(1);
            SunmiPrintHelper.getInstance().printText("ใบจ่ายอุปกรณ์\n", 32, true, false);
            SunmiPrintHelper.getInstance().printText("แผนก:" + showDepName + "       รอบ:" + d_round + "\n", 28, false, false);
            SunmiPrintHelper.getInstance().printText("วันที่:" + DocDate + " น. " + "\n", 28, false, false);
            SunmiPrintHelper.getInstance().setAlign(0);
            SunmiPrintHelper.getInstance().printText("ใบจ่าย:" + DocNo + "\n", 28, false, false);

            if (!RefDocno.equals("")) {
                SunmiPrintHelper.getInstance().printText("ใบส่งล้าง:" + RefDocNo + " " + RefDocNoSend + "น." + "\n", 28, false, false);
            }

            SunmiPrintHelper.getInstance().printText("************************************************\n", 24, false, false);

            String[] text_col = new String[]{"รายการ", "จำนวน"};

            int[] width = new int[]{5, 1};

            int[] align_col = new int[]{1, 1};

            SunmiPrintHelper.getInstance().printTable(text_col, width, align_col);

            int I_Sum = 0;
            int I_PayQty = 0;
            int I_No = 1;


            for (int i = 0; i < Model_Payout_Detail_item.size(); i++) {

                I_PayQty = Model_Payout_Detail_item.get(i).getPay_Qty_();

                String[] text = new String[]{I_No + ". " + Model_Payout_Detail_item.get(i).getItemname(), Model_Payout_Detail_item.get(i).getPay_Qty()};
                int[] align = new int[]{0, 1};

                SunmiPrintHelper.getInstance().printTable(text, width, align);

                I_Sum += I_PayQty;

                I_No++;

            }

            SunmiPrintHelper.getInstance().printText("\n", 28, false, false);

            String[] text_sum = new String[]{"รวม  ", Integer.toString(I_Sum)};
            int[] align_sum = new int[]{2, 1};

            SunmiPrintHelper.getInstance().printTable(text_sum, width, align_sum);

            SunmiPrintHelper.getInstance().setAlign(0);

            SunmiPrintHelper.getInstance().printText("************************************************\n", 24, false, false);

            SunmiPrintHelper.getInstance().setAlign(1);

            SunmiPrintHelper.getInstance().printQr(DocNo, 5, 0);

            if (WA_IsUsedWash) {
                SunmiPrintHelper.getInstance().printText("\nผู้จ่าย (CSSD) : " + user_name_pay + "\n", 28, false, false);
            } else {
                SunmiPrintHelper.getInstance().printText("\nผู้จ่าย (CSSD) : " + user_name_pay + "\n", 28, false, false);
            }

            SunmiPrintHelper.getInstance().printText("ผู้รับ (แผนก)  :  ____________________\n\n", 28, false, false);

            SunmiPrintHelper.getInstance().setAlign(1);

            if (!WA_IsUsedWash) {
                // SunmiPrintHelper.getInstance().printText("*** งานจ่ายกลาง " + OrgName + " *** \n\n", 22, false, false);
            }

            SunmiPrintHelper.getInstance().setAlign(2);

            SunmiPrintHelper.getInstance().feedPaper();

            SunmiPrintHelper.getInstance().cutpaper();

            SunmiPrintHelper.getInstance().initPrinter();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testPrint() {
        Toast.makeText(DispensingActivity.this, "Test print !!", Toast.LENGTH_SHORT).show();

        try {

            SunmiPrintHelper.getInstance().setAlign(0);
            SunmiPrintHelper.getInstance().printText("Test:" + DocNo + "\n", 28, false, false);
            SunmiPrintHelper.getInstance().setAlign(2);
            SunmiPrintHelper.getInstance().printQr(DocNo, 5, 0);
            SunmiPrintHelper.getInstance().feedPaper();
            SunmiPrintHelper.getInstance().cutpaper();

            SunmiPrintHelper.getInstance().initPrinter();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void dialog_wait_scan(String data,String type){
//        ProgressDialog wait_dialog = new ProgressDialog(DispensingActivity.this);
//
//        wait_dialog.setCancelable(false);
//
//        wait_dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ยกเลิก", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();//dismiss dialog
//            }
//        });
//
//        wait_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                int keyCode = keyEvent.getKeyCode();
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        Log.d("tog_diKeyListener","enter = "+mass_onkey);
//                        wait_dialog.dismiss();
//
//                        Checkuser(mass_onkey,DocNo,"pay",type);
//
//                        mass_onkey = "";
//
//                        return false;
//                    }
//
//                    int unicodeChar = keyEvent.getUnicodeChar();
//
//                    if(unicodeChar!=0){
//                        mass_onkey=mass_onkey+(char)unicodeChar;
//                        Log.d("tog_dispatchKey","unicodeChar = "+unicodeChar);
//                    }
//
//                    return false;
//                }
//                return false;
//            }
//        });
//
//        wait_dialog.show();
//    }

    public void Checkuser(String qr_code, String docno, String xsel, String type) {

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("check").equals("true")) {
                            if (type.equals("1")) {
                                updatePayout(DocNo, DepID, "1");
                            } else {
                                updatePayout(DocNo, DepID, "0");
                            }
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(DispensingActivity.this);
                            builder.setCancelable(true);
                            builder.setTitle("แจ้งเตือน !!");
                            builder.setMessage("ไม่พบรหัสผู้ใช้");
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("qr_code", params[0]);
                data.put("docno", params[1]);
                data.put("xsel", params[2]);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "chk_qr/check_qr.php", data);

                return result;
            }
        }

        Checkuser ru = new Checkuser();
        ru.execute(qr_code, docno, xsel);
    }

    @Override
    public void onBackPressed() {

        if (Block_1.getVisibility() == View.VISIBLE) {
            img_back_1.callOnClick();
        } else if (Block_2.getVisibility() == View.VISIBLE) {
            img_back_2.callOnClick();
        } else {
            img_back_3.callOnClick();
        }

    }

    public void removePayout(final String p_docno) {

        class RemovePayout extends AsyncTask<String, Void, String> {
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

                        if (c.getString("result").equals("A")) {

                            Log.d("tog_RemovePayout", "displayDepartment");
//                            displayDepartment(txt_search_department.getText().toString(), -1,"");
                            displayPay(DepID, null, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

                        } else {
                            Toast.makeText(DispensingActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    focus();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_user", ((CssdProject) getApplication()).I_CustomerId + "");
                data.put("p_docno", p_docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_remove_payout.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_RemovePayout", "result = " + result);
                return result;
            }
        }

        RemovePayout ru = new RemovePayout();

        ru.execute();
    }


    public void callDialogMenu(String p_docno) {

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

                        if (c.getString("result").equals("A")) {
                            AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("ยืนยันลบเอกสารนี้หรือไม่")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            removePayout(p_docno);
//                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }

                                    }).create();
                            dialog.show();
                        } else {
                            callDialog("หมายเหตุ", "1");
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

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_count_payout_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }
        }

        Check ru = new Check();

        ru.execute();
    }

    public void SetAdapter (final String Itemcode, final String Qty, final String itemname, final String Stock, final String RefDocNo,String p_usage){

        boolean check_model = false;

        Iterator li = Model_Payout_Detail_item.iterator();

        while (li.hasNext()) {

            ModelPayoutDetails m = (ModelPayoutDetails) li.next();

            if (m.getItemcode().equals(Itemcode)){

                check_model = true;

                int Pay_Qty = Integer.parseInt(m.getPay_Qty());

                int i_1 = Pay_Qty + 1;

                m.setPay_Qty(String.valueOf(i_1));

                if(!RefDocNo.equals("")){
                    int Balance = Integer.parseInt(m.getBalance());

                    int b = Balance - 1;

                    m.setBalance(String.valueOf(b));
                }

                int Stock_Qty = Integer.parseInt(m.getStock_Qty());

                int s_1 = Stock_Qty - 1;

                m.setStock_Qty(String.valueOf(s_1));


                ArrayAdapter<ModelPayoutDetails> adapter;
//                adapter = new ListPayoutDetailItemNewAdapter(CssdPayout.this, Model_Payout_Detail_item, RefDocNo, DocNo);
                adapter = new ListPayoutDetailItemAdapter(DispensingActivity.this, Model_Payout_Detail_item,PA_IsWastingPayout,RefDocNo);
                list_payout_detail_item.setAdapter(adapter);

            }
        }

        if(check_model == false) {

            //--------------------------------------------

            int stock = Integer.parseInt(Stock);

            String stc = String.valueOf(stock - 1);

            //--------------------------------------------

            int cnt = 1;

            String Cnt = String.valueOf(cnt);

            Model_Payout_Detail_item.add(
                    new ModelPayoutDetails(
                            "",
                            Itemcode,
                            itemname,
                            "",
                            p_usage,

                            "1",
                            stc,
                            Cnt,
                            "0",
                            "",

                            "",
                            "",
                            0
                    )
            );

            ArrayAdapter<ModelPayoutDetails> adapter;
//            adapter = new ListPayoutDetailItemNewAdapter(CssdPayout.this, Model_Payout_Detail_item, "", DocNo);
            adapter = new ListPayoutDetailItemAdapter(DispensingActivity.this, Model_Payout_Detail_item,PA_IsWastingPayout,RefDocNo);
            list_payout_detail_item.setAdapter(adapter);

        }
    }

    ArrayList<String> scan_log_listItems=new ArrayList<String>();

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        if (Block_1.getVisibility() == View.VISIBLE) {
            return super.dispatchKeyEvent(event);
        }

        if(linear_layout_search.getVisibility() == View.VISIBLE){
            return super.dispatchKeyEvent(event);
        }

        int keyCode = event.getKeyCode();

        Log.d("tog_allKey","keyCode = "+keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(keyCode == KeyEvent.KEYCODE_BACK ){
                onBackPressed();
            }
            else if (keyCode == KeyEvent.KEYCODE_ENTER) {

                Log.d("tog_allKey", "mass_usage_code = "+mass_usage_code);
                String mass = mass_usage_code;
                mass_usage_code = "";
                Log.d("tog_add_pay_usage_code", "Empty = "+mass);
                if (mass.length() > 0) {
                    if (Block_1.getVisibility() == View.GONE) {
//                        for(int iii=0;iii<scan_log_listItems.size();iii++){
//                            if(scan_log_listItems.get(iii).equals(mass)){
//                                return false;
//                            }
//                        }

                        if(test_2811){
                            //        B_IsNonSelectDocument = true;
                            if (switch_opt.isChecked()) {
                                removeItem(mass);
                            } else {
                                if (Block_5.getVisibility() == View.VISIBLE) {
                                    Log.d("scan_log_adapter", "notifyDataSetChanged : 4");
                                    scan_log_listItems.add(mass);
                                    scan_log_adapter.notifyDataSetChanged();
                                    scan_log_num.setText("Number = "+scan_log_listItems.size());
                                }else{
                                    Block_5.setVisibility(View.VISIBLE);
                                }
                            }

                        }else{
                            Log.d("tog_add_pay_usage_code", "checkInput = "+mass);
                            checkInput(mass);
                            Log.d("tog_dispatchKey","checkInput = "+mass);
                        }
                    }
                }
                return false;
            }

            int unicodeChar = event.getUnicodeChar();

            if(unicodeChar!=0){
                mass_usage_code=mass_usage_code+(char)unicodeChar;
            }

            Log.d("tog_dispatchKey","keyCode = "+keyCode);
            Log.d("tog_dispatchKey","mass_onkey = "+mass_usage_code);

            return false;
        }

        return super.dispatchKeyEvent(event);
    }
}