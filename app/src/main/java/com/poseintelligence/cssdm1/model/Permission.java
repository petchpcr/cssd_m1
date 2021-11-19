package com.poseintelligence.cssdm1.model;

public class Permission {
    private String userid;
    private String Name;
    private String Lang;

    public String getUserid() {
        return userid;
    }
    public void setUserid(String userid) {
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
}
