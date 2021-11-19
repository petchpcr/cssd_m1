package com.poseintelligence.cssdm1;

import static java.lang.Class.forName;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.model.ConfigM1;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity implements View.OnClickListener {
    private String getUrl="";
    private TextView tName;
    private RelativeLayout R2;
    private RelativeLayout RR1;
    private RelativeLayout RR2;
    private RelativeLayout RR3;
    private RelativeLayout RR4;
    private RelativeLayout RR5;
    private RelativeLayout RR6;

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
        R2 = (RelativeLayout) findViewById(R.id.R2);
        RR1 = (RelativeLayout) findViewById(R.id.RR1);
        RR2 = (RelativeLayout) findViewById(R.id.RR2);
        RR3 = (RelativeLayout) findViewById(R.id.RR3);
        RR4 = (RelativeLayout) findViewById(R.id.RR4);
        RR5 = (RelativeLayout) findViewById(R.id.RR5);
        RR6 = (RelativeLayout) findViewById(R.id.RR6);
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
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.weight = 320;
                params.height = 320;
                IV.setLayoutParams(params);
                String uri = "@drawable/"+cM1.get(i).getBtImg();
                int imageResource = getResources().getIdentifier(uri, null, getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                IV.setImageDrawable( res );
                IV.setOnClickListener(clickInLinearLayout);
                IV.setTag(i);
                switch(n) {
                    case 0: RR1.addView(IV); break;
                    case 1: RR2.addView(IV); break;
                    case 2: RR3.addView(IV); break;
                    case 3: RR4.addView(IV); break;
                    case 4: RR5.addView(IV); break;
                }
                n++;
            }
        }
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