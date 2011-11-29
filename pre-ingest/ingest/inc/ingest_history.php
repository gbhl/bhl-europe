<?php
// ********************************************
// ** FILE:    INGEST_HISTORY.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// $arrProvider = get_provider_details($user_id);


echo "<h2>Ingest History of <font color=maroon> ".$user_name." </font></h2>";

// PREVIOUS INGEST ATTEMPTS
    $query = "select i.ingest_id, i.content_id, i.ingest_status, i.ingest_last_successful_step, 
    i.ingest_version, i.ingest_do_ocr, i.ingest_do_taxon, i.ingest_do_sm, 
    c.content_name, c.content_alias, c.content_type, c.content_root, c.content_ctime  
    from ingests i, content c
where i.user_id=".$user_id." order by i.ingest_time ";

$result = mysql_select($query);

$line = mysql_fetch_array($result);

echo _TABLE;

while ($line)
{
    echo TR.$line['ingest_id'];
    
    $line = mysql_fetch_array($result);
}

echo _TAB;

mysql_free_result($result);
    
?>
