package com.poseintelligence.cssdm1.model;

public class ModelUsageCount {

    String UsageCode;
    String ItemName;
    String UsageCount;
    int index;

    public ModelUsageCount(String UsageCode, String ItemName, String UsageCount, int index) {
        this.UsageCode = UsageCode;
        this.ItemName = ItemName;
        this.UsageCount = UsageCount;
        this.index = index;
    }

    public String getUsageCount() { return UsageCount; }

    public void setUsageCount(String usageCount) { UsageCount = usageCount; }

    public String getItemName() { return ItemName; }

    public void setItemName(String itemName) { ItemName = itemName; }

    public String getUsageCode() {
        return UsageCode;
    }

    public void setUsageCode(String usageCode) { UsageCode = usageCode; }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
