package com.poseintelligence.cssdm1.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelMachine;

import java.util.ArrayList;

public class ListBoxMachineAdapter extends RecyclerView.Adapter<ListBoxMachineAdapter.ViewHolder> {

    private ArrayList<ModelMachine> mData;
    private LayoutInflater mInflater;
    private Context context;
    private int select_mac_pos = -1;
    onItemSelect select_mac= new onItemSelect();
    RecyclerView wiget_list;

    // data is passed into the constructor
    public ListBoxMachineAdapter(Context context, ArrayList<ModelMachine> data,RecyclerView wiget_list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
        this.wiget_list = wiget_list;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_list_box_machine_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String macname = mData.get(position).getMachineName();
        holder.macname.setText(macname);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_mac_pos==position){
                    select_mac_pos=-1;
                }else{
                    select_mac_pos = position;
                    wiget_list.smoothScrollToPosition(select_mac_pos);
                }
                onItemSelect(holder.macname,holder.mac,holder.mac_image,holder.ll);
            }
        });

        if(position==select_mac_pos){
            setItemSelect(holder.macname,holder.mac,holder.mac_image,holder.ll);
        }else{
            setItemUnSelect(holder.macname,holder.mac,holder.mac_image,holder.ll);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView macname;
        TextView mac;
        ImageView mac_image;
        LinearLayout ll;


        ViewHolder(View itemView) {
            super(itemView);
            macname = itemView.findViewById(R.id.macname);
            mac = itemView.findViewById(R.id.mac);
            mac_image = itemView.findViewById(R.id.mac_image);
            ll = itemView.findViewById(R.id.ll);
        }

    }

    public void setItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll){
        mac_image.setBackgroundResource(R.drawable.ic_sterile_blue);
        ll.setBackgroundResource(R.drawable.rectangle_blue);
        mac.setTextColor(Color.WHITE);
        macname.setTextColor(Color.WHITE);
    }

    public void setItemUnSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll){
        mac_image.setBackgroundResource(R.drawable.ic_sterile_gray);
        ll.setBackgroundResource(R.drawable.rectangle_box_g);
        mac.setTextColor(context.getResources().getColor(R.color.colorTitleGray));
        macname.setTextColor(context.getResources().getColor(R.color.colorTitleGray));
    }

    public void onItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll) {
        if(select_mac.macname!=null){
            setItemUnSelect(select_mac.macname,select_mac.mac,select_mac.mac_image,select_mac.ll);
        }
        if(select_mac_pos>=0){
            select_mac.setItemSelect(macname,mac,mac_image,ll);
            setItemSelect(select_mac.macname,select_mac.mac,select_mac.mac_image,select_mac.ll);
        }
    }

    public class onItemSelect {
        TextView macname;
        TextView mac;
        ImageView mac_image;
        LinearLayout ll;

        public void setItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll) {
            this.macname = macname;
            this.mac = mac;
            this.mac_image = mac_image;
            this.ll = ll;
        }

        public TextView getMacname() {
            return macname;
        }

        public TextView getMac() {
            return mac;
        }

        public ImageView getMac_image() {
            return mac_image;
        }

        public LinearLayout getLl() {
            return ll;
        }
    }
}
