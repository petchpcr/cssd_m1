package com.poseintelligence.cssdm1.model;

public class ModelMachine {
    String ID;
    String MachineID;
    String MachineName;
    String IsStatus;

    public ModelMachine(String machineID, String machineName) {
        MachineID = machineID;
        MachineName = machineName;
    }

    public String getID() {
        return ID;
    }

    public String getMachineID() {
        return MachineID;
    }

    public String getMachineName() {
        return MachineName;
    }

    public String getIsStatus() {
        return IsStatus;
    }
}
