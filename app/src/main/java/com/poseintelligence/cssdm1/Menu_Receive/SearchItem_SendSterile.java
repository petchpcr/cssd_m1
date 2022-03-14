package com.poseintelligence.cssdm1.Menu_Receive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.search_sendsterileAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.Response_Aux_itemstock;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class SearchItem_SendSterile extends AppCompatActivity {
    private ArrayList<Response_Aux_itemstock> MODEL_ITEM_STOCK = new ArrayList<>();

    private String TAG_RESULTS="result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    private String p_bid = "1";
    private String p_docno = null;
    public String p_dept_id = null;
    private String p_user_code = null;
    public String p_dept_id_select = null;
    private String B_ID = "1";

    // IsReuse = true; create send sterile (Import from send sterile)
    // IsReuse = false; create wash (Import spacial)
    private boolean IsReuse = true;

    private ImageView img_back;
    private Button button_search;
    private ImageView button_import;
    private GridView grid;
    private EditText edt_search;
    private LinearLayout LinearLayout_department_search;
    private Switch switch_mode;
    private Switch switch_new;
    private boolean p_switch_washdep = false;
    private SearchableSpinner spn_department_search;

    // Config
    private boolean SS_IsUsedItemSet = true;
    private boolean SS_IsUsedItemDepartment = false;
    private boolean SS_IsSortByUsedCount = false;

    private ArrayList<String> ar_list_dept_id = new ArrayList<String>();
    private ArrayList<String> ar_list_dept_name = new ArrayList<String>();
    private ArrayAdapter<String> adapter_spinner;

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_item_send_sterile);

        getSupportActionBar().hide();

        byConfig();

        byWidget();

        byIntent();

        byEvent();

        switch_new.setVisibility(SS_IsUsedItemDepartment ? View.VISIBLE : View.GONE);

        if(p_dept_id != null && p_dept_id.equals("-1")){
            LinearLayout_department_search.setVisibility(View.VISIBLE);
            defineDepartment();
        }else{
            LinearLayout_department_search.setVisibility(View.GONE);
            displayItemStock("%");
        }

        /*
        if(p_dept_id != null && (!p_dept_id.equals("-1"))) {
            displayItemStock("%");
        }
        */

        //KeyboardUtils.hideKeyboard(SearchItem_SendSterile.this);
    }

    private void byConfig(){
        // -----------------------------------------------------------------------------------------
        // Get Config
        // -----------------------------------------------------------------------------------------
        SS_IsUsedItemSet = ((CssdProject) getApplication()).isSS_IsUsedItemSet();
        SS_IsUsedItemDepartment = ((CssdProject) getApplication()).isSS_IsUsedItemDepartment();
        SS_IsSortByUsedCount = ((CssdProject) getApplication()).isSS_IsSortByUsedCount();

        //System.out.println("SS_IsUsedItemDepartment = " + SS_IsUsedItemDepartment );

    }

    public void byIntent() {

        Bundle bd = getIntent().getExtras();

        if (bd != null){

            String xSel = bd.getString("xSel");
            p_docno = bd.getString("p_docno");
            p_dept_id = bd.getString("p_dept_id");
            p_user_code = bd.getString("p_user_code");
            IsReuse = bd.getBoolean("IsReuse", true);
            p_bid = bd.getString("B_ID");
            p_switch_washdep = bd.getBoolean("p_switch_washdep");

            Log.d("tog_xSel","p_switch_washdep = " + p_switch_washdep);
            Log.d("OOOO","p_docno = " + p_docno);
            Log.d("OOOO","p_dept_id = " + p_dept_id);
            Log.d("OOOO","p_user_code = " + p_user_code);
            Log.d("OOOO","IsReuse = " + IsReuse);
            Log.d("OOOO","p_bid = " + p_bid);

        }

    }

    private void byWidget(){
        grid = (GridView) findViewById(R.id.grid);
        edt_search = (EditText) findViewById(R.id.edt_search);

        img_back = (ImageView) findViewById(R.id.img_back);
        button_import = (ImageView) findViewById(R.id.bt_import_sendsterile);
        button_search = (Button) findViewById(R.id.bt_searchsendsterile);
        switch_mode = (Switch) findViewById(R.id.switch_mode);
        switch_new = (Switch) findViewById(R.id.switch_new);
        spn_department_search = ( SearchableSpinner ) findViewById(R.id.spn_department_search);
        spn_department_search.setTitle("เลือกแผนก");
        spn_department_search.setPositiveButton("");

        LinearLayout_department_search = (LinearLayout) findViewById(R.id.LinearLayout_department_search);

        button_search.bringToFront();

        //switch_mode.setChecked(SS_IsUsedItemDepartment);

        if(switch_mode.isChecked()) {
            button_search.setBackground(ContextCompat.getDrawable(SearchItem_SendSterile.this,R.drawable.ic_qrcode2));
        }else{
            button_search.setBackground(ContextCompat.getDrawable(SearchItem_SendSterile.this,R.drawable.ic_search_c));
        }
    }

    public void byEvent() {

        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!switch_mode.isChecked())
                    displayItemStock(edt_search.getText().toString().replace(' ','%'));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        edt_search.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                try {
                    if (switch_mode.isChecked() && event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_CENTER:
                            case KeyEvent.KEYCODE_ENTER:

                                String s = edt_search.getText().toString();


                                if(SS_IsUsedItemDepartment && s.length() < 3){
                                    // Department
                                    findDepartment(s);
                                    return true;
                                }else if (s.length() > 7) {
                                    // Item
                                    s = s.substring(0, 6);
                                }

                                addItem(s);

                            default:
                                break;
                        }
                    }
                    return false;
                }catch (Exception e){
                    return false;
                }

            }
        });


        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                displayItemStock(edt_search.getText().toString());

                edt_search.requestFocus();
            }
        });

        button_import.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddSendSterileDetail();

                //finish();
            }
        });

        switch_new.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch_new.setText(switch_new.isChecked() ? "รายการใหม่" : "รายการปกติ");
            }
        });

        switch_mode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switch_mode.isChecked()) {
                    grid.setAdapter(null);
                    MODEL_ITEM_STOCK.clear();
                    button_search.setBackground(ContextCompat.getDrawable(SearchItem_SendSterile.this,R.drawable.ic_qrcode2));
                }else{
                    displayItemStock("%");
                    button_search.setBackground(ContextCompat.getDrawable(SearchItem_SendSterile.this,R.drawable.ic_search_c));
                }
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void findDepartment(String S_DeptId){

        //System.out.println("S_DeptId = " + S_DeptId);
        //System.out.println("ar_list_dept_id.size() = " + ar_list_dept_id.size());

        for(int i=0;i<ar_list_dept_id.size();i++) {

            //System.out.println(S_DeptId + " = " + ar_list_dept_id.get(i));

            if(ar_list_dept_id.get(i).equals(S_DeptId)) {

                //System.out.println(S_DeptId);

                p_dept_id_select = ar_list_dept_id.get(i);

                spn_department_search.setSelection(i);

                displayItemStock("%");

                hideKeyboard(SearchItem_SendSterile.this);

                edt_search.setText("");
                edt_search.requestFocus();

                switch_mode.setChecked(false);
                button_search.setBackground(ContextCompat.getDrawable(SearchItem_SendSterile.this,R.drawable.ic_search_c));

                return;
            }
        }

        // Not found

        Toast.makeText(SearchItem_SendSterile.this, "ยังพบรายการในแผนกนี้ !!", Toast.LENGTH_SHORT).show();

        grid.setAdapter(null);
        MODEL_ITEM_STOCK.clear();

        hideKeyboard(SearchItem_SendSterile.this);

        edt_search.setText("");
        edt_search.requestFocus();

    }

    public void defineDepartment() {

        defineDepartment("x");

        spn_department_search.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //System.out.println("1111");

                p_dept_id_select = ar_list_dept_id.get(position);

                displayItemStock("%");

                edt_search.requestFocus();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public void defineDepartment(final String str) {

        class DefineDepartment extends AsyncTask<String, Void, String> {
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
                    JSONArray setRs = jsonObj.getJSONArray(TAG_RESULTS);

                    ar_list_dept_id.clear();
                    ar_list_dept_id.add("");
                    ar_list_dept_name.add("");

                    for (int i = 0; i < setRs.length(); i++) {

                        JSONObject c = setRs.getJSONObject(i);
                        ar_list_dept_name.add(c.getString("xName"));
                        ar_list_dept_id.add(c.getString("xID"));

                    }

                    adapter_spinner = new ArrayAdapter<String>(SearchItem_SendSterile.this, android.R.layout.simple_spinner_dropdown_item, ar_list_dept_name);
                    adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spn_department_search.setAdapter(adapter_spinner);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_select_department_by_send_sterile.php", data);
                return result;
            }
        }
        DefineDepartment ru = new DefineDepartment();
        ru.execute();
    }

    public void displayItemStock(final String Usage_code) {

        class DisplayItemStock extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    MODEL_ITEM_STOCK.clear();
                    Response_Aux_itemstock newsData;
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){
                        newsData = new Response_Aux_itemstock();
                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("bool").equals("true")) {
                            newsData.setFields1(c.getString("ID"));
                            newsData.setFields2(c.getString("Item_Code"));
                            newsData.setFields3(c.getString("PackDate"));
                            newsData.setFields4(c.getString("ExpDate"));
                            newsData.setFields5(c.getString("Dept"));
                            newsData.setFields6("");
                            newsData.setFields7(c.getString("Status"));
                            newsData.setFields8(c.getString("UsageID"));
                            newsData.setFields9(c.getString("Item_Name"));
                            newsData.setxFields10(c.getString("DeptID"));
                            newsData.setxFields11("0");
                            newsData.setxFields12(c.getString("itemqty"));
                            newsData.setxFields13("0");
                            newsData.setxFields14("0");
                            newsData.setxFields15(c.getString("Shelflife"));
                            newsData.setxFields16(c.getString("IsSet"));
                            newsData.setIs_Check(true);

                            MODEL_ITEM_STOCK.add( newsData );

                            if(i==0 && !Usage_code.equals("")){
                                edt_search.requestFocus();
                            }

                        }else{
                            edt_search.requestFocus();
                        }
                    }

                    grid.setAdapter(new search_sendsterileAdapter(SearchItem_SendSterile.this, MODEL_ITEM_STOCK));
                    Log.d("tog_display98","data222222");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("Usage_code", Usage_code);
                data.put("B_ID",p_bid);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                if(SS_IsUsedItemSet){
                    data.put("p_IsUsedItemSet", "1");
                }

                if(SS_IsSortByUsedCount){
                    data.put("p_IsSortByUsedCount", "1");
                }

                if(p_dept_id != null && p_dept_id.equals("-1") && p_dept_id_select != null && !p_dept_id_select.equals("") ) {

                    if(SS_IsUsedItemDepartment){
                        data.put("p_IsUsedItemDepartment", "1");
                    }

                    data.put("p_DeptID", p_dept_id_select);

                }else if(SS_IsUsedItemDepartment && p_dept_id != null){

                    if(SS_IsUsedItemDepartment){
                        data.put("p_IsUsedItemDepartment", "1");
                    }

                    data.put("p_DeptID", p_dept_id);
                }

                if(!IsReuse){
                    data.put("p_is_reuse", "0");
                }

                data.put("p_switch_washdep", p_switch_washdep ? "1" : "0");

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_item_by_send_sterile.php",data);
                Log.d("tog_display67","result : "+result);
                Log.d("tog_display66","data : "+data);
                return  result;
            }
        }

        DisplayItemStock ru = new DisplayItemStock();
        ru.execute();
    }

    private void onAddSendSterileDetail(){

        if( p_dept_id != null && p_dept_id.equals("-1") && p_dept_id_select == null){
            Toast.makeText(SearchItem_SendSterile.this, "ยังไม่ได้เลือกแผนก !!", Toast.LENGTH_SHORT).show();
            return;
        }

        String d_data = "";

        try {

            List<Response_Aux_itemstock> DATA_MODEL = MODEL_ITEM_STOCK;

            Iterator li = DATA_MODEL.iterator();

            while (li.hasNext()) {

                try {

                    Response_Aux_itemstock m = (Response_Aux_itemstock) li.next();

                    if(!m.getFields6().trim().equals("") && !m.getFields6().trim().equals("0")){
                        d_data += m.getFields1() + "," + m.getFields6()+ "," ;
                    }

                } catch (Exception e) {
                    Toast.makeText(SearchItem_SendSterile.this, "มีบางรายการไม่สามารถส่งล้างได้ !!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(d_data.equals("")){
            return;
        }

        final String p_data = d_data.substring(0, d_data.length() - 1);
        final String p_is_new_department = switch_new.isChecked() ? "1" : "0";

        //System.out.println(p_data);

        class AddSendSterileDetail extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(SearchItem_SendSterile.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCancelable(false);
                this.dialog.show();

            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {

                    MODEL_ITEM_STOCK.clear();

                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){

                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {

                            Intent intent = new Intent();
                            setResult((IsReuse ? 100 : 8888) , intent);
                            intent.putExtra("DocNo", c.getString("DocNo"));
                            intent.putExtra("p_data", p_data);

                            Toast.makeText(SearchItem_SendSterile.this, c.getString("Message"), Toast.LENGTH_SHORT).show();

                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            finish();

                        }else{
                            Toast.makeText(SearchItem_SendSterile.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                if(p_docno != null && !p_docno.equals("")) {
                    data.put("p_docno", p_docno);
                }

                data.put("p_data", p_data);
                data.put("p_user_code", p_user_code);
                data.put("p_bid", p_bid);

                if(p_dept_id.equals("-1")) {
                    data.put("p_dept_id_select", p_dept_id_select);
                }

                data.put("p_dept_id", p_dept_id);

                data.put("p_switch_washdep", p_switch_washdep ? "1" : "0");

                if( IsReuse && ( ((CssdProject) getApplication()).getCustomerId() == 201)){
                    data.put("p_is_used_itemstock_department", "0");
                    data.put("p_is_new_department", p_is_new_department);
                }else {
                    data.put("p_is_used_itemstock_department", "1");
                }

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String file = IsReuse ? "cssd_add_send_sterile_detail.php" : "cssd_add_wash_detail_un_reuse.php";

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + file, data);

                Log.d("tog_add_ss","result : "+result);
                Log.d("tog_add_ss","data : "+data);

                return  result;
            }
        }

        AddSendSterileDetail ru = new AddSendSterileDetail();
        ru.execute();

    }

    public void addItem(final String p_qr) {

        boolean IsSearch = false;

        Iterator li = MODEL_ITEM_STOCK.iterator();

        while (li.hasNext()) {
            Response_Aux_itemstock r = (Response_Aux_itemstock) li.next();
            //System.out.println(r.getFields2() + " = " + p_qr);
            if (r.getFields2().equals(p_qr)) {
                IsSearch = true;
                break;
            }
        }

        //System.out.println("IsSearch = " + IsSearch);

        if (IsSearch) {
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    edt_search.setText("");
                    edt_search.requestFocus();
                }
            }, 500);

        } else {

            class DisplayItemStock extends AsyncTask<String, Void, String> {

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    try {

                        Response_Aux_itemstock newsData;
                        JSONObject jsonObj = new JSONObject(s);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < rs.length(); i++) {
                            newsData = new Response_Aux_itemstock();
                            JSONObject c = rs.getJSONObject(i);

                            if (c.getString("bool").equals("true")) {
                                newsData.setFields1(c.getString("ID"));
                                newsData.setFields2(c.getString("Item_Code"));
                                newsData.setFields3(c.getString("PackDate"));
                                newsData.setFields4(c.getString("ExpDate"));
                                newsData.setFields5(c.getString("Dept"));
                                newsData.setFields6("");
                                newsData.setFields7(c.getString("Status"));
                                newsData.setFields8(c.getString("UsageID"));
                                newsData.setFields9(c.getString("Item_Name"));
                                newsData.setxFields10(c.getString("DeptID"));
                                newsData.setxFields11("0");
                                newsData.setxFields12(c.getString("itemqty"));
                                newsData.setxFields13("0");
                                newsData.setxFields14("0");
                                newsData.setxFields15(c.getString("Shelflife"));
                                newsData.setxFields16(c.getString("IsSet"));
                                newsData.setIs_Check(true);

                                MODEL_ITEM_STOCK.add(newsData);

                                if (i == 0 && !p_qr.equals("")) {
                                    edt_search.requestFocus();
                                }

                            } else {
                                Toast.makeText(SearchItem_SendSterile.this, "ไม่พบรายการ !!", Toast.LENGTH_SHORT).show();

                                edt_search.requestFocus();

                            }
                        }

                        grid.setAdapter(new search_sendsterileAdapter(SearchItem_SendSterile.this, MODEL_ITEM_STOCK));
                        Log.d("tog_display98","data222222");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        edt_search.setText("");
                        edt_search.requestFocus();
                    }
                }

                @Override
                protected String doInBackground(String... params) {
                    HashMap<String, String> data = new HashMap<String, String>();
                    data.put("p_qr", p_qr);
                    data.put("B_ID", p_bid);
                    data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());


                    if (SS_IsUsedItemSet) {
                        data.put("p_IsUsedItemSet", "1");
                    }

                    if(SS_IsSortByUsedCount){
                        data.put("p_IsSortByUsedCount", "1");
                    }

                    if (SS_IsUsedItemDepartment && p_dept_id != null) {
                        data.put("p_IsUsedItemDepartment", "1");
                        data.put("p_DeptID", p_dept_id);
                    }

                    if (!IsReuse) {
                        data.put("p_is_reuse", "0");
                    }


                    data.put("p_switch_washdep", p_switch_washdep ? "1" : "0");

                    String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_item_by_send_sterile.php", data);
                    Log.d("tog_display","result : "+result);
                    Log.d("tog_display","data : "+data);
                    return result;
                }
            }

            DisplayItemStock ru = new DisplayItemStock();
            ru.execute();
        }
    }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
