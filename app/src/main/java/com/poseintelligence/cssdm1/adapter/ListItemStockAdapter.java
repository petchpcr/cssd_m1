package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelItemstockReceiveDetail;

import java.util.List;

public class ListItemStockAdapter extends ArrayAdapter<ModelItemstockReceiveDetail> {

    private final List<ModelItemstockReceiveDetail> DATA_MODEL;
    private final Activity context;

    public ListItemStockAdapter(Activity context, List<ModelItemstockReceiveDetail> DATA_MODEL) {
        super(context, R.layout.list_item_stock, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_item_stock, null);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.txt_no = (TextView) view.findViewById(R.id.txt_no);
            viewHolder.txt_item_code = (TextView) view.findViewById(R.id.txt_item_code);
            viewHolder.txt_item_name = (TextView) view.findViewById(R.id.txt_item_name);
            viewHolder.txt_qty = (TextView) view.findViewById(R.id.txt_qty);

            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        // =========================================================================================
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.txt_no.setText(DATA_MODEL.get(position).getNo());
        holder.txt_item_name.setText(DATA_MODEL.get(position).getItemname());
        holder.txt_item_code.setText(DATA_MODEL.get(position).getItemcode());
        holder.txt_qty.setText(DATA_MODEL.get(position).getQty());

        // =========================================================================================

        return view;
    }

    static class ViewHolder {
        int index;
        TextView txt_no;
        TextView txt_item_name;
        TextView txt_item_code;
        TextView txt_qty;
    }

}