<?php
//EDIT LOG
// 23-01-2026 08.53 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 23-01-2026 14.55 : เลขเอกสาร 'XXYYMM'+'B_ID-'+'#####' (SS2601A-00001)
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require 'connect.php';
$resArray = array();

// -----------------------------------------------------
// Generate SendSterile DocNo
// -----------------------------------------------------

function getSendSterileDocNo($conn, $p_dept_id, $p_user_code)
{
	$B_ID = $_POST["B_ID"];
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

    $d_IsNonSelectDepartment = "0";
    $p_DB = $_POST['p_DB'];
    $p_switch_washdep = $_POST['p_switch_washdep'];

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

    // $sql = 
    //     "   SELECT      CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())), 3, 4),LPAD(MONTH(DATE(NOW())), 2, 0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo, 8, 5),UNSIGNED INTEGER)), 0) +1) , 5, 0)) AS DocNo
    //         FROM        sendsterile
    //         WHERE       DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
    //         ORDER BY    DocNo DESC
    //         LIMIT       1 ";

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
                    AND         DATE(DocDate) = DATE(NOW()) 
                    $AND_B_ID
                    $limit ";

        
        $meQuery_ = $conn->prepare($sql_);
        $meQuery_->execute();

        if ($row = $meQuery_->fetch(PDO::FETCH_ASSOC)) {

            $d_docno = $row["DocNo"];

            return $d_docno;
        }

        if($p_DB == 0){
            $sql =  "SELECT         CONCAT('NA/',DATE_FORMAT(NOW(), '%y%m%d')) AS DocNo LIMIT 1 ";
        }else if($p_DB == 1){
            $sql =  "SELECT  TOP 1  CONCAT('NA/',FORMAT( GETDATE(),'yyyy-MM-dd')) AS DocNo ";
        }
        
    }

    $meQuery = $conn->prepare($sql);
    $meQuery->execute();

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docno = $row["DocNo"];

        if($p_DB == 0){
            $date = "NOW()";
        }else if($p_DB == 1){
            $date = "getDate()";
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
                                IsNonSelectDepartment,
                                IsWashDept
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
                                $d_IsNonSelectDepartment,
                                $p_switch_washdep
                                $VALUES_B_ID
                            )";

        $result_insert = $conn->prepare($sql_insert);
        $result_insert->execute();


        return $d_docno;

    } else {
        return null;
    }
}

/*

// -----------------------------------------------------
// Generate SendSterile DocNo
// -----------------------------------------------------

function getSendSterileDocNo($conn, $p_dept_id, $p_user_code, $p_bid)
{

    $sql = "SELECT      CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())), 3, 4),LPAD(MONTH(DATE(NOW())), 2, 0),'-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo, 8, 5),UNSIGNED INTEGER)), 0) +1) , 5, 0)) AS DocNo
            FROM        sendsterile
            WHERE       DocNo Like CONCAT('SS',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'%')
            ORDER BY    DocNo DESC
            LIMIT       1 ";

    $meQuery = mysqli_query($conn, $sql);

    if ($row = mysqli_fetch_assoc($meQuery)) {

        $d_docno = $row["DocNo"];

        $sql_insert = "  INSERT INTO sendsterile (
                                DocNo,
                                DocDate,
                                ModifyDate,
                                DeptID,
                                UserCode,
                                Qty,
                                IsCancel,
                                Remark,
                                B_ID
                            )
                        VALUES
                            (   '$d_docno',
                                NOW(),
                                NOW(),
                                '$p_dept_id',
                                '$p_user_code',
                                0,
                                0,
                                '',
                                $p_bid
                            )";

        $result_insert = mysqli_query($conn, $sql_insert);

        return $d_docno;

    }else{
        return null;
    }
}
*/

// -----------------------------------------------------
// Add SendSterile Detail
// -----------------------------------------------------

function addSendSterileDetail($conn, $p_docno, $p_user_code, $p_item_code, $p_dept_id, $p_qty)
{
    // -----------------------------------------------------
    // Create Item Stock
    // -----------------------------------------------------

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){
        $date = "NOW()";

        $top = "";
		$limit = "LIMIT 1";
    }else if($p_DB == 1){
        $date = "GETDate()";

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


    if($p_DB == 0){
        $sql = "INSERT INTO itemstock(
                            ItemCode,
                            DepID,
                            Qty,
                            IsStatus,
                            IsPay,

                            IsNew,
                            IsNewUsage,
                            IsDispatch,
                            UsageCode,
                            PackingMatID,

                            CreateDate,
                            LastSendDeptDate,
                            IsCancel
                            $INSERT_B_ID
                        )  
                        VALUES ";
                
                $d_max = (int)$p_qty;

                $sql_ = "";

                for ($i = 0; $i < $d_max; $i = $i + 1) { 

                    $sql_ = $sql_.
                        "(
                            '$p_item_code',
                            $p_dept_id,
                            1,
                            -1,
                            0,

                            1,
                            1,
                            0,
                            (
                                SELECT      CONCAT('$p_item_code', '-', DATE_FORMAT(NOW(),'%y'), HEX(MONTH(NOW())), '-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(COALESCE(its.UsageCode, '00001'), 12, 16),UNSIGNED INTEGER)),0) + 1) , 5, 0)) AS UsageCode
                                FROM        itemstock AS its 
                                WHERE       its.ItemCode = '$p_item_code'
                                AND         DATE_FORMAT(CreateDate,'%y') = DATE_FORMAT(NOW(),'%y')
                                $AND_B_ID
                                ORDER BY    its.UsageCode DESC
                                LIMIT       1
                            ),
                            (
                                SELECT      item.PackingMatID
                                FROM        item
                                WHERE       item.itemcode = '$p_item_code'
                                $AND_B_ID
                                LIMIT       1
                            ),

                            $date ,
                            $date ,
                            0
                            $VALUES_B_ID
                        ),";

                    /*
                    if($i != ($d_max-1)){
                        $sql_ = $sql_.",";
                    }
                    */

                    if( $i == ($d_max-1) || ($i>0 && $i%9 == 0) ){
                        $result_insert = mysqli_query($conn, $sql.substr($sql_, 0, -1));
                        $sql_ = "";
                    }
                }
    }else if($p_DB == 1){
        $d_max = (int)$p_qty;

        $sql_ = "";

        for ($i = 0; $i < $d_max; $i = $i + 1) { 
            $usageCodeQ = " SELECT COALESCE(  
                            (  
                                    SELECT		TOP 1   
                                                CONCAT(  
                                                                its.ItemCode,
                                                                '-',
                                                                FORMAT (GETDATE(), 'yy'),
                                                                FORMAT(CONVERT(INT,FORMAT(GETDATE(), 'MM')), 'X'),
                                                                '-',
                                                                RIGHT(REPLICATE('0', 5) +  CONVERT(VARCHAR, CONVERT( INT, SUBSTRING(COALESCE(its.UsageCode, '0'), 12, 16 ) ) + 1 ) , 5) 
                                                            )  
                                    FROM        itemstock AS its   
                                    WHERE       its.ItemCode = '$p_item_code'   
                                    AND         FORMAT (its.CreateDate, 'yy') = FORMAT (GETDATE(), 'yy')   
                                    $AND_B_ID
                                    ORDER BY    its.UsageCode DESC  
                                ),
                                CONCAT('$p_item_code','-',FORMAT (GETDATE(), 'yy'),FORMAT(CONVERT(INT,FORMAT(GETDATE(), 'MM')), 'X'),'-00001')
                            )    AS usageCode";

            $query_usageCode = $conn->prepare($usageCodeQ);
            $query_usageCode->execute();

            while ($rs_usageCode = $query_usageCode->fetch(PDO::FETCH_ASSOC)){
               
                $usageCode = $rs_usageCode['usageCode'];
            
            }     

            $sql = "INSERT INTO itemstock(
                ItemCode,
                DepID,
                Qty,
                IsStatus,
                IsPay,

                IsNew,
                IsNewUsage,
                IsDispatch,
                UsageCode,
                PackingMatID,

                CreateDate,
                LastSendDeptDate,
                IsCancel
                $INSERT_B_ID
            )  
            VALUES (
                    '$p_item_code',
                    $p_dept_id,
                    1,
                    -1,
                    0,

                    1,
                    1,
                    0,
                    (SELECT COALESCE(  
                    (  
                            SELECT		TOP 1   
                                        CONCAT(  
                                                        its.ItemCode,
                                                        '-',
                                                        FORMAT (GETDATE(), 'yy'),
                                                        FORMAT(CONVERT(INT,FORMAT(GETDATE(), 'MM')), 'X'),
                                                        '-',
                                                        RIGHT(REPLICATE('0', 5) +  CONVERT(VARCHAR, CONVERT( INT, SUBSTRING(COALESCE(its.UsageCode, '0'), 12, 16 ) ) + 1 ) , 5) 
                                                    )  
                            FROM        itemstock AS its   
                            WHERE       its.ItemCode = '$p_item_code'   
                            AND         FORMAT (its.CreateDate, 'yy') = FORMAT (GETDATE(), 'yy')   
                            $AND_B_ID
                            ORDER BY    its.UsageCode DESC  
                        ),
                        CONCAT('$p_item_code','-',FORMAT (GETDATE(), 'yy'),FORMAT(CONVERT(INT,FORMAT(GETDATE(), 'MM')), 'X'),'-00001')
                    )    AS usageCode),
                    (
                        SELECT      TOP 1
                                    item.PackingMatID
                        FROM        item
                        WHERE       item.itemcode = '$p_item_code'
                        $AND_B_ID
                    ),

                    GETDATE(),
                    GETDATE(),
                    0 
                    $VALUES_B_ID)";

            $result_insert = $conn->prepare($sql);
            $result_insert->execute();

        }


    }



    //echo $sql.$sql_;

    //$result_insert = mysqli_query($conn, $sql.$sql_);

    // -----------------------------------------------------
    // Create Send Sterile Detail
    // -----------------------------------------------------

    if (isset($_POST["p_is_new_department"])) {
        $p_is_new_department = $_POST['p_is_new_department'];
    } else {
        $p_is_new_department = "0";
    }

    if (!empty($result_insert)) {
        if (!empty($result_insert)) {
           
            // -----------------------------------------------------
            // Select Insert ItemStock to Sterile Detail
            // -----------------------------------------------------

            if($p_DB == 0){
                $top = "";
                $limit = "LIMIT $p_qty ";
            }else if($p_DB == 1){
                $top = "TOP $p_qty ";
                $limit = "";
            }

            $sql_insert =  "INSERT INTO sendsteriledetail ( 
                                        sendsteriledetail.SendSterileDocNo,
                                        sendsteriledetail.ItemStockID,
                                        sendsteriledetail.Qty,
                                        sendsteriledetail.Remark,
                                        sendsteriledetail.UsageCode,
                                        sendsteriledetail.IsSterile,
                                        sendsteriledetail.IsStatus,
                                        sendsteriledetail.IsNewDepartment 
                                        $INSERT_B_ID
                            ) 
                            SELECT      $top
                                        '$p_docno',
                                        itemstock.RowID,
                                        1,
                                        '',
                                        itemstock.UsageCode,
                                        0,
                                        0,
                                        $p_is_new_department
                                        $INSERT_B_ID  

                            FROM        itemstock 

                            WHERE       itemstock.ItemCode = '$p_item_code' 
                            AND         itemstock.IsNew = 1
                            AND         itemstock.IsNewUsage = 1
                            AND         itemstock.IsStatus = -1 
                            AND         itemstock.IsPay = 0    
                            AND         itemstock.IsDispatch = 0 
                            $AND_B_ID
                            ORDER BY    itemstock.RowID DESC 

                            $limit";

            // echo $sql_insert;

            //echo "<br/>";
            $query_sql_insert = $conn->prepare($sql_insert);
            $query_sql_insert->execute();


            // -----------------------------------------------------
            // Update Join ItemStock and Sterile Detail
            // -----------------------------------------------------

            if($p_DB == 0){
                $sql_update =  "UPDATE 		itemstock 

                                INNER JOIN 	sendsteriledetail 
                                ON 			sendsteriledetail.ItemStockID = itemstock.RowID 
                                
                                SET 		itemstock.IsStatus = 0
                                
                                WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno' 
                                AND         itemstock.IsNew = 1
                                AND         itemstock.IsNewUsage = 1
                                AND         itemstock.IsStatus = -1 
                                AND         itemstock.IsPay = 0    
                                AND         itemstock.IsDispatch = 0 
                                AND 		itemstock.IsCancel = 0 ";
            }else if($p_DB == 1){
                $sql_update =  "UPDATE 		itemstock 

                                SET 		itemstock.IsStatus = 0

                                FROM        itemstock

                                INNER JOIN 	sendsteriledetail 
                                ON 			sendsteriledetail.ItemStockID = itemstock.RowID 
                                
                                WHERE 		sendsteriledetail.SendSterileDocNo = '$p_docno' 
                                AND         itemstock.IsNew = 1
                                AND         itemstock.IsNewUsage = 1
                                AND         itemstock.IsStatus = -1 
                                AND         itemstock.IsPay = 0    
                                AND         itemstock.IsDispatch = 0 
                                AND 		itemstock.IsCancel = 0 ";
            }




            $query_sql_update = $conn->prepare($sql_update);
            $query_sql_update->execute();

            return $d_max;

        }else{
            return 0;
        }
    }else{
        return 0;
    }
}

function getSql($p_docno)
{
    $B_ID = $_POST["B_ID"];
	$AND_B_ID = "";
	if($B_ID != "0"){
		$AND_B_ID = " AND sendsteriledetail.B_ID = $B_ID";
	}
	$strSQL = 	"	SELECT 		itemstock.DepID, 
                                itemstock.ItemCode, 
                                COUNT(sendsteriledetail.ID) AS Qty  

					FROM 		sendsteriledetail 

					INNER JOIN 	itemstock 
                    ON 			sendsteriledetail.ItemStockID = itemstock.RowID

					WHERE		sendsteriledetail.SendSterileDocNo = '$p_docno' 
                    AND 		sendsteriledetail.IsNewDepartment = 1 
                    $AND_B_ID 

					GROUP BY 	itemstock.DepID, 
                                itemstock.ItemCode " ;

    //echo $strSQL;

	return $strSQL;
}

// -----------------------------------------------------
// Update itemstock_department
// -----------------------------------------------------

function updateItemstockDepartment($conn, $d_itemcode, $d_dept_id, $d_qty, $d_ref_docno)
{
    // -----------------------------------------------------
	// Check itemstock_department
	// -----------------------------------------------------
    $p_DB = $_POST['p_DB'];
    if($p_DB == 0){
        $top = "";
        $limit = "LIMIT 1";
        $date = "NOW()";
    }else if($p_DB == 1){
        $top = "TOP 1";
        $limit = "";
        $date = "getDate()";
    }

    $B_ID = $_POST["B_ID"];
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

	$strSQL = "	SELECT		 $top
                            itemstock_department.RowID 

                FROM 		itemstock_department

                WHERE		itemstock_department.ItemCode = '$d_itemcode' 

                AND			itemstock_department.DeptID = $d_dept_id 
                $AND_B_ID

                $limit " ; 

    //echo $strSQL;
    $result = $conn->prepare($strSQL);
    $result->execute();

    if (!empty($result)) {

    }else{
        if($p_DB == 0){
            $date = "NOW()";
        }else if($p_DB == 1){
            $date = "getDate()";
        }


        $sql_insert = "INSERT INTO 	itemstock_department
                        (
                            ItemCode,
                            DeptID,
                            Qty,
                            ModifyDate
                            $INSERT_B_ID
                        )
                    VALUES 
                        (
                            '$d_itemcode',
                            $d_dept_id,
                            0,
                            $date
                            $VALUES_B_ID
                        )" ;

        //echo $sql_insert;
        $result_insert = $conn->prepare($sql_insert);
        $result_insert->execute();
    }

	// -----------------------------------------------------
	// Update itemstock_department
	// -----------------------------------------------------

	$sql_update = " UPDATE 			itemstock_department

					SET 			Qty = (Qty - $d_qty),
									ModifyDate = $date 

					WHERE			itemstock_department.ItemCode = '$d_itemcode' 
					AND				itemstock_department.DeptID = $d_dept_id " ;
 
    $result_update = $conn->prepare($sql_update);
    $result_update->execute();
	//echo $sql_update;

	$sql_insert = "INSERT INTO 	itemstock_department_log
						(
							    ItemStockDept_RowID,
							    Qty,
							    CreateDate,
							    RefDocNo
                                $INSERT_B_ID
						)
					SELECT	
                                $top
                            	itemstock_department.RowID,
								($d_qty * (-1)),
								$date,
								'$d_ref_docno'
                                $INSERT_B_ID

					FROM 		itemstock_department

					WHERE		itemstock_department.ItemCode = '$d_itemcode' 
					AND			itemstock_department.DeptID = $d_dept_id 
                    $AND_B_ID

					$limit " ;

	//echo $sql_insert;
    $result_insert = $conn->prepare($sql_insert);
    $result_insert->execute();

	return $d_qty;

}

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_user_code"]) && isset($_POST["p_data"]) && isset($_POST["p_dept_id"])) {

    $d_opt = "E";
    $d_complete = 0;

    $p_user_code = $_POST['p_user_code'];
    $p_data = $_POST['p_data'];

    // Department Document
    $p_dept_id = $_POST['p_dept_id'];

    $B_ID = $_POST['B_ID'];

    // Department NA Select Form
    if (isset($_POST["p_dept_id_select"])) {
        $p_dept_id_select = $_POST['p_dept_id_select'];
    } else {
        $p_dept_id_select = $p_dept_id;
    }

    // New Item Department Select Form
    if (isset($_POST["p_is_new_department"])) {
        $p_is_new_department = $_POST['p_is_new_department'];
    } else {
        $p_is_new_department = "0";
    }

    $p_DB = $_POST['p_DB'];


    // -----------------------------------------------------
    // Check Document
    // -----------------------------------------------------
    if (isset($_POST["p_docno"]) ) {

        $d_docno = $_POST['p_docno'];

    } else {

        $d_docno = getSendSterileDocNo($conn, $p_dept_id, $p_user_code);

    }

    if($d_docno === null){
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
    $d_array_data = explode(",", $p_data);
	$d_data = 2;
	$d_max = sizeof($d_array_data);

	if ($d_max > 0) {

		for ($i = 0; $i < $d_max; $i = $i + $d_data) {

			if ($d_array_data[$i] == '') {
				break;
			}

            $d_item_code = $d_array_data[$i];
            $d_qty = $d_array_data[$i+1];

            $d_complete += addSendSterileDetail($conn, $d_docno, $p_user_code, $d_item_code, $p_dept_id_select, $d_qty);
			
		}
    }

    // ---------------------------------------------
	// Update item department
	// ---------------------------------------------
	if ( isset($_POST["p_is_used_itemstock_department"]) && $p_is_new_department == "0") {
        
        $result = $conn->prepare(getSql($d_docno));
        $result->execute();

		if (!empty($result)) {

			while ($row = $result->fetch(PDO::FETCH_ASSOC)) {

				$d_itemcode = $row["ItemCode"];
				$d_dept_id = $row["DepID"];
				$d_qty = $row["Qty"];

				if($d_itemcode === null){
					break;
				}

				$d_return = updateItemstockDepartment($conn, $d_itemcode, $d_dept_id, $d_qty, $d_docno);
	
			}
		}  	
	}
    
    // -----------------------------------------------------
    // Result
    // -----------------------------------------------------
    if($d_complete > 0){
        $d_opt = "A";
        $d_message = "สร้างรายการส่งล้างสำเร็จ ". $d_complete . " รายการ.";
    }else{
        $d_message = "ไม่สามารถสร้างรายการส่งล้างได้ !!!";
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