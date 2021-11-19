package com.poseintelligence.cssdm1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.adapter.ListItemStockAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelItemstockReceiveDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReturnActivity extends AppCompatActivity {
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    //------------------------------------------------
    // Session Variable
    private String getUrl="";
    private int userid;
    //------------------------------------------------

    private EditText edt_usage_code;
    private ListView list_search;
    private ImageView btn_complete;
    private ImageView img_back;

    List<ModelItemstockReceiveDetail> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        getSupportActionBar().hide();
        byIntent();
        byWidget();
        byEvent();
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    private void byIntent() {
        // Argument
        Intent intent = getIntent();
        getUrl = ((CssdProject) getApplication()).getxUrl();
        userid = ((CssdProject) getApplication()).getPm().getUserid();
    }

    private void byWidget() {
        edt_usage_code = (EditText) findViewById(R.id.edt_usage_code);
        list_search = (ListView) findViewById(R.id.list_search);
        img_back = (ImageView) findViewById(R.id.img_back);
    }

    private void byEvent() {
        list_search.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View view, final int pos, long id) {

                AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReturnActivity.this);
                quitDialog.setTitle(Cons.TITLE);
                quitDialog.setIcon(R.drawable.pose_favicon_2x);
                quitDialog.setMessage(Cons.CONFIRM_DELETE);

                quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            list.remove(pos);

                            for (int i = pos; i < list.size(); i++) {
                                list.get(i).setIndex(i);
                            }

                            ArrayAdapter<ModelItemstockReceiveDetail> adapter;
                            adapter = new ListItemStockAdapter(ReturnActivity.this, list);
                            list_search.setAdapter(adapter);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                quitDialog.show();



                return true;
            }
        });

        btn_complete = (ImageView) findViewById(R.id.btn_complete);
        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(ReturnActivity.this);
                quitDialog.setTitle(Cons.TITLE);
                quitDialog.setIcon(R.drawable.pose_favicon_2x);
                quitDialog.setMessage(Cons.CONFIRM_RECEIVE_TO_CSSD);

                quitDialog.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String list_id = "";

                        for (int i = 0; i < list.size(); i++) {
                            list_id += list.get(i).getID() + "," ;
                        }

                        // Toast.makeText(CssdReturnItemStock.this, "list_id = " + list_id, Toast.LENGTH_SHORT).show();


                        if(!list_id.equals(""))
                            onReceive(list_id);

                    }
                });

                quitDialog.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                quitDialog.show();
            }
        });

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReturnActivity.this,MainMenu.class);
                startActivity(intent);
                finish();
            }
        });

        edt_usage_code.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:

                            addToList(edt_usage_code.getText().toString());

                            return true;
                        default:
                            break;
                    }
                }

                return false;

            }


        });
    }

    private void addToList(final String p_qr) {

        list_search.setAdapter(null);

        class Add extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReturnActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);

                        if (c.getString("result").equals("A")) {

                            if(checkUsageCode(p_qr)){
                                list.add(
                                        new ModelItemstockReceiveDetail(
                                                c.getString("RowID"),
                                                "1",
                                                c.getString("itemcode"),
                                                c.getString("itemname"),
                                                c.getString("UsageCode"),
                                                list.size()
                                        )
                                );
                            }
                        }else{
                            Toast.makeText(ReturnActivity.this, "ไม่สามารถรับรายการนี้เข้าจ่ายกลางได้ !!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ArrayAdapter<ModelItemstockReceiveDetail> adapter;
                adapter = new ListItemStockAdapter(ReturnActivity.this, list);
                list_search.setAdapter(adapter);

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }

                edt_usage_code.setText("");
                edt_usage_code.requestFocus();

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_qr", p_qr);
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_select_item_stock.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Add obj = new Add();

        obj.execute();
    }

    private boolean checkUsageCode(String p_qr){
        for(int i = 0; i < list.size(); i++) {
            if (list.get(i).getItemcode().equals(p_qr)) {
                Toast.makeText(ReturnActivity.this, "มีรายการแล้ว !!", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    private void onReceive(final String p_data){

        class Receive extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(ReturnActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                this.dialog.show();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for (int i = 0; i < rs.length(); i++) {
                        JSONObject c = rs.getJSONObject(i);
                        Toast.makeText(ReturnActivity.this, c.getString("Message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                    list.clear();

                    list_search.setAdapter(null);

                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }

                    edt_usage_code.setText("");
                    edt_usage_code.requestFocus();
                }



            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String, String>();

                data.put("p_data", p_data);
                data.put("p_user_id", userid+"");
//                data.put("p_DB", ((CssdProject) getApplication()).getD_DATABASE());

                String result = null;

                try {
                    result = httpConnect.sendPostRequest(getUrl + "cssd_update_item_stock_to_cssd.php", data);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return result;
            }

            // =========================================================================================
        }

        Receive obj = new Receive();

        obj.execute();
    }

    @Override
    public void onBackPressed() {

    }
}