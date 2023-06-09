package com.poseintelligence.cssdm1.model;

public class ModelDocnoPayoutSignature {

    String ID;
    String Docno;
    String Docdate;
    String Depname;
    String Payround;

    int index = 0;

    public ModelDocnoPayoutSignature(String ID, String Docno, String Docdate, String Depname, String Payround, int index){
        this.ID = ID;
        this.Docno = Docno;
        this.Docdate = Docdate;
        this.Depname = Depname;
        this.Payround = Payround;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getNo() {
        return (this.index + 1) + ".";
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDocno() {
        return Docno;
    }

    public void setDocno(String docno) {
        Docno = docno;
    }

    public String getDocdate() {
        return Docdate;
    }

    public void setDocdate(String docdate) {
        Docdate = docdate;
    }

    public String getDepname() {
        return Depname;
    }

    public void setDepname(String depname) {
        Depname = depname;
    }

    public String getPayround() {
        return Payround;
    }

    public void setPayround(String payround) {
        Payround = payround;
    }
}
