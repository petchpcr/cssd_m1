<?php
// EDIT LOG
// 22-01-2026 10.50 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';
$resArray = array();

function getSql()
{
	$B_ID = $_POST["B_ID"];
	$sql = 			 	"SELECT 		washdetail.ID, 
										washdetail.ItemStockID, 
										washdetail.WashDocNo, 
										COALESCE( (packingmat.Shelflife), 0) AS Shelflife, 
										washdetail.Qty, 
										washdetail.PackingMatID 

							FROM 		washdetail 

							LEFT JOIN 	wash
							ON			wash.DocNo = washdetail.WashDocNo

							LEFT JOIN 	itemstock
							ON			itemstock.RowID = washdetail.ItemStockID

							LEFT JOIN 	packingmat
							ON			packingmat.ID = washdetail.PackingMatID 
							
							WHERE 		itemstock.IsStatus = 1 
							AND			itemstock.IsCancel = 0 
							AND	 		washdetail.B_ID = $B_ID 
							AND 		wash.B_ID = $B_ID
							AND 		itemstock.B_ID = $B_ID
							";

	// Status
	if (isset($_POST["p_is_status"])) {

		$p_is_status = $_POST['p_is_status'];

		$sql = 	$sql . "AND 	washdetail.IsStatus = $p_is_status ";

		if($p_is_status == "1"){

			$sql = 	$sql . "AND  wash.IsStatus = 1  ";
		
		}

	} else {
		$sql = 	$sql . 	"AND 	washdetail.IsStatus = 1 ";
		$sql = 	$sql .	"AND	wash.IsStatus = 1 ";
	}

	// Packing ID
	if (isset($_POST["p_PackingMatID"])) {

		$p_PackingMatID = $_POST['p_PackingMatID'];

		$sql = 	$sql . "AND 	washdetail.PackingMatID = $p_PackingMatID ";
	}

	// List ID
	if (isset($_POST["p_list_id"])) {

		$p_list_id = $_POST['p_list_id'];

		$sql = 	$sql . "AND 	washdetail.ID IN (" . substr($p_list_id, 0, -1) . ")";
	}

	return $sql;
}

function getSqlDocNo()
{
	$B_ID = $_POST["B_ID"];
	$sql = 	"SELECT		washdetail.WashDocNo
										
			FROM 		washdetail 

			LEFT JOIN 	wash
			ON			wash.DocNo = washdetail.WashDocNo

			LEFT JOIN 	itemstock
			ON			itemstock.RowID = washdetail.ItemStockID

			LEFT JOIN 	packingmat
			ON			packingmat.ID = washdetail.PackingMatID 
			
			WHERE 		itemstock.IsStatus = 1 
			AND			itemstock.IsCancel = 0
			AND	 		washdetail.B_ID = $B_ID 
			AND 		wash.B_ID = $B_ID
			AND 		itemstock.B_ID = $B_ID";

	if (isset($_POST["p_is_status"])) {

		$p_is_status = $_POST['p_is_status'];

		$sql = 	$sql . "AND 	washdetail.IsStatus = $p_is_status ";

		if($p_is_status == "1"){

			$sql = 	$sql . "AND  wash.IsStatus = 1  ";
		
		}

	} else {
		$sql = 	$sql . 	"AND 	washdetail.IsStatus = 1 ";
		$sql = 	$sql .	"AND	wash.IsStatus = 1 ";
	}

	return $sql;
}

function getSqlCondition()
{
	$sql = "";
	// Branch ID
	if ( isset($_POST["B_ID"]) ) {	

		$B_ID = $_POST["B_ID"];
				
		$sql = $sql."AND	 washdetail.B_ID = $B_ID ";
	}

	// Packing ID
	if (isset($_POST["p_PackingMatID"])) {

		$p_PackingMatID = $_POST['p_PackingMatID'];

		$sql = 	$sql . "AND 	washdetail.PackingMatID = $p_PackingMatID ";
	}

	// List ID
	if (isset($_POST["p_list_id"])) {

		$p_list_id = $_POST['p_list_id'];

		$sql = 	$sql . "AND 	washdetail.ID IN (" . substr($p_list_id, 0, -1) . ")";
	}

	return $sql;

}

function getSterileData($conn, $p_docno)
{
	$B_ID = $_POST["B_ID"];
	$p_DB = $_POST['p_DB'];

	if($p_DB == 0){
		$top = " ";
		$limit = " LIMIT 1";
		$date = " NOW() ";
		$DocDate = "DocDate";
  	}else if($p_DB == 1){
		$top = "TOP 1 ";
		$limit = " ";
		$date = " GETDATE() ";
		$DocDate = "FORMAT (DocDate, 'yyyy-MM-dd HH:mm:ss') AS DocDate";
  	}

	$sql_doc_date = "SELECT 	$top $DocDate,
								PrepareCode,
								ApproveCode,
								SterileCode,
								PackingCode
	 
					FROM 		sterile 

					WHERE		DocNo = '$p_docno' 
					AND         B_ID = $B_ID

					$limit ";

	$res = $conn->prepare($sql_doc_date);
	$res->execute();	

	if ($row = $res->fetch(PDO::FETCH_ASSOC)) {
		$d_doc_date = $row["DocDate"] . ",". $row["PrepareCode"] . ",". $row["ApproveCode"] . ",". $row["SterileCode"] . ",". $row["PackingCode"] ;
	} else {
		$d_doc_date = "$date,0,0,0,0,888";
	}

	return $d_doc_date;
}

// =======================================================================================
// -- MAIN
// =======================================================================================

if (isset($_POST["p_list_id"]) && isset($_POST["p_docno"])) {

	if($p_DB == 0){
		$top = " ";
		$limit = " LIMIT 1";
		$date = " NOW() ";
		$DocDate = "DocDate";
  	}else if($p_DB == 1){
		$top = "TOP 1 ";
		$limit = " ";
		$date = " GETDATE() ";
		$DocDate = "FORMAT (DocDate, 'yyyy-MM-dd HH:mm:ss') AS DocDate";
  	}

	$d_doc_date = "$date";
	$p_prepare_code = "0";
	$p_approve_code = "0";
	$p_sterile_code = "0";
	$p_packing_code = "0";

	$p_list_id = $_POST["p_list_id"];
	$p_docno = $_POST['p_docno'];

	$B_ID = $_POST["B_ID"];
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

	$p_bid = "1";

	if ( isset($_POST["p_bid"]) ) {	
		$p_bid = $_POST["p_bid"];
	}

	$sql_select_wash_item = getSql();
	$sql_condition = getSqlCondition();
	
	// =======================================================================================
	// Insert steriledetail	
	// =======================================================================================

	// get docdate
	$d_sterile_data = getSterileData($conn, $p_docno);

	$d_array_data = explode(",", $d_sterile_data);

	$d_doc_date = $d_array_data[0];
	$p_prepare_code = $d_array_data[1];
	$p_approve_code = $d_array_data[2];
	$p_sterile_code = $d_array_data[3];
	$p_packing_code = $d_array_data[4];

	$p_packing = "(SELECT sterile.PackingCode FROM sterile WHERE sterile.DocNo = '$p_docno' AND sterile.B_ID = $B_ID $limit)";
	$p_sterile = "(SELECT sterile.SterileCode FROM sterile WHERE sterile.DocNo = '$p_docno' AND sterile.B_ID = $B_ID $limit)";
	$p_approve = "(SELECT sterile.ApproveCode FROM sterile WHERE sterile.DocNo = '$p_docno' AND sterile.B_ID = $B_ID $limit)";
	$p_prepare = "(SELECT sterile.PrepareCode FROM sterile WHERE sterile.DocNo = '$p_docno' AND sterile.B_ID = $B_ID $limit)";

	if ( isset($_POST["p_PrepareCode"]) ) {	
		$p_prepare_code = $_POST["p_PrepareCode"];

		if($p_prepare_code == "0"){
			$p_prepare_code = $p_prepare;
		}
	}else{
		$p_prepare_code = $p_prepare;
	}

	if ( isset($_POST["p_ApproveCode"]) ) {	
		$p_approve_code = $_POST["p_ApproveCode"];

		if($p_approve_code == "0"){
			$p_approve_code = $p_approve;
		}
	}else{
		$p_approve_code = $p_approve;
	}

	if ( isset($_POST["p_SterileCode"]) ) {	
		$p_sterile_code = $_POST["p_SterileCode"];

		if($p_SterileCode == "0"){
			$p_SterileCode = $p_sterile;
		}
	}else{
		$p_SterileCode = $p_sterile;
	}

	if ( isset($_POST["p_PackingCode"]) ) {	
		$p_packing_code = $_POST["p_PackingCode"];

		if($p_packing == "0"){
			$p_packing_code = $p_packing;
		}
	}else{
		$p_packing_code = $p_packing;
	}

	 	

	if($p_DB == 0){

		$sql_insert = "	INSERT INTO 	steriledetail (
							DocNo, 
							ItemStockID, 
							Qty, 
							ImportID, 
							IsStatus, 
							SterileDate, 
							PrepareCode,
							ApproveCode,
							SterileCode,
							PackingCode,
							ExpireDate, 
							PackingMatID,
							B_ID
				)
				SELECT
							'$p_docno', 
							s.ItemStockID, 
							s.Qty,
							s.ID, 
							0,
							'$d_doc_date', 
							$p_prepare_code,
							$p_approve_code,
							$p_sterile_code,
							$p_packing_code,
							('$d_doc_date' + INTERVAL s.Shelflife DAY + INTERVAL -1 DAY ),
							s.PackingMatID,
							$B_ID
				FROM  	(
						$sql_select_wash_item
					) AS s ";

	}else if($p_DB == 1){

		$sql_insert = "	INSERT INTO 	steriledetail (
							DocNo, 
							ItemStockID, 
							Qty, 
							ImportID, 
							IsStatus, 
							SterileDate, 
							PrepareCode,
							ApproveCode,
							SterileCode,
							PackingCode,
							ExpireDate, 
							PackingMatID,
							B_ID
				)
				SELECT
							'$p_docno', 
							ItemStockID, 
							Qty,
							ID, 
							0,
							'$d_doc_date', 
							$p_prepare_code,
							$p_approve_code,
							$p_sterile_code,
							$p_packing_code,
						    DATEADD(DAY, s.Shelflife - 1 , CONVERT(DATE, '$d_doc_date')), 
							PackingMatID,
							$B_ID 
				FROM  	(
						$sql_select_wash_item
					) S";

	}

	// echo $sql_insert;
	$res_insert = $conn->prepare($sql_insert);
	$res_insert->execute();	

	// =======================================================================================
	// Create Item Stock Log
	// =======================================================================================	

	if($p_DB == 0){
		$sql_insert_stock_log = "	INSERT INTO itemstocklog ( 
											ItemStockID, 
											CreateDate, 
											SterileDetailID,
											B_ID
										) 
									SELECT 		s.ItemStockID, 
									$date, 
											(SELECT ID FROM steriledetail WHERE ItemStockID = s.ItemStockID AND B_ID = $B_ID ORDER BY ID DESC LIMIT 1),
											$B_ID
									FROM  	(
											$sql_select_wash_item
										) AS s ";
	}else{
		$sql_insert_stock_log = "	INSERT INTO itemstocklog ( 
											ItemStockID, 
											CreateDate, 
											SterileDetailID,
											B_ID
										) 
									SELECT 		s.ItemStockID, 
									$date, 
											(SELECT TOP 1 ID FROM steriledetail WHERE ItemStockID = s.ItemStockID AND B_ID = $B_ID ORDER BY ID DESC),
											$B_ID
									FROM  	(
											$sql_select_wash_item
										) s ";
	}

	$res_insert = $conn->prepare($sql_insert_stock_log);
	$res_insert->execute();	
	// =======================================================================================
	// Update Item Stock
	// =======================================================================================

	// $max = sizeof($aItemStockID);

	if($p_DB == 0){
		$sql_update_item_stock =
									"UPDATE		itemstock 

									LEFT JOIN 	washdetail
									ON			washdetail.ItemStockID = itemstock.RowID

									LEFT JOIN 	packingmat
									ON			packingmat.ID = washdetail.PackingMatID
									
									LEFT JOIN 	wash
									ON			wash.DocNo = washdetail.WashDocNo
									
									SET 		itemstock.IsStatus = 2 , 
												itemstock.IsNew = 0 , 
												itemstock.IsPay = 0 , 
												itemstock.UsageCount = (UsageCount + 1),
												itemstock.PackDate = $d_doc_date,
												ExpireDate = ( $d_doc_date + INTERVAL packingmat.Shelflife DAY + INTERVAL -1 DAY ) ,
												itemstock.LastSterileDetailID = (SELECT steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID AND steriledetail.B_ID = $B_ID ORDER BY ID DESC LIMIT 1) 
									
									WHERE 		washdetail.IsStatus = 1 
									AND 		itemstock.IsStatus = 1 
									AND 		itemstock.IsCancel = 0  
									AND			wash.IsStatus = 1
									AND 		itemstock.B_ID = $B_ID";
	}else{
		
		$sql_update_item_stock =
								"	UPDATE		itemstock 
									
									SET 		itemstock.IsStatus = 2 , 
												itemstock.IsNew = 0 , 
												itemstock.IsPay = 0 , 
												itemstock.UsageCount = (UsageCount + 1),
												itemstock.PackDate = '$d_doc_date',
												ExpireDate = DATEADD(DAY, packingmat.Shelflife - 1 , '$d_doc_date'), 
												itemstock.LastSterileDetailID = (SELECT TOP 1 steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID AND steriledetail.B_ID = $B_ID ORDER BY ID DESC) 
									
									FROM 		itemstock

									LEFT JOIN 	washdetail
									ON			washdetail.ItemStockID = itemstock.RowID

									LEFT JOIN 	packingmat
									ON			packingmat.ID = washdetail.PackingMatID
									
									LEFT JOIN 	wash
									ON			wash.DocNo = washdetail.WashDocNo

									WHERE 		washdetail.IsStatus = 1 
									AND 		itemstock.IsStatus = 1 
									AND 		itemstock.IsCancel = 0  
									AND			wash.IsStatus = 1 
									AND 		itemstock.B_ID = $B_ID";
	}
   
	$res_insert = $conn->prepare(($sql_update_item_stock.$sql_condition));
	$res_insert->execute();	

	// =======================================================================================
	// Update washdetail
	// =======================================================================================
    $sql_update_wash_detail = 	
								"UPDATE		washdetail 

								SET 		washdetail.IsStatus = 2 

								WHERE 		washdetail.IsStatus = 1 ";

	$res_insert = $conn->prepare(($sql_update_wash_detail.$sql_condition));
	$res_insert->execute();	

	// =======================================================================================
	// Update wash
	// =======================================================================================
	
	$sql_wash_docno = getSqlDocNo();

	$res = $conn->prepare($sql_wash_docno.$sql_condition);
	$res->execute();	
	while ($row = $res->fetch(PDO::FETCH_ASSOC)) {
			
			$d_WashDocNo = $row["WashDocNo"];

			$sql_update_doc = 		"UPDATE 		wash 
									SET 			wash.IsStatus = 2 
									WHERE 			wash.DocNo = '$d_WashDocNo' 
									AND 			(SELECT COUNT(*) FROM washdetail WHERE washdetail.WashDocNo = wash.`DocNo` AND washdetail.IsStatus < 2 AND washdetail.B_ID = $B_ID) = 0 ";

			$res = $conn->prepare($sql_update_doc);
			$res->execute();	
	}

	// =======================================================================================
	// Update Print Count
	// =======================================================================================
				
	// $Sql = "UPDATE 		steriledetail 	
	// 		SET 		PrintCount = '1' 

	// 		WHERE 		steriledetail.DocNo = '$p_docno'

	// 		AND 		steriledetail.PackingMatID IN('10','11','12')";

	// $res = $conn->prepare($Sql);
	// $res->execute();		
	

	if (isset($_POST["is_new"])){	
		$p_list_id = $_POST['p_list_id'];

		$w_id = "(" . substr($p_list_id, 0, -1) . ")";
		$is_new = $_POST["is_new"];
		$BasketID = $_POST["BasketID"];
		$SterileMachineID = "";
	
		$SqlSterileMachineID = "SELECT 	steriledetail.ItemStockID,
						steriledetail.ImportID,
						steriledetail.ID,
						SterileMachineID 
				FROM 	steriledetail,sterile 
				WHERE 	steriledetail.ImportID IN $w_id 
				AND 	sterile.DocNo = steriledetail.DocNo
				AND		sterile.DocNo = '$p_docno'
				AND     steriledetail.B_ID = '$B_ID'
				AND     sterile.B_ID = '$B_ID'";
	
		$res = $conn->prepare($SqlSterileMachineID);
		$res->execute();	
		while ($row = $res->fetch(PDO::FETCH_ASSOC)) {
	
			$ItemStockID = $row["ItemStockID"];
			$ImportID = $row["ImportID"];
			$ID = $row["ID"];
			$SterileMachineID = $row["SterileMachineID"];
	
			if(!$is_new){
				$sql_itemstockdetailbasket = " INSERT INTO itemstockdetailbasket (BasketID, ItemStockID, SSDetailID, WashDetailID, SterileDetailID, PairTime,IsActive, B_ID) 
												VALUES('$BasketID', '$ItemStockID', '', '$ImportID', '$ID', '$date', 1, $B_ID) ";
			}else{
				$sql_itemstockdetailbasket = "UPDATE 	itemstockdetailbasket 
												SET 	SterileDetailID = $ID 
												WHERE 	BasketID = '$BasketID' 
												AND 	WashDetailID = '$ImportID'
												AND		IsActive = 1";
			}
				
			$result = $conn->prepare($sql_itemstockdetailbasket);
			$result->execute();
		
		}
	
		$sql_basket = "	UPDATE 	basket 
			SET 	InMachineID = $SterileMachineID ,RefDocNo = '$p_docno'
			WHERE 	ID = '$BasketID'";
		$result = $conn->prepare($sql_basket);
		$result->execute();
	}

	array_push(
		$resArray,
		array(
			'result' => "A",
			'sql_select_wash_item' => '',
			'sql_insert' => $sql_insert,
			'sql_update_wash_detail' => $sql_update_wash_detail.$sql_condition,
			'sql_update_doc' => '',
			'sql_update_item_stock' => $sql_update_item_stock.$sql_condition,
			'sql_insert_stock_log' =>'',
			'SqlSterileMachineID' => $SqlSterileMachineID,
			'sql_basket' => $sql_basket
		)
	);


} else {
	array_push(
		$resArray,
		array(
			'result' => "I",
			'Message' => 'ข้อมูลที่ส่งมาไม่ถูกต้อง!!'
		)
	);
}

echo json_encode(array("result" => $resArray));
unset($conn);
die;
?>