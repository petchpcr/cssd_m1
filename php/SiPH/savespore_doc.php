<?php
//EDIT LOG
//24-01-2026 9.17 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$DocNo = $_POST['DocNo'];
$ProGram = $_POST['ProGram'];
$AtpTest = $_POST['AtpTest'];
$Code = $_POST['Code'];
$Test = $_POST['Test'];
$Remark = $_POST['Remark'];
$Page = $_POST['Page'];
$EmpCode = $_POST['EmpCode'];
$B_ID = $_POST['B_ID'];
$Point_test = $_POST['Point_test'];

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

if (!$AtpTest == 1) {
    $AtpTest == FALSE;
}else {
    $AtpTest == TRUE;
}

// if (!$Test == 1) {
//     $Test ==FALSE;
// }else {
//     $Test == TRUE;
// }

if (!$ProGram == null) {
	$ProGram;
} else {
	$ProGram = 0;
}

if (!$Point_test == null) {
	$Point_test;
} else {
	$Point_test = '';
}

$Sql = 	"INSERT INTO testresult (
								IsATPRapidTest,
								DocNo,
								DocDate,
								ProgramTest,
								Remark,
								UserApprove,
								B_ID,
								Point_test,
								IsATP,
								IsResultTest
					) VALUES (
								null,
								'$DocNo',
								$date,
								$ProGram,
								'$Remark',
								'$EmpCode',
								$B_ID,
								'$Point_test',
								0,
								$AtpTest
								)";   

$meQuery = $conn->prepare($Sql);

if($meQuery->execute()){
  array_push($resArray,array('finish' => 'true'));
}else{
  array_push($resArray,array('finish' => 'false','sql' => $Sql));
}

$SqlIn = "INSERT INTO testresult_log (UserApprove,Dateprocess,Process,DocNo,B_ID)VALUES ('$EmpCode',$date,'SaveTest','$DocNo',$B_ID)";
$meQuery = $conn->prepare($SqlIn);
$meQuery->execute();

if($p_DB == 0){
	if (!$Page == 0){
		$sql_update = "UPDATE sterile SET IsActive = TRUE WHERE DocNo = '$DocNo' ";
	}else {
		$sql_update = "UPDATE wash SET IsActive = TRUE WHERE DocNo = '$DocNo' ";
	}
	
}else if($p_DB == 1){
	if (!$Page == 0){
		$sql_update = "UPDATE sterile SET IsActive = 1 WHERE DocNo = '$DocNo' ";
	}else {
		$sql_update = "UPDATE wash SET IsActive = 1 WHERE DocNo = '$DocNo' ";
	}
}

$meQuery = $conn->prepare($sql_update);
$meQuery->execute();

if (!$Page == 0){
	$sql_update1 = "UPDATE sterile SET TestProgramID = '$ProGram' WHERE DocNo = '$DocNo' ";
}else {
	$sql_update1 = "UPDATE wash SET TestProgramID = '$ProGram' WHERE DocNo = '$DocNo' ";
}
$meQuery = $conn->prepare($sql_update1);
$meQuery->execute();

echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>