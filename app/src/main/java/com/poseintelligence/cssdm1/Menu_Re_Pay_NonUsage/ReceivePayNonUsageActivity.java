package com.poseintelligence.cssdm1.Menu_Re_Pay_NonUsage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_Dispensing.DispensingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDepartmentAdapter;
import com.poseintelligence.cssdm1.adapter.ListPayoutDocumentAdapter;
import com.poseintelligence.cssdm1.adapter.ListReceiveDetailItemAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDepartment;
import com.poseintelligence.cssdm1.model.ModelItemstockReceiveDetail;
import com.poseintelligence.cssdm1.model.ModelPayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ReceivePayNonUsageActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    String userid;

    int block_display = 0;

    private boolean PA_IsShowToastDialog = true;

    private boolean mode;

    private EditText txt_usage_code;
    private TextView label_info;
    private TextView txt_caption;
    private TextView txt_cap_f_mode;

    private ImageView imageBack;
    private ImageView imageCreate;

    private Switch switch_mode;
    private Switch switch_opt;

    private ListView list_department;
    private ListView list_detail_item;
    private ListView list_pay;

    private LinearLayout linear_layout_department;
    private LinearLayout linear_layout_document;
    private LinearLayout linear_layout_item;
    private TextView txt_search_department;
    private Button btn_search_department;

    private List<ModelPayout> Model_Pay;
    private List<ModelItemstockReceiveDetail> Model_Receive_Detail_Item;

    private String DocNo = null;
    private String RefDocNo = null;
    private String DepID = null;
    boolean IsAlertChangeMode = true;
    private String DepName = "-";

    final String[] option = new String[]{"ลบรายการ", "ออก"};
    final String[] option_ = new String[]{"ลบเอกสาร", "ออก"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_pay_non_usage);

        byIntent();

        byWidget();

        byEvent();

        displayDepartment(null);

    }

    public void focus() {
        txt_usage_code.setText("");
        txt_usage_code.requestFocus();
    }

    private void byIntent() {
        // Argument
//        Intent intent = getIntent();
//        userid = intent.getStringExtra("userid");
//        mode = intent.getBooleanExtra("mode", true);
//        B_ID = intent.getStringExtra("B_ID");


        userid = ((CssdProject) getApplication()).getPm().getUserid()+"";
    }

    private void byWidget() {
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageCreate = (ImageView) findViewById(R.id.imageCreate);

        linear_layout_department = (LinearLayout) findViewById(R.id.linear_layout_department);
        linear_layout_document = (LinearLayout) findViewById(R.id.linear_layout_document);
        linear_layout_item = (LinearLayout) findViewById(R.id.linear_layout_item);

        txt_search_department = (EditText) findViewById(R.id.txt_search_department);
        btn_search_department = (Button) findViewById(R.id.btn_search_department);

        list_department = (ListView) findViewById(R.id.list_department);

        list_pay = (ListView) findViewById(R.id.list_pay);
        list_detail_item = (ListView) findViewById(R.id.list_detail_item);

        txt_usage_code = (EditText) findViewById(R.id.txt_usage_code);
        label_info = (TextView) findViewById(R.id.label_info);

        txt_caption = (TextView) findViewById(R.id.txt_caption);
        txt_cap_f_mode = (TextView) findViewById(R.id.txt_cap_f_mode);

        switch_mode = (Switch) findViewById(R.id.switch_mode);
        switch_opt = (Switch) findViewById(R.id.switch_opt);

//         Setting Mode
//        displayMode(mode);

    }

    private void byEvent() {

        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switch_mode.setText("ทั้งหมด ");
                } else {
                    switch_mode.setText("ค้างจ่าย ");
                }

                displayPay(DepID, null);
            }
        });

        switch_opt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (DocNo != null && IsAlertChangeMode) {
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
                    quitDialog.setTitle(Cons.TITLE);
                    quitDialog.setMessage("ยืนยันเปลี่ยนโหมดการทำงาน");

                    quitDialog.setPositiveButton(Cons.OK, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            displayMode(switch_opt.isChecked());

                            dialog.dismiss();
                        }
                    });

                    quitDialog.setNegativeButton(Cons.CANCEL, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            IsAlertChangeMode = false;
                            switch_opt.setChecked(!switch_opt.isChecked());
                            IsAlertChangeMode = true;
                        }
                    });

                    quitDialog.show();

                } else {
                    if(IsAlertChangeMode)
                        displayMode(isChecked);
                }

            }
        });

        txt_search_department.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            displayDepartment(txt_search_department.getText().toString());

                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        txt_search_department.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                displayDepartment(s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        btn_search_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDepartment(txt_search_department.getText().toString());
            }

        });


        txt_usage_code.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            checkInput();

                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }


        });

        /*
        list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DepID = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
                DepName = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                DocNo = null;
                RefDocNo = null;

                try {

                    if (DepID == null) {
                        list_detail_item.setAdapter(null);
                        list_pay.setAdapter(null);
                    } else {

                        label_info.setText(" แผนก: " + DepName + " (เอกสารใหม่)");

                        // Pay
                        displayPay(DepID, null);

                        list_detail_item.setAdapter(null);

                        hideKeyboard(CssdReceiveNonUsage.this);

                    }

                } catch (Exception e) {

                }
            }
        });
        */

        list_pay.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, int pos, long id) {

                DocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
                RefDocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();

                displayPayoutDetail(DocNo, false);

                onDeleteDocument(DocNo);

                return true;
            }
        });

        list_pay.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(2)).getText().toString();
                RefDocNo = ((TextView) ((RelativeLayout) ((LinearLayout) view).getChildAt(0)).getChildAt(4)).getText().toString();

                displayPayoutDetail(DocNo, true);
            }
        });

        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tog_back","block_display = "+block_display);
                if(block_display == 0 || block_display == 1){
                    Intent intent = new Intent(ReceivePayNonUsageActivity.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }else if(block_display == 2){
                    block_display = 1;
                    linear_layout_department.setVisibility(View.VISIBLE);
                    linear_layout_document.setVisibility(View.GONE);
                }else{
                    block_display = 2;
                    linear_layout_department.setVisibility(View.GONE);
                    linear_layout_document.setVisibility(View.VISIBLE);
                }

            }
        });

        imageCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Check DocNo
                if (DocNo == null) {
                    Toast.makeText(ReceivePayNonUsageActivity.this, Cons.WARNING_SELECT_DOC, Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
                quitDialog.setTitle(Cons.TITLE);
                quitDialog.setIcon(R.drawable.pose_favicon_2x);
                quitDialog.setMessage(Cons.CONFIRM_CLOSE);

                quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        List<ModelItemstockReceiveDetail> DATA_MODEL = Model_Receive_Detail_Item;

                        Iterator li = DATA_MODEL.iterator();

                        String data = "";

                        while (li.hasNext()) {
                            ModelItemstockReceiveDetail m = (ModelItemstockReceiveDetail) li.next();

                            if(!m.getQty().trim().equals("") && !m.getQty().equals("0"))
                                data += m.getItemcode() + "@" + m.getQty() + "@";
                        }

                        if (data.equals("")) {
                            return;
                        }

                        if (mode) {
                            updateReceive(DocNo, data);
                        } else {
                            checkBeforeUpdatePayout(DocNo, data);
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

        });

        //txt_usage_code.setShowSoftInputOnFocus(false);

    }

    private void displayMode(boolean mode) {
        linear_layout_department.setVisibility(mode ? View.GONE : View.VISIBLE);
        linear_layout_document.setVisibility(mode ? View.GONE : View.VISIBLE);
        linear_layout_item.setVisibility(!mode ? View.GONE : View.VISIBLE);
        list_detail_item.setAdapter(null);
        list_pay.setAdapter(null);
        //label_info.setVisibility(mode ? View.INVISIBLE : View.VISIBLE);
        label_info.setText("");
        txt_caption.setText(mode ? "รับเข้าเครื่องมือ (Non-Usage)" : "จ่ายเครื่องมือให้แผนก (Non-Usage)");
        txt_cap_f_mode.setText(mode ? "รับเข้า" : "ยิงจ่าย");
        switch_opt.setText(mode ? "รับเข้า" : "ยิงจ่าย");
        imageCreate.setImageResource(mode ? R.drawable.bt_save_blue : R.drawable.bt_close_payout);
        this.mode = mode;

        block_display = mode ? 0 : 1;
    }

    private void set_block_display(int mode) {
        block_display = mode;
        Log.d("tog_dis","block_display = "+block_display);
        if(mode == 1){
            linear_layout_department.setVisibility(View.VISIBLE);
            linear_layout_document.setVisibility(View.GONE);
            linear_layout_item.setVisibility(View.GONE);
        }else if(mode == 2){
            linear_layout_department.setVisibility(View.GONE);
            linear_layout_document.setVisibility(View.VISIBLE);
            linear_layout_item.setVisibility(View.GONE);
        }else{
            linear_layout_department.setVisibility(View.GONE);
            linear_layout_document.setVisibility(View.GONE);
            linear_layout_item.setVisibility(View.VISIBLE);
        }
    }

    private void displayPay(final String p_department_id, final String p_docno) {

        if (p_department_id == null && p_docno == null) {
            return;
        }

        final boolean IsShowAll = switch_mode.isChecked();

        class DisplayPay extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceivePayNonUsageActivity.this);

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
                                            c.getString("CreateDate"),
                                            c.getString("Payout_Status_ID"),
                                            false,
                                            "",
                                            i
                                    )
                            );
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Model_Pay = list;

                ArrayAdapter<ModelPayout> adapter;
                adapter = new ListPayoutDocumentAdapter(ReceivePayNonUsageActivity.this, Model_Pay, true);
                list_pay.setAdapter(adapter);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                set_block_display(2);
                focus();

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

                data.put("p_is_special", "1");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payouts.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        DisplayPay obj = new DisplayPay();
        obj.execute();
    }


    private void displayDepartment(final String pDepName) {
        class DisplayDepartment extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
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
                                getDepartment(
                                        c.getString("xID"),
                                        c.getString("xDepName"),
                                        c.getString("xDepName2")
                                )
                        );

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ArrayAdapter<ModelDepartment> adapter;
                adapter = new ListDepartmentAdapter(ReceivePayNonUsageActivity.this, list,"#FFFFFF");
                list_department.setAdapter(adapter);

                list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        ((ListDepartmentAdapter) adapter).setSelection(position);

                        DepID = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
                        DepName = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                        DocNo = null;
                        RefDocNo = null;

                        try {

                            if (DepID == null) {
                                list_detail_item.setAdapter(null);
                                list_pay.setAdapter(null);
                            } else {

                                label_info.setText(" แผนก: " + DepName + " (เอกสารใหม่)");

                                // Pay
                                displayPay(DepID, null);

                                list_detail_item.setAdapter(null);

                                hideKeyboard(ReceivePayNonUsageActivity.this);

                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                });
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if (pDepName != null && !pDepName.trim().equals("")) {
                    data.put("pDepName", pDepName);
                }

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_department.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            private ModelDepartment getDepartment(String ID, String depName, String depName2) {
                return new ModelDepartment(
                        ID,
                        depName,
                        depName2,
                        "0"
                );
            }

            // =========================================================================================
        }

        DisplayDepartment obj = new DisplayDepartment();
        obj.execute();
    }

    public void checkInput() {

        if (!mode && DepID == null) {
            Toast.makeText(ReceivePayNonUsageActivity.this, Cons.WARNING_SELECT_DEP, Toast.LENGTH_SHORT).show();
            focus();
            return;
        }

        String input = txt_usage_code.getText().toString();

        if(input.length() < 6){
            txt_usage_code.setText("");
            Toast.makeText(ReceivePayNonUsageActivity.this, "รูปแบบรหัสรายการไม่ถูกต้อง !!", Toast.LENGTH_SHORT).show();
            focus();
            return;
        }

        if (switch_opt.isChecked()) {
            addReceiveItem(input);
        } else {
            addPayItem(input);
        }
    }

    public void displayPayoutDetail(final String p_docno, final boolean isShowDialog) {

        label_info.setText(" แผนก: " + DepName + " No." + p_docno);

        DocNo = p_docno;

        class DisplayPayout extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceivePayNonUsageActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if (isShowDialog) {
                    this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                    this.dialog.show();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                //System.out.println(result);

                List<ModelItemstockReceiveDetail> list = new ArrayList<>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            list.add(
                                    new ModelItemstockReceiveDetail(
                                            c.getString("ID"),
                                            c.getString("Qty"),
                                            c.getString("Stock_Qty"),
                                            c.getString("itemname"),
                                            c.getString("itemcode"),
                                            i
                                    )
                            );
                        }
                    }

                } catch (JSONException e) {
                    list_detail_item.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Receive_Detail_Item = list;

                ArrayAdapter<ModelItemstockReceiveDetail> adapter;
                adapter = new ListReceiveDetailItemAdapter(ReceivePayNonUsageActivity.this, Model_Receive_Detail_Item, false);
                list_detail_item.setAdapter(adapter);

                if (isShowDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }

                focus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_docno", p_docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }


            // =========================================================================================
        }

        DisplayPayout obj = new DisplayPayout();
        obj.execute();
    }

    public void onDeleteDocument(final String p_docno) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReceivePayNonUsageActivity.this, android.R.layout.select_dialog_item, option_ );

        AlertDialog.Builder builder = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
        builder.setIcon(R.drawable.pose_favicon_2x);
        builder.setTitle(Cons.TITLE);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // ListView Clicked item value
                if (which == 0) {
                    // =============================================================================
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
                    quitDialog.setTitle(Cons.TITLE);
                    quitDialog.setMessage("ยืนยันลบเอกสาร ?");

                    quitDialog.setPositiveButton(Cons.OK, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDocument();
                        }
                    });

                    quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    quitDialog.show();

                    // =============================================================================
                }else {
                    // Exit
                    return;
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateReceive(final String p_docno, final String p_data) {

        class DisplayPayout extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceivePayNonUsageActivity.this);

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            DocNo = null;
                            RefDocNo = null;
                            list_detail_item.setAdapter(null);

                            label_info.setText("");

                            hideKeyboard(ReceivePayNonUsageActivity.this);

                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }


                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                focus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", p_docno);
                data.put("p_data", p_data);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_item_stock_receive.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        DisplayPayout obj = new DisplayPayout();
        obj.execute();
    }

    public void checkBeforeUpdatePayout(final String p_docno, final String p_data) {

        class Check extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceivePayNonUsageActivity.this);

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            updatePayout(p_docno, p_data);
                        }else{
                            callDialog(c.getString("Message"));
                            return;
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
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_before_update_payout.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Check obj = new Check();
        obj.execute();
    }

    public void addReceiveItem(final String p_usage_code) {

        class Add extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        String msg = c.getString("Message");

                        if (c.getString("result").equals("A")) {
                            DocNo = c.getString("DocNo");
                            label_info.setText("No." + DocNo);
                            displayStockReceiveDetail(DocNo);
                        } else {
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

                if (DocNo != null)
                    data.put("p_docno", DocNo);

                data.put("p_usage_code", p_usage_code);
                data.put("p_qty", "1");
                data.put("p_user_code", userid);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_receive_detail_code.php", data);

                return result;
            }
        }

        Add ru = new Add();

        ru.execute();
    }


    public void addPayItem(final String p_usage_code) {

        class Add extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                //System.out.println(s);

                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        String msg = c.getString("Message");

                        if (c.getString("result").equals("A")) {

                            if(DocNo == null){
                                displayPay(DepID, null);
                            }

                            DocNo = c.getString("DocNo");
                            label_info.setText("No." + DocNo);

                            displayPayoutDetail(DocNo, true);

                        } else {
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

                if (DocNo != null)
                    data.put("p_docno", DocNo);

                data.put("p_dept_id", DepID);
                data.put("p_usage_code", p_usage_code);
                data.put("p_qty", "1");
                data.put("p_user_code", userid);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_code.php", data);

                return result;
            }
        }

        Add ru = new Add();

        ru.execute();
    }

    public void onDeleteDetail(final String p_item_code) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ReceivePayNonUsageActivity.this, android.R.layout.select_dialog_item, option );

        AlertDialog.Builder builder = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
        builder.setIcon(R.drawable.pose_favicon_2x);
        builder.setTitle(Cons.TITLE);

        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                // ListView Clicked item value
                if (which == 0) {
                    // =============================================================================
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReceivePayNonUsageActivity.this);
                    quitDialog.setTitle(Cons.TITLE);
                    quitDialog.setMessage("ยืนยันลบรายการ ?");

                    quitDialog.setPositiveButton(Cons.OK, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteDetail(p_item_code);
                        }
                    });

                    quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    quitDialog.show();

                    // =============================================================================
                }else {
                    // Exit
                    return;
                }
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteDetail(final String p_item_code) {
        class Delete extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if(mode)
                    displayStockReceiveDetail(DocNo);
                else
                    displayPayoutDetail(DocNo, false);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", DocNo);
                data.put("p_item_code", p_item_code);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + ( mode ? "cssd_delete_item_stock_receive_detail.php" : "cssd_delete_payout_detail.php"), data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Delete obj = new Delete();
        obj.execute();
    }

    public void onUpdateQty(final String ID, final String Qty) {
        class Update extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                //txt_usage_code.requestFocus();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", ID);
                data.put("p_qty", Qty);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_item_stock_receive_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Update obj = new Update();
        obj.execute();
    }

    public void onUpdatePayQty(final String ID, final String Qty) {
        class Update extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                //txt_usage_code.requestFocus();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_id", ID);
                data.put("p_qty", Qty);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_detail_non_usage.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Update obj = new Update();
        obj.execute();
    }

    private void deleteDocument() {

        if(DocNo == null){
            return;
        }

        class Delete extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if(!mode)
                    displayPay(DepID, null);

                DocNo = null;
                RefDocNo = null;
                list_detail_item.setAdapter(null);

                label_info.setText(mode ? "" : (" แผนก: " + DepName + " (เอกสารใหม่)") );
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", DocNo);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + ( mode ? "cssd_delete_item_stock_receive.php" : "cssd_delete_payout.php"), data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Delete obj = new Delete();
        obj.execute();
    }

    public void updatePayout(final String p_docno, final String p_data) {

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

                            DocNo = null;
                            RefDocNo = null;
                            list_detail_item.setAdapter(null);

                            label_info.setText(" แผนก: " + DepName + " (เอกสารใหม่)");

                            // Pay
                            displayPay(DepID, null);

                            hideKeyboard(ReceivePayNonUsageActivity.this);

                        }
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }

                focus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", p_docno);
                data.put("p_data", p_data);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_payout_by_receive_non_usage.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Update obj = new Update();
        obj.execute();
    }

    private void callDialog(final String Message) {

        if (PA_IsShowToastDialog) {

            final Dialog dialog = new Dialog(ReceivePayNonUsageActivity.this, R.style.DialogCustomTheme);

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

        } else {
            Toast.makeText(ReceivePayNonUsageActivity.this, Message, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayStockReceiveDetail(final String p_docno) {

        list_detail_item.setAdapter(null);

        if (p_docno == null) {
            return;
        }

        class Display extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReceivePayNonUsageActivity.this);

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

                List<ModelItemstockReceiveDetail> list = new ArrayList<>();
                list_detail_item.setAdapter(null);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            list.add(
                                    new ModelItemstockReceiveDetail(
                                            c.getString("ID"),
                                            c.getString("Qty"),
                                            c.getString("Stock_Qty"),
                                            c.getString("itemname"),
                                            c.getString("itemcode"),
                                            i
                                    )
                            );

                        }
                    }

                    Model_Receive_Detail_Item = list;

                    ArrayAdapter<ModelItemstockReceiveDetail> adapter;
                    adapter = new ListReceiveDetailItemAdapter(ReceivePayNonUsageActivity.this, Model_Receive_Detail_Item, true);
                    list_detail_item.setAdapter(adapter);

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
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_docno", p_docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_itemstock_receive_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Display obj = new Display();
        obj.execute();
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

}