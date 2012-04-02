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
// 
// CONTROL:
// http://bhl-int.nhm.ac.uk/admin/queues.jsp

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
$messageHelper = new MessageHelper("tcp://bhl-mandible.nhm.ac.uk:61613");
$messageHelper->informIngest("10706-aaaaaa", "file:///dev/null");

    */

    return $messageHelper->informIngest($content_guid, "file://".$content_aip);
}


// ***********************************************
// CHECK INFORMATION FROM INGEST
// ***********************************************
function check_state_old($content_guid="")
// ***********************************************
{
    $messageHelper = new MessageHelper(_MB_URL);
    
    /*
    Subscribe to ActiveMQ topic named 'ingest' by default
    */
    $messageHelper->subscribe();
    /*
    Wait and receive message from topic 'ingest' (60 seconds)
    Message content:
        "STATUS": ["COMPLETED", "FAILED"]
        "EXCEPTIONS"(if FAILED): stackTrace from Batch Ingest
    */
    $msg = $messageHelper->receive();
    
    print_r($msg->map);
    
    $messageHelper->unsubscribe();
    
    return $msg;
    
    // Following process after receiving reports from Ingest Tool...
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
function get_topic_old()
// ***********************************************
{
    $messageHelper = new MessageHelper(_MB_URL);
    
    /*
    Subscribe to ActiveMQ topic named 'ingest' by default
    */
    $messageHelper->subscribe("/topic/preingest");
    /*
    Wait and receive message from topic 'ingest' (60 seconds)
    Message content:
        "STATUS": ["COMPLETED", "FAILED"]
        "EXCEPTIONS"(if FAILED): stackTrace from Batch Ingest
    */
    $msg = $messageHelper->receive();
    
    print_r($msg->map);
    
    $messageHelper->unsubscribe("/topic/preingest");
    
    return $msg;
    
    // Following process after receiving reports from Ingest Tool...
}


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
function test_mb_old()
// ***********************************************
{
    echo_pre(get_topic());
    
    echo_pre(mb_send_ready("test-guid-123","/dev/null"));
    
    nl(2);
    
    sleep(2);
    
//  echo_pre(check_state());

    echo_pre(get_topic());
}


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
