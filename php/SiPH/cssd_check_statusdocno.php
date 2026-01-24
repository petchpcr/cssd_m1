<?php
//EDIT LOG
//24-01-2026 8.53 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$senddocno = $_POST['senddocno'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$strSQL = " SELECT      sendsterile.IsStatus

            FROM        sendsterile
            
            WHERE       sendsterile.DocNo = '$senddocno'
            AND         sendsterile.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();

while($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
    $IsStatus = $Result["IsStatus"];

    array_push(
        $resArray, 
        array('IsStatus' => $IsStatus)
    );
}

echo json_encode(array("result"=>$resArray));
unset($conn);
    die;
?>