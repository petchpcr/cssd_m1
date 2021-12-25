package com.poseintelligence.cssdm1.adapter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_MachineTest.MachineTestDetailActivity;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.Item;
import com.poseintelligence.cssdm1.model.ModelChkMachine;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListItemBasketAdapter extends ArrayAdapter<Item> {
    private final ArrayList<Item> list;
    private final Activity context;
    int width=0;
    float d_x = 0;

    public ListItemBasketAdapter(Activity context, ArrayList<Item> list) {
        super(context, R.layout.activity_list_item_basket_adapter, list);
        this.context = context;
        this.list = list;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_list_item_basket_adapter, parent, false);

        HorizontalScrollView h_sc = (HorizontalScrollView) view.findViewById(R.id.h_scroll);
        TextView text_1 = (TextView) view.findViewById(R.id.text_1);
        TextView text_2 = (TextView) view.findViewById(R.id.text_2);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        LinearLayout bt_delete = (LinearLayout) view.findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SterileActivity)context).item_to_delete(position);
            }
        });

        text_1.setWidth(width-260);

        h_sc.setHorizontalScrollBarEnabled(false);

        text_1.setText(list.get(position).getName());
        text_2.setText(list.get(position).getItemCode());

        checkBox.setChecked(list.get(position).isChk());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                list.get(position).setChk(b);
                get_list_checkbox();
            }
        });
        Log.d("tog_chk","chk = "+position+"---"+list.get(position).isChk());

//        h_sc.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view,  int x, int y, int oldX, int oldY) {
//                Log.d("tog_OnScroll","x = "+x+" -- ox = "+oldX);
//                d_x = x;
//            }
//        });
//
//        h_sc.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//
//                if(motionEvent.getAction()==MotionEvent.ACTION_UP){
//                    if(d_x>70){
//                        h_sc.smoothScrollTo(200,0);
//                        Log.d("tog_OnScroll","R");
//                    }else{
//                        h_sc.smoothScrollTo(0,0);
//                        Log.d("tog_OnScroll","L");
//                    }
//                }
//                return false;
//            }
//        });


        return view;
    }

    public void get_list_checkbox(){
        ((SterileActivity)context).get_list_checkbox_to_delete();
    }
}