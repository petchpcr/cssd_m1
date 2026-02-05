<?php
// EDIT LOG
// 26-01-2026 10.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query	
// 26-01-2026 10.40 : ยังไม่ได้แก้ไข B_ID ในการ UPDATE
// 13-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';
$resArray = array();

$B_ID = $_POST["B_ID"];
$WHERE_B_ID = "";
if($B_ID != "0"){
	$WHERE_B_ID = " WHERE	B_ID = $B_ID ";
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
// =======================================================================================
// -- MAIN
// =======================================================================================

// check for post data
if (isset($_POST["p_data"])) {

	if (isset($_POST["p_docno"])) {
		$p_docno = $_POST['p_docno'];

		// -------------------------------------------
		// Check Sterile Machine
		// -------------------------------------------

		$sql_check_machine = "SELECT $top ID FROM sterilemachine WHERE DocNo = '$p_docno' AND IsActive = 0 $WHERE_AND_B_ID $limit";

		$result = $conn->prepare($sql_check_machine);
		$result->execute();
	
		if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
			// .. To do
		}else{
			array_push(
				$resArray, 
				array(
					'result' => "M",
					'Message'=>'ไม่สามารถลบรายการได้ เนื่องจากเอกสารนี้ไม่ได้ผูกกับเครื่องฆ่า หรือ เครื่องฆ่าเชื้อกำลังทำงานอยู่ !!',						
				)
			);
	
			echo json_encode(array("result" => $resArray));
	
			unset($conn);
			die;
		}

	}

	// var
    $p_data = $_POST['p_data'];

    $d_array_data = explode("@", $p_data);

    $d_data = 3; // LIST_ID + "@" + LIST_IMPORT_ID + "@" + LIST_ITEM_STOCK_ID

	$d_is_has_no_wash = false;
	$d_is_has_new_item = false;

    $sql_list_sterile_detail_id = "";
    $sql_list_wash_detail_id = "";
    $sql_list_row_id = "";

    $d_max = sizeof($d_array_data);

	// ------------------------------------------------------------------------------

    for ($i = 0; $i < $d_max; $i = $i + $d_data) {

		// Check Import Id
        if ($d_array_data[$i] == '') {
            break;
		}else if(!$d_is_has_new_item && $d_array_data[$i+1] == '0'){
			$d_is_has_new_item = true;
		}else if(!$d_is_has_no_wash && $d_array_data[$i+1] == '-1'){
			$d_is_has_no_wash = true;
		}

		$sql_list_sterile_detail_id = $sql_list_sterile_detail_id . $d_array_data[$i] . ',';
		$sql_list_wash_detail_id 	= $sql_list_wash_detail_id . $d_array_data[$i+1] . ',';
		$sql_list_row_id 			= $sql_list_row_id . $d_array_data[$i+2] . ',';
	}
	
	// ------------------------------------------------------------------------------
	// Check list Id
	// ------------------------------------------------------------------------------
	if($sql_list_sterile_detail_id == "" || $sql_list_wash_detail_id == "" || $sql_list_row_id == ""){
		array_push(
			$resArray, 
			array(
				'result' => "E",
				'Message'=>'ไม่สามารถลบรายการได้ !!',						
			)
		);

		echo json_encode(array("result" => $resArray));

		unset($conn);
		die;
	}

	// ------------------------------------------------------------------------------
	// Var List Condition
	// ------------------------------------------------------------------------------
	$sql_list_sterile_detail_id = substr($sql_list_sterile_detail_id, 0, -1);
	$sql_list_wash_detail_id 	= substr($sql_list_wash_detail_id, 0, -1);
	$sql_list_row_id 			= substr($sql_list_row_id, 0, -1);

	$d_complete_1 = "0";
	$d_complete_2 = "0";
	$d_complete_3 = "1";

	// ------------------------------------------------------------------------------
    // Update Item Stock (3) (On Stock) (From New Item Stock)
	// ------------------------------------------------------------------------------
	if($d_is_has_new_item){

    	if($p_DB == 0){
			$sql_update3 = "UPDATE		itemstock 

							LEFT JOIN 	steriledetail
							ON			steriledetail.ItemStockID = itemstock.RowID

							SET 		itemstock.IsStatus = 3, 
										itemstock.IsNew = 1 , 
										itemstock.IsNewUsage = 1 

							WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
							AND			steriledetail.ImportID = 0 
							AND 		itemstock.IsStatus = 2 ";
		}else if($p_DB == 1){
			$sql_update3 = "UPDATE		itemstock 

			
							SET 		itemstock.IsStatus = 3, 
										itemstock.IsNew = 1 , 
										itemstock.IsNewUsage = 1 

							FROM		itemstock

							LEFT JOIN 	steriledetail
							ON			steriledetail.ItemStockID = itemstock.RowID

							WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
							AND			steriledetail.ImportID = 0 
							AND 		itemstock.IsStatus = 2 ";
		}

		$res_update = $conn->prepare($sql_update3);
		$res_update->execute();

		if (!empty($res_update)) {	
			$d_complete_1 = "0";
		}else{
			$d_complete_1 = "1";
		}
	}

	// ------------------------------------------------------------------------------
    // Update Item Stock (0) (Send Sterile) (From No Wash)
	// ------------------------------------------------------------------------------
	if($d_is_has_no_wash){


		if($p_DB == 0){
				$sql_update0 = "UPDATE		itemstock 

								LEFT JOIN 	steriledetail
								ON			steriledetail.ItemStockID = itemstock.RowID

								SET 		itemstock.IsStatus = 0  

								WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
								AND			( steriledetail.ImportID = -1 ) 
								AND 		itemstock.IsStatus = 2 ";
		}else if($p_DB == 1){
			$sql_update0 = "	UPDATE		itemstock 

								SET 		itemstock.IsStatus = 0  

								FROM		itemstock

								LEFT JOIN 	steriledetail
								ON			steriledetail.ItemStockID = itemstock.RowID


								WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
								AND			( steriledetail.ImportID = -1 ) 
								AND 		itemstock.IsStatus = 2 ";
		}

		$res_update = $conn->prepare($sql_update0);
		$res_update->execute();

		
		if (! (!empty($res_update)) ) {	
			$d_complete_2 = "0";
		}else{
			$d_complete_2 = "1";
		}
	}

	// ------------------------------------------------------------------------------
    // Update Item Stock (1)
    // ------------------------------------------------------------------------------

    if($p_DB == 0){
		$sql_update1 = "UPDATE		itemstock 

						LEFT JOIN 	steriledetail
						ON			steriledetail.ItemStockID = itemstock.RowID

						SET 		itemstock.IsStatus = 1, 
									itemstock.UsageCount = (UsageCount - 1)

						WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
						AND			steriledetail.ImportID > 0 
						AND 		itemstock.IsStatus = 2 ";
	}else if($p_DB == 1){
		$sql_update1 = "UPDATE		itemstock 

						SET 		itemstock.IsStatus = 1, 
									itemstock.UsageCount = (UsageCount - 1)

						FROM		itemstock

						LEFT JOIN 	steriledetail
						ON			steriledetail.ItemStockID = itemstock.RowID

						WHERE 		steriledetail.ID IN ($sql_list_sterile_detail_id) 
						AND			steriledetail.ImportID > 0 
						AND 		itemstock.IsStatus = 2 ";
	}

	$res_update = $conn->prepare($sql_update1);
	$res_update->execute();

	if (!empty($res_update)) {	
		$d_complete_3 = "1";
	}

	// ------------------------------------------------------------------------------
    // Check Result Update Item Stock (0, 1, 3)
    // ------------------------------------------------------------------------------
	if ($d_complete_1 == "0" && $d_complete_2 == "0" && $d_complete_3 == "0") {	

		array_push(
				$resArray,
				array(
					'result' => "W",
					'Message'=>'รายการได้ถูกลบไปแล้ว !!',	
				)
			);

		echo json_encode(array("result"=>$resArray));
			
		unset($conn);
		die;
	}
	
	// ------------------------------------------------------------------------------
    // DELETE STERILE DETAIL
    // ------------------------------------------------------------------------------
    $sql_delete = " DELETE 
					FROM 	steriledetail 
					WHERE 	ID IN (" . $sql_list_sterile_detail_id . ") ";

	$query1 = $conn->prepare($sql_delete);
	$query1->execute();

	// ------------------------------------------------------------------------------
    // DELETE itemstocklog
	// ------------------------------------------------------------------------------

	/*
    $strSQL = "	DELETE 
				FROM 	itemstocklog 
				WHERE 	SterileDetailID IN (" . $sql_list_sterile_detail_id . ") ";
    mysqli_query($conn, $strSQL);
	*/

	$strSQL = "	UPDATE 	itemstocklog 
				SET 	IsCancel = 1,
						CancelDate = $date 
				WHERE 	SterileDetailID IN (" . $sql_list_sterile_detail_id . ") ";

	$query2 = $conn->prepare($strSQL);
	$query2->execute();
	

	// ------------------------------------------------------------------------------
    // UPDATE WASH DETAIL (From Wash)
    // ------------------------------------------------------------------------------
    $strSQL = "	UPDATE 	washdetail 
				SET 	IsStatus = 1 
				WHERE 	ID IN ($sql_list_wash_detail_id) ";

	$query3 = $conn->prepare($strSQL);
	$query3->execute();

    // ------------------------------------------------------------------------------
    // UPDATE WASH
    // ------------------------------------------------------------------------------

		
    if($p_DB == 0){
			$strSQL = "	UPDATE 		wash 

						LEFT JOIN 	washdetail
						ON			wash.DocNo = washdetail.WashDocNo 

						SET 		wash.IsStatus = 1 

						WHERE 		washdetail.ID IN ($sql_list_wash_detail_id) ";
	}else if($p_DB == 1){
			$strSQL = "	UPDATE 		wash 

						SET 		wash.IsStatus = 1 

						FROM		wash

						LEFT JOIN 	washdetail
						ON			wash.DocNo = washdetail.WashDocNo 


						WHERE 		washdetail.ID IN ($sql_list_wash_detail_id) ";
	}


	$query4 = $conn->prepare($strSQL);
	$query4->execute();


	// ------------------------------------------------------------------------------

	array_push(
		$resArray, 
		array(
				'result' => "A",					
		)
	);
    // -------------------------------------------------------
    // echo json
    // -------------------------------------------------------
    echo json_encode(array("result" => $resArray));

    // -------------------------------------------------------
    // Close Connection
    // -------------------------------------------------------

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
