<?php

// ********************************************
// ** FILE:    GET_TAXONS.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GET TAXONS VIA WEBSERVICE
// ********************************************
// 
// TEST-URL: http://localhost/index.php?menu_nav=get_taxons&contentDir=

// file_get_contents($filename, $flags, $context, $offset, $maxlen)

// http://tomdancer.com/setting-a-timeout-on-file_get_contents-for-urls/

echo "<h1 style='margin-top: 3px;'>Taxon Finder for extracted page contents active</h1>";


// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTaxons = getPageTaxonFiles($user_id,$contentDir);
$nTaxons   = count($arrTaxons);

if ($nTaxons == 0)    {
    $arrTaxons = getPageTaxonFiles($user_id, $destDir);    
    $nTaxons   = count($arrTaxons);    
}

if ($nTaxons > 0)    echo "Taxon files present - no taxonfinder initiated!\n";
else 
{   
    include("inc/taxonfinder.php");
}


// NEU ZAEHLEN
$nTaxons  = count($arrTaxons);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTaxons > 0) {
    // IF SUCCESSFUL SET STATE TO 4
    setContentSteps($content_id, 4);
    
    $csvTextfiles = implode(_TRENNER, $arrTaxons);
    $csvTextfiles = str_replace(_CONTENT_ROOT, "", $csvTextfiles);
    mysql_select("update content set content_pages_taxon='" . $csvTextfiles . "' where content_id=" . $content_id);

    // $endmsg .= $nTaxons . " files generated and database updated successfully.";
    $endmsg .= "For ".$nTextFiles." text files ".$nTaxons." taxon files (.tax) generated and database updated successfully.";
} else if (_QUEUE_MODE) {
    echo queue_add($curQueueFile, $arrQueueCommands);
}
else
    echo _ERR . " The necessary files could not be prepared!";


?>
