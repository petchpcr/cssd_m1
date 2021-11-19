package com.poseintelligence.cssdm1.model;

public class ModelDepartment {

    String ID;
    String DepName;
    String DepName2;
    String IsCancel;

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

    public String getIsCancel() {
        return IsCancel;
    }

    public void setIsCancel(String isCancel) {
        IsCancel = isCancel;
    }

}
