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

include_once(_SHARED."pdf_tools.php");


$relativePDF = basename($sourcePDF);
$nPages      = getNumPagesInPDF(array($sourcePDF));

echo "<h3>Try to extract text from ".$nPages." pages in " . $relativePDF . 
        " <font size=-2>(1 Step Operation, please be patient...</font></h3><pre>";

// EINZELNE PAGES EXTRAHIEREN AUS DEM PDF
for ($i=1;$i<=$nPages;$i++)
{
    ob_start();
    
    // http://foolabs.com/xpdf/about.html
    // http://foolabs.com/xpdf/download.html
    // http://linux.die.net/man/1/pdftotext
    
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

        if (file_exists($outputFile))
            $arrTextFiles[] = str_replace(_CONTENT_ROOT,'',$outputFile);
    }
    else {
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
