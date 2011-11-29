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
            user_config, user_config_smt, user_memo
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
        ingest_id, content_id, user_id, ingest_structure, ingest_alias, ingest_time, 
        ingest status, ingest_last_successful_step, ingest_version, ingest_do_ocr, 
        ingest_do_taxon, ingest_do_sm 
        from ingests where ";
    
    if (is_numeric($ingest_id))         $query .= "ingest_id=".$ingest_id;
    else if (is_numeric($ingest_id))    $query .= "content_id=".$content_id;
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



// ********************************************
function getMetaDataFile($user_id,$content_dir)
// ********************************************
{
    $arrUserDir = explode(_TRENNER,abfrage("select user_directory from users where user_id=".$user_id));

	$nUserDir   = count($arrUserDir);

	$interesting = false;

	for ($i=0;$i<$nUserDir;$i++)
	{
		if (($arrUserDir[$i]==$content_dir)||(_CONTENT_ROOT.$arrUserDir[$i]==$content_dir))
                    $interesting = true;		// AB CONTENT VERZEICHNIS

		if (($interesting)&&(isMetadata($arrUserDir[$i])))	return _CONTENT_ROOT.$arrUserDir[$i];
	}

	return false;
}




// ***********************
function isPagedata($path)
// ***********************
{
    $arrME = explode(',',_PAGEDATA_EXT);
    
    if (instr(basename($path),$arrME,true,true)) return true;

    return false;
}


// *****************************
function show_user_dir($user_id)
// *****************************
{
    echo "<h1 style='margin-top: 6px;'>Last Analyzed Upload Filestructure Elements <font size=-1> (from Management Database, not realtime)</font></h1>";
    
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



// *************************
function getPageFiles($path)
// *************************
{
    $arrPageExt = array_values(explode(',',_PAGEDATA_EXT));
    $arrFiles   = getDirectory($path);
    $nFiles     = count($arrFiles);
    
    $arrPageFiles = array();
    
   
    for ($i=0;$i<$nFiles;$i++)
    {
        // instr($haystack, $needle, $ignoreCase, $oneMatchOnly)
        
        if (instr(basename(strtolower($arrFiles[$i])),$arrPageExt,true,true))
                $arrPageFiles[] = $arrFiles[$i];
                
    }
    
    return $arrPageFiles;
}



?>