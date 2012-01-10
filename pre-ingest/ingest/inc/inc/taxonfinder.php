<?php
// ********************************************
// ** FILE:    TAXONFINDER.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************


// TEXTFILES ERMITTELN (IN UPLOAD ODER .AIP)
$arrPagesTextFiles = getContentFiles($contentDir,'ocrdata',true);
$nTextFiles = count($arrPagesTextFiles);

$resource_context = stream_context_create(array(
    'http' => array(
        'timeout' => _TAXON_WEB_TIMEOUT
    )
        )
);

if ($nTextFiles==0) die(_ERR."No page text/OCR files found in upload or "._AIP_DIR." directory!");


echo "<h3>Try to get taxons for ".$nTextFiles." text files.</h3>";

// FUER ALLE TEXTFILES TAXON WEBSERVICE AUFRUFEN
echo "<pre>";

for ($i = 0; $i < $nTextFiles; $i++) 
{
    ob_start();
    
    // $outputFile   = str_replace("//","/",$contentDir."/"._AIP_DIR."/".basename($arrPagesTextFiles[$i]).".TAX");
    $outputFile = substr(basename($arrPagesTextFiles[$i]), 0, strrpos(basename($arrPagesTextFiles[$i]), '.'));
    
    // DARF KEIN .TXT IN SICH TRAGEN
    $outputFile = clean_path($contentDir . "/" . $outputFile . ".tax");

    $inputFileSelfReferenceHTTPpath = _HOME . "" . $arrPagesTextFiles[$i];

    $myURL = _TAXON_FINDER . _REVERSE_LOOKUP_URL;

    // CONTENT NAME IST PFAD OHNE _CONTENT_ROOT BIS RUNTER ZUM MEDIUM UND DAMIT RICHTIG
    $myPath = clean_path($arrProvider['user_content_home']."/".$contentName."/".basename($arrPagesTextFiles[$i]));
    
    $myURL .= $myPath;
   
    if (!_QUEUE_MODE)
    {
        echo "Try to analyze " . basename($arrPagesTextFiles[$i]) . " .... ";
        
        // echo $myURL . "\n";
        
        // TAXONFINDING
        $strXMLTaxons = file_get_contents($myURL, 0, $resource_context);
        $nBytes = false;

        // WRITE TAXON FILE FOR PAGE IF NOT EMPTY
        if (!empty($strXMLTaxons)) 
        {
            $nBytes = file_put_contents($outputFile, $strXMLTaxons);

            if ($nBytes !== false) {
                echo $nBytes . " bytes written, ok.\n";
                $arrTaxons[] = $outputFile;
            }

            if ($nBytes === false) {
                echo "No taxons found or error.\n";
            }
        }
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

echo "</pre>";


?>
