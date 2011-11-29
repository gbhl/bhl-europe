<?php
// ********************************************
// ** FILE:    INGEST_DETAIL.PHP             **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// get_ingest_details($ingest_id="",$content_id)
        
$arrDetails = get_ingest_details($ingest_id);

echo "<h2>Details</h2>";

echo_pre($arrDetails);


?>
