package com.poseintelligence.cssdm1.model;

public class Item {
    private String itemCode;
    private String Name;
    private boolean chk;

    public Item(String itemCode, String name, boolean chk) {
        this.itemCode = itemCode;
        Name = name;
        this.chk = chk;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public boolean isChk() {
        return chk;
    }

    public void setChk(boolean chk) {
        this.chk = chk;
    }
}
