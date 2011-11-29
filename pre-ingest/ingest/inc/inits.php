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

$arrEnumStatus = mysql_enum_array('ingests','ingest_status',$db);

$arrEnumCTypes = mysql_enum_array('content','content_type',$db);



// CLI JOBS

if ((isset($argv)) && (array_key_exists(1, $argv)))
{
	if ($arg[1]=='job_prepare_ingest') $menu_nav='job_prepare_ingest';
	if ($arg[1]=='job_ingest_fedora')  $menu_nav='job_ingest_fedora';
}


?>
