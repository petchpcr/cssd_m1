<?php
//EDIT LOG
//23-01-2026 15.49 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

if (isset($_POST["p_qr"])) {

    $p_qr = $_POST['p_qr'];
    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    $B_ID = $_POST['B_ID'];

    $strSQL = 
        " SELECT        $top
                        item.itemcode,
                        item.itemname,
                        itemstock.UsageCode,
                        itemstock.RowID

          FROM          itemstock

          INNER JOIN    item
          ON            item.itemcode = itemstock.ItemCode

          WHERE         itemstock.UsageCode ='$p_qr' 
          AND           itemstock.IsStatus = 4 
          AND           itemstock.B_ID = '$B_ID'
          AND           itemstock.IsPay = 1    
          AND           itemstock.IsCancel = 0 

          $limit ";

    $result = $conn->prepare($strSQL);
    $result->execute();

    $i=0;
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $i++;
        array_push(
            $resArray, array(
                'result' => "A",
                'itemcode' => $row["itemcode"],
                'itemname' => $row["itemname"],
                'UsageCode' => $row["UsageCode"],
                'RowID' => $row["RowID"],
            )
        );

    }

    if($i==0){
        array_push(
            $resArray, array(
                'result' => "E",
                'SQL' => $strSQL,
                'Message' => 'ไม่พบข้อมูล!!',
            )
        );
    }
} else {
    array_push(
        $resArray, array(
            'result' => "I",
            'Message' => 'ข้อมูลที่ส่งมาไม่ถูกต้อง !!',
        )
    );
}

echo json_encode(array("result" => $resArray));
unset($conn);
die;

?>