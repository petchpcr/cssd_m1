package com.poseintelligence.cssdm1.model;

public class ModelPayoutDetails {
    String  ID,
            itemcode,
            itemname,
            ItemStockID,
            UsageCode,
            Pay_Qty,
            Stock_Qty,
            Qty,
            Balance,
            RefDocNo,
            Program,
            IsReceiveNotSterile,
            QtyUrgent;

    int index = 0;

    public ModelPayoutDetails(String ID, String itemcode, String itemname, String itemStockID, String usageCode, String pay_Qty, String stock_Qty, String qty, String balance, String refDocNo, String Program, String IsReceiveNotSterile, int index) {
        this.ID = ID;
        this.itemcode = itemcode;
        this.itemname = itemname;
        ItemStockID = itemStockID;
        UsageCode = usageCode;

        Pay_Qty = pay_Qty;
        Stock_Qty = stock_Qty;
        Qty = qty;
        Balance = balance;
        RefDocNo = refDocNo;

        this.Program = Program;
        this.IsReceiveNotSterile = IsReceiveNotSterile;
        this.index = index;
        this.QtyUrgent = "0";
    }

    public String getIsReceiveNotSterile() { return IsReceiveNotSterile;
    }

    public void setIsReceiveNotSterile(String isReceiveNotSterile) { IsReceiveNotSterile = isReceiveNotSterile;
    }

    public String getIsWasting() {
        return Program == null ? "0" : Program;
    }

    public String getProgram() {
        return Program;
    }

    public void setProgram(String program) {
        Program = program;
    }

    public String getBalance() {
        return Balance;
    }

    public void setBalance(String balance) {
        Balance = balance;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getRefDocNo() {
        return RefDocNo;
    }

    public void setRefDocNo(String refDocNo) {
        RefDocNo = refDocNo;
    }

    public String getQty() {
        return Qty;
    }

    public int getPay_Qty_() {

        int Qty_ = 0;

        try{
            Qty_ = Integer.valueOf(Pay_Qty).intValue();
        }catch (Exception e){

        }

        return Qty_;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public int getIndex() {
        return index;
    }

    public String getNo() {
        return (this.index + 1) + ".";
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getItemcode() {
        return itemcode;
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
        return ItemStockID;
    }

    public void setItemStockID(String itemStockID) {
        ItemStockID = itemStockID;
    }

    public String getUsageCode() {
        return UsageCode;
    }

    public void setUsageCode(String usageCode) {
        UsageCode = usageCode;
    }

    public String getPay_Qty() {
        return Pay_Qty;
    }

    public void setPay_Qty(String pay_Qty) {
        Pay_Qty = pay_Qty;
    }

    public String getStock_Qty() {
        return Stock_Qty;
    }

    public void setStock_Qty(String stock_Qty) {
        Stock_Qty = stock_Qty;
    }

    public String getQtyUrgent() {
        return QtyUrgent;
    }

    public void setQtyUrgent(String qtyUrgent) {
        QtyUrgent = qtyUrgent;
    }
}
