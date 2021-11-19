package com.poseintelligence.cssdm1.core.date;

import java.util.Date;

public class CountDownTime {
    public static String getCountLabel(Date start_date, Date end_date) {
        long diff = end_date.getTime() - start_date.getTime();

        long Hours = diff / (60 * 60 * 1000) % 24;
        long Minutes = diff / (60 * 1000) % 60;
        long Seconds = diff / 1000 % 60;

        //return ( Hours > 0 ? (String.format("%02d", Hours) + " ชั่วโมง ") : "" ) + ( Minutes > 0 ? (String.format("%02d", Minutes) + " นาที ") : "") + String.format("%02d", Seconds) + " วินาที";

        //return ( Hours > 0 ? (String.format("%02d", Hours) + ":") : "" ) + ( Minutes > 0 ? (String.format("%02d", Minutes) + ":") : "") + String.format("%02d", Seconds) + "";

        return String.format("%02d", Hours) + ":" + String.format("%02d", Minutes) + ":" + String.format("%02d", Seconds);
    }

    public static String getPauseTime(String PauseTime) {
        if(PauseTime.substring(0, 2).equals("00")){
            return PauseTime.substring(3, 8);
        }else{
            return PauseTime;
        }
    }
}
