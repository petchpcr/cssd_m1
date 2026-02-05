<?php
//EDIT LOG
//23-01-2026 15.41 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$usagecode = $_POST['usagecode'];
$sel = $_POST['sel'];
$itemname = $_POST['itemname'];
$itemcode = $_POST['itemcode'];
$type = $_POST['type'];
$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];
// ========================================

$i = 0;

if ($type == 'remark') {
    // $strSQL = "	SELECT
    //                 RemarkAdmin.Pictruetext AS Picture
    //             FROM
    //                 RemarkAdmin
    //             WHERE
    //                 RemarkAdmin.SensterileDocNo = '$sel'
    //             AND RemarkAdmin.ItemCode = '$itemcode'
    //             AND RemarkAdmin.IsPicture = 1 ";
    // $meQuery = mysqli_query($conn, $strSQL);
    // while ($Result = mysqli_fetch_assoc($meQuery)) {
    //     $i++;
    //     $Picture = $Result["Picture"];
    // }

    $strSQL = "	SELECT
                    remarkadmin.Pictruetext AS Picture,
                    remarktype.NameType,
                    remarkadmin.Note
                FROM
                    remarkadmin
                INNER JOIN remarktype 
                ON remarkadmin.RemarkTypeID = remarktype.ID
                WHERE
                    remarkadmin.SensterileDocNo = '$sel'
                AND remarkadmin.ItemCode = '$itemcode'
                AND remarkadmin.IsPicture = 1 
                AND remarkadmin.B_ID = '$B_ID' ";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $i++;
        $Picture = $Result["Picture"];
        $NameType = $Result["NameType"];
        $Note = $Result["Note"];
    }

    if ($i == 0) {
        array_push(
            $resArray,
            array(
                'Picture' => $Picture,
                'NameType' => $NameType,
                'Note' => $Note,
                'finish' => false,
            )
        );
    } else {
        array_push(
            $resArray,
            array(
                'Picture' => $Picture,
                'NameType' => $NameType,
                'Note' => $Note,
                'finish' => true,
            )
        );
    }
}else {
    $strSQL = "	SELECT
                itemstock.RowID
            FROM
                itemstock
            WHERE
                itemstock.UsageCode = '$usagecode'
                AND itemstock.B_ID = '$B_ID' ";
    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $i++;
        $RowID = $Result["RowID"];

        $strSQL = "	SELECT
                    steriledetail.ID
                FROM
                    steriledetail
                WHERE
                    steriledetail.ItemStockID = '$RowID'
                    AND steriledetail.B_ID = '$B_ID' ";
        $meQuery = $conn->prepare($strSQL);
        $meQuery->execute();
        while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $ID = $Result["ID"];
            $i++;
        }

        if ($sel == 2) {
            $strSQL = "	SELECT
                        item.Picture
                    FROM
                        item
                    WHERE
                        item.itemname = '$itemname'
                        AND item.B_ID = '$B_ID'
                    GROUP BY
                        Picture";
            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $Picture = $Result["Picture"];
                $i++;
            }

            $strSQL = "	SELECT
                        item.itemname AS itemnamesel2
                    FROM
                        itemstock
                    INNER JOIN item ON itemstock.ItemCode = item.itemcode
                    WHERE
                        itemstock.UsageCode = '$usagecode'
                        AND itemstock.B_ID = '$B_ID'
                    GROUP BY
                        item.itemname";
            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $itemnamesel2 = $Result["itemnamesel2"];
                $i++;
            }
        } else {
            $strSQL = "	SELECT
                        item.Picture
                    FROM
                        itemstock
                    INNER JOIN item ON itemstock.ItemCode = item.itemcode
                    WHERE
                        itemstock.RowID = '$RowID'";
            $meQuery = $conn->prepare($strSQL);
            $meQuery->execute();
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                $Picture = $Result["Picture"];
                $i++;
            }
        }
    }


    if ($i == 0) {
        array_push(
            $resArray,
            array(
                'ID' => $ID,
                'Picture' => $Picture,
                'itemnamesel2' => $itemnamesel2,
                'finish' => false,
            )
        );
    } else {
        array_push(
            $resArray,
            array(
                'ID' => $ID,
                'Picture' => $Picture,
                'itemnamesel2' => $itemnamesel2,
                'finish' => true,
            )
        );
    }
	
}
// ========================================			

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

?>