<?php
// ********************************************
// ** FILE:    OLEF_PAGES.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    23.01.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// ENTSPRECHENDES PAGE OBJEKT IN OLEF MIT PAGE SEITEN (ALS NAMEN) UND TAXONS ERZEUGEN

echo "<h3>Finishing OLEF Pages</h3><pre>";



// ******************
// * PAGES + TAXONS *
// ******************

// PAGE - TAXON ARRAY AUFBAUEN
$arrTaxons = array();

// TAXONSFILES
$arrTaxonsF = getContentFiles($contentDir, 'single_suffix', true,_TAXON_EXT); 
$nTaxonsF   = count($arrTaxonsF);
$arrTaxonsF = sortShortFirst($arrTaxonsF);  // IMPORTANT PRE SORT

$pattern  = "<nameString>";
$patternE = "</nameString>";

for ($i=0;$i<$nTaxonsF;$i++)
{   
    // TAXON FILE LADEN & TAXONS HOLEN
    $arrTaxons[($i+1)] = "".str_ireplace(array($pattern,$patternE),"",
            implode(_TRENNER,file_get_content_filtered($arrTaxonsF[$i],array($pattern),"<!",true,true)));
    /*
     <results>
      <allNames>
      <entity>
      <nameString>Abies</nameString>
      <namebankID>2645028</namebankID>
      </entity>
     * 
     *
      </allNames>
      </results>
     */
}

reset($arrTaxons);

// echo_pre($arrTaxons);



// ****************************************************************
// ALLE PAGE OBJEKTE ERZEUGEN UND TAXONS HINZUFUEGEN FALLS MOEGLICH
// ****************************************************************

$arrTiffs = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
$nTiffs   = count($arrTiffs);
$arrTiffs = sortShortFirst($arrTiffs);  // IMPORTANT PRE SORT


// LOAD OLEF TO DOM
$domDoc   = new DOMDocument();
@$domDoc->load(_OLEF_FILE);

$docRoot = $domDoc->documentElement;

/* 
<level>monograph</level>
 * ...
<itemInformation>
    <files>
        <file>
            <reference type="path">onzeflorabeschri00oude_0231.tif</reference>
            <pages>
                <page>
                    <name>231</name>
                    <taxon>
                        <dwc:scientificName>Lentibulariaceae</dwc:scientificName>
                    </taxon>
                    <taxon>
                        <dwc:scientificName>Oleaceae</dwc:scientificName>
                    </taxon>
                </page>
            </pages>
        </file>
    </files>
</itemInformation>
</element>
</olef>
*/

// ZUSATZHIERARCHIE DIE EINZUBAUEN IST
$arrNewNodes = array('itemInformation','files','file','reference','pages','page','name','taxon');
$nNewNodes   = count($arrNewNodes);


$containerElement = $domDoc->getElementsByTagName('element')->item(0);


// CLEANUP FIRST IF PART TO ADD EXISTS
$dropNode = $docRoot->getElementsByTagName($arrNewNodes[0])->item(0);

// VOM PARENT WEG MUSS DAS CHILD GELOESCHT WERDEN!
if (($dropNode)&&($dropNode!=null))
$containerElement->removeChild($dropNode);



// ADD itemInformation
$containerElement = $containerElement->appendChild($domDoc->createElement($arrNewNodes[0])); 

// ADD files
$containerElement = $containerElement->appendChild($domDoc->createElement($arrNewNodes[1]));  


// ***********************************************************************
// SCHLEIFE UEBER ALLE TIFFS UND ALLE HINZUZUFUEGENDEN ATTRIBUTE PRO SEITE
// ***********************************************************************

for ($curTiff=0;$curTiff<$nTiffs;$curTiff++)
{
    // $domDoc->getElementsByTagName('files')->item(0);  // start element wieder auf file
    
    $curParent    = $containerElement;
    $arrPageInfos = getPageInfoFromFile($arrTiffs[$curTiff],$curTiff);
    
    for ($curNode=2;$curNode<$nNewNodes;$curNode++)             // von file bis taxon
    {
        $curNodeName  = $arrNewNodes[$curNode];
        
        if ($curNodeName!='taxon')                              // taxon verwaltet sich selbst
        $node = $domDoc->createElement($curNodeName,"\n");      // no value here
        
        switch($curNodeName)
        {
            case 'page':
             $node->setAttribute("pageType",$arrPageInfos[2]);
             $curParent = $curParent->appendChild($node);        // TYP DER PAGE GEM. FSG
            break;
        
            case 'reference':
             $node->setAttribute("type", "path");
             $node->nodeValue = $arrTiffs[$curTiff];          
             $curParent->appendChild($node);                     // WIRD NICHT ZU PARENTELEMENT
            break;
            
            case 'name':
              $pageInfos = $arrPageInfos[3];
              
              // SUPPORT FUER 4 SEITEN PRO FILE
              if ((array_key_exists(4, $arrPageInfos))&&($arrPageInfos[4]!=""))   
                      $pageInfos .= ",".$arrPageInfos[4];
              if ((array_key_exists(5, $arrPageInfos))&&($arrPageInfos[5]!=""))    
                      $pageInfos .= ",".$arrPageInfos[5];
              if ((array_key_exists(6, $arrPageInfos))&&($arrPageInfos[6]!=""))    
                      $pageInfos .= ",".$arrPageInfos[6];
                                    
              $node->nodeValue = $pageInfos;
              $curParent->appendChild($node);                    // WIRD NICHT ZU PARENTELEMENT
            break;
        
            case 'taxon':

                // SCHLEIFE UEBER ALLE GEFUNDEN TAXONS AUF DIESER SEITE - 'dwc:scientificName'
                if (trim(str_replace(array(","," "),"",$arrTaxons[($curTiff+1)]))!="")
                {
                    $arrPageTaxons = explode(_TRENNER,$arrTaxons[($curTiff+1)]);
                    $nPageTaxons   = count($arrPageTaxons);
                    
                    for ($i=0;$i<$nPageTaxons;$i++)
                    {
                        $curTaxon = trim($arrPageTaxons[$i]);
                        
                        if ($curTaxon!="")
                        {
                            $node = $domDoc->createElement($curNodeName,"\n");  // taxon node
                            $curTaxonNode = $curParent->appendChild($node);

                            $node = $domDoc->createElement("dwc:scientificName",$curTaxon);  // taxon string
                            $node->setAttribute("xmlns:dwc", "http://rs.tdwg.org/dwc/terms/");

                            $curTaxonNode->appendChild($node);
                        }
                    }
                }
                                                                    // da letzter newNode zurueck zum element container
                break;
            
            default:
              $curParent = $curParent->appendChild($node);          // WIRD ZU NEUEM PARENT NODE
        }

       unset($node);

    }
}


// SPEICHERN MODIFIZIERTEN OLEF
if ($domDoc->save(_OLEF_FILE)>0) echo "OLEF saved ..... ok\n";
else                             echo _ERR." OLEF pages could not be written!\n";


echo "OLEF pages generated.\n";

echo "\n\n</pre>\n";



?>
