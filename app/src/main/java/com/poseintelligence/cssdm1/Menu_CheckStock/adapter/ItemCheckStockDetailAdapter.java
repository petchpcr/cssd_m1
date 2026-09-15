package com.poseintelligence.cssdm1.Menu_CheckStock.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.CheckStockDetail;

import java.util.ArrayList;

public class ItemCheckStockDetailAdapter extends ArrayAdapter<CheckStockDetail> {

    private static final int COLOR_BORDER_DEFAULT = Color.parseColor("#9E9E9E");
    private static final int COLOR_BORDER_GREEN = Color.parseColor("#43A047");
    private static final int COLOR_BORDER_RED = Color.parseColor("#E53935");
    private static final int COLOR_EXPIRE_DEFAULT = Color.parseColor("#000000");
    private static final int COLOR_EXPIRE_OVER = Color.parseColor("#E53935");
    private static final int COLOR_EXPIRE_BEFORE = Color.parseColor("#FB8C00");
    private static final int COLOR_FILL = Color.WHITE;

    private final ArrayList<CheckStockDetail> listData;
    private final Activity context;
    private final int strokeWidthPx;
    private final float cornerRadiusPx;

    public ItemCheckStockDetailAdapter(Activity aActivity, ArrayList<CheckStockDetail> listData) {
        super(aActivity, 0, listData);
        this.context = aActivity;
        this.listData = listData;
        this.strokeWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics());
        this.cornerRadiusPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 6, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public CheckStockDetail getItem(int position) {
        return listData.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_check_stock_detail, parent, false);
            holder = new ViewHolder();
            holder.txtNo = (TextView) v.findViewById(R.id.txt_no);
            holder.txtItemCode = (TextView) v.findViewById(R.id.txt_item_code);
            holder.txtItemName = (TextView) v.findViewById(R.id.txt_item_name);
            holder.txtExpire = (TextView) v.findViewById(R.id.txt_expire);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        CheckStockDetail item = listData.get(position);
        holder.txtNo.setText((position + 1) + ".");
        holder.txtItemCode.setText(item.getUsageCode() != null ? item.getUsageCode() : "");
        holder.txtItemName.setText(item.getItemname() != null ? item.getItemname() : "");
        String date = item.getDate() != null ? item.getDate() : "";
        holder.txtExpire.setText("หมดอายุ : " + date);

        int borderColor = COLOR_BORDER_DEFAULT;
        if (item.isChecked()) {
            borderColor = item.isMainDep() ? COLOR_BORDER_GREEN : COLOR_BORDER_RED;
        }
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.RECTANGLE);
        bg.setColor(COLOR_FILL);
        bg.setCornerRadius(cornerRadiusPx);
        bg.setStroke(strokeWidthPx, borderColor);
        v.setBackground(bg);

        int expireColor = COLOR_EXPIRE_DEFAULT;
        if (item.isOverExp()) {
            expireColor = COLOR_EXPIRE_OVER;
        } else if (item.isBeforeExp()) {
            expireColor = COLOR_EXPIRE_BEFORE;
        }
        holder.txtExpire.setTextColor(expireColor);

        return v;
    }

    static class ViewHolder {
        TextView txtNo;
        TextView txtItemCode;
        TextView txtItemName;
        TextView txtExpire;
    }
}
