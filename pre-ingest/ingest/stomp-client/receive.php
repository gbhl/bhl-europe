<?php

require_once 'MessageHelper.php';

$messageHelper = new MessageHelper("tcp://bhl-mandible.nhm.ac.uk:61613");
$messageHelper->subscribe();
$msg = $messageHelper->receive();

echo $msg;

?>
