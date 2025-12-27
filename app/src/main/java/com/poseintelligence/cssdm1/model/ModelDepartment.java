package com.poseintelligence.cssdm1.model;

public class ModelDepartment {

    String ID;
    String DepName;
    String DepName2;
    String IsWeb;
    String IsCancel;
    String IsUrgent;

    public ModelDepartment(String ID, String DepName) {
        this.ID = ID;
        this.DepName = DepName;
    }

    public ModelDepartment(String ID, String DepName, String DepName2, String isCancel) {
        this.ID = ID;
        this.DepName = DepName;
        this.DepName2 = DepName2;
        IsCancel = isCancel;
    }

    public ModelDepartment(String ID, String DepName, String DepName2, String IsWeb, String isCancel) {
        this.ID = ID;
        this.DepName = DepName;
        this.DepName2 = DepName2;
        this.IsWeb = IsWeb;
        IsCancel = isCancel;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDepName() {
        return DepName;
    }

    public void setDepName(String DepName) {
        this.DepName = DepName;
    }

    public String getDepName2() {
        return DepName2;
    }

    public void setDepName2(String DepName2) {
        this.DepName2 = DepName2;
    }

    public String getIsWeb() {
        return IsWeb;
    }

    public void setIsWeb(String isWeb) {
        IsWeb = isWeb;
    }

    public String getIsCancel() {
        return IsCancel;
    }

    public void setIsCancel(String isCancel) {
        IsCancel = isCancel;
    }

    public String getIsUrgent() {
        return IsUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        IsUrgent = isUrgent;
    }
}
