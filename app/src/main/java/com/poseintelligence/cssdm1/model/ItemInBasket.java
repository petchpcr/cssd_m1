package com.poseintelligence.cssdm1.model;

public class ItemInBasket {
    private String Row_id;
    private String ItemStockID;
    private String Name;
    private String usagecode;
    private String SSDetailID;
    private String WashDetailID;
    private String SterileDetailID;
    private boolean chk;

    public ItemInBasket(String row_id, String itemStockID, String name, String usagecode,String SSDetailID, String washDetailID, String sterileDetailID, boolean chk) {
        Row_id = row_id;
        ItemStockID = itemStockID;
        Name = name;
        this.usagecode = usagecode;
        this.SSDetailID = SSDetailID;
        WashDetailID = washDetailID;
        SterileDetailID = sterileDetailID;
        this.chk = chk;
    }

    public String getRow_id() {
        return Row_id;
    }

    public void setRow_id(String row_id) {
        Row_id = row_id;
    }

    public String getItemCode() {
        return usagecode;
    }

    public void setItemCode(String usagecode) {
        this.usagecode = usagecode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isChk() {
        return chk;
    }

    public void setChk(boolean chk) {
        this.chk = chk;
    }

    public String getItemStockID() {
        return ItemStockID;
    }

    public void setItemStockID(String itemStockID) {
        ItemStockID = itemStockID;
    }

    public String getWashDetailID() {
        return WashDetailID;
    }

    public void setWashDetailID(String washDetailID) {
        WashDetailID = washDetailID;
    }

    public String getSterileDetailID() {
        return SterileDetailID;
    }

    public void setSterileDetailID(String sterileDetailID) {
        SterileDetailID = sterileDetailID;
    }

    public String getSSDetailID() {
        return SSDetailID;
    }

    public void setSSDetailID(String SSDetailID) {
        this.SSDetailID = SSDetailID;
    }
}
