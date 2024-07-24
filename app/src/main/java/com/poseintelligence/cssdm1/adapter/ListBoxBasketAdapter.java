package com.poseintelligence.cssdm1.adapter;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_Sterile.SterileActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.BasketTag;

import java.util.ArrayList;

public class ListBoxBasketAdapter extends RecyclerView.Adapter<ListBoxBasketAdapter.ViewHolder> {

    private ArrayList<BasketTag> all_mData;
    private ArrayList<BasketTag> mData;
    private LayoutInflater mInflater;
    private Context context;
    public int select_basket_pos = -1;
    public onItemSelect select_item= new onItemSelect();
    RecyclerView wiget_list;

    // data is passed into the constructor
    public ListBoxBasketAdapter(Context context, ArrayList<BasketTag> alldata, ArrayList<BasketTag> data,RecyclerView wiget_list,int select_basket_pos) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.all_mData = alldata;
        this.mData = data;
//        this.mData = new ArrayList<>();
        this.wiget_list = wiget_list;
        this.select_basket_pos = select_basket_pos;
//        int position = 0;
//        if(!((SterileActivity)context).get_mac_select_id().equals("Empty")){
//            for(position = 0;position<all_mData.size();position++){
//                if (((SterileActivity) context).get_mac_select_doc().equals(all_mData.get(position).getRefDocNo())) {
//                    mData.add(all_mData.get(position));
//                }
//            }
//        }else{
//            mData = data;
//        }
//        Log.d("tog_timer_php","==================================== end1 ====================================");
        Log.d("tog_getbasket","get_mac_select_id = "+((SterileActivity)context).get_mac_select_id());
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.activity_list_box_basket_adapter, parent, false);

//        Log.d("tog_timer_php","onCreateViewHolder = "+select_basket_pos);
        Log.d("tog_getbasket","onCreateViewHolder = "+select_basket_pos);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(mData.get(position).getName());
        holder.qty.setText(mData.get(position).getQty()+"");

        Log.d("tog_getbasket","position = "+mData.get(position).getPosition()+"---"+select_basket_pos);
        holder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("tog_getbasket","ll select_basket_pos = "+select_basket_pos);
                if(select_basket_pos==mData.get(position).getPosition()){
                    select_basket_pos=-1;
                    ListBoxBasketAdapter.this.notifyDataSetChanged();
                }else{
                    Log.d("tog_getbasket","onClick basket_pos_non_approve = "+mData.get(position).getPosition());
                    ((SterileActivity)context).basket_pos_non_approve = mData.get(position).getPosition();
                    ((SterileActivity)context).reload_mac();
                }

            }
        });

        if(mData.get(position).getPosition()==select_basket_pos){
            onItemSelect(holder.name,holder.qty,holder.image,holder.ll);
        }else{
            setItemUnSelect(holder.name,holder.qty,holder.image,holder.ll);

//            //1907
////            Log.d("tog_getbasket_mac","get_mac_select_id = "+((SterileActivity)context).get_mac_select_id());
//            if(!((SterileActivity)context).get_mac_select_id().equals("Empty")){
////                if(((SterileActivity)context).get_mac_select_id().equals(mData.get(position).getMacId())){
//
//                    if (!((SterileActivity) context).get_mac_select_doc().equals(mData.get(position).getRefDocNo())) {
////                        holder.ll.setVisibility(View.GONE);
////                        holder.qty.setVisibility(View.GONE);
//                        if(holder.itemView.getVisibility()==View.VISIBLE){
//
//                            Log.d("tog_timer_php","GONE = "+mData.get(position).getPosition()+" --- "+select_basket_pos);
//                            holder.itemView.setVisibility(View.GONE);
//                            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
//                        }
//                    }else{
//                        Log.d("tog_getbasket_mac", "Basket  = " + (mData.get(position).getName()));
//                        Log.d("tog_getbasket_mac", "getRefDocNo = " + (mData.get(position).getRefDocNo()));
//                    }
////                }
//            }
//            //1907
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView qty;
        ImageView image;
        LinearLayout ll;


        ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.basketname);
            qty = itemView.findViewById(R.id.text_qty);
            image = itemView.findViewById(R.id.image);
            ll = itemView.findViewById(R.id.ll);
        }

    }

    public void setItemSelect(){

//        select_item.image.setBackgroundResource(R.drawable.bi_basket_w);
//        select_item.ll.setBackgroundResource(R.drawable.rectangle_blue);
//        select_item.qty.setVisibility(View.VISIBLE);
//        select_item.name.setTextColor(Color.WHITE);

        select_item.ll.setVisibility(View.GONE);

        ((SterileActivity)context).show_basket(select_item.name.getText().toString(),View.VISIBLE);
    }

    public void setItemUnSelect(TextView name, TextView qty, ImageView image, LinearLayout ll){
        ll.setVisibility(View.VISIBLE);
        image.setBackgroundResource(R.drawable.bi_basket_g);
        ll.setBackgroundResource(R.drawable.rectangle_box_g);
        qty.setVisibility(View.GONE);
        name.setTextColor(context.getResources().getColor(R.color.colorTitleGray));
    }

    public void onScanSelect(int pos) {
        select_basket_pos = pos;
        if(select_basket_pos>=0){
            wiget_list.smoothScrollToPosition(0);
//            for(int position = 0;position<mData.size();position++){
//                if (select_basket_pos == mData.get(position).getPosition()) {
//                    ((SterileActivity)context).show_basket(mData.get(select_basket_pos).getName(),View.VISIBLE);
//                }
//            }
            ((SterileActivity)context).show_basket(all_mData.get(select_basket_pos).getName(),View.VISIBLE);
        }
//        wiget_list.smoothScrollToPosition(0);
    }

    public void onItemSelect(TextView name, TextView qty, ImageView image, LinearLayout ll) {
        if(select_item.name!=null){
            setItemUnSelect(select_item.name,select_item.qty,select_item.image,select_item.ll);

            ((SterileActivity)context).show_basket("onItemSelect",View.GONE);
        }
        if(select_basket_pos>=0){
            select_item.setItemSelect(name,qty,image,ll);
            setItemSelect();
        }

        ((SterileActivity)context).get_list_checkbox_to_delete();
    }

    public class onItemSelect {
        TextView name;
        TextView qty;
        ImageView image;
        LinearLayout ll;

        public void setItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll) {
            Log.d("tog_getbasket4","select_item.name = "+macname.getText().toString());
            this.name = macname;
            this.qty = mac;
            this.image = mac_image;
            this.ll = ll;
        }

        public TextView getname() {
            return name;
        }

        public TextView getqty() {
            return qty;
        }

        public ImageView get_image() {
            return image;
        }

        public LinearLayout getLl() {
            return ll;
        }
    }
}
