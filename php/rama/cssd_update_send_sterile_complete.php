<?php
//EDIT LOG
// 23-01-2026 11.39 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';

$resArray = array();

$p_DB = $_POST['p_DB'];

//---------------------------------------------
// Echo Json
//---------------------------------------------

function echoJson($resArray)
{
    echo json_encode(array("result" => $resArray));
}

//---------------------------------------------
// Update Send Sterile
//---------------------------------------------

function updateSendSterile($conn, $p_docno, $p_send_id, $p_receive_id, $p_dept_id, $p_status)
{

    $p_DB = $_POST['p_DB'];


    if (isset($_POST["p_is_usedwash"])) {
        $p_is_usedwash = $_POST['p_is_usedwash'];
    }else{
        $p_is_usedwash = "0";
    }

    if ( isset($_POST["p_is_separatetosterile"]) ) {
        $p_is_separatetosterile = $_POST['p_is_separatetosterile'];
	}

    if($p_dept_id == "0"){
        $d_sql_f_dept_id = "";
    }else{
        $d_sql_f_dept_id = "DeptID = $p_dept_id,";
    }

    if($p_DB == 0){
        $date = " NOW() ";
    }else if($p_DB == 1){
        $date = " GETDATE() ";
    }

    if($p_is_separatetosterile == 1){
        $strSQL =  "UPDATE      sendsterile
                    SET         $d_sql_f_dept_id
                                IsStatus = 4,
                                ModifyDate = $date,
                                UserReceive = '$p_receive_id',
                                UserSend = '$p_send_id'

                    WHERE       DocNo = '$p_docno'";

        $result = $conn->prepare($strSQL);
        $result->execute();

    }else{

        if($p_is_usedwash == "0"){
            $strSQL =  "UPDATE      sendsterile
                        SET         $d_sql_f_dept_id
                                    IsStatus = $p_status,
                                    ModifyDate = $date,
                                    UserReceive = '$p_receive_id',
                                    UserSend = '$p_send_id'
    
                        WHERE       DocNo='$p_docno'";
            
            $result = $conn->prepare($strSQL);
            $result->execute();
        }else{
            $strSQL =  "UPDATE      sendsterile
                        SET         $d_sql_f_dept_id
                                    IsStatus = 1,
                                    ModifyDate =  $date,
                                    UserReceive = '$p_receive_id',
                                    UserSend = '$p_send_id'
    
                        WHERE       DocNo='$p_docno'";

            $result = $conn->prepare($strSQL);
            $result->execute();
        }
    }
    
}

//---------------------------------------------
// Update Send Sterile Detail
//---------------------------------------------

function updateSendSterileDetail($conn, $p_docno, $p_status, $d_array_ID_separatetosterile)
{
    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if($p_DB == 0){
        $date = " NOW() ";
    }else if($p_DB == 1){
        $date = " GETDATE() ";
    }

    if (isset($_POST["p_is_usedwash"])) {
        $p_is_usedwash = $_POST['p_is_usedwash'];
    }else{
        $p_is_usedwash = "0";
    }

    for($i = 0 ; $i < sizeof($d_array_ID_separatetosterile) ; $i++){

        if($i==0){
            $t="";
        }else{
            $t=","; 
        }
        
        $d_array_ID_new .= $t.$d_array_ID_separatetosterile[$i];
    }

    $Sql = "	SELECT  COUNT( sendsteriledetail.IsSelected ) AS cnt_all
                FROM    sendsteriledetail 
                WHERE   sendsteriledetail.SendSterileDocNo = '$p_docno' 
                AND     sendsteriledetail.IsStatus = 2
                AND     sendsteriledetail.B_ID = $B_ID ";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt_all = $Result["cnt_all"];
    }

    $Sql = "	SELECT  COUNT(sendsteriledetail.ID) AS cnt_id
                FROM    sendsteriledetail 
                WHERE   sendsteriledetail.SendSterileDocNo = '$p_docno' 
                AND     sendsteriledetail.IsSelected = 1
                AND     sendsteriledetail.B_ID = $B_ID ";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt_id = $Result["cnt_id"];
    }

    if($cnt_all == $cnt_id){
        $strSQL = " UPDATE    sendsteriledetail
                    SET       sendsteriledetail.IsSelected = 1
                    WHERE     sendsteriledetail.ID IN($d_array_ID_new) 
                    AND       sendsteriledetail.SendSterileDocNo = '$p_docno'";

        $result = $conn->prepare($strSQL);
        $result->execute();

    }else{
        $strSQL = " UPDATE    sendsteriledetail
                    SET       sendsteriledetail.IsSelected = 0
                    WHERE     sendsteriledetail.ID NOT IN($d_array_ID_new) 
                    AND       sendsteriledetail.SendSterileDocNo = '$p_docno'";

        $result = $conn->prepare($strSQL);
        $result->execute();

    }

    if($p_is_usedwash == "0"){
        $strSQL = " UPDATE    sendsteriledetail
                    SET       sendsteriledetail.IsStatus = $p_status
                    WHERE     sendsteriledetail.SendSterileDocNo = '$p_docno'
                    AND       sendsteriledetail.IsSelected = 1";

        $result = $conn->prepare($strSQL);
        $result->execute();
    }else{
        $strSQL = " UPDATE    sendsteriledetail
                    SET       sendsteriledetail.IsStatus = 1
                    WHERE     sendsteriledetail.SendSterileDocNo = '$p_docno' 
                    AND       sendsteriledetail.IsSelected = 1";

        $result = $conn->prepare($strSQL);
        $result->execute();
    }

    $Sql = "	SELECT  COUNT( sendsteriledetail.IsSelected ) AS cnt_all
                FROM    sendsteriledetail 
                WHERE   sendsteriledetail.SendSterileDocNo = '$p_docno' 
                AND     sendsteriledetail.IsStatus = 2
                AND     sendsteriledetail.B_ID = $B_ID ";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt_all = $Result["cnt_all"];
    }

    $Sql = "	SELECT  COUNT(sendsteriledetail.ID) AS cnt_id
                FROM    sendsteriledetail 
                WHERE   sendsteriledetail.SendSterileDocNo = '$p_docno' 
                AND     sendsteriledetail.B_ID = $B_ID ";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $cnt_id = $Result["cnt_id"];
    }

    if($cnt_all == $cnt_id){
        $strSQL =  "UPDATE      sendsterile
                    SET       
                                sendsterile.IsStatus = 2,
                                sendsterile.ModifyDate = $date

                    WHERE       sendsterile.DocNo = '$p_docno'";

        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();
    }
}

//---------------------------------------------
// Check Send Sterile
//---------------------------------------------

function checkDocument($conn, $p_docno)
{
    $d_check = "0";
    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    $sql = "SELECT      $top 
                        sendsterile.DocNo 

            FROM        sendsterile

            WHERE       sendsterile.DocNo ='$p_docno'

            AND         sendsterile.IsStatus IN (1, 2)
            AND         sendsterile.B_ID = $B_ID

            $limit ";

    $query = $conn->prepare($sql);
    $query->execute();

    if ($rs = $query->fetch(PDO::FETCH_ASSOC)) {
        $d_check = "1";
    }

    return $d_check;
}

//---------------------------------------------
// Create Group Payout 
//---------------------------------------------

function createGroupPayout($conn, $p_docno, $B_ID)
{

    $d_payout_docno = "";
    $d_DeptID = "";
    $d_count_document = "0";

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
        $date = " NOW() ";

    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
        $date = " GETDATE() ";
    }

    // Get Send Sterile Data
    $sql_ss =
          "SELECT  $top  sendsterile.DocNo,
                         sendsterile.DeptID,
                         sendsterile.UserCode,
                         sendsterile.Qty,
                         sendsterile.IsWeb

          FROM           sendsterile

          WHERE          sendsterile.DocNo ='$p_docno'

          AND            sendsterile.B_ID = $B_ID

          $limit";

    $query1 = $conn->prepare($sql_ss);
    $query1->execute();

    if ($rs1 = $query1->fetch(PDO::FETCH_ASSOC)) {
        $d_docno = $rs1["DocNo"];
        $d_DeptID = $rs1["DeptID"];
        $d_UserCode = $rs1["UserCode"];
        $d_Qty = $rs1["Qty"];
        $d_IsWeb = $rs1["IsWeb"];
    }

    if($d_DeptID == ""){
        return "";
    }

    // Get Check Payout

    if($p_DB == 0){
        $strSQL = " SELECT      DocNo
                    FROM        `payout`
                    WHERE       DATE(CreateDate) = DATE(NOW()) 
                    AND         DeptID = $d_DeptID
                    AND         IsCancel = 0 
                    /*AND         IsWeb = 0*/
                    AND         IsBorrow = 0 
                    AND         IsGroup = 1   
                    AND         B_ID = $B_ID
                    LIMIT       1 ";

    }else if($p_DB == 1){
        $strSQL = " SELECT      TOP 1
                                DocNo
                    FROM        payout
                    WHERE       FORMAT(payout.CreateDate,'dd-MM-yyyy')  = FORMAT( GETDATE(),'dd-MM-yyyy') 
                    AND         DeptID = $d_DeptID
                    AND         IsCancel = 0 
                    /*AND         IsWeb = 0*/
                    AND         IsBorrow = 0 
                    AND         IsGroup = 1   
                    AND         B_ID = $B_ID";
    }


    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_payout_docno = $Result["DocNo"];

        $sql_update_payout = 
        
        "   UPDATE      payout 
            SET         ModifyDate = $date, 
                        IsStatus = 0,
                        Remark = CONCAT(Remark , ', $d_docno')
            WHERE       DocNo = '$d_payout_docno' ";
        
        $meQuery1 = $conn->prepare($sql_update_payout);
        $meQuery1->execute();

    }else{

        // Check Send Sterile DocNo
        if ($d_docno != "") {

            // Gen Payout DocNo
            // $sql_gen_docno =
            //       "SELECT     CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo

            //        FROM       payout

            //        WHERE      DocNo

            //        LIKE       CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')

            //        ORDER BY   DocNo DESC

            //        LIMIT 1 ";

            // $query_gen_docno = mysqli_query($conn, $sql_gen_docno);

            $Doc_B_ID = chr(64+$B_ID);
            if($p_DB == 0){

                $sql_gen_docno = "	SELECT 		CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
                
                                    FROM 		payout

                                    WHERE 		DocNo Like CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                                    ORDER BY 	DocNo DESC
                                    
                                    LIMIT 		1";
        
            }else if($p_DB == 1){
        
                $sql_gen_docno = "SELECT
                        ISNULL(
                            (
                            SELECT TOP
                                1 CONCAT (
                                    'PA',
                                    RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                                    '$Doc_B_ID-',
                                    REPLACE( STR( ( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' ) 
                                ) AS DocNo 
                            FROM
                                payout 
                            ORDER BY
                                payout.DocNo DESC 
                            ),
                            CONCAT (
                                'PA',
                                RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                                '$Doc_B_ID-00001' 
                            ) 
                        ) AS DocNo";
            }


            $query_gen_docno = $conn->prepare($sql_gen_docno);
            $query_gen_docno->execute();

            if ($rs_gen_docno = $query_gen_docno->fetch(PDO::FETCH_ASSOC)) {

                $d_payout_docno = $rs_gen_docno["DocNo"];

                if($p_DB == 0){
                    $date = "NOW()";
                }else if($p_DB == 1){
                    $date = "getDate()";
                }

                // Create Payout
                $sql_insert_payout = " INSERT INTO  payout
                                    (           payout.DocNo,
                                                payout.CreateDate,
                                                payout.ModifyDate,
                                                payout.DeptID,
                                                payout.UserCode,
                                                payout.Qty,
                                                payout.IsStatus,
                                                payout.Remark,
                                                payout.RefDocNo,
                                                payout.IsCheck,
                                                payout.IsGroup,
                                                payout.B_ID
                                    ) VALUES (
                                                '$d_payout_docno',
                                                $date,
                                                $date,
                                                '$d_DeptID',
                                                '$d_UserCode',
                                                '$d_Qty',
                                                0,
                                                'สร้างใบจ่ายจากใบส่งล้าง $d_docno',
                                                '$d_docno',
                                                1,
                                                1,
                                                $B_ID
                                      )";
                $query1 = $conn->prepare($sql_insert_payout);
                $query1->execute();
            }
        }
    }

    // Add Payout Detail
    $sql_send_sterile_detail =
                    "SELECT     itemstock.ItemCode,
                                COALESCE (SUM(sendsteriledetail.Qty), 0) AS Qty

                    FROM        sendsteriledetail

                    INNER JOIN  itemstock
                    ON          sendsteriledetail.ItemStockID=itemstock.RowID 

                    INNER JOIN  item
                    ON          item.itemcode=itemstock.ItemCode

                    WHERE       sendsteriledetail.SendSterileDocNo = '$d_docno'
                    
                    AND         itemstock.IsCancel = 0 
                    AND         sendsteriledetail.B_ID = $B_ID
                    AND         itemstock.B_ID = $B_ID
                    AND         item.IsRecieveRecordOnly = 0 
                    
                    GROUP BY    itemstock.ItemCode ";

                    
    $stmt_send_sterile_detail = $conn->prepare($sql_send_sterile_detail);
    $stmt_send_sterile_detail->execute();

    while ($rs = $stmt_send_sterile_detail->fetch(PDO::FETCH_ASSOC)) {

        $d_itemcode = $rs["ItemCode"];
        $d_qty = $rs["Qty"];
        $d_id = "";

        if($p_DB == 0){
            $top = "";
            $limit = "LIMIT 1";
            $date = "NOW()";
    
        }else if($p_DB == 1){
            $top = "TOP 1";
            $limit = "";
            
            $date = "getDate()";
        }

        $sql_check_payout_detail = 
                "   SELECT      $top
                                ID
                    FROM        payoutdetail
                    WHERE       ItemCode = '$d_itemcode' 
                    AND         DocNo = '$d_payout_docno'
                    AND         B_ID = $B_ID
                    $limit ";

        $stmt_check_payout_detail = $conn->prepare($sql_check_payout_detail);
        $stmt_check_payout_detail->execute();

        if ($rs_check_payout_detail = $stmt_check_payout_detail->fetch(PDO::FETCH_ASSOC)) {
            
            $d_id = $rs_check_payout_detail["ID"];

            $sql_add_payout_detail = 

                    "UPDATE     payoutdetail 
                    SET         Qty = ( Qty + $d_qty ),
                                IsStatus = 0,
				                ModifyTime = $date 
                    WHERE       ID = $d_id ";

        }else{

            $sql_add_payout_detail =
                    "INSERT INTO    payoutdetail (
                        payoutdetail.DocNo,
                        payoutdetail.ItemStockID,
                        payoutdetail.ItemCode,
                        payoutdetail.Qty,
                        payoutdetail.Remark,
                        payoutdetail.IsStatus,
                        payoutdetail.ImportID,
                        payoutdetail.B_ID
                    ) 
                    VALUES
                    (                       
                        '$d_payout_docno',
                        0,
                        '$d_itemcode',
                        $d_qty,
                        '',
                        0,
                        null,
                        $B_ID
                    )";
        }
        
        $query1 = $conn->prepare($sql_add_payout_detail);
        $query1->execute();
    }        

    return $d_payout_docno;
}

//---------------------------------------------
// Create Payout
//---------------------------------------------

function createPayout($conn, $p_docno, $B_ID)
{

    $p_usage_separate_id = $_POST['p_usage_separate_id'];
    $p_usage_separate_id = str_replace(str_split('[]'), '', $p_usage_separate_id);
    $d_array_ID_new = str_replace(' ', '', $p_usage_separate_id);

    $d_payout_docno = "";

    if (isset($_POST["p_is_copy_to_payout"])) {
        $d_is_copy_payout = $_POST['p_is_copy_to_payout'];
    }else{
        $d_is_copy_payout = "1";
    }

    if ( isset($_POST["p_is_separatetosterile"]) ) {
        $p_is_separatetosterile = $_POST['p_is_separatetosterile'];
	}

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    // Get Check Payout

    if($p_is_separatetosterile == 1){
        $d_count_pay = "0";

        $Sql = "SELECT  COUNT(sendsteriledetail.ID) AS qty
                FROM    sendsteriledetail
                WHERE   sendsteriledetail.ID IN ($d_array_ID_new)
                AND     sendsteriledetail.B_ID = $B_ID ";

        $meQuery = mysqli_query($conn, $Sql);

        while ($Result = mysqli_fetch_assoc($meQuery)) {
            $qty = $Result["qty"];
        }
    }else{
        $strSQL = " SELECT      $top
                                COUNT(*) AS count_pay 
                    FROM        payout
                    WHERE       RefDocNo = '$p_docno'
                    AND         B_ID = $B_ID
                    $limit ";

        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();

        $d_count_pay = "0";

        if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $d_count_pay = $Result["count_pay"];
        }
    }

    if ($d_is_copy_payout == "1" && $d_count_pay == "0") {

        // POST Send Sterile Data
        $sql_ss =
          "SELECT   $top 
                    sendsterile.DocNo,
                    sendsterile.DeptID,
                    sendsterile.UserCode,
                    sendsterile.Qty,
                    sendsterile.IsWeb

          FROM      sendsterile

          WHERE     sendsterile.DocNo ='$p_docno'

          AND       sendsterile.B_ID = '$B_ID' 

         $limit ";

        $query1 = $conn->prepare($sql_ss);
        $query1->execute();

        if ($rs1 = $query1->fetch(PDO::FETCH_ASSOC)) {
            $d_docno = $rs1["DocNo"];
            $d_DeptID = $rs1["DeptID"];
            $d_UserCode = $rs1["UserCode"];
            $d_Qty = $rs1["Qty"];
            $d_IsWeb = $rs1["IsWeb"];
        } else {
            $d_docno = "";
            $d_DeptID = "";
            $d_UserCode = "";
            $d_Qty = "";
            $d_IsWeb = "";
        }

        // Check SendSterile Detail To Payout

        $d_count_send_to_pay = 0;

        $sql_condition = "AND item.IsRecieveRecordOnly = 0 ";

        if (isset($_POST["p_project_id"])) {
            $p_project_id = $_POST['p_project_id'];

            if($p_project_id == "214"){
                $sql_condition = "AND item.IsReceiveManual = 1 ";  
            } 
            
        }

        // Check
        $sql_check =
                "	SELECT  	COUNT(*) AS count_send_to_pay 
	
                    FROM        sendsteriledetail 
                        
                    INNER JOIN  itemstock 
                    ON          sendsteriledetail.ItemStockID=itemstock.RowID  
                        
                    INNER JOIN  item 
                    ON          item.itemcode=itemstock.ItemCode 
                        
                    WHERE 		sendsteriledetail.SendSterileDocNo ='$d_docno'
                    AND         sendsteriledetail.B_ID = '$B_ID'

                    $sql_condition 

                    AND         itemstock.IsCancel = 0 ";

        $query_check = $conn->prepare($sql_check);
        $query_check->execute();

        if ($rs_check = $query_check->fetch(PDO::FETCH_ASSOC)) {

            $d_count_send_to_pay = (int)$rs_check["count_send_to_pay"];
        }

        // Check Send Sterile DocNo

        if ($d_docno != "" && $d_count_send_to_pay > 0) {

            // Gen Payout DocNo
            // $sql_gen_docno =
            //       "SELECT     CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo

            //        FROM       payout

            //        WHERE      DocNo

            //        LIKE       CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')

            //        ORDER BY   DocNo DESC

            //        LIMIT 1 ";

            // $query_gen_docno = mysqli_query($conn, $sql_gen_docno);
            $Doc_B_ID = chr(64+$B_ID);
            if($p_DB == 0){

                $sql_gen_docno = "	SELECT 		CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
                
                                    FROM 		payout

                                    WHERE 		DocNo Like CONCAT('PA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                                    ORDER BY 	DocNo DESC
                                    
                                    LIMIT 		1";
        
            }else if($p_DB == 1){
        
                $sql_gen_docno = "SELECT
                        ISNULL(
                            (
                            SELECT TOP
                                1 CONCAT (
                                    'PA',
                                    RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                                    '$Doc_B_ID-',
                                    REPLACE( STR( ( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' ) 
                                ) AS DocNo 
                            FROM
                                payout 
                            ORDER BY
                                payout.DocNo DESC 
                            ),
                            CONCAT (
                                'PA',
                                RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                                '$Doc_B_ID-00001' 
                            ) 
                        ) AS DocNo";
            }
          
            $query_gen_docno = $conn->prepare($sql_gen_docno);
            $query_gen_docno->execute();

            if ($rs_gen_docno = $query_gen_docno->fetch(PDO::FETCH_ASSOC)) {

                $d_payout_docno = $rs_gen_docno["DocNo"];

                if($p_DB == 0){
                    $date = "NOW()";
                }else if($p_DB == 1){
                    $date = "GETDATE()";
                }

                // Create Payout
                $sql_insert_payout = " INSERT INTO  payout
                                    (           payout.DocNo,
                                                payout.CreateDate,
                                                payout.ModifyDate,
                                                payout.DeptID,
                                                payout.UserCode,
                                                payout.Qty,
                                                payout.IsStatus,
                                                payout.Remark,
                                                payout.RefDocNo,
                                                payout.IsCheck,
                                                payout.B_ID
                                    ) VALUES (
                                                '$d_payout_docno',
                                                $date,
                                                $date,
                                                '$d_DeptID',
                                                '$d_UserCode',
                                                '$d_Qty',
                                                0,
                                                'สร้างใบจ่ายจากใบส่งล้าง',
                                                '$d_docno',
                                                1,
                                                $B_ID
                                      )";                        

                $query_gen = $conn->prepare($sql_insert_payout);
                $query_gen->execute();

                // Create Payout Detail
                
                if($p_is_separatetosterile == 1){
                    $sql_insert_payout_detail ="INSERT INTO  payoutdetail (
                                                                payoutdetail.DocNo,
                                                                payoutdetail.ItemStockID,
                                                                payoutdetail.ItemCode,
                                                                payoutdetail.Qty,
                                                                payoutdetail.Remark,
                                                                payoutdetail.IsStatus,
                                                                payoutdetail.ImportID,
                                                                payoutdetail.B_ID
                                                ) SELECT
                                                                '$d_payout_docno',
                                                                0,
                                                                itemstock.ItemCode,
                                                                $qty,
                                                                '',
                                                                0,
                                                                null,
                                                                $B_ID

                                                    FROM        sendsteriledetail

                                                    INNER JOIN  itemstock
                                                    ON          sendsteriledetail.ItemStockID=itemstock.RowID 

                                                    INNER JOIN  item
                                                    ON          item.itemcode=itemstock.ItemCode

                                                    WHERE       sendsteriledetail.SendSterileDocNo = '$d_docno' 

                                                    $sql_condition

                                                    AND         itemstock.IsCancel = 0 
                                                    AND         sendsteriledetail.B_ID = '$B_ID'

                                                    GROUP BY    itemstock.ItemCode";

                    $query_gen1 = $conn->prepare($sql_insert_payout_detail);
                    $query_gen1->execute();
                }else{
                    $sql_insert_payout_detail ="INSERT INTO  payoutdetail (
                                                                payoutdetail.DocNo,
                                                                payoutdetail.ItemStockID,
                                                                payoutdetail.ItemCode,
                                                                payoutdetail.Qty,
                                                                payoutdetail.Remark,
                                                                payoutdetail.IsStatus,
                                                                payoutdetail.ImportID,
                                                                payoutdetail.B_ID
                                                ) SELECT
                                                                '$d_payout_docno',
                                                                0,
                                                                itemstock.ItemCode,
                                                                COALESCE (SUM(sendsteriledetail.Qty), 0) AS Qty,
                                                                '',
                                                                0,
                                                                null,
                                                                $B_ID

                                                    FROM        sendsteriledetail

                                                    INNER JOIN  itemstock
                                                    ON          sendsteriledetail.ItemStockID=itemstock.RowID 

                                                    INNER JOIN  item
                                                    ON          item.itemcode=itemstock.ItemCode

                                                    WHERE       sendsteriledetail.SendSterileDocNo = '$d_docno' 

                                                    $sql_condition

                                                    AND         itemstock.IsCancel = 0 
                                                    AND         sendsteriledetail.B_ID = '$B_ID'

                                                    GROUP BY    itemstock.ItemCode";

                    $query_gen1 = $conn->prepare($sql_insert_payout_detail);
                    $query_gen1->execute();
                }
            }
        }
    }

    return $d_payout_docno;
}

//---------------------------------------------
// Create Occurrence
//---------------------------------------------

function createOccurrence($conn, $p_docno, $B_ID)
{
    $d_occurrence_docno = "";
    
    $p_DB = $_POST['p_DB'];
    
    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
    }

    $strSQL = "SELECT   $top
                        COUNT(*) AS c

              FROM    sendsteriledetail

              WHERE   sendsteriledetail.SendSterileDocNo = '$p_docno'

              AND     sendsteriledetail.OccuranceQty != 0
              AND     sendsteriledetail.B_ID = '$B_ID'

              $limit ";

    $query1 = $conn->prepare($strSQL);
    $query1->execute();

    $c = 0;
    $d_qty = "0";

    if ($rs1 = $query1->fetch(PDO::FETCH_ASSOC)) {
        $c = (int) $rs1["c"];
        $d_qty = $rs1["c"];
    }

    if ($c > 0) {

        // Gen Occurrence DocNo

        // $sql_gen_docno =
        //         "SELECT    CONCAT('OC',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo

        //         FROM      occurance

        //         WHERE     DocNo LIKE CONCAT('OC',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')

        //         ORDER BY  DocNo DESC

        //         LIMIT 1";

        // $query_gen_docno = mysqli_query($conn, $sql_gen_docno);

        $Doc_B_ID = chr(64+$B_ID);
        if($p_DB == 0){

            $sql_gen_docno = "	SELECT 		CONCAT('OC',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
            
                                FROM 		occurance

                                WHERE 		DocNo Like CONCAT('OC',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                                ORDER BY 	DocNo DESC
                                
                                LIMIT 		1";
    
        }else if($p_DB == 1){
    
            $sql_gen_docno = "SELECT
                    ISNULL(
                        (
                        SELECT TOP
                            1 CONCAT (
                                'OC',
                                RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                                '$Doc_B_ID-',
                                REPLACE( STR( ( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' ) 
                            ) AS DocNo 
                        FROM
                            occurance 
                        ORDER BY
                            occurance.DocNo DESC 
                        ),
                        CONCAT (
                            'OC',
                            RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                            '$Doc_B_ID-00001' 
                        ) 
                    ) AS DocNo";
        }

        $query_gen_docno = $conn->prepare($sql_gen_docno);
        $query_gen_docno->execute();

        if ($rs_gen_docno = $query_gen_docno->fetch(PDO::FETCH_ASSOC)) {

            // POST Occurrence DocNo
            $d_occurrence_docno = $rs_gen_docno["DocNo"];

            if($p_DB == 0){
                $top = "";
                $limit = "LIMIT 1";
                $date = "NOW()";
        
            }else if($p_DB == 1){
                $top = "TOP 1";
                $limit = "";
                $date = "getDate()";
            }

            // Create Occurrence
            $sql_insert_occurrence = "INSERT INTO occurance (
                                                DocNo,
                                                DocDate,
                                                ModifyDate,
                                                OccuranceID,
                                                UserCode,
                                                MachineID,
                                                Round,
                                                Remark,
                                                RefDocNo,
                                                DepID,
                                                StepNo,
                                                IsCancel,
                                                B_ID
                                    )
                                    SELECT     $top
                                                '$d_occurrence_docno',
                                                $date,
                                                $date,
                                                COALESCE ((SELECT OccuranceQty FROM sendsteriledetail WHERE sendsteriledetail.SendSterileDocNo = '$p_docno' ORDER BY sendsteriledetail.ID ASC LIMIT 1), 0),
                                                sendsterile.UserCode,
                                                0,
                                                0,
                                                '-',
                                                sendsterile.DocNo,
                                                sendsterile.DeptID,
                                                Null,
                                                0,
                                                sendsterile.B_ID

                                    FROM        sendsterile

                                    WHERE       sendsterile.DocNo = '$p_docno'
                                    AND         sendsterile.B_ID = $B_ID
                                    $limit ";
            
            $result1 = $conn->prepare($sql_insert_occurrence);
            $result1->execute();

            // Create Occurrence Detail
            $sql_insert_occurrence_detail =
                                "   INSERT INTO  occurancedetail (
                                                  DocNo,
                                                  ItemStockID,
                                                  Remark,
                                                  RefDocNo,
                                                  SendSterileDetailID,
                                                  B_ID
                                    )
                                    SELECT        '$d_occurrence_docno',
                                                  sendsteriledetail.ItemStockID,
                                                  '',
                                                  '$p_docno',
                                                  sendsteriledetail.ID,
                                                  sendsteriledetail.B_ID

                                    FROM          sendsteriledetail

                                    WHERE         sendsteriledetail.SendSterileDocNo = '$p_docno'
                                    AND           B_ID = $B_ID

                                    ORDER BY      sendsteriledetail.ID ASC ";

            $result2 = $conn->prepare($sql_insert_occurrence_detail);
            $result2->execute();
        }
    }

    return $d_occurrence_docno;
}

//---------------------------------------------
// Create Wash
//---------------------------------------------

function createWash($conn, $p_docno, $B_ID)
{

    $p_DB = $_POST['p_DB'];

    if (isset($_POST["p_zone_id"])) {
        $p_zone_id = $_POST['p_zone_id'];
    } else {
        $p_zone_id = "0";
    }

    if($p_DB == 0){
        $limit = "LIMIT	1";
        $top = "";
    }else if($p_DB == 1){
        $limit = "";
        $top = "TOP 1";
    }

    // Get Check Has Wash
    $strSQL = " SELECT      $top
                            COUNT(*) AS count_wash 
                FROM        wash
                WHERE       RefDocNo = '$p_docno'
                AND         B_ID = $B_ID
                $limit ";

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    $count_wash = "0";

    if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $count_wash = $Result["count_wash"];

        if($count_wash != "0"){
            return "";
        }
    }

    $d_docno = "";

    // $strSQL = " SELECT 		CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo 
    //             FROM 		wash 
    //             WHERE 		DocNo Like CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
    //             ORDER BY 	DocNo DESC 
    //             LIMIT 1";

    // $meQuery = mysqli_query($conn, $strSQL);
    $Doc_B_ID = chr(64+$B_ID);
    if($p_DB == 0){

        $sql_gen_docno = "	SELECT 		CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
        
                            FROM 		wash

                            WHERE 		DocNo Like CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                            ORDER BY 	DocNo DESC
                            
                            LIMIT 		1";

    }else if($p_DB == 1){

        $sql_gen_docno = "SELECT
                ISNULL(
                    (
                    SELECT TOP
                        1 CONCAT (
                            'WA',
                            RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                            '$Doc_B_ID-',
                            REPLACE( STR( ( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' ) 
                        ) AS DocNo 
                    FROM
                        wash 
                    ORDER BY
                        wash.DocNo DESC 
                    ),
                    CONCAT (
                        'WA',
                        RIGHT ( YEAR ( getDate( ) ), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP ) ), 2 ),
                        '$Doc_B_ID-00001' 
                    ) 
                ) AS DocNo";
    }

    $meQuery = $conn->prepare($sql_gen_docno);
    $meQuery->execute();

    if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
      $d_docno = $Result["DocNo"];

    if($p_DB == 0){
        $date = "NOW()";
        $datenow = "DATE(NOW())";
        $limit = "LIMIT	1";
        $top = "";
    }else if($p_DB == 1){
        $date = "getDate()";
        $datenow = "FORMAT( GETDATE(),'dd-MM-yyyy')";
        $limit = "";
        $top = "TOP 1";
    }

    $p_dept_id = $_POST['p_dept_id'];

      $sql_insert = 
      
      "INSERT INTO wash
            (	
                DocNo,
                DocDate,
                ModifyDate,
                UserCode,
                DeptID,

                Qty,
                IsStatus,
                WashProgramID,
                WashMachineID,
                WashRoundNumber,
                
                StartTime,
                FinishTime,
                Remark,
                RefDocNo,
                B_ID 	
			)
      SELECT    $top
                '$d_docno',
                $date,
                $date,
                sendsterile.UserCode,
                sendsterile.DeptID,
                1,
                1,
                0,
                0,
                0,
                $date,
                $date,
                'create auto $p_docno',
                sendsterile.DocNo,
                sendsterile.B_ID 
                

        FROM    sendsterile

        WHERE   sendsterile.DocNo = '$p_docno'
        AND     sendsterile.B_ID = $B_ID

        $limit ";
			  
        $meQuery1 = $conn->prepare($sql_insert);
        $meQuery1->execute();
      
        $sql_insert_detail = 
      
            "INSERT INTO washdetail 
            (
                          WashDocNo,
                          ItemStockID,
                          Qty,
                          IsStatus,
                          DeptID,

                          IsReSterile,
                          ReSterileTypeID,
                          PackingMatID,
                          ImportID,
                          B_ID,
                          ZoneID 
            ) 
            SELECT        '$d_docno',
                          sendsteriledetail.ItemStockID,
                          1,
                          1,
                          $p_dept_id,

                          0,
                          0,
                          item.PackingMatID,
                          sendsteriledetail.ID,
                          sendsteriledetail.B_ID,
                          $p_zone_id 

            FROM        sendsteriledetail

            INNER JOIN  itemstock 
            ON 		    itemstock.RowID = sendsteriledetail.ItemStockID

            INNER JOIN  item
            ON          item.itemcode=itemstock.ItemCode

            WHERE       sendsteriledetail.SendSterileDocNo = '$p_docno'
            AND         item.IsRecieveRecordOnly = 0 
            AND         sendsteriledetail.B_ID = $B_ID

            ORDER BY    sendsteriledetail.ID ASC ";  

        $meQuery2 = $conn->prepare($sql_insert_detail);
        $meQuery2->execute();

    
    }

    return $d_docno;
}

//---------------------------------------------
// Update SortByUsedCount
//---------------------------------------------

function updateSortByUsedCount($conn, $p_dept_id)
{   
    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    if($p_DB == 0){
        $strSQL = " UPDATE      itemdepartment 
                    SET         UsedCount = (
                                                SELECT 		COUNT(sendsteriledetail.ID) 
                                                FROM 		sendsteriledetail 
                                                LEFT JOIN 	sendsterile   
                                                ON			sendsterile.DocNo = sendsteriledetail.SendSterileDocNo
                                                WHERE 		SUBSTRING(sendsteriledetail.UsageCode, 1, 6) = itemdepartment.itemcode 
                                                AND 		( DATE(sendsterile.DocDate) BETWEEN  DATE( NOW() - INTERVAL 7 day ) AND DATE(NOW())  )
                                                AND 		sendsterile.DeptID = itemdepartment.DeptID 
                                                AND         sendsteriledetail.B_ID = $B_ID          
                                            )
                    WHERE    	itemdepartment.DeptID = $p_dept_id
                    AND         itemdepartment.B_ID = $B_ID ";
    }else if($p_DB == 1){
        $strSQL = " UPDATE itemdepartment 
                    SET UsedCount = (
                        SELECT COUNT
                            ( sendsteriledetail.ID ) 
                        FROM
                            sendsteriledetail
                            LEFT JOIN sendsterile ON sendsterile.DocNo = sendsteriledetail.SendSterileDocNo 
                        WHERE
                            SUBSTRING ( sendsteriledetail.UsageCode, 1, 6 ) = itemdepartment.itemcode 
                            AND ( FORMAT(sendsterile.DocDate, 'dd-MM-yyyy') BETWEEN  GETDATE() - DATEADD(DAY,7,GETDATE())  AND FORMAT ( GETDATE( ), 'dd-MM-yyyy' ) ) 
                            AND sendsterile.DeptID = itemdepartment.DeptID 
                            AND sendsteriledetail.B_ID = $B_ID
                        ) 
                    WHERE
                        itemdepartment.DeptID = $p_dept_id
                    AND itemdepartment.B_ID = $B_ID ";
    }


    $query1 = $conn->prepare($strSQL);
    $query1->execute();
}

//---------------------------------------------
// Update Item Stock
//---------------------------------------------

function updateItemStock($conn, $p_docno, $B_ID)
{

    if (isset($_POST["p_is_usedwash"])) {
        $p_is_usedwash = $_POST['p_is_usedwash'];
    }else{
        $p_is_usedwash = "0";
    }

    $p_DB = $_POST['p_DB'];
    /*
    $strSQL = " UPDATE 		itemstock

                INNER JOIN	sendsteriledetail
                ON 			itemstock.RowID = sendsteriledetail.ItemStockID

                SET			itemstock.IsDispatch = 0,
                            itemstock.IsNew = 0,
                            itemstock.IsNewUsage = 0,
                            itemstock.IsPay = 0,
                            itemstock.IsStatus = 1

                WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                AND			itemstock.IsCancel = 0 ";
    */

    if($p_is_usedwash == "0"){
        
        if($p_DB == 0){

            $strSQL = " UPDATE 		itemstock
                        
                        SET			itemstock.IsDispatch = 0,
                                    itemstock.IsNew = 0,
                                    itemstock.IsNewUsage = 0,
                                    itemstock.IsPay = 0,
                                    itemstock.IsStatus = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN 1 ELSE 3 END ),
                                    itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE NOW() END ),
                                    itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATE_ADD(NOW(), INTERVAL 100 YEAR) END ) 

                        INNER JOIN	sendsteriledetail
                        ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                        INNER JOIN	item 
                        ON 			item.itemcode = itemstock.ItemCode 

                        WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                        AND			sendsteriledetail.IsSelected = 1

                        AND			itemstock.IsCancel = 0 
                        ";

            $query = $conn->prepare($strSQL);
            $query->execute();

        }else if($p_DB == 1){

            $strSQL = " UPDATE 		itemstock

                        SET			itemstock.IsDispatch = 0,
                                    itemstock.IsNew = 0,
                                    itemstock.IsNewUsage = 0,
                                    itemstock.IsPay = 0,
                                    itemstock.IsStatus = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN 1 ELSE 3 END ),
                                    itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE GETDATE() END ),
                                    itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATEADD(YEAR,100,GETDATE()) END ) 
                        
                        FROM        itemstock

                        INNER JOIN	sendsteriledetail
                        ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                        INNER JOIN	item 
                        ON 			item.itemcode = itemstock.ItemCode 

                        WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                        AND			sendsteriledetail.IsSelected = 1

                        AND			itemstock.IsCancel = 0 ";   

            $query = $conn->prepare($strSQL);
            $query->execute();
            
        }

    }else{

        if($p_DB == 0){

            $strSQL = " UPDATE 		itemstock

                        INNER JOIN	sendsteriledetail
                        ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                        INNER JOIN	item 
                        ON 			item.itemcode = itemstock.ItemCode 

                        SET			itemstock.IsDispatch = 0,
                                    itemstock.IsNew = 0,
                                    itemstock.IsNewUsage = 0,
                                    itemstock.IsPay = 0,
                                    itemstock.IsStatus = 0,
                                    itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE NOW() END ),
                                    itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATE_ADD(NOW(), INTERVAL 100 YEAR) END ) 

                        WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                        AND			sendsteriledetail.IsSelected = 1

                        AND			itemstock.IsCancel = 0 ";

        }else if($p_DB == 1){

            $strSQL = " UPDATE 		itemstock

                        SET			itemstock.IsDispatch = 0,
                                    itemstock.IsNew = 0,
                                    itemstock.IsNewUsage = 0,
                                    itemstock.IsPay = 0,
                                    itemstock.IsStatus = 0,
                                    itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE GETDATE() END ),
                                    itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATEADD(YEAR,100,GETDATE()) END ) 
                        
                        FROM        itemstock

                        INNER JOIN	sendsteriledetail
                        ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                        INNER JOIN	item 
                        ON 			item.itemcode = itemstock.ItemCode 

                        WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                        AND			sendsteriledetail.IsSelected = 1

                        AND			itemstock.IsCancel = 0 ";

            $query = $conn->prepare($strSQL);
            $query->execute();

        }
    }
}

// -----------------------------------------------------
// Generate SendSterile DocNo
// -----------------------------------------------------

function getSendSterileDocNo($conn, $p_dept_id, $p_user_code, $B_ID)
{

    // $sql = 
    //     "   SELECT      CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())), 3, 4),LPAD(MONTH(DATE(NOW())), 2, 0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo, 8, 5),UNSIGNED INTEGER)), 0) +1) , 5, 0)) AS DocNo
    //         FROM        sendsterile
    //         WHERE       DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
    //         ORDER BY    DocNo DESC
    //         LIMIT       1 ";

    
    // $meQuery = mysqli_query($conn, $sql);

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];
    $Doc_B_ID = chr(64+$B_ID);
    if($p_DB == 0){

        $sql_gen_docno = "	SELECT 		CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
        
                            FROM 		sendsterile

                            WHERE 		DocNo Like CONCAT('SS','$Doc_B_ID',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'%')
                            ORDER BY 	DocNo DESC
                            
                            LIMIT 		1";

    }else if($p_DB == 1){

        $sql_gen_docno = "SELECT
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

    $meQuery = $conn->prepare($sql_gen_docno);
    $meQuery->execute();

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docno = $row["DocNo"];

        if($p_DB == 0){
            $date = "NOW()";
            $datenow = "DATE(NOW())";
            $limit = "LIMIT	1";
            $top = "";
        }else if($p_DB == 1){
            $date = "getDate()";
            $datenow = "FORMAT( GETDATE(),'dd-MM-yyyy')";
            $limit = "";
            $top = "TOP 1";
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
                                B_ID,
                                IsNonSelectDepartment
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
                                $B_ID,
                                0
                            )";

        $result_insert = $conn->prepare($sql_insert);
        $result_insert->execute();

        return $d_docno;

    } else {
        return null;
    }
}

//---------------------------------------------
// Update SendSterile Detail DocNo
//---------------------------------------------

function updateSendSterileDetailDocNo($conn, $p_docno, $d_docno, $d_dept_id)
{

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $strSQL = " UPDATE 		sendsteriledetail

                    INNER JOIN	itemstock
                    ON 			itemstock.RowID = sendsteriledetail.ItemStockID

                    SET			sendsteriledetail.SendSterileDocNo = '$d_docno' 

                    WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno' 

                    AND         itemstock.DepID = $d_dept_id  
                    AND			itemstock.IsCancel = 0 ";

    }else if($p_DB == 1){

        $strSQL = " UPDATE 		sendsteriledetail

                    SET			sendsteriledetail.SendSterileDocNo = '$d_docno' 

                    FROM        sendsteriledetail

                    INNER JOIN	itemstock
                    ON 			itemstock.RowID = sendsteriledetail.ItemStockID


                    WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno' 

                    AND         itemstock.DepID = $d_dept_id  
                    AND			itemstock.IsCancel = 0 ";
    }

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
}

function deleteSendSterile($conn, $p_docno)
{
    $p_DB = $_POST['p_DB'];

    $strSQL = " DELETE 		
                FROM        sendsterile
                WHERE 		DocNo = '$p_docno' ";

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
}

function getSql($p_docno)
{

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    // Get Department
    $strSQL = " SELECT      itemstock.DepID,
                            sendsterile.B_ID,
                            sendsterile.UserCode 

                FROM        sendsteriledetail 

                INNER JOIN	sendsterile
                ON 			sendsterile.DocNo = sendsteriledetail.SendSterileDocNo

                INNER JOIN	itemstock
                ON 			itemstock.RowID = sendsteriledetail.ItemStockID

                WHERE       sendsteriledetail.SendSterileDocNo = '$p_docno' 

                AND         itemstock.IsCancel = 0 
                AND         sendsteriledetail.B_ID = '$B_ID'

                GROUP BY    itemstock.DepID,
                            sendsterile.B_ID,
                            sendsterile.UserCode ";

    return $strSQL;
}

function createSendSterileNowash($conn, $p_dept_id, $p_user_code, $B_ID, $d_array_ID, $p_docno)
{
    // Get DocNo
    // $sql = 
    //     "   SELECT      CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())), 3, 4),LPAD(MONTH(DATE(NOW())), 2, 0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo, 8, 5),UNSIGNED INTEGER)), 0) +1) , 5, 0)) AS DocNo
    //         FROM        sendsterile
    //         WHERE       DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
    //         ORDER BY    DocNo DESC
    //         LIMIT       1 ";

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];
    $Doc_B_ID = chr(64+$B_ID);

    $p_is_washnotpasstodepartment = $_POST['p_is_washnotpasstodepartment'];

    if($p_DB == 0){

        $sql_gen_docno = "	SELECT 		CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
        
                            FROM 		sendsterile

                            WHERE 		DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,5),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                            ORDER BY 	DocNo DESC
                            
                            LIMIT 		1";

    }else if($p_DB == 1){

        $sql_gen_docno = "SELECT
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

    $meQuery = $conn->prepare($sql_gen_docno);
    $meQuery->execute();


    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docno = $row["DocNo"];

        if($p_DB == 0){
			$date = "NOW()";
		}else if($p_DB == 1){
			$date = "GETDATE()";
		}

        if($p_is_washnotpasstodepartment == '1'){

        }else{
            $sql_insert = " INSERT INTO sendsterile (
                                    DocNo,
                                    DocDate,
                                    ModifyDate,
                                    DeptID,
                                    UserCode,

                                    Qty,
                                    IsCancel,
                                    Remark,
                                    B_ID,
                                    IsNonSelectDepartment,

                                    IsWashDept,
                                    IsStatus
                                )
                            VALUES
                                (   '$d_docno',
                                    $date,
                                    $date,
                                    '2',
                                    '1',

                                    0,
                                    0,
                                    '',
                                    $B_ID,
                                    0,

                                    1,
                                    1
                                )";

            $result_insert = $conn->prepare($sql_insert);
            $result_insert->execute();

            $d_array_ID_new = "";

            for($i = 0 ; $i < sizeof($d_array_ID) ; $i++){

                if($i == 0){
                    $t = "";
                }else{
                    $t = ","; 
                }
                
                $d_array_ID_new .= $t.$d_array_ID[$i];
            }

            $strSQL_Nowash = "  UPDATE 		sendsteriledetail

                                SET			sendsteriledetail.SendSterileDocNo = '$d_docno',
                                            sendsteriledetail.IsStatus = 1
                                
                                WHERE       sendsteriledetail.ItemStockID IN ($d_array_ID_new)
                                
                                AND         sendsteriledetail.SendSterileDocNo = '$p_docno'";

            $result_UPDATE = $conn->prepare($strSQL_Nowash);
            $result_UPDATE->execute();

            if($p_DB == 0){
                $strSQL = " UPDATE 		itemstock

                            INNER JOIN	sendsteriledetail
                            ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                            INNER JOIN	item 
                            ON 			item.itemcode = itemstock.ItemCode 

                            SET			itemstock.IsDispatch = 0,
                                        itemstock.IsNew = 0,
                                        itemstock.IsNewUsage = 0,
                                        itemstock.IsPay = 0,
                                        itemstock.IsStatus = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN 1 ELSE 3 END ),
                                        itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE NOW() END ),
                                        itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATE_ADD(NOW(), INTERVAL 100 YEAR) END ) 

                            WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                            AND			itemstock.IsCancel = 0 ";
            }else if($p_DB == 1){
                $strSQL = " UPDATE 		itemstock

                            INNER JOIN	sendsteriledetail
                            ON 			itemstock.RowID = sendsteriledetail.ItemStockID 

                            INNER JOIN	item 
                            ON 			item.itemcode = itemstock.ItemCode 

                            SET			itemstock.IsDispatch = 0,
                                        itemstock.IsNew = 0,
                                        itemstock.IsNewUsage = 0,
                                        itemstock.IsPay = 0,
                                        itemstock.IsStatus = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN 1 ELSE 3 END ),
                                        itemstock.PackDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.PackDate ELSE GETDATE() END ),
                                        itemstock.ExpireDate = ( CASE WHEN item.IsReceiveNotSterile = 0 THEN itemstock.ExpireDate ELSE DATEADD(YEAR,100,GETDATE()) END ) 

                            WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno'

                            AND			itemstock.IsCancel = 0 ";
            }

            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();

            createWash($conn, $p_docno, $B_ID);

            createPayout($conn, $p_docno, $B_ID);

            //createPayout($conn, $d_docno, $B_ID);

            return $d_docno;
        }

    } 

}

//---------------------------------------------
// MAIN
//---------------------------------------------

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    //---------------------------------------------
    // Local Variable 
    //---------------------------------------------
    $d_payout_docno = "N/A";
    $d_wash_docno = "N/A";
    $d_status = "2";
    $d_message_payout = "";
    $d_message_wash = "";

    //---------------------------------------------
    // POST Variable
    //---------------------------------------------
    if (isset($_POST["DocNo"])) {
        $p_docno = $_POST['DocNo'];
    }

    if (isset($_POST["p_dept_id"])) {
        $p_dept_id = $_POST['p_dept_id'];
    }else{
        $p_dept_id = "0";
    }

    if (isset($_POST["send_id"])) {
        $p_send_id = $_POST['send_id'];
    }

    if (isset($_POST["receive_id"])) {
        $p_receive_id = $_POST['receive_id'];
    }

    if (isset($_POST["p_is_copy_to_payout"])) {
        $p_is_copy_to_payout = $_POST['p_is_copy_to_payout'];
    }else{
        $p_is_copy_to_payout = "1";
    }

    if (isset($_POST["p_is_group_payout"])) {
        $p_is_group_payout = $_POST['p_is_group_payout'];
    }else{
        $p_is_group_payout = "0";
    }

    if (isset($_POST["p_is_approve"])) {
        $p_is_approve = $_POST['p_is_approve'];

        $d_status = ($p_is_approve == "1") ? "1" : "2";

    }else{
        $p_is_approve = "0";
    }

    if (isset($_POST["p_is_usedwash"])) {
        $p_is_usedwash = $_POST['p_is_usedwash'];
    }else{
        $p_is_usedwash = "0";
    }

    $B_ID = $_POST['B_ID'];

    if ( isset($_POST["p_IsSortByUsedCount"]) ) {
        $p_is_update_sort_by_used_count = "1";
	}else{
        $p_is_update_sort_by_used_count = "0";
    }

    if ( isset($_POST["p_usage_nowash_id"]) ) {
        $p_usage_nowash_id = $_POST['p_usage_nowash_id'];
	}

    if ( isset($_POST["p_usage_separate_id"]) ) {
        $p_usage_separate_id = $_POST['p_usage_separate_id'];
	}

    if ( isset($_POST["p_is_usedself_washdepartment"]) ) {
        $p_is_usedself_washdepartment = $_POST['p_is_usedself_washdepartment'];
	}

    if ( isset($_POST["p_is_separatetosterile"]) ) {
        $p_is_separatetosterile = $_POST['p_is_separatetosterile'];
	}

    if ( isset($_POST["p_is_washnotpasstodepartment"]) ) {
        $p_is_washnotpasstodepartment = $_POST['p_is_washnotpasstodepartment'];
	}

    $p_usage_nowash_id = str_replace(str_split('[]'), '', $p_usage_nowash_id);
    $p_usage_nowash_id = str_replace(' ', '', $p_usage_nowash_id);
    $d_array_ID = explode(",", $p_usage_nowash_id);

    $p_usage_separate_id = str_replace(str_split('[]'), '', $p_usage_separate_id);
    $p_usage_separate_id = str_replace(' ', '', $p_usage_separate_id);
    $d_array_ID_separatetosterile = explode(",", $p_usage_separate_id);

    if($p_is_usedself_washdepartment == '1'){
        // Update Send Sterile
        if($d_array_ID != null){
            createSendSterileNowash($conn, $d_dept_id, $d_user_code, $B_ID, $d_array_ID, $p_docno);
        }
    }

    // Check Send Sterile 
    $d_check = checkDocument($conn, $p_docno);

    if($d_check == "1"){
        array_push(
            $resArray,
            array(
                'result' => "E",
                'Message' => ('เอกสารรับอุปกรณ์ที่ '.$p_docno.' ได้ถูกบันทึกไปแล้ว !! ') ,
            )
        );
        

        unset($conn);
        die;

        echo json_encode(array("result" => $resArray));

        return;
    }

    //---------------------------------------------
    // Document NA
    //---------------------------------------------

    $d_prefix_docno = substr($p_docno, 0, 2);

    if($d_prefix_docno == "NA"){

        $meQuery = $conn->prepare(getSql($p_docno));
        $meQuery->execute();


        $d_payout_docno = "";

        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

            $d_dept_id = $Result["DepID"];
            $B_ID = $Result["B_ID"];
            $d_user_code = $Result["UserCode"];

            // Generate DocNo From Department ID
            $d_docno = getSendSterileDocNo($conn, $d_dept_id, $d_user_code, $B_ID);

            // Update DocNo
            updateSendSterileDetailDocNo($conn, $p_docno, $d_docno, $d_dept_id);

            //---------------------------------------------
            // Document
            //---------------------------------------------

            // Update Send Sterile
            updateSendSterile($conn, $d_docno, $p_send_id, $p_receive_id, $d_dept_id, $d_status);
        
            // Update Send Sterile Detail
            updateSendSterileDetail($conn, $d_docno, $d_status);

            // Create Occurrence
            $d_occurrence_docno = createOccurrence($conn, $d_docno, $B_ID);

            // Create Wash
            if($p_is_approve == "0"){

                // Create Payout
                if($p_is_group_payout == "1"){
                    // Copy to payout 1 document per day
                    $d_payout_docno = createGroupPayout($conn, $d_docno, $B_ID);
                }else{
                    // Copy payout by document
                    if($p_is_copy_to_payout == "1" && $d_dept_id != "20"){
                        $d_payout_docno = $d_payout_docno . createPayout($conn, $d_docno, $B_ID) . ", ";
                    }
                }

                if($p_is_usedwash == "0"){
                    $d_wash_docno = createWash($conn, $d_docno, $B_ID);
                }
            
                // Update ItemStock
                if($$p_is_usedself_washdepartment != '1'){
                    updateItemStock($conn, $d_docno, $B_ID);
                }

                //  IsSortByUsedCount Item-Department
                if($p_is_update_sort_by_used_count == "1"){
                    updateSortByUsedCount($conn, $d_dept_id);
                }

                if($p_is_usedwash == "0"){
                    $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปที่โซนฆ่าเชื้อ ";
                }else{
                    $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปที่โซนล้าง ";
                }
            }else{
                $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปอนุมัติเพื่อไปโซนฆ่าเชื้อ ";
            }
        }

        // Message Payout
        if($p_is_copy_to_payout == "1" && $d_dept_id != "20"){
            $d_message_payout = " สร้างเอกสารจ่าย ". $d_payout_docno;
        }

        // Delete Document NA
        deleteSendSterile($conn, $p_docno);
        
        array_push(
            $resArray,
            array(
                'result' => "A",
                'Finish' => 'true',
                'd_payout_docno' => $d_payout_docno,
                'd_occurrence_docno' => $d_occurrence_docno,
                'd_wash_docno' => $d_wash_docno,
                'Message' => ('บันทึกเอกสารรับอุปกรณ์'.$d_message_payout.$d_message_wash.' เรียบร้อย !! ') ,
            )
        );

        echo json_encode(array("result" => $resArray));

        unset($conn);
        die;

        return;
    }

    //---------------------------------------------
    // Document
    //---------------------------------------------

    // Update Send Sterile
    updateSendSterile($conn, $p_docno, $p_send_id, $p_receive_id, $p_dept_id, $d_status);
   
    // Update Send Sterile Detail
    updateSendSterileDetail($conn, $p_docno, $d_status, $d_array_ID_separatetosterile);

    // Create Occurrence
    $d_occurrence_docno = createOccurrence($conn, $p_docno, $B_ID);

    // Create Wash
    if($p_is_approve == "0"){

        // Create Payout
        if($p_is_group_payout == "1"){

            // Copy to payout 1 document per day

            if($p_dept_id == "35"){
                if($p_is_copy_to_payout == "1"){
                    $d_payout_docno = createGroupPayout($conn, $p_docno, $B_ID);
                    $d_message_payout = " สร้างเอกสารจ่าย ".$d_payout_docno;
                }
            }else{
                if($p_is_copy_to_payout == "1" && $p_dept_id != "20"){
                    $d_payout_docno = createPayout($conn, $p_docno, $B_ID);
                    $d_message_payout = " สร้างเอกสารจ่าย ".$d_payout_docno;
                }
            }

        }else{
           
            // Copy payout by document
            
            if($p_is_copy_to_payout == "1" && $p_dept_id != "20"){
                $d_payout_docno = createPayout($conn, $p_docno, $B_ID);
                $d_message_payout = " สร้างเอกสารจ่าย ".$d_payout_docno;
            }
        }

        if($p_is_usedwash == "0"){
            $d_wash_docno = createWash($conn, $p_docno, $B_ID);
        }
      
        // Update ItemStock
        if($$p_is_usedself_washdepartment != '1'){
            updateItemStock($conn, $p_docno, $B_ID);
        }

        //  IsSortByUsedCount Item-Department
        if($p_is_update_sort_by_used_count == "1"){
            updateSortByUsedCount($conn, $p_dept_id);
        }

       
        if($p_is_usedwash == "0"){
            $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปที่โซนฆ่าเชื้อ ";
        }else{
            $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปที่โซนล้าง ";
        }
    }else{
        $d_message_wash = " รายการทั้งหมดได้ถูกส่งต่อไปอนุมัติเพื่อไปโซนฆ่าเชื้อ ";
    }
   
    array_push(
        $resArray,
        array(
            'result' => "A",
            'Finish' => 'true',
            'd_payout_docno' => $d_payout_docno,
            'd_occurrence_docno' => $d_occurrence_docno,
            'd_wash_docno' => $d_wash_docno,
            'Message' => ('บันทึกเอกสารรับอุปกรณ์'.$d_message_payout.$d_message_wash.' เรียบร้อย !! ') ,
        )
    );

} else {
    array_push(
        $resArray,
        array(
            'result' => "I",
            'Finish' => 'fasle',
            'Message' => 'ข้อมูลที่ส่งมาไม่ถูกต้อง!!',
        )
    );
}

echo json_encode(array("result" => $resArray));

unset($conn);
die;

?>