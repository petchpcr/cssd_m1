package com.poseintelligence.cssdm1.model;

public class ModelItemstockReceiveDetail {
    String  ID,
            Qty,
            Stock_Qty,
            itemname,
            itemcode;

    int index = 0;

    public ModelItemstockReceiveDetail(String ID, String qty, String stock_Qty, String itemname, String itemcode, int index) {
        this.ID = ID;
        Qty = qty;
        Stock_Qty = stock_Qty;
        this.itemname = itemname;
        this.itemcode = itemcode;
        this.index = index;
    }

    public String getStock_Qty() {
        return Stock_Qty;
    }

    public void setStock_Qty(String stock_Qty) {
        Stock_Qty = stock_Qty;
    }

    public String getItemcode() {
        return itemcode;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getNo() {
        return (index == -1) ? "" : ((index + 1) + ".");
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }


}
