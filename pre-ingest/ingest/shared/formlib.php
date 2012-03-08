<?php
// ***************************************
// ** FILE:    FORMLIB.PHP              **
// ** PURPOSE: FORMULARELEMENTE         **
// ** DATE:    25.12.2009               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************



// ***************************
function cleanup_tag($part="")
// ***************************
{
    return str_replace(array(" ","(",")",".",",",";",".","\\","/","#","!","&","|","<",">"),"",$part);
}



// *************
// *** F0008 ***
// *************
function button($value='',$js_action='',$width=-1,$height=-1,$textalign='center',$id='',$echome=true,$tabindex=0)
{
    $retVal = "";

    if ($width == "")   $width  = -1;
    if ($height== "")   $height = -1;

    if ($textalign=="") $textalign = "center";

    $retVal = "<input value='".$value."' name=\"button\" class=\"schalter\" ";

    if ($id!="")        $retVal .= " id=\"".$id."\" ";

    if ($textalign!="") $retVal .= "style=\"text-align: ".$textalign."; ";

    if (($width != -1) and($width  != "")) { $retVal .=  "width: ".$width."px; ";    }
    if (($height != -1)and($height != "")) { $retVal .=  "height: ".$height."px;  "; }

    $retVal .=  "\" ";  // -ende- of style

    if ($tabindex!=0) $retVal .= " tabindex='".$tabindex."' ";

    // if (eregi("^(submit|reset)", $js_action))
    if (preg_match("/^(submit|reset)/i", $js_action))
    	$retVal .= " type=\"".$js_action."\">";
    else
    	$retVal .= " type=\"button\" onClick=\"javascript: ".$js_action."\">";

    if ($echome) echo   $retVal;
    else         return $retVal;
}



// *************
// *** F0007 ***
// *************
function ibutton($image,$value='',$js_action='',$width=-1,$height=-1,$render=1,$textcolor='#aaaaaa',$echome=true)
{
    if ($width == "") $width=-1;
    if ($height== "") $height=-1;

    $img_paths= get_img_paths($image);

    $retVal = "<input value='".$value."' name='button' style=\"";

    $retVal .= "color: ".$textcolor."; font: 1px; line-height: 0px; background-image: url('".$img_paths[1]."'); ";

    $retVal .= "background-position: 0px 0px; background-repeat: no-repeat; ";

    if (($width != -1) and($width  != ""))	{ $retVal .= "width: ".$width."px; "; }
    if (($height != -1)and($height != "")) { $retVal .= "height: ".$height."px;  "; }

    if ($render==1)
    {
        $arrDim = img_dim($img_paths[0],2);
        $retVal .= " width: ".($arrDim[0]+7)."px; height: ".($arrDim[1]+7)."px; ";
    }

    $retVal .= "\" ";  // -ende- of style
    
    // if (eregi("^(submit|reset)", $js_action))
    if (preg_match("/^(submit|reset)/i", $js_action))
    	$retVal .= " type=\"".$js_action."\">";
    else
    	$retVal .= " type=\"button\" onClick=\"javascript: ".$js_action."\">";

    if ($echome) echo   $retVal;
    else         return $retVal;
}









// ************************************************************************
function imgButton($image,$value='',$js_action='',$echome=true, $naming='')
// ************************************************************************
{

    $img_paths= get_img_paths($image);

    $retVal = "<input type='image' border='0' src='".$img_paths[1]."' ".img_dim($img_paths[0]);

    if ($naming!="") $retVal .= " name='imgButton_".cleanup_tag($value)."' ";
    else             $retVal .= " name='button' ";

    if (strlen($js_action)>1)
    $retVal .= " onClick=\"javascript: ".$js_action." return false;\" ";

    $retVal .= " class='imgButton' >";

    if ($echome) echo   $retVal;
    else         return $retVal;
}





// *************
// *** F0005 ***
// *************
function textfeld($variable,$wert='',$size=25,$maxlen=30,$title='',$js_action='',$password=false,$echome=true,$tabindex=0)
{
    $retVal = "";

    if ($size=="") $size=25;

    if ($title != '')
    {
        $retVal .= "<font class=eingabetitel>".$title."</font>";

        if ( (!instr(strtolower($title),"<br>"))  and (!instr(strtolower($title),"<td")) )
        $retVal .= "&nbsp;";
    }

    $retVal .= "<input class=eingabefeld type='";

    if ($password) $retVal .= "password";
    else		   $retVal .= "text";

    $retVal .= "' ";

    if ($tabindex!=0) $retVal .= " tabindex='".$tabindex."' ";

    $retVal .= " name=".$variable." value=\"".$wert."\" size='".$size."' maxlength='".$maxlen."' ".$js_action." >";

    if ($echome) echo   $retVal;
    else         return $retVal;
}




// *************
// *** F0006 ***
// *************
function textarea($variable,$wert='',$cols=80,$rows=10,$title='',$js_action='',$echome=true,$style="",
$readonly=false,$tabindex=0)
{
    $retVal = "";

    if ($cols=="") $cols=80;
    if ($rows=="") $rows=2;

    if ($title != '')	$retVal .= "<font class=eingabetitel>".$title."</font>";

    if ( (!instr(strtolower($title),"<br>"))  and (!instr(strtolower($title),"<td")) )
    $retVal .= "&nbsp;";

    if ( ($style != "") and (trim(strtolower(substr($style,0,5)))!="style") ) $style = "style=\"".$style."\"";

    $retVal .= "<textarea name=\"".$variable."\" cols=\"".$cols."\" rows=\"".$rows."\" ";

    if ($readonly) $retVal .= " readonly ";

    if ($tabindex!=0) $retVal .= " tabindex='".$tabindex."' ";

    $retVal .= " class=\"eingabefeld\" ".$style." wrap=\"soft\" ".$js_action.">";

    $retVal .= $wert."</textarea>\n";

    if ($echome)	echo   $retVal;
    else			return $retVal;
}









// *************
// *** F0004 ***
// *************
function checkbox($variable, $checked=0, $title='', $js_action = '',$text_position='before',
        $tabindex=0,$echome=true,$value=1)
{
    $retVal = "";

    if ($checked=="")       $checked=0;
    if ($text_position=="") $text_position = 'before';

    if ($checked == 1)  $checked = "checked";
    else                $checked = "";

    if (($title != '')&&($text_position=='before'))  
        $retVal .= "<font class='eingabetitel'>".$title."</font> ";

    $retVal .= "<input type=checkbox name='".$variable."' value='".$value."' ".$checked." ".$js_action;

    if ($tabindex!=0) $retVal .= " tabindex='".$tabindex."' ";

    $retVal .= ">\n";

    if (($title != '')&&($text_position=='after'))   
        $retVal .= "<font class='eingabetitel'>".$title."</font> ";

    if ($echome) echo $retVal;
    else         return $retVal;
}







// *************
// *** F0001 ***
// *************
function form($name='',$action='',$method='POST',$js_action='',$echome=true,$style="")
{
    $retVal = "";
    
    if ($action=='') $action = _SYSTEM;
    if ($method=='') $method = 'POST';

    $retVal .= "<form id='".$name."' name='".$name."' action='".$action."' method='".$method."' ";

    if ($js_action != '')	$retVal .= "onSubmit=\"javascript: ".$js_action."\"";

    if ($style != '')		$retVal .= " ".$style." ";  // AUCH CLASS ANGABE MOEGLICH

    $retVal .= ">";

    if ($echome)	echo   $retVal;
    else			return $retVal;
}




// *******************************
function close_form($echome=true)
// *******************************
{
    $retVal = "";

    $retVal .=  "</form>";

    if ($echome)	echo   $retVal;
    else			return $retVal;
}




// *************
// *** F0002 ***
// *************
function hidden ($variable,$wert="",$echome=true)
{
    $retVal = "";

    $retVal .= "\n<input type='hidden' name='".$variable."' value='".$wert."' >\n";

    if ($echome)	echo   $retVal;
    else			return $retVal;
}






// *************************************************************
function schaltflaeche($title,$action='',$width='',$echome=true)
// *************************************************************
{
    $retVal = "";

    // INTERNET EXPLORER
    if (is_ie())
    {
        $retVal .= "<table border=0 cellspacing=0 cellpadding=0 ";

        if (is_numeric($width))
        $retVal .= " width='".$width."' ";

        $retVal .= "><tr><td onClick=\"".$action."\" class=schaltflaeche  style='cursor:pointer;'> ";
        $retVal .= $title._TAB;
    }
    // NETSCAPE / MOZILLA ...
    else
    {
        $retVal .= "<a href='#' onClick=\"".$action."\">";
        $retVal .= "<layer id=\"noname\" name=\"noname\" class=schaltflaeche ";

        if (is_numeric($width))  $retVal .= " width=\"".$width."\" ";

        $retVal .= ">".$title."</layer></a><br>";
    }

    if ($echome)	echo   $retVal;
    else			return $retVal;
}




// *********************************************
// *** RADIO ELMENT GROUP                    ***
// *********************************************
// *** DATA_SRC KANN TABELLE ODER ARRAY SEIN ***
// *********************************************
function radio($src_tab,$variable='',$wert="",$title="",$js="",
               $text_col=1,$db = _DB_MAIN,$layout=1,$echome=true)
{
    $retVal = "";

    if (is_array($src_tab)) $mysql_radio = false;
    else 					$mysql_radio = true;


    // *** MYSQL QUELLE ***
    if ($mysql_radio)
    {
        if ($db=='') 		$db = _DB_MAIN;
        if ($text_col=='') 	$text_col = 1;

        $col1 = col2name($db.".".$src_tab,0);
        $col2 = col2name($db.".".$src_tab,$text_col);

        $query  = "select ".$col1.", ".$col2." from ".$src_tab;

        if (!instr($src_tab,"order by"))	$query .= " order by ".$col1;

        $result = mysql_select($query,$db);
    }


    // *** LAYOUT TABELLE ***
    if ($layout>10)			$retVal .=  "<table border=0 class='radiogroup'><tr><td>";

    if ($title != "")		$retVal .=  "<font class=eingabetitel>".$title."</font>";

    $str1 = " ".$js."><font class=standard ";

    if ($layout==1)			$str1 .= " style='margin-left: -5px;' ";		// horiz. abst. zw. radio und text verkl.

    $str1 .= ">";


    // *** MYSQL QUELLE ***
    if ($mysql_radio)
    {
        while ($line = mysql_fetch_array($result))
        {
            $retVal .=  "<input type=radio name='".$variable."' value='".$line[0]."' ";

            if ($line[0] == $wert) $retVal .=  " checked ";

            $retVal .=  $str1.$line[1]."</font>";

            if ($layout==1) $retVal .= "&nbsp;&nbsp;";     		     // layout nebeneinander

            if ( ($layout==2) or ($layout>10) ) $retVal .= "<br>";   // layout untereinander bzw. tabelle
        }
    }


    // *** ARRAY QUELLE ***
    // Start mit index 0 ABER VALUE 1 !
    else
    {
        $nrows = count($src_tab);

        for($i=0;$i<$nrows;$i++)
        {
            $retVal .=  "<input type=radio name='".$variable."' value='".($i+1)."' ";

            if ((($i+1) == $wert) or ($src_tab[$i]==$wert)) $retVal .=  " checked ";

            $retVal .=  $str1.$src_tab[$i]."</font>";

            if ($layout==1) $retVal .= "&nbsp;&nbsp;";	   		   // layout nebeneinander

            if ( ($layout==2) or ($layout>10) ) $retVal .= "<br>"; // layout untereinander bzw. tabelle
        }
    }

    // *** LAYOUT TABELLE ***
    if ($layout>10)	$retVal .=  _TAB;

    if ($echome)	echo   $retVal;
    else			return $retVal;
}




/**
 * PRINT AN DROPDOWN ACCORDING TO AN ARRAY OF VALUES OR AN GIVEN RANGE OF VALUES (E.G. 1-1000)
 *
 * @param unknown_type $myArr
 * @param unknown_type $feldname
 * @param unknown_type $wert
 * @param unknown_type $size
 * @param unknown_type $title
 * @param unknown_type $js
 *
 * TO SET NON NUMVERIC STANDARD VALUES (0...N) DEFINE THE ARRAY KEYS (E.G. "100"=>"HUNDRED")
 */
function arr_dropdown($myArr,$feldname="",$wert=1,$size=1,$title="",$js="",$noKeys=false)
// ************************************************************************
{
    // *********************************************************************
    // *** KEIN ARRAY SONDERN WERTEBEREICH (z.B. 1-12, 1999-2010) MITGEGEBEN
    // *** NUMERISCHER 1DIM ARRAY WIRD ERZEUGT
    // *********************************************************************
    if (!is_array($myArr))
    {
        $von   = (int) substr($myArr,0,strpos($myArr,"-")+1);
        $bis   = (int) substr($myArr,strpos($myArr,"-")+1,9);	// bis: max. 9stellige zahl

        unset($myArr);
        $myArr = array();

        // ARRAY MIT INDEX = VALUE AUFBAUEN
        for ($i=$von;$i<=$bis;$i++) $myArr[$i] = $i;
    }

    reset($myArr);

    // **************************
    // *** ARRAYS VORBEREITEN ***
    // **************************
    $nElements = count($myArr);
    $arrKeys   = array_keys($myArr);

    // ***********************
    // *** SELECT AUFBAUEN ***
    // ***********************
    if ($nElements>0)
    {
        if ($title != "") echo "\n\n<font class=eingabetitel>".$title."</font>";

        echo "\n\n<select class='eingabefeld' name='".$feldname."' ";

        if (is_numeric($size))  echo " size='".$size."' ";

        if ($js!="")            echo " ".$js." ";

        echo ">\n";

        for ($i=0;$i<$nElements;$i++)
        {
            if (!$noKeys) 
            {
                echo "<option value='".$arrKeys[$i]."' ";
                if ($arrKeys[$i] == $wert)	echo " selected "; 
                echo ">".$myArr[($arrKeys[$i])]."</option>\n";
            }
            else    // KEINE KEYS, VALUE = INHALT
            {
                echo "<option value='".$myArr[$i]."' ";
                if ($myArr[$i] == $wert)	echo " selected "; 
                echo ">".$myArr[$i]."</option>\n";                
            }
        }
        echo "</select>\n";
    }
}





// ***********************************************************
function draw_prepared_dd($prepared_dd,$var,$val,$echome=true)
// ***********************************************************
// VALUE MUSS INNERHALB VON " STEHEN - FKT. ABHAENGIG VON DROP_DOWN FKT.
// VORHER MUSS DD PREPARED WERDEN! Z.B.:
// $prepared_dd = ora_drop_down("INV","AB_LDAP_STATUS","ID","BEZEICHNUNG","xxxxxx","yyyyyy",120,"","","","STYLE",false);
{
    // 1. SELECT VALUE UND VAR.NAME RICHTIGSTELLEN
    $drop_down = str_replace("xxxxxx",$var,$prepared_dd);
    $drop_down = str_replace("yyyyyy",$val,$drop_down);

    // 2. SELECTION DURCHFUEHREN
    $drop_down = str_replace("\"".$val."\"","\"".$val."\" selected",$drop_down);

    // 3. AUSGABE
    if ($echome) echo   $drop_down;
    else 		 return $drop_down;
}




/**
 * WRITE HINT WITH COMMENT FONT CLASS
 *
 * @param unknown_type $hint_text
 * @param unknown_type $echome
 * @return unknown
 */
function hint($hint_text="",$echome=true,$italic=false,$margin_top=0)
{
    $rVal = "<font class='comment'>".$hint_text."</font>";

    if ($italic) $rVal = "<i>".$rVal."</i>";

    if ($margin_top<>0)
    $rVal = "<div style=\"position: absolute; margin-top: ".$margin_top."px;\">".$rVal."</div>";

    if ($echome) echo   $rVal."\n";
    else         return $rVal;
}



?>