<?php
//EDIT LOG
// 23-01-2026 9.05 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 23-01-2026 15.00 : เลขเอกสาร 'XXYYMM'+'B_ID-'+'#####' (WA2601A-00001)
// 16-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require 'connect.php';
$resArray = array();

// -----------------------------------------------------
// Generate wash DocNo
// -----------------------------------------------------

function getWashDocNo($conn, $p_dept_id, $p_user_code)
{

    $p_DB = $_POST['p_DB'];
    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";
		$d_docdate = "NOW()";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";
        $d_docdate = "GETDATE()";
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

    if($p_DB == 0){

        $sql = "SELECT 	CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,8,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo 
                FROM 		wash 
                WHERE 		DocNo Like CONCAT('WA',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                ORDER BY 	DocNo DESC 
                LIMIT 1";

    }else if($p_DB == 1){
        
        $sql = "SELECT
                ISNULL(
                    (
                    SELECT
                        TOP 1 CONCAT (
                            'WA',
                            RIGHT ( YEAR ( GETDATE ()), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP )), 2 ),
                            '-',
                        REPLACE ( STR (( CAST ( RIGHT ( DocNo, 5 ) AS INT ) + 1 ), 5 ), ' ', '0' )) AS DocNo 
                    FROM
                        wash 
                    WHERE
                        DocNo LIKE CONCAT ( 'WA', RIGHT ( YEAR ( GETDATE ()), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP )), 2 ), '$Doc_B_ID%' ) 
                    ORDER BY
                        wash.DocNo DESC 
                    ),
                CONCAT( 'WA', RIGHT ( YEAR ( GETDATE ()), 2 ) + RIGHT ( '0' + RTRIM( MONTH ( CURRENT_TIMESTAMP )), 2 ), '$Doc_B_ID-00001' ) 
                ) AS DocNo";

    }

    $meQuery = $conn->prepare($sql);
	$meQuery->execute();	

    if ($row = $meQuery->fetch(PDO::FETCH_ASSOC)) {

        $d_docno = $row["DocNo"];

        $sql_insert = " INSERT INTO wash
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
                            RefDocNo
                            $INSERT_B_ID
                        )
                        VALUES
                        (   
                            '$d_docno',
                            $d_docdate,
                            $d_docdate,
                            '$p_user_code',
                            '$p_dept_id',
                                
                            1,
                            1,
                            0,
                            0,
                            0,

                            $d_docdate,
                            $d_docdate,
                            'create by item not reuse',
                            ''
                            $VALUES_B_ID
                        )";

        //echo $sql_insert;

        $result_insert = $conn->prepare($sql_insert);
	    $result_insert->execute();	

        return $d_docno;

    }else{
        return null;
    }
}

// -----------------------------------------------------
// Add wash Detail
// -----------------------------------------------------

function addWashDetail($conn, $p_docno, $p_user_code, $p_item_code, $p_dept_id, $p_qty)
{
    // -----------------------------------------------------
    // Create Item Stock
    // -----------------------------------------------------

    $p_DB = $_POST['p_DB'];
    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

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

            if($p_DB == 0){

                $sql_ = $sql_.
                                "(
                                    '$p_item_code',
                                    $p_dept_id,
                                    1,
                                    -2,
                                    0,

                                    1,
                                    1,
                                    0,
                                    (
                                        SELECT      CONCAT('$p_item_code', '-', DATE_FORMAT(NOW(),'%y'), HEX(MONTH(NOW())), '-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(COALESCE(its.UsageCode, '00000'), 12, 16),UNSIGNED INTEGER)),0) + 1) , 5, 0)) AS UsageCode
                                        FROM        itemstock AS its 
                                        WHERE       its.ItemCode = '$p_item_code'
                                        AND         DATE_FORMAT(CreateDate,'%y') = DATE_FORMAT(NOW(),'%y') 
                                        AND 		its.IsCancel = 0 
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

                                    NOW(),
                                    NOW(),
                                    0
                                    $VALUES_B_ID
                                ),";

            }

            if( $i == ($d_max-1) || ($i>0 && $i%9 == 0) ){
                
                $result_insert = $conn->prepare($sql.substr($sql_, 0, -1));
                $result_insert->execute();	

                $sql_ = "";
            }
        }

    }else{

        $d_max = (int)$p_qty;

        for ($i = 0; $i < $d_max; $i++) { 

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
                                -2,
                                0,

                                1,
                                1,
                                0,
                                (
                                    SELECT COALESCE(  
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
                                        CONCAT(  
                                            '$p_item_code',   
                                            '-',   
                                            FORMAT (GETDATE(), 'yy'),   
                                            FORMAT(CONVERT(INT,FORMAT(GETDATE(), 'MM')), 'X'),   
                                            '-00001'   
                                        )  
                                    )  
                                ),
                                (
                                    SELECT      TOP 1 item.PackingMatID
                                    FROM        item
                                    WHERE       item.itemcode = '$p_item_code'
                                    $AND_B_ID
                                ),

                                GETDATE(),
                                GETDATE(),
                                0
                                $VALUES_B_ID
                            )";

            $result_insert = $conn->prepare($sql);
            $result_insert->execute();	

        }

    }

    // -----------------------------------------------------
    // Create Wash Detail
    // -----------------------------------------------------

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT $p_qty";

    }else if($p_DB == 1){
        
        $top = "TOP $p_qty";
        $limit = " ";

    }

    if (!empty($result_insert)) {

        // -----------------------------------------------------
        // Select Insert ItemStock to Sterile Detail
        // -----------------------------------------------------

        $sql_insert =  "INSERT INTO washdetail ( 
                                    WashDocNo,
                                    ItemStockID,
                                    Qty,
                                    IsStatus,
                                    DeptID,

                                    IsReSterile,
                                    ReSterileTypeID,
                                    PackingMatID,
                                    ImportID
                                    $INSERT_B_ID
                        ) 
                        SELECT      $top
                                    '$p_docno',
                                    itemstock.RowID,
                                    1,
                                    1,
                                    itemstock.DepID,
                                    
                                    0,
                                    0,
                                    itemstock.PackingMatID,
                                    0
                                    $INSERT_B_ID

                        FROM        itemstock 

                        WHERE       itemstock.ItemCode = '$p_item_code' 
                        AND         itemstock.IsNew = 1
                        AND         itemstock.IsNewUsage = 1
                        AND         itemstock.IsStatus = -2 
                        AND         itemstock.IsPay = 0    
                        AND         itemstock.IsDispatch = 0 
                        AND 		itemstock.IsCancel = 0 
                        $AND_B_ID
                        ORDER BY    itemstock.RowID DESC 

                        $limit       ";

        $res_insert = $conn->prepare($sql_insert);
        $res_insert->execute();	

        // -----------------------------------------------------
        // Update Join ItemStock and Sterile Detail
        // -----------------------------------------------------

        if($p_DB == 0){

            $sql_update =  "UPDATE 		itemstock 

                            INNER JOIN 	washdetail 
                            ON 			washdetail.ItemStockID = itemstock.RowID 
                                        
                            SET 		itemstock.IsStatus = 1
                            
                            WHERE 		washdetail.WashDocNo = '$p_docno' 
                            AND         itemstock.IsNew = 1
                            AND         itemstock.IsNewUsage = 1
                            AND         itemstock.IsStatus = -2 
                            AND         itemstock.IsPay = 0    
                            AND         itemstock.IsDispatch = 0 
                            AND 		itemstock.IsCancel = 0 ";
    
        }else if($p_DB == 1){
            
            $sql_update =  "UPDATE 		itemstock 
                        
                            SET 		itemstock.IsStatus = 1

                            FROM        itemstock

                            INNER JOIN 	washdetail 
                            ON 			washdetail.ItemStockID = itemstock.RowID 
                            
                            WHERE 		washdetail.WashDocNo = '$p_docno' 
                            AND         itemstock.IsNew = 1
                            AND         itemstock.IsNewUsage = 1
                            AND         itemstock.IsStatus = -2 
                            AND         itemstock.IsPay = 0    
                            AND         itemstock.IsDispatch = 0 
                            AND 		itemstock.IsCancel = 0 ";
        }

        $res_update = $conn->prepare($sql_update);
        $res_update->execute();	

        return $d_max;
       
    }else{
        return 0;
    }
}

// =====================================================
// Main
// =====================================================

if (isset($_POST["p_user_code"]) && isset($_POST["p_data"]) && isset($_POST["p_dept_id"])) {

    $d_opt = "E";
    $d_complete = 0;

    $p_user_code = $_POST['p_user_code'];
    $p_data = $_POST['p_data'];
    $p_dept_id = $_POST['p_dept_id'];
    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

    }

    $B_ID = $_POST['B_ID'];

    // -----------------------------------------------------
    // Check Document
    // -----------------------------------------------------
    if (isset($_POST["p_docno"]) ) {

        $d_docno = $_POST['p_docno'];

    } else {

        $d_docno = getWashDocNo($conn, $p_dept_id, $p_user_code);

    }

    if($d_docno === null){
        array_push(
            $resArray, array(
                'result' => $d_opt,
                'Message' => "ไม่สามารถสร้างเอกสารส่งฆ่าเชื้อได้ !!",
                'DocNo' => $d_docno,
            )
        );

        echo json_encode(array("result" => $resArray));

        return;
    }

    // -----------------------------------------------------
    // Add Wash Detail
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

            $d_complete += addWashDetail($conn, $d_docno, $p_user_code, $d_item_code, $p_dept_id, $d_qty);
			
		}
    }
    
    // -----------------------------------------------------
    // Result
    // -----------------------------------------------------
    if($d_complete > 0){
        $d_opt = "A";
        $d_message = "สร้างรายการส่งฆ่าเชื้อสำเร็จ ". $d_complete . " รายการ.";
    }else{
        $d_message = "ไม่สามารถสร้างรายการส่งฆ่าเชื้อได้ !!!";
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