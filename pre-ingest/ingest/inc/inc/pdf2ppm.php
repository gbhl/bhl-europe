<?php
// ********************************************
// ** FILE:    PDF2PPM.PHP                   **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
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

include_once(_SHARED."pdf_tools.php");

$relativePDF = basename($sourcePDF);

$arrPPM = getContentFiles($contentDir, 'single_suffix', true,'.ppm');
$nPPM   = count($arrPPM);

// GENERATE TIFs FROM PPM
if ($nPPM>0)
{
    // maas et al 2011 annonaceae index nordic j bot 29 3 257-356.pdf-000001.ppm
    // maas et al 2011 annonaceae index nordic j bot 29 3 257-356_1.ppm
    
    echo "<h3>PPMs found - file name convention renaming check invoked</h3>Pages renaming: ";
    $nRenamed=0;
    
    $pdfStructure = pdfInfo::read($sourcePDF)->getStructure();
    var_export( $pdfStructure );
    
    foreach( $arrPPM as $entryPPM ) {
        $namePPM = preg_replace( '/\-(\d+)\.ppm$/i', '_$1.ppm', $entryPPM);
        
        // Check if we have a structure information
        if( $pdfStructure != false ) {
            // Find sequence of current page
            $matches = array();
            if( preg_match('/(\d+)\.ppm$/i', $namePPM, $matches) > 0 ) {
                $sequence = intval($matches[1]);
                $pageInfo = $pdfStructure[$sequence];
                
                // If it is a number, we use it as printed page number
                if(is_numeric($pageInfo)) {
                    $namePPM = preg_replace( '/\.ppm$/', '_' . _DEFAULT_PAGETYPE . '_' . $pageInfo . '.ppm', $namePPM );
                }
                // Else we use it as page-type
                else {
                    $pageInfo = preg_replace( array('/\s/', '/_/', '/\./'), '', $pageInfo );
                    $namePPM = preg_replace( '/\.ppm$/', '_' . $pageInfo . '.ppm', $namePPM );
                }
            }
        }

        // Rename suffix created by pdftoppm to conform to FSG standard
        if( !rename( $entryPPM, $namePPM ) ) {
            echo 'Error: Unable to rename ' . $entryPPM . '<br />\n';
        }
        
        /*if (instr(basename($arrPPM[$i]),".pdf"))
        {
            $newPPMname = str_replace(
                    array(
                        "-0000000000",
                        "-000000000",
                        "-00000000",
                        "-0000000",
                        "-000000",
                        "-00000",
                        "-0000",
                        "-000",
                        "-00",
                        "-0"),"_",basename($arrPPM[$i]));
            
            $newPPMname = str_replace(
                    array(".pdf"
                        ),"",$newPPMname);

            // **************************
            //   WEITERE PDF ANALYSE !!!
            // **************************
            // GATHERING OF REAL(!) PAGE NUMBER AND PAGE TYPE FROM PDF PROPERTIES
            
            $seqNumber  = substr($newPPMname,strrpos($newPPMname,"_")+1);
            $seqNumber  = str_replace(".ppm","",$seqNumber);

            $pageNumber = $seqNumber;       // WHEN MISSING IN PPDF METADATA

            // TYPE UND PAGE NUMBER ANHAENGEN
            $newPPMname = str_replace(".ppm","_"._DEFAULT_PAGETYPE."_".$pageNumber.".ppm",$newPPMname);

            // NEUEN ABSOLUT PFAD BAUEN
            $newPPMname = str_replace(basename($arrPPM[$i]),"",$arrPPM[$i]).$newPPMname;            

            if (rename($arrPPM[$i],$newPPMname))
            {
                echo $i.",";
                $nRenamed++;
            }
        }*/
    }
    
    $arrPPM = getContentFiles($contentDir, 'single_suffix', true,'.ppm');
    $nPPM   = count($arrPPM);
    
    echo "<h3>PPMs checked, now going directly to conversion to TIF</h3>";
    
    include("images2tiff.php");
}
// GENERATE PPM
else
{
    ob_start();
    
    echo "<h3>Try to generate PPM's from " . $relativePDF . "</h3><pre>\nRunning...\n\n";

    echo invisible_html(1024 * 5);
    
    $outputFile = $destDir . basename(cleanPDFName($relativePDF), '.pdf');    // not real output file is pdftoppm root!
                                                                    // PPM-root-nnnnnn.ppm, where nnnnnn is the page number. 
                                                                    // Remove '_' since they will confuse the final page parsing step

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
$nPPM = count(getContentFiles($contentDir, 'single_suffix', true,'.ppm'));
