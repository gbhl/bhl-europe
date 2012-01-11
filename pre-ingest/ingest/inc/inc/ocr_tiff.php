<?php

// TIFFS VON DATENBANK HOLEN DA IN USER_DIRECTORY ALTSTAND VON ANALYZE 
$query = "select content_pages_tiff from content where content_id=" . $content_id;
$arrTiffs = explode(_TRENNER, abfrage($query, $db));
$nPages   = count($arrTiffs);


echo "<h3>Try to recognize Text in " . $nPages . " Page Images.</h3>";

echo "<pre>";

for ($i = 0; $i < $nPages; $i++) 
{
    ob_start();

    $outputFile = $destDir.basename($arrTiffs[$i]);

    // define("_OCR_DAT",  "/usr/local/share/tessdata/");
    // export TESSDATA_PREFIX=/some/path/to/tessdata
    $myCmd = _TESSERACT . " " . _CONTENT_ROOT . $arrTiffs[$i] . " " . _CONTENT_ROOT . $outputFile . " -l eng ";
    $myCmd = exec_prepare($myCmd);    
    
    if (!_QUEUE_MODE) 
    {
        $output = array();
        $return_var = "";

        $rLine = exec($myCmd, $output, $return_var);

        if ($return_var == 0)
            echo $myCmd . "\nOCR ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
        else
            echo "Error in OCR; " . $rLine . "\n";

        if (file_exists(_CONTENT_ROOT . $outputFile . ".txt"))
            $arrTextFiles[] = $outputFile . ".txt";
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


?>
