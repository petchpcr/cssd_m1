package com.poseintelligence.cssdm1.Menu_CheckStock.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.CheckStockItem;

import java.util.ArrayList;

public class ItemCheckStockAdapter extends ArrayAdapter<CheckStockItem> {

    private static final int COLOR_INCOMPLETE = Color.parseColor("#E85A6A");
    private static final int COLOR_COMPLETE = Color.parseColor("#43A047");

    private final ArrayList<CheckStockItem> listData;
    private final Activity context;

    public ItemCheckStockAdapter(Activity aActivity, ArrayList<CheckStockItem> listData) {
        super(aActivity, 0, listData);
        this.context = aActivity;
        this.listData = listData;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public CheckStockItem getItem(int position) {
        return listData.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_to_check_stock, parent, false);
            holder = new ViewHolder();
            holder.txtNo = (TextView) v.findViewById(R.id.txt_no);
            holder.txtItemName = (TextView) v.findViewById(R.id.txt_item_name);
            holder.txtQty = (TextView) v.findViewById(R.id.txt_qty);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        CheckStockItem item = listData.get(position);
        holder.txtNo.setText((position + 1) + ".");
        holder.txtItemName.setText(item.getItemname() != null ? item.getItemname() : "");
        holder.txtQty.setText(item.getCheckQty() + "/" + item.getQty());

        int textColor = item.isComplete() ? COLOR_COMPLETE : COLOR_INCOMPLETE;
        holder.txtNo.setTextColor(textColor);
        holder.txtItemName.setTextColor(textColor);
        holder.txtQty.setTextColor(textColor);

        return v;
    }

    static class ViewHolder {
        TextView txtNo;
        TextView txtItemName;
        TextView txtQty;
    }
}
