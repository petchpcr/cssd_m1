package com.poseintelligence.cssdm1.Menu_Signature_Department;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.model.ModelDocnoPayoutSignature;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDisplayDocnoPayoutAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SignatureDepartmentActivity extends AppCompatActivity {

    private ImageView backpage;
    private Button btn_search;
    private TextView txt_sdate;
    private TextView txt_edate;
    private ListView rq_listdoc;

    private SearchableSpinner spn_department_form;
    private String DocDate_s = "";
    private String DocDate_e = "";
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private List<ModelDocnoPayoutSignature> Model_RQ = null;
    private Calendar myCalendar = Calendar.getInstance();
    private final ArrayList<String> array_deptsp = new ArrayList<String>();
    private final ArrayList<String> list_sp = new ArrayList<String>();
    private ArrayAdapter<String> adapter_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_department);

        byWidget();
    }

    @Override
    public void onResume(){
        super.onResume();
        getlistdata();

    }

    public void byWidget() {

        spn_department_form = ( SearchableSpinner ) findViewById(R.id.spn_department_form);
        spn_department_form.setTitle("เลือกแผนก");
        spn_department_form.setPositiveButton("ปิด");

        btn_search = (Button) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                getlistdata();
            }
        });

        backpage = ( ImageView ) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(SignatureDepartmentActivity.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        });

        rq_listdoc = (ListView) findViewById(R.id.rq_listdoc);

        txt_sdate = (TextView) findViewById(R.id.txt_sdate);
        final DatePickerDialog.OnDateSetListener date_s = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate_S();
            }
        };

        txt_sdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignatureDepartmentActivity.this, date_s, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txt_edate = (TextView) findViewById(R.id.txt_edate);
        final DatePickerDialog.OnDateSetListener date_e = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate_E();
            }
        };

        txt_edate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(SignatureDepartmentActivity.this, date_e, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(isNullOrEmpty(DocDate_s)) {
            updateDate_S();
        }else {
            txt_sdate.setText(DocDate_s);
        }

        if(isNullOrEmpty(DocDate_e)) {
            updateDate_E();
        }else {
            txt_edate.setText(DocDate_e);
        }

        getdeptsp("");

        getlistdata();
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    private void updateDate_S() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_sdate.setText(sdf.format(myCalendar.getTime()));
        DocDate_s = txt_sdate.getText().toString();
    }

    private void updateDate_E() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_edate.setText(sdf.format(myCalendar.getTime()));
        DocDate_e = txt_edate.getText().toString();
    }


    public void getlistdata() {
        class getlistdata extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(SignatureDepartmentActivity.this);
            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setTitle(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    List<ModelDocnoPayoutSignature> list = new ArrayList<>();
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        list.add(
                                get(
                                        c.getString("RowID"),
                                        c.getString("CreateDate"),
                                        c.getString("Docno"),
                                        c.getString("DepName2"),
                                        c.getString("Round"),
                                        i
                                )
                        );
                    }
                    Model_RQ = list;
                    ArrayAdapter<ModelDocnoPayoutSignature> adapter;
                    adapter = new ListDisplayDocnoPayoutAdapter(SignatureDepartmentActivity.this, Model_RQ);
                    rq_listdoc.setAdapter(adapter);
                    rq_listdoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                         @Override
                         public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                             Object o = rq_listdoc.getItemAtPosition(position);
                             ModelDocnoPayoutSignature newsData = ( ModelDocnoPayoutSignature ) o;

                             String DocNo = "";
                             String Depname = "";
                             String Docdate = "";

                             DocNo = newsData.getDocno();
                             Depname = newsData.getDepname();
                             Docdate = newsData.getDocdate();

                             Intent intent = new Intent(SignatureDepartmentActivity.this, SignatureDepartmentDetailActivity.class);
                             intent.putExtra("DocNo", DocNo);
                             intent.putExtra("Depname", Depname);
                             intent.putExtra("Docdate",Docdate);
                             startActivity(intent);

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("p_sdate", txt_sdate.getText().toString());
                data.put("p_edate", txt_edate.getText().toString());
                data.put("p_dept_person", String.valueOf(spn_department_form.getSelectedItem()));
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_signature.php", data);

                    Log.d("tog_display1","getxUrl : "+((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_signature.php");
                    Log.d("tog_display1","result : "+result);
                    Log.d("tog_display1","data : "+data);

                }catch(Exception e){

                    e.printStackTrace();

                }

                return result;
            }
            private ModelDocnoPayoutSignature get(String ID, String Docno, String Docdate, String Depname, String Payround, int index){
                return new ModelDocnoPayoutSignature(ID, Docno, Docdate, Depname, Payround, index);
            }
            // =========================================================================================
        }
        getlistdata obj = new getlistdata();
        obj.execute();
    }

    public void getdeptsp(String x) {
        class getdeptsp extends AsyncTask<String, Void, String> {
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
                    array_deptsp.clear();

                    array_deptsp.add("");
//                    list_sp.add("-");

                    for (int i = 0; i < rs.length(); i++) {

                        JSONObject c = rs.getJSONObject(i);
                        list_sp.add(c.getString("xName"));
                        array_deptsp.add(c.getString("xID"));

                    }
                    adapter_spinner = new ArrayAdapter<String>(SignatureDepartmentActivity.this, android.R.layout.simple_spinner_dropdown_item, list_sp);
                    adapter_spinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    spn_department_form.setAdapter(adapter_spinner);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "sendsterile/get_department_sendsterile.php", data);

                Log.d("tog_display1","getxUrl : "+((CssdProject) getApplication()).getxUrl() + "sendsterile/get_department_sendsterile.php");
                Log.d("tog_display2","result : "+result);
                Log.d("tog_display3","data : "+data);

                return result;
            }
        }

        getdeptsp ru = new getdeptsp();
        ru.execute(x);

    }

    public void onBackPressed() {
        backpage.callOnClick();
    }

}