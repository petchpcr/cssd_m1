<?php
//EDIT LOG
// 23-01-2026 13.20 : เพิ่ม Building_ID (B_ID) ในการดึงข้อมูล
require 'connect.php';
$array = array();

$UsageCode= $_POST['UsageCode'];
$USAGE = substr($UsageCode,0,6);
$B_ID = $_POST['B_ID'];
$DocNo = $_POST['DocNo'];
$p_DB = $_POST['p_DB'];

if($p_DB == 0){
   $group1 = "";
}else if($p_DB == 1){
   $group1 = ",itemdetail.Qty";
}

$Sql1 = "SELECT      itemdetail.Qty

         FROM        sendsteriledetail

         INNER JOIN  itemstock 
         ON          itemstock.UsageCode = sendsteriledetail.UsageCode

         INNER JOIN  itemdetail 
         ON          itemdetail.itemcode = itemstock.ItemCode

         INNER JOIN  item 
         ON          item.itemcode = itemdetail.itemDetailID

         WHERE       itemdetail.itemcode = '$USAGE'

         AND         sendsteriledetail.B_ID = '$B_ID'

         GROUP BY    item.itemcode $group1";

$meQuery = $conn->prepare($Sql1);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
   $Qty = $Result["Qty"];
   $Total += $Qty;
   
}

if($p_DB == 0){
   $group = "";
}else if($p_DB == 1){
   $group = ",itemdetail.ID";
}

$Sql1 = "SELECT
            COALESCE (
               (
                  SELECT      remarkadmin.QtyItemDetail

                  FROM        remarkadmin

                  INNER JOIN  itemstock 
                  ON          remarkadmin.ItemStockID = itemstock.RowID

                  INNER JOIN  item 
                  ON          itemstock.ItemCode = item.itemcode

                  INNER JOIN  sendsterile 
                  ON          remarkadmin.SensterileDocNo = sendsterile.DocNo

                  INNER JOIN  sendsteriledetail 
                  ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                  WHERE       remarkadmin.ItemDetailID = itemdetail.ID

                  AND         remarkadmin.Isstatus != 1
                  AND         remarkadmin.IsNew = 1
                  AND         itemstock.UsageCode = '$UsageCode'
                  AND         remarkadmin.SensterileDocNo = '$DocNo'
                  AND         sendsteriledetail.B_ID = '$B_ID'

                  GROUP BY    remarkadmin.QtyItemDetail
               ),
               0
            ) AS QtyItemDetail,
            itemdetail.Qty AS Qty_item

         FROM     sendsteriledetail

         INNER JOIN  itemstock 
         ON          itemstock.UsageCode = sendsteriledetail.UsageCode

         INNER JOIN  itemdetail 
         ON          itemdetail.itemcode = itemstock.ItemCode

         INNER JOIN  item 
         ON          item.itemcode = itemdetail.itemDetailID

         LEFT JOIN   remarkadmin 
         ON          item.itemcode = remarkadmin.ItemCode

         WHERE       itemstock.UsageCode = '$UsageCode'
         AND         sendsteriledetail.B_ID = '$B_ID'
         
         GROUP BY    item.itemcode, 
                     remarkadmin.QtyItemDetail,
	                  itemdetail.Qty  $group";

$meQuery = $conn->prepare($Sql1);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
   $QtyItemDetail = $Result["QtyItemDetail"];
   $Qty_item = $Result["Qty_item"];

   if($QtyItemDetail > $Qty_item){
      $Total1 += $QtyItemDetail;
   }else{
      $Total2 += $QtyItemDetail;
   }

   $Total3 = $Total1 - $Total2;
  
}

$Sum = $Total + $Total3 ;
array_push(
   $array,
   array('Qty' => $Sum)
);

echo json_encode(array("result"=> $array));

unset($conn);
die;
 ?>