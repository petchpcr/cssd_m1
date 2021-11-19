package com.poseintelligence.cssdm1.core.date;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by paradox on 3/23/2015.
 */
public class DateTime {
    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTimeSec() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateTimeFull() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDate(String Format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format, Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int getMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date)).intValue() - 1;
    }

    public static String getHour() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static int getAlertTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm", Locale.getDefault());
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date)).intValue();
    }

    public static int getH() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HHmm", Locale.getDefault());
        Date date = new Date();
        return Integer.valueOf(dateFormat.format(date)).intValue();
    }

    public static String nextDate(String date, int d, String Format) {
        if(d < 1)
            return date;

        for (int i = 0; i < d; i++)
            date = next(date);

        if(Format.equals("yyyy-mm-dd"))
            return convertDate(date);
        else
            return date;

    }

    public static String getTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    static String next(String Date) {
        int d = Integer.valueOf(Date.substring(0, 2)).intValue();
        int m = Integer.valueOf(Date.substring(3, 5)).intValue();
        int y = Integer.valueOf(Date.substring(6, 10)).intValue();

        int dt = 0;

        if (m == 4 || m == 6 || m == 9 || m == 11) {
            dt = 30;
        } else if (m == 2) {
            dt = leapYear(y);
        } else {
            dt = 31;
        }

        if (d < dt) {
            d++;
        } else {
            if (d == dt) {
                d = 1;
                m++;
                if (m > 12) {
                    m = 1;
                    y++;
                }
            }
        }

        if (d < 10 && m < 10)
            return ("0" + d + "-0" + m + "-" + y);
        else if (d < 10 && m > 9)
            return ("0" + d + "-" + m + "-" + y);
        else if (d > 9 && m < 10)
            return (d + "-0" + m + "-" + y);


        return (d + "-" + m + "-" + y);
    }

    public static String previousDate(String date, int d, String Format) {
        if(d < 1)
            return date;

        for (int i = 0; i < d; i++)
            date = previous(date);

        if(Format.equals("yyyy-mm-dd"))
            return convertDate(date);
        else
            return date;

    }

    static String previous(String Date) {
        int d = Integer.valueOf(Date.substring(0, 2)).intValue();
        int m = Integer.valueOf(Date.substring(3, 5)).intValue();
        int y = Integer.valueOf(Date.substring(6, 10)).intValue();

        int dt = 0;

        if (m == 4 || m == 6 || m == 9 || m == 11) {
            dt = 30;
        } else if (m == 2) {
            dt = leapYear(y);
        } else {
            dt = 31;
        }

        if (d > 1) {
            d--;
        } else {
            d = dt;
            m--;
            if (m == 0) {
                m = 12;
                y--;
            }
        }

        if (d < 10 && m < 10)
            return ("0" + d + "-0" + m + "-" + y);
        else if (d < 10 && m > 9)
            return ("0" + d + "-" + m + "-" + y);
        else if (d > 9 && m < 10)
            return (d + "-0" + m + "-" + y);


        return (d + "-" + m + "-" + y);
    }

    static int leapYear(int theYear) {
        if (theYear < 100) {
            if (theYear > 40) {
                theYear += 1900;
            } else {
                theYear += 2000;
            }
        }

        if (theYear % 4 == 0) {
            if (theYear % 100 != 0) {
                return 29;
            } else if (theYear % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        } else {
            return 28;
        }
    }

    public static String convertDate(String StrDate) { //dd-MM-YYYY To YYYY-MM-dd
        //int IntYear = Integer.valueOf(StrDate.substring(6,10));
        return (StrDate.substring(6,10) +"-"+StrDate.substring(3,5)+"-"+StrDate.substring(0,2));
    }

    public static String convertDate2(String StrDate) { //YYYY-MM-dd To dd-MM-YYYY
        //int IntYear = Integer.valueOf(StrDate.substring(6,10));
        return (StrDate.substring(8,10) +"-"+StrDate.substring(5,7)+"-"+StrDate.substring(0,4));
    }

}
