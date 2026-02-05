<?php
// EDIT LOG
// 22-01-2026 11.10 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST["B_ID"];
	$WHERE_B_ID = "";
	if($B_ID != "0"){
		$WHERE_B_ID = " AND B_ID = $B_ID";
	}

	$p_DB = $_POST['p_DB'];

	$basket_id = $_POST['basket_id'];

	$Sql = "SELECT
				ID,
				BasketCode,
				BasketName,
				InMachineID,
				TypeId,
				RefDocNo
			FROM
				basket 
			WHERE
				BasketType = 1 
				AND IsCancel = 0
				$WHERE_B_ID

			";


	if($basket_id!="null"){
		$Sql = $Sql."AND BasketCode = '$basket_id' ";
	}

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$xID 			= $Result["ID"];
		$BasketCode 	= $Result["BasketCode"];
		$BasketName 	= $Result["BasketName"];
		$InMachineID 	= $Result["InMachineID"];
		$TypeId 	= $Result["TypeId"];
		$RefDocNo 	= $Result["RefDocNo"];

		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'xID'			=>$xID,
					'BasketCode'	=>$BasketCode,
					'BasketName'	=>$BasketName,
					'InMachineID'	=>$InMachineID,
					'TypeId'	=>$TypeId,
					'RefDocNo'	=>$RefDocNo
				)
			);

		$i++;
	}


	if($i == 0) {
		array_push(
			$resArray,
			array(	'xID'			=>'',
					'BasketCode'	=>'',
					'BasketName'	=>'',
					'InMachineID'		=>'',
					'TypeId'	=>'',
					'RefDocNo'	=>''
				)
			);
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
