<?php

//EDIT LOG
//23-01-2026 15.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

// -----------------------------------------------------
// Update itemstock_department
// -----------------------------------------------------

function updateItemstockDepartment($conn, $d_itemcode, $d_dept_id, $d_qty, $d_ref_docno)
{
    // -----------------------------------------------------
    // Update itemstock_department
    // -----------------------------------------------------

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if($p_DB == 0){
        $date = "NOW()";
    }else if($p_DB == 1){
        $date = "GETDATE()";
    }

    $sql_update = " UPDATE      itemstock_department

                    SET 	    Qty = (Qty + $d_qty),
                                ModifyDate = $date

                    WHERE	    itemstock_department.ItemCode = '$d_itemcode'
                    AND		    itemstock_department.DeptID = $d_dept_id 
                    AND         itemstock_department.B_ID = $B_ID ";

    $query_gen_docno = $conn->prepare($sql_update);
    $query_gen_docno->execute();
    //echo $sql_update;

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
        $date = "NOW()";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
        $date = "GETDATE()";
    }

    $sql_insert = "INSERT INTO 	itemstock_department_log
                    (
                                ItemStockDept_RowID,
                                Qty,
                                CreateDate,
                                RefDocNo,
                                B_ID
                    )
                  SELECT		$top
                                itemstock_department.RowID,
                                $d_qty,
                                $date,
                                '$d_ref_docno',
                                $B_ID

                  FROM 		    itemstock_department

                  WHERE		    itemstock_department.ItemCode = '$d_itemcode'
                  AND		    itemstock_department.DeptID = $d_dept_id
                  AND         itemstock_department.B_ID = $B_ID

                  $limit ";

    //echo $sql_insert;

    $result_insert = $conn->prepare($sql_insert);
    $result_insert->execute();
    return $d_qty;

}

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_id"]) && isset($_POST["p_user_code"])) {

    $p_id = $_POST['p_id'];
    $p_user_code = $_POST['p_user_code'];

    if ( isset($_POST["p_is_separatetosterile"]) ) {
        $p_is_separatetosterile = $_POST['p_is_separatetosterile'];
	}

    $d_opt = "E";
    $d_usage_code = "";
    $d_complete = 0;

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if ($p_is_separatetosterile == 1){
        $p_id = str_replace(str_split('[]'), '', $p_id);
        $p_id = str_replace(' ', '', $p_id);
        $d_array_ID = explode(",", $p_id);
    
        for($i = 0 ; $i < sizeof($d_array_ID) ; $i++){
    
            if($i==0){
                $t="";
            }else{
                $t=","; 
            }
            
            $d_array_ID_new .= $t.$d_array_ID[$i];
        }

        $p_id = str_replace(str_split('[]'), ' ', $p_id);
        $p_id = explode(",", $p_id);

    }

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }
    
    if($p_is_separatetosterile == 1){
        for ($j = 0; $j < count($p_id); $j++) {

            $sql_query = "  SELECT      $top
                                        itemstock.UsageCode,
                                        itemstock.RowID,
                                        itemstock.ItemCode,
                                        itemstock.DepID,
                                        itemstock.IsNew,
                                        sendsterile.DocNo,
                                        sendsterile.IsWeb,
                                        sendsteriledetail.IsNewDepartment 

                            FROM        itemstock

                            LEFT JOIN   sendsteriledetail
                            ON          itemstock.RowID = sendsteriledetail.ItemStockID

                            LEFT JOIN   sendsterile
                            ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                            WHERE       sendsteriledetail.ID IN ($p_id[$j])

                            AND         itemstock.IsStatus = 0
                            AND         itemstock.B_ID = $B_ID
                            AND         sendsteriledetail.B_ID = $B_ID
                            $limit ";

            $meQuery = $conn->prepare($sql_query);
            $meQuery->execute();
            if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $d_usage_code = $Result['UsageCode'];
                $d_row_id = $Result['RowID'];
                $d_is_new = $Result['IsNew'];
                $d_docno = $Result['DocNo'];
                $d_is_web = $Result['IsWeb'];
                $d_itemcode = $Result['ItemCode'];
                $d_dept_id = $Result['DepID'];
                $d_is_new_department = $Result['IsNewDepartment'];
            }

            if ($d_usage_code == "") {
                array_push(
                    $resArray, array(
                        'result' => $d_opt,
                        'Message' => "ไม่สามารถลบรายการได้ !!",
                        'sql_query' => $sql_query,
                    )
                );

                echo json_encode(array("result" => $resArray));

                return;
            }

            // ---------------------------------------------
            // Update item department
            // ---------------------------------------------
            if (isset($_POST["p_is_used_itemstock_department"]) && $d_is_new_department == "0") {
                $d_return = updateItemstockDepartment($conn, $d_itemcode, $d_dept_id, '1', $d_docno);
            }

            // -----------------------------------------------------
            // Update Item Stock
            // -----------------------------------------------------
            if ($d_is_new == "1") {

                $sql_update = 
                        " UPDATE  itemstock
                            SET     IsCancel = 1,
                                    IsStatus = 8
                            WHERE   itemstock.RowID = $d_row_id ";

            } else {

                $sql_update = 
                        " UPDATE  itemstock
                            SET     IsStatus = ( CASE WHEN $d_is_web <> 0 THEN 5 ELSE 4 END ),
                                    itemstock.IsPay = 1
                            WHERE   itemstock.RowID = $d_row_id ";

            }
            $query1 = $conn->prepare($sql_update);
            $query1->execute();
        }
    }else{
         $sql_query = " SELECT      $top
                                    itemstock.UsageCode,
                                    itemstock.RowID,
                                    itemstock.ItemCode,
                                    itemstock.DepID,
                                    itemstock.IsNew,
                                    sendsterile.DocNo,
                                    sendsterile.IsWeb,
                                    sendsteriledetail.IsNewDepartment 

                        FROM        itemstock

                        LEFT JOIN   sendsteriledetail
                        ON          itemstock.RowID = sendsteriledetail.ItemStockID

                        LEFT JOIN   sendsterile
                        ON          sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                        WHERE       sendsteriledetail.ID = '$p_id'

                        AND         itemstock.IsStatus = 0
                        AND         itemstock.B_ID = $B_ID
                        AND         sendsteriledetail.B_ID = $B_ID

                        $limit ";

        $meQuery = $conn->prepare($sql_query);
        $meQuery->execute();
        if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $d_usage_code = $Result['UsageCode'];
            $d_row_id = $Result['RowID'];
            $d_is_new = $Result['IsNew'];
            $d_docno = $Result['DocNo'];
            $d_is_web = $Result['IsWeb'];
            $d_itemcode = $Result['ItemCode'];
            $d_dept_id = $Result['DepID'];
            $d_is_new_department = $Result['IsNewDepartment'];
        }

        if ($d_usage_code == "") {
            array_push(
                $resArray, array(
                    'result' => $d_opt,
                    'Message' => "ไม่สามารถลบรายการได้ !!",
                    'sql_query' => $sql_query,
                )
            );

            echo json_encode(array("result" => $resArray));

            return;
        }

        // ---------------------------------------------
        // Update item department
        // ---------------------------------------------
        if (isset($_POST["p_is_used_itemstock_department"]) && $d_is_new_department == "0") {
            $d_return = updateItemstockDepartment($conn, $d_itemcode, $d_dept_id, '1', $d_docno);
        }

        // -----------------------------------------------------
        // Update Item Stock
        // -----------------------------------------------------
        if ($d_is_new == "1") {

            $sql_update = 
                    " UPDATE  itemstock
                        SET     IsCancel = 1,
                                IsStatus = 8
                        WHERE   itemstock.RowID = $d_row_id ";

        } else {

            $sql_update = 
                    " UPDATE  itemstock
                        SET     IsStatus = ( CASE WHEN $d_is_web <> 0 THEN 5 ELSE 4 END ),
                                itemstock.IsPay = 1
                        WHERE   itemstock.RowID = $d_row_id ";

        }

        $query1 = $conn->prepare($sql_update);
        $query1->execute();

    }

    //-------------------------------------------------------------------------------------------------------------

    if ($p_is_separatetosterile == 1){

        // -----------------------------------------------------
        // Remove Send Sterile
        // -----------------------------------------------------
        $sql_delete = 
                    "   DELETE
                        FROM    sendsteriledetail
                        WHERE   sendsteriledetail.ID IN ($d_array_ID_new) ";

        $result_delete = $conn->prepare($sql_delete);
        $result_delete->execute();
        if (!empty($result_delete)) {
            if (!empty($result_delete)) {
                $d_complete++;
            }
        }

        // -----------------------------------------------------
        // Update Sterile Detail
        // -----------------------------------------------------

        if($p_DB == 0){
            $date = 'NOW()';
        }else if($p_DB == 1){
            $date = 'GETDATE()';
        }

        $sql_update = 
                    "   UPDATE      steriledetail

                        LEFT JOIN 	washdetail
                        ON	        washdetail.ID = steriledetail.ImportID

                        SET         steriledetail.IsCancel = 1,
                                    steriledetail.CancelDate = $date

                        WHERE       washdetail.ImportID IN ($d_array_ID_new) 
                        AND         washdetail.B_ID = $B_ID 
                        AND         steriledetail.B_ID = $B_ID ";

        $query1 = $conn->prepare($sql_update);
        $query1->execute();
        // -----------------------------------------------------
        // Result
        // -----------------------------------------------------
        if ($d_complete > 0) {
            $d_opt = "A";
            $d_message = "ลบรายการสำเร็จ.";
        } else {
            $d_message = "ไม่สามารถลบรายการได้ !!!";
        }

        array_push(
            $resArray, array(
                'result' => $d_opt,
                'Message' => $d_message,
                'sql_delete' => $sql_delete,
                'sql_query' => $sql_query,
            )
        );

    }else{

        // -----------------------------------------------------
        // Remove Send Sterile
        // -----------------------------------------------------
        $sql_delete = 
                    "   DELETE
                        FROM    sendsteriledetail
                        WHERE   sendsteriledetail.ID = '$p_id' ";

        $result_delete = $conn->prepare($sql_delete);
        $result_delete->execute();
        if (!empty($result_delete)) {
            if (!empty($result_delete)) {
                $d_complete++;
            }
        }

        // -----------------------------------------------------
        // Update Sterile Detail
        // -----------------------------------------------------

        if($p_DB == 0){
            $date = 'NOW()';
        }else if($p_DB == 1){
            $date = 'GETDATE()';
        }

        $sql_update = 
                    "   UPDATE      steriledetail

                        LEFT JOIN 	washdetail
                        ON	        washdetail.ID = steriledetail.ImportID

                        SET         steriledetail.IsCancel = 1,
                                    steriledetail.CancelDate = $date

                        WHERE       washdetail.ImportID = '$p_id' 
                        AND         washdetail.B_ID = $B_ID
                        AND         steriledetail.B_ID = $B_ID ";

        $query1 = $conn->prepare($sql_update);
        $query1->execute();
        // -----------------------------------------------------
        // Result
        // -----------------------------------------------------
        if ($d_complete > 0) {
            $d_opt = "A";
            $d_message = "ลบรายการสำเร็จ.";
        } else {
            $d_message = "ไม่สามารถลบรายการได้ !!!";
        }

        array_push(
            $resArray, array(
                'result' => $d_opt,
                'Message' => $d_message,
                'sql_delete' => $sql_delete,
                'sql_query' => $sql_query,
            )
        );
    }

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