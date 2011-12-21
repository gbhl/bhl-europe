<?php

/*
  GET GUID
  INVOKED BY GET_METADATA.PHP
 * 
 * 
  Based on Noid from CPAN by Kunze.

  Used following schema to setup NOID:
  noid dbcreate a0.reeeeee - 10706

  Generating around 550 million ids with prefix a0.

  Registered Handle.net handle 10706 for BHL-Europe.

  http://search.cpan.org/dist/Noid/noid#SYNOPSIS
 * 
 * file:///G:/dev/nhm/bhl/ingest/_docs/noid.htm
 * 
 * http://linux.softpedia.com/get/Programming/Libraries/Noid-27141.shtml
 * https://wiki.ucop.edu/display/Curation/NOID
 * http://search.cpan.org/~jak/Noid-0.424/
 * 
 * http://www.cdlib.org/services/uc3/curation/
 * 
 * SEE DOCUMENTS IN ../../_DOCS/
 * 
 * evtl schreibt der minter in ein NOID verzeichnis
*/

$outputFile = $destDir."guid_checkout.txt";


// GET A NEW NOID

$cmd = _NOID." > '".$outputFile."'";

$output = array();

$return_var = "";

$rLine = exec($myCmd, $output, $return_var);

if ($return_var == 0) echo $myCmd . "\nMinting ok!            " . str_replace("()", "", "(" . $rLine . ")") . "\n";
else                  echo "Error in noid minting so using own algorithm; " . $rLine . "\n";

if (file_exists($outputFile))
{
    $cGUID = file_get_contents ($filename);
    
    echo "Minted ID was: ".$cGUID." \n";
    
    @unlink($outputFile);
}
else
{   // a0.reeeeee - 10706  - eigener minter erzeugt max 12 stellige guids mithilfe von time
    $cGUID = _NOID_PREFIX.substr('00000000000000000000'.time(),-20).$content_id;
    
    echo "Minted: ".$cGUID." \n";
    // noid example
    // noid dbcreate f5.reedeedk long 13030 cdlib.org oac/cmp
    // namespace ranging from a low of 13030/f5000000s to a high of 13030/f5zz9zz94
    
}

?>
