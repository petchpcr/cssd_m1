package com.poseintelligence.cssdm1.Menu_CheckStock;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_CheckStock.adapter.ItemCheckStockDetailAdapter;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.CheckStockDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckStockItemActivity extends AppCompatActivity {
    protected final String TAG_RESULTS = "result";
    protected JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    public static String folder_php = "check_stock/";
    public String getUrl;
    public String p_DB;

    private ListView listStock;
    private String itemCode;
    private String stock;

    ArrayList<CheckStockDetail> xlist_detail = new ArrayList<>();
    ItemCheckStockDetailAdapter detailAdapter;
    private int requestId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock_item);
        getSupportActionBar().hide();

        byWidget();
        byIntent();
        getStockDetail();
    }

    public void byIntent() {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            itemCode = bd.getString("itemCode", "");
            stock = bd.getString("stock", "1");
        } else {
            itemCode = "";
            stock = "1";
        }

        getUrl = ((CssdProject) getApplication()).getxUrl() + folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
    }

    public void byWidget() {
        listStock = findViewById(R.id.listview_items_in_stock);
        detailAdapter = new ItemCheckStockDetailAdapter(CheckStockItemActivity.this, xlist_detail);
        listStock.setAdapter(detailAdapter);

        Button btDelete = findViewById(R.id.bt_delete);
        Button btSave = findViewById(R.id.bt_save);

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStockDepDetail();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStockDepDetail();
            }
        });
    }

    public void getStockDetail() {
        final int reqId = ++requestId;

        class get_detail extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockItemActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                if (reqId != requestId || isFinishing()) {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    return;
                }

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    xlist_detail.clear();
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        xlist_detail.add(new CheckStockDetail(
                                c.getString("StockRowID"),
                                c.getString("UsageCode"),
                                c.getString("itemname"),
                                c.getString("DepID"),
                                c.getString("DepName"),
                                c.getString("date"),
                                c.getString("time"),
                                c.getString("OverExp"),
                                c.getString("BeforeExp"),
                                c.getString("IsCheckStock")
                        ));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    detailAdapter.notifyDataSetChanged();

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_DB", p_DB);
                data.put("userID", ((CssdProject) getApplication()).getPm().getUserid() + "");
                data.put("depId", "20");
                data.put("itemCode", itemCode);
                data.put("stock", stock);

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_stock_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_get_detail", "get_stock_detail data = " + data);
                Log.d("tog_get_detail", "get_stock_detail result = " + result);
                return result;
            }
        }

        get_detail obj = new get_detail();
        obj.execute();
    }

    public void selectStockDepDetail(String usageCode){
        class selectStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockItemActivity.this);

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
                    JSONObject c = rs.getJSONObject(0);
                    Toast.makeText(CheckStockItemActivity.this, c.getString("message"), Toast.LENGTH_SHORT).show();

//                    c.getString("result").equals("A")
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    getStockDetail();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("usageCode", usageCode);
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "select_stock_dep_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_selectStock", "get_stock_detail data = " + data);
                Log.d("tog_selectStock", "get_stock_detail result = " + result);
                return result;
            }
        }

        selectStock obj = new selectStock();
        obj.execute();
    }

    public void removeStockDepDetail() {
        class removeStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockItemActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(CheckStockItemActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    getStockDetail();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("itemCode", itemCode);
                data.put("userID", ((CssdProject) getApplication()).getCustomerId() + "");
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "remove_stock_dep_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_removeStock", "remove_stock_dep_detail data = " + data);
                Log.d("tog_removeStock", "remove_stock_dep_detail result = " + result);
                return result;
            }
        }

        removeStock obj = new removeStock();
        obj.execute();
    }

    public void setStockDepDetail() {
        class setStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockItemActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(CheckStockItemActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    getStockDetail();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("itemCode", itemCode);
                data.put("userID", ((CssdProject) getApplication()).getCustomerId() + "");
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "set_stock_dep_detail.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_setStock", "set_stock_dep_detail data = " + data);
                Log.d("tog_setStock", "set_stock_dep_detail result = " + result);
                return result;
            }
        }

        setStock obj = new setStock();
        obj.execute();
    }

    String mass_onkey="";
    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        int keyCode = event.getKeyCode();

//        Log.d("tog_allKey","keyCode = "+keyCode);
        if (event.getAction() == KeyEvent.ACTION_DOWN)
        {
            if(keyCode == KeyEvent.KEYCODE_BACK){
                onBackPressed();
            }else if (keyCode == KeyEvent.KEYCODE_ENTER) {
                if(mass_onkey.length()==0){
                    return false;
                }
                String key = mass_onkey.substring(0,1);

                Log.d("tog_dispatchKey","key = "+key);
                Log.d("tog_dispatchKey","mass_onkey = "+mass_onkey);
                if(key.toLowerCase().equals("i")){
                    selectStockDepDetail(mass_onkey);
                }
                
                mass_onkey = "";
                return false;
            }

            int unicodeChar = event.getUnicodeChar();

            if(unicodeChar!=0){
                mass_onkey=mass_onkey+(char)unicodeChar;
            }

            Log.d("tog_dispatchKey","keyCode = "+keyCode);

            return false;
        }
        return super.dispatchKeyEvent(event);
    }
}
