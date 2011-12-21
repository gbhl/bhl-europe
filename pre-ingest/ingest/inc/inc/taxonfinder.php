<?php

// !!! QUEUING EINBAUEN
// !!! file check in allen so einbauen

$arrPagesTextFiles = getPageFiles($contentDir,".txt");  // REALE FILES DURCHSUCHEN upload
$nTextFiles = count($arrPagesTextFiles);

if ($nTextFiles == 0)
$arrPagesTextFiles = getPageFiles($destDir,".txt");     // REALE FILES DURCHSUCHEN .aip
$nTextFiles = count($arrPagesTextFiles);

progressBar("Please wait, Taxon Finding is running ...", "processing.gif", "margin-top: 55px; left: 300px;", "visible", 2);


$resource_context = stream_context_create(array(
    'http' => array(
        'timeout' => _TAXON_WEB_TIMEOUT
    )
        )
);

// FUER ALLE TEXTFILES TAXON WEBSERVICE AUFRUFEN
echo "<pre>";

for ($i = 0; $i < $nTextFiles; $i++) {
    ob_start();
    // $outputFile   = str_replace("//","/",$contentDir."/"._AIP_DIR."/".basename($arrPagesTextFiles[$i]).".TAX");

    $outputFile = substr(basename($arrPagesTextFiles[$i]), 0, strrpos(basename($arrPagesTextFiles[$i]), '.'));
    // darf kein .txt in sich tragen
    $outputFile = str_replace("//", "/", $contentDir . "/" . $outputFile . ".tax");

    $inputFileSelfReferenceHTTPpath = _HOME . "" . $arrPagesTextFiles[$i];

    $myURL = _TAXON_FINDER . _REVERSE_LOOKUP_URL;

    // !!!! change this    _HOME."http://bhl_TAXON_TESTFILE";       http://bhl-int.nhm.ac.uk
    $myURL .= "testdata/spices_prepared/Darwins_Origin/" . basename($arrPagesTextFiles[$i]);

    echo "Try to analyze " . basename($arrPagesTextFiles[$i]) . " .... ";
    // echo $myURL . "\n";
    // TAXONFINDING
    $strXMLTaxons = $file_get_contents($myURL, 0, $resource_context);
    $nBytes = false;

    // WRITE TAXON FILE FOR PAGE IF NOT EMPTY
    if (!empty($strXMLTaxons)) {
        $nBytes = file_put_contents($outputFile, $strXMLTaxons);

        if ($nBytes !== false) {
            echo $nBytes . " bytes written, ok.\n";
            $arrTaxons[] = $outputFile;  // !!! diese zeile checken und queuing rein
        }

        if ($nBytes === false) {
            echo "No taxons found or error.\n";
        }
    }

    @ob_end_flush();
    @ob_flush();
    @flush();
}

echo "</pre>";

close_progressBar();
?>
