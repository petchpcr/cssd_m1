package com.poseintelligence.cssdm1.RFID.IDataT2X;

import android.content.Context;
import android.util.Log;

import com.poseintelligence.cssdm1.RFID.IDataT2X.util.MyCrashHandler;
import com.tencent.mmkv.MMKV;
import com.uhf.base.UHFManager;

public class UHFT2X {

    public static byte[] UHF = {0x01, 0x02, 0x03};
    private UHFManager uhfMangerImpl;
    private static UHFT2X myApp;
    private Context context;
    //是否启动盘点声音
    // Whether to activate the inventory sound
    public static boolean ifOpenSound = true;
    //应用是否处于弹框状态
    // Is the application in a pop-up box
    //  public static AlertDialog showAtd = null;
    public static int currentInvtDataType = -1;
    public static boolean ifSupportR2000Fun = true;
    public static boolean if5100Module = false;
    public static boolean if7100Module = false;
    public static boolean ifRMModule = false;

    public static boolean ifLockTagRead = false;
    public static boolean ifUM510 = false;

    //是否以ASCII码显示
    public static boolean ifASCII = false;

    //是否自动停止读卡
    public static boolean ifAutoStopLabel = false;

    //自动停止读卡时间
    public static int stopLabelTime = -1;

    //设置标签协议 1:ISO 2:GB
    public static int protocolType = 1;

    public static boolean ifFirst = true;

    public static int powerSize = 5;
    //控制将功率设到0的标志（规则：功率设置小于5实际设成5，等于0不读卡）
    public static boolean powerChange = false;

    public static int MaxPower = 33;

    //用来保存设置的参数的标志，暂时下电保存功率，频率，盘点模式
    public static boolean saveSet = true;

    public static boolean isShangHaiTaoPinCustom = false;

    public static boolean isZhuYanCustom = false;

    public static boolean isZhuYanCustomReading = false;
//    private iScanInterface miScanInterface;
    public static boolean isLowPower = false;
    public static boolean isChangeBaud = false;

//    public static UHFModuleType currentUHFModule = UHFModuleType.UM_MODULE;

    private UHFT2X(Context context) {
        this.context = context;
        Log.d("tog_T2X","onCreate UHFT2X");
        myApp = this;
        MMKV.initialize(context);

        MyCrashHandler.getInstance().init(context);
        //miScanInterface = new iScanInterface(context);
    }

    public static UHFT2X getMyApp() {
        return myApp;
    }

    public static void initialize(Context context) {
        if (myApp == null) {
            Log.d("tog_T2X","initialize UHFT2X context");
            myApp = new UHFT2X(context);
        }
    }

    public void setUhfMangerImpl(UHFManager uhfMangerImpl) {
        this.uhfMangerImpl = uhfMangerImpl;
    }

    public UHFManager getUhfMangerImpl() {
        return uhfMangerImpl;
    }

//    public iScanInterface getiScanInterface() {
//        return miScanInterface;
//    }

    public Context getContext() {
        return context;
    }

}
