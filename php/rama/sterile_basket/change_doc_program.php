<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

$docno = $_POST['docno'];
$p_SterileProgramID = $_POST['p_SterileProgramID'];
// $B_ID = $_POST['B_ID'];

$SqlUpdate="UPDATE sterile SET SterileProgramID ='$p_SterileProgramID' WHERE sterile.DocNo='$docno'";

if($stmt = $conn->query( $SqlUpdate )){
	array_push( $resArray,array('result' => "A",)  );
}else{
	array_push( $resArray,array('result' => "E",));
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>
