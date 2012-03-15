<?php

require_once 'MessageHelper.php';

$messageHelper = new MessageHelper("tcp://bhl-mandible.nhm.ac.uk:61613");
$messageHelper->informIngest("10706-aaaaaa", "file:///mnt/....");

?>