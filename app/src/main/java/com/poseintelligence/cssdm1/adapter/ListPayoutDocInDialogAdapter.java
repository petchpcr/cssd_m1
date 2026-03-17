package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelPayout;

import java.util.List;

public class ListPayoutDocInDialogAdapter extends ArrayAdapter<ModelPayout> {

    private final List<ModelPayout> DATA_MODEL;
    private final Activity context;

    public ListPayoutDocInDialogAdapter(Activity context, List<ModelPayout> DATA_MODEL) {
        super(context, R.layout.list_payout_document_in_dialog, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.list_payout_document_in_dialog, parent, false);

        final TextView txt_department = (TextView) view.findViewById(R.id.txt_department);
        final TextView txt_doc_date = (TextView) view.findViewById(R.id.txt_doc_date);
        final TextView txt_doc_no = (TextView) view.findViewById(R.id.txt_doc_no);

        txt_doc_date.setText(DATA_MODEL.get(position).getCreateDate().replace("-","/"));
        txt_doc_no.setText(DATA_MODEL.get(position).getDocNo());
        txt_department.setText(DATA_MODEL.get(position).getDepName());


        return view;
    }

}