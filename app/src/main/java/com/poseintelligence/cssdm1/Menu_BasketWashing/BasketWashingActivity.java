package com.poseintelligence.cssdm1.Menu_BasketWashing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListBoxWashBasketAdapter;
import com.poseintelligence.cssdm1.adapter.ListBoxWashMachineAdapter;
import com.poseintelligence.cssdm1.adapter.ListItemWashBasketAdapter;
import com.poseintelligence.cssdm1.core.audio.iAudio;
import com.poseintelligence.cssdm1.core.connect.AsonData;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.BasketTag;
import com.poseintelligence.cssdm1.model.ItemInBasket;
import com.poseintelligence.cssdm1.model.ModelMachine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class BasketWashingActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    String folder_php = "wash_basket/";
    public String getUrl;
    public String p_DB;

    boolean get_data = false;

    String mass_onkey="";

    RecyclerView list_mac;
    RecyclerView list_basket;
    ListView list_item_basket;
    TextView bt_delete;
    TextView bt_select_all;
    LinearLayoutManager lm;
    public TextView title_2;

    LinearLayout ll_mac;
    LinearLayout rl_basket;

    TextView macname;
    TextView basketname;
    TextView text_qty;


    public int mac_id_non_approve = -1;
    public int basket_pos_non_approve = -1;

    boolean is_select_all = false;
    boolean is_add_item = false;

    public boolean is_have_loader=false;

    String typeID="";

    ArrayList<ModelMachine> list = new ArrayList<>();
    ListBoxWashMachineAdapter list_mac_adapter;
    public String mac_empty_id = "Empty";

    ArrayList<BasketTag> xlist_basket = new ArrayList<>();
    ListBoxWashBasketAdapter list_basket_adapter;

    ArrayList<ItemInBasket> xlist_item_basket = new ArrayList<>();
    ListItemWashBasketAdapter list_item_basket_adapter;

    public AlertDialog.Builder alert_builder;
    public AlertDialog alert;

    ProgressDialog wait_dialog;
    ProgressDialog loading_dialog;

    boolean tid = false;

    private iAudio nMidia;

    final int wait_scan_program = 1;
    final int wait_scan_employee = 2;
    final int wait_scan_type = 3;

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

    HashMap<String, Integer> map_machine = new HashMap<String, Integer>();
    Handler check_active_machine_handler  = new Handler();
    Runnable check_active_machine_runnable = new Runnable() {
        @Override
        public void run() {

            check_active_machine();

            check_active_machine_handler.postDelayed(check_active_machine_runnable, 30000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_washing);

        byIntent();

        byWidget();

        nMidia = new iAudio(this);
//        set_program_dialog();
    }

    public void byIntent(){
        getUrl=((CssdProject) getApplication()).getxUrl()+folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BasketWashingActivity.this, MainMenu.class);
        startActivity(intent);
        check_active_machine_handler.removeCallbacks(check_active_machine_runnable);
        finish();
    }

    public void byWidget(){
        title_2 = (TextView) findViewById(R.id.title_2);
        list_mac = (RecyclerView) findViewById(R.id.list_mac);
        list_basket = (RecyclerView) findViewById(R.id.list_basket);
        list_item_basket = (ListView) findViewById(R.id.list_item_basket);

        bt_delete = (TextView) findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = "";
                String data = "";
                for(int i=xlist_item_basket.size()-1;i>=0;i--){
                    if(xlist_item_basket.get(i).isChk()){
                        id = id+xlist_item_basket.get(i).getRow_id()+",";
                        data = data+xlist_item_basket.get(i).getWashDetailID() + "@" + xlist_item_basket.get(i).getSSDetailID() + "@" + xlist_item_basket.get(i).getItemStockID() + "@";
                        Log.d("tog_delete","data = "+data);
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

        alert_builder = new AlertDialog.Builder(BasketWashingActivity.this);

        loading_dialog = new ProgressDialog(BasketWashingActivity.this);
        loading_dialog.setCanceledOnTouchOutside(false);
        loading_dialog.setMessage("Loading...");


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
                reload_basket();
            }
        });

        rl_basket.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if(xlist_basket.get(basket_pos_non_approve).getMacId().equals("null")){
                    return false;
                }

                new AlertDialog.Builder(BasketWashingActivity.this)
                        .setMessage("ต้องการลบตะกร้าออกจากเครื่องหรือไม่")

                        .setPositiveButton("ยืนยัน", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = "";
                                String data = "";

                                for(int i=xlist_item_basket.size()-1;i>=0;i--){
                                    id = id+xlist_item_basket.get(i).getRow_id()+",";
                                    data = data+xlist_item_basket.get(i).getWashDetailID() + "@" + xlist_item_basket.get(i).getSSDetailID() + "@" + xlist_item_basket.get(i).getItemStockID() + "@";
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

        macname = (TextView) findViewById(R.id.macname);
        basketname = (TextView) findViewById(R.id.basketname);
        text_qty = (TextView) findViewById(R.id.text_qty);

        get_machine("null");
        get_basket("null");
    }

    public void loading_dialog_show(){
        if(!loading_dialog.isShowing()){
            loading_dialog.show();
        }
    }

    public void loading_dialog_dismiss(){
        if(loading_dialog.isShowing()){
            loading_dialog.dismiss();
        }
    }

    public void get_machine(String mac_id) {

        class get_machine extends AsyncTask<String, Void, String> {
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    if(mac_id.equals("null")){
                        list.clear();

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {
                                list.add(new ModelMachine(c.getString("xID"),c.getString("xMachineName2"),c.getString("IsActive"),c.getString("IsBrokenMachine"),c.getString("DocNo"),c.getString("WashProcessID"),c.getString("FinishTime")));
                                map_machine.put(c.getString("xID"),i);


                                if(c.getInt("IsActive")==1){
                                    if(c.getInt("FinishTime")<=0){
                                        updateFinishMachine(c.getInt("xID"),c.getString("DocNo"));
                                    }
                                }
                            }
                        }

                        list.add(new ModelMachine(mac_empty_id,mac_empty_id,"0","0",mac_empty_id,mac_empty_id,""));

                        list_mac_adapter = new ListBoxWashMachineAdapter(BasketWashingActivity.this, list,list_mac);
                        list_mac.setAdapter(list_mac_adapter);

                        show_mac("",View.GONE);
                        mac_id_non_approve = -1;
                        show_basket("",View.GONE);
                        basket_pos_non_approve = -1;
                        Log.d("tog_get_machine","mac_id_non_approve = "+mac_id_non_approve);

                        loading_dialog_dismiss();

                        //toy
                        check_active_machine_handler.postDelayed(check_active_machine_runnable, 5000);
                    }else{
                        if(mac_id.equals(mac_empty_id)){
                            list.get(list.size()-1).setIsActive("0");
                            mac_id_non_approve = list.size()-1;
                            reload_basket();
                        }else{
                            for (int i = 0; i < rs.length(); i++) {
                                JSONObject c = rs.getJSONObject(i);
                                Log.d("tog_scan_basket","xID = "+c.getString("xID")+"---"+mac_id);

                                for (int j = 0; j < list.size(); j++) {
                                    if(list.get(j).getMachineID().equals(c.getString("xID"))){
                                        list.get(j).setIsActive(c.getString("IsActive"));
                                        list.get(j).setIsBrokenMachine(c.getString("IsBrokenMachine"));
                                        list.get(j).setDocNo(c.getString("DocNo"));

                                        if(c.getString("xID").equals(mac_id)){
                                            if(c.getString("IsActive").equals("1")){
                                                show_dialog("Warning","เครื่องกำลังทำงาน");

                                            }
                                            else if(c.getString("IsBrokenMachine").equals("1")){
                                                show_dialog("Warning","เครื่องไม่พร้อมใช้งาน");
                                            }
                                            else{
                                                if(c.getString("DocNo").equals("null")||c.getString("DocNo")==null){
                                                    dialog_wait_scan(new String[]{wait_scan_program+"",list.get(j).getTypeID(),list.get(j).getMachineID()});
                                                }else{
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
                    show_log_error("get_washmachine.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("mac_id", mac_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash1
                    result = httpConnect.sendPostRequest(getUrl + "get_washmachine.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                loading_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    if(basket_id.equals("null")){
                        xlist_basket.clear();
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {
                                xlist_basket.add(new BasketTag(c.getString("xID"),c.getString("BasketName"),c.getString("BasketCode"),c.getString("InMachineID"),0));
                            }
                        }

                        basket_pos_non_approve = -1;
                        list_basket_adapter = new ListBoxWashBasketAdapter(BasketWashingActivity.this, xlist_basket,list_basket);
                        list_basket.setAdapter(list_basket_adapter);

//                        show_basket("",View.GONE);

                        loading_dialog_dismiss();
                    }else{
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                if(c.getString("BasketCode").toLowerCase().equals(basket_id.toLowerCase())){

                                    for (int j = 0; j < xlist_basket.size(); j++) {
                                        if(xlist_basket.get(j).getBasketCode().equals(c.getString("BasketCode"))){
                                            xlist_basket.get(j).setMacId(c.getString("InMachineID"));

                                            int mac_pos = mac_id_non_approve;

                                            Log.d("tog_getbasket","j = "+j);
                                            Log.d("tog_getbasket_mac_pos","mac_pos = "+mac_pos);

                                            if(mac_pos>=0){
                                                String mac_id = list.get(mac_pos).getMachineID();
                                                if(c.getString("InMachineID").equals("null")){//ตะกร้าไม่มีเครื่อง
                                                    //add basket in mac
                                                    Log.d("tog_getbasket","mac_empty_id = "+mac_id.equals(mac_empty_id));
                                                    if(mac_id.equals(mac_empty_id)){
                                                        list_mac_adapter.onScanSelect(mac_id_non_approve);
                                                        list_basket_adapter.onScanSelect(j);
                                                    }else{
                                                        is_add_item = true;
                                                    }

                                                    get_item_in_basket(j,list.get(mac_pos).getDocNo());

                                                }else if (c.getString("InMachineID").equals(mac_id)){
                                                    get_item_in_basket(j,list.get(mac_pos).getDocNo());
                                                }else{
                                                    if(mac_pos==list.size()-1){
                                                        list_mac_adapter.onScanSelect(-1);
                                                    }

                                                    show_dialog("Warning","ตะกร้าไม่พร้อมใช้งาน");
                                                }
                                            }else{
                                                list_basket_adapter.onScanSelect(j);
                                                get_item_in_basket(j,"");
                                            }

                                            list_mac_adapter.notifyDataSetChanged();
                                            list_basket_adapter.notifyDataSetChanged();
                                        }
                                    }

                                }
                            }
                        }
                    }

                } catch (JSONException e) {
                    show_log_error("get_washbasket.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", basket_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash1
                    result = httpConnect.sendPostRequest(getUrl + "get_washbasket.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                    result = httpConnect.sendPostRequest(getUrl + "get_washmachine.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        check_active_machine obj = new check_active_machine();

        obj.execute();
    }


    public void updateFinishMachine(final int p_machine_no, final String p_doc_no) {

        class UpdateFinishMachine extends AsyncTask<String, Void, String> {

            //------------------------------------------------
            // Background Worker Process Variable
//            private boolean Success = false;
//            private ArrayList<String> data = null;
//            private int size = 0;
            //------------------------------------------------

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
//                AsonData ason = new AsonData(result);
//
//                Success = ason.isSuccess();
//                size = ason.getSize();
//                data = ason.getASONData();
//
//                //if(Success && data != null) {
//                int MachineNo = 0;
//
//                try {
//                    MachineNo = Integer.valueOf(data.get(0)).intValue();
//                }catch(Exception e){
//                    e.printStackTrace();
//                    return ;
//                }
//
//                // New Button Sterile Machine (Set Machine Data)
//                newMachine(
//                        getMachineId(MachineNo),
//                        getMachineName(MachineNo),
//                        "0",
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        MachineNo);
//
//                // Clear Machine
//                clearMachine(MachineNo);
//
//                //}
//            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_docno", p_doc_no);
                data.put("p_machine_no", Integer.toString(p_machine_no));
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                //String result = httpConnect.sendPostRequest(Url.URL + "cssd_update_wash_finish_time_.php", data);
                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_update_wash_finish_time_.php", data);
                return result;
            }

            // =========================================================================================
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

    public void delete_basket_from_mac(String p_data,String doc_no){
        class delete_basket_from_mac extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_dialog_show();
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

    public void item_to_delete(String id,String data){
        class delete_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_dialog_show();
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
                            }else if(list.get(list_mac_adapter.select_mac_pos).getDocNo().equals("Empty")){
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
                data.put("basket_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getBasketCode());
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl()+SterileActivity.folder_php + "delete_item_in_basket.php", data);
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
                            if(list_basket_adapter.select_basket_pos==-1){
                                reload_mac();
                            }else{
                                get_item_in_basket(list_basket_adapter.select_basket_pos,"");
                            }
                        }
                    }

                } catch (JSONException e) {
                    show_log_error("cssd_remove_wash_detail.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_data", p_data);
                data.put("p_docno", doc_no);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                //wash1
                String result = httpConnect.sendPostRequest(getUrl + "cssd_remove_wash_detail.php", data);

                return result;
            }

            // // ---------------------------------------------------------------
        }

        RemoveSterileDetail obj = new RemoveSterileDetail();
        obj.execute();
    }

    public void add_item_to_basket(String basket_id,String usage_code){
        class add_item extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_dialog_show();
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
                                            c.getString("ss_id")+",",
                                            basket_id
                                    );
                                }else{
                                    reload_basket();
                                    nMidia.getAudio("okay");
                                }
                            }else{

                                reload_basket();
                                nMidia.getAudio("okay");
                            }
                        }else if (c.getString("result").equals("D")){
                            if(c.getString("basket_id").equals(basket_id)){
                                show_dialog("Warning","รายการซ้ำ","repeat_scan");
                            }else{
                                show_dialog("Warning","รายการนี้อยู่ในตะกร้าอื่น","no");
                            }
                        }else if(c.getString("result").equals("T")){
                            show_dialog("Warning","ไม่สามารถเพิ่มได้","no");
                        }else if(c.getString("result").equals("M")){
                            show_dialog("Warning","บางรายการไม่สามารถเข้าเครื่องได้","no");
                        }else{
                            show_dialog("Warning","ไม่พบรายการ","no_item_found");
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
                    if(tid){
                        data.put("program_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getTypeProcessID());
                    }
                }

                String result = null;

                try {
                    //wash1
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


    public void startMachine(String p_doc_no,String p_MachineID,String p_ProcessID) {

        if(p_doc_no.equals("null")){
            return;
        }

        class StartMachine extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_dialog_show();
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
                            get_basket("null");
                        }

                    }

                }catch (Exception e){
                    show_log_error("cssd_update_wash_start_time.php Error = "+e);
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_docno", p_doc_no);
                data.put("p_WashMachineID", p_MachineID);
                data.put("p_WashProcessID", p_ProcessID);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
//                if(IsUsedProcessTimeByWashType) {
                    data.put("p_process_time_by_wash_type", "1");
//                }
                //wash
                String result = httpConnect.sendPostRequest(getUrl + "cssd_update_wash_start_time.php", data);

                return result;
            }
        }

        StartMachine obj = new StartMachine();
        obj.execute();
    }

    public void addSterileDetailById(String p_docno, String p_data,String basket_id) {

        class AddSterileDetail extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading_dialog_show();
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

                            reload_basket();
                            nMidia.getAudio("okay");
                        }else{
                            show_dialog("Warning","ไม่สามารถเพิ่มรายการได้","no");
                        }

                    }

                } catch (JSONException e) {
                    show_log_error("cssd_add_wash_detail.php Error = "+e);
                    e.printStackTrace();
                }finally{
                    is_add_item = false;
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_list_id", p_data);
                data.put("p_docno", p_docno);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("is_new", is_add_item+"");
                data.put("BasketID", basket_id);

                // wash1
                String result = httpConnect.sendPostRequest(getUrl + "cssd_add_wash_detail.php", data);

                return result;
            }

            // // ---------------------------------------------------------------
        }

        AddSterileDetail obj = new AddSterileDetail();
        obj.execute();
    }

    public void get_item_in_basket(int pos,String doc){

        Log.d("tog_get_item","pos = " + pos);
        if(pos>=0){
            String basket_id = xlist_basket.get(pos).getID();
            class get_item extends AsyncTask<String, Void, String> {
                String w_id = "";
                boolean get_data = true;

                // variable
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    loading_dialog_show();
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        ArrayList<ItemInBasket> list_item = new ArrayList<>();

                        xlist_basket.get(pos).setTypeProcessID("");
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                list_item.add(new ItemInBasket(
                                        c.getString("xID"),
                                        c.getString("ItemStockID"),
                                        c.getString("itemname"),
                                        c.getString("UsageCode"),
                                        c.getString("SSDetailID"),
                                        c.getString("WashDetailID"),
                                        "",
                                        false
                                ));

                                w_id = w_id+c.getString("SSDetailID")+",";

//                                Log.d("tog_get_item","typeID " + c.getString("WashProcessID")+"---"+typeID);

                                if(is_add_item&&(!c.getString("WashProcessID").equals(typeID)||c.getString("WashMachineID").equals("null"))){
                                    if(!c.getString("WashProcessID").equals(typeID)){
                                        Log.d("tog_get_item","typeID " + c.getString("WashProcessID")+"---"+typeID);
                                        show_dialog("Warning","ไม่สามารถเพิ่มได้","no");
                                    }else if(c.getString("WashMachineID").equals("null")){
                                        show_dialog("Warning","บางรายการไม่สามารถเข้าเครื่องได้","no");
                                    }
                                    typeID = "";
                                    is_add_item = false;
                                    get_data =false;
                                }

                                xlist_basket.get(pos).setTypeProcessID(c.getString("WashProcessID"));

                            }else{
                                is_add_item = false;
                            }
                        }

                        Log.d("tog_get_item","is_add_item && rs.length()!=0 = " + (is_add_item && rs.length()!=0));
                        if(get_data){

                            xlist_item_basket.clear();
                            xlist_item_basket = list_item;

                            if(is_add_item){
                                addSterileDetailById( doc, w_id,basket_id);
                            }else{

                                if(tid){
                                    if(xlist_basket.get(pos).getTypeProcessID().equals("")){
                                        if(typeID.equals("")){
                                            dialog_wait_scan(new String[]{wait_scan_type+"",pos+""});
                                        }else{
                                            xlist_basket.get(pos).setTypeProcessID(typeID);
                                            reload_done(pos);
                                        }
                                    }else{
                                        reload_done(pos);
                                    }
                                }else{
                                    reload_done(pos);
                                }

                                loading_dialog_dismiss();
                            }
                        }


                    } catch (JSONException e) {
                        show_log_error("get_item_in_wash_basket.php Error = "+e);
                        e.printStackTrace();
                    }

                }

                @Override
                protected String doInBackground(String... params) {
                    HashMap<String, String> data = new HashMap<String, String>();

                    data.put("basket_id", basket_id);
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
                        result = httpConnect.sendPostRequest(getUrl + "get_item_in_wash_basket.php", data);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.d("tog_get_item","data = " + data);
                    Log.d("tog_get_item","result = " + result);
                    return result;
                }

                // =========================================================================================
            }

            get_item obj = new get_item();

            obj.execute();
        }

    }

    public void reload_done(int pos){
        list_item_basket_adapter = new ListItemWashBasketAdapter(BasketWashingActivity.this,xlist_item_basket);
        list_item_basket.setAdapter(list_item_basket_adapter);
        list_basket_adapter.onScanSelect(pos);
        if(pos>=0){
            xlist_basket.get(pos).setQty(xlist_item_basket.size());
            text_qty.setText(xlist_item_basket.size()+"");
        }
        list_basket_adapter.notifyDataSetChanged();

        list_mac_adapter.onScanSelect(mac_id_non_approve);
        list_mac_adapter.notifyDataSetChanged();
        list_item_basket.invalidateViews();
    }

    public void show_dialog(String title,String mass,String sound){
        show_dialog(title,mass);
        nMidia.getAudio(sound);
    }

    public void show_dialog(String title,String mass){
        mac_id_non_approve = list_mac_adapter.select_mac_pos;
        basket_pos_non_approve = list_basket_adapter.select_basket_pos;

        list_basket_adapter.notifyDataSetChanged();
        list_mac_adapter.notifyDataSetChanged();
        list_item_basket.invalidateViews();

        loading_dialog_dismiss();

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

        handler.postDelayed(runnable, 1500);
    }

    public void reload_mac(){
        if(list_mac_adapter.select_mac_pos<list.size()&&list_mac_adapter.select_mac_pos>=0){
            String mac_ID = list.get(list_mac_adapter.select_mac_pos).getMachineID();
            get_machine(mac_ID);
        }else{
            get_machine(mac_empty_id);
        }
    }

    public void reload_basket(){
        int basket_pos = basket_pos_non_approve;

        Log.d("tog_basket","basket pos = "+basket_pos);
        if(basket_pos>=0){
            Log.d("tog_basket","getBasketCode = "+xlist_basket.get(basket_pos).getBasketCode());
            get_basket(xlist_basket.get(basket_pos).getBasketCode());
        }else{

            get_basket("null");
//            loading_dialog_dismiss();

            xlist_item_basket.clear();
            list_item_basket_adapter = new ListItemWashBasketAdapter(BasketWashingActivity.this,xlist_item_basket);
            list_item_basket.setAdapter(list_item_basket_adapter);

            list_mac_adapter.onScanSelect(mac_id_non_approve);
//            list_basket_adapter.notifyDataSetChanged();
            list_mac_adapter.notifyDataSetChanged();
//            list_item_basket.invalidateViews();
        }
    }


    public void create_doc(String p_WashProgramID,String p_WashTypeID,String p_WashMachineID) {
        final  String p_TestProgramID = "0" ;

        class CreateWash_ extends AsyncTask<String, Void, String> {
            //------------------------------------------------
            // Background Worker Process Variable
            private boolean Success = false;
            private ArrayList<String> data = null;
            private int size = 0;
            //------------------------------------------------
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading_dialog_show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                AsonData ason = new AsonData(result);
                Success = ason.isSuccess();
                size = ason.getSize();
                data = ason.getASONData();
                if(Success && data != null) {

                    Log.d("BANKTEST","data = "+data.get(1));

                    dialog_wait_scan(new String[]{wait_scan_employee+"","FALSE",p_WashMachineID,data.get(1)});
//                    get_machine(p_WashMachineID);
                }else{
                    show_dialog("Warning","ไม่สามารถสร้างเอกสารได้");
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_UserCode", ((CssdProject) getApplication()).getCustomerId()+"" );
                data.put("p_Qty", "1");
                data.put("p_WashProgramID", p_WashProgramID);
                data.put("p_WashMachineID", p_WashMachineID);
                data.put("p_TestProgramID", p_TestProgramID);
                data.put("p_WashTypeID", p_WashTypeID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_create_wash.php", data);

                Log.d("BANKTEST",data+"");
                Log.d("BANKTEST",result+"");

                return result;
            }
        }
        CreateWash_ obj = new CreateWash_();
        obj.execute();
    }

    public void get_doc_in_mac(String docno){

        class get_doc_in_mac extends AsyncTask<String, Void, String> {

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading_dialog_show();
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
//                            list.get(mac_id_non_approve).setProgramID(c.getString("washProgramID"));
                            list.get(mac_id_non_approve).setProgramName(c.getString("WashingProcess"));
                            list.get(mac_id_non_approve).setRoundNumber(c.getString("washRoundNumber"));
                            list.get(mac_id_non_approve).setUserLoader(c.getString("user"));

                            typeID = list.get(mac_id_non_approve).getTypeID();
                            get_data =true;
                            reload_basket();
                        }
                    }

                    if(!get_data){
                        show_dialog("Warning","ไม่พบเอกสารในเครื่อง");
                        get_data = false;
                    }


                } catch (JSONException e) {

                    show_log_error("get_wash_doc.php Error = "+e);
                    e.printStackTrace();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("docno", docno);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    //wash1
                    result = httpConnect.sendPostRequest(getUrl + "get_wash_doc.php", data);
                } catch (Exception e) {
                    show_log_error("get_wash_doc.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        get_doc_in_mac obj = new get_doc_in_mac();

        obj.execute();
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
                    show_log_error("check_wash_process.php Error = "+e);
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
                    result = httpConnect.sendPostRequest(getUrl + "check_wash_process.php", data);
                } catch (Exception e) {
                    show_log_error("check_wash_process.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        set_processID obj = new set_processID();
        obj.execute(process_id);

    }

    public void set_loder(String emp_code,boolean startMachine,String mac_id,String Doc_no){

        class set_loder extends AsyncTask<String, Void, String> {

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
                                startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getTypeID());
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
                    //wash1
                    result = httpConnect.sendPostRequest(getUrl + "check_qr.php", data);
                } catch (Exception e) {
                    show_log_error("check_qr.php");
                    e.printStackTrace();
                }

                Log.d("tog_sterile_process","result = "+result);

                return result;
            }

        }

        set_loder obj = new set_loder();
        obj.execute(emp_code);
    }

    public void set_program(String program_id,String ProcessId,String p_WashMachineID){

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
                            create_doc(ProcessId,c.getString("value"),p_WashMachineID);
                        }else{
                            show_dialog("Warning","ไม่พบโปรแกรมล้าง");
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
                d.put("p_filter", ProcessId);
                d.put("p_qr", program_id);
                d.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_check_data_by_qr.php", d);
                }catch(Exception e){
                    e.printStackTrace();
                }

                return result;
            }


            // =========================================================================================
        }

        set_program obj = new set_program();
        obj.execute();
    }

    public void dialog_wait_scan(String[] data){

        wait_dialog = new ProgressDialog(BasketWashingActivity.this);
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

                switch (for_scan){
                    case wait_scan_program :

                        show_dialog("w","ยกเลิกการสร้างเอกสาร");
                        break;
                    case wait_scan_employee :
                        if(Boolean.parseBoolean(data[1])){
                            show_dialog("w","ยกเลิกการเริ่มการทำงานเครื่อง");
                        }else{
                            get_machine(data[2]);
                        }
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
                                set_program(mass_onkey.substring(1),data[1],data[2]);
                                break;
                            case wait_scan_employee :
                                set_loder(mass_onkey, Boolean.parseBoolean(data[1]),data[2],data[3]);
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
                Log.d("tog_dispatchKey","enter = "+mass_onkey);
                mass_onkey= mass_onkey.toLowerCase();
                String key = mass_onkey.substring(0,1);
                switch (key)
                {
                    case "m":
                        get_machine(mass_onkey.substring(1));
                        break;
                    case "b":
                        for(int i=0;i<xlist_basket.size();i++){
                            if(xlist_basket.get(i).getBasketCode().equals(mass_onkey)){
                                basket_pos_non_approve=i;
                                reload_mac();
                                mass_onkey = "";
                                return false;
                            }
                        }
                        show_dialog("Warning","ไม่พบตะกร้า");
                        break;
                    case "i":

                        Log.d("tog_dispatchcase","I = ");
                        if(list_basket_adapter.select_basket_pos>=0){
                            add_item_to_basket(xlist_basket.get(list_basket_adapter.select_basket_pos).getID(),mass_onkey);
                        }else{
                            show_dialog("Warning","กรุณาเลือกตะกร้าที่จะเพิ่มรายการ");
                        }

                        break;
                    case "s":

                        if(mass_onkey.equals("sc012")){
                            if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-1)){
                                if(is_have_loader){
                                    startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getTypeID());
                                }else{
                                    dialog_wait_scan(new String[]{wait_scan_employee+"","TRUE","",list.get(list_mac_adapter.select_mac_pos).getDocNo()});
                                }
                            }else{
                                show_dialog("Warning","กรุณาเลือกเครื่องล้าง");
                            }
                        }

                        break;
                    case "e":

                        if(list_mac_adapter.select_mac_pos>=0&&(list_mac_adapter.select_mac_pos!=list.size()-1)){
                            set_loder(mass_onkey, false,list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getDocNo());
                        }else{
                            show_dialog("Warning","กรุณาเลือกเครื่องล้าง");
                        }

                        break;
                    default:
                }
                mass_onkey = "";
                return false;
            }
            int unicodeChar = event.getUnicodeChar();

            if(unicodeChar!=0){
                mass_onkey=mass_onkey+(char)unicodeChar;
                Log.d("tog_dispatchKey","unicodeChar = "+unicodeChar);
            }

            Log.d("tog_dispatchKey","mass_onkey = "+mass_onkey);

            return false;
        }

        return super.dispatchKeyEvent(event);
    }

    public void show_log_error(String mass){
        loading_dialog_dismiss();

        alert_builder.setTitle("Error");
        alert_builder.setMessage(mass);

        alert = alert_builder.create();

        if(false){
            alert.show();
        }

    }

    public String get_mac_select_id(){
        if(list_mac_adapter.select_mac_pos>=0){
            return list.get(list_mac_adapter.select_mac_pos).getMachineID();
        }

        return mac_empty_id;
    }

    public void show_mac(String name,int v){
        ll_mac.setVisibility(v);
        macname.setText(name);

    }

    public void show_basket(String name,int v){
        rl_basket.setVisibility(v);
        basketname.setText(name);
    }

}
