<?php
// ********************************************
// ** FILE:    OLEF_PAGES.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.01.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************

// find root 'element'
$elementElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'element');
if( $elementElements->length <= 0 ) {
    throw new Exception("OLEF root 'element' not found (pages)!: " . $_NAMESPACE_OLEF_PREFIX);
}
$elementElement = $elementElements->item(0);

// find itemInformation tag
$iiElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'itemInformation');
$iiElement = null;
if( $iiElements->length > 0 ) {
    $iiElement = $iiElements->item(0);
}
else {
    $iiElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'itemInformation');
    $elementElement->appendChild( $iiElement );
}

// find files element
$fsElements = $iiElement->getElementsByTagNameNS($_NAMESPACE_OLEF, 'files');
$fsElement = null;
if( $fsElements->length > 0 ) {
    $fsElement = $fsElements->item(0);
}
else {
    $fsElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'files');
    $iiElement->appendChild($fsElement);
}

// list all tifs (therefor pages) to be processed
$arrTiffs = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
$arrTiffs = sortPageFiles($arrTiffs);  // IMPORTANT PRE SORT

// generate page information (including scientific names) for each file
foreach( $arrTiffs as $tiffIndex => $tiffFile ) {
    // derive taxon-filename from tiff-filename
    $taxonFile = $tiffFile . _TAXON_EXT;
    // extract page information from filename
    $arrPageInfos = getPageInfoFromFile($tiffFile);
    if( !isset($arrPageInfos[2]) ) $arrPageInfos[2] = _DEFAULT_PAGETYPE;
    
    // create file-element for OLEF
    $fElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'file');
    $fElement->setAttribute($_NAMESPACE_OLEF_PREFIX . 'type', 'image');
    $fsElement->appendChild($fElement);
    
    // create reference element for file
    $rElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'reference');
    $rElement->setAttribute($_NAMESPACE_OLEF_PREFIX . 'type', 'path');
    $rElement->nodeValue = $tiffFile;
    $fElement->appendChild($rElement);
    
    // create pages element
    $psElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'pages');
    $fElement->appendChild($psElement);
    
    // create page entry
    $pElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'page');
    $pElement->setAttribute($_NAMESPACE_OLEF_PREFIX . 'pageType', $arrPageInfos[2]);
    $pElement->setAttribute($_NAMESPACE_OLEF_PREFIX . 'sequence', $arrPageInfos[1]);
    // check if we have page-name info
    if( isset($arrPageInfos[3]) ) {
        $pnElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'name', $arrPageInfos[3]);
        $pElement->appendChild($pnElement);
    }
    $psElement->appendChild($pElement);
    
    // load taxonFinder result and add any found taxon-names to the OLEF
    $domTaxons = new DOMDocument();
    if( $domTaxons->load($taxonFile) ) {
        echo "Found taxons!";
        
        // find all entity elements
        $entityElements = $domTaxons->getElementsByTagName('entity');
        foreach( $entityElements as $entityElement ) {

            // check if we have a valid namebankID
            $bNamebankID = false;
            $nameString = null;
            foreach( $entityElement->childNodes as $entityChildElement ) {
                switch( $entityChildElement->nodeName ) {
                    case 'namebankID':
                        $bNamebankID = true;
                        break;
                    case 'nameString':
                        $nameString = $entityChildElement->nodeValue;
                        break;
                }
            }

            // if we do not have a namebank ID, skip to next entry
            if( !$bNamebankID ) continue;

            // add name to page of OLEF
            $tElement = $olefDom->createElement($_NAMESPACE_OLEF_PREFIX . 'taxon');
            $pElement->appendChild($tElement);
            $snElement = $olefDom->createElement($_NAMESPACE_DWC_PREFIX . 'scientificName', $nameString);
            $tElement->appendChild($snElement);
        }
    }
}
