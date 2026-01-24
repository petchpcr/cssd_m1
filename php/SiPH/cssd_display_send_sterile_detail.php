<?php
//EDIT LOG
// 23-01-2026 12.54 : เพิ่ม Building_ID (B_ID) ในการดึงข้อมูล
require 'connect.php';
require 'class.php';

$array = array();
$dateobj = new DatetimeTH();

$DocNo = $_POST['DocNo'];
$B_ID = $_POST['B_ID'];
$p_DB = $_POST['p_DB'];
$p_is_su = $_POST['p_is_su'];
$payoutIsStatus = '1';

$Sql = "SELECT  sendsterile.IsStatus AS Status_Doc
        FROM    sendsterile 
        WHERE   sendsterile.DocNo = '$DocNo' 
        AND     sendsterile.B_ID = '$B_ID' ";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $Status_Doc = $Result["Status_Doc"];
}

if($Status_Doc == 4 || $Status_Doc == 0){
    $Status_sql = "AND sendsteriledetail.IsStatus = 0";
}else{
    $Status_sql = "";
}

if($p_is_su == 1){
    $Status_sql = "";
}

if($p_DB == 0){
    $group = "";
}else if($p_DB == 1){
    $group = ",
                a.IsRemarkExpress,
                a.RemarkSend,
                a.itemname,
                a.itemcode,
                a.Qty,
                a.Remark,
                a.xIsSterile,
                a.ssIsStatus,
                a.IsStatus,
                a.ResterileType,
                a.Resterilename,
                a.OccuranceQty,
                a.RowID,
                a.SS_rowID,
                a.Shelflife,
                a.PhysicianID,
                a.PhysicianName,
                a.HnInfo,
                a.HnName,
                a.HNID,
                a.RemarkExpress,
                a.IsDenger,
                a.UsageCount,
                b.ItemCode,
                b.ItemCount,
                a.NoWash";
}

$Sql3 = "SELECT * FROM (
                    SELECT
                                CASE WHEN remarkadmin.ItemStockID = sendsteriledetail.ItemStockID THEN
                                    1
                                ELSE
                                    0
                                END AS RemarkSend,
                                item.itemname,
                                item.itemcode,
                                item.NoWash ,
                                sendsteriledetail.Qty,
                                sendsteriledetail.Remark,
                                sendsteriledetail.IsSterile AS xIsSterile,
                                sendsteriledetail.UsageCode,
                                sendsteriledetail.IsStatus AS ssIsStatus,
                                sendsterile.IsStatus,
                                sendsteriledetail.ResterileType,
                                resteriletype.ResterileType AS Resterilename,
                                sendsteriledetail.OccuranceQty,
                                itemstock.RowID,
                                sendsteriledetail.ID AS SS_rowID,
                                packingmat.Shelflife,
                                COALESCE(sendsteriledetail.PhysicianID, '' ) AS PhysicianID,
                                COALESCE(physician.PhysicianName, '' ) AS PhysicianName,
                                COALESCE(hotpitalnumber.HnCode, '') AS HnInfo, 
                                COALESCE(CONCAT(hotpitalnumber.TitleName, ' ', hotpitalnumber.FName, ' ',hotpitalnumber.LName), '') AS HnName,
                                sendsteriledetail.HNID,
                                itemstock.RemarkExpress,
                                itemstock.IsRemarkExpress,
                                item.IsDenger,
                                itemstock.UsageCount

                    FROM        sendsteriledetail

                    INNER JOIN  itemstock
                    ON          itemstock.RowID = sendsteriledetail.ItemStockID

                    INNER JOIN  item
                    ON          itemstock.ItemCode = item.itemcode

                    INNER JOIN  sendsterile
                    ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                    LEFT JOIN   resteriletype
                    ON          sendsteriledetail.ResterileType = resteriletype.ID 

                    INNER JOIN  packingmat
                    ON          itemstock.PackingMatID = packingmat.ID 

                    LEFT JOIN 	physician 
                    ON			physician.PhysicianID = sendsteriledetail.PhysicianID 

                    LEFT JOIN 	hotpitalnumber 
                    ON			hotpitalnumber.Id = sendsteriledetail.HNID 

                    LEFT JOIN   remarkadmin 
                    ON          sendsteriledetail.ItemStockID = remarkadmin.ItemStockID

                    WHERE       sendsteriledetail.SendSterileDocNo='$DocNo'
                    AND         sendsteriledetail.IsCancel = 0
                    $Status_sql
                    AND         itemstock.IsCancel = 0
                    AND         sendsteriledetail.B_ID = $B_ID

                ) a,
        (SELECT     itemstock.ItemCode,
                    COUNT(itemstock.ItemCode) AS ItemCount

        FROM        sendsteriledetail

        INNER JOIN  itemstock
        ON          itemstock.RowID = sendsteriledetail.ItemStockID

        INNER JOIN  item
        ON          itemstock.ItemCode = item.itemcode

        WHERE       sendsteriledetail.SendSterileDocNo='$DocNo'
        AND         sendsteriledetail.IsCancel = 0
        $Status_sql
        AND         sendsteriledetail.B_ID = $B_ID
        AND         itemstock.IsCancel = 0
        GROUP BY    itemstock.ItemCode
    ) b
        WHERE         a.itemcode = b.itemcode
        GROUP BY      a.UsageCode $group
        ORDER BY      itemname,a.UsageCode ";
        
        //ORDER BY    SS_rowID DESC ";

$meQuery = $conn->prepare($Sql3);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $NoWash = $Result["NoWash"];
    $RemarkSend = $Result["RemarkSend"];
    $UsageCode = $Result["UsageCode"];
    $ItemCode = $Result["ItemCode"];
    $itemname = $Result["itemname"];
    $Qty = $Result["Qty"];
    $IsSterile = $Result["xIsSterile"];
    $Remark = $Result["Remark"];
    $IsStatus = $Result["IsStatus"];
    $ResterileType = $Result["ResterileType"];
    $Resterilename = $Result["Resterilename"];
    $OccuranceQty = $Result["OccuranceQty"];
    $RowID = $Result["RowID"];
    $SS_RowID = $Result["SS_rowID"];
    $Shelflife = $Result["Shelflife"];
    $ssIsStatus = $Result["ssIsStatus"];
    $ItemCount = $Result["ItemCount"];
    $PhysicianID = $Result["PhysicianID"];
    $PhysicianName = $Result["PhysicianName"];
    $HnInfo = $Result["HnInfo"]; 
    $HnName = $Result["HnName"];
    $HNID = $Result["HNID"];
    $RemarkExpress = $Result["RemarkExpress"] == null ? '' : $Result["RemarkExpress"];
    $IsRemarkExpress = $Result["IsRemarkExpress"];
    $IsDenger = $Result["IsDenger"];
    $UsageCount = $Result["UsageCount"];

    $Sql_get_basket = "SELECT basket.Alternatename 
    FROM basket,itemstockdetailbasket 
    WHERE basket.ID = itemstockdetailbasket.BasketID 
    AND SSDetailID = $SS_RowID
    AND basket.B_ID = $B_ID
    AND itemstockdetailbasket.B_ID = $B_ID ";

    $basket_meQuery = $conn->prepare($Sql_get_basket);
    $basket_meQuery->execute();
    $basket_Result = $basket_meQuery->fetch(PDO::FETCH_ASSOC);
    $basket = $basket_Result["Alternatename"];

    array_push($array,
        array('flag' => "true",
            'RemarkSend' => $RemarkSend,
            'UsageCode' => $UsageCode,
            'ItemCode' => $ItemCode,
            'itemname' => $itemname,
            'Qty' => $Qty,
            'IsSterile' => $IsSterile,
            'Remark' => $Remark,
            'DepName2' => '',
            'DocNo' => $DocNo,
            'IsStatus' => $IsStatus,
            'ResterileType' => $ResterileType,
            'Resterilename' => $Resterilename,
            'OccuranceQty' => $OccuranceQty,
            'RowID' => $RowID,
            'SS_RowID' => $SS_RowID,
            'Shelflife' => $Shelflife,
            'ssIsStatus' => $ssIsStatus,
            'ItemCount' => $ItemCount,
            'payoutIsStatus' => $payoutIsStatus,
            'PhysicianID' => $PhysicianID,
            'PhysicianName' => $PhysicianName,
            'HnInfo' => $HnInfo, 
            'HnName' => $HnName,
            'HNID' => $HNID,
            'RemarkExpress' => $RemarkExpress,
            'IsRemarkExpress' => $IsRemarkExpress,
            'IsDenger' => $IsDenger,
            'UsageCount' => $UsageCount,
            'basket' => $basket,
            'NoWash' => $NoWash,
        )
    );
}

echo json_encode(array("result" => $array));

unset($conn);
die;

?>