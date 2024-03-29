package com.poseintelligence.cssdm1.model;

public class ModelMachine {
    String MachineID;
    String MachineName;
    String IsActive;
    String IsBrokenMachine;
    String DocNo;
    String ProgramID;
    String TypeID;
    String ProgramName;
    String RoundNumber;
    String UserLoader;
    String FinishTime;

    public ModelMachine(String machineID, String machineName, String isActive, String isBrokenMachine,String DocNo,String TypeID,String FinishTime) {
        MachineID = machineID;
        MachineName = machineName;
        IsActive = isActive;
        IsBrokenMachine = isBrokenMachine;
        this.DocNo = DocNo;
        this.TypeID = TypeID;
        this.FinishTime = FinishTime;
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

    public void setMachineID(String machineID) {
        MachineID = machineID;
    }

    public void setMachineName(String machineName) {
        MachineName = machineName;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getRoundNumber() {
        return RoundNumber;
    }

    public int getIntRoundNumber() {
        return Integer.parseInt(RoundNumber);
    }

    public void setRoundNumber(String roundNumber) {
        RoundNumber = roundNumber;
    }

    public String getProgramName() {
        return ProgramName;
    }

    public void setProgramName(String programName) {
        ProgramName = programName;
    }

    public String getTypeID() {
        return TypeID;
    }

    public void setTypeID(String typeID) {
        TypeID = typeID;
    }

    public String getUserLoader() {
        return UserLoader;
    }

    public void setUserLoader(String userLoader) {
        UserLoader = userLoader;
    }

    public String getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }
}
