<?php
// ********************************************
// ** FILE:    INGEST_LIST.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// CONTENT PROVIDER'S VIEW TO NOT INGESTED CONTENT

include_once(_SHARED."formlib.php");
include_once(_SHARED."imagelib.php");


// ADMIN ACCOUNT
include("provider_details.php");


// ADMIN MY FILES

echo "<center>\n";

include("content_list.php");


// UPPER MENU

?>

<div id=logodiv style="margin-top:20px;">

<a href='http://www.bhl-europe.eu/' target='_blank'><img src="img/logo.png" width=277 height=92 border=0></a>

</div>

</center>
