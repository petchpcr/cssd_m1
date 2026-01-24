<?php
//EDIT LOG
//23-01-2026 15.52 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';

$resArray = array();

// ---------------------------------------------------------
// Echo Json
// ---------------------------------------------------------

function echoJson($resArray)
{
    echo json_encode(array("result" => $resArray));
}

// ---------------------------------------------------------
// MAIN
// ---------------------------------------------------------

if ($_SERVER['REQUEST_METHOD'] == 'POST') {

    $p_DB = $_POST['p_DB'];

    if (isset($_POST["p_data"])) {
        $p_data = $_POST['p_data'];

        $p_data = substr($p_data, 0, -1);
    }

    if (isset($_POST["p_user_id"])) {
        $p_user_id = $_POST['p_user_id'];
    }else{
        $p_user_id = "0";
    }

    // -----------------------------------------------------
    // Add To History
    // -----------------------------------------------------

    if($p_DB == 0){
        $date = "NOW()";
    }else if($p_DB == 1){
        $date = "GETDATE()";
    }

    $B_ID = $_POST['B_ID'];

    $strSQL =   "INSERT INTO    itemstock_history 
                (
                            CreateDate,
                            ItemStockID,
                            Info,
                            UserCode,
                            B_ID
                )
                SELECT      $date,
                            RowID,
                            'คืนเข้าจ่ายกลาง',
                            $p_user_id,
                            $B_ID

                FROM        itemstock
                WHERE 		itemstock.RowID IN ($p_data) 
                AND         itemstock.IsStatus = 4 
                AND         itemstock.IsPay = 1    
                AND         itemstock.IsCancel = 0 
                AND         itemstock.B_ID = $B_ID
                ";

    $query1 = $conn->prepare($strSQL);
    $query1->execute();
    // -----------------------------------------------------
    // Update Item Stock
    // -----------------------------------------------------

    $strSQL = " UPDATE 		itemstock 

                SET			itemstock.IsStatus = 3,
                            itemstock.IsPay = 0,
                            itemstock.IsBorrow = 0,
                            itemstock.LastReceiveDeptDate = $date,
                            itemstock.DepID = ( CASE WHEN item.IsSpecial = '0' THEN 20 ELSE itemstock.DepID END)

                FROM 		itemstock

                INNER JOIN  item
                ON          item.itemcode = itemstock.ItemCode 

                WHERE 		itemstock.RowID IN ($p_data) 
                AND         itemstock.IsStatus = 4 
                AND         itemstock.IsPay = 1  
                AND         itemstock.IsCancel = 0 ";

    $query1 = $conn->prepare($strSQL);
    $query1->execute();

    // -----------------------------------------------------
    // Update Payout Detail Sub
    // -----------------------------------------------------

    $d_array_data = explode(",", $p_data);
	$d_data = 1;
	$d_max = sizeof($d_array_data);

	if ($d_max > 0) {

        if($p_DB == 0){
            $top = "";
            $limit = "LIMIT 1";
        }else if($p_DB == 1){
            $top = "TOP 1";
            $limit = "";
        }

		for ($i = 0; $i < $d_max; $i = $i + $d_data) {

			if ($d_array_data[$i] == '') {
				break;
			}

            $d_row_id = $d_array_data[$i];

            $strSQL = " DELETE 		
                        FROM  		payoutdetailsub     
                        WHERE 		IsStatus = 1
                        AND 		ID IN ( 
                                            SELECT * FROM (
                                                            SELECT 		$top
                                                                        ID 
                                                            FROM 		payoutdetailsub 
                                                            WHERE 		ItemStockID = $d_row_id 
                                                            AND 		payoutdetailsub.B_ID = $B_ID
                                                            ORDER BY 	payoutdetailsub.ID DESC
                                                            $limit
                                                        ) AS p
                                    );";

            $query1 = $conn->prepare($strSQL);
            $query1->execute();
            /*
            $strSQL = " UPDATE 		payoutdetailsub
                        SET			IsStatus = 8,
                                    Remark = 'ลบรายการ (คืนของเข้าสต๊อก)'
                                    
                        WHERE 		itemstock.RowID = $d_row_id ";

            mysqli_query($conn, $strSQL);
            */
			
		}
    }


    array_push(
        $resArray,
        array(
            'result' => "A",
            'Message' => "รับเข้าสำเร็จ !!"
        )
    );

} else {
    array_push(
        $resArray,
        array(
            'result' => "I",
            'Message' => "รับเข้าไม่สำเร็จ !!"

        )
    );
}

echo json_encode(array("result" => $resArray));
unset($conn);
die;
?>