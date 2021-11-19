package com.poseintelligence.cssdm1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.ReceiveActivity;
import com.poseintelligence.cssdm1.model.pCustomer;


import java.util.ArrayList;

public class ListSendSterileDetailSetAdapter extends ArrayAdapter {

    private ArrayList<pCustomer> listData;
    private AppCompatActivity aActivity;
    String CheckAllAd = "1";

    public boolean SS_IsUsedRemarks = false;
    
    public ListSendSterileDetailSetAdapter(AppCompatActivity aActivity, ArrayList<pCustomer> listData) {
        super(aActivity, 0, listData);
        this.aActivity= aActivity;
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
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) aActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.list_send_sterile_detail_set, parent, false);

        TextView txtitemname = (TextView) v.findViewById(R.id.itemname);
        TextView txtxqty = (TextView) v.findViewById(R.id.xqty);
        CheckBox chk_remark = (CheckBox) v.findViewById(R.id.chk_remark);

        SS_IsUsedRemarks = ((CssdProject) aActivity.getApplication()).isSS_IsUsedRemarks();

        if (SS_IsUsedRemarks){
            chk_remark.setVisibility(View.VISIBLE);
        }else {
            chk_remark.setVisibility(View.GONE);
        }

        txtitemname.setPaintFlags(txtitemname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtitemname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReceiveActivity)aActivity).LoadImg(listData.get(position).getItemcode(),"2",listData.get(position).getUsageCode(),listData.get(position).getItemname(),"noremark");
            }
        });

        if (!listData.get(position).getRemarkAdmin().equals("0")){
            chk_remark.setChecked(false);
            if (SS_IsUsedRemarks){
                txtitemname.setTextColor(Color.RED);
                txtxqty.setTextColor(Color.RED);
            }else {
                txtitemname.setTextColor(Color.BLACK);
                txtxqty.setTextColor(Color.BLACK);
            }
        }else {
            chk_remark.setChecked(true);
        }

        chk_remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listData.get(position).getRemarkAdmin().equals("0")){
                    if (chk_remark.isChecked()){
                        ((ReceiveActivity)aActivity).callDialog(listData.get(position).getItemname(),"0",listData.get(position).getXqty(),listData.get(position).getQtyItemDetail());
                    }else {
                        ((ReceiveActivity)aActivity).callDialog(listData.get(position).getItemname(),"0",listData.get(position).getXqty(),listData.get(position).getQtyItemDetail());
                    }
                }
            }
        });

        txtitemname.setText(listData.get(position).getItemname());
        txtxqty.setText( listData.get(position).getXqty() );

        return v;
    }

}