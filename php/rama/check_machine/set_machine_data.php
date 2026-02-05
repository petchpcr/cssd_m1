<?php
//EDIT LOG
//24-01-2026 8.57 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
// 13-02-2026 : แก้ไขยกเลิกใช้ B_ID เมื่อส่งค่าเป็น 0 (ทุกอาคาร)
require '../connect.php';

$p_DB = $_POST['p_DB'];

if (isset($_POST['id']) && isset($_POST['machineID']) && isset($_POST['userID']) && isset($_POST['isResult'])) {
    $firstTime = filter_var($_POST['firstTime'], FILTER_VALIDATE_BOOLEAN);
    $id = $_POST['id'];
    $machineID = $_POST['machineID'];
    $userID = $_POST['userID'];
    $isResult = $_POST['isResult'];
    $note = $_POST['note'];

    $B_ID = $_POST['B_ID'];
    $WHERE_B_ID = "";
    $INSERT_B_ID = "";
    $VALUES_B_ID = "";
    if($B_ID != "0"){
        $WHERE_B_ID = " WHERE B_ID = $B_ID";
        $INSERT_B_ID = ", B_ID";
        $VALUES_B_ID = ", $B_ID";
    }

    if($_POST['imageBase64Encode2'] == "null"){
        $imageBase64Encode = [$_POST['imageBase64Encode1']];
    }else{
        $imageBase64Encode = [$_POST['imageBase64Encode1'],$_POST['imageBase64Encode2']];
    }
    $filedName = ["Pic1", "Pic2"];
    $countImage = 1;
    $error = false;
    
    if($p_DB == 0){
        function getImgPath($conn){
            $B_ID = $_POST['B_ID'];
            $WHERE_B_ID = "";
            if($B_ID != "0"){
                $WHERE_B_ID = " WHERE B_ID = $B_ID";
            }
            $img_path = "";
            $strSQL = "SELECT MD_ImagePath FROM configuration $WHERE_B_ID";
            $result = mysqli_query($conn, $strSQL);
            while ($row = mysqli_fetch_array($result)) {
                $img_path = $row["MD_ImagePath"];
            }
            return $img_path;
        }
        
        if ($firstTime) { // Create new row
        
            $Sql = "INSERT INTO sterilemachine_check(CreateDate, MachineID, IsResult, Remark, UserCreate $INSERT_B_ID) 
                    VALUES (NOW(), $machineID, $isResult, '$note', $userID $VALUES_B_ID)";

            if ($conn -> query($Sql)) {
                $id = $conn -> insert_id;
                $resultComplete = "insert_success";
            } else {
                $error = true;
                $resultComplete = "insert_failed";
            }
                
        } else { // Update old row
            $Sql = "UPDATE sterilemachine_check 
                    SET     LastUpdate = NOW(),
                            MachineID = $machineID, 
                            IsResult = $isResult, 
                            Pic1 = NULL,
                            Pic2 = NULL,
                            Remark = '$note', 
                            UserUpdate = $userID
                    WHERE ID = $id";
            if (mysqli_query($conn, $Sql)) {
                $resultComplete = "update_success";
    
            } else {
                $error = true;
                $resultComplete = "update_failed";
            }
        }
    
        if (!$error) {
            // $resultComplete = "Length => " . sizeof($imageBase64Encode);
    
            for ($i = 0; $i < sizeof($imageBase64Encode); $i++) {
        
                $imagePath = "CM_" . $id . "_pic_" . $countImage++ . ".PNG";
                
                try {
                    $resultImage = file_put_contents(getImgPath($conn)."uploads/".$imagePath, base64_decode($imageBase64Encode[$i]));
                    
                    if ($resultImage) {
                        $strUpdateImage = "UPDATE sterilemachine_check SET $filedName[$i] = '$imagePath' WHERE ID = $id";
                        mysqli_query($conn, $strUpdateImage);
        
                        $resultComplete = "success";
                    } else {
                        $resultComplete = getImgPath($conn)."////".$imageBase64Encode[$i];
                    }
                } catch (Exception $e) {
                    $resultComplete = $e;
                }
            }
        }
        
        mysqli_close($conn);
    }else{
        function getImgPath($conn){
            $B_ID = $_POST['B_ID'];
             $WHERE_B_ID = "";
            $INSERT_B_ID = "";
            $VALUES_B_ID = "";
            if($B_ID != "0"){
                $WHERE_B_ID = " WHERE B_ID = $B_ID";
                $INSERT_B_ID = ", B_ID";
                $VALUES_B_ID = ", $B_ID";
            }
            $img_path = "";
            $strSQL = "SELECT MD_ImagePath FROM configuration $WHERE_B_ID";
            $result = $conn->prepare($strSQL);
            $result->execute();
            while ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $img_path = $row["MD_ImagePath"];
            }
            return $img_path;
        }
    
        if ($firstTime) { // Create new row

            $Sql = "INSERT INTO sterilemachine_check(CreateDate, MachineID, IsResult, Remark, UserCreate $INSERT_B_ID) VALUES (GETDATE(), $machineID, $isResult, '$note', $userID $VALUES_B_ID)";
            $result = $conn->prepare($Sql);
            if ($result->execute()) {
                $id = $conn->lastInsertId();
                $resultComplete = "insert_success";
            } else {
                $error = true;
                $resultComplete = "insert_failed = ".$Sql;
            }
                
        } else { // Update old row
            $Sql = "UPDATE sterilemachine_check 
                    SET     LastUpdate = GETDATE(),
                            MachineID = $machineID, 
                            IsResult = $isResult, 
                            Pic1 = NULL,
                            Pic2 = NULL,
                            Remark = '$note', 
                            UserUpdate = $userID
                    WHERE ID = $id";
            $result = $conn->prepare($Sql);
            if ($result->execute()) {
                $resultComplete = "update_success";
    
            } else {
                $error = true;
                $resultComplete = "update_failed";
            }
        }
    
        if (!$error) {
            // $resultComplete = "Length => " . sizeof($imageBase64Encode);
    
            for ($i = 0; $i < sizeof($imageBase64Encode); $i++) {
        
                $imagePath = "CM_" . $id . "_pic_" . $countImage++ . ".PNG";
                
                try {
                    $resultImage = file_put_contents(getImgPath($conn)."uploads/".$imagePath, base64_decode($imageBase64Encode[$i]));
                    
                    if ($resultImage) {
                        $strUpdateImage = "UPDATE sterilemachine_check SET $filedName[$i] = '$imagePath' WHERE ID = $id";
                        $meQuery = $conn->prepare($strUpdateImage);
                        $meQuery->execute();
        
                        $resultComplete = "success";
                    } else {
                        $resultComplete = getImgPath($conn)."////".$imageBase64Encode[$i];
                    }
                } catch (Exception $e) {
                    $resultComplete = $e;
                }
            }
        }
    
        unset($conn);
    }
    
} else {
    $resultComplete = "Please send data [POST]";
}
echo $resultComplete;