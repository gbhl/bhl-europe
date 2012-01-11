<?php
// ***************************************
// ** FILE:    IMAGELIB.PHP             **
// ** PURPOSE: DIV. IMAGING TOOLS       **
// ** DATE:    07.06.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************

// *******
// INT0001
// *******
// type... 0 normal
//     ... 1 css
//     ... 2 array

function img_dim($img,$type=0)
{
  $arr_img = GetImageSize($img);
  if ($type==0)
	  return $arr_img[3]; 														// normal fuer bild
  if ($type==1)
	  return " width: ".$arr_img[0]."px; height: ".$arr_img[1]."px; ";			// fuer style (CSS)
  if ($type==2)
	  return array($arr_img[0],$arr_img[1]);									// als 2 numerische
}



// ******************************
function get_img_paths($image)
// ******************************
// RUECKGABE: ABSOLUTER FILESYSTEMPFAD, RELATIVE URL
{
    $arr_paths = array("","");

    if (!instr(substr($image,strlen($image)-6,6),"."))        // DEFAULT EXTENSION .GIF ANHAENGEN
    $image .= ".gif";


    // 1. ABSOLUTER PFAD WURDE MITGEGEBEN

    if ( (substr($image,0,1) == "/") or (substr($image,1,1) == ":") )
    {
        $arr_paths[0] = $image;
        $arr_paths[1] = _HOME.str_replace(_ABS,"",$image);
    }


    // 2. RELATIVER PFAD AUF BELIEBIGER EBENE - ABSOLUTPFADE WERDEN REKONSTRUIERT
    else
    {
        if (instr($image,"img/"))				// SYSTEMNAHE IMAGES
        {
            $arr_paths[0] = _HOME_ABS.$image;
            $arr_paths[1] = _HOME_URL.$image;
        }
        else if (instr($image,"art_"))			// USER ARTIKELBILDER
        {
            $arr_paths[0] = _USR_ART.$image;
            $arr_paths[1] = _USR_ARH.$image;
        }
        else									// USER DESIGN IMAGES
        {
            $arr_paths[0] = _USR_ABS.$image;
            $arr_paths[1] = _USR_URL.$image;
        }
    }

    return $arr_paths;
}







// *************
// *** I0001 ***
// *************
function icon($image,$alt='',$action='',$width='',$height='',$echo = true,$img_map=false,$id='')
{
 $rw = "";

 $img_paths= get_img_paths($image);

 if (file_exists($img_paths[0]))
 {
  $rw .= "<img border=0 align=absmiddle src='".$img_paths[1]."' ";

  if ($action != "")
  {
      $rw .= " style='cursor:pointer; z-index:4;' ";

      // DEFAULT EVENT ONCLICK FALLS KEIN EVENT MITGEGEBEN, ODER KEIN FUEHRENDES HOCHKOMMA
      // UND AUCH NICHT BEI STYLE ANWEISUNG ANSTATT JS
      if ( (strtolower(substr(trim($action),0,2))!="on")  and
           (substr(trim($action),0,1)!="\"") and
           (substr(trim($action),0,1)!="'") and
           (!instr($action,"style=")) )
      {
          $action = " onClick=\"".$action."\" ";
      }
  }

  $dim = img_dim($img_paths[0],2); 	// array (width,height)

  if ($width  == '')  $rw .= " width='".$dim[0]."' ";
  else				  $rw .= " width='".$width."' ";

  if ($height  == '') $rw .= " height='".$dim[1]."' ";
  else				  $rw .= " height='".$height."' ";

  if ($alt != '') 	$rw .= " alt=\"".$alt."\" title=\"".$alt."\" ";
  if ($id  != '') 	$rw .= " id=\"".$id."\" ";

  if ($img_map) 	$rw .= " usemap=\"#".$img_map."\" ";

  $rw .= " ".$action.">";

  if ($echo)	{ echo $rw; return true; }
  else			{ return $rw; }
 }

 else return false;
}





// ********************************************
function img_map($map_id,$arr_areas,$echo=true)
// ********************************************
// poly,rect,...
{
	$rw  = "\n<map id=\"".$map_id."\" name=\"".$map_id."\">\n";

	$i = 0;
	while (isset($arr_areas[$i]))
	{
		$rw .= "<area shape=\"".$arr_areas[$i]['shape']."\" coords=\"".$arr_areas[$i]['coords']."\" ";

		if ($arr_areas[$i]['href'] != "") $rw .= "href=\"".$arr_areas[$i]['href']."\" ";

		$rw .= "alt=\"".$arr_areas[$i]['alt']."\" title=\"".$arr_areas[$i]['alt']."\" ".$arr_areas[$i]['js_action']." >\n";

		$i++;
	}

	$rw .= "<area shape=\"default\" nohref>\n";
	$rw .= "</map>\n";

	if ($echo)	{ echo $rw; return true; }
	else		{ return $rw; }

}






// *************
// *** I0002 ***
// *************
function dummy($width=5,$height=5,$js_action="",$echome=true)
{
 if ($echome) icon(_SHARED_IMG."dummy.gif","",$js_action,$width,$height);
 else         return icon(_SHARED_IMG."dummy.gif","",$js_action,$width,$height,false);
}




// **********************************************************
// LIEFERT BEI ERFOLG FILENAME
// **********************************************************
function generate_thumb($input_image,$output_image,$resize = false,$resize_x = 0,$resize_y = 0,$sharpen = false, $quality = "70",$overwrite=false)
{

 $retval = false;	// 0...false alles andere ist true

 // OUTPUT FILE ERZEUGEN ODER ZUORDNEN
 if (file_exists($input_image))
 {

  // ERZEUGUNG NUR FALLS NICHT VORHANDEN ODER �BERSCHREIBEN TRUE
  if ( (!file_exists($output_image)) or ($overwrite) )
  {
   $cmd  = _IMAGEMAGICK." ";

   if ($resize)																	// resize
	{
	 $cmd .= " -size   ".$resize_x."x".$resize_y." ";
	 $cmd .= $input_image;
	 $cmd .= " -resize ".$resize_x."x".$resize_y." ";
	}

   if ($sharpen)		$cmd .= " -sharpen 1 ";									// nix oder 1
   if ( (is_numeric($quality)) and ($quality > -1) and ($quality < 101) )
					$cmd .= " -quality ".$quality." ";							// 0 -100

   $cmd .= " +profile \"*\" ";													// unnoetige img info raus

   if ($resize)		$cmd .= $output_image." ";
   else				$cmd .= $input_image." ".$output_image." ";

   system($cmd,$retval);
   if ($retval==0) $rw = true;

  } // ERZEUGUNG

  else
   if (file_exists($output_image)) $rw = true;

 } // INPUTFILE EXISTIERT


 if ($rw)
	 return substr($output_image,strrpos($output_image,"/")+1,strlen($output_image)-strrpos($output_image,"/"));


 //str_replace(_USR_ABS,"",$output_image);


 else		return false;
}



/**
	 * Animated GIF Progress (System is working) Indicator
	 *
	 * PROBLEM: Page reload causes IE to stop animation,
	 *
	 * LOESUNG: document.images[0].src = document.images[0].src; mit setTimeout() vor redirect/submit
	 *
	 * @param string $wait_text
	 * @param string $image
	 * @param string $align
	 * @param string $visibility
	 * @param string $baseStyle
	 */
function progressBar($wait_text="",$image="",$css="",$visibility="",$baseStyle=1)
{
    if ($wait_text=="")  $wait_text = "Please Wait...";
    if ($image=="")      $image = _SHARED_IMG."bigrotation2.gif";
    if ($visibility=="") $visibility = "hidden";

    echo "<div name=\"progressBar\" id=\"progressBar\" style=\"".$css." text-align: center; visibility: ".$visibility."; ";

    switch ($baseStyle)
    {
        // MITTIGE DIALOGFORM
        case 2:
            echo "border: 2px outset black; background-color: white; padding: 20px 50px 20px 50px; z-Index: 1001; align:center; position: absolute;\">";
            break;

        default:
            echo "\">";
            break;
    }

    icon($image);

    if ($wait_text!="")    {        nl(); echo $wait_text;    }

    echo "</div>";

    if ($baseStyle==2)
    {
        // if ($fullscreen==1)
        // js_command("eval(\"document.getElementById('progressBar').style.left = '\" + ((window.screen.width / 2)-(parseInt((document.getElementById('progressBar').offsetWidth))/2)) + \"px';\");");
        // else
        // js_command("eval(\"document.getElementById('progressBar').style.left = '\" + (((window.screen.width) / 2)-(parseInt((document.getElementById('progressBar').offsetWidth))/2)) + \"px';\");");
    }
}



// ************************
function close_progressBar()
{
      js_command("hideLayer('progressBar');");
}
// ************************


/** *****************************************************
		 * RETURN ABSOLUTE PATH TO MIME SYMBOL
		 *
		 * @param string $mime_file (FULL FILENAME OR FILE EXTENSION ONLY)
		 * @param int    $size (s)mall,(m)edium,(l)arge
		 * @return ABSOLUTE PATH TO MIME SYMBOL ICON
		 */
function get_mime_thumb($mime_file,$size="")
//  *****************************************************
{
    // $size "" <==> $size "m" (MEDIUM) WITHOUT POSTFIX
    if ($size=="m") $size = "";

    if (!in_array($size,array("s","l",""))) $size = "";

    if ($size!="") $size = "_".$size;

    $mime_file = trim(strtolower($mime_file));


    // FALLS FILENAME MITGEKOMMEN UND NICHT NUR EXTENSION
    $pos = strrpos($mime_file,".");

    if (($pos===false)&&(strlen($mime_file)>0))     $file_extension = $mime_file;
    else                                            $file_extension = substr($mime_file,$pos+1);

    $thumb   = _SHARED_IMG."mime/".$file_extension.$size.".gif";

    if (file_exists($thumb))     return $thumb;

    return _SHARED_IMG."mime/unknown".$size.".gif";
}


?>