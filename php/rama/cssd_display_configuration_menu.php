<?php
//EDIT LOG
//24-01-2026 11.11 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';

$resArray = array();
if ($_SERVER['REQUEST_METHOD'] == 'POST') {

	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST['B_ID'];

	if($p_DB == 0){
		$top = "";
		$limit = "LIMIT 1";
	}else if($p_DB == 1){
		$top = "TOP 1";
		$limit = "";
	}

    $Sql = "SELECT 	$top 

					configuration_menu.*
					
			FROM	configuration_menu 
			WHERE configuration_menu.B_ID = $B_ID";

	if (isset($_POST["p_user_id"])) {

		$p_user_id = $_POST['p_user_id'];

		$Sql = $Sql."AND configuration_menu.UserID = $p_user_id ";
	}

	$Sql = $Sql."ORDER BY configuration_menu.RowID DESC ";

	$Sql = $Sql." $limit ";

	//echo $Sql;

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();

	$i = 0;
	
    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {
		array_push( 
			$resArray,array(
				'result'=>"A",
				'Desktop_SendSterile'=>$row["Desktop_SendSterile"],
				'Desktop_Wash'=>$row["Desktop_Wash"],
				'Desktop_Sterile'=>$row["Desktop_Sterile"],
				'Desktop_ApproveStock'=>$row["Desktop_ApproveStock"],
				'Desktop_Payout'=>$row["Desktop_Payout"],
				'Desktop_Report'=>$row["Desktop_Report"],
				'Desktop_ItemStock'=>$row["Desktop_ItemStock"],
				'Desktop_Recall'=>$row["Desktop_Recall"],
				'Desktop_Setting'=>$row["Desktop_Setting"],
				'Desktop_Occurrence'=>$row["Desktop_Occurrence"],
				'Desktop_ComputeExpireDate'=>$row["Desktop_ComputeExpireDate"],
				'Desktop_Receive_Pay_NonUsage'=>$row["Desktop_Receive_Pay_NonUsage"],
				'Desktop_ReturnToStock'=>$row["Desktop_ReturnToStock"],
				'Desktop_TakeBack'=>$row["Desktop_TakeBack"],
				'Desktop_LabelType'=>$row["Desktop_LabelType"]
			)
		);
		
    }else{
		array_push( 
			$resArray,array(
				'result'=>"E"
			)
		);
	}

    echo json_encode(array("result" => $resArray));

	unset($conn);
	die;
}
?>