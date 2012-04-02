<?php
// ********************************************
// ** FILE:    OCR_TIFF.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// OCR VON IMAGE QUELLE MIT TESSERACT ...

// http://code.google.com/p/tesseract-ocr/wiki/ReadMe
// http://code.google.com/p/tesseract-ocr/
// http://code.google.com/p/tesseract-ocr/downloads/list


// TIFFS ERMITTELN
// $query = "select content_pages_tiff from content where content_id=" . $content_id;
// $arrTiffs = explode(_TRENNER, abfrage($query, $db));

$arrTiffs = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
// $nTiffs   = count($arrTiffs);



echo "<h3>Try to recognize Text in " . $cPages . " Page Images.</h3><pre>\n";

$ocrLang = getOCRlang($content_id,$olef_file);

for ($i = 0; $i < $cPages; $i++) 
{
    ob_start();

    $outputFile = $destDir.basename($arrTiffs[$i]);

    // define("_OCR_DAT",  "/usr/local/share/tessdata/");
    // export TESSDATA_PREFIX=/some/path/to/tessdata
    // tesseract <image.tif> <outputbasename> [-l <langid>] [configs]

    $myCmd = _TESSERACT . " \"" . $arrTiffs[$i] . "\" \"" . $outputFile . "\" -l ".$ocrLang." ";
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

        if (file_exists($outputFile . ".txt"))
            $arrTextFiles[] = str_replace(_CONTENT_ROOT,'',$outputFile . ".txt");
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
