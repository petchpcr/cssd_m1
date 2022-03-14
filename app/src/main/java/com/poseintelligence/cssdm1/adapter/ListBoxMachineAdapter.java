package com.poseintelligence.cssdm1.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.poseintelligence.cssdm1.Menu_BasketWashing.BasketWashingActivity;
import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelMachine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListBoxMachineAdapter extends RecyclerView.Adapter<ListBoxMachineAdapter.ViewHolder> {

    private ArrayList<ModelMachine> mData;
    private LayoutInflater mInflater;
    private Context context;
    public int select_mac_pos = -1;
    onItemSelect select_mac= new onItemSelect();
    RecyclerView wiget_list;

    final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

    // data is passed into the constructor
    public ListBoxMachineAdapter(Context context, ArrayList<ModelMachine> data,RecyclerView wiget_list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = data;
        this.wiget_list = wiget_list;

        ((SterileActivity)context).title_2.setText(" ");
        ((SterileActivity)context).is_have_loader = false;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_list_box_machine_adapter, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String macname = mData.get(position).getMachineName();
        holder.macname.setText(macname);

        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(select_mac_pos==position){
                    select_mac_pos=-1;
                    ListBoxMachineAdapter.this.notifyDataSetChanged();
                }else{
                    ((SterileActivity)context).mac_id_non_approve  = position;
                    ((SterileActivity)context).get_machine(mData.get(position).getMachineID());
                }
            }
        });


        if(position==select_mac_pos){
            onItemSelect(holder.macname,holder.mac,holder.mac_image,holder.ll);
        }else{
            if(mData.get(position).getIsActive().equals("1")){
                holder.mac_image.setBackgroundResource(R.drawable.ic_sterile_red);
                holder.ll.setBackgroundResource(R.drawable.rectangle_red);
                holder.mac.setTextColor(Color.WHITE);
                holder.macname.setTextColor(Color.WHITE);
            }else if(mData.get(position).getIsBrokenMachine().equals("1")){
                holder.mac_image.setBackgroundResource(R.drawable.ic_sterile_black);
                holder.ll.setBackgroundResource(R.drawable.rectangle_hard_gray);
                holder.mac.setTextColor(Color.WHITE);
                holder.macname.setTextColor(Color.WHITE);
            }else{
                setMacUnSelect(holder.macname,holder.mac,holder.mac_image,holder.ll);
            }
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

    public void setMacSelect(){
//        select_mac.mac_image.setBackgroundResource(R.drawable.ic_sterile_blue);
//        select_mac.ll.setBackgroundResource(R.drawable.rectangle_blue);
//        select_mac.mac.setTextColor(Color.WHITE);
//        select_mac.macname.setTextColor(Color.WHITE);

        select_mac.ll.setVisibility(View.GONE);

        ((SterileActivity)context).show_mac(select_mac.macname.getText().toString(),View.VISIBLE);
    }

    public void setMacUnSelect(TextView mac, TextView macname, ImageView mac_image, LinearLayout ll){
        ll.setVisibility(View.VISIBLE);
        mac_image.setBackgroundResource(R.drawable.ic_sterile_gray);
        ll.setBackgroundResource(R.drawable.rectangle_box_g);
        mac.setTextColor(context.getResources().getColor(R.color.colorTitleGray));
        macname.setTextColor(context.getResources().getColor(R.color.colorTitleGray));
    }

    public void onItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll) {
        if(select_mac.macname!=null){
            setMacUnSelect(select_mac.macname,select_mac.mac,select_mac.mac_image,select_mac.ll);

            ((SterileActivity)context).show_mac("",View.GONE);
        }

        if(select_mac_pos>=0){
            select_mac.setItemSelect(macname,mac,mac_image,ll);
            setMacSelect();
            setProgramAndRound();
        }else{
            ((SterileActivity)context).title_2.setText(" ");
        }
    }

    public void onScanSelect(int pos) {
        select_mac_pos = pos;
        if(select_mac_pos>=0){
//            wiget_list.scrollToPosition(select_mac_pos);
            ((SterileActivity)context).show_mac(mData.get(pos).getMachineName(),View.VISIBLE);

            setProgramAndRound();
        }
    }

    public void setProgramAndRound() {
//        if(select_mac_pos==mData.size()-1){
//            ((SterileActivity)context).title_2.setText(" ");
//        }else{
//            ((SterileActivity)context).title_2.setText("โปรแกรม : "+mData.get(select_mac_pos).getProgramName()+"\tรอบ : "+mData.get(select_mac_pos).getRoundNumber());
//        }

        if(select_mac_pos==mData.size()-1){
            ((SterileActivity)context).title_2.setText(" ");
            ((SterileActivity)context).is_have_loader = false;
        }else{
            String loader = mData.get(select_mac_pos).getUserLoader();

            if(loader.equals(" ")){
                loader = "-";
                ((SterileActivity)context).is_have_loader = false;
            }else{
                ((SterileActivity)context).is_have_loader = true;
            }

            ((SterileActivity)context).title_2.setText("โปรแกรม : "+mData.get(select_mac_pos).getProgramName()+"\tรอบ : "+mData.get(select_mac_pos).getRoundNumber()+"\nผู้ทำรายการ : "+loader);
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
