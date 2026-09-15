package com.poseintelligence.cssdm1.Menu_CheckStock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_CheckStock.adapter.ItemCheckStockAdapter;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.CheckStockItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CheckStockMainActivity extends AppCompatActivity {
    protected final String TAG_RESULTS = "result";
    protected JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    public static String folder_php = "check_stock/";
    public String getUrl;
    public String p_DB;

    private static final int SWITCH_ANIM_DURATION = 180;

    private ListView listStock;
    private Button btnInStock;
    private Button btnAllStock;
    private EditText etSearch;

    ArrayList<CheckStockItem> xlist_check_stock = new ArrayList<>();
    ArrayList<CheckStockItem> xlist_display = new ArrayList<>();
    ItemCheckStockAdapter stockAdapter;
    protected boolean showingAllStock = false;
    private boolean switching = false;
    private int requestId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_stock_main);
        getSupportActionBar().hide();

        byWidget();

        byIntent();

        getItemsInStock(showingAllStock);
    }

    //tab
    public void resetListState() {
        listStock.clearChoices();
        listStock.clearFocus();
        listStock.setSelection(0);
        listStock.scrollTo(0, 0);
    }

    private void showTab(final boolean allStock) {
        if (switching || allStock == showingAllStock) {
            return;
        }
        showingAllStock = allStock;

        animateListSwitch(allStock, new Runnable() {
            @Override
            public void run() {
                xlist_check_stock.clear();
                xlist_display.clear();
                etSearch.setText("");
                stockAdapter.notifyDataSetChanged();
                resetListState();

                getItemsInStock(allStock);
            }
        });
    }

    private void animateListSwitch(boolean goRight, final Runnable swapData) {
        final int width = listStock.getWidth();
        if (width == 0) {
            swapData.run();
            return;
        }

        switching = true;
        setTabsEnabled(false);

        final int outX = goRight ? -width : width;
        final int inX = goRight ? width : -width;

        listStock.animate()
                .translationX(outX)
                .alpha(0f)
                .setDuration(SWITCH_ANIM_DURATION)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        swapData.run();

                        listStock.setTranslationX(inX);
                        listStock.animate()
                                .translationX(0f)
                                .alpha(1f)
                                .setDuration(SWITCH_ANIM_DURATION)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        switching = false;
                                        setTabsEnabled(true);
                                    }
                                });
                    }
                });
    }

    private void setTabsEnabled(boolean enabled) {
        btnInStock.setEnabled(enabled);
        btnAllStock.setEnabled(enabled);
    }

    //tab

    public void byIntent(){
        Bundle bd = getIntent().getExtras();

        getUrl=((CssdProject) getApplication()).getxUrl()+folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
    }

    public void byWidget(){
        listStock = findViewById(R.id.listview_items_in_stock);
        btnInStock = findViewById(R.id.btn_in_stock);
        btnAllStock = findViewById(R.id.btn_all_stock);
        etSearch = findViewById(R.id.et_search);

        stockAdapter = new ItemCheckStockAdapter(CheckStockMainActivity.this, xlist_display);
        listStock.setAdapter(stockAdapter);

        listStock.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckStockItem item = xlist_display.get(position);
                Intent intent = new Intent(CheckStockMainActivity.this, CheckStockItemActivity.class);
                intent.putExtra("itemCode", item.getItemCode());
                intent.putExtra("stock", showingAllStock ? "0" : "1");
                intent.putExtra("itemname", item.getItemname());
                startActivity(intent);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterItems(s.toString());
            }
        });

        btnInStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTab(false);
            }
        });

        btnAllStock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTab(true);
            }
        });
    }

    public void getItemsInStock(final boolean allStock){

        final int reqId = ++requestId;

        class get_item extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockMainActivity.this);

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
                    xlist_check_stock.clear();
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        xlist_check_stock.add(new CheckStockItem(
                            c.getString("RowID"),
                            c.getString("ItemCode"),
                            c.getString("itemname"),
                            c.getString("DeptID"),
                            c.getString("DepName"),
                            c.getString("CheckQty"),
                            c.getString("Qty")
                        ));

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally{
                    filterItems(etSearch.getText().toString());
                    resetListState();

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_DB", p_DB);
                data.put("userID", ((CssdProject) getApplication()).getPm().getUserid()+"");
                data.put("depId", "20");

                String result = null;

                try {
                    if(allStock){
                        result = httpConnect.sendPostRequest(getUrl + "get_stock_out.php", data);
                    }else{
                        result = httpConnect.sendPostRequest(getUrl + "get_stock_in.php", data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

               Log.d("tog_get_item","get_items_in_stock data = " + data);
               Log.d("tog_get_item","get_items_in_stock result = " + result);
                return result;
            }

            // =========================================================================================
        }

        get_item obj = new get_item();
        
        obj.execute();
    }



    private void filterItems(String keyword) {
        xlist_display.clear();

        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        if (query.length() == 0) {
            xlist_display.addAll(xlist_check_stock);
        } else {
            for (int i = 0; i < xlist_check_stock.size(); i++) {
                CheckStockItem item = xlist_check_stock.get(i);
                if (containsIgnoreCase(item.getItemname(), query)
                        || containsIgnoreCase(item.getItemCode(), query)) {
                    xlist_display.add(item);
                }
            }
        }

        stockAdapter.notifyDataSetChanged();
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
