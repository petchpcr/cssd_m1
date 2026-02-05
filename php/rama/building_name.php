<?php

//EDIT LOG
//24-01-2026 11.08 : คืนค่า ชื่ออาคารทั้งหมดพร้อม B_ID
require 'connect.php';

$resArray = array();
$Sql = "    SELECT * FROM organization";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    array_push($resArray,array(
        'building_name' => $Result['OrgName'],
        'B_ID' => $Result['B_ID'],
    ));
}

echo json_encode(array("result"=>$resArray));

?>