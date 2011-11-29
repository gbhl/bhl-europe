<?php
// ********************************************
// ** FILE:    SESSION.PHP                   **
// ** PURPOSE: UNIVERSAL SESSION HANDLER     **
// **          FOR MYSQL + COOKIES           **
// ** DATE:    19.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// SIMPLE SESSION HANDLER - NICHT MANDANTENFAEHIG
// FUER STANDARD USERS UND USER_SESSIONS TABELLENVORGABE
// 
// MUSS OHNE COOKIES $_POST[_SESSION_ID_RAW] MITLIEFERN!

// INITs
if (!defined("_SESSION_COOKIE"))      define("_SESSION_COOKIE",         true);
if (!defined("_SESSION_TAB"))         define("_SESSION_TAB",            "user_session");
if (!defined("_SESSION_ID_SALT"))     define("_SESSION_ID_SALT",        "qms9d8fziap7");
if (!defined("_SESSION_ID_OFFSET"))   define("_SESSION_ID_OFFSET",      473802);
if (!defined("_SESSION_EXPIRE_TIME")) define("_SESSION_EXPIRE_TIME",    86400);

if (!defined("_SESSION_ID_RAW"))      define("_SESSION_ID_RAW",         "session_id_raw");

if (!defined("_USER_TAB"))            define("_USER_TAB",               "users");




// ***************************************************
// * ERZEUGT SESSION COOKIE UND DB EINTRAG UND       *
// * GIBT BEI ERFOLG $SESSION_ID ZURUECK SONST FALSE *
// ***************************************************
function session_create($user_id,$db=_DB_MAIN,$session_id='',$setCookie=true,$insertDB=true) 
{
 $cookie_ok = true; 
 $db_ok     = true;
 
 if (!is_numeric($user_id)) return false;
 
 
 // CREATE NEW SESSION ID 
 if ((!is_numeric($session_id))||($session_id<_SESSION_ID_OFFSET))
 { 
     // DATABASE SESSION PK 
     $new_session_id = 1 + ((int) abfrage("select max(session_id) from "._SESSION_TAB,$db));
     
     if ($new_session_id<_SESSION_ID_OFFSET)    // FALLS ERSTE SESSION
     $new_session_id = $new_session_id + _SESSION_ID_OFFSET;
 }
 else
     $new_session_id = $session_id;


 // CREATE DATABASE SESSION RAW ID = COOKIE SESSION ID
 $new_session_id_raw = _SESSION_ID_SALT.$new_session_id."_".time();


 // COOKIE SETZEN
 if ($setCookie)    
     $cookie_ok = setcookie(_SESSION_ID_RAW,$new_session_id_raw,time()+_SESSION_EXPIRE_TIME);	

 // SESSION IN DB EROEFFNEN
 if ($insertDB)
 {
    $query = "select count(1) from "._SESSION_TAB." where session_id=".$new_session_id;
    
    if (((int) abfrage($query,$db)) == 0)
    {
     $query = "insert into "._SESSION_TAB." (session_id, user_id, last_action) 
         VALUES (".$new_session_id.",".$user_id.",now()) ";
     
     if (mysql_select($query,$db)<0) $db_ok = false;
    }
 }  

 if ( ($cookie_ok) && ($db_ok)) return $new_session_id;
 else                           return false; 
 
}



// ********************************************************************
// GET THE NUMERICAL DATABASE PK SESSION ID FROM A RAW SESSION ID
// E.G. FROM THE ID IN THE COOKIE
// ********************************************************************
function session_get_id_num($session_id_raw)
{
 $rw = str_replace(_SESSION_ID_SALT,'',$session_id_raw);
 $rw = trim(substr($rw,0,strpos($rw,"_")));
 
 if ($rw!='')   return $rw;
 else           return false;
}



// ********************************************************************
// GET THE RAW DATABASE SESSION ID FROM A NUMERICAL DATABASE SESSION PK
// E.G. THE ID IN THE COOKIE
// ********************************************************************
function session_get_id_raw($session_id,$db=_DB_MAIN)
{
  $rw = false;
  
  if (is_numeric($session_id)) 
  {
      $query = "select session_id_raw from "._SESSION_TAB." where session_id=".$session_id;
      
      $rw = abfrage($query,$db);
      
      if ($rw != '') return $rw;
  }
  
  return false;
}


// **************************************************************
// LOESCHT SESSION COOKIE UND SESSION DB ROW 
// GIBT BEI ERFOLG TRUE ZURUECK
// **************************************************************
function session_drop($session_id,$db=_DB_MAIN,$deleteDB=true)
{
 $rw = FALSE;
 
 // COOKIE LOESCHEN MIT EXPIRE VOR 1 STUNDE
 $rw = setcookie(_SESSION_ID_RAW,"dummy",time() - 3600);   
 
 // DB SESSION ROW LOESCHEN
 if (($deleteDB)&&(is_numeric($session_id)))
 mysql_select("delete from "._SESSION_TAB." where session_id=".$session_id,$db);

 return $rw;
}


// ***********************************************************
// HOLT RAW SESSION ID VOM MITGESENDETEN CLIENT COOKIE
// FALLS CLIENT MITGESENDET HAT (SESSION BESTEHT)
// ***********************************************************
function session_get_client_info()
{
    $cookie = array();
    
    $cookie = @explode(";",$_COOKIE[_SESSION_ID_RAW]);

    return $cookie[0];
}



// **********************************************
// RETURNS USER_ID FOR SESSION_ID
// **********************************************
function session_user_id($session_id,$db=_DB_MAIN)
{
 if (is_numeric($session_id))
 {
  $query = "select user_id from "._SESSION_TAB." where session_id=".$session_id;
  
  $user_id = abfrage($query,$db);
  
  if (strlen($user_id) > 0) return $user_id;
 }
 
 return false;
}


// **********************************************
// RETURNS USER_NAME FOR SESSION_ID
// **********************************************
function session_user_name($session_id,$db=_DB_MAIN)
{
 if (is_numeric($session_id))
 {
  $query = "select u.user_name from "._SESSION_TAB." s, "._USER_TAB." u 
      where s.user_id=u.user_id and s.session_id=".$session_id;
    
  $user_name = abfrage($query,$db);
  
  if (strlen($user_name) > 2) return $user_name;
 }

 return false;
}











// **************************************
//          AUSFUEHRUNGSSCHICHT
// **************************************

// INITS
$user_id    = false;           // KEIN USER IST KORREKT ANGEMELDET

$authenticated = false;

if (!isset($db))            $db = _DB_MAIN;
if (!isset($session_id))    $session_id = '';


// ZEITPUNKT LOGIN 
// CREDENTIALS SIND NUR ZUM ZEITPUNKT DES LOGINS EXPLIZIT VERFUEGBAR
if ((isset($user_name)) && (isset($user_pwd)))
{
    $query = "select user_id from users 
        where user_name='".$user_name."' and user_pwd=password('".$user_pwd."')";

    $user_id = abfrage($query,$db); 
            
    // WENN CREDENTIALS OK
    if ((is_numeric($user_id))&&($user_id>=1))
    {
        // NEUE SESSION
        $new_session_id = session_create($user_id,$db,$session_id,_SESSION_COOKIE,true); 
        
        if ($new_session_id)  $session_id = $new_session_id;
        else                  echo _SESSION_FAILED;
        
        unset($new_session_id);
    }
}
// ZEITPUNKT MITTEN IN EINER SESSION
else
{
    // WENN COOKIE ENABLED - DARAUS SESSION RAW ID HOLEN
    if (_SESSION_COOKIE)
    {
        // CLIENT SESSION INFOS ANALYSIEREN (MITGESENDETE COOKIES)
        $session_id_raw = session_get_client_info();
        
        if (strlen($session_id_raw) > 5) 
            $session_id = session_get_id_num($session_id_raw);
        
        unset($session_id_raw);
    }
    else    // KEIN COOKIE ENABLED DANN SESSION RAW ID MUSS VOM CLIENT UEBER POST GEKOMMEN SEIN
    {
        $session_id = session_get_id_num($_POST[_SESSION_ID_RAW]);
    }
    
    if (is_numeric($session_id)) 
    { 
        $user_id   = session_user_id($session_id,$db);
        $user_name = session_user_name($session_id,$db);
    }
}

// *********************
// FALLS SESSION FAILED
// *********************
if ((!$user_id)||(!is_numeric($user_id))||(!$user_name)||(strlen($user_name)<3))
{
        unset($user_id);
        unset($user_name);
        unset($user_pwd);
        unset($session_id);
}
else 
{
    // ****************************************
    // // UPDATE LAST ACTION DATETIME TIMESTAMP
    // ****************************************
    mysql_select("update "._SESSION_TAB." set last_action = now() where session_id=".$session_id,$db);
    
    // SUCCESS
    $authenticated = true;
    
    // INFO
    // WEITERVERWENDET WERDEN KOENNEN: $USER_NAME, $USER_ID, $SESSION_ID (NICHT SESSION_ID_RAW!)
}



// **************************
// *** GARBAGE COLLECTION ***
// **************************
$query  = "delete from "._SESSION_TAB." where user_id not in 
(select u.user_id from "._USER_TAB." u) or 
last_action < ".(time()-_SESSION_EXPIRE_TIME)." ";

mysql_select($query,$db);
 

 
// DIVERSE AKTIONEN (LOGOUT UND REDIRECT ETC.) SIND 
// IM ANSCHLIESSENDEN APPLIKATIONSCODE ZU HINTERLEGEN NICHT MEHR HIER
// EBENSO WIE NEUE SESSION CREATE NACH LOGOUT

// if (isset($session_id))  $session_id = urldecode($session_id);
// if (drop_session(urldecode($session_id)))


?>
