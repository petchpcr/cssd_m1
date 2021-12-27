package com.poseintelligence.cssdm1.model;

public class ModelMachine {
    String MachineID;
    String MachineName;
    String IsActive;
    String IsBrokenMachine;
    String DocNo;

    public ModelMachine(String machineID, String machineName, String isActive, String isBrokenMachine,String DocNo) {
        MachineID = machineID;
        MachineName = machineName;
        IsActive = isActive;
        IsBrokenMachine = isBrokenMachine;
        DocNo = DocNo;
    }

    public String getMachineID() {
        return MachineID;
    }

    public String getMachineName() {
        return MachineName;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = isActive;
    }

    public String getIsBrokenMachine() {
        return IsBrokenMachine;
    }

    public void setIsBrokenMachine(String isBrokenMachine) {
        IsBrokenMachine = isBrokenMachine;
    }

    public void setIDocNo(String DocNo) {
        this.DocNo = DocNo;
    }

    public String getIDocNo() {
        return DocNo;
    }
}
