<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST['B_ID'];

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
			AND sterile.B_ID = $B_ID
			AND sterileprogram.B_ID = $B_ID
			AND steriletype_process.B_ID = $B_ID
			AND users.B_ID = $B_ID
			AND employee.B_ID = $B_ID";

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
