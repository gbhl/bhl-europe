<?php
// ***************************************
// ** FILE:    DATETIME.PHP             **
// ** PURPOSE: DIV. DATE & TIME TOOLS   **
// ** DATE:    05.01.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************


// *************
// *** T0001 ***
// *************
// GEHT NUR FUER REINES DATUM OHNE ZEIT, SONST datetime2mysql() ...
function date2mysql($date)
{
 $returnwert = "1970-01-01";
 $date = trim($date);

 if ( (strlen($date) > 5) and (strlen($date) < 11) )
 {

 // ERSTES TRENNZEICHEN IST AN STELLE 1,2,3 ODER 4
 $trennzeichen		= "";
 $trennzeichen_pos	= 0;

 for ($i=1;$i<5;$i++)
 {
  if (!is_numeric($date[$i]))
	  {
	   $trennzeichen = $date[$i];
	   $trennzeichen_pos = $i;
	   break;
	  }
 }

 $arrDate = explode($trennzeichen,$date);

 if ($trennzeichen_pos == 4)  // Y m d
 {
  $returnwert = (string) @date ("Y-m-d", mktime(0,0,0,$arrDate[1],$arrDate[2],$arrDate[0]));
 }
 else                         // d m Y
 {
   if (strlen($arrDate[2]) == 2) $arrDate[2] = "20".$arrDate[2];
   $returnwert = (string) @date("Y-m-d", mktime(0,0,0,$arrDate[1],$arrDate[0],$arrDate[2]));
 }


 } // ENDE FALLS DATES LAENGE PASST


 if ( (strlen($returnwert) < 5) or ($returnwert == "1970-01-01") )
 {
  if ( (strlen($date) > 5) and (strlen($date) < 11) )
	$returnwert = date2my($date);
  else
	$returnwert = "2000-01-01";		// FEHLERHAFTES DATUM - UNBRAUCHBAR
 }
 return $returnwert;
}





// *************
// *** T0002 ***
// *************
// yyyy-mm-dd ---> dd.mm.yyyy
function date2gui($myDate,$show_time=true)
{

 $pos1    =  strpos($myDate,"-");
 $pos2	  =  strrpos($myDate,"-");
 $pos3    =  strpos($myDate,":");

 // tag
 $d  = substr($myDate,$pos2+1,2);
 if (strlen($d)==1) $d = "0".$d;

 // monat
 $m  = substr($myDate,$pos1+1,$pos2-$pos1-1);
 if (strlen($m)==1) $m = "0".$m;

 // jahr
 $y = substr($myDate,0,$pos1);
 if ((strlen($y)==2) and (substr($y,0,1)== "0")) $y = "20".$y;
 if ((strlen($y)==2) and (substr($y,0,1)!= "0")) $y = "19".$y;
 if ( strlen($y)==1) $y = "200".$y;

 // zeit
 if( (!($pos3 === false)) and ($show_time) )
  	return $d.".".$m.".".$y." ".str_replace(' ','0',substr($myDate,$pos3-2,2)).":".str_replace(' ','0',substr($myDate,$pos3+1,2));
 else
 	return $d.".".$m.".".$y;

}




// *************
// *** T0003 ***
// *************
// dd.mm.yyyy ---> yyyy-mm-dd
function date2my($myDate)
{
 $pos1    =  strpos($myDate,".");
 $pos2	  = strrpos($myDate,".");

 // jahr
 $y  = substr($myDate,$pos2+1,4);
 if ((strlen($y)==2) and (substr($y,0,1)== "0")) $y = "20".$y;
 if ((strlen($y)==2) and (substr($y,0,1)!= "0")) $y = "19".$y;
 if ( strlen($y)==1) $y = "200".$y;

 // monat
 $m  = substr($myDate,$pos1+1,$pos2-$pos1-1);
 if (strlen($m)==1) $m = "0".$m;

 // tag
 $d = substr($myDate,0,$pos1);
 if (strlen($d)==1) $d = "0".$d;


 return $y."-".$m."-".$d;

}






//////////////////////////////////////////////////////////////////
//			Date2Time				                            //
//--------------------------------------------------------------//
// DatumString im Format	2002-04-04 23:03:00		            //
//////////////////////////////////////////////////////////////////
function Date2Time($datumstr)
{
 $Jahr   = substr($datumstr,0,4);
 $Monat  = substr($datumstr,5,2);
 $Tag    = substr($datumstr,8,2);
 $Stunde = substr($datumstr,11,2);
 $Minute = substr($datumstr,14,2);
 $Sekunde= substr($datumstr,17,2);

 print "$Stunde, $Minute, $Sekunde, $Monat, $Tag, $Jahr\n";
 $LongTime = mktime($Stunde, $Minute, $Sekunde, $Monat, $Tag, $Jahr);
 return($LongTime);
}



// *****************************************************************
// Purpose: Konvertiert beliebiges Datum oder Access Timestamp etc.
//          in sortierbares standard www Ausgabedatum
// Author:  Mehrrath Andreas
// Date:    22.02.2002
// *****************************************************************
function date2www($date)
{
 // mktime (int hour, int minute, int second, int month, int day, int year)
 $time_stamp = mktime(0,0,0,01,01,date("Y"));

 if ( (strlen($date)== 10) and (strrpos($date,'-')>0) )  // 2002-12-21
 $time_stamp = mktime(0,0,0,substr($date,5,2),substr($date,8,2),substr($date,0,4));

 if ( (strlen($date)== 14) and (!strrpos($date,'-')) )   // 20020213113402
 $time_stamp = mktime(substr($date,8,2),substr($date,10,2),substr($date,12,2),substr($date,4,2),substr($date,6,2),substr($date,0,4));

 if ( (strlen($date)== 19) and (strrpos($date,'-')>0) )	 // 2002-01-01 12:00:00
 $time_stamp = mktime(substr($date,11,2),substr($date,14,2),substr($date,17,2),substr($date,5,2),substr($date,8,2),substr($date,0,4));

 $returnwert = @ date("Y.m.d H:i:s", $time_stamp);
 return $returnwert;
}






// *************
// *** T0006 ***
// *************
function zeitintervall($feld,$intervall_id)
{
 $intervall_id = (int) $intervall_id;
 $ret = "";

 switch($intervall_id)
 {
  case 1: // today
		$ret = " and date_format(".$feld.",'%Y-%m-%d') = date_format(CURRENT_DATE,'%Y-%m-%d') ";
	  break;
  case 2: // yesterday
		$ret  = " and to_days(date_format(".$feld.",'%Y-%m-%d')) = ";
		$ret .= " ( to_days(date_format(CURRENT_DATE,'%Y-%m-%d')) - 1) ";
  	  break;
  case 3: // thisweek
        $day = (int) date("w"); // 0..sonntag 6..samstag
		$j=0;
		for ($i=0;$i<$day;$i++)
		{
		 if ($i==0) $ret .= " and ( ";
		 else       $ret .= " or ";

 		 $ret .= " to_days(date_format(".$feld.",'%Y-%m-%d')) = ";		 // !!! das noch pruefen
		 $ret .= " ( to_days(date_format(CURRENT_DATE,'%Y-%m-%d')) - ".$i.") ";
		 $j=1;
		}
        if ($j>0) $ret .= " ) ";
	  break;
  case 4: // thismonth
		$ret = " and date_format(".$feld.",'%Y%m') = date_format(CURRENT_DATE,'%Y%m') ";
	  break;
  case 5: // thisyear
		$ret = " and date_format(".$feld.",'%Y') = date_format(CURRENT_DATE,'%Y') ";
	  break;
 }

 return $ret;

}




// **************************
function datetime2date($wert)
// **************************
// DATUM MUSS VORNE STEHEN
{
 return str_replace('-','.',substr($wert,0,10));
}




// ***************************************************
function correctDate($now="",$abweichung_in_stunden=-1)
// ***************************************************
{
 $abweichung_in_stunden = (int) $abweichung_in_stunden;

 if ($now=="") $now = time();

 return ( (int) ($now+($abweichung_in_stunden*3600)));
}




/**
* @return void
* @param unknown $date
* @param unknown $without_year
* @param unknown $without_seconds
* @DESC WITHOUT SECONDS FUNKIONIERT NUR BEI ZEITSTRING MIT : TRENNER UND ZEITANGABE HINTER DEM DATUM !
22.3.2007 , 2007-03-12, 2007/2/3, 2007.2.3, 12-03-2007, 12/03/2007  --> 22.3 , 03-12 ..
*/
// *****************************************************************
function shrink_date($date,$without_year=true,$without_seconds=true)
// *****************************************************************
{
	$retVal = $date;

	// OHNE JAHR
	if ($without_year)
	{
		$arrYears = array();

		for ($i=1900;$i<2900;$i++) $arrYears[] = $i;

		$retVal = str_replace($arrYears,"#",$date);

		$retVal = str_replace(array(".#","-#","/#","#.","#-","#/"),"",$retVal);
	}

	// OHNE SEKUNDEN
	if ($without_seconds)
	{
		// nur falls sekunden vorkommen d.h. min 2 mal : vorkommt
		foreach (count_chars($retVal, 1) as $i => $val) {
			if ((chr($i)==":") and ($val>1))
			{
				$retVal =  substr($retVal,0,(strrpos($retVal,":")));
				break;
			}
		}
	}

	return $retVal;
}






// *************************************
function get_next_day($year,$month,$day)
// *************************************
{
	$next_day = mktime(0, 0, 0, $month , ($day)+1, $year);
	return array(date("Y",$next_day),date("m",$next_day),date("d",$next_day));
}





// *****************************************************************
// Purpose: Untersucht einen einzelnen String, ob er als
//			gueltiges Datumsformat interpretiert werden kann
//			Dazu wird der String mit den Delimitern " ", ".", "/","-"
//			getrennt, und die ersten drei Werte an checkdate
// 			uebergeben
// Eingabestring muss folgendermaßen formatiert sein:
//			'dd mm yyyy'
//		 	'dd/mm/yyyy'
//	   	    'dd.mm.yyyy'
//	   	    'dd-mm-yyyy'
//			optional kann dahinter ein Datum stehen:
//			'dd.mm.yyyy hh:mm:ss'	 (wird aber ignoriert)
// Rueckgabewert: False (wenn kein Datum), sonst Datum (formatiert)
// Author:  Martin Mann
// Date:    07.09.2007
// #################################################################
function str_ifdate($sDateString)
// #################################################################
{
	$bIsDate = false;
	$sDelim  = ".";

// strip whitespace and multiple spaces
	$sDateString = trim($sDateString);
	$sDateString = preg_replace('/\s{1,}/'," ",$sDateString);

// suche delimiter
	if (strpos($sDateString," ") > 0) $sDelim = " ";
	if (strpos($sDateString,".") > 0) $sDelim = ".";
	if (strpos($sDateString,"/") > 0) $sDelim = "/";
	if (strpos($sDateString,"-") > 0) $sDelim = "-";

// teile string
	$arrStrParts = array(" "," "," "," ");
	$arrStrParts = explode($sDelim, $sDateString, 4);
	$iNumParts   = count($arrStrParts);								// count elements

// abort if no valid date
	if ($iNumParts < 3) 											// date only valid if at least 3 elements
	{
		$sDateString = false;
		return $sDateString;
	}
	else
	{

// check year and time
		if (strpos(trim($arrStrParts[2])," ") > 1)
		{
			$sYearTime = explode(" ", trim($arrStrParts[2]), 2);
			$arrStrParts[2] = $sYearTime[0];
			$arrStrParts[3] = $sYearTime[1];
			$iNumParts = 4;
		}

// check date (Achtung: mm-dd-yyyy)

 		for ($k=0;$k<=2;$k++)
 		{
 			$arrStrParts[$k] = intval(trim($arrStrParts[$k]));
 			if (!is_numeric($arrStrParts[$k]))
 			{
 				$sDateString = false;
				return $sDateString;
 			}
 		}

		$bIsDate = checkdate($arrStrParts[1],$arrStrParts[0],$arrStrParts[2]);

// Final reformat: datestring mit "."
		$sDateString = false;
		if ($bIsDate)
		{
			if ($iNumParts == 4)
				$sDateString = trim($arrStrParts[0].".".$arrStrParts[1].".".$arrStrParts[2]." ".trim($arrStrParts[3]));
			else
				$sDateString = trim($arrStrParts[0].".".$arrStrParts[1].".".$arrStrParts[2]);
		}
	}

// Debug
//		echo "DEBUG: sDelim: ".$sDelim."<br>";
//  	echo "DEBUG: Tag, Mon, Jahr, Rest: '".$arrStrParts[0]."' '".$arrStrParts[1]."' '".$arrStrParts[2]."' '".$arrStrParts[3]."'<br>";
//  	echo "DEBUG: sDateString: ".$sDateString."<br>";
//		echo "DEBUG: bIsDate: ".$bIsDate."<br>";


// Return

	if (!$bIsDate) $sDateString = false;  // mehrrath a.
 	return $sDateString;
}








// *******************************
// *** T000? ***
// *******************************
function datetime2mysql($datetime)
{
 // DATUM MUSS VON ZEIT MIT BLENK GETRENNT SEIN UND VOR DER ZEIT STEHEN
 $datetime = trim($datetime);
 $pos      = strpos($datetime," ");

 if ($pos === false) $rw = date2mysql($datetime); // EVTL. NUR DATUM VORH.
 else
 {
 	$date = date2mysql(substr($datetime,0,$pos));
 	$time = str_replace(" ","",substr($datetime,$pos));
 	$rw = $date." ".$time;
 }
 return $rw;
}




/**
 * Check whether a date is later than the other
 *
 * @param string $check_date             Format: DD.MM.YYYY
 * @param string $conditional_date       Format: DD.MM.YYYY
 */
function is_later($check_date,$conditional_date)
{
    $check_date       = mktime(12,00,00,substr($check_date,3,2),substr($check_date,0,2),substr($check_date,6,4));
    $conditional_date = mktime(12,00,00,substr($conditional_date,3,2),substr($conditional_date,0,2),substr($conditional_date,6,4));

    if ($check_date<=$conditional_date) return false;
    else                                return true;
}




?>