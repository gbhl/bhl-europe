<?php
// ********************************************
// ** FILE:    DIR_TOOLS.PHP                 **
// ** PURPOSE: GENERAL UTILITIES             **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



// ******************************************************************************************
function getDirectory( $path = '.', $arrContents=array(),$level = 0, 
        $ignore = array('.', '..' ), $max_levels=1000)
// ******************************************************************************************
{
	// INITS
	if ($ignore=="")	  $ignore = array('.', '..' );
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


?>