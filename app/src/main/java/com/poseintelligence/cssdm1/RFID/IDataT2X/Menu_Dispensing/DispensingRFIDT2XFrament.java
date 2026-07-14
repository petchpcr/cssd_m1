package com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_Dispensing;

import static com.poseintelligence.cssdm1.data.Master.acForResultRFID;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.RFIDT2XFrament;
import com.poseintelligence.cssdm1.RFID.IDataT2X.T2XMainActivity;
import com.poseintelligence.cssdm1.RFID.adapter.RFIDMapToPay;
import com.poseintelligence.cssdm1.RFID.model.Item;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DispensingRFIDT2XFrament extends RFIDT2XFrament {
    private HTTPConnect httpConnect = new HTTPConnect();

    private String p_docno = null;
    public String p_dept_id = null;

    Boolean switch_opt = false;

    private RFIDMapToPay itemToPayAdapter,itemCantPayAdapter;

    @Override
    protected void byIntent() {
        Bundle bd = getActivity().getIntent().getExtras();

        if (bd != null){
            p_docno = bd.getString("p_docno");
            p_dept_id = bd.getString("p_dept_id");
            switch_opt = bd.getBoolean("switch_opt");

            Log.d("tog_byIntent","p_docno = "+p_docno);
            Log.d("tog_byIntent","p_dept_id = "+p_dept_id);
        }
    }

    @Override
    protected void initView(final View v) {
        super.initView(v);
        TextView txtRFIDMatch = v.findViewById(R.id.txtRFIDMatch);
        txtRFIDMatch.setText("RFID ในระบบ : ");
//        if(switch_opt){
//            txtRFIDMatch.setText("ลบได้ : ");
//        }else{
//            txtRFIDMatch.setText("จ่ายได้ : ");
//        }

        ((TextView) v.findViewById(R.id.txtRFIDFound)).setText("RFID ที่พบ");

        TextView txtHeadRFIDCount1 = v.findViewById(R.id.txtHeadRFIDCount1);
        TextView txtRFIDCount1 = v.findViewById(R.id.txtRFIDCount1);
        TextView txtHeadRFIDCount2 = v.findViewById(R.id.txtHeadRFIDCount2);
        TextView txtRFIDCount2 = v.findViewById(R.id.txtRFIDCount2);

        txtHeadRFIDCount1.setText("จ่ายได้ : ");
        txtHeadRFIDCount2.setText("ไม่สามารถจ่ายได้ : ");
        txtHeadRFIDCount1.setVisibility(View.GONE);
        txtHeadRFIDCount2.setVisibility(View.GONE);

        itemToPayAdapter = new RFIDMapToPay(getActivity(), itemList1);
        listView_item_rfid1.setAdapter(itemToPayAdapter);

        itemCantPayAdapter = new RFIDMapToPay(getActivity(), itemList2);
        listView_item_rfid2.setAdapter(itemCantPayAdapter);

        itemToPayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                adapterCountUI(itemList1.size(),txtHeadRFIDCount1,txtRFIDCount1);
            }
        });

        itemCantPayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                adapterCountUI(itemList2.size(),txtHeadRFIDCount2,txtRFIDCount2);
            }
        });

        onMappingRfidRunnable();
    }

    public void adapterCountUI(int size,TextView head,TextView tail){

        tail.setText(String.valueOf(size));
        textViewRFIDMatch.setText(String.valueOf(itemList1.size()+itemList2.size()));

        head.setVisibility(View.VISIBLE);
        tail.setVisibility(View.VISIBLE);
        if(size<=0){
            head.setVisibility(View.GONE);
            tail.setVisibility(View.GONE);
        }
    }

    @Override
    protected void clearData(){
        super.clearData();
        itemToPayAdapter.notifyDataSetChanged();
        itemCantPayAdapter.notifyDataSetChanged();
    }

    String tempEpc= "";
    @Override
    protected void mapRFID(String epc){
        Item ix = new Item(epc);
        tagList.put(epc, ix);
        textViewRFIDFound.setText(tagList.size()+"");
        Log.d("tog_t2xmapRFID"," epc = " + epc);
        tempEpc = tempEpc+",'"+epc+"'";
    }

    @Override
    protected void onMappingRfidRunnable(){
        super.onMappingRfidRunnable();
        dialog_handler.postDelayed(dialog_map_rfid_runnable, 300);
        if(tempEpc.length() > 0){
            String epcToCheck = tempEpc.substring(1);
            tempEpc= "";
            if(switch_opt){
    //            map_rfid_to_remove_payout(epc);
            }else{
                map_rfid_topay(epcToCheck);
            }
        }
    }

    @Override
    protected void allComplete(){
        String intent_usagecode = "";
        for(int i = 0; i< itemList1.size(); i++){
            Log.d("tog_itemList", itemList1.get(i).getUsagecode());
            intent_usagecode += ","+ itemList1.get(i).getUsagecode();
        }
        if(intent_usagecode.length()>0){
            intent_usagecode = intent_usagecode.substring(1);
        }

        Intent intent = new Intent();
        intent.putExtra("intent_usagecode", intent_usagecode);

        ((T2XMainActivity) getActivity()).goBack(acForResultRFID, intent);
    }

    public void map_rfid_topay(String rfidList){

        class map_rfid_topay extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                preMapingRFID();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                Log.d("tog_runnable","onPostExecute map_call_count = "+map_call_count);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    JSONArray rs = jsonObj.getJSONArray("result");

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if(tagList.get(c.getString("rfid"))!=null){
                            
                            Log.d("tog_statusName",c.getString("rfid") + " statusName = "+c.getString("statusName"));
                            
                            tagList.get(c.getString("rfid")).setRow_id(c.getString("Row_id"));
                            tagList.get(c.getString("rfid")).setRfidStatus(c.getString("result"));
                            tagList.get(c.getString("rfid")).setUsagecode(c.getString("usagecode"));
                            tagList.get(c.getString("rfid")).setItemCode(c.getString("ItemCode"));
                            tagList.get(c.getString("rfid")).setIsStatus(c.getString("isStatus"));
                            tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));
                            tagList.get(c.getString("rfid")).setItemName(c.getString("ItemName"));

                            if(!c.getString("result").equals("N")) {
                                Log.d("tog_get_item_rfid","usagecode = "+c.getString("usagecode"));
//                                itemList.add(0,tagList.get(c.getString("rfid")));
                                if(c.getString("result").equals("A")) {
                                    int add_index = 0;
                                    for(int j = 0; j< itemList1.size(); j++){
                                        if(itemList1.get(j).getItemCode().equals(c.getString("ItemCode"))){
                                            add_index = j;
                                            break;
                                        }

                                        if(itemList1.get(j).getRfidStatus().equals("E")){break;}
                                    }
                                    itemList1.add(add_index,tagList.get(c.getString("rfid")));
                                }else{
                                    itemList2.add(tagList.get(c.getString("rfid")));
                                }
                                itemToPayAdapter.notifyDataSetChanged();
                                itemCantPayAdapter.notifyDataSetChanged();
                            }
//                            else{
//
//                                itemList.add(tagList.get(c.getString("rfid")));
//                            }
//                            item_list_adapter.notifyDataSetChanged();

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                    map_call_count--;
                    Log.d("tog_runnable","finally map_call_count = "+map_call_count);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                String result = null;

                data.put("rfidList", rfidList);

                data.put("p_docno", p_docno == null ? "" : p_docno);
                data.put("p_qty", "1");
                data.put("p_DB", ((CssdProject) getActivity().getApplication()).getD_DATABASE());
                data.put("p_DeptID", p_dept_id);


                try {
                    result = httpConnect.sendPostRequest(getUrl + "rfid_scanner/map_rfid_check_status_item_topay.php", data);
                } catch (Exception e) {
                    Log.d("tog_get_item_rfid","catch e = "+result);
                    e.printStackTrace();
                }

                Log.d("tog_get_item_rfid","data = "+data);
                Log.d("tog_get_item_rfid","result = "+result);

                return result;
            }


            // =========================================================================================
        }

        map_rfid_topay obj = new map_rfid_topay();
        obj.execute();
    }

//    public void map_rfid_to_remove_payout(String rfid){
//
//        class map_rfid_to_remove_payout extends AsyncTask<String, Void, String> {
//
//
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                preMapingRFID();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                Log.d("tog_runnable","onPostExecute map_call_count = "+map_call_count);
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    JSONArray rs = jsonObj.getJSONArray("result");
//
//                    for(int i=0;i<rs.length();i++){
//                        JSONObject c = rs.getJSONObject(i);
//                        if(tagList.get(c.getString("rfid"))!=null){
//                            Log.d("tog_statusName",c.getString("usagecode") + " statusName = "+c.getString("statusName"));
//                            if(c.getString("isStatus").equals("E") || c.getString("isStatus").equals("MASS")) {
//                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("statusName"));
//                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));
//
////                                if(c.getString("isStatus").equals("MASS")){
////                                    usagecodeOnStock +=  c.getString("usagecode")+"\n";
////                                    isshowDialogOnStock = true;
////                                }
//
//                            }else{
//
//                                tagList.get(c.getString("rfid")).setRow_id(c.getString("Row_id"));
//                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("usagecode"));
//                                tagList.get(c.getString("rfid")).setIsStatus(c.getString("isStatus"));
//                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));
//
//                                Log.d("tog_get_item_rfid","usagecode = "+c.getString("usagecode"));
//                                itemList1.add(tagList.get(c.getString("rfid")));
//
//                                textViewRFIDMatch.setText(itemList1.size()+"");
//                                itemToPayAdapter.notifyDataSetChanged();
//                                itemCantPayAdapter.notifyDataSetChanged();
//
//                            }
//
//                        }
//
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }finally {
//
//                    map_call_count--;
//                    dialog_handler.postDelayed(dialog_map_rfid_runnable, 1000);
//                    Log.d("tog_runnable","finally map_call_count = "+map_call_count);
//                }
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String,String>();
//
//                String result = null;
//
//                data.put("p_docno", p_docno);
//                data.put("rfid", rfid);
//                data.put("p_qty", "1");
//                data.put("p_DB", ((CssdProject) getActivity().getApplication()).getD_DATABASE());
//
//
//                try {
//                    result = httpConnect.sendPostRequest(getUrl + "rfid_scanner/map_rfid_to_remove_payout.php", data);
//                } catch (Exception e) {
//                    Log.d("tog_get_item_rfid","catch e = "+result);
//                    e.printStackTrace();
//                }
//
//                Log.d("tog_re_item_rfid","data = "+data);
//                Log.d("tog_re_item_rfid","result = "+result);
//
//                return result;
//            }
//
//
//            // =========================================================================================
//        }
//
//        map_rfid_to_remove_payout obj = new map_rfid_to_remove_payout();
//        obj.execute();
//    }

}
