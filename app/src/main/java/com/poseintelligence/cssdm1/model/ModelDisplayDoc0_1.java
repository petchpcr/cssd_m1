package com.poseintelligence.cssdm1.model;

public class ModelDisplayDoc0_1 {
    String DocNo;
    String WashMachineID;
    String WashRoundNumber;
    String TestProgramName;
    String WashTypeName;
    String IsActive;
    String ID;
    int index;

    public ModelDisplayDoc0_1(String DocNo, String WashTypeName, String WashMachineID, String WashRoundNumber, String TestProgramName, String IsActive, String ID) {
        this.DocNo = DocNo;
        this.WashTypeName = WashTypeName;
        this.WashMachineID = WashMachineID;
        this.WashRoundNumber = WashRoundNumber;
        this.TestProgramName = TestProgramName;
        this.IsActive = IsActive;
        this.ID = ID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        ID = ID;
    }

    public String getIsActive() {
        return IsActive;
    }

    public void setIsActive(String isActive) {
        IsActive = IsActive;
    }

    public String getWashTypeName() {
        return WashTypeName;
    }

    public void setWashTypeName(String washTypeName) {
        WashTypeName = WashTypeName;
    }

    public String getTestProgramName() {
        return TestProgramName;
    }

    public void setTestProgramName(String testProgramName) {
        TestProgramName = TestProgramName;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = DocNo;
    }

    public String getWashMachineID() {
        return WashMachineID;
    }

    public void setWashMachineID(String washMachineID) {
        WashMachineID = WashMachineID;
    }

    public String getWashRoundNumber() {
        return WashRoundNumber;
    }

    public void setWashRoundNumber(String washRoundNumber) {
        WashRoundNumber = WashRoundNumber;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;

    }
}

