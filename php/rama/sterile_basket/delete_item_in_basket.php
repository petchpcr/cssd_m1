<?php
// EDIT LOG
// 22-01-2026 11.15 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query	
// 22-01-2026 11.25 : ยังไม่ได้แก้ไข B_ID ในการ UPDATE
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST['B_ID'];
	$p_DB = $_POST['p_DB'];

	$ID = $_POST['ID'];
	$basket_id = $_POST['basket_id'];

	
	$ID = "(" . substr($ID, 0, -1) . ")";

	$Sql = "UPDATE 	itemstockdetailbasket 
			SET 	IsActive = 0 
			WHERE 	ID IN $ID";

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();

	$Sql_chk = "SELECT
				*
			FROM
				itemstockdetailbasket
			WHERE
				BasketID = '$basket_id'
				AND IsActive = 1
				AND B_ID = '$B_ID'";

	$meQuery = $conn->prepare($Sql_chk);
	$meQuery->execute();

	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$i++;
	}

	if($i==0){
		$sql_basket = "	UPDATE 	basket 
		SET 	InMachineID = null ,RefDocNo = null
		WHERE 	BasketID = '$basket_id'";

		$result = $conn->prepare($sql_basket);
		$result->execute();
	}

	array_push(
		$resArray,
		array(	'result'=>"A",'sql'=>$Sql,
			)
		);

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
