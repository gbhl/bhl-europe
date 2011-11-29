<?php
// ***************************************
// ** FILE:    MYSQL.PHP                **
// ** PURPOSE: MYSQL ABSTRACTION LAYER  **
// ** DATE:    10.05.2011               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************


if (!defined("_DB_DEBUG"))       define("_DB_DEBUG",false);
if (!defined("_DB_ERROR_QUERY")) define("_DB_ERROR_QUERY","DB QUERY ERROR");
if (!defined("_DB_PREFIX"))      define("_DB_PREFIX","");


// hints: mysql_fetch_array resultType: MYSQL_ASSOC, MYSQL_NUM und MYSQL_BOTH


// **********************
// MYSQL UNIVERSALABFRAGE
// **********************
function mysql_select($query,$db=_DB_MAIN)
{
 if ($db=="") $db = _DB_MAIN;

 // 2. INT NAME VERVOLLSTAENDIGEN BEI USER DATENBANK SELECT (FALLS NOETIG)
 if (($db != _DB_MAIN)&&(!instr($db,"main_db"))&&(!instr($db,_INT_NAME."_"))&&($db!=""))
	$db = _INT_NAME."_".$db;

 // 1. DB PREFIX VERVOLLSTAENDIGUNG
 if ((_DB_PREFIX != "")&&(!instr($db,_DB_PREFIX))&&($db!=""))
 	$db = _DB_PREFIX.$db;

 return mysql_sel($query,$db);
}






// *****************
// MYSQL KERNABFRAGE
// *****************
function mysql_sel($query,$db="")
{
    if (_DB_DEBUG)
    {
        $link = @mysql_connect(_DB_SERVER,_DB_USER,_DB_PWD) or
        die (_DB_ERROR_CONNECT." ErrorNo:".mysql_errno()."  Error:".mysql_error());

		if ($db!="") { mysql_select_db ($db) or die (_DB_ERROR_SELECTDB." Database: ".$db); }

        $result = @mysql_query ($query);

        if (mysql_errno())
        echo "<br>\n"._DB_ERROR_QUERY." ErrorNo:".mysql_errno()." Error:".mysql_error()."<br>\nQUERY: ".$query;
    }
    else
    {
        $link = @mysql_connect(_DB_SERVER,_DB_USER,_DB_PWD) or die (_DB_ERROR_CONNECT);

        if ($db!="") { mysql_select_db ($db) or die (_DB_ERROR_SELECTDB); }

        $result = mysql_query ($query) or die (_DB_ERROR_QUERY);
    }

    $nrows_updated = mysql_affected_rows();
    mysql_close($link);

    if (preg_match("/^(insert|update|alter|delete|create|drop|reload|shutdown|grant|index)/i", $query))
    return $nrows_updated;
    else
    return $result;
}



// *******************
// MYSQL EINZELABFRAGE
// *******************
function abfrage($query,$db=_DB_MAIN,$key=0)
{

 if (preg_match("/^(insert|update|alter|delete|create|drop|reload|shutdown|grant|index)/i", $query))
  return false;
 else
 {
  $wert   = '';
  $erfolg = '';

  $result = mysql_select ($query,$db);
  $erfolg = (int) @mysql_num_rows($result);

  if (strlen($erfolg)>0)
  {
   $line = mysql_fetch_row($result);
   $wert = $line[$key];
  }

  return $wert;
 }
}




// *********************************************
// GET SINGLE ROW INTO ARRAY OR SEPARATED STRING
// *********************************************
function mysql_get_line($query,$db=_DB_MAIN,$trenner="")
{
 if ($db == "") $db = _DB_MAIN;
 
 $result = mysql_select($query,$db);
 $erfolg = (int) @mysql_num_rows($result);
 
  if (strlen($erfolg)>0)
  { 
     if ($trenner=="")  return mysql_fetch_assoc($result);
     else               return implode($trenner,mysql_fetch_row($result)); 
  }

  return false;
}



// **********************************************************
// *** 1 DIM ARRAY ODER KOMMALISTE AUS SELECT GENERIEREN  ***
// **********************************************************
// ehem: build_array
function mysql_get_array($query,$db=_DB_MAIN,$emptyCols=true,$trenner="")
{
 if ($db == "") $db = _DB_MAIN;

 $arr_ret = array();

 $result = mysql_select($query,$db);
 $line   = mysql_fetch_row($result);

 while ($line)
 {
	if ((trim($line[0]) != "")||($emptyCols)) $arr_ret[] = $line[0];
	$line = mysql_fetch_row($result);
 }

 if ($trenner=="")      return $arr_ret;
 else                   return implode($trenner,$arr_ret);
}




// ********************************************
function is_numeric_col($tab,$col,$db=_DB_MAIN)
// ********************************************
{
 $typ = mysql_field_type(mysql_select("select ".$col." from ".$tab." limit 1",$db),0);

 if (preg_match("/^(int|float|dec)/i", $typ)) return true;
 else return false;
}






// **********************
// SPALTENNAMEN ERRUIEREN
// **********************
function col2name($tab,$colnr)
{
 $zw_res = mysql_select("select * from ".$tab." limit 1");
 $name = mysql_field_name ($zw_res, $colnr);
 mysql_free_result($zw_res);
 return $name;
}






// **********************
function usergroup($user)
// **********************
/*		z.B.
		1...... Standardbenutzer	/  Customer
		9...... Administrator  		/  2nd Level Support
*/
{
 return abfrage("select gruppe from user where login='".$user."'");
}



// *******************************************
function nextPK($tab,$db=_DB_MAIN,$pkCol="id")
// *******************************************
{
	return 1+((int) abfrage("select max(ifnull(".$pkCol.",0)) from ".$tab,$db));
}



// *************************
function db_exists($db_name)
// *************************
{
 $rw = false;

 $result = mysql_select("show databases");
 $line   = mysql_fetch_row($result);

 while ($line)
 {
  if (strtolower(trim($line[0]))==strtolower(trim($db_name)))   { $rw = true; break; }

  $line = mysql_fetch_row($result);
 }

 unset($result);

 return $rw;
}



// ***********************************
function mysql_clean_string($myString)
// ***********************************
{
	$rVal = $myString;
	$rVal = str_replace("\\","\\\\",$rVal);		// input backslash mask
	$rVal = str_replace("'","\\'",$rVal);		// input ' mask
	return $rVal;
}



// ******************************************
function mysql_enum_array($table,$col,$db="")
// ******************************************
{
    $retVal = abfrage("SHOW COLUMNS FROM ".$table." LIKE '".$col."'",$db,1);
    
    $retVal = substr($retVal,strpos($retVal,'\''));
    
    $retVal = substr($retVal,0,strlen($retVal)-1);
    
    // FALLS STRING WERTE
    if (instr($retVal,"','"))   return explode("','",substr($retVal,1,strlen($retVal)-2)); 
    else                        return explode(",",$retVal);
}


?>