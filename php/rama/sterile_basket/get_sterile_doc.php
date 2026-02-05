<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$p_DB = $_POST['p_DB'];

	$B_ID = $_POST["B_ID"];
	$WHERE_B_ID = "";
	if($B_ID != "0"){
		$WHERE_B_ID = " AND sterile.B_ID = $B_ID";
	}

	$docno = $_POST['docno'];

	$Sql = "SELECT 	SterileProgramID,
				SterileRoundNumber,
				sterileprogram.SterileName,
				sterileprogram.SterileName2,
				steriletype_process.SterileTypeID ,
				sterile.TestProgramID ,
				employee.FirstName,
				employee.LastName
			FROM sterile 
			LEFT JOIN sterileprogram ON sterile.SterileProgramID = sterileprogram.ID 
			LEFT JOIN steriletype_process ON sterile.SterileProgramID = steriletype_process.SterileProcessID
			LEFT JOIN users ON sterile.SterileCode = users.ID
			LEFT JOIN employee ON employee.EmpCode = users.EmpCode
			WHERE DocNo =  '$docno'
			$WHERE_B_ID";

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){

		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'SterileRoundNumber'	=>$Result["SterileRoundNumber"],
					'SterileProgramID'	=>$Result["SterileProgramID"],
					'TestProgramID'	=>$Result["TestProgramID"],
					'SterileName'	=>$Result["SterileName2"],
					'SterileTypeID'	=>$Result["SterileTypeID"],
					'user'	=>$Result["FirstName"]." ".$Result["LastName"]
				)
			);

		$i++;
	}


	if($i == 0) {
		array_push(
			$resArray,
			array(	'result'=>"E"
				)
			);
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
