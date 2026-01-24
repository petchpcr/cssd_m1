<?php
// EDIT LOG
// 22-01-2026: แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$p_DB = $_POST['p_DB'];
	$basket_id = $_POST['basket_id'];
	$mac_id = $_POST['mac_id'];
	$B_ID = $_POST['B_ID'];

	$Sql_chk = "SELECT
					IsActive
				FROM
				sterilemachine
				WHERE
				id = $mac_id
				AND B_ID = $B_ID";

	$meQuery = $conn->prepare($Sql_chk);
	$meQuery->execute();

	$IsActive = 0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$IsActive = $Result["IsActive"];
	}

	if($IsActive==0){
		$sql_basket = "	UPDATE 	basket 
		SET 	InMachineID = null ,RefDocNo = null
		WHERE 	BasketCode = '$basket_id' AND B_ID = '$B_ID'";

		$result = $conn->prepare($sql_basket);
		$result->execute();

		array_push(
			$resArray,
			array(	'result'=>"A",'sql'=>$Sql,
				)
			);
	}else{
		array_push(
			$resArray,
			array(	'result'=>"E",'sql'=>$Sql,
				)
			);
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
