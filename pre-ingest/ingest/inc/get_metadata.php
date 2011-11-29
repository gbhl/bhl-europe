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

// REQUIRES: $content_id

$myCmd       = _SMT;

$arrProvider = get_provider_details($user_id);


echo "<h1>Mapping Your Metadata to OLEF</h1>";


$myParams     = abfrage("select user_config_smt from users as wert where user_id=".$user_id,$db);

$workDir      = str_replace("//","/",_WORK_DIR.$arrProvider['user_content_id']."/");

$contentDir   = abfrage("select content_root from content where content_id=".$content_id);

$fileMetadata = getMetaDataFile($user_id,$contentDir);




@mkdir(_WORK_DIR.$arrProvider['user_content_id']);

@mkdir(str_replace("//","/",$contentDir."/"._AIP_DIR."/"));


$outputFile   = str_replace("//","/",$contentDir."/"._AIP_DIR."/"._AIP_OLEF_FN);


$myParams = str_replace(array("<input_file>", "<input file>"),$fileMetadata,$myParams);

$myParams = str_replace(array("<output_file>","<output file>"),$outputFile,$myParams);


echo "Executing: <pre> ".htmlspecialchars( $myCmd." ".$myParams)."</pre>";

nl();

// !!! execute here

// _WORKING_

// java.exe -jar "G:\dev\nhm\bhl\ingest\bin\smt\SMT-cli.jar" -m c -cm 5 -if -of 

// $rootDir = _CONTENT_ROOT."/".$arrProvider['user_content_home'];

?>
