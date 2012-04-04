<?php
// ********************************************
// ** FILE:    DIR_TOOLS.PHP                 **
// ** PURPOSE: GENERAL UTILITIES             **
// ** DATE:    28.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



// ***************************************************************************************
function getDirectory($path='.',$arrContents=array(),$level=0,$ignore="",$max_levels=1000)
// ***************************************************************************************
{
    // INITS
    if ($ignore=="")      $ignore = array('.', '..' );
    if ($arrContents=="") $arrContents = array();

    // TRIM LAST SLASH
    if (substr($path,(strlen($path)-1),1)=="/") $path = substr($path,0,strlen($path)-1);

    if (is_readable($path))
    {
        $dh = opendir( $path );

        while( false !== ( $file = readdir( $dh ) ) )
        {
            if( !in_array( $file, $ignore ) )       // Check that this file is not to be ignored
            {
                $cur_abs = str_replace('//','',$path."/".$file );
                        
                // 1. ** DIRECTORY **
                if (( is_dir($cur_abs ))&&($max_levels>=$level+1))
                {
                    // RECURSION
                    $arrContents[] = $cur_abs;
                    $arrContents = getDirectory($cur_abs,$arrContents,($level+1),$ignore, $max_levels);
                }
                // 2. ** FILE **
                else
                {
                    $arrContents[] = $cur_abs;
                }
            }
        }
        closedir( $dh );    
        
        return $arrContents;
    }
    
    return array();
} 


// *****************************
function get_dir_size($dir_name)
// *****************************
{
    $dir_size = 0;

    if (is_dir($dir_name)) {
        if ($dh = opendir($dir_name)) {
            while (($file = readdir($dh)) !== false) {
                if ($file != '.' && $file != '..') {
                    if (is_file($dir_name . '/' . $file)) {
                        $dir_size += filesize($dir_name . '/' . $file);
                    }
                    /* check for any new directory inside this directory */
                    if (is_dir($dir_name . '/' . $file)) {
                        $dir_size += get_dir_size($dir_name . '/' . $file);
                    }
                }
            }
        }
    }
    closedir($dh);
    
    return $dir_size;
}


// ******************
function rrmdir($dir) 
// ******************
{
   // if (instr($dir,":\"))
   if (is_dir($dir)) 
   {
     $objects = scandir($dir);
     foreach ($objects as $object) {
       if ($object != "." && $object != "..") {
         if (filetype($dir."/".$object) == "dir") rrmdir($dir."/".$object); else @unlink($dir."/".$object);
       }
     }
     reset($objects);
     @rmdir($dir);
   }
 }



// **********************************************************************
function print_dir_arr($arrDir,$arrIcons="",$dirsOnly=false,$abs_path="")
// **********************************************************************
{
    include_once(_SHARED."imagelib.php");
    
    if ($arrIcons=="")  $arrIcons = array("folder_16.png","file_16.png");

    echo "<div id=\"folderlist\" name=\"folderlist\" class=\"folderlist\">";

    $nFiles = count($arrDir);

    if ($nFiles==0) echo "Info: Directory is empty or not readable/analyzed.<br>\n";
    else
    {
     for ($i=0;$i<$nFiles;$i++)
     {
            if ((is_dir($arrDir[$i]))||(is_dir($abs_path.$arrDir[$i])))
                    icon($arrIcons[0]);
            else	if (!$dirsOnly)					
                    icon($arrIcons[1]);

            echo "&nbsp;".$arrDir[$i]."\n<br>\n";
      }
    }
    echo $nFiles." Elements.";
    echo "</div>";
}



// *************************************************
function is_dir_empty($myDir,$CountOnlyFiles=false)
// *************************************************
// RETURNS FALSE IF NO DIRECTORY OR NOT EMPTY, 
// $ONLYFILES ... CHECK IF DIR HAS FILES (IGNORES SUBDIRS)
{
    if (!is_dir($myDir)) return false;
    
    $arrContent = getDirectory($myDir,"",0,"",0);
    $nContent   = count($arrContent);
    
    if ($nContent==0) return true;
    
    $nFiles=0;
    
    for ($i=0;$i<$nContent;$i++) {
        if (!is_dir($arrContent[$i])) $nFiles++;
    }
    
    if (($nFiles==0)&&($CountOnlyFiles)) return true;

    return false;
}




?>