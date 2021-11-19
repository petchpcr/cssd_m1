package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelPayout;

import java.util.List;

public class ListPayoutDocumentAdapter extends ArrayAdapter<ModelPayout> {

    private final List<ModelPayout> DATA_MODEL;
    private final Activity context;
    private final boolean IsSpecial;

    public ListPayoutDocumentAdapter(Activity context, List<ModelPayout> DATA_MODEL, boolean IsSpecial) {
        super(context, R.layout.list_payout_document, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        this.IsSpecial = IsSpecial;
    }

    private static final int NOT_SELECTED = -1;
    private int selectedPos = NOT_SELECTED;

    // if called with the same position multiple lines it works as toggle
    public void setSelection(int position) {
        /*if (selectedPos == position) {
            selectedPos = NOT_SELECTED;
        } else {
            selectedPos = position;
        }*/

        selectedPos = position;

        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.list_payout_document, parent, false);

        final TextView txt_no = (TextView) view.findViewById(R.id.txt_no);
        final TextView txt_department = (TextView) view.findViewById(R.id.txt_department);
        final TextView txt_doc_date = (TextView) view.findViewById(R.id.txt_doc_date);
        final TextView txt_doc_no = (TextView) view.findViewById(R.id.txt_doc_no);
        final TextView txt_status = (TextView) view.findViewById(R.id.txt_status);
        final TextView txt_department_id = (TextView) view.findViewById(R.id.txt_department_id);
        final TextView txt_ref_doc_no = (TextView) view.findViewById(R.id.txt_ref_doc_no);
        final TextView txt_doc_date_time = (TextView) view.findViewById(R.id.txt_doc_date_time);

        if (position == selectedPos) {
            // your color for selected item
            view.setBackgroundColor(Color.parseColor("#FAD7A0"));
        } else {
            // your color for non-selected item
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        txt_no.setText(DATA_MODEL.get(position).getNo());
        txt_doc_date.setText(DATA_MODEL.get(position).getCreateDate().replace("-","/"));
        txt_doc_no.setText(DATA_MODEL.get(position).getDocNo());
        txt_department.setText(DATA_MODEL.get(position).getDepNameByPayItem(IsSpecial));
        txt_department_id.setText(DATA_MODEL.get(position).getDepartment_ID());
        txt_status.setText(DATA_MODEL.get(position).getPayout_Status());
        //final int index = (DATA_MODEL.get(position).getIndex());
        //final String Desc = DATA_MODEL.get(position).getDesc();
        txt_ref_doc_no.setText(DATA_MODEL.get(position).getRefDocNo());
        txt_doc_date_time.setText(DATA_MODEL.get(position).getDocDateTime());

        return view;
    }

}