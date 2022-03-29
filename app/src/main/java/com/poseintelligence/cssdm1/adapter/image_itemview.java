package com.poseintelligence.cssdm1.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.poseintelligence.cssdm1.CssdProject;
import com.poseintelligence.cssdm1.Menu_Remark.RemarkActivity;
import com.poseintelligence.cssdm1.R;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class image_itemview  extends RecyclerView.Adapter<image_itemview.MyViewHolder> {

    private Context context;
    private ArrayList<String> arrayList = new ArrayList<>();
    View.OnClickListener imageSlidertOnClickListener;

    public image_itemview(Context context, ArrayList<String> arrayList,View.OnClickListener imageSlidertOnClickListener) {
        this.context = context;
        this.arrayList = arrayList;
        this.imageSlidertOnClickListener = imageSlidertOnClickListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.image_itemview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        if(!arrayList.get(position).equals(null)){
            try {
                URL imageUrl1 = new URL(arrayList.get(position));
                Bitmap bmp = BitmapFactory.decodeStream(imageUrl1.openConnection().getInputStream());
                holder.img.setImageBitmap(bmp);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        holder.tvName.setText("รูปที่ "+(position+1));
        holder.img.setOnClickListener(imageSlidertOnClickListener);

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView img;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView4);
            img = itemView.findViewById(R.id.imageView3);
        }
    }
}