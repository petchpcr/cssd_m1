<?php
//EDIT LOG
//23-01-2026 15.43 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

$DOC_NO = $_POST['DOC_NO'];

$iwash = 0;

$strSQL = " SELECT
                wash.DocNo
            FROM
                wash
            WHERE
                wash.DocNo = '$DOC_NO'
                AND wash.B_ID = '$B_ID'";

if ($p_DB == 0) {

    $meQuery = mysqli_query($conn, $strSQL);

    while ($Result = mysqli_fetch_assoc($meQuery)) {
        $DocNoWash = $Result["DocNo"];
        $iwash++;
    }

} else {

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $DocNoWash = $Result["DocNo"];
        $iwash++;
    }

}

$strSQL = " SELECT
                sterile.DocNo
            FROM
                sterile
            WHERE
                sterile.DocNo = '$DOC_NO'
                AND sterile.B_ID = '$B_ID'";

if ($p_DB == 0) {

    $meQuery = mysqli_query($conn, $strSQL);

    while ($Result = mysqli_fetch_assoc($meQuery)) {
        $DocNoSterile = $Result["DocNo"];
    }

} else {

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $DocNoSterile = $Result["DocNo"];
    }

}

$i = 0;

if ($iwash == 1) {

    $strSQL = " SELECT      washdetail.ID

                FROM        itemstock

                INNER JOIN  item 
                ON          itemstock.ItemCode = item.itemcode

                INNER JOIN  packingmat 
                ON          item.PackingMatID = packingmat.ID

                INNER JOIN  washdetail 
                ON          itemstock.RowID = washdetail.ItemStockID

                WHERE       washdetail.IsEms = 1

                AND         washdetail.WashDocNo = '$DOC_NO'
                AND         itemstock.B_ID = '$B_ID'";

    if ($p_DB == 0) {

        $meQuery = mysqli_query($conn, $strSQL);

        while ($Result = mysqli_fetch_assoc($meQuery)) {
            $ID = $Result["ID"];

            $strSQL = " UPDATE  washdetail
                        SET     washdetail.IsEms = 2
                        WHERE   washdetail.ID = '$ID'";
            mysqli_query($conn, $strSQL);

            array_push(
                $resArray,
                array(
                    'finish' => true,
                )
            );
        }

    } else {

        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();

        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $ID = $Result["ID"];

            $strSQL = " UPDATE  washdetail
                        SET     washdetail.IsEms = 2
                        WHERE   washdetail.ID = '$ID'";

            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();

            array_push(
                $resArray,
                array(
                    'finish' => true,
                )
            );
        }
    }
} else {
    $strSQL = " SELECT      steriledetail.ID

                FROM        itemstock

                INNER JOIN  item 
                ON          itemstock.ItemCode = item.itemcode

                INNER JOIN  packingmat 
                ON          item.PackingMatID = packingmat.ID

                INNER JOIN  steriledetail 
                ON          steriledetail.ItemStockID = itemstock.RowID

                WHERE       steriledetail.IsEms = 1

                AND         steriledetail.DocNo = '$DOC_NO'
                AND         itemstock.B_ID = '$B_ID'";

    if ($p_DB == 0) {
        $meQuery = mysqli_query($conn, $strSQL);
        while ($Result = mysqli_fetch_assoc($meQuery)) {
            $ID = $Result["ID"];

            $strSQL = " UPDATE  steriledetail
                        SET     steriledetail.IsEms = 2
                        WHERE   steriledetail.ID = '$ID'";

            mysqli_query($conn, $strSQL);

            array_push(
                $resArray,
                array(
                    'finish' => true,
                )
            );
        }

    } else {

        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();

        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $ID = $Result["ID"];

            $strSQL = " UPDATE  steriledetail
                        SET     steriledetail.IsEms = 2
                        WHERE   steriledetail.ID = '$ID'";

            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();

            array_push(
                $resArray,
                array(
                    'finish' => true,
                )
            );
        }
    }
}

// ========================================

echo json_encode(array("result" => $resArray));

unset($conn);
die;

?>
