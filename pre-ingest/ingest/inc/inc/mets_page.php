<?php
// ********************************************
// ** FILE:    METS_PAGE.PHP                 **
// ** PURPOSE: prepare page METS data        **
// ** DATE:    31.08.2012                    **
// ** AUTHOR:  Wolfgang Koller               **
// ********************************************

// find root METS element
$metsElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_METS, 'mets');
if( $metsElements->length <= 0 ) {
    throw new Exception("METS root element not found (page)");
}
// get (first) root element and assign OBJID
$metsElement = $metsElements->item(0);
$metsElement->setAttribute('OBJID', $cleanPageID);
$metsElement->setAttribute('LABEL', 'Page ' . $i);

// find file references
$flocatElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_METS, 'FLocat');
if( $flocatElements->length <= 0 ) {
    throw new Exception("METS FLocat element(s) not found");
}
// update all found file references
foreach( $flocatElements as $flocatElement ) {
    switch( $flocatElement->getAttributeNS(_NAMESPACE_XLINK, 'title') ) {
        case 'TIFF':
            $flocatElement->setAttributeNS(_NAMESPACE_XLINK, 'href', "file://".clean_path($arrTiffs[($i-1)]));
            break;
        case 'OCR':
            $flocatElement->setAttributeNS(_NAMESPACE_XLINK, 'href', "file://".clean_path($arrOCR[($i-1)]));
            break;
    }
}

// find rdf:Description
$rdfElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_RDF_SYNTAX, 'Description');
if( $rdfElements->length <= 0 ) {
    throw new Exception("rdf:Description element not found!");
}
$rdfElement = $rdfElements->item(0);
$rdfElement->setAttributeNS(_NAMESPACE_RDF_SYNTAX, 'about', 'info:fedora/'.$cleanPageID);

// update isMemberOf info as well
$imoElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_FEDORA_RELATIONS, 'isMemberOf');
if( $imoElements->length <= 0 ) {
    throw new Exception("isMemberOf element not found!");
}
$imoElement = $imoElements->item(0);
$imoElement->setAttributeNS(_NAMESPACE_RDF_SYNTAX, 'resource', 'info:fedora/'.$cleanObjID);

// update dc:identifier
$identifierElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_DC, 'identifier');
if( $identifierElements->length <= 0 ) {
    throw new Exception("dc:identifier element not found!");
}
$identifierElement = $identifierElements->item(0);
$identifierElement->nodeValue = $cleanPageID;

// update dc:title
$identifierElements = $metsDom->getElementsByTagNameNS(_NAMESPACE_DC, 'title');
if( $identifierElements->length <= 0 ) {
    throw new Exception("dc:title element not found!");
}
$identifierElement = $identifierElements->item(0);
$identifierElement->nodeValue = "Page " . $i;
