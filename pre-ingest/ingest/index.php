<?php
// ********************************************
// ** FILE:    INDEX.PHP                     **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// MAIN INGESTION KERNEL


// FIRST LOAD INSTALLATION CONFIGURATION
if (substr($_SERVER['DOCUMENT_ROOT'],1,1)==":")     include_once("config/config_dev.php");
else                                                include_once("config/config_prod.php");


// APPLICATION GLOBALs
include_once("inc/globals.php");            // SYSTEM PARAMENTER (DERIVED)


// SHARED LIBS
include_once(_SHARED."tools.php");
include_once(_SHARED."mysql.php");
include_once("inc/ingest_tools.php");

include_once("inc/inits.php");



if (!_CLI_EXECUTION)
{
    include_once(_SHARED."session.php");
    
    // ******
    // LOGOUT
    // ******
    if ((isset($menu_nav))&&($menu_nav=="logout"))
    {
        if (isset($session_id)) session_drop($session_id);
        unset($user_name);
        unset($user_id);
        unset($user_pwd);
        $menu_nav='portal';
    }
    
    // SICHERHEITSUMLEITUNG & MENU_NAV MUSS SCHON FUER LAY_HEADER KLAR SEIN
    if ((!$authenticated)&&(!in_array($menu_nav,array('about','help'))))
        $menu_nav = "portal";
    
    
    // DATABASE OPERATIONS
    if (isset($sub_action))
    {
        include("inc/db_ops.php");
    }
    
    
    // AUSGABE
    include_once("inc/lay_header.php");

    
}   // - no cli -


// *****************************************************
switch($menu_nav)
{
    case "ingest_list":
        include_once("inc/ingest_list.php");
        break;
    
    case "upload_analyze":
        include_once("inc/upload_analyze.php");
        break;
    
    case "ingest_detail":
        include_once("inc/ingest_detail.php");
        break;
    
    case "get_metadata":
        include_once("inc/get_metadata.php");
        break;

    case "job_prepare_ingest":
        include_once("inc/job_prepare_ingest.php");
        break;

    case "job_ingest_fedora":
        include_once("inc/job_ingest_fedora.php");
        break;

    case "ingest_history":
        include_once('inc/ingest_history.php');
        break;
    
    case "ingest_log":
        include_once("inc/ingest_log.php");
        break;

    case "show_content_root":
		show_content_root();
		break;
   
    case "show_user_dir":
        show_user_dir($user_id);
        break;
    
    case "selftest":
        include_once("inc/selftest.php");                
        break;
    
    case "admin":
        include_once("inc/admin.php");        
        break;
    
    case "about":
        show_help_file(_ABS."docs/README.txt");
        break;
    
    case "help":
        show_help_file(_ABS."docs/HELP.txt");
        show_help_file(_ABS."docs/INSTALL.txt");
        break;
    
    default: 
        include_once("inc/portal.php");
        break;    
}

// *****************************************************

if (!_CLI_EXECUTION)  include_once("inc/lay_footer.php");

?>