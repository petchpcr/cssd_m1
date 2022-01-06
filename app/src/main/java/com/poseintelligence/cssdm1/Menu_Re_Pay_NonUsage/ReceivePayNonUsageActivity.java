package com.poseintelligence.cssdm1.Menu_Re_Pay_NonUsage;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
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
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
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
    private TextView txt_search_department;
    private Button btn_search_department;

    private List<ModelPayout> Model_Pay;
    private List<ModelItemstockReceiveDetail> Model_Receive_Detail_Item;

    private String DocNo = null;
    private String RefDocNo = null;
    private String DepID = null;
    boolean IsAlertChangeMode = true;
    private String DepName = "-";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_pay_non_usage);

        byIntent();

        byWidget();

        byEvent();

//        displayDepartment(null);

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
    }

    private void byWidget() {
        imageBack = (ImageView) findViewById(R.id.imageBack);
        imageCreate = (ImageView) findViewById(R.id.imageCreate);

        linear_layout_department = (LinearLayout) findViewById(R.id.Block1);
        linear_layout_document = (LinearLayout) findViewById(R.id.Block2);

        txt_search_department = (EditText) findViewById(R.id.txt_search_department);
        btn_search_department = (Button) findViewById(R.id.btn_search_department);

        list_department = (ListView) findViewById(R.id.list_department);

        list_pay = (ListView) findViewById(R.id.list_pay);
        list_detail_item = (ListView) findViewById(R.id.list_payout_detail_item);

        txt_usage_code = (EditText) findViewById(R.id.txt_usage_code);
        label_info = (TextView) findViewById(R.id.label_info);

        txt_caption = (TextView) findViewById(R.id.txt_caption);
        txt_cap_f_mode = (TextView) findViewById(R.id.txt_cap_f_mode);

        switch_mode = (Switch) findViewById(R.id.switch_mode);
        switch_opt = (Switch) findViewById(R.id.switch_opt);

//         Setting Mode
        displayMode(mode);

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
                    AlertDialog.Builder quitDialog = new AlertDialog.Builder(CssdReceiveNonUsage.this);
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
                finish();
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

//    private void displayDepartment(final String pDepName) {
//        class DisplayDepartment extends AsyncTask<String, Void, String> {
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
//                List<ModelDepartment> list = new ArrayList<>();
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for (int i = 0; i < rs.length(); i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        list.add(
//                                getDepartment(
//                                        c.getString("xID"),
//                                        c.getString("xDepName"),
//                                        c.getString("xDepName2")
//                                )
//                        );
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                Model_Department = list;
//
//                final ArrayAdapter<ModelDepartment> adapter;
//                adapter = new ListDepartmentAdapter(CssdReceiveNonUsage.this, list,"#FFFFFF");
//                list_department.setAdapter(adapter);
//
//                list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                        ((ListDepartmentAdapter) adapter).setSelection(position);
//
//                        DepID = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
//                        DepName = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
//                        DocNo = null;
//                        RefDocNo = null;
//
//                        try {
//
//                            if (DepID == null) {
//                                list_detail_item.setAdapter(null);
//                                list_pay.setAdapter(null);
//                            } else {
//
//                                label_info.setText(" แผนก: " + DepName + " (เอกสารใหม่)");
//
//                                // Pay
//                                displayPay(DepID, null);
//
//                                list_detail_item.setAdapter(null);
//
//                                hideKeyboard(CssdReceiveNonUsage.this);
//
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                });
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String, String>();
//
//                if (pDepName != null && !pDepName.trim().equals("")) {
//                    data.put("pDepName", pDepName);
//                }
//
//                String result = null;
//
//                try {
//                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getUrl() + "cssd_select_department.php", data);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                return result;
//            }
//
//            private ModelDepartment getDepartment(String ID, String depName, String depName2) {
//                return new ModelDepartment(
//                        ID,
//                        depName,
//                        depName2,
//                        "0"
//                );
//            }
//
//            // =========================================================================================
//        }
//
//        DisplayDepartment obj = new DisplayDepartment();
//        obj.execute();
//    }

    private void displayMode(boolean mode) {
        linear_layout_department.setVisibility(mode ? View.GONE : View.VISIBLE);
        linear_layout_document.setVisibility(mode ? View.GONE : View.VISIBLE);
        list_detail_item.setAdapter(null);
        list_pay.setAdapter(null);
        //label_info.setVisibility(mode ? View.INVISIBLE : View.VISIBLE);
        label_info.setText("");
        txt_caption.setText(mode ? "รับเข้าเครื่องมือ (Non-Usage)" : "จ่ายเครื่องมือให้แผนก (Non-Usage)");
        txt_cap_f_mode.setText(mode ? "รับเข้า" : "ยิงจ่าย");
        switch_opt.setText(mode ? "รับเข้า" : "ยิงจ่าย");
        imageCreate.setImageResource(mode ? R.drawable.bt_save_blue : R.drawable.bt_close_payout);
        this.mode = mode;

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
}