<?php 
    require '../connect.php';
    $resArray = array();
    if(isset($_GET["DocNo"])) {
        $ImageData1 = $_GET['image1'];
        $ImageName1 = $_GET['name1'];
        
        $ImageData2 = $_GET['image2'];
        $ImageName2 = $_GET['name2'];

        $DocNo = $_GET['DocNo'];
        
        $ImagePath1 = $ImageName1.".PNG";
        $ImagePath2 = $ImageName2.".PNG";


         if($ImageData2=="null"){
             $insertSQL = "UPDATE item SET Picture = '$ImagePath1' WHERE itemcode = '$DocNo' ";
         }else{
             $insertSQL = "UPDATE item SET Picture = '$ImagePath1', Picture2 = '$ImagePath2' WHERE itemcode = '$DocNo' ";
         }
        $b1 = false;
        $b2 = false;
         if(mysqli_query($conn, $insertSQL)){

            move_uploaded_file($ImagePath1,base64_decode($ImageData1));

            //$b1 = file_put_contents($ImagePath1,base64_decode($ImageData1));

            if($ImageData2!="null"){
                $b2 = file_put_contents($ImagePath2,base64_decode($ImageData2));
            }
            if($b1){
                if($b2){
                    array_push($resArray,array('finish' => 'true2'));
                }else{
                    array_push($resArray,array('finish' => 'true1'));
                }
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
    mysqli_close($conn);
?>