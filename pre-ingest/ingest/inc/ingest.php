<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// INGEST TO FEDORA

// ALLE INGEST DETAILS ZUM CONTENT

$arrIngestDetails = get_ingest_details("",$content_id);    


// icon($image, $alt, $action, $width, $height, $echo, $img_map, $id)
if ($arrIngestDetails['ingest_status']=='')  // undefiniert
{
    // PREPARE ANBIETEN (=OLEF)
    // prepare this mediums metadata
    // generate ocr task

    // ingest
}        


/*
 * 
 * button("Reload & Analyze My Current Uploads",
        ,900,32,'center'); 
// icon($image, $alt, $action, $width, $height, $echo, $img_map, $id)
*/



?>
