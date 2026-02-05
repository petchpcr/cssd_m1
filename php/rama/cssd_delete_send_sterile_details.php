<?php
//EDIT LOG
//23-01-2026 12.42 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_data"]) && isset($_POST["p_user"])) {

    $p_data = $_POST['p_data'];
    $p_user = $_POST['p_user'];
    $p_DB = $_POST['p_DB'];
    $d_opt = "E";
    $d_usage_code = "";
    $d_complete = 0;
    $d_is_status = "0";

    $B_ID = $_POST['B_ID'];

    $sql_query = "  SELECT      itemstock.UsageCode,
                                itemstock.RowID,
                                itemstock.LastReceiveInDeptDate AS is_last_receive,
                                itemstock.LastPayDeptDate AS LastPayDeptDate,
                                sendsterile.DocNo,
                                sendsterile.IsWeb,
                                sendsterile.IsStatus,
                                sendsteriledetail.ID 

                    FROM        itemstock

                    LEFT JOIN   sendsteriledetail
                    ON          itemstock.RowID = sendsteriledetail.ItemStockID

                    LEFT JOIN   sendsterile
                    ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                    WHERE       sendsteriledetail.ID IN ($p_data) 

                    AND         itemstock.IsCancel = 0  
                    AND         itemstock.IsStatus <> 8 
                    AND         itemstock.B_ID = $B_ID 
                    AND         sendsteriledetail.B_ID = $B_ID ";

    $meQuery = $conn->prepare($sql_query);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_usage_code = $Result['UsageCode'];
        $d_row_id = $Result['RowID'];
        $d_is_last_receive = $Result['is_last_receive'];
        $d_last_pay_dept_date = $Result['LastPayDeptDate'];
        $d_docno = $Result['DocNo'];
        $d_is_web = $Result['IsWeb'];
        $d_id = $Result['ID'];
        $d_is_status = $Result['IsStatus'];

        // -----------------------------------------------------
        // Update Item Stock
        // -----------------------------------------------------
        if ($d_last_pay_dept_date == "0") {

            $sql_update = " UPDATE  itemstock
                            SET     IsCancel = 1,
                                    IsStatus = 8,
                                    PreviousStatus = $d_is_status 
                            WHERE   itemstock.RowID = $d_row_id ";

        } else {
            $sql_update = " UPDATE  itemstock
                            SET     IsStatus = ( CASE WHEN $d_is_web <> 0 THEN 5 ELSE 4 END ),
                                    itemstock.IsPay = 1,
                                    PreviousStatus = $d_is_status 
                            WHERE   itemstock.RowID = $d_row_id ";
        }

        $query1 = $conn->prepare($sql_update);
        $query1->execute();
        // -----------------------------------------------------
        // Update Sterile Detail
        // -----------------------------------------------------
        if($p_DB == 0){
            $date = "NOW()";
        }else if($p_DB == 1){
            $date = "getDate()";
        }


        $sql_update = " UPDATE      steriledetail 

                        LEFT JOIN 	washdetail
                        ON		    washdetail.ID = steriledetail.ImportID

                        SET         steriledetail.IsCancel = 1,
                                    steriledetail.CancelDate = $date 

                        WHERE       washdetail.ImportID = $d_id ";

        $query1 = $conn->prepare($sql_update);
        $query1->execute();
        // -----------------------------------------------------
        // Mark Send Sterile
        // -----------------------------------------------------
        $sql_delete = " UPDATE  sendsteriledetail 
                        SET     IsCancel = 1,
                                CancelDate	= $date,
                                UserCancel	= $p_user 

                        WHERE   sendsteriledetail.ID = $d_id ";

        $result_delete = $conn->prepare($sql_delete);
        $result_delete->execute();

        if (!empty($result_delete)) {
            if (!empty($result_delete)) {
                $d_complete++;
            }
        }
    }

    // -----------------------------------------------------
	// Update Payout
	// -----------------------------------------------------
    if($d_is_status <> "0"){
        $sql_query = 
            "	SELECT      payoutdetail.DocNo , 
        					payoutdetail.ItemCode, 
        					Count(payoutdetail.ID) AS c 
            
        		FROM        payoutdetail 
                            
        		INNER JOIN 	payout  
        		ON 			payout.DocNo = payoutdetail.DocNo 
                            
        		INNER JOIN 	sendsterile 
        		ON 			sendsterile.DocNo = payout.RefDocNo  
                            
        		INNER JOIN 	sendsteriledetail 
        		ON 			sendsteriledetail.SendSterileDocNo = sendsterile.DocNo 
                            
        		INNER JOIN 	itemstock 
        		ON 			itemstock.RowID = sendsteriledetail.ItemStockID 
                            
        		WHERE       sendsteriledetail.ID IN ($p_data) 
                            
        		AND 		itemstock.ItemCode = payoutdetail.ItemCode 
                AND         itemstock.B_ID = $B_ID 
                AND         sendsterile.B_ID = $B_ID
                AND         payout.B_ID = $B_ID
                            
        		GROUP BY   	payoutdetail.DocNo  , 
        					payoutdetail.ItemCode ";

        $meQuery = $conn->prepare($sql_query);
        $meQuery->execute();
        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

            $d_DocNo = $Result['DocNo'];
            $d_ItemCode = $Result['ItemCode'];
            $d_Count = $Result['c'];

            
            if($p_DB == 0){
                $date = "NOW()";
            }else if($p_DB == 1){
                $date = "getDate()";
            }

            // -----------------------------------------------------
            // Update Payout Detail QTY
            // -----------------------------------------------------
            $sql_update = 
                "   UPDATE      payoutdetail 
                    SET         Qty = ( Qty - $d_Count ), 
                                ModifyTime = $date  
                    
                    WHERE       payoutdetail.DocNo = '$d_DocNo' 
                    AND 		payoutdetail.ItemCode = '$d_ItemCode' 
                    AND 		IsStatus IN (0, 1) ";
            $query_1 = $conn->prepare($sql_update);
            $query_1->execute();
        }
    }

    // -----------------------------------------------------
    // Result
    // -----------------------------------------------------
    if ($d_complete > 0) {
        $d_opt = "A";
        $d_message = "ลบรายการสำเร็จ.";
    } else {
        $d_message = "ไม่สามารถลบรายการได้ !!";
    }

    array_push(
        $resArray, array(
            'result' => $d_opt,
            'Message' => $d_message
        )
    );

} else {
    array_push(
        $resArray, array(
            'result' => "I",
            'Message' => 'ข้อมูลที่ส่งมาไม่ถูกต้อง!!',
        )
    );
}

echo json_encode(array("result" => $resArray));

unset($conn);
die;

?>