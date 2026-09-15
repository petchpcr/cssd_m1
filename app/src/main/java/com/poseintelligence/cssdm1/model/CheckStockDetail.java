package com.poseintelligence.cssdm1.model;

public class CheckStockDetail {
    private String StockRowID;
    private String UsageCode;
    private String itemname;
    private String DepID;
    private String DepName;
    private String date;
    private String time;
    private String OverExp;
    private String BeforeExp;
    private String IsCheckStock;

    public CheckStockDetail(String StockRowID, String UsageCode, String itemname, String DepID,
                            String DepName, String date, String time, String OverExp,
                            String BeforeExp, String IsCheckStock) {
        this.StockRowID = StockRowID;
        this.UsageCode = UsageCode;
        this.itemname = itemname;
        this.DepID = DepID;
        this.DepName = DepName;
        this.date = date;
        this.time = time;
        this.OverExp = OverExp;
        this.BeforeExp = BeforeExp;
        this.IsCheckStock = IsCheckStock;
    }

    public String getStockRowID() {
        return StockRowID;
    }

    public String getUsageCode() {
        return UsageCode;
    }

    public String getItemname() {
        return itemname;
    }

    public String getDepID() {
        return DepID;
    }

    public String getDepName() {
        return DepName;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getOverExp() {
        return OverExp;
    }

    public String getBeforeExp() {
        return BeforeExp;
    }

    public String getIsCheckStock() {
        return IsCheckStock;
    }

    public boolean isChecked() {
        return "1".equals(IsCheckStock);
    }

    public boolean isMainDep() {
        return "20".equals(DepID);
    }

    public boolean isOverExp() {
        return "yes".equalsIgnoreCase(OverExp);
    }

    public boolean isBeforeExp() {
        return "yes".equalsIgnoreCase(BeforeExp);
    }
}
