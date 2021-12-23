package com.poseintelligence.cssdm1.adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.pCustomer;

import java.util.ArrayList;

public class ListSendSterileAdapter extends ArrayAdapter {

    private ArrayList<pCustomer> listData;
    private AppCompatActivity aActivity;
    private HTTPConnect httpConnect = new HTTPConnect();
    private String B_ID = null;


    public ListSendSterileAdapter(AppCompatActivity aActivity, ArrayList<pCustomer> listData, String B_ID) {
        super(aActivity, 0, listData);
        this.aActivity = aActivity;
        this.listData = listData;
        this.B_ID = B_ID;
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
        final View v = inflater.inflate(R.layout.list_send_sterile_adapter, parent, false);

        final TextView w_docno = (TextView) v.findViewById(R.id.w_docno);
        final TextView w_note = (TextView) v.findViewById(R.id.w_note);
        final TextView w_docdate = (TextView) v.findViewById(R.id.w_docdate);
        final TextView w_dapt = (TextView) v.findViewById(R.id.w_dapt);
        final TextView img_sttus = (TextView) v.findViewById(R.id.iswashdept);
        final TextView w_bt_note = (TextView) v.findViewById(R.id.w_bt_note);
        final TextView tQty1 = (TextView) v.findViewById(R.id.tQty1);
        final TextView tQty2 = (TextView) v.findViewById(R.id.tQty2);
        final TextView textView6 = (TextView) v.findViewById(R.id.textView6);

        w_docdate.setText(listData.get(position).getDocdate() + " " + listData.get(position).getTime() + " (" + listData.get(position).getSend_From() + ")" );
        w_dapt.setText(listData.get(position).getDeptname());
        w_docno.setText(listData.get(position).getDocno());
        w_note.setText(listData.get(position).getNote());
        tQty1.setText(listData.get(position).getQty1());
        tQty2.setText(listData.get(position).getQty2());

        final ImageView btn_del = (ImageView) v.findViewById(R.id.btn_del);

        if(((ReceiveActivity) aActivity).WA_IsUsedWash){
            tQty1.setVisibility(View.INVISIBLE);
            tQty2.setVisibility(View.INVISIBLE);
            textView6.setText("("+listData.get(position).getQty2()+")");
        }

        if (listData.get(position).getIsStatus().equals("0")) {
            img_sttus.setBackgroundResource(R.drawable.ic_radiobox_fill);
        } else {
            img_sttus.setBackgroundResource(R.drawable.ic_radiobox_unfill);
        }

        if (listData.get(position).getNote().equals("")){
            w_bt_note.setBackgroundResource(R.drawable.ic_list_grey);
        }else {
            w_bt_note.setBackgroundResource(R.drawable.ic_list_blue);
        }

        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReceiveActivity) aActivity).onLongClick(listData.get(position).getDocno());
            }
        });

        w_bt_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listData.get(position).getIsStatus().equals("0")){
                    final Dialog dialog = new Dialog(aActivity);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.remark_dialog);
                    dialog.setCancelable(true);
                    dialog.setTitle("หมายเหตุ...");

                    final EditText note1 = (EditText) dialog.findViewById(R.id.note1);
                    note1.setText(listData.get(position).getNote());
                    ImageView button1 = (ImageView) dialog.findViewById(R.id.button1);
                    button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            w_note.setText(note1.getText());

                            listData.get(position).setNote(note1.getText().toString());

                            ((ReceiveActivity) aActivity).updateRemark(listData.get(position).getDocno(), listData.get(position).getNote(), "1");

                            dialog.dismiss();
                        }
                    });

                    ImageView button5 = (ImageView) dialog.findViewById(R.id.button5);
                    button5.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            w_note.setText(listData.get(position).getNote());
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }else{
                    Toast.makeText(getContext(), "เอกสารนี้ส่งล้างแล้ว", Toast.LENGTH_SHORT).show();
                }

            }
        });


        return v;
    }
}


