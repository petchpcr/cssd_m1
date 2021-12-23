package com.poseintelligence.cssdm1.Menu_Remark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
import com.poseintelligence.cssdm1.model.ModelDisplayDocRemark;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class dialog_delete_pic_remark extends Activity {

    CheckBox rd1,rd2,rd3;
    ImageView pic1,pic2,pic3;
    TextView submit,cancel;

    String Picture,Picture2,Picture3,ID;

    private JSONArray rs = null;
    private String TAG_RESULTS="result";
    private HTTPConnect httpConnect = new HTTPConnect();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_delete_pic_remark);

        try {
            Picasso.setSingletonInstance(
                    new Picasso.Builder(this)
                            .build());
        } catch ( IllegalStateException e ) {

        }

        byIntent();

        init();
    }

    private void byIntent(){
        Intent intent = getIntent();
        Picture = intent.getStringExtra("Picture");
        Picture2 = intent.getStringExtra("Picture2");
        Picture3 = intent.getStringExtra("Picture3");
        ID = intent.getStringExtra("ID");
    }

    public void init() {
        rd1 = (CheckBox) findViewById(R.id.rd1);
        rd2 = (CheckBox) findViewById(R.id.rd2);
        rd3 = (CheckBox) findViewById(R.id.rd3);

        if (Picture.equals("null")){
            rd1.setEnabled(false);
        }

        if (Picture2.equals("null")){
            rd2.setEnabled(false);
        }

        if (Picture3.equals("null")){
            rd3.setEnabled(false);
        }

        pic1 = (ImageView) findViewById(R.id.pic1);
        pic2 = (ImageView) findViewById(R.id.pic2);
        pic3 = (ImageView) findViewById(R.id.pic3);

        URL imageUrl = null;
        URL imageUrl2 = null;
        URL imageUrl3 = null;
        try {
            imageUrl = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/"+Picture);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            imageUrl2 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/"+Picture2);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            imageUrl3 = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/"+Picture3);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Picasso.get().load(String.valueOf(imageUrl)).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(pic1);

        Picasso.get().load(String.valueOf(imageUrl2)).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(pic2);

        Picasso.get().load(String.valueOf(imageUrl3)).networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(pic3);


        submit = (TextView) findViewById(R.id.submit);
        cancel = (TextView) findViewById(R.id.cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!rd1.isChecked() && !rd2.isChecked() && !rd3.isChecked()){
                    Toast.makeText(dialog_delete_pic_remark.this,"กรุณาเลือกรูปภาพที่จะลบ !!",Toast.LENGTH_SHORT).show();
                }else {
                    DeletePic(ID,"","","");
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void DeletePic(String ID,String Name_pic1,String Name_pic2,String Name_pic3){
        class DeletePic extends AsyncTask<String,Void,String> {
            private ProgressDialog dialog = new ProgressDialog(dialog_delete_pic_remark.this);
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
                    List<ModelDisplayDocRemark> list = new ArrayList<>();
                    for(int i=0;i<rs.length();i++){
                        JSONObject c = rs.getJSONObject(i);
                        if (c.getString("finish").equals("true1")){
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
            @SuppressLint("WrongThread")
            @Override
            protected String doInBackground(String... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("ID",ID);
                data.put("Picture",Picture);
                data.put("Picture2",Picture2);
                data.put("Picture3",Picture3);
                if (rd1.isChecked()){
                    data.put("Name_pic1","1");
                }else {
                    data.put("Name_pic1","0");
                }
                if (rd2.isChecked()){
                    data.put("Name_pic2","1");
                }else {
                    data.put("Name_pic2","0");
                }
                if (rd3.isChecked()){
                    data.put("Name_pic3","1");
                }else {
                    data.put("Name_pic3","0");
                }
                String result = null;
                result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/Delete_pic_Muti.php",data);
                Log.d("tog_delete_pic","data = "+data);
                Log.d("tog_delete_pic","result = "+result);
                return  result;
            }
        }
        DeletePic ui = new DeletePic();
        ui.execute();
    }
}