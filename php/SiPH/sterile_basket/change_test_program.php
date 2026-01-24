<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

$docno = $_POST['docno'];
$TestProgramID = $_POST['TestProgramID'];
$B_ID = $_POST['B_ID'];	

$SqlUpdateSterile="UPDATE sterile SET TestProgramID ='$TestProgramID' WHERE sterile.DocNo='$docno' AND sterile.B_ID = $B_ID";

if($stmt1 = $conn->query( $SqlUpdateSterile )){
	array_push( $resArray,array('result' => "A",)  );
}else{
	array_push( $resArray,array('result' => "E2",));
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>
