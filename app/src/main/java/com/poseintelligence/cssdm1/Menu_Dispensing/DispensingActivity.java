package com.poseintelligence.cssdm1.Menu_Dispensing;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDepartmentAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDetailItemAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDetailSubAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDocumentAdapter;
import com.poseintelligence.cssdm1.core.audio.iAudio;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.date.DateTime;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDepartment;
import com.poseintelligence.cssdm1.model.ModelPayout;
import com.poseintelligence.cssdm1.model.ModelPayoutDetailSub;
import com.poseintelligence.cssdm1.model.ModelPayoutDetails;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class DispensingActivity extends AppCompatActivity {

    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    private iAudio nMidia;

    private List<ModelDepartment> Model_Department;
    private List<ModelPayout> Model_Pay;
    private List<ModelPayoutDetails> Model_Payout_Detail_item;
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


    private boolean Is_imageCreate = false;
    private boolean Is_Zone = false;
    // ---------------------------------------------------------------------------------------------
    // Obj
    // ---------------------------------------------------------------------------------------------
    private LinearLayout Block_1;
    private LinearLayout Block_2;
    private LinearLayout Block_3;
    private LinearLayout Block_4;
    private RelativeLayout linear_layout_search;

    private ImageView img_back_1;
    private ImageView img_back_2;
    private ImageView img_back_3;
    private ImageView imageCreate;
    private SearchableSpinner spn_zone;

    private TextView txt_search_department;
    private TextView title_2;
    private TextView title_3;

    private ListView list_department;
    private ListView list_pay;
    private ListView list_payout_detail_item;

    private Button btn_search_department;

    private Switch switch_mode;
    private Switch switch_opt;

    private EditText txt_usage_code;
    // ---------------------------------------------------------------------------------------------
    // Obj
    // ---------------------------------------------------------------------------------------------
    private String DocNo = null;
    private String RefDocNo = null;
    private String DepID = null;
    private int DepIndex = -1;
    private String DepName = "-";
    private String DocDateTime = "";
    private String p_receive_code = null;
    private String p_approve_code = null;

    public static String text_search_department = "";

    boolean s_expiring = false;

    private TextToSpeech tts;

    public void speakText(String textContents) {
        tts.speak(textContents, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispensing);
        getSupportActionBar().hide();

        // -----------------------------------------------------------------------
        // Sound
        // -----------------------------------------------------------------------
        speakTextInit();
        nMidia = new iAudio(this);
        // -----------------------------------------------------------------------

        byWidget();

        byEvent();

        byConfig();
 // -----------------------------------------------------------------------
        Block_1.setVisibility( View.VISIBLE );
        Block_2.setVisibility( View.GONE );
        Block_3.setVisibility( View.GONE );
        Block_4.setVisibility( View.GONE );
        spn_zone.setVisibility(Is_Zone?View.VISIBLE:View.GONE);
        if(Is_Zone==false)
            displayDepartment(txt_search_department.getText().toString(), -1,"");

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
                Log.d("tog_textToSpeech","TextToSpeech i = "+i);

                if(i==TextToSpeech.SUCCESS){

                    Log.d("tog_textToSpeech","SUCCESS");
                    int language=tts.setLanguage(Locale.US);
                    if(language==TextToSpeech.LANG_MISSING_DATA || language==TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        Toast.makeText(DispensingActivity.this, "LANG MISSING DATA OR LANG NOT SUPPORTED", Toast.LENGTH_SHORT).show();
                    }else{
//                        Toast.makeText(DispensingActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();

//                        speakText("SUCCESS");
                    }
                }
                else{

                    Log.d("tog_textToSpeech","FAIL");
                    Toast.makeText(DispensingActivity.this, "Your Device Is Missing TTS", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void byWidget() {
        Block_1 = ( LinearLayout ) findViewById(R.id.Block1);
        Block_2 = ( LinearLayout ) findViewById(R.id.Block2);
        Block_3 = ( LinearLayout ) findViewById(R.id.Block3);
        Block_4 = ( LinearLayout ) findViewById(R.id.Block4);
        linear_layout_search = ( RelativeLayout ) findViewById(R.id.linear_layout_search);

        img_back_1 = ( ImageView ) findViewById(R.id.img_back_1);
        img_back_2 = ( ImageView ) findViewById(R.id.img_back_2);
        img_back_3 = ( ImageView ) findViewById(R.id.img_back_3);
        imageCreate = (ImageView) findViewById(R.id.imageCreate);

        spn_zone = ( SearchableSpinner ) findViewById(R.id.spn_zone);
        txt_search_department = (EditText) findViewById(R.id.txt_search_department);
        txt_usage_code = (EditText) findViewById(R.id.txt_usage_code);
        btn_search_department = (Button) findViewById(R.id.btn_search_department);
        list_department = (ListView) findViewById(R.id.list_department);
        list_pay = (ListView) findViewById(R.id.list_pay);

        switch_opt = (Switch) findViewById(R.id.switch_opt);
        switch_mode = (Switch) findViewById(R.id.switch_mode);

        title_2 = (TextView) findViewById(R.id.title_2);
        title_3 = (TextView) findViewById(R.id.title_3);
        list_payout_detail_item = (ListView) findViewById(R.id.list_payout_detail_item);
    }

    private void byEvent() {
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
                displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
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
                list_department_adapter.notifyDataSetChanged();
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
                Block_3.setVisibility( View.GONE );
                Block_4.setVisibility( View.GONE );
                switch_opt.setVisibility( View.GONE );
                imageCreate.setVisibility( View.GONE );
                txt_search_department.setText("");
                displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
            }
        });

        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                //System.out.println("SelectedItemPosition = " + DepIndex);

                if(isChecked){
                    switch_mode.setText("ทั้งหมด ");
                }else{
                    switch_mode.setText("ค้างจ่าย ");
                }

                //displayPay(DepID, null);

                clearDocument();

                /*if(switch_department.isChecked()){
                    findDepartmentByQR(DepID);
                }else{
                    displayDepartment(null, DepIndex);
                }*/
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
                Block_1.setVisibility( View.GONE );
                Block_2.setVisibility( View.VISIBLE );
                Block_3.setVisibility( View.GONE );
                Block_4.setVisibility( View.VISIBLE );
                switch_opt.setVisibility( View.GONE );
                imageCreate.setVisibility( View.GONE );
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                linear_layout_search.setLayoutParams(params);
                DocNo = null;
                RefDocNo = null;
            }
        });

        switch_opt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    switch_opt.setText("ลบ ");
                }else{
                    switch_opt.setText("เพิ่ม ");
                }
            }
        });

        imageCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check DocNo
                if(DocNo == null){
                    Toast.makeText( DispensingActivity.this, Cons.WARNING_SELECT_DOC, Toast.LENGTH_SHORT).show();
                    return;
                }

                //Check Scan Qty
                boolean B_HasPay = false;

                if(Model_Payout_Detail_item.size() > 0) {

                    Iterator li = Model_Payout_Detail_item.iterator();

                    while (li.hasNext()) {

                        ModelPayoutDetails m = (ModelPayoutDetails) li.next();

                        //System.out.println(m.getPay_Qty() + " = " + (!m.getPay_Qty().equals("")) + " && " + !m.getPay_Qty().equals("0"));

                        if (!m.getPay_Qty().equals("") && !m.getPay_Qty().equals("0")) {
                            B_HasPay = true;
                            break;
                        }

                    }
                }

                if(!B_HasPay){
                    Toast.makeText(DispensingActivity.this, "ไม่มีรายการจ่าย !!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check User Receive
//                if(PA_IsUsedRecipienter) {
//                    try {
//                        p_receive_code = data_user_receive_id.get(spn_usr_receive.getSelectedItem());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        Toast.makeText(CssdPayout.this, "ยังไม่ได้เลือกผู้รับ !!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//
//
//                    if (p_receive_code == null) {
//                        Toast.makeText(CssdPayout.this, "ยังไม่ได้เลือกผู้รับ !!", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }

                callCloseDocument();
            }

        });

        txt_usage_code.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
//                    speakText("Strat");
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String S_Code = txt_usage_code.getText().toString().toLowerCase();
                            Log.d("top_dep","S_Code = "+S_Code);
                            if(S_Code.substring(0,1).equals("d")){
                                boolean x = true;
                                for(int i = 0;i<Model_Department.size();i++){

                                    Log.d("top_dep","ID = "+Model_Department.get(i));
                                    if(Model_Department.get(i).equals(S_Code.substring(1))){
                                        list_department.setSelection(i);
                                        select_dept(i);
                                        x=false;

                                    }
                                }
                                if(x){
                                    Toast.makeText(DispensingActivity.this, "ไม่พบแผนก !!", Toast.LENGTH_SHORT).show();
                                }

                                txt_usage_code.setText("");
                                return false;
                            }else{
                                checkInput();
                            }


//                            String txt = txt_usage_code.getText().toString();
//                            if(!B_IsNonSelectDocument) {
//                                if (DepID != null) {
//                                    checkInput();
//                                } else {
//                                    findDepartmentByQR(txt, true);
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

        txt_search_department.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d("top_dep","default");
                text_search_department = txt_search_department.getText().toString();
                list_department_adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txt_search_department.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
//                    speakText("Strat");
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            String S_Code = txt_search_department.getText().toString().toLowerCase();
                            Log.d("top_dep","S_Code = "+S_Code);
                            if(S_Code.substring(0,1).equals("d") || S_Code.substring(0,1).equals("D")){
                                boolean x = true;
                                for(int i = 0;i<Model_Department.size();i++){

                                    Log.d("top_dep","ID = "+Model_Department.get(i).getID());
                                    if(Model_Department.get(i).getID().equals(S_Code.substring(1))){
                                        list_department.setSelection(i);
                                        select_dept(i);
                                        x=false;

                                    }
                                }
                                if(x){
                                    Toast.makeText(DispensingActivity.this, "ไม่พบแผนก !!", Toast.LENGTH_SHORT).show();
                                }

                                txt_search_department.setText("");
                                return false;
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
        ST_SoundAndroidVersion9 = ((CssdProject) getApplication()).isST_SoundAndroidVersion9();
        Log.d("tog_LoadConfig","PA_IsNotificationPopupExpiringScan = "+PA_IsNotificationPopupExpiringScan);
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
        PA_IsEditManualPayoutQty = ((CssdProject) getApplication()).isPA_IsEditManualPayoutQty();
        PA_IsUsedApprover = ((CssdProject) getApplication()).isPA_IsUsedApprover();
        PA_IsUsedRecipienter = ((CssdProject) getApplication()).isPA_IsUsedRecipienter();
        PA_IsConfirmClosePayout = ((CssdProject) getApplication()).isPA_IsConfirmClosePayout();
        PA_IsUsedFIFO  = ((CssdProject) getApplication()).isPA_IsUsedFIFO();
        PA_IsWastingPayout = ((CssdProject) getApplication()).isPA_IsWastingPayout();

        //======================================================
        //  M1
        //======================================================
        Is_imageCreate = ((CssdProject) getApplication()).getcM1().get(0).getActive();
        Is_Zone = ((CssdProject) getApplication()).getcM1().get(7).getActive();
    }

    private void checkInput(){

        String input = txt_usage_code.getText().toString();

//        B_IsNonSelectDocument = true;
        if(switch_opt.isChecked()) {
            removeItem(input);
        }else {
//            Log.d("OOOO","Length : "+input.length());
            checkDuplicate(input);
        }
    }

    public void checkDuplicate(final String usagecode) {
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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getInt("Cnt")>0){

                            if(ST_SoundAndroidVersion9){
                                speakText("no");
                            }else{
                                nMidia.getAudio("repeat_scan");
                            }
                        }else{

                            if (SR_ReceiveFromDeposit) {
                                addItem( usagecode );
                            }else{
                                if (PA_IsUsedFIFO) {
                                    String arr[] = usagecode.split("-");
                                    String itemcode = arr[0];
                                    Log.d("tog_FIFO","checkFIFO ");
                                    checkFIFO(itemcode, usagecode);
                                } else {
                                    checkExpiring(usagecode);
                                }
                            }
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
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_usedcode", usagecode);
                data.put("p_docno", (DocNo==null?"":DocNo ));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("xxx","p_itemcode : "+itemcode);
//                Log.d("xxx","p_usedcode : "+usagecode);
//                Log.d("xxx","p_docno : "+(DocNo==null?"":DocNo ));

                String result = null;

                try {
//                    Log.d("OOOO","result : "+((CssdProject) getApplication()).getxUrl() + "cssd_check_duplicate.php?"+data);
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_duplicate.php", data);
//                    Log.d("OOOO","result : "+result);
                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("tog_duplicate","data = "+data);
                Log.d("tog_duplicate","result = "+result);

                return result;
            }
        }

        checkDuplicate ru = new checkDuplicate();

        ru.execute();
    }

    public void checkFIFO(final String itemcode,final String usagecode) {
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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(c.getString("result").equals("A")) {
//                            Log.d("xxx",c.getString("Cnt"));
                            final String xUsageCode = c.getString("usedcode");

                            Log.d("tog_FIFO","checkFIFO Cnt = "+c.getString("Cnt"));
                            if(c.getString("Cnt").equals("0")) {

                                checkExpiring( xUsageCode );

                            }else{
//                                Log.d("xxx","fifo...");

                                if(ST_SoundAndroidVersion9){
                                    speakText("fifo");
                                }else{
                                    nMidia.getAudio("fifo");
                                }

                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                addItem( xUsageCode );
                                                checkExpiring( xUsageCode );
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
                        }if(c.getString("result").equals("R")) {

                            if(ST_SoundAndroidVersion9){
                                speakText("no");
                            }else{
                                nMidia.getAudio("repeat_scan");
                            }

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
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_itemcode", itemcode);
                data.put("p_usedcode", usagecode);
                data.put("p_docno", ( DocNo==null?"":DocNo ));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                Log.d("xxx","p_itemcode : "+itemcode);
//                Log.d("xxx","p_usedcode : "+usagecode);
//                Log.d("xxx","p_docno : "+(DocNo==null?"":DocNo ));

                String result = null;

                try {
                    Log.d("xxx","result : "+((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_fifo.php", data);
                    Log.d("xxx","result : "+result);
                }catch(Exception e){
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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        Log.d("xxx","result : "+c.getString("result"));
                        final String xUsageCode = c.getString("usedcode");
                        if(c.getString("result").equals("A")) {
                            Log.d("xxx", "result : " + c.getString("DateDiff"));

                            if(c.getInt("DateDiff")<0) {
                                if(ST_SoundAndroidVersion9){
                                    speakText("expire");
                                }else{
                                    nMidia.getAudio("expire");
                                }
                            }else if(c.getInt("DateDiff") < 4) {

                                if(ST_SoundAndroidVersion9){
                                    speakText("close expiring ");
                                }else{
                                    nMidia.getAudio("expiring");
                                    nMidia.getAudio("within");
                                }

                                if(c.getInt("DateDiff")==0){

                                    if(ST_SoundAndroidVersion9){
                                        speakText("today");
                                    }else{
                                        nMidia.getAudio("today");
                                    }

                                }else{

                                    if(ST_SoundAndroidVersion9){
                                        speakText("in "+c.getString("DateDiff")+" day");
                                    }else{
                                        nMidia.getAudio(c.getString("DateDiff"));
                                        nMidia.getAudio("day");
                                    }

                                }
                                AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("คุณต้องการเพิ่มรายการนี้หรือไม่")
                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                addItem( xUsageCode );
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

                                if(PA_IsNotificationPopupExpiringScan){
                                    dialog.show();
                                }else{
                                    s_expiring = true;
                                    addItem( xUsageCode );
                                }

                            }else{
                                addItem( xUsageCode );
                            }
                        }if(c.getString("result").equals("E")) {
                            if( (xUsageCode.length() == 6) || (xUsageCode.length() == 11) ) {
                                addItem( xUsageCode );
                            }else {

                                if(ST_SoundAndroidVersion9){
                                    speakText("no");
                                }else{
                                    nMidia.getAudio("no_item_found");
                                }
                            }
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
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_usedcode", usagecode);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    Log.d("tog_exp","URL = "+((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php");
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_expiring.php", data);
                    Log.d("tog_exp","data = "+data);
                    Log.d("tog_exp","result = "+result);
                }catch(Exception e){
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

    public void checkItemCode(final String p_item_code){
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
                        }else{

                            if(ST_SoundAndroidVersion9){
                                speakText("no");
                            }else{
                                nMidia.getAudio("no");
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

    public void openPopupEnterQty(String p_item_code, String p_item_name, int p_count){
//        Intent intent = new Intent(CssdPayout.this, CssdEnterPayout.class);
//        intent.putExtra("p_item_code", p_item_code);
//        intent.putExtra("p_item_name", p_item_name);
//        intent.putExtra("p_count", p_count);
//        startActivityForResult(intent, 1000);
    }

    // ------------------------------------------------------------------
    // Add Payout
    // ------------------------------------------------------------------

    public void addItem(final String p_usage_code) {

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

                                Log.d("tog_s_expiring","s_expiring: "+s_expiring);
                                if(!s_expiring){
                                    s_expiring = false;
                                    if(ST_SoundAndroidVersion9){
                                        speakText("complete");
                                    }else{
                                        nMidia.getAudio("okay" );
                                    }
                                }
                                
                            }else{
                                Log.d("tog_s_expiring","s_expiring: "+s_expiring);
                                if(!s_expiring){
                                    s_expiring = false;
                                    if(ST_SoundAndroidVersion9){
                                        speakText(c.getString("PayQty"));
                                    }else{
                                        nMidia.getAudio(c.getString("PayQty"));
                                    }
                                }
                            }
                            if(DocNo == null){

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
                                title_3.setText(DocNo+" / "+DepName);
                                Block_1.setVisibility( View.GONE );
                                Block_2.setVisibility( View.GONE );
                                Block_3.setVisibility( View.VISIBLE );
                                Block_4.setVisibility( View.VISIBLE );
                                switch_opt.setVisibility( View.VISIBLE );
                                imageCreate.setVisibility( Is_imageCreate?View.VISIBLE:View.GONE );
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
                                displayPayoutDetail(DocNo, false);
                            }

                        }else{
                            if(ST_SoundAndroidVersion9){
                                speakText("no");
                            }else{
                                nMidia.getAudio("no");
                            }
                            callDialog(msg);

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
                Log.d("OOOO","data : "+result);
                Log.d("OOOO","result : "+result);
                return result;
            }
        }

        Add ru = new Add();

        ru.execute();
    }

    private void clearAll(boolean isDisplayDepartment){
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

//        spn_usr_receive.setSelection(0);
//
//        // Display Department
//        if(switch_department.isChecked()) {
//            if(isDisplayDepartment) {
//                findDepartmentByQR(DepID_Old, false);
//            }
//        }else {
//            displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//        }

        displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
        hideKeyboard(DispensingActivity.this);
    }



    // ------------------------------------------------------------------
    // Remove Item
    // ------------------------------------------------------------------

    public void removeItem(final String p_usage_code) {

        if(DocNo == null){

            if(ST_SoundAndroidVersion9){
                speakText("no");
            }else{
                nMidia.getAudio("no");
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

                            if(ST_SoundAndroidVersion9){
                                speakText("okay");
                            }else{
                                nMidia.getAudio("okay");
                            }

                            displayPayoutDetail(DocNo, false);
                        }else{
                            callDialog(c.getString("Message"));

                            if(ST_SoundAndroidVersion9){
                                speakText("no");
                            }else{
                                nMidia.getAudio("no");
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
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid()+"");
                data.put("p_is_manual", (RefDocNo == null || RefDocNo.trim().equals("")) ? "1" : "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_remove_payout_detail_usage.php", data);

                Log.d("tog_remove","data = "+data);
                Log.d("tog_remove","result = "+result);
                return result;
            }
        }

        Remove ru = new Remove();

        ru.execute();
    }

    // ------------------------------------------------------------------
    // Open qr
    // ------------------------------------------------------------------

    private void callQR(String data, String decs){
//        Intent i = new Intent(CssdPayout.this, CssdScanQr.class);
//        i.putExtra("data", data);
//        i.putExtra("decs", decs);
//        i.putExtra("B_ID", B_ID);
//        startActivityForResult(i, Master.getResult(data));
    }

    private void callCloseDocument(){
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(DispensingActivity.this);
        quitDialog.setTitle(Cons.TITLE);
        quitDialog.setIcon(R.drawable.pose_favicon_2x);
        quitDialog.setMessage(Cons.CONFIRM_CLOSE);

        quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

//                if(PA_IsUsedApprover){
//                    callQR("user_approve", "สแกนรหัสผู้อนุมัติจ่าย เพื่อปิดเอกสาร.");
//                }else{
//                    updatePayout(DocNo, DepID);
//                }
                updatePayout(DocNo, DepID);
            }
        });

        quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        quitDialog.show();
    }

    private void clearDocument(){

        DocNo = null;
        RefDocNo = null;

//        p_receive_code = null;
//        p_approve_code = null;

        list_payout_detail_item.setAdapter(null);

//        spn_usr_receive.setSelection(0);

        if(B_IsNonSelectDocument) {
//            label_division.setText("เลือก : NA");
            displayDocumentNA();
        }else {
//            label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
            displayPay(DepID, null,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
        }

        hideKeyboard(DispensingActivity.this);
    }

    // ------------------------------------------------------------------
    // Update Payout
    // ------------------------------------------------------------------
    public void updatePayout(final String p_docno, final String p_dept_id){

        if(!B_IsNonSelectDocument){
            if(p_dept_id == null || p_dept_id.equals("") || p_dept_id.equals("0") || p_dept_id.equals("-1")){
                return;
            }
        }

        class Update extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

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

                //System.out.println(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            DocNo = null;
                            RefDocNo = null;
                            DepIndex = -1;

                            list_payout_detail_item.setAdapter(null);
                            list_pay.setAdapter(null);
                            list_department.setAdapter(null);
                            Block_1.setVisibility( View.GONE );
                            Block_2.setVisibility( View.VISIBLE );
                            Block_3.setVisibility( View.GONE );
                            Block_4.setVisibility( View.VISIBLE );
                            switch_opt.setVisibility( View.GONE );
                            imageCreate.setVisibility( View.GONE );
                            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            linear_layout_search.setLayoutParams(params);
                            DocNo="";
                            displayPay(DepID, null,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

//                            spn_usr_receive.setSelection(0);
//
//                            if(B_IsNonSelectDocument) {
//                                label_division.setText("เลือก : NA");
//                                displayDocumentNA();
//                            }else{
//                                label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
//
//                                // Pay
//                                //displayPay(DepID, null);
//
//                                // Display Department
//                                if (!switch_department.isChecked()) {
//                                    displayDepartment(null, -1,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
//                                }
//                            }

                            hideKeyboard(DispensingActivity.this);

                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    focus();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                if( ((CssdProject) getApplication()).getCustomerId() == 201 || ((CssdProject) getApplication()).getCustomerId() == 211){
                    data.put("p_is_used_itemstock_department", "1");
                }

                if(p_dept_id != null) {
                    data.put("p_dept_id", p_dept_id);
                }

                if(PA_IsUsedRecipienter && p_approve_code != null){
                    data.put("p_receive_code", p_receive_code);
                }

                if(PA_IsUsedApprover && p_approve_code != null){
                    data.put("p_approve_code", p_approve_code);
                }

                data.put("p_userid", ((CssdProject) getApplication()).getPm().getUserid()+"");
                data.put("p_bid", ((CssdProject) getApplication()).getPm().getBdCode()+"");
                data.put("p_docno", DocNo);
                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout.php", data);

                }catch(Exception e){
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
                data.put("B_ID",((CssdProject) getApplication()).getPm().getBdCode()+"");
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

    public void displayDepartment(final String pDepName, final int depIndex, final String p_zone){

        final boolean is_borrow = false; // switch_type.isChecked();

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

                    for(int i=0;i<rs.length();i++){
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

                    list_department_adapter = new ListDepartmentAdapter(DispensingActivity.this, list, "#D6EAF8");
                    list_department.setAdapter(list_department_adapter);

//                    if(depIndex > -1) {
//                        ((ListDepartmentAdapter) adapter).setSelection(depIndex);
//                        DepName = Model_Department.get(depIndex).getDepName();
//                        DepIndex = depIndex;
//                    }else if(DepID != null){
//
//                        Iterator li = Model_Department.iterator();
//                        int i = 0;
//
//                        while (li.hasNext()) {
//
//                            ModelDepartment m = (ModelDepartment) li.next();
//
//                            if (m.getID().equals(DepID)) {
//                                ((ListDepartmentAdapter) adapter).setSelection(i);
//                                DepName = m.getDepName();
//                                break;
//                            }
//
//                            i++;
//                        }
//                    }

                    list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            ((ListDepartmentAdapter) list_department_adapter).setSelection(position);
                            select_dept(position);
                        }
                    });

                    if(DepIndex > -1){
                        displayPay(DepID, null,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    //txt_search_department.requestFocus();

                    txt_usage_code.requestFocus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_used_in_payout", "p");

                if(pDepName != null && !pDepName.trim().equals("")){
                    data.put("pDepName", pDepName);
                }

                data.put("p_is_borrow", is_borrow ? "1" : "0");

                if (Is_Zone){
                    data.put("p_zone", p_zone);
                }else {
                    data.put("p_zone", "");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_department.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }

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
        DepIndex = position;
        DepID = Model_Department.get(position).getID();
        DepName =  Model_Department.get(position).getDepName2();
        DocNo = null;
        RefDocNo = null;
        DocDateTime = "";
        switch_opt.setChecked(false);

        title_2.setText( " แผนก: " + DepName );
        try {

            if(DepID == null){
                list_payout_detail_item.setAdapter(null);
                list_pay.setAdapter(null);
            }else {

//                                    label_division.setText(" แผนก: " + DepName + " (เอกสารใหม่)");

                // Pay
                displayPay(DepID, null,ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));

                list_payout_detail_item.setAdapter(null);

                hideKeyboard(DispensingActivity.this);

            }

        }catch (Exception e){

        }
//                            title_2.setText( "  " );
        Block_1.setVisibility( View.GONE );
        Block_2.setVisibility( View.VISIBLE );
        Block_3.setVisibility( View.GONE );
        Block_4.setVisibility( View.VISIBLE );
        switch_opt.setVisibility( View.GONE );
        imageCreate.setVisibility( View.GONE );
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
    private void displayDocumentNA(){
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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {

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
                }finally {
                    focus();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                String result = null;

                data.put("p_is_non_department", "1");

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payouts.php", data);
                }catch(Exception e){
                    e.printStackTrace();
                }
                Log.d("tog_displayPay_NA","data"+data);
                Log.d("tog_displayPay_NA","result"+result);
                return result;
            }

            // -------------------------------------------------------------
        }

        DisplayPay obj = new DisplayPay();

        obj.execute();
    }

    public void focus(){
        txt_usage_code.setText("");
        txt_usage_code.requestFocus();
    }

    // ------------------------------------------------------------------
    // Display Payout
    // ------------------------------------------------------------------
    private void displayPay(final String p_department_id, final String p_docno, final String p_zone){

        if(p_department_id == null && p_docno == null) {
            return;
        }

        final boolean IsShowAll = switch_mode.isChecked();

        class DisplayPay extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

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

                List<ModelPayout> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {

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
                                            i
                                    )
                            );
                        }
                    }

                    Model_Pay = list;

                    // Set Adepter
                    final ArrayAdapter<ModelPayout> adapter = new ListPayoutDocumentAdapter(DispensingActivity.this, Model_Pay, false);
                    list_pay.setAdapter(adapter);

                    checkUsagecodeNotRetrun(DepID);

                    // OnClick
                    list_pay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Log.d("tog_listPay","setOnItemClick");
                            ((ListPayoutDocumentAdapter) adapter).setSelection(position);

                            DocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
                            RefDocNo = ((TextView)((RelativeLayout)((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();
                            switch_opt.setChecked(false);
                            title_3.setText(DocNo+" / "+DepName);
                            displayPayoutDetail(DocNo, true);
//                            Toast.makeText(DispensingActivity.this, "list_pay...!", Toast.LENGTH_SHORT).show();
                            Block_1.setVisibility( View.GONE );
                            Block_2.setVisibility( View.GONE );
                            Block_3.setVisibility( View.VISIBLE );
                            Block_4.setVisibility( View.VISIBLE );
                            switch_opt.setVisibility( View.VISIBLE );
                            imageCreate.setVisibility( Is_imageCreate?View.VISIBLE:View.GONE );
                            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params1.width = Is_imageCreate?255:505;
                            params1.setMargins(0, 0, 15, 0);
                            linear_layout_search.setLayoutParams(params1);
                            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            params2.width = 250;
                            imageCreate.setLayoutParams(params2);
                        }
                    });

                    list_pay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                            callDialogMenu(Model_Pay.get(i).getDocNo());
                            Log.d("tog_listPay","setOnItemLong");

                            return true;
                        }
                    });

                    // Set Select Index
                    if(DocNo != null) {
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
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    focus();

                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                if(IsShowAll) {
                    data.put("p_show_all_docno", "1");
                }else{
                    data.put("p_show_accrued", "1");
                }

                if(p_department_id != null) {
                    data.put("p_department_id", p_department_id);
                }else if(p_docno != null) {
                    data.put("p_docno", p_docno);
                }

                data.put("p_is_borrow", "0"); //switch_type.isChecked() ? "1" : "0");

                if (Is_Zone){
                    data.put("p_zone", p_zone);
                }else {
                    data.put("p_zone", "");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payouts.php", data);
                    Log.d("tog_displayPay","data = "+data);
                    Log.d("tog_displayPay","result = "+result);
                }catch(Exception e){
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

    public String getRefDocNo(final String RefDocNo){
        return RefDocNo == null || RefDocNo.trim().equals("") ? "-" : RefDocNo;
    }

    public void displayPayoutDetail(final String p_docno, final boolean isShowDialog){

        //System.out.println("p_docno 2 = " + p_docno);

//        title_2.setText( " แผนก: " + DepName +  " ใบจ่ายที่: " + p_docno + " อ้างอิง: " + getRefDocNo(RefDocNo) );

        DocNo = p_docno;

        class DisplayPayout extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(DispensingActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if(isShowDialog) {
                    this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                    this.dialog.show();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayoutDetails> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            String RefDocNo = null;
                            if(!c.isNull("RefDocNo")){
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
                        }else{
                            //System.out.println(c.getString("SQL"));
                        }
                    }

                } catch (JSONException e) {
                    list_payout_detail_item.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Payout_Detail_item = list;

                ArrayAdapter<ModelPayoutDetails> adapter;
                adapter = new ListPayoutDetailItemAdapter(DispensingActivity.this, Model_Payout_Detail_item,PA_IsWastingPayout);
                list_payout_detail_item.setAdapter(adapter);

                if (isShowDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }

                focus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_docno", p_docno);
                data.put("p_is_create_receive_department", PA_IsCreateReceiveDepartment ? "1" : "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl()  + "cssd_display_payout_detail_by_item.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("tog_dis_payout","data = "+data);
                Log.d("tog_dis_payout","result = "+result);

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

    public void updateQty(final String p_id, final String p_qty){

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
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_id", p_id);
                data.put("p_qty", p_qty);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_detail_qty.php", data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                return result;
            }

            // -------------------------------------------------------------
        }

        Update obj = new Update();
        obj.execute();
    }

    public void callDialogRemove(final String ID){
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

                        }else{
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

        txt_caption.setText( code + " - " + name);

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

    private void displayPayoutDetailSub(final GridView gridView, final Dialog dialog, final String id){

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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            list.add(
                                    new ModelPayoutDetailSub(
                                            c.getString("ID"),
                                            c.getString("ItemStockID"),
                                            c.getString("UsageCode"),
                                            i
                                    )
                            );
                        }else{
                            //System.out.println(c.getString("SQL"));
                        }
                    }

                } catch (JSONException e) {
                    gridView.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Payout_Detail_Sub = list;

                if(Model_Payout_Detail_Sub != null && Model_Payout_Detail_Sub.size() > 0) {

                    ArrayAdapter<ModelPayoutDetailSub> adapter;
                    adapter = new ListPayoutDetailSubAdapter(DispensingActivity.this, Model_Payout_Detail_Sub);
                    gridView.setAdapter(adapter);

                    dialog.show();

                    focus();
                }else{
                    Toast.makeText(DispensingActivity.this, Cons.WARNING_NOT_FOUND, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_id", id);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail_sub.php", data);
                }catch(Exception e){
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

        if( ( RefDocNo == null || RefDocNo.trim().equals("") || RefDocNo.trim().equals("-") ) && PA_IsEditManualPayoutQty) {

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
    public void updatePayoutDetailQty(final String id, final String Qty_Enter_New){

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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            displayPayoutDetail(DocNo, false);
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
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_id", id);
                data.put("p_qty", Qty_Enter_New);
                data.put("p_is_edit_payout_detail_qty", "1");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_detail_qty.php", data);
                }catch(Exception e){
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

        if (PA_IsShowToastDialog) {

            final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);

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
                    dialog.dismiss();
                }
            });

            dialog.show();

            txt_tmp.requestFocus();

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

        }else{
            Toast.makeText(DispensingActivity.this, Message, Toast.LENGTH_SHORT).show();
        }

    }


    private void callDialog_type1(final String Message) {

        if (PA_IsShowToastDialog) {

            final Dialog dialog = new Dialog(DispensingActivity.this, R.style.DialogCustomTheme);

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
                    dialog.dismiss();
                }
            });

            dialog.show();

            txt_tmp.requestFocus();

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

        }else{
            Toast.makeText(DispensingActivity.this, Message, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {

        if(Block_1.getVisibility()==View.VISIBLE){
            img_back_1.callOnClick();
        }else if(Block_2.getVisibility()==View.VISIBLE){
            img_back_2.callOnClick();
        }else{
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

                        if(c.getString("result").equals("A")){

                            if(B_IsNonSelectDocument){

                                DocNo = null;

                                list_payout_detail_item.setAdapter(null);

                                switch_opt.setChecked(true);
                                switch_opt.setText(switch_opt.isChecked() ? "ลบ " : "เพิ่ม ");

                                displayDocumentNA();

                            }else {
                                clearAll(true);
                            }

                        }else{
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
                data.put("p_user", ((CssdProject) getApplication()).I_CustomerId+"");
                data.put("p_docno", p_docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_remove_payout.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

                        if(c.getString("result").equals("A")){
                            AlertDialog dialog = new AlertDialog.Builder(DispensingActivity.this).setMessage("ยืนยันลบเอกสารนี้หรือไม่")
                                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }

                                    }).create();
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
}