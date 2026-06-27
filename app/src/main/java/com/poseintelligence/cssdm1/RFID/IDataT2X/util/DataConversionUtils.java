package com.poseintelligence.cssdm1.RFID.IDataT2X.util;

import android.text.TextUtils;
import android.util.Log;

public class DataConversionUtils {

    private static String HexToBin(String hexNumber) {
        String inputNumber = "";
        StringBuilder binaryNumber = new StringBuilder();

        switch (hexNumber.length() % 4) {
            case 1:
                inputNumber =  "000" + hexNumber;
                break;
            case 2:
                inputNumber = "00" + hexNumber;
                break;
            case 3:
                inputNumber = "0" + hexNumber;
                break;
            case 0:
                inputNumber = hexNumber;
                break;
        }
        if (TextUtils.isEmpty(inputNumber))
            return null;
        for (int i = 0;i<inputNumber.length();i++) {
            String binaryBlock;
            switch (inputNumber.charAt(i)) {
                case '0':
                    binaryBlock = "0000";
                    break;
                case '1':
                    binaryBlock = "0001";
                    break;
                case '2':
                    binaryBlock = "0010";
                    break;
                case '3':
                    binaryBlock = "0011";
                    break;
                case '4':
                    binaryBlock = "0100";
                    break;
                case '5':
                    binaryBlock = "0101";
                    break;
                case '6':
                    binaryBlock = "0110";
                    break;
                case '7':
                    binaryBlock = "0111";
                    break;
                case '8':
                    binaryBlock = "1000";
                    break;
                case '9':
                    binaryBlock = "1001";
                    break;
                case 'A':
                    binaryBlock = "1010";
                    break;
                case 'B':
                    binaryBlock = "1011";
                    break;
                case 'C':
                    binaryBlock = "1100";
                    break;
                case 'D':
                    binaryBlock = "1101";
                    break;
                case 'E':
                    binaryBlock = "1110";
                    break;
                case 'F':
                    binaryBlock = "1111";
                    break;
                default:
                    return null;
            }
            binaryNumber.append(binaryBlock);
        }
        return binaryNumber.toString();
    }

    private static int BinaryToDecimal(String binaryNumber) {
        int decimalNumber = Integer.parseInt(binaryNumber,2);
        return decimalNumber;
    }

    private static int GetCompanyPrefixLength(int partitionValue, int mode) {
        switch (mode)
        {
            case 1:
                switch (partitionValue)
                {
                    case 0:
                        return 40;
                    case 1:
                        return 37;
                    case 2:
                        return 34;
                    case 3:
                        return 30;
                    case 4:
                        return 27;
                    case 5:
                        return 24;
                    case 6:
                        return 20;
                    default:

                }
            case 2:
                switch (partitionValue) {
                    case 0:
                        return 12;
                    case 1:
                        return 11;
                    case 2:
                        return 10;
                    case 3:
                        return 9;
                    case 4:
                        return 8;
                    case 5:
                        return 7;
                    case 6:
                        return 6;
                    default:

                }
            default:

        }
        return 0;
    }

    public static int CalculateCheckDigit(String UPC) {
        int checkDigit;
        int factor = 3;
        int sum = 0;
        for (int index = UPC.length(); index > 0; index--) {
            sum = sum + Integer.parseInt(UPC.substring(index - 1, index)) * factor;
            factor = 4 - factor;
        }
        checkDigit = ((1000 - sum) % 10);

        return checkDigit;
    }

    public static String Decode(String hexEPC) {
        if (hexEPC.length() == 0) {
            return null;
        }
        String binaryEpc = HexToBin(hexEPC);
        if (TextUtils.isEmpty(binaryEpc))
            return null;
        Log.e("TAG", "Decode: " + binaryEpc );
        String header;
        String itemIdentifier;

        // If EPC begins with "00", header field is 8 bits, else 2 bits
        // Ref: EPCTagDataSpecification11rev124 Page 16
        int headerLength;
        if (binaryEpc.substring(0, 2).equals("00")) {
            headerLength = 8;
        } else {
            headerLength = 2;
        }

        header = binaryEpc.substring(0, headerLength);
        switch (header)
        {
            case ("00110000"):
            {
                // Retrieve the partition value from 11-13 bits of EPC and get corresponding company prefix length
                int partition = BinaryToDecimal(binaryEpc.substring(11, 14));
                int companyPrefixLengthBinary = GetCompanyPrefixLength(partition, 1);
                int companyPrefixLengthDecimal = GetCompanyPrefixLength(partition, 2);

                // company prefix lengh + item reference length = 44 bits -
                // get length of item reference
                int itemReferenceLenghtBinary = 44 - companyPrefixLengthBinary;
                int itemReferenceLenghtDecimal = 13 - companyPrefixLengthDecimal;
                String companyPrefix = padRight(String.valueOf(BinaryToDecimal(binaryEpc.substring(14, companyPrefixLengthBinary+14))),companyPrefixLengthDecimal,'0');
                String itemReference = padRight(String.valueOf(BinaryToDecimal(binaryEpc.substring(14 + companyPrefixLengthBinary, 14 + companyPrefixLengthBinary+itemReferenceLenghtBinary))),itemReferenceLenghtDecimal, '0');
                // The first digit of item reference moves to the first position of EPC
                itemIdentifier = itemReference.substring(0, 1) + companyPrefix + itemReference.substring(1, itemReference.length());

                String checkDigit = String.valueOf(CalculateCheckDigit(itemIdentifier));

                return itemIdentifier + checkDigit;
            }
            case ("10"): {
                // Retrieve the company prefix index from bits 5-19
                String companyPrefixIndex = binaryEpc.substring(5, 19);
                String decimalCompanyPrefixIndex = String.valueOf(BinaryToDecimal(companyPrefixIndex));
                String companyPrefix = GetCompanyPrefix(decimalCompanyPrefixIndex);
                int itemReferenceLength = 13 - companyPrefix.length();
                String itemReference = binaryEpc.substring(19, 39);
                String decimalItemReference = padRight(String.valueOf(BinaryToDecimal(itemReference)),itemReferenceLength, '0');
                String checkDigitCalculator = decimalItemReference.substring(0, 1) +
                        companyPrefix + decimalItemReference.substring(1, itemReferenceLength);

                String checkDigit = String.valueOf(CalculateCheckDigit(checkDigitCalculator));
                return checkDigitCalculator + checkDigit;
            }
            case ("00110001"):
                return null;
            default:
                return null;
        }
    }

    public static String padLeft(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

    public static String GetCompanyPrefix(String companyprefixIndex) {

        return "";
    }

    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }

        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, diff, src.length());
        for (int i = 0; i < diff; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }

}
