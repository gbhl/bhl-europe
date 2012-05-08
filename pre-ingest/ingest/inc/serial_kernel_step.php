<?php
// ********************************************
// ** FILE:    SERIAL_KERNEL_STEP.PHP        **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    27.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************
// SERIAL KERNEL STEP CODE KOMPRIMIERUNG - ANTI REDUNDANZ

// REQUIRES: $sLevel     

// eval("echo \"Entering \".\$L".$sLevel."Directory[\$l".$sLevel."];");

$arrQueueCommands = array();

$old_contentDir = $contentDir;
$old_destDir    = $destDir;
$old_olef_file  = $olef_file;
$old_cPages     = $cPages;

$stepFinished   = true;         // GRUNDSAETZLICH AUF TRUE

if ($sLevel > 0) {
eval("

\$contentDir = \$L".$sLevel."Directory[\$l".$sLevel."];
\$destDir    = clean_path(\$contentDir . \"/\" . _AIP_DIR . \"/\");
\$olef_file  = clean_path(\$destDir.     \"/\" . _AIP_OLEF_FN);
\$cPages     = (int) count(getContentFiles(\$contentDir,'pagedata',false));

if (\$cPages==0) \$cPages = (int) count(getContentFiles(\$contentDir,'pagedata',true));

");
}


// Execute step for current directory
// Check if directory contains a least one file, or if we are processing
// the metadata (since for OAI-PMH providers the directory might be empty)
if( !in_array($contentDir,$arrAnalyzedDirs) && (!is_dir_empty($contentDir,true) || $menu_nav == 'get_metadata') ) { //&&(!is_dir_empty($contentDir,true))) {
    if (!is_dir($destDir)) @mkdir($destDir);        // WO NOETIG .AIP ANLEGEN
    
    $stepFinished   = false;                        // ZURUECKSETZEN DA FOLG. STEP ERFOLGREICH SEIN MUSS...

    // ***********************
    include($menu_nav.".php");
    // ***********************
    
    // Metadata fetching for section(s) might fail, since it is optional
    if( $sLevel == 1 && $menu_nav == 'get_metadata' && $stepFinished == false ) {
        $stepFinished = true;
    }

    $arrAnalyzedDirs[] = $contentDir;

    // KOMMANDOS QUEUEN ODER ABSCHLUSS 

    // 1. METS ABSCHLUSS
    if ($menu_nav=='get_mets')
    {
        // WRITE INGEST READY FILE 
        if ($ingestReady) 
        {
            // CONTROLFILE
            if (@file_put_contents($destDir._FEDORA_CF_READY,date("Y-m-d H:i:s")."\tSet ready."))
            $nMarked++;

            // ACTIVE MQ MESSAGE (STOMP)
            include_once("message_broker.php");

            if ((_MODE=='production') && (mb_send_ready($cGUID,$destDir))) {
                $nSent++;
            }
        }
        else  $endmsg .= "Package in ".str_replace(_CONTENT_ROOT,'',$contentDir)." is not ready for ingest (see msgs. above).";
    }
    // 2. QUEUING BEI ALLEN ANDEREN SCHRITTEN AUSSER METADATEN
    else if ((_QUEUE_MODE)&&($menu_nav!='get_metadata'))
    {
        echo queue_add($curQueueFile, $arrQueueCommands);
    }
    // 3. BEI METADATEN GUIDS BEHANDELN
    else if ($menu_nav=='get_metadata')
    {
        // SAVE SERIAL ITEM GUID SEPARATELY
        if (file_exists($olef_file)) file_put_contents(clean_path(dirname($olef_file)."/"._AIP_GUID_FN), $cGUID);
    }

}


// WENN DIESER SERIAL STEP NICHT ERFOGREICH WIRD DIE GESAMTE LEVEL BEARBEITUNG UNGUELTIG
if (!$stepFinished) $allLevelsDone = false;


$contentDir = $old_contentDir;
$destDir    = $old_destDir;
$olef_file  = $old_olef_file;
$cPages     = $old_cPages;

unset($arrQueueCommands);

unset($stepFinished);
