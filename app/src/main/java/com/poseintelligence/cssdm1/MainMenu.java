package com.poseintelligence.cssdm1;

import static java.lang.Class.forName;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_Dispensing.DispensingActivity;
import com.poseintelligence.cssdm1.Menu_MachineTest.MachineTestActivity;
import com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity;
import com.poseintelligence.cssdm1.Menu_RecordTest.ResultsActivity;
import com.poseintelligence.cssdm1.Menu_Remark.RemarkActivity;
import com.poseintelligence.cssdm1.Menu_Return.ReturnActivity;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.model.ConfigM1;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    private String getUrl="";
    private TextView tName;

    ArrayList<LinearLayout> list_menu = new ArrayList<>();

    private ImageView tExit;
    LinearLayout LX;
    private ArrayList<ConfigM1> cM1;
    private View.OnClickListener clickInLinearLayout;

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
//                    case "bt_dispensing": getoPage(SterileActivity.class); break;
                    case "bt_dispensing": getoPage(DispensingActivity.class); break;
                    case "bt_receive": getoPage(ReceiveActivity.class); break;
                    case "bt_remark": getoPage(RemarkActivity.class); break;
                    case "bt_results": getoPage(ResultsActivity.class); break;
                    case "bt_returnofcssd": getoPage(ReturnActivity.class); break;
                    case "bt_machine_test": getoPage(MachineTestActivity.class); break;
                    case "bt_sterile": getoPage(SterileActivity.class); break;
                }
            }
        };
        ShowMenu();
    }

    private void getoPage(Class gx){
        Intent intent = new Intent(MainMenu.this,gx);
        startActivity(intent);
        finish();
    }

    private void ShowMenu() {
        int n=0;
        for(int i=0;i<cM1.size();i++) {
            if (cM1.get(i).getShowBtn()) {
                ImageView IV = new ImageView(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 150);
                params.setMargins(20,55,20,35);
//                params.weight = 150;
//                params.height = 150;
                IV.setLayoutParams(params);
                String uri = "@drawable/"+cM1.get(i).getBtImg();
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                IV.setImageDrawable( res );

                Log.d("tog_menu","getDrawable : " +cM1.get(i).getBtImg());

                menu_addView(i,n,IV);
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
            case "bt_sterile": t.setText("ฆ่าเชื้อ"); break;
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