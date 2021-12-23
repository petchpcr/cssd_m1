package com.poseintelligence.cssdm1.model;

public class ModelChkMachine {
    String ID;
    String CreateDate;
    String LastUpdate;
    String MachineID;
    String MachineName;
    String IsResult;
    String Pic1;
    String Pic2;
    String Remark;
    boolean enableEdit;

    public ModelChkMachine(String ID, String createDate, String lastUpdate, String machineID, String machineName, String isResult, String pic1, String pic2, String remark, boolean enableEdit) {
        this.ID = ID;
        CreateDate = createDate;
        LastUpdate = lastUpdate;
        MachineID = machineID;
        MachineName = machineName;
        IsResult = isResult;
        Pic1 = pic1;
        Pic2 = pic2;
        Remark = remark;
        this.enableEdit = enableEdit;
    }

    public String getID() {
        return ID;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public String getLastUpdate() {
        return LastUpdate;
    }

    public String getMachineID() {
        return MachineID;
    }

    public String getMachineName() {
        return MachineName;
    }

    public String getIsResult() {
        return IsResult;
    }

    public String getPic1() {
        return Pic1;
    }

    public String getPic2() {
        return Pic2;
    }

    public String getRemark() {
        return Remark;
    }

    public boolean isEnableEdit() {
        return enableEdit;
    }
}
