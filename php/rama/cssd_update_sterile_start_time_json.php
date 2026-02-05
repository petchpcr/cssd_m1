<?php
// EDIT LOG
// 22-01-2026 10.50 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();

// check for post data
if (isset($_POST["p_docno"])) {

    $p_docno = $_POST["p_docno"];

    $B_ID = $_POST['B_ID'];
    $p_DB = $_POST['p_DB'];

    if($p_DB == 0){

        $top = " ";
        $limit = "LIMIT 1 ";

        $date = " NOW() ";
    
    }else if($p_DB == 1){
        
        $top = "TOP 1 ";
        $limit = " ";

        $date = " GETDATE() ";
    
    }	

    $d_check = "1";

    // -------------------------------------------
    // Check Sterile Machine
    // -------------------------------------------
    $sql_str_machine = "SELECT	$top
                                ID

                        FROM 	sterilemachine

                        WHERE 	DocNo = '$p_docno'

                        AND     StartTime IS NOT NULL
                        AND     B_ID = $B_ID

                        $limit ";

    $result = $conn->prepare($sql_str_machine);
	$result->execute();

    if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
        $d_check = "0";
    }

    // -------------------------------------------
    // Result Empty Or Not Empty Sterile Machine
    // -------------------------------------------

    if ($d_check == "0") {

        if (isset($_POST["p_SterileMachineID"])) {
            $p_SterileMachineID = $_POST["p_SterileMachineID"];
        }

        // -------------------------------------------
        // RETURN sterilemachine
        // -------------------------------------------

        if($p_DB == 0){

            $sql_machine = "SELECT	ID,
                                    MachineName2 AS MachineName,
                                    if(IsActive,'1','0') AS IsActive ,
                                    COALESCE(DocNo,'-') AS DocNo,
                                    COALESCE(DATE_FORMAT(StartTime, '%Y-%m-%d %H:%i:%s'),'-') AS StartTime,
                                    COALESCE(DATE_FORMAT(FinishTime, '%Y-%m-%d %H:%i:%s'),'-') AS FinishTime,
                                    COALESCE(DATE_FORMAT(PauseTime, '%Y-%m-%d %H:%i:%s'),'-') AS PauseTime,
                                    if(IsPause,'1','0') AS IsPause

                            FROM 	sterilemachine

                            WHERE 	ID = $p_SterileMachineID
                            AND     B_ID = $B_ID

                            LIMIT 1 ";


		}else if($p_DB == 1){

            $sql_machine = "SELECT	$top 
                                    sterilemachine.ID,
                                    sterilemachine.MachineName2 AS MachineName,
                                    sterilemachine.IsActive AS IsActive ,
                                    COALESCE(sterilemachine.DocNo,'-') AS DocNo,
                                    COALESCE(FORMAT(sterilemachine.StartTime,  'dd/MM/yyyy, hh:mm:ss'),'-') AS StartTime,   
									COALESCE(FORMAT(sterilemachine.FinishTime, 'dd/MM/yyyy, hh:mm:ss'),'-') AS FinishTime,  
									COALESCE(FORMAT(sterilemachine.PauseTime,  'dd/MM/yyyy, hh:mm:ss'),'-') AS PauseTime, 
                                    sterilemachine.IsPause AS IsPause

                            FROM 	sterilemachine

                            WHERE 	sterilemachine.ID = $p_SterileMachineID 
                            AND     sterilemachine.B_ID = $B_ID";

		}
       
        $result_machine = $conn->prepare($sql_machine);
		$result_machine->execute();

        if (!empty($result_machine)) {
            if ($row = $result_machine->fetch(PDO::FETCH_ASSOC)) {
                array_push(
                    $resArray,
                    array(
                        'result' => "A",
                        'ID' => $row["ID"],
                        'MachineName' => $row["MachineName"],
                        'IsActive' => $row["IsActive"],
                        'DocNo' => $row["DocNo"],
                        'StartTime' => $row["StartTime"],
                        'FinishTime' => $row["FinishTime"],
                        'PauseTime' => $row["PauseTime"],
                        'IsPause' => $row["IsPause"],
                    )
                );
            }
        } else {
            array_push(
                $resArray,
                array(
                    'result' => "E",
                )
            );
        }

    } else {

        // -------------------------------------------
        // Select Time
        // -------------------------------------------

        $strSQL =  "SELECT      $top
                                sterileprogram.ProcessTime

                    FROM 		sterile

                    LEFT JOIN 	sterileprogram
                    ON			sterileprogram.ID = sterile.SterileProgramID

                    WHERE 		sterile.DocNo = '$p_docno'
                    AND         sterile.B_ID = $B_ID
                    AND         sterileprogram.B_ID = $B_ID

                    $limit ";

        $result = $conn->prepare($strSQL);
		$result->execute();

        $p_time = 0;

        if (!empty($result)) {
            if ($row = $result->fetch(PDO::FETCH_ASSOC)) {
                $p_time = (int) $row["ProcessTime"];
            }
        }

        // -------------------------------------------
        // Update sterile
        // -------------------------------------------

        if($p_DB == 0){

			$sql_update_wash = 	 "UPDATE sterile SET StartTime = NOW(), FinishTime = (NOW() + INTERVAL $p_time SECOND) WHERE DocNo = '$p_docno' ";

		}else if($p_DB == 1){

			$sql_update_wash = 	 "UPDATE sterile SET sterile.StartTime = GETDATE(), sterile.FinishTime = DATEADD(SECOND, $p_time, GETDATE()) WHERE sterile.DocNo = '$p_docno'";

		}

        $res_update_wash = $conn->prepare($sql_update_wash);
		$res_update_wash->execute();

        if (!empty($res_update_wash)) {

            // -------------------------------------------
            // Update steriledetail IsStatus = 1
            // -------------------------------------------
            $sql_update_sterile_detail = "UPDATE	steriledetail SET IsStatus = 1 WHERE DocNo = '$p_docno' ";

            $res_update_sterile_detail = $conn->prepare($sql_update_sterile_detail);
            $res_update_sterile_detail->execute();

            // -------------------------------------------
            // Update Sterile Machine
            // -------------------------------------------

            if($p_DB == 0){

				$sql_update = 	 "UPDATE sterilemachine SET DocNo = '$p_docno' , IsActive = 1, StartTime = NOW(), FinishTime = (NOW() + INTERVAL $p_time SECOND), IsPause = 0, PauseTime = null, LastTime = NOW() WHERE DocNo = '$p_docno' ";

			}else if($p_DB == 1){

				$sql_update = 	 "UPDATE sterilemachine SET DocNo = '$p_docno' , IsActive = 1, StartTime = GETDATE(), FinishTime = DATEADD(SECOND, $p_time, GETDATE()) , IsPause = 0, PauseTime = null, LastTime =  GETDATE() WHERE DocNo = '$p_docno' ";

			}

            $res_update = $conn->prepare($sql_update);
            $res_update->execute();

            // -------------------------------------------
            // RETURN sterilemachine
            // -------------------------------------------
                        
			if($p_DB == 0){
				$sql_machine = "SELECT	ID, 
										MachineName2 AS MachineName,  
										if(IsActive,'1','0') AS IsActive ,   
										COALESCE(DocNo,'-') AS DocNo,   
										COALESCE(DATE_FORMAT(StartTime, '%Y-%m-%d %H:%i:%s'),'-') AS StartTime,   
										COALESCE(DATE_FORMAT(FinishTime, '%Y-%m-%d %H:%i:%s'),'-') AS FinishTime,  
										COALESCE(DATE_FORMAT(PauseTime, '%Y-%m-%d %H:%i:%s'),'-') AS PauseTime, 
										if(IsPause,'1','0') AS IsPause   

								FROM 	sterilemachine 

								WHERE 	DocNo = '$p_docno' 
                                AND     B_ID = $B_ID";

			}else if($p_DB == 1){
				$sql_machine = "SELECT	$top
										sterilemachine.ID, 
										sterilemachine.MachineName2 AS MachineName,  
										sterilemachine.IsActive AS IsActive ,   
										COALESCE(sterilemachine.DocNo,'-') AS DocNo,   
										COALESCE(FORMAT(sterilemachine.StartTime,  'yyyy-MM-dd HH:mm:ss'),'-') AS StartTime,   
										COALESCE(FORMAT(sterilemachine.FinishTime, 'yyyy-MM-dd HH:mm:ss'),'-') AS FinishTime,  
										COALESCE(FORMAT(sterilemachine.PauseTime, 'yyyy-MM-dd HH:mm:ss'),'-') AS PauseTime, 
										sterilemachine.IsPause AS IsPause   

								FROM 	sterilemachine 

								WHERE 	sterilemachine.DocNo = '$p_docno' 
                                AND     sterilemachine.B_ID = $B_ID";

			}

            $result_machine = $conn->prepare($sql_machine);
            $result_machine->execute();


            if (!empty($result_machine)) {
                if ($row = $result_machine->fetch(PDO::FETCH_ASSOC)) {
                    array_push(
                        $resArray,
                        array(
                            'result' => "A",
                            'ID' => $row["ID"],
                            'MachineName' => $row["MachineName"],
                            'IsActive' => $row["IsActive"],
                            'DocNo' => $row["DocNo"],
                            'StartTime' => $row["StartTime"],
                            'FinishTime' => $row["FinishTime"],
                            'PauseTime' => $row["PauseTime"],
                            'IsPause' => $row["IsPause"],
                        )
                    );
                }
            } else {
                array_push(
                    $resArray,
                    array(
                        'result' => "E",
                    )
                );
            }

        } else {
            array_push(
                $resArray,
                array(
                    'result' => "E",
                )
            );
        }
    }

} else {
    array_push(
        $resArray,
        array(
            'result' => "I",
        )
    );
}

// -------------------------------------------------------
// echo json
// -------------------------------------------------------

echo json_encode(array("result" => $resArray));

// -------------------------------------------------------
// Close Connection
// -------------------------------------------------------

unset($conn);
die;

?>