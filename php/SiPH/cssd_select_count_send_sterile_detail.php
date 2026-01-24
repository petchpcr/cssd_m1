<?php
//EDIT LOG
// 23-01-26 12:37 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';

$resArray = array();

// check for post data
if ( isset($_POST["p_docno"])) {	
	
	$d_result = "E";
	$d_message = "ไม่สามารถลบเอกสารได้ !!";

	$p_docno = $_POST["p_docno"];
	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST['B_ID'];

	if($p_DB == 0){
		$top = "";
		$limit = "LIMIT 1";
	}else if($p_DB == 1){
		$top = "TOP 1";
		$limit = "";
	}

	$strSQL = "	SELECT		$top	
							COUNT(*) AS c

				FROM 		sendsteriledetail

				WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'
				AND 		sendsteriledetail.IsCancel = 0 
				AND 		sendsteriledetail.B_ID = $B_ID

				$limit ";

	$result = $conn->prepare($strSQL);
	$result->execute();

	if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
		if((int)$row["c"] == 0){
			$d_result = "A";
		}else{
			$d_result = "E";
		}

	}
	
}

array_push(
	$resArray, 
		array(
		'result' => $d_result,
		'Message'=> $d_message
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
