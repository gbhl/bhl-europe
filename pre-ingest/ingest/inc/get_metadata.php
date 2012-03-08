<?php
// ********************************************
// ** FILE:    GET_METADATA.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GET METADATA VIA SCHEMA MAPPING TOOL (SMT)
// TO OLEF FORMAT 
// ********************************************

ob_start();

$inputFile = "";

// **********************************
// A) WEBSERVICE METADATA PROVIDER WS
// **********************************
$metadata_ws = trim($arrProvider['metadata_ws']);

if ($metadata_ws != "")
{
   
    echo "<h3 style='margin-top: 3px;'>Try to get Metadata from Webservice</h3>";

    $resource_context = stream_context_create(array(
        'http' => array(
            'timeout' => _WEBSERVICE_TIMEOUT
        )
            )
    );

    // DIRECTORY NAME IS KEY TO GATHER
    $metadataFile = clean_path($contentDir."/metadata_ws.xml");
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
    if (file_put_contents($metadataFile,file_get_contents($myURL, 0, $resource_context))>0)
    $inputFile = $metadataFile;
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
    echo "<h1 style='margin-top: 3px;'>Mapping Your Metadata to OLEF Repository Standard</h1>";
    echo invisible_html(1024*5);
    
    echo "Executing Schema Mapper...<pre>\n";    

    @ob_end_flush();
    @ob_flush();
    @flush(); 
    sleep(1);

    include_once("inc/metadata.php");

    if (file_exists(_OLEF_FILE)) 
    {
        $olef = file_get_contents(_OLEF_FILE);
        echo "\n<h2>OLEF Result: &nbsp; ";
        icon("green_16.png");
        echo "</h2>";

        echo "<hr>\n<pre>" . htmlspecialchars($olef) . " \n\n";
        

        // IPR EXTRAHIEREN
        $content_ipr = "";
        if (instr($olef, "accessCondition",true,true))      // instr($haystack, $needle, $ignoreCase, $oneMatchOnly)
        {
            $pos1        = stripos($olef,"accessCondition");
            $pos2        = stripos($olef,">",$pos1+14);
            $pos3        = stripos($olef,"<",$pos2+1);
            $content_ipr = str_replace("\n","",trim(substr($olef,$pos2+1,$pos3-($pos2+1))));
        }

        // NUR EIN GUELTIGER EINTRAG WIRD UEBERNOMMEN
        if (!instr($content_ipr,array_keys($arrIPR),true,true))  $content_ipr = "";
                
        if ($content_ipr == "")
        $content_ipr = abfrage("select default_ipr from "._USER_TAB." where user_id=".$user_id,$db);

        // CONTENT IPR UPDATE
        $query = "update content set content_ipr = '".$content_ipr."' where content_id=".$content_id;
        mysql_select($query,$db);
        
        echo "\nContent IPR: ".$content_ipr."\n\n";
        
        unset($content_ipr);


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
            if (getContentSteps($content_id)<1) setContentSteps($content_id, 1);
        }

        unset($olef);
    }
    else
        echo _ERR . "Could not generate OLEF/GUID! (Metadata/Minter missing or not interpretable.)";
}

?>
