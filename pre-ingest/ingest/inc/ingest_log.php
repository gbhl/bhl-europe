<?php
// ********************************************
// ** FILE:    INGEST_LOG.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************


echo "<h2>Ingest Log of <font color=maroon> ".$user_name." </font></h2>";


$query = "select l.log_time, l.log_text 
    from ingest_log l, ingests i, content c 
    where l.ingest_id=i.ingest_id and i.content_id=c.content_id
and i.user_id=".$user_id." order by l.log_time desc    
";

$result = mysql_select($query,$db);
$nrows  = mysql_num_rows($result);

if ($nrows>0)
{
    echo "
        <style>
        pre { font: 12px courier new; }
        </style>
        <pre>\n";

    $line = mysql_fetch_row($result);

    while ($line)
    {
        echo $line[0]."\t".$line[1]."\n";

        $line = mysql_fetch_row($result);
    }

    echo "</pre>";
}

else echo "My Ingest Log is currently empty.";


?>
