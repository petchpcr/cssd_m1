<?php
//EDIT LOG
//24-01-2026 9.22 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
date_default_timezone_set("Asia/Bangkok");
require 'connect.php';
$resArray = array();

$p_DB = $_POST['p_DB'];

if($p_DB == 0){

	$top = " ";
	$limit = "LIMIT 1 ";
	$date = "NOW()";

}else if($p_DB == 1){
	
	$top = "TOP 1 ";
	$limit = " ";
	$date = "getDate()";

}

$B_ID = $_POST['B_ID'];

$type = $_POST['type'];
$program = $_POST['program'];
// ========================================
if ($type == 'no_std'){
    if ($program == 'Dirty Zone'){
        $strSQL = "	SELECT
                        testprogram.TestProgramName,
                        testprogram.ID
                    FROM
                        testprogram
                    WHERE
                        testprogram.ID != 0
                    AND testprogram.Zone = 1
                    AND testprogram.B_ID = $B_ID";
    }else if($program == 'Clean Zone'){
        $strSQL = "	SELECT
                        testprogram.TestProgramName,
                        testprogram.ID
                    FROM
                        testprogram
                    WHERE
                        testprogram.ID != 0
                    AND testprogram.Zone = 2
                    AND testprogram.B_ID = $B_ID";
    }else if($program == 'Sterile Zone'){
        $strSQL = "	SELECT
                        testprogram.TestProgramName,
                        testprogram.ID
                    FROM
                        testprogram
                    WHERE
                        testprogram.ID != 0
                    AND testprogram.Zone = 3
                    AND testprogram.B_ID = $B_ID";
    }

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
            $TestProgramName = $Result["TestProgramName"];
            $ID = $Result["ID"];
            array_push(
                $resArray,
                array(
                    'TestProgramName' => $TestProgramName,
                    'ID' => $ID,
                    'finish' => true,
                )
            );
    }
}else {
    if ($type == 1) {
        $strSQL = "	SELECT
                        testprogram.TestProgramName,
                        testprogram.ID
                    FROM
                        testprogram
                    WHERE testprogram.TestProgramType = 1
                    AND testprogram.ID != 0
                    AND testprogram.Zone = 0
                    AND testprogram.TestProgramName != '-'
                    AND testprogram.B_ID = $B_ID";

    }else {
        $strSQL = "	SELECT
                        testprogram.TestProgramName,
                        testprogram.ID
                    FROM
                        testprogram
                    WHERE testprogram.TestProgramType = 0
                    AND testprogram.ID != 0
                    AND testprogram.Zone = 0
                    AND testprogram.TestProgramName != '-'
                    AND testprogram.B_ID = $B_ID";
    }

    $meQuery = $conn->prepare($strSQL);
    $meQuery->execute();

    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        $TestProgramName = $Result["TestProgramName"];
        $ID = $Result["ID"];
        array_push(
            $resArray,
            array(
                'TestProgramName' => $TestProgramName,
                'ID' => $ID,
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
