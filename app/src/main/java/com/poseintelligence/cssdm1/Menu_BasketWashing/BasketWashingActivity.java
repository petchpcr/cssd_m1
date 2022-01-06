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
import android.widget.ListView;
import android.widget.TextView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListBoxWashBasketAdapter;
import com.poseintelligence.cssdm1.adapter.ListBoxWashMachineAdapter;
import com.poseintelligence.cssdm1.adapter.ListItemWashBasketAdapter;
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

public class BasketWashingActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    String folder_php = "wash_basket/";
    public String getUrl;
    public String p_DB;

    boolean get_data = false;
    boolean show_wait = false;

    String mass_onkey="";

    RecyclerView list_mac;
    RecyclerView list_basket;
    ListView list_item_basket;
    TextView bt_delete;
    TextView bt_select_all;
    LinearLayoutManager lm;
    public TextView title_2;

    boolean on_scan_program_mac = false;
    public int mac_id_non_approve = -1;
    public int basket_pos_non_approve = -1;

    boolean is_select_all = false;
    boolean is_add_item = false;

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

    ProgressDialog program_dialog;
    ProgressDialog loadind_dialog;

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

    Handler handler_re_scan_text  = new Handler();
    Runnable runnable_re_scan_text = new Runnable() {
        @Override
        public void run() {
            mass_onkey = "";
            handler_re_scan_text.postDelayed(runnable_re_scan_text, 200);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_washing);

        byIntent();

        byWidget();

//        set_program_dialog();

//        handler_re_scan_text.postDelayed(runnable_re_scan_text, 200);
    }

    public void byIntent(){
        getUrl=((CssdProject) getApplication()).getxUrl()+folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BasketWashingActivity.this, MainMenu.class);
        startActivity(intent);
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
                    bt_select_all.setText("Unselect all");
                }else{
                    is_select_all = false;
                    bt_select_all.setText("Select all");
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

        loadind_dialog = new ProgressDialog(BasketWashingActivity.this);
        loadind_dialog.setCanceledOnTouchOutside(false);
        loadind_dialog.setMessage("Loading...");

        get_machine("null");
        get_basket("null");
    }

    public void loadind_dialog_show(){
        if(!loadind_dialog.isShowing()){
            loadind_dialog.show();
        }
    }

    public void loadind_dialog_dismis(){
        if(loadind_dialog.isShowing()){
            loadind_dialog.dismiss();
        }
    }

    public void get_machine(String mac_id) {

        class get_machine extends AsyncTask<String, Void, String> {
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

                    if(mac_id.equals("null")){
                        list.clear();

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {
                                list.add(new ModelMachine(c.getString("xID"),c.getString("xMachineName2"),c.getString("IsActive"),c.getString("IsBrokenMachine"),c.getString("DocNo"),c.getString("WashProcessID")));
                            }
                        }

                        list.add(new ModelMachine(mac_empty_id,mac_empty_id,"0","0",mac_empty_id,mac_empty_id));

                        list_mac_adapter = new ListBoxWashMachineAdapter(BasketWashingActivity.this, list,list_mac);
                        list_mac.setAdapter(list_mac_adapter);

                        loadind_dialog_dismis();
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
                                                show_dialog("Warning","Machine is running");

                                            }
                                            else if(c.getString("IsBrokenMachine").equals("1")){
                                                show_dialog("Warning","Machine is broken");
                                            }
                                            else{
                                                if(c.getString("DocNo").equals("null")){

                                                    show_dialog("Warning","No Document in machine");
//                                                    set_mac_program_id(c.getString("xMachineName2"),c.getString("xID"));
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

        xlist_item_basket.clear();
        list_item_basket_adapter = new ListItemWashBasketAdapter(BasketWashingActivity.this,xlist_item_basket);
        list_item_basket.setAdapter(list_item_basket_adapter);

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

                        list_basket_adapter = new ListBoxWashBasketAdapter(BasketWashingActivity.this, xlist_basket,list_basket);
                        list_basket.setAdapter(list_basket_adapter);

                        loadind_dialog_dismis();
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

                                            if(mac_pos>0){
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
                                                    list_basket_adapter.onScanSelect(-1);
                                                    if(mac_pos==list.size()-1){
                                                        list_mac_adapter.onScanSelect(-1);
                                                        show_dialog("Warning","Basket is in some machine");
                                                    }else{
                                                        show_dialog("Warning","Basket is in another machine");
                                                    }
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
            bt_select_all.setText("Unselect all");
        }else{
            is_select_all = false;
            bt_select_all.setText("Select all");
        }

        if(x==0){
            show_bt_delete(false);
        }else{
            show_bt_delete(true);
        }

    }

    public void item_to_delete(String id,String data){
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
                            get_item_in_basket(list_basket_adapter.select_basket_pos,"");
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
                                            c.getString("ss_id")+",",
                                            basket_id
                                    );
                                }else{
                                    reload_basket();
                                }
                            }
                            else{
                                reload_basket();
                            }
                        }else if (c.getString("result").equals("D")){
                            if(c.getString("basket_id").equals(basket_id)){
                                show_dialog("Warning","Item has in this basket");
                            }else{
                                show_dialog("Warning","Item has in some basket");
                            }
                        }else{
                            show_dialog("Warning","Not found item or Program mismatch");
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
                }else{
                    data.put("program_id", xlist_basket.get(list_basket_adapter.select_basket_pos).getTypeProcessID());
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

                loadind_dialog_show();
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

                    loadind_dialog_show();
                }

                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);

                    try {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);
                        xlist_item_basket.clear();
                        xlist_basket.get(pos).setTypeProcessID("");
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                xlist_item_basket.add(new ItemInBasket(
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

                                if(is_add_item&&!c.getString("WashProcessID").equals(typeID)){
                                    typeID = "";
                                    show_dialog("Warning","Some item in basket program mismatch");
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
                            if(is_add_item){
                                addSterileDetailById( doc, w_id,basket_id);
                            }else{

                                if(xlist_basket.get(pos).getTypeProcessID().equals("")){
                                    set_processID(pos);
                                }else{
                                    reload_done(pos);
                                }
                                loadind_dialog_dismis();
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
        }
        list_basket_adapter.notifyDataSetChanged();

        list_mac_adapter.onScanSelect(mac_id_non_approve);
        list_mac_adapter.notifyDataSetChanged();
        list_item_basket.invalidateViews();
    }

    public void set_processID(int pos){

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

        ProgressDialog dialog = new ProgressDialog(BasketWashingActivity.this);
        dialog.setMessage("Please scan Process ID");
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog
                reload_done(-1);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        Log.d("tog_diKeyListener","enter = "+mass_onkey);

                        set_processID obj = new set_processID();
                        obj.execute(mass_onkey);

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

        if(typeID.equals("")){
            dialog.show();
        }else{
            xlist_basket.get(pos).setTypeProcessID(typeID);
            reload_done(pos);
        }

    }

    public void show_dialog(String title,String mass){
        mac_id_non_approve = list_mac_adapter.select_mac_pos;
        basket_pos_non_approve = list_basket_adapter.select_basket_pos;

        list_basket_adapter.notifyDataSetChanged();
        list_mac_adapter.notifyDataSetChanged();
        list_item_basket.invalidateViews();

        loadind_dialog_dismis();

        alert_builder.setTitle(title);
        alert_builder.setMessage(mass);

        alert = alert_builder.create();

        alert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Log.d("tog_alert_onDismiss","alert_onDismiss = " + on_scan_program_mac);
                if(show_wait){
                    on_scan_program_mac = true;
                    show_wait = false;
                    program_dialog.show();
                }
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
            loadind_dialog_dismis();

            list_mac_adapter.onScanSelect(mac_id_non_approve);
            list_basket_adapter.notifyDataSetChanged();
            list_mac_adapter.notifyDataSetChanged();
            list_item_basket.invalidateViews();
        }
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
                            list.get(mac_id_non_approve).setProgramID(c.getString("washProgramID"));
                            list.get(mac_id_non_approve).setProgramName(c.getString("WashingProcess"));
                            list.get(mac_id_non_approve).setRoundNumber(c.getString("washRoundNumber"));

                            typeID = list.get(mac_id_non_approve).getTypeID();
                            get_data =true;
                            reload_basket();
                        }
                    }

                    if(!get_data){
                        show_dialog("Warning","Not found program id");
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


    //    public void show_dialog_and_wait(String title,String mass){
//        show_wait = true;
//        show_dialog(title,mass);
//    }

//    public void set_program_dialog(){
//        program_dialog = new ProgressDialog(SterileActivity.this);
//        program_dialog.setMessage("Please scan program");
//        program_dialog.setCancelable(false);
//
//        program_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
//            @Override
//            public void onDismiss(DialogInterface dialogInterface) {
//                Log.d("tog_alert_onDismiss","program_dialog_onDismiss = " + on_scan_program_mac);
//                on_scan_program_mac = false;
//            }
//        });
//
//        program_dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("tog_alert_onDismiss","program_dialog = " + on_scan_program_mac);
//                program_dialog.dismiss();//dismiss dialog
//
//            }
//        });
//
//        program_dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                int keyCode = keyEvent.getKeyCode();
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
//                {
//                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                        Log.d("tog_diKeyListener","enter = "+mass_onkey);
//                        String key = mass_onkey.substring(0,1);
//                        key= key.toLowerCase();
//                        switch (key)
//                        {
//                            case "p":
//                                if(on_scan_program_mac){
//                                    check_pro_id(mass_onkey.substring(1));
//                                }
//                                break;
//                            default:
//                                show_dialog_and_wait("Warning","Not found program id");
//                        }
//                        mass_onkey = "";
//                        return false;
//                    }
//
//                    if(keyCode != KeyEvent.KEYCODE_SHIFT_LEFT && keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT){
//                        char unicodeChar = (char)keyEvent.getUnicodeChar();
//                        mass_onkey=mass_onkey+unicodeChar;ssssssssssssssssssssssssssssssss
//                    }
//
//                    return false;
//                }
//                return false;
//            }
//        });
//    }

//    public void set_mac_program_id(String mac_name,String mac_id){
//        mac_id_non_approve = mac_id;
//        on_scan_program_mac = true;
//        program_dialog.setTitle(mac_name);
//
//        program_dialog.show();
//    }

//    public void check_pro_id(String pro_id){
//
//        Log.d("tog_sterile_process","pro_id = "+pro_id);
//        class check_pro_id extends AsyncTask<String, Void, String> {
//
//            // variable
//            @Override
//            protected void onPreExecute() {
//                super.onPreExecute();
//                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
//                this.dialog.show();
//            }
//
//            @Override
//            protected void onPostExecute(String result) {
//                super.onPostExecute(result);
//
//                try {
//                    JSONObject jsonObj = new JSONObject(result);
//                    rs = jsonObj.getJSONArray(TAG_RESULTS);
//
//                    for (int i = 0; i < rs.length(); i++) {
//                        JSONObject c = rs.getJSONObject(i);
//
//                        if (c.getString("result").equals("A")) {
//                            if(pro_id.equals(c.getString("process_id"))){
//                                process_id = c.getString("process_id");
//                                get_data = true;
//                                get_machine(mac_id_non_approve);
////                                reload_basket();
//                            }
//                        }
//                    }
//
//                    if(!get_data){
//                        show_dialog_and_wait("Warning","Not found program id");
//                        get_data = false;
//                    }
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//
//                if (dialog.isShowing()) {
//                    dialog.dismiss();
//                }
//
//
//            }
//
//            @Override
//            protected String doInBackground(String... params) {
//                HashMap<String, String> data = new HashMap<String, String>();
//
//                data.put("mac_id", mac_id_non_approve);
//                data.put("p_DB", p_DB);
//
//                String result = null;
//
//                try {
//                    result = httpConnect.sendPostRequest(getUrl + "get_sterile_process.php", data);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.d("tog_sterile_process","result = "+result);
//
//                return result;
//            }
//
//        }
//
//        check_pro_id obj = new check_pro_id();
//
//        obj.execute();
//    }


    public void set_loder(){

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
                            startMachine(list.get(list_mac_adapter.select_mac_pos).getDocNo(),list.get(list_mac_adapter.select_mac_pos).getMachineID(),list.get(list_mac_adapter.select_mac_pos).getTypeID());
                        }else{
                            show_dialog("Warning","Employee code is invalid");
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

                data.put("docno", list.get(list_mac_adapter.select_mac_pos).getDocNo());
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

        ProgressDialog dialog = new ProgressDialog(BasketWashingActivity.this);
        dialog.setMessage("Please scan employee code");
        dialog.setCancelable(false);

        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();//dismiss dialog

            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                int keyCode = keyEvent.getKeyCode();
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
                {
                    if (keyCode == KeyEvent.KEYCODE_ENTER) {
                        Log.d("tog_diKeyListener","enter = "+mass_onkey);

                        get_doc_in_mac obj = new get_doc_in_mac();
                        obj.execute(mass_onkey);

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

        dialog.show();
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
                String key = mass_onkey.substring(0,1);
                key= key.toLowerCase();
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
                        show_dialog("Warning","Not found basket");
                        break;
                    case "i":

                        Log.d("tog_dispatchcase","I = ");
                        if(list_basket_adapter.select_basket_pos>=0){
                            add_item_to_basket(xlist_basket.get(list_basket_adapter.select_basket_pos).getID(),mass_onkey);
                        }else{
                            show_dialog("Warning","Please select basket to add");
                        }

                        break;
                    case "s":

                        if(mass_onkey.equals("sc012")){
                            if(list_mac_adapter.select_mac_pos>=0&&list_mac_adapter.select_mac_pos!=list.size()){
                                set_loder();
                            }else{
                                show_dialog("Warning","Please select machine to start");
                            }
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
        loadind_dialog_dismis();

        alert_builder.setTitle("Error");
        alert_builder.setMessage(mass);

        alert = alert_builder.create();

        if(true){
            alert.show();
        }

    }

}
