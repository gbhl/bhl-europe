<?php
// ********************************************
// ** FILE:    INITS.PHP                     **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    13.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// http://at.php.net/manual/de/reserved.variables.argv.php


// *******************
// INITS
// *******************
if (_CLI_EXECUTION)                           $menu_nav="selftest";
else 
    if ((!isset($menu_nav))||($menu_nav=="")) $menu_nav="portal";

if (!isset($db))    $db=_DB_MAIN;


// *******************
// AUSWAHLFELDER DB
// *******************
$arrEnumCStatus = mysql_enum_array('content','content_status',$db); 

$arrEnumIStatus = mysql_enum_array('ingests','ingest_status',$db);

$arrEnumCTypes  = mysql_enum_array('content','content_type',$db);


// *******************
// CLI JOBS (DEPRECATED)
// *******************
if ((isset($argv)) && (array_key_exists(1, $argv)))
{
	if ($arg[1]=='job_prepare_ingest') $menu_nav='job_prepare_ingest';
	if ($arg[1]=='job_ingest_fedora')  $menu_nav='job_ingest_fedora';
}


// *******************
// SESSION
// *******************
if (!_CLI_EXECUTION) include_once(_SHARED."session.php");



// *******************
// PROVIDER DETAILS
// *******************
if (isset($user_id))
{
    $arrProvider = get_provider_details($user_id);

    // QUEUING ENABLED (WRITE PREPROCESS SCRIPT)
    
    // FALLS NICHT GERADE PROVIDER DETAILS QUEUING ENABLED ODER DISABLE
    if ((isset($queue_mode))&&($queue_mode==1))                         define ("_QUEUE_MODE",  true);
    else if ((isset($sub_action))&&($sub_action=="save_cp_details"))    define ("_QUEUE_MODE",  false);
    
    // SONST NIMM EINSTELLUNG
    else if ($arrProvider['queue_mode']==1)                             define ("_QUEUE_MODE",  true);
    else                                                                define ("_QUEUE_MODE",  false);
    
    define("_USER_CONTENT_ROOT", clean_path(_CONTENT_ROOT."/".$arrProvider['user_content_home']));
    
    define("_USER_WORKDIR",      clean_path(_WORK_DIR . $arrProvider['user_content_id'] . "/"));
    
    // GENERATE USER WORK DIR
    if (!is_dir(_USER_WORKDIR))  @mkdir(_USER_WORKDIR);




    // *******************
    // CONTENT VORAB INFOS
    // *******************
    if ((isset($content_id)) && (is_numeric($content_id))) 
    {
        $arrContentDetails = get_content_details($content_id);

        $contentDir  = $arrContentDetails['content_root'];
        $contentName = $arrContentDetails['content_name'];
        $cPages      = $arrContentDetails['content_pages'];
        $cType       = $arrContentDetails['content_type'];

        // GENERATE .AIP DIR
        $destDir     = clean_path($contentDir . "/" . _AIP_DIR . "/");
        if (!is_dir($destDir))      @mkdir($destDir);

        $curQueueFile = clean_path(_USER_WORKDIR._QUEUE_PREFIX.$content_id);

        $isPDF = false;

        if (isPDF($contentName))
        {
            $isPDF = true;
            $sourcePDF = clean_path($contentDir."/".$contentName);
        }

        define("_OLEF_FILE",        $destDir._AIP_OLEF_FN);

        $olef_file = _OLEF_FILE;
    }
    
}
