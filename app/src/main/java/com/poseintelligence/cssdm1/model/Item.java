package com.poseintelligence.cssdm1.model;

public class Item {
    private String Row_id;
    private String ItemStockID;
    private String Name;
    private String usagecode;
    private String WashDetailID;
    private String SterileDetailID;
    private String SterileProgramID;
    private boolean chk;

    public Item(String row_id, String itemStockID, String name, String usagecode, String washDetailID, String sterileDetailID, String sterileProgramID, boolean chk) {
        Row_id = row_id;
        ItemStockID = itemStockID;
        Name = name;
        this.usagecode = usagecode;
        WashDetailID = washDetailID;
        SterileDetailID = sterileDetailID;
        SterileProgramID = sterileProgramID;
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

    public String getSterileProgramID() {
        return SterileProgramID;
    }

    public void setSterileProgramID(String sterileProgramID) {
        SterileProgramID = sterileProgramID;
    }
}
