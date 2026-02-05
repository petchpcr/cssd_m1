<?php
// EDIT LOG
// 22-01-2026 11.44 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query	
// 13-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST['B_ID'];
    $WHERE_B_ID = "";
    $INSERT_B_ID = "";
    $VALUES_B_ID = "";
 	$WHERE_itsk_B_ID = "";
	$WHERE_it_basket_B_ID = "";
	$WHERE_st_d_B_ID = "";
    if($B_ID != "0"){
        $WHERE_B_ID = " WHERE B_ID = $B_ID";
        $INSERT_B_ID = ", B_ID";
        $VALUES_B_ID = ", $B_ID";
        $WHERE_itsk_B_ID = " AND itemstock.B_ID = $B_ID";
        $WHERE_it_basket_B_ID = " AND itemstockdetailbasket.B_ID = $B_ID";
		$WHERE_st_d_B_ID = " AND steriledetail.B_ID = $B_ID";
    }
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

  	$SR_IsCheckItemInMachine = 0;
	$SS_IsMatchBasketAndType = 0;
	$Sql = "SELECT SR_IsCheckItemInMachine,SS_IsMatchBasketAndType FROM configuration $WHERE_B_ID ";
	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$SR_IsCheckItemInMachine = $Result["SR_IsCheckItemInMachine"];
		$SS_IsMatchBasketAndType = $Result["SS_IsMatchBasketAndType"];
	}


	$basket_id = $_POST['basket_id'];
	$usagecode = $_POST['usage_code'];

	$program_id = "-1";
	if (isset($_POST["program_id"])){
		$program_id = $_POST['program_id'];
	}

	$mac_id = "-1";
	if (isset($_POST["mac_id"])){
		if($SR_IsCheckItemInMachine==1){
			$mac_id = $_POST['mac_id'];
		}
	}

	$Sql = "SELECT
			$top
				itemstock.RowID ,
				washdetail.ID,
				steriletype_item.SterileTypeID,
				sterilemachine_item.SterileMachineID
			FROM
				itemstock
				LEFT JOIN washdetail ON washdetail.ItemStockID = itemstock.RowID 
				LEFT JOIN 	item 		ON	item.itemcode = itemstock.ItemCode  
				LEFT JOIN  steriletype_item ON item.itemcode = steriletype_item.ItemCode AND steriletype_item.SterileTypeID = $program_id
				LEFT JOIN sterilemachine_item	ON item.itemcode = sterilemachine_item.ItemCode AND sterilemachine_item.SterileMachineID = '$mac_id'
			WHERE
				itemstock.UsageCode = '$usagecode'
				AND washdetail.IsStatus = 1
				AND itemstock.IsStatus = 1
				AND itemstock.IsCancel = 0
				AND washdetail.PrintCount > 0 
				$WHERE_itsk_B_ID";
	
	
	$Sql = $Sql."ORDER BY washdetail.ModifyDate DESC $limit";

	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$ItemStockID 	= $Result["RowID"];
		$w_id 	= $Result["ID"];
		$SterileTypeID 	= $Result["SterileTypeID"];
		$SterileMachineID 	= $Result["SterileMachineID"];

		if($SS_IsMatchBasketAndType&&$SterileTypeID==null){
			array_push(
				$resArray,
				array(	'result'=>"T",'sql'=>$Sql,)
			);

			echo json_encode(array("result"=>$resArray));
			unset($conn);
			die;
		}

		// if(isset($_POST["program_id"])&&$SterileTypeID==null){
		// 	array_push(
		// 		$resArray,
		// 		array(	'result'=>"T",'sql'=>$Sql,)
		// 	);

		// 	echo json_encode(array("result"=>$resArray));
		// 	unset($conn);
		// 	die;
		// }

		if (isset($_POST["mac_id"])){
			if($SR_IsCheckItemInMachine==1&&$SterileMachineID==null){
				array_push(
					$resArray,
					array(	'result'=>"M",'sql'=>$Sql,)
				);
	
				echo json_encode(array("result"=>$resArray));
				unset($conn);
				die;
			}
		}

		$Sql_check = "SELECT * FROM itemstockdetailbasket WHERE ItemStockID = '$ItemStockID' AND IsActive = 1 $WHERE_it_basket_B_ID";

		$meQuery1 = $conn->prepare($Sql_check);
		$meQuery1->execute();
		
		$j=0;
		while ($Result1 = $meQuery1->fetch(PDO::FETCH_ASSOC)){
			$j++;
			array_push(
				$resArray,
				array(	'result'=>"D",'item_id'=>$Result1['ID'],'basket_id'=>$Result1['BasketID'],'sql'=>$Sql_check,)
			);
		}

		if($j==0){

			if($basket_id != "-"){
				$Sql_in = "	INSERT INTO itemstockdetailbasket (BasketID, ItemStockID, WashDetailID, PairTime,IsActive $INSERT_B_ID) 
							VALUES($basket_id, '$ItemStockID', '$w_id', $date, 1 $VALUES_B_ID) 
							";

				$meQuery2 = $conn->prepare($Sql_in);
				$meQuery2->execute();
			}

			array_push(
				$resArray,
				array(	'result'=>"A",'w_id'=>$w_id,)
			);
		}
		
		$i++;
	}


	if($i == 0) {

		$Sql_check = "	SELECT * 
						FROM itemstockdetailbasket 
						INNER JOIN itemstock ON ItemStockID = itemstock.RowID 
						WHERE itemstock.UsageCode = '$usagecode'
						AND IsActive = 1
						$WHERE_itsk_B_ID";

		$meQuery2 = $conn->prepare($Sql_check);
		$meQuery2->execute();
		
		$j=0;
		if ($Result2 = $meQuery2->fetch(PDO::FETCH_ASSOC)){
			array_push(
				$resArray,
				array(	'result'=>"D",'basket_id'=>$Result2['BasketID'],'sql'=>$Sql_check,)
			);
		}else{
			$SqlSterileMachineID = "SELECT 	steriledetail.DocNo,steriledetail.IsStatus
									FROM 	steriledetail
									INNER JOIN itemstock ON  steriledetail.ItemStockID = itemstock.RowID
									WHERE itemstock.UsageCode = '$usagecode'
									AND steriledetail.IsStatus != 2 
									$WHERE_itsk_B_ID
									$WHERE_st_d_B_ID";

			$res = $conn->prepare($SqlSterileMachineID);
			$res->execute();	
			if ($row = $res->fetch(PDO::FETCH_ASSOC)) {
				array_push(
					$resArray,
					array(	'result'=>"D",'basket_id'=>'---','sDocNo'=>$row['DocNo'],'sql'=>$Sql_check,)
				);
			}else{
				array_push(
					$resArray,array('result'=>"E",'sql'=>$Sql,)
				);
			}
		}
		
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
