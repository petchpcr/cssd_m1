package com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_Sterile;

import static com.poseintelligence.cssdm1.data.Master.acForResultRFID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.T2XMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SterileRFIDActivity extends SterileActivity {

    ProgressDialog RFID_dialog;
    int rfid_usage_code_count = 0;
    int rfid_usage_code_complete = 0;
    int rfid_usage_code_size = 0;

    ImageView bt_rfid;

    private void gotoRFIDPage(){
        Intent i = new Intent(this, T2XMainActivity.class);
        i.putExtra("fragment", 2);
        i.putExtra("p_docno", get_mac_select_doc());
        i.putExtra("basket_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getID());
        startActivityForResult(i, acForResultRFID);
    }

    @Override
    public void byWidget() {
        super.byWidget();

        bt_rfid = (ImageView) findViewById(R.id.bt_rfid);
        bt_rfid.setVisibility(View.GONE);
        bt_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoRFIDPage();
            }
        });

        RFID_dialog = new ProgressDialog(this);
    }

    @Override
    public void show_basket(String name, int v) {
        super.show_basket(name, v);
        Log.d("tog_show_basket", "list.size() = " + list.size() + " emtpyPos = " + emtpyPos);

        Log.d("tog_show_basket", "list_mac_adapter.select_mac_pos = " + list_mac_adapter.select_mac_pos);
        if (bt_rfid != null) {
            if(list_mac_adapter.select_mac_pos < 0 || list_mac_adapter.select_mac_pos == list.size()-emtpyPos){
                bt_rfid.setVisibility(v == View.VISIBLE ? View.VISIBLE : View.GONE);
            }else{
                bt_rfid.setVisibility(View.GONE);
            }
            
        }
    }

    @Override
    public void show_mac(String name,int v){
        super.show_mac(name, v);
        if(!name.equals("Empty")){
            bt_rfid.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == acForResultRFID && data != null){
            String intent_usagecode = data.getStringExtra("intent_usagecode");
            if (intent_usagecode != null && !intent_usagecode.isEmpty()) {
                String[] usagecode_rfid = intent_usagecode.split(",");
                Log.d("tog_itemList", usagecode_rfid.length + "");

                String basket_id = "-";
                if (list_basket_adapter != null && list_basket_adapter.select_basket_pos >= 0) {
                    basket_id = xlist_basket.get(list_basket_adapter.select_basket_pos).getID();
                }

                add_rfid_to_basket(basket_id, intent_usagecode);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void add_rfid_to_basket(String basket_id,String rfid_usage_code){
        String[] rfid_usage_code_list = rfid_usage_code.split(",");
        rfid_usage_code_count = 0;
        rfid_usage_code_complete = 0;
        rfid_usage_code_size = rfid_usage_code_list.length;

        if(!RFID_dialog.isShowing()){
            RFID_dialog.setTitle("กำลังเพิ่มรายการ");
            RFID_dialog.setMessage("เพิ่มสำเร็จ "+rfid_usage_code_count+"/"+rfid_usage_code_size);
            RFID_dialog.setCancelable(false);
            RFID_dialog.show();
        }

        class add_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                devLog("(add_item 1) == basket_id = "+basket_id+" /// usage_code = "+rfid_usage_code);
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
                            rfid_usage_code_count++;
                        }

                    }

                } catch (JSONException e) {
                    show_log_error("add_item_to_basket.php Error = "+e);
                    e.printStackTrace();
                }finally {
                    rfid_usage_code_complete++;
                    RFID_dialog.setMessage("เพิ่มสำเร็จ "+rfid_usage_code_count+"/"+rfid_usage_code_size);

                    if(rfid_usage_code_complete == rfid_usage_code_size){
                        reload_basket();

                        RFID_dialog.getWindow().getDecorView().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(RFID_dialog.isShowing()){
                                    RFID_dialog.dismiss();
                                }
                            }
                        }, 1000);
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", basket_id);
                data.put("usage_code", params[0].toUpperCase());
                data.put("p_DB", p_DB);

                Log.d("tog_add_item","getDocNo = " + list.get(list_mac_adapter.select_mac_pos).getDocNo());

                if(!list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                    data.put("program_id", list.get(list_mac_adapter.select_mac_pos).getTypeID());
                    data.put("mac_id", list.get(list_mac_adapter.select_mac_pos).getMachineID());
                }else{
                    data.put("program_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getTypeProcessID());
                }

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "add_item_to_basket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_add_item","data = " + data);
                Log.d("tog_add_item","result = " + result);


                return result;
            }

            // =========================================================================================
        }

        for(int i=0;i<rfid_usage_code_list.length;i++){
            Log.d("tog_usagecode_rfid", rfid_usage_code_list[i]);
            add_item obj = new add_item();
            obj.execute(rfid_usage_code_list[i]);
        }
    }
}
