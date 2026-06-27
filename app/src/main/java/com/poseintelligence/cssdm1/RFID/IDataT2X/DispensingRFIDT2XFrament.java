package com.poseintelligence.cssdm1.RFID.IDataT2X;

import static com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X.ifASCII;
import static com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X.ifRMModule;
import static com.poseintelligence.cssdm1.data.Master.acForResultRFID;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.adapter.RFIDMapToReceive;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.BackResult;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.BaseFragment;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.GetRFIDThread;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnKeyListener;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnLowPower;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.MUtil;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.ThreadUtil;
import com.poseintelligence.cssdm1.RFID.model.Item;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.uhf.base.UHFManager;
import com.uhf.base.UHFModuleType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DispensingRFIDT2XFrament extends BaseFragment implements BackResult, OnKeyListener, OnLowPower {
    private HTTPConnect httpConnect = new HTTPConnect();
    public String getUrl;

    private Handler handler = new Handler();
    private TextView textViewRFIDMatch;

    Button btn_clear,btn_complete;

    RFIDMapToReceive item_list_adapter;
    ArrayList<Item> itemList = new ArrayList<Item>();
    HashMap<String, Item> tagList = new HashMap<String, Item>();

    String txt_btn_complete="";
    String txt_btn_clear="";

    ProgressBar progressBar_clear;
    ProgressBar progressBar_complete;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_rfid_t2x, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        byIntent();
        initView(view);
    }

    private void initView(final View v) {

        getUrl=((CssdProject) getActivity().getApplication()).getxUrl();

        ListView specific_Msg = v.findViewById(R.id.listView_item_rfid);

        TextView txtRFIDMatch = v.findViewById(R.id.txtRFIDMatch);
        if(switch_opt){
            txtRFIDMatch.setText("ลบได้ : ");
        }else{
            txtRFIDMatch.setText("จ่ายได้ : ");
        }

        textViewRFIDMatch = v.findViewById(R.id.textViewRFIDMatch);
        textViewRFIDMatch.setText("0");

        btn_complete = (Button) v.findViewById(R.id.btn_complete);
        btn_complete.setEnabled(false);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                all_complete();
            }
        });

        btn_clear = (Button) v.findViewById(R.id.btn_clear);
        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (GetRFIDThread.getInstance().isIfPostMsg()) {
                    MUtil.show("notice_clean_data");
                } else {
                    clearData();
                }
            }
        });

        progressBar_clear= (ProgressBar) v.findViewById(R.id.progressBarClear);
        progressBar_complete= (ProgressBar) v.findViewById(R.id.progressBarComplete);

        txt_btn_complete=btn_complete.getText().toString();
        txt_btn_clear=btn_clear.getText().toString();
        progressBar_clear.setVisibility(View.GONE);
        progressBar_complete.setVisibility(View.GONE);

        item_list_adapter = new RFIDMapToReceive(getActivity(), itemList);
        specific_Msg.setAdapter(item_list_adapter);
        GetRFIDThread.getInstance().setBackResult(this);

    }

    @Override
    public void refreshUI() {
        super.refreshUI();

    }

    private void clearData() {
        tagList.clear();
        itemList.clear();

        dataMap.clear();
        realDataMap.clear();
        realKeyList.clear();

        textViewRFIDMatch.setText("0");
        btn_complete.setEnabled(false);

        item_list_adapter.notifyDataSetChanged();

    }

    // Start or Stop RFID
    Runnable task;
    Runnable stop_time_task;
    boolean ifOpenFan = false;
    public void startOrStopRFID() {
        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
        if (flag && UHFT2X.isLowPower) {
            MUtil.show(getString(R.string.low_power));
            return;
        }
        if (flag) {
            if (UHFModuleType.SLR_MODULE == UHFManager.getType() && UHFT2X.if5100Module){
                UHFT2X.getMyApp().getUhfMangerImpl().slrInventoryModeSet(0);
            }
            UHFT2X.getMyApp().getUhfMangerImpl().startInventoryTag();

            ifSoundThreadAlive = true;
            if (ifRMModule && UHFModuleType.RM_MODULE == UHFManager.getType()) {
                handler.postDelayed(task = new Runnable() {
                    @Override
                    public void run() {
                        if (Temp > 55) {
                            if (!ifOpenFan) {
                                UHFT2X.getMyApp().getUhfMangerImpl().openFan();
                                ifOpenFan = true;
                            }
                        }else if (Temp < 55) {
                            if (ifOpenFan) {
                                UHFT2X.getMyApp().getUhfMangerImpl().closeFan();
                                ifOpenFan = false;
                            }
                        }
                        handler.postDelayed(this,5000);
                    }
                },5000);
            }

            reducingPowerDissipation(true);
        } else {
            ifHaveTag = false;
            ifSoundThreadAlive = false;
            Boolean i = UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();
            if (ifRMModule && UHFModuleType.RM_MODULE == UHFManager.getType()) {
                if (task !=null) {
                    if (ifOpenFan) {
                        UHFT2X.getMyApp().getUhfMangerImpl().closeFan();
                        ifOpenFan = false;
                    }
                    handler.removeCallbacks(task);
                }
            }
            if (stop_time_task != null) {
                handler.removeCallbacks(stop_time_task);
            }
            reducingPowerDissipation(false);
            //cancelVolumeTimer();
        }
        GetRFIDThread.getInstance().setIfPostMsg(flag);
        ((T2XMainActivity) getActivity()).setTextViewUHFStatus(flag);
    }

    //The label's info what has been identified
    private Map<String, Integer> dataMap = new HashMap<>();

    private long postDataTime = 0;

    int Temp = 0;
    @Override
    public void postResult(String[] tagData) {
        postDataTime = SystemClock.elapsedRealtime();
        ifHaveTag = true;
        // get TID
        String tid = tagData[0];
        String usr = tagData[0];
        String rfu = tagData[0];
        String rssi = tagData[2];
        if (ifRMModule) {
            if (Temp != Integer.parseInt(tagData[3]))
                Temp = Integer.parseInt(tagData[3]);
            Log.e("TAG", "postResult: " +Temp);
        }
        // get EPC
        if(UHFT2X.currentInvtDataType == 3){
            usr = tagData[0];
        } else if(UHFT2X.currentInvtDataType == 4) {
            tid = tagData[0].substring(0,24);
            usr = tagData[0].substring(24);
        }else if (UHFT2X.currentInvtDataType == 5) {
            tid = tagData[0].substring(0,24);
            rfu = tagData[0].substring(24,tagData[0].length()-2);
        } else if (UHFT2X.currentInvtDataType == 6){
            rfu =tagData[0];
        }
        String epc = tagData[1];
        if (ifASCII) {
            tid = convertHexToString(tid);
            epc = convertHexToString(epc);
            usr = convertHexToString(usr);
            rfu = convertHexToString(rfu);
        }

        if (UHFManager.getType() == UHFModuleType.UM_MODULE || UHFManager.getType() == UHFModuleType.RM_MODULE) {
            int Hb = Integer.parseInt(rssi.substring(0, 2), 16);
            int Lb = Integer.parseInt(rssi.substring(2, 4), 16);
            int rssi1 = ((Hb - 256 + 1) * 256 + (Lb - 256)) / 10;
            rssi = String.valueOf(rssi1);
        }
        String filterData = UHFT2X.currentInvtDataType == 1 || UHFT2X.currentInvtDataType == 2 || UHFT2X.currentInvtDataType == 4 || UHFT2X.currentInvtDataType == 5 ? tid : epc;

        Integer number = dataMap.get(/*epc*/filterData);
        if (number == null) {
            dataMap.put(/*epc*/filterData, 1);
            updateUI(epc, tid, usr, rfu, rssi,1);
        }
    }

    @Override
    public void postInventoryRate(final long rate) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // tags per second
//                readNumbers.setText(rate + readNumber);
            }
        });
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        Log.d("tog_t2x","KeyDown keyCode =" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BUTTON_3) {
            startOrStopRFID();
        }
//        if (keyCode == KeyEvent.KEYCODE_F8 || keyCode == KeyEvent.KEYCODE_F4 || keyCode == KeyEvent.KEYCODE_BUTTON_4 || keyCode == KeyEvent.KEYCODE_PROG_RED || keyCode == KeyEvent.KEYCODE_BUTTON_3/*|| keyCode == KeyEvent.KEYCODE_BUTTON_1|| keyCode ==KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10*/) {
//
//            Log.d("tog_t2x","KeyDown startOrStopRFID");
//            startOrStopRFID();
//        } else if (keyCode == KeyEvent.KEYCODE_BUTTON_2) {
//            Log.d("tog_t2x","KeyDown KEYCODE_BUTTON_2");
//            if (isDown) {
//                if (UHFT2X.getMyApp().getiScanInterface() == null)
//                    return;
//                UHFT2X.getMyApp().getiScanInterface().scan_start();
//                isDown = false;
//            }
//        }
    }
    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {
        Log.d("tog_t2x","KeyUp keyCode =" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BUTTON_3) {
            startOrStopRFID();
        }

//        if (keyCode == KeyEvent.KEYCODE_BUTTON_2) {
//            Log.d("tog_t2x","KeyUp KEYCODE_BUTTON_2");
//            if (UHFT2X.getMyApp().getiScanInterface() == null)
//                return;
//            UHFT2X.getMyApp().getiScanInterface().scan_stop();
//            isDown = true;
//        }
    }

    private Map<String, Integer> realDataMap = new HashMap<>();
    private List<String> realKeyList = new ArrayList<>();

    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

    private void updateUI(final String epc, final String tid, final String usr, final String rfu, final String rssi, final int readNumberss) {
        if (!isAdded())
            return;
        (getActivity()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String filterData = UHFT2X.currentInvtDataType == 1 || UHFT2X.currentInvtDataType == 2 || UHFT2X.currentInvtDataType == 4 || UHFT2X.currentInvtDataType == 5 ? tid : epc;

                realDataMap.put(/*epc*/filterData, 1);
                realKeyList.add(/*epc*/filterData);
                Log.d("tog_t2x","tid = " + tid + " epc = " + epc +" usr = " + usr + " rfu = " + rfu);



                playSound();
                map_usagecode(epc);
            }
        });
    }

    private boolean ifSoundThreadAlive = true;
    private boolean ifHaveTag = false;
    private long lastTime;

    private void playSound() {
        ThreadUtil.getInstance().getExService().execute(new Runnable() {
            @Override
            public void run() {
                while (UHFT2X.ifOpenSound && ifSoundThreadAlive) {
                    Log.d("tog_playSound","playSound2");
                    if (ifHaveTag) {
                        lastTime = SystemClock.elapsedRealtime();
                        //超过1s无数据暂停播放声音
                        //Pause playing sound without data for more than 1 s
                        if (lastTime != 0 && lastTime - postDataTime > 600) {
                            ifHaveTag = false;
                            continue;
                        }
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 40);
//                        UHFT2X.getMyApp().playSound();
                        SystemClock.sleep(100);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ifHaveTag = false;
        ifSoundThreadAlive = false;
    }

    public String convertHexToString(String hex){

        StringBuilder sb = new StringBuilder();
        if (hex == null) {
            return null;
        }
        //49204c6f7665204a617661 split into two characters 49, 20, 4c... //保证两位进行操作
        for( int i=0; i<hex.length()-1; i+=2 ){
            //grab the hex in pairs //将数据两位分组
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal //将十六进制转换成十进制
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character //将十进制转换成ascii字符
            sb.append((char)decimal);
        }

        return sb.toString();
    }

    private void reducingPowerDissipation(boolean ifStart) {
        Intent PowerDissipation = new Intent("android.intent.action.CONTINUCEUHF");
        PowerDissipation.putExtra("ifStart",ifStart);
        (getContext()).sendBroadcast(PowerDissipation);
    }

    @Override
    public void chargeChange(boolean isLow) {
        if (isLow) {
            if (GetRFIDThread.getInstance().isIfPostMsg()) {
                startOrStopRFID();
                (getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MUtil.show(getString(R.string.low_power));
                    }
                });
            }
        }
    }

    //custom1 don't detele func

    private void map_usagecode(String epc){
        Item ix = new Item(epc);
        tagList.put(epc, ix);

        if(switch_opt){
            map_rfid_to_remove_payout(epc);
        }else{
            map_rfid_topay(epc);
        }
    }

    private void all_complete(){

        String intent_usagecode = "";
        for(int i=0;i<itemList.size();i++){
            Log.d("tog_itemList",itemList.get(i).getUsagecode());
            intent_usagecode += ","+itemList.get(i).getUsagecode();
        }
        intent_usagecode = intent_usagecode.substring(1);

        Intent intent = new Intent();
        intent.putExtra("intent_usagecode", intent_usagecode);

        ((T2XMainActivity) getActivity()).goBack(acForResultRFID, intent);

    }

    private void on_map_rfid_runnable(){

    }

    private String p_docno = null;
    public String p_dept_id = null;
    Boolean switch_opt = false;

    public void byIntent() {
        Bundle bd = getActivity().getIntent().getExtras();

         if (bd != null){


             p_docno = bd.getString("p_docno");
             p_dept_id = bd.getString("p_dept_id");
             switch_opt = bd.getBoolean("switch_opt");

             Log.d("tog_byIntent","p_docno = "+p_docno);
             Log.d("tog_byIntent","p_dept_id = "+p_dept_id);
         }

    }

    int map_call_count = 0;
    Handler dialog_handler  = new Handler();
    Runnable dialog_map_rfid_runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("tog_runnable2","dialog_map_rfid_runnable map_call_count = "+map_call_count);
            if(map_call_count==0){
                dialog_handler.removeCallbacks(dialog_map_rfid_runnable);
                if(itemList.size()>0){
                    btn_complete.setEnabled(true);
                }else{
                    btn_complete.setEnabled(false);
                }

                btn_complete.setEnabled(true);
                btn_clear.setEnabled(true);
                btn_complete.setText(txt_btn_complete);
                btn_clear.setText(txt_btn_clear);
                progressBar_clear.setVisibility(View.GONE);
                progressBar_complete.setVisibility(View.GONE);

                on_map_rfid_runnable();

            }

        }
    };

    //custom2

    public void map_rfid_topay(String rfid){

        class map_rfid_topay extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                btn_complete.setEnabled(false);
                btn_clear.setEnabled(false);
                btn_complete.setText("");
                btn_clear.setText("");
                progressBar_clear.setVisibility(View.VISIBLE);
                progressBar_complete.setVisibility(View.VISIBLE);
                map_call_count++;
                Log.d("tog_runnable","onPreExecute map_call_count = "+map_call_count);
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
                            Log.d("tog_statusName",c.getString("usagecode") + " statusName = "+c.getString("statusName"));
                            if(c.getString("isStatus").equals("E") || c.getString("isStatus").equals("MASS")) {
                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("statusName"));
                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));

//                                if(c.getString("isStatus").equals("MASS")){
//                                    usagecodeOnStock +=  c.getString("usagecode")+"\n";
//                                    isshowDialogOnStock = true;
//                                }

                            }else{

                                tagList.get(c.getString("rfid")).setRow_id(c.getString("Row_id"));
                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("usagecode"));
                                tagList.get(c.getString("rfid")).setIsStatus(c.getString("isStatus"));
                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));

                                Log.d("tog_get_item_rfid","usagecode = "+c.getString("usagecode"));
                                itemList.add(tagList.get(c.getString("rfid")));

                                textViewRFIDMatch.setText(itemList.size()+"");
                                item_list_adapter.notifyDataSetChanged();

                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                    map_call_count--;
                    dialog_handler.postDelayed(dialog_map_rfid_runnable, 1000);
                    Log.d("tog_runnable","finally map_call_count = "+map_call_count);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                String result = null;

                data.put("rfid", rfid);

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

    public void map_rfid_to_remove_payout(String rfid){

        class map_rfid_to_remove_payout extends AsyncTask<String, Void, String> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                btn_complete.setEnabled(false);
                btn_clear.setEnabled(false);
                btn_complete.setText("");
                btn_clear.setText("");
                progressBar_clear.setVisibility(View.VISIBLE);
                progressBar_complete.setVisibility(View.VISIBLE);
                map_call_count++;
                Log.d("tog_runnable","onPreExecute map_call_count = "+map_call_count);
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
                            Log.d("tog_statusName",c.getString("usagecode") + " statusName = "+c.getString("statusName"));
                            if(c.getString("isStatus").equals("E") || c.getString("isStatus").equals("MASS")) {
                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("statusName"));
                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));

//                                if(c.getString("isStatus").equals("MASS")){
//                                    usagecodeOnStock +=  c.getString("usagecode")+"\n";
//                                    isshowDialogOnStock = true;
//                                }

                            }else{

                                tagList.get(c.getString("rfid")).setRow_id(c.getString("Row_id"));
                                tagList.get(c.getString("rfid")).setUsagecode(c.getString("usagecode"));
                                tagList.get(c.getString("rfid")).setIsStatus(c.getString("isStatus"));
                                tagList.get(c.getString("rfid")).setStatusName(c.getString("statusName"));

                                Log.d("tog_get_item_rfid","usagecode = "+c.getString("usagecode"));
                                itemList.add(tagList.get(c.getString("rfid")));

                                textViewRFIDMatch.setText(itemList.size()+"");
                                item_list_adapter.notifyDataSetChanged();

                            }

                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                    map_call_count--;
                    dialog_handler.postDelayed(dialog_map_rfid_runnable, 1000);
                    Log.d("tog_runnable","finally map_call_count = "+map_call_count);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                String result = null;

                data.put("p_docno", p_docno);
                data.put("rfid", rfid);
                data.put("p_qty", "1");
                data.put("p_DB", ((CssdProject) getActivity().getApplication()).getD_DATABASE());


                try {
                    result = httpConnect.sendPostRequest(getUrl + "rfid_scanner/map_rfid_to_remove_payout.php", data);
                } catch (Exception e) {
                    Log.d("tog_get_item_rfid","catch e = "+result);
                    e.printStackTrace();
                }

                Log.d("tog_re_item_rfid","data = "+data);
                Log.d("tog_re_item_rfid","result = "+result);

                return result;
            }


            // =========================================================================================
        }

        map_rfid_to_remove_payout obj = new map_rfid_to_remove_payout();
        obj.execute();
    }

}
