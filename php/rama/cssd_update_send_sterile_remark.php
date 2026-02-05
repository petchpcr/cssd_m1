<?php
//EDIT LOG
// 23-01-2026 12.49 : ตรวจสอบการใช้ Building_ID (B_ID) ในการ update ไม่มีการเพิ่ม B_ID ลงไป
require 'connect.php';
require 'class.php';

$array = array();

$DocNo = $_POST['DocNo'];
$remark = $_POST['remark'];
$Check = $_POST['check'];
$B_ID = $_POST['B_ID'];
$p_DB = $_POST['p_DB'];

if ($Check == 1) {
    $Sql1 = "   UPDATE  sendsterile

                SET     sendsterile.Remark ='$remark'

                WHERE   sendsterile.DocNo = '$DocNo'";

    $meQuery = $conn->prepare($Sql1);
    $meQuery->execute();

} else if ($Check == 2) {
    $Sql2 = "   UPDATE  sendsteriledetail

                SET     sendsteriledetail.Remark ='$remark'

                WHERE   sendsteriledetail.ID = '$DocNo'";

    $meQuery = $conn->prepare($Sql2);
    $meQuery->execute();
}

array_push($array,
    array('flag' => "true",
    )
);

echo json_encode(array("result" => $array));

unset($conn);
die;

?>