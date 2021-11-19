package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelPayoutDetailSub;

import java.util.List;

public class ListPayoutDetailSubAdapter extends ArrayAdapter<ModelPayoutDetailSub> {
    private List<ModelPayoutDetailSub> DATA_MODEL;
    private Activity context;


    public ListPayoutDetailSubAdapter(Activity aActivity, List<ModelPayoutDetailSub> DATA_MODEL) {
        super(aActivity, 0, DATA_MODEL);
        this.context = aActivity;
        this.DATA_MODEL = DATA_MODEL;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View v = inflater.inflate(R.layout.list_payout_detail_sub, parent, false);

        final int index = position;

        final TextView txt_qr = (TextView) v.findViewById(R.id.txt_qr);
        final TextView txt_no = (TextView) v.findViewById(R.id.txt_no);
        final TextView txt_id = (TextView) v.findViewById(R.id.txt_id);

        txt_qr.setText(DATA_MODEL.get(position).getUsageCode());
        txt_id.setText(DATA_MODEL.get(position).getID());
        txt_no.setText("No." + (index + 1));


        return v;
    }
}
