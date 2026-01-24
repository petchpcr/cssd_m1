<?php
//EDIT LOG
//23-01-2026 15.36 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$p_docNo = $_POST['p_docNo'];
$p_bid = $_POST['p_bid'];
$B_ID = $_POST['B_ID'];

// ========================================
$strSQL = "	SELECT
                sendsterile.IsStatus
            FROM
                sendsterile
            WHERE
                sendsterile.DocNo = '$p_docNo'
                AND sendsterile.B_ID = '$p_bid' ";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
$i = 0;
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $IsStatus = $Result["IsStatus"];
    $i ++;
    array_push(
        $resArray,
        array(
            'IsStatus' => $IsStatus,
            'finish' => true,
        )
    );
}	
// ========================================
if ($i == 0) {
    array_push(
        $resArray,
        array(
            'IsStatus' => "",
            'finish' => false,
        )
    );
}			

echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>
