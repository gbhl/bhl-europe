<?php

// PARSE + EDIT PAGE XML


// ATTRIBUTE DES AKTUELLEN NODES BEARBEITEN
foreach ($nodeAttributes as $nodeAttribute)
{
    // ALLE OBJIDs
    if ($nodeAttribute->name == 'OBJID')
      $curElement->setAttribute('OBJID',urlencode($pageID));
    
    // PAGE TIFF
    if ($nodeName=='METS:FLocat')
    {
        if ($nodeAttribute->name == 'href')
        $curElement->setAttribute('xlink:href',"file://".clean_path(_CONTENT_ROOT.$arrTiffs[($i-1)]));
    }
    
    // rdf:Description --> rdf:about="info:fedora/bhle:a0hhmgs3-00002"
    if ($nodeName=='rdf:Description')
    {
        if ($nodeAttribute->name == 'about')
        $curElement->setAttribute('rdf:about','info:fedora/'.urlencode($pageID));
    }
    
    // <isMemberOf  --> rdf:resource="info:fedora/bhle:a0hhmgs3"
    //                  rdf:resource="info:fedora/bhle:10706/a0000000000013270571001"
    if ($nodeName=='isMemberOf')
    {
        if ($nodeAttribute->name == 'resource')
        $curElement->setAttribute('rdf:resource','info:fedora/'.urlencode($objID));
    }
}


// VALUES DES AKTUELLEN NODES BEARBEITEN

if ($nodeName=='dc:identifier')
{
    $curElement->nodeValue = urlencode($pageID);
}

if ($nodeName=='dc:title')
{
    $curElement->nodeValue = "Page ".$i;    // !!! GET PAGE NUMBER FROM $CUR_TIFF BASENAME ?
}



?>
