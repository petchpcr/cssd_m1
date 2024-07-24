package com.poseintelligence.cssdm1.model;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ModelItemSelectInBasket{
    public TextView macname;
    public TextView mac;
    public ImageView mac_image;
    public LinearLayout ll;

    public void setItemSelect(TextView macname, TextView mac, ImageView mac_image, LinearLayout ll) {
        this.macname = macname;
        this.mac = mac;
        this.mac_image = mac_image;
        this.ll = ll;
    }
}