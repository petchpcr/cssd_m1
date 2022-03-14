package com.poseintelligence.cssdm1.adapter;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.Menu_Receive.ReceiveActivity;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.connect.xControl;
import com.poseintelligence.cssdm1.model.Response_Aux;
import com.poseintelligence.cssdm1.model.pCustomer;

import java.util.ArrayList;
import java.util.HashMap;

public class ListSendSterileDetailAdapter extends ArrayAdapter {

    private ArrayList<pCustomer> listData;
    private AppCompatActivity atv;
    private Context context;
    public boolean IsAdmin;

    private HTTPConnect httpConnect = new HTTPConnect();
    private xControl xCtl = new xControl();
    private String B_ID = null;
    private int mode = 1;

    private boolean WA_IsUsedWash = false;
    private boolean Switch_Mode = false;

    String S_StatusDoc = "";

    public ListSendSterileDetailAdapter(AppCompatActivity atv, ArrayList<pCustomer> listData, boolean IsAdmin, int mode, String B_ID, boolean WA_IsUsedWash, boolean Switch_Mode) {
        super(atv, 0, listData);
        this.IsAdmin = IsAdmin;
        this.atv= atv;
        this.listData = listData;
        this.B_ID = B_ID;
        this.mode = mode;
        this.WA_IsUsedWash = WA_IsUsedWash;
        this.Switch_Mode = Switch_Mode;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) atv.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = inflater.inflate(R.layout.list_send_sterile_detail_adapter, parent, false);

        final ImageView imv_re_sterile = (ImageView)v.findViewById(R.id.w_resterile);
        final TextView txtitemname = (TextView)v.findViewById(R.id.w_itemname);
        final TextView H_remark = (TextView)v.findViewById(R.id.textView53);
        final TextView txtUcode= (TextView) v.findViewById(R.id.w_ucode);
        final TextView w_date= (TextView) v.findViewById(R.id.w_date);
        final TextView itemqty = (TextView) v.findViewById(R.id.itemqty);
        final TextView txtxremark_detail = (TextView) v.findViewById(R.id.w_remark_detail);
        final TextView textView53 = (TextView) v.findViewById(R.id.textView53);
        final TextView r_name = (TextView) v.findViewById(R.id.w_r_name);
        final TextView txt_physician = (TextView) v.findViewById(R.id.txt_physician);
        final TextView bt_note = (TextView) v.findViewById(R.id.w_bt_note);
        final TextView bt_risk = (TextView) v.findViewById(R.id.w_bt_risk);
        final TextView w_bt_express = (TextView) v.findViewById(R.id.w_bt_express);
        final CheckBox chk = (CheckBox) v.findViewById(R.id.chk);
        final CheckBox chk_box = (CheckBox) v.findViewById(R.id.chk_box);
        final TextView basket_name = (TextView) v.findViewById(R.id.basket_name);
        final RelativeLayout rel_row = ( RelativeLayout ) v.findViewById(R.id.rel_row);

        final ImageView btdel = (ImageView) v.findViewById(R.id.w_btdel);
        final TextView bt_add_to_basket = (TextView) v.findViewById(R.id.bt_add_to_basket);

        // Set
        txtitemname.setText(listData.get(position).getItemname());
        txtitemname.setPaintFlags(txtitemname.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtitemname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ReceiveActivity)atv).LoadImg( listData.get(position).getItemcode(),"1",listData.get(position).getUsageCode(),listData.get(position).getItemname(),"noremark");
            }
        });
        txtUcode.setText(listData.get(position).getUsageCode());
        itemqty.setText("[ "+listData.get(position).getItemCount()+" ]");
        w_date.setText(" ("+listData.get(position).getPackdate()+" วัน)");
        txtxremark_detail.setText(listData.get(position).getXremark());
        basket_name.setText(listData.get(position).getBasketname());

        if((( ReceiveActivity ) atv).SS_IsUsedBasket){
            bt_add_to_basket.setVisibility(View.VISIBLE);
        }

        if (WA_IsUsedWash){
            w_bt_express.setVisibility(View.VISIBLE);
            bt_risk.setVisibility(View.VISIBLE);

            H_remark.setVisibility(View.INVISIBLE);
            txtxremark_detail.setVisibility(View.INVISIBLE);
            r_name.setVisibility(View.INVISIBLE);
            w_date.setVisibility(View.INVISIBLE);
        }else {
            w_bt_express.setVisibility(View.INVISIBLE);
            bt_risk.setVisibility(View.INVISIBLE);
        }

        chk_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    ((ReceiveActivity) atv).usageNowash(listData.get(position).getItemID(), "0");
                }else{
                    ((ReceiveActivity) atv).usageNowash(listData.get(position).getItemID(), "1");
                }
            }
        });

        if (Switch_Mode){
            chk_box.setVisibility(View.VISIBLE);
            chk_box.setChecked(true);
        }else {
            chk_box.setVisibility(View.INVISIBLE);
        }
//
//        chk_box.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                ((ReceiveActivity) atv).usageNowash(listData.get(position).getItemID(), listData.get(position).getItemID());
//            }
//        });

        if (listData.get(position).getXremark().equals("")){
            if(listData.get(position).getRemarkSend().equals("1")){
                bt_note.setBackgroundResource(R.drawable.ic_list_blue);
            }else {
                bt_note.setBackgroundResource(R.drawable.ic_list_grey);
            }
        }else {
            bt_note.setBackgroundResource(R.drawable.ic_list_blue);
        }

        if (listData.get(position).getRemarkEms().equals("0")){
            w_bt_express.setBackgroundResource(R.drawable.ic_item_express_gray);
        }else {
            w_bt_express.setBackgroundResource(R.drawable.ic_item_express_red);
        }

        w_bt_express.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if((listData.get(position).getIsStatus().equals("0"))){
                    S_StatusDoc = "0";
                }else {
                    S_StatusDoc = "1";
                }

                final Dialog dialog = new Dialog(atv);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.ems_dialog);
                dialog.setCancelable(true);
                dialog.setTitle("หมายเหตุ...");

                final EditText note1 = (EditText) dialog.findViewById(R.id.note1);

                note1.setText(listData.get(position).getRemarkExpress());

                ImageView button1 = (ImageView) dialog.findViewById(R.id.button1);
                ImageView button5 = (ImageView) dialog.findViewById(R.id.button5);

                final RadioButton urgent = (RadioButton) dialog.findViewById(R.id.urgent);

                if(listData.get(position).getRemarkEms().equals("1")){
                    urgent.setChecked(true);
                }

                if (S_StatusDoc.equals("0")) {
                    button1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
//                            bt_ems.setBackgroundResource(R.drawable.ic_stopwatchx64_red2);
                            if(urgent.isChecked()){

                                (( ReceiveActivity ) atv).updateRemarkems(listData.get(position).getUsageCode(), note1.getText().toString(), "1");
                            }else{

                                (( ReceiveActivity ) atv).updateRemarkems(listData.get(position).getUsageCode(), note1.getText().toString(), "2");
                            }
                            (( ReceiveActivity ) atv).displaySendSterileDetail(listData.get(position).getDocno());
                            dialog.dismiss();
                        }
                    });
                    button5.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            (( ReceiveActivity ) atv).updateRemarkems(listData.get(position).getUsageCode(), note1.getText().toString(), "0");
                            (( ReceiveActivity ) atv).displaySendSterileDetail(listData.get(position).getDocno());
                            dialog.dismiss();
                        }
                    });
                }else {
                    button1.setEnabled(false);
                    button5.setEnabled(false);
                    button1.setBackgroundResource(R.drawable.bt_save_grey);
                    button5.setBackgroundResource(R.drawable.bt_cancel_gray);
                    note1.setEnabled(false);
                    note1.setTextColor(Color.BLACK);
                }
                dialog.show();
            }
        });

        if(listData.get(position).getPhysicianName() != null && !listData.get(position).getPhysicianName().equals("")) {
            txt_physician.setText(listData.get(position).getPhysicianName() + " , " + listData.get(position).getHnInfo());
        }

        chk.setChecked(listData.get(position).isIscheck());

        chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                listData.get(position).setIscheck(chk.isChecked());
            }
        });

        ArrayList<Response_Aux> resultsR = xCtl.getListResterileType();

//        rel_row.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(mode == 1) {
//                    ((ReceiveActivity) atv).displaySterileDetailSet(listData.get(position).getUsageCode());
//                    ((ReceiveActivity) atv).getListDetailQty(listData.get(position).getUsageCode());
//                    ((ReceiveActivity) atv).getUsagecode(listData.get(position).getUsageCode());
//                }else {
//                    ((ReceiveActivity) atv).displaySterileDetailSet(listData.get(position).getUsageCode());
//                    ((ReceiveActivity) atv).getListDetailQty(listData.get(position).getUsageCode());
//                    ((ReceiveActivity) atv).getUsagecode(listData.get(position).getUsageCode());
//                }
//            }
//        });

//        rel_row.setOnLongClickListener(new View.OnLongClickListener() {
//            public boolean onLongClick(View v) {
//
//                if(mode == 1) {
//                    ((CssdSendSterile) atv).openPhysician(listData.get(position).getSs_rowid());
//                }
//
//                return false;
//            }
//        });



        if(listData.get(position).getIsStatus().equals("2")){

            btdel.setVisibility(View.GONE);

            if(!IsAdmin) {
                imv_re_sterile.setEnabled(false);
            }

            btdel.setEnabled(false);
            bt_note.setEnabled(false);
            bt_risk.setEnabled(false);
        }

        if(!listData.get(position).getOccuranceID().equals("0")){
            txtitemname.setTextColor(Color.RED);
            txtUcode.setTextColor(Color.RED);
            txtxremark_detail.setTextColor(Color.RED);
            textView53.setTextColor(Color.RED);
//            bt_risk.setEnabled(false);
//            bt_risk.setBackgroundResource(R.drawable.ic_risk_icon);
        }

        if (listData.get(position).getIsDenger().equals("0")){
            bt_risk.setBackgroundResource(R.drawable.ic_risk_icon_gray);
        }else {
            bt_risk.setBackgroundResource(R.drawable.ic_risk_icon);
        }

        if(listData.get(position).getIsSterile().equals("1")){
            for(int i=0;i<resultsR.size();i++){
                if(listData.get(position).getResteriletype().equals(resultsR.get(i).getFields1())){
                    listData.get(position).setResterilename(resultsR.get(i).getFields2());
                }
            }

            r_name.setText(listData.get(position).getResterilename());
            imv_re_sterile.setImageResource(R.drawable.alert_0);
        }


        btdel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
                builder.setCancelable(true);
                builder.setTitle("ยืนยัน");
                builder.setMessage("ต้องการลบหรือไม่");

                builder.setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //if(IsAdmin){
                                if(mode == 1)
                                    ( (ReceiveActivity)atv ).onDeleteSterileDetail(listData.get(position).getSs_rowid());
//                                else
//                                    ( (CssdApproveSendSterile)atv ).onDeleteSterileDetail(listData.get(position).getSs_rowid());

                            }
                        });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                
                AlertDialog dialog = builder.create();

                dialog.show();
            }

        });


        imv_re_sterile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(atv);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.resterile_dialog);
                dialog.setCancelable(true);
                dialog.setTitle("ประเภท Re-Sterile...");

                final Spinner ResterileType = (Spinner) dialog.findViewById(R.id.spn_list);
                xCtl.ListResterileType(ResterileType, atv ,listData.get(position).getResterilename());

                final ArrayList<Response_Aux> resultsResterileType = xCtl.getListResterileType();

                ResterileType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int pn, long id) {
                        //id rester
                        listData.get(position).setResteriletype(resultsResterileType.get(pn).getFields1());

                        //name rester
                        listData.get(position).setResterilename(ResterileType.getSelectedItem().toString());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        //Another interface callback
                    }
                });

                ImageView button1 = (ImageView) dialog.findViewById(R.id.button1);
                button1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        if(!ResterileType.getSelectedItem().equals("-")) {
                            listData.get(position).setIsSterile("1");
                            imv_re_sterile.setImageResource(R.drawable.alert_0);
                            r_name.setText(listData.get(position).getResterilename());
                        }

                        if (ResterileType.getSelectedItem().equals("-")) {
                            listData.get(position).setIsSterile("0");
                            imv_re_sterile.setImageResource(R.drawable.ic_r_grey);
                            r_name.setText("");
                        }

                        updateReSterileType_(listData.get(position).getSs_rowid(), listData.get(position).getIsSterile(), listData.get( position ).getResterilename());

                        dialog.dismiss();
                    }
                });

                ImageView cancel = (ImageView) dialog.findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {
                            if (ResterileType.getSelectedItem().equals("-")) {
                                listData.get(position).setIsSterile("0");
                                imv_re_sterile.setImageResource(R.drawable.ic_r_grey);
                                r_name.setText("");
                            }

                            updateReSterileType(listData.get(position).getSs_rowid(), listData.get(position).getIsSterile(), listData.get(position).getResteriletype());

                        }catch(Exception e){

                        }

                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        if(!listData.get(position).getBasketname().equals("")){
            bt_add_to_basket.setBackgroundResource(R.drawable.bi_basket_b);
        }else {
            bt_add_to_basket.setBackgroundResource(R.drawable.bi_basket_g);
        }

        bt_add_to_basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!listData.get(position).getBasketname().equals("")){
                    basket_detail_delete(listData.get(position).getSs_rowid());
                }else {
                    if(listData.get(position).getResteriletype().equals("1")){
                        AlertDialog.Builder builder = new AlertDialog.Builder(atv);
                        builder.setCancelable(true);
                        builder.setTitle("ยืนยัน");
                        builder.setMessage("ต้องการนำรายการหมดอายุ เข้าตะกร้า Wash Tag หรือไม่");
                        builder.setPositiveButton("ใช่",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((ReceiveActivity) atv ).insert_item_to_basket_and_re(listData.get(position).getItemID(),listData.get(position).getSs_rowid());
                                    }
                                });
                        builder.setNegativeButton("ไม่", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }else{
                        ((ReceiveActivity) atv ).insert_item_to_basket(listData.get(position).getItemID(),listData.get(position).getSs_rowid());
                    }
                }

            }
        });

//        bt_risk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                final Dialog dialog = new Dialog(atv);
//                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                dialog.setContentView(R.layout.risk_dialog_sendsterile);
//                dialog.setCancelable(true);
//                dialog.setTitle("ประเภทความเสี่ยง...");
//
//                final Spinner OccuranceType = (Spinner) dialog.findViewById(R.id.spn_list);
//
//                xCtl.ListOccuranceType(OccuranceType, atv );
//
//                final ArrayList<Response_Aux> resultsOccuranceType = xCtl.getListOccuranceType();
//
//                OccuranceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> parent, View view, int pn, long id) {
//                        listData.get(position).setOccurancetype(resultsOccuranceType.get(pn).getFields1());
//                        listData.get(position).setOccurancename(OccuranceType.getSelectedItem().toString());
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> parent) {
//                        //Another interface callback
//                    }
//                });
//
//                Button button1 = (Button) dialog.findViewById(R.id.button1);
//                button1.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        listData.get(position).setOccuranceID(listData.get(position).getOccurancetype());
//
//                        updateOccurrenceQty(listData.get(position).getUcode(),listData.get(position).getDocno(),listData.get(position).getDept(),listData.get(position).getItemID(),listData.get(position).getOccurancetype());
//
//                        bt_risk.setBackgroundResource(R.drawable.ic_risk_icon);
//                        txtitemname.setTextColor(Color.RED);
//                        txtUcode.setTextColor(Color.RED);
//                        txtxremark_detail.setTextColor(Color.RED);
//                        textView53.setTextColor(Color.RED);
//
//                        dialog.dismiss();
//                    }
//                });
//
//                Button button5 = (Button) dialog.findViewById(R.id.button5);
//                button5.setOnClickListener(new View.OnClickListener() {
//                    public void onClick(View v) {
//                        listData.get(position).setOccuranceID(listData.get(position).getOccurancetype());
//
//                        updateOccurrenceQty_0(listData.get(position).getUcode(),listData.get(position).getDocno(),listData.get(position).getDept(),listData.get(position).getItemID(),listData.get(position).getOccurancetype());
//                        imv_re_sterile.setBackgroundResource(R.drawable.ic_alert_g);
//                        bt_risk.setBackgroundResource(R.drawable.ic_risk_icon_gray);
//                        txtitemname.setTextColor(Color.BLACK);
//                        txtUcode.setTextColor(Color.BLACK);
//                        txtxremark_detail.setTextColor(Color.BLACK);
//                        textView53.setTextColor(Color.BLACK);
//
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            }
//        });

        bt_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(atv);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.remark_dialog);
                dialog.setCancelable(true);
                dialog.setTitle("หมายเหตุ...");

                final EditText note1 = (EditText) dialog.findViewById(R.id.note1);
                note1.setText(listData.get(position).getXremark());
                ImageView button1 = (ImageView) dialog.findViewById(R.id.button1);
                button1.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        listData.get(position).setXremark(note1.getText().toString());
                        
                        updateRemark(listData.get(position).getSs_rowid(),listData.get(position).getXremark(),"2");

                        txtxremark_detail.setText(listData.get(position).getXremark());
                        
                        dialog.dismiss();
                    }
                });

                ImageView button5 = (ImageView) dialog.findViewById(R.id.button5);
                button5.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        txtxremark_detail.setText(listData.get(position).getXremark());
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });

        return v;
    }

    

    private void updateReSterileType(final String UsageCode, final String Check, final String ss) {
        class UpdateReSterileType extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("ID", UsageCode);
                data.put("Check", Check);
                data.put("ss", ss);
                data.put("B_ID",B_ID);
//                data.put("p_DB", ((CssdProject) context).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) atv.getApplication()).getxUrl() + "cssd_update_send_sterile_detail_re_sterile_type.php",data);

                return result;
            }
        }

        UpdateReSterileType ru = new UpdateReSterileType();
        
        ru.execute();
    }

    private void updateReSterileType_(final String UsageCode, final String Check, final String ss) {
        class UpdateReSterileType extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("ID", UsageCode);
                data.put("Check", Check);
                data.put("ss", ss);
                data.put("B_ID",B_ID);
//                data.put("p_DB", ((CssdProject) context).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) atv.getApplication()).getxUrl() + "cssd_update_send_sterile_detail_re_sterile_type_.php",data);
                
                return result;
            }
        }

        UpdateReSterileType ru = new UpdateReSterileType();
        ru.execute();
    }


    private void updateRemark(final String DocNo, final String remark, final String check) {
        class UpdateRemark extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                
            }
            
            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("DocNo", DocNo);
                data.put("remark", remark);
                data.put("check", check);
                data.put("B_ID", B_ID);
//                data.put("p_DB", ((CssdProject) context).getD_DATABASE());
                
                String result = httpConnect.sendPostRequest(((CssdProject) atv.getApplication()).getxUrl() + "cssd_update_send_sterile_remark.php",data);
                
                return result;
            }
        }

        UpdateRemark ru = new UpdateRemark();
        
        ru.execute();
    }

    private void updateOccurrenceQty(final String usercode, final String docno, final String dept, final String itemcode, final String octype) {
        class Update extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("usercode", usercode);
                data.put("docno", docno);
                data.put("dept", dept);
                data.put("itemcode", itemcode);
                data.put("octype", octype);
                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) context).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) atv.getApplication()).getxUrl() + "cssd_update_send_sterile_detail_occurrence_qty.php",data);

                return result;
            }
        }

        Update ru = new Update();

        ru.execute();
    }

    private void updateOccurrenceQty_0(final String usercode, final String docno, final String dept, final String itemcode, final String octype) {
        class Update extends AsyncTask<String, Void, String> {
            // variable

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();

                data.put("usercode", usercode);
                data.put("docno", docno);
                data.put("dept", dept);
                data.put("itemcode", itemcode);
                data.put("octype", octype);
                data.put("B_ID",B_ID);
                data.put("p_DB", ((CssdProject) context).getD_DATABASE());

                String result = httpConnect.sendPostRequest(((CssdProject) atv.getApplication()).getxUrl() + "cssd_update_send_sterile_detail_occurrence_qty_0.php",data);

                return result;
            }
        }

        Update ru = new Update();

        ru.execute();
    }


    public void basket_detail_delete(String BasketID){
        ((ReceiveActivity) atv).basket_detail_delete(BasketID,"1");
    }
}



