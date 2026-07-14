package com.poseintelligence.cssdm1.RFID.IDataT2X.event;

import android.os.SystemClock;
import android.util.Log;

import com.poseintelligence.cssdm1.RFID.IDataT2X.UHFT2X;

import java.util.Arrays;

public class GetRFIDThread extends Thread {


    private GetRFIDThread() {
    }

    public static GetRFIDThread getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final GetRFIDThread instance = new GetRFIDThread();
    }

    private BackResult ba;

    private boolean ifPostMsg = false;

    public void setBackResult(BackResult ba) {
        this.ba = ba;
    }

    public boolean isIfPostMsg() {
        return ifPostMsg;
    }

    private boolean flag = true;

    public void destoryThread() {
        flag = false;
    }

    public void restartThread() {
        flag = true;
    }

    private long sTime;

    public void setIfPostMsg(boolean ifPostMsg) {
        if (ifPostMsg) {
            sTime = SystemClock.elapsedRealtime();
        }
        this.ifPostMsg = ifPostMsg;
    }
    //是否处于查询标签模式
    // Whether in query tag mode
    private boolean searchTag;

    public void setSearchTag(boolean searchTag) {
        this.searchTag = searchTag;
    }
    //是否处于锁定标签界面下盘点
    private boolean lockPostTag = false;

    public void setLockPostTag(boolean lockPostTag) {
        this.lockPostTag = lockPostTag;
    }
    public boolean getLockPostTag() {
        return lockPostTag;
    }


    @Override
    public void run() {
        long curTime, oldTime = 0;
        //每秒的读取速率
        // Read rate per second
        long readRate = 0;
        //开始盘点的时间
        // Start time
        long tempTime = 0;
        while (true) {
//            Log.e("tog_flag","while (flag)");
            if(!flag){
                SystemClock.sleep(1000);
                continue;
            }
            SystemClock.sleep(10);
            if (ifPostMsg) {
                if (tempTime == 0 && sTime != 0) {
                    tempTime = sTime;
                }
                long cTime = SystemClock.elapsedRealtime();
                if (cTime - tempTime >= 1000 && tempTime != 0) {
                    ba.postInventoryRate(readRate);
                    readRate = 0;
                    tempTime = cTime;
                }
                UHFT2X uhft2x = UHFT2X.getMyApp();
                String[] tagData = null;
                if (uhft2x != null && uhft2x.getUhfMangerImpl() != null) {
                    tagData = uhft2x.getUhfMangerImpl().readTagFromBuffer();
                }
                if (tagData != null) {
                    if (UHFT2X.powerSize != 0) {
                        if (UHFT2X.isZhuYanCustom) {
                            if (UHFT2X.isZhuYanCustomReading) {
                                if (tagData[1].startsWith("582004")) {
                                    readRate++;
                                    oldTime = 0;
                                    ba.postResult(tagData);
                                }
                            }else {
                                readRate++;
                                oldTime = 0;
                                ba.postResult(tagData);
                            }
                        }else {
                            readRate++;
                            oldTime = 0;
                            ba.postResult(tagData);
                        }
                    }
                } else if (searchTag) {
                    //当超过一秒查询不到标签，清空状态
                    // Clear status when no tag is queried for more than one second
                    curTime = System.currentTimeMillis();
                    if ((curTime - oldTime) > 2000 && (oldTime != 0)) {
                        ba.postResult(null);
                    }
                    if (oldTime == 0) {
                        oldTime = curTime;
                    }
                }
            }else if(lockPostTag) {
                UHFT2X uhft2x = UHFT2X.getMyApp();
                String[] tagData = null;
                if (uhft2x != null && uhft2x.getUhfMangerImpl() != null) {
                    tagData = uhft2x.getUhfMangerImpl().readTagFromBuffer();
                }
                Log.e("tog_get_rfid","epcFottest = " + tagData);
                if (tagData != null) {
                    ba.postResult(tagData);
                }
            }else {
                if (readRate != 0 ) {
                    //重置时间数据
                    // Reset time
                    sTime =0;
                    tempTime =0;
                    readRate = 0;
                }
            }
        }
    }
}
