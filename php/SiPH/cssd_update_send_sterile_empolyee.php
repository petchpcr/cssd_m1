<?php
//EDIT LOG
// 23-01-2026 10.29 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
require 'class.php';

$array = array();

$d_em = $_POST['d_em'];
$d_dep = $_POST['d_dep'];
$d_docno = $_POST['d_docno'];
$d_issend = $_POST['d_issend'];
$d_isreceive = $_POST['d_isreceive'];
$B_ID = $_POST['B_ID'];
$p_DB = $_POST['p_DB'];

$Sql = "SELECT     employee.ID

        FROM       employee

        WHERE      employee.EmpCode = '$d_em'
        AND        employee.B_ID = $B_ID";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();	

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $p_ID = $Result["ID"];

    if ($d_issend == '1'){

        $Sql2 ="UPDATE  sendsterile

                SET     sendsterile.UserSend = '$p_ID'

                WHERE   sendsterile.DocNo = '$d_docno'";

        $meQuery = $conn->prepare($Sql2);
        $meQuery->execute();

    }else {

        $Sql2 ="UPDATE  sendsterile

                SET     sendsterile.UserReceive = '$p_ID'

                WHERE   sendsterile.DocNo = '$d_docno'";

        $meQuery = $conn->prepare($Sql2);
        $meQuery->execute();

    }
    
}

array_push($array,
    array('ID' => $p_ID,
    )
);

echo json_encode(array("result" => $array));

unset($conn);
    die;

?>