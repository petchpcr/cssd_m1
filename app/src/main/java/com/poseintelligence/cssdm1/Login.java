package com.poseintelligence.cssdm1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ConfigM1;
import com.poseintelligence.cssdm1.model.Parameter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class Login extends AppCompatActivity {
    String getUrl="";
    static final int READ_BLOCK_SIZE = 100;
    static final String FILE_CONFIG = "config.txt";

    private EditText uname;
    private EditText pword;
    private LinearLayout form;
    private ImageView submit;
    private Spinner spinner_building;
    private ImageView imageView3;
    private ImageView iSetting;
    private TextView textView3;
    private TextView building_name;
    private View.OnClickListener clickInLinearLayout;

    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect http = new HTTPConnect();

    ArrayList<ConfigM1> config_m1 = new ArrayList<>();

    //Check if internet is present or not
    private boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        try {
            if( ReadFile().length() == 0 ){
                //Do something
                pDialog();
            } else {
                //Nothing
                getUrl =  ReadFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        byWidget();

        byEvent();

        onLoadConfiguration();
        get_building_name();
    }

    // ===============================================
    // RW File
    // ===============================================
    public void WriteFile(String Url ){
        try {
            FileOutputStream fileout=openFileOutput(FILE_CONFIG, MODE_PRIVATE);
            OutputStreamWriter outputWriter=new OutputStreamWriter(fileout);
            outputWriter.write( Url );
            outputWriter.close();
            //display file saved message
            Toast.makeText(getBaseContext(),  "File saved successfully!",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_custom, null);
        builder.setView(view);
        final EditText url = (EditText) view.findViewById(R.id.Url);
        LinearLayout LL = (LinearLayout) view.findViewById(R.id.LL);
        LinearLayout LX;
        url.setText( getUrl );
//        url.setText( "http://poseintelligence.dyndns.biz:8888/cssd_2_us/" );
        for(int i=0;i<config_m1.size();i++){
            if(config_m1.get(i).getStatus()) {
//                Log.d("OOOO",config_m1.get(i).getCngName());
                LX = new LinearLayout(this);
                LinearLayout L1 = new LinearLayout(this);
                LinearLayout L2 = new LinearLayout(this);
                TextView valueTV  = new TextView( this );
                Switch valueSw = new Switch( this );

                valueTV.setText(config_m1.get(i).getCngName());
                valueTV.setId(i);

                valueSw.setChecked( config_m1.get(i).getActive() );

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                LX.setOrientation(LinearLayout.HORIZONTAL);
                LX.setLayoutParams( params );

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params1.weight = 0.2f;
                params1.setMargins(15, 0, 0, 0);
                params1.gravity= Gravity.CENTER_VERTICAL;
                L1.setLayoutParams( params1 );

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params2.weight = 0.8f;
                params2.gravity= Gravity.RIGHT;
                L2.setLayoutParams( params2 );

                L1.addView(valueTV);
                L2.addView(valueSw);

                LX.addView(L1);
                LX.addView(L2);

                LL.addView(LX);

                valueSw.setTag(Integer.toString(i));
                valueSw.setOnClickListener(clickInLinearLayout);

            }
        }

        builder.setPositiveButton("บันทึก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Check username password
                if ( !url.getText().equals("") ) {
                    getUrl = url.getText().toString();
                    ((CssdProject) getApplication()).setxUrl(getUrl);
                    WriteFile( getUrl );
                    String configm1="";
                    for(int i=0;i<config_m1.size();i++){
                        configm1 += config_m1.get(i).getCngId()+","+config_m1.get(i).getActive()+":";
                    }
//                    Log.d("OOOO",configm1.substring(0,configm1.length()-1));
                    finish();
                    startActivity(getIntent());
                }
            }
        });
        builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    public String ReadFile(){
        try {
            FileInputStream fileIn=openFileInput(FILE_CONFIG);
            InputStreamReader InputRead= new InputStreamReader(fileIn);
            char[] inputBuffer= new char[READ_BLOCK_SIZE];
            String s="";
            int charRead;

            while ((charRead=InputRead.read(inputBuffer))>0) {
                // char to string conversion
                String readstring=String.copyValueOf(inputBuffer,0,charRead);
                s +=readstring;
            }
            InputRead.close();
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    // ===============================================

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void byWidget() {
        textView3 = (TextView) findViewById(R.id.textView3);
        iSetting = (ImageView) findViewById(R.id.iSetting);
        spinner_building = (Spinner) findViewById(R.id.spinner_building);
        uname = (EditText) findViewById(R.id.txt_username);
        pword = (EditText) findViewById(R.id.txt_password);
        submit = (ImageView) findViewById(R.id.button_login);
        imageView3 = (ImageView) findViewById(R.id.QrCode);
        form = (LinearLayout) findViewById(R.id.form);
        building_name = (TextView) findViewById(R.id.building_name);
    }

    private void byEvent(){
            uname.setText("");
            pword.setText("");

        clickInLinearLayout = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer position = Integer.parseInt(v.getTag().toString());
//                Log.d("OOOO","Clicked item at position: " + position + " : " + config_m1.get(position).getActive());

                config_m1.get( position ).setActive( config_m1.get(position).getActive()==true?false:true );
//                Log.d("OOOO","Clicked item at position: " + position + " : " + config_m1.get(position).getActive());
                set_config_m1( config_m1.get(position).getCngId(),config_m1.get(position).getActive() );
            }
        };

        iSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog();
            }
        });

        form.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focus();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dep_device();
                try {
                    onLogin(uname.getText().toString(), pword.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        pword.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            try {
                                onLogin(uname.getText().toString(), pword.getText().toString());
                            } catch (Exception e) {
                                Toast.makeText(Login.this, "ข้อมูลไม่ถูกต้อง !!", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                                focus();
                            }

                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });

        uname.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            final String txt = uname.getText().toString();

                            if(!uname.getText().equals("") && pword.getText().equals("")){
                                pword.requestFocus();
                            }

                            return true;
                        default:
                            break;
                    }
                }

                return false;
            }
        });
    }

    private void focus(){
        pword.setText("");
        uname.setText("");

        uname.requestFocus();
    }

    public void onLogin(final String uname, final String pword) {
        class onLogin extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(Login.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setIcon(R.drawable.pose_favicon_2x);
                dialog.setMessage(Cons.WAIT_FOR_AUTHENTICATION);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (!c.getString("id").equals("false")) {
                            Intent intent = new Intent(Login.this,MainMenu.class);
                            Parameter pm = new Parameter();
                            pm.setUserid( c.getInt("id") );
                            pm.setName( c.getString("username") );
                            pm.setLang( c.getString("Lang") );
                            pm.setIsAdmin( c.getString("IsAdmin" ).equals("0")?false:true );
                            pm.setEmCode( c.getString("EmpCode" ) );
                            pm.setEmName( c.getString("EmpName" ) );
                            pm.setBdCode( 1 );
                            pm.setBdName( "-" );
                            pm.setIsSU( c.getBoolean("IsSU" ) );

                            ((CssdProject) getApplication()).setxUrl(getUrl);
                            ((CssdProject) getApplication()).setPm( pm );
                            ((CssdProject) getApplication()).setcM1( config_m1 );
                            startActivity(intent);
//                            Toast.makeText(Login.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                } catch (JSONException e) {
//                    Toast.makeText(Login.this, Cons.WARNING_CONNECT_SERVER_FAIL, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("uname", uname);
                data.put("pword", pword);
                Log.d("tog_login","data = "+data);
                String result = http.sendPostRequest(getUrl + "Login/get_login.php", data);
                Log.d("tog_login","result = "+result);
                return result;
            }
        }

        onLogin ru = new onLogin();
        ru.execute();
    }

    public void get_config_m1() {
        class on_get_config_m1 extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(Login.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setIcon(R.drawable.pose_favicon_2x);
                dialog.setMessage(Cons.WAIT_FOR_AUTHENTICATION);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
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
                            ConfigM1 m1 = new ConfigM1();
                            m1.setCngId( c.getString("CngId") );
                            m1.setCngName( c.getString("CngName") );
                            m1.setCngComment( c.getString("CngComment") );
                            m1.setBtImg( c.getString("imgBtn") );
                            m1.setActive( c.getString("isActive").equals("1")?true:false );
                            m1.setStatus( c.getString("isStatus").equals("1")?true:false );
                            m1.setShowBtn( c.getString("showBtn").equals("1")?true:false );
                            config_m1.add( m1 );
                        }
                    }
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
                HashMap<String, String> data = new HashMap<String, String>();
//                Log.d("OOOO",getUrl + "cssd_display_config_M1.php?"+data);
                String result = http.sendPostRequest(getUrl + "cssd_display_config_M1.php", data);
//                Log.d("OOOO",result);
                return result;
            }
        }

        on_get_config_m1 ru = new on_get_config_m1();
        ru.execute();
    }

    public void set_config_m1(final String idx,final boolean chk) {
        class on_set_config_m1 extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(Login.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setIcon(R.drawable.pose_favicon_2x);
                dialog.setMessage(Cons.WAIT_FOR_AUTHENTICATION);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                String Message="";
                try {
                    JSONObject jsonObj = new JSONObject(s);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);
                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("result").equals("A")) {
                            Message = c.getString("Message");
                        }
                    }
                    Toast.makeText(Login.this, Message, Toast.LENGTH_SHORT).show();
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
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("chk", chk?"1":"0");
                data.put("idx", idx+"");
//                Log.d("OOOO",getUrl + "cssd_update_config_M1.php?"+data);
                String result = http.sendPostRequest(getUrl + "cssd_update_config_M1.php", data);
//                Log.d("OOOO",result);
                return result;
            }
        }

        on_set_config_m1 ru = new on_set_config_m1();
        ru.execute();
    }

    public void onLoadConfiguration() {
        class LoadConfiguration extends AsyncTask<String, Void, String> {

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

//                            ((CssdProject) getApplication()).setD_DATABASE(c.getString("p_DB"));
                            ((CssdProject) getApplication()).setMN_IsUsedFormNonUsage(c.getBoolean("MN_IsUsedFormNonUsage"));
                            ((CssdProject) getApplication()).setMN_IsUsedComputeDate(c.getBoolean("MN_IsUsedComputeDate"));
                            ((CssdProject) getApplication()).setMN_IsUsedReceiveDevice(c.getBoolean("MN_IsUsedReceiveDevice"));

                            ((CssdProject) getApplication()).setMD_IsUsedSoundScanQR(c.getBoolean("MD_IsUsedSoundScanQR"));
                            ((CssdProject) getApplication()).setMD_IsAutoItemCode(c.getBoolean("MD_IsAutoItemCode"));
                            ((CssdProject) getApplication()).setMD_IsItemPriceCode(c.getBoolean("MD_IsItemPriceCode"));
                            ((CssdProject) getApplication()).setMD_IsUsedItemCode2(c.getBoolean("MD_IsUsedItemCode2"));

                            ((CssdProject) getApplication()).setSS_IsUsedItemSet(c.getBoolean("SS_IsUsedItemSet"));
                            ((CssdProject) getApplication()).setSS_IsCopyPayout(c.getBoolean("SS_IsCopyPayout"));
                            ((CssdProject) getApplication()).setSS_IsShowSender(c.getBoolean("SS_IsShowSender"));
                            ((CssdProject) getApplication()).setSS_IsReceiverDropdown(c.getBoolean("SS_IsReceiverDropdown"));
                            ((CssdProject) getApplication()).setSS_IsApprove(c.getBoolean("SS_IsApprove"));
                            ((CssdProject) getApplication()).setSS_IsUsedItemDepartment(c.getBoolean("SS_IsUsedItemDepartment"));
                            ((CssdProject) getApplication()).setSS_IsUsedReceiveTime(c.getBoolean("SS_IsUsedReceiveTime"));
                            ((CssdProject) getApplication()).setSS_IsNonSelectDepartment(c.getBoolean("SS_IsNonSelectDepartment"));
                            ((CssdProject) getApplication()).setSS_IsUsedBasket(c.getBoolean("SS_IsUsedBasket"));
                            ((CssdProject) getApplication()).setSS_IsUsedNotification(c.getBoolean("SS_IsUsedNotification"));
                            ((CssdProject) getApplication()).setSS_IsUsedRemarks(c.getBoolean("SS_IsUsedRemarks"));
                            ((CssdProject) getApplication()).setSS_IsUsedSelfWashDepartment(c.getBoolean("SS_IsUsedSelfWashDepartment"));
                            ((CssdProject) getApplication()).setSS_IsGroupPayout(c.getBoolean("SS_IsGroupPayout"));
                            ((CssdProject) getApplication()).setSS_IsSortByUsedCount(c.getBoolean("SS_IsSortByUsedCount"));
                            ((CssdProject) getApplication()).setSS_IsUsedZoneSterile(c.getBoolean("SS_IsUsedZoneSterile"));
                            ((CssdProject) getApplication()).setSS_IsUsedClosePayout(c.getBoolean("SS_IsUsedClosePayout"));
                            ((CssdProject) getApplication()).setSS_IsUsedChangeDepartment(c.getBoolean("SS_IsUsedChangeDepartment"));

                            ((CssdProject) getApplication()).setSR_IsUsedPreparer(c.getBoolean("SR_IsUsedPreparer"));
                            ((CssdProject) getApplication()).setSR_IsUsedApprover(c.getBoolean("SR_IsUsedApprover"));
                            ((CssdProject) getApplication()).setSR_IsUsedPacker(c.getBoolean("SR_IsUsedPacker"));
                            ((CssdProject) getApplication()).setSR_IsUsedSteriler(c.getBoolean("SR_IsUsedSteriler"));
                            ((CssdProject) getApplication()).setSR_IsUsedDBUserOperation(c.getBoolean("SR_IsUsedDBUserOperation"));
                            ((CssdProject) getApplication()).setSR_IsUsedDropdownUserOperation(c.getBoolean("SR_IsUsedDropdownUserOperation"));
                            ((CssdProject) getApplication()).setSR_IsRememberUserOperation(c.getBoolean("SR_IsRememberUserOperation"));
                            ((CssdProject) getApplication()).setSR_IsEditRound(c.getBoolean("SR_IsEditRound"));
                            ((CssdProject) getApplication()).setSR_IsUsedOccupancyRate(c.getBoolean("SR_IsUsedOccupancyRate"));
                            ((CssdProject) getApplication()).setSR_IsUsedUserOperationDetail(c.getBoolean("SR_IsUsedUserOperationDetail"));
                            ((CssdProject) getApplication()).setSR_IsApproveSterile(c.getBoolean("SR_IsApproveSterile"));
                            ((CssdProject) getApplication()).setSR_IsShowFormCheckList(c.getBoolean("SR_IsShowFormCheckList"));
                            ((CssdProject) getApplication()).setSR_IsUsedImportNonReuse(c.getBoolean("SR_IsUsedImportNonReuse"));
                            ((CssdProject) getApplication()).setSR_IncExp(c.getBoolean("SR_IncExp"));
                            ((CssdProject) getApplication()).setSR_Is_Preview_Print_Sticker(c.getBoolean("SR_Is_Preview_Print_Sticker"));
                            ((CssdProject) getApplication()).setSR_Is_Preview_Print_Sticker(c.getBoolean("SR_Is_NonSelectRound"));
                            ((CssdProject) getApplication()).setSR_IsEditSterileProgram(c.getBoolean("SR_IsEditSterileProgram"));
                            ((CssdProject) getApplication()).setSR_IsNotApprove(c.getBoolean("SR_IsNotApprove"));
                            ((CssdProject) getApplication()).setSR_IsUsedNotification(c.getBoolean("SR_IsUsedNotification"));
                            ((CssdProject) getApplication()).setSR_ReceiveFromDeposit(c.getBoolean("SR_ReceiveFromDeposit"));

                            ((CssdProject) getApplication()).setPA_IsUsedRecipienter(c.getBoolean("PA_IsUsedRecipienter"));
                            ((CssdProject) getApplication()).setPA_IsUsedApprover(c.getBoolean("PA_IsUsedApprover"));
                            ((CssdProject) getApplication()).setPA_IsConfirmClosePayout(c.getBoolean("PA_IsConfirmClosePayout"));
                            ((CssdProject) getApplication()).setPA_IsUsedDepartmentQR(c.getBoolean("PA_IsUsedDepartmentQR"));
                            ((CssdProject) getApplication()).setPA_DefaultDepartmentQR(c.getBoolean("PA_DefaultDepartmentQR"));
                            ((CssdProject) getApplication()).setPA_IsEditManualPayoutQty(c.getBoolean("PA_IsEditManualPayoutQty"));
                            ((CssdProject) getApplication()).setPA_IsCreateReceiveDepartment(c.getBoolean("PA_IsCreateReceiveDepartment"));
                            ((CssdProject) getApplication()).setPA_IsUsedZonePayout(c.getBoolean("PA_IsUsedZonePayout"));
                            ((CssdProject) getApplication()).setPA_IsShowBorrowNotReturn(c.getBoolean("PA_IsShowBorrowNotReturn"));
                            ((CssdProject) getApplication()).setPA_IsUsedFIFO(c.getBoolean("PA_IsUsedFIFO"));

                            ((CssdProject) getApplication()).setWA_IsUsedWash(c.getBoolean("WA_IsUsedWash"));
                            ((CssdProject) getApplication()).setWA_IsUsedNotification(c.getBoolean("WA_IsUsedNotification"));

                            ((CssdProject) getApplication()).setST_IsUsedNotification(c.getBoolean("ST_IsUsedNotification"));

                            ((CssdProject) getApplication()).setAP_NotApproveReturnToPreviousProcess(c.getInt("AP_NotApproveReturnToPreviousProcess"));
                            ((CssdProject) getApplication()).setAP_AddRickReturnToPreviousProcess(c.getInt("AP_AddRickReturnToPreviousProcess"));
                            ((CssdProject) getApplication()).setAP_IsUsedNotification(c.getBoolean("AP_IsUsedNotification"));
                            ((CssdProject) getApplication()).setAP_UsedScanForApprove(c.getBoolean("AP_UsedScanForApprove"));

                            get_config_m1();

                        }else{
                            Toast.makeText(Login.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(Login.this, Cons.WARNING_SEARCH_NOT_FOUND, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = http.sendPostRequest(getUrl + "cssd_select_configurations.php", data);

                Log.d("tog_LoadConfig","data = "+data);
                Log.d("tog_LoadConfig","result = "+result);

                return result;
            }

        }

        LoadConfiguration ru = new LoadConfiguration();
        ru.execute();
    }

    public void get_building_name() {
        class get_building_name extends AsyncTask<String, Void, String> {

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

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        building_name.setText(c.getString("building_name"));
                    }

                } catch (JSONException e) {
                    Toast.makeText(Login.this, Cons.WARNING_SEARCH_NOT_FOUND, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = http.sendPostRequest(getUrl + "building_name.php", data);

                Log.d("tog_building_name","getUrl = "+getUrl + "building_name.php");
                Log.d("tog_building_name","data = "+data);
                Log.d("tog_building_name","result = "+result);

                return result;
            }

        }

        get_building_name ru = new get_building_name();
        ru.execute();
    }


    public static String getSerialNumber() {
        String serialNumber;

        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);

            serialNumber = (String) get.invoke(c, "gsm.sn1");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ril.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "ro.serialno");
            if (serialNumber.equals(""))
                serialNumber = (String) get.invoke(c, "sys.serialnumber");
            if (serialNumber.equals(""))
                serialNumber = Build.SERIAL;

            // If none of the methods above worked
            if (serialNumber.equals(""))
                serialNumber = null;
        } catch (Exception e) {
            e.printStackTrace();
            serialNumber = null;
        }

        Log.d("serialNumber","serialNumber = "+serialNumber);
        return serialNumber;
    }

    public void dep_device(){
        if(getSerialNumber().equals("L203P85U01743")){
            onLogin("user1", "111");


        }
    }
}