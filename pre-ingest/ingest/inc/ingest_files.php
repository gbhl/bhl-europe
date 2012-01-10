<?php
// ********************************************
// ** FILE:    INGEST_FILES.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// VARIOUS FUNCTIONS ON INGEST FILES 

// ******************
function isPDF($path)
// ******************
{
    $myFile = strtolower(basename($path));
    
    if (strpos($myFile,'.pdf')!==false) return true;
    
    if (strpos($myFile,'_pdf')!==false) return true;        // checkbox value name mismatch after post
    
    return false;
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

    if (instr(basename($path),$arrME,true,true)) return true;

    return false;
}



// ***************************************************************************
// LOOKS FOR READY INGEST FILES 
// LOOKS IN FILESYSTEM(!) - NOT IN FAST (BUT OLD) DATABASE CACHE
// ***************************************************************************
// TYPES: single_suffix|metadata|pagedata|bookdata|ocrdata 
// ***************************************************************************
function getContentFiles($path,$type='metadata',$include_aip=true,$additional_suffix="")
{
    $arrPageFiles = array();    

    switch($type)
    {
        case 'single_suffix':
            $arrPageExt = array_values(array($additional_suffix));
            break;

        case 'pagedata':
            $arrPageExt = array_values(explode(',', _PAGEDATA_EXT));
            break;

        case 'ocrdata':
            $arrPageExt = array_values(explode(',', _OCRDATA_EXT));
            break;

        case 'bookdata':
            $arrPageExt = array_values(explode(',', _BOOKDATA_EXT));
            break;

        case 'metadata':
            $arrPageExt = array_values(explode(',', _METADATA_EXT));
            break;
        
        default:
            die(_ERR."USE ONE OF THE FOLLOWING TYPES: single_suffix|metadata|pagedata|bookdata|ocrdata");
            break;
    }

    // ADD ADDITIONAL SUFFIX TO THE END OF THE SUFFIXES
    if (($additional_suffix!="")&&($type!='single_suffix'))
    {
        for($i=0;$i<count($arrPageExt);$i++)
                $arrPageExt[$i] = $arrPageExt[$i].$additional_suffix;
    }
    
    reset($arrPageExt);    
    
    // DIRECTORY EINLESEN ( NICHT REKURIV - JOURNALS ? !!!)
    $arrFiles       = getDirectory(clean_path($path),array(),0,"",0);

    if ($include_aip) {
        $arrFiles   = getDirectory(clean_path($path."/"._AIP_DIR),$arrFiles,0,"",0);
    }

    // !!! TEST
    // print_r($arrFiles);
    
    $nFiles = count($arrFiles);

    for ($i = 0; $i < $nFiles; $i++) 
    { 
        $pos = strrpos($arrFiles[$i],".");
       
        if ($pos!==false)
        {   
            $importantPart = substr($arrFiles[$i],$pos);
            
            // ANHAENGEN FALLS DIESER SUFFIX GESUCHT IST
            if (instr(strtolower($importantPart), $arrPageExt, true, true))
                $arrPageFiles[] = $arrFiles[$i];
        }
    }

    return $arrPageFiles;
}





// DEPRECATED
// ****************************************************************
function getFilesFromDatabase($user_id,$content_dir,$type2find='metadata')
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




?>
