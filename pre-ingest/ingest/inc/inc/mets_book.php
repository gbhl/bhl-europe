<?php
// ********************************************
// ** FILE:    METS_BOOK.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  Wolfgang Koller               **
// ********************************************

// find root METS element
$metsElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_METS, 'mets');
if( $metsElements->length <= 0 ) {
    throw new Exception("METS root element not found");
}
// get (first) root element and assign OBJID
$metsElement = $metsElements->item(0);
$metsElement->setAttribute('OBJID', $cleanObjID);

// find rdf:Description
$rdfElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_RDF_SYNTAX, 'Description');
if( $rdfElements->length <= 0 ) {
    throw new Exception("rdf:Description element not found!");
}
$rdfElement = $rdfElements->item(0);
$rdfElement->setAttributeNS(_NAMESPACE_RDF_SYNTAX, 'about', 'info:fedora/'.$cleanObjID);

// check if we have a isMemberOf info as well
$imoElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_FEDORA_RELATIONS, 'isMemberOf');
if( $imoElements->length > 0 ) {
    $imoElement = $imoElements->item(0);
    $imoElement->setAttributeNS(_NAMESPACE_RDF_SYNTAX, 'resource', 'info:fedora/'.$cleanObjParentID);
}

// update dc:identifier
$identifierElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_DC, 'identifier');
if( $identifierElements->length <= 0 ) {
    throw new Exception("dc:identifier element not found!");
}
$identifierElement = $identifierElements->item(0);
$identifierElement->nodeValue = $cleanObjID;

// find olef-node
$olefElements = $metsDom->getElementsByTagName('olef');
if( $olefElements->length > 0 ) {
    $olefElement = $olefElements->item(0);
    $xmlDataElement = $olefElement->parentNode;

    // Find title tag for use within dc:title
    $titleInfoNodeList = $olefDom->getElementsByTagNameNS(_NAMESPACE_MODS, 'titleInfo');
    // Check if we found a titleInfo entry
    if( $titleInfoNodeList->length > 0 ) {
        // Find all dublin-core title fields
        $titleNodeList = $metsDom->getElementsByTagNameNS(_NAMESPACE_DC, 'title');
        // Cycle through entries and assign them the mods titleInfo value
        for( $k = 0; $k < $titleNodeList->length; $k++ ) {
            $titleNodeList->item($k)->nodeValue = trim($titleInfoNodeList->item(0)->nodeValue);
        }
    }
    
    // make sure that the xsi namespace is defined
    $attr = $olefDom->createAttribute( 'xmlns:xsi' );
    $attr->value = 'http://www.w3.org/2001/XMLSchema-instance';
    $olefDom->documentElement->setAttributeNode($attr);
    
    // replace olef placeholder element
    $xmlDataElement->replaceChild($metsDom->importNode($olefDom->documentElement, true), $olefElement);
}
