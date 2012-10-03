<?php
// ********************************************
// ** FILE:    CONTENT_LIST_BUTTONS.PHP      **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GUI WORKSTEPS

/* CONTENT_LAST_SUCC_STEP
 * 
 * 0 ... no step
 * 1 ... metadata generated
 * 2 ... page images (tiffs) ok
 * 3 ... ocr or pdf2text done
 * 4 ... taxonfinding done
 * 5 ... gid (?)
 * 6 ... format specs (?)
 * 7 ... 
 * 8 ... aip ready for ingest
 * 9 ... ingested
 * 
 * 10 .. checked
 * 
 */

$text201 = "currently running or in queue ...";




// METADATA (1)
$command = "onClick=\"javascript: popup_win('gm_" . $line[0] . "','"._SYSTEM."?menu_nav=get_metadata&content_id=".$line[0]."',1000,500);\""; 

if ($line[10]==0)        icon("metadata.png","Prepare Metadata now...",$command); 
else                     icon("metadata0.png","Metadata already prepared.",$command); 


// -------- queueable methods start -----

// IMAGES (2)
$command = "onClick=\"javascript: popup_win('pi_" . $line[0] . "','"._SYSTEM."?menu_nav=get_images&content_id=".$line[0]."',1000,500);\"";

if (($jobRunning) && ($line[10]==1))
    icon("tif32_anim.gif",      "Image Processing ".$text201);
else if ($line[10]==0) 
    icon("tif32_00.png",        "Prepare Metadata first!");
else if ($line[10]==1)    
    icon("tif32.png",           "Prepare Page Images now...",$command); 
else 
    icon("tif32_0.png",         "Images already prepared.",$command); 


// OCR (3)

if (!$isPDF)   // FALLS NICHT PDF
{
    $command = "onClick=\"javascript: popup_win('po_" . $line[0] . "','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."',1000,500);\"";
    
    if (($jobRunning) && ($line[10]==2))
        icon("ocr_anim.gif",       "OCR ".$text201);
    else if ($line[10]<2)
        icon("ocr00.png",          "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)    
        icon("ocr.png",            "Generate OCR for all Pages now...",$command); 
    else
        icon("ocr0.png",           "OCR already prepared.",$command); 
}
else           // PDF TO TEXT & TIF HERE
{
    $command = "onClick=\"javascript: popup_win('pp_" . $line[0] . "','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."&pdf=1',1000,500);\"";
    
    if (($jobRunning) && ($line[10]==2))
        icon("pdf2txt_anim.gif",   "PDF to Text ".$text201);
    else if ($line[10]<2)    
        icon("pdf2txt00.png",      "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)
        icon("pdf2txt.png",        "Prepare PDF Text now...",$command); 
    else
        icon("pdf2txt0.png",       "PDF Text already prepared.",$command); 
}


// TAXON (4)
$command = "onClick=\"javascript: popup_win('tx_" . $line[0] . "','"._SYSTEM."?menu_nav=get_taxons&content_id=".$line[0]."',1000,500);\"";

if (($jobRunning) && ($line[10]==3))
    icon("taxon_anim.gif",          "Taxonfinder ".$text201);
else if ($line[10]<3) 
    icon("taxon00.png",             "Prepare (OCR) Text first!");
else if ($line[10]==3)    
    icon("taxon.png",               "Prepare/Gather Taxonometric Information now...",$command); 
else 
    icon("taxon0.png",              "Taxonometric Information already present.",$command); 



// FORMAT SPECS (5) - MEDIA ATTRIBUTES



// METS INGEST CONTROL FILES

$command = "onClick=\"javascript: popup_win('in_" . $line[0] . "','"._SYSTEM."?menu_nav=get_mets&content_id=".$line[0]."',1000,500);\"";

if (!isset($endJS)) $endJS = "";

// check for last successfull step
if( $line[10] == 4 ) {
    // check if we are waiting for the ingest to happen
    /*if ($line[12] == 1) {
        icon("planning_anim.gif", "Ingest ".$text201);
    }
    else {*/
        icon("planning.png", "Ingest now...",$command);
    //}
}
// already ingested
else if( $line[10] == 5 ) {
    // check if ingest is still running
    $queueResult = mysql_select("SELECT * FROM `ingest_queue` WHERE `content_id` = '" . $line[0] . "' AND `ingest_status` != 'finished'");
    $bQueueFailed = false;
    $bQueueRunning = false;
    $bQueueFinished = true;
    if( mysql_num_rows($queueResult) > 0 ) {
        $bQueueFinished = false;
        
        while( ($queueRow = mysql_fetch_array($queueResult)) ) {
            $queueStatus = $queueRow['ingest_status'];

            if( $queueStatus == 'error' ) {
                $bQueueFailed = true;
                break;
            }
            else if( $queueStatus == 'running' ) {
                $bQueueRunning = true;
            }
        }
    }
    
    // evaluate queue statuses
    if( $bQueueFailed ) {
        icon("planning_failed.png", "Ingest failed, please contact administrator");
    }
    else if( $bQueueRunning ) {
        icon("planning_anim.gif", "Ingest ".$text201);
    }
    else if( $bQueueFinished ) {
        echo "<div id=\"dialog_".$line[0]."\" title=\""._APP_NAME." - Ingest \">
            <br><ul>
            <li><u><a href='#' ".$command.">Regenerate ingest package (OLEF mods, page METS)...</a></u></li><br>
            <li><u><a href='"._FEDORA_ADMIN_GUI."' target=_blank>Show Ingest Log on Fedora...</a></u></li><br>
            <li><u><a href='"._SYSTEM."?menu_nav=ingest_list&sub_action=reset_ingest&content_id=".$line[0]."' target=_blank>Reset AIP to status \"not ingested\", removed flag \"ready for ingest\" and METS files...</a></u><br>
            <font size=1>Enables you to re-prepare steps and/or re-ingest...</font></li><br>
            </ul>
            </div>";

        $endJS .= "
        jQuery.noConflict();
        (function($) 
        {
            $( \"#dialog_".$line[0]."\" ).dialog({  autoOpen: false, width: 600, height: 320, draggable:true, modal: false,  buttons: { \"Close\": function() { $(this).dialog(\"close\"); } }   });

            $('#ingestButton_".$line[0]."').click(function() 
            {
                $( \"#dialog_".$line[0]."\" ).dialog('open');
            });
        })(jQuery);
        ";

        icon("planning0.png",
            "Already ingested see Fedora logs.",
            "onClick=\"\"","","",true,false,"ingestButton_".$line[0]);
    }
    else {
        icon("planning_queued.png", "Ingest queued, waiting for processing");
    }
}
else {
    icon("planning00.png",             "Media not ready for release!");
}

// DROP INGEST
lz(1);
icon("sep.png");
lz(1);

icon("failed_30.png",   "Drop Content from Content Management (not Filesystem). Drop if ingested correctly or to re-analyze/prepare.",
        "onClick=\"nachfrage('Drop selected Content Element from Management?','"._SYSTEM."?menu_nav=".$menu_nav."&sub_action=drop_content&content_id=".$line[0]."');\"");


// VISIBLE STATUS UPDATES (GUI ONLY)

if (file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_FINISHED))
{
    $endJS .= "
        
    document.forms.form_ingest_manager.content_status_".$line[0].".options[3].selected = true;

 ";

}
else if (file_exists(clean_path($line[3]."/"._AIP_DIR."/")._FEDORA_CF_READY))
{
        $endJS .= "
        
    document.forms.form_ingest_manager.content_status_".$line[0].".options[2].selected = true;

 ";
}

?>
