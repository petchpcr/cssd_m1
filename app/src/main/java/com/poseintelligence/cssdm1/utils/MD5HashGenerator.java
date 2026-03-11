package com.poseintelligence.cssdm1.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5HashGenerator {

    public static String generateMD5(final String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String h = Integer.toHexString(0xFF & b);
                while (h.length() < 2) {
                    hexString.append("0");
                }
                hexString.append(h);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace(); // Log the error in Android using Log.e()
        }
        return "";
    }
}