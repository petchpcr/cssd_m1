<?php
require 'connect_inventory.php';
$resArray = array();

$TestProgramID = $_POST['TestProgramID'];

$jdata = file_get_contents('php://input');

$jdata_decode = json_decode($jdata,true);

$i = 0;

$result_Json = $jdata_decode;

$TestProgramID = $result_Json["TestProgramID"];

$B_ID = $_POST['B_ID'];

if($TestProgramID != ''){

    $Sql = "SELECT
                * 
            FROM
                (
                SELECT
                    wh_inventory.LotNo,
                    wh_inventory.ItemCode,
                    wh_inventory.EXPDate,
                    wh_payout_detail.DocNo,
                    wh_payout_detail.Create_Date,
                    wh_inventory.RowID,
                    SUM(wh_payout_detail.Qty - wh_payout_detail.CutQty) AS Qty 
                FROM
                    wh_inventory
                    INNER JOIN wh_payout_detail ON wh_inventory.RowID = wh_payout_detail.InventoryRowID
                    INNER JOIN wh_payout ON wh_payout.DocNo = wh_payout_detail.DocNo 
                WHERE
                    wh_inventory.TestProgram_ID = '$TestProgramID' 
                    AND wh_payout.IsUse = 0 
                    AND wh_inventory.TestProgram_ID != 0 
                    AND wh_inventory.B_ID = $B_ID
                    AND wh_payout_detail.B_ID = $B_ID
                    AND wh_payout.B_ID = $B_ID
                GROUP BY
                    wh_inventory.LotNo,
                    wh_inventory.ItemCode,
                    wh_inventory.EXPDate,
                    wh_payout_detail.DocNo,
                    wh_payout_detail.Create_Date,
                    wh_inventory.RowID 
                ORDER BY
                    wh_inventory.EXPDate ASC 
                ) AS dd 
            WHERE
                dd.Qty > 0";

    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();	
    $i = 0;
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
        $LotNo = $Result["LotNo"];
        $Qty = $Result["Qty"];
        $RowID = $Result["RowID"];

        array_push( $resArray,array('LotNo'=>$LotNo,'Qty'=>$Qty,'RowID'=>$RowID));
        $i++;
    }

}else{
    array_push( $resArray,array('LotNo'=>'','Qty'=>'','RowID'=>''));
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>
