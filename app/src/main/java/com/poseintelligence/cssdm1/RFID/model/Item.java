package com.poseintelligence.cssdm1.RFID.model;

public class Item {
    private String RFID;
    private String Row_id;
    private String usagecode;
    private String isStatus;
    private String statusName;

    public Item(String RFID) {
        this.RFID = RFID;
        this.usagecode = "usagecode";
    }

    public String getRFID() {
        return RFID;
    }

    public void setRFID(String xRFID) {
        RFID = xRFID;
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
}
