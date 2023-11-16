package com.poseintelligence.cssdm1;

import static java.lang.Class.forName;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.Menu_Dispensing.DispensingActivity;
import com.poseintelligence.cssdm1.Menu_MachineTest.MachineTestActivity;
import com.poseintelligence.cssdm1.Menu_Re_Pay_NonUsage.ReceivePayNonUsageActivity;
import com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity;
import com.poseintelligence.cssdm1.Menu_RecordTest.ResultsActivity;
import com.poseintelligence.cssdm1.Menu_Remark.RemarkActivity;
import com.poseintelligence.cssdm1.Menu_Return.ReturnActivity;
import com.poseintelligence.cssdm1.Menu_Signature_Department.SignatureDepartmentActivity;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ConfigM1;
import com.poseintelligence.cssdm1.model.ModelDisplayDoc0_1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    private String getUrl="";
    private HTTPConnect http = new HTTPConnect();
    private TextView tName;

    ArrayList<LinearLayout> list_menu = new ArrayList<>();

    private ImageView tExit;
    LinearLayout LX;
    private ArrayList<ConfigM1> cM1;
    private View.OnClickListener clickInLinearLayout;

    HashMap<String, String> user_menu = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        byWidget();
        byEvent();

//        Log.d("OOOO","Url : " + getUrl);
//        Log.d("OOOO","Userid : " + ((CssdProject) getApplication()).getPm().getUserid());
    }

    private void byWidget() {
        tExit = (ImageView) findViewById(R.id.tExit);
        tName = (TextView) findViewById(R.id.tName);

        list_menu.add((LinearLayout) findViewById(R.id.RR1));
        list_menu.add((LinearLayout) findViewById(R.id.RR2));
        list_menu.add((LinearLayout) findViewById(R.id.RR3));
        list_menu.add((LinearLayout) findViewById(R.id.RR4));
        list_menu.add((LinearLayout) findViewById(R.id.RR5));
        list_menu.add((LinearLayout) findViewById(R.id.RR6));
        list_menu.add((LinearLayout) findViewById(R.id.RR7));
        list_menu.add((LinearLayout) findViewById(R.id.RR8));
        list_menu.add((LinearLayout) findViewById(R.id.RR9));
    }

    private void byEvent() {
        getUrl = ((CssdProject) getApplication()).getxUrl();
        tName.setText( ((CssdProject) getApplication()).getPm().getEmName() );
        cM1 = ((CssdProject) getApplication()).getcM1();
        tExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(MainMenu.this,Login.class);
                startActivity(intent);
                finish();
            }
        });

        clickInLinearLayout = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Integer position = Integer.parseInt(v.getTag().toString());
//                Log.d("OOOO","Clicked item at position: " + position + " : " + cM1.get(position).getCngName());
                switch(cM1.get(position).getBtImg()) {
                    case "bt_dispensing": getoPage(DispensingActivity.class); break;
                    case "bt_receive": getoPage(ReceiveActivity.class); break;
                    case "bt_remark": getoPage(RemarkActivity.class); break;
                    case "bt_results": getoPage(ResultsActivity.class); break;
                    case "bt_returnofcssd": getoPage(ReturnActivity.class); break;
                    case "bt_machine_test": getoPage(MachineTestActivity.class); break;
                    case "bt_sterile": getoPage(SterileActivity.class); break;
                    case "bt_basket_washing": getoPage(BasketWashingActivity.class); break;
                    case "bt_nonusage": getoPage(ReceivePayNonUsageActivity.class); break;
                    case "bt_signature_dept": getoPage(SignatureDepartmentActivity.class); break;
                }
            }
        };

        get_ShowMenu_user();
    }

    private void getoPage(Class gx){
        Intent intent = new Intent(MainMenu.this,gx);
        startActivity(intent);
        finish();
    }

    public void get_ShowMenu_user() {
        class ShowMenu extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(MainMenu.this);

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
                    JSONObject c = new JSONObject(s);
                    for(int i=0;i<cM1.size();i++) {
                        if (((CssdProject) getApplication()).Project().equals("VCH")){
                            user_menu.put(cM1.get(i).getBtImg(),c.getString(cM1.get(i).getBtImg()));
                        }else{
                            if(!c.isNull(cM1.get(i).getBtImg())){
                                user_menu.put(cM1.get(i).getBtImg(),c.getString(cM1.get(i).getBtImg()));
                            }
                        }
                    }

                } catch (JSONException e) {
                    Log.d("tog_menu","e : " +e);
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                    ShowMenu();
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("userid", ((CssdProject) getApplication()).getPm().getUserid()+"");
                String result = http.sendPostRequest(getUrl + "cssd_display_config_M1_user.php", data);

                Log.d("tog_menu","data = "+data);
                Log.d("tog_menu","result = "+result);

//                Toast.makeText(MainMenu.this, "data = "+data, Toast.LENGTH_LONG).show();
                return result;
            }
        }

        ShowMenu ru = new ShowMenu();
        ru.execute();
    }

    public void ShowMenu() {

        int n=0;
        for(int i=0;i<cM1.size();i++) {
            if (cM1.get(i).getShowBtn()) {
                ImageView IV = new ImageView(MainMenu.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
                params.setMargins(20,55,20,35);
                IV.setLayoutParams(params);
                String uri = "@drawable/"+cM1.get(i).getBtImg();
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                IV.setImageDrawable( res );

                Log.d("tog_menu","getDrawable : " +cM1.get(i).getBtImg());

                if(user_menu.isEmpty()){
                    menu_addView(i,n,IV);
                }else{
                    if (user_menu.get(cM1.get(i).getBtImg()).equals("1")) {
                        menu_addView(i,n,IV);
                    }
                }

//                Toast.makeText(MainMenu.this, "data = "+data, Toast.LENGTH_LONG).show();
                n++;
            }
        }
    }

    public void menu_addView(int position,int pos,View v) {

        list_menu.get(pos).setVisibility(View.VISIBLE);
        TextView t = new TextView(MainMenu.this);
        switch(cM1.get(position).getBtImg()) {
            case "bt_receive": t.setText("บันทึกรับ"); break;
            case "bt_dispensing": t.setText("บันทึกจ่าย"); break;
            case "bt_remark": t.setText("บันทึกรูปหมายเหตุ"); break;
            case "bt_results": t.setText("บันทึกผล"); break;
            case "bt_returnofcssd": t.setText("คืนของ\nเข้าสต๊อกจ่ายกลาง"); break;
            case "bt_machine_test": t.setText("เก็บข้อมูล\nตรวจสอบเครื่อง"); break;
            case "bt_sterile": t.setText("นำเข้าตะกร้า-เครื่องฆ่าเชื้อ"); break;
            case "bt_basket_washing": t.setText("นำเข้าตะกร้า-เครื่องล้าง"); break;
            case "bt_nonusage": t.setText("รับ-จ่าย รายการ non-usage"); break;
            case "bt_signature_dept": t.setText("ลงชื่อผู้รับแผนก"); break;
        }

        t.setGravity(Gravity.CENTER);
//        t.setGravity(Gravity.TOP);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 70);
        params.setMargins(10,5,10,0);
        t.setLayoutParams(params);
        t.setTypeface(null, Typeface.BOLD);
        list_menu.get(pos).addView(v);
        list_menu.get(pos).addView(t);
        list_menu.get(pos).setOnClickListener(clickInLinearLayout);
        list_menu.get(pos).setTag(position);
        list_menu.get(pos).setPadding(25,25,25,25);
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        Log.d("OOOO","Clicked item at : " + v.getId());
    }

    @Override
    public void onBackPressed() {


    }
}