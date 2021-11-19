package com.poseintelligence.cssdm1.model;

public class ModelDisplayDoc1_1 {
    String DocNo;
    String SterileName;
    String SterileMachineID;
    String SterileRoundNumber;
    String TestProgramName;
    String IsActive;
    String ID;
    int index;


    public ModelDisplayDoc1_1(String DocNo, String SterileName, String SterileMachineID, String SterileRoundNumber, String TestProgramName, String IsActive, String ID) {
        this.DocNo = DocNo;
        this.SterileName = SterileName;
        this.SterileMachineID = SterileMachineID;
        this.SterileRoundNumber = SterileRoundNumber;
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

    public String getTestProgramName() {
        return TestProgramName;
    }

    public void setTestProgramName(String testProgramName) {
        TestProgramName = TestProgramName;
    }

    public String getSterileName() {
        return SterileName;
    }

    public void setSterileName(String sterileName) {
        SterileName = SterileName;
    }

    public String getSterileMachineID() {
        return SterileMachineID;
    }

    public void setSterileMachineID(String sterileMachineID) {
        SterileMachineID = SterileMachineID;
    }

    public String getSterileRoundNumber() {
        return SterileRoundNumber;
    }

    public void setSterileRoundNumber(String sterileRoundNumber) {
        SterileRoundNumber = SterileRoundNumber;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = DocNo;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;

    }
}
