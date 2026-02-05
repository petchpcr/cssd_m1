<?php
//EDIT LOG
//24-01-2026 8.53 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$data = $_POST['data'];
$B_ID = $_POST['B_ID'];
$i = 0;
// ========================================
$strSQL = "	SELECT  remarktype.NameType,
	                remarktype.ID,
                    remarktype.Opt
            FROM    remarktype
            WHERE   remarktype.Iscancel != 1
            AND     remarktype.B_ID = $B_ID";
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $i++;
    $NameType = $Result["NameType"];
    $ID = $Result["ID"];
    $Opt = $Result["Opt"];

    array_push(
        $resArray,
        array(
            'NameType' => $NameType,
            'ID' => $ID,
            'Opt' => $Opt,
            'finish' => true,
        )
    );	    
}


if ($i == 0) {
    array_push(
        $resArray,
        array(
            'NameType' => "",
            'ID' => "",
            'Opt' => "",
            'finish' => false,
        )
    );	  
}
	
// ========================================
			
echo json_encode(array("result"=>$resArray));

unset($conn);
die;
 
?>