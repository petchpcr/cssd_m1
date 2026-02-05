<?php
//EDIT LOG
//23-01-2026 12.30 : ตรวจสอบ Building_ID (B_ID) ไม่มีการเพิ่ม B_ID
require 'connect.php';

$resArray = array();
$p_DB = $_POST['p_DB'];

function onChangeDepartmentItem($conn, $d_usagecode, $d_dept_id)
{

	// ---------------------------------------------
	// Update Department ItemStock 
	// ---------------------------------------------

	$sql_update_ = "UPDATE  itemstock
                    SET     itemstock.DepID = '$d_dept_id'
                    WHERE   itemstock.UsageCode = '$d_usagecode'";

	$query1 = $conn->prepare($sql_update_);
	$query1->execute();
	return 1;
}

function onChangeDepartment($conn, $d_usagecode, $d_dept_id)
{

	$d_return = 0;

	// ---------------------------------------------
	// Check Create Receive Department
	// ---------------------------------------------

    $d_return = onChangeDepartmentItem($conn, $d_usagecode, $d_dept_id);

	return $d_return;

}

//=============================================
// Main
//=============================================

if ( isset($_POST["p_usagecode"]) ) {	

    if ( isset($_POST["p_dept_id"]) ) {
        $p_dept_id = $_POST['p_dept_id'];
    }

    if ( isset($_POST["p_usagecode"]) ) {
        $p_usagecode = $_POST['p_usagecode'];
    }
    
    $d_return = onChangeDepartment($conn, $p_usagecode, $p_dept_id);

	// ---------------------------------------------
	// Return 
	// ---------------------------------------------

	array_push( 
		$resArray,array(
			'result'=>"A",
			'SQL'=>''
		)
	);

}else{
	array_push( 
		$resArray,array(
			'result'=>"I",
			'Message'=>'ข้อมูลที่ส่งมาไม่ถูกต้อง!!',
			'SQL'=>''
		)
	);
}

unset($conn);
die;
		
echo json_encode(array("result"=>$resArray));

?> 
