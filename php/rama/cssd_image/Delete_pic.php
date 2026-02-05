<?php 
    require '../connect.php';
    $resArray = array();
    if(isset($_POST["ID"])) {
        // $ImageData1 = $_POST['image1'];
        $ImageName1 = $_POST['name1'];

        $ID = $_POST['ID'];
        
        // $ImagePath1 = $ImageName1.".PNG";
        // $ImagePath2 = $ImageName2.".PNG";

        unlink($ImageName1);

        $insertSQL = "  UPDATE remarkadmin
                        SET remarkadmin.Picture = NULL,
                            remarkadmin.IsPicture = 0
                        WHERE
                            remarkadmin.ID = '$ID'";

        $query1 = $conn->prepare($insertSQL);
        $query1->execute();
        array_push($resArray, array('finish' => 'true1'));
    } else {
        array_push($resArray,array('finish' => 'false3'));
    }
    echo json_encode(array("result"=>$resArray));
    
    unset($conn);
    die;
?>