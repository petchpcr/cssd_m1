<?php
//EDIT LOG
//24-01-2026 8.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$senddocno = $_POST['senddocno'];
$usagecode = $_POST['usagecode'];
$itemname = $_POST['itemname'];
$p_DB = $_POST['p_DB'];

$B_ID = $_POST["B_ID"];
$WHERE_B_ID = "";
$AND_B_ID = "";
$sendsterile_B_ID = "";
$remarkadmin_B_ID = "";	
if($B_ID != "0"){
	$WHERE_B_ID = " WHERE B_ID = $B_ID";
	$AND_B_ID = " AND B_ID = $B_ID";
	$sendsterile_B_ID = " AND sendsterile.B_ID = $B_ID";
	$remarkadmin_B_ID = " AND remarkadmin.B_ID = $B_ID";
}

if ($senddocno == '') {
    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }
    $strSQL = " SELECT      $top
                            sendsteriledetail.SendSterileDocNo

                FROM        sendsterile

                INNER JOIN  sendsteriledetail 
                ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                INNER JOIN  itemstock 
                ON          sendsteriledetail.ItemStockID = itemstock.RowID

                WHERE       itemstock.UsageCode = '$usagecode'
                $sendsterile_B_ID

                ORDER BY    sendsterile.DocNo DESC
                
                $limit";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $SendSterileDocNo = $Result["SendSterileDocNo"];
    }
}

$strSQL = " SELECT  item.itemcode AS itemcodedt

            FROM    item

            WHERE   item.itemname = '$itemname'
            $AND_B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $itemcodedt = $Result["itemcodedt"];
}

$strSQL = " SELECT  itemstock.RowID

            FROM    itemstock

            WHERE   itemstock.UsageCode = '$usagecode'
            $AND_B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $RowID = $Result["RowID"];
}

if ($senddocno != '') {
    $strSQL = " SELECT  remarkadmin.ID

                FROM    remarkadmin

                WHERE   remarkadmin.ItemStockID = '$RowID'
                AND     remarkadmin.ItemCode = '$itemcodedt'
                AND     remarkadmin.SensterileDocNo = '$senddocno'
                AND     remarkadmin.Isstatus != 1
                $remarkadmin_B_ID";
    $cnt = 0;
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt++;
    }
}else {
    $strSQL = " SELECT  remarkadmin.ID

                FROM    remarkadmin

                WHERE   remarkadmin.ItemStockID = '$RowID'
                AND     remarkadmin.ItemCode = '$itemcodedt'
                AND     remarkadmin.SensterileDocNo = '$SendSterileDocNo'
                AND     remarkadmin.Isstatus != 1
                $remarkadmin_B_ID ";
    $cnt = 0;
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt++;
    }
}


if ($cnt == 0){
    if ($cnt == 0) {
        $cnt = 1;
    }
    array_push($resArray,array('finish' => $cnt));
}else{
    if ($senddocno != '') {
        array_push($resArray, array('finish' => $cnt));
    }else {
        $cnt = 1;
        array_push($resArray, array('finish' => $cnt));
    }
}


echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>