<?php

/* DEFINES THE FOLLOWING (IF POSSIBLE):

_REMOTE_ADDR
_REMOTE_PROXY
_REMOTE_USER
_REMOTE_DOMAIN

*/


$tmparr = array();
$remoteProxy = "";


// 1. PROOF INSECURE EXISTENCE OF USABLE HTTP_CLIENT_IP VAR AT FIRST
if ( (isset($_SERVER["HTTP_CLIENT_IP"])) and (is_numeric(substr($_SERVER["HTTP_CLIENT_IP"],0,1))) )
{
    $tmparr[] = $_SERVER['HTTP_CLIENT_IP'];
}
// 2. HTTP_CLIENT_IP NICHT GESETZT
elseif ( (isset($_SERVER['REMOTE_ADDR']))   and (is_numeric(substr($_SERVER['REMOTE_ADDR'],0,1))) )
{
    $tmparr[] = $_SERVER['REMOTE_ADDR'];
}


// 1. PROXY USES PROXIES - THEREFORE $_SERVER['HTTP_X_FORWARDED_FOR'] IS AN ARRAY STARTING WITH CLIENT IP
if (isset($_SERVER['HTTP_X_FORWARDED_FOR']) && strpos($_SERVER['HTTP_X_FORWARDED_FOR'],','))
{
    $tmparr +=  explode(',',$_SERVER['HTTP_X_FORWARDED_FOR']);
}

// 2. SINGLE PROXY
elseif (isset($_SERVER['HTTP_X_FORWARDED_FOR']))
{
    $tmparr[] = $_SERVER['HTTP_X_FORWARDED_FOR'];
}


// IF PROXY EXISTS $_SERVER['REMOTE_ADDR'] IS 1ST PROXY if ( (count($tmparr)>0) and ($client_ip != $_SERVER['REMOTE_ADDR']) )
if ( (count($tmparr)>0) and (isset($_SERVER["HTTP_CLIENT_IP"])) and (isset($_SERVER["REMOTE_ADDR"])) and
($_SERVER["HTTP_CLIENT_IP"] != $_SERVER['REMOTE_ADDR']) )
{
    // $remoteProxy = $_SERVER['REMOTE_ADDR'];
    $tmparr[1] = $_SERVER['REMOTE_ADDR'];
}

if (array_key_exists(1,$tmparr)) $remoteProxy = $tmparr[1];

// $tmparr[] = $_SERVER['REMOTE_ADDR'];

if (array_key_exists(0,$tmparr))    define("_REMOTE_ADDR",$tmparr[0]);  // IST SOWIESO IMMER AN ERSTER STELLE DA
else                                define("_REMOTE_ADDR","");

define("_REMOTE_PROXY",		trim($remoteProxy));	// ENTWEDER ERSTE FORWARDED VAR ODER LETZTE (REMOTE_ADDR)

unset ($remoteProxy); unset ($tmparr);


$remote_user   = "";
$remote_domain = "";

if (array_key_exists("REMOTE_USER",$_SERVER))
{
    $remote_user = $_SERVER["REMOTE_USER"];

    if (!(strpos($remote_user,"@")===false))            // DOMAIN SUFFIX ENTHALTEN
    {
        $remote_domain = trim(substr($remote_user,strpos($remote_user,"@")+1));
        $remote_user   = trim(substr($remote_user,0,strpos($remote_user,"@")));
    }
}

define("_REMOTE_USER",     $remote_user);
define("_REMOTE_DOMAIN",   $remote_domain);

unset ($remote_user); unset ($remote_domain);


?>