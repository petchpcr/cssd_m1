<?php
//EDIT LOG
// 23-01-2026 11.49 : ตรวจสอบการใช้ Building_ID (B_ID) ในการ update ไม่มีการเพิ่ม B_ID ลงไป
require 'connect.php';
$resArray = array();

// check for post data
if ( isset($_POST["p_data"]) && isset($_POST["p_is_cancel"])) {	 

	$p_data = $_POST["p_data"];
	$p_is_cancel = $_POST["p_is_cancel"];
	if($p_DB == 0){
		$date = "NOW()";
	}else if($p_DB == 1){
		$date = "getDate()";
	}
	if($p_is_cancel == "1"){
		$sql_update = 	 "UPDATE itemstock SET IsStatus = 8, IsCancel = 1, CancelDate = $date WHERE RowID IN ($p_data) ";
	}else{

		$p_is_status = $_POST["p_is_status"];

		if($p_is_status == "0" || $p_is_status == "3"){
			$d_is_pay = "0";
		}else if($p_is_status == "4" || $p_is_status == "5"){
			$d_is_pay = "1";
		}

		$sql_update = 	 "UPDATE itemstock SET IsStatus = $p_is_status, IsPay = $d_is_pay, IsCancel = 0, CancelDate = null WHERE RowID = $p_data ";
	}
					
	$res_update = $conn->prepare($sql_update);
	$res_update->execute();

	array_push( 
		$resArray,array(
			'result'=>"A",
			'Message'=>"ทำการเปลี่ยนข้อมูลสำเร็จ !!",
		)
	);
	
}else if ( isset($_POST["p_id"]) && isset($_POST["p_is_cancel"])) {	

	$p_id = $_POST["p_id"];
	$p_is_cancel = $_POST["p_is_cancel"];
	$p_DB = $_POST['p_DB'];

	if($p_DB == 0){
		$date = "NOW()";
	}else if($p_DB == 1){
		$date = "getDate()";
	}
	if($p_is_cancel == "1"){
		$sql_update =	"UPDATE itemstock SET IsStatus = 8, IsCancel = 1, CancelDate = $date WHERE RowID = $p_id ";
		$message = 	 	"ทำการยกเลิก Usage Code สำเร็จ !!";
	}else{
		$sql_update = 	 "UPDATE itemstock SET UsageCount = 0 WHERE RowID = $p_id ";
		$message = 	 	"ทำการ Reset Usage Code สำเร็จ !!";
	}

	$res_update = $conn->prepare($sql_update);
	$res_update->execute();

	array_push( 
		$resArray,array(
			'result'=>"A",
			'Message'=>$message,
		)
	);

}else{
	array_push( 
		$resArray,array(
			'result'=>"I",
			'SQL'=>'',
		)
	);
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?> 
