package com.poseintelligence.cssdm1.RFID.IDataT2X.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.poseintelligence.cssdm1.R;
import com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X;


public final class MUtil {


    public static void show(String text) {
        UHFT2X uhft2x = UHFT2X.getMyApp();
        if (uhft2x != null) {
            Toast.makeText(uhft2x.getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    public static void show(int rid) {
        UHFT2X uhft2x = UHFT2X.getMyApp();
        if (uhft2x != null) {
            Toast.makeText(uhft2x.getContext(), rid, Toast.LENGTH_SHORT).show();
        }
    }

    private static ProgressDialog dialog;

    public static void showProgressDialog(String text, Context con) {
        if (dialog == null) {
            dialog = new ProgressDialog(con);
            dialog.setTitle(text);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public static void cancleDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    private static AlertDialog atdialog;


    public static void warningDialog(Context con) {
        if (atdialog == null) {
            atdialog = new AlertDialog.Builder(con).create();
            atdialog.setTitle(R.string.poweon_failed);
            atdialog.setMessage(con.getString(R.string.notice_power_failed));
            atdialog.setCancelable(false);
            atdialog.show();
        }
    }

    public static void cancelWaringDialog() {
        if (atdialog != null) {
            atdialog.cancel();
            atdialog = null;
        }
    }

    /**
     * Convert byte array to hex
     *
     * @param bytes Hexadecimal byte array
     * @return HexString
     */
    public static String byteToHex(byte[] bytes) {
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            // Each byte is represented by two characters;if the number of digits is not enough,filled with 0 before the high digit
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return sb.toString().trim();
    }

    /**
     * Convert hex to byte array
     *
     * @param hex hexString
     * @return Hexadecimal byte array
     */
    public static byte[] hexToByte(String hex) {
        int m = 0, n = 0;
        // Every two characters to describe a byte
        int byteLen = hex.length() / 2;
        byte[] ret = new byte[byteLen];
        for (int i = 0; i < byteLen; i++) {
            m = i * 2 + 1;
            n = m + 1;
            int intVal = Integer.decode("0x" + hex.substring(i * 2, m) + hex.substring(m, n));
            ret[i] = Byte.valueOf((byte) intVal);
        }
        return ret;
    }

}
