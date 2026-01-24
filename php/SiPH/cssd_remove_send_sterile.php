<?php
//EDIT LOG
// 23-01-2026 11.49 : ตรวจสอบการใช้ Building_ID (B_ID) ในการ update ไม่มีการเพิ่ม B_ID ลงไป
require 'connect.php';
$resArray = array();

// check for post data
if ( isset($_POST["p_docno"]) && isset($_POST["p_user"])) {	
	
	$d_result = "E";
	$d_message = "ไม่สามารถลบเอกสารได้ !!";

	$p_docno = $_POST["p_docno"];
	$p_user = $_POST["p_user"];
	$p_DB = $_POST['p_DB'];
	$d_prefix  = substr($p_docno, 0, 2);

	if($p_DB == 0){
		$date = "NOW()";
	}else if($p_DB == 1){
		$date = "GETDATE()";
	}


	if($d_prefix == "NA"){
		$sql_update = "	DELETE 	

						FROM 	sendsterile 
						
						WHERE 	DocNo = '$p_docno' ";

		$res_update = $conn->prepare($sql_update);
		$res_update->execute();

	}else{
		$sql_update = "	UPDATE 	sendsterile 
						SET 	ModifyDate = $date, 
								UserCode = $p_user, 
								IsCancel = 1, 
								Remark = 'ลบเอกสารไม่มีรายการ'  
						WHERE 	DocNo = '$p_docno' ";

		$res_update = $conn->prepare($sql_update);
		$res_update->execute();

		$d_result = "A";
		$d_message = "ลบเอกสารสำเร็จ !!";
	}

}

array_push(
	$resArray, 
		array(
			'result' => $d_result,
			'Message'=> $d_message,
	)
);
	
// -------------------------------------------------------
// echo json
// -------------------------------------------------------
echo json_encode(array("result" => $resArray));

// -------------------------------------------------------
// Close Connection
// -------------------------------------------------------
unset($conn);
die;

?> 
