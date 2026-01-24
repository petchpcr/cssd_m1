<?php
//EDIT LOG
//24-01-2026 9.26 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();
  
$xSel = $_POST['xSel']??"";
$Date = $_POST['Date'];
$B_ID = $_POST['B_ID'];
$p_DB = $_POST['p_DB'];

if($p_DB == 0){
      $top = " ";
      $limit = " LIMIT 1";
      $datenow = " NOW() ";
      $dateonly = " DATE( ";
      $ifnull = "IFNULL";
}else if($p_DB == 1){
      $top = "TOP 1 ";
      $limit = " ";
      $datenow = " GETDATE() ";
      $dateonly = " CONVERT(DATE, ";
      $ifnull = "ISNULL";
}
    
if( $xSel == 0 ){
      $Sql = "    SELECT
                        wash.DocNo,
                        $ifnull(washtype.WashTypeName,'') AS WashTypeName,
                        wash.WashRoundNumber,
                        $ifnull(testprogram.TestProgramName,'-') AS TestProgramName,
                        wash.IsActive,
                        wash.TestProgramID AS ID,
                        washmachine.MachineName AS WashMachineID
                  FROM
                        wash
                  LEFT JOIN testprogram ON wash.TestProgramID = testprogram.ID
                  LEFT JOIN washtype ON wash.WashTypeID = washtype.ID
                  INNER JOIN washmachine ON wash.WashMachineID = washmachine.ID
                  INNER JOIN washdetail ON wash.DocNo = washdetail.WashDocNo
                  WHERE
                        $dateonly wash.DocDate) = '$Date'
                  AND wash.B_ID = $B_ID
                  AND testprogram.B_ID = $B_ID
                  AND wash.IsStatus != 0
                  AND washdetail.IsStatus != 0
                  GROUP BY
                        wash.DocNo,
                        WashTypeName,
				wash.WashRoundNumber,
                        testprogram.TestProgramName,
                        wash.IsActive,
				TestProgramID,
				MachineName
                  ORDER BY
                        wash.DocNo ASC";
}else if( $xSel == 1 ){
            $Sql = "    SELECT
                              sterile.DocNo,
                              $ifnull(sterileprogram.SterileName2,'') AS SterileName,
                              sterile.SterileRoundNumber,
                              $ifnull(testprogram.TestProgramName,'-') AS TestProgramName,
                              sterile.IsActive,
                              sterile.TestProgramID AS ID,
                              sterilemachine.MachineName2 AS SterileMachineID
                        FROM
                              sterile
                        LEFT JOIN testprogram ON sterile.TestProgramID = testprogram.ID
                        LEFT JOIN sterileprogram ON sterile.SterileProgramID = sterileprogram.ID
                        INNER JOIN sterilemachine ON sterile.SterileMachineID = sterilemachine.ID
                        WHERE
                              $dateonly sterile.DocDate) = '$Date'
                        AND sterile.B_ID = $B_ID
                        AND testprogram.B_ID = $B_ID
                        AND sterile.IsStatus != 0
                        GROUP BY
                              sterile.DocNo,
					SterileName2,
                              sterile.SterileRoundNumber,
                              testprogram.TestProgramName,
                              sterile.IsActive,
                              sterile.TestProgramID,
                              sterilemachine.MachineName2
                        ORDER BY
                              sterile.DocNo ASC";
      }

$meQuery = $conn->prepare($Sql);
$meQuery->execute();
	
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
      array_push($resArray,array(
      'DocNo' => $Result['DocNo'],

      'WashTypeName' => $Result['WashTypeName']??"",
      'WashMachineID' => $Result['WashMachineID']??"",
      'WashRoundNumber' => $Result['WashRoundNumber']??"",
      

      'SterileName' => $Result['SterileName']??"",
      'SterileMachineID' => $Result['SterileMachineID']??"",
      'SterileRoundNumber' => $Result['SterileRoundNumber']??"",

      'TestProgramName' => $Result['TestProgramName'],
      'IsActive' => $Result['IsActive'],
      'ID' => $Result['ID'],
      ));
}
echo json_encode(array("result"=>$resArray));

unset($conn);
die;
?>
