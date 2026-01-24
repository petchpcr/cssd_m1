<?php
//EDIT LOG
//24-01-2026 9.17 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
date_default_timezone_set("Asia/Bangkok");
$resArray = array();

$DocNo = $_POST['DocNo'];
$AtpTest = $_POST['AtpTest'];
$Code = $_POST['Code'];
$Remark = $_POST['Remark'];
$B_ID = $_POST['B_ID'];
$EmpCode = $_POST['EmpCode'];

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

$TestProgramName = $_POST['TestProgramName']??"-";
// if ($TestProgramName == "") {
// 	$TestProgramName = "-";
// }else {
// 	$TestProgramName = $_POST['TestProgramName'];
// }

if (!$AtpTest == 1) {
    $AtpTest == FALSE;
}else {
    $AtpTest == TRUE;
}

$CountAtp =0;
$strSQL = " SELECT
				testresult.CountAtp
			FROM
				testresult
			WHERE
				testresult.DocNo = '$DocNo'
			AND testresult.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
	$CountAtp = $Result["CountAtp"];
}

$strSQL = " SELECT	testresult.ID

			FROM	testresult 
			WHERE testresult.B_ID = $B_ID ";

$meQuery = $conn->prepare($strSQL);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
	$ID = $Result["ID"];
}

$strSQL = " SELECT	testprogram.ID AS TestProgram
			FROM	testprogram
			WHERE 	testprogram.TestProgramName = '$TestProgramName'
			AND 	testprogram.B_ID = $B_ID ";
			
$meQueryTestProgram = $conn->prepare($strSQL);
$meQueryTestProgram->execute();

while ($Result = $meQueryTestProgram->fetch(PDO::FETCH_ASSOC)) {
	$TestProgram = $Result["TestProgram"];
	
	//sterile
	$strSQL = " SELECT
				sterile.DocNo,
				sterile.TestProgramID
			FROM
				sterile
			WHERE
				sterile.DocNo = '$DocNo'
			AND sterile.B_ID = $B_ID ";

	$meQuery = $conn->prepare($strSQL);
	$meQuery->execute();
	
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
		$SqlSS = "  UPDATE sterile
                SET sterile.TestProgramID = '$TestProgram'
                WHERE
                    sterile.DocNo = '$DocNo'
				AND sterile.B_ID = $B_ID ";
		$meQueryUPDATE = $conn->prepare($SqlSS);
		$meQueryUPDATE->execute();
	}
	//wash
	$strSQL = " SELECT
				wash.DocNo,
				wash.TestProgramID
			FROM
				wash
			WHERE
				wash.DocNo = '$DocNo'
				AND wash.B_ID = $B_ID ";

	$meQuery = $conn->prepare($strSQL);
	$meQuery->execute();
	
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
		$SqlSS = "  UPDATE wash
                SET wash.TestProgramID = '$TestProgram'
                WHERE
                    wash.DocNo = '$DocNo'";
		
		$meQueryUPDATE = $conn->prepare($SqlSS);
		$meQueryUPDATE->execute();
	}
}

$SqlIn = "INSERT INTO testresult_log (UserApprove,Dateprocess,Process,DocNo,B_ID) VALUES ('$EmpCode',$date,'SaveATP','$DocNo',$B_ID)";

$meQuery = $conn->prepare($SqlIn);
$meQuery->execute();

$Sql = "INSERT INTO testresult (
			DocNo,
			DocDate,
			IsATPRapidTest,
			UserApprove,
			ATP_Code,
			Remark,
			IsATP,
			ProgramTest,
			IsResultTest,
			B_ID,
			CountAtp
		)
		VALUES
			(
				'$DocNo',
				$date,
				$AtpTest,
				'$EmpCode',
				'$Code',
				'$Remark',
				1,
				$TestProgram,
				$AtpTest,
				$B_ID,
				$CountAtp + 1 
			)";  

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

if($meQuery ->rowCount() != 0){
  array_push($resArray,array('finish' => 'true'));
}else{
  array_push($resArray,array('finish' => 'false','sql'=> $Sql));
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>