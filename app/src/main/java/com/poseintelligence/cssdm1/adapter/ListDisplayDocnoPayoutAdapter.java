package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.model.ModelDocnoPayoutSignature;
import com.poseintelligence.cssdm1.R;

import java.util.List;

public class ListDisplayDocnoPayoutAdapter extends ArrayAdapter {

    private final List<ModelDocnoPayoutSignature> DATA_MODEL;
    private final Activity context;
    String Url;


    public ListDisplayDocnoPayoutAdapter(Activity context, List<ModelDocnoPayoutSignature> DATA_MODEL) {
        super(context, R.layout.activity_list_display_docno_payout_adapter, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        Url = ((CssdProject) context.getApplication()).getxUrl();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_list_display_docno_payout_adapter, parent, false);

        final TextView txt_no = (TextView) view.findViewById(R.id.txt_no);
        final TextView txt_docdate = (TextView) view.findViewById(R.id.txt_docdate);
        final TextView txt_docno = (TextView) view.findViewById(R.id.txt_docno);
        final TextView txt_depname = (TextView) view.findViewById(R.id.txt_depname);
        final TextView txt_roundpay = (TextView) view.findViewById(R.id.txt_roundpay);

        txt_no.setText(DATA_MODEL.get(position).getNo());
        txt_docdate.setText(DATA_MODEL.get(position).getDocdate());
        txt_docno.setText(DATA_MODEL.get(position).getDocno());
        txt_depname.setText(DATA_MODEL.get(position).getDepname());
        txt_roundpay.setText(DATA_MODEL.get(position).getPayround());

        return view;
    }
}