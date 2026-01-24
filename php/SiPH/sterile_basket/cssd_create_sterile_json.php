<?php
//EDIT LOG
// 22-01-2026 16.03 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 23-01-2026 14.24 : แก้ไขเพิ่ม Building_ID (B_ID) เลขเอกสาร 'XXYYMM'+'B_ID-'+'#####' (S2601A-0001)
require '../connect.php';
$resArray = array();

// =======================================================
// Add Sterile Detail By Select For Insert (1)
// =======================================================

function getSql($p_qty){

    $p_data = $_POST['p_data'];

    //--------------------------------------------------------------------
    //  0 = mysqli , 1 = sqlserver
    //--------------------------------------------------------------------

    $B_ID = $_POST['B_ID'];
    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT $p_qty";

    }else if($p_DB == 1){
        
        $top = "TOP $p_qty";
        $limit = " ";

    }

    $p_PackingMatID = "";

    $sql = "SELECT 	
                        washdetail.ID,
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
            AND         washdetail.B_ID = $B_ID
            AND         wash.B_ID = $B_ID
            AND         itemstock.B_ID = $B_ID
             ";

    // Status
    if (isset($_POST["p_is_status"])) {

        $p_is_status = $_POST['p_is_status'];

        $sql = $sql . "AND 	washdetail.IsStatus = $p_is_status ";

        if ($p_is_status == "1") {

            $sql = $sql . "AND  wash.IsStatus = 1  ";

        }

    } else {
        $sql = $sql . "AND 	washdetail.IsStatus = 1 ";
        $sql = $sql . "AND	wash.IsStatus = 1 ";
    }

    // Packing ID
    if (isset($_POST["p_PackingMatID"])) {

        $p_PackingMatID = $_POST['p_PackingMatID'];

        $sql = $sql . "AND 	washdetail.PackingMatID = $p_PackingMatID ";
    }

    // List Itemcode

    $d_array_data = explode("@", $p_data);
    $d_data = 1;
    $d_max = sizeof($d_array_data);

    if ($d_max > 0) {

        $sql = $sql . "AND ( ";

        for ($i = 0; $i < $d_max; $i = $i + $d_data) {
            if ($d_array_data[$i] == '') {
                break;
            }

            if ($i > 0) {
                $sql = $sql . " OR  ";
            }

            $sql = $sql . "itemstock.ItemCode = '" . $d_array_data[$i] . "' ";
        }

        $sql = $sql . ") ";
    }

    // $sql = $sql . "ORDER BY itemstock.RowID ASC ";

    if ($p_DB == 0){

        if ($p_qty > 0) {
            $sql = $sql . "LIMIT $p_qty";
        }

    }
   
    return $sql;
}

function getSqlDocNo($p_qty){

    $p_data = $_POST['p_data'];

    $B_ID = $_POST['B_ID'];
    $p_DB = $_POST['p_DB'];

    //--------------------------------------------------------------------
    //  0 = mysqli , 1 = sqlserver
    //--------------------------------------------------------------------

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT $p_qty";

    }else if($p_DB == 1){
        
        $top = "TOP $p_qty";
        $limit = " ";

    }

    $p_PackingMatID = "";

    $sql = "SELECT		washdetail.WashDocNo

			FROM 		washdetail

			LEFT JOIN 	wash
			ON			wash.DocNo = washdetail.WashDocNo

			LEFT JOIN 	itemstock
			ON			itemstock.RowID = washdetail.ItemStockID

			LEFT JOIN 	packingmat
            ON			packingmat.ID = washdetail.PackingMatID 
            
            WHERE 		itemstock.IsStatus = 1 
		    AND			itemstock.IsCancel = 0 
            AND         washdetail.B_ID = $B_ID
            AND         wash.B_ID = $B_ID
            AND         itemstock.B_ID = $B_ID
             ";

    if (isset($_POST["p_is_status"])) {

        $p_is_status = $_POST['p_is_status'];

        $sql = $sql . "AND 	washdetail.IsStatus = $p_is_status ";

        if ($p_is_status == "1") {

            $sql = $sql . "AND  wash.IsStatus = 1  ";

        }

    } else {
        $sql = $sql . "AND 	washdetail.IsStatus = 1 ";
        $sql = $sql . "AND	wash.IsStatus = 1 ";
    }

    if (isset($_POST["p_PackingMatID"])) {

        $p_PackingMatID = $_POST['p_PackingMatID'];

        $sql = $sql . "AND 		washdetail.PackingMatID = $p_PackingMatID ";
    }

    $d_array_data = explode("@", $p_data);
    $d_data = 1;
    $d_max = sizeof($d_array_data);

    if ($d_max > 0) {

        $sql = $sql . "AND ( ";

        for ($i = 0; $i < $d_max; $i = $i + $d_data) {
            if ($d_array_data[$i] == '') {
                break;
            }

            if ($i > 0) {
                $sql = $sql . " OR  ";
            }

            $sql = $sql . "itemstock.ItemCode = '" . $d_array_data[$i] . "' ";
        }

        $sql = $sql . ") ";
    }

    $sql = $sql . "GROUP BY	washdetail.WashDocNo ";

    if ($p_DB == 0){

        if ($p_qty > 0) {
            $sql = $sql . "LIMIT $p_qty";
        }    

    }
    
    return $sql;
}

function getSqlCondition(){
    $sql = "";

    $p_data = $_POST['p_data'];

    $p_PackingMatID = "";

    $p_DB = $_POST['p_DB'];

    if (isset($_POST["B_ID"])) {

        $B_ID = $_POST["B_ID"];

        $sql = $sql . "AND	 washdetail.B_ID = $B_ID ";
    }

    if (isset($_POST["p_PackingMatID"])) {

        $p_PackingMatID = $_POST['p_PackingMatID'];

        $sql = $sql . "AND 		washdetail.PackingMatID = $p_PackingMatID ";
    }
    

    $d_array_data = explode("@", $p_data);
    $d_data = 1;
    $d_max = sizeof($d_array_data);

    if ($d_max > 0) {

        $sql = $sql . "AND ( ";

        for ($i = 0; $i < $d_max; $i = $i + $d_data) {
            if ($d_array_data[$i] == '') {
                break;
            }

            if ($i > 0) {
                $sql = $sql . " OR  ";
            }

            $sql = $sql . "itemstock.ItemCode = '" . $d_array_data[$i] . "' ";
        }

        $sql = $sql . ") ";
    }

    return $sql;

}

function getSterileData($conn, $p_docno){

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

        $date = "DocDate";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

        $date = "FORMAT(sterile.DocDate, 'yyyy-MM-dd') AS DocDate";

    }

    $sql_doc_date ="SELECT 	$top
                            $date ,
							PrepareCode,
							ApproveCode,
							SterileCode,
							PackingCode

					FROM 	sterile

					WHERE	DocNo = '$p_docno'

					$limit";

    $result = $conn->prepare($sql_doc_date);
	$result->execute();

    if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $DocDate = $row["DocNo"];
        $d_doc_date = "'" . $row["DocDate"] . "'," . $row["PrepareCode"] . "," . $row["ApproveCode"] . "," . $row["SterileCode"] . "," . $row["PackingCode"];
    } else {
        $d_doc_date = "NOW(),0,0,0,0";
    }

    return $d_doc_date;
}

// -------------------------------------------------------
// Add Sterile Detail By Select For Insert
// -------------------------------------------------------

function addSterileDetail($conn, $p_docno){
    $sql_condition = "";

    $d_doc_date = "NOW()";
    $p_prepare_code = "0";
    $p_approve_code = "0";
    $p_sterile_code = "0";
    $p_packing_code = "0";

    $p_qty = 0;
    $p_is_status = "1";
    $B_ID = "1";

    $p_DB = $_POST['p_DB'];
    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

    }

    if (isset($_POST["B_ID"])) {
        $B_ID = $_POST["B_ID"];
    }

    if (isset($_POST["p_is_status"])) {
        $p_is_status = $_POST['p_is_status'];
    }

    if (isset($_POST["p_qty"])) {
        $p_qty = $_POST["p_qty"];
    }

    $sql_select_wash_item = getSql($p_qty);
    $sql_condition = getSqlCondition();

    // -------------------------------------------------------
    // POST WashDetail (ID, RowId)
    // -------------------------------------------------------

    if ($p_qty > 0) {

        $sql_list_condition_row_id = "";
        $sql_list_condition_id = "";

        $meQuery = $conn->prepare($sql_select_wash_item);
        $meQuery->execute();

        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $sql_list_condition_id .= $Result['ID'] . ",";
            $sql_list_condition_row_id .= $Result['ItemStockID'] . ",";
        }

        if ($sql_list_condition_row_id == "" || $sql_list_condition_id == "") {
            
            array_push(
                $resArray,
                array(
                    'result' => "E",
                )
            );

            return;

        }

        $sql_list_condition_id = "(" . substr($sql_list_condition_id, 0, -1) . ")";

        $sql_list_condition_row_id = "(" . substr($sql_list_condition_row_id, 0, -1) . ")";

    }

    // -------------------------------------------------------
    // Insert steriledetail
    // -------------------------------------------------------

    // POST docdate
    $d_sterile_data = getSterileData($conn, $p_docno);

    $d_array_data = explode(",", $d_sterile_data);

    $d_doc_date = $d_array_data[0];
    $p_prepare_code = $d_array_data[1];
    $p_approve_code = $d_array_data[2];
    $p_sterile_code = $d_array_data[3];
    $p_packing_code = $d_array_data[4];

    if (isset($_POST["p_PrepareCode"])) {
        $d_prepare_code = $_POST["p_PrepareCode"];

        if ($d_prepare_code != "0") {
            $p_prepare_code = $d_prepare_code;
        }

    }

    if (isset($_POST["p_ApproveCode"])) {
        $d_approve_code = $_POST["p_ApproveCode"];

        if ($d_approve_code != "0") {
            $p_approve_code = $d_approve_code;
        }
    }

    if (isset($_POST["p_SterileCode"])) {
        $d_sterile_code = $_POST["p_SterileCode"];

        if ($d_sterile_code != "0") {
            $p_sterile_code = $d_sterile_code;
        }
    }

    if (isset($_POST["p_PackingCode"])) {
        $d_packing_code = $_POST["p_PackingCode"];

        if ($d_packing_code != "0") {
            $p_packing_code = $d_packing_code;
        }
    }

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $sql_insert =  "INSERT INTO steriledetail (
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
                                        $d_doc_date,
                                        $p_prepare_code,
                                        $p_approve_code,
                                        $p_sterile_code,
                                        $p_packing_code,
                                        ($d_doc_date + INTERVAL s.Shelflife DAY + INTERVAL -1 DAY ),
                                        s.PackingMatID,
                                        $B_ID
                        FROM  	(
                                    $sql_select_wash_item
                                ) AS s ";

        $res_insert = $conn->prepare($sql_insert);
        $res_insert->execute();

    }else if($p_DB == 1){
        
        $sql_insert =  "INSERT INTO steriledetail (
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
                                        $d_doc_date,
                                        $p_prepare_code,
                                        $p_approve_code,
                                        $p_sterile_code,
                                        $p_packing_code,
                                        DATEADD(DAY, s.Shelflife - 1, $d_doc_date),
                                        s.PackingMatID,
                                        $B_ID
                        FROM  	(
                                    $sql_select_wash_item
                                ) AS s ";

        $res_insert = $conn->prepare($sql_insert);
        $res_insert->execute();

    }

    // -------------------------------------------------------
    // Create Item Stock Log
    // -------------------------------------------------------

    //--------------------------------------------------------------------
    //  0 = mysqli , 1 = sqlserver
    //--------------------------------------------------------------------

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

        $date = " NOW() ";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

        $date = " GETDATE() ";

    }

    $sql_insert_stock_log = "   INSERT INTO itemstocklog (
                                    ItemStockID,
                                    CreateDate,
                                    SterileDetailID,
                                    B_ID
                                )
                                SELECT 		s.ItemStockID,
                                            $date,
                                            (   SELECT      $top
                                                            ID 
                                                FROM        steriledetail 
                                                WHERE       ItemStockID = s.ItemStockID 
                                                AND         steriledetail.B_ID = $B_ID
                                                ORDER BY ID DESC 
                                                $limit ),
                                            $B_ID
                                FROM  	(
                                            $sql_select_wash_item
                                        ) AS s ";
   
    $meQuery = $conn->prepare($sql_insert_stock_log);
    $meQuery->execute();

    // -------------------------------------------------------
    // Update Item Stock
    // -------------------------------------------------------

    if ($p_qty == 0) {

        if($p_DB == 0){
            $sql_update_item_stock ="   UPDATE		itemstock

                                        SET 		itemstock.IsStatus = 2 ,
                                                    itemstock.IsNew = 0 ,
                                                    itemstock.IsPay = 0 ,
                                                    itemstock.UsageCount = (UsageCount + 1),
                                                    itemstock.PackDate = $d_doc_date,
                                                    itemstock.ExpireDate = ( $d_doc_date + INTERVAL packingmat.Shelflife DAY + INTERVAL -1 DAY ) ,
                                                    itemstock.LastSterileDetailID = (SELECT steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID ORDER BY ID DESC LIMIT 1)

                                      

                                        LEFT JOIN 	washdetail
                                        ON			washdetail.ItemStockID = itemstock.RowID

                                        LEFT JOIN 	packingmat
                                        ON			packingmat.ID = washdetail.PackingMatID

                                        LEFT JOIN 	wash
                                        ON			wash.DocNo = washdetail.WashDocNo

                                        WHERE 		washdetail.IsStatus = $p_is_status
                                        AND			wash.IsStatus = 1 
                                        AND         itemstock.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 ";

            $meQuery = $conn->prepare($sql_update_item_stock . $sql_condition);
            $meQuery->execute();

        }else if($p_DB == 1){
            $sql_update_item_stock ="   UPDATE		itemstock

                                        SET 		itemstock.IsStatus = 2 ,
                                                    itemstock.IsNew = 0 ,
                                                    itemstock.IsPay = 0 ,
                                                    itemstock.UsageCount = (UsageCount + 1),
                                                    itemstock.PackDate = $d_doc_date,
                                                    itemstock.ExpireDate = $d_doc_date ,
                                                    itemstock.LastSterileDetailID = (SELECT TOP 1 steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID)

                                        FROM        itemstock

                                        LEFT JOIN 	washdetail
                                        ON			washdetail.ItemStockID = itemstock.RowID

                                        LEFT JOIN 	packingmat
                                        ON			packingmat.ID = washdetail.PackingMatID

                                        LEFT JOIN 	wash
                                        ON			wash.DocNo = washdetail.WashDocNo

                                        WHERE 		washdetail.IsStatus = $p_is_status
                                        AND			wash.IsStatus = 1 
                                        AND         itemstock.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 ";

            $meQuery = $conn->prepare($sql_update_item_stock . $sql_condition);
            $meQuery->execute();

        }

    } else {

        if($p_DB == 0){

            $sql_update_item_stock ="   UPDATE		itemstock
                                        SET 		itemstock.IsStatus = 2 ,
                                                    itemstock.IsNew = 0 ,
                                                    itemstock.IsPay = 0 ,
                                                    itemstock.UsageCount = (UsageCount + 1),
                                                    itemstock.PackDate = $d_doc_date,
                                                    itemstock.ExpireDate = ( $d_doc_date + INTERVAL packingmat.Shelflife DAY + INTERVAL -1 DAY ),
                                                    itemstock.LastSterileDetailID = (SELECT TOP 1 steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID ORDER BY ID DESC)

                                        WHERE 		itemstock.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 
                                        AND         itemstock.RowID IN " . $sql_list_condition_row_id;

            $meQuery = $conn->prepare($sql_update_item_stock);
            $meQuery->execute();

        }else if($p_DB == 1){

            $sql_update_item_stock ="   UPDATE		itemstock
                                        SET 		itemstock.IsStatus = 2 ,
                                                    itemstock.IsNew = 0 ,
                                                    itemstock.IsPay = 0 ,
                                                    itemstock.UsageCount = (UsageCount + 1),
                                                    itemstock.PackDate = $d_doc_date,
                                                    itemstock.ExpireDate = (DATEADD(DAY, packingmat.Shelflife - 1, $d_doc_date ),
                                                    itemstock.LastSterileDetailID = (SELECT steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID)

                                        WHERE 		itemstock.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 
                                        AND         itemstock.RowID IN " . $sql_list_condition_row_id;

            $meQuery = $conn->prepare($sql_update_item_stock);
            $meQuery->execute();

        }
    }

    // -------------------------------------------------------
    // Update washdetail
    // -------------------------------------------------------

    if ($p_qty == 0) {

        if($p_DB == 0){
            
            $sql_update_wash_detail = " UPDATE		washdetail

                                        LEFT JOIN  	itemstock
                                        ON   		itemstock.RowID = washdetail.ItemStockID

                                        LEFT JOIN 	packingmat
                                        ON			packingmat.ID = washdetail.PackingMatID

                                        LEFT JOIN 	wash
                                        ON			wash.DocNo = washdetail.WashDocNo

                                        SET 		washdetail.IsStatus = 2

                                        WHERE 		washdetail.IsStatus = $p_is_status
                                        AND			wash.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 ";

            $meQuery = $conn->prepare($sql_update_wash_detail . $sql_condition);
            $meQuery->execute();

        }else{

            $sql_update_wash_detail = " UPDATE		washdetail

                                        SET 		washdetail.IsStatus = 2

                                        FROM        washdetail

                                        LEFT JOIN  	itemstock
                                        ON   		itemstock.RowID = washdetail.ItemStockID

                                        LEFT JOIN 	packingmat
                                        ON			packingmat.ID = washdetail.PackingMatID

                                        LEFT JOIN 	wash
                                        ON			wash.DocNo = washdetail.WashDocNo

                                        WHERE 		washdetail.IsStatus = $p_is_status
                                        AND			wash.IsStatus = 1 
                                        AND			itemstock.IsCancel = 0 ";

            $meQuery = $conn->prepare($sql_update_wash_detail . $sql_condition);
            $meQuery->execute();

        }

    } else {

        $sql_update_wash_detail = " UPDATE		washdetail

                                    LEFT JOIN  	itemstock
                                    ON   		itemstock.RowID = washdetail.ItemStockID

                                    LEFT JOIN 	packingmat
                                    ON			packingmat.ID = washdetail.PackingMatID

                                    LEFT JOIN 	wash
                                    ON			wash.DocNo = washdetail.WashDocNo

                                    SET 		washdetail.IsStatus = 2

                                    WHERE 		itemstock.IsCancel = 0 
                                    AND         washdetail.ID IN " . $sql_list_condition_id;

        $meQuery = $conn->prepare($sql_update_wash_detail);
        $meQuery->execute();

    }

    // -------------------------------------------------------
    // Update wash
    // -------------------------------------------------------

    $sql_wash_docno = getSqlDocNo($p_qty);

    $res = $conn->prepare($sql_wash_docno);
    $res->execute();

    if (!empty($res)) {
        while ($row = $res->fetch(PDO::FETCH_ASSOC)) {

            $d_WashDocNo = $row["WashDocNo"];

            $sql_update_doc = " UPDATE 		wash

								SET 		wash.IsStatus = 2

								WHERE 		wash.DocNo = '$d_WashDocNo'

								AND 		(SELECT COUNT(*) FROM washdetail WHERE washdetail.WashDocNo = wash.DocNo AND washdetail.IsStatus < 2) = 0 ";

            $meQuery = $conn->prepare($sql_update_doc);
            $meQuery->execute();
        }
    }

    // -------------------------------------------------------
    // Update Print Count
    // -------------------------------------------------------

    $Sql = "UPDATE 		steriledetail

			SET 		PrintCount = '1'

			WHERE 		steriledetail.DocNo = '$p_docno'

			AND 		steriledetail.PackingMatID IN('10','11','12')";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

}

// =======================================================
// Add Sterile Detail By Id (2)
// =======================================================

function getSql_ById(){

    $B_ID = $_POST['B_ID'];

    $sql = "SELECT 		washdetail.ID,
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
            AND         washdetail.B_ID = $B_ID
            AND         wash.B_ID = $B_ID 
            AND         itemstock.B_ID = $B_ID
             ";

    // Status
    if (isset($_POST["p_is_status"])) {

        $p_is_status = $_POST['p_is_status'];

        $sql = $sql . "AND 	washdetail.IsStatus = $p_is_status ";

        if ($p_is_status == "1") {

            $sql = $sql . "AND  wash.IsStatus = 1  ";

        }

    } else {
        $sql = $sql . "AND 	washdetail.IsStatus = 1 ";
        $sql = $sql . "AND	wash.IsStatus = 1 ";
    }

    // Packing ID
    if (isset($_POST["p_PackingMatID"])) {

        $p_PackingMatID = $_POST['p_PackingMatID'];

        $sql = $sql . "AND 	washdetail.PackingMatID = $p_PackingMatID ";
    }

    // List ID
    if (isset($_POST["p_data"])) {

        $p_data = $_POST['p_data'];

        $sql = $sql . "AND 	washdetail.ID IN (" . substr($p_data, 0, -1) . ")";
    }

    $sql = $sql . "ORDER BY itemstock.RowID ASC ";

    return $sql;
}

function getSqlCondition_ById(){
    $sql = "";
    // Branch ID
    if (isset($_POST["B_ID"])) {

        $B_ID = $_POST["B_ID"];

        $sql = $sql . "AND	 washdetail.B_ID = $B_ID ";
    }

    // Packing ID
    if (isset($_POST["p_PackingMatID"])) {

        $p_PackingMatID = $_POST['p_PackingMatID'];

        $sql = $sql . "AND 	washdetail.PackingMatID = $p_PackingMatID ";
    }

    // List ID
    if (isset($_POST["p_data"])) {

        $p_data = $_POST['p_data'];

        $p_data = str_replace("@", ",", $p_data);

        $sql = $sql . "AND 	washdetail.ID IN (" . substr($p_data, 0, -1) . ")";
    }

    return $sql;

}

// -------------------------------------------------------
// Add Sterile Detail By Id
// -------------------------------------------------------

function addSterileDetailById($conn, $p_docno){
    $d_doc_date = "NOW()";
    $p_prepare_code = "0";
    $p_approve_code = "0";
    $p_sterile_code = "0";
    $p_packing_code = "0";

    $B_ID = $_POST["B_ID"];

    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1";

    }else if($p_DB == 1){
        
        $top = "TOP 1";
        $limit = " ";

    }

    $sql_select_wash_item = getSql_ById();
    $sql_condition = getSqlCondition_ById();

    // -------------------------------------------------------
    // Insert steriledetail
    // -------------------------------------------------------

    // POST docdate **
    $d_sterile_data = getSterileData($conn, $p_docno);

    $d_array_data = explode(",", $d_sterile_data);

    $d_doc_date = $d_array_data[0];
    $p_prepare_code = $d_array_data[1];
    $p_approve_code = $d_array_data[2];
    $p_sterile_code = $d_array_data[3];
    $p_packing_code = $d_array_data[4];

    if (isset($_POST["p_PrepareCode"])) {
        $p_prepare_code = $_POST["p_PrepareCode"];
    }

    if (isset($_POST["p_ApproveCode"])) {
        $p_approve_code = $_POST["p_ApproveCode"];
    }

    if (isset($_POST["p_SterileCode"])) {
        $p_sterile_code = $_POST["p_SterileCode"];
    }

    if (isset($_POST["p_PackingCode"])) {
        $p_packing_code = $_POST["p_PackingCode"];
    }

    if($p_DB == 0){

        $sql_insert =  "INSERT INTO steriledetail (
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
                                    $d_doc_date,
                                    $p_prepare_code,
                                    $p_approve_code,
                                    $p_sterile_code,
                                    $p_packing_code,
                                    ($d_doc_date + INTERVAL s.Shelflife DAY + INTERVAL -1 DAY ),
                                    s.PackingMatID,
                                    $B_ID
                        FROM  	(
                                $sql_select_wash_item
                            ) AS s ";

                            
        $res_insert = $conn->prepare($sql_insert);
        $res_insert->execute();

    }else if($p_DB == 1){
        
        $sql_insert =  "INSERT INTO steriledetail (
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
                                    $d_doc_date,
                                    $p_prepare_code,
                                    $p_approve_code,
                                    $p_sterile_code,
                                    $p_packing_code,
                                    ( DATEADD(DAY, s.Shelflife - 1, $d_doc_date),
                                    s.PackingMatID,
                                    $B_ID
                        FROM  	(
                                $sql_select_wash_item
                            ) AS s ";

        $res_insert = $conn->prepare($sql_insert);
        $res_insert->execute();

    }

    //echo "</br> 1." . $sql_insert;

    // -------------------------------------------------------
    // Create Item Stock Log
    // -------------------------------------------------------
    
    if($p_DB == 0){

        $sql_insert_stock_log = "   INSERT INTO itemstocklog (
                                                ItemStockID,
                                                CreateDate,
                                                SterileDetailID,
                                                B_ID
                                            )
                                    SELECT 		s.ItemStockID,
                                                NOW(),
                                                (SELECT ID FROM steriledetail WHERE ItemStockID = s.ItemStockID AND B_ID = $B_ID ORDER BY ID DESC LIMIT 1),
                                                $B_ID
                                    FROM  	(
                                                $sql_select_wash_item
                                            ) AS s ";

        $meQuery = $conn->prepare($sql_insert_stock_log);
        $meQuery->execute();

    }else if($p_DB == 1){

        $sql_insert_stock_log = "   INSERT INTO itemstocklog (
                                                ItemStockID,
                                                CreateDate,
                                                SterileDetailID,
                                                B_ID
                                            )
                                    SELECT 		s.ItemStockID,
                                                GETDATE(),
                                                (SELECT TOP 1 ID FROM steriledetail WHERE ItemStockID = s.ItemStockID AND B_ID = $B_ID ORDER BY ID DESC ),
                                                $B_ID
                                    FROM  	(
                                                $sql_select_wash_item
                                            ) AS s ";

        $meQuery = $conn->prepare($sql_insert_stock_log);
        $meQuery->execute();

    }

    //echo "</br> 2." . $sql_insert_stock_log;

    // -------------------------------------------------------
    // Update Item Stock
    // -------------------------------------------------------

    if($p_DB == 0){
        $sql_update_item_stock = "  UPDATE		itemstock

                                    LEFT JOIN 	washdetail
                                    ON			washdetail.ItemStockID = itemstock.RowID

                                    LEFT JOIN 	packingmat
                                    ON			packingmat.ID = washdetail.PackingMatID

                                    LEFT JOIN 	wash
                                    ON			wash.DocNo = washdetail.WashDocNo

                                    WHERE 		washdetail.IsStatus = 1
                                    AND			wash.IsStatus = 1 
                                    WHERE 		itemstock.IsStatus = 1 
                                    AND			itemstock.IsCancel = 0 ";

        $meQuery = $conn->prepare($sql_update_item_stock);
        $meQuery->execute();

    }else if($p_DB == 1){

        $sql_update_item_stock = "  UPDATE		itemstock

                                    SET 		itemstock.IsStatus = 2 ,
                                                itemstock.IsNew = 0 ,
                                                itemstock.IsPay = 0 ,
                                                itemstock.UsageCount = (UsageCount + 1),
                                                itemstock.PackDate = $d_doc_date,  
                                                itemstock.ExpireDate = (DATEADD(DAY, packingmat.Shelflife - 1, $d_doc_date )),
                                                itemstock.LastSterileDetailID = (SELECT TOP 1 steriledetail.ID FROM steriledetail WHERE steriledetail.ItemStockID = itemstock.RowID AND B_ID = $B_ID ORDER BY ID DESC)

                                    FROM        itemstock    

                                    LEFT JOIN 	washdetail
                                    ON			washdetail.ItemStockID = itemstock.RowID

                                    LEFT JOIN 	packingmat
                                    ON			packingmat.ID = washdetail.PackingMatID

                                    LEFT JOIN 	wash
                                    ON			wash.DocNo = washdetail.WashDocNo

                                    WHERE 		washdetail.IsStatus = 1
                                    AND			wash.IsStatus = 1 
                                    WHERE 		itemstock.IsStatus = 1 
                                    AND			itemstock.IsCancel = 0 ";

        $meQuery = $conn->prepare($sql_update_item_stock);
        $meQuery->execute();

    }
    //echo "</br> 3." . $sql_update_item_stock . $sql_condition;

    // -------------------------------------------------------
    // Update washdetail
    // -------------------------------------------------------
    $sql_update_wash_detail =  "UPDATE		washdetail

                                LEFT JOIN  	itemstock
                                ON   		itemstock.RowID = washdetail.ItemStockID

                                LEFT JOIN 	packingmat
                                ON			packingmat.ID = washdetail.PackingMatID

                                LEFT JOIN 	wash
                                ON			wash.DocNo = washdetail.WashDocNo

                                SET 		washdetail.IsStatus = 2

                                WHERE 		washdetail.IsStatus = 1
                                AND			wash.IsStatus = 1 
                                AND			itemstock.IsCancel = 0 ";

    $meQuery = $conn->prepare($sql_update_wash_detail . $sql_condition);
    $meQuery->execute();
    //echo "</br> 4." . $sql_update_wash_detail . $sql_condition;

    // -------------------------------------------------------
    // Update wash **
    // -------------------------------------------------------

    $sql_wash_docno = getSqlDocNo("1");

    $res = $conn->prepare($conn, $sql_wash_docno . $sql_condition);
    $res->execute();
    //echo "</br> 5." . $sql_wash_docno . $sql_condition;

    if (!empty($res)) {
        while ($row = $res->fetch(PDO::FETCH_ASSOC)) {

            $d_WashDocNo = $row["WashDocNo"];

            $sql_update_doc = " UPDATE 	wash

								SET 	wash.IsStatus = 2

								WHERE 	wash.DocNo = '$d_WashDocNo'

								AND 	(SELECT COUNT(*) FROM washdetail WHERE washdetail.WashDocNo = wash.`DocNo` AND washdetail.IsStatus < 2 AND washdetail.B_ID = $B_ID) = 0 ";

            $res = $conn->prepare($conn, $sql_update_doc);
            $res->execute();
            //echo "</br> 6." . $sql_update_doc;
        }
    }

    // -------------------------------------------------------
    // Update Print Count
    // -------------------------------------------------------

    $Sql = "UPDATE 		steriledetail

			SET 		PrintCount = '1'

			WHERE 		steriledetail.DocNo = '$p_docno'

			AND 		steriledetail.PackingMatID IN('10','11','12')";

    $res = $conn->prepare($conn, $Sql);
    $res->execute();
   
}

function getSqlWashDetail(){
    $B_ID = $_POST['B_ID'];
	$p_SterileProcessID = $_POST["p_SterileProcessID"];

	$strSQL = 	"SELECT 	sterileprogram.SterileName AS i_program,    
							sterileprogram.SterileName2 AS i_program2,   
							sterileprogram.ID AS i_program_id,   
							item.itemcode  AS i_id,   			
							item.itemcode  AS i_code,   
							item.Barcode  AS i_barcode ,       
							item.itemname AS i_name, 											
							item.Alternatename  AS i_alt_name,       
							SUM(washdetail. Qty ) AS i_qty,
							COALESCE(packingmat.PackingMat, '-') AS PackingMat, 
							COALESCE(packingmat.Shelflife, '-') AS Shelflife, 
							COALESCE(packingmat.ID, '0') AS PackingMatID 		

				FROM 		washdetail        
				
				LEFT JOIN 	wash   				
				ON			wash.DocNo = washdetail.WashDocNo 

				LEFT JOIN 	itemstock   		
				ON			itemstock.RowID = washdetail.ItemStockID  

				LEFT JOIN 	item 				
				ON			item.itemcode = itemstock.ItemCode  

				LEFT JOIN 	sterileprogramitem  
				ON			sterileprogramitem.ItemCode = item.itemCode 

				LEFT JOIN 	sterileprocess 		
				ON			sterileprocess.SterileProgramID = sterileprogramitem.SterileProgramID 

				LEFT JOIN 	sterileprogram  	
				ON			sterileprogram.ID = sterileprocess.SterileProgramID  

				LEFT JOIN 	packingmat  		
				ON			packingmat.ID = washdetail.PackingMatID  							

				WHERE 		wash.IsStatus = 1 

                AND 		sterileprocess.ID = $p_SterileProcessID 

                AND			itemstock.IsCancel = 0 
                AND         washdetail.B_ID = $B_ID
                AND         wash.B_ID = $B_ID
                AND         itemstock.B_ID = $B_ID ";


	if( isset($_POST["p_Mode"]) ){
		$p_Mode = $_POST["p_Mode"];
					
		$strSQL =	$strSQL . "AND 		washdetail.IsStatus = $p_Mode ";

		if($p_Mode == "1"){
			$strSQL =	$strSQL . "AND 		itemstock.IsStatus = 1 ";
		}
					
	}else{
		$strSQL =	$strSQL . "AND 		washdetail.IsStatus = 1 ";
		$strSQL =	$strSQL . "AND 		itemstock.IsStatus = 1 ";
	}
					
	$strSQL =	$strSQL .	"GROUP BY 	sterileprogram.SterileName,   
										sterileprogram.SterileName2,     
										sterileprogram.ID ,     
										item.itemcode ,     
										item.itemname ,     
										item.Alternatename ,     
										item.Barcode,   
										packingmat.PackingMat,
										packingmat.Shelflife,
										packingmat.ID 									

							ORDER BY 	sterileprogram.SterileName ";  

	return $strSQL;
		
}

function getSqlSterileDetail($p_docno){
    $d_join = "";

    $d_price = "item.SalePrice AS Price";

    if (isset($_POST["p_is_item_price_code"])) {

        $d_price = 	"COALESCE(prices.PriceCode, '') AS Price";

        $d_join = " LEFT JOIN 	prices
					ON			prices.ID = item.PriceID ";
    }

    if (isset($_POST["p_used_user_detail"])) {
        $d_field = "steriledetail";
    } else {
        $d_field = "sterile";
    }

    if (isset($_POST["p_inc_exp"])) {
        $d_inc_exp = "+ INTERVAL 1 DAY";
    } else {
        $d_inc_exp = "";
    }

    //  0 = mysqli , 1 = sqlserver
    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    $d_set = "";

    if($p_DB == 0){
     
        $strSQL = "	SELECT 		    steriledetail.ID,
                                    steriledetail.DocNo,
                                    steriledetail.ItemStockID,
                                    steriledetail.Qty,
                                    CONCAT(item.itemname , ' (', label.LabelGroup, ') ') AS itemname,
                                    item.itemcode,
                                    itemstock.UsageCode,
                                    (packingmat.Shelflife) AS age,
                                    COALESCE(steriledetail.ImportID,'-') AS ImportID,
                                    DATE_FORMAT( steriledetail.SterileDate, '%d-%m-%Y') AS SterileDate,
                                    DATE_FORMAT( ( steriledetail.ExpireDate $d_inc_exp ), '%d-%m-%Y') AS ExpireDate,
                                    if(steriledetail.IsStatus,'1','0') AS IsStatus,
                                    steriledetail.OccuranceQty,
                                    (CASE WHEN IsPrintDepartment = 1
                                        THEN (CASE WHEN item.SpecialID = 1 THEN department.DepName ELSE 'CSSD' END )
                                        ELSE '' END
                                    ) AS DepName,
                                    (CASE WHEN IsPrintDepartment = 1
                                        THEN (CASE WHEN item.SpecialID = 1 THEN department.DepName2 ELSE 'CSSD' END )
                                        ELSE '' END
                                    ) AS DepName2,
                                    label.LabelGroup AS LabelID,
                                    COALESCE(CONCAT(employee_1.FirstName, ' ', employee_1.LastName), '-') AS usr_sterile,
                                    COALESCE(CONCAT(employee_2.FirstName, ' ', employee_2.LastName), '-') AS usr_prepare,
                                    COALESCE(CONCAT(employee_3.FirstName, ' ', employee_3.LastName), '-') AS usr_approve,
                                    COALESCE(CONCAT(employee_4.FirstName, ' ', employee_4.LastName), '-') AS usr_packing,
                                    sterile.SterileRoundNumber,

                                    sterilemachine.MachineName2 AS MachineName,
                                    $d_price,
                                    CONCAT( 'TIME: ', DATE_FORMAT(steriledetail.SterileDate, '%H:%i') ) AS Time,
                                    item.SterileProcessID,
                                    steriledetail.PrintCount,
                                    label.Printer,
                                    itemstock.UsageCount

                    FROM 			steriledetail

                    LEFT JOIN 		sterile
                    ON				sterile.DocNo = steriledetail.DocNo

                    LEFT JOIN 		sterilemachine
                    ON				sterilemachine.ID = sterile.SterileMachineID

                    LEFT JOIN 		employee AS employee_1
                    ON 				employee_1.ID = $d_field.SterileCode

                    LEFT JOIN 		employee AS employee_2
                    ON 				employee_2.ID = $d_field.PrepareCode

                    LEFT JOIN 		employee AS employee_3
                    ON 				employee_3.ID = $d_field.ApproveCode

                    LEFT JOIN 		employee AS employee_4
                    ON 				employee_4.ID = $d_field.PackingCode

                    LEFT JOIN 		itemstock
                    ON				itemstock.RowID = steriledetail.ItemStockID

                    LEFT JOIN 		item
                    ON				item.itemcode = itemstock.ItemCode

                    $d_join

                    LEFT JOIN 		department
                    ON				department.ID = itemstock.DepID

                    LEFT JOIN 		label
                    ON				label.LabelGroup = item.LabelGroupID

                    LEFT JOIN 		packingmat
                    ON				packingmat.ID = steriledetail.PackingMatID

                    WHERE 			steriledetail.DocNo = '$p_docno'
                    AND 			label.IsActive = 1 
                    AND			    itemstock.IsCancel = 0 
                    AND             itemstock.B_ID = $B_ID
                    AND             steriledetail.B_ID = $B_ID 
                    AND             sterile.B_ID = $B_ID 
                    AND             sterilemachine.B_ID = $B_ID 
                    AND             item.B_ID = $B_ID";

    }else if($p_DB == 1){
     
        $strSQL = "	SELECT 			steriledetail.ID,
                                    steriledetail.DocNo,
                                    steriledetail.ItemStockID,
                                    steriledetail.Qty,
                                    CONCAT(item.itemname , ' (', label.LabelGroup, ') ') AS itemname,
                                    item.itemcode,
                                    itemstock.UsageCode,
                                    (packingmat.Shelflife) AS age,
                                    COALESCE(steriledetail.ImportID,'-') AS ImportID,
                                    FORMAT( steriledetail.SterileDate,  'dd/MM/yyyy') AS SterileDate,
                                    FORMAT(DATEADD(DAY, 1 , steriledetail.ExpireDate),'dd/MM/yyyy') AS ExpireDate
                                    steriledetail.IsStatus AS IsStatus,
                                    steriledetail.OccuranceQty,
                                    (CASE WHEN IsPrintDepartment = 1
                                        THEN (CASE WHEN item.SpecialID = 1 THEN department.DepName ELSE 'CSSD' END )
                                        ELSE '' END
                                    ) AS DepName,
                                    (CASE WHEN IsPrintDepartment = 1
                                        THEN (CASE WHEN item.SpecialID = 1 THEN department.DepName2 ELSE 'CSSD' END )
                                        ELSE '' END
                                    ) AS DepName2,
                                    label.LabelGroup AS LabelID,
                                    COALESCE(CONCAT(employee_1.FirstName, ' ', employee_1.LastName), '-') AS usr_sterile,
                                    COALESCE(CONCAT(employee_2.FirstName, ' ', employee_2.LastName), '-') AS usr_prepare,
                                    COALESCE(CONCAT(employee_3.FirstName, ' ', employee_3.LastName), '-') AS usr_approve,
                                    COALESCE(CONCAT(employee_4.FirstName, ' ', employee_4.LastName), '-') AS usr_packing,
                                    sterile.SterileRoundNumber,
                                    sterilemachine.MachineName2 AS MachineName,
                                    $d_price,
                                    CONCAT( 'TIME: ', FORMAT(steriledetail.SterileDate, 'hh:ss') ) AS Time,
                                    item.SterileProcessID,
                                    steriledetail.PrintCount,
                                    label.Printer,
                                    itemstock.UsageCount

                    FROM 			steriledetail

                    LEFT JOIN 		sterile
                    ON				sterile.DocNo = steriledetail.DocNo

                    LEFT JOIN 		sterilemachine
                    ON				sterilemachine.ID = sterile.SterileMachineID

                    LEFT JOIN 		employee AS employee_1
                    ON 				employee_1.ID = $d_field.SterileCode

                    LEFT JOIN 		employee AS employee_2
                    ON 				employee_2.ID = $d_field.PrepareCode

                    LEFT JOIN 		employee AS employee_3
                    ON 				employee_3.ID = $d_field.ApproveCode

                    LEFT JOIN 		employee AS employee_4
                    ON 				employee_4.ID = $d_field.PackingCode

                    LEFT JOIN 		itemstock
                    ON				itemstock.RowID = steriledetail.ItemStockID

                    LEFT JOIN 		item
                    ON				item.itemcode = itemstock.ItemCode

                    $d_join

                    LEFT JOIN 		department
                    ON				department.ID = itemstock.DepID

                    LEFT JOIN 		label
                    ON				label.LabelGroup = item.LabelGroupID

                    LEFT JOIN 		packingmat
                    ON				packingmat.ID = steriledetail.PackingMatID

                    WHERE 			steriledetail.DocNo = '$p_docno'
                    AND 			label.IsActive = 1 
                    AND			    itemstock.IsCancel = 0 
                    AND             itemstock.B_ID = $B_ID
                    AND             steriledetail.B_ID = $B_ID  
                    AND             sterile.B_ID = $B_ID
                    AND             sterilemachine.B_ID = $B_ID
                    AND             item.B_ID = $B_ID ";

    }

    if (isset($_POST["p_order_by_item_name"])) {
        $strSQL = $strSQL . "ORDER BY item.itemname ASC ";
    } else {
        $strSQL = $strSQL . "ORDER BY steriledetail.ID  DESC ";
    }

    return $strSQL;
    
}

function getSqlGroupSterileDetail($p_docno){
    $B_ID = $_POST['B_ID'];
    $strSQL = 			"SELECT 		0 AS ID,
										steriledetail.DocNo,
										0 AS ItemStockID,
										COUNT(steriledetail.Qty) AS Qty,
										CONCAT(item.itemname , ' (', label.LabelGroup, ') ') AS itemname,

										item.itemcode,
										item.itemcode AS UsageCode,
										(packingmat.Shelflife) AS age,
										0 AS ImportID,
										'' AS SterileDate,
										'' AS ExpireDate,
										0 AS IsStatus,
										0 AS OccuranceQty,

										'' AS DepName,

										'' AS DepName2,

										label.LabelGroup AS LabelID,
										'' AS usr_sterile,
										'' AS usr_prepare,
										'' AS usr_approve,
										'' AS usr_packing,
										0 AS SterileRoundNumber,

										'' AS MachineName,
										'' AS Time,
										item.SterileProcessID,

										(
											SELECT 		COUNT(*) 
											FROM 		steriledetail AS sd 
											WHERE 		sd.DocNo = steriledetail.DocNo 
											AND 		sd.ItemStockID = steriledetail.ItemStockID
											AND 		sd.PrintCount > 0
                                            AND         sd.B_ID = $B_ID
										) AS 			PrintCount,

										label.Printer,
										0 AS UsageCount

						FROM 			steriledetail

						LEFT JOIN 		sterile
						ON				sterile.DocNo = steriledetail.DocNo

						LEFT JOIN 		itemstock
						ON				itemstock.RowID = steriledetail.ItemStockID

						LEFT JOIN 		item
						ON				item.itemcode = itemstock.ItemCode

						LEFT JOIN 		label
						ON				label.LabelGroup = item.LabelGroupID

						LEFT JOIN 		packingmat
						ON				packingmat.ID = steriledetail.PackingMatID

						WHERE 			steriledetail.DocNo = '$p_docno'
                        AND 			label.IsActive = 1 
                        AND			    itemstock.IsCancel = 0 
                        AND             itemstock.B_ID = $B_ID
                        AND             steriledetail.B_ID = $B_ID
                        AND             sterile.B_ID = $B_ID
                        AND             item.B_ID = $B_ID ";


    $strSQL = $strSQL . "GROUP BY 	steriledetail.DocNo,
                                    item.itemcode,							
									item.itemname , 
									label.LabelGroup,
									packingmat.Shelflife,
									item.SterileProcessID,
									label.Printer,
                                    steriledetail.ItemStockID ";

    return $strSQL;
    
}

function getSqlItemSet($p_itemcode){

    $p_DB = $_POST['p_DB'];
    $B_ID = $_POST['B_ID'];

    
    if($p_DB == 0){

        $top100 = " ";
        $limit100 = "LIMIT 100";

    }else if($p_DB == 1){
        
        $top100 = "TOP 100";
        $limit100 = " ";

    }

    $sql_set = "SELECT 		$top100
                            itemdetail.itemcode,
                            item.itemname, 
                            concat(itemdetail.Qty, ' ' , COALESCE(units.UnitName,'-') ) as qty

				FROM 		`itemdetail`

				LEFT JOIN 	item
				ON			item.itemcode = `itemdetail`.itemDetailID

				LEFT JOIN 	units
				ON			units.ID = `item`.UnitID

				WHERE   	itemdetail.itemcode = '$p_itemcode' 

				AND			itemdetail.IsShowInSticker = 1 
                AND         itemdetail.B_ID = $B_ID
                AND         item.B_ID = $B_ID
                AND         units.B_ID = $B_ID

                ORDER BY 	itemdetail.ID ASC 
					
                $limit100 ";
                
    return $sql_set;      
}

// *******************************************************
// -- MAIN
// *******************************************************

$p_UserCode = $_POST["p_UserCode"];
$p_SterileProgramID = $_POST["p_SterileProgramID"];
$p_SterileMachineID = $_POST["p_SterileMachineID"];

// Var
$p_PrepareCode = "0";
$p_ApproveCode = "0";
$p_SterileCode = "0";
$p_PackingCode = "0";
$d_DocNo = "";

$p_DB = $_POST['p_DB'];

if($p_DB == 0){

    $top1 = " ";
    $limit1 = "LIMIT 10";

}else if($p_DB == 1){
    
    $top1 = "TOP 10";
    $limit1 = " ";

}

// Config
if (isset($_POST["p_IsUsedDBUserOperation"])) {
    $p_IsUsedDBUserOperation = $_POST["p_IsUsedDBUserOperation"];
} else {
    $p_IsUsedDBUserOperation = "1";
}

if (isset($_POST["p_IsRememberUserOperation"])) {
    $p_IsRememberUserOperation = $_POST["p_IsRememberUserOperation"];
} else {
    $p_IsRememberUserOperation = "1";
}

if (isset($_POST["p_OccupancyRate"])) {
    $p_OccupancyRate = $_POST["p_OccupancyRate"];
} else {
    $p_OccupancyRate = "0";
}

// Document Data
if (isset($_POST["p_doc_date"])) {
    if($p_DB == 0){

        $p_doc_date = $_POST["p_doc_date"];
        $p_doc_date = "'" . $p_doc_date . "'";
    
    }else if($p_DB == 1){
        
       $p_doc_date = "GETDATE()";
    
    }
   
} else {
  
    if($p_DB == 0){

        $p_doc_date = "NOW()";
    
    }else if($p_DB == 1){
        
        $p_doc_date = "GETDATE()";
    
    }
}

if (isset($_POST["p_Is_NonSelectRound"])) {
    $p_Is_NonSelectRound = $_POST["p_Is_NonSelectRound"];
} else {
    $p_Is_NonSelectRound = "0";
}

if (isset($_POST["p_SterileTypeID"])) {
    $p_SterileTypeID = $_POST["p_SterileTypeID"];
}

// ==========================================================================================
// Check DocNo In Machine
// ==========================================================================================

$d_DocNo = "";

$Sql = "	SELECT 		$top1
                        DocNo 

            FROM 		sterilemachine  

            WHERE 		ID = '$p_SterileMachineID' 
            AND 		sterilemachine.B_ID = $B_ID

            ";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

if ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $d_DocNo = $Result["DocNo"];
}

if($d_DocNo == "" ){

    // -------------------------------------------------------
    // POST User Sterile
    // -------------------------------------------------------
    
    if ($p_PrepareCode == "e" && $p_ApproveCode == "e" && $p_SterileCode == "e" && $p_PackingCode == "e") {

        if ($p_IsUsedDBUserOperation == "1") {

            if ($p_IsRememberUserOperation == "1") {

                $sql_time ="SELECT 	$top1
                                    usersterile.PrepareCode,
                                    usersterile.ApproveCode,
                                    usersterile.SterileCode ,
                                    usersterile.PackingCode

                            FROM 	usersterile
                            WHERE 	usersterile.B_ID = $B_ID

                            $limit1 ";

                $result_time = $conn->prepare($sql_time);
                $result_time->execute();

                if ($row = $result_time->fetch(PDO::FETCH_ASSOC)) {
                    $p_PrepareCode = $row["PrepareCode"];
                    $p_ApproveCode = $row["ApproveCode"];
                    $p_SterileCode = $row["SterileCode"];
                    $p_PackingCode = $row["PackingCode"];
                }
            }
        } else {
            $sql_time ="SELECT 	$top1
                                timetabledetail.Emp1,
                                timetabledetail.Emp2,
                                timetabledetail.Emp3

                        FROM 	timetabledetail
                        WHERE 	timetabledetail.B_ID = $B_ID

                        $limit1 ";

            $result_time = $conn->prepare($sql_time);
            $result_time->execute();

            if ($row = $result_time->fetch(PDO::FETCH_ASSOC)) {
                $p_PrepareCode = $row["Emp1"];
                $p_ApproveCode = $row["Emp2"];
                $p_SterileCode = $row["Emp3"];
                $p_PackingCode = $row["Emp3"];
            }
        }
    }

    // -------------------------------------------------------
    // DocNo
    // -------------------------------------------------------
    $Doc_B_ID = chr(64+$B_ID);
    $Sql = "	SELECT
                    TOP 1 CONCAT (
                        'S',
                        RIGHT (YEAR(GETDATE()), 2) + RIGHT (
                            '0' + RTRIM(MONTH(CURRENT_TIMESTAMP)),
                            2
                        ),
                        '$Doc_B_ID-',
                        REPLACE(
                            STR(
                                (
                                    CAST (RIGHT(DocNo, 4) AS INT) + 1
                                ),
                                4
                            ),
                            ' ',
                            '0'
                        )
                    ) AS DocNo
                FROM
                    sterile
                ORDER BY
                    sterile.DocNo DESC";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $d_DocNo_Check = $Result["DocNo"];
    }


    if($p_DB == 0){

        $Sql = "	SELECT 		CONCAT('S',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID-', LPAD( (COALESCE(MAX(CONVERT(SUBSTRING(DocNo,7,5),UNSIGNED INTEGER)),0)+1) ,5,0)) AS DocNo
                    FROM 		sterile
                    WHERE 		DocNo Like CONCAT('S',SUBSTRING(YEAR(DATE(NOW())),3,4),LPAD(MONTH(DATE(NOW())),2,0),'$Doc_B_ID%')
                    ORDER BY 	DocNo DESC
                    LIMIT 		1";

    }else if($p_DB == 1){
        
        $Sql = "	SELECT
                    ISNULL(
                        (
                            SELECT
                                TOP 1 CONCAT (
                                    'S',
                                    RIGHT (YEAR(GETDATE()), 2) + RIGHT (
                                        '0' + RTRIM(MONTH(CURRENT_TIMESTAMP)),
                                        2
                                    ),
                                    '$Doc_B_ID-',
                                    REPLACE(
                                        STR(
                                            (
                                                CAST (RIGHT(DocNo, 4) AS INT) + 1
                                            ),
                                            4
                                        ),
                                        ' ',
                                        '0'
                                    )
                                ) AS DocNo
                            FROM
                                sterile
														WHERE 		DocNo Like  CONCAT (
                            'S',
                            RIGHT (YEAR(GETDATE()), 2) + RIGHT (
                                '0' + RTRIM(MONTH(CURRENT_TIMESTAMP)),
                                2
                            ),'$Doc_B_ID%')
                            ORDER BY
                                sterile.DocNo DESC
                        ),
                        CONCAT (
                            'S',
                            RIGHT (YEAR(GETDATE()), 2) + RIGHT (
                                '0' + RTRIM(MONTH(CURRENT_TIMESTAMP)),
                                2
                            ),
                            '$Doc_B_ID-0001'
                        )
                    ) AS DocNo";

        $meQuery = $conn->prepare($Sql);
        $meQuery->execute();
        
    }

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $d_DocNo = $Result["DocNo"];
    }

    // -------------------------------------------------------
    //  sterileRoundNumber
    // -------------------------------------------------------

    if($p_DB == 0){

        $top1 = " ";
        $limit1 = "LIMIT 10";

    }else if($p_DB == 1){
        
        $top1 = "TOP 10";
        $limit1 = " ";

    }


    if($p_Is_NonSelectRound == "1"){

        $d_SterileRoundNumber = "0";

    }else{

        if($p_DB == 0){

            $sql_round_no = "   SELECT  COUNT(*) + 1 AS c 

                                FROM    sterile 

                                WHERE	DATE(DocDate) = DATE($p_doc_date) 

                                AND		SterileMachineID = '$p_SterileMachineID' 

                                AND     sterile.SterileRoundNumber != 0 
                                AND     sterile.B_ID = $B_ID";

            $result_round_no = $conn->prepare($sql_round_no);
            $result_round_no->execute();
    
        }else if($p_DB == 1){
            
            $sql_round_no = "   SELECT  COUNT(*) + 1 AS c 

                                FROM    sterile 

                                WHERE	CONVERT(DATE, DocDate) = CONVERT(DATE, GETDATE())
                                AND		SterileMachineID = '$p_SterileMachineID' 

                                AND     sterile.SterileRoundNumber != 0 
                                AND     sterile.B_ID = $B_ID";

            $result_round_no = $conn->prepare($sql_round_no);
            $result_round_no->execute();
    
        }

        $d_SterileRoundNumber = "0";

        if (!empty($result_round_no)) {
            if ($row = $result_round_no->fetch(PDO::FETCH_ASSOC)) {
                $d_SterileRoundNumber = $row["c"];
            }
        }
        
    }

    // -------------------------------------------------------

    if ($d_DocNo != '') {

        $sql_insert = "INSERT INTO sterile (
                                        DocNo,
                                        DocDate,
                                        ModifyDate,
                                        UserCode,
                                        DeptID,

                                        Qty,
                                        IsStatus,
                                        SterileProgramID,
                                        SterileMachineID,
                                        SterileRoundNumber,

                                        StartTime,
                                        FinishTime,
                                        Remark,
                                        IsOccurance,
                                        PrepareCode,

                                        ApproveCode,
                                        SterileCode,
                                        PackingCode,
                                        OccupancyRate,
                                        B_ID,

                                        SterileTypeID

                        ) VALUES (
                                        '$d_DocNo',
                                        $p_doc_date ,
                                        $p_doc_date ,
                                        '$p_UserCode',
                                        null,

                                        0,
                                        0,
                                        '$p_SterileProgramID',
                                        '$p_SterileMachineID',
                                        '$d_SterileRoundNumber',
                                        null,
                                        null,
                                        '',
                                        0,
                                        $p_PrepareCode ,
                                        $p_SterileCode ,
                                        $p_SterileCode ,
                                        $p_PackingCode,
                                        $p_OccupancyRate,
                                        $B_ID,

                                        $p_SterileTypeID
                        ) ";

        $result_insert = $conn->prepare($sql_insert);
        $result_insert->execute();

        // -------------------------------------------------------
        // batch
        // -------------------------------------------------------
        $sql_insert_batch = "   INSERT INTO batch 
                                (   DocNo, 
                                    MachineID, 
                                    MachineRound, 
                                    MachineType, 
                                    CreateDate,
                                    B_ID
                                ) 
                                VALUES      (
                                    '$d_DocNo',
                                    '$p_SterileMachineID',
                                    '$d_SterileRoundNumber',
                                    2,
                                    $p_doc_date,
                                    $B_ID
                                )
        ";
        $result_insert_batch = $conn->prepare($sql_insert_batch);
        $result_insert_batch->execute();

        // -------------------------------------------------------
        // Update Sterile Machine
        // -------------------------------------------------------
        $sql_update = " UPDATE  sterilemachine
                        SET 	DocNo = '$d_DocNo',
                                IsActive = 0,
                                StartTime = NULL,
                                FinishTime = NULL,
                                LastTime = $p_doc_date
                        WHERE
                                ID = '$p_SterileMachineID'";

        $res_update = $conn->prepare($sql_update);
        $res_update->execute();

        array_push(
            $resArray, array(
                'result' => "A",
                'DocNo' => $d_DocNo,
                'sql_insert' => $sql_insert,
                'sql_update' => $sql_update,
            )
        );

    } else {
        array_push(
            $resArray, array(
                'result' => "E",
                'Message' => 'ไม่สามารถสร้างเอกสารฆ่าเชื้อได้ !!',
            )
        );
    }
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