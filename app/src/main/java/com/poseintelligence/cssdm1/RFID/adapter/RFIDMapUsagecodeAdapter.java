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
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.model.Item;

import java.util.List;

public class RFIDMapUsagecodeAdapter extends ArrayAdapter<Item> {
    private final List<Item> list;
    private final Activity context;

    public RFIDMapUsagecodeAdapter(Activity context, List<Item> list) {
        super(context, R.layout.activity_list_1, list);
        this.context = context;
        this.list = list;
    }

//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = inflater.inflate(R.layout.list_rfid_map_usagecoge, parent, false);
//
//        final TextView no = (TextView) view.findViewById(R.id.textViewNumber);
//        final TextView rfid = (TextView) view.findViewById(R.id.textViewRFID);
//        final TextView usagecode = (TextView) view.findViewById(R.id.textViewUsagecode);
//        final LinearLayout buttonLayout = (LinearLayout) view.findViewById(R.id.buttonLayout);
//        final Button buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
//        final Button buttonInfo = (Button) view.findViewById(R.id.buttonInfo);
//        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.rfid_progress_loader);
//
//        if(list.get(position).getUsagecode()!=null){
//            progressBar.setVisibility(View.GONE);
//            buttonLayout.setVisibility(View.VISIBLE);
//            if(list.get(position).getIsStatus()=="OK"){
//                rfid.setTextColor(Color.parseColor("#FE5cb85c"));
//                usagecode.setTextColor(Color.parseColor("#FE5cb85c"));
//            }else{
//                rfid.setTextColor(Color.parseColor("#FEC70000"));
//                usagecode.setTextColor(Color.parseColor("#FEC70000"));
//            }
//        }else{
//            buttonLayout.setVisibility(View.INVISIBLE);
//        }
//
//        buttonInfo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showDialog(list.get(position).getStatusName());
//            }
//        });
//
//        no.setText((position+1)+"");
//        rfid.setText(list.get(position).getRFID());
//        usagecode.setText(list.get(position).getUsagecode());
//
//
//        return view;
//    }

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