package com.poseintelligence.cssdm1.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Movie;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.viewpager2.widget.ViewPager2;

import com.github.islamkhsh.CardSliderViewPager;
import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.Menu_Remark.RemarkActivity;
import com.poseintelligence.cssdm1.Menu_Remark.dialog_delete_pic_remark;
import com.poseintelligence.cssdm1.model.ModelDisplayDocRemark;

import java.util.ArrayList;
import java.util.List;

public class ListDisplayDocRemarkAdapter extends ArrayAdapter {

    private final List<ModelDisplayDocRemark> DATA_MODEL;
    private final Activity context;
    private String Deptid;
    String codeData_pic1;
//    String TitlePic;
    String Url;

    public ListDisplayDocRemarkAdapter(Activity context, List<ModelDisplayDocRemark> DATA_MODEL) {
        super(context, R.layout.activity_list_display_doc_remark_adapter, DATA_MODEL);
        this.context = context;
        this.DATA_MODEL = DATA_MODEL;
        Url = ((CssdProject) context.getApplication()).getxUrl();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.activity_list_display_doc_remark_adapter, parent, false);

        TextView txt_no = ( TextView ) view.findViewById(R.id.txt_no);
        TextView txt_docno = ( TextView ) view.findViewById(R.id.txt_docno);
        TextView txt_itemname = ( TextView ) view.findViewById(R.id.txt_itemname);
        TextView txt_usagecode = ( TextView ) view.findViewById(R.id.txt_usagecode);
        TextView txt_type = ( TextView ) view.findViewById(R.id.txt_type);
        RelativeLayout R1 = ( RelativeLayout ) view.findViewById(R.id.R1);
        RelativeLayout R2 = ( RelativeLayout ) view.findViewById(R.id.R2);
        ImageView image1 = ( ImageView ) view.findViewById(R.id.image1);
        ImageView del_pic = ( ImageView ) view.findViewById(R.id.del_pic);
        ImageView del_pic1 = ( ImageView ) view.findViewById(R.id.del_pic1);

        ViewPager2 imageSlider = view.findViewById(R.id.img_view);

        ArrayList<String> arrayList = new ArrayList<>();


        if (DATA_MODEL.get(position).getMutiPic_Remark().equals("1")){
            R1.setVisibility(View.GONE);
            R2.setVisibility(View.VISIBLE);

            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo()," R2 ");
        }else {
            R1.setVisibility(View.VISIBLE);
            R2.setVisibility(View.GONE);

            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo()," R1 ");
        }

        if (DATA_MODEL.get(position).getMutiPic_Remark().equals("1")){

            image1.setVisibility(View.GONE);

            txt_no.setTextColor(Color.RED);
            txt_docno.setTextColor(Color.RED);
            txt_itemname.setTextColor(Color.RED);
            txt_usagecode.setTextColor(Color.RED);
            txt_type.setTextColor(Color.RED);
            del_pic1.setVisibility(View.GONE);
            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo(),"getIsPicture = "+DATA_MODEL.get(position).getIsPicture());
            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo(),"getPicture = "+DATA_MODEL.get(position).getPicture());
            int num_pic = 0;

            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo()," slideModels1");
            if (DATA_MODEL.get(position).getPicture().equals("") || DATA_MODEL.get(position).getPicture().equals("null")){
                arrayList.add("null");
            }else {
                arrayList.add(Url+"cssd_image/"+DATA_MODEL.get(position).getPicture());
                del_pic1.setVisibility(View.VISIBLE);
                num_pic++;
            }

            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo()," slideModels2");
            if (DATA_MODEL.get(position).getPicture2().equals("") || DATA_MODEL.get(position).getPicture2().equals("null")){
                arrayList.add("null");
            }else {
                arrayList.add(Url+"cssd_image/"+DATA_MODEL.get(position).getPicture2());
                del_pic1.setVisibility(View.VISIBLE);
                num_pic++;
            }

            Log.d("tog_remark"+DATA_MODEL.get(position).getSensterileDocNo()," slideModels3");
            if (DATA_MODEL.get(position).getPicture3().equals("") || DATA_MODEL.get(position).getPicture3().equals("null")){
                arrayList.add("null");
            }else {
                arrayList.add(Url+"cssd_image/"+DATA_MODEL.get(position).getPicture3());
                del_pic1.setVisibility(View.VISIBLE);
                num_pic++;
            }

            View.OnClickListener imageSlidertOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = imageSlider.getCurrentItem();
                    if (pos == 0) {
                        Log.d("image1", "1");
                        if (DATA_MODEL.get(position).getPicture().equals("") || DATA_MODEL.get(position).getPicture().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "1");
                        } else {
                            open_pic(position , arrayList);
                        }
                    } else if (pos == 1) {
                        Log.d("image1", "2");
                        if (DATA_MODEL.get(position).getPicture2().equals("") || DATA_MODEL.get(position).getPicture2().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "2");
                        } else {
                            open_pic(position , arrayList);
                        }
                    } else if (pos == 2) {
                        Log.d("image1", "3");
                        if (DATA_MODEL.get(position).getPicture3().equals("") || DATA_MODEL.get(position).getPicture3().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "3");
                        } else {
                            open_pic(position , arrayList);
                        }
                    }
                }
            };

            image_itemview MyAdapter = new image_itemview(context, arrayList,imageSlidertOnClickListener);

            imageSlider.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

            imageSlider.setAdapter(MyAdapter);


//            if(num_pic == 3){
            if(num_pic >= 1){
                txt_no.setTextColor(Color.BLACK);
                txt_docno.setTextColor(Color.BLACK);
                txt_itemname.setTextColor(Color.BLACK);
                txt_usagecode.setTextColor(Color.BLACK);
                txt_type.setTextColor(Color.BLACK);
            }
        }else {
            if (DATA_MODEL.get(position).getIsPicture().equals("1")){
                del_pic.setVisibility(View.VISIBLE);
                txt_no.setTextColor(Color.BLACK);
                txt_docno.setTextColor(Color.BLACK);
                txt_itemname.setTextColor(Color.BLACK);
                txt_usagecode.setTextColor(Color.BLACK);
                txt_type.setTextColor(Color.BLACK);
                Log.d("DJGJDGVJ",DATA_MODEL.get(position).getPictruetext());
                codeData_pic1 = "data:image/jpeg;base64,"+DATA_MODEL.get(position).getPictruetext();
                codeData_pic1 = codeData_pic1.replace("data:image/jpeg;base64,","");
                byte[] code_pic1 = Base64.decode(codeData_pic1,Base64.DEFAULT);
                Bitmap bitmap_pic1 = BitmapFactory.decodeByteArray(code_pic1,0,code_pic1.length);
                image1.setImageBitmap(bitmap_pic1);
            }else {
                txt_no.setTextColor(Color.RED);
                txt_docno.setTextColor(Color.RED);
                txt_itemname.setTextColor(Color.RED);
                txt_usagecode.setTextColor(Color.RED);
                txt_type.setTextColor(Color.RED);
                del_pic.setVisibility(View.GONE);
            }
        }

        del_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                builder.setTitle("ยืนยัน");
                builder.setMessage("ต้องการลบรูปภาพหรือไม่");
                builder.setPositiveButton("ตกลง",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                (( RemarkActivity ) context).DelPicture(DATA_MODEL.get(position).getID(),DATA_MODEL.get(position).getPicture());
                            }
                        });
                builder.setNegativeButton("ยกเลิก", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        del_pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DATA_MODEL.get(position).getPicture().equals("null") && DATA_MODEL.get(position).getPicture2().equals("null") && DATA_MODEL.get(position).getPicture3().equals("null")){

                }else {
                    Intent intent = new Intent(context, dialog_delete_pic_remark.class);
                    intent.putExtra("Picture",DATA_MODEL.get(position).getPicture());
                    intent.putExtra("Picture2",DATA_MODEL.get(position).getPicture2());
                    intent.putExtra("Picture3",DATA_MODEL.get(position).getPicture3());
                    intent.putExtra("ID",DATA_MODEL.get(position).getID());
                    context.startActivity(intent);
                }
            }
        });

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("DJGJDGVJ",DATA_MODEL.get(position).getIsPicture());
                Log.d("janetest",image1.getDrawingCache()+"");
                if (DATA_MODEL.get(position).getIsPicture().equals("0")){
                    (( RemarkActivity ) context).OpenCamera(DATA_MODEL.get(position).getID(),DATA_MODEL.get(position).getSensterileDocNo(),"1");
                }else {

//                    open_pic(position , slideModels1,image1.getDrawingCache());
                }

            }
        });

        txt_no.setText(position+1+"");
        txt_docno.setText(DATA_MODEL.get(position).getSensterileDocNo()+"  "+DATA_MODEL.get(position).getDepName2());
//        txt_docno.setText(DATA_MODEL.get(position).getMutiPic_Remark());
        txt_itemname.setText(DATA_MODEL.get(position).getItemname());
        txt_usagecode.setText(DATA_MODEL.get(position).getUsageCode());
        if (DATA_MODEL.get(position).getNote().equals("")||DATA_MODEL.get(position).getNote().equals("null")){
            txt_type.setText("ประเภท : "+DATA_MODEL.get(position).getNameType());
        }else {
            txt_type.setText("ประเภท : "+DATA_MODEL.get(position).getNameType()+"   "+"( "+DATA_MODEL.get(position).getNote()+" )");
        }

        // =========================================================================================
        return view;
    }

    public void open_pic(int position,ArrayList<String> arrayList){
        codeData_pic1 = "data:image/jpeg;base64,"+DATA_MODEL.get(position).getPictruetext();
        codeData_pic1 = codeData_pic1.replace("data:image/jpeg;base64,","");
        byte[] code_pic1 = Base64.decode(codeData_pic1,Base64.DEFAULT);
        Bitmap bitmap_pic12 = BitmapFactory.decodeByteArray(code_pic1,0,code_pic1.length);

        AlertDialog.Builder quitDialog = new AlertDialog.Builder(context);
        final View customLayout = context.getLayoutInflater().inflate( R.layout.activity_dialog_pic_remark, null);
        quitDialog.setView(customLayout);

        ViewPager2 al_imageslide = (ViewPager2) customLayout.findViewById(R.id.imageslide);
        TextView al_text_remark = (TextView) customLayout.findViewById(R.id.text_remark);
        if (DATA_MODEL.get(position).getNote().equals("")){
            al_text_remark.setText(DATA_MODEL.get(position).getNameType());
        }else {
            al_text_remark.setText(DATA_MODEL.get(position).getNameType()+"("+DATA_MODEL.get(position).getNote()+")");
        }
        ImageView al_images = (ImageView) customLayout.findViewById(R.id.images);

        AlertDialog alert = quitDialog.create();

        alert.show();

        if (DATA_MODEL.get(position).getMutiPic_Remark().equals("1")){

            View.OnClickListener imageSlidertOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = al_imageslide.getCurrentItem();
                    if (pos == 0) {
                        Log.d("image1", "1");
                        if (DATA_MODEL.get(position).getPicture().equals("") || DATA_MODEL.get(position).getPicture().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "1");
                        }
                    } else if (pos == 1) {
                        Log.d("image1", "2");
                        if (DATA_MODEL.get(position).getPicture2().equals("") || DATA_MODEL.get(position).getPicture2().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "2");
                        }
                    } else if (pos == 2) {
                        Log.d("image1", "3");
                        if (DATA_MODEL.get(position).getPicture3().equals("") || DATA_MODEL.get(position).getPicture3().equals("null")) {
                            ((RemarkActivity) context).OpenCamera(DATA_MODEL.get(position).getID(), DATA_MODEL.get(position).getSensterileDocNo(), "3");
                        }
                    }
                }
            };

            image_itemview MyAdapter = new image_itemview(context, arrayList,imageSlidertOnClickListener);

            al_imageslide.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);

            al_imageslide.setAdapter(MyAdapter);

            al_images.setVisibility(View.GONE);
            Log.d("image2","1");
        }else{

            al_images.setImageBitmap(bitmap_pic12);
            al_imageslide.setVisibility(View.GONE);
        }
    }
}