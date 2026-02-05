<?php

//EDIT LOG
//23-01-2026 15.39 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query ข้อมูล
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();
//50094385
$DocNo = $_POST['DocNo'];
$sel = $_POST['sel'];

$Usagecode = $_POST['Usagecode'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$i = 0;
$count = 0;

$strSQL = "	SELECT
                itemstock.RowID
            FROM
                itemstock
            WHERE
                itemstock.UsageCode = '$Usagecode'
                AND itemstock.B_ID = '$B_ID'";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $RowID = $Result["RowID"];
}

$strSQL = "	SELECT
                configuration_permission.IsStatus
            FROM
                configuration_permission
            WHERE
                configuration_permission.ID = '20'
                AND configuration_permission.B_ID = '$B_ID'";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $IsStatus = $Result["IsStatus"];
}
// ========================================
// if ($sel == 2) {
    $strSQL = "	SELECT
                    CASE
                WHEN itemstock.UsageCount >= item.LimitUse AND item.LimitUse != 0 THEN
                    itemstock.UsageCode
                WHEN itemstock.UsageCount < item.LimitUse THEN
                    itemstock.UsageCode
                END AS UsageCode,
                item.itemname,
                itemstock.UsageCount
                FROM
                    item
                INNER JOIN itemstock ON item.itemcode = itemstock.ItemCode
                WHERE
                    itemstock.UsageCode = '$Usagecode'
                    AND itemstock.B_ID = '$B_ID'";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $UsageCode = $Result["UsageCode"];
        $itemname = $Result["itemname"];
        $UsageCount = $Result["UsageCount"];
        $i ++;
    }

    $strSQL = "	SELECT
                    COUNT(itemstock.IsRemarkExpress) AS condition1
                FROM
                    sendsteriledetail
                INNER JOIN itemstock ON sendsteriledetail.ItemStockID = itemstock.RowID
                INNER JOIN item ON itemstock.ItemCode = item.itemcode
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND sendsteriledetail.B_ID = '$B_ID'
                AND itemstock.IsRemarkExpress = 1";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $condition1 = $Result["condition1"];
        $i++;
    }

    $strSQL = "	SELECT
                    COUNT(itemstock.IsRemarkExpress) AS condition6
                FROM
                    sendsteriledetail
                INNER JOIN itemstock ON sendsteriledetail.ItemStockID = itemstock.RowID
                INNER JOIN item ON itemstock.ItemCode = item.itemcode
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND sendsteriledetail.B_ID = '$B_ID'
                AND itemstock.IsRemarkExpress = 2";

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $condition6 = $Result["condition6"];
        $i++;
    }

    $strSQL = "	SELECT
                    COUNT(item.IsDenger) AS condition2
                FROM
                    sendsteriledetail
                INNER JOIN itemstock ON sendsteriledetail.ItemStockID = itemstock.RowID
                INNER JOIN item ON itemstock.ItemCode = item.itemcode
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND sendsteriledetail.B_ID = '$B_ID'
                AND item.IsDenger = 1";

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $condition2 = $Result["condition2"];
        $i++;
    }

    $strSQLs = "SELECT
                    remarkadmin.ItemStockID AS Qty
                FROM
                    itemstock
                LEFT JOIN itemdetail ON itemdetail.itemcode = itemstock.ItemCode
                LEFT JOIN item ON item.itemcode = itemdetail.itemDetailID
                LEFT JOIN remarkadmin ON item.itemcode = remarkadmin.ItemCode
                LEFT JOIN washdetail ON remarkadmin.ItemStockID = washdetail.ItemStockID
                LEFT JOIN wash ON washdetail.WashDocNo = wash.DocNo
                LEFT JOIN sendsteriledetail ON itemstock.RowID = sendsteriledetail.ItemStockID
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND itemstock.RowID = remarkadmin.ItemStockID
                AND remarkadmin.Isstatus != 1
                AND item.B_ID = '$B_ID'
                AND itemstock.B_ID = '$B_ID'
                AND wash.B_ID = '$B_ID'
                GROUP BY
                    remarkadmin.ItemStockID";

    $meQuery = $conn->prepare($strSQLs);
    $meQuery->execute();

    $rowcount = 0;
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $ItemStockID = $Result["ItemStockID"];
        $rowcount++;
    }

    $strSQL = "	SELECT
                    COUNT(itemstock.UsageCount) AS condition3
                FROM
                    sendsteriledetail
                INNER JOIN itemstock ON sendsteriledetail.ItemStockID = itemstock.RowID
                INNER JOIN item ON itemstock.ItemCode = item.itemcode
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND sendsteriledetail.B_ID = '$B_ID'
                AND item.LimitUse > 0
                AND itemstock.UsageCount >= item.LimitUse";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $condition3 = $Result["condition3"];
        $i++;
    }

    $strSQL = "	SELECT
                    COUNT(
                        sendsteriledetail.ResterileType
                    ) AS condition5
                FROM
                    sendsteriledetail
                INNER JOIN resteriletype ON sendsteriledetail.ResterileType = resteriletype.ID
                WHERE
                    sendsteriledetail.SendSterileDocNo = '$DocNo'
                AND sendsteriledetail.ResterileType = 1
                AND sendsteriledetail.B_ID = '$B_ID'";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $condition5 = $Result["condition5"];
        $i++;
    }

// ========================================
if ($i == 0) {
    array_push(
        $resArray,
        array(
            'UsageCode' => "",
            'itemname' => "",
            'UsageCount' => "",
            'cnt' => "",
            'condition1' => "0",
            'condition2' => "0",
            'condition3' => "0",
            'condition4' => "0",
            'condition5' => "0",
            'condition6' => "0",
            'IsStatus' => "",
            'finish' => false,
        )
    );	
}else {
    array_push(
        $resArray,
        array(
            'UsageCode' => $UsageCode,
            'itemname' => $itemname,
            'UsageCount' => $UsageCount,
            'cnt' => $cnt,
            'condition1' => $condition1,
            'condition2' => $condition2,
            'condition4' => $condition3,
            'condition3' => $rowcount,
            'condition5' => $condition5,
            'condition6' => $condition6,
            'IsStatus' => $IsStatus,
            'finish' => true,
        )
    );	
}
		

echo json_encode(array("result"=> $resArray));

unset($conn);
die;
?>