<?php
// ********************************************
// ** FILE:    GET_METS.PHP                  **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************
// CREATE METS CONTROL FILES FOR INGEST
// ********************************************

// CREATE BOOK & PAGE METS CONTENT DESCRIPTOR XMLS

// $destDir .... path to mets files

$curStep = 5;


include_once(_SHARED."file_operations.php");
include_once(_SHARED."AutoDOMDocument.php");


if (!isset($content_id))   die(_ERR." Need content information, ID to prepare METS!");


// ONLY GENERATE METS WHERE A OLEF FILE EXISTS
if (file_exists($olef_file))
{
    // *****
    // INITS
    // *****
    if ($cType=='serial')
    {
        $cGUID       = trim(file_get_contents(clean_path($destDir."/"._AIP_GUID_FN)));
        $objID       = "bhle:".$cGUID;
        $cleanObjID  = str_replace("/","-",$objID);

        // PARENT
        $pGUID       = getParentGuid($contentDir,$sLevel);

        if ($pGUID!="") $objParentID = "bhle:".$pGUID;
        else            $objParentID = "";

        $cleanObjParentID  = str_replace("/","-",$objParentID);
    }
    else
    {
        $cGUID       = $arrContentDetails['content_guid'];
        $objID       = "bhle:".$cGUID;
        $cleanObjID  = str_replace("/","-",$objID);
        $pGUID       = "";
        $objParentID = "";
    }
    
    // load OLEF dom
    $olefDom = new AutoDOMDocument();
    $olefDom->load($olef_file);
    $olefDom->formatOutput = true;
    
    // find OLEF namespace prefix (since this might change)
    $olefNodes = $olefDom->getElementsByTagName( 'olef' );
    if( $olefNodes->length <= 0 ) {
        throw new Exception('Not a valid OLEF input!');
    }
    $olefNode = $olefNodes->item(0);
    $_NAMESPACE_OLEF = $olefNode->namespaceURI;

    // OLEF FERTIGSTELLEN
    echo '<h3>Finishing OLEF Pages & Data</h3><pre>';
    try {
        // OLEF FERTIGSTELLEN
        include("inc/olef_pages.php");
        include("inc/olef_mods.php");
    }
    catch( Exception $e ) {
        echo _ERR. " Unable to complete OLEF: " . $e->getMessage();
    }
    echo "</pre>\n";

    
    // METS FERTIGSTELLEN
    echo "<h3>Prepare METS Files for media(parts)</h3><pre>";

    // TIFFS IN CONTENT TABELLE WERDEN BEI IMAGES UND PDFS AKTUELL GEHALTEN DAHER VERWENDBAR
    // $query    = "select content_pages_tiff from content where content_id=" . $content_id." order by content_pages_tiff asc";
    // $arrTiffs = explode(_TRENNER, abfrage($query, $db));

    $arrTiffs  = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
    // $nTiffs = count($arrTiffs);
    $arrTiffs  = sortPageFiles($arrTiffs);  // IMPORTANT PRE SORT


    // OCR IN CONTENT TABELLE WERDEN BEI IMAGES UND PDFS AKTUELL GEHALTEN DAHER VERWENDBAR
    // $query    = "select content_pages_text from content where content_id=" . $content_id." order by content_pages_text asc";
    // $arrOCR   = explode(_TRENNER, abfrage($query, $db));

    $arrOCR   = getContentFiles($contentDir, 'ocrdata',true);
    // $nOCR  = count($arrOCR);
    $arrOCR   = sortPageFiles($arrOCR);   // IMPORTANT PRE SORT



    $filesGenerated = 0;

    // LOOP FOR BOOK + ALL PAGES

    for ($i=0;$i<=$cPages;$i++)     // $cPages + 1 book
    {
        ob_start();

        $metsDom = new DOMDocument();

        // BOOK METS BEFORE PAGE 1 - MONOGRAPH METS
        if ($i==0) 
        {
            $metsFile = $destDir.str_replace(array(":","/"),"_",$objID).".xml";   // FILENAME
            
            if ($cType=='serial')
            {
                // INCLUDE TEMPLATE FOR CURRENT LEVEL
                $metsDom->load(_ABS."inc/xml/".$arrSerialLevels[$sLevel].".xml");
            }
            else
                $metsDom->load(_ABS."inc/xml/monograph.xml");
        }
        // PAGE METS
        else
        {
            // OBJID + PAGE NR.
            $pageID      = $objID."-".substr("00000".$i,-5);     // interne page id = OBJID   !!!! oder reale page nr??
            $cleanPageID = str_replace("/","-",$pageID);
            $cur_tiff    = basename($arrTiffs[($i-1)]);
            $metsFile    = $destDir.str_replace(array(":","/"),"_",$pageID).".xml";   // FILENAME
            $metsDom->load(_ABS."inc/xml/page.xml");
        }
        $metsDom->preserveWhiteSpace = false;
        $metsDom->formatOutput = true;

        // update mets files
        try {
            if ($i==0) include("inc/mets_book.php");
            if ($i>0)  include("inc/mets_page.php");

            // write file to output
            if (file_put_contents($metsFile, $metsDom->saveXML())>0) { 
                $filesGenerated++;

                echo "METS file: ".basename($metsFile)." \t\t generated...\n";

                // IF SUCCESSFUL SET STATE TO 5
                if ($cType == 'monograph') {
                    if (getContentSteps($content_id)<$curStep) setContentSteps($content_id, $curStep);
                }
            }
            else {
                echo _ERR."METS file: ".basename($metsFile)." \t\t generation failed!\n";
            }

        }
        catch(Exception $e) {
            echo _ERR . " METS file generation: " . $e->getMessage();
        }

        // send output
        @ob_end_flush();
        @ob_flush();
        @flush();    
    }


    if ($filesGenerated<($cPages-2)) 
    {
        echo _ERR."METS files creation failed (".$filesGenerated."/".$cPages." generated)!\n"; 
        $ingestReady = false;
        $stepErrors = true;
    }
    else {
        $stepFinished = true;
        $ingestReady = true;
    }

    echo "\n\n</pre>\n";
}
// Check if we have an empty dir (e.g. for section level)
else if( is_dir_empty($contentDir,true) ) {
    $stepFinished = true;
    $ingestReady = false;
    
}
else {
    $stepFinished = false;
    $ingestReady = false;
}
