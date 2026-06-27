package com.poseintelligence.cssdm1.RFID.IDataT2X.util;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtil {
    private ExecutorService executors;

    private ThreadUtil() {
        executors = Executors.newFixedThreadPool(3);
    }

    public static ThreadUtil getInstance() {
        return MySingleton.instance;
    }

    static class MySingleton {
        static final ThreadUtil instance = new ThreadUtil();
    }

    public ExecutorService getExService() {
        return executors;
    }



}
