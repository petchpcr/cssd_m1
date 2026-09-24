package com.poseintelligence.cssdm1.Menu_ParStock.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelParStockItem;

import java.util.List;

public class ListAddParItemAdapter extends ArrayAdapter<ModelParStockItem> {
    private final List<ModelParStockItem> list;
    private final Activity context;

    public ListAddParItemAdapter(Activity context, List<ModelParStockItem> list) {
        super(context, R.layout.activity_list_add_par_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_list_add_par_item, parent, false);

        TextView txtItemCode = (TextView) view.findViewById(R.id.txt_itemcode);
        TextView txtItemName = (TextView) view.findViewById(R.id.txt_itemname);

        ModelParStockItem item = list.get(position);
        txtItemCode.setText(item.getItemcode() != null ? item.getItemcode() : "");
        txtItemName.setText(item.getItemname() != null ? item.getItemname() : "");

        return view;
    }
}
