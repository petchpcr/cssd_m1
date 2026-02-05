<?php
require 'connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST['B_ID'];
	$xSel = $_POST['xSel'];
	if($xSel=="0"){
		$Sql = "SELECT		washmachine.ID,
							washmachine.MachineName

				FROM		washmachine

				WHERE 		washmachine.IsCancel = 0
				AND			washmachine.B_ID = $B_ID

				ORDER BY 	washmachine.ID";
	}else{
		$Sql = "SELECT		sterilemachine.ID,
							sterilemachine.MachineName2 AS MachineName

				FROM		sterilemachine

				WHERE 		sterilemachine.IsCancel = 0
				AND			sterilemachine.B_ID = $B_ID

				ORDER BY 	sterilemachine.ID";
	}



	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
		
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$xID	 		= $Result["ID"];
		$xName 	= $Result["MachineName"];

		array_push(
			$resArray,
			array(	'xId'			=>$xID,
					'MachineName'	=>$xName,
				)
			);

		$i++;
	}


	echo json_encode(array("result"=>$resArray));

	unset($conn);
	die;

}

?>
