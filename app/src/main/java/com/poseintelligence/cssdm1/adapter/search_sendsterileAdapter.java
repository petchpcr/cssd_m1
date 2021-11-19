package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.Response_Aux_itemstock;

import java.util.ArrayList;

public class search_sendsterileAdapter extends ArrayAdapter {

    private ArrayList<Response_Aux_itemstock> listData ;
    private Activity context;

    public search_sendsterileAdapter(Activity aActivity, ArrayList<Response_Aux_itemstock> listData) {
        super(aActivity, 0, listData);
        this.context = aActivity;
        this.listData = listData;
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
        final View v = inflater.inflate(R.layout.list_search_sendsterile, parent, false);

        final TextView tFields1 = (TextView) v.findViewById(R.id.txt_itemcode);
        final TextView tFields2 = (TextView) v.findViewById(R.id.txt_desc);
        final EditText EditNum1 = (EditText) v.findViewById(R.id.etxt_qty);

        //tFields1.setText(listData.get(position).getFields2() + " : " + listData.get(position).getFields9() );
        tFields1.setText(listData.get(position).getFields9() );
        tFields2.setText(listData.get(position).getxFields15() + " วัน");
        EditNum1.setText( listData.get(position).getFields6() );

        /*
        System.out.println(listData.get(position).getFields1());
        System.out.println(listData.get(position).getFields2());
        System.out.println(listData.get(position).getFields3());
        System.out.println(listData.get(position).getFields4());
        System.out.println(listData.get(position).getFields5());
        System.out.println(listData.get(position).getFields6());
        System.out.println(listData.get(position).getFields7());
        System.out.println(listData.get(position).getFields8());
        System.out.println(listData.get(position).getFields9());
        */

        EditNum1.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus){
                if (hasFocus)
                    ((EditText)v).selectAll();

            }
        });

        EditNum1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                listData.get(position).setFields6( EditNum1.getText()+"" );
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        return v;
    }

}
