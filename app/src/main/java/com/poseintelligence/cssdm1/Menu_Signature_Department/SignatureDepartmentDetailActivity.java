package com.poseintelligence.cssdm1.Menu_Signature_Department;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.model.ModelPayoutDetailSignature;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListPayoutDetailItemSignatureAdapter;
import com.poseintelligence.cssdm1.core.connect.HTTPConnect;
import com.poseintelligence.cssdm1.core.string.Cons;
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

public class SignatureDepartmentDetailActivity extends AppCompatActivity {

    private ImageView backpage;
    public  ImageView imageItemShow;
    private TextView txt_depname;
    private TextView txt_docno;
    private TextView txt_docdate;
    private ListView list_payput_detail;
    private Button btn_signature;
    public Animator currentAnimator;
    public ConstraintLayout container;
    public Boolean dialogBoolean = false;

    public Dialog dialog;
    String depname = "";
    String docno = "";
    String docdate = "";
    public int shortAnimationDuration;
    private String imageItemShowitemCode = "";
    private String TAG_RESULTS = "result";
    private JSONArray rs = null;
    private HTTPConnect httpConnect = new HTTPConnect();

    private List<ModelPayoutDetailSignature> Model_Payout_Detail_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature_department_detail);

        byWidget();

        byIntent();
    }

    private void byIntent(){
        // Argument
        Intent intent = getIntent();
        depname = intent.getStringExtra("Depname");
        docno = intent.getStringExtra("DocNo");
        docdate = intent.getStringExtra("Docdate");

        txt_depname.setText("แผนก : " + depname);
        txt_docno.setText("No. : " + docdate);
        txt_docdate.setText("วันที่ : " + docno);

    }

    public void byWidget() {
        shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        container = (ConstraintLayout)findViewById(R.id.container);
        imageItemShow = (ImageView) findViewById(R.id.imageItemShow);

        txt_depname = (TextView) findViewById(R.id.txt_depname);
        txt_docno = (TextView) findViewById(R.id.txt_docno);
        txt_docdate = (TextView) findViewById(R.id.txt_docdate);
        list_payput_detail = (ListView) findViewById(R.id.list_payput_detail);

        btn_signature = (Button) findViewById(R.id.btn_signature);
        btn_signature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(SignatureDepartmentDetailActivity.this, SignaturePadAtivity.class);
                intent.putExtra("docno", docdate);
                startActivity(intent);
                finish();
            }
        });

        backpage = (ImageView) findViewById(R.id.backpage);
        backpage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do something when the corky2 is clicked
                Intent intent = new Intent(SignatureDepartmentDetailActivity.this, SignatureDepartmentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        imageItemShow.setVisibility(View.GONE);

        displayPayoutDetail(docno,false);
    }

    public void displayPayoutDetail(final String p_docno, final boolean isShowDialog){

        class DisplayPayout extends AsyncTask<String, Void, String> {

            private ProgressDialog dialog = new ProgressDialog(SignatureDepartmentDetailActivity.this);

            // variable
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                if(isShowDialog) {
                    this.dialog.setMessage(Cons.WAIT_FOR_PROCESS);
                    this.dialog.show();
                }
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);

                List<ModelPayoutDetailSignature> list = new ArrayList<>();

                String s_qty = "0";
                String s_p_qty = "0";

                int qty = 0;
                int p_qty = 0;

                try {
                    JSONObject jsonObj = new JSONObject(result);
                    rs = jsonObj.getJSONArray(TAG_RESULTS);

                    for(int i=0;i<rs.length();i++){

                        JSONObject c = rs.getJSONObject(i);

                        if(c.getString("result").equals("A")) {
                            list.add(
                                    new ModelPayoutDetailSignature(
                                            c.getString("ID"),
                                            c.getString("itemcode"),
                                            c.getString("itemname"),
                                            c.getString("ItemStockID"),
                                            c.getString("UsageCode"),

                                            c.getString("Pay_Qty"),
                                            c.getString("Stock_Qty"),
                                            c.getString("Qty"),
                                            c.getString("Balance_Qty"),
                                            null,

                                            c.getString("IsWasting"),
                                            c.getString("IsReceiveNotSterile"),
                                            "",
                                            "",
                                            i
                                    )
                            );

                        }else{

                        }
                    }

                } catch (JSONException e) {
                    list_payput_detail.setAdapter(null);
                    e.printStackTrace();
                }

                Model_Payout_Detail_item = list;

                ArrayAdapter<ModelPayoutDetailSignature> adapter;
                adapter = new ListPayoutDetailItemSignatureAdapter(SignatureDepartmentDetailActivity.this, Model_Payout_Detail_item,"","-1", "1");
                list_payput_detail.setAdapter(adapter);

                if (isShowDialog && dialog.isShowing()) {
                    dialog.dismiss();
                }

            }

            @Override
            protected String doInBackground(String... params) {
                HashMap<String, String> data = new HashMap<String,String>();
                data.put("p_docno", docdate);
                data.put("p_is_create_receive_department", false ? "1" : "0");

                String result = null;

                try {

                    result = httpConnect.sendPostRequest(((CssdProject) getApplication()).getxUrl() + "cssd_display_payout_detail_by_item_ipdopd.php", data);

                }catch(Exception e){
                    e.printStackTrace();
                }

                Log.d("tog","result = "+data);
                Log.d("tog","result = "+result);

                return result;
            }

            // -------------------------------------------------------------

        }

        DisplayPayout obj = new DisplayPayout();
        obj.execute();
    }

    public void zoomImageFromThumb(final View thumbView,String itemCode) {

        Log.d("ttest_InsertDetail", String.valueOf(thumbView));
        Log.d("ttest_InsertDetail",itemCode);
        Log.d("ttest_InsertDetail",imageItemShowitemCode);

        if(itemCode == imageItemShowitemCode){
            imageItemShow.callOnClick();
        }else {
            imageItemShowitemCode = itemCode;
            // If there's an animation in progress, cancel it
            // immediately and proceed with this one.
            if (currentAnimator != null) {
                currentAnimator.cancel();
            }

            // Load the high-resolution "zoomed-in" image.

            // Calculate the starting and ending bounds for the zoomed-in image.
            // This step involves lots of math. Yay, math.
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail,
            // and the final bounds are the global visible rectangle of the container
            // view. Also set the container view's offset as the origin for the
            // bounds, since that's the origin for the positioning animation
            // properties (X, Y).
            thumbView.getGlobalVisibleRect(startBounds);
            container.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);
            // Adjust the start bounds to be the same aspect ratio as the final
            // bounds using the "center crop" technique. This prevents undesirable
            // stretching during the animation. Also calculate the start scaling
            // factor (the end scaling factor is always 1.0).
            float startScale;
            if ((float) finalBounds.width() / finalBounds.height()
                    > (float) startBounds.width() / startBounds.height()) {
                // Extend start bounds horizontally
                startScale = (float) startBounds.height() / finalBounds.height();
                float startWidth = startScale * finalBounds.width();
                float deltaWidth = (startWidth - startBounds.width()) / 2;
                startBounds.left -= deltaWidth;
                startBounds.right += deltaWidth;
            } else {
                // Extend start bounds vertically
                startScale = (float) startBounds.width() / finalBounds.width();
                float startHeight = startScale * finalBounds.height();
                float deltaHeight = (startHeight - startBounds.height()) / 2;
                startBounds.top -= deltaHeight;
                startBounds.bottom += deltaHeight;
            }

            // Hide the thumbnail and show the zoomed-in view. When the animation
            // begins, it will position the zoomed-in view in the place of the
            // thumbnail.
//            thumbView.setAlpha(0f);
            imageItemShow.setVisibility(View.VISIBLE);

            // Set the pivot point for SCALE_X and SCALE_Y transformations
            // to the top-left corner of the zoomed-in view (the default
            // is the center of the view).
            imageItemShow.setPivotX(0f);
            imageItemShow.setPivotY(0f);

            // Construct and run the parallel animation of the four translation and
            // scale properties (X, Y, SCALE_X, and SCALE_Y).
            AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(imageItemShow, View.X,
                            startBounds.left, finalBounds.left))
                    .with(ObjectAnimator.ofFloat(imageItemShow, View.Y,
                            startBounds.top, finalBounds.top))
                    .with(ObjectAnimator.ofFloat(imageItemShow, View.SCALE_X,
                            startScale, 1f))
                    .with(ObjectAnimator.ofFloat(imageItemShow,
                            View.SCALE_Y, startScale, 1f));
            set.setDuration(shortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    currentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    currentAnimator = null;
                }
            });
            set.start();
            currentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down
            // to the original bounds and show the thumbnail instead of
            // the expanded image.
            final float startScaleFinal = startScale;
            imageItemShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentAnimator != null) {
                        currentAnimator.cancel();
                    }

                    // Animate the four positioning/sizing properties in parallel,
                    // back to their original values.
                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator
                                    .ofFloat(imageItemShow, View.X, startBounds.left))
                            .with(ObjectAnimator
                                    .ofFloat(imageItemShow,
                                            View.Y, startBounds.top))
                            .with(ObjectAnimator
                                    .ofFloat(imageItemShow,
                                            View.SCALE_X, startScaleFinal))
                            .with(ObjectAnimator
                                    .ofFloat(imageItemShow,
                                            View.SCALE_Y, startScaleFinal));
                    set.setDuration(shortAnimationDuration);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if(dialogBoolean){
                                dialog.show();
                                dialogBoolean=false;
                            }
                            thumbView.setAlpha(1f);
                            imageItemShow.setVisibility(View.GONE);
                            currentAnimator = null;
                            imageItemShowitemCode ="";
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            if(dialogBoolean){
                                dialog.show();
                                dialogBoolean=false;
                            }
                            thumbView.setAlpha(1f);
                            imageItemShow.setVisibility(View.GONE);
                            currentAnimator = null;
                            imageItemShowitemCode ="";
                        }
                    });
                    set.start();
                    currentAnimator = set;
                }
            });

            URL imageUrl = null;
            try {
                imageUrl = new URL(((CssdProject) getApplication()).getxUrl() + "cssd_image/"+itemCode+"_pic1"+".PNG");
                Log.d("tog","result = "+imageUrl);
            } catch (MalformedURLException e) {
                Log.d("tog","result = "+e);
                throw new RuntimeException(e);
            }

            Picasso.get().load(String.valueOf(imageUrl)).networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageItemShow);
        }
    }
}