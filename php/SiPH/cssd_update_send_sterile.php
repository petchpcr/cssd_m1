<?php
//EDIT LOG
//23-01-2026 15.35 : ตรวจสอบ Building_ID ไม่มีการเพิ่ม B_ID ในการอัพเดท
require 'connect.php';

// check for post data
if ( isset($_POST["p_doc_no"]) && isset($_POST["p_value"]) && isset($_POST["p_field"]) ) {	

	// =======================================================================================
	$p_doc_no = $_POST["p_doc_no"];
	$p_value = $_POST["p_value"];
	$p_field = $_POST["p_field"];
	// =======================================================================================
			

	if($p_field == "24"){
		$sql_update = 	 "UPDATE sendsterile SET UserReceive = '$p_value' WHERE DocNo = '$p_doc_no' ";
	}else if($p_field == "25"){
		$sql_update = 	 "UPDATE sendsterile SET UserSend = '$p_value' WHERE DocNo = '$p_doc_no' ";
	}else if($p_field == "33"){
		$sql_update = 	 "UPDATE sendsterile SET ZoneID = '$p_value' WHERE DocNo = '$p_doc_no' ";
	}else {
		$sql_update = 	 "UPDATE sendsterile SET $p_field = '$p_value' WHERE DocNo = '$p_doc_no' ";
	}
		
		$res_update = $conn->prepare($sql_update);
		$res_update->execute();
		echo "A"; 


	// =======================================================================================
	
	unset($conn);
	die;
	
}else{
	echo "I"; 
}

?> 
