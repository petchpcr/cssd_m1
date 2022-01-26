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
import com.poseintelligence.cssdm1.model.ModelDepartment;

import java.util.List;

public class ListDepartmentAdapter extends ArrayAdapter<ModelDepartment> {
    private final List<ModelDepartment> list;
    private final Activity context;
    private String color;

    public ListDepartmentAdapter(Activity context, List<ModelDepartment> list, String color) {
        super(context, R.layout.activity_list_1, list);
        this.context = context;
        this.list = list;
        this.color = color;
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
        final View view = inflater.inflate(R.layout.activity_list_1, parent, false);

        //final LinearLayout relativeLayout = (LinearLayout) view.findViewById(R.id.relativeLayout);
        final TextView code = (TextView) view.findViewById(R.id.code);
        final TextView name = (TextView) view.findViewById(R.id.basketname);
        final TextView name2 = (TextView) view.findViewById(R.id.name2);

        //relativeLayout.setBackgroundColor(Color.parseColor(color));
        name2.setText(list.get(position).getDepName());
        name.setText(list.get(position).getDepName2());
        code.setText(list.get(position).getID());

        if (position == selectedPos) {
            // your color for selected item
            view.setBackgroundColor(Color.parseColor("#D6EAF8"));
        } else {
            // your color for non-selected item
            view.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        return view;
    }

}