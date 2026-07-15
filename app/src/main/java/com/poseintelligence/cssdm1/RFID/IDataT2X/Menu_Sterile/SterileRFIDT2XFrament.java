package com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_Sterile;

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

public class SterileRFIDT2XFrament extends RFIDT2XFrament {
    private HTTPConnect httpConnect = new HTTPConnect();

    private String p_docno = null;
    public String basket_id = null;

    private RFIDMapToPay itemToSterileAdapter, itemCantSterileAdapter;

    @Override
    protected void byIntent() {
        Bundle bd = getActivity().getIntent().getExtras();

        if (bd != null){
            // p_docno = bd.getString("p_docno");
            basket_id = bd.getString("basket_id");

        }
    }

    @Override
    protected void initView(final View v) {
        super.initView(v);
        TextView txtRFIDMatch = v.findViewById(R.id.txtRFIDMatch);
        txtRFIDMatch.setText("RFID ในระบบ : ");

        ((TextView) v.findViewById(R.id.txtRFIDFound)).setText("RFID ที่พบ");

        TextView txtHeadRFIDCount1 = v.findViewById(R.id.txtHeadRFIDCount1);
        TextView txtRFIDCount1 = v.findViewById(R.id.txtRFIDCount1);
        TextView txtHeadRFIDCount2 = v.findViewById(R.id.txtHeadRFIDCount2);
        TextView txtRFIDCount2 = v.findViewById(R.id.txtRFIDCount2);

        txtHeadRFIDCount1.setText("เพิ่มได้ : ");
        txtHeadRFIDCount2.setText("ไม่สามารถเพิ่มได้ : ");
        txtHeadRFIDCount1.setVisibility(View.GONE);
        txtHeadRFIDCount2.setVisibility(View.GONE);

        itemToSterileAdapter = new RFIDMapToPay(getActivity(), itemList1);
        listView_item_rfid1.setAdapter(itemToSterileAdapter);

        itemCantSterileAdapter = new RFIDMapToPay(getActivity(), itemList2);
        listView_item_rfid2.setAdapter(itemCantSterileAdapter);

        itemToSterileAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                adapterCountUI(itemList1.size(),txtHeadRFIDCount1,txtRFIDCount1);
            }
        });

        itemCantSterileAdapter.registerDataSetObserver(new DataSetObserver() {
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
        itemToSterileAdapter.notifyDataSetChanged();
        itemCantSterileAdapter.notifyDataSetChanged();
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
            map_rfid_tosterile(epcToCheck);
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

    public void map_rfid_tosterile(String rfidList){

        class map_rfid_tosterile extends AsyncTask<String, Void, String> {


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
                                itemToSterileAdapter.notifyDataSetChanged();
                                itemCantSterileAdapter.notifyDataSetChanged();
                            }

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

                data.put("basket_id", basket_id);

                data.put("p_DB", ((CssdProject) getActivity().getApplication()).getD_DATABASE());

                try {
                    result = httpConnect.sendPostRequest(getUrl + "rfid_scanner/sterile/map_rfid_check_status_add_to_basket.php", data);
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

        map_rfid_tosterile obj = new map_rfid_tosterile();
        obj.execute();
    }

}
