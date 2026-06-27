package com.poseintelligence.cssdm1.RFID.IDataT2X.event;

public interface BackResult extends OnKeyDownListener {
    void postResult(String[] tagData);

    void postInventoryRate(long rate);
}
