package com.poseintelligence.cssdm1.data;

public class Master {
    public static final int ss_Clear_Expire = 1;
    public static final int ss_Clear_Wash_Tag = 2;
    public static final int ss_Focus_Wash_Tag_Box = 3;
    public static final int ss_Focus_Itemset_Scan_Box = 4;
    public static final int ss_Clear_Button = 5;
    public static final int ss_Done_Button = 6;
    public static final int ss_Wash_Tag_Expire = 7;
    public static final int ss_Clear_Wash_Tag_and_Focus_Wash_Tag = 8;
    public static final int ss_Clear_Check_List = 9;
    public static final int ss_Check_List_Print = 10;
    public static final int ss_Check_Clear_Basket = 11;
    public static final int ss_Start_Mac_Wash = 12;
    public static final int ss_Start_Mac_Sterile = 13;

    public static final int CssdWashDetailNoWash = 203;
    public static final int CssdWashDetailManagement = 200;
    public static final int CssdSterileDraft = 201;
    public static final int CssdNewItemStock = 202;
    public static final int add_wash_detail = 100;
    public static final int open_send_sterile = 101;
    public static final int enter_qty = 102;
    public static final int item = 0;
    public static final int special = 1;
    public static final int supplier = 2;
    public static final int packingmat = 3;
    public static final int manufact = 4;
    public static final int washingprocess = 5;
    public static final int sterileprocess = 6;
    public static final int label = 7;
    public static final int department = 8;
    public static final int itemtype = 9;
    public static final int units = 10;
    public static final int washprocess = 11;
    public static final int users = 12;
    public static final int user_prepare = 13;
    public static final int user_aprrove = 14;
    public static final int user_sterile = 15;
    public static final int sterile_machine = 16;
    public static final int recall_type = 17;
    public static final int recall_type_2 = 18;
    public static final int department_2 = 19;
    public static final int department2 = 20;
    public static final int department2_2 = 21;
    public static final int label_group = 22;
    public static final int sterile_test_program = 23;
    public static final int user_receive = 24;
    public static final int user_send = 25;
    public static final int wash_test_program  = 26;
    public static final int wash_type = 27;
    public static final int user_packing = 28;
    public static final int round = 29;
    public static final int prices = 30;
    public static final int occupancy_rate = 31;
    public static final int sterile_program = 32;

    public static final String s_item = "item";
    public static final String s_special = "special";
    public static final String s_supplier = "supplier";
    public static final String s_packingmat = "packingmat";
    public static final String s_manufact = "manufact";
    public static final String s_washingprocess = "washingprocess";
    public static final String s_sterileprocess = "sterileprocess";
    public static final String s_label = "label";
    public static final String s_department = "department";
    public static final String s_itemtype = "itemtype";
    public static final String s_units = "units";
    public static final String s_washprocess = "washprocess";
    public static final String s_users = "users";
    public static final String s_user_prepare = "user_prepare";
    public static final String s_user_approve = "user_approve";
    public static final String s_user_sterile = "user_sterile";
    public static final String s_sterile_machine = "sterile_machine";
    public static final String s_recall_type = "recall_type";
    public static final String s_recall_type_2 = "recall_type_2";
    public static final String s_department_2 = "department_2";
    public static final String s_department2_2 = "department2_2";
    public static final String s_department2 = "department2";
    public static final String s_LabelGroup = "LabelGroup";
    public static final String s_sterile_test_program = "sterile_test_program";
    public static final String s_user_receive = "user_receive";
    public static final String s_user_send = "user_send";
    public static final String s_wash_test_program = "wash_test_program";
    public static final String s_wash_type = "wash_type";
    public static final String s_user_packing = "user_packing";
    public static final String s_Round = "round";
    public static final String s_Price = "prices";
    public static final String s_occupancy_rate = "occupancy_rate";
    public static final String s_sterile_program = "sterile_program";

    public static final int getResult(String data_){
        if(data_ == null){
            return -1;
        }else if(data_.equals(s_item)){
            return item;
        }else if(data_.equals(s_special)){
            return special;
        }else if(data_.equals(s_supplier)){
            return supplier;
        }else if(data_.equals(s_packingmat)){
            return packingmat;
        }else if(data_.equals(s_manufact)){
            return manufact;
        }else if(data_.equals(s_washingprocess)){
            return washingprocess;
        }else if(data_.equals(s_sterileprocess)){
            return sterileprocess;
        }else if(data_.equals(s_label)){
            return label;
        }else if(data_.equals(s_department)){
            return department;
        }else if(data_.equals(s_itemtype)){
            return itemtype;
        }else if(data_.equals(s_units)){
            return units;
        }else if(data_.equals(s_washprocess)){
            return washprocess;
        }else if(data_.equals(s_users)){
            return users;
        }else if(data_.equals(s_user_prepare)){
            return user_prepare;
        }else if(data_.equals(s_user_approve)){
            return user_aprrove;
        }else if(data_.equals(s_user_sterile)){
            return user_sterile;
        }else if(data_.equals(s_user_packing)){
            return user_packing;
        }else if(data_.equals(s_sterile_machine)){
            return sterile_machine;
        }else if(data_.equals(s_recall_type)){
            return recall_type;
        }else if(data_.equals(s_recall_type_2)){
            return recall_type_2;
        }else if(data_.equals(s_department_2)){
            return department_2;
        }else if(data_.equals(s_department2_2)){
            return department2_2;
        }else if(data_.equals(s_department2)){
            return department2;
        }else if(data_.equals(s_LabelGroup)){
            return label_group;
        }else if(data_.equals(s_sterile_test_program)){
            return sterile_test_program;
        }else if(data_.equals(s_user_receive)){
            return user_receive;
        }else if(data_.equals(s_user_send)){
            return user_send;
        }else if(data_.equals(s_wash_test_program)){
            return wash_test_program;
        }else if(data_.equals(s_wash_type)){
            return wash_type;
        }else if(data_.equals(s_Round)){
            return round;
        }else if(data_.equals(s_Price)){
            return prices;
        }else if(data_.equals(s_Price)){
            return occupancy_rate;
        }else if(data_.equals(s_sterile_program)){
            return sterile_program;
        }else{
            return -1;
        }
    }
}
