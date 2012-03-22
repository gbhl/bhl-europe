<?php
// ********************************************
// ** FILE:    OLEF_MODS.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.01.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// OLEF MODIFICATIONS

echo "<h3>Finishing OLEF Data</h3><pre>";


// ****************************************
//  ADD Intellectual Property Rights  (IPR) 
// ****************************************

/* $arrFind = array(
    'accessCondition<bibliographicInformation><mods:',
    '<bibliographicInformation>\n<mods:',
    '<bibliographicInformation>\n\n<mods:');
*/

// http://www.loc.gov/standards/mods/v3/mods-userguide-elements.html#accesscondition

if (!file_content_exists(_OLEF_FILE,"accessCondition",true,true))
{
    if ($arrContentDetails['content_ipr']!="")
    {
        // LOAD OLEF TO DOM
        $domDoc   = new DOMDocument();
        @$domDoc->load(_OLEF_FILE);

        // ADD NODE     mods:accessCondition to bibliographicInformation
        $node = $domDoc->createElement("mods:accessCondition",$arrContentDetails['content_ipr']);
        $node->setAttribute("xmlns:mods", "http://www.loc.gov/mods/v3");
        $node->setAttribute("type", "use and reproduction");

        $bis = $domDoc->getElementsByTagName("bibliographicInformation");

        foreach($bis as $bi) {
         $element = $bi;
         $newnode = $element->appendChild($node);
        }

        // SPEICHERN MODIFIZIERTEN OLEF
        if ($domDoc->save(_OLEF_FILE)>0) echo "Missing IPR Information added ... ok\n";
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

$recordContentSource = strtoupper(trim($arrProvider['user_content_id']));
if ($recordContentSource=='') $recordContentSource = strtoupper(trim($arrProvider['user_name']));

if ((!file_content_exists(_OLEF_FILE,"mods:recordContentSource",true,true))||
    (!file_content_exists(_OLEF_FILE,"mods:recordContentSource>".$recordContentSource,true,true)))
{
    // LOAD OLEF TO DOM
    $domDoc   = new DOMDocument();
    @$domDoc->load(_OLEF_FILE);

    // ADD NODE     mods:recordContentSource to bibliographicInformation
    $node = $domDoc->createElement("mods:recordContentSource",  $recordContentSource);

    $bis = $domDoc->getElementsByTagName("bibliographicInformation");

    foreach($bis as $bi) {
        $element = $bi;
        $newnode = $element->appendChild($node);
    }

    // SPEICHERN MODIFIZIERTEN OLEF
    if ($domDoc->save(_OLEF_FILE)>0) echo "Missing Provider Information added ... ok\n";
    else                             echo _ERR." Provider Information addition failed!\n";
}



// *************************
// ADD OLEF NAMESPACE PREFIX
// *************************
// DEPRECATED: SOLUTION NOW IN <olef_pages.php>

/*
if (!file_content_exists(_OLEF_FILE,"<olef:",true,true))
{
    $before = file_get_contents(_OLEF_FILE);
    $after  = str_replace(
            array("</",     "<",     "<olef:/olef:","</olef:/olef:","</olef:mods:","<olef:mods:","<olef: ","</olef: ","</olef:olef:","<olef:olef:","</olef:dwc:","<olef:dwc:"),
            array("</olef:","<olef:","<olef:",      "</olef:",      "</mods:",     "<mods:",     "<olef:", "</olef:", "</olef:",     "<olef:"     ,"</dwc:",     "<dwc:"),
            $before);
    
    file_put_contents(_OLEF_FILE,$after);
    
    unset($before); unset($after);
}
*/

echo "OLEF mods done.\n";

echo "\n\n</pre>\n";

?>
