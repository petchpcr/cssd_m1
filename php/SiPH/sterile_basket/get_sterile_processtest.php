<?php
// EDIT LOG
// 22-01-2026 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST' && $_POST['pro_id']!=null){

	$p_DB = $_POST['p_DB'];

	$mac_id = $_POST['mac_id'];
	$pro_id = $_POST['pro_id']??"";

	$B_ID = $_POST['B_ID'];

	$p_DB = $_POST['p_DB'];
	if($p_DB == 0){
		$top = " ";
		$limit = " LIMIT 1";
		$date = " NOW() ";
		$dateonly = " DATE( ";
		$ifnull = "IFNULL";
  	}else if($p_DB == 1){
		$top = "TOP 1 ";
		$limit = " ";
		$date = " GETDATE() ";
		$dateonly = " CONVERT(DATE, ";
		$ifnull = "ISNULL";
  	}

	$SR_IsUsedSterileType = 0;
	$SR_IsUsedMachinePairProcess = 0;
	$Sql_con = "SELECT SR_IsUsedSterileType,SR_IsUsedMachinePairProcess FROM configuration WHERE B_ID = $B_ID";
	$meQuery = $conn->prepare($Sql_con);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		
		$SR_IsUsedSterileType = $Result["SR_IsUsedSterileType"];
		$SR_IsUsedMachinePairProcess = $Result["SR_IsUsedMachinePairProcess"];
	}

	if($SR_IsUsedSterileType==1){



		$Sql = "SELECT $top
					sterileprocess.ID,
					-- sterileprogram.SterileName ,
					CONCAT(sterileprogram.SterileName, ' (',steriletype.SterileProcessTypeName,')') AS SterileName,
					steriletype_process.SterileTypeID,
					steriletype_process.SterileProcessID
				FROM
					steriletype_machine
					LEFT JOIN sterilemachine ON sterilemachine.ID = steriletype_machine.SterileMachineID
					LEFT JOIN steriletype ON steriletype.ID = steriletype_machine.SterileTypeID
					LEFT JOIN steriletype_process ON steriletype_process.SterileTypeID = steriletype.ID
					LEFT JOIN sterileprocess ON sterileprocess.ID = steriletype_process.SterileProcessID
					LEFT JOIN sterileprogram ON sterileprogram.ID = sterileprocess.SterileProgramID 
				WHERE
					sterilemachine.IsCancel = 0 
					AND sterileprogram.IsCancel = 0 
					AND sterileprocess.IsCancel = 0 
					AND steriletype.IsProcessTest = 1
					AND steriletype_machine.SterileMachineID= $mac_id
					AND sterileprocess.SterileProgramID = $pro_id 
					AND steriletype_machine.B_ID = $B_ID
					AND sterilemachine.B_ID = $B_ID
					AND steriletype.B_ID = $B_ID
					AND steriletype_process.B_ID = $B_ID
					AND sterileprocess.B_ID = $B_ID
					AND sterileprogram.B_ID = $B_ID
					$limit";
		// AND sterileprocess.ProcessTest = 0
		// AND steriletype.IsProcessTest = 0";
	}else if($SR_IsUsedMachinePairProcess==1){
		$Sql = "SELECT  $top
					sterilemachineprogram.SterileMachineID,
					sterilemachineprogram.SterileProgramID,
					steriletype_process.SterileTypeID
				FROM
					sterilemachineprogram
					INNER JOIN sterileprocess ON sterileprocess.SterileProgramID = sterilemachineprogram.SterileProgramID
					INNER JOIN steriletype_process ON sterileprocess.ID = steriletype_process.SterileProcessID
				WHERE
					sterilemachineprogram.SterileMachineID = $mac_id
					AND sterilemachineprogram.SterileProgramID = $pro_id 
					AND sterileprocess.ProcessTest = 1
					AND sterileprocess.IsCancel = 0 
					AND sterilemachineprogram.B_ID = $B_ID
					AND sterileprocess.B_ID = $B_ID
					AND steriletype_process.B_ID = $B_ID
					$limit";
					// AND sterileprocess.ProcessTest = 0";
	}else{
		$Sql = "SELECT  $top
					sterilemachinedetail.SterileMachineID,
					sterilemachinedetail.SterileProcessID,
					steriletype_process.SterileTypeID
				FROM
					sterilemachinedetail 
					INNER JOIN sterileprocess ON sterilemachinedetail.SterileProcessID = sterileprocess.ID
					INNER JOIN steriletype_process ON sterilemachinedetail.SterileProcessID = steriletype_process.SterileProcessID
				WHERE
					SterileMachineID = $mac_id
					AND sterileprocess.IsCancel = 0 
					AND sterileprocess.SterileProgramID = $pro_id 
					AND sterileprocess.ProcessTest = 1
					AND sterilemachinedetail.B_ID = $B_ID
					AND sterileprocess.B_ID = $B_ID
					$limit";
					// AND sterileprocess.ProcessTest = 0";
	}


	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$xID 			= $Result["ID"]??"";
		$mac_id 	= $Result["SterileMachineID"]??"";
		$process_id 	= $Result["SterileProcessID"]??"";
		$type_id 	= $Result["SterileTypeID"];
		$i++;
	}


	if($i == 0) {
		array_push(
			$resArray,
			array(	'result'=>"E",
					'xID' =>'',
					'mac_id'	=>'',
					'process_id'	=>'',
					'$Sql '	=>$Sql 
				)
			);
	}else{
		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'xID'		=>$xID,
					'mac_id'	=>$mac_id,
					'process_id'	=>$process_id,
					'type_id'	=>$type_id
				)
			);
	}


}else{
	array_push(
		$resArray,
		array(	'result'=>"E",
				'xID' =>'',
				'mac_id'	=>'',
				'process_id'	=>'',
				'$Sql '	=>"" 
			)
		);
}
echo json_encode(array("result"=>$resArray));
unset($conn);
die;
?>
