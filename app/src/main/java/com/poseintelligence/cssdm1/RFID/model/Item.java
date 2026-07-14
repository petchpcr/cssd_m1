package com.poseintelligence.cssdm1.RFID.model;

public class Item {
    private String tagRfid;
    private String rfidStatus;
    private String Row_id;
    private String usagecode;
    private String ItemCode;
    private String isStatus;
    private String ItemName;
    private String statusName;

    public Item(String RFID) {
        this.tagRfid = RFID;
        this.usagecode = "usagecode";
    }

    public String getTagRfid() {
        return tagRfid;
    }

    public void setTagRfid(String xRFID) {
        tagRfid = xRFID;
    }

    public String getRfidStatus() {
        return rfidStatus;
    }

    public void setRfidStatus(String rfidStatus) {
        this.rfidStatus = rfidStatus;
    }

    public String getRow_id() {
        return Row_id;
    }

    public void setRow_id(String row_id) {
        Row_id = row_id;
    }

    public String getUsagecode() {
        return usagecode;
    }

    public void setUsagecode(String xusagecode) {
        usagecode = xusagecode;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String xItemCode) {
        ItemCode = xItemCode;
    }

    public String getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(String xisStatus) {
        isStatus = xisStatus;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String xstatusName) {
        statusName = xstatusName;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }
}
