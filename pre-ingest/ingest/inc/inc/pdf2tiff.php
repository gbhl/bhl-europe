<?php

// GENERIERE TIFFs AUS PDFs UEBER PPM's

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


$arrPPM = getContentFiles($contentDir, 'single_suffix', true,'.ppm');
$nPPM = count($arrPPM);


// GENERATE TIFs FROM PPM
if ($nPPM>0)
{
    echo "<h3>PPMs found - going directly to conversion to tif</h3>";
    include("images2tiff.php");
}
// GENERATE PPM
else
{
    echo "<h3>Try to generate PPM's from " . $relativePDF . "</h3>";

    echo "<pre>";

    $outputFile = $destDir.$relativePDF.".tif";  

    $myCmd = _PDFTOPPM . " " . $sourcePDF . " " .  $destDir;


    // KORREKTUR TESTUMGEBUNG (WINDOWS)
    if (instr($myCmd, ":/"))
        $myCmd = str_replace("/", "\\", $myCmd);

    if (!_QUEUE_MODE) {
        $output = array();
        $return_var = "";

        $rLine = exec($myCmd, $output, $return_var);

        if ($return_var == 0)
            echo $myCmd . "\nPDFTOPPM ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
        else
            echo "Error in PDFTOPPM; " . $rLine . "\n";

        // NOW MAKE THESE PPM TO THE PAGEDATA IN THE DATABASE FOR IMAGES2TIFF.PHP
        //if (file_exists($outputFile))   $arrTextFiles[] = $outputFile;
    }
    else {
        // INGEST SCRIPT COMMANDS
        echo $myCmd . "\n";
        $arrQueueCommands[] = $myCmd;
    }

    // $myCmd = _IMG_MAGICK_CONVERT . " " . $arrPageSources[$i] . " " . $outputFile; !!!

    echo "</pre>";
}


?>
