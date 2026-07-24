package com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_Dispensing;

import static com.poseintelligence.cssdm1.data.Master.acForResultRFID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_Dispensing.DispensingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.T2XMainActivity;
import com.poseintelligence.cssdm1.core.string.Cons;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DispensingRFIDActivity extends DispensingActivity {

    ProgressDialog RFID_dialog;
    String[] usagecode_rfid = new String[0];
//    int cnt_add_rfid = 0;

    Button bt_rfid;

    private void gotoRFIDPage(){

        if(Block_3.getVisibility() == View.VISIBLE || Block_2.getVisibility() == View.VISIBLE){
            
            Intent i = new Intent(this, T2XMainActivity.class);
            i.putExtra("fragment", 1);

            i.putExtra("p_docno", DocNo==null ? "-" : DocNo);
            Log.d("tog_docno", DocNo==null ? "-" : DocNo);

            i.putExtra("p_dept_id", DepID);
            i.putExtra("switch_opt", switch_opt.isChecked());
            startActivityForResult(i, acForResultRFID);
        }
    }

    @Override
    protected void byWidget() {
        super.byWidget();

        bt_rfid = (Button) findViewById(R.id.bt_rfid);
        bt_rfid.setVisibility(View.VISIBLE);
        bt_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!switch_opt.isChecked()) {
                    gotoRFIDPage();
                }else{
                    Toast.makeText(DispensingRFIDActivity.this, "ไม่สามารถลบผ่าน RFID ได้!!", Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        RFID_dialog = new ProgressDialog(this);
    }

//    @Override
//    public void block2_Visible()  {
//        super.block2_Visible();
//        bt_rfid.setVisibility(View.VISIBLE);
//    }
//
//    @Override
//    public void block3_Visible()  {
//        super.block3_Visible();
//        bt_rfid.setVisibility(View.VISIBLE);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == acForResultRFID){
            String intent_usagecode = data.getStringExtra("intent_usagecode");
            usagecode_rfid = new String[0];
            if (intent_usagecode != null && !intent_usagecode.isEmpty()) {
                usagecode_rfid = intent_usagecode.split(",");
            }

            Log.d("tog_itemList",usagecode_rfid.length+"");
            if(usagecode_rfid.length>0){
                if(!switch_opt.isChecked()) {
//                    String list_usagecode_rfid = "'"+intent_usagecode.replace(",","','")+"'";
                    Log.d("tog_usagecode_rfid",intent_usagecode);
                    addItemRFID(intent_usagecode);
                }else{
                    for(int i=0;i<usagecode_rfid.length;i++){
                        Log.d("tog_usagecode_rfid",usagecode_rfid[i]);
//                        removeItemRFID(usagecode_rfid[i].toUpperCase());
                    }
                }

            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    int rfid_usage_code_count = 0;
    int rfid_usage_code_size = 0;
    public void addItemRFID(final String rfid_usage_code) {

        String[] rfid_usage_code_list = rfid_usage_code.split(",");
        rfid_usage_code_count=0;
        rfid_usage_code_size = rfid_usage_code_list.length;

        final boolean B_Is_Borrow = false;
        String p_is_borrow = B_Is_Borrow ? "1" : "0";

        if(!RFID_dialog.isShowing()){
            RFID_dialog.setTitle("กำลังเพิ่มรายการ");
            RFID_dialog.setMessage("เพิ่มสำเร็จ "+rfid_usage_code_count+"/"+rfid_usage_code_size);
            RFID_dialog.setCancelable(false);
            RFID_dialog.show();
        }

        class Add extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")){
//                            DocNo = c.getString("p_docno");
                            rfid_usage_code_count++;

                            if (DocNo == null) {

                                DocNo = c.getString("DocNo");

                                if (B_IsNonSelectDocument) {
                                    list_department.setAdapter(null);
                                    displayDocumentNA();

                                } else {
                                    displayPay(DepID, DocNo, ar_list_zone_id.get(spn_zone.getSelectedItemPosition()));
                                }

                                //handler_dept.removeCallbacks(runnable_dept);
                                if (Model_Pay.size() > 0) {
                                    title_3.setText(DocNo + " / " + Model_Pay.get(0).getDepName() + " (M)");
                                } else {
                                    title_3.setText(DocNo + " / " + DepName + " (M)");
                                }

                                block3_Visible();

                                // ได้ DocNo แล้ว → ยิงตัวที่เหลือแบบ parallel
                                for (int j = 1; j < rfid_usage_code_list.length; j++) {
                                    Add ru = new Add();
                                    ru.execute(rfid_usage_code_list[j]);
                                }

                            }
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    RFID_dialog.setMessage("เพิ่มสำเร็จ "+rfid_usage_code_count+"/"+rfid_usage_code_size);

                    if(rfid_usage_code_count == rfid_usage_code_size){
                        block3_Visible();

                        displayPayoutDetail(DocNo, false);

                        RFID_dialog.getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(RFID_dialog.isShowing()){
                                    RFID_dialog.dismiss();
                                }}
                        }, 1000);
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
                data.put("p_usage_code",params[0].toUpperCase());
                data.put("p_qty", "1");
                data.put("p_user_code", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("p_device", "M1");

                Log.d("OOOO", ((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php?" + data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_add_payout_detail_usage.php", data);
                Log.d("tog_add_pay_detail", "data : " + data);
                Log.d("tog_add_pay_detail", "result : " + result);
                return result;

            }
        }

        if (DocNo == null) {
            Add AddItem = new Add();
            AddItem.execute(rfid_usage_code_list[0]);
        } else {
            for (int i = 0; i < rfid_usage_code_list.length; i++) {
                Add ru = new Add();
                ru.execute(rfid_usage_code_list[i]);
            }
        }
    }

}