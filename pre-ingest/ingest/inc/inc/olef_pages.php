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
    throw new Exception("OLEF root 'element' not found (pages)!");
}
$elementElement = $elementElements->item(0);

// find itemInformation tag
$iiElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'itemInformation');
$iiElement = null;
if( $iiElements->length > 0 ) {
    $iiElement = $iiElements->item(0);
}
else {
    $iiElement = $olefDom->appendChild($elementElement, $_NAMESPACE_OLEF, 'itemInformation');
}

// find files element
$fsElements = $iiElement->getElementsByTagNameNS($_NAMESPACE_OLEF, 'files');
$fsElement = null;
if( $fsElements->length > 0 ) {
    $fsElement = $fsElements->item(0);
}
else {
    $fsElement = $olefDom->appendChild($iiElement, $_NAMESPACE_OLEF, 'files');
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
    $fElement = $olefDom->appendChild($fsElement, $_NAMESPACE_OLEF, 'file');
    $fElement->setAttributeNS($_NAMESPACE_OLEF, 'type', 'image');
    
    // create reference element for file
    $rElement = $olefDom->appendChild($fElement, $_NAMESPACE_OLEF, 'reference', $tiffFile);
    $rElement->setAttributeNS($_NAMESPACE_OLEF, 'type', 'path');
    
    // create pages element
    $psElement = $olefDom->appendChild($fElement, $_NAMESPACE_OLEF, 'pages');
    
    // create page entry
    $pElement = $olefDom->appendChild($psElement, $_NAMESPACE_OLEF, 'page');
    $pElement->setAttributeNS($_NAMESPACE_OLEF, 'pageType', $arrPageInfos[2]);
    $pElement->setAttributeNS($_NAMESPACE_OLEF, 'sequence', $arrPageInfos[1]);
    // check if we have page-name info
    if( isset($arrPageInfos[3]) ) {
        $pnElement = $olefDom->appendChild($pElement, $_NAMESPACE_OLEF, 'name', $arrPageInfos[3]);
    }
    
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
            $tElement = $olefDom->appendChild($pElement, $_NAMESPACE_OLEF, 'taxon');
            $snElement = $olefDom->appendChild($tElement, _NAMESPACE_DWC, 'scientificName', $nameString);
        }
    }
}
