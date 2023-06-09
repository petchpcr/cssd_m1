package com.poseintelligence.cssdm1.model;

public class BasketTag {
    String  ID,
            Name,
            MacId,BasketCode,
            MacName,
            IsActive_Basket,
            RefDocNo,
            TypeProcessID;
    int     qty;

    Boolean MacActive = false;

    public BasketTag(String ID, String name, String macId, String macName, int qty, String BasketCode, String IsActive_Basket, String RefDocNo) {
        this.ID = ID;
        Name = name;
        MacId = macId;
        MacName = macName;
        this.qty = qty;
        this.BasketCode =BasketCode ;
        this.IsActive_Basket = IsActive_Basket ;
        this.RefDocNo = RefDocNo ;
    }

    public BasketTag(String ID, String name, String macId, String macName, int qty, String BasketCode, String IsActive_Basket, String RefDocNo, String TypeProcessID) {
        this.ID = ID;
        Name = name;
        MacId = macId;
        MacName = macName;
        this.qty = qty;
        this.BasketCode =BasketCode ;
        this.IsActive_Basket = IsActive_Basket ;
        this.RefDocNo = RefDocNo ;
        this.TypeProcessID = TypeProcessID ;
    }

    public BasketTag(String ID,String name, String BasketCode, String macId,int qty, String TypeProcessID, String RefDocNo) {
        this.ID = ID;
        Name = name;
        this.qty = qty;
        this.BasketCode =BasketCode ;
        MacId = macId ;
        this.TypeProcessID = TypeProcessID ;
        this.RefDocNo = RefDocNo ;
    }

    public String getRefDocNo() {
        return RefDocNo;
    }

    public void setRefDocNo(String refDocNo) {
        RefDocNo = refDocNo;
    }

    public String getIsActive_Basket() {
        return IsActive_Basket;
    }

    public void setIsActive_Basket(String isActive_Basket) {
        IsActive_Basket = isActive_Basket;
    }

    public Boolean getMacActive() {
        return MacActive;
    }

    public void setMacActive(Boolean macActive) {
        MacActive = macActive;
    }

    public String getMacName() {
        return MacName;
    }

    public void setMacName(String macName) {
        MacName = macName;
    }

    public String getBasketCode() {
        return BasketCode;
    }

    public void setBasketCode(String basketCode) {
        BasketCode = basketCode;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getMacId() {
        return MacId;
    }

    public void setMacId(String macId) {
        MacId = macId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getTypeProcessID() {
        return TypeProcessID;
    }

    public void setTypeProcessID(String typeProcessID) {
        TypeProcessID = typeProcessID;
    }
}
