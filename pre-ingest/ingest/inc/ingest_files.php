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
function getContentFiles($path,$type='metadata',$include_aip=true,$additional_suffix="",$depth=0)
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

    // DIRECTORY EINLESEN
    $arrFiles       = getDirectory(clean_path($path),array(),0,"",$depth);

    // HAENGT NOCHMAL .AIP FILES DAZU FALLS DEPTH == 0
    if ((((int)$depth==0))&&($include_aip)&&(is_dir(clean_path($path."/"._AIP_DIR)))) {
        $arrFiles   = getDirectory(clean_path($path."/"._AIP_DIR),$arrFiles,0,"",$depth);
    }

    $nFiles = count($arrFiles);

    // CONTROL FILES ETC. ZUM IGNORIEREN
    $arrCF = array(_FEDORA_CF_READY,_FEDORA_CF_FINISHED,_FEDORA_CF_RUNNING,
        _AIP_OLEF_FN,_AIP_GUID_FN,_THUMB_BGRD,_THUMB_FN);

    for ($i = 0; $i < $nFiles; $i++) 
    {
        $pos = strrpos($arrFiles[$i],".");

        if (($pos!==false)&&(!in_array(basename($arrFiles[$i]),$arrCF)))
        {   
            $importantPart = substr($arrFiles[$i],$pos);

            // ANHAENGEN FALLS DIESER SUFFIX GESUCHT IST
            if ((strlen($arrFiles[$i])>3)&&(instr(strtolower($importantPart), $arrPageExt, true, true)))
                $arrPageFiles[] = $arrFiles[$i];
        }
    }

    return $arrPageFiles;
}



// *******************************
function sortShortFirst($arrToSort)
// *******************************
// EXAMPLE SORT SUPPORT:
// maas_et_al_2011_annonaceae_index_nordic.pdf_100_PDF_100.txt
// maas_et_al_2011_annonaceae_index_nordic.pdf_1_PDF_1.txt
/* 
 * example: 
$arrToSort = array(
    'maas_et_al_2011_annonaceae_index_nordic.pdf_50_PDF_11.txt',
    'maas_et_al_2011_annonaceae_index_nordic.pdf_200_PDF_1.txt',
    'maas_et_al_2011_annonaceae_index_nordic.pdf_100_PDF_100.txt',
    'maas_et_al_2011_annonaceae_index_nordic.pdf_1_PDF_1.txt'
    
);

*/
{
    $arrSorted = $arrToSort;
    $nElements = count($arrSorted);
    
    sort($arrSorted);
    
    // BUILD MULTI ARRAY WITH LENGTH AS INDEX
    
    $arrTemp = array();
    $minLen  = 0;
    $maxLen  = 0;
    
    for ($i=0;$i<$nElements;$i++)
    {
        $curLen = strlen($arrSorted[$i]);

        $arrTemp[($curLen)][] = $arrSorted[$i];

        if ($minLen>$curLen)    $minLen = $curLen;
        if ($maxLen<$curLen)    $maxLen = $curLen;
    }
    
    // ES DARF NICHT MEHR SORT V. WERDEN, DENN SORTIERUNG KILLT KEYS!
    
    // FALLS GLEICHLANG, RETURNIERE NORMAL SORTIERTEN EINGANGSARRAY
    if ($maxLen==$minLen)   return $arrSorted;

    reset($arrTemp);
    
    // UNTERSCHIEDL. LAENGEN -> KURZE ZUERST
    // SORT INDIVIDUAL LENGTH ARRAYS AND ADD ALL TOGETHER WITH SHORTEST FIRST
    $arrReturn = array();
    
    for ($curLen=$minLen;$curLen<=$maxLen;$curLen++)
    {
        if (array_key_exists($curLen, $arrTemp))
        {
            sort($arrTemp[$curLen]);               // IM AKTUELLEN LAENGE ARRAY SORTIEREN
            $arrReturn = array_merge($arrReturn,$arrTemp[$curLen]);
        }
    }
    
    // echo_pre($arrReturn);
    
    return $arrReturn;
}





// ****************************************************************************
function getPageInfoFromFile($file_path,$curOrderNumber="",$errorTolerant=false)
// ****************************************************************************
// 
// AB ELEMENT 3 SIND ALLES PAGE NUMBERS !
// $part .... prefix[0], sequence[1], type[2], pagenumbers[3] .... [n]
// 
// NBGB013726AIGR1889FLOREELE00_0007_PAGE_3_4_5.tif
// 
// maas et al 2011 annonaceae index nordic j bot 29 3 257-356.pdf-000001.tif
// ODER
// maas et al 2011 annonaceae index nordic j bot 29 3 257-356_1_PAGE_1.tif
// HINT:      
// ALL INFORMATION EXCEPT ID AND SEQUENCE IN THE FILENAME ARE OPTIONAL.
{
     include_once(_SHARED."file_operations.php");

     $arrReturn = array();
     $minParts  = 2;
     $optParts  = 4;

     $file_name = file_remove_extension(basename($file_path));

     // MAX. 2 EXTENSIONS WEG (Z.B. .tif.tax) NICHT BEI PDF (da z.b.: xxxxxxxxxx.pdf_7_PDF_7.tax)
     if (!isPDF($file_name)) $file_name = file_remove_extension($file_name);

     $arrReturn = explode("_",$file_name);
     $nArr      = count($arrReturn);

     // FEHLERBEHANDLUNG
     if (($nArr<$optParts)&&($errorTolerant)&&(is_numeric($curOrderNumber)))
     {
        if ($nArr==2)      $arrReturn = array_merge($arrReturn,array(_DEFAULT_PAGETYPE,$curOrderNumber));
        else if ($nArr==3) $arrReturn = array_merge($arrReturn,array($curOrderNumber));
        
        $nArr = count($arrReturn);
     }

     // PREFIX | SEQUENCE | TYPE | PAGENUMBER   (SEQUENCE=PAGENUMBER)

     if ($nArr < $minParts) 
     {
        if (basename($file_path)!="") $errFile = basename($file_path);
        else                          $errFile = $file_path;
        
        echo _ERR."Filename convention broken! Minimal requirements (PREFIX_SEQUENCE nomenclatur) not fulfilled.<br>\n 
            Please rename your uploaded page files according to the File Submission Guidelines.<br>\n
            (FSG: <b>PREFIX | SEQUENCE | [TYPE | PAGENUMBER]</b>)!<br>\n
            Affected File: <font color=red><b>".$errFile."</b></font><br>\n";

        return false;
     }

    return $arrReturn;
}




// **************************************************
function sortPageFiles($arrToSort,$sortBy='sequence')
// **************************************************
// SORTIERT FILES DEFAULT NACH SEQUENCE NUMMER
// SORTIERT IM PAGENUMBER MODUS NACH SEQUENCE WENN PAGE NUMBERS FEHLEN
// WENN NOMENKLATUR NICHT STIMMT KOMMT SORTSHORTFIRST() ZUM EINSATZ
// PRE-SEQ-TYPE-PNO
// AUF FUER TAXON FILES
{
    if ($sortBy=='pagenumber')  $sortBy = 2;    // pagenumber sort index
    else                        $sortBy = 1;    // sequence sort index

    $sortShortFirst = false;

    if (is_array($arrToSort))
    {
        $nElements = count($arrToSort);
        
        if ($nElements>1)
        {
            // ZUSAMMENSETZUNG MIT STICHPROBE EVALUIEREN
            $arrFileParts = getPageInfoFromFile($arrToSort[0]);

            if ($arrFileParts)  // FALLS MINDESTENS MIN. NOMENKLATUR VORH.
            {
                // PAGE NUMBER OD. SEQUENCE INFO VORHANDEN
                if ((!array_key_exists($sortBy,$arrFileParts))||($arrFileParts[$sortBy]==""))
                {
                    if ($sortBy==1) $sortShortFirst = true;
                    else
                    {
                        $sortBy = $sortBy-1;    // ALTERNATIV AUF SEQUENCE ZURUECKSPRINGEN

                        if ((!array_key_exists($sortBy,$arrFileParts))||($arrFileParts[$sortBy]==""))
                        {
                            $sortShortFirst = true;
                        }
                    }
                }
            }
            else $sortShortFirst = true;

            // NUR SORTSHORTFIRST MOEGLICH
            if ($sortShortFirst)    return sortShortFirst($arrToSort);
            else
            {
                // OPTIMALE SORTIERUNG NACH SPALTE MOEGLICH
                // NBGB013726AIGR1889FLOREELE00_0007_PAGE_3_4_5.tif
                // maas et al 2011 annonaceae index nordic j bot 29 3 257-356.pdf-000001.tif
                $arrSortable = array();
                $arrMerker   = array();
                $notified    = false;

                // MIT BETREFFENDER SPALTE GEINDEXTER ARRAY AUFBAU
                for ($i=0;$i<$nElements;$i++)
                {
                    $partsCur = getPageInfoFromFile($arrToSort[$i]);

                    if ($partsCur) 
                    {
                        $curKey   = $partsCur[$sortBy];

                        if (is_numeric($curKey))
                        {
                            if (!in_array($curKey,$arrMerker))  $arrSortable[($curKey)] = $arrToSort[$i];
                            else if (!$notified) {
                                echo ERR."One or more duplicate Sortig Kriteria(s) : <b>".$curKey."</b> : e.g. in file ".basename($arrToSort[$i])." !\n<br>";
                                $notified = true;
                            }

                            $arrMerker[] = $curKey;
                        }
                        else if (!$notified) {
                            echo _ERR." There is a (Sortig Kriteria) filename part missing (".basename($arrToSort[$i]).")\n<br>";
                            $notified = true;
                        }
                    }
                }

                ksort($arrSortable);        // key sort
                reset($arrSortable);
                
                // echo_pre($arrSortable);

                $arrSorted = array();       // ausgabe array

                // KEYS DES SORTIERTEN ARRAYS WIEDER VON 0...N AUFBAUEN
                while (list($key, $val) = each($arrSortable)) 
                {
                    $arrSorted[] = $val;
                }

                return $arrSorted;
            }
        }
        else return $arrToSort;
    }

    return false;
}



// **************************************************************
function getParentInfo($contentDir,$sLevel=1,$target=_AIP_GUID_FN)
// **************************************************************
// LEVEL 0 ... SERIAL   ->   3 ... ARTICLE
{
    if ($sLevel>0)
    {
        // PHP 4. COMPAT. LETZTE SLASH WEG
        if (substr($contentDir,(strlen($contentDir)-1),1)=="/")    
                $contentDir = substr($contentDir,0,(strlen($contentDir)-1));

        $parentDir = dirname($contentDir);                             // UEBERGEORDNETES VERZ.
        $curFile   = clean_path($parentDir."/"._AIP_DIR."/".$target);  // TARGET DARIN?

        if (file_exists($curFile)) return trim(file_get_contents($curFile));

        if ($sLevel==1) return "";

        $parentDir = dirname($parentDir);
        $curFile   = clean_path($parentDir."/"._AIP_DIR."/".$target);
        
        if (file_exists($curFile)) return trim(file_get_contents($curFile));
        
        if ($sLevel==2) return "";
                
        $parentDir = dirname($parentDir);
        $curFile   = clean_path($parentDir."/"._AIP_DIR."/".$target);
        
        if (file_exists($curFile)) return trim(file_get_contents($curFile)); 
    }

    return "";
}



// GET GUID FROM FIRST EXISTING PARENT RELATIVE TO 
// $contentDir (A PARENT LEVEL CAN BE EMTPY AND 
// THEREFORE THE PARENT 2 LAYERS UP!)
function getParentGuid($contentDir,$sLevel=1)
{
    return getParentInfo($contentDir,$sLevel,_AIP_GUID_FN);
}



// GET OLEF FROM FIRST EXISTING PARENT RELATIVE TO 
// $contentDir (A PARENT LEVEL CAN BE EMTPY AND 
// THEREFORE THE PARENT 2 LAYERS UP!)
function getParentOlef($contentDir,$sLevel=1)
{
    return getParentInfo($contentDir,$sLevel,_AIP_OLEF_FN);
}




?>
