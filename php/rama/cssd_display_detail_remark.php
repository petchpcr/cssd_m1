<?php
//EDIT LOG
//24-01-2026 8.52 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$senddocno = $_POST['senddocno'];
$itemname = $_POST['itemname'];
$Type = $_POST['Type'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$strSQL1 = " SELECT
                item.itemcode AS itemcodedt
            FROM
                item
            WHERE
                item.itemname = '$itemname'
                AND     item.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL1);
$meQuery->execute();                
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $itemcodedt = $Result["itemcodedt"];
}

$strSQL = " SELECT
                    remarkadmin.RemarkTypeID,
                    remarkadmin.Note,
                    remarkadmin.DepRemark
                FROM
                    remarkadmin
                WHERE
                    remarkadmin.ItemCode = '$itemcodedt'
                    AND remarkadmin.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $RemarkTypeID = $Result["RemarkTypeID"];
    $Note = $Result["Note"];
    $DepRemark = $Result["DepRemark"];


    array_push(
        $resArray,
        array(
            'RemarkTypeID' => $RemarkTypeID,
            'Note' => $Note,
            'DepRemark' => $DepRemark
        )
    );
}    


echo json_encode(array("result"=> $resArray));
unset($conn);
die;
?>