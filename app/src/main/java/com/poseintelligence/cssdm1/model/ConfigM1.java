package com.poseintelligence.cssdm1.model;

public class ConfigM1 {
    private String CngId;
    private String CngName;
    private String CngComment;
    private String btImg;
    private boolean isActive;
    private boolean isStatus;
    private boolean showBtn;

    public String getCngId() {
        return CngId;
    }
    public void setCngId(String CngId) {
        this.CngId = CngId;
    }

    public String getCngName() {
        return CngName;
    }
    public void setCngName(String CngName) {
        this.CngName = CngName;
    }

    public String getCngComment() {
        return CngComment;
    }
    public void setCngComment(String CngComment) {
        this.CngComment = CngComment;
    }

    public String getBtImg() {
        return btImg;
    }
    public void setBtImg(String btImg) {
        this.btImg = btImg;
    }

    public boolean getActive() {
        return isActive;
    }
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public boolean getStatus() { return isStatus; }
    public void setStatus(boolean isStatus) {
        this.isStatus = isStatus;
    }

    public boolean getShowBtn() { return showBtn; }
    public void setShowBtn(boolean showBtn) { this.showBtn = showBtn; }
}
