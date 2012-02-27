<?php

// PARSE + EDIT BOOK XML

// INCOMING: $curElement, $nodeAttributes, $nodeValue




// ATTRIBUTE DES AKTUELLEN NODES BEARBEITEN
foreach ($nodeAttributes as $nodeAttribute)
{
    if ($nodeName=='METS:mets')
    {
        if ($nodeAttribute->name == 'OBJID')
            $curElement->setAttribute('OBJID',str_replace("/","-",($objID)));
    }
    
    // rdf:Description --> rdf:about="info:fedora/bhle:a0hhmgs0"
    if ($nodeName=='rdf:Description')
    {
        if ($nodeAttribute->name == 'about')
        $curElement->setAttribute('rdf:about','info:fedora/'.str_replace("/","-",($objID)));
    }
}



// VALUES DES AKTUELLEN NODES BEARBEITEN

if ($nodeName=='dc:identifier')
{
    $curElement->nodeValue = str_replace("/","-",($objID));
}


// ADD OLEF
if ($nodeValue=='*olefdata')
{
    /*
    <METS:amdSec ID="OLEF">
    <METS:techMD ID="OLEF.0">
    <METS:mdWrap MIMETYPE="text/xml" MDTYPE="OTHER" LABEL="OLEF Metadata">
    <METS:xmlData>
    <olef></olef>
    </METS:xmlData></METS:mdWrap>
    </METS:techMD>
    </METS:amdSec>
    */
    if ($nodeName=='METS:xmlData')
    {
        $arrAway = array("<?xml version=\"1.0\" encoding=\"utf-8\"?>","<olef>","</olef>");

        $olefXML = html_entity_decode(implode("\n",
                file_get_content_filtered(_OLEF_FILE, $arrAway, "", true)));
        
        // PLATZHALTER NODE ENTFERNEN
        $removeNode = $docRoot->getElementsByTagName('olef')->item(0);
        
        // VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN!
        $oldnode    = $curElement->removeChild($removeNode);

        // OLEF EINFUEGEN
        // $node    = $domDoc->createTextNode("olef","\n".$olefXML."\n");
        $node    = $domDoc->createTextNode("<olef>\n".$olefXML."\n</olef>\n");
        $newnode = $curElement->appendChild($node);
     }
}


?>
