<?php

// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 13-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';
$resArray = array();

$emp_code = $_POST['emp_code'];
$B_ID = $_POST['B_ID'];
$WHERE_B_ID = "";
if($B_ID != "0"){
    $WHERE_B_ID = " AND users.B_ID = $B_ID";
}

$Sql = "SELECT	users.ID,
					users.EmpCode,
					users.IsCancel

			FROM	users

			WHERE 	EmpCode = '$emp_code' 
			$WHERE_B_ID
			AND 	IsCancel=0";
	$i=0;
	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();

	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$EmpCode = $Result["EmpCode"];
		$id = $Result["ID"];


		if (isset($_POST["docno"])){
			$docno = $_POST['docno'];
			$SqlUpdate="UPDATE sterile SET SterileCode ='$id' WHERE sterile.DocNo='$docno'";

			if($stmt = $conn->query( $SqlUpdate )){
				array_push( $resArray,array('check'=>'true')  );
			}else{
				array_push( $resArray,array('check'=>'false'));
			}
		}else{
			array_push( $resArray,array('check'=>'true','id'=>$id)  );
		}

		$i++;
	}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>
