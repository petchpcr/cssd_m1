package com.poseintelligence.cssdm1.Menu_RecordTest;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListDisplayDocAdapter0_1;
import com.poseintelligence.cssdm1.adapter.ListDisplayDocAdapter1_1;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.ModelDisplayDoc0_1;
import com.poseintelligence.cssdm1.model.ModelDisplayDoc1_1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ResultsActivity extends AppCompatActivity {

    private TextView txt_date;
    private ListView rq_listdoc;
//    private ListView rq_listdoc1;
    private Spinner pg_spinner;
    private JSONArray rs = null;
    private String B_ID = null;
    private String Username = null;
    private String TAG_RESULTS="result";
    private HTTPConnect httpConnect = new HTTPConnect();
    ArrayList<String> liststatus = new ArrayList<String>();
    String DocDate="";
    String xSel = "1";
    String page = "";
    String EmpCode;
    Button bt_list;
    private ImageView backpage;
    private Calendar myCalendar = Calendar.getInstance();
    private View decorView;

    String ID;
    String FirstName;
    String LastName;
    String DepID;
    String DepName;
    String images;
    String Password;

    private RelativeLayout R1,R2,R1_1;

    private Spinner mac_spinner,round_spinner;
    String fill_MachineID = "-";
    String fill_RoundNumber = "-";
    ArrayList<String> list_fill_MachineID = new ArrayList<String>();
    ArrayList<String> list_fill_RoundNumber = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_test_list_doc);
        getSupportActionBar().hide();

//        decorView = getWindow().getDecorView();
//        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
//            @Override
//            public void onSystemUiVisibilityChange(int visibility) {
//                if (visibility == 0) {
//                    decorView.setSystemUiVisibility(hideSystemUI());
//                }
//            }
//        });

        byIntent();

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getlistdata(xSel,convertdate(DocDate));
//        getlistdata1("1",convertdate(DocDate));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void byIntent() {
        EmpCode = ((CssdProject) getApplication()).getPm().getEmCode()+"";
        B_ID = ((CssdProject) getApplication()).getPm().getBdCode()+"";
    }

//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus) {
//            decorView.setSystemUiVisibility(hideSystemUI());
//        }
//    }

    private int hideSystemUI() {
        return View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                ;
    }

    public void init() {

        R1 = ( RelativeLayout ) findViewById(R.id.R1);
        R2 = ( RelativeLayout ) findViewById(R.id.R2);

        rq_listdoc = (ListView) findViewById(R.id.rq_listdoc);

        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(ResultsActivity.this, MainMenu.class);
                startActivity(intent1);
                finish();
            }
        });

        bt_list = (Button) findViewById(R.id.bt_list);


        txt_date = ( TextView ) findViewById(R.id.txt_date);

        pg_spinner = (Spinner) findViewById(R.id.pg_spinner);

        pg_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    xSel = "0";
                } else {
                    xSel = "1";
                }

                getlistMac(xSel);
                mac_spinner.setSelection(0);
                round_spinner.setSelection(0);
                fill_MachineID = "-";
                fill_RoundNumber = "-";

                getlistdata(xSel, convertdate(DocDate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        mac_spinner = (Spinner) findViewById(R.id.mac_spinner);
        mac_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fill_MachineID = list_fill_MachineID.get(position);

                getlistdata(xSel, convertdate(DocDate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        round_spinner = (Spinner) findViewById(R.id.round_spinner);
        round_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fill_RoundNumber = "-";
                } else {
                    fill_RoundNumber = position+"";
                }

                getlistdata(xSel, convertdate(DocDate));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setSpinnerStatus();

        bt_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getlistdata(xSel, convertdate(DocDate));
//                getlistdata1(xSel, convertdate(DocDate));
            }
        });

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ResultsActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        if(isNullOrEmpty(DocDate)) {
            updateDate();
        }else {
            txt_date.setText(DocDate);
            getlistdata(xSel, convertdate(DocDate));
//            getlistdata1("1", convertdate(DocDate));
        }

        pg_spinner.setSelection(1);
    }

    private String DateNow() {
        String myFormat = "dd-MM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        return sdf.format(myCalendar.getTime());
    }

    private void updateDate() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_date.setText(sdf.format(myCalendar.getTime()));
        DocDate = txt_date.getText().toString();
        getlistdata(xSel, convertdate(DocDate));
//        getlistdata1("1", convertdate(DocDate));
    }

    public void setSpinnerStatus(){
        liststatus.add("ล้าง");
        liststatus.add("ฆ่าเชื้อ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, liststatus);
        pg_spinner.setAdapter(adapter);

        list_fill_RoundNumber.clear();
        list_fill_RoundNumber.add("-");
        for(int i=1;i<=50;i++){
            list_fill_RoundNumber.add(i+"");
        }
        round_spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, list_fill_RoundNumber));

    }

    public void getlistMac(final String xsel) {
        class getlistMac extends AsyncTask<String, Void, String> {
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
//                    ArrayList<String> list_fill_MachineName = new ArrayList<>();

                    list_fill_MachineID.clear();
//                    list_fill_MachineID.add("-");
                    list_fill_MachineID.add("-");

                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);

//                        list_fill_MachineID.add(c.getString("xId"));
                        list_fill_MachineID.add(c.getString("MachineName"));

                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ResultsActivity.this, android.R.layout.simple_dropdown_item_1line, list_fill_MachineID);
                    mac_spinner.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("xSel", xsel);
                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "get_machine_resulttest.php", data);
                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("tog_fill_mac","result : "+result);
                Log.d("tog_fill_mac","data : "+data);

                return result;
            }

            // =========================================================================================
        }
        getlistMac obj = new getlistMac();
        obj.execute();
    }

    public void getlistdata(final String xsel, final String Date) {
        class getlistdata extends AsyncTask<String, Void, String> {
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
                    if(xSel.equals("0")){
                        List<ModelDisplayDoc0_1> Model_RQ = new ArrayList<>();
                        for(int i=0;i<rs.length();i++){
                            JSONObject c = rs.getJSONObject(i);

                            boolean is_add = true;

                            if(!fill_MachineID.equals("-")){
                                if(!c.getString("WashMachineID").equals(fill_MachineID)){
                                    is_add = false;
                                }
                            }

                            if(!fill_RoundNumber.equals("-")){
                                if(!c.getString("WashRoundNumber").equals(fill_RoundNumber)){
                                    is_add = false;
                                }
                            }

                            Log.d("tog_fill_","DocNo : "+c.getString("DocNo")+"-"+is_add+"-"+c.getString("WashMachineID")+"-"+fill_MachineID+"-"+c.getString("WashRoundNumber")+"-"+fill_RoundNumber);
                            if(is_add){
                                Model_RQ.add(
                                        new ModelDisplayDoc0_1(
                                                c.getString("DocNo"),
                                                c.getString("WashTypeName"),
                                                c.getString("WashMachineID"),
                                                c.getString("WashRoundNumber"),
                                                c.getString("TestProgramName"),
                                                c.getString("IsActive"),
                                                c.getString("ID")
                                        )
                                );
                            }

                        }

                        ArrayAdapter<ModelDisplayDoc0_1> adapter;
                        adapter = new ListDisplayDocAdapter0_1(ResultsActivity.this, Model_RQ);
                        rq_listdoc.setAdapter(adapter);
                        rq_listdoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(ResultsActivity.this, InSertImageSporeDocActivity.class);
                                intent.putExtra("WashRoundNumber", Model_RQ.get(position).getWashRoundNumber());
                                intent.putExtra("TestProgramName", Model_RQ.get(position).getTestProgramName());
                                intent.putExtra("WashMachineID", Model_RQ.get(position).getWashMachineID());
                                intent.putExtra("IsActive", Model_RQ.get(position).getIsActive());
                                intent.putExtra("DocNo", Model_RQ.get(position).getDocNo());
                                intent.putExtra("ID", Model_RQ.get(position).getID());
                                intent.putExtra("page", page = "0");
                                intent.putExtra("EmpCode", EmpCode);
                                intent.putExtra("B_ID", B_ID);
                                intent.putExtra("Username", Username);
                                startActivity(intent);
                            }
                        });

                    }else{

                        List<ModelDisplayDoc1_1> Model_RQ = new ArrayList<>();
                        for(int i=0;i<rs.length();i++){
                            JSONObject c = rs.getJSONObject(i);

                            boolean is_add = true;

                            if(!fill_MachineID.equals("-")){
                                if(!c.getString("SterileMachineID").equals(fill_MachineID)){
                                    is_add = false;
                                }
                            }

                            if(!fill_RoundNumber.equals("-")){
                                if(!c.getString("SterileRoundNumber").equals(fill_RoundNumber)){
                                    is_add = false;
                                }
                            }
                            Log.d("tog_fill_","DocNo : "+c.getString("DocNo")+"-"+is_add+"-"+c.getString("SterileMachineID")+"-"+fill_MachineID+"-"+c.getString("SterileRoundNumber")+"-"+fill_RoundNumber);

                            if(is_add){
                                Model_RQ.add(
                                        new ModelDisplayDoc1_1(
                                                c.getString("DocNo"),
                                                c.getString("SterileName"),
                                                c.getString("SterileMachineID"),
                                                c.getString("SterileRoundNumber"),
                                                c.getString("TestProgramName"),
                                                c.getString("IsActive"),
                                                c.getString("ID")
                                        )
                                );
                            }

                        }
                        ArrayAdapter<ModelDisplayDoc1_1> adapter;
                        adapter = new ListDisplayDocAdapter1_1(ResultsActivity.this, Model_RQ);
                        rq_listdoc.setAdapter(adapter);
                        rq_listdoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                                Intent intent = new Intent(ResultsActivity.this, InSertImageSporeDocActivity.class);
                                intent.putExtra("SterileRoundNumber", Model_RQ.get(position).getSterileRoundNumber());
                                intent.putExtra("SterileMachineID", Model_RQ.get(position).getSterileMachineID());
                                intent.putExtra("TestProgramName", Model_RQ.get(position).getTestProgramName());
                                intent.putExtra("IsActive", Model_RQ.get(position).getIsActive());
                                intent.putExtra("DocNo", Model_RQ.get(position).getDocNo());
                                intent.putExtra("ID", Model_RQ.get(position).getID());
                                intent.putExtra("page", page = "1");
                                intent.putExtra("EmpCode", EmpCode);
//                            Log.d("KFLHDL",EmpCode);
                                intent.putExtra("B_ID", B_ID);
                                intent.putExtra("Username", Username);
                                startActivity(intent);
                            }
                        });
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("Date",Date);
                data.put("xSel", xsel);
                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                String result = null;
                try {
                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_spore_doc.php", data);
                    Log.d("BFGDH",result);
                    Log.d("BFGDH",data+"");
                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("tog_dis_spore","result : "+result);
                Log.d("tog_dis_spore","data : "+data);

                return result;
            }

            // =========================================================================================
        }
        getlistdata obj = new getlistdata();
        obj.execute();
    }

    public String convertdate(String date){
        String A_date[] = date.split("/");
        date="";
        for(int i=A_date.length-1;i>=0;i--){
            if(i!=0){
                date+=A_date[i]+"-";
            }else{
                date+=A_date[i];
            }
        }
        return date;
    }

    public static boolean isNullOrEmpty(String str) {
        if(str != null && !str.isEmpty())
            return false;
        return true;
    }

    @Override
    public void onBackPressed() {

        backpage.callOnClick();
//        AlertDialog.Builder quitDialog = new AlertDialog.Builder(ResultsActivity.this);
//        quitDialog.setTitle("CSSD");
//        quitDialog.setIcon(R.drawable.pose_favicon_2x);
//        quitDialog.setMessage("ยืนยันการออกจากโปรแกรม ?");
//        quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Intent intent = new Intent(ResultsActivity.this,MainMenu.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//        quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//            }
//        });
//        quitDialog.show();
    }
}
