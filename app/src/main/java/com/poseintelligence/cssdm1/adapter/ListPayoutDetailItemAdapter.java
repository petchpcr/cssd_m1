package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.Menu_Dispensing.DispensingActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.model.ModelPayoutDetails;

import java.util.List;

public class ListPayoutDetailItemAdapter extends ArrayAdapter<ModelPayoutDetails> {

    private final List<ModelPayoutDetails> DATA_MODEL;
    private final Activity context;
    String RefDocNo;

    private boolean WA_IsUsedWash;
    private boolean PA_IsWastingPayout = false;

    public ListPayoutDetailItemAdapter(Activity context, List<ModelPayoutDetails> DATA_MODEL, boolean PA_IsWastingPayout,String RefDocNo) {
        super(context, R.layout.list_payout_detail_item, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        this.RefDocNo = RefDocNo;
        this.PA_IsWastingPayout = PA_IsWastingPayout;
        WA_IsUsedWash = ((DispensingActivity) context).WA_IsUsedWash;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.list_payout_detail_item, parent, false);

        final TextView txt_no = (TextView) view.findViewById(R.id.txt_no);
        final TextView txt_item_code = (TextView) view.findViewById(R.id.txt_item_code);
        final TextView txt_item_name = (TextView) view.findViewById(R.id.txt_item_name);

        final TextView txt_stock_qty = (TextView) view.findViewById(R.id.txt_stock_qty);
        final TextView txt_request_qty = (TextView) view.findViewById(R.id.txt_request_qty);
        final TextView txt_qty_balance = (TextView) view.findViewById(R.id.txt_qty_balance);
        final TextView txt_qty = (TextView) view.findViewById(R.id.txt_qty);
        final EditText txt_enter_qty = (EditText) view.findViewById(R.id.txt_enter_qty);

        final RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.relativeLayout);

        final String ID = DATA_MODEL.get(position).getID();
        txt_no.setText(DATA_MODEL.get(position).getNo());
        txt_item_code.setText(DATA_MODEL.get(position).getItemcode());
        txt_item_name.setText(DATA_MODEL.get(position).getItemname());

        final int STOCK_QTY = Integer.valueOf( DATA_MODEL.get(position).getStock_Qty() ).intValue();
        final int QTY = Integer.valueOf( DATA_MODEL.get(position).getQty() ).intValue();

        //txt_stock_qty.setText(Integer.toString(STOCK_QTY));

        if (RefDocNo.equals("") || RefDocNo.equals("null")){
            txt_request_qty.setVisibility(View.INVISIBLE);
        }else {
            txt_request_qty.setVisibility(View.VISIBLE);
        }

        if(DATA_MODEL.get(position).getIsWasting().equals("1")){

            if (!DATA_MODEL.get(position).getIsReceiveNotSterile().equals("1")){

                if (DATA_MODEL.get(position).getIsWasting().equals("1")){

                    if (PA_IsWastingPayout){
                        txt_qty.setText("0");
                        txt_qty.setVisibility(View.VISIBLE);
                        txt_enter_qty.setVisibility(View.GONE);
                    }else {
                        //txt_qty.setVisibility(View.GONE);
                        txt_enter_qty.setVisibility(View.VISIBLE);
                        txt_enter_qty.setText(DATA_MODEL.get(position).getQty());
                    }

                    txt_stock_qty.setText(Integer.toString(STOCK_QTY));

                    txt_request_qty.setText(DATA_MODEL.get(position).getQty());


                    if (DATA_MODEL.get(position).getRefDocNo().equals("")){
                        txt_qty.setText(DATA_MODEL.get(position).getQty());
                        txt_qty_balance.setText("0");
                    }else {
                        txt_qty.setText(DATA_MODEL.get(position).getPay_Qty());
                        txt_qty_balance.setText(DATA_MODEL.get(position).getBalance());
                    }

                    txt_enter_qty.setText(DATA_MODEL.get(position).getPay_Qty());

                    if(DATA_MODEL.get(position).getBalance().equals("0")){
                        relativeLayout.setBackgroundResource(R.drawable.rectangle_gray);
                    }

                    relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((DispensingActivity) context).openDialogPayoutDetailSub(ID,  txt_item_code.getText().toString(), txt_item_name.getText().toString());
                            return true;
                        }
                    });

                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            //System.out.println("onClick");

                            ((DispensingActivity) context).openDialogPayoutDetailQty(
                                    ID,
                                    txt_item_code.getText().toString(),
                                    txt_item_name.getText().toString(),
                                    txt_request_qty.getText().toString(),
                                    txt_enter_qty.getText().toString()
                            );
                        }
                    });
                }else {
                    txt_qty.setVisibility(View.VISIBLE);
                    txt_enter_qty.setVisibility(View.GONE);
                    txt_stock_qty.setText(Integer.toString(STOCK_QTY));

                    txt_request_qty.setText(DATA_MODEL.get(position).getQty());
                    txt_qty_balance.setText(DATA_MODEL.get(position).getBalance());
                    txt_qty.setText(DATA_MODEL.get(position).getPay_Qty());
                    txt_enter_qty.setText(DATA_MODEL.get(position).getPay_Qty());

                    if(DATA_MODEL.get(position).getBalance().equals("0")){
                        relativeLayout.setBackgroundResource(R.drawable.rectangle_gray);
                    }

                    relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            ((DispensingActivity) context).openDialogPayoutDetailSub(ID,  txt_item_code.getText().toString(), txt_item_name.getText().toString());
                            return true;
                        }
                    });

                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {

                            //System.out.println("onClick");

                            ((DispensingActivity) context).openDialogPayoutDetailQty(
                                    ID,
                                    txt_item_code.getText().toString(),
                                    txt_item_name.getText().toString(),
                                    txt_request_qty.getText().toString(),
                                    txt_enter_qty.getText().toString()
                            );
                        }
                    });
                }

            }else {

                txt_qty.setVisibility(View.INVISIBLE);
                txt_enter_qty.setVisibility(View.VISIBLE);

                //----------------------------------------------------------------
                txt_stock_qty.setText( Integer.toString(STOCK_QTY- QTY ) );
                txt_request_qty.setText(DATA_MODEL.get(position).getQty());
                txt_qty_balance.setText("0");
                txt_qty.setText("0");
                txt_enter_qty.setText(DATA_MODEL.get(position).getQty());

                // Default Enter Qty = Request Qty
                DATA_MODEL.get(position).setPay_Qty(DATA_MODEL.get(position).getQty());

                txt_enter_qty.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {

                        //System.out.println(hasFocus);

                        if (!hasFocus) {
                            try {

                                String s_ = txt_enter_qty.getText().toString();

                                if(! s_.equals(DATA_MODEL.get(position).getQty())) {

                                    //int Stock_Qty = Integer.valueOf(DATA_MODEL.get(position).getStock_Qty()).intValue();
                                    boolean B_IsEmpty =  s_.length() == 0;
                                    s_ = B_IsEmpty ? "0" : s_;


                                    //System.out.println(s_);

                                    int Stock_Qty = STOCK_QTY;
                                    int Enter_Qty = Integer.valueOf(s_).intValue();

                                    //System.out.println(B_IsEmpty + ", " + Stock_Qty + ", " +Enter_Qty);

                                    if(Stock_Qty >= Enter_Qty) {
                                        ((DispensingActivity) context).updateQty(ID, s_);

                                        String Tmp_Stock_Qty = Integer.toString(STOCK_QTY - Enter_Qty);

                                        txt_request_qty.setText(s_);
                                        txt_stock_qty.setText(Tmp_Stock_Qty);

                                        DATA_MODEL.get(position).setPay_Qty(s_);
                                        DATA_MODEL.get(position).setQty(s_);
                                        DATA_MODEL.get(position).setStock_Qty(Tmp_Stock_Qty);
                                    }else{
                                        Toast.makeText(context, "ป้อนจำนวนเกิน", Toast.LENGTH_SHORT).show();
                                        txt_enter_qty.setText(DATA_MODEL.get(position).getQty());
                                        //txt_enter_qty.requestFocus();
                                    }

                                    if(B_IsEmpty) {
                                        txt_enter_qty.setText("0");
                                    }

                                }



                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {
                        ((DispensingActivity) context).callDialogRemove(ID);
                        return false;
                    }
                });
            }

        }else{

            txt_qty.setVisibility(View.VISIBLE);
            txt_enter_qty.setVisibility(View.GONE);
            txt_stock_qty.setText(Integer.toString(STOCK_QTY));

            txt_request_qty.setText(DATA_MODEL.get(position).getQty());
            txt_qty_balance.setText(DATA_MODEL.get(position).getBalance());
            txt_qty.setText(DATA_MODEL.get(position).getPay_Qty());
            txt_enter_qty.setText(DATA_MODEL.get(position).getPay_Qty());

            if(DATA_MODEL.get(position).getBalance().equals("0")){
                relativeLayout.setBackgroundResource(R.drawable.rectangle_gray);
            }

            relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    ((DispensingActivity) context).openDialogPayoutDetailSub(ID,  txt_item_code.getText().toString(), txt_item_name.getText().toString());
                    return true;
                }
            });

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    //System.out.println("onClick");

                    ((DispensingActivity) context).openDialogPayoutDetailQty(
                            ID,
                            txt_item_code.getText().toString(),
                            txt_item_name.getText().toString(),
                            txt_request_qty.getText().toString(),
                            txt_enter_qty.getText().toString()
                    );
                }
            });
        }

        return view;
    }
}