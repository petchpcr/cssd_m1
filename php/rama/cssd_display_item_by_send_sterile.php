<?php
//EDIT LOG
//23-01-2026 08.22 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

	$B_ID = $_POST['B_ID'];
	$p_DB = $_POST['p_DB'];

	$d_condition = "";

	$d_join = "";

	$d_order_by = "ORDER BY    item.itemname ";

	if (isset($_POST["Usage_code"])) {

		$Usage_code = $_POST['Usage_code'];

        $d_condition = "AND 		(
						item.itemcode LIKE '%" . str_replace(" ", "%", $Usage_code) . "%' OR 
						item.itemname LIKE '%" . str_replace(" ", "%", $Usage_code) . "%' OR
						item.Alternatename LIKE '%" . str_replace(" ", "%", $Usage_code) . "%'
						) ";
	}else if (isset($_POST["p_qr"])) {

		$p_qr = $_POST['p_qr'];

        $d_condition = "AND 	item.itemcode = '$p_qr' ";
	}


	if (isset($_POST["p_IsUsedItemSet"])) {
        $d_condition = $d_condition . " AND		item.IsSet = 1 ";
	}else{
		$d_condition = $d_condition . " AND		(item.IsSet = 1 OR item.IsSet = 0) ";
	}


	if (isset($_POST["p_switch_washdep"])) {
		if($_POST["p_switch_washdep"]){
			$d_condition = $d_condition . " AND 		item.IsWashDept = 1 ";
		}else{
			$d_condition = $d_condition . " AND item.NoWash IN (0,1) AND 		item.IsWashDept = 0 ";
		}
	}else{
        $d_condition = $d_condition . " AND 		item.IsWashDept = 0 ";
	}


	if (isset($_POST["p_is_reuse"])) {

		$p_is_reuse = $_POST["p_is_reuse"];

		$d_condition = $d_condition . "AND		( item.IsReuse = '$p_is_reuse' OR item.IsWasting = 1 ) ";
		
    }
	
	if (isset($_POST["p_IsUsedItemDepartment"]) || isset($_POST["p_DeptID"])) {

		$p_DeptID = $_POST['p_DeptID'];

        $d_join = "	INNER JOIN 	itemdepartment 
					ON 			itemdepartment.itemcode = item.itemcode ";

		if($p_DeptID <> ""){
			$d_condition = $d_condition . "AND		itemdepartment.DeptID = $p_DeptID ";
		}

		if ( isset($_POST["p_IsSortByUsedCount"]) ) {
			$d_order_by = "ORDER BY    itemdepartment.UsedCount DESC";
		}
		
	}

    if($p_DB == 0){
			$Sql = "SELECT		item.itemcode AS RowID,
								item.itemcode AS ItemCode,
								'' AS PackDate,
								'' AS ExpireDate,
								'' AS DepID,
								'' AS DepName2,
								1,
								0,
								item.itemcode AS UsageCode,
								item.itemname AS Item_name,
								0 AS itemqty,
								packingmat.Shelflife,
								item.IsSet 

					FROM		item 

					INNER JOIN 	packingmat 
					ON 			item.PackingMatID = packingmat.ID 

					$d_join 

					WHERE 		item.IsNormal = 1 
					AND 		( item.IsNonUsage = 0 OR item.IsNonUsage IS NULL )
					AND 		item.B_ID = $B_ID
					$d_condition 

					GROUP BY 	item.itemcode  

					$d_order_by 

					LIMIT 		200 ";
    }else if($p_DB == 1){
			$Sql = "SELECT		TOP 200
								item.itemcode AS RowID,
								item.itemcode AS ItemCode,
								'' AS PackDate,
								'' AS ExpireDate,
								'' AS DepID,
								'' AS DepName2,
								1,
								0,
								item.itemcode AS UsageCode,
								item.itemname AS Item_name,
								0 AS itemqty,
								packingmat.Shelflife,
								item.IsSet 

					FROM		item 

					INNER JOIN 	packingmat 
					ON 			item.PackingMatID = packingmat.ID 

					$d_join 

					WHERE 		item.IsNormal = 1 
					AND 		( item.IsNonUsage = 0 OR item.IsNonUsage IS NULL )
					AND 		item.B_ID = $B_ID
					$d_condition 

					GROUP BY 	item.itemcode  ,
								item.itemname ,
								packingmat.Shelflife,
								item.IsSet,
								itemdepartment.UsedCount

					$d_order_by ";
    }



	//echo $Sql;

		$meQuery = $conn->prepare($Sql);
		$meQuery->execute();
		$i = 0;
	
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $xId = $Result["RowID"];
        $xItem_Code = $Result["ItemCode"];
        $xPackDate = $Result["PackDate"];
        $xExpDate = $Result["ExpireDate"];
        $xDeptID = $Result["DepID"];
        $xDept = $Result["DepName2"];
        $xQty = "1";
        $xStatus = "0";
        $xUsageID = $Result["UsageCode"];
        $xItem_Name = $Result["Item_name"];
        $itemqty = $Result["itemqty"];
        $Shelflife = $Result["Shelflife"];
        $IsSet = $Result["IsSet"];

		array_push(
			$resArray, 
			array('bool' => "true", 
			'ID' => $xId, 
			'Item_Code' => $xItem_Code, 
			'PackDate' => $xPackDate, 
			'ExpDate' => $xExpDate, 
			'DeptID' => $xDeptID, 
			'Dept' => $xDept,
			'Qty' => $xQty, 
			'Status' => $xStatus, 
			'UsageID' => $xUsageID, 
			'Item_Name' => $xItem_Name, 
			'itemqty' => $itemqty, 
			'Shelflife' => $Shelflife, 
			'IsSet' => $IsSet
			)
		);
		
			$i++;
    }

    if ($i == 0) {
        array_push(
			$resArray, array(
				'bool' => "false", 
				'ID' => '', 
				'Item_Code' => '', 
				'PackDate' => '', 
				'ExpDate' => '', 
				'DeptID' => '', 
				'Dept' => '', 
				'Qty' => '', 
				'Status' => '',
				'UsageID' => '', 
				'Item_Name' => '', 
				'itemqty' => '',
				'Sql' => $Sql
			)
		);
    }

    echo json_encode(array("result" => $resArray));

	unset($conn);
	die;

?>