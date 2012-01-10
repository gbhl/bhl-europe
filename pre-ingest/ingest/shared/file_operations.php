<?php
// ******************************************
// ** FILE:    FILE_OPERATIONS.PHP         **
// ** PURPOSE: SHARED LIBARY               **
// ** DATE:    13.04.2011                  **
// ** AUTHOR:  ANDREAS MEHRRATH            **
// ******************************************
/*
INFOS:   file_delete funktion war aus tools !!!
         funktion delete_file sollte nicht mehr benutzt werden wird mit file_delete ersetzt !!!
         auch schauen ob dieses file ueberall included ist fuer die fkt.aufrufe file_delete()
*/


// **********************************************
// *** DELETE_FILES() = ALIAS FOR FILE_DELETE ***
// **********************************************
function delete_file($filename)
{
 return file_delete($filename);
}



// ****************************************
// *** DATEI / DIRECTORY LOESCHEN FKT.  ***
// ****************************************
function file_delete($filename="",$secure=true)
{
    $retVal = true;

    if (is_dir($filename))
    {
        if (!$secure)  @rmdir($filename);
        else
        {
            // DIESE VERZEICHNISSE DUERFEN NICHT GELOESCHT WERDEN (USERDATA Z.B. SCHON LIEGT DIREKT IN HTDOCS)
            $arrNotAllowed2del = array("include","inc","img","app","shared","css","js","mail");
            $nNotAllowed       = count($arrNotAllowed2del);

            for ($i=0;$i<$nNotAllowed;$i++) {
                if ( instr( strtolower($filename), $arrNotAllowed2del[$i]) ) $retVal = false;
            }

            if ($retVal)		$retVal = @rmdir($filename);

            return $retVal;
        }
    }
    else
    {
        $filename = str_replace(" ","",$filename);
        $filename = str_replace(chr(92),"/",$filename);

        // NUR FILES IM USERDATA KOENNEN PER INTERFACES GELOESCHT WERDEN
        if ( (!(instr($filename,"userdata"))) or (strlen($filename)<15) or (!(is_file($filename)))  ) return false;

        // *** FORBIDDEN EXTENSIONS - NO DELETE ***
        $arrNotAllowed2del = array("php3","php4","php5","php6","php","js","css","htm","html","sh","inc","map","fnt","tpl");
        $nNotAllowed       = count($arrNotAllowed2del);

        for ($i=0;$i<$nNotAllowed;$i++) {
            if ( instr( strtolower(strstr($filename,".")), $arrNotAllowed2del[$i]) ) $retVal = false;
        }

        if ($retVal)		$retVal = @unlink($filename);

        return $retVal;
    }
}



// ***************************
// *** FILE AGE IN SECONDS ***
// ***************************
function file_age($myFile)
{

	if (file_exists($myFile))	return (time() - filemtime($myFile));
	else						return false;

}




// ***********************************************************************
// GIBT ZEILE (OHNE SUCHWERT) ZURUECK IN DER DER SUCHWERT GEFUNDEN WURDE
// TABULATOR GETRENNTE SPALTEN IM FILE SIND VORAUSSETZUNG !
// DEPRECATED DUE: FILE_GET_CONTENTS()
// *******************************************************************
function get_file_value($file_name,$value,$retCol=0,$ignoreCase=false)
// *******************************************************************
{
	$retVal = "";
	if ($retCol == "") $retCol = 0;

	if (file_exists($file_name))
	{
		if ($fp = fopen($file_name,"r"))
		{
			while (!feof($fp))
			{
				$line = trim(fgets($fp));

				// VALUE GEFUNDEN / KOMMENTARE AUSLASSEN
				if ( (substr($line,0,1) != "#")&&(instr($line,$value,$ignoreCase)) )
				{
					$arrVal = explode("\t",$line);
					$retVal = $arrVal[$retCol];
					break;
				}
			}
			@fclose($fp);
		}
	}
	return $retVal;
}




/**
 * @return boolean
 * @param string  $filename
 * @param string  $content
 * @param integer $mode
 * @desc Schreibt Datei $filename mit $content. Ueberschreibt etwaige vorh. Datei !
*/
// *************************************************
function write_file($filename,$content="",$mode="w")
// *************************************************
{
	if ($fp = fopen($filename,$mode))
	{
		fputs($fp,$content);
		@fclose($fp);
		return true;
	}
	else
		return false;
}




/**
 * @return string / false
 * @param string $filename
 * @desc Liest ein ganzes File ein und gibt den Inhalt als 1 String zurueck. 
 * Nur fuer kleine txt Files verwenden.

   !!! deprecated: new: file_get_contents();
*/
// **************************
function read_file($filename)
// **************************
{
	$content = false;

	if ($fp = fopen($filename,"r"))
	{
		while (!feof($fp))
		{
			$content .= fgets($fp);
		}
		@fclose($fp);
	}

	return $content;
}




/**
 * HOLT INFORMATIONEN AUS TEXT FILES, WOBEI LEERZEILEN ODER ZEILEN MIT AUSSCHLIESSLICH
 * SPEZIELLEN ZEICHEN AUSGELASSEN WERDEN.
 *
 * @param   string  $fileName
 * @param   array   $arrLinesToRemove
 * @return  array   FILTERED LINES OF FILE
 *
 * 2ND PARAMETER DEFINES LINES WHICH CONTAIN THESE CHARACTERS ONLY(!) WILL BE EXCLUDED
 */
function file_get_content_filtered($fileName,$arrLinesToRemove="",$commentLinePrefix="")
// *************************************************************************************
{
    $rVal = array();

    if (!is_array($arrLinesToRemove))
    $arrLinesToRemove = array(" ","#","*","-","_","=",".",":","~");

    $fp = @fopen($fileName,"r");

    if (!$fp) return false;
    else
    {
        while (!feof($fp))
        {
            $line = trim(trim(fgets($fp)),"\x00..\x1F");

            $probeLine = trim(str_replace($arrLinesToRemove,"",$line));

            if ($probeLine != "")
            {
                if (($commentLinePrefix=="")||(substr($line,0,strlen($commentLinePrefix))!=$commentLinePrefix))
                $rVal[] = $line;
            }
        }
        fclose($fp);

        return $rVal;
    }
    
    return false;
}



// ***************************************
function file_line_exists($myFile,$myLine)
// ***************************************
{
    if (is_readable($myFile)) 
    {
        $fContent = file_get_contents($myFile);
        if (strpos($fContent,$myLine)===false)  return false;
        
        return true;
    }
    
    return false;
}



?>