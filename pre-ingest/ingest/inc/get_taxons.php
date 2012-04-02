<?php
// ********************************************
// ** FILE:    GET_TAXONS.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GET TAXONS VIA WEBSERVICE
// ********************************************
// TEST-URL: http://localhost/index.php?menu_nav=get_taxons&contentDir=


echo "<h1 style='margin-top: 3px;'>Run Taxon Finder Service for pages text</h1>";


// BEREITS BEREITGESTELLT (ODER ERZEUGT IM QUEUEING)?
$arrTaxons = getContentFiles($contentDir, 'single_suffix', true,_TAXON_EXT);
$nTaxons   = count($arrTaxons);


// TEXTFILES ERMITTELN (IN UPLOAD ODER .AIP)
$arrPagesTextFiles = getContentFiles($contentDir,'ocrdata',true);
$nTextFiles = count($arrPagesTextFiles);


if ($nTaxons >= $cPages)  echo "All taxonometric files present - nothing to do!\n";
else                      include("inc/taxonfinder.php");


// NEU ZAEHLEN
$nTaxons  = count($arrTaxons);

// IN JEDEM FALL OB VORH. ODER GERADE ERZEUGT DIE DATENBANK UPDATEN
if ($nTaxons >= $cPages) 
{
    // IF SUCCESSFUL SET STATE TO 4
    if (getContentSteps($content_id)<4) setContentSteps($content_id, 4);
    
    $csvTextfiles = implode(_TRENNER, $arrTaxons);
    $csvTextfiles = str_replace(_CONTENT_ROOT, "", $csvTextfiles);
    mysql_select("update content set content_pages_taxon='" . $csvTextfiles 
            . "' where content_id=" . $content_id);

    $endmsg .= "For ".$nTextFiles." text files ".$nTaxons.
            " taxon files (.tax) generated/found. Database updated successfully.";
}
else if (!_QUEUE_MODE) 
    echo _ERR . "Not all necessary taxonometric files could be prepared!";


?>
