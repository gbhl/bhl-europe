<?php

// PARSE + EDIT PAGE XML


// ATTRIBUTE DES AKTUELLEN NODES BEARBEITEN
foreach ($nodeAttributes as $nodeAttribute)
{
    if ($nodeName=='METS:mets')
    {
        if ($nodeAttribute->name == 'OBJID')
            $curElement->setAttribute('OBJID',str_replace("/","-",($pageID)));
        
        if ($nodeAttribute->name == 'LABEL')        
            $curElement->setAttribute('LABEL','Page '.$i);  // !!! richtige nr verwenden
    }
    
    // PAGE TIFF & OCR PATHS
    if (($nodeName=='METS:FLocat')&&($nodeAttribute->name == 'href'))
    {
        if ($curElement->getAttribute('xlink:title')=='TIFF')
            $curElement->setAttribute('xlink:href',"file://".clean_path(_CONTENT_ROOT.$arrTiffs[($i-1)]));
        
        if ($curElement->getAttribute('xlink:title')=='OCR')
            $curElement->setAttribute('xlink:href',"file://".clean_path(_CONTENT_ROOT.$arrOCR[($i-1)]));
    }
    
    // rdf:Description --> rdf:about="info:fedora/bhle:a0hhmgs3-00002"
    if ($nodeName=='rdf:Description')
    {
        if ($nodeAttribute->name == 'about')
        $curElement->setAttribute('rdf:about','info:fedora/'.str_replace("/","-",($pageID)));
    }
    
    // <isMemberOf  --> rdf:resource="info:fedora/bhle:a0hhmgs3"
    //                  rdf:resource="info:fedora/bhle:10706/a0000000000013270571001"
    if ($nodeName=='isMemberOf')
    {
        if ($nodeAttribute->name == 'resource')
        $curElement->setAttribute('rdf:resource','info:fedora/'.str_replace("/","-",($objID)));
    }
        
}


// VALUES DES AKTUELLEN NODES BEARBEITEN

if ($nodeName=='dc:identifier')
{
    $curElement->nodeValue = str_replace("/","-",($pageID));
}

if ($nodeName=='dc:title')
{
    $curElement->nodeValue = "Page ".$i;    // !!! GET PAGE NUMBER FROM $CUR_TIFF BASENAME ?
}



?>
