<?php
//EDIT LOG
// 23-01-2026 13.08 : เพิ่ม Building_ID (B_ID) ในการดึงข้อมูล
require 'connect.php';
$array = array();

$p_is_used_remarks = $_POST['p_is_used_remarks'];
$UsageCode = $_POST['UsageCode'];
$B_ID = $_POST['B_ID'];
$DocNo = $_POST['DocNo'];
$p_DB = $_POST['p_DB'];

if($p_is_used_remarks == "0"){

  $Sql1 = " SELECT        itemstock.ItemCode,
                          item.itemname,
                          itemdetail.Qty

            FROM          itemstock

            INNER JOIN    itemdetail
            ON            itemdetail.itemcode = itemstock.ItemCode

            INNER JOIN    item
            ON            item.itemcode = itemdetail.itemDetailID

            WHERE         itemstock.UsageCode ='$UsageCode'
            AND           itemstock.B_ID = '$B_ID' 
            AND 		      itemstock.IsCancel = 0 ";

  $meQuery = $conn->prepare($Sql1);
  $meQuery->execute();
  while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

      $itemname = $Result["itemname"];
      $Qty = $Result["Qty"];

      array_push(
        $array,
        array(
          'itemname' => $itemname,
          'Qty' => $Qty,
          'MutiPic_Remark' => '',
          'RemarkAdmin' => '',
          'IsPicture' => '',
          'RemarkItemCode' => '',
          'DocNo' => '',
          'QtyItemDetail' => '0'
        )
      );
  }

}else{

  $Sql1 = " SELECT    sendsterile.IsStatus

            FROM      sendsterile

            WHERE     sendsterile.DocNo = '$DocNo'";
  $meQuery = $conn->prepare($Sql1);
  $meQuery->execute();
  while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $d_isstatus = $Result["IsStatus"];
  } 

  if ($d_isstatus == '1') {

    if($p_DB == 0){

      $Sql1 = " SELECT
                  *
                FROM
                  (
                    SELECT
                      (
                        SELECT
                          configprogram.MutiPic_Remark
                        FROM
                          configprogram
                        WHERE configprogram.B_ID = $B_ID
                      ) AS MutiPic_Remark,
                      sendsteriledetail.UsageCode,
                      itemstock.ItemCode,
                      item.itemname,
                      itemdetail.Qty,
                      (
                        SELECT
                          COUNT(remarkadmin.ItemDetailID) AS remarkadmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.IsNew = 1
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.B_ID = '$B_ID'
                      ) AS RemarkAdmin,
                      (
                        SELECT
                          COUNT(remarkadmin.IsPicture) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.IsPicture = 1
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.B_ID = '$B_ID'
                      ) AS IsPicture,
                      remarkadmin.ItemCode AS RemarkItemCode,
                      COALESCE (
                        (
                          SELECT
                            remarkadmin.QtyItemDetail
                          FROM
                            remarkadmin
                          INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                          INNER JOIN item ON itemstock.ItemCode = item.itemcode
                          INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                          INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                          WHERE
                            remarkadmin.ItemDetailID = itemdetail.ID
                          AND remarkadmin.Isstatus != 1
                          AND remarkadmin.IsNew = 1
                          AND itemstock.UsageCode = '$UsageCode'
                          AND remarkadmin.SensterileDocNo = '$DocNo'
                          AND remarkadmin.B_ID = '$B_ID'
                          GROUP BY
                            remarkadmin.QtyItemDetail
                        ),
                        0
                      ) AS QtyItemDetail
                    FROM
                      sendsteriledetail
                    INNER JOIN itemstock ON itemstock.UsageCode = sendsteriledetail.UsageCode
                    INNER JOIN itemdetail ON itemdetail.itemcode = itemstock.ItemCode
                    INNER JOIN item ON item.itemcode = itemdetail.itemDetailID
                    LEFT JOIN remarkadmin ON item.itemcode = remarkadmin.ItemCode
                    WHERE
                      itemstock.UsageCode = '$UsageCode'
                    AND sendsteriledetail.B_ID = $B_ID
                    AND remarkadmin.B_ID = $B_ID
                    GROUP BY
                      item.itemcode
                  ) AS Sql1
                ORDER BY
                  Sql1.QtyItemDetail DESC";

    }else if($p_DB == 1){

      $Sql1 = " SELECT
                  *
                FROM
                  (
                    SELECT
                      (
                        SELECT
                          configprogram.MutiPic_Remark
                        FROM
                          configprogram
                        WHERE configprogram.B_ID = $B_ID
                      ) AS MutiPic_Remark,
                      sendsteriledetail.UsageCode,
                      itemstock.ItemCode,
                      item.itemname,
                      itemdetail.Qty,
                      (
                        SELECT
                          COUNT(remarkadmin.ItemDetailID) AS remarkadmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.IsNew = 1
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.B_ID = '$B_ID'
                      ) AS RemarkAdmin,
                      (
                        SELECT
                          COUNT(remarkadmin.IsPicture) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.IsPicture = 1
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.B_ID = '$B_ID'
                      ) AS IsPicture,
                      remarkadmin.ItemCode AS RemarkItemCode,
                      COALESCE (
                        (
                          SELECT
                            remarkadmin.QtyItemDetail
                          FROM
                            remarkadmin
                          INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                          INNER JOIN item ON itemstock.ItemCode = item.itemcode
                          INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                          INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                          WHERE
                            remarkadmin.ItemDetailID = itemdetail.ID
                          AND remarkadmin.Isstatus != 1
                          AND remarkadmin.IsNew = 1
                          AND itemstock.UsageCode = '$UsageCode'
                          AND remarkadmin.SensterileDocNo = '$DocNo'
                          AND remarkadmin.B_ID = '$B_ID'
                          GROUP BY
                            remarkadmin.QtyItemDetail
                        ),
                        0
                      ) AS QtyItemDetail
                    FROM
                      sendsteriledetail
                    INNER JOIN itemstock ON itemstock.UsageCode = sendsteriledetail.UsageCode
                    INNER JOIN itemdetail ON itemdetail.itemcode = itemstock.ItemCode
                    INNER JOIN item ON item.itemcode = itemdetail.itemDetailID
                    LEFT JOIN remarkadmin ON item.itemcode = remarkadmin.ItemCode
                    WHERE
                      itemstock.UsageCode = '$UsageCode'
                    AND sendsteriledetail.B_ID = '$B_ID'
                    AND remarkadmin.B_ID = '$B_ID'
                    GROUP BY
                    item.itemcode,
										sendsteriledetail.UsageCode,
                    itemstock.ItemCode,
                    item.itemname,
                    itemdetail.Qty,
										itemdetail.ID,
										remarkadmin.ItemCode
                  ) AS Sql1
                ORDER BY
                  Sql1.QtyItemDetail DESC";
    }

    $meQuery = $conn->prepare($Sql1);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
      $MutiPic_Remark = $Result["MutiPic_Remark"];
      $itemname = $Result["itemname"];
      $Qty = $Result["Qty"];
      $RemarkAdmin = $Result["RemarkAdmin"];
      $IsPicture = $Result["IsPicture"];
      $RemarkItemCode = $Result["RemarkItemCode"];
      $QtyItemDetail = $Result["QtyItemDetail"];
  
      array_push(
        $array,
        array(
          'MutiPic_Remark' => $MutiPic_Remark,
          'itemname' => $itemname,
          'RemarkAdmin' => $RemarkAdmin,
          'Qty' => $Qty,
          'IsPicture' => $IsPicture,
          'RemarkItemCode' => $RemarkItemCode,
          'DocNo' => $DocNo,
          'QtyItemDetail' => $QtyItemDetail
        )
      );
    }   

  }else {

    if($p_DB == 0){

      $Sql1 = " SELECT
                  *
                FROM
                  (
                    SELECT
                      (
                        SELECT
                          configprogram.MutiPic_Remark
                        FROM
                          configprogram
                          WHERE configprogram.B_ID = $B_ID
                      ) AS MutiPic_Remark,
                      sendsteriledetail.UsageCode,
                      itemstock.ItemCode,
                      item.itemname,
                      itemdetail.Qty,
                      (
                        SELECT
                          COUNT(remarkadmin.ItemDetailID) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.IsNew = 1
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.B_ID = $B_ID
                      ) AS RemarkAdmin,
                      (
                        SELECT
                          COUNT(remarkadmin.IsPicture) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.IsPicture = 1
                        AND remarkadmin.IsNew = 1
                        AND remarkadmin.B_ID = $B_ID
                      ) AS IsPicture,
                      remarkadmin.ItemCode AS RemarkItemCode,
                      COALESCE (
                        (
                          SELECT
                            remarkadmin.QtyItemDetail
                          FROM
                            remarkadmin
                          INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                          INNER JOIN item ON itemstock.ItemCode = item.itemcode
                          INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                          INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                          WHERE
                            remarkadmin.ItemDetailID = itemdetail.ID
                          AND remarkadmin.Isstatus != 1
                          AND remarkadmin.IsNew = 1
                          AND itemstock.UsageCode = '$UsageCode'
                          AND remarkadmin.SensterileDocNo = '$DocNo'
                          AND remarkadmin.B_ID = $B_ID
                          GROUP BY
                            remarkadmin.QtyItemDetail
                        ),
                        0
                      ) AS QtyItemDetail
                    FROM
                      sendsteriledetail
                    INNER JOIN itemstock ON itemstock.UsageCode = sendsteriledetail.UsageCode
                    INNER JOIN itemdetail ON itemdetail.itemcode = itemstock.ItemCode
                    INNER JOIN item ON item.itemcode = itemdetail.itemDetailID
                    LEFT JOIN remarkadmin ON item.itemcode = remarkadmin.ItemCode
                    WHERE
                      itemstock.UsageCode = '$UsageCode'
                    AND sendsteriledetail.B_ID = $B_ID
                    AND remarkadmin.B_ID = $B_ID
                    GROUP BY
                      item.itemcode
                  ) AS Sql1
                ORDER BY
                  Sql1.QtyItemDetail DESC";

    }else if($p_DB == 1){

      $Sql1 = " SELECT
                  *
                FROM
                  (
                    SELECT
                      (
                        SELECT
                          configprogram.MutiPic_Remark
                        FROM
                          configprogram
                        WHERE configprogram.B_ID = $B_ID
                      ) AS MutiPic_Remark,
                      sendsteriledetail.UsageCode,
                      itemstock.ItemCode,
                      item.itemname,
                      itemdetail.Qty,
                      (
                        SELECT
                          COUNT(remarkadmin.ItemDetailID) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND remarkadmin.Isstatus != 1
                        AND remarkadmin.IsNew = 1
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.B_ID = $B_ID
                      ) AS RemarkAdmin,
                      (
                        SELECT
                          COUNT(remarkadmin.IsPicture) AS RemarkAdmin
                        FROM
                          remarkadmin
                        INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                        INNER JOIN item ON itemstock.ItemCode = item.itemcode
                        INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                        INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                        WHERE
                          remarkadmin.ItemDetailID = itemdetail.ID
                        AND itemstock.UsageCode = '$UsageCode'
                        AND remarkadmin.SensterileDocNo = '$DocNo'
                        AND remarkadmin.B_ID = $B_ID
                        AND remarkadmin.IsPicture = 1
                        AND remarkadmin.IsNew = 1
                      ) AS IsPicture,
                      remarkadmin.ItemCode AS RemarkItemCode,
                      COALESCE (
                        (
                          SELECT
                            remarkadmin.QtyItemDetail
                          FROM
                            remarkadmin
                          INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                          INNER JOIN item ON itemstock.ItemCode = item.itemcode
                          INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                          INNER JOIN sendsteriledetail ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                          WHERE
                            remarkadmin.ItemDetailID = itemdetail.ID
                          AND remarkadmin.Isstatus != 1
                          AND remarkadmin.IsNew = 1
                          AND itemstock.UsageCode = '$UsageCode'
                          AND remarkadmin.SensterileDocNo = '$DocNo'
                          AND remarkadmin.B_ID = $B_ID
                          GROUP BY
                            remarkadmin.QtyItemDetail
                        ),
                        0
                      ) AS QtyItemDetail
                    FROM
                      sendsteriledetail
                    INNER JOIN itemstock ON itemstock.UsageCode = sendsteriledetail.UsageCode
                    INNER JOIN itemdetail ON itemdetail.itemcode = itemstock.ItemCode
                    INNER JOIN item ON item.itemcode = itemdetail.itemDetailID
                    LEFT JOIN remarkadmin ON item.itemcode = remarkadmin.ItemCode
                    WHERE
                      itemstock.UsageCode = '$UsageCode'
                    AND sendsteriledetail.B_ID = '$B_ID'
                    AND remarkadmin.B_ID = $B_ID
                    GROUP BY
                      item.itemcode,
                      sendsteriledetail.UsageCode,
                      itemstock.ItemCode,
                      item.itemname,
                      itemdetail.Qty,
                      itemdetail.ID,
                      remarkadmin.ItemCode
                  ) AS Sql1
                ORDER BY
                  Sql1.RemarkAdmin DESC";
    }

    $meQuery = $conn->prepare($Sql1);
    $meQuery->execute();

  

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
      $MutiPic_Remark = $Result["MutiPic_Remark"];
      $itemname = $Result["itemname"];
      $Qty = $Result["Qty"];
      $RemarkAdmin = $Result["RemarkAdmin"];
      $IsPicture = $Result["IsPicture"];
      $RemarkItemCode = $Result["RemarkItemCode"];
      $QtyItemDetail = $Result["QtyItemDetail"];
  
      array_push(
        $array,
        array(
          'MutiPic_Remark' => $MutiPic_Remark,
          'itemname' => $itemname,
          'RemarkAdmin' => $RemarkAdmin,
          'Qty' => $Qty,
          'IsPicture' => $IsPicture,
          'RemarkItemCode' => $RemarkItemCode,
          'DocNo' => $DocNo,
          'QtyItemDetail' => $QtyItemDetail
        )
      );
    }   
  }
   
}


echo json_encode(array("result" => $array));

unset($conn);
die;

?>
