<?php
// ********************************************
// ** FILE:    MESSAGE_BROKER.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    13.03.2012                    **
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

    $messageHelper->informIngest($content_guid, "file://".$content_aip);
}


// ***********************************************
// CHECK INFORMATION FROM INGEST
// ***********************************************
function check_state($content_guid)
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
    
    // print_r($msg->map);
    // Following process after receiving reports from Ingest Tool...
}



// ***********************************************
// TEST MESSAGE BROKER AVAILABILITY
// ***********************************************
function test_mb()
// ***********************************************
{

    // t.b.d.

}



/*
 
Hi Andy,

Actually, you don't need to maintain the message queue on your side, because ActiveMQ 
 * stores all un-read message and that's the reason why we choose a message queue but 
 * not a message topic. On the other hand, Ingest Tool only provides details of each 
 * batch ingest after each batch job finishes, so there is no RUNNING status that 
 * Preingest can receive.


There are several suggestions and corrections
1. Please move the stomp-client folder (except examples) into your project.
2.�Apologies, I missed the GUID field in the ingest's response. Therefore, the 
 * response (in structure of Map) should include GUID, STATUS and EXCEPTIONS (optional)
3. Regularly receive responses (every 5 minutes) from ActiveMQ and update the 
 * status of the corresponding items in Preingest (I presume that all status are stored 
 * in database), but not to check the status on its own initiative.


  
 */
?>
