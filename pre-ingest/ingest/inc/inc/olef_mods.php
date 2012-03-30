<?php
// ********************************************
// ** FILE:    OLEF_MODS.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// OLEF MODIFICATIONS

echo "<h3>Finishing OLEF Data</h3><pre>";



// **********************
// ADD GUID + PARENT GUID
// **********************
if (!file_content_exists($olef_file,"olef:guid",true,true))
{
    // LOAD OLEF TO DOM
    $domDoc   = new DOMDocument();
    @$domDoc->load($olef_file);

    // ADD NODE TO OLEF ELEMENT
    $node1 = $domDoc->createElement("olef:guid",$objID);

    $node1b=false;
    if (($cType=='serial')&&($sLevel>1)) $node1b = $domDoc->createElement("olef:parentGUID",$objParentID);

    $bis = $domDoc->getElementsByTagName("element");

    foreach($bis as $bi)
    {
        $element = $bi;
        $newnode = $element->appendChild($node1);       // guid
        if ($node1b) $element->appendChild($node1b);    // parent guid
    }

    // SPEICHERN MODIFIZIERTEN OLEF
    if ($domDoc->save($olef_file)>0) echo "GUID added ... ok\n";
    else                             { echo _ERR." GUID addition failed!\n"; $ingestReady = false; }
}



// ***************************************
//  ADD INTELLECTUAL PROPERTY RIGHTS (IPR) 
// ***************************************
/* $arrFind = array(
    'accessCondition<bibliographicInformation><mods:',
    '<bibliographicInformation>\n<mods:',
    '<bibliographicInformation>\n\n<mods:');
    http://www.loc.gov/standards/mods/v3/mods-userguide-elements.html#accesscondition
*/
if (!file_content_exists($olef_file,"accessCondition",true,true))
{
    if ($arrContentDetails['content_ipr']!="")
    {
        // LOAD OLEF TO DOM
        $domDoc   = new DOMDocument();
        @$domDoc->load($olef_file);

        // ADD NODE     mods:accessCondition to bibliographicInformation
        $node2 = $domDoc->createElement("mods:accessCondition",$arrContentDetails['content_ipr']);
        $node2->setAttribute("xmlns:mods",   "http://www.loc.gov/mods/v3");
        $node2->setAttribute("type",         "use and reproduction");

        $bis = $domDoc->getElementsByTagName("bibliographicInformation");

        foreach($bis as $bi) {
         $element = $bi;
         $newnode = $element->appendChild($node2);
        }

        // SPEICHERN MODIFIZIERTEN OLEF
        if ($domDoc->save($olef_file)>0) echo "Missing IPR Information added ... ok\n";
        else                             { echo _ERR." IPR Information addition failed!\n"; $ingestReady = false; }
    }
    else 
    {
        echo _ERR."IPR Information missing in delivered Metadata! Please select 
            Intellectual Property Rights (IPR) and SAVE them!\n";
        $ingestReady = false;
    }
}



// ************************
// mods:recordContentSource
// ************************
// ADD TAG IF NOT EQUAL TO CURRENT LOGGED IN CONTENT PROVIDER UPPERCASE
// https://github.com/bhle/bhle/issues/365

$newNodeName = "recordContentSource";

$recordContentSource = strtoupper(trim($arrProvider['user_content_id']));
if ($recordContentSource=='') $recordContentSource = strtoupper(trim($arrProvider['user_name']));

// EXISTIERT RICHTIGER EINTRAG BEREITS?
if ((!file_content_exists($olef_file,$recordContentSource."</mods:".$newNodeName,true,true)))    
{
    // LOAD OLEF TO DOM
    $domDoc   = new DOMDocument();
    @$domDoc->load($olef_file);

    // 1. ETWAIGE VORH. FALSCHE EINTRAEGE LOESCHEN (FALSCH DA OBIGE IF BEDINGUNG NICHT ERFUELLT)
    // -----------------------------------------------------------------------------------------
    $docRoot = $domDoc->documentElement;
    
    $allElements = $domDoc->getElementsByTagName('*');      // RETURNS A NEW INSTANCE OF CLASS DOMNODELIST

    // NODES DES JEW. TEMPATES DURCHGEHEN UND BEARBEITEN
    foreach( $allElements as $curElement )
    {
         $nodeAttributes = $curElement->attributes;
         $nodeValue      = trim($curElement->nodeValue);
         $nodeName       = trim($curElement->nodeName);

         if ($nodeName=='olef:bibliographicInformation')    // IMMER MIT PREFIX !
         {
            $removeNode = null;
            $removeNode = @$docRoot->getElementsByTagName($newNodeName)->item(0);
            // var_dump($removeNode); echo_pre($removeNode);
            if (($removeNode!=null)&&($removeNode)&&($removeNode!='null'))
            @$curElement->removeChild($removeNode);    // VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN

            $removeNode = null;
            $removeNode = @$docRoot->getElementsByTagName('mods:'.$newNodeName)->item(0);
            if (($removeNode!=null)&&($removeNode)&&($removeNode!='null'))
            @$curElement->removeChild($removeNode);    // VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN
         }
    }
    
    // 2. ADD (NEW) RIGHT NODE     mods:recordContentSource to bibliographicInformation
    // --------------------------------------------------------------------------------
    $node3 = $domDoc->createElement("mods:".$newNodeName,  $recordContentSource);
    $node3->setAttribute("xmlns:mods",   "http://www.loc.gov/mods/v3");
    
    $bis = $domDoc->getElementsByTagName("bibliographicInformation");

    foreach($bis as $bi) {
        $element = $bi;
        $newnode = $element->appendChild($node3);
    }

    // SPEICHERN MODIFIZIERTEN OLEF
    if ($domDoc->save($olef_file)>0) echo "Missing Provider Information added ... ok\n";
    else                             echo _ERR." Provider Information addition failed!\n";
}



// *************************
// ADD OLEF NAMESPACE PREFIX
// *************************
// DEPRECATED: SOLUTION NOW IN <olef_pages.php>

/*
if (!file_content_exists($olef_file,"<olef:",true,true))
{
    $before = file_get_contents($olef_file);
    $after  = str_replace(
            array("</",     "<",     "<olef:/olef:","</olef:/olef:","</olef:mods:","<olef:mods:","<olef: ","</olef: ","</olef:olef:","<olef:olef:","</olef:dwc:","<olef:dwc:"),
            array("</olef:","<olef:","<olef:",      "</olef:",      "</mods:",     "<mods:",     "<olef:", "</olef:", "</olef:",     "<olef:"     ,"</dwc:",     "<dwc:"),
            $before);
    
    file_put_contents($olef_file,$after);
    
    unset($before); unset($after);
}
*/

echo "OLEF mods done.\n";

echo "\n\n</pre>\n";

?>
