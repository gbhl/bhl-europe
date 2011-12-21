<?php
// ********************************************
// ** FILE:    INGEST_TOOLS.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************


// ************************************
function get_provider_details($user_id)
// ************************************
{
    if (is_numeric($user_id))
    {
        $query = "select user_name, user_content_home, user_content_id, is_admin, 
            user_config, user_config_smt, user_memo, queue_mode 
            from "._USER_TAB." where user_id=".$user_id;

        return mysql_get_line($query);
    }
    
    return false;    
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



// ***********************
function isMetadata($path)
// ***********************
{
    $arrME = explode(',',_METADATA_EXT);
    
    if (instr(basename($path),$arrME,true,true)) return true;
            
    return false;
}


// ***********************
function isPagedata($path)
// ***********************
{
    $arrME = explode(',',_PAGEDATA_EXT);
    // echo_pre($arrME); echo $path;
    if (instr(basename($path),$arrME,true,true)) return true;

    return false;
}


// ********************************************
function getMetaDataFile($user_id,$content_dir)
// ********************************************
{
 return getMediaFiles($user_id,$content_dir,'metadata');
}


// **********************************************
function getPageSourceFiles($user_id,$content_dir)
// **********************************************
{
 return getMediaFiles($user_id,$content_dir,'pagesource');      
}


// **********************************************
function getPageTIFFiles($user_id,$content_dir)
// **********************************************
{
 return getMediaFiles($user_id,$content_dir,'tiff');      
}


// **********************************************
function getPageTextFiles($user_id,$content_dir)
// **********************************************
{
 return getMediaFiles($user_id,$content_dir,'text');      
}


// ****************************************************************
function getMediaFiles($user_id,$content_dir,$type2find='metadata')
// ****************************************************************
{
    $arrUserDir  = explode(_TRENNER,abfrage("select user_directory from users where user_id=".$user_id));

    $nUserDir    = count($arrUserDir);

    $interesting = false;
    
    $arrRet      = array();

    for ($i=0;$i<$nUserDir;$i++)
    {
            if (!instr($arrUserDir[$i],"/"._AIP_DIR))   // LEAF AWAY BECAUSE OF ORDER FOR INTERESTING 
            {
            // AB CONTENT VERZEICHNIS WIRDS DAS EINE VERZ. INTERESTING
            if (is_dir(_CONTENT_ROOT.$arrUserDir[$i]))
            {
                if (_CONTENT_ROOT.$arrUserDir[$i]==$content_dir)    $interesting = true;
                else $interesting = false;
            }

            // metadata
            if (($interesting)&&($type2find=='metadata')&&(isMetadata($arrUserDir[$i])))
                return _CONTENT_ROOT.$arrUserDir[$i]; // only 1 metdata file returned
            
            // pagesource
            if (($interesting)&&($type2find=='pagesource')&&(isPagedata($arrUserDir[$i])))	
                $arrRet[] = _CONTENT_ROOT.$arrUserDir[$i];
            
            // tiff
            if (($interesting)&&($type2find=='tiff')&&(instr(basename($arrUserDir[$i]),array(".tif",".tiff"),true,true)))	
                $arrRet[] = _CONTENT_ROOT.$arrUserDir[$i];           
             
            // text
            if (($interesting)&&($type2find=='text')&&(instr(basename($arrUserDir[$i]),array(".txt",".text",".ocr"),true,true)))	
                $arrRet[] = _CONTENT_ROOT.$arrUserDir[$i];
            }
    }

    return $arrRet;
}



// *****************************
function show_user_dir($user_id)
// *****************************
{
    echo "<h1 style='margin-top: 6px;'>Last Analyzed Upload Filestructure Elements <font size=-1> (from Management Database, not realtime, created by last Upload Analyze)</font></h1>";
    
    $dirC = abfrage("select user_directory from users where user_id=".$user_id);
    
	if ($dirC!="")  $dirC = explode('|',$dirC);
	else			$dirC = array();
  
	print_dir_arr($dirC);
}



// ****************************
function print_dir_arr($arrDir)
// *****************************
{
	include_once(_SHARED."imagelib.php");

	echo "<div id=folder name=folder style=\"background-color: #E4F8F7;\">";

	$nFiles = count($arrDir);

	if ($nFiles==0)	echo "User Directory is empty or not analyzed now.";
	else
	{
		echo "<font style=\"font: 13px courier new; font-weight: bold;\">";

		for ($i=0;$i<$nFiles;$i++)	
	    {
			if ((is_dir($arrDir[$i]))||(is_dir(_CONTENT_ROOT.$arrDir[$i])))
				icon("folder_16.png");
			else						
				icon("file_16.png");

			echo "&nbsp;".$arrDir[$i]."\n<br>\n";
		}

		echo "</font>";
	}

	echo "</div>";
}



// *************************
function show_content_root()
// *************************
{
	include_once(_SHARED."dir_tools.php");

    echo "<h1 style='margin-top: 6px;'>Content Root <font size=-1> (for orientation purposes only)</font></h1>";
    
	$arrDir = getDirectory(_CONTENT_ROOT,array(),0,"",0);

	print_dir_arr($arrDir);
}



// ******************
function isPDF($path)
// ******************
{
    $myFile = strtolower(basename($path));
    
    if (strpos($myFile,'.pdf')!==false) return true;
    
    if (strpos($myFile,'_pdf')!==false) return true;        // checkbox value name mismatch after post
    
    return false;
}



// ************************************************
function getPageFiles($path, $additional_suffix="") 
// ************************************************
{
    include_once(_SHARED."dir_tools.php");

    $arrPageExt = array_values(explode(',', _PAGEDATA_EXT));
    
    if ($additional_suffix!="")
    {
        for($i=0;$i<count($arrPageExt);$i++)
                $arrPageExt[$i] = $arrPageExt[$i].$additional_suffix;
    }
    
    reset($arrPageExt);
    
    $arrFiles     = getDirectory($path);
    $nFiles       = count($arrFiles);
    $arrPageFiles = array();

    for ($i = 0; $i < $nFiles; $i++) 
    {
        // instr($haystack, $needle, $ignoreCase, $oneMatchOnly)
        if (instr(basename(strtolower($arrFiles[$i])), $arrPageExt, true, true))
            $arrPageFiles[] = $arrFiles[$i];
    }
    
    return $arrPageFiles;
}



// SET LAST SUCCESSFUL STEP WITH CONTENT OPERATIONS
function setContentSteps($content_id,$step=0)
{
    return mysql_select("update content set content_last_succ_step=".$step." where content_id=".$content_id);
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


// ADD COMMANDS TO QUEUE SCRIPT FILE
function queue_add($queueFile, $arrQueueCommands) 
{
    $destFile = _WORK_DIR . $queueFile;

    $content2write = implode("\n", $arrQueueCommands);
    
    $nBytes = file_put_contents($destFile, "\n".$content2write."\n", FILE_APPEND);
    
    if($nBytes>0)
         return $nBytes. " Bytes / ".count($arrQueueCommands)." Commands queued.\n";
    else return _ERR." Queueing failed!";
}



?>