package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_MachineTest.MachineTestDetailActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.model.ModelChkMachine;

import org.json.JSONArray;

import java.util.ArrayList;

public class ListChkMachineAdapter extends ArrayAdapter<ModelChkMachine> {
    private final ArrayList<ModelChkMachine> list;
    private final Activity context;


    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    Uri image_uri1;
    Uri image_uri2;

    public ListChkMachineAdapter(Activity context, ArrayList<ModelChkMachine> list) {
        super(context, R.layout.activity_list_chk_machine_adapter, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_list_chk_machine_adapter, parent, false);

        final LinearLayout ll = (LinearLayout) view.findViewById(R.id.ln_1);
        final TextView txt_mac_name = (TextView) view.findViewById(R.id.txt_mac_name);
        final TextView txt_test_result = (TextView) view.findViewById(R.id.txt_test_result);
        final TextView txt_doc_date = (TextView) view.findViewById(R.id.txt_doc_date);
        final TextView txt_last_date = (TextView) view.findViewById(R.id.txt_last_date);
        final ImageView cycle_status = (ImageView) view.findViewById(R.id.cycle_status);

        txt_mac_name.setText(list.get(position).getMachineName());
        txt_test_result.setText(list.get(position).getIsResult());
        txt_doc_date.setText(list.get(position).getCreateDate());
        txt_last_date.setText(list.get(position).getLastUpdate());

        if(list.get(position).getIsResult().equals("ผ่าน")){
            cycle_status.setColorFilter(Color.parseColor("#b3ff99"));
        }else if(list.get(position).getIsResult().equals("ไม่ผ่าน")){
            cycle_status.setColorFilter(Color.parseColor("#428ac9"));
        }

        if(list.get(position).isEnableEdit()){
            ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MachineTestDetailActivity.class);
                    intent.putExtra("ID", list.get(position).getID());
                    intent.putExtra("CreateDate", list.get(position).getCreateDate());
                    intent.putExtra("LastUpdate", list.get(position).getLastUpdate());
                    intent.putExtra("MachineID", list.get(position).getMachineID());
                    intent.putExtra("MachineName", list.get(position).getMachineName());
                    intent.putExtra("IsResult", list.get(position).getIsResult());
                    intent.putExtra("Pic1", list.get(position).getPic1());
                    intent.putExtra("Pic2", list.get(position).getPic2());
                    intent.putExtra("Remark", list.get(position).getRemark());

                    context.startActivity(intent);
                }
            });
        }

        return view;
    }
}