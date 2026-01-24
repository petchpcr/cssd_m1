<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST['B_ID'];

	$SR_IsUsedSterileType = 0;
	$Sql = "SELECT SR_IsUsedSterileType FROM configuration WHERE B_ID = $B_ID ";
	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$SR_IsUsedSterileType = $Result["SR_IsUsedSterileType"];
	}

	$S_Sterile_Type_Or_Process_ID = $_POST['S_Sterile_Type_Or_Process_ID'];
	if($SR_IsUsedSterileType) {
		$Sql = " SELECT   steriletype_testprogram.TestProgramID AS TestProgramID,
		testprogram.TestProgramName AS Label
		FROM   steriletype_testprogram
		LEFT JOIN  testprogram ON   testprogram.ID = steriletype_testprogram.TestProgramID
		WHERE   steriletype_testprogram.SterileTypeID = $S_Sterile_Type_Or_Process_ID 
		AND   testprogram.IsCancel = 0 
		AND   steriletype_testprogram.B_ID = $B_ID	
		AND   testprogram.B_ID = $B_ID
		ORDER BY  testprogram.TestProgramName";
	}else{
		$Sql = " SELECT   steriletestprogram.TestProgramID AS TestProgramID
		testprogram.TestProgramName AS Label
		
		FROM   steriletestprogram
		LEFT JOIN  testprogram ON   testprogram.ID = steriletestprogram.TestProgramID
		WHERE   steriletestprogram.SterileProcessID = $S_Sterile_Type_Or_Process_ID 
		AND   testprogram.IsCancel = 0 
		AND   steriletestprogram.B_ID = $B_ID	
		AND   testprogram.B_ID = $B_ID
		ORDER BY  testprogram.TestProgramName";
	}




	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){

		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'TestProgramID'	=>$Result["TestProgramID"],
					'Label'	=>$Result["Label"]
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
