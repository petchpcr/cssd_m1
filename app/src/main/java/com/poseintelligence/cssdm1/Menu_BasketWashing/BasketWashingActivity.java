package com.poseintelligence.cssdm1.Menu_BasketWashing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.poseintelligence.cssdm1.adapter.ListBoxBasketAdapter;
import com.poseintelligence.cssdm1.adapter.ListBoxMachineAdapter;
import com.poseintelligence.cssdm1.adapter.ListItemBasketAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.BasketTag;
import com.poseintelligence.cssdm1.model.Item;
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
    String folder_php = "sterile_basket/";
    public String getUrl;
    public String p_DB;

    boolean basket_1st = false;

    String mass_onkey="";

    RecyclerView list_mac;
    RecyclerView list_basket;
    ListView list_item_basket;
    TextView bt_delete;
    TextView bt_select_all;
    LinearLayoutManager lm;

    boolean is_select_all = false;

    ArrayList<ModelMachine> list = new ArrayList<>();
    ListBoxMachineAdapter list_mac_adapter;
    public String mac_empty_id = "Empty";

    ArrayList<BasketTag> xlist_basket = new ArrayList<>();
    ListBoxBasketAdapter list_basket_adapter;

    ArrayList<Item> xlist_item_basket = new ArrayList<>();
    ListItemBasketAdapter list_item_basket_adapter;

    public AlertDialog.Builder alert_builder;
    public AlertDialog alert;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket_washing);

        byIntent();

        byWidget();
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
        list_mac = (RecyclerView) findViewById(R.id.list_mac);
        list_basket = (RecyclerView) findViewById(R.id.list_basket);
        list_item_basket = (ListView) findViewById(R.id.list_item_basket);

        bt_delete = (TextView) findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=xlist_item_basket.size()-1;i>=0;i--){
                    Log.d("tog_delete","position = "+i);
                    if(xlist_item_basket.get(i).isChk()){
                        item_to_delete(i);
                        Log.d("tog_delete","position = delete");
                    }
                }
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

        get_machine("null");
        get_basket("null");
        alert_builder = new AlertDialog.Builder(BasketWashingActivity.this);



        xlist_item_basket.add(new Item("I00215-219-00001","หน่วยตรวจผู้ป่วยนอกรังสีร่วมรักษาระบบประสาทและหลอดเลือด",false));
        xlist_item_basket.add(new Item("I00215-219-00002","Jug",false));
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("2","9999",false));
        xlist_item_basket.add(new Item("3","9999",false));
        xlist_item_basket.add(new Item("4","9999",false));
        xlist_item_basket.add(new Item("5","9999",false));
        xlist_item_basket.add(new Item("6","9999",false));
        xlist_item_basket.add(new Item("7","9999",false));

    }

    public void get_machine(String mac_id) {

        class get_machine extends AsyncTask<String, Void, String> {

            int mac_select_id = -1;

            private ProgressDialog dialog = new ProgressDialog(BasketWashingActivity.this);

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

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    if(mac_id.equals("null")){
                        list.clear();

                        list_mac_adapter = new ListBoxMachineAdapter(BasketWashingActivity.this, list,list_mac);
                        list_mac.setAdapter(list_mac_adapter);
                    }else{
                        if(mac_id.equals(mac_empty_id)){
                            list.get(list.size()-1).setIsActive("0");
                            list_mac_adapter.onScanSelect(list.size()-1);
                            basket_1st =false;
                            reload_basket();
                        }else{
                            for (int i = 0; i < rs.length(); i++) {
                                JSONObject c = rs.getJSONObject(i);
                                Log.d("tog_scan_basket","xID = "+c.getString("xID")+"---"+mac_id);

                                for (int j = 0; j < list.size(); j++) {
                                    if(list.get(j).getMachineID().equals(c.getString("xID"))){
                                        list.get(j).setIsActive(c.getString("IsActive"));
                                        list.get(j).setIsBrokenMachine(c.getString("IsBrokenMachine"));

                                        if(c.getString("xID").equals(mac_id)){
                                            if(c.getString("IsActive").equals("1")){
                                                show_dialog("Warning","Machine is running");
                                            }else if(c.getString("IsBrokenMachine").equals("1")){
                                                show_dialog("Warning","Machine is broken");
                                            }else{
                                                mac_select_id = j;
                                                list_mac_adapter.onScanSelect(mac_select_id);
                                                basket_1st =false;
                                                reload_basket();
                                            }
                                        }
                                    }
                                }

                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


                if (dialog.isShowing()) {
                    dialog.dismiss();
                }


            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("mac_id", mac_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterilemachine.php", data);
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
            int pos = -1;
            private ProgressDialog dialog = new ProgressDialog(BasketWashingActivity.this);

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

                        list_basket_adapter = new ListBoxBasketAdapter(BasketWashingActivity.this, xlist_basket,list_basket);
                        list_basket.setAdapter(list_basket_adapter);
                    }else{
                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("result").equals("A")) {

                                if(c.getString("BasketCode").toLowerCase().equals(basket_id.toLowerCase())){

                                    for (int j = 0; j < xlist_basket.size(); j++) {
                                        if(xlist_basket.get(j).getBasketCode().equals(c.getString("BasketCode"))){
                                            xlist_basket.get(j).setMacId(c.getString("InMachineID"));

                                            int mac_pos = list_mac_adapter.select_mac_pos;

                                            if(mac_pos>0){
                                                String mac_id = list.get(mac_pos).getMachineID();
                                                if(c.getString("InMachineID").equals("null")){//ตะกร้าไม่มีเครื่อง
                                                    //add basket in mac
                                                    get_item_in_basket(j);
                                                    list_basket_adapter.onScanSelect(j);
                                                }else if (c.getString("InMachineID").equals(mac_id)){
                                                    get_item_in_basket(j);
                                                    list_basket_adapter.onScanSelect(j);
                                                }else{
                                                    list_basket_adapter.onScanSelect(-1);
                                                    if(mac_pos==list.size()-1){
                                                        list_mac_adapter.onScanSelect(-1);
                                                        show_dialog("Warning","Basket is in some machine");
                                                    }else{
                                                        show_dialog("Warning","Basket is in another machine");
                                                    }
                                                }
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
                    e.printStackTrace();
                }


                if (dialog.isShowing()) {
                    dialog.dismiss();
                }


            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("basket_id", basket_id);
                data.put("p_DB", p_DB);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_sterilebasket.php", data);
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

        if(x==xlist_item_basket.size()){
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

    public void item_to_delete(int index){
        xlist_item_basket.remove(index);
        list_item_basket.invalidateViews();

        get_item_in_basket(list_basket_adapter.select_basket_pos);
    }

    public void get_item_in_basket(int pos){
        if(pos>=0){
            list_item_basket_adapter = new ListItemBasketAdapter(this,xlist_item_basket);
            list_item_basket.setAdapter(list_item_basket_adapter);

            xlist_basket.get(pos).setQty(xlist_item_basket.size());
            list_basket_adapter.notifyDataSetChanged();
        }

    }

    public void show_dialog(String title,String mass){
        alert_builder.setTitle(title);
        alert_builder.setMessage(mass);

        alert = alert_builder.create();
        alert.show();

        handler.postDelayed(runnable, 1500);
    }

    public void reload_mac(){
        if(list_mac_adapter.select_mac_pos<list.size()&&list_mac_adapter.select_mac_pos>=0){
            String mac_ID = list.get(list_mac_adapter.select_mac_pos).getMachineID();
            get_machine(mac_ID);
        }else{
            get_machine(mac_empty_id);
            basket_1st = true;
        }
    }

    public void reload_basket(){
        int basket_pos = list_basket_adapter.select_basket_pos;

        Log.d("tog_basket","basket pos = "+basket_pos);
        if(basket_pos>=0){
            Log.d("tog_basket","getBasketCode = "+xlist_basket.get(basket_pos).getBasketCode());
            get_basket(xlist_basket.get(basket_pos).getBasketCode());
        }else{
            list_mac_adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();

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
                        reload_mac();
                        break;
                    default:
                }
                mass_onkey = "";
                return false;
            }

            if(keyCode != KeyEvent.KEYCODE_SHIFT_LEFT && keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT){
                char unicodeChar = (char)event.getUnicodeChar();
                mass_onkey=mass_onkey+unicodeChar;
            }

            Log.d("tog_dispatchKey","keyCode = "+keyCode);
            Log.d("tog_dispatchKey","mass_onkey = "+mass_onkey);

            return false;
        }

        return false;
    }


}