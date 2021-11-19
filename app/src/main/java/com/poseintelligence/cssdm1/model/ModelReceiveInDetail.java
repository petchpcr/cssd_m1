package com.poseintelligence.cssdm1.model;

public class ModelReceiveInDetail {
    String DocNo;
    String UsageCode;
    String IsStatus;
    String ItemName;
    String Qty;
    String IsStatusDetail;
    String ChkReceiveIn;
    String OccuranceTypeID;
    String chkreceiveInItem;
    boolean ChkStatus;
    boolean ChkClick;
    int index;

    public ModelReceiveInDetail(
            String DocNo,
            String UsageCode,
            String IsStatus,
            String ItemName,
            String Qty,
            String IsStatusDetail,
            String ChkReceiveIn,
            String OccuranceTypeID,
            boolean ChkStatus,
            String chkreceiveInItem,
            int index
    ) {
        this.DocNo = DocNo;
        this.UsageCode = UsageCode;
        this.IsStatus = IsStatus;
        this.ItemName = ItemName;
        this.IsStatusDetail = IsStatusDetail;
        this.Qty = Qty;
        this.ChkReceiveIn = ChkReceiveIn;
        this.OccuranceTypeID = OccuranceTypeID;
        this.ChkStatus = ChkStatus;
        this.chkreceiveInItem = chkreceiveInItem;
        this.index = index;
    }

    public ModelReceiveInDetail(

            boolean ChkStatus
    ) {

        this.ChkStatus = ChkStatus;
    }

    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }

    public String getDocNo() {
        return DocNo;
    }
    public void setDocNo(String docNo) { this.DocNo = docNo; }

    public String getChkreceiveInItem() {
        return chkreceiveInItem;
    }
    public void setChkreceiveInItem(String chkreceiveInItem) { this.chkreceiveInItem = chkreceiveInItem; }

    public String getUsageCode() { return UsageCode; }
    public void setUsageCode(String UsageCode) {
        this.UsageCode = UsageCode;
    }

    public String getIsStatus() {
        return this.IsStatus;
    }
    public void setIsStatus(String isStatus) {
        this.IsStatus = isStatus;
    }

    public String getItemName() {
        return this.ItemName;
    }
    public void setItemName(String ItemName) {
        this.ItemName = ItemName;
    }

    public String getIsStatusDetail() {
        return this.IsStatusDetail;
    }
    public void setIsStatusDetail(String IsStatusDetail) {
        this.IsStatusDetail = IsStatusDetail;
    }

    public String getQty() {
        return Qty;
    }
    public void setQty(String qty) {
        Qty = qty;
    }

    public String getChkReceiveIn() {
        return this.ChkReceiveIn;
    }
    public void setChkReceiveIn(String ChkReceiveIn) {
        this.ChkReceiveIn = ChkReceiveIn;
    }

    public String getOccuranceTypeID() {
        return this.OccuranceTypeID;
    }
    public void setOccuranceTypeID(String OccuranceTypeID) {
        this.OccuranceTypeID = OccuranceTypeID;
    }

    public boolean getChkStatus() {
        return this.ChkStatus;
    }
    public void setChkStatus(boolean ChkStatus) {
        this.ChkStatus = ChkStatus;
    }

    public boolean getChkClick() {
        return this.ChkClick;
    }
    public void setChkClick(boolean ChkClick) {
        this.ChkClick = ChkClick;
    }

}