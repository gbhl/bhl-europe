<?php

// HOLE PAGES SOURCES UND KONVIERTIERE SIE
// HIER AUCH PPM BEACHTEN DA VON PDF2TIFF AUFGERUFEN


unset($arrTiffs);

$arrTiffs = array();


$arrPageSources = getContentFiles($contentDir, 'pagedata', true); // tif, ppm, ...
$nPages = count($arrPageSources);


if ($nPages==0) echo _ERR." No Page Sources found.)";
else 
{
    echo "<h3>Try to convert " . $nPages . " foreign Page Sources to TIF.</h3>";

    echo "<pre>";

    for ($i = 0; $i < $nPages; $i++) {
        ob_start();

        $outputFile = substr($arrPageSources[$i], 0, strrpos($arrPageSources[$i], ".")) . ".tif";
        $outputFile = $destDir . basename($outputFile);

        $myCmd = _IMG_MAGICK_CONVERT . " " . $arrPageSources[$i] . " " . $outputFile;
        $myCmd = exec_prepare($myCmd);
        
        if (!_QUEUE_MODE) 
        {
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
}



?>
