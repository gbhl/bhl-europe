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

    // OLEF FERTIGSTELLEN
    include("inc/olef_pages.php");
    include("inc/olef_mods.php");

    
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

        $domDoc = new DOMDocument();    

        // BOOK METS BEFORE PAGE 1 - MONOGRAPH METS
        if ($i==0) 
        {
            $metsFile = $destDir.str_replace(array(":","/"),"_",$objID).".xml";   // FILENAME
            
            if ($cType=='serial')
            {
                // INCLUDE TEMPLATE FOR CURRENT LEVEL
                $domDoc->load(_ABS."inc/xml/".$arrSerialLevels[$sLevel].".xml");
            }
            else
                $domDoc->load(_ABS."inc/xml/monograph.xml");
        }
        // PAGE METS
        else
        {
            // OBJID + PAGE NR.
            $pageID      = $objID."-".substr("00000".$i,-5);     // interne page id = OBJID   !!!! oder reale page nr??
            $cleanPageID = str_replace("/","-",$pageID);
            $cur_tiff    = basename($arrTiffs[($i-1)]);
            $metsFile    = $destDir.str_replace(array(":","/"),"_",$pageID).".xml";   // FILENAME
            $domDoc->load(_ABS."inc/xml/page.xml");
        }

        $docRoot = $domDoc->documentElement;
        $allElements = $domDoc->getElementsByTagName('*');      // RETURNS A NEW INSTANCE OF CLASS DOMNODELIST

        // NODES DES JEW. TEMPATES DURCHGEHEN UND BEARBEITEN
        foreach( $allElements as $curElement )
        {
            $nodeAttributes = $curElement->attributes;
            $nodeValue      = trim($curElement->nodeValue);
            $nodeName       = trim($curElement->nodeName);

            if ($i==0) include("inc/mets_book.php");
            if ($i>0)  include("inc/mets_page.php");
        }

        // Nicely format the output
        $domDoc->formatOutput = true;
        $xmlStr = $domDoc->saveXML();

        //$xmlStr = str_replace(array("&gt;","&lt;","\n\n"),array(">","<","\n"),$xmlStr);

        // OLD: $domDoc->save($metsFile)>0) 
        if (file_put_contents($metsFile, $xmlStr)>0) 
        { 
            $filesGenerated++;

            // LOG
            echo "METS file: ".basename($metsFile)." \t\t generated...\n";
        }
        else
            echo _ERR."METS file: ".basename($metsFile)." \t\t generation failed!\n";

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
