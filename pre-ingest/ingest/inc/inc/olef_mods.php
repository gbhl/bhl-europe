<?php
// ********************************************
// ** FILE:    OLEF_MODS.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************
// OLEF MODIFICATIONS POSTPROCESSING
// find root "element" entry of olef data
$elementElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'element');
if( $elementElements->length <= 0 ) {
    throw new Exception("OLEF root 'element' not found!: " . $_NAMESPACE_OLEF);
}
$elementElement = $elementElements->item(0);

// update guid information
$guidElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'guid');
$guidElement = null;
if( $guidElements->length > 0 ) {
    $guidElement = $guidElements->item(0);
}
else {
    $guidElement = $olefDom->appendChild($elementElement, $_NAMESPACE_OLEF, 'guid');
}
$guidElement->nodeValue = $objID;

// update parent-guid information
if( $objParentID != "" ) {
    $parentGuidElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, 'parentGUID');
    $parentGuidElement = null;
    if( $parentGuidElements->length > 0 ) {
        $parentGuidElement = $parentGuidElements->item(0);
    }
    else {
        $parentGuidElement = $olefDom->appendChild($elementElement, $_NAMESPACE_OLEF, 'parentGUID');
    }
    $parentGuidElement = $objParentID;
}

// find bibliographic information
$biElements = $olefDom->getElementsByTagNameNS($_NAMESPACE_OLEF, "bibliographicInformation");
if( $biElements->length <= 0 ) {
    throw new Exception("Unable to find bibliographicInformation");
}
$biElement = $biElements->item(0);

// add IPR information (if it doesn't exist)
$acElements = $olefDom->getElementsByTagNameNS(_NAMESPACE_MODS, 'accessCondition');
if( $acElements->length <= 0 ) {
    $acElement = $olefDom->appendChild($biElement, _NAMESPACE_MODS, 'accessCondition');
    $acElement->setAttributeNS(_NAMESPACE_MODS, 'type', 'use and reproduction' );
}
else {
    $acElement = $acElements->item(0);
}
$acElement->nodeValue = $arrContentDetails['content_ipr'];

// Construct correct recordcontentsource name
$recordContentSource = strtoupper(trim($arrProvider['user_content_id']));
if ($recordContentSource=='') $recordContentSource = strtoupper(trim($arrProvider['user_name']));
// find recordContentSource entries
$rcsElements = $olefDom->getElementsByTagNameNS(_NAMESPACE_MODS, 'recordContentSource');
$rcsElement = null;
if( $rcsElements->length > 0 ) {
    foreach( $rcsElements as $currRcsElement ) {
        if( $currRcsElement->nodeValue == $recordContentSource ) {
            $rcsElement = $currRcsElement;
            break;
        }
    }
}
// check if we found a valid entry, if not create one
if( $rcsElement == null ) {
    // Try to find recordInfo node
    $riElements = $olefDom->getElementsByTagNameNS(_NAMESPACE_MODS, 'recordInfo');
    $riElement = null;
    // Attach recordContentSource node to first recordInfo node
    if( $riElements->length > 0 ) {
        $riElement = $riElements->item(0);
    }
    else {
        // Create new recordInfo node
        $riElement = $olefDom->appendChild($biElement, _NAMESPACE_MODS, 'recordInfo' );
    }
    $rcsElement = $olefDom->appendChild($riElement, _NAMESPACE_MODS, 'recordContentSource');
}
$rcsElement->nodeValue = $recordContentSource;

echo "OLEF data updated.\n";
