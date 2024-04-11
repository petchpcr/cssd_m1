package com.poseintelligence.cssdm1.Menu_Sterile.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;

import java.util.ArrayList;
import java.util.HashMap;

public class itemAdapter extends ArrayAdapter {
    private ArrayList<String> listData ;
    private HashMap<String, String> listDataDetail ;
    String color;
    private Activity context;
    public itemAdapter(Activity aActivity, ArrayList<String> listData,HashMap<String, String> listDataDetail) {
        super(aActivity, 0, listData);
        this.context = aActivity;
        this.listData = listData;
        this.listDataDetail = listDataDetail;
        this.color = color;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.list_item_to_add_sterile, parent, false);

        final TextView tFields1 = (TextView) v.findViewById(R.id.textView1);
        final TextView tFields2 = (TextView) v.findViewById(R.id.textView2);

        tFields1.setText(listData.get(position));
        tFields2.setText(listDataDetail.get(listData.get(position)));

        if(listDataDetail.get(listData.get(position)).equals("เพิ่มสำเร็จ")){

            tFields1.setTextColor(Color.GREEN);
            tFields2.setTextColor(Color.GREEN);
        }else{
            if(!listDataDetail.get(listData.get(position)).equals("loading...")){
                tFields1.setTextColor(Color.RED);
                tFields2.setTextColor(Color.RED);
            }
        }

        return v;
    }

}
