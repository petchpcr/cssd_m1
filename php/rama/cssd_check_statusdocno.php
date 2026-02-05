<?php
//EDIT LOG
//24-01-2026 8.53 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$senddocno = $_POST['senddocno'];
$p_DB = $_POST['p_DB'];

$B_ID = $_POST["B_ID"];
$AND_B_ID = "";
if($B_ID != "0"){
    $AND_B_ID = " AND B_ID = $B_ID";
}


$strSQL = " SELECT      sendsterile.IsStatus

            FROM        sendsterile
            
            WHERE       sendsterile.DocNo = '$senddocno'
            $AND_B_ID";

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