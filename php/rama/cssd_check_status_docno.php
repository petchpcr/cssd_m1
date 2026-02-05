<?php
//EDIT LOG
//23-01-2026 15.36 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$p_docNo = $_POST['p_docNo'];

$B_ID = $_POST["B_ID"];
$AND_B_ID = "";
if($B_ID != "0"){
    $AND_B_ID = " AND B_ID = $B_ID";
	}

// ========================================
$strSQL = "	SELECT
                sendsterile.IsStatus
            FROM
                sendsterile
            WHERE
                sendsterile.DocNo = '$p_docNo'
            $AND_B_ID ";
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
