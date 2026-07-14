package com.poseintelligence.cssdm1.RFID.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.model.Item;

import java.util.List;

public class RFIDMapToPay extends ArrayAdapter<Item> {
    private final List<Item> list;
    private final Activity context;

    public RFIDMapToPay(Activity context, List<Item> list) {
        super(context, R.layout.activity_list_1, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_rfid_map_to_receive, parent, false);

        final TextView no = (TextView) view.findViewById(R.id.textViewNumber);
        final TextView rfid = (TextView) view.findViewById(R.id.textViewRFID);
        final TextView usagecode = (TextView) view.findViewById(R.id.textViewUsagecode);
        final TextView itemName = (TextView) view.findViewById(R.id.textViewItemName);
        final LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
        final Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
        final Button buttonInfo = (Button) view.findViewById(R.id.buttonInfo);

        if(list.get(position).getRfidStatus().equals("A")){
            buttonDelete.setVisibility(View.VISIBLE);
            buttonInfo.setVisibility(View.GONE);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.remove(position);
                    RFIDMapToPay.this.notifyDataSetChanged();
                }
            });
        }else{
            no.setTextColor(Color.GRAY);
            rfid.setTextColor(Color.GRAY);
            usagecode.setTextColor(Color.GRAY);
            itemName.setTextColor(Color.GRAY);
            buttonDelete.setVisibility(View.GONE);
            buttonInfo.setVisibility(View.VISIBLE);
            buttonInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog(list.get(position).getUsagecode()+" "+list.get(position).getStatusName());
                }
            });
        }

        no.setText((position+1)+"");
        rfid.setText(list.get(position).getTagRfid());
        usagecode.setText(list.get(position).getUsagecode());
        itemName.setText(list.get(position).getItemName());

        return view;
    }

    public void showDialog(String mass){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setMessage(mass);
        builder.setPositiveButton("ปิด", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}