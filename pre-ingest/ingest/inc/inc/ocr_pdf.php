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

echo "<h3>Try to extract Text from " . $relativePDF . "</h3>";

echo "<pre>";

$outputFile = $destDir.$relativePDF.".txt";  

$myCmd = _PDFTOTEXT . " " . $sourcePDF . " " .  $outputFile;


// KORREKTUR TESTUMGEBUNG (WINDOWS)
if (instr($myCmd, ":/"))
    $myCmd = str_replace("/", "\\", $myCmd);

if (!_QUEUE_MODE) {
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

echo "</pre>";


?>
