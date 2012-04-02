<?php
// ********************************************
// ** FILE:    SERIAL_KERNEL_STEP.PHP        **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    27.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// SERIAL KERNEL STEP CODE KOMPRIMIERUNG - ANTI REDUNDANZ

// REQUIRES: $sLevel     

// eval("echo \"Entering \".\$L".$sLevel."Directory[\$l".$sLevel."];");


eval("
\$old_contentDir = \$contentDir;
\$old_destDir    = \$destDir;
\$old_olef_file  = \$olef_file;
\$old_cPages     = \$cPages;


\$contentDir = \$L".$sLevel."Directory[\$l".$sLevel."];
\$destDir    = clean_path(\$contentDir . \"/\" . _AIP_DIR . \"/\");  if (!is_dir(\$destDir)) @mkdir(\$destDir);
\$olef_file  = clean_path(\$destDir.     \"/\" . _AIP_OLEF_FN);
\$cPages     = (int) count(getContentFiles(\$contentDir,'pagedata',false));

if (\$cPages==0) \$cPages = (int) count(getContentFiles(\$contentDir,'pagedata',true));

if (!\$l".($sLevel+1)."ready) { include(\$menu_nav.\".php\"); \$l".($sLevel+1)."ready = true; } 

");





// KOMMANDOS QUEUEN ODER ABSCHLUSS 

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
    else  $endmsg .= "Due some missing information the package is not ready for ingest.";
}
else if ((_QUEUE_MODE)&&($menu_nav!='get_metadata'))
{
    echo queue_add($curQueueFile, $arrQueueCommands);
}



$contentDir = $old_contentDir;
$destDir    = $old_destDir;
$olef_file  = $old_olef_file;
$cPages     = $old_cPages;


?>
