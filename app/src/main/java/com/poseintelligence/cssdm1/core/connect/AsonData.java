package com.poseintelligence.cssdm1.core.connect;

import java.util.ArrayList;

/**
 * Created by LABTOP on 8/12/2016.
 //  =================================
 // Create by 	: paradox
 //	Create date : 2016-08-16
 // Update by 	: paradox
 //	Update date : 2016-08-16
 // ==================================
 */
public class AsonData {

    private final String S = "@";
    private ArrayList<String> data = null;
    private boolean Success = false;
    private int size = 0;

    //Test

    public AsonData(String Str) {
        try {
            System.out.println(Str);
            int i = -1;
            String[] data = new String[3];

            /*
            System.out.println(Str);
            System.out.println(Str.length());
            System.out.println(Str);
            System.out.println(Str.length());
            */

            Str = Str.trim();

            for (String retval : Str.split(S, 3)) {
                data[++i] = retval;
            }


            System.out.println(Str);
            System.out.println("0 = " + data[0]);
            System.out.println("1 = " + data[1]);
            System.out.println("2 = " + data[2]);


            if (data[0].equals("0") || data[0] == null) {
                this.Success = false;
                this.size = 0;
                return;
            } else {
                this.Success = true;
                this.size = Integer.valueOf(data[1]).intValue();
            }

            ArrayList<String> a = new ArrayList<>();
            int idx = 0;
            for (String retval : Str.split(S)) {
                a.add(ifNull(retval));
                //System.out.println((idx++) + " = " + retval + ";");
            }


            // remove success & size
            a.remove(1);
            a.remove(0);

            this.data = a;
        }catch(Exception e){

        }
    }

    public String ifNull(String txt){
        return txt.equals("null") ? "" : txt;
    }

    public int getSize(){
        return size;
    }

    public boolean isSuccess(){
        return Success;
    }

    public ArrayList<String> getASONData(){
        return data;
    }
}
