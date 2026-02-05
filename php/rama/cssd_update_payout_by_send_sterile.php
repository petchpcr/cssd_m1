<?php
//EDIT LOG
//23-01-2026 12.27 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';

$resArray = array();

function onCompleteNormal($conn, $d_docno, $d_dept_id)
{
	
	// Default Status
	$d_doc_status = "2";
	$d_status = "4";
	$d_status_sub = "1";
	$d_is_cancel = "0";
	$d_cancel_date = "null";
	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST['B_ID'];
	// ---------------------------------------------
	//  Get IsRepeat
	// ---------------------------------------------
	if($p_DB == 0){
		$top = "";
		$limit = "LIMIT 1";
	}else if($p_DB == 1){
			$top = "TOP 1";
			$limit = "";
	}

	$sql = 	"	SELECT $top	
						COALESCE( IsRepeat, '0') AS IsRepeat, 
						COALESCE( IsReceiveManual, '0') AS IsReceiveManual 

				FROM 	department 

				WHERE 	ID = $d_dept_id 
				AND 	B_ID = $B_ID

				$limit";

	$meQuery = $conn->prepare($sql);
	$meQuery->execute();
	if ($res = $meQuery->fetch(PDO::FETCH_ASSOC)) {

		if( $res['IsRepeat'] == "0"){
			$d_is_cancel = "1";
			$d_cancel_date = "NOW()";

			$d_doc_status = "3";
			$d_status_sub = "2";
			$d_status = "8";
		}

		if( $res['IsReceiveManual'] == "0"){
			$d_doc_status = "3";
			$d_status = "5";
			$d_status_sub = "1";
		}
	}

	// ---------------------------------------------
	// Update payout 
	// ---------------------------------------------
	if($p_DB == 0){
			$date = "NOW()";
	}else if($p_DB == 1){
			$date = "getDate()";
	}


	$sql_update = 	"UPDATE 	payout
					SET 		payout.IsStatus = $d_doc_status,
								PayDate = $date,
								ModifyDate = $date,
								Remark = CONCAT(COALESCE(`Remark`, ''), ', ปิดเอกสาร') 

					WHERE 		payout.DocNo = '$d_docno' 
					
					AND 		payout.IsStatus IN (0, 1) ";

	//echo $sql_update;

	$res_update = $conn->prepare($sql_update);
	$res_update->execute();
	if (empty($res_update)) {	

		array_push(
			$resArray, 
			array( 	
				'result' => "E"	
			)
		);
		
		unset($conn);
		die;
		
		echo json_encode(array("result"=>$resArray));

		return;

	}

	// ---------------------------------------------
	// Update payout detail
	// ---------------------------------------------

	$sql_update = " UPDATE 		payoutdetail

					SET 		payoutdetail.IsStatus = 1 

					WHERE 		payoutdetail.DocNo = '$d_docno' 

					AND 		payoutdetail.IsStatus = 0 ";

	$res_update = $conn->prepare($sql_update);
	$res_update->execute();
	// ---------------------------------------------
	// Update item stock 
	// ---------------------------------------------

	if($p_DB == 0){
		$sql_update = " UPDATE 			itemstock

										LEFT JOIN		payoutdetailsub 
										ON 				payoutdetailsub.ItemStockID = itemstock.RowID

										LEFT JOIN		payoutdetail
										ON 				payoutdetail.ID = payoutdetailsub.Payoutdetail_RowID 

										LEFT JOIN		item
										ON 				item.itemcode = itemstock.ItemCode 

										SET 			itemstock.IsStatus = $d_status,
														itemstock.IsCancel = $d_is_cancel,
														itemstock.CancelDate = $d_cancel_date,
														itemstock.IsPay = 1,
														itemstock.IsHN = 0 

										WHERE			payoutdetail.DocNo = '$d_docno' 

										AND				itemstock.B_ID = '$B_ID'

										AND				itemstock.IsStatus = 3 ";
}else if($p_DB == 1){

	$sql_update = " UPDATE 			itemstock

									SET 				itemstock.IsStatus = $d_status,
															itemstock.IsCancel = $d_is_cancel,
															itemstock.CancelDate = $d_cancel_date,
															itemstock.IsPay = 1,
															itemstock.IsHN = 0 
									
									FROM				itemstock

									LEFT JOIN		payoutdetailsub 
									ON 					payoutdetailsub.ItemStockID = itemstock.RowID

									LEFT JOIN		payoutdetail
									ON 					payoutdetail.ID = payoutdetailsub.Payoutdetail_RowID 

									LEFT JOIN		item
									ON 					item.itemcode = itemstock.ItemCode 

									WHERE				payoutdetail.DocNo = '$d_docno' 
									AND				itemstock.B_ID = '$B_ID'

									AND					itemstock.IsStatus = 3 ";
}



$query1 = $conn->prepare($sql_update);
$query1->execute();
	// ---------------------------------------------
	// Update payout detail sub
	// ---------------------------------------------

	$sql_update_ = " UPDATE 		payoutdetailsub 

					LEFT JOIN		payoutdetail
					ON 				payoutdetail.ID = payoutdetailsub.Payoutdetail_RowID

					SET 			payoutdetailsub.IsStatus = $d_status_sub  

					WHERE			payoutdetail.DocNo = '$d_docno' 
					
					AND 			payoutdetail.IsStatus < 2 ";

	$query1 = $conn->prepare($sql_update_);
	$query1->execute();
	return 1;
}

function onComplete($conn, $d_docno, $d_dept_id)
{

	$d_return = 0;

	// ---------------------------------------------
	// Check Create Receive Department
	// ---------------------------------------------

    $d_return = onCompleteNormal($conn, $d_docno, $d_dept_id);

	return $d_return;

}

//=============================================
// Main
//=============================================

if ( isset($_POST["p_docno"]) ) {	

	// Default Status
	$d_doc_status = "2";
	$d_status = "4";
	$d_status_sub = "1";
	$d_is_cancel = "0";
	$d_cancel_date = "null";

	//=============================================
    // Document 
    //=============================================

    if ( isset($_POST["p_dept_id"]) ) {
        $p_dept_id = $_POST['p_dept_id'];
    }

    if ( isset($_POST["p_docno"]) ) {
        $p_docno = $_POST['p_docno'];
    }
    
    $d_return = onComplete($conn, $p_docno, $p_dept_id);

	// ---------------------------------------------
	// Return 
	// ---------------------------------------------

	array_push( 
		$resArray,array(
			'result'=>"A",
			'SQL'=>''
		)
	);

}else{
	array_push( 
		$resArray,array(
			'result'=>"I",
			'Message'=>'ข้อมูลที่ส่งมาไม่ถูกต้อง!!',
			'SQL'=>''
		)
	);
}

unset($conn);
die;
		
echo json_encode(array("result"=>$resArray));

?> 
