<?php
//EDIT LOG
//24-01-2026 11.06 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$userid = $_POST['userid'];

$Sql_1 = "SELECT * FROM config_menu_m1 WHERE userid = '$userid' AND B_ID = '$B_ID' ";
            
$meQuery = $conn->prepare($Sql_1);
$meQuery->execute();


if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {
  foreach ($row as $column => $value) {
    $resArray[$column] = $row[$column];
    // array_push(
    //   $resArray, 
    //     $column => $row[$column];
    // );
  }
}

echo json_encode($resArray);

unset($conn);
    die;

?>
