package com.poseintelligence.cssdm1.model;

import android.graphics.Color;

import com.poseintelligence.cssdm1.CssdProject;

public class ModelPayout {
    String Department_ID;
    String DepName;
    String DocNo;
    String CreateDate;
    String Qty;
    String BorrowBalanceQty;
    String Balance;
    String IsStatus;
    String Payout_Status;
    String IsWeb = "0";
    String IsGroupPayout = "0";
    String DocDateTime;
    String Payout_Status_ID;
    String DocDateSend;
    boolean boo_Payout_Status;
    boolean Is_Check_Doc;

    String IsUrgent;
    String QtyUrgent;

    String PayQty,Count_Qty, Desc, RefDocNo, IsSpecial = "0";

    int index = 0;

    public ModelPayout(String department_ID, String depName, String docNo, String createDate, String qty, String payQty, String count_Qty, String isStatus, String payout_Status, String desc, String refDocNo, String IsSpecial, String IsWeb,String DocDateTime, String Payout_Status_ID, boolean boo_Payout_Status, String DocDateSend, int index) {
        Department_ID = department_ID;
        DepName = depName;
        DocNo = docNo;
        CreateDate = createDate;
        Qty = qty;
        PayQty = payQty;
        Count_Qty = count_Qty;
        IsStatus = isStatus;
        Payout_Status = payout_Status;
        Desc = desc;
        RefDocNo = refDocNo;
        this.IsSpecial = IsSpecial;
        this.IsWeb = IsWeb;
        this.DocDateTime = DocDateTime;
        this.Payout_Status_ID = Payout_Status_ID;
        this.boo_Payout_Status = boo_Payout_Status;
        this.DocDateSend = DocDateSend;
        this.index = index;
    }

    public ModelPayout(String department_ID, String depName, String docNo, String createDate, String qty, String payQty, String count_Qty, String isStatus, String payout_Status, String desc, String refDocNo, String IsSpecial, String IsWeb,String DocDateTime, String DocDateSend, int index) {
        Department_ID = department_ID;
        DepName = depName;
        DocNo = docNo;
        CreateDate = createDate;
        Qty = qty;
        PayQty = payQty;
        Count_Qty = count_Qty;
        IsStatus = isStatus;
        Payout_Status = payout_Status;
        Desc = desc;
        RefDocNo = refDocNo;
        this.IsSpecial = IsSpecial;
        this.IsWeb = IsWeb;
        this.DocDateTime = DocDateTime;
        this.index = index;
        this.DocDateSend = DocDateSend;
    }

    public String getDocDateTime() {
        return DocDateTime;
    }

    public void setDocDateTime(String docDateTime) {
        DocDateTime = docDateTime;
    }

    public String getIsWeb() {
        return IsWeb;
    }

    public void setIsWeb(String isWeb) {
        IsWeb = isWeb;
    }

    public String getIsSpecial() {
        return IsSpecial;
    }

    public void setIsSpecial(String isSpecial) {
        IsSpecial = isSpecial;
    }

    public String getRefDocNo() {

        //System.out.println("RefDocNo = " + RefDocNo == null );

        return RefDocNo == null ? "" : RefDocNo;
    }

    public void setRefDocNo(String refDocNo) {
        RefDocNo = refDocNo;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getDepartment_ID() {
        return Department_ID;
    }

    public void setDepartment_ID(String department_ID) {
        Department_ID = department_ID;
    }

    public String getDepName() {
        return DepName;
    }

    public String getDepNameByPayItem(boolean IsSpecial) {

        //System.out.println("RefDocNo = " + RefDocNo);

        String prefix = "";
        if(IsSpecial){
            prefix = "S";
        }else if(IsWeb.equals("1")){
            prefix = "W";
            if( CssdProject.Project.equals("SiPH")){prefix = "เบิก";}
        }else if(IsWeb.equals("2")){
            prefix = "B";
        }else if(IsWeb.equals("0") && RefDocNo != null && !RefDocNo.trim().equals("")){
            prefix = "R";
        }else{
            prefix = "M";
        }

        if (IsGroupPayout.equals("1")) {
            try {
                if(RefDocNo.substring(0,1).equals("S")){
                    prefix = "R";
                }else{
                    if(IsWeb.equals("1")){
                        prefix = "W";

                        if( CssdProject.Project.equals("SiPH")){prefix = "เบิก";}
                    }else{
                        prefix = "GROUP";
                    }

                }
            }catch (Exception e){

            }
        }

//        if(IsSpecial){
//            prefix = "S";
//        }else if(IsWeb.equals("1")){
//            prefix = "W";
//        }else if(IsWeb.equals("2")){
//            prefix = "B";
//        }else if(IsWeb.equals("0") && RefDocNo != null && !RefDocNo.trim().equals("")){
//            prefix = "R";
//        }else{
//            prefix = "M";
//        }

        //System.out.println(Department_ID);



        return ( (Department_ID != null && Department_ID.equals("-1")) ? "สร้างใบจ่ายรวมแผนก" : DepName ) + " (" + prefix + ")";

        //return DepName + " " + ( IsSpecial ? "(S)" : ( (RefDocNo != null && !RefDocNo.trim().equals("")) ? "(R)" : "(M)") );
    }

    public String getDepNameByPayItem() {

        String prefix = "";

        if(IsSpecial.equals("1")){
            prefix = "S";
        }else if(IsWeb.equals("1")){
            prefix = "W";

            if( CssdProject.Project.equals("SiPH")){prefix = "เบิก";}
        }else if(IsWeb.equals("2")){
            prefix = "B";
        }else if(IsWeb.equals("0") && RefDocNo != null && !RefDocNo.trim().equals("")){
            prefix = "R";
        }else{
            prefix = "M";
        }

        if (IsGroupPayout.equals("1")) {
            try {
                if(RefDocNo.substring(0,1).equals("S")){
                    prefix = "R";
                }else{
                    if(IsWeb.equals("1")){
                        prefix = "W";

                        if( CssdProject.Project.equals("SiPH")){prefix = "เบิก";}
                    }else{
                        prefix = "GROUP";
                    }

                }
            }catch (Exception e){

            }
        }

//        if(this.IsSpecial.equals("1")){
//            prefix = "S";
//        }else if(IsWeb.equals("1")){
//            prefix = "W";
//        }else if(IsWeb.equals("2")){
//            prefix = "B";
//        }else if(IsWeb.equals("0") && RefDocNo != null && !RefDocNo.trim().equals("")){
//            prefix = "R";
//        }else{
//            prefix = "M";
//        }

        return ( (Department_ID != null && Department_ID.equals("-1")) ? "สร้างใบจ่ายรวมแผนก (เอกสารชั่วคราว)" : DepName ) + " (" + prefix + ")";

        //return DepName + " (" + prefix + ")";

        //return DepName + " " + ( this.IsSpecial.equals("1") ? "(S)" : ( (RefDocNo != null && !RefDocNo.trim().equals("")) ? "(R)" : "(M)") );
    }

    public void setDepName(String depName) {
        DepName = depName;
    }

    public String getCreateDate() {
        return CreateDate;
    }

    public void setCreateDate(String createDate) {
        CreateDate = createDate;
    }

    public String getNo() {
        return (index+1)+".";
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getQty() {
        return Qty;
    }

    public void setQty(String qty) {
        Qty = qty;
    }

    public String getPayQty() {
        return PayQty;
    }

    public void setPayQty(String payQty) {
        PayQty = payQty;
    }

    public String getCount_Qty() {
        return Count_Qty;
    }

    public void setCount_Qty(String count_Qty) {
        Count_Qty = count_Qty;
    }

    public String getIsStatus() {
        return IsStatus;
    }

    public void setIsStatus(String isStatus) {
        IsStatus = isStatus;
    }

    public String getPayout_Status() {
        return Payout_Status;
    }

    public void setPayout_Status(String payout_Status) {
        Payout_Status = payout_Status;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getPayout_Status_ID() {
        return Payout_Status_ID;
    }

    public void setPayout_Status_ID(String payout_Status_ID) {
        Payout_Status_ID = payout_Status_ID;
    }

    public String getDocDateSend() {
        return DocDateSend;
    }

    public void setDocDateSend(String docDateSend) {
        DocDateSend = docDateSend;
    }

    public boolean isBoo_Payout_Status() {
        return boo_Payout_Status;
    }

    public void setBoo_Payout_Status(boolean boo_Payout_Status) {
        this.boo_Payout_Status = boo_Payout_Status;
    }

    public String getIsGroupPayout() {
        return IsGroupPayout;
    }

    public void setIsGroupPayout(String isGroupPayout) {
        IsGroupPayout = isGroupPayout;
    }

    public boolean isIs_Check_Doc() {
        return Is_Check_Doc;
    }

    public void setIs_Check_Doc(boolean is_Check_Doc) {
        Is_Check_Doc = is_Check_Doc;
    }

    public String getIsUrgent() {
        return IsUrgent;
    }

    public void setIsUrgent(String isUrgent) {
        IsUrgent = isUrgent;
    }
}
