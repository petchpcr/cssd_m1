<?php
//EDIT LOG
// 23-01-2026 10.46 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 23-01-2026 15.04 : เลขเอกสาร 'XXYYMM'+'B_ID-'+'#####' (SS2601A-00001)
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require 'connect.php';
$resArray = array();

// -----------------------------------------------------
// Check Qr
// ---------------------------------------------
function checkQr($conn, $p_qr, $p_dept_id, $B_ID){

    $p_DB = $_POST['p_DB'];
    if (isset($_POST["p_is_used_itemstock_department"]) ){
        $p_is_used_itemstock_department = $_POST['p_is_used_itemstock_department'];
    }else{
        $p_is_used_itemstock_department = "0";
    }
    
    $d_row_id = "-";
    $d_message = "";
    $d_return = "E";

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

	$payout_B_ID = "";
	$itemstock_B_ID = "";
	if($B_ID != "0"){
		$payout_B_ID = " AND payout.B_ID = $B_ID";
		$itemstock_B_ID = " AND itemstock.B_ID = $B_ID";
	}


    $sql = "SELECT      $top
                        payoutdetail.DocNo

            FROM        payout

            INNER JOIN  payoutdetail 
            ON          payout.DocNo = payoutdetail.DocNo

            INNER JOIN  payoutdetailsub 
            ON          payoutdetail.ID = payoutdetailsub.Payoutdetail_RowID

            WHERE       payoutdetailsub.UsageCode = '$p_qr'
            $payout_B_ID 
            ORDER BY    payout.DocNo DESC

            $limit";

            
    $meQuery = $conn->prepare($sql);
    $meQuery->execute();


	if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docNo = $row["DocNo"];

    } 

     $sql ="SELECT      department.DepName

            FROM        itemstock

            INNER JOIN  department 
            ON          itemstock.DepID = department.ID

            WHERE       itemstock.UsageCode = '$p_qr'
            $itemstock_B_ID ";

    $meQuery = $conn->prepare($sql);
    $meQuery->execute();

	if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_DepName = $row["DepName"];

    } 

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    $sql = "SELECT      $top
                        itemstock.RowID,
                        itemstock.DepID,
                        itemstock.B_ID,
                        itemstock.IsStatus,
                        itemstock.IsNewUsage AS IsNewUsage,
                        itemstock.IsPay,
                        itemstock.IsBorrow,
                        department.DepName2,
                        COALESCE(itemstock.UsageCount, 0) AS UsageCount,
                        COALESCE(item.RoundOfTimeUsed, 0) AS RoundOfTimeUsed  

            FROM        itemstock

            LEFT JOIN   department
            ON          department.ID = itemstock.DepID 

            LEFT JOIN   item
            ON          item.itemcode = itemstock.ItemCode

            WHERE       itemstock.UsageCode ='$p_qr'
            $itemstock_B_ID

            $limit  ";

    /*
    AND     itemstock.DepID = '$p_dept_id'
    AND     itemstock.B_ID = '$B_ID'
    AND     ( itemstock.IsStatus = 4 OR itemstock.IsStatus = 5 )
    AND     itemstock.IsNewUsage = 0
    AND     itemstock.IsPay = 1
     */

    $meQuery = $conn->prepare($sql);
    $meQuery->execute();

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_dept_name = $row["DepName2"];
        $d_dept_id = $row["DepID"];
        $d_b_id = $row["B_ID"];
        $d_is_status = $row["IsStatus"];
        $d_is_new = $row["IsNewUsage"];
        $d_is_pay = $row["IsPay"];
        $d_is_borrow = $row["IsBorrow"];

        if ($d_is_status != "4" and $d_is_status != "5") {

            if ($d_is_status == "0") {
                $d_status_name = "ส่งล้าง";
            }else if ($d_is_status == "1" or $d_is_status == "2") {
                $d_status_name = "ฆ่าเชื้อ";
            }else if ($d_is_status == "3") {
                $d_status_name = "จ่ายกลาง";
            } else {
                $d_status_name = "อื่นๆ ($d_is_status)";
            }

            $d_message = "รหัสใช้งานนี้อยู่ในแผนก $d_dept_name มีสถานะ '$d_status_name' ไม่สามารถนำเข้าส่งล้างได้ !!";
        } else if ($d_is_pay == "0") {
            $d_message = "รหัสใช้งานนี้มีสถานะยังไม่ถูกจ่าย !!";
        } else if ($d_is_borrow == "1") {
            $d_message = "รหัสใช้งานนี้อยู่ในสถานะยืม ไม่สามารถบันทึกรับเข้าได้ !!";
        } else if ($d_is_new != "0") {
            $d_message = "รหัสใช้งานนี้มีสถานะเป็นรายการใหม่ ไม่สามารถนำเข้าส่งล้างได้ !!";
        } else if ( ($p_is_used_itemstock_department == "0") && ($d_dept_id != $p_dept_id) ) {
            $d_message = "ส่งล้างไม่ตรงแผนก. รหัสใช้งานนี้อยู่ในแผนก $d_dept_name !!";
        } else {

            $d_row_id = $row["RowID"];
            $d_RoundOfTimeUsed = (int)$row["RoundOfTimeUsed"];
            $d_UsageCount = (int)$row["UsageCount"];

            if(( $d_RoundOfTimeUsed > 0 ) && ($d_RoundOfTimeUsed <= $d_UsageCount) ){
                $d_message = "รหัสใช้งานนี้ได้ถูกใช้งาน $d_RoundOfTimeUsed รอบ ซึ่งหมดรอบการใช้งานแล้ว !!";
                $d_return = "B";
            }else{
                $d_message = "รหัสใช้งานนี้สามารถส่งล้างได้ !!";
                $d_return = "A";
            }
        }

    } else {
        $d_message = "ไม่พบรหัสใช้งานนี้ในระบบ !!";
    }

    return $d_row_id . "," . $d_message . "," . $d_return . "," . $d_is_status . "," . $d_is_pay . "," . $d_docNo . "," . $d_DepName;

}

// -----------------------------------------------------
// Generate SendSterile DocNo
// -----------------------------------------------------

function getSendSterileDocNo($conn, $p_dept_id, $p_user_code, $B_ID)
{
	$AND_B_ID = "";
    $INSERT_B_ID = "";
    $VALUES_B_ID = "";
    $Doc_B_ID = "";
	if($B_ID != "0"){
		$AND_B_ID = " AND B_ID = $B_ID";
        $INSERT_B_ID = ", B_ID";
        $VALUES_B_ID = ", $B_ID";
        
        $Doc_B_ID = chr(64+$B_ID);
	}


    $p_DB = $_POST['p_DB'];
    
    $d_IsNonSelectDepartment = "0";

    // $sql = 
    //     "   SELECT      CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())), 3, 4),LPAD(MONTH(DATE(NOW())), 2, 0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo, 8, 5),UNSIGNED INTEGER)), 0) +1) , 5, 0)) AS DocNo
    //         FROM        sendsterile
    //         WHERE       DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
    //         ORDER BY    DocNo DESC
    //         LIMIT       1 ";
    if($p_DB == 0){

        $sql = "	SELECT 		CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
        
                            FROM 		sendsterile

                            WHERE 		DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                            ORDER BY 	DocNo DESC
                            
                            LIMIT 		1";

    }else if($p_DB == 1){

        $sql = "SELECT
                ISNULL(
                    (
                    SELECT TOP
                        1 CONCAT (
                            'SS',
                            RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                            '$Doc_B_ID-',
                            REPLACE( STR( ( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' ) 
                        ) AS DocNo 
                    FROM
                        sendsterile 
                    ORDER BY
                        sendsterile.DocNo DESC 
                    ),
                    CONCAT (
                        'SS',
                        RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                        '$Doc_B_ID-00001' 
                    ) 
                ) AS DocNo";
    }

    // Non Select Department

    if($p_dept_id == "-1"){

        $d_IsNonSelectDepartment = "1";
        if($p_DB == 0){
            $top = "";
            $limit = "LIMIT 1";
        }else if($p_DB == 1){
            $top = "TOP 1";
            $limit = "";
        }
        $sql_ = 
                "   SELECT      $top
                                DocNo
                    FROM        sendsterile 
                    WHERE       IsNonSelectDepartment = 1 
                    AND         FORMAT(sendsterile.DocDate,'dd-MM-yyyy') = FORMAT( GETDATE(),'dd-MM-yyyy')  
                    $AND_B_ID
                    $limit ";

        $meQuery_ = $conn->prepare($sql_);
        $meQuery_->execute();

        if ($row_ = $meQuery_->fetch(PDO::FETCH_ASSOC)) {

            $d_docno = $row_["DocNo"];

            return $d_docno;
        }

        if($p_DB == 0){
            $sql =  "SELECT      CONCAT('NA/',DATE_FORMAT(NOW(), '%y%m%d')) AS DocNo LIMIT 1 ";

        }else if($p_DB == 1){
            $sql =  "SELECT     TOP 1     CONCAT('NA/',FORMAT( GETDATE(),'yyyy-MM-dd')) AS DocNo  ";

        }

    }

    $meQuery = $conn->prepare($sql);
    $meQuery->execute();

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docno = $row["DocNo"];

        if($p_DB == 0){
            $date = "NOW()";
        }else if($p_DB == 1){
            $date = "GETDATE()";
        }

        $sql_insert = "  INSERT INTO sendsterile (
                                DocNo,
                                DocDate,
                                ModifyDate,
                                DeptID,
                                UserCode,
                                Qty,
                                IsCancel,
                                Remark,
                                IsNonSelectDepartment
                                $INSERT_B_ID
                            )
                        VALUES
                            (   '$d_docno',
                                $date,
                                $date,
                                '$p_dept_id',
                                '$p_user_code',
                                0,
                                0,
                                '',
                                $d_IsNonSelectDepartment
                                $VALUES_B_ID
                            )";

        $result_insert = $conn->prepare($sql_insert);
        $result_insert->execute();
        
        return $d_docno;

    } else {
        return null;
    }
}

// -----------------------------------------------------
// Add SendSterile Detail
// -----------------------------------------------------

function addSendSterileDetail($conn, $p_docno, $p_user_code, $p_row_id, $B_ID)
{
    // -----------------------------------------------------
    // Select Insert ItemStock to Sterile Detail
    // -----------------------------------------------------
    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";

        $Date = "NOW()";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";

        $Date = "GETDATE()";
    }

    $AND_B_ID = "";
    $INSERT_B_ID = "";
    $VALUES_B_ID = "";
	if($B_ID != "0"){
		$AND_B_ID = " AND B_ID = $B_ID";
        $INSERT_B_ID = ", B_ID";
        $VALUES_B_ID = ", $B_ID";
	}

    $sql_insert = "INSERT INTO  sendsteriledetail (
                                sendsteriledetail.SendSterileDocNo,
                                sendsteriledetail.ItemStockID,
                                sendsteriledetail.Qty,
                                sendsteriledetail.Remark,
                                sendsteriledetail.UsageCode,
                                sendsteriledetail.IsSterile,
                                sendsteriledetail.IsStatus
                                $INSERT_B_ID
                    )
                    SELECT      $top
                                '$p_docno',
                                itemstock.RowID,
                                1,
                                '',
                                itemstock.UsageCode,
                                0,
                                0
                                $VALUES_B_ID

                    FROM        itemstock

                    WHERE       itemstock.RowID = $p_row_id 
                    $AND_B_ID

                    $limit ";

    $query2 = $conn->prepare($sql_insert);
    $query2->execute();
    // -----------------------------------------------------
    // Update ItemStock 
    // -----------------------------------------------------

    $sql_update = " UPDATE 		itemstock
                    SET 		itemstock.IsStatus = 0,
                                itemstock.IsNewUsage = 0,
                                itemstock.IsNew = 0,
                                itemstock.IsPay = 0 ,
                                itemstock.IsDispatch = 0,
                                LastReceiveDeptDate = $Date

                    WHERE 		itemstock.RowID = $p_row_id 
                    AND 		itemstock.IsCancel = 0 ";

    $result_update = $conn->prepare($sql_update);
    $result_update->execute();

    if (!empty($result_update)) {
        return 1; 
    }else{
        return 0;
    }
}

// -----------------------------------------------------
// Update itemstock_department
// -----------------------------------------------------

function updateItemstockDepartment($conn, $d_qr, $d_ref_docno)
{
    $p_DB = $_POST['p_DB'];
    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

	$B_ID = $_POST["B_ID"];
    $AND_B_ID = "";
    $INSERT_B_ID = "";
    $VALUES_B_ID = "";
	if($B_ID != "0"){
		$AND_B_ID = " AND B_ID = $B_ID";
        $INSERT_B_ID = ", B_ID";
        $VALUES_B_ID = ", $B_ID";
	}


    $sql = "SELECT      $top
                        itemstock.DepID,
                        itemstock.ItemCode 

            FROM        itemstock

            WHERE       itemstock.UsageCode = '$d_qr'

            AND 		itemstock.IsCancel = 0 
            $AND_B_ID

            $limit ";


    $meQuery = $conn->prepare($sql);
    $meQuery->execute();

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_itemcode = $row["ItemCode"];
        $d_dept_id = $row["DepID"];
                
    }else{
        return;
    }

    // -----------------------------------------------------
    // Update itemstock_department
    // -----------------------------------------------------

    $sql_update = " UPDATE      itemstock_department

                    SET 	    Qty = (Qty - 1),
                                ModifyDate = NOW()

                    WHERE	    itemstock_department.ItemCode = '$d_itemcode'
                    AND		    itemstock_department.DeptID = $d_dept_id 
                    $AND_B_ID ";

    $query_update = $conn->prepare($sql_update);
    $query_update->execute();
    //echo $sql_update;
    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    if($p_DB == 0){
        $date = "NOW()";
    }else if($p_DB == 1){
        $date = "getDate()";
    }

    $sql_insert = "INSERT INTO 	itemstock_department_log
                    (
                                ItemStockDept_RowID,
                                Qty,
                                CreateDate,
                                RefDocNo
                                $INSERT_B_ID
                    )
                    SELECT		$top
                                itemstock_department.RowID,
                                -1,
                                $date,
                                '$d_ref_docno'
                                $VALUES_B_ID

                    FROM 		    itemstock_department

                    WHERE		    itemstock_department.ItemCode = '$d_itemcode'
                    AND		    itemstock_department.DeptID = $d_dept_id
                    $AND_B_ID

                    $limit ";

    //echo $sql_insert;

    $result_insert = $conn->prepare($sql_insert);
    $result_insert->execute();

    return $d_qr;

}

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_user_code"]) && isset($_POST["p_qr"]) && isset($_POST["p_dept_id"])) {

    $d_opt = "E";
    $d_complete = 0;
    $d_status ;
    $d_ispay ;
    $d_docno_payout ;
    $d_depName ;

    $p_user_code = $_POST['p_user_code'];
    $p_qr = $_POST['p_qr'];
    $p_dept_id = $_POST['p_dept_id'];

    $B_ID = $_POST['B_ID'];

    $sql_Dep = "SELECT  itemstock.DepID
                FROM    itemstock
                WHERE   itemstock.UsageCode = '$p_qr'
                AND     itemstock.B_ID = $B_ID ";

    $meQuery_Dep = $conn->prepare($sql_Dep);
    $meQuery_Dep->execute();

    if ($row_Dep = $meQuery_Dep->fetch(PDO::FETCH_ASSOC)) {

        $p_dept_id = $row_Dep["DepID"];

    } 

    // -----------------------------------------------------
    // Check QR
    // -----------------------------------------------------
    $p_data = checkQr($conn, $p_qr, $p_dept_id, $B_ID);
    
    $d_array_data = explode(",", $p_data);

    $d_row_id = $d_array_data[0];
    $d_message = $d_array_data[1];
    $d_result = $d_array_data[2];
    $d_status = $d_array_data[3];
    $d_ispay = $d_array_data[4];
    $d_docno_payout = $d_array_data[5];
    $d_depName = $d_array_data[6];

    // echo $d_row_id.", ".$d_message;

    if ($d_result == "B" OR $d_result == "E") {
        array_push(
            $resArray, array(
                'result' => $d_result,
                'RowId' => $d_row_id,
                'Message' => $d_message,
                'DocNo' => '',
                'IsStatus' => $d_status,
                'Ispay' => $d_ispay,
                'DocNo_Payput' => $d_docno_payout,
                'DepName' => $d_depName,
                'DepID' => $p_dept_id
            )
        );

        echo json_encode(array("result" => $resArray));

        return;
    }

    // -----------------------------------------------------
    // Check Document
    // -----------------------------------------------------
    $d_docno = $_POST['p_docno'];

    if ($d_docno != null) {

        $d_docno = $_POST['p_docno'];

    } else {

        $d_docno = getSendSterileDocNo($conn, $p_dept_id, $p_user_code, $B_ID);

    }

    // $d_docno = getSendSterileDocNo($conn, $p_dept_id, $p_user_code, $B_ID);

    if ($d_docno === null) {
        array_push(
            $resArray, array(
                'result' => $d_opt,
                'Message' => "ไม่สามารถสร้างเอกสารส่งล้างได้ !!",
                'DocNo' => $d_docno,
            )
        );

        echo json_encode(array("result" => $resArray));

        return;
    }

    // -----------------------------------------------------
    // Add Send Sterile Detail
    // -----------------------------------------------------
    $d_complete = addSendSterileDetail($conn, $d_docno, $p_user_code, $d_row_id, $B_ID);

    // -----------------------------------------------------
    // Result
    // -----------------------------------------------------
    if ($d_complete > 0) {
        $d_opt = "A";
        $d_message = "นำเข้ารายการส่งล้างสำเร็จ.";

        // ---------------------------------------------
        // Update item department
        // ---------------------------------------------
        if ( isset($_POST["p_is_used_itemstock_department"]) ) {
            $d_return = updateItemstockDepartment($conn, $p_qr, $d_docno);
        }

    } else {
        $d_message = "ไม่สามารถส่งล้างได้ !!";
    }

    array_push(
        $resArray, array(
            'result' => $d_opt,
            'Message' => $d_message,
            'DocNo' => $d_docno,
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