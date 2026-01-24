<?php
//EDIT LOG
// 23-01-2026 10.28 : เพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_qr"])) {

    $p_qr = $_POST['p_qr'];
    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

        $date = " AND DATE(sendsterile.DocDate) = DATE(NOW())";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

        $date = " AND FORMAT(sendsterile.DocDate , 'dd/MM/yyyy,') =  FORMAT(GETDATE() , 'dd/MM/yyyy,')";

    }
    //$p_not_in_data = $_POST['p_not_in_data'];

    $d_opt = "E";
    $d_usage_code = "";
    $d_complete = 0;

    $sql_query = "  SELECT      $top
                                item.itemname,
                                item.itemcode,
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
                                0 AS ItemCount

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

                    WHERE       itemstock.UsageCode = '$p_qr'

                    AND         itemstock.IsCancel = 0
                    AND         itemstock.IsStatus <> 8
                    AND         sendsterile.B_ID = $B_ID
                    
                    $date

                    ORDER BY    sendsteriledetail.ID DESC

                    $limit ";

    $meQuery = $conn->prepare($sql_query);
    $meQuery->execute();	

    if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

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

        array_push($resArray,
            array(  'result' => "A",
                    'UsageCode' => $UsageCode,
                    'ItemCode' => $ItemCode,
                    'itemname' => $itemname,
                    'Qty' => $Qty,
                    'IsSterile' => $IsSterile,
                    'Remark' => $Remark,
                    'DepName2' => $DepName2,
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
                    'payoutIsStatus' => '0',
            )
        );
    } else {
        array_push(
          $resArray, array(
              'result' => "E",
              'Message' => $sql_query,
          )
      );
    }

} else {
    array_push(
        $resArray, array(
            'result' => "I",
            'Message' => 'ข้อมูลที่ส่งมาไม่ถูกต้อง!!',
        )
    );
}

echo json_encode(array("result" => $resArray));

unset($conn);
die;

?>