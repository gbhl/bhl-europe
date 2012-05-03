<?php
// ********************************************
// ** FILE:    METS_BOOK.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  Wolfgang Koller               **
// ********************************************

// PARSE + EDIT BOOK XML OR SERIAL LEVEL MAIN XML

// INCOMING: $curElement, $nodeAttributes, $nodeValue




// ATTRIBUTE DES AKTUELLEN NODES BEARBEITEN
foreach ($nodeAttributes as $nodeAttribute)
{
    if ($nodeName=='METS:mets')
    {
        if ($nodeAttribute->name == 'OBJID')
            $curElement->setAttribute('OBJID',$cleanObjID);
    }
    
    // rdf:Description --> rdf:about="info:fedora/bhle:a0hhmgs0"
    if ($nodeName=='rdf:Description')
    {
        if ($nodeAttribute->name == 'about')
        $curElement->setAttribute('rdf:about','info:fedora/'.$cleanObjID);
    }

    // <isMemberOf  --> rdf:resource="info:fedora/bhle:a0hhmgs3"
    //                  rdf:resource="info:fedora/bhle:10706/a0000000000013270571001"
    // SERIALS CHILD - sLevel > 0
    if ($nodeName=='isMemberOf')
    {
        if (($nodeAttribute->name == 'resource')&&($cleanObjParentID!=""))
        $curElement->setAttribute('rdf:resource','info:fedora/'.$cleanObjParentID);
    }
    
}



// VALUES DES AKTUELLEN NODES BEARBEITEN

if ($nodeName=='dc:identifier')
{
    $curElement->nodeValue = $cleanObjID;
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
     
    <olef>
    <olef:olef xmlns:olef="http://www.bhl-europe.eu/bhl-schema/v0.3/">
    <olef:element>
    ......
    </olef:olef>
    </olef> 
     
    */
    if ($nodeName=='METS:xmlData')
    {
        // Load original OLEF
        $olefDom = new DOMDocument();
        $olefDom->load($olef_file);
        
        // Remove OLEF placeholders in template
        $removeNode = null;
        $removeNode = @$docRoot->getElementsByTagName('olef')->item(0);
        if (($removeNode!=null)&&($removeNode!='null')&&($removeNode))
        @$curElement->removeChild($removeNode);           // Everything below parent has to be removed
        
        $removeNode = null;
        $removeNode = @$docRoot->getElementsByTagName('olef:olef')->item(0);
        if (($removeNode!=null)&&($removeNode!='null')&&($removeNode))
        @$curElement->removeChild($removeNode);           // Everything below parent has to be removed
        
        // Insert OLEF
        $newnode = $curElement->appendChild($domDoc->importNode($olefDom->documentElement, true));

        $attr = $domDoc->createAttribute( 'xmlns:xsi' );
        $attr->value = 'http://www.w3.org/2001/XMLSchema-instance';
        $newnode->appendChild( $attr );
    }
}
