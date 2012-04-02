<?php
// ********************************************
// ** FILE:    GET_IMAGES.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
$curStep = 2;

echo "<h1 style='margin-top: 3px;'>Preparing/Generating TIF files from various sources</h1>";

// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTiffs = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
$nTiffs   = count($arrTiffs);

if ($nTiffs >= $cPages)    echo "All TIF files present - nothing to do!\n";
else 
{   
    if ($isPDF) include("inc/pdf2ppm.php");      // RUFT FALLS PPM EXISTIEREN SELBST IMAGES2TIFF AUF
    else        include("inc/images2tiff.php");  // KEIN PDF
}

// NEU ZAEHLEN
$nTiffs   = count($arrTiffs);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTiffs >= $cPages)
{
    // IF SUCCESSFUL SET STATE TO 2
    if ($cType == 'monograph') {
        if (getContentSteps($content_id)<$curStep) setContentSteps($content_id, $curStep);
    }

    $csvTiffs = implode(_TRENNER, $arrTiffs);
    $csvTiffs = str_replace(_CONTENT_ROOT, "", $csvTiffs);
    mysql_select("update content set content_pages_tiff='" . $csvTiffs . "' where content_id=" . $content_id);

    $endmsg .= $nTiffs . " files generated and database updated successfully.";
    
    $stepFinished = true;
}
else if (!_QUEUE_MODE) 
{
    if (!$isPDF)   { echo _ERR . "Not all necessary page image files could be prepared!"; $stepErrors = true;    }
    else           { echo "INFO: ".$nPPM." PPMs created. <b>RUN THIS STEP AGAIN TO CREATE TIFFs</b> from them!"; }
}



?>
