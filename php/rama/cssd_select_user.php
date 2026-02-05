<?php
//EDIT LOG
//23-06-2024 12.33 : เพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

$B_ID = $_POST['B_ID'];
	
$Sql = "SELECT 		users.ID AS ID ,
					employee.FirstName AS UserName,
					CONCAT(employee.EmpCode, ' : ', employee.FirstName, ' ', employee.LastName) AS LabelName  
		FROM 		users,employee 
		WHERE 		IsCancel = 0 
		AND 		employee.EmpCode = users.EmpCode 
		AND 		users.B_ID = '$B_ID'";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();
$i=0;

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)){
	$xId = $Result["ID"];
	$UserName = $Result["UserName"];
	$LabelName = $Result["LabelName"];

	array_push( $resArray,array('xID'=>$xId,'xName'=>$UserName, 'LabelName'=>$LabelName));
	$i++;
}

echo json_encode(array("result"=>$resArray));

unset($conn);
die;

	
?>
