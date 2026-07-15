<?php
require 'connect.php';
$resArray = array();

if (isset($_POST["p_usedcode"])) {
	error_log(date('Y-m-d H:i:s') . PHP_EOL, 3, __DIR__ . '\debug\cssd_check_expiring.log');
	error_log(' POST: ' . json_encode($_POST, JSON_UNESCAPED_UNICODE | JSON_THROW_ON_ERROR) . PHP_EOL, 3, __DIR__ . '\debug\cssd_check_expiring.log');

	$p_usedcode = $_POST['p_usedcode'];
	$p_DB = $_POST['p_DB'];
	$B_ID = $_POST["B_ID"];
	$PackingMatID = '0';

	$sql = "SELECT	itemstock.PackingMatID
			FROM 	itemstock
			where 	(itemstock.UsageCode = '$p_usedcode' OR itemstock.UsageCode2 = '$p_usedcode')
			AND		itemstock.B_ID = '$B_ID'";

	$meQuery = $conn->prepare($sql);
	$meQuery->execute();

	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
		$PackingMatID = $Result["PackingMatID"];
	}

	if ($p_DB == 0) {

		$sql = "SELECT 	DATEDIFF(DATE(itemstock.ExpireDate), DATE(NOW())) AS DateDiff
				FROM 	itemstock
				where 	(itemstock.UsageCode = '$p_usedcode' OR itemstock.UsageCode2 = '$p_usedcode')
				AND		itemstock.B_ID = '$B_ID'";
	} else if ($p_DB == 1) {

		if ($PackingMatID == 136 || $PackingMatID == 26) {
			// if($PackingMatID == 12 || $PackingMatID == 13 ){
			$sql = "SELECT	10 AS DateDiff
					FROM 	itemstock
					where 	(itemstock.UsageCode = '$p_usedcode' OR itemstock.UsageCode2 = '$p_usedcode')
					AND		itemstock.B_ID = '$B_ID'";
		} else {
			$sql = "SELECT TOP 1 
						DATEDIFF( DAY, CONVERT ( DATE, GETDATE( ) ), CONVERT ( DATE, itemstock.ExpireDate ) ) AS DateDiff 
					FROM
						itemstock
					WHERE
						(itemstock.UsageCode = '$p_usedcode' OR itemstock.UsageCode2 = '$p_usedcode')
					AND		itemstock.B_ID = '$B_ID'";
		}
	}
	// array_push(
	// 	$resArray,
	// 	array(
	// 		'result' => "I",
	// 		'Sql' => $sql
	// 	)
	// );
	// echo json_encode(array("result" => $resArray));die;

	$meQuery = $conn->prepare($sql);
	$meQuery->execute();

	$DateDiff = 0;
	$i = 0;
	//error_log('Sql '.$sql.PHP_EOL, 3, __DIR__ . '\debug\cssd_check_expiring.log');
	while ($Result = $meQuery->fetch(PDO::FETCH_ASSOC)) {
		$DateDiff = $Result["DateDiff"];
		array_push(
			$resArray,
			array(
				'result' => "A",
				'usedcode' => $p_usedcode,
				'DateDiff' => $DateDiff,
				'Cnt' => '1'
			)
		);
		echo json_encode(array("result" => $resArray));
		error_log(' echo: ' . json_encode(array('result' => $resArray)) . PHP_EOL, 3, __DIR__ . '\debug\cssd_check_expiring.log');
		unset($conn);
		die;
	}

	array_push(
		$resArray,
		array(
			'result' => "E",
			'usedcode' => $p_usedcode,
			'DateDiff' => $DateDiff,
			'Cnt' => '0',
			'Sql' => $sql
		)
	);

	echo json_encode(array("result" => $resArray));
} else {

	array_push(
		$resArray,
		array(
			'result' => "I",
			'Cnt' => '0'
		)
	);

	echo json_encode(array("result" => $resArray), JSON_UNESCAPED_UNICODE);
}

error_log(' echo: ' . json_encode(array('result' => $resArray)) . PHP_EOL, 3, __DIR__ . '\debug\cssd_check_expiring.log');

unset($conn);
die;
