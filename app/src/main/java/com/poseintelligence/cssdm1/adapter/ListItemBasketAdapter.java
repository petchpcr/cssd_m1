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

import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ItemInBasket;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListItemBasketAdapter extends ArrayAdapter<ItemInBasket> {
    private final ArrayList<ItemInBasket> list;
    private final Activity context;

    boolean mac_is_working = false;
    int width=0;

    public ListItemBasketAdapter(Activity context, ArrayList<ItemInBasket> list) {
        super(context, R.layout.activity_list_item_basket_adapter, list);
        this.context = context;
        this.list = list;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        mac_is_working = ((SterileActivity)context).mac_is_working;
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
                ((SterileActivity)context).item_to_delete(list.get(position).getRow_id()+",",list.get(position).getSterileDetailID() + "@" + list.get(position).getWashDetailID() + "@" + list.get(position).getItemStockID());
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

        if(mac_is_working){
            checkBox.setVisibility(View.GONE);
            bt_delete.setVisibility(View.GONE);
        }

        return view;
    }

    public void get_list_checkbox(){
        ((SterileActivity)context).get_list_checkbox_to_delete();
    }
}