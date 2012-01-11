<?php

// GENERIERE TEXT AUS PDFS
// 
// INCOMING: $content_id, $sourcePDF
/*
 The Xpdf tools use the following exit codes:

    1 No error.
    2 Error opening a PDF file.
    3 Error opening an output file.
    4 Error related to PDF permissions.
    5 Other error.
*/

$relativePDF = basename($sourcePDF);
$nPages      = getNumPagesInPDF($sourcePDF);


echo "<h3>Try to extract text from ".$nPages." pages in " . $relativePDF . "</h3>";

echo "<pre>";

// EINZELNE PAGES EXTRAHIEREN AUS DEM PDF
for ($i=1;$i<=$nPages;$i++)
{
    $myCmd = str_replace("SSSS",$sourcePDF,_PDFTOTEXT);
    
    $outputFile = $destDir.$relativePDF."_".$i."_PDF_".$i.".txt";

    // FIRST - LAST PAGE  - OUTPUT FILE 
    $myCmd = str_replace(array('FFFF','LLLL','OOOO'),array($i,$i,$outputFile),$myCmd);
    $myCmd = exec_prepare($myCmd);
    
    if (!_QUEUE_MODE) 
    {
        $output = array();
        $return_var = "";

        $rLine = exec($myCmd, $output, $return_var);

        if ($return_var == 0)
            echo $myCmd . "\nPDFTOTEXT ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
        else
            echo "Error in PDFTOTEXT; " . $rLine . "\n";

        if (file_exists($outputFile))   $arrTextFiles[] = $outputFile;
    }
    else {
        // INGEST SCRIPT COMMANDS
        echo $myCmd . "\n";
        $arrQueueCommands[] = $myCmd;
    }
}

echo "</pre>";


?>
