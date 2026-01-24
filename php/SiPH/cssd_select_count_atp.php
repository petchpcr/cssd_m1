<?php
//EDIT LOG
//24-01-2026 9.25 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$DocNo = $_POST['DocNo'];
$B_ID = $_POST['B_ID'];
// ========================================
$strSQL = "	SELECT
                COUNT(testresult.IsATP) + 1 AS IsATP
            FROM
                testresult
            WHERE
                testresult.DocNo = '$DocNo'
            AND testresult.IsATP = 1
            AND testresult.B_ID = $B_ID";
$i = 0;

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
	
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $IsATP = $Result["IsATP"];
}
// ========================================
array_push(
    $resArray,
    array(
        'IsATP' => $IsATP,
    )
);			

echo json_encode(array("result"=>$resArray));
unset($conn);
die;
?>
