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



// A) WEBSERVICE METADATA PROVIDER WS
$metadata_ws = trim($arrProvider['metadata_ws']);

if ($metadata_ws != "")
{
   
    echo "<h3 style='margin-top: 3px;'>Try to get Metadata from Webservice</h3>";

    $resource_context = stream_context_create(array(
        'http' => array(
            'timeout' => _TAXON_WEB_TIMEOUT
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

    // DIRECTORY NAME IS KEY TO GATHER
    if (file_put_contents($metadataFile,file_get_contents($myURL, 0, $resource_context))>0)
    $inputFile = $metadataFile;
}
else
{
    // B) HOLEN DES LOKALEN METADATENFILES

    $inputFiles  = getContentFiles($contentDir,'metadata',false);
    $inputFile   = $inputFiles[0];
    unset($inputFiles);
}

if ((!isset($inputFile))||(!file_exists($inputFile))) 
    die (_ERR." Metadata File not found or connection failure.");

ob_start();

echo "<h1 style='margin-top: 3px;'>Mapping Your Metadata to OLEF Repository Standard</h1>";

echo invisible_html(1024 * 5);

@ob_end_flush();
@ob_flush();
@flush();
sleep(1);

$myCmd = _SMT;

$myParams   = abfrage("select user_config_smt from users as wert where user_id=" . $user_id, $db);

$outputFile = $destDir._AIP_OLEF_FN;

$myParams   = str_replace(array("<input_file>", "<input file>"), $inputFile, $myParams);

$myParams   = str_replace(array("<output_file>", "<output file>"), $outputFile, $myParams);

@ob_end_clean();


$myCmd = $myCmd . " " . $myParams;

// KORREKTUR TESTUMGEBUNG (WINDOWS)
if (instr($myCmd, ":/"))
    $myCmd = str_replace("/", "\\", $myCmd);

echo "Executing Schema Mapper: 
        <pre>\n";

echo str_replace(array("-if", "-of"), array("\n-if", "\n-of"), htmlspecialchars($myCmd));

if ((!is_array($inputFile)) && (file_exists($inputFile))) 
{
    $output = array();
    $return_var = "";
    
    $rLine = @exec($myCmd, $output, $return_var);

    echo "\n\nReturn Code: " . $return_var . " (" . $rLine . ")\n";
    // echo print_r($output);
    // echo_pre($output);
}
else
    echo _ERR . " Input File missing or not found.";

echo "</pre>";

nl();

if (file_exists($outputFile)) 
{
    $olef = file_get_contents($outputFile);
    echo "\n<h2>OLEF Result: &nbsp; ";
    icon("green_16.png");
    echo "</h2>";

    echo "<hr>\n<pre>" . htmlspecialchars($olef) . " \n\n";
    
    $cGUID = "";
    include("get_guid.php");
    
    echo "</pre>";    
    
    // NUR BEI GUID STEP OK
    if (($cGUID!="") &&($olef!=""))
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


?>
