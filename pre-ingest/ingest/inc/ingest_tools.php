<?php
// ********************************************
// ** FILE:    INGEST_TOOLS.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    19.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

include_once("ingest_files.php");       // TOOLS FOR INGEST FILES


// ************************************
function get_provider_details($user_id)
// ************************************
{
    if (is_numeric($user_id))
    {
        $query = "select user_name, user_content_home, user_content_id, is_admin, 
            user_config, user_config_smt, user_memo, queue_mode, metadata_ws, default_ipr  
            from "._USER_TAB." where user_id=".$user_id;

        if (is_numeric($user_id)) return mysql_get_line($query);
    }
    
    return false;    
}



// **************************************
// CONTENT DETAILS HOLEN
// **************************************
function get_content_details($content_id)
{
    $query = "select 
        content_root, content_name, content_pages, content_type, content_ipr, content_alias, content_status, 
        content_ctime, content_atime, content_size, content_last_succ_step, content_guid
        from content where content_id=".$content_id;
    
    if (is_numeric($content_id))    return mysql_get_line($query);
    else                            return false;
}




// ***************************************************
// INGEST DETAILS UEBER INGEST ODER CONTENT ID HOLEN
function get_ingest_details($ingest_id="",$content_id)
// ***************************************************
{
    $query = "select 
        ingest_id, content_id, user_id, ingest_structure_id, ingest_alias, ingest_time, 
        ingest_status, ingest_last_successful_step, ingest_version, ingest_do_ocr, 
        ingest_do_taxon, ingest_do_sm 
        from ingests where ";
    
    if (is_numeric($ingest_id))         $query .= "ingest_id=".$ingest_id;
    else if (is_numeric($content_id))   $query .= "content_id=".$content_id;
    else return false;

    return mysql_get_line($query); 
}



// **********************
function show_app_title()
// **********************
{
 echo "<h1>"._APP_NAME." - <font size=-1>Version "._APP_VERSION."</font></h1>";    
}



// *******************************
function show_help_file($filename)
// *******************************
{
    show_app_title();
    echo "<PRE>";
    echo file_get_contents($filename);
    ?>
    </PRE>
    <center>
    <input type="button" onClick="window.close();" class="button" style="margin-top: 0px; width: 130px;" value=" Close ">
    </center>
    <br>
    <?php
}



// SET LAST SUCCESSFUL STEP WITH CONTENT OPERATIONS
function setContentSteps($content_id,$step=0)
{
    return mysql_select("update content set content_last_succ_step=".$step." where content_id=".$content_id);
}


// GET LAST SUCCESSFUL STEP WITH CONTENT OPERATIONS
function getContentSteps($content_id)
{
    return abfrage("select content_last_succ_step from content where content_id=".$content_id);
}

        

// SET INGEST_LAST_SUCCESSFUL_STEP
function setIngestState($ingest_id,$state=0)
{
    if ((is_numeric($ingest_id))&&(is_numeric($state))) {
        $query = "update ingests set ingest_last_successful_step=".$state." where ingest_id=".$ingest_id;
        return mysql_select($query);
    }
    
    return false;
}



// ADD COMMANDS TO QUEUE SCRIPT FILE IF NOT ALREADY INSIDE

function queue_add($queueFile, $arrQueueCommands) 
{
    include_once(_SHARED."file_operations.php");
    
    $content2write = "";
    $linesAdded = 0;
    $nBytes = 0;

    $nCommands = count($arrQueueCommands);

    if ($nCommands>0)
    {
        for ($i=0;$i<$nCommands;$i++)
        {
            // ONLY ADD COMMAND IF NOT EXISTING
            if (!file_content_exists($queueFile,$arrQueueCommands[$i],true,true))
            {
                $content2write .= $arrQueueCommands[$i]."\n";
                $linesAdded++;
            }
        }

        if ($content2write!="")
        $nBytes = file_put_contents($queueFile, "\n".$content2write."\n", FILE_APPEND);

        if($nBytes>0)   
        {
            return $nBytes. " Bytes / ".$linesAdded." commands queued.\n";
            
            // MAKE CHECK COPY 
            // file_put_contents($queueFile.".chk", "\n".$content2write."\n", FILE_APPEND);
        }
        else            return _ERR." Queueing failed or commands already queued!";
    }
    
    return "Info: Nothing queued.";
}



// ********************************************************
function close_ingest_popup($button_text="",$refresh=true)
// ********************************************************
// BUTTON CLOSES CURRENT POPUP AND RE-LOADS INGEST_LIST
{
   include_once(_SHARED."formlib.php");
   
   if ($refresh)
   button($button_text,"if (opener.document) { opener.document.body.focus(); opener.document.location.href='"._SYSTEM."?menu_nav=ingest_list'; } window.close();",900,-1);
   else
   button($button_text,"window.close();",900,-1);
}



// ***********************************
function getReverseLookupURL($absPath)
// ***********************************
{
    $retVal = str_replace(_CONTENT_ROOT,"",$absPath);

    $retVal = _REVERSE_LOOKUP_URL.$retVal;

    return $retVal;
}


// ***************************************************
function getOCRlang($content_id,$olef_file=_OLEF_FILE)
// ***************************************************
{
    // SET $tesseractMappings/_TESSERACT_DEAULT_LANG
    include(_ABS."config/tesseract_config.php");    // x-times not once
    
    $olefLang = "";
    $retLang  = _TESSERACT_DEAULT_LANG;
    
    // LOAD OLEF TO DOM
    $domDoc   = new DOMDocument();
    $domDoc->load($olef_file);

    // SEARCH FOR mods:languageTerm
    $allElements = $domDoc->getElementsByTagName('*');
    
    // NODES DES JEW. TEMPATES DURCHGEHEN UND BEARBEITEN
    foreach( $allElements as $curElement )
    {
      // $nodeAttributes = $curElement->attributes;
         $nodeName = trim($curElement->nodeName);
         
         if ($nodeName=="mods:language")
         {
             $olefLang = strtolower(trim($curElement->nodeValue));
             break;
         }
    }
    
    // IF NOT CLEAR USE: _TESSERACT_DEAULT_LANG
    if ($olefLang=="")  return _TESSERACT_DEAULT_LANG;
    else
    {
        $nMappings = count($tesseractMappings);
        
        // GET KEY OF LANGUAGE WHICH IS DIRECTLY THE TESSERACT LANG ABREVIATION
        $searchfor = ",".$olefLang.",";
        
        $ret = preg_grep("/{$searchfor}/", $tesseractMappings );

        list($key,$val) = each($ret);
        
        // NUR VERWENDEN WENN TRAINEDDATA EXISTIERT
        if (($key!="")&&(file_exists(_OCR_DAT.$key.".traineddata")))  return $key;
    }
    
    echo "Taking default OCR language (".$retLang.") ...\n";
    
    return $retLang;
}



// ************************************************************
function is_content_job_running($contentID,$regard_queued=true)
// ************************************************************
{
    $contentID = (int) $contentID;
    
    // EXISTIEREN CONTENT ID JOB FILES IN EXEC DIR UNTERVERZEICHNISSEN
    // SEE INIT.PHP
    
    $arrJobFiles = getContentFiles(_QUEUE_RUNDIR,'single_suffix',false,_QUEUE_SUFFIX,2);
    
    // QUEUE DIR AUCH BERUECKSICHTIGEN
    if ($regard_queued)
    $arrJobFiles = array_merge($arrJobFiles,getContentFiles(_USER_WORKDIR,'single_suffix',false,_QUEUE_SUFFIX,2));
    
    $nJobFiles   = count($arrJobFiles);
    $contentJobs = 0;

    for ($i=0;$i<$nJobFiles;$i++)
    {
        if (instr(basename($arrJobFiles[$i]),_QUEUE_PREFIX.$contentID._QUEUE_SUFFIX))  $contentJobs++;
    }
    
    if ($contentJobs>0)   return true;
    
    return false;
}



?>