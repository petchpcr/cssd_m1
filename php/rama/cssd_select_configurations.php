<?php
//EDIT LOG
//24-01-2026 11.10 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
	require 'connect.php';
	$resArray = array();
	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST['B_ID'];

	if($p_DB == 0){
		$top = "";
		$limit = "LIMIT 1";
	}else if($p_DB == 1){
		$top = "TOP 1";
		$limit = "";
	}
	$Sql = "SELECT  $top   
					*       
			FROM 	configuration
			WHERE 	B_ID = $B_ID

			$limit ";

	$mQuery = $conn->prepare($Sql);
	$mQuery->execute();
	if ($Result = $mQuery->fetch(PDO::FETCH_ASSOC)){	

		array_push( $resArray,array( 
			'result'=>'A',
			'Message'=>'Complete !!',

			'MN_IsUsedFormNonUsage' => false,
			'MN_IsUsedComputeDate' => false,
			'MN_IsUsedReceiveDevice' => false,

			'MD_URL' => $Result["MD_URL"],
			'MD_IsUsedSoundScanQR' => (boolean)$Result["MD_IsUsedSoundScanQR"],
			'MD_IsAutoItemCode' => (boolean)$Result["MD_IsAutoItemCode"],
			'MD_IsItemPriceCode' => (boolean)$Result["MD_IsItemPriceCode"],
			'MD_IsUsedItemCode2' => (boolean)$Result["MD_IsUsedItemCode2"],
			'MD_IsShowQrcodeOnSlip' => (boolean)$Result["MD_IsShowQrcodeOnSlip"],
			'MD_IsShowThermometer' => (boolean)$Result["MD_IsShowThermometer"],
			'MD_IsShowThermometerID' => $Result["MD_IsShowThermometerID"],

            'SS_IsUsedItemSet' => (boolean)$Result["SS_IsUsedItemSet"],
			'SS_IsCopyPayout' => (boolean)$Result["SS_IsCopyPayout"],
			'SS_IsShowSender' => (boolean)$Result["SS_IsShowSender"],
			'SS_IsReceiverDropdown' => (boolean)$Result["SS_IsReceiverDropdown"],
			'SS_IsApprove' => (boolean)$Result["SS_IsApprove"],
			'SS_IsUsedItemDepartment' => (boolean)$Result["SS_IsUsedItemDepartment"],
			'SS_IsUsedReceiveTime' => (boolean)$Result["SS_IsUsedReceiveTime"],
			'SS_IsNonSelectDepartment' => (boolean)$Result["SS_IsNonSelectDepartment"],
			'SS_IsGroupPayout' => (boolean)$Result["SS_IsGroupPayout"],
			'SS_IsSortByUsedCount' => (boolean)$Result["SS_IsSortByUsedCount"],
			'SS_IsUsedZoneSterile' => (boolean)$Result["SS_IsUsedZoneSterile"],
			'SS_IsUsedBasket' => (boolean)$Result["SS_IsUsedBasket"],
			'SS_IsUsedNotification' => (boolean)$Result["SS_IsUsedNotification"],
			'SS_IsUsedRemarks' => (boolean)$Result["SS_IsUsedRemarks"],
			'SS_IsUsedSelfWashDepartment' => (boolean)$Result["SS_IsUsedSelfWashDepartment"],
			'SS_IsUsedClosePayout' => (boolean)$Result["SS_IsUsedClosePayout"],
			'SS_IsUsedChangeDepartment' => (boolean)$Result["SS_IsUsedChangeDepartment"],
			'SS_IsSeparateToSterile' => (boolean)$Result["SS_IsSeparateToSterile"],
			'SS_IsWashNotPassToDepartment' => (boolean)$Result["SS_IsWashNotPassToDepartment"],
			'SS_IsGroupSendsterile' => (boolean)$Result["SS_IsGroupSendsterile"],
			'SS_IsShowDetailSendSterileOnReport' => (boolean)$Result["SS_IsShowDetailSendSterileOnReport"],

			'ST_IsDuplicateAuthentication' => (boolean)$Result["ST_IsDuplicateAuthentication"],

			'SR_IsUsedPreparer' => (boolean)$Result["SR_IsUsedPreparer"],
			'SR_IsUsedApprover' => (boolean)$Result["SR_IsUsedApprover"],
			'SR_IsUsedPacker' => (boolean)$Result["SR_IsUsedPacker"],
			'SR_IsUsedSteriler' => (boolean)$Result["SR_IsUsedSteriler"],
			'SR_IsUsedDBUserOperation' => (boolean)$Result["SR_IsUsedDBUserOperation"],
			'SR_IsUsedDropdownUserOperation' => (boolean)$Result["SR_IsUsedDropdownUserOperation"],
			'SR_IsRememberUserOperation' => (boolean)$Result["SR_IsRememberUserOperation"],
			'SR_IsEditRound' => (boolean)$Result["SR_IsEditRound"],
			'SR_IsUsedOccupancyRate' => (boolean)$Result["SR_IsUsedOccupancyRate"],
			'SR_IsUsedUserOperationDetail' => (boolean)$Result["SR_IsUsedUserOperationDetail"],
			'SR_IsApproveSterile' => (boolean)$Result["SR_IsApproveSterile"],
			'SR_IsShowFormCheckList' => (boolean)$Result["SR_IsShowFormCheckList"],
			'SR_IsUsedImportNonReuse' => (boolean)$Result["SR_IsUsedImportNonReuse"],
			'SR_IncExp' => (boolean)$Result["SR_IncExp"],
			'SR_Is_Preview_Print_Sticker' => (boolean)$Result["SR_Is_Preview_Print_Sticker"],
			'SR_Is_NonSelectRound' => (boolean)$Result["SR_Is_NonSelectRound"],
			'SR_IsEditSterileProgram'=> (boolean)$Result["SR_IsEditSterileProgram"],
			'SR_IsNotApprove'=> (boolean)$Result["SR_IsNotApprove"],
			'SR_IsPrintByUser'=> (boolean)$Result["SR_IsPrintByUser"],
			'SR_IsMultiDocument'=> (boolean)$Result["SR_IsMultiDocument"],
			'SR_UsedStickerTH'=> (boolean)$Result["SR_UsedStickerTH"],
			'SR_IsUsedNotification'=> (boolean)$Result["SR_IsUsedNotification"],
			'SR_ReceiveFromDeposit'=> (boolean)$Result["SR_ReceiveFromDeposit"],
			'SR_IsUsedScanRemovePrintComplete'=> (boolean)$Result["SR_IsUsedScanRemovePrintComplete"],
			'SR_IsUsedScanImportAll'=> (boolean)$Result["SR_IsUsedScanImportAll"],
			'SR_IsUsedScanRemoveAll'=> (boolean)$Result["SR_IsUsedScanRemoveAll"],

			'PA_IsUsedRecipienter' =>(boolean)$Result["PA_IsUsedRecipienter"],
			'PA_IsUsedApprover' =>(boolean)$Result["PA_IsUsedApprover"],
			'PA_IsConfirmClosePayout' =>(boolean)$Result["PA_IsConfirmClosePayout"],
			'PA_IsUsedDepartmentQR' =>(boolean)$Result["PA_IsUsedDepartmentQR"],
			'PA_DefaultDepartmentQR' =>(boolean)$Result["PA_DefaultDepartmentQR"],
			'PA_IsEditManualPayoutQty' => (boolean)$Result["PA_IsEditManualPayoutQty"],
			'PA_IsCreateReceiveDepartment' => (boolean)$Result["PA_IsCreateReceiveDepartment"],
			'PA_IsUsedFIFO' => (boolean)$Result["PA_IsUsedFIFO"],
			'PA_IsUsedZonePayout' => (boolean)$Result["PA_IsUsedZonePayout"],
			'PA_IsShowBorrowNotReturn' => (boolean)$Result["PA_IsShowBorrowNotReturn"],
			'PA_SelectPrintSlipOrReport' => (int)$Result["PA_SelectPrintSlipOrReport"],
			'PA_IsAddPayoutDepartment' => (int)$Result["PA_IsAddPayoutDepartment"],
			'PA_IsCloseDocumentOnRemark' => (boolean)$Result["PA_IsCloseDocumentOnRemark"],
			'PA_IsWastingPayout' => (boolean)$Result["PA_IsWastingPayout"],
			'PA_IsShowMenuAutoPay' => (boolean)$Result["PA_IsShowMenuAutoPay"],
			'PA_IsShowNotificationItemBorrow' => (boolean)$Result["PA_IsShowNotificationItemBorrow"],
			'PA_IsShowTransports' => (boolean)$Result["PA_IsShowTransports"],
			'PA_SwitchDocNoPay' => (boolean)$Result["PA_SwitchDocNoPay"],
			'PA_IsGroupDepartment' => (boolean)$Result["PA_IsGroupDepartment"],
			'PA_IsShowShelflifeItem' => (boolean)$Result["PA_IsShowShelflifeItem"],

			'WA_IsUsedWash' => (boolean)$Result["WA_IsUsedWash"],
			'WA_IsUsedNotification' => (boolean)$Result["WA_IsUsedNotification"],
			
			'AP_NotApproveReturnToPreviousProcess' => (int)$Result["AP_NotApproveReturnToPreviousProcess"],
			'AP_AddRickReturnToPreviousProcess' => (int)$Result["AP_AddRickReturnToPreviousProcess"],
			'AP_IsUsedNotification' => (boolean)$Result["AP_IsUsedNotification"],
			'AP_UsedScanForApprove' => (boolean)$Result["AP_UsedScanForApprove"],
			'AP_NotApproveReturnToPreviousMultiProcess' => (boolean)$Result["AP_NotApproveReturnToPreviousMultiProcess"],
			'AP_NotApproveReturnOnBasket' => (boolean)$Result["AP_NotApproveReturnOnBasket"],

			'ST_IsUsedNotification' => (boolean)$Result["ST_IsUsedNotification"],
			'ST_SoundAndroidVersion9' => (boolean)$Result["ST_SoundAndroidVersion9"],
			'ST_UrlAuthentication' => $Result["ST_UrlAuthentication"],
			'ST_IsUsedEnterPasswordAfterScanLogin' => (boolean)$Result["ST_IsUsedEnterPasswordAfterScanLogin"],

			'PA_IsNotificationPopupExpiringScan' => (boolean)$Result["PA_IsNotificationPopupExpiringScan"],
			'PA_IsUsedPayOkSound' => (boolean)$Result["PA_IsUsedPayOkSound"],

			'SS_IsFixOptTypeInBasket' => (boolean)$Result["SS_IsFixOptTypeInBasket"],
			'SS_IsMatchBasketAndType' => (boolean)$Result["SS_IsMatchBasketAndType"],
			'SR_IsUsedBasket_M1' => (boolean)$Result["SR_IsUsedBasket_M1"],
			'SR_IsUsedLot' => (boolean)$Result["SR_IsUsedLot"],

		));

	}else {
		array_push( 
			$resArray,array(
				'result'=>"E",
				'Message'=>'ไม่สามารถโหลดคอนฟิกได้ !!',
				'SQL'=>$Sql,
			)
		);
	}

	echo json_encode(array("result" => $resArray));

	unset($conn);
	die;
			
?>