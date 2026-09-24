package com.poseintelligence.cssdm1.model;

public class ModelParStockItem {
    private String id;
    private String itemcode;
    private String itemname;
    private String parQty;
    private String qty;
    private String remark;

    public ModelParStockItem(String id, String itemcode, String itemname, String parQty, String qty, String remark) {
        this.id = id;
        this.itemcode = itemcode;
        this.itemname = itemname;
        this.parQty = parQty;
        this.qty = qty;
        this.remark = remark;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getParQty() {
        return parQty;
    }

    public void setParQty(String parQty) {
        this.parQty = parQty;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
