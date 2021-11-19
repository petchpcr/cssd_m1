package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;

import java.util.ArrayList;

public class SpinnerListAdapter extends ArrayAdapter<String> {
    private Activity activitySpinner;
    private ArrayList mData;

    public SpinnerListAdapter(Activity activitySpinner, ArrayList objects) {
        super(activitySpinner, 0, objects);
        this.activitySpinner = activitySpinner;
        mData = objects;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater mInflater = (LayoutInflater) activitySpinner.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = mInflater.inflate(R.layout.list_spinner, parent, false);
        TextView label = (TextView) row.findViewById(R.id.tFields1);
        label.setText(mData.get(position).toString());

        //Set meta data here and later we can access these values from OnItemSelected Event Of Spinner
        row.setTag(R.string.meta_position, Integer.toString(position));
        row.setTag(R.string.meta_title, mData.get(position).toString());

        return row;
    }
}