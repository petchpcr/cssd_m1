<?php

//EDIT LOG
//24-01-2026 9.22 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();
  
    $DocNo = $_POST['DocNo'];
    $B_ID = $_POST['B_ID'];
    
              $Sql = "SELECT
                        testresult.DocNo,
                        testprogram.TestProgramName AS ProgramTest,
                        testresult.IsATPRapidTest,
                        testresult.Point_test,
                        testresult.IsResultTest,
                        testresult.Remark,
                        testresult.Pic1,
                        testresult.Pic2,
                        testresult.img_pic1,
                        testresult.img_pic2
                      FROM
                        testresult
                      INNER JOIN testprogram ON testresult.ProgramTest = testprogram.ID
                      WHERE
                        testresult.DocNo = '$DocNo'
                      AND testresult.B_ID = '$B_ID'
                      AND testresult.IsATP != 1";
    
    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();
    
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
      array_push($resArray,array(
      'DocNo' => $Result['DocNo'],
      'ProgramTest' => $Result['ProgramTest'],
      'IsATPRapidTest' => $Result['IsATPRapidTest'],
      'Point_test' => $Result['Point_test'],
      'IsResultTest' => $Result['IsResultTest'],
      'Remark' => $Result['Remark'],
      'Pic1' => $Result['Pic1'],
      'Pic2' => $Result['Pic2'],
      'img_pic1' => $Result['img_pic1'],
      'img_pic2' => $Result['img_pic2'],
      ));
    }
    echo json_encode(array("result"=>$resArray));
    
    unset($conn);
    die;
?>
