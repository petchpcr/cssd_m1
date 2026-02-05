<?php
//EDIT LOG
// 22-01-2026 15.45 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$array = array();

$p_DB = $_POST['p_DB']??"";
$B_ID = $_POST['B_ID'];

$Sql = "SELECT 		department.ID,
					department.DepName2 AS xName 

		FROM 		department 

        WHERE       department.IsCancel = 0 
        AND         department.B_ID = $B_ID

		ORDER BY 	COALESCE(department.PriorityNo, 1000) ASC, DepName2 ASC ";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

while($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
    $xID = $Result["ID"];
    $xName = $Result["xName"];

    array_push(
        $array, 
        array(  'xID' => $xID, 
                'xName' => $xName
            )
        );
}

echo json_encode(array("result" => $array));

unset($conn);
    die;
