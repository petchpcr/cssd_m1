<?php
// EDIT LOG
// 22-01-2026 11.04 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
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

	$mac_id = $_POST['mac_id'];

	$Sql = "SELECT 	sterilemachine.ID,
					sterilemachine.MachineName2,
					sterilemachine.IsActive,
					sterilemachine.IsBrokenMachine,
					sterilemachine.DocNo,
					DATEDIFF(SECOND, GETDATE(), FinishTime) AS FinishTime
	

			FROM 	sterilemachine
			WHERE 	sterilemachine.IsCancel = 0
			$WHERE_B_ID 

			";


	if($mac_id!="null" && $mac_id!="Empty"){
		$Sql = $Sql."AND sterilemachine.ID = '$mac_id' ";
	}

	$Sql = $Sql."ORDER BY sterilemachine.ID DESC ";

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$xID 			= $Result["ID"];
		$xMachineName2 	= $Result["MachineName2"];
		$IsActive 	= $Result["IsActive"];
		$IsBrokenMachine 	= $Result["IsBrokenMachine"];
		$DocNo 	= $Result["DocNo"];
		$FinishTime 	= $Result["FinishTime"];

		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'xID'			=>$xID,
					'xMachineName2'	=>$xMachineName2,
					'IsActive'		=>$IsActive,
					'IsBrokenMachine'		=>$IsBrokenMachine,
					'DocNo'			=>$DocNo,
					'FinishTime'	=>$FinishTime
				)
			);

		$i++;
	}


	if($i == 0) {
		array_push(
			$resArray,
			array(	'xID'			=>'',
					'IsActive'	=>'',
					'xMachineName2'	=>'',
					'IsBrokenMachine'		=>'',
					'DocNo'		=> ''
				)
			);
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
