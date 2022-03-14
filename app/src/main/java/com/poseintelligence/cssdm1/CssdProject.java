package com.poseintelligence.cssdm1;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.poseintelligence.cssdm1.model.ConfigM1;
import com.poseintelligence.cssdm1.model.Parameter;

import java.util.ArrayList;

public class CssdProject extends Application {
    private AppCompatActivity xActivity;

    private String D_DATABASE="1";
    private String xUrl;
    private Parameter pm;
    private ArrayList<ConfigM1> cM1;

    public int I_CustomerId = 201;

    boolean MN_IsUsedFormNonUsage = false;
    boolean MN_IsUsedComputeDate = false;
    boolean MN_IsUsedReceiveDevice = false;

    boolean MD_IsUsedSoundScanQR = false;
    boolean MD_IsAutoItemCode = false;
    boolean MD_IsItemPriceCode = false;
    boolean MD_IsUsedItemCode2 = false;

    boolean SS_IsUsedItemSet = false;
    boolean SS_IsCopyPayout = false;
    boolean SS_IsShowSender = false;
    boolean SS_IsReceiverDropdown = false;
    boolean SS_IsApprove = false;
    boolean SS_IsUsedItemDepartment = false;
    boolean SS_IsUsedReceiveTime = false;
    boolean SS_IsNonSelectDepartment = false;
    boolean SS_IsGroupPayout = false;
    boolean SS_IsSortByUsedCount = false;
    boolean SS_IsUsedZoneSterile = false;
    boolean SS_IsUsedBasket = false;
    boolean SS_IsUsedNotification = false;
    boolean SS_IsUsedRemarks = false;
    boolean SS_IsUsedSelfWashDepartment = false;
    boolean SS_IsUsedClosePayout = false;
    boolean SS_IsUsedChangeDepartment = false;

    boolean SR_IsUsedPreparer = false;
    boolean SR_IsUsedApprover = false;
    boolean SR_IsUsedPacker = false;
    boolean SR_IsUsedSteriler = false;
    boolean SR_IsUsedDBUserOperation = false;
    boolean SR_IsUsedDropdownUserOperation = false;
    boolean SR_IsRememberUserOperation = false;
    boolean SR_IsEditRound = false;
    boolean SR_IsUsedOccupancyRate = false;
    boolean SR_IsUsedUserOperationDetail = false;
    boolean SR_IsApproveSterile = false ;
    boolean SR_IsShowFormCheckList = false ;
    boolean SR_IsUsedImportNonReuse = false;
    boolean SR_IncExp = false;
    boolean SR_Is_Preview_Print_Sticker = false;
    boolean SR_Is_NonSelectRound = false;
    boolean SR_IsEditSterileProgram = false;
    boolean SR_IsNotApprove = false;
    boolean SR_IsPrintByUser = false;
    boolean SR_IsMultiDocument = false;
    boolean SR_UsedStickerTH = false;
    boolean SR_IsUsedNotification = false;
    boolean SR_ReceiveFromDeposit = false;

    boolean PA_IsUsedRecipienter = false;
    boolean PA_IsUsedApprover = false;
    boolean PA_IsConfirmClosePayout = false;
    boolean PA_IsUsedDepartmentQR = false;
    boolean PA_DefaultDepartmentQR = false;
    boolean PA_IsEditManualPayoutQty = false;
    boolean PA_IsCreateReceiveDepartment = false;
    boolean PA_IsUsedZonePayout = false;
    boolean PA_IsShowBorrowNotReturn = false;
    boolean PA_IsUsedFIFO = false;
    boolean PA_IsWastingPayout = false;

    boolean WA_IsUsedWash = false;
    boolean WA_IsUsedNotification = false;

    boolean ST_IsUsedNotification = false;

    int AP_NotApproveReturnToPreviousProcess = 0;
    int AP_AddRickReturnToPreviousProcess = 0;
    boolean AP_IsUsedNotification = false;
    boolean AP_UsedScanForApprove = false;

    boolean ST_SoundAndroidVersion9 = false;
    boolean PA_IsNotificationPopupExpiringScan = false;
    boolean PA_IsUsedPayOkSound = false;

    public boolean isPA_IsWastingPayout() {
        return PA_IsWastingPayout;
    }

    public void setPA_IsWastingPayout(boolean PA_IsWastingPayout) {
        this.PA_IsWastingPayout = PA_IsWastingPayout;
    }

    public AppCompatActivity getxActivity() { return xActivity; }
    public void setxActivity(AppCompatActivity xActivity) {
        this.xActivity = xActivity;
    }

    public int getCustomerId() { return I_CustomerId; }
    public void setCustomerId(String s_CustomerId) { I_CustomerId = Integer.valueOf(s_CustomerId).intValue(); }

    public String getxUrl() { return xUrl; }
    public void setxUrl(String xUrl) {
        this.xUrl = xUrl;
    }

    public Parameter getPm(){ return pm; }
    public void setPm(Parameter pm) {
        this.pm = pm;
    }

    public ArrayList<ConfigM1> getcM1(){ return cM1; }
    public void setcM1(ArrayList<ConfigM1> cM1) {
        this.cM1 = cM1;
    }

    public String getD_DATABASE() {
        return D_DATABASE;
    }
    public void setD_DATABASE(String d_DATABASE) {
        D_DATABASE = d_DATABASE;
    }

    public boolean isSS_IsUsedClosePayout() {
        return SS_IsUsedClosePayout;
    }
    public void setSS_IsUsedClosePayout(boolean SS_IsUsedClosePayout) {
        this.SS_IsUsedClosePayout = SS_IsUsedClosePayout;
    }

    public boolean isSS_IsUsedChangeDepartment() {
        return SS_IsUsedChangeDepartment;
    }

    public void setSS_IsUsedChangeDepartment(boolean SS_IsUsedChangeDepartment) {
        this.SS_IsUsedChangeDepartment = SS_IsUsedChangeDepartment;
    }

    public boolean isPA_IsUsedZonePayout() {
        return PA_IsUsedZonePayout;
    }
    public void setPA_IsUsedZonePayout(boolean PA_IsUsedZonePayout) {
        this.PA_IsUsedZonePayout = PA_IsUsedZonePayout;
    }

    public boolean isPA_IsUsedFIFO() {
        return PA_IsUsedFIFO;
    }
    public void setPA_IsUsedFIFO(boolean PA_IsUsedFIFO) {
        this.PA_IsUsedFIFO = PA_IsUsedFIFO;
    }

    public boolean isPA_IsShowBorrowNotReturn() {
        return PA_IsShowBorrowNotReturn;
    }

    public void setPA_IsShowBorrowNotReturn(boolean PA_IsShowBorrowNotReturn) {
        this.PA_IsShowBorrowNotReturn = PA_IsShowBorrowNotReturn;
    }

    public boolean isAP_UsedScanForApprove() {
        return AP_UsedScanForApprove;
    }

    public void setAP_UsedScanForApprove(boolean AP_UsedScanForApprove) {
        this.AP_UsedScanForApprove = AP_UsedScanForApprove;
    }

    public boolean isAP_IsUsedNotification() {
        return AP_IsUsedNotification;
    }

    public void setAP_IsUsedNotification(boolean AP_IsUsedNotification) {
        this.AP_IsUsedNotification = AP_IsUsedNotification;
    }

    public boolean isSR_IsUsedNotification() {
        return SR_IsUsedNotification;
    }

    public void setSR_IsUsedNotification(boolean SR_IsUsedNotification) {
        this.SR_IsUsedNotification = SR_IsUsedNotification;
    }

    public boolean isWA_IsUsedNotification() {
        return WA_IsUsedNotification;
    }

    public void setWA_IsUsedNotification(boolean WA_IsUsedNotification) {
        this.WA_IsUsedNotification = WA_IsUsedNotification;
    }

    public boolean isST_IsUsedNotification() {
        return ST_IsUsedNotification;
    }

    public void setST_IsUsedNotification(boolean ST_IsUsedNotification) {
        this.ST_IsUsedNotification = ST_IsUsedNotification;
    }

    public boolean isSS_IsUsedNotification() {
        return SS_IsUsedNotification;
    }

    public void setSS_IsUsedNotification(boolean SS_IsUsedNotification) {
        this.SS_IsUsedNotification = SS_IsUsedNotification;
    }

    public boolean isSS_IsUsedRemarks() {
        return SS_IsUsedRemarks;
    }

    public void setSS_IsUsedRemarks(boolean SS_IsUsedRemarks) {
        this.SS_IsUsedRemarks = SS_IsUsedRemarks;
    }

    public boolean isSS_IsUsedSelfWashDepartment() {
        return SS_IsUsedSelfWashDepartment;
    }

    public void setSS_IsUsedSelfWashDepartment(boolean SS_IsUsedSelfWashDepartment) {
        this.SS_IsUsedSelfWashDepartment = SS_IsUsedSelfWashDepartment;
    }

    public int getAP_NotApproveReturnToPreviousProcess() {
        return AP_NotApproveReturnToPreviousProcess;
    }

    public void setAP_NotApproveReturnToPreviousProcess(int AP_NotApproveReturnToPreviousProcess) {
        this.AP_NotApproveReturnToPreviousProcess = AP_NotApproveReturnToPreviousProcess;
    }

    public int getAP_AddRickReturnToPreviousProcess() {
        return AP_AddRickReturnToPreviousProcess;
    }

    public void setAP_AddRickReturnToPreviousProcess(int AP_AddRickReturnToPreviousProcess) {
        this.AP_AddRickReturnToPreviousProcess = AP_AddRickReturnToPreviousProcess;
    }

    public boolean isSS_IsUsedBasket() {
        return SS_IsUsedBasket;
    }

    public void setSS_IsUsedBasket(boolean SS_IsUsedBasket) {
        this.SS_IsUsedBasket = SS_IsUsedBasket;
    }

    public boolean isWA_IsUsedWash() {
        return WA_IsUsedWash;
    }

    public void setWA_IsUsedWash(boolean WA_IsUsedWash) {
        this.WA_IsUsedWash = WA_IsUsedWash;
    }

    public boolean isMN_IsUsedFormNonUsage() {
        return MN_IsUsedFormNonUsage;
    }

    public void setMN_IsUsedFormNonUsage(boolean MN_IsUsedFormNonUsage) {
        this.MN_IsUsedFormNonUsage = MN_IsUsedFormNonUsage;
    }

    public boolean isMN_IsUsedComputeDate() {
        return MN_IsUsedComputeDate;
    }

    public void setMN_IsUsedComputeDate(boolean MN_IsUsedComputeDate) {
        this.MN_IsUsedComputeDate = MN_IsUsedComputeDate;
    }

    public boolean isMN_IsUsedReceiveDevice() {
        return MN_IsUsedReceiveDevice;
    }

    public void setMN_IsUsedReceiveDevice(boolean MN_IsUsedReceiveDevice) {
        this.MN_IsUsedReceiveDevice = MN_IsUsedReceiveDevice;
    }

    public boolean isMD_IsUsedSoundScanQR() {
        return MD_IsUsedSoundScanQR;
    }

    public void setMD_IsUsedSoundScanQR(boolean MD_IsUsedSoundScanQR) {
        this.MD_IsUsedSoundScanQR = MD_IsUsedSoundScanQR;
    }

    public boolean isMD_IsAutoItemCode() {
        return MD_IsAutoItemCode;
    }

    public void setMD_IsAutoItemCode(boolean MD_IsAutoItemCode) {
        this.MD_IsAutoItemCode = MD_IsAutoItemCode;
    }

    public boolean isMD_IsItemPriceCode() {
        return MD_IsItemPriceCode;
    }

    public void setMD_IsItemPriceCode(boolean MD_IsItemPriceCode) {
        this.MD_IsItemPriceCode = MD_IsItemPriceCode;
    }

    public boolean isMD_IsUsedItemCode2() {
        return MD_IsUsedItemCode2;
    }

    public void setMD_IsUsedItemCode2(boolean MD_IsUsedItemCode2) {
        this.MD_IsUsedItemCode2 = MD_IsUsedItemCode2;
    }

    public boolean isSS_IsUsedItemSet() {
        return SS_IsUsedItemSet;
    }

    public void setSS_IsUsedItemSet(boolean SS_IsUsedItemSet) {
        this.SS_IsUsedItemSet = SS_IsUsedItemSet;
    }

    public boolean isSS_IsCopyPayout() {
        return SS_IsCopyPayout;
    }

    public void setSS_IsCopyPayout(boolean SS_IsCopyPayout) {
        this.SS_IsCopyPayout = SS_IsCopyPayout;
    }

    public boolean isSS_IsShowSender() {
        return SS_IsShowSender;
    }

    public void setSS_IsShowSender(boolean SS_IsShowSender) {
        this.SS_IsShowSender = SS_IsShowSender;
    }

    public boolean isSS_IsReceiverDropdown() {
        return SS_IsReceiverDropdown;
    }

    public void setSS_IsReceiverDropdown(boolean SS_IsReceiverDropdown) {
        this.SS_IsReceiverDropdown = SS_IsReceiverDropdown;
    }

    public boolean isSS_IsApprove() {
        return SS_IsApprove;
    }

    public void setSS_IsApprove(boolean SS_IsApprove) {
        this.SS_IsApprove = SS_IsApprove;
    }

    public boolean isSS_IsUsedItemDepartment() {
        return SS_IsUsedItemDepartment;
    }

    public void setSS_IsUsedItemDepartment(boolean SS_IsUsedItemDepartment) {
        this.SS_IsUsedItemDepartment = SS_IsUsedItemDepartment;
    }

    public boolean isSS_IsUsedReceiveTime() {
        return SS_IsUsedReceiveTime;
    }

    public void setSS_IsUsedReceiveTime(boolean SS_IsUsedReceiveTime) {
        this.SS_IsUsedReceiveTime = SS_IsUsedReceiveTime;
    }

    public boolean isSS_IsNonSelectDepartment() {
        return SS_IsNonSelectDepartment;
    }

    public void setSS_IsNonSelectDepartment(boolean SS_IsNonSelectDepartment) {
        this.SS_IsNonSelectDepartment = SS_IsNonSelectDepartment;
    }

    public boolean isSS_IsGroupPayout() {
        return SS_IsGroupPayout;
    }

    public void setSS_IsGroupPayout(boolean SS_IsGroupPayout) {
        this.SS_IsGroupPayout = SS_IsGroupPayout;
    }

    public boolean isSS_IsSortByUsedCount() {
        return SS_IsSortByUsedCount;
    }

    public void setSS_IsSortByUsedCount(boolean SS_IsSortByUsedCount) {
        this.SS_IsSortByUsedCount = SS_IsSortByUsedCount;
    }

    public boolean isSS_IsUsedZoneSterile() {
        return SS_IsUsedZoneSterile;
    }

    public void setSS_IsUsedZoneSterile(boolean SS_IsUsedZoneSterile) {
        this.SS_IsUsedZoneSterile = SS_IsUsedZoneSterile;
    }

    public boolean isSR_IsUsedPreparer() {
        return SR_IsUsedPreparer;
    }

    public void setSR_IsUsedPreparer(boolean SR_IsUsedPreparer) {
        this.SR_IsUsedPreparer = SR_IsUsedPreparer;
    }

    public boolean isSR_IsUsedApprover() {
        return SR_IsUsedApprover;
    }

    public void setSR_IsUsedApprover(boolean SR_IsUsedApprover) {
        this.SR_IsUsedApprover = SR_IsUsedApprover;
    }

    public boolean isSR_IsUsedPacker() {
        return SR_IsUsedPacker;
    }

    public void setSR_IsUsedPacker(boolean SR_IsUsedPacker) {
        this.SR_IsUsedPacker = SR_IsUsedPacker;
    }

    public boolean isSR_IsUsedSteriler() {
        return SR_IsUsedSteriler;
    }

    public void setSR_IsUsedSteriler(boolean SR_IsUsedSteriler) {
        this.SR_IsUsedSteriler = SR_IsUsedSteriler;
    }

    public boolean isSR_IsUsedDBUserOperation() {
        return SR_IsUsedDBUserOperation;
    }

    public void setSR_IsUsedDBUserOperation(boolean SR_IsUsedDBUserOperation) {
        this.SR_IsUsedDBUserOperation = SR_IsUsedDBUserOperation;
    }

    public boolean isSR_IsUsedDropdownUserOperation() {
        return SR_IsUsedDropdownUserOperation;
    }

    public void setSR_IsUsedDropdownUserOperation(boolean SR_IsUsedDropdownUserOperation) {
        this.SR_IsUsedDropdownUserOperation = SR_IsUsedDropdownUserOperation;
    }

    public boolean isSR_IsRememberUserOperation() {
        return SR_IsRememberUserOperation;
    }

    public void setSR_IsRememberUserOperation(boolean SR_IsRememberUserOperation) {
        this.SR_IsRememberUserOperation = SR_IsRememberUserOperation;
    }

    public boolean isSR_IsEditRound() {
        return SR_IsEditRound;
    }

    public void setSR_IsEditRound(boolean SR_IsEditRound) {
        this.SR_IsEditRound = SR_IsEditRound;
    }

    public boolean isSR_IsUsedOccupancyRate() {
        return SR_IsUsedOccupancyRate;
    }

    public void setSR_IsUsedOccupancyRate(boolean SR_IsUsedOccupancyRate) {
        this.SR_IsUsedOccupancyRate = SR_IsUsedOccupancyRate;
    }

    public boolean isSR_IsUsedUserOperationDetail() {
        return SR_IsUsedUserOperationDetail;
    }

    public void setSR_IsUsedUserOperationDetail(boolean SR_IsUsedUserOperationDetail) {
        this.SR_IsUsedUserOperationDetail = SR_IsUsedUserOperationDetail;
    }

    public boolean isSR_IsApproveSterile() {
        return SR_IsApproveSterile;
    }

    public void setSR_IsApproveSterile(boolean SR_IsApproveSterile) {
        this.SR_IsApproveSterile = SR_IsApproveSterile;
    }

    public boolean isSR_IsShowFormCheckList() {
        return SR_IsShowFormCheckList;
    }

    public void setSR_IsShowFormCheckList(boolean SR_IsShowFormCheckList) {
        this.SR_IsShowFormCheckList = SR_IsShowFormCheckList;
    }

    public boolean isSR_IsUsedImportNonReuse() {
        return SR_IsUsedImportNonReuse;
    }

    public void setSR_IsUsedImportNonReuse(boolean SR_IsUsedImportNonReuse) {
        this.SR_IsUsedImportNonReuse = SR_IsUsedImportNonReuse;
    }

    public boolean isSR_IncExp() {
        return SR_IncExp;
    }

    public void setSR_IncExp(boolean SR_IncExp) {
        this.SR_IncExp = SR_IncExp;
    }

    public boolean isSR_Is_Preview_Print_Sticker() {
        return SR_Is_Preview_Print_Sticker;
    }

    public void setSR_Is_Preview_Print_Sticker(boolean SR_Is_Preview_Print_Sticker) {
        this.SR_Is_Preview_Print_Sticker = SR_Is_Preview_Print_Sticker;
    }

    public boolean isSR_Is_NonSelectRound() {
        return SR_Is_NonSelectRound;
    }

    public void setSR_Is_NonSelectRound(boolean SR_Is_NonSelectRound) {
        this.SR_Is_NonSelectRound = SR_Is_NonSelectRound;
    }

    public boolean isSR_IsEditSterileProgram() {
        return SR_IsEditSterileProgram;
    }

    public void setSR_IsEditSterileProgram(boolean SR_IsEditSterileProgram) {
        this.SR_IsEditSterileProgram = SR_IsEditSterileProgram;
    }

    public boolean isSR_IsNotApprove() {
        return SR_IsNotApprove;
    }

    public void setSR_IsNotApprove(boolean SR_IsNotApprove) {
        this.SR_IsNotApprove = SR_IsNotApprove;
    }

    public boolean isSR_IsPrintByUser() {
        return SR_IsPrintByUser;
    }

    public void setSR_IsPrintByUser(boolean SR_IsPrintByUser) {
        this.SR_IsPrintByUser = SR_IsPrintByUser;
    }

    public boolean isSR_IsMultiDocument() {
        return SR_IsMultiDocument;
    }

    public void setSR_IsMultiDocument(boolean SR_IsMultiDocument) {
        this.SR_IsMultiDocument = SR_IsMultiDocument;
    }

    public boolean isSR_UsedStickerTH() {
        return SR_UsedStickerTH;
    }

    public void setSR_UsedStickerTH(boolean SR_UsedStickerTH) {
        this.SR_UsedStickerTH = SR_UsedStickerTH;
    }

    public boolean isPA_IsUsedRecipienter() {
        return PA_IsUsedRecipienter;
    }

    public void setPA_IsUsedRecipienter(boolean PA_IsUsedRecipienter) {
        this.PA_IsUsedRecipienter = PA_IsUsedRecipienter;
    }

    public boolean isPA_IsUsedApprover() {
        return PA_IsUsedApprover;
    }

    public void setPA_IsUsedApprover(boolean PA_IsUsedApprover) {
        this.PA_IsUsedApprover = PA_IsUsedApprover;
    }

    public boolean isPA_IsConfirmClosePayout() {
        return PA_IsConfirmClosePayout;
    }

    public void setPA_IsConfirmClosePayout(boolean PA_IsConfirmClosePayout) {
        this.PA_IsConfirmClosePayout = PA_IsConfirmClosePayout;
    }

    public boolean isPA_IsUsedDepartmentQR() {
        return PA_IsUsedDepartmentQR;
    }

    public void setPA_IsUsedDepartmentQR(boolean PA_IsUsedDepartmentQR) {
        this.PA_IsUsedDepartmentQR = PA_IsUsedDepartmentQR;
    }

    public boolean isPA_DefaultDepartmentQR() {
        return PA_DefaultDepartmentQR;
    }

    public void setPA_DefaultDepartmentQR(boolean PA_DefaultDepartmentQR) {
        this.PA_DefaultDepartmentQR = PA_DefaultDepartmentQR;
    }

    public boolean isPA_IsEditManualPayoutQty() {
        return PA_IsEditManualPayoutQty;
    }

    public void setPA_IsEditManualPayoutQty(boolean PA_IsEditManualPayoutQty) {
        this.PA_IsEditManualPayoutQty = PA_IsEditManualPayoutQty;
    }

    public boolean isPA_IsCreateReceiveDepartment() {
        return PA_IsCreateReceiveDepartment;
    }

    public void setPA_IsCreateReceiveDepartment(boolean PA_IsCreateReceiveDepartment) {
        this.PA_IsCreateReceiveDepartment = PA_IsCreateReceiveDepartment;
    }

    public boolean isSR_ReceiveFromDeposit() {
        return SR_ReceiveFromDeposit;
    }

    public void setSR_ReceiveFromDeposit(boolean SR_ReceiveFromDeposit) {
        this.SR_ReceiveFromDeposit = SR_ReceiveFromDeposit;
    }

    public boolean isST_SoundAndroidVersion9() {
        return ST_SoundAndroidVersion9;
    }

    public void setST_SoundAndroidVersion9(boolean ST_SoundAndroidVersion9) {
        this.ST_SoundAndroidVersion9 = ST_SoundAndroidVersion9;
    }

    public boolean isPA_IsNotificationPopupExpiringScan() {
        return PA_IsNotificationPopupExpiringScan;
    }

    public void setPA_IsNotificationPopupExpiringScan(boolean PA_IsNotificationPopupExpiringScan) {
        this.PA_IsNotificationPopupExpiringScan = PA_IsNotificationPopupExpiringScan;
    }

    public boolean isPA_IsUsedPayOkSound() {
        return PA_IsUsedPayOkSound;
    }

    public void setPA_IsUsedPayOkSound(boolean PA_IsUsedPayOkSound) {
        this.PA_IsUsedPayOkSound = PA_IsUsedPayOkSound;
    }
}
