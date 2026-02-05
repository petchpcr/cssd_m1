<?php 
    require '../connect.php';
    $resArray = array();
    if(isset($_POST["ID"])) {
        $ImageData1 = $_POST['image1'];
        $ImageName1 = $_POST['name1'];
        $picnum = $_POST['picnum'];

        $ID = $_POST['ID'];
        
        $ImagePath1 = $ImageName1.".PNG";
        // $ImagePath2 = $ImageName2.".PNG";

        if($picnum == 1){
            $insertSQL = "  UPDATE remarkadmin
                            SET remarkadmin.Picture = '$ImagePath1',
                                remarkadmin.IsPicture = 1,
                                remarkadmin.Pictruetext = '$ImageData1'
                            WHERE
                                remarkadmin.ID = '$ID'";
        }else if($picnum == 2){
            $insertSQL = "  UPDATE remarkadmin
                            SET remarkadmin.Pictrue2 = '$ImagePath1',
                                remarkadmin.IsPicture = 1,
                                remarkadmin.Pictruetext2 = '$ImageData1'
                            WHERE
                                remarkadmin.ID = '$ID'";
        }else if($picnum == 3){
            $insertSQL = "  UPDATE remarkadmin
                            SET remarkadmin.Pictrue3 = '$ImagePath1',
                                remarkadmin.IsPicture = 1,
                                remarkadmin.Pictruetext3 = '$ImageData1'
                            WHERE
                                remarkadmin.ID = '$ID'";
        }
        
        $b1 = false;
        $query1 = $conn->prepare($insertSQL);
         if($query1->execute()){
            $b1 = file_put_contents($ImagePath1,base64_decode($ImageData1));
            if($b1){
                array_push($resArray, array('finish' => 'true1'));
            }else{
                array_push($resArray,array('finish' => 'false1'));
            }
         }else{
           array_push($resArray,array('finish' => 'false2'));
         }
    } else {
        array_push($resArray,array('finish' => 'false3'));
    }
    echo json_encode(array("result"=>$resArray));

    unset($conn);
    die;
?>