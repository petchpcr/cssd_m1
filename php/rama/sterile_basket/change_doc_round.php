<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

$docno = $_POST['docno'];
$SterileRoundNumber = $_POST['SterileRoundNumber'];
// $B_ID = $_POST['B_ID'];

$SqlUpdateSterile="UPDATE sterile SET SterileRoundNumber ='$SterileRoundNumber' WHERE sterile.DocNo='$docno'";

$SqlUpdateBach="UPDATE batch SET MachineRound ='$SterileRoundNumber' WHERE batch.DocNo='$docno'";
if($stmt1 = $conn->query( $SqlUpdateSterile )){
	if($stmt2 = $conn->query( $SqlUpdateBach )){
		array_push( $resArray,array('result' => "A",)  );
	}else{
		array_push( $resArray,array('result' => "E1",));
	}
}else{
	array_push( $resArray,array('result' => "E2",));
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>
