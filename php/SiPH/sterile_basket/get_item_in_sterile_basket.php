<?php
// EDIT LOG
// 22-01-2026 11.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

if($_SERVER['REQUEST_METHOD']=='POST'){

	$B_ID = $_POST['B_ID'];

	$SR_IsCheckItemInMachine = 0;
	$SS_IsMatchBasketAndType = 0;
	$Sql = "SELECT SR_IsCheckItemInMachine,SS_IsMatchBasketAndType FROM configuration WHERE B_ID = $B_ID ";
	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
		$SR_IsCheckItemInMachine = $Result["SR_IsCheckItemInMachine"];
		$SS_IsMatchBasketAndType = $Result["SS_IsMatchBasketAndType"];
	}

	if($SR_IsCheckItemInMachine==0){
		$mac_id = 0;
	}else{
		$mac_id = $_POST['mac_id'];
	}

	$p_DB = $_POST['p_DB'];

	$typeID = $_POST['typeID'];

	$basket_id = $_POST['basket_id'];

	$DocNo = $_POST['DocNo'];

	if($basket_id == "-"){
		$Sql = "SELECT 		'-' AS ID,
					st_detail.ItemStockID,
					item.itemname,
					itemstock.UsageCode,
					ImportID AS WashDetailID,
					st_detail.ID AS SterileDetailID,
					'-' AS TypeID,
					steriletype_item.SterileTypeID,
					sterilemachine_item.SterileMachineID
					FROM 	(SELECT * FROM steriledetail WHERE steriledetail.DocNo = '$DocNo' )	st_detail 
					INNER JOIN itemstock ON st_detail.ItemStockID = itemstock.RowID 
					INNER JOIN item ON itemstock.ItemCode = item.itemcode
					LEFT JOIN sterilemachine_item	ON item.itemcode = sterilemachine_item.ItemCode AND sterilemachine_item.SterileMachineID = '$mac_id'
					LEFT JOIN steriletype_item ON item.itemcode = steriletype_item.ItemCode AND steriletype_item.SterileTypeID = '$typeID'
					
				WHERE  itemstock.IsStatus NOT IN (3,4,8) 
				AND itemstock.B_ID = $B_ID
				AND item.B_ID = $B_ID
				AND st_detail.ItemStockID NOT IN 
					(SELECT
						ItemStockID
					FROM
						itemstockdetailbasket
						INNER JOIN itemstock ON itemstockdetailbasket.ItemStockID = itemstock.RowID 
					WHERE
						itemstock.IsStatus NOT IN (3,4,8) AND itemstockdetailbasket.IsActive = 1 
						AND itemstockdetailbasket.B_ID = $B_ID
						AND itemstock.B_ID = $B_ID
					) ORDER BY st_detail.ID ASC";

	}else{
		$Sql = "SELECT
				ibk.ID,
				ItemStockID,
				item.itemname,
				itemstock.UsageCode,
				WashDetailID,
				SterileDetailID ,
				TypeID,
				steriletype_item.SterileTypeID,
				sterilemachine_item.SterileMachineID
			FROM
				(SELECT * FROM itemstockdetailbasket WHERE BasketID = $basket_id AND itemstockdetailbasket.IsActive = 1) ibk
				INNER JOIN itemstock ON ibk.ItemStockID = itemstock.RowID 
				INNER JOIN item ON itemstock.ItemCode = item.itemcode
				INNER JOIN basket ON basket.ID = ibk.BasketID
				LEFT JOIN sterilemachine_item	ON item.itemcode = sterilemachine_item.ItemCode AND sterilemachine_item.SterileMachineID = '$mac_id'
				LEFT JOIN steriletype_item ON item.itemcode = steriletype_item.ItemCode AND steriletype_item.SterileTypeID = '$typeID'
			WHERE
				itemstock.IsStatus NOT IN (3,4,8)
				AND ibk.B_ID = $B_ID
				AND itemstock.B_ID = $B_ID
				AND item.B_ID = $B_ID
			ORDER BY ibk.ID ASC";

	}

	
	$meQuery = $conn->prepare($Sql);
	$meQuery->execute();
	 
	$i=0;
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){

		if($SR_IsCheckItemInMachine==0){
			$SterileMachineID = 0;
		}else{
			$SterileMachineID = $Result["SterileMachineID"];
		}
		
		if($SS_IsMatchBasketAndType==1){
			$TypeID = $Result["TypeID"];
		}else{
			$TypeID = $Result["SterileTypeID"];
		}

		array_push(
			$resArray,
			array(	
					'result'=>"A",
					'xID'			=>$Result["ID"],
					'ItemStockID'	=>$Result["ItemStockID"],
					'itemname'	=>$Result["itemname"],
					'UsageCode'			=>$Result["UsageCode"],
					'WashDetailID'	=>$Result["WashDetailID"],
					'SterileDetailID'	=>$Result["SterileDetailID"],
					'SterileProgramID'	=>$TypeID,
					'SterileMachineID'	=>$SterileMachineID
					
				)
			);

		$i++;
	}


	if($i == 0) {
		array_push(
			$resArray,
			array(	'result'=>"E",
					'Sql'=>$Sql
				)
			);
	}

	echo json_encode(array("result"=>$resArray));
	unset($conn);
	die;
}

?>
