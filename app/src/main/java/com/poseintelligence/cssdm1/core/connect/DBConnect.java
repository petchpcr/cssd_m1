package com.poseintelligence.cssdm1.core.connect;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

public class DBConnect {
    public static String host = "10.11.9.6";
    public static String user = "sa";
    public static String pass = "A$192dijd";
    public static String dbName = "cssd_praram9";
    public static String ConnectionString = "jdbc:jtds:sqlserver://"+host+";databaseName="+dbName+";encrypt=false;trustServerCertificate=true;loginTimeout=30;user=" + user + ";password=" + pass;

    public static String getItemstockAddToSterileBasket(HashMap<String, String> data){
        String message  = "";
        String ItemStockID = "";
        String w_id  = "";
        String SterileTypeID = "";
        String SterileMachineID = "";
        int IsStatus = -1;
        int IsPay = -1;
        boolean sr_IsCheckItemInMachine = false;
        boolean ss_IsMatchBasketAndType = false;

        String b_check_id ="";
        String b_check_basketid = "";

        JSONObject json = new JSONObject();
        JSONArray array = new JSONArray();
        JSONObject item = new JSONObject();

        Connection con=null;
        try
        {

            DriverManager.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            con= DriverManager.getConnection(ConnectionString);

            String get_config = "EXEC getConfigAddSterileBasket";
            CallableStatement cs_get_config=con.prepareCall(get_config);
            ResultSet rec_get_config  = cs_get_config.executeQuery();

            while((rec_get_config!=null) && (rec_get_config.next()))
            {
                sr_IsCheckItemInMachine = rec_get_config.getBoolean("SR_IsCheckItemInMachine");
                ss_IsMatchBasketAndType = rec_get_config.getBoolean("SS_IsMatchBasketAndType");
            }
            Log.d("tog_add_item","sr_IsCheckItemInMachine = "+sr_IsCheckItemInMachine);
            Log.d("tog_add_item","ss_IsMatchBasketAndType = "+ss_IsMatchBasketAndType);



            String command = "EXEC getItemstockAddToSterileBasket ?,?,?,?,?,?";
            CallableStatement cs=con.prepareCall(command);
            cs.setString(1, data.get("usage_code"));
            cs.setString(2, data.containsKey("program_id")?data.get("program_id"):" ");
            cs.setString(3, data.containsKey("mac_id")?data.get("mac_id"):" ");
            cs.registerOutParameter(4, Types.VARCHAR);
            cs.registerOutParameter(5, Types.VARCHAR);
            cs.registerOutParameter(6, Types.VARCHAR);

            ResultSet rec  = cs.executeQuery();

            int i_cnt = 0;
            while((rec!=null) && (rec.next()))
            {
                i_cnt++;
                ItemStockID 	= rec.getString("RowID");
                w_id 	= rec.getString("ID");
                SterileTypeID 	= rec.getString("SterileTypeID");
                SterileMachineID 	= rec.getString("SterileMachineID");

                IsStatus 	= rec.getInt("IsStatus");
                IsPay 	= rec.getInt("IsPay");
            }

            String docno_sterile = cs.getString(4);

            b_check_id = cs.getString(5);
            b_check_basketid = cs.getString(6);

            Log.d("tog_add_item","ItemStockID = "+ItemStockID);
            Log.d("tog_add_item","w_id = "+w_id);
            Log.d("tog_add_item","SterileTypeID = "+SterileTypeID);
            Log.d("tog_add_item","SterileMachineID = "+SterileMachineID);

            Log.d("tog_add_item","IsStatus = "+IsStatus);
            Log.d("tog_add_item","IsPay = "+IsPay);

            Log.d("tog_add_item","docno_sterile = "+docno_sterile);


            Log.d("tog_add_item","b_check_id = "+b_check_id);
            Log.d("tog_add_item","b_check_basketid = "+b_check_basketid);

            boolean to_continue = true;
            if(i_cnt>0){
                if(SterileTypeID==null && ss_IsMatchBasketAndType){
                    to_continue = false;
                    item.put("result", "T");
                }

                if(to_continue){
                    if(data.containsKey("mac_id")){
                        if(SterileMachineID==null && sr_IsCheckItemInMachine){
                            to_continue = false;
                            item.put("result", "M");
                        }
                    }
                }

                if(to_continue){
                    if(b_check_id!=null){
                        item.put("result", "D");
                        item.put("item_id", b_check_id);
                        item.put("basket_id", b_check_basketid);
                    }else{
                        String set = "EXEC setAddSterileBasket ?,?,?";
                        CallableStatement cs_set=con.prepareCall(set);
                        cs_set.setString(1, data.get("basket_id"));
                        cs_set.setString(2, ItemStockID);
                        cs_set.setString(3, w_id);
                        cs_set.executeQuery();

                        item.put("result", "A");
                        item.put("w_id", w_id);
                    }
                }

            }else{
                if(docno_sterile!=null){
                    to_continue = false;
                    item.put("result", "D");
                    item.put("sDocNo", docno_sterile);
                    item.put("basket_id", "---");
                }

                if(IsStatus<0){
                    to_continue = false;

                    item.put("result", "E");
                }

                if(to_continue){
                    if(IsStatus==0){
                        item.put("result", "N");
                        item.put("Message", "อยู่กระบวนการรับล้าง");
                    }else if(IsStatus==1){
                        item.put("result", "N");
                        item.put("Message", "อยู่เครื่องล้าง/กำลังแพ๊ค");
                    }else if(IsStatus==2){
                        item.put("result", "N");
                        item.put("Message", "รอนำเข้าจ่ายกลาง");
                    }else if(IsStatus==3 && IsPay == 0){
                        item.put("result", "N");
                        item.put("Message", "อยู่ห้องปลอดเชื้อ");
                    }else if(IsStatus==3 && IsPay == 1){
                        item.put("result", "N");
                        item.put("Message", "รายการรอปิดใบจ่าย");
                    }else if(IsStatus==4 && IsPay == 1){
                        item.put("result", "N");
                        item.put("Message", "รายการถูกจ่ายแล้ว");
                    }else if(IsStatus==5 && IsPay == 1){
                        item.put("result", "N");
                        item.put("Message", "รายการอยู่แผนก");
                    }else{
                        item.put("result", "E");
                    }
                }

            }

            array.put(item);
            json.put("result", array);
        }
        catch (Exception e)
        {
            Log.d("tog_add_item","Exception "+ e);
        }
        finally {
            if(con!=null){
                try
                {
                    con.close();
                }
                catch (SQLException e)
                {
                    Log.d("tog_add_item","SQLException "+ e );
                }
            }
        }

        if(json.length()==0){
            try {
                item.put("result", "E");
                array.put(item);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        message = json.toString();

        return message;
    }
}
