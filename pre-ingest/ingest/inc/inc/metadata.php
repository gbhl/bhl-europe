<?php
// ********************************************
// ** FILE:    METADATA.PHP                  **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    20.12.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



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
$myCmd = exec_prepare($myCmd);

echo "Executing Schema Mapper: <pre>\n";

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


?>
