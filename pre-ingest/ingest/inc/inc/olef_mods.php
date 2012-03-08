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
    $ipr = abfrage("select content_ipr from content where content_id=".$content_id);
    
    if ($ipr!="")
    {
        // LOAD OLEF TO DOM
        $domDoc   = new DOMDocument();
        @$domDoc->load(_OLEF_FILE);

        // ADD NODE     mods:accessCondition to bibliographicInformation

        $node = $domDoc->createElement("mods:accessCondition",$ipr);

        $node->setAttribute("xmlns:mods", "http://www.loc.gov/mods/v3");
        $node->setAttribute("type", "use and reproduction");

        $bis = $domDoc->getElementsByTagName("bibliographicInformation");

        foreach($bis as $bi)
        {
         $element = $bi;
         $newnode = $element->appendChild($node);
        }

        // SPEICHERN MODIFIZIERTEN OLEF
        if ($domDoc->save(_OLEF_FILE)>0) echo "Missing IPR Information added ... ok\n";
        else                             echo _ERR." IPR Information failed!\n";
    }
    else 
    {
        echo _ERR."IPR Information missing in delivered Metadata so please enter 
            Intellectual Property Rights (IPR) information!\n";
        $ingestReady = false;
    }
}
// !!! FALLS NICHT IN ARRAY VORKOMMEN DEN VORH. NODE UEBERSCHREIBEN MIT CONTENT_IPR DB DATA



// *************************
// ADD OLEF NAMESPACE PREFIX
// *************************
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
