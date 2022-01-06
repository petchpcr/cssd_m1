package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_Re_Pay_NonUsage.ReceivePayNonUsageActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelItemstockReceiveDetail;

import java.util.List;


public class ListReceiveDetailItemAdapter extends ArrayAdapter<ModelItemstockReceiveDetail> {

    private final List<ModelItemstockReceiveDetail> DATA_MODEL;
    private final Activity context;
    private final boolean mode;

    public ListReceiveDetailItemAdapter(Activity context, List<ModelItemstockReceiveDetail> DATA_MODEL, boolean mode) {
        super(context, R.layout.list_receive_detail_item, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        this.mode = mode;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = null;

        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_receive_detail_item, null);

            final ViewHolder viewHolder = new ViewHolder();

            viewHolder.txt_no = (TextView) view.findViewById(R.id.txt_no);
            viewHolder.txt_item_code = (TextView) view.findViewById(R.id.txt_item_code);
            viewHolder.txt_item_name = (TextView) view.findViewById(R.id.txt_item_name);

            viewHolder.txt_stock_qty = (TextView) view.findViewById(R.id.txt_stock_qty);
            viewHolder.txt_qty = (EditText) view.findViewById(R.id.txt_qty);

            viewHolder.relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

            viewHolder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((ReceivePayNonUsageActivity)context).onDeleteDetail(viewHolder.txt_item_code.getText().toString());
                    return true;
                }
            });

            viewHolder.txt_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(final View v, boolean hasFocus) {
                    Handler handler = new Handler();
                    Runnable delayedAction = null;

                    if (!hasFocus) {

                        if (delayedAction != null) {
                            handler.removeCallbacks(delayedAction);
                        }

                        delayedAction = new Runnable() {
                            @Override
                            public void run() {
                                TextView text_qty = (TextView) v;

                                int  enter_qty = 0;



                                if(!mode){

                                    try {
                                        enter_qty = Integer.valueOf(text_qty.getText().toString()).intValue();
                                    } catch (Exception e) {
                                        ((ReceivePayNonUsageActivity)context).onUpdatePayQty(viewHolder.ID, "0");
                                        DATA_MODEL.get(viewHolder.index).setQty("0");
                                        viewHolder.txt_qty.setText("0");
                                        return;
                                    }

                                    // Pay
                                    try {

                                        int stock_qty = Integer.valueOf(viewHolder.txt_stock_qty.getText().toString()).intValue();

                                        System.out.println(enter_qty + " <= " + stock_qty);

                                        if(enter_qty <= stock_qty){
                                            DATA_MODEL.get(viewHolder.index).setQty(Integer.toString(enter_qty));
                                            ((ReceivePayNonUsageActivity)context).onUpdatePayQty(viewHolder.ID, Integer.toString(enter_qty));
                                        }else{
                                            ((ReceivePayNonUsageActivity)context).onUpdatePayQty(viewHolder.ID, "0");
                                            DATA_MODEL.get(viewHolder.index).setQty("0");
                                            viewHolder.txt_qty.setText("0");
                                            return;
                                        }

                                    }catch(Exception e){
                                        ((ReceivePayNonUsageActivity)context).onUpdatePayQty(viewHolder.ID, "0");
                                        DATA_MODEL.get(viewHolder.index).setQty("0");
                                        viewHolder.txt_qty.setText("0");
                                        return;
                                    }

                                }else {
                                    // Receive
                                    try {

                                        ((ReceivePayNonUsageActivity) context).onUpdateQty(viewHolder.ID, text_qty.getText().toString());

                                        DATA_MODEL.get(viewHolder.index).setQty(text_qty.getText().toString());

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        };

                        //delay this new search by one second
                        handler.postDelayed(delayedAction, 100);

                    }
                }

            });


            view.setTag(viewHolder);

        } else {
            view = convertView;
        }

        // =========================================================================================
        final ViewHolder holder = (ViewHolder) view.getTag();
        holder.ID = DATA_MODEL.get(position).getID();
        holder.txt_no.setText(DATA_MODEL.get(position).getNo());
        holder.txt_item_code.setText(DATA_MODEL.get(position).getItemcode());
        holder.txt_item_name.setText(DATA_MODEL.get(position).getItemname());

        holder.txt_stock_qty.setText(DATA_MODEL.get(position).getStock_Qty());
        holder.txt_qty.setText(DATA_MODEL.get(position).getQty());

        holder.index = (DATA_MODEL.get(position).getIndex());
        // =========================================================================================

        return view;
    }

    static class ViewHolder {
        int index;
        String ID;
        TextView txt_no;
        TextView txt_item_code;
        TextView txt_item_name;

        TextView txt_stock_qty;
        EditText txt_qty;

        RelativeLayout relativeLayout;
    }

}