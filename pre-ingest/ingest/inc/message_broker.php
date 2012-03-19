<?php
// ********************************************
// ** FILE:    MESSAGE_BROKER.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    19.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// see docs/internal/stomp_info.txt

/*
define ("_MB_ABS",    "/mnt/nfs-demeter/development/stomp-client/");
define ("_MB_URL",    "tcp://bhl-mandible.nhm.ac.uk:61613");
*/

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
function check_state($content_guid="")
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
    
    return $msg;
    
    // Following process after receiving reports from Ingest Tool...
}



// ***********************************************
// TEST MESSAGE BROKER AVAILABILITY
// ***********************************************
function test_mb()
// ***********************************************
{
    echo_pre(mb_send_ready("test-guid-123","/dev/null"));
    
    nl(2);
    
    sleep(10);
    
    echo_pre(check_state());
}






// TEST
if ((isset($debug))&&($debug==1))   
{
    echo "NOW TESTING MB.\n\n";
    
    test_mb();

    echo "END TESTING MB.\n\n";
}

    
?>
