package com.poseintelligence.cssdm1.Menu_ParStock.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_ParStock.ParStockMainActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelParStockItem;

import java.util.List;

public class ListParItemAdapter extends ArrayAdapter<ModelParStockItem> {
    private final List<ModelParStockItem> list;
    private final Activity context;

    public ListParItemAdapter(Activity context, List<ModelParStockItem> list) {
        super(context, R.layout.activity_list_par_item, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.activity_list_par_item, parent, false);

        TextView txtItemName = (TextView) view.findViewById(R.id.textView9);
        TextView txtParQty = (TextView) view.findViewById(R.id.textView18);
        TextView txtQty = (TextView) view.findViewById(R.id.textView19);

        final ModelParStockItem item = list.get(position);
        txtItemName.setText(item.getItemname() != null ? item.getItemname() : "");
        txtParQty.setText(item.getParQty() != null ? item.getParQty() : "0");
        txtQty.setText(item.getQty() != null ? item.getQty() : "0");

        Button btnEditQty = (Button) view.findViewById(R.id.button_edit_qty);
        btnEditQty.setFocusable(false);
        btnEditQty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ParStockMainActivity) context).openDialogCheckParStock(item);
            }
        });

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ((ParStockMainActivity) context).openDialogEditParQty(item);
                return true;
            }
        });

        return view;
    }
}
