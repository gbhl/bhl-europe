<?php
// ********************************************
// ** FILE:    METADATA.PHP                  **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    06.02.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

$myCmd = _SMT;

$myParams   = abfrage("select user_config_smt from users as wert where user_id=" . $user_id, $db);

$myParams   = str_replace(array("<input_file>", "<input file>"),  "\"".$inputFile."\"", $myParams);

$myParams   = str_replace(array("<output_file>", "<output file>"),"\""._OLEF_FILE."\"", $myParams);

@ob_end_clean();

$myCmd = $myCmd . " " . $myParams;
$myCmd = exec_prepare($myCmd);


// echo str_replace(array("-if", "-of"), array("\n-if", "\n-of"), htmlspecialchars($myCmd));
echo htmlspecialchars($myCmd);

if ((!is_array($inputFile)) && (file_exists($inputFile))) 
{
    $output = array();
    $return_var = "";
    
    $rLine = @exec($myCmd, $output, $return_var);

    echo "\n\nReturn Code: " . $return_var . " (" . $rLine . ")\n";
}
else
    echo _ERR . " Input File missing or not found.";

echo "\n\n</pre>\n";

nl();


?>
