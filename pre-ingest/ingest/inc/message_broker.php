<?php
// ********************************************
// ** FILE:    MESSAGE_BROKER.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    19.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// http://activemq.apache.org/stomp.html
// http://bhl-int.nhm.ac.uk/admin
// https://github.com/bhle/bhle/issues/309
// see docs/internal/stomp_info.txt


require_once(_MB_ABS."MessageHelper.php");


// ***********************************************
// WHEN THERE IS AN AIP READY TO INGEST
// ***********************************************
function mb_send_ready($content_guid,$content_aip)
// ***********************************************
{
    $messageHelper = new MessageHelper(_MB_URL);
    
    /*
    MessageHelper::informIngest($guid, $uri)
    send a map message to a message queue named 'preingest'
    Messasge content:
    "GUID": $guid
    "URI": $uri
    */

    return $messageHelper->informIngest($content_guid, "file://".$content_aip);
}


// ***********************************************
// CHECK INFORMATION FROM INGEST
// ***********************************************
function check_state()
// ***********************************************
{
	$messageHelper = new MessageHelper("tcp://bhl-mandible.nhm.ac.uk:61613");
	// add get_topic as a callback function
	$messageHelper->subscribe('get_topic');
}


// ***********************************************
// ECHO INFORMATION FROM INGEST
// ***********************************************
function get_topic($msg)
// ***********************************************
{
 	$body = json_decode($msg->body);
	echo_pre("guid: ".$body->GUID);
	echo_pre("status: ".$body->STATUS);
	if ($body->STATUS == "FAILED") {
		echo_pre("exceptions: ".$body->EXCEPTIONS);
	}
}



// ***********************************************
// TEST MESSAGE BROKER AVAILABILITY
// ***********************************************
function test_mb()
// ***********************************************
{
    $messageHelper = check_state();
    
    mb_send_ready("test-guid-123","/dev/null");
	
	// A thread as a subscriber will echo the message from ingest with exceptions because /dev/null has no eligible item
	
	$messageHelper->unsubscribe();
}



// TEST
if ((isset($debug))&&($debug==1))   
{
    echo "NOW TESTING MB.\n\n";
    
    test_mb();

    echo "END TESTING MB.\n\n";
}

    
?>
