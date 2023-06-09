package com.poseintelligence.cssdm1.Menu_Signature_Department;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.kyanogen.signatureview.SignatureView;
import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class SignaturePadAtivity extends AppCompatActivity {
    private Bitmap bitmap;
    private Button btn_clear;
    private Button btn_save;
    private SignatureView signatureViewPay;
    private String path;
    private static final String Image_DIRECTORY = "/cssd_image/uploads/";

    private String TAG_RESULTS = "result";

    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();
    private String docno = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_pad_ativity);

        byIntent();

        byWidget();

    }

    private void byIntent(){
        // Argument
        Intent intent = getIntent();

        docno = intent.getStringExtra("docno");

    }

    public void byWidget() {
        signatureViewPay = (SignatureView) findViewById(R.id.signature_view);
        btn_clear = (Button) findViewById(R.id.btn_clear);
        btn_save = (Button) findViewById(R.id.btn_save);

        btn_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureViewPay.clearCanvas();
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmap = signatureViewPay.getSignatureBitmap();
                uploadImage("", bitmap);
            }
        });
    }

    private void uploadImage(String SendDocNo,Bitmap binary){
        class UploadImage extends AsyncTask<String,Void,String> {
            private ProgressDialog dialog = new ProgressDialog(SignaturePadAtivity.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                try {
                    JSONObject jsonObj = new JSONObject(result);

                    finish();

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
                Bitmap bitmap1 = null;
                String image_str1 = "null";

                int width = 700;
                int height = 700;

                try {

                    ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                    bitmap1 = signatureViewPay.getSignatureBitmap();
                    bitmap1 = Bitmap.createScaledBitmap(bitmap1, width,height, true);
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1); //compress to which format you want.
                    byte [] byte_arr1 = stream1.toByteArray();
                    image_str1 = Base64.encodeToString(byte_arr1, Base64.DEFAULT);
                }catch (Exception e){

                }

                HashMap<String,String> data = new HashMap<>();

                data.put("image1", image_str1);
                data.put("docno", docno);
                data.put("name1",docno+"_pic1");

                String result = null;

                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_image/UploadImage_Signature.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("togBANK","result = "+data);
                Log.d("togBANK","result = "+data);
                Log.d("togBANK","result = "+result);

                return result;
            }
        }

        UploadImage ui = new UploadImage();
        ui.execute();

    }
}