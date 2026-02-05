<?php
//EDIT LOG
//24-01-2026 8.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$remarkselect = $_POST['remarkselect'];
$noteremark = $_POST['noteremark'];
$senddocno = $_POST['senddocno'];
$usagecode = $_POST['usagecode'];
$itemname = $_POST['itemname'];
$depname = $_POST['depname'];
$EmpCode = $_POST['EmpCode'];
$Type = $_POST['EmpCode'];
$Qty_save = $_POST['Qty_save'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$strSQL = " SELECT  remarktype.NameType,
                    remarktype.ID
            FROM    remarktype
            WHERE   remarktype.NameType = '$remarkselect'
            AND     remarktype.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $remarkselect = $Result["ID"];
}

if ($senddocno == '') {

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    $strSQL = " SELECT  $top
                        sendsteriledetail.SendSterileDocNo
                FROM
                    sendsterile
                INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                INNER JOIN itemstock ON sendsteriledetail.ItemStockID = itemstock.RowID
                WHERE
                    itemstock.UsageCode = '$usagecode'
                AND         sendsterile.B_ID = $B_ID
                ORDER BY
                    sendsterile.DocNo DESC
                $limit";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $SendSterileDocNo = $Result["SendSterileDocNo"];
    }
}

$strSQL = " SELECT
                employee.ID
            FROM
                employee
            WHERE
                employee.EmpCode = '$EmpCode'
                AND employee.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $IDEm = $Result["ID"];
}

$strSQL = " SELECT
                itemstock.RowID,
                itemstock.ItemCode
            FROM
                itemstock
            WHERE
                itemstock.UsageCode = '$usagecode'
                AND itemstock.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $RowID = $Result["RowID"];
    $ItemCode = $Result["ItemCode"];
}

$strSQL = " SELECT
                item.itemcode AS itemcodedt
            FROM
                item
            WHERE
                item.itemname = '$itemname'
                AND     item.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $itemcodedt = $Result["itemcodedt"];
}

$strSQL = " SELECT
                itemdetail.ID
            FROM
                itemdetail
            WHERE
                itemdetail.itemcode = '$ItemCode'
            AND itemdetail.itemDetailID = '$itemcodedt'
            AND itemdetail.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $ID = $Result["ID"];
}

if ($senddocno != '') {
    $strSQL = " SELECT
                remarkadmin.ID AS IDRow
            FROM
                remarkadmin
            WHERE
                remarkadmin.SensterileDocNo = '$senddocno'
            AND remarkadmin.ItemStockID = '$RowID'
            AND remarkadmin.ItemDetailID = '$ID'
            AND remarkadmin.B_ID = $B_ID ";
            
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $IDRow = $Result["IDRow"];
    }
}else {
    $strSQL = " SELECT
                remarkadmin.ID AS IDRow
            FROM
                remarkadmin
            WHERE
                remarkadmin.SensterileDocNo = '$SendSterileDocNo'
            AND remarkadmin.ItemStockID = '$RowID'
            AND remarkadmin.ItemDetailID = '$ID'
            AND remarkadmin.B_ID = $B_ID ";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $IDRow = $Result["IDRow"];
    }
}


if ($IDRow != '') {
    $Sql = "UPDATE remarkadmin
            SET remarkadmin.Isstatus = 0,
            remarkadmin.DateUnRemark = NULL,
            remarkadmin.UnAdminRemerk = NULL,
            remarkadmin.AdminRemark = $IDEm,
            remarkadmin.IsNew = 1,
            remarkadmin.DepRemark = '$depname',
            remarkadmin.RemarkTypeID = '$remarkselect',
            remarkadmin.Note = '$noteremark',
            remarkadmin.QtyItemDetail = '$Qty_save'
            WHERE
                remarkadmin.ID = '$IDRow'";
}else {
    if($p_DB == 0){
        $date = "NOW()";
    }else if($p_DB == 1){
        $date = "GETDATE()";
    }
    if ($senddocno != '') {
        $Sql = "INSERT INTO remarkadmin (
                remarkadmin.RemarkTypeID,
                remarkadmin.Note,
                remarkadmin.SensterileDocNo,
                remarkadmin.ItemStockID,
                remarkadmin.ItemDetailID,
                remarkadmin.DateRemark,
                remarkadmin.DepRemark,
                remarkadmin.Isstatus,
                remarkadmin.AdminRemark,
                remarkadmin.ItemCode,
                remarkadmin.QtyItemDetail,
                remarkadmin.IsNew,
                B_ID
            )
            VALUES
                (
                    $remarkselect,
                    '$noteremark',
                    '$senddocno',
                    '$RowID',
                    '$ID',
                    $date,
                    '$depname',
                    '0',
                    '$IDEm',
                    '$itemcodedt',
                    '$Qty_save',
                    1,
                    $B_ID
                )";   
    }else {
        $Sql = "INSERT INTO remarkadmin (
                remarkadmin.RemarkTypeID,
                remarkadmin.Note,
                remarkadmin.SensterileDocNo,
                remarkadmin.ItemStockID,
                remarkadmin.ItemDetailID,
                remarkadmin.DateRemark,
                remarkadmin.DepRemark,
                remarkadmin.Isstatus,
                remarkadmin.AdminRemark,
                remarkadmin.ItemCode,
                remarkadmin.QtyItemDetail,
                remarkadmin.IsNew,
                B_ID
            )
            VALUES
                (
                    $remarkselect,
                    '$noteremark',
                    '$SendSterileDocNo',
                    '$RowID',
                    '$ID',
                    $date,
                    '$depname',
                    '0',
                    '$IDEm',
                    '$itemcodedt',
                    '$Qty_save',
                    1,
                    $B_ID
                )";   
    }
}

$query1 = $conn->prepare($Sql);
$query1->execute();

if(!empty($query1)){
  array_push($resArray,array('finish' => 'true'));
}else{
  array_push($resArray,array('finish' => 'false'));
}


echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>