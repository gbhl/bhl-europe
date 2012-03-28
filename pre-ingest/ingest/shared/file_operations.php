<?php
// ******************************************
// ** FILE:    FILE_OPERATIONS.PHP         **
// ** PURPOSE: SHARED LIBARY               **
// ** DATE:    13.03.2012                  **
// ** AUTHOR:  ANDREAS MEHRRATH            **
// ******************************************
/*
INFOS:   FUNKTION DELETE_FILE SOLLTE NICHT MEHR BENUTZT WERDEN WIRD MIT FILE_DELETE ERSETZT
         AUCH SCHAUEN OB DIESES FILE UEBERALL INCLUDED IST FUER DIE FKT.AUFRUFE FILE_DELETE()
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
                $fp = @fopen($file_name,"r");
                
		if ($fp)
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
		}
                
                @fclose($fp);
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
        $fp = @fopen($filename,$mode);
                
	if ($fp)
	{
		fputs($fp,$content);
                
		@fclose($fp);
                
		return true;
	}
	
        @fclose($fp);
		
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

        $fp = @fopen($filename,"r");
        
	if ($fp)
	{
		while (!feof($fp))
		{
			$content .= fgets($fp);
		}
	}
        
        @fclose($fp);

	return $content;
}




/**
 * HOLT INFORMATIONEN AUS TEXT FILES, WOBEI LEERZEILEN ODER ZEILEN MIT AUSSCHLIESSLICH
 * SPEZIELLEN ZEICHEN AUSGELASSEN WERDEN.
 *
 * @param   string  $fileName
 * @param   array   $arrLinesToRemove
 * @param   string  $commentLinePrefix
 * @param   boolean $ignoreCase
 * @param   boolean $inverted
 * @return  array   FILTERED LINES OF FILE
 *
 * 2ND PARAMETER DEFINES LINES WHICH CONTAIN THESE CHARACTERS ONLY(!) WILL BE EXCLUDED
 * 
 * IF INVERTED = TRUE LINES TO IGNORE BECOME LINES TO RETURN
 */
function file_get_content_filtered($fileName,$arrLinesToRemove="",$commentLinePrefix="",
        $ignoreCase=false,$inverted=false)
// *************************************************************************************
{
    $rVal = array();

    if (!is_array($arrLinesToRemove)) $arrLinesToRemove = array(" ","#","*","-","_","=",".",":","~");

    $fp = @fopen($fileName,"r");

    if (!$fp) { 
        @fclose($fp); 
        return false;
    }
    else
    {
        while (!feof($fp))
        {
            $line = trim(trim(fgets($fp)),"\x00..\x1F");
            
            // 1. $arrLinesToRemove WEG (DIE NUR AUS ZU ENTFERNENDEN ZEICHEN BESTEHEN)
            if (!$inverted)
            {
                if ($ignoreCase) $probeLine = trim(str_ireplace($arrLinesToRemove,"",$line)); 
                else             $probeLine = trim( str_replace($arrLinesToRemove,"",$line));

                if ($probeLine != "")
                {
                    // KEINE KOMMENTARZEILEN
                    if (($commentLinePrefix=="")||(substr(trim($line),0,strlen($commentLinePrefix))!=$commentLinePrefix))
                    $rVal[] = $line;
                }
            }
            // 2. ZEILEN DIE EINEN EINTRAG VON $arrLinesToRemove BEINHALTEN RUECKGEBEN
            else {
                    // KEINE KOMMENTARZEILEN
                    if (($commentLinePrefix=="")||(substr(trim($line),0,strlen($commentLinePrefix))!=$commentLinePrefix))
                    {
                        if ($ignoreCase)    $probeLine = trim(str_ireplace($arrLinesToRemove,"",$line)); 
                        else                $probeLine = trim( str_replace($arrLinesToRemove,"",$line));

                        // EIN EINTRAG WURDE MIN. ERSETZT
                        if ($line!=$probeLine)  $rVal[] = $line;
                    }
                }
        }
        @fclose($fp);

        return $rVal;
    }
    
    @fclose($fp);
    
    return false;
}



/**
 * CHECKT FILE AUF VORHANDENSEIN DIVERSER INHALTE (ARRAY)
 *
 * @param   string    $myFile
 * @param   array     $myContent
 * @param   boolean   $ignoreCase
 * @param   boolean   $oneMatchOnly
 * 
 * @return  boolean true|false
 *
 */
function file_content_exists($myFile,$myContent,$ignoreCase=false,$oneMatchOnly=false,$maxlen=null)
// ************************************************************************************************
{
    if (!is_readable($myFile)) return false;
    
    // DEFINED IN THE PHP SOURCE CODE AS PHP_STREAM_COPY_ALL, AND BECAUSE THAT IS NOT 
    // AVAILABLE TO US MERE MORTAL USERS, THAT CONSTANT IS DEFINED AS  ((SIZE_T)-1) OR -1 

    if (($maxlen!=null)&&(is_numeric($maxlen)))
        return instr(file_get_contents($myFile, false, null, -1, $maxlen),
        $myContent,$ignoreCase,$oneMatchOnly);
    else
        return instr(file_get_contents($myFile, false, null, -1),
        $myContent,$ignoreCase,$oneMatchOnly);
}



// ************************************
function file_get_extension($file_name)
// ************************************
// LIEFERT EXTENSION MIT PUNKT
{
     $pos1 = strrpos($file_name,".");
     
     if ($pos1!==false) return trim(substr($file_name,$pos1));
     
     return false;
}



// ***************************************
function file_remove_extension($file_name)
// ***************************************
// ENTFERNT (NUR LETZTE) EXTENSION
{
     $pos1 = strrpos($file_name,".");
     
     if ($pos1!==false) $file_name = substr($file_name,0,$pos1);    // LETZTE EXTENSION WEG
     
     return $file_name;
}


?>