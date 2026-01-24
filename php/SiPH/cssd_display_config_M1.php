<?php
//EDIT LOG
//24-01-2026 11.08 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$Sql_1 = "SELECT    *

          FROM      Config_M1 
          
          WHERE     Config_M1.B_ID = $B_ID 
          ORDER BY  Id ASC";
            
$meQuery = $conn->prepare($Sql_1);
$meQuery->execute();

while($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){

  array_push($resArray,array(
    'result' => 'A',
        'CngId' => $Result['Id'],
        'CngName' => $Result['Cng_Name'],
        'CngComment' => $Result['Cng_Comment'],
        'isActive' => $Result['isActive'],
        'isStatus' => $Result['isStatus'],
        'imgBtn' => $Result['imgBtn'],
        'showBtn' => $Result['showBtn'],
  ));

}

echo json_encode(array("result"=>$resArray));

unset($conn);
    die;

?>
