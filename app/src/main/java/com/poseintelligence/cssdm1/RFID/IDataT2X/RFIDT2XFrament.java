package com.poseintelligence.cssdm1.RFID.IDataT2X;

import static com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X.ifASCII;
import static com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X.ifRMModule;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
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
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.BackResult;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.BaseFragment;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.GetRFIDThread;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnKeyListener;
import com.poseintelligence.cssdm1.RFID.IDataT2X.event.OnLowPower;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.MUtil;
import com.poseintelligence.cssdm1.RFID.IDataT2X.util.ThreadUtil;
import com.poseintelligence.cssdm1.RFID.model.Item;
import com.uhf.base.UHFManager;
import com.uhf.base.UHFModuleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RFIDT2XFrament extends BaseFragment implements BackResult, OnKeyListener, OnLowPower {
    public String getUrl;

    private Handler handler = new Handler();
    protected TextView textViewRFIDFound,textViewRFIDMatch;

    Button btn_clear,btn_complete;

    protected ArrayList<Item> itemList1 = new ArrayList<Item>();
    protected ArrayList<Item> itemList2 = new ArrayList<Item>();
    protected HashMap<String, Item> tagList = new HashMap<String, Item>();

    protected ListView listView_item_rfid1,listView_item_rfid2;

    String txt_btn_complete="";
    String txt_btn_clear="";

    ProgressBar progressBar_clear;
    ProgressBar progressBar_complete;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getUrl=((CssdProject) getActivity().getApplication()).getxUrl();
        return inflater.inflate(R.layout.fragment_list_rfid_t2x, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        byIntent();
        initView(view);
    }

    protected void initView(final View v) {

        listView_item_rfid1 = v.findViewById(R.id.listView_item_rfid1);
        listView_item_rfid2 = v.findViewById(R.id.listView_item_rfid2);

        textViewRFIDFound = v.findViewById(R.id.textViewRFIDFound);
        textViewRFIDFound.setText("0");
        textViewRFIDMatch = v.findViewById(R.id.textViewRFIDMatch);
        textViewRFIDMatch.setText("0");

        btn_complete = (Button) v.findViewById(R.id.btn_complete);
        btn_complete.setEnabled(false);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allComplete();
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


        GetRFIDThread.getInstance().setBackResult(this);
    }

    @Override
    public void refreshUI() {
        super.refreshUI();

    }

    protected void clearData() {
        tagList.clear();
        itemList1.clear();
        itemList2.clear();

        dataMap.clear();
        realDataMap.clear();
        realKeyList.clear();

        textViewRFIDMatch.setText("0");
        textViewRFIDFound.setText("0");

        btn_complete.setEnabled(false);
    }

    // Start or Stop RFID
//    Runnable task;
    Runnable stop_time_task;
//    boolean ifOpenFan = false;
    public void startOrStopRFID() {

        boolean flag = !GetRFIDThread.getInstance().isIfPostMsg();
        Log.d("tog_t2x_key","startOrStopRFID flag="+flag);
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
            playSound();

            reducingPowerDissipation(true);
        } else {
            ifSoundThreadAlive = false;
            Boolean i = UHFT2X.getMyApp().getUhfMangerImpl().stopInventory();

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

//    private long postDataTime = 0;

    int Temp = 0;
    @Override
    public void postResult(String[] tagData) {
//        postDataTime = SystemClock.elapsedRealtime();
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
//        if (getActivity() == null)
//            return;
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                // tags per second
////                readNumbers.setText(rate + readNumber);
//            }
//        });
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        Log.d("tog_t2x_key","KeyDown keyCode =" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BUTTON_3 && event.getRepeatCount() == 0) {
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
        Log.d("tog_t2x_key","KeyUp keyCode =" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BUTTON_3 && event.getRepeatCount() == 0) {
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
                tagCount++;
                mapRFID(epc);
            }
        });
    }

    private boolean ifSoundThreadAlive = true;
//    private boolean ifHaveTag = false;
//    private long lastTime;
    private int tagCount=0;

    private void playSound() {

        ThreadUtil.getInstance().getExService().execute(new Runnable() {
            @Override
            public void run() {
                while (UHFT2X.ifOpenSound && (ifSoundThreadAlive ||tagCount>0)) {
                    if (tagCount>0) {
                        tagCount--;
                        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 30);
                        SystemClock.sleep(40);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ifSoundThreadAlive = false;
        dialog_handler.removeCallbacks(dialog_map_rfid_runnable);
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

    protected int map_call_count = 0;
    protected Handler dialog_handler  = new Handler();
    protected Runnable dialog_map_rfid_runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("tog_runnable2","dialog_map_rfid_runnable map_call_count = "+map_call_count);
            if(map_call_count==0){
                if(itemList1.size()>0){
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
            }

            onMappingRfidRunnable();
        }
    };

    protected void preMapingRFID(){
        btn_complete.setEnabled(false);
        btn_clear.setEnabled(false);
        btn_complete.setText("");
        btn_clear.setText("");
        progressBar_clear.setVisibility(View.VISIBLE);
        progressBar_complete.setVisibility(View.VISIBLE);
        map_call_count++;
    }
    //Declare method
    protected void byIntent() {}

    protected void mapRFID(String epc){}

    protected void allComplete(){}

    protected void onMappingRfidRunnable(){
    }

}
