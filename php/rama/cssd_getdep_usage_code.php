<?php 
//EDIT LOG
// 23-01-2026 10.27 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
    require 'connect.php';
 
    $resArray = array();
        $p_DB = $_POST['p_DB'];
        $p_qr = $_POST['p_qr'];
        $DocNo = $_POST['DocNo'];
        $B_ID = $_POST['B_ID'];
        // $p_DB = 1;
        // $p_qr = "I00215-219-00002";

        $insertSQL = "  SELECT 
                            itemstock.DepID
                        FROM
                            dbo.itemstock
                            WHERE itemstock.UsageCode='$p_qr'
                            AND itemstock.B_ID = $B_ID";

        $query1 = $conn->prepare($insertSQL);
        $query1->execute();
        if (!empty($query1)) {
            while ($row = $query1->fetch(PDO::FETCH_ASSOC)) {
                array_push( 
                    $resArray,array(
                        'result'=>"A",
                        'DepID'=>$row["DepID"],
                    )
                );
            }		

        } else {

            array_push( 
                $resArray,array(
                    'result'=>"E",
                    'Message'=>'ไม่พบข้อมูล!!',
                    'SQL'=>$strSQL,
                )
            );

        }
   
    echo json_encode(array("result"=>$resArray));
    

    unset($conn);
    die;
?>