<?php
// ********************************************
// ** FILE:    GET_METADATA.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************
// GET METADATA VIA SCHEMA MAPPING TOOL (SMT)
// TO OLEF FORMAT 
// ********************************************

$curStep = 1;

ob_start();

$inputFile = "";


// **********************************
// A) WEBSERVICE METADATA PROVIDER WS
// **********************************
$metadata_ws = trim($arrProvider['metadata_ws']);

if ($metadata_ws != "")
{
    echo "<h3 style='margin-top: 3px;'>Try to get MetaData from Webservice</h3>";

    $resource_context = stream_context_create(array(
        'http' => array(
            'timeout' => _WEBSERVICE_TIMEOUT
        )
            )
    );

    // DIRECTORY NAME IS KEY TO GATHER
    $wsFile = clean_path($destDir."/metadata.ws");
    $metadataFile = clean_path($destDir."/metadata.xml");
    $wsKey = $contentName;
    $pos   = strrpos($wsKey,"/");
    
    if ($pos!==false)
    {
        $wsKey = substr($wsKey,$pos+1);
        if ($wsKey=="") $wsKey = basename($contentName);    // nimmt auch /etc/  --> etc
    }

    $myURL = $metadata_ws.$wsKey;
    
    echo "Try to get metadata over: <b>".$myURL."</b>"; nl(2);

    // DIRECTORY NAME IS KEY TO GATHER
    if (file_put_contents($wsFile,file_get_contents($myURL, 0, $resource_context))>0) {
        
        // Find metadata within envelope
        $wsDoc = new DOMDocument();
        $wsDoc->load($wsFile);
        $metadataNode = $wsDoc->getElementsByTagNameNS('http://www.openarchives.org/OAI/2.0/', 'metadata')->item(0);
        $metadataContent = null;
        // Find first child-node which is an element
        for($i = 0; $i < $metadataNode->childNodes->length; $i++) {
            $currNode = $metadataNode->childNodes->item($i);
            
            if($currNode->nodeType == XML_ELEMENT_NODE) {
                $metadataContent = $currNode;
                break;
            }
        }
        
        // Check if we found some metadata content
        if( $metadataContent != null ) {
            // Extract metadata content and copy it over
            $metadataDoc = new DOMDocument();
            $metadataContent = $metadataDoc->importNode($metadataContent, true);
            $metadataDoc->appendChild( $metadataContent );

            // Save metadata & remove webservice cached result
            $metadataDoc->save($metadataFile);
            unlink($wsFile);

            // Set new metadata-file as input
            $inputFile = $metadataFile;
        }
    }
}

// ***********************************
// B) HOLEN DES LOKALEN METADATENFILES
// ***********************************
if ($inputFile=="")
{
    nl(); echo "Try to get local metadata."; nl();
    $inputFiles  = getContentFiles($contentDir,'metadata',false);
    if (array_key_exists(0, $inputFiles))   $inputFile   = $inputFiles[0];
    unset($inputFiles);
}

if (!file_exists($inputFile)) 
{
     echo _ERR." No local metadata file found or connection failure to webservice.";
     nl(2);
}
else
{
    echo "<h1 style='margin-top: 3px;'>Mapping Metadata to OLEF Repository Standard ";

    if ($cType == 'serial')
        echo "<br><font size=1 color=blue>For: .".str_replace(_USER_CONTENT_ROOT,"",$contentDir)."</font>";

    echo "</h1>";

    echo invisible_html(1024*5);

    echo "Executing Schema Mapper...<pre>\n";    

    @ob_end_flush();
    @ob_flush();
    @flush(); 
    sleep(1);

    include("inc/metadata.php");

    if (file_exists($olef_file)) 
    {
        $olef = file_get_contents($olef_file);
        
        echo "\n<h2>OLEF Result: &nbsp; "; icon("green_16.png");  echo "</h2>";

        echo "<hr>\n<pre>" . htmlspecialchars($olef) . " \n\n";

        // IPR EXTRAHIEREN / FESTLEGEN
        include("inc/ipr_info.php");

        // GUID MINTING
        $cGUID = "";
        include("inc/guid_minter.php");

        echo "\n\n</pre>\n";

        // NUR BEI GUID STEP OK
        if (($cGUID!="")&&($olef!=""))
        {
            mysql_select("update content set content_status='in preparation', 
                content_olef='" . mysql_clean_string($olef) .
                    "', content_guid='".$cGUID."' where content_id=" . $content_id);

            mysql_select("insert into content_guid (content_id,guid,released,last_action) values (".
                    $content_id.",'".$cGUID."',now(),now())");

            // IF SUCCESSFUL SET STATE TO 1 
            if ($cType == 'monograph') {
                if (getContentSteps($content_id)<$curStep) setContentSteps($content_id, $curStep);
            }
            
            $stepFinished = true;
        }
        else {
            echo _ERR . "Could not generate Metadata OLEF/GUID! (Metadata/Minter missing or not functional)";
            $stepErrors = true;
        }

        unset($olef);
    }
    else 
    {
        echo _ERR . "Could not generate METADATA.";
        $stepErrors = true;
    }
}


?>
