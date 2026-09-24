package com.poseintelligence.cssdm1.Menu_ParStock;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_Dispensing.CheckQR_Approve;
import com.poseintelligence.cssdm1.Menu_ParStock.adapter.ListAddParItemAdapter;
import com.poseintelligence.cssdm1.Menu_ParStock.adapter.ListParItemAdapter;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.date.DateTime;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelParStockItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParStockMainActivity extends AppCompatActivity {
    protected final String TAG_RESULTS = "result";
    protected JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    public static String folder_php = "par_stock/";
    public String getUrl;
    public String p_DB;

    private String departmentId;
    private String depName;
    private String depName2;
    private int userId;

    private TextView title_1;
    private TextView txt_date_now;
    private Button button_add_par_item;
    private Button btn_search_par_item;
    private ImageView img_back_1;
    private ListView list_par_item;
    private EditText txt_search_par_item;

    private List<ModelParStockItem> Model_Par_Item = new ArrayList<>();
    private ListParItemAdapter list_par_item_adapter;

    private List<ModelParStockItem> Model_Add_Par_Item = new ArrayList<>();
    private ListAddParItemAdapter list_add_par_item_adapter;
    private ListView list_add_par_item;
    private EditText txt_search_add_par_item;

    private Dialog pendingCheckDialog;
    private String pendingParStockId;
    private String pendingQty;
    private String pendingRemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_stock_main);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        byWidget();
        byIntent();
        getItemParStock();
    }

    public void byIntent() {
        Bundle bd = getIntent().getExtras();
        if (bd != null) {
            departmentId = bd.getString("Department_ID", "");
            depName = bd.getString("DepName", "");
            depName2 = bd.getString("DepName2", "");
        } else {
            departmentId = "";
            depName = "";
            depName2 = "";
        }

        getUrl = ((CssdProject) getApplication()).getxUrl() + folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
        userId = ((CssdProject) getApplication()).getPm().getUserid();

        String title = depName2;
        if (title == null || title.trim().equals("")) {
            title = depName;
        }
        if (title == null) {
            title = "";
        }
        title_1.setText(title);

        txt_date_now.setText(DateTime.getDate());

        boolean isAdmin = ((CssdProject) getApplication()).getPm().getIsAdmin();
        button_add_par_item.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }

    public void byWidget() {
        title_1 = findViewById(R.id.title_1);
        txt_date_now = findViewById(R.id.txt_date_now);
        button_add_par_item = findViewById(R.id.button_add_par_item);
        btn_search_par_item = findViewById(R.id.btn_search_par_item);
        img_back_1 = findViewById(R.id.img_back_1);
        list_par_item = findViewById(R.id.list_par_item);
        txt_search_par_item = findViewById(R.id.txt_search_par_item);

        img_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_search_par_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterParItems(txt_search_par_item.getText().toString());
            }
        });

        txt_search_par_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterParItems(s.toString());
            }
        });

        button_add_par_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogAddParItem();
            }
        });
    }

    public void getItemParStock() {
        class GetItemParStock extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_LOADING);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelParStockItem> list = new ArrayList<>();

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (!c.has("id") || "E".equals(c.optString("result"))) {
                                continue;
                            }

                            list.add(new ModelParStockItem(
                                    c.getString("id"),
                                    c.optString("itemcode", ""),
                                    c.optString("itemname", ""),
                                    c.optString("par_qty", "0"),
                                    c.optString("qty", "0"),
                                    c.optString("remark", "")
                            ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Model_Par_Item = list;
                    filterParItems(txt_search_par_item.getText().toString());

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);
                data.put("department_id", departmentId);

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_item_par_stock.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        GetItemParStock obj = new GetItemParStock();
        obj.execute();
    }

    private void filterParItems(String keyword) {
        List<ModelParStockItem> list = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase();

        if (query.length() == 0) {
            list.addAll(Model_Par_Item);
        } else {
            for (int i = 0; i < Model_Par_Item.size(); i++) {
                ModelParStockItem item = Model_Par_Item.get(i);
                if (containsIgnoreCase(item.getItemname(), query)
                        || containsIgnoreCase(item.getItemcode(), query)) {
                    list.add(item);
                }
            }
        }

        list_par_item_adapter = new ListParItemAdapter(ParStockMainActivity.this, list);
        list_par_item.setAdapter(list_par_item_adapter);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void openDialogAddParItem() {
        final Dialog dialog = new Dialog(ParStockMainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_par_item);
        dialog.setCancelable(true);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        }

        ImageView img_close_add_par_item = dialog.findViewById(R.id.img_close_add_par_item);
        Button btn_search_add_par_item = dialog.findViewById(R.id.btn_search_add_par_item);
        list_add_par_item = dialog.findViewById(R.id.list_add_par_item);
        txt_search_add_par_item = dialog.findViewById(R.id.txt_search_add_par_item);

        img_close_add_par_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_search_add_par_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterAddParItems(txt_search_add_par_item.getText().toString());
            }
        });

        txt_search_add_par_item.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterAddParItems(s.toString());
            }
        });

        list_add_par_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_add_par_item_adapter == null) {
                    return;
                }
                ModelParStockItem item = list_add_par_item_adapter.getItem(position);
                if (item == null) {
                    return;
                }
                openDialogEnterParQty(item);
            }
        });

        dialog.show();
        getItemNotInParStock();
    }

    private void getItemNotInParStock() {
        class GetItemNotInParStock extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_LOADING);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelParStockItem> list = new ArrayList<>();

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (!c.has("itemcode") || "E".equals(c.optString("result"))) {
                                continue;
                            }

                            list.add(new ModelParStockItem(
                                    "",
                                    c.optString("itemcode", ""),
                                    c.optString("itemname", ""),
                                    "0",
                                    "0",
                                    ""
                            ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Model_Add_Par_Item = list;
                    if (txt_search_add_par_item != null) {
                        filterAddParItems(txt_search_add_par_item.getText().toString());
                    }

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);
                data.put("department_id", departmentId);

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "get_item_not_in_par_stock.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        GetItemNotInParStock obj = new GetItemNotInParStock();
        obj.execute();
    }

    private void filterAddParItems(String keyword) {
        if (list_add_par_item == null) {
            return;
        }

        List<ModelParStockItem> list = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase();

        if (query.length() == 0) {
            list.addAll(Model_Add_Par_Item);
        } else {
            for (int i = 0; i < Model_Add_Par_Item.size(); i++) {
                ModelParStockItem item = Model_Add_Par_Item.get(i);
                if (containsIgnoreCase(item.getItemname(), query)
                        || containsIgnoreCase(item.getItemcode(), query)) {
                    list.add(item);
                }
            }
        }

        list_add_par_item_adapter = new ListAddParItemAdapter(ParStockMainActivity.this, list);
        list_add_par_item.setAdapter(list_add_par_item_adapter);
    }

    private void openDialogEnterParQty(final ModelParStockItem item) {
        final Dialog dialog = new Dialog(ParStockMainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_item_to_par_stock);
        dialog.setCancelable(true);

        final TextView txt_item_name = (TextView) dialog.findViewById(R.id.txt_item_name);
        final TextView txt_caption = (TextView) dialog.findViewById(R.id.txt_caption);
        final EditText edt_qty = (EditText) dialog.findViewById(R.id.edt_qty);
        final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

        txt_caption.setText("ป้อนจำนวน PAR");
        txt_item_name.setText(item.getItemcode() + " : " + item.getItemname());
        edt_qty.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_qty.setText("");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyText = edt_qty.getText().toString().trim();
                if (qtyText.equals("")) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_QTY, Toast.LENGTH_SHORT).show();
                    return;
                }

                int parQty;
                try {
                    parQty = Integer.parseInt(qtyText);
                } catch (Exception e) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_MISS_VALUE, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (parQty < 1) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_LESS_VALUE, Toast.LENGTH_SHORT).show();
                    return;
                }

                addParStock(item.getItemcode(), String.valueOf(parQty), dialog);
            }
        });

        dialog.show();
        edt_qty.requestFocus();
    }

    private void addParStock(final String itemcode, final String parQty, final Dialog qtyDialog) {
        class AddParStock extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                String message = Cons.WARNING_SAVE_UNCOMPLETED;
                boolean success = false;

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        if (rs.length() > 0) {
                            JSONObject c = rs.getJSONObject(0);
                            message = c.optString("Message", message);
                            success = "A".equals(c.optString("result"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Toast.makeText(ParStockMainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (success) {
                        if (qtyDialog != null && qtyDialog.isShowing()) {
                            qtyDialog.dismiss();
                        }
                        getItemNotInParStock();
                        getItemParStock();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);
                data.put("itemcode", itemcode);
                data.put("department_id", departmentId);
                data.put("par_qty", parQty);
                data.put("create_by", String.valueOf(userId));

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "add_par_stock.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        AddParStock obj = new AddParStock();
        obj.execute();
    }

    public void openDialogEditParQty(final ModelParStockItem item) {
        if (item == null) {
            return;
        }

        if (!((CssdProject) getApplication()).getPm().getIsAdmin()) {
            return;
        }

        final Dialog dialog = new Dialog(ParStockMainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_item_to_par_stock);
        dialog.setCancelable(true);

        final TextView txt_item_name = (TextView) dialog.findViewById(R.id.txt_item_name);
        final TextView txt_caption = (TextView) dialog.findViewById(R.id.txt_caption);
        final EditText edt_qty = (EditText) dialog.findViewById(R.id.edt_qty);
        final Button btn_ok = (Button) dialog.findViewById(R.id.btn_ok);

        txt_caption.setText("แก้ไขจำนวน PAR");
        txt_item_name.setText(item.getItemcode() + " : " + item.getItemname());
        btn_ok.setText("บันทึก");
        edt_qty.setInputType(InputType.TYPE_CLASS_NUMBER);
        edt_qty.setText(item.getParQty() != null ? item.getParQty() : "");

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyText = edt_qty.getText().toString().trim();
                if (qtyText.equals("")) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_QTY, Toast.LENGTH_SHORT).show();
                    return;
                }

                int parQty;
                try {
                    parQty = Integer.parseInt(qtyText);
                } catch (Exception e) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_MISS_VALUE, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (parQty < 1) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_LESS_VALUE, Toast.LENGTH_SHORT).show();
                    return;
                }

                updateParStock(item.getId(), String.valueOf(parQty), dialog);
            }
        });

        dialog.show();
        edt_qty.requestFocus();
        edt_qty.selectAll();
    }

    private void updateParStock(final String parStockId, final String parQty, final Dialog qtyDialog) {
        class UpdateParStock extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                String message = Cons.WARNING_SAVE_UNCOMPLETED;
                boolean success = false;

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        if (rs.length() > 0) {
                            JSONObject c = rs.getJSONObject(0);
                            message = c.optString("Message", message);
                            success = "A".equals(c.optString("result"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Toast.makeText(ParStockMainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (success) {
                        if (qtyDialog != null && qtyDialog.isShowing()) {
                            qtyDialog.dismiss();
                        }
                        getItemParStock();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);
                data.put("id", parStockId);
                data.put("par_qty", parQty);
                data.put("modify_by", String.valueOf(userId));

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "update_par_stock.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        UpdateParStock obj = new UpdateParStock();
        obj.execute();
    }

    public void openDialogCheckParStock(final ModelParStockItem item) {
        if (item == null) {
            return;
        }

        final Dialog dialog = new Dialog(ParStockMainActivity.this, R.style.DialogCustomTheme);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_check_par_stock);
        dialog.setCancelable(true);

        final TextView txt_item_name = (TextView) dialog.findViewById(R.id.item_name);
        final TextView txt_item_par = (TextView) dialog.findViewById(R.id.item_par);
        final TextView txt_item_qty = (TextView) dialog.findViewById(R.id.item_qty);
        final EditText edt_qty = (EditText) dialog.findViewById(R.id.edt_qty);
        final EditText edt_remark = (EditText) dialog.findViewById(R.id.edt_remark);
        final Button btn_cancle = (Button) dialog.findViewById(R.id.bnt_cancle);
        final Button btn_ok = (Button) dialog.findViewById(R.id.bnt_ok);

        txt_item_name.setText(item.getItemname() != null ? item.getItemname() : "");
        txt_item_par.setText(item.getParQty() != null ? item.getParQty() : "0");
        txt_item_qty.setText(item.getQty() != null ? item.getQty() : "0");
        edt_qty.setText("");
        edt_remark.setText("");

        String lastRemark = item.getRemark() != null ? item.getRemark().trim() : "";
        if (!lastRemark.equals("")) {
            edt_remark.setHint(lastRemark);
        }

        btn_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyText = edt_qty.getText().toString().trim();
                if (qtyText.equals("")) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_QTY, Toast.LENGTH_SHORT).show();
                    return;
                }

                int qty;
                try {
                    qty = Integer.parseInt(qtyText);
                } catch (Exception e) {
                    Toast.makeText(ParStockMainActivity.this, Cons.WARNING_ENTER_MISS_VALUE, Toast.LENGTH_SHORT).show();
                    return;
                }

                String remarkText = edt_remark.getText().toString().trim();
                if (remarkText.equals("")) {
                    remarkText = item.getRemark() != null ? item.getRemark().trim() : "";
                }

                pendingParStockId = item.getId();
                pendingQty = String.valueOf(qty);
                pendingRemark = remarkText;
                pendingCheckDialog = dialog;
                openDialogCheckEmployee();
            }
        });

        dialog.show();
        edt_qty.requestFocus();
    }

    private void openDialogCheckEmployee() {
        Intent i = new Intent(ParStockMainActivity.this, CheckQR_Approve.class);
        i.putExtra("xSel", "payrow");
        i.putExtra("remark", "payrow");
        i.putExtra("DocNo", "");
        i.putExtra("B_ID", "1");
        i.putExtra("type", "0");
        startActivityForResult(i, 1155);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1155 && data != null) {
            String createBy = data.getStringExtra("RETURN_ID");
            if (createBy == null || createBy.trim().equals("")) {
                Toast.makeText(ParStockMainActivity.this, "ไม่พบรหัสผู้ใช้", Toast.LENGTH_SHORT).show();
                return;
            }

            addParStockLog(pendingParStockId, pendingQty, pendingRemark, createBy, pendingCheckDialog);
        }
    }

    private void addParStockLog(final String parStockId, final String qty, final String remark, final String createBy, final Dialog checkDialog) {
        class AddParStockLog extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockMainActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.TITLE);
                this.dialog.setIcon(R.drawable.pose_favicon_2x);
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.setCanceledOnTouchOutside(false);
                this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                this.dialog.setIndeterminate(true);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                String message = Cons.WARNING_SAVE_UNCOMPLETED;
                boolean success = false;

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        if (rs.length() > 0) {
                            JSONObject c = rs.getJSONObject(0);
                            message = c.optString("Message", message);
                            success = "A".equals(c.optString("result"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    Toast.makeText(ParStockMainActivity.this, message, Toast.LENGTH_SHORT).show();

                    if (success) {
                        if (checkDialog != null && checkDialog.isShowing()) {
                            checkDialog.dismiss();
                        }
                        getItemParStock();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);
                data.put("par_stock_id", parStockId);
                data.put("par_qty", qty);
                data.put("remark", remark);
                data.put("create_by", createBy);

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "add_par_stock_log.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        AddParStockLog obj = new AddParStockLog();
        obj.execute();
    }
}
