<?php
//EDIT LOG
// 23-01-2026 12.52 : เพิ่ม Building_ID (B_ID) ในการดึงข้อมูล
require 'connect.php';
require 'class.php';

$array = array();
$dateobj = new DatetimeTH();

if (isset($_POST["search"])) {
    $search = $_POST['search'];
} else {
    $search = "";
}

if (isset($_POST["status"])) {
    $status = $_POST['status'];
} else {
    $status = "0";
}

if (isset($_POST["department_id"])) {
    $department_id = $_POST['department_id'];
} else {
    $department_id = "";
}

if (isset($_POST["p_wash_dep"])) {
    $p_wash_dep = $_POST['p_wash_dep'];
} else {
    $p_wash_dep = "1";
}

if (isset($_POST["date"])) {
  $date = $_POST['date'];
  $date = explode("/", $date);
  $datetime = $date[2] . "-" . $date[1] . "-" . $date[0];
}else{
  $datetime = "";
}

if (isset($_POST["p_is_non_select_department"])) {
    $p_is_non_select_department = $_POST['p_is_non_select_department'];
} else {
    $p_is_non_select_department = "";
}

$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

if($p_DB == 0){

    $top = " ";
    $limit = "LIMIT 1";

    $date = " DATE(sendsterile.DocDate) AS DocDate ";

    $Datetime = " AND DATE(sendsterile.DocDate) = DATE('$datetime') ";


}else if($p_DB == 1){
    
    $top = "TOP 1";
    $limit = " ";

    $date = " CONVERT(DATE,sendsterile.DocDate) AS DocDate ";

    $Datetime = " AND FORMAT(sendsterile.DocDate , 'yyyy-MM-dd') = '$datetime' ";

}

$Sql = "        SELECT      sendsterile.DocNo,
                            CONVERT(DATE,sendsterile.DocDate) AS DocDate ,
                            department.ID,
                            COALESCE (department.DepName2, '') AS DepName2,
                            sendsterile.Qty,
                            sendsterile.Remark,
                            sendsterile.IsStatus,
                            cast(sendsterile.DocDate AS TIME) AS xtime,
                            UserReceive,
                            UserSend,
                            sendsterile.ZoneID,
                            COALESCE ( CONCAT(employee_1.FirstName, ' ', employee_1.LastName), '') AS usr_receive,
                            COALESCE ( CONCAT(employee_2.FirstName, ' ', employee_2.LastName), '') AS usr_send,
                            (CASE sendsterile.IsWeb WHEN 0 THEN 'Desktop' WHEN 1 THEN 'Web' WHEN 2 THEN 'Mobile' ELSE 'Web CSSD' END) AS Send_From,
                            sendsterile.IsWeb,
                            (
                                SELECT Count( ss.DocNo ) AS Qty
                                FROM sendsterile AS ss
                                INNER JOIN sendsteriledetail AS ssd
                                ON ss.DocNo = ssd.SendSterileDocNo
                                WHERE ss.DocNo = sendsterile.DocNo
                                AND ss.B_ID = '$B_ID'
                            ) AS Qty1,
                            (
                                SELECT SUM( ssd.Qty ) AS Qty
                                FROM sendsterile AS ss
                                INNER JOIN sendsteriledetail AS ssd
                                ON ss.DocNo = ssd.SendSterileDocNo
                                WHERE ss.DocNo = sendsterile.DocNo
                                AND ss.B_ID = '$B_ID'
                            ) AS Qty2

                FROM        sendsterile

                LEFT JOIN   department
                ON          department.ID=sendsterile.DeptID

                LEFT JOIN   employee AS employee_1
		        ON 		    employee_1.ID = sendsterile.UserReceive

                LEFT JOIN 	employee AS employee_2
		        ON 	        employee_2.ID = sendsterile.UserSend

                WHERE       sendsterile.IsCancel = 0
                AND         sendsterile.IsStatus <> 3

                AND         sendsterile.B_ID = '$B_ID' ";

    if ($p_wash_dep == '1') {
        $Sql = $Sql . "AND        IsWashDept = 1 ";
    }else{
        $Sql = $Sql . "AND        IsWashDept = 0 ";
    }

if(isset($_POST["is_admin"])){
    
    $Sql = $Sql . "AND       DATE(sendsterile.DocDate) = DATE(NOW()) ";

    if ($search != '') {
        $Sql = $Sql . "AND       sendsterile.DocNo LIKE '%$search%' ";
    }

    if ($department_id != '') {
        $Sql = $Sql . "AND       sendsterile.DeptID ='$department_id' ";
    }

}else{

    if ($search != '') {
        $Sql = $Sql . "AND       sendsterile.DocNo LIKE '%$search%' ";
    }

    if ($datetime != '') {
        $Sql = $Sql . $Datetime;
    }

    if ($department_id != '') {
        $Sql = $Sql . "AND       sendsterile.DeptID = '$department_id' ";
    }

    if ($p_is_non_select_department != '') {
        $Sql = $Sql . "AND       sendsterile.IsNonSelectDepartment = 1 ";
    }

    if ($status == '-1') {
        $Sql = $Sql . "AND       (sendsterile.IsStatus = 0 OR sendsterile.IsStatus = 1 OR sendsterile.IsStatus = 2) ";
    }else{
        $Sql = $Sql . "AND       (sendsterile.IsStatus = $status OR sendsterile.IsStatus = 4)";
    }

    if (isset($_POST["p_is_doc"])) {
        $Sql = $Sql . "AND       sendsterile.DocNo LIKE 'S%' ";
    }
}

$Sql = $Sql . "ORDER BY    sendsterile.DocNo DESC ";

$meQuery = $conn->prepare($Sql);
$meQuery->execute();

while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {

    $DocNo = $Result["DocNo"];
    $date = explode("-", $Result["DocDate"]);
    $ptime = explode(":", $Result["xtime"]);
    $xtime = $ptime[0] . ":" . $ptime[1];
    $datetime = $date[2] . "/" . $date[1] . "/" . $date[0];
    $DocDate = $datetime;
    $DeptID = $Result["ID"];
    $DepName2 = $Result["DepName2"];
    $Qty = $Result["Qty"];
    $Remark = $Result["Remark"];
    $IsStatus = $Result["IsStatus"];

    array_push($array,
        array('flag' => "true",
            'DocNo' => $DocNo,
            'DocDate' => $DocDate,
            'DeptID' => $DeptID,
            'DepName2' => $DepName2,
            'Qty' => $Qty,
            'Remark' => $Remark,
            'IsStatus' => $IsStatus,
            'xtime' => $xtime,
            'UserReceive' => $Result["UserReceive"],
            'UserSend' => $Result["UserSend"],
            'usr_receive' => $Result["usr_receive"],
            'usr_send' => $Result["usr_send"],
            'Send_From' => $Result["Send_From"],
            'IsWeb' => $Result["IsWeb"],
            'Qty1' => $Result["Qty1"],
            'Qty2' => $Result["Qty2"],
            'ZoneID' => $Result["ZoneID"],
        )
    );
}

echo json_encode(array("result" => $array));

unset($conn);
die;

?>