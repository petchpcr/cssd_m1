<?php
//EDIT LOG
//24-01-2026 8.35 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
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


$page = $_POST['page'];
$RowId = $_POST['RowId'];
$itemdetail = $_POST['itemdetail'];
$itemcode = $_POST['itemcode'];
$type = $_POST['type'];
$p_DB = $_POST['p_DB'];

$B_ID = $_POST['B_ID'];

$strSQL = " SELECT
                configuration_permission.IsStatus
            FROM
                configuration_permission
            WHERE
                configuration_permission.ID = 2
            AND configuration_permission.IsCancel != 1
            AND configuration_permission.IsView != 0
            AND configuration_permission.B_ID = $B_ID ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $IsStatus = $Result["IsStatus"];
}

if ($page == 'checklist') {
    if ($type == 'approve') {
        if ($IsStatus == 0) {
            $strSQL = " SELECT
                            remarkadmin.SensterileDocNo
                        FROM
                            remarkadmin
                        WHERE
                            remarkadmin.ItemDetailID = '$itemdetail'
                        AND remarkadmin.ItemCode = '$itemcode'
                        AND remarkadmin.ItemStockID = '$RowId'
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.IsstatusApprove != 1
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.B_ID = $B_ID ";
            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $SensterileDocNo = $Result["SensterileDocNo"];

                $strSQL = " SELECT
                            remarkadmin.ID AS IDChecklist,
                            remarkadmin.SensterileDocNo
                        FROM
                            remarkadmin
                        WHERE
                            remarkadmin.SensterileDocNo = '$SensterileDocNo'
                        AND remarkadmin.ItemStockID = '$RowId'
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.IsstatusApprove != 1
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.B_ID = $B_ID ";
                $meQuery = $conn->prepare($strSQL);
                $meQuery->execute();
                while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                    $IDChecklist = $Result["IDChecklist"];

                    $Sql = "UPDATE remarkadmin
                            SET remarkadmin.IsstatusApprove = 1,
                            remarkadmin.AdminApprove = '$EmpCode'
                            WHERE
                                remarkadmin.ID = '$IDChecklist'";
                    mysqli_query($conn, $Sql);
                }
            }

            array_push($resArray, array('finish' => 'trueapprove'));
        }else {
            $strSQL = " SELECT
                            remarkadmin.ID AS IDChecklist
                        FROM
                            remarkadmin
                        WHERE
                            remarkadmin.ItemDetailID = '$itemdetail'
                        AND remarkadmin.ItemCode = '$itemcode'
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.ItemStockID = '$RowId'
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.B_ID = $B_ID ";
            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $IDChecklist = $Result["IDChecklist"];
                $Sql = "UPDATE RemarkAdmin
                        SET remarkadmin.IsstatusApprove = 1,
                            remarkadmin.AdminApprove = '$EmpCode'
                        WHERE
                            remarkadmin.ItemCode = '$itemcode'
                        AND remarkadmin.ItemStockID = '$RowId'";
                    $meQuery = $conn->prepare($Sql);
                if ($meQuery->execute()) {
                    array_push($resArray, array('finish' => 'trueapprove'));
                } else {
                    array_push($resArray, array('finish' => 'falseapprove'));
                }
            }
        }
       
    }else if ($type == 'delete'){

        if($p_DB == 0){
            $top = "";
            $limit = "LIMIT 1";
        }else if($p_DB == 1){
            $top = "TOP 1";
            $limit = "";
        }

        $Sql1 = "SELECT $top
            remarkadmin.SensterileDocNo
        FROM
            remarkadmin
        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
        WHERE
            itemstock.UsageCode = '$p_usage_code'
        AND remarkadmin.B_ID = $B_ID
        ORDER BY
            remarkadmin.ID DESC
        $limit ";

        $meQuery = $conn->prepare($Sql1);
        $meQuery->execute();
        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $SensterileDocNo = $Result["SensterileDocNo"];
        }

        $strSQL = " SELECT
                    remarkadmin.ID AS IDChecklist
                FROM
                    remarkadmin
                WHERE
                    remarkadmin.ItemDetailID = '$itemdetail'
                AND remarkadmin.ItemCode = '$itemcode'
                AND remarkadmin.ItemStockID = '$RowId'
                AND remarkadmin.Isstatus != 1
                AND remarkadmin.B_ID = $B_ID ";

        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();
        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

            if($p_DB == 0){
                $date = "NOW()";
            }else if($p_DB == 1){
                $date = "getDate()";
            }

            $IDChecklist = $Result["IDChecklist"];
            $Sql = "UPDATE remarkadmin
                    SET remarkadmin.DateUnRemark = $date,
                    remarkadmin.UnAdminRemerk = '$EmpCode',
                    remarkadmin.Isstatus = 1,
                    remarkadmin.IsNew = 0,
                    remarkadmin.IsstatusApprove = 0,
                    remarkadmin.Picture = NULL,
                    remarkadmin.IsPicture = 0,
                    remarkadmin.IsRemarkRound = 0
                    WHERE
                        remarkadmin.ItemCode = '$itemcode'
                    AND remarkadmin.ItemStockID = '$RowId'
                    AND remarkadmin.B_ID = $B_ID ";

            $query1 = $conn->prepare($Sql);
            

            if ($query1->execute()) {
                array_push($resArray, array('finish' => 'truedelete'));
            } else {
                array_push($resArray, array('finish' => 'falsedelete'));
            }
        }
    }
}else {
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
        $ID_emcode = $Result["ID"];
    }

    $strSQL = " SELECT
                    itemstock.RowID,
                    itemstock.ItemCode
                FROM
                    itemstock
                WHERE
                    itemstock.UsageCode = '$usagecode'";

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
                    AND item.B_ID = $B_ID ";

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

    if($p_DB == 0){
        $date = "NOW()";
    }else if($p_DB == 1){
        $date = "getDate()";
    }

    $Sql = "UPDATE remarkadmin
            SET remarkadmin.DateUnRemark = $date,
                remarkadmin.UnAdminRemerk = '$ID_emcode',
                remarkadmin.Isstatus = 1,
                remarkadmin.Picture = NULL,
                remarkadmin.IsPicture = 0,
                remarkadmin.IsstatusApprove = 0
            WHERE
                remarkadmin.ItemCode = '$itemcodedt'
            AND remarkadmin.ItemStockID = '$RowID'";

    $query1 = $conn->prepare($Sql);
    $query1->execute();

    array_push($resArray, array('finish' => 'true'));

}

echo json_encode(array("result"=> $resArray));
unset($conn);
die;
?>