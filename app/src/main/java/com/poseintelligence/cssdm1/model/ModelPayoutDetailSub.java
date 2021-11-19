package com.poseintelligence.cssdm1.model;

public class ModelPayoutDetailSub {
    String  ID,
            ItemStockID,
            UsageCode;

    boolean IsCheck = false;

    int index = 0;

    public ModelPayoutDetailSub(String ID, String itemStockID, String usageCode, int index) {
        this.ID = ID;
        ItemStockID = itemStockID;
        UsageCode = usageCode;
        this.index = index;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isCheck() {
        return IsCheck;
    }

    public void setCheck(boolean check) {
        IsCheck = check;
    }
}
