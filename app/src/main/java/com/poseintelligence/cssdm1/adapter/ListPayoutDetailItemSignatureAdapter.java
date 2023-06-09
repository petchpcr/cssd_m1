package com.poseintelligence.cssdm1.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.poseintelligence.cssdm1.Menu_Signature_Department.SignatureDepartmentDetailActivity;
import com.poseintelligence.cssdm1.model.ModelPayoutDetailSignature;
import com.poseintelligence.cssdm1.R;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class ListPayoutDetailItemSignatureAdapter extends ArrayAdapter<ModelPayoutDetailSignature> {

    private final List<ModelPayoutDetailSignature> DATA_MODEL;
    private final Activity context;
    private String RefDocNo = "";
    private String Docno = "";
    private String Page = "";

    public ListPayoutDetailItemSignatureAdapter(Activity context, List<ModelPayoutDetailSignature> DATA_MODEL, String RefDocNo, String Docno, String Page) {
        super(context, R.layout.activity_list_payout_detail_item_signature_adapter, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        this.RefDocNo = RefDocNo;
        this.Docno = Docno;
        this.Page = Page;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_list_payout_detail_item_signature_adapter, parent, false);

        final ImageView img_zoom = (ImageView) view.findViewById(R.id.img_zoom);

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

        img_zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SignatureDepartmentDetailActivity)context).zoomImageFromThumb(img_zoom,DATA_MODEL.get(position).getItemcode());
            }
        });

        URL imageUrl = null;
        try {
            imageUrl = new URL("http://192.168.25.43/cssd_vch_renovate/cssd_image/"+DATA_MODEL.get(position).getItemcode()+"_pic1"+".PNG");
            Log.d("tog","result = "+imageUrl);
        } catch (MalformedURLException e) {
            Log.d("tog","result = "+e);
            throw new RuntimeException(e);
        }

        Picasso.get().load(String.valueOf(imageUrl)).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(img_zoom);


        if(DATA_MODEL.get(position).getIsWasting().equals("1")){

            if (!DATA_MODEL.get(position).getIsReceiveNotSterile().equals("1")){

                if (DATA_MODEL.get(position).getIsWasting().equals("1")){
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

                                        int Stock_Qty = STOCK_QTY;
                                        int Enter_Qty = Integer.valueOf(s_).intValue();

                                        //System.out.println(B_IsEmpty + ", " + Stock_Qty + ", " +Enter_Qty);

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

                            return false;
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

//                            ((CssdPayout_IPDOPD)context).ShowDetailIPD_OPD(txt_item_name.getText().toString(),"ILSV1G-215-00003","",txt_item_code.getText().toString(),"");



                            return true;
                        }
                    });

                    relativeLayout.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {


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

                                    boolean B_IsEmpty =  s_.length() == 0;

                                    s_ = B_IsEmpty ? "0" : s_;

                                    int Stock_Qty = STOCK_QTY;
                                    int Enter_Qty = Integer.valueOf(s_).intValue();



                                }

                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                });

                relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                    public boolean onLongClick(View v) {

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


                    return true;
                }
            });

            relativeLayout.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
//                    if (!RefDocNo.equals("-0")){
//                        ((com.Renovate_test.cssd.CssdPayout) context).openDialogPayoutDetailQty(
//                                ID,
//                                txt_item_code.getText().toString(),
//                                txt_item_name.getText().toString(),
//                                txt_request_qty.getText().toString(),
//                                txt_enter_qty.getText().toString()
//                        );
//                    }
                }
            });
        }

        if (RefDocNo.equals("") || RefDocNo.equals("null")){
            txt_request_qty.setVisibility(View.INVISIBLE);
        }else {
            txt_request_qty.setVisibility(View.VISIBLE);
        }

        if (RefDocNo.equals("-0")) {
            if (!Docno.equals("")) {
                txt_stock_qty.setVisibility(View.GONE);
                txt_enter_qty.setVisibility(View.GONE);

                txt_request_qty.setVisibility(View.VISIBLE);
                txt_qty_balance.setVisibility(View.VISIBLE);
                txt_qty.setText(DATA_MODEL.get(position).getPay_Qty());
            } else {
                txt_qty.setText(DATA_MODEL.get(position).getQty());

                txt_stock_qty.setVisibility(View.GONE);
                txt_qty_balance.setVisibility(View.GONE);
                txt_request_qty.setVisibility(View.GONE);
                txt_enter_qty.setVisibility(View.GONE);
            }
        }

        if (Page.equals("1")){
            if (DATA_MODEL.get(position).getPay_Qty().equals("0")){
                txt_item_code.setText(DATA_MODEL.get(position).getItemcode());
            }else {
                txt_item_code.setText(DATA_MODEL.get(position).getUsageCode());
            }
        }else {
            txt_item_code.setText(DATA_MODEL.get(position).getItemcode());
        }

        return view;
    }
}