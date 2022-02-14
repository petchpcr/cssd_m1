package com.poseintelligence.cssdm1.Menu_MachineTest;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.MainMenu;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListChkMachineAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.ModelChkMachine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class MachineTestActivity extends AppCompatActivity {

    private String TAG_RESULTS = "result";
    private HTTPConnect httpConnect = new HTTPConnect();

    ImageView backpage;

    ListView rq_listdoc;

    TextView txt_date;

    ArrayList<ModelChkMachine> doc_list = new ArrayList<>();

    String getxUrl="";

    private Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_machine_test);

        getSupportActionBar().hide();

        byIntent();

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDate();
    }

    public void onBackPressed() {
        backpage.callOnClick();
    }

    public void byIntent() {
        getxUrl = ((CssdProject) getApplication()).getxUrl();
    }

    public void init() {

        rq_listdoc = (ListView) findViewById(R.id.rq_listdoc);

        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MachineTestActivity.this, MainMenu.class);
                startActivity(intent);
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view,
                                  int year,
                                  int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDate();
            }

        };

        txt_date = (TextView) findViewById(R.id.txt_date);

        txt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(MachineTestActivity.this,R.style.CustomDatePickerDialogTheme, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)){
                    @Override
                    protected void onCreate(Bundle savedInstanceState)
                    {
                        super.onCreate(savedInstanceState);
                        int day = getContext().getResources()
                                .getIdentifier("android:id/day", null, null);
                        if(day != 0){
                            View dayPicker = findViewById(day);
                            if(dayPicker != null){
                                dayPicker.setVisibility(View.GONE);
                            }
                        }
                    }


                };

                dpd.show();
            }
        });
    }

    private void updateDate() {

        String myFormat = "MMMM/yyyy"; //In which you need put here
//        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("th","th"));
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txt_date.setText(sdf.format(myCalendar.getTime()));

        get_doc();
    }

    public void get_doc() {
        class get_doc extends AsyncTask<String, Void, String> {
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

                    doc_list.clear();

                    for (int i = 0; i < setRs.length(); i++) {
                        JSONObject c = setRs.getJSONObject(i);
                        ModelChkMachine x = new ModelChkMachine(
                                c.getString("ID"),
                                c.getString("CreateDate"),
                                c.getString("LastUpdate"),
                                c.getString("MachineID"),
                                c.getString("MachineName"),
                                c.getString("IsResult"),
                                c.getString("Pic1"),
                                c.getString("Pic2"),
                                c.getString("Remark"),
                                c.getBoolean("enableEdit")
                        );

                        doc_list.add(x);
                    }

                    rq_listdoc.setAdapter(new ListChkMachineAdapter(MachineTestActivity.this, doc_list));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                String myFormat = "yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                String m = (myCalendar.getTime().getMonth()+1)+"";
                String y = sdf.format(myCalendar.getTime());
                Log.d("tog_date","date = "+m+"/"+y);

                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());
                data.put("slcMonth", m);
                data.put("slcYear", y);

                String result = httpConnect.sendPostRequest(getxUrl + "check_machine/get_machine_data.php", data);
                return result;
            }
        }
        get_doc ru = new get_doc();
        ru.execute();
    }
}