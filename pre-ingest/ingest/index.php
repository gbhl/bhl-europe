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
include_once(_SHARED."dir_tools.php");

include_once("inc/ingest_tools.php");

include_once("inc/inits.php");          // includes session



if (!_CLI_EXECUTION)
{
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
    
    if ((!isset($menu_nav))||($menu_nav==''))  $menu_nav = 'portal';
    
    // AUSGABE
    if (!isset($noheader))
    include_once("inc/lay_header.php");
    
}   // - no cli -


// *****************************************************
switch($menu_nav)
{
    case "ingest_list":
        include_once("inc/ingest_list.php");
        break;
    
    case "ingest":
        include_once("inc/ingest.php");
        break;
    
    case "upload_analyze":
        include_once("inc/upload_analyze.php");
        break;
    
    case "ingest_detail":
        include_once("inc/ingest_detail.php");
        break;
    
    // *******************************
    // PREPARATION STEP KERNEL TARGETS
    // *******************************
    case "get_metadata":    // olef + guid
    case "get_images":
    case "get_ocr":
    case "get_taxons":
    case "get_mets":
        include_once("inc/step_kernel.php");
        break;

    case "test_mb":
        $debug = 1;
        include_once("inc/message_broker.php");
        break;

 /*   case "job_prepare_ingest":
        include_once("inc/job_prepare_ingest.php");
        break;

    case "job_ingest_fedora":
        include_once("inc/job_ingest_fedora.php");
        break; */

    case "ingest_history":
        include_once('inc/ingest_history.php');
        break;
    
    case "ingest_log":
        include_once("inc/ingest_log.php");
        break;

    case "show_content_root":
        echo "<h1 style='margin-top: 6px;'>Content Root <font size=-1> (1st level overview only)</font></h1>";
        print_dir_arr(getDirectory(_CONTENT_ROOT,array(),0,"",1));
        break;

    case "show_user_dir":
    case "show_user_content_root":
        echo "<h1 style='margin-top: 6px;'>Your Content Root <font size=-1> (for orientation purposes only)</font></h1>";
        print_dir_arr(getDirectory(_USER_CONTENT_ROOT,array(),0,"",_ANALYZE_MAX_DEPTH));        
        break;
    
    case "show_working_dir":
        echo "<h1 style='margin-top: 6px;'>Working Directory<font size=-1> (for orientation purposes only)</font></h1>";
        print_dir_arr(getDirectory(_WORK_DIR.$arrProvider['user_content_id'],array(),0,"",_ANALYZE_MAX_DEPTH));        
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
        show_help_file(_ABS."docs/HISTORY.txt");
        show_help_file(_ABS."docs/INSTALL.txt");
        break;
    
    case "file_tree": 
        // WIRD VON CONTEN_LIST UND AJAX AUFGERUFEN

        if ((isset($start_root))&&($start_root!="/")) {
            $root = urldecode($start_root);
            $root = clean_path(_CONTENT_ROOT."/".str_replace(_CONTENT_ROOT,"",$root));
        }
        else echo _ERR."forbidden.";
        
        // FILE_TREE.PHP WIRD SPAETER VON LAY_FOOTER AUFGERUFEN

        break;
        
    case "file_download":
        include_once(_SHARED."file_download.php");
        die();
        break;

    case "test":
        include_once("inc/test.php");
        die();
        break;
    
    default:
        include_once("inc/portal.php");
        break;
}


// *****************************************************

if (!_CLI_EXECUTION)
        include_once("inc/lay_footer.php");

?>
