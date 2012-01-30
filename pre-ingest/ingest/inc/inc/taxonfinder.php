<?php
// ********************************************
// ** FILE:    TAXONFINDER.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

/* 
  http://www.ubio.org/index.php?pagename=services_overview');

Recommened:
 * http://www.ubio.org/index.php?pagename=services_overview
 * 
 * http://www.ubio.org/index.php?pagename=xml_services
 * 
 * http://www.ubio.org/index.php?pagename=soap_methods/taxonFinder
 * 
 * test:
 * http://www.ubio.org/tools/Small.txt
 * http://www.ubio.org/webservices/service.php?function=taxonFinder&includeLinks=0&url=http://www.ubio.org/tools/Small.txt
 * http://www.ubio.org/tools/recognize.php
 * http://www.ubio.org/portal/index.php
 * 
 * Saccharum alopecuroides subvar. angustifolium (Nees) Roberty
Acacia angustissima (P. Mill.) Kuntze var. cuspidata (Schlecht.) L. Benson
Selaginella riddellii van Eselt. 
 * 
 * old:
 * http://www.ubio.org/index.php?pagename=services_methods2
 * 
 * other
 * http://www.ncbi.nlm.nih.gov/pmc/articles/PMC555944/
 * 
 * 
 */

// TEXTFILES ERMITTELN (IN UPLOAD ODER .AIP)
$arrPagesTextFiles = getContentFiles($contentDir,'ocrdata',true);
$nTextFiles = count($arrPagesTextFiles);

$resource_context = stream_context_create(array(
    'http' => array(
        'timeout' => _WEBSERVICE_TIMEOUT
    )
        )
);

if ($nTextFiles==0) die(_ERR."No page text/OCR files found in upload or "._AIP_DIR." directory!");

echo "<h3>Try to get taxons for ".$nTextFiles." text files.</h3><pre>";


// FUER ALLE TEXTFILES TAXON WEBSERVICE AUFRUFEN
for ($i = 0; $i < $nTextFiles; $i++) 
{
    ob_start();
    
    // $outputFile   = str_replace("//","/",$contentDir."/"._AIP_DIR."/".basename($arrPagesTextFiles[$i]).".TAX");
    $outputFile = substr(basename($arrPagesTextFiles[$i]), 0, strrpos(basename($arrPagesTextFiles[$i]), '.'));
    
    // DARF KEIN .TXT IN SICH TRAGEN
    $outputFile = clean_path($destDir . $outputFile . _TAXON_EXT);

    $myURL = _TAXON_FINDER . getReverseLookupURL($arrPagesTextFiles[$i]);
  
    if (!_QUEUE_MODE)
    {
        echo "Try to analyze " . basename($arrPagesTextFiles[$i]) . " ...\n".$myURL."\n";
       
        // TAXONFINDING
        $strXMLTaxons = file_get_contents($myURL, 0, $resource_context);
        $nBytes = false;

        // WRITE TAXON FILE FOR PAGE IF NOT EMPTY
        if (!empty($strXMLTaxons)) 
        {
            $nBytes = file_put_contents($outputFile, $strXMLTaxons);

            if ($nBytes !== false) {
                echo $nBytes . " bytes written, ok.\n";
                $arrTaxons[] = str_replace(_CONTENT_ROOT,'',$outputFile);
            }
            else    echo "No taxons returned or connection error.\n";
        }
        
        echo "\n";
    }
    else
    {
        // IN QUEUE WIRD TAXONFINDER UEBER WGET AUSGEFUEHRT NICHT PER PHP COMMANDS
        $myCmd = "wget -t 1 -O ".$outputFile." \"".$myURL."\"";
        
        // INGEST SCRIPT COMMANDS
        echo $myCmd . "\n";
        $arrQueueCommands[] = $myCmd;        
    }

    @ob_end_flush();
    @ob_flush();
    @flush();
}

echo "\n\n</pre>\n";


?>
