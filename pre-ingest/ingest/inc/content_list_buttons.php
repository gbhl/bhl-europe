<?php
// ********************************************
// ** FILE:    CONTENT_LIST_BUTTONS.PHP      **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************


// WORKSTEPS
// arr_dropdown($myArr, $feldname, $wert, $size, $title, $js)


// !!! HIER EINSCHALTEN ALLER TASKS FUER DEN ZU ERSTELLENDEN JOB!!!!

// !!! VON GRAU AUF FARBIG -- WENN FERTIG AUF HAECKCHEN

// !!! BUTTON CSS UEBERARBEITEN WIE IN PROVIDER DETAILS ABER ABGERUNDET ETC.



echo "<a href='#' >";
icon("metadata.png","Prepare Metadata","onClick=\"javascript: popup_win('gm','"._SYSTEM."?menu_nav=get_metadata&content_id=".$line[0]."',1000,500);\"");
echo "</a>";


// icon($image, $alt, $action, $width, $height, $echo, $img_map, $id)

/*
 * 
 * button("Reload & Analyze My Current Uploads",
        ,900,32,'center'); 


if ($arrIngestDetails['ingest_last_successful_step']==0)    
{
// METADATA
    icon("metadata.png",     "Prepare Metadata");
else
    icon("metadata0.png",    "");
*/



// IMAGES
if ($arrIngestDetails['ingest_last_successful_step']<2)    
    icon("tif32.png",         "Process Image Transformations");
else
    icon("tif32.png",        "");


// OCR
// FALLS NICHT PDF
if (!$isPDF)   
{
    // OCR FOR TIF HERE
    if ($arrIngestDetails['ingest_last_successful_step']<2)    
        icon("ocr00.png",          "Generate OCR for all Pages");
    else
        icon("ocr0.png",         "");
}

// PDF TO TEXT & TIF HERE
else
{
    if ($arrIngestDetails['ingest_last_successful_step']<2)    
        icon("pdf2tiff00.png",      "Generate TIFF and TEXT from PDF");
    else
        icon("pdf2tiff0.png",     "");
}

// TAXON
if ($arrIngestDetails['ingest_last_successful_step']==0)    
    icon("taxon00.png",        "Get Taxons for all Pages");
else
    icon("taxon0.png",       "");        

// INGEST
if ($arrIngestDetails['ingest_last_successful_step']==0)    
    icon("planning00.png",     "Queue/Start Fedora Ingest");
else
    icon("planning00.png",    "");

        // !!! icon("planning0.png",    "");

lz(2);
icon("failed_30.png",   "Drop Content from Content Management (not Filesystem)",
        "onClick=\"nachfrage('Drop selected Content Element from Management?','"._SYSTEM."?menu_nav=".$menu_nav."&sub_action=drop_content&content_id=".$line[0]."');\"");

// icon($image, $alt, $action, $width, $height, $echo, $img_map, $id)
if ($arrIngestDetails['ingest_status']=='')  // undefiniert
{
    // PREPARE ANBIETEN (=OLEF)
    // prepare this mediums metadata
    // generate ocr task

    // ingest
}        



?>
