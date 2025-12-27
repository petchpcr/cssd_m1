package com.poseintelligence.cssdm1.model;

public class Parameter {
    private int userid;
    private String Name;
    private String Lang;
    private boolean IsAdmin;
    private String EmCode;
    private String EmName;
    private int BdCode;
    private String BdName;
    private boolean IsSU;
    private String login_token;

    public int getUserid() { return userid; }
    public void setUserid(int userid) {
        this.userid = userid;
    }

    public String getName() {
        return Name;
    }
    public void setName(String name) {
        this.Name = name;
    }

    public String getLang() {
        return Lang;
    }
    public void setLang(String Lang) {
        this.Lang = Lang;
    }

    public boolean getIsAdmin() {
        return IsAdmin;
    }
    public void setIsAdmin(boolean IsAdmin) {
        this.IsAdmin = IsAdmin;
    }

    public String getEmCode() { return EmCode; }
    public void setEmCode(String EmCode) { this.EmCode = EmCode; }

    public String getEmName() {
        return EmName;
    }
    public void setEmName(String EmName) { this.EmName = EmName; }

    public int getBdCode() {
        return BdCode;
    }
    public void setBdCode(int BdCode) {
        this.BdCode = BdCode;
    }

    public String getBdName() {
        return BdName;
    }
    public void setBdName(String BdName) {
        this.BdName = BdName;
    }

    public boolean getIsSU() {
        return IsSU;
    }
    public void setIsSU(boolean IsSU) {
        this.IsSU = IsSU;
    }

    public String getLogin_token() {
        return login_token;
    }

    public void setLogin_token(String login_token) {
        this.login_token = login_token;
    }
}
