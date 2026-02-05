<?php

//EDIT LOG
//24-01-2026 9.24 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();
  
$DocNo = $_POST['DocNo'];
$B_ID = $_POST['B_ID'];
$p_DB = $_POST['p_DB'];

if($p_DB == 0){
  $top = " ";
  $limit = " LIMIT 1";
  $datenow = " NOW() ";
  $dateonly = " DATE( ";
  $ifnull = "IFNULL";
}else if($p_DB == 1){
  $top = "TOP 1 ";
  $limit = " ";
  $datenow = " GETDATE() ";
  $dateonly = " CONVERT(DATE, ";
  $ifnull = "ISNULL";
}

$Sql = "SELECT
          testresult.DocNo,
          testresult.CountAtp,
          $ifnull(testresult.IsATP, 0) AS IsATP,
          $ifnull(testresult.ATP_Code, 0) AS ATP_Code
        FROM
          testresult
        WHERE
          testresult.DocNo = '$DocNo'
        AND testresult.B_ID = $B_ID
        AND testresult.IsATP != 0";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();
	
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    array_push($resArray, array(
        'DocNo' => $Result['DocNo'],
        'CountAtp' => $Result['CountAtp'],
        'IsATP' => $Result['IsATP'],
        'ATP_Code' => $Result['ATP_Code'],
    ));
}

echo json_encode(array("result"=>$resArray));
unset($conn);
die;

?>