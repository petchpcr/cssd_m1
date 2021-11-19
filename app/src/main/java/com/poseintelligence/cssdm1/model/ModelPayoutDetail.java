package com.poseintelligence.cssdm1.model;

public class ModelPayoutDetail {
    String ID;
    String itemcode;
    String itemname;
    String itemStockID;
    String Qty;
    String Borrow_Balance_Qty;
    String Balance_Qty;
    String QR_Qty;
    String Enter_Qty;
    int index;

    public ModelPayoutDetail(String ID, String itemcode, String itemname, String itemStockID, String qty, String borrow_Balance_Qty, String balance_Qty, String QR_Qty, String enter_Qty, int index) {
        this.ID = ID;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.itemStockID = itemStockID;
        Qty = qty;
        Borrow_Balance_Qty = borrow_Balance_Qty;
        Balance_Qty = balance_Qty;
        this.QR_Qty = QR_Qty;
        Enter_Qty = enter_Qty;
        this.index = index;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getItemcode() {
        return itemcode;
    }

    public String getEnter_Qty() {
        return Enter_Qty;
    }

    public void setEnter_Qty(String enter_Qty) {
        Enter_Qty = enter_Qty;
    }

    public void setItemcode(String itemcode) {
        this.itemcode = itemcode;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getItemStockID() {
        return itemStockID;
    }

    public void setItemStockID(String itemStockID) {
        this.itemStockID = itemStockID;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getBorrow_Balance_Qty() {
        return Borrow_Balance_Qty;
    }

    public void setBorrow_Balance_Qty(String borrow_Balance_Qty) {
        Borrow_Balance_Qty = borrow_Balance_Qty;
    }

    public String getBalance_Qty() {
        return Balance_Qty;
    }

    public void setBalance_Qty(String balance_Qty) {
        Balance_Qty = balance_Qty;
    }

    public String getQR_Qty() {
        return QR_Qty;
    }

    public int getEnterQty() {
        return Integer.valueOf(Enter_Qty).intValue();
    }

    public int getQRQty() {
        return Integer.valueOf(QR_Qty).intValue();
    }

    public int getBalanceQty() {
        return Integer.valueOf(Balance_Qty).intValue();
    }

    public int getIncQR_Qty() {
        return Integer.valueOf(QR_Qty).intValue()+1;
    }

    public void setQR_Qty(String QR_Qty) {
        this.QR_Qty = QR_Qty;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNo() {
        return Integer.toString(index+1);
    }
}
