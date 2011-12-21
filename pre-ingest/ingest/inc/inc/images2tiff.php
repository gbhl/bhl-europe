<?php

// HOLE PAGES SOURCES UND KONVIERTIERE SIE
unset($arrTiffs);

$arrTiffs = array();

$arrPageSources = getPageSourceFiles($user_id, $contentDir);

$nPages = count($arrPageSources);

$arrQueueCommands = array();

echo "<h3>No required TIF Page Images present, try to convert " . $nPages . " foreign Page Sources.</h3>";

progressBar("Please wait, converting ...", "processing.gif", "margin-top: 55px; left: 300px;", "visible", 2);

echo "<pre>";

for ($i = 0; $i < $nPages; $i++) {
    ob_start();

    $outputFile = substr($arrPageSources[$i], 0, strrpos($arrPageSources[$i], ".")) . ".tif";

    $outputFile = $destDir . basename($outputFile);

    $myCmd = _IMG_MAGICK_CONVERT . " " . $arrPageSources[$i] . " " . $outputFile;

    // KORREKTUR TESTUMGEBUNG (WINDOWS)
    if (instr($myCmd, ":/"))
        $myCmd = str_replace("/", "\\", $myCmd);

    if (!_QUEUE_MODE) {
        $output = array();
        $return_var = "";

        $rLine = exec($myCmd, $output, $return_var);

        if ($return_var == 0)
            echo $myCmd . "\nConvert ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
        else
            echo "Error in converting; " . $rLine . "\n";

        if (file_exists($outputFile)) $arrTiffs[] = $outputFile;
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

echo "</pre>";

close_progressBar();
?>
