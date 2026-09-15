package com.poseintelligence.cssdm1.RFID.IDataT2X.Menu_CheckStock;

import static com.poseintelligence.cssdm1.data.Master.acForResultRFID;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_CheckStock.CheckStockItemActivity;
import com.poseintelligence.cssdm1.Menu_CheckStock.CheckStockMainActivity;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.T2XMainActivity;
import com.poseintelligence.cssdm1.core.string.Cons;

import java.util.HashMap;

public class CheckStockMainRFIDActivity extends CheckStockMainActivity {

    ProgressDialog RFID_dialog;
    String[] usagecode_rfid = new String[0];

    @Override
    public void byWidget() {
        super.byWidget();

        Button bt_rfid = (Button) findViewById(R.id.bt_rfid);
        bt_rfid.setVisibility(View.VISIBLE);
        bt_rfid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(CheckStockMainRFIDActivity.this, "CheckStockItemRFIDActivity", Toast.LENGTH_SHORT).show();
                gotoRFIDPage();
            }
        });

        bt_rfid = (Button) findViewById(R.id.bt_rfid);

        LinearLayout ll_check_stock = (LinearLayout) findViewById(R.id.ll_check_stock);
        ll_check_stock.setVisibility(View.VISIBLE);

        Button btDelete = findViewById(R.id.bt_delete);
        Button btSave = findViewById(R.id.bt_save);

        btDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStockDepDetail();
            }
        });

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStockDepDetail();
            }
        });

        RFID_dialog = new ProgressDialog(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == acForResultRFID){
            String intent_usagecode = data.getStringExtra("intent_usagecode");
            usagecode_rfid = new String[0];
            if (intent_usagecode != null && !intent_usagecode.isEmpty()) {
                usagecode_rfid = intent_usagecode.split(",");
            }

            Log.d("tog_itemList",usagecode_rfid.length+"");
            if(usagecode_rfid.length>0){
                select_stock_rfid(intent_usagecode);
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void gotoRFIDPage(){
        Intent i = new Intent(this, T2XMainActivity.class);
        i.putExtra("fragment", 3);
        startActivityForResult(i, acForResultRFID);
    }

    public void removeStockDepDetail() {
        class removeStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockMainRFIDActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(CheckStockMainRFIDActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    getItemsInStock(showingAllStock);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("userID", ((CssdProject) getApplication()).getCustomerId() + "");
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "remove_stock_dep_detail_all.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_removeStock", "remove_stock_dep_detail data = " + data);
                Log.d("tog_removeStock", "remove_stock_dep_detail result = " + result);
                return result;
            }
        }

        removeStock obj = new removeStock();
        obj.execute();
    }

    public void setStockDepDetail() {
        class setStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockMainRFIDActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(CheckStockMainRFIDActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    getItemsInStock(showingAllStock);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("userID", ((CssdProject) getApplication()).getCustomerId() + "");
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "set_stock_dep_detail_all.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_setStock", "set_stock_dep_detail data = " + data);
                Log.d("tog_setStock", "set_stock_dep_detail result = " + result);
                return result;
            }
        }

        setStock obj = new setStock();
        obj.execute();
    }

    public void select_stock_rfid(String intent_usagecode) {
        class setStock extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(CheckStockMainRFIDActivity.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog.setTitle(Cons.TITLE);
                dialog.setMessage(Cons.WAIT_FOR_LOADING);
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0xEFFFFFFF));
                dialog.setIndeterminate(true);

                dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    if (result != null && !result.isEmpty()) {
                        Toast.makeText(CheckStockMainRFIDActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    getItemsInStock(showingAllStock);
                }
            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("usagecode", intent_usagecode);
                data.put("depId", "20");

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "select_stock_rfid.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("tog_setStock", "set_stock_dep_detail data = " + data);
                Log.d("tog_setStock", "set_stock_dep_detail result = " + result);
                return result;
            }
        }

        setStock obj = new setStock();
        obj.execute();
    }
}
