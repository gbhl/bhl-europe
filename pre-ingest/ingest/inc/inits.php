<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

// http://at.php.net/manual/de/reserved.variables.argv.php

// INITS
if (_CLI_EXECUTION)                           $menu_nav="selftest";
else 
    if ((!isset($menu_nav))||($menu_nav=="")) $menu_nav="portal";

if (!isset($db))    $db=_DB_MAIN;



// AUSWAHLFELDER DB

$arrEnumCStatus = mysql_enum_array('content','content_status',$db); 

$arrEnumIStatus = mysql_enum_array('ingests','ingest_status',$db);

$arrEnumCTypes  = mysql_enum_array('content','content_type',$db);



// CLI JOBS

if ((isset($argv)) && (array_key_exists(1, $argv)))
{
	if ($arg[1]=='job_prepare_ingest') $menu_nav='job_prepare_ingest';
	if ($arg[1]=='job_ingest_fedora')  $menu_nav='job_ingest_fedora';
}

// SESSION

if (!_CLI_EXECUTION) include_once(_SHARED."session.php");



// PROVIDER DETAILS
if (isset($user_id))
{
    $arrProvider = get_provider_details($user_id);

    // QUEUING ENABLED (WRITE PREPROCESS SCRIPT)

    if (($arrProvider['queue_mode']==1)||(isset($queue_mode))) define ("_QUEUE_MODE",  true);
    else                                                       define ("_QUEUE_MODE",  false);
}

// CONTENT QUEUE

if (isset($content_id))
$curQueueFile = _QUEUE_PREFIX.$content_id._QUEUE_SUFFIX;


?>
