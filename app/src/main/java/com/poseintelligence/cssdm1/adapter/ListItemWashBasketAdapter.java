package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ItemInBasket;

import java.util.ArrayList;

public class ListItemWashBasketAdapter extends ArrayAdapter<ItemInBasket> {
    private final ArrayList<ItemInBasket> list;
    private final Activity context;
    int width=0;
    float d_x = 0;

    public ListItemWashBasketAdapter(Activity context, ArrayList<ItemInBasket> list) {
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
                ((BasketWashingActivity)context).item_to_delete(list.get(position).getRow_id()+",",list.get(position).getWashDetailID() + "@" + list.get(position).getSSDetailID() + "@" + list.get(position).getItemStockID());
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

        return view;
    }

    public void get_list_checkbox(){
        ((BasketWashingActivity)context).get_list_checkbox_to_delete();
    }
}
