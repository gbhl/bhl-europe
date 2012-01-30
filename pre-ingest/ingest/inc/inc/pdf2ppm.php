<?php
// ********************************************
// ** FILE:    PDF2PPM.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// GENERIERE PPM/TIFFs AUS PDFs

// INCOMING: $content_id, $sourcePDF

/*
 The Xpdf tools use the following exit codes:

    1 No error.
    2 Error opening a PDF file.
    3 Error opening an output file.
    4 Error related to PDF permissions.
    5 Other error.
   
    PPM-root-nnnnnn.ppm, where nnnnnn is the page number. 
 */

$relativePDF = basename($sourcePDF);


$arrPPM = getContentFiles($contentDir, 'single_suffix', true,'.ppm');
$nPPM   = count($arrPPM);


// GENERATE TIFs FROM PPM
if ($nPPM>0)
{
    echo "<h3>PPMs found - going directly to conversion to tif</h3>";
    include("images2tiff.php");
}
// GENERATE PPM
else
{
    ob_start();
    
    echo "<h3>Try to generate PPM's from " . $relativePDF . "</h3><pre>\nRunning...\n\n";

    echo invisible_html(1024 * 5);
    
    $outputFile = $destDir.$relativePDF;  // not real output file is pdftoppm root!
                                          // PPM-root-nnnnnn.ppm, where nnnnnn is the page number. 

    $myCmd = _PDFTOPPM . " \"" . $sourcePDF . "\" \"" . $outputFile."\""; 
    $myCmd = exec_prepare($myCmd); 
    
    @ob_end_flush();
    @ob_flush();
    @flush();
    
    if (!_QUEUE_MODE) 
    {
        $output = array();
        $return_var = "";

        $rLine = exec($myCmd, $output, $return_var);

        if ($return_var == 0)
            echo $myCmd . "\nPDFTOPPM ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
        else
            echo "Error in PDFTOPPM; " . $rLine . "\n";

        // DONT MAKE THESE PPM TO THE PAGEDATA IN THE DATABASE FOR IMAGES2TIFF.PHP
    }
    else {
        // INGEST SCRIPT COMMANDS
        echo $myCmd . "\n";
        $arrQueueCommands[] = $myCmd;
    }

    echo "\n\n</pre>\n";
}


// PPMS WIEDER ZAEHLEN
if (!_QUEUE_MODE)
$nPPM = getContentFiles($contentDir, 'single_suffix', true,'.ppm');


?>
