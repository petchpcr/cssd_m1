<?php
//EDIT LOG
//24-01-2026 9.24 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();
  
    $DocNo = $_POST['DocNo'];
    $B_ID = $_POST['B_ID'];
    $Page = $_POST['Page'];

            if ($Page == 0) {
                $Sql = "SELECT
                            washmachine.MachineName AS WashMachineID,
                            wash.WashRoundNumber AS WashRoundNumber
                        FROM
                            wash
                        INNER JOIN washmachine ON wash.WashMachineID = washmachine.ID
                        WHERE
                            wash.DocNo = '$DocNo'
                        AND wash.B_ID = '$B_ID'
                        ORDER BY
                            wash.DocNo ASC";
            } else if ($Page == 1) {
                $Sql = "SELECT
                            sterilemachine.MachineName2 AS SterileMachineID,
                            sterile.SterileRoundNumber AS SterileRoundNumber
                        FROM
                            sterile
                        INNER JOIN sterilemachine ON sterile.SterileMachineID = sterilemachine.ID
                        WHERE
                            sterile.DocNo = '$DocNo'
                        AND sterile.B_ID = '$B_ID'
                        ORDER BY
                            sterile.DocNo ASC";
            }

            $meQuery = $conn->prepare($Sql);
            $meQuery->execute();
            
            while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
                    array_push($resArray, array(
                        'WashMachineID' => $Result['WashMachineID'],
                        'WashRoundNumber' => $Result['WashRoundNumber'],
                        'SterileMachineID' => $Result['SterileMachineID'],
                        'SterileRoundNumber' => $Result['SterileRoundNumber'],
                    ));
            }
echo json_encode(array("result" => $resArray));
unset($conn);
die;
?>