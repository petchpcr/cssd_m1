package com.poseintelligence.cssdm1.model;

public class ModelDisplayDocRemark {
    String ID;
    String SensterileDocNo;
    String DepName2;
    String itemname;
    String UsageCode;
    String NameType;
    String Note;
    String IsPicture;
    String Picture;
    String Picture2;
    String Picture3;
    String Pictruetext;
    String Pictruetext2;
    String Pictruetext3;
    String MutiPic_Remark;

    public ModelDisplayDocRemark(String ID, String SensterileDocNo, String DepName2, String itemname, String UsageCode, String NameType, String Note, String IsPicture, String Picture, String Picture2, String Picture3, String Pictruetext, String Pictruetext2, String Pictruetext3, String MutiPic_Remark) {
        this.ID = ID;
        this.SensterileDocNo = SensterileDocNo;
        this.DepName2 = DepName2;
        this.itemname = itemname;
        this.UsageCode = UsageCode;
        this.NameType = NameType;
        this.Note = Note;
        this.IsPicture = IsPicture;
        this.Picture = Picture;
        this.Picture2 = Picture2;
        this.Picture3 = Picture3;
        this.Pictruetext = Pictruetext;
        this.Pictruetext2 = Pictruetext2;
        this.Pictruetext3 = Pictruetext3;
        this.MutiPic_Remark = MutiPic_Remark;
    }

    public String getPictruetext() { return Pictruetext; }

    public void setPictruetext(String pictruetext) { Pictruetext = pictruetext; }

    public String getPictruetext2() { return Pictruetext2; }

    public void setPictruetext2(String pictruetext2) { Pictruetext2 = pictruetext2; }

    public String getPictruetext3() { return Pictruetext3; }

    public void setPictruetext3(String pictruetext3) { Pictruetext3 = pictruetext3; }

    public String getPicture2() { return Picture2; }

    public void setPicture2(String picture2) { Picture2 = picture2; }

    public String getPicture3() { return Picture3; }

    public void setPicture3(String picture3) { Picture3 = picture3; }

    public String getMutiPic_Remark() { return MutiPic_Remark; }

    public void setMutiPic_Remark(String mutiPic_Remark) { MutiPic_Remark = mutiPic_Remark; }

    public String getIsPicture() {
        return IsPicture;
    }

    public void setIsPicture(String isPicture) {
        IsPicture = isPicture;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSensterileDocNo() {
        return SensterileDocNo;
    }

    public void setSensterileDocNo(String sensterileDocNo) {
        SensterileDocNo = sensterileDocNo;
    }

    public String getDepName2() {
        return DepName2;
    }

    public void setDepName2(String depName2) {
        DepName2 = depName2;
    }

    public String getItemname() {
        return itemname;
    }

    public void setItemname(String itemname) {
        this.itemname = itemname;
    }

    public String getUsageCode() {
        return UsageCode;
    }

    public void setUsageCode(String usageCode) {
        UsageCode = usageCode;
    }

    public String getNameType() {
        return NameType;
    }

    public void setNameType(String nameType) {
        NameType = nameType;
    }

    public String getNote() {
        return Note;
    }

    public void setNote(String note) {
        Note = note;
    }
}

