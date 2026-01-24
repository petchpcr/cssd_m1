<?php
//EDIT LOG
//24-01-2026 8.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$senddocno = $_POST['senddocno'];
$usagecode = $_POST['usagecode'];
$itemname = $_POST['itemname'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];
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
                AND         sendsterile.B_ID = $B_ID

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
            AND     item.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $itemcodedt = $Result["itemcodedt"];
}

$strSQL = " SELECT  itemstock.RowID

            FROM    itemstock

            WHERE   itemstock.UsageCode = '$usagecode'
            AND     itemstock.B_ID = $B_ID ";
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
                AND     remarkadmin.B_ID = $B_ID ";
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
                AND     remarkadmin.B_ID = $B_ID ";
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