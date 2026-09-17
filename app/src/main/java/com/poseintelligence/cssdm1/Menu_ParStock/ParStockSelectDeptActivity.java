package com.poseintelligence.cssdm1.Menu_ParStock;

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
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.Menu_ParStock.adapter.ListDepartmentAdapter;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDepartment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParStockSelectDeptActivity extends AppCompatActivity {
    protected final String TAG_RESULTS = "result";
    protected JSONArray rs = null;
    public HTTPConnect httpConnect = new HTTPConnect();
    public static String folder_php = "par_stock/";
    public String getUrl;
    public String p_DB;

    private ListView list_department;
    private EditText txt_search_department;
    private Button btn_search_department;
    private ImageView img_back_1;

    private List<ModelDepartment> Model_Department = new ArrayList<>();
    private ListDepartmentAdapter list_department_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_par_stock_select_dept);
        getSupportActionBar().hide();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        byIntent();
        byWidget();
        displayDepartment();
    }

    public void byIntent() {
        getUrl = ((CssdProject) getApplication()).getxUrl() + folder_php;
        p_DB = ((CssdProject) getApplication()).getD_DATABASE();
    }

    public void byWidget() {
        list_department = findViewById(R.id.list_department);
        txt_search_department = findViewById(R.id.txt_search_department);
        btn_search_department = findViewById(R.id.btn_search_department);
        img_back_1 = findViewById(R.id.img_back_1);

        img_back_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMainMenu();
            }
        });

        btn_search_department.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterDepartments(txt_search_department.getText().toString());
            }
        });

        txt_search_department.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                filterDepartments(s.toString());
            }
        });

        list_department.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (list_department_adapter == null) {
                    return;
                }
                ModelDepartment dept = list_department_adapter.getItem(position);
                if (dept == null) {
                    return;
                }
                list_department_adapter.setSelection(position);

                Intent intent = new Intent(ParStockSelectDeptActivity.this, ParStockMainActivity.class);
                intent.putExtra("Department_ID", dept.getID());
                intent.putExtra("DepName", dept.getDepName());
                intent.putExtra("DepName2", dept.getDepName2());
                startActivity(intent);
            }
        });
    }

    public void displayDepartment() {
        class DisplayDepartment extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(ParStockSelectDeptActivity.this);

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

                List<ModelDepartment> list = new ArrayList<>();

                try {
                    if (result != null && !result.trim().equals("")) {
                        JSONObject jsonObj = new JSONObject(result);
                        rs = jsonObj.getJSONArray(TAG_RESULTS);

                        for (int i = 0; i < rs.length(); i++) {
                            JSONObject c = rs.getJSONObject(i);

                            if (!c.has("Department_ID") || "E".equals(c.optString("result"))) {
                                continue;
                            }

                            list.add(new ModelDepartment(
                                    c.getString("Department_ID"),
                                    c.getString("DepName"),
                                    c.getString("DepName2"),
                                    c.optString("IsCancel", "0")
                            ));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    Model_Department = list;
                    filterDepartments(txt_search_department.getText().toString());

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", p_DB);

                String result = null;
                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_display_document.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_par_stock", "data = " + data);
                Log.d("tog_par_stock", "result = " + result);
                return result;
            }
        }

        DisplayDepartment obj = new DisplayDepartment();
        obj.execute();
    }

    private void filterDepartments(String keyword) {
        List<ModelDepartment> list = new ArrayList<>();
        String query = keyword == null ? "" : keyword.trim().toLowerCase();

        if (query.length() == 0) {
            list.addAll(Model_Department);
        } else {
            for (int i = 0; i < Model_Department.size(); i++) {
                ModelDepartment dept = Model_Department.get(i);
                if (containsIgnoreCase(dept.getDepName(), query)
                        || containsIgnoreCase(dept.getDepName2(), query)) {
                    list.add(dept);
                }
            }
        }

        list_department_adapter = new ListDepartmentAdapter(ParStockSelectDeptActivity.this, list, "#D6EAF8");
        list_department.setAdapter(list_department_adapter);
    }

    private boolean containsIgnoreCase(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }

    private void goMainMenu() {
        Intent intent = new Intent(ParStockSelectDeptActivity.this, MainMenu.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        goMainMenu();
        super.onBackPressed();
    }
}
