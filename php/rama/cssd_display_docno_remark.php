<?php
//EDIT LOG
//23-01-2026 15.30 : แก้ไขเพิ่ม Building_ID (B_ID) ในการ query
require 'connect.php';
$resArray = array();
  
$Date = $_POST['Date'];
$key = $_POST['key'];
$p_DB = $_POST['p_DB'];
// $Date = "All";
// $key = "I00020-21C-00004";
// $p_DB = "1";

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

$B_ID = $_POST['B_ID'];

$strSQL =  "SELECT
                configprogram.MutiPic_Remark AS Isconfig
            FROM
                configprogram
                WHERE configprogram.B_ID = '$B_ID' ";		
$meQuery = $conn->prepare($strSQL);
$meQuery->execute();
        
while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
    $Isconfig = $Result["Isconfig"];
}

if($Isconfig == '0'){
    if ($key != '') {  
        if($Date == 'All'){
             $Sql = "SELECT
                    remarkadmin.ID,
                    remarkadmin.SensterileDocNo,
                    department.DepName2,
                    item.itemname,
                    itemstock.UsageCode,
                    remarktype.NameType,
                    remarkadmin.Note,
                    remarkadmin.IsPicture,
                    remarkadmin.Picture AS Picture,
                    remarkadmin.Pictrue2 AS Picture2,
                    remarkadmin.Pictrue3 AS Picture3,
                    remarkadmin.Pictruetext AS Pictruetext,
                    remarkadmin.Pictruetext2 AS Pictruetext2,
                    remarkadmin.Pictruetext3 AS Pictruetext3,
                    configprogram.MutiPic_Remark
                FROM
                    remarkadmin
                INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                INNER JOIN department ON sendsterile.DeptID = department.ID
                INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                configprogram
                WHERE itemstock.UsageCode = '$key'
                AND remarkadmin.B_ID = '$B_ID'
                AND sendsterile.B_ID = '$B_ID'
                AND itemstock.B_ID = '$B_ID'
                ORDER BY remarkadmin.SensterileDocNo DESC";
        }else{
            $Sql = "SELECT
                    remarkadmin.ID,
                    remarkadmin.SensterileDocNo,
                    department.DepName2,
                    item.itemname,
                    itemstock.UsageCode,
                    remarktype.NameType,
                    remarkadmin.Note,
                    remarkadmin.IsPicture,
                    remarkadmin.Picture AS Picture,
                    remarkadmin.Pictrue2 AS Picture2,
                    remarkadmin.Pictrue3 AS Picture3,
                    remarkadmin.Pictruetext AS Pictruetext,
                    remarkadmin.Pictruetext2 AS Pictruetext2,
                    remarkadmin.Pictruetext3 AS Pictruetext3,
                    configprogram.MutiPic_Remark
                FROM
                    remarkadmin
                INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                INNER JOIN department ON sendsterile.DeptID = department.ID
                INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                configprogram
                WHERE
                    $dateonly remarkadmin.DateRemark) = '$Date'
                AND itemstock.UsageCode = '$key'
                AND remarkadmin.B_ID = '$B_ID'
                AND sendsterile.B_ID = '$B_ID'
                AND itemstock.B_ID = '$B_ID'
                ORDER BY remarkadmin.SensterileDocNo DESC";
        }

    }else {
        if ($Date == 'All') {
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    WHERE
                        remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }else {
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    WHERE
                        $dateonly remarkadmin.DateRemark) = '$Date'
                        
                    AND remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }
    }
    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();
        
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        array_push($resArray, array(
            'ID' => $Result['ID'],
            'SensterileDocNo' => $Result['SensterileDocNo'],
            'DepName2' => $Result['DepName2'],
            'itemname' => $Result['itemname'],
            'UsageCode' => $Result['UsageCode'],
            'NameType' => $Result['NameType'],
            'Note' => $Result['Note'],
            'IsPicture' => $Result['IsPicture'],
            'Picture' => $Result['Picture'],
            'Picture2' => $Result['Picture2'],
            'Picture3' => $Result['Picture3'],
            'Pictruetext' => $Result['Pictruetext'],
            'Pictruetext2' => $Result['Pictruetext2'],
            'Pictruetext3' => $Result['Pictruetext3'],
            'MutiPic_Remark' => $Result['MutiPic_Remark'],
        ));
    }
}else {
    if ($key != '') {
        if ($Date == 'All') {
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    WHERE
                        itemstock.UsageCode = '$key'
                        
                    AND remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }else{
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    WHERE
                        $dateonly remarkadmin.DateRemark) = '$Date'
                    AND itemstock.UsageCode = '$key'
                    
                    AND remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }
       
    }else {
        if ($Date == 'All') {
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    
                    WHERE remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }else {
            $Sql = "SELECT
                        remarkadmin.ID,
                        remarkadmin.SensterileDocNo,
                        department.DepName2,
                        item.itemname,
                        itemstock.UsageCode,
                        remarktype.NameType,
                        remarkadmin.Note,
                        remarkadmin.IsPicture,
                        remarkadmin.Picture AS Picture,
                        remarkadmin.Pictrue2 AS Picture2,
                        remarkadmin.Pictrue3 AS Picture3,
                        remarkadmin.Pictruetext AS Pictruetext,
                        remarkadmin.Pictruetext2 AS Pictruetext2,
                        remarkadmin.Pictruetext3 AS Pictruetext3,
                        configprogram.MutiPic_Remark
                    FROM
                        remarkadmin
                    INNER JOIN sendsterile ON remarkadmin.SensterileDocNo = sendsterile.DocNo
                    INNER JOIN department ON sendsterile.DeptID = department.ID
                    INNER JOIN item ON remarkadmin.ItemCode = item.itemcode
                    INNER JOIN itemstock ON remarkadmin.ItemStockID = itemstock.RowID
                    INNER JOIN remarktype ON remarkadmin.RemarkTypeID = remarktype.ID,
                    configprogram
                    WHERE
                        $dateonly remarkadmin.DateRemark) = '$Date'
                        
                    AND remarkadmin.B_ID = '$B_ID'
                    AND sendsterile.B_ID = '$B_ID'
                    AND itemstock.B_ID = '$B_ID'
                    ORDER BY remarkadmin.SensterileDocNo DESC";
        }
    }
    $meQuery = $conn->prepare($Sql);
    $meQuery->execute();
        
    while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
        array_push($resArray, array(
            'ID' => $Result['ID'],
            'SensterileDocNo' => $Result['SensterileDocNo'],
            'DepName2' => $Result['DepName2'],
            'itemname' => $Result['itemname'],
            'UsageCode' => $Result['UsageCode'],
            'NameType' => $Result['NameType'],
            'Note' => $Result['Note'],
            'IsPicture' => $Result['IsPicture'],
            'Picture' => $Result['Picture'],
            'Picture2' => $Result['Picture2'],
            'Picture3' => $Result['Picture3'],
            'Pictruetext' => $Result['Pictruetext'],
            'Pictruetext2' => $Result['Pictruetext2'],
            'Pictruetext3' => $Result['Pictruetext3'],
            'MutiPic_Remark' => $Result['MutiPic_Remark'],
        ));
    }
}
// echo json_encode(array("result" => $sql));
echo json_encode(array("result" => $resArray));

unset($conn);
die;
?>
