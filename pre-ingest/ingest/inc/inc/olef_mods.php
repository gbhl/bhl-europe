<?php
// ********************************************
// ** FILE:    OLEF_MODS.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ** AUTHOR:  WOLFGANG KOLLER               **
// ********************************************
// OLEF MODIFICATIONS POSTPROCESSING

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
    if($objParentID!="") $node1b = $domDoc->createElement("olef:parentGUID",$objParentID);
 
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
        $node2 = $domDoc->createElementNS('http://www.loc.gov/mods/v3', 'mods:accessCondition', $arrContentDetails['content_ipr'] );
        $node2->setAttributeNS('http://www.loc.gov/mods/v3', 'type', 'use and reproduction' );

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

// Construct correct recordcontent source name
$recordContentSource = strtoupper(trim($arrProvider['user_content_id']));
if ($recordContentSource=='') $recordContentSource = strtoupper(trim($arrProvider['user_name']));

// Create DOMDocument from OLEF-File
$domDoc   = new DOMDocument();
$domDoc->load($olef_file);

// Find olef namespace URI
$olefNodes = $domDoc->getElementsByTagName( 'olef' );
if( $olefNodes->length <= 0 ) {
    echo _ERR . 'Not a valid OLEF input!\n';
}
else {
    // Use first node to find out the namespace-uri
    $olefNode = $olefNodes->item(0);
    $olefNS = $olefNode->namespaceURI;
    
    // Find bibliographicInformation
    $biNodes = $domDoc->getElementsByTagNameNS($olefNS, 'bibliographicInformation');
    if( $biNodes->length <= 0 ) {
        echo _ERR . 'No bibliographicInformation found - check your metadata!\n';
    }
    else {
        $biNode = $biNodes->item(0);
        
        // Try to find existing recordContentSource entry
        $bRcsEntryFound = false;
        $rcsNodes = $domDoc->getElementsByTagNameNS(_NAMESPACE_MODS, 'recordContentSource');
        for( $i = 0; $i < $rcsNodes->length; $i++ ) {
            // Check if node already contains correct entry
            if( strcasecmp($rcsNodes->item($i)->nodeValue, $recordContentSource) == 0 ) {
                $bRcsEntryFound = true;
                break;
            }
        }

        // If we did not find a valid entry, add it
        if( !$bRcsEntryFound ) {
            // Create missing recordContentSource
            $rcsNode = $domDoc->createElementNS(_NAMESPACE_MODS, 'recordContentSource', $recordContentSource );

            // Try to find recordInfo node
            $riNodes = $domDoc->getElementsByTagNameNS(_NAMESPACE_MODS, 'recordInfo');
            $riNode = null;
            // Attach recordContentSource node to first recordInfo node
            if( $riNodes->length > 0 ) {
                $riNode = $riNodes->item(0);
            }
            else {
                // Create new recordInfo node
                $riNode = $domDoc->createElementNS(_NAMESPACE_MODS, 'recordInfo' );
                $biNode->appendChild($riNode);
            }
            // Finally append recordContentSource node
            $riNode->appendChild($rcsNode);

            // Save modified OLEF xml
            $domDoc->save($olef_file);
        }
    }
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
