<?php

//EDIT LOG
//24-01-2026 8.58 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require '../connect.php';

$resArray = array();

$slcMonth = $_POST['slcMonth'];
$slcYear = $_POST['slcYear'];

$p_DB = $_POST['p_DB'];
$B_ID = $_POST['B_ID'];

if($p_DB == 0){
    $Sql = "SELECT      smchk.ID,
                        IFNULL(DATE_FORMAT(smchk.CreateDate,'%d-%m-%Y'),'-') AS CreateDate,
                        IFNULL(DATE_FORMAT(smchk.LastUpdate,'%d-%m-%Y'),'-') AS LastUpdate,
                        sm.ID AS MachineID,
                        sm.MachineName2 AS MachineName,
                        CASE
                            WHEN smchk.IsResult = 0 THEN 'ไม่ผ่าน'
                            WHEN smchk.IsResult = 1 THEN 'ผ่าน'
                            ELSE 'รอตรวจสอบ'
                        END AS IsResult,
                        smchk.Pic1,
                        smchk.Pic2,
                        smchk.Remark,
                        IF(
                            (
                                $slcMonth < MONTH(NOW())
                                OR
                                $slcYear < YEAR(NOW())
                            ), 
                            false,
                            true
                        ) AS enableEdit
            FROM        sterilemachine sm
            LEFT JOIN   sterilemachine_check smchk 
            ON          smchk.MachineID = sm.ID 
            AND         MONTH(smchk.CreateDate) = $slcMonth
            AND         YEAR(smchk.CreateDate) = $slcYear
            AND         smchk.B_ID = $B_ID
            WHERE       sm.B_ID = $B_ID 
            ORDER BY    sm.MachineName2 ASC";

    $result = mysqli_query($conn, $Sql);
    while ($row = mysqli_fetch_array($result)) {
        array_push(
            $resArray,
            array(
                'ID' => $row["ID"],
                'CreateDate' => $row["CreateDate"],
                'LastUpdate' => $row["LastUpdate"],
                'MachineID' => $row["MachineID"],
                'MachineName' => $row["MachineName"],
                'IsResult' => $row["IsResult"],
                'Pic1' => $row["Pic1"],
                'Pic2' => $row["Pic2"],
                'Remark' => $row["Remark"],
                'enableEdit' => boolval($row["enableEdit"]),
            )
        );
    }

    mysqli_close($conn);

    echo json_encode(array("result"=>$resArray));
}else{
    $Sql = "SELECT      smchk.ID,
                        ISNULL(CONVERT(VARCHAR(10), CAST(smchk.CreateDate AS Date), 105),'-') AS CreateDate,
                        ISNULL(CONVERT(VARCHAR(10), CAST(smchk.LastUpdate AS Date), 105),'-') AS LastUpdate,
                        sm.ID AS MachineID,
                        sm.MachineName2 AS MachineName,
                        CASE
                            WHEN smchk.IsResult = 0 THEN 'ไม่ผ่าน'
                            WHEN smchk.IsResult = 1 THEN 'ผ่าน'
                            ELSE 'รอตรวจสอบ'
                        END AS IsResult,
                        smchk.Pic1,
                        smchk.Pic2,
                        smchk.Remark,
                        CASE
                            WHEN 11 < MONTH(GETDATE()) OR 2021 < YEAR(GETDATE()) THEN 1
                            ELSE 0 
                        END AS enableEdit
            FROM        sterilemachine sm
            LEFT JOIN   sterilemachine_check smchk 
            ON          smchk.MachineID = sm.ID 
            AND         MONTH(smchk.CreateDate) = $slcMonth
            AND         YEAR(smchk.CreateDate) = $slcYear
            AND         smchk.B_ID = $B_ID
            WHERE       sm.B_ID = $B_ID
            ORDER BY    sm.MachineName2 ASC";

    $result = $conn->prepare($Sql);
    $result->execute();
    while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        array_push(
            $resArray,
            array(
                'ID' => $row["ID"],
                'CreateDate' => $row["CreateDate"],
                'LastUpdate' => $row["LastUpdate"],
                'MachineID' => $row["MachineID"],
                'MachineName' => $row["MachineName"],
                'IsResult' => $row["IsResult"],
                'Pic1' => $row["Pic1"],
                'Pic2' => $row["Pic2"],
                'Remark' => $row["Remark"],
                'enableEdit' => boolval($row["enableEdit"]),
            )
        );
    }
    unset($conn);
    echo json_encode(array("result"=>$resArray));
}
