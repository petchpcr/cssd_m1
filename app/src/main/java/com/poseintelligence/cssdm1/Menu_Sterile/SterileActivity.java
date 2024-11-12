package com.poseintelligence.cssdm1.Menu_Sterile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_Sterile.adapter.itemAdapter;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListBoxBasketAdapter;
import com.poseintelligence.cssdm1.adapter.ListBoxMachineAdapter;
import com.poseintelligence.cssdm1.adapter.ListItemBasketAdapter;
import com.poseintelligence.cssdm1.core.audio.iAudio;
import com.poseintelligence.cssdm1.core.connect.DBConnect;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.BasketTag;
import com.poseintelligence.cssdm1.model.ItemInBasket;
import com.poseintelligence.cssdm1.model.ModelMachine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
public class SterileActivity extends AppCompatActivity{

    //DEV
    TextView timerS;
    TextView timerE;
    TextView timerT;

    private final String TAG_RESULTS = "result";
    private JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    public static String folder_php = "sterile_basket/";
    public String getUrl;
    public String p_DB;

    boolean stack_scan = false;
    boolean sp_connect = false;

    boolean test_machine = false;

    boolean get_data = false;

    String mass_onkey="";

    RecyclerView list_mac;
    RecyclerView list_basket;
    ListView list_item_basket;

    ImageView mac_image;
    TextView mac_text;
    TextView bt_delete;
    TextView bt_select_all;
    LinearLayoutManager lm;
    public TextView title_2;

    public int mac_round_on_change = 0;
    ArrayAdapter<String> adp_mac_round;
    public Spinner mac_round;
    ArrayAdapter<String> adp_mac_pro_test;
    public Spinner mac_pro_test;
    ArrayAdapter<String> adp_mac_test_point;
    ArrayAdapter<String> adp_mac_test_point_default;
    public Spinner mac_test_point;

    boolean isShow_editDocTab_1 = false;
    public boolean SR_IsEditRound = false;
    public boolean SR_M1IsSetTestProg = false;
    public boolean SR_IsUsedLot = false;

    public boolean ST_SoundAndroidVersion9 = false;
    LinearLayout ll_mac;
    LinearLayout rl_basket;
    TextView macname;
    TextView basketname;
    TextView text_qty;

    public int mac_id_non_approve = -1;
    public int basket_pos_non_approve = -1;

    boolean is_select_all = false;
    boolean is_add_item = false;

    String loader = "";
    public boolean is_have_loader=false;

    String typeID="";

    private iAudio nMidia;
    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_RING, 100);

    ArrayList<ModelMachine> list = new ArrayList<>();
    ListBoxMachineAdapter list_mac_adapter;
    public String mac_empty_id = "Empty";
    public String basket_empty_id = "Empty";

    ArrayList<BasketTag> xlist_basket = new ArrayList<>();
    HashMap<String,Integer> map_index_xlist_basket = new HashMap<String, Integer>();
    ListBoxBasketAdapter list_basket_adapter;

    ArrayList<ItemInBasket> xlist_item_basket = new ArrayList<>();
    ListItemBasketAdapter list_item_basket_adapter;

    public AlertDialog.Builder alert_builder;
    public AlertDialog alert;

    ProgressDialog wait_dialog;
    ProgressDialog loadind_dialog;

    private Vibrator x_vibrator;

    final int wait_scan_program = 1;
    final int wait_scan_employee = 2;
    final int wait_scan_type = 3;

    public boolean mac_is_working = false;

    Boolean SR_IsUsedDBUserOperation;
    Boolean SR_IsRememberUserOperation;

    public Boolean  SR_IsUsedBasket_M1 = false;

    Boolean SR_IsTestProgramRunRound_M1 = false;
    boolean SR_IsProgramTestSplitRound = false;

    Handler handler  = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (alert.isShowing()) {
                alert.dismiss();
            }

            handler.removeCallbacks(runnable);
        }
    };

    HashMap<String,Integer> map_machine = new HashMap<String, Integer>();

    int time_cnt = -1;
    int time_cnt_back = 0;
    Handler check_active_machine_handler  = new Handler();
    Runnable check_active_machine_runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("tog_time_cnt_back","time_cnt_back = "+time_cnt_back);
            if(time_cnt_back<100){

                if(!loadind_dialog.isShowing()){
                    if(time_cnt>5){
                        check_active_machine();
                        time_cnt = 0;
                    }

                    check_basket();

                    check_item_in_basket();

                    time_cnt++;
                    time_cnt_back++;
                }

                check_active_machine_handler.postDelayed(check_active_machine_runnable, 3000);
            }else{
                Intent intent = new Intent(SterileActivity.this, MainMenu.class);
                startActivity(intent);
                check_active_machine_handler.removeCallbacks(check_active_machine_runnable);
                finish();
            }
        }
    };

    @Override
    protected void onDestroy() {
        Log.d("tog_lcycle","onDestroy");
        super.onDestroy();
//        check_active_machine_handler.removeCallbacks(check_active_machine_runnable);
    }

    int emtpyPos = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sterile);

        Log.d("tog_lcycle","onCreate");


//        demo_usage.add("I00867-232-00001");
//        demo_usage.add("I06375-232-00001");
//        demo_usage.add("I07094-232-00001");
//        demo_usage.add("I07808-242-00935");
//        demo_usage.add("I06886-232-00001");
//        demo_usage.add("I00021-232-00001");
//        demo_usage.add("I07629-236-00001");
//        demo_usage.add("I06370-232-00001");
//        demo_usage.add("I00503-237-00001");
//        demo_usage.add("I00870-232-00001");
//        demo_usage.add("I00099-232-00001");
//        demo_usage.add("I00867-232-00001");
//        demo_usage.add("I07325-242-47489");

        byIntent();

        byWidget();

        // -----------------------------------------------------------------------
        // vibrator
        x_vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // -----------------------------------------------------------------------
        // Sound
        nMidia = new iAudio(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("tog_lcycle","onPause");

        check_active_machine_handler.removeCallbacks(check_active_machine_runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("tog_lcycle","onResume");
        time_cnt_back = 0;
        if(time_cnt>=0){
            check_active_machine();
            check_basket();

            loadind_dialog_show();
            check_item_in_basket();
            check_active_machine_handler.postDelayed(check_active_machine_runnable, 5000);
        }
    }

    public void byIntent(){
        Bundle bd = getIntent().getExtras();
        String MenuName = bd.getString("MenuName");
        if(MenuName.equals("bt_sterile_mac_test")){
            test_machine = true;
        }

        getUrl=((CssdProject) getApplication()).getxUrl()+folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();

        SR_IsUsedDBUserOperation = ((CssdProject) getApplication()).isSR_IsUsedDBUserOperation() ;
        SR_IsRememberUserOperation = ((CssdProject) getApplication()).isSR_IsRememberUserOperation() ;

        SR_IsUsedBasket_M1 = ((CssdProject) getApplication()).isSR_IsUsedBasket_M1();
        SR_IsTestProgramRunRound_M1 = ((CssdProject) getApplication()).isSR_IsTestProgramRunRound_M1();
        SR_IsProgramTestSplitRound = ((CssdProject) getApplication()).isSR_IsProgramTestSplitRound();
        Log.d("tog_SR_RunRound_M1","SR_IsTestProgramRunRound_M1 = "+SR_IsTestProgramRunRound_M1);

        SR_IsEditRound = ((CssdProject) getApplication()).isSR_IsEditRound();
        SR_IsUsedLot = ((CssdProject) getApplication()).isSR_IsUsedLot();

        LinearLayout linear_mac_test_point = (LinearLayout) findViewById(R.id.linear_mac_test_point);
        if(SR_IsUsedLot){
            linear_mac_test_point.setVisibility(View.VISIBLE);
        }else{
            linear_mac_test_point.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if(box_item_detail.getVisibility()==View.VISIBLE){
            box_item_detail.setVisibility(View.GONE);
        }else{
            Intent intent = new Intent(SterileActivity.this, MainMenu.class);
            startActivity(intent);
            check_active_machine_handler.removeCallbacks(check_active_machine_runnable);
            finish();
        }
    }

    LinearLayout box_item_detail;
    LinearLayout box_item_detail2;
    ListView lv_itemdetail;
    Button bt_add_colse;
    TextView scan_macname;
    TextView scan_basketname;
    TextView scan_qty;
    ArrayList<String> list_usagecode_all = new ArrayList<String>();
    ArrayList<String> list_usagecode_to_add = new ArrayList<String>();
    ArrayList<String> list_usagecode_to_false = new ArrayList<String>();
    HashMap<String, String> usagecode_to_add = new HashMap<String, String>();

    public void byWidget(){

        //DEV
        timerS = (TextView) findViewById(R.id.timerS);
        timerE = (TextView) findViewById(R.id.timerE);
        timerT = (TextView) findViewById(R.id.timerT);

        title_2 = (TextView) findViewById(R.id.title_2);
        list_mac = (RecyclerView) findViewById(R.id.list_mac);
        list_basket = (RecyclerView) findViewById(R.id.list_basket);
        list_item_basket = (ListView) findViewById(R.id.list_item_basket);

        mac_image = (ImageView) findViewById(R.id.mac_image);

        editDocTab_1();

        bt_delete = (TextView) findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = "";
                String data = "";
                for(int i=xlist_item_basket.size()-1;i>=0;i--){
                    if(xlist_item_basket.get(i).isChk()){
                        id = id+xlist_item_basket.get(i).getRow_id()+",";
                        data = data+xlist_item_basket.get(i).getSterileDetailID() + "@" + xlist_item_basket.get(i).getWashDetailID() + "@" + xlist_item_basket.get(i).getItemStockID() + "@";
                    }
                }

                item_to_delete(id,data);
            }
        });

        bt_select_all = (TextView) findViewById(R.id.bt_select);
        bt_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!is_select_all){
                    is_select_all = true;
                    bt_select_all.setText("ไม่เลือกทั้งหมด");
                }else{
                    is_select_all = false;
                    bt_select_all.setText("เลือกทั้งหมด");
                }

                for(int i=0;i<xlist_item_basket.size();i++){
                    xlist_item_basket.get(i).setChk(is_select_all);
                }

                list_item_basket.invalidateViews();
                get_list_checkbox_to_delete();

            }
        });

        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list_mac.setLayoutManager(lm);

        list_basket.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        alert_builder = new AlertDialog.Builder(SterileActivity.this);

        loadind_dialog = new ProgressDialog(SterileActivity.this);
        loadind_dialog.setCanceledOnTouchOutside(false);
        loadind_dialog.setMessage("Sync ...");

//        SQLite_loadind_dialog = new ProgressDialog(SterileActivity.this);
//        SQLite_loadind_dialog.setCanceledOnTouchOutside(false);
//        SQLite_loadind_dialog.setMessage("กำลังโหลดข้อมูล");

        ll_mac = (LinearLayout) findViewById(R.id.ll_mac);
        ll_mac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_mac("",View.GONE);
                show_basket("",View.GONE);
                mac_id_non_approve = -1;
                list_mac_adapter.select_mac_pos=-1;
                basket_pos_non_approve = -1;
                list_basket_adapter.select_basket_pos=-1;
                reload_mac();
            }
        });

        rl_basket = (LinearLayout) findViewById(R.id.rl_basket);
        rl_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_basket("",View.GONE);
                basket_pos_non_approve = -1;
                list_basket_adapter.select_basket_pos=-1;

                Log.d("tog_delete","1");
                reload_basket();
            }
        });

        rl_basket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(xlist_basket.get(basket_pos_non_approve).getMacId().equals("null")){
                    return false;
                }

                new AlertDialog.Builder(SterileActivity.this)
                        .setMessage("ต้องการลบตะกร้าออกจากเครื่องหรือไม่")

                        .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = "";
                                String data = "";

                                for(int i=xlist_item_basket.size()-1;i>=0;i--){
                                    id = id+xlist_item_basket.get(i).getRow_id()+",";
                                    data = data+xlist_item_basket.get(i).getSterileDetailID() + "@" + xlist_item_basket.get(i).getWashDetailID() + "@" + xlist_item_basket.get(i).getItemStockID() + "@";
                                    Log.d("tog_delete","data = "+data);
                                }

                                delete_basket_from_mac(data,list.get(list_mac_adapter.select_mac_pos).getDocNo());

                            }
                        })

                        .setNegativeButton("ยกเลิก", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

                return false;
            }
        });

        mac_text = (TextView) findViewById(R.id.mac);
        macname = (TextView) findViewById(R.id.macname);
        basketname = (TextView) findViewById(R.id.basketname);
        text_qty = (TextView) findViewById(R.id.text_qty);

        get_machine("null");

        is_set_test_machine();
        box_item_detail = (LinearLayout) findViewById(R.id.box_item_detail);
        box_item_detail2 = (LinearLayout) findViewById(R.id.box_item_detail2);
        lv_itemdetail = (ListView) findViewById(R.id.lv_itemdetail);
        bt_add_colse = (Button) findViewById(R.id.bt_add_colse);
        scan_macname = (TextView) findViewById(R.id.scan_macname);
        scan_basketname = (TextView) findViewById(R.id.scan_basketname);
        scan_qty = (TextView) findViewById(R.id.scan_qty);

        bt_add_colse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadind_dialog_show();
                reload_mac();
                list_usagecode_all.clear();
                list_usagecode_to_add.clear();
                list_usagecode_to_false.clear();
                usagecode_to_add.clear();
                lv_itemdetail_adapter.notifyDataSetChanged();
                show_detail(false);
            }
        });

        lv_itemdetail_adapter = new itemAdapter(SterileActivity.this, list_usagecode_all,usagecode_to_add);

        lv_itemdetail.setAdapter(lv_itemdetail_adapter);

    }

//    public void list_basket_adapter_notifyDataSetChanged(){
//        if(get_mac_select_id().equals("Empty")){
//
//            for(){
//
//            }
//            if (get_mac_select_doc().equals(mData.get(position).getRefDocNo())) {
//                holder.ll.setVisibility(View.GONE);
//                holder.qty.setVisibility(View.GONE);
//            }else{
//                Log.d("tog_getbasket_mac", "Basket  = " + (mData.get(position).getName()));
//                Log.d("tog_getbasket_mac", "getRefDocNo = " + (mData.get(position).getRefDocNo()));
//            }
//        }else{
//            xlist_basket_onShow = xlist_basket;
//            list_mac_adapterNotifyDataSetChanged();
//        }
//    }

    public void is_set_test_machine(){

        LinearLayout tab_basket = (LinearLayout) findViewById(R.id.tab_basket);
        LinearLayout tab_item = (LinearLayout) findViewById(R.id.tab_item);
        TextView title_test = (TextView) findViewById(R.id.title_test);
        TextView textViewBasketH = (TextView) findViewById(R.id.textViewBasketH);

        if(test_machine){

            title_test.setVisibility(View.VISIBLE);

            tab_basket.setVisibility(View.GONE);
            tab_item.setVisibility(View.GONE);
            list_basket.setVisibility(View.GONE);
            list_item_basket.setVisibility(View.GONE);
            textViewBasketH.setVisibility(View.GONE);
            SR_IsUsedBasket_M1 = false;
        }else{
            Log.d("tog_tag_get_basket","1");

            title_test.setVisibility(View.GONE);

            tab_basket.setVisibility(View.VISIBLE);
            tab_item.setVisibility(View.VISIBLE);
            list_item_basket.setVisibility(View.VISIBLE);

            if(SR_IsUsedBasket_M1){
                list_basket.setVisibility(View.VISIBLE);
                textViewBasketH.setVisibility(View.VISIBLE);
                emtpyPos = 1;
            }else{
                list_basket.setVisibility(View.GONE);
                textViewBasketH.setVisibility(View.GONE);
            }

            get_basket("null");
        }
    }

    public void show_detail(boolean show) {
        if(show){
            scan_qty.setText("");
            box_item_detail.animate().alpha(1.0f).setDuration(50).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    box_item_detail.setVisibility(View.VISIBLE);
                    box_item_detail2.animate().translationY(0).setDuration(200);
                }
            });
        }else{
            bt_add_colse.setEnabled(false);
            box_item_detail.animate().alpha(0.0f).setDuration(200).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    box_item_detail.setVisibility(View.GONE);
                }
            });
            box_item_detail2.animate().translationY(box_item_detail.getHeight()).setDuration(200);
        }

    }

    public void editDocTab_1(){

        LinearLayout edit_doc_tab_1 = (LinearLayout) findViewById(R.id.edit_doc_tab_1);

        if(!isShow_editDocTab_1){
            edit_doc_tab_1.setVisibility(View.GONE);
            return;
        }

        mac_round = (Spinner) findViewById(R.id.mac_round);
        ArrayList<String> data_mac_round = new ArrayList<String>();
        data_mac_round.add("");
        for(int i=1;i<=100;i++){
            data_mac_round.add(i+"");
        }
        adp_mac_round = new ArrayAdapter<String>(SterileActivity.this, R.drawable.spn_bg_sr,data_mac_round);
        adp_mac_round.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mac_round.setAdapter(adp_mac_round);
        mac_round.setEnabled(false);

        mac_round.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("tog_change_doc_round","mac_round_on_change = "+mac_round_on_change);
                Log.d("tog_change_doc_round","change_doc_round = "+i);

                if(mac_round_on_change!=i && i>0) {
                    if (list_mac_adapter.select_mac_pos >= 0) {
                        if (!list.get(list_mac_adapter.select_mac_pos).getMachineID().equals(mac_empty_id)) {
                            change_doc_round(i+"",list.get(list_mac_adapter.select_mac_pos).getDocNo());
                        } else {
                            Log.d("tog_change_doc_round", "mac_empty_id");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("tog_change_doc_round","onNothingSelected");
            }
        });

        mac_pro_test = (Spinner) findViewById(R.id.mac_pro_test);
        mac_pro_test.setEnabled(false);
        mac_pro_test.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String val = data_mac_pro_test.get(mac_pro_test.getSelectedItem().toString());
                if (list_mac_adapter.select_mac_pos >= 0) {
                    if (!list.get(list_mac_adapter.select_mac_pos).getMachineID().equals(mac_empty_id)) {
                        change_test_program(val,list.get(list_mac_adapter.select_mac_pos).getDocNo());
                    } else {
                        Log.d("tog_mac_pro_test", "mac_empty_id");
                    }
                }else {
                    Log.d("tog_mac_pro_test", "select_mac_pos = "+list_mac_adapter.select_mac_pos);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("tog_change_doc_round","onNothingSelected");
            }
        });

        ArrayList<String> mac_test_point_data_default = new ArrayList<String>();
        mac_test_point_data_default.add(" ");
        adp_mac_test_point_default = new ArrayAdapter<String>(SterileActivity.this,R.drawable.spn_bg_sr,mac_test_point_data_default);
        adp_mac_test_point_default.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mac_test_point = (Spinner) findViewById(R.id.mac_test_point);
        mac_test_point.setEnabled(false);
        mac_test_point.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("tog_mac_round_on_change","mac_round_on_change = "+mac_round_on_change);

                if(mac_round_on_change!=i && i>0) {
                    if (list_mac_adapter.select_mac_pos >= 0) {
                        if (!list.get(list_mac_adapter.select_mac_pos).getMachineID().equals(mac_empty_id)) {
                            change_doc_round(i+"",list.get(list_mac_adapter.select_mac_pos).getDocNo());
                        } else {
                            Log.d("tog_change_doc_round", "mac_empty_id");
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d("tog_change_doc_round","onNothingSelected");
            }
        });
    }

    public void loadind_dialog_show(){

        Log.d("tog_loadind_dialog","loadind_dialog_show");
        if(!loadind_dialog.isShowing()){
            time_cnt_back = 0;
            loadind_dialog.show();
        }
    }

    public void loadind_dialog_dismis(){

        Log.d("tog_loadind_dialog","loadind_dialog_dismis");
        if(loadind_dialog.isShowing()){
            loadind_dialog.dismiss();
        }
    }

    public void get_machine(String mac_id) {

        Log.d("tog_tag_get_machine","-------------------------------------------get_machine-------------------------------------------");
        class get_machine extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mac_is_working = false;
                bt_select_all.setVisibility(View.VISIBLE);
                mac_image.setBackgroundResource(R.drawable.ic_sterile_blue);
                ll_mac.setBackgroundResource(R.drawable.rectangle_blue);
                mac_text.setTextColor(getResources().getColor(R.color.colorTitleGray));
                macname.setTextColor(getResources().getColor(R.color.colorTitleGray));

                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loadind_dialog_dismis();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    Log.d("tog_position_machine","mac_id = "+mac_id);
                    if(mac_id.equals("null")){
                        list.clear();
                        map_machine.clear();

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {
                                list.add(new ModelMachine(c.getString("xID"),c.getString("xMachineName2"),c.getString("IsActive"),c.getString("IsBrokenMachine"),c.getString("DocNo"),"",c.getString("FinishTime")));
                                map_machine.put(c.getString("xID"),i);

                                if(c.getInt("IsActive")==1){
                                    if(c.getInt("FinishTime")<=0){
                                        updateFinishMachine(c.getInt("xID"),c.getString("DocNo"));
                                    }
                                }
                            }
                        }

                        if(SR_IsUsedBasket_M1){
                            list.add(new ModelMachine(mac_empty_id,mac_empty_id,"0","0",mac_empty_id,"",""));
                        }

                        Log.d("tog_position_machine","setAdapter = S");
                        list_mac_adapter = new ListBoxMachineAdapter(SterileActivity.this, list,list_mac);
                        list_mac.setAdapter(list_mac_adapter);

                        Log.d("tog_position_machine","setAdapter = E");
                        show_mac("",View.GONE);
                        mac_id_non_approve = -1;
                        show_basket("",View.GONE);
                        basket_pos_non_approve = -1;

                        //toy
                        check_active_machine_handler.postDelayed(check_active_machine_runnable, 5000);

                    }else{
                        if(mac_id.equals(mac_empty_id)){
                            Log.d("tog_mac_empty_id","emtpyPos = "+emtpyPos);
                            if(emtpyPos>0){
                                list.get(list.size()-emtpyPos).setIsActive("0");
                                mac_id_non_approve = list.size()-emtpyPos;
                            }

                            reload_basket();
                        }else{
                            for (int i = 0; i < rs.length(); i++) {
                                JSONObject c = rs.getJSONObject(i);
                                Log.d("tog_position_machine","xID = "+c.getString("xID")+"---"+mac_id);

                                Log.d("tog_timer_php","onPostExecute get_sterilemachine.php");
                                for (int j = 0; j < list.size(); j++) {
                                    if(list.get(j).getMachineID().equals(c.getString("xID"))){
                                        list.get(j).setIsActive(c.getString("IsActive"));
                                        list.get(j).setIsBrokenMachine(c.getString("IsBrokenMachine"));
                                        list.get(j).setDocNo(c.getString("DocNo"));

                                        if(c.getString("xID").equals(mac_id)){

                                            if(c.getString("IsActive").equals("1")){
//                                                show_dialog("Warning","เครื่องกำลังทำงาน");
                                                mac_is_working = true;

                                                bt_select_all.setVisibility(View.GONE);
                                                mac_image.setBackgroundResource(R.drawable.ic_sterile_red);
                                                ll_mac.setBackgroundResource(R.drawable.rectangle_red);
                                                mac_text.setTextColor(Color.WHITE);
                                                macname.setTextColor(Color.WHITE);

                                                get_doc_in_mac(c.getString("DocNo"));
                                                mac_id_non_approve = j;
                                            }
                                            else if(c.getString("IsBrokenMachine").equals("1")){
                                                show_dialog("Warning","เครื่องไม่พร้อมใช้งาน");
                                            }
                                            else{
                                                if(c.getString("DocNo").equals("null")){
//                                                    dialog_wait_scan(new String[]{wait_scan_program+"",list.get(j).getMachineID()});

                                                    dialog_wait_scan(new String[]{wait_scan_employee+"",list.get(j).getMachineID()});
                                                }else{

                                                    Log.d("get_doc_in_mac",c.getString("DocNo"));

                                                    Log.d("tog_timer_php","ส่งต่อ get_doc_in_mac (ดึงข้อมูลเอกสารในเครื่อง)");

                                                    get_doc_in_mac(c.getString("DocNo"));
                                                    mac_id_non_approve = j;
                                                }

                                            }


                                        }
                                    }
                                }

                            }
                        }

                    }


                } catch (JSONException e) {

                    show_log_error("get_sterilemachine.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {

                Log.d("tog_timer_php","เรียก get_sterilemachine.php");
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("mac_id", mac_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterilemachine.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_add_basket","get_machine result = "+result);

                Log.d("tog_timer_php","รับข้อมูล = "+result);
                return result;
            }

            // =========================================================================================
        }

        get_machine obj = new get_machine();

        obj.execute();
    }

    public void get_basket(String basket_id) {

        class get_basket extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                loadind_dialog_dismis();
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    Log.d("tog_loop","basket_id = "+basket_id);
                    if(basket_id.equals("null")){
                        xlist_basket.clear();

//                        xlist_basket.add(new BasketTag(basket_empty_id,basket_empty_id,"","",0,""));

                        int cnt =0;
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                xlist_basket.add(new BasketTag(c.getString("xID"),c.getString("BasketName"),c.getString("BasketCode"),c.getString("InMachineID"),0,c.getString("TypeId"),c.getString("RefDocNo"),cnt));

                                map_index_xlist_basket.put(c.getString("xID"),cnt);
                                cnt++;
                            }
                        }
                        list_basket_adapter = new ListBoxBasketAdapter(SterileActivity.this, xlist_basket, xlist_basket,list_basket,-1);
                        list_basket.setAdapter(list_basket_adapter);

                        show_basket("",View.GONE);
                        basket_pos_non_approve = -1;

                        if(mac_id_non_approve>=0){
                            get_item_in_basket(-1,list.get(mac_id_non_approve).getDocNo());
                        }

                    }else{
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                if(c.getString("BasketCode").toLowerCase().equals(basket_id.toLowerCase())){

                                    Log.d("tog_add_basket","get_basket start loop ");
                                    for (int j = 0; j < xlist_basket.size(); j++) {
                                        if(xlist_basket.get(j).getBasketCode().equals(c.getString("BasketCode"))){

                                            xlist_basket.get(j).setMacId(c.getString("InMachineID"));
                                            xlist_basket.get(j).setRefDocNo(c.getString("RefDocNo"));

                                            int mac_pos = mac_id_non_approve;

                                            Log.d("tog_getbasket","j = "+j);

                                            Log.d("tog_loop","mac_pos = "+mac_pos);
                                            if(mac_pos>=0){
                                                String mac_id = list.get(mac_pos).getMachineID();
                                                Log.d("tog_add_basket","get_basket mac_id= "+mac_id);
                                                Log.d("tog_timer_php","ส่งต่อ get_item_in_basket  (ดึงข้อมูลรายการในตะกร้า)");
                                                if(c.getString("InMachineID").equals("null")){//ตะกร้าไม่มีเครื่อง
                                                    //add basket in mac
                                                    Log.d("tog_getbasket","mac_empty_id = "+mac_id.equals(mac_empty_id));
                                                    if(mac_id.equals(mac_empty_id)){
                                                        list_mac_adapter.onScanSelect(mac_id_non_approve);
                                                        list_basket_adapter.onScanSelect(j);
                                                    }else{
                                                        is_add_item = true;
                                                    }

                                                    Log.d("tog_loop","get_basket => get_item_in_basket -1");

                                                    get_item_in_basket(j,list.get(mac_pos).getDocNo());

                                                }else if (c.getString("InMachineID").equals(mac_id)){
                                                    Log.d("tog_loop","get_basket => get_item_in_basket -2");
                                                    get_item_in_basket(j,list.get(mac_pos).getDocNo());
                                                }else{
                                                    show_dialog("Warning","ตะกร้าไม่พร้อมใช้งาน");
                                                }
                                            }else{
                                                list_basket_adapter.onScanSelect(j);
                                                get_item_in_basket(j,"");
                                            }

                                            list_mac_adapter.notifyDataSetChanged();
                                            list_mac_adapterNotifyDataSetChanged();
                                        }
                                    }

                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    show_log_error("get_sterilebasket.php Error = "+e);
                    e.printStackTrace();
                }

                Log.d("tog_timer_php","end get_sterilebasket");
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                Log.d("tog_timer_php","เรียก get_sterilebasket.php");
                data.put("basket_id", basket_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterilebasket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_basket","data = "+data);
                Log.d("tog_basket","result = "+result);

                Log.d("tog_add_basket","get_basket = "+result);
                Log.d("tog_timer_php","รับข้อมูล = "+result);
                return result;
            }

            // =========================================================================================
        }

        get_basket obj = new get_basket();

        obj.execute();
    }

    public void check_active_machine() {

        Log.d("tog_chk_active_mac","check_active_machine");
        class check_active_machine extends AsyncTask<String, Void, String> {
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

                            list.get(map_machine.get(c.getString("xID"))).setFinishTime(c.getString("FinishTime"));
                            list.get(map_machine.get(c.getString("xID"))).setIsActive(c.getString("IsActive"));
                            list.get(map_machine.get(c.getString("xID"))).setIsBrokenMachine(c.getString("IsBrokenMachine"));
                            list.get(map_machine.get(c.getString("xID"))).setDocNo(c.getString("DocNo"));
                            if(c.getInt("IsActive")==1){
                                if(c.getInt("FinishTime")<=0){
                                    updateFinishMachine(c.getInt("xID"),c.getString("DocNo"));
                                }
                            }
                        }
                    }

                    list_mac_adapter.notifyDataSetChanged();

                } catch (JSONException e) {
//                    show_log_error("get_washmachine.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("mac_id", "null");
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash1
                    result = httpConnect.sendPostRequestBackground(getUrl + "get_sterilemachine.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_chk_mac_isAc","result = "+result);

                return result;
            }

            // =========================================================================================
        }

        check_active_machine obj = new check_active_machine();

        obj.execute();
    }

    public void updateFinishMachine(final int p_machine_no, final String p_doc_no) {

        if(p_machine_no == 0 || p_doc_no == null)
            return;

        class UpdateFinishMachine extends AsyncTask<String, Void, String> {

            // variable
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
//                    JSONObject jsonObj = new JSONObject(s);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for (int i = 0; i < rs.length(); i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        if (c.getString("result").equals("A")) {
////                            check_active_machine();
//                        }
//                    }
//                }catch (Exception e){
//                    e.printStackTrace();
//                }
//            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_docno", p_doc_no);
                data.put("p_machine_no", Integer.toString(p_machine_no));

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_sterile_finish_time_json.php", data);

                return result;
            }

            // // ---------------------------------------------------------------
        }

        UpdateFinishMachine obj = new UpdateFinishMachine();
        obj.execute();
    }


    public void show_bt_delete(boolean s){
        if(s){
            bt_delete.setVisibility(View.VISIBLE);
        }else{
            bt_delete.setVisibility(View.GONE);
        }
    }

    public void get_list_checkbox_to_delete(){
        int x = 0;
        for(int i=0;i<xlist_item_basket.size();i++){
            if(xlist_item_basket.get(i).isChk()){
                x++;
            }
        }

        if(x==xlist_item_basket.size() && xlist_item_basket.size()!=0){
            is_select_all = true;
            bt_select_all.setText("ไม่เลือกทั้งหมด");
        }else{
            is_select_all = false;
            bt_select_all.setText("เลือกทั้งหมด");
        }

        if(x==0){
            show_bt_delete(false);
        }else{
            show_bt_delete(true);
        }

    }


    public void move_item_basket(String id,String basket_id,String usage_code){

        loadind_dialog_dismis();

        class move_item_basket extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
                            if(list_basket_adapter.select_basket_pos>=0){
                                add_item_to_basket(xlist_basket.get(list_basket_adapter.select_basket_pos).getID(),usage_code);
                            }else{
                                add_item_to_basket("-",usage_code);
                            }

                        }

                    }

                } catch (JSONException e) {
                    show_log_error("delete_item_in_basket.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("ID", id);
                data.put("basket_id", basket_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "delete_item_in_basket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_delete_item","data = " + data);
                Log.d("tog_delete_item","result = " + result);
                return result;
            }

            // =========================================================================================
        }

        AlertDialog.Builder builder1 = new AlertDialog.Builder(SterileActivity.this);
        builder1.setMessage("รายการนี้อยู่ในตะกร้าอื่น ต้องการย้ายรายการหรือไม่");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "ย้ายรายการ",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        move_item_basket obj = new move_item_basket();
                        obj.execute();
                    }
                });

        builder1.setNegativeButton(
                "ยกเลิก",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void item_to_delete(String id,String data){

        if(list_basket_adapter.select_basket_pos<0){
            removeSterileDetail(data,list.get(list_mac_adapter.select_mac_pos).getDocNo());
            return;
        }
        class delete_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
                            if(list_mac_adapter.select_mac_pos<0){
                                get_item_in_basket(list_basket_adapter.select_basket_pos,"");
                            }if(list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                                get_item_in_basket(list_basket_adapter.select_basket_pos,"");
                            }else{
                                removeSterileDetail(data,list.get(list_mac_adapter.select_mac_pos).getDocNo());
                            }
                        }

                    }

                } catch (JSONException e) {
                    show_log_error("delete_item_in_basket.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("ID", id);
                data.put("basket_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getID());
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "delete_item_in_basket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_delete_item","data = " + data);
                Log.d("tog_delete_item","result = " + result);
                return result;
            }

            // =========================================================================================
        }

        delete_item obj = new delete_item();

        obj.execute();
    }

    public void removeSterileDetail(String p_data,String doc_no) {

        class RemoveSterileDetail extends AsyncTask<String, Void, String> {

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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            get_item_in_basket(list_basket_adapter.select_basket_pos,doc_no);
                        }
                    }

                } catch (JSONException e) {
                    show_log_error("cssd_remove_sterile_details_json.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_data", p_data);
                data.put("p_docno", doc_no);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                // ---------------------------------------------------------------------------------
                // call php
                // ---------------------------------------------------------------------------------
                String result = httpConnect.sendPostRequest(getUrl + "cssd_remove_sterile_details_json.php", data);

                Log.d("tog_delete_basket","data = " + data);
                Log.d("tog_delete_basket","result = " + result);

                return result;
            }

            // // ---------------------------------------------------------------
        }

        RemoveSterileDetail obj = new RemoveSterileDetail();
        obj.execute();
    }

    public void add_item_to_basket(String basket_id,String usage_code){
        if(stack_scan){
            if(box_item_detail.getVisibility()==View.GONE){

                show_detail(true);
            }
            stack_add_item_to_basket(basket_id,usage_code);
        }else{
            normal_add_item_to_basket(basket_id,usage_code);
        }

    }

    public void normal_add_item_to_basket(String basket_id,String usage_code){
        class add_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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

                            if(list_mac_adapter.select_mac_pos != 0){
                                if(!list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                                    addSterileDetailById(
                                            list.get(list_mac_adapter.select_mac_pos).getDocNo(),
                                            c.getString("w_id")+",",
                                            basket_id
                                    );
                                }else{
                                    reload_basket();
//                                    playAudio("okay");
                                }
                            }else{
                                reload_basket();
//                                playAudio("okay");
                            }
                        }else if (c.getString("result").equals("D")){
                            if(c.getString("basket_id").equals("---")){
                                boolean sDocNo = true;
                                if(list_mac_adapter.select_mac_pos != 0){
                                    if(!list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                                        if(c.getString("sDocNo").equals(list.get(list_mac_adapter.select_mac_pos).getDocNo())){
                                            show_dialog("Warning","รายการซ้ำ","repeat_scan");
                                            x_vibrator.vibrate(500);
//                                            show_dialog("Warning","รายการซ้ำ");
                                            sDocNo = false;
                                        }
                                    }
                                }

                                if(sDocNo){

                                    show_dialog("Warning","รายการอยู่ในเอกสารอื่น ("+c.getString("sDocNo")+")","no");
                                }

                            }else{
                                if(c.getString("basket_id").equals(basket_id)){
                                    show_dialog("Warning","รายการซ้ำ","repeat_scan");
                                    x_vibrator.vibrate(500);
//                                    show_dialog("Warning","รายการซ้ำ");
                                }else{
//                                show_dialog("Warning","รายการนี้อยู่ในตะกร้าอื่น","no");
//                                show_log_error("Usage = "+c.getString("basket_id")+" --- This = "+basket_id);
                                    move_item_basket(c.getString("item_id")+",",c.getString("basket_id"),usage_code);
                                }
                            }

                        }else if(c.getString("result").equals("T")){
                            show_dialog("Warning","ไม่สามารถเพิ่มได้","no");
                        }else if(c.getString("result").equals("M")){
                            show_dialog("Warning","บางรายการไม่สามารถเข้าเครื่องได้","no");
                        }else if(c.getString("result").equals("N")){
                            show_dialog("Warning",c.getString("Message"),"no");
                            x_vibrator.vibrate(500);
//                            show_dialog("Warning",c.getString("Message"));
                        }else{
                            x_vibrator.vibrate(500);
                            show_dialog("Warning","ไม่พบรายการ","no_item_found");
//                            show_dialog("Warning","ไม่พบรายการ");
                        }

                    }

                } catch (JSONException e) {
                    show_log_error("add_item_to_basket.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", basket_id);
                data.put("usage_code", usage_code);
                data.put("p_DB", p_DB);

                Log.d("tog_add_item","getDocNo = " + list.get(list_mac_adapter.select_mac_pos).getDocNo());

                if(!list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                    data.put("program_id", list.get(list_mac_adapter.select_mac_pos).getTypeID());
                    data.put("mac_id", list.get(list_mac_adapter.select_mac_pos).getMachineID());
                }else{
                    data.put("program_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getTypeProcessID());
                }
//                else if(xlist_item_basket.size()>0){
//                    if(tid){
//                        data.put("program_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getTypeProcessID());
//                    }
//                }
                String result = null;

                if(sp_connect){
                    result = DBConnect.getItemstockAddToSterileBasket(data);
                }else{
                    try {
                        result = httpConnect.sendPostRequest(getUrl + "add_item_to_basket.php", data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                Log.d("tog_add_item","data = " + data);
                Log.d("tog_add_item","result = " + result);
                return result;
            }

            // =========================================================================================
        }

        add_item obj = new add_item();

        obj.execute();
    }

    ArrayAdapter<itemAdapter> lv_itemdetail_adapter;

    public void stack_add_item_to_basket(String basket_id, String usage_code){
        list_usagecode_to_add.remove(usage_code);
        list_usagecode_to_add.add(0,usage_code);
        usagecode_to_add.put(usage_code,"loading...");
        list_usagecode_all.clear();
        list_usagecode_all.addAll(list_usagecode_to_false);
        list_usagecode_all.addAll(list_usagecode_to_add);
        lv_itemdetail_adapter.notifyDataSetChanged();

        class add_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
//                loadind_dialog_show();
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
                            usagecode_to_add.put(usage_code,"เพิ่มสำเร็จ");
                            list_usagecode_to_add.remove(usage_code);
                            list_usagecode_to_add.add(usage_code);
                            continue;
                        }else if (c.getString("result").equals("D")){
                            if(c.getString("basket_id").equals("---")){
                                boolean sDocNo = true;
                                if(list_mac_adapter.select_mac_pos != 0){
                                    if(!list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
                                        if(c.getString("sDocNo").equals(list.get(list_mac_adapter.select_mac_pos).getDocNo())){
//                                            show_dialog("Warning","รายการซ้ำ","repeat_scan");
                                            usagecode_to_add.put(usage_code,"รายการซ้ำ");
                                            sDocNo = false;
                                        }
                                    }
                                }

                                if(sDocNo){

//                                    show_dialog("Warning","รายการอยู่ในเอกสารอื่น ("+c.getString("sDocNo")+")","no");
                                    usagecode_to_add.put(usage_code,"อยู่ในเอกสาร "+c.getString("sDocNo"));
                                }

                            }else{
                                if(c.getString("basket_id").equals(basket_id)){
//                                    show_dialog("Warning","รายการซ้ำ","repeat_scan");
                                    usagecode_to_add.put(usage_code,"รายการซ้ำ");
                                }else{
//                                    show_dialog("Warning","รายการนี้อยู่ในตะกร้าอื่น","no");
                                    usagecode_to_add.put(usage_code,"รายการนี้อยู่ในตะกร้าอื่น");
                                }
                            }

                        }else if(c.getString("result").equals("T")){
//                            show_dialog("Warning","ไม่สามารถเพิ่มได้","no");
                            usagecode_to_add.put(usage_code,"ไม่สามารถเพิ่มได้");
                        }else if(c.getString("result").equals("M")){
//                            show_dialog("Warning","บางรายการไม่สามารถเข้าเครื่องได้","no");
                            usagecode_to_add.put(usage_code,"บางรายการไม่สามารถเข้าเครื่องได้");
                        }else if(c.getString("result").equals("N")){
//                            show_dialog("Warning",c.getString("Message"),"no");
                            usagecode_to_add.put(usage_code,c.getString("Message"));
                        }else{
//                            show_dialog("Warning","ไม่พบรายการ","no_item_found");
                            usagecode_to_add.put(usage_code,"ไม่พบรายการ");
                        }

                        playAudio("no");
                        x_vibrator.vibrate(500);
                        list_usagecode_to_add.remove(usage_code);
                        list_usagecode_to_false.remove(usage_code);
                        list_usagecode_to_false.add(usage_code);
                    }
                    list_usagecode_all.clear();
                    list_usagecode_all.addAll(list_usagecode_to_false);
                    list_usagecode_all.addAll(list_usagecode_to_add);
                    scan_qty.setText("("+list_usagecode_all.size()+")");
//                    loadind_dialog_dismis();
                    lv_itemdetail_adapter.notifyDataSetChanged();

                    bt_add_colse.setEnabled(false);
                    boolean is_loaded = true;
                    for(int i =0;i<list_usagecode_to_add.size();i++){
                        if(usagecode_to_add.get(list_usagecode_to_add.get(i)).equals("loading...")){
                            is_loaded = false;
                            break;
                        }
                    }

                    if(list_usagecode_to_add.size()<=0){
                        bt_add_colse.setEnabled(true);
                    }

                    if(is_loaded){
                        bt_add_colse.setEnabled(true);
                    }

//                    if(list_usagecode_to_false.size()<=0 &&is_loaded){
//                        show_detail(false);
//                    }

                } catch (JSONException e) {
                    show_log_error("add_item_to_basket.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", basket_id);
                data.put("usage_code", usage_code);
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

        add_item obj = new add_item();

        obj.execute();
    }

    public void startMachine(String p_doc_no,String p_SterileMachineID) {

        if(p_doc_no.equals("null")){
            return;
        }

        class StartMachine extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
                            list_mac_adapter.select_mac_pos = -1;
                            list_basket_adapter.select_basket_pos = -1;
                            get_machine("null");
                            Log.d("tog_tag_get_basket","2");
                            get_basket("null");
                            list_item_basket.setAdapter(null);

                            playAudio("okay");
                        }

                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_docno", p_doc_no);
                data.put("p_SterileMachineID", p_SterileMachineID);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_sterile_start_time_json.php", data);

                return result;
            }
        }

        StartMachine obj = new StartMachine();
        obj.execute();
    }

    public void addSterileDetailById(String p_docno, String p_data,String basket_id) {

        final boolean mode = false;

        class AddSterileDetail extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    JSONObject c =null;
                    for(int i=0;i<rs.length();i++){
                        c = rs.getJSONObject(i);
                    }

                    if(c.getString("result").equals("A")) {
                        playAudio("okay");

                        Log.d("tog_loop","addSterileDetailById => reload_basket");

                        Log.d("tog_tag_reload_basket","5");

                        Log.d("tog_timer_php","addSterileDetailById => reload_basket");
                        reload_basket();

//                            if(basket_id.equals("")){
//                                Log.d("tog_timer_php","ส่งต่อ reload_basket (โหลดรายการในเครื่องหลังเพิ่ม)");
//                                reload_basket();
//                            }else{
//                                if(rl_basket.getVisibility()==View.VISIBLE){
//                                    reload_basket();
//                                }else{
////                                    set_timerE();
//                                    Log.d("tog_timer_php"," ==================================== end2 ==================================== ");
//                                }
//
//                            }
//                            else{
//
//                                Log.d("tog_timer_php","(โหลดตะกร้าในเครื่องหลังเพิ่ม)");
//                                Log.d("tog_timer_php","basket_id = "+basket_id);
//                                for (int j = 0; j < xlist_basket.size(); j++) {
//                                    Log.d("tog_timer_php","getBasketCode = "+xlist_basket.get(j).getBasketCode());
//                                    if(xlist_basket.get(j).getBasketCode().equals(basket_id)){
//
//                                        xlist_basket.get(j).setMacId(list.get(list_mac_adapter.select_mac_pos).getMachineID());
//                                        xlist_basket.get(j).setRefDocNo(p_docno);
//
//                                        list_mac_adapter.notifyDataSetChanged();
//                                        list_mac_adapterNotifyDataSetChanged();
//
//                                        Log.d("tog_timer_php"," ==================================== end2 ==================================== ");
//                                    }
//                                }
//                            }

                    }else if(c.getString("result").equals("N")){
                        show_dialog("Warning",c.getString("mass"),"no");
                    }else{
                        show_dialog("Warning","ไม่สามารถเพิ่มรายการได้","no");
                    }

//                    reload_basket();
                } catch (JSONException e) {
                    show_log_error("cssd_add_sterile_detail_by_id.php Error = "+e);
                    e.printStackTrace();
                }finally{

                    Log.d("tog_getAudio","is_add_item = "+is_add_item);
                    is_add_item = false;
                    loadind_dialog_dismis();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                Log.d("tog_timer_php","เรียก cssd_add_sterile_detail_by_id.php");
                data.put("p_list_id", p_data);
                data.put("p_docno", p_docno);
                data.put("p_IsUsedUserOperationDetail", ((CssdProject) getApplication()).isSR_IsUsedUserOperationDetail() ? "1" : "0");

                data.put("p_is_status", mode ? "-1" : "1");

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                if(SR_IsUsedBasket_M1){
                    data.put("is_new", is_add_item+"");
                    data.put("BasketID", basket_id);
                }

                // Add Select Insert
                String result = httpConnect.sendPostRequest(getUrl + "cssd_add_sterile_detail_by_id.php", data);
                Log.d("tog_add","data = " + data);
                Log.d("tog_add","result = " + result);

                Log.d("tog_timer_php","รับข้อมูล = cssd_add_sterile_detail_by_id.php");

                return result;
            }

            // // ---------------------------------------------------------------
        }

        AddSterileDetail obj = new AddSterileDetail();
        obj.execute();
    }

    int item_in_basket_pos = -1;
    String item_in_basket_doc = "";
    public void get_item_in_basket(int pos,String doc){

        Log.d("tog_get_item","pos = " + pos);

        class get_item extends AsyncTask<String, Void, String> {
            String w_id = "";
            boolean get_data = true;

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.d("tog_timer_php","start get_item_in_sterile_basket");
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    ArrayList<ItemInBasket> list_item = new ArrayList<>();
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        Log.d("tog_loop_i","get_basket is_add_item = "+i);
//                            xlist_basket.get(pos).setTypeProcessID("");
                        if (c.getString("result").equals("A")) {

                            list_item.add(new ItemInBasket(
                                    c.getString("xID"),
                                    c.getString("ItemStockID"),
                                    c.getString("itemname"),
                                    c.getString("UsageCode"),
                                    "",
                                    c.getString("WashDetailID"),
                                    c.getString("SterileDetailID"),
                                    false
                            ));

                            w_id = w_id+c.getString("WashDetailID")+",";

                            Log.d("tog_loop","get_basket is_add_item = "+is_add_item);
                            if(is_add_item){
                                if(c.getString("SterileMachineID").equals("null")){
                                    show_dialog("Warning","บางรายการไม่สามารถเข้าเครื่องได้","no");
                                    typeID = "";
                                    is_add_item = false;
                                    get_data =false;
                                }else{
                                    if(SR_IsUsedBasket_M1){
//                                        Log.d("tog_timer_php","SterileProgramID = "+c.getString("UsageCode")+" -- "+c.getString("SterileProgramID"));
                                        if(!c.getString("SterileProgramID").equals(typeID)){
                                            show_dialog("Warning","ไม่สามารถเพิ่มได้","no");
                                            typeID = "";
                                            is_add_item = false;
                                            get_data =false;
                                        }
                                    }
                                }
                            }

//                                xlist_basket.get(pos).setTypeProcessID(c.getString("SterileProgramID"));
                        }else{
                            is_add_item = false;
//                            if(c.getString("result").equals("N")){
//                                show_dialog("Warning",c.getString("Message"),"no");
//                            }
                        }
                    }

                    Log.d("tog_timer_php","end get_item_in_sterile_basket");
                    Log.d("tog_get_item","is_add_item && rs.length()!=0 = " + (is_add_item && rs.length()!=0));
                    if(get_data){

                        xlist_item_basket.clear();
                        xlist_item_basket = list_item;

                        item_in_basket_pos = pos;
                        item_in_basket_doc = doc;

                        if(is_add_item){
                            Log.d("tog_getAudio","addSterileDetailById = 2" );

                            Log.d("tog_timer_php","ส่งต่อ addSterileDetailById (นำรายการเข้าเครื่อง)");
                            if(pos>=0){
                                addSterileDetailById( doc, w_id,xlist_basket.get(pos).getID());
                            }else{
                                addSterileDetailById( doc, w_id,"");
                            }
                        }else{
//                                if(tid){
//                                    if(xlist_basket.get(pos).getTypeProcessID().equals("")){
//                                        if(typeID.equals("")){
//                                            dialog_wait_scan(new String[]{wait_scan_type+"",pos+""});
//                                        }else{
//                                            xlist_basket.get(pos).setTypeProcessID(typeID);
//                                            reload_done(pos);
//                                        }
//                                    }else{
//                                        reload_done(pos);
//                                    }
//                                }else{
//                                    reload_done(pos);
//                                }

                            Log.d("tog_timer_php","ส่งต่อ reload_done (จบการทำงาน)");
                            reload_done(pos);

                        }
                    }


                } catch (JSONException e) {
                    show_log_error("get_item_in_sterile_basket.php Error = "+e);
                    e.printStackTrace();
                }
                loadind_dialog_dismis();

                Log.d("tog_add_basket"," ==================================== end ==================================== ");
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                Log.d("tog_timer_php","เรียก get_item_in_sterile_basket.php");
                if(pos>=0){
                    data.put("basket_id", xlist_basket.get(pos).getID());
                    data.put("DocNo", doc);
                }else{
                    data.put("basket_id", "-");
                    data.put("DocNo", doc);
                }

                data.put("p_DB", p_DB);
                data.put("typeID", typeID);

                if(mac_id_non_approve>=0){
                    if(!list.get(mac_id_non_approve).getDocNo().equals("Empty")){
                        data.put("mac_id", list.get(mac_id_non_approve).getMachineID());
                    }
                }

                String result = null;

                try {
                    //wash
                    result = httpConnect.sendPostRequest(getUrl + "get_item_in_sterile_basket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_get_item","get_item_in_basket data = " + data);
                Log.d("tog_get_item","get_item_in_basket result = " + result);

                Log.d("tog_add_basket","get_item_in_basket result = " + result);
                Log.d("tog_timer_php","get_item_in_basket result = " + result);
                Log.d("tog_timer_php","รับข้อมูล = get_item_in_sterile_basket.php");
                return result;
            }

            // =========================================================================================
        }

        get_item obj = new get_item();

        obj.execute();
    }
    
    public void playAudio(String sound){
        //nMidia.getAudio(sound);

        if(sound.equals("okay")){
//            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 80);
            toneG.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 1000);
        }else{
            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 1000);
        }
    }

    public void show_dialog(String title,String mass,String sound){
        playAudio(sound);
        show_dialog(title,mass);
    }

    public void show_dialog(String title,String mass){
        loadind_dialog_dismis();

        mac_id_non_approve = list_mac_adapter.select_mac_pos;

        list_mac_adapter.notifyDataSetChanged();
        if(!test_machine){
            basket_pos_non_approve = list_basket_adapter.select_basket_pos;
            list_mac_adapterNotifyDataSetChanged();
            list_item_basket.invalidateViews();
        }

        alert_builder.setTitle("");
        alert_builder.setMessage(mass);

        alert = alert_builder.create();

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                handler.removeCallbacks(runnable);
            }
        });

        alert.show();

        handler.postDelayed(runnable, 3000);
    }

    public void reload_mac(){

        Log.d("tog_tag_get_machine","2");
        if(list_mac_adapter.select_mac_pos<list.size()-emtpyPos&&list_mac_adapter.select_mac_pos>=0){
            String mac_ID = list.get(list_mac_adapter.select_mac_pos).getMachineID();

            Log.d("tog_add_basket","reload_mac mac_ID = "+mac_ID);
            Log.d("tog_timer_php","ส่งต่อ get_machine (ดึงข้อมูลเครื่องที่จะเพิ่มหลังรีเฟรช)");
            get_machine(mac_ID);
        }else{
            get_machine(mac_empty_id);
        }
    }

    public void reload_basket(){
        int basket_pos = basket_pos_non_approve;
//        show_basket("",View.GONE);
        Log.d("tog_basket","basket pos = "+basket_pos);
        Log.d("tog_loop","basket pos = "+basket_pos);

        if(basket_pos>=0){
            Log.d("tog_basket","getBasketCode = "+xlist_basket.get(basket_pos).getBasketCode());

            Log.d("tog_loop","reload_basket => get_basket");
            Log.d("tog_tag_get_basket","3");

            Log.d("tog_add_basket","reload_basket = "+xlist_basket.get(basket_pos).getBasketCode());
            Log.d("tog_timer_php","ส่งต่อ get_basket (ดึงข้อมูลตะกร้า)");
            get_basket(xlist_basket.get(basket_pos).getBasketCode());
        }else{

            Log.d("tog_tag_get_basket","4");
            get_basket("null");

//            loadind_dialog_dismis();

//            xlist_item_basket.clear();
//            list_item_basket_adapter = new ListItemBasketAdapter(SterileActivity.this,xlist_item_basket);
//            list_item_basket.setAdapter(list_item_basket_adapter);

//            list_mac_adapter.onScanSelect(mac_id_non_approve);
//            list_mac_adapterNotifyDataSetChanged();
//            list_mac_adapter.notifyDataSetChanged();
//            list_item_basket.invalidateViews();
        }
    }

    public void createSterile(String p_SterileProgramID, String p_SterileMachineID, String p_SterileTypeID,String p_Is_NonSelectRound) {

        class CreateSterile extends AsyncTask<String, Void, String> {


            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
//                            get_machine(p_SterileMachineID);
//                            dialog_wait_scan(new String[]{wait_scan_employee+"","FALSE",p_SterileMachineID,c.getString("DocNo")});

                            set_loder(loader, false,p_SterileMachineID,c.getString("DocNo"));
                        }else{
                            show_dialog("Warning","ไม่สามารถสร้างเอกสารได้");
                        }
                    }
                } catch (JSONException e) {
                    show_dialog("Warning","ไม่สามารถสร้างเอกสารได้");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_ungroup", "1");

                data.put("p_UserCode", ((CssdProject) getApplication()).getCustomerId()+"" );
                data.put("p_SterileProgramID", p_SterileProgramID);
                data.put("p_SterileMachineID", p_SterileMachineID);
                data.put("p_SterileTypeID", p_SterileTypeID);
                data.put("p_IsUsedDBUserOperation", SR_IsUsedDBUserOperation ? "1" : "0");
                data.put("p_IsRememberUserOperation", SR_IsRememberUserOperation ? "1" : "0");
                data.put("Istest_machine", test_machine?"1":"0");

                data.put("p_Is_NonSelectRound", p_Is_NonSelectRound);

                data.put("p_AddMode", "0");
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(getUrl+ "cssd_create_sterile_json.php", data);

                Log.d("tog_create_sterile","data = "+data);
                Log.d("tog_create_sterile","result = "+result);
                return result;
            }
        }

        CreateSterile obj = new CreateSterile();
        obj.execute();
    }

    public void change_doc_program(String p_SterileProgramID, String docno) {

        class change_doc_program extends AsyncTask<String, Void, String> {


            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
                            reload_mac();
                        }else{
                            show_dialog("Warning","ไม่สามารถเปลี่ยนโปรแกรมได้");
                        }
                    }
                } catch (JSONException e) {
                    show_dialog("Warning","ไม่สามารถเปลี่ยนโปรแกรมได้");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_SterileProgramID", p_SterileProgramID);
                data.put("docno", docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(getUrl+ "change_doc_program.php", data);

                Log.d("tog_change_doc_program","data = "+data);
                Log.d("tog_change_doc_program","result = "+result);
                return result;
            }
        }

        change_doc_program obj = new change_doc_program();
        obj.execute();
    }

    HashMap<String, String> data_mac_pro_test = new HashMap<String, String>();

    public void get_test_program(String S_Sterile_Type_Or_Process_ID,String TestProgramID){

        class get_test_program extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ArrayList<String> _data = new ArrayList<String>();
                _data.add("-");
                data_mac_pro_test.put("-","0");
                int pos_select = 0;
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            _data.add(c.getString("Label"));
                            data_mac_pro_test.put(c.getString("Label"),c.getString("TestProgramID"));

                            if(c.getString("TestProgramID").equals(TestProgramID)){
                                pos_select = i+1;
                            }
                        }
                    }

                    adp_mac_pro_test = new ArrayAdapter<String>(SterileActivity.this,R.drawable.spn_bg_sr,_data);
                    adp_mac_pro_test.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mac_pro_test.setAdapter(adp_mac_pro_test);

                    mac_pro_test.setSelection(pos_select);

                    if(SR_IsUsedLot){
                        get_test_point(TestProgramID);
                    }else{
                        loadind_dialog_dismis();
                    }

                } catch (JSONException e) {

                    show_log_error("get_test_program.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String result = null;

                data.put("S_Sterile_Type_Or_Process_ID", S_Sterile_Type_Or_Process_ID);
                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_test_program.php", data);
                } catch (Exception e) {
                    show_log_error("get_test_program.php");
                    e.printStackTrace();
                }

                Log.d("tog_get_test_program","result = "+result);

                return result;
            }

        }

        get_test_program obj = new get_test_program();

        obj.execute();
    }

    public void change_test_program(String TestProgramID, String docno) {

        class change_test_program extends AsyncTask<String, Void, String> {


            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d("tog_get_test_point","change_test_program");
                loadind_dialog_show();
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
                            if(SR_IsUsedLot){
                                get_test_point(TestProgramID);
                            }
                            loadind_dialog_dismis();
                        }else{
                            show_dialog("Warning","ไม่สามารถโปรแกรมทดสอบได้"+c.getString("result"));
                        }
                    }
                } catch (JSONException e) {
                    show_dialog("Warning","ไม่สามารถโปรแกรมทดสอบได้");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("TestProgramID", TestProgramID);
                data.put("docno", docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(getUrl+ "change_test_program.php", data);

                Log.d("tog_mac_pro_test", "data = "+data);

                return result;
            }
        }

        change_test_program obj = new change_test_program();
        obj.execute();
    }

    HashMap<String, String> data_mac_test_point = new HashMap<String, String>();
    public void get_test_point(String TestProgramID){

        class get_test_point extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadind_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                ArrayList<String> _data = new ArrayList<String>();

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        _data.add("Lot No."+c.getString("LotNo"));
                        data_mac_test_point.put("Lot No."+c.getString("LotNo"),c.getString("RowID"));

                    }

                    adp_mac_test_point = new ArrayAdapter<String>(SterileActivity.this,R.drawable.spn_bg_sr,_data);
                    adp_mac_test_point.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mac_test_point.setAdapter(adp_mac_test_point);
                    mac_test_point.setEnabled(true);
//                    mac_test_point.setSelection(pos_select);

                } catch (JSONException e) {

                    show_log_error("get_test_program.php Error = "+e);
                    e.printStackTrace();
                }

                loadind_dialog_dismis();
            }

            @Override
            protected String doInBackground(String... params) {
                String Json_data = "{\"TestProgramID\": \"" + TestProgramID + "\"}";

//                HashMap<String, String> data = new HashMap<String, String>();
//                data.put("TestProgramID", TestProgramID);

                String result = null;

                try {
                    result = httpConnect.sendPostRequestJson_data(((CssdProject) getApplication()).getMD_URL() + "api_inventory/cssd_select_lot.php", Json_data);
                } catch (Exception e) {
                    show_log_error("api_inventory/cssd_select_lot.php");
                    e.printStackTrace();
                }

                Log.d("tog_get_test_point","URL = "+((CssdProject) getApplication()).getMD_URL() + "api_inventory/cssd_select_lot.php");
                Log.d("tog_get_test_point","result = "+result);

                return result;
            }

        }

        get_test_point obj = new get_test_point();

        obj.execute();
    }

    public void change_test_point(String TestProgramID, String docno) {

        class change_test_program extends AsyncTask<String, Void, String> {


            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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

                            loadind_dialog_dismis();
                        }else{
                            show_dialog("Warning","ไม่สามารถโปรแกรมทดสอบได้"+c.getString("result"));
                        }
                    }
                } catch (JSONException e) {
                    show_dialog("Warning","ไม่สามารถโปรแกรมทดสอบได้");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("TestProgramID", TestProgramID);
                data.put("docno", docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(getUrl+ "change_test_program.php", data);

                Log.d("tog_mac_pro_test", "data = "+data);

                return result;
            }
        }

        change_test_program obj = new change_test_program();
        obj.execute();
    }

    public void change_doc_round(String SterileRoundNumber, String docno) {

        class change_doc_round extends AsyncTask<String, Void, String> {


            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loadind_dialog_show();
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
                            reload_mac();
                        }else{

                            AlertDialog.Builder builderalert_r = new AlertDialog.Builder(SterileActivity.this);
                            builderalert_r.setMessage("คุณได้เลือกรอบฆ่าเชื้อซ้ำ \n("+c.getString("DocNo")+" เครื่อง "+c.getString("MachineName2")+",รอบ "+c.getString("SterileRoundNumber")+") !!");
                            builderalert_r.setCancelable(false);

                            builderalert_r.setNegativeButton(
                                    "ปิด",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();

                                            reload_mac();
                                        }
                                    });

                            AlertDialog alert_r = builderalert_r.create();
                            alert_r.show();
                        }
                    }
                } catch (JSONException e) {
                    show_dialog("Warning","ไม่สามารถเปลี่ยนรอบได้");
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("SterileRoundNumber", SterileRoundNumber);
                data.put("docno", docno);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = httpConnect.sendPostRequest(getUrl+ "change_doc_round.php", data);

                Log.d("tog_change_doc_round","data = "+data);
                Log.d("tog_change_doc_round","result = "+result);
                return result;
            }
        }

        change_doc_round obj = new change_doc_round();
        obj.execute();
    }

    public void get_doc_in_mac(String docno){

        class get_doc_in_mac extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadind_dialog_show();
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
//                            list.get(mac_id_non_approve).setProgramID(c.getString("SterileProgramID"));
                            list.get(mac_id_non_approve).setTypeID(c.getString("SterileTypeID"));
                            list.get(mac_id_non_approve).setProgramName(c.getString("SterileName"));
                            list.get(mac_id_non_approve).setRoundNumber(c.getString("SterileRoundNumber"));
                            list.get(mac_id_non_approve).setUserLoader(c.getString("user"));

                            typeID = c.getString("SterileTypeID");
                            get_data =true;
                            Log.d("tog_tag_reload_basket","6");

                            if(SR_M1IsSetTestProg){
                                get_test_program(typeID,c.getString("TestProgramID"));
                            }

                            Log.d("tog_add_basket","get_doc_in_mac set data");
                            Log.d("tog_timer_php","ส่งต่อ reload_basket  (รีเฟรชข้อมูลตะกร้า)");

//                            loadind_dialog_dismis();
                            reload_basket();
                        }
                    }

                    if(!get_data){
                        show_dialog("Warning","ไม่พบเอกสาร");
                        get_data = false;
                    }


                } catch (JSONException e) {

                    show_log_error("get_sterile_doc.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                Log.d("tog_timer_php","เรียก get_sterile_doc.php");
                data.put("docno", docno);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterile_doc.php", data);
                } catch (Exception e) {
                    show_log_error("get_sterile_doc.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                Log.d("tog_add_basket","get_doc_in_mac result = "+result);
                Log.d("tog_timer_php","รับข้อมูล = get_sterile_doc.php");
                return result;
            }

        }

        get_doc_in_mac obj = new get_doc_in_mac();

        obj.execute();
    }

    public void list_mac_adapterNotifyDataSetChanged(){
//        list_mac_adapterNotifyDataSetChanged();
        ArrayList<BasketTag> xlist_basketShow = new ArrayList<>();
        if(!get_mac_select_id().equals("Empty")){
            for(int position = 0;position<xlist_basket.size();position++){
                if (get_mac_select_doc().equals(xlist_basket.get(position).getRefDocNo())) {
                    xlist_basketShow.add(xlist_basket.get(position));
                }
            }
        }else{
            xlist_basketShow = xlist_basket;
        }

        list_basket_adapter = new ListBoxBasketAdapter(SterileActivity.this,xlist_basket,xlist_basketShow,list_basket,list_basket_adapter.select_basket_pos);
        list_basket.setAdapter(list_basket_adapter);
    }

    public void reload_done(int pos){
        list_mac_adapter.onScanSelect(mac_id_non_approve);
        list_mac_adapter.notifyDataSetChanged();

        list_item_basket_adapter = new ListItemBasketAdapter(SterileActivity.this,xlist_item_basket);
        list_item_basket.setAdapter(list_item_basket_adapter);
        list_basket_adapter.onScanSelect(pos);

        if(pos>=0){
            xlist_basket.get(pos).setQty(xlist_item_basket.size());
            text_qty.setText(xlist_item_basket.size()+"");
        }

        list_mac_adapterNotifyDataSetChanged();


        list_item_basket.invalidateViews();
//        set_timerE();
        Log.d("tog_timer_php"," ==================================== end1 ==================================== ");
    }

    public void get_loder(String emp_code,String MachineID){

        class get_doc_in_mac extends AsyncTask<String, Void, String> {

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

                        if (c.getBoolean("check")) {
                            loader = emp_code;
                            dialog_wait_scan(new String[]{wait_scan_program+"",MachineID});
                        }else{
                            show_dialog("Warning","ไม่พบรหัสผู้ใช้งาน");
                        }
                    }


                } catch (JSONException e) {
                    show_log_error("check_qr.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("emp_code", params[0]);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "check_qr.php", data);
                } catch (Exception e) {
                    show_log_error("check_qr.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        get_doc_in_mac obj = new get_doc_in_mac();
        obj.execute(mass_onkey);
    }

    public void set_loder(String emp_code,boolean startMachine,String mac_id,String Doc_no){

        class get_doc_in_mac extends AsyncTask<String, Void, String> {

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

                        if (c.getBoolean("check")) {
                            if(startMachine){
                                startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID());
                            }else{
                                get_machine(mac_id);
                            }
                        }else{
                            show_dialog("Warning","ไม่พบรหัสผู้ใช้งาน");
                        }
                    }


                } catch (JSONException e) {
                    show_log_error("check_qr.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("docno",Doc_no );
                data.put("emp_code", params[0]);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "check_qr.php", data);
                } catch (Exception e) {
                    show_log_error("check_qr.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        get_doc_in_mac obj = new get_doc_in_mac();
        obj.execute(emp_code);
    }

    public void set_processID(int pos,String process_id){

        class set_processID extends AsyncTask<String, Void, String> {

            String processID="";
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

                        if (c.getBoolean("check")) {
                            xlist_basket.get(pos).setTypeProcessID(processID);
                            reload_done(pos);
                        }else{
                            xlist_basket.get(pos).setTypeProcessID("");
                            reload_done(-1);
                            show_dialog("Warning","Process id is invalid");
                        }
                    }


                } catch (JSONException e) {
                    show_log_error("check_sterile_type.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                processID = params[0];
                data.put("processID", params[0]);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash1
                    result = httpConnect.sendPostRequest(getUrl + "check_sterile_type.php", data);
                } catch (Exception e) {
                    show_log_error("check_sterile_type.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        set_processID obj = new set_processID();
        obj.execute(mass_onkey);

    }

    public void set_program(String program_id,String MachineID,int isset){

        class set_program extends AsyncTask<String, Void, String> {

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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            if(isset==1){
                                createSterile(c.getString("process_id"), MachineID, c.getString("type_id"),"0");
                            }else{
                                change_doc_program(c.getString("process_id"), list.get(list_mac_adapter.select_mac_pos).getDocNo());
                            }
//                            show_dialog("Warning","ไม่พบโปรแกรมล้าง -- "+c.getString("process_id")+" --- "+c.getString("type_id"));
                        }else{
                            show_dialog("Warning","ไม่พบโปรแกรมฆ่าเชื้อ");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> d = new HashMap<String,String>();

                String result = null;

                d.put("p_data", "wash_type");
                d.put("mac_id", MachineID);
                d.put("pro_id", program_id);
                d.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());


                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterile_process.php", d);
                } catch (Exception e) {
                    show_log_error("get_sterile_process.php");
                    e.printStackTrace();
                }


                Log.d("tog_sterile_process","data = "+d);
                Log.d("tog_sterile_process","result = "+result);

                return result;
            }


            // =========================================================================================
        }

        set_program obj = new set_program();
        obj.execute();
    }

    public void set_test_program(String program_id,String MachineID,int isset){

        class set_test_program extends AsyncTask<String, Void, String> {

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

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            if(isset==1){
                                if(SR_IsTestProgramRunRound_M1){
                                    createSterile(c.getString("process_id"), MachineID, c.getString("type_id"),"0");
                                }else{
                                    createSterile(c.getString("process_id"), MachineID, c.getString("type_id"),"1");
                                }
                            }else{

                                if(SR_IsProgramTestSplitRound){
                                    alert_builder.setMessage("ไม่อนุญาตให้เปลี่ยน โปรแกรมหรือรอบ บนอุปกรณ์นี้\nโปรดแจ้ง Admin");
                                    alert_builder.setCancelable(false);
                                    alert_builder.setPositiveButton("ตกลง",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {alert.dismiss();}
                                    });

                                    alert = alert_builder.create();

                                    alert.show();
                                }else{
                                    change_doc_program(c.getString("process_id"), list.get(list_mac_adapter.select_mac_pos).getDocNo());
                                }

                            }
                        }else{
                            show_dialog("Warning","ไม่พบโปรแกรมทดสอบ");
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> d = new HashMap<String,String>();

                String result = null;

                d.put("p_data", "wash_type");
                d.put("mac_id", MachineID);
                d.put("pro_id", program_id);
                d.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());


                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterile_processtest.php", d);
                } catch (Exception e) {
                    show_log_error("get_sterile_process.php");
                    e.printStackTrace();
                }

                Log.d("get_sterile_processtest","data = "+d);
                Log.d("get_sterile_processtest","result = "+result);

                return result;
            }


            // =========================================================================================
        }

        set_test_program obj = new set_test_program();
        obj.execute();
    }

    public void dialog_wait_scan(String[] data){
        wait_dialog = new ProgressDialog(SterileActivity.this);
        int for_scan = Integer.parseInt(data[0]);
        switch (for_scan){
            case wait_scan_program :
                wait_dialog.setMessage("กรุณาสแกนรหัสโปรแกรม");
                break;
            case wait_scan_employee :
                wait_dialog.setMessage("กรุณาสแกนรหัสผู้ใช้งาน");
                break;
            case wait_scan_type :
                wait_dialog.setMessage("กรุณาสแกนประเภท");
                break;
        }

        wait_dialog.setCancelable(false);

        wait_dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog
//                show_dialog("w","ยกเลิกการสร้างเอกสาร");
                switch (for_scan){
                    case wait_scan_program :

                        show_dialog("w","ยกเลิกการสร้างเอกสาร");
                        break;
                    case wait_scan_employee :
//                        if(Boolean.parseBoolean(data[1])){
//                            show_dialog("w","ยกเลิกการเริ่มการทำงานเครื่อง");
//                        }else{
//                            get_machine(data[2]);
//                        }
                        show_dialog("w","ยกเลิกการสร้างเอกสาร");
                        break;
                    case wait_scan_type :

                        show_dialog("w","ยกเลิกการสร้างเอกสาร");
                        break;
                }
            }
        });

        wait_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        Log.d("tog_diKeyListener","enter = "+mass_onkey);
                        wait_dialog.dismiss();
                        switch (for_scan){
                            case wait_scan_program :
                                if(test_machine){
                                    set_test_program(mass_onkey.substring(1),data[1],1);
                                }else{
                                    set_program(mass_onkey.substring(1),data[1],1);
                                }
                                break;
                            case wait_scan_employee :
                                get_loder(mass_onkey,data[1]);
//                                set_loder(mass_onkey, Boolean.parseBoolean(data[1]),data[2],data[3]);
                                break;
                            case wait_scan_type :
                                set_processID(Integer.parseInt(data[1]),mass_onkey);
                                break;
                        }

                        mass_onkey = "";

                        return false;
                    }

                    int unicodeChar = keyEvent.getUnicodeChar();

                    if(unicodeChar!=0){
                        mass_onkey=mass_onkey+(char)unicodeChar;
                        Log.d("tog_dispatchKey","unicodeChar = "+unicodeChar);
                    }

                    return false;
                }
                return false;
            }
        });

        wait_dialog.show();
    }

    double t_s = 0;
    double t_e = 0;

    SimpleDateFormat timer_format = new SimpleDateFormat("mm:ss.SSS");
    SimpleDateFormat cal_sc_format = new SimpleDateFormat("ss.SSS");
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();

        Log.d("tog_allKey","keyCode = "+keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(keyCode == KeyEvent.KEYCODE_BACK ){
                onBackPressed();
            }
            else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                timerS.setText("Start : "+timer_format.format(new Date()));
                t_s = Double.parseDouble(cal_sc_format.format(new Date()));
                Log.d("tog_timer_php"," ==================================== start ==================================== ");
                Log.d("tog_add_basket"," ==================================== start ==================================== ");
                Log.d("tog_timer_php","สแกนตะกร้า = "+mass_onkey);
                Log.d("tog_add_basket","mass_onkey= "+mass_onkey);
                Log.d("tog_allKey","keyCode = enter");
//                Log.d("tog_dispatchKey","enter = "+mass_onkey);
//                mass_onkey =mass_onkey.substring(7);
                Log.d("tog_dispatchKey","enter substring = "+mass_onkey);
                mass_onkey= mass_onkey.toLowerCase();
                String key = mass_onkey.substring(0,1);

//                if(mass_onkey.equals("test_scan_add")){
//                    demo_handler.postDelayed(demo_runnable, 300);
//                    mass_onkey = "";
//                    return false;
//                }

                if(!key.equals("i")){
                    if(box_item_detail.getVisibility()==View.VISIBLE){
//                        if(list_usagecode_to_false.size()<=0 && bt_add_colse.isEnabled()){
//                            show_detail(false);
//                        }else{
//                            show_dialog("Warning","กำลังประมวณผล");
//                            mass_onkey = "";
//                            return false;
//                        }

//                        show_dialog("Warning","กำลังประมวณผล");
                        x_vibrator.vibrate(1000);
                        mass_onkey = "";
                        return false;
                    }
                }

                if(test_machine){
                    if(key.equals("b") || key.equals("i") ){
                        mass_onkey = "";
                        show_dialog("Warning","โปรแกรมทดสอบไม่สามารถเพิ่มตะกร้าหรือรายการได้");
                        return false;
                    }
                }

                Log.d("tog_dispatchKey","equals m = "+key.equals("m"));
                if(key.equals("m")){
                    get_machine(mass_onkey.substring(1));
                }else if(mac_is_working){
                    show_dialog("Warning","เครื่องกำลังทำงาน");
                }else{
                    switch (key)
                    {
                        case "b":
                            if(SR_IsUsedBasket_M1){
                                for(int i=0;i<xlist_basket.size();i++){
                                    if(xlist_basket.get(i).getBasketCode().equals(mass_onkey)){

                                        basket_pos_non_approve=i;
                                        Log.d("tog_add_basket","basket_pos_non_approve = "+basket_pos_non_approve);
                                        Log.d("tog_timer_php","ส่งต่อ reload_mac (รีเฟรชข้อมูลเครื่องให้เป็นปัจจุบัน)");
//                                        add_basket_to_machine();
                                        reload_mac();
//                                        reload_basket();
                                        mass_onkey = "";
                                        return false;
                                    }
                                }
                                show_dialog("Warning","ไม่พบตะกร้า");
                            }

                            break;
                        case "s":

                            if(mass_onkey.equals("sc013")){
//                            if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-emtpyPos)){
//                                dialog_wait_scan(new String[]{wait_scan_employee+""});
//                            }else{
//                                show_dialog("Warning","กรุณาเลือกเครื่องฆ่าเชื้อ");
//                            }

                                if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-emtpyPos)){
                                    if(is_have_loader){

                                        startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID());
//                                    startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getTypeID());
                                    }else{
//                                        dialog_wait_scan(new String[]{wait_scan_employee+"","TRUE","",list.get(list_mac_adapter.select_mac_pos).getDocNo()});
                                        show_dialog("Warning","กรุณาสแกนรหัสผู้ใช้งาน");
                                    }
                                }else{
                                    show_dialog("Warning","กรุณาเลือกเครื่องฆ่าเชื้อ");
                                }
                            }else{
                                if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-emtpyPos)){
                                    if(test_machine){
                                        set_test_program(mass_onkey.substring(1),list.get(list_mac_adapter.select_mac_pos).getMachineID(),0);
                                    }else{
                                        set_program(mass_onkey.substring(1),list.get(list_mac_adapter.select_mac_pos).getMachineID(),0);
                                    }
                                }
                            }

                            break;
                        case "e":

                            if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-emtpyPos)){
//                            if(!is_have_loader){
                                set_loder(mass_onkey, false,list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getDocNo());
//                            }
                            }else{
                                show_dialog("Warning","กรุณาเลือกเครื่องฆ่าเชื้อ");
                            }

                            break;
                        default:
                            Log.d("tog_dispatchcase","I = ");

                            if(test_machine){
                                show_dialog("Warning","โปรแกรมทดสอบไม่สามารถเพิ่มตะกร้าหรือรายการได้");
                                return false;
                            }

                            if(list_basket_adapter.select_basket_pos>=0){
                                add_item_to_basket(xlist_basket.get(list_basket_adapter.select_basket_pos).getID(),mass_onkey);
                            }else{

                                if(list_mac_adapter.select_mac_pos>=0){
                                    Log.d("tog_getMachineID",list.get(list_mac_adapter.select_mac_pos).getMachineID());
                                    if(!list.get(list_mac_adapter.select_mac_pos).getMachineID().equals(mac_empty_id)){
                                        add_item_to_basket("-",mass_onkey);
                                    }else{
                                        show_dialog("Warning","กรุณาเลือกตะกร้าหรือเครื่องที่จะเพิ่มรายการ");
                                    }
                                }else{
                                    show_dialog("Warning","กรุณาเลือกเครื่องที่จะเพิ่มรายการ");
                                }
                            }

                            break;
                    }
                }

                mass_onkey = "";
                return false;
            }

            int unicodeChar = event.getUnicodeChar();

            if(unicodeChar!=0){
                mass_onkey=mass_onkey+(char)unicodeChar;
                Log.d("tog_dispatchKey","unicodeChar = "+unicodeChar);
            }

            Log.d("tog_dispatchKey","keyCode = "+keyCode);
            Log.d("tog_dispatchKey","mass_onkey = "+mass_onkey);

            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    public void show_log_error(String mass){
        loadind_dialog_dismis();

        alert_builder.setTitle("Error");
        alert_builder.setMessage(mass);

        alert = alert_builder.create();

        if(((CssdProject) getApplication()).Is_de_bug){
            alert.show();
        }

    }

    public String get_mac_select_id(){
        if(list_mac_adapter.select_mac_pos>=0){
            return list.get(list_mac_adapter.select_mac_pos).getMachineID();
        }

        return mac_empty_id;
    }

    public String get_mac_select_doc(){
        if(list_mac_adapter.select_mac_pos>=0){
            return list.get(list_mac_adapter.select_mac_pos).getDocNo();
        }

        return "";
    }

    public void show_mac(String name,int v){
        ll_mac.setVisibility(v);
        macname.setText(name);

        if(name.equals("Empty")){
            scan_macname.setText("-");
        }else{
            scan_macname.setText(name);
        }

    }

    public void show_basket(String name,int v){
        rl_basket.setVisibility(v);
        basketname.setText(name);

        if(name.equals("Empty")){
            scan_basketname.setText("-");
        }else{
            scan_basketname.setText(name);
        }

        Log.d("tog_show_basket","show_basket = "+name+"----"+v);
    }

    public void delete_basket_from_mac(String p_data,String doc_no){
        class delete_basket_from_mac extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadind_dialog_show();
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

                            xlist_basket.get(basket_pos_non_approve).setMacId("null");
                            list_mac_adapterNotifyDataSetChanged();
                            rl_basket.callOnClick();

                            removeSterileDetail(p_data,doc_no);
                        }else{
                            show_dialog("Warning","เครื่องกำลังทำงานไม่สามารถลบตะกร้าได้");
                        }

                    }

                } catch (JSONException e) {
                    show_log_error("delete_basket_from_mac.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getBasketCode());
                data.put("mac_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getMacId());
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash
                    result = httpConnect.sendPostRequest(getUrl + "delete_basket_from_mac.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_delete_basket","data = " + data);
                Log.d("tog_delete_basket","result = " + result);
                return result;
            }

            // =========================================================================================
        }

        delete_basket_from_mac obj = new delete_basket_from_mac();

        obj.execute();
    }

    public void setProgramAndRound(int select_mac_pos,int mData_size,ModelMachine mData_select_mac_pos) {
        int emtpyPos = 0;
        if(SR_IsUsedBasket_M1){
            emtpyPos = 1;
        }
//        if(select_mac_pos==mData.size()-1){
//            ((SterileActivity)context).title_2.setText(" ");
//        }else{
//            ((SterileActivity)context).title_2.setText("โปรแกรม : "+mData.get(select_mac_pos).getProgramName()+"\tรอบ : "+mData.get(select_mac_pos).getRoundNumber());
//        }

        if(select_mac_pos==(mData_size-emtpyPos)){
            title_2.setText(" ");

//            mac_round.setSelection(0, false);
//            mac_round.setEnabled(false);
//
//            mac_pro_test.setSelection(0, false);
//            mac_pro_test.setEnabled(false);
//
//            mac_test_point.setAdapter(adp_mac_test_point_default);
//            mac_test_point.setEnabled(false);

            is_have_loader = false;
        }else{
            String loader = mData_select_mac_pos.getUserLoader();

            if(loader.equals(" ")){
                loader = "-";
                is_have_loader = false;
            }else{
                is_have_loader = true;
            }

            title_2.setText("โปรแกรม : "+mData_select_mac_pos.getProgramName()+"\tรอบ : "+mData_select_mac_pos.getRoundNumber()+"\nผู้ทำรายการ : "+loader);
            if(isShow_editDocTab_1){
                title_2.setText("โปรแกรม : "+mData_select_mac_pos.getProgramName()+"\nผู้ทำรายการ : "+loader);
                mac_round.setEnabled(SR_IsEditRound);

                mac_round_on_change = mData_select_mac_pos.getIntRoundNumber();
                Log.d("tog_change_doc_round", "setSelection"+mac_round_on_change);
                mac_round.setSelection(mData_select_mac_pos.getIntRoundNumber(), false);

                mac_pro_test.setEnabled(true);
                mac_test_point.setEnabled(true);

                if(mData_select_mac_pos.getIsActive().equals("1")){
                    mac_round.setEnabled(false);
                    mac_pro_test.setEnabled(false);
                    mac_test_point.setEnabled(false);
                }
            }
        }
    }

    public void check_basket() {

        class get_basket extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    int cnt = 0;
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {
                            BasketTag x = xlist_basket.get(map_index_xlist_basket.get(c.getString("xID")));

                            if(!c.getString("RefDocNo").equals(x.getRefDocNo())){
                                x.setMacId(c.getString("InMachineID"));
                                x.setTypeProcessID(c.getString("TypeId"));
                                x.setRefDocNo(c.getString("RefDocNo"));
                                cnt++;
                                loadind_dialog_show();
                            }
                        }
                    }

                    if(cnt>0){
                        list_mac_adapterNotifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    show_log_error("get_sterilebasket.php Error = "+e);
                    e.printStackTrace();
                }
                loadind_dialog_dismis();
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", "null");
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequestBackground(getUrl + "get_sterilebasket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_basket","check_basket result = "+result);

                return result;
            }

            // =========================================================================================
        }

        get_basket obj = new get_basket();

        if(!test_machine){
            obj.execute();
        }

    }
    String check_item_in_basket_string = "";
    public void check_item_in_basket(){

        int pos = item_in_basket_pos;
        String doc = item_in_basket_doc;
        class get_item extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    String uc = "";
                    int cnt = 0;
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("result").equals("A")) {
                            uc = uc+c.getString("UsageCode");
                            if(cnt>=xlist_item_basket.size()){

                                loadind_dialog_show();
                                xlist_item_basket.add(new ItemInBasket(
                                        c.getString("xID"),
                                        c.getString("ItemStockID"),
                                        c.getString("itemname"),
                                        c.getString("UsageCode"),
                                        "",
                                        c.getString("WashDetailID"),
                                        c.getString("SterileDetailID"),
                                        false
                                ));

                            }else{
//                                Log.d("tog_chk_item_in_basket","list_item getUsagecode = "+xlist_item_basket.get(cnt).getUsagecode());
//                                Log.d("tog_chk_item_in_basket","list_item c.getString(UsageCode) = "+c.getString("UsageCode"));

                                if(!xlist_item_basket.get(cnt).getUsagecode().equals(c.getString("UsageCode"))){
                                    loadind_dialog_show();
                                    xlist_item_basket.remove(cnt);
                                }
                            }

                            cnt++;
                        }
                    }

                    while (xlist_item_basket.size()>rs.length()){
                        xlist_item_basket.remove(rs.length());
                    }

                    if(!check_item_in_basket_string.equals(uc)){
                        check_item_in_basket_string = uc;
                        list_item_basket_adapter.notifyDataSetChanged();
                    }
                    loadind_dialog_dismis();
                } catch (JSONException e) {
                    show_log_error("get_item_in_sterile_basket.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                if(pos>=0){
                    data.put("basket_id", xlist_basket.get(pos).getID());
                    data.put("DocNo", doc);
                }else{
                    data.put("basket_id", "-");
                    data.put("DocNo", doc);
                }

                data.put("p_DB", p_DB);
                data.put("typeID", typeID);

                if(mac_id_non_approve>=0){
                    if(!list.get(mac_id_non_approve).getDocNo().equals("Empty")){
                        data.put("mac_id", list.get(mac_id_non_approve).getMachineID());
                    }
                }

                String result = null;

                try {
                    //wash
                    result = httpConnect.sendPostRequestBackground(getUrl + "get_item_in_sterile_basket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_get_item","check_item_in_basket data = " + data);
                Log.d("tog_get_item","check_item_in_basket result = " + result);
                return result;
            }

            // =========================================================================================
        }

        get_item obj = new get_item();

        if(!test_machine){
            obj.execute();
        }
    }

    public void set_timerE(){

        timerE.setText("End : "+timer_format.format(new Date()));
        t_e = Double.parseDouble(cal_sc_format.format(new Date()));
        String t_e_s = Double.toString(t_e-t_s);
        if(t_e_s.length()>5){
            t_e_s = t_e_s.substring(0,5);
        }

        timerT.setText("Total : "+t_e_s);

        if(t_s>0){
            timerS.setVisibility(View.VISIBLE);
            timerE.setVisibility(View.VISIBLE);
            timerT.setVisibility(View.VISIBLE);
        }

    }
}