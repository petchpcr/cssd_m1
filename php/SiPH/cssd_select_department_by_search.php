<?php
//EDIT LOG
// 23-01-2026 10.47 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$array = array();

$sql_condition = "";

if (isset($_POST["p_DeptID"])) {
    $p_div_id = $_POST['p_div_id'];

    $sql_condition = "AND DivID = $p_div_id ";
}

if (isset($_POST["p_id"])) {
    $p_id = $_POST['p_id'];

    $sql_condition = "AND ID = $p_id ";
}

$B_ID = $_POST['B_ID'];

$Sql = "SELECT 		ID,
					DepName2 AS xName

		FROM 		department

        WHERE       B_ID = $B_ID

		$sql_condition

		ORDER BY 	DepName2 ASC ";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();
$d_complete = 0;

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

    $xID = $Result["ID"];
    $xName = $Result["xName"];

    array_push(
        $array, array(
            'result' => "A",
            'xID' => $xID,
            'xName' => $xName,
        )
    );

    $d_complete = 1;
}

if ($d_complete == 0) {
    array_push(
        $array, array(
            'result' => "E",
            'Message' => "กรุณาเลือกแผนก",
        )
    );
}

echo json_encode(array("result" => $array));

unset($conn);
die;

?>