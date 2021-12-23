package com.poseintelligence.cssdm1.Menu_Sterile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.TextView;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.adapter.ListBoxBasketAdapter;
import com.poseintelligence.cssdm1.adapter.ListBoxMachineAdapter;
import com.poseintelligence.cssdm1.adapter.ListItemBasketAdapter;
import com.poseintelligence.cssdm1.model.BasketTag;
import com.poseintelligence.cssdm1.model.Item;
import com.poseintelligence.cssdm1.model.ModelMachine;

import java.util.ArrayList;

public class SterileActivity extends AppCompatActivity {

    RecyclerView list_mac;
    RecyclerView list_basket;
    ListView list_item_basket;
    TextView bt_delete;
    TextView bt_select_all;
    LinearLayoutManager lm;
    boolean is_select_all = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sterile);

        byIntent();

        byWidget();
    }

    public void byIntent(){

    }

    public void byWidget(){
        list_mac = (RecyclerView) findViewById(R.id.list_mac);
        list_basket = (RecyclerView) findViewById(R.id.list_basket);
        list_item_basket = (ListView) findViewById(R.id.list_item_basket);

        bt_delete = (TextView) findViewById(R.id.bt_delete);
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        bt_select_all = (TextView) findViewById(R.id.bt_select);
        bt_select_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!is_select_all){
                    is_select_all = true;
                    bt_select_all.setText("Unselect all");
                    show_bt_delete(is_select_all);
                }else{
                    is_select_all = false;
                    bt_select_all.setText("Select all");
                    show_bt_delete(is_select_all);
                }

            }
        });

        ArrayList<ModelMachine> list = new ArrayList<>();
        list.add(new ModelMachine("1","H2O2-222"));
        list.add(new ModelMachine("2","2"));
        list.add(new ModelMachine("3","3"));
        list.add(new ModelMachine("4","4"));
        list.add(new ModelMachine("5","5"));
        list.add(new ModelMachine("6","6"));
        list.add(new ModelMachine("7","7"));
        list.add(new ModelMachine("8","8"));
        list.add(new ModelMachine("9","9"));

        ArrayList<BasketTag> xlist_basket = new ArrayList<>();
        xlist_basket.add(new BasketTag("1","Basket Tongtang","1","1",1));
        xlist_basket.add(new BasketTag("2","Basket 2","1","1",5));
        xlist_basket.add(new BasketTag("3","3","1","1",7));
        xlist_basket.add(new BasketTag("4","4","1","1",3));
        xlist_basket.add(new BasketTag("5","5","1","1",5));
        xlist_basket.add(new BasketTag("6","6","1","1",2));
        xlist_basket.add(new BasketTag("7","7","1","1",11));
        xlist_basket.add(new BasketTag("8","8","1","1",91));
        xlist_basket.add(new BasketTag("9","9","1","1",31));
//        list_mac.setAdapter(new ListBoxMachineAdapter(this,list));
        lm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        list_mac.setLayoutManager(lm);
        list_mac.setAdapter(new ListBoxMachineAdapter(this, list,list_mac));

        list_basket.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        list_basket.setAdapter(new ListBoxBasketAdapter(this, xlist_basket,list_basket));

        ArrayList<Item> xlist_item_basket = new ArrayList<>();
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("1","9999",false));
        xlist_item_basket.add(new Item("1","9999",false));
        list_item_basket.setAdapter(new ListItemBasketAdapter(this,xlist_item_basket));

    }

    public void show_bt_delete(boolean s){
        if(s){
            bt_delete.setVisibility(View.VISIBLE);
        }else{
            bt_delete.setVisibility(View.GONE);
        }
    }

}