<?php

require_once 'MessageHelper.php';

function echo_message($msg) {
	$body = json_decode($msg->body);
	var_dump($body);
	echo "guid: ".$body->GUID."\n";
	echo "status: ".$body->STATUS."\n";
	if ($body->STATUS == "FAILED") {
		echo "exceptions: ".$body->EXCEPTIONS."\n";
	}
}

$messageHelper = new MessageHelper("tcp://bhl-mandible.nhm.ac.uk:61613");
$messageHelper->subscribe('echo_message');
?>
