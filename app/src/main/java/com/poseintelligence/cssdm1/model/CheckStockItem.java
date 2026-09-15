package com.poseintelligence.cssdm1.model;

public class CheckStockItem {
    private String RowID;
    private String ItemCode;
    private String itemname;
    private String DeptID;
    private String DepName;
    private String CheckQty;
    private String Qty;

    public CheckStockItem(String RowID, String ItemCode, String itemname, String DeptID, String DepName, String CheckQty, String Qty) {
        this.RowID = RowID;
        this.ItemCode = ItemCode;
        this.itemname = itemname;
        this.DeptID = DeptID;
        this.DepName = DepName;
        this.CheckQty = CheckQty;
        this.Qty = Qty;
    }

    public String getRowID() {
        return RowID;
    }

    public void setRowID(String rowID) {
        RowID = rowID;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getDeptID() {
        return DeptID;
    }

    public void setDeptID(String deptID) {
        DeptID = deptID;
    }

    public String getDepName() {
        return DepName;
    }

    public void setDepName(String depName) {
        DepName = depName;
    }

    public String getCheckQty() {
        return CheckQty;
    }

    public void setCheckQty(String checkQty) {
        CheckQty = checkQty;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public boolean isComplete() {
        return toInt(Qty) > 0 && toInt(CheckQty) >= toInt(Qty);
    }

    private int toInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }
}
