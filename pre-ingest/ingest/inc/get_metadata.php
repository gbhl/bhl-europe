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

echo "<h1 style='margin-top: 3px;'>Mapping Your Metadata to OLEF Repository Standard</h1>";

progressBar("Please wait, Schema Mapping in Progress...", "processing.gif", "margin-top: 55px; left: 300px;", "visible", 2);

echo invisible_html(1024 * 5);

@ob_end_flush();
@ob_flush();
@flush();
sleep(1);

$myCmd = _SMT;

$myParams   = abfrage("select user_config_smt from users as wert where user_id=" . $user_id, $db);

$inputFile  = getMetaDataFile($user_id, $contentDir);

$outputFile = $destDir._AIP_OLEF_FN;

$myParams   = str_replace(array("<input_file>", "<input file>"), $inputFile, $myParams);

$myParams   = str_replace(array("<output_file>", "<output file>"), $outputFile, $myParams);

@ob_end_clean();

close_progressBar();


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

        // IF SUCCESSFUL SET STATE TO 1 
        setContentSteps($content_id, 1);
    }

    unset($olef);
}
else
    echo _ERR . " Could not generate OLEF/GUID! (Metadata/Minter missing or not interpretable.)";


?>
