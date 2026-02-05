<?php
// EDIT LOG
// 22-01-2026 11.18 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query SELECT
// 22-01-2026 11.20 : ***ยังไม่ได้ใส่ Building_ID (B_ID) ในการ UPDATE
require 'connect.php';
$resArray = array();

function isNotApprove($conn): mixed
{
	
	$B_ID = $_POST['B_ID'];
	$d_is_not_approve = 0;

	$Sql = "	SELECT      SR_IsNotApprove AS SR_IsNotApprove   
			 
			 	FROM 		configuration 
				WHERE 		B_ID = '$B_ID' ";

	$result = $conn->prepare($Sql);
	$result->execute();

	if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
		$d_is_not_approve = $row["SR_IsNotApprove"];	
	}

	return $d_is_not_approve;
}

	// check for post data
	if (isset($_POST["p_docno"]) && isset($_POST["p_machine_no"])) {

		$d_is_not_approve = isNotApprove($conn);
		
		$p_machine_no = $_POST["p_machine_no"];
		$p_docno = $_POST["p_docno"];
		$p_DB = $_POST['p_DB'];

		if($p_DB == 0){

			$date = " NOW() ";
		
		}else if($p_DB == 1){
		
			$date = " GETDATE() ";
		
		}

		//-----------------------------------------
		// Update sterile
		//-----------------------------------------

		$sql_update_sterile = "	UPDATE 		sterile 

								SET 		IsStatus = 1 

								WHERE 		DocNo = '$p_docno' 
								AND 		IsStatus = 0 ";

		$res_update_sterile = $conn->prepare($sql_update_sterile);
		$res_update_sterile->execute();

		if (!empty($res_update_sterile)) {
			//-----------------------------------------	
			// Update sterile Machine
			//-----------------------------------------	

			$sql_update = 	 "UPDATE sterilemachine SET DocNo = null , IsActive = 0, StartTime = null, FinishTime = null, IsPause = 0, PauseTime = null, LastTime = $date WHERE DocNo = '$p_docno' ";

			$res = $conn->prepare($sql_update);
			$res->execute();

			if($d_is_not_approve == "1"){
				//---------------------------------------------
				// Update Item Stock
				//---------------------------------------------
				if($p_DB == 0){

					$Sql_2 = "	UPDATE 		itemstock 

								LEFT JOIN 	steriledetail
								ON			steriledetail.ItemStockID = itemstock.RowID

								SET 		itemstock.IsStatus = 3,
											itemstock.IsStock = 1   

								WHERE 		steriledetail.DocNo = '$p_docno' 
								AND			steriledetail.IsOccurance = 0 ";
				
				}else if($p_DB == 1){
				
					$Sql_2 = "	UPDATE 		itemstock 

								SET 		itemstock.IsStatus = 3,
											itemstock.IsStock = 1   

								FROM 		itemstock

								LEFT JOIN 	steriledetail
								ON			steriledetail.ItemStockID = itemstock.RowID

								WHERE 		steriledetail.DocNo = '$p_docno' 
								AND			steriledetail.IsOccurance = 0 ";
				
				}

				$res = $conn->prepare($Sql_2);
				$res->execute();

				//---------------------------------------------
				// Update steriledetail
				//---------------------------------------------
				$Sql_3 =   "UPDATE 		steriledetail 

							SET 		steriledetail.IsStatus = 2  

							WHERE 		steriledetail.DocNo = '$p_docno' 
							AND			steriledetail.IsOccurance = 0 ";

				$res = $conn->prepare($Sql_3);
				$res->execute();

			}

			array_push(
				$resArray, 
				array(
						'result' => "A",
						'machine_no' => $p_machine_no					
				)
			); 

		} else {
			array_push(
				$resArray, 
				array(
						'result' => "E"				
				)
			); 
		}
		
	} else {
		array_push(
			$resArray, 
			array(
					'result' => "I"				
			)
		); 
	}

	// -------------------------------------------------------
    // echo json
    // -------------------------------------------------------
    echo json_encode(array("result" => $resArray));

    // -------------------------------------------------------
    // Close Connection
    // -------------------------------------------------------
	unset($conn);
	die;
?>