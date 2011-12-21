<?php
// ********************************************
// ** FILE:    CONTENT_LIST_BUTTONS.PHP      **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// GUI WORKSTEPS


// !!! HIER EINSCHALTEN ALLER TASKS FUER DEN ZU ERSTELLENDEN JOB!!!!
// !!! BUTTON CSS UEBERARBEITEN WIE IN PROVIDER DETAILS ABER ABGERUNDET ETC.


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


// METADATA (1)
$command = "onClick=\"javascript: popup_win('gm','"._SYSTEM."?menu_nav=get_metadata&content_id=".$line[0]."',1000,500);\""; 

if ($line[10]==0)        icon("metadata.png","Prepare Metadata now...",$command); 
else                     icon("metadata0.png","Metadata already prepared.",$command); 


// -------- queueable methods start -----

// IMAGES (2)
$command = "onClick=\"javascript: popup_win('pi','"._SYSTEM."?menu_nav=get_images&content_id=".$line[0]."',1000,500);\"";

if ($line[10]==0) 
    icon("tif32_00.png","Prepare Metadata first!");
else if ($line[10]==1)    
    icon("tif32.png","Prepare Page Images now...",$command); 
else 
    icon("tif32_0.png","Images already prepared.",$command); 


// OCR (3)

if (!$isPDF)   // FALLS NICHT PDF
{
    $command = "onClick=\"javascript: popup_win('po','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."',1000,500);\"";
    if ($line[10]<2)
        icon("ocr00.png",          "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)    
        icon("ocr.png",            "Generate OCR for all Pages now...",$command); 
    else
        icon("ocr0.png",           "OCR already prepared.",$command); 
}
else           // PDF TO TEXT & TIF HERE
{
    $command = "onClick=\"javascript: popup_win('pp','"._SYSTEM."?menu_nav=get_ocr&content_id=".$line[0]."&pdf=1',1000,500);\"";
    if ($line[10]<2)    
        icon("pdf2tiff00.png",      "Prepare Metadata and Page Images first!");
    else if ($line[10]==2)
        icon("pdf2tiff.png",        "Prepare PDF Text now...",$command); 
    else
        icon("pdf2tiff0.png",       "PDF Text already prepared.",$command); 
}


// TAXON (4)
$command = "onClick=\"javascript: popup_win('tx','"._SYSTEM."?menu_nav=get_taxons&content_id=".$line[0]."',1000,500);\"";

if ($line[10]<3) 
    icon("taxon00.png",             "Prepare (OCR) Text first!");
else if ($line[10]==3)    
    icon("taxon.png",               "Prepare/Gather Taxonometric Information now...",$command); 
else 
    icon("taxon0.png",              "Taxonometric Information already present.",$command); 


// FORMAT SPECS (5)

// GLOBAL UNIQUE ID (6)



// INGEST (9)
$command = "onClick=\"javascript: popup_win('in','"._SYSTEM."?menu_nav=ingest&content_id=".$line[0]."',1000,500);\"";

if ($line[10]<4)   // !!! CHANGE THAT TO 6 IF 5+6 ARE NECESSARY
    icon("planning00.png",             "Media not ready for Ingest!");
else if ($line[10]==3)    
    icon("planning.png",               "Ingest Media now...",$command); 
else 
    icon("planning0.png",              "Media is already ingested.",$command); 



// DROP INGEST

lz(2);
icon("failed_30.png",   "Drop Content from Content Management (not Filesystem). Drop if ingested correctly or to re-analyze/prepare.",
        "onClick=\"nachfrage('Drop selected Content Element from Management?','"._SYSTEM."?menu_nav=".$menu_nav."&sub_action=drop_content&content_id=".$line[0]."');\"");


?>
