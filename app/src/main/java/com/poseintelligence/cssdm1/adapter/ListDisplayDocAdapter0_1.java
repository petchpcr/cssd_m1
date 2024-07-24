package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelDisplayDoc0_1;

import java.util.List;

public class ListDisplayDocAdapter0_1 extends ArrayAdapter<ModelDisplayDoc0_1> {

    private final List<ModelDisplayDoc0_1> DATA_MODEL;
    private final Activity context;
    private String Deptid;

    public ListDisplayDocAdapter0_1(Activity context, List<ModelDisplayDoc0_1> DATA_MODEL) {
        super(context, R.layout.activity_list_display_doc_adapter0_1, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        this.Deptid = Deptid;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.activity_list_display_doc_adapter0_1, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.txt_no = (TextView) view.findViewById(R.id.txt_no);
            viewHolder.ln_1 = (LinearLayout) view.findViewById(R.id.ln_1);
            viewHolder.txt_docno = (TextView) view.findViewById(R.id.txt_docno);
            viewHolder.txt_time = (TextView) view.findViewById(R.id.txt_docno11);
            viewHolder.txt_qty  = (TextView) view.findViewById(R.id.txt_qty);
            viewHolder.txt_docno1 = (TextView) view.findViewById(R.id.txt_docno1);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }

        // =========================================================================================
        final ViewHolder holder = (ViewHolder) view.getTag();

        holder.txt_docno1.setText(DATA_MODEL.get(position).getDocNo());

        holder.txt_no.setText(position+1+"");
        holder.txt_docno.setText(DATA_MODEL.get(position).getWashTypeName());

        if (!DATA_MODEL.get(position).getTestProgramName().equals("null")){
            holder.txt_time.setText(DATA_MODEL.get(position).getTestProgramName());
        }else {
            holder.txt_time.setText("-");
        }

        if (!DATA_MODEL.get(position).getWashMachineID().equals("null")){
            holder.txt_qty.setText(DATA_MODEL.get(position).getWashMachineID() + " รอบ : "+DATA_MODEL.get(position).getWashRoundNumber());
        }else {
            holder.txt_qty.setText("รอบ : "+DATA_MODEL.get(position).getWashRoundNumber());
        }


        if(DATA_MODEL.get(position).getIsActive().equals("1")){
            holder.txt_no.setTextColor( Color.BLACK );
            holder.txt_docno.setTextColor( Color.BLACK );
            holder.txt_time.setTextColor( Color.BLACK );
            holder.txt_qty.setTextColor( Color.BLACK );
            holder.txt_docno1.setTextColor( Color.BLACK );
        }else if(DATA_MODEL.get(position).getIsActive().equals("0")) {
            holder.txt_no.setTextColor( Color.RED );
            holder.txt_docno.setTextColor( Color.RED );
            holder.txt_time.setTextColor( Color.RED );
            holder.txt_qty.setTextColor( Color.RED );
            holder.txt_docno1.setTextColor( Color.RED );
        }

        // =========================================================================================

        return view;
    }

    static class ViewHolder {
        LinearLayout ln_1;
        TextView txt_no;
        TextView txt_time;
        TextView txt_docno;
        TextView txt_qty;
        TextView txt_docno1;
    }

}