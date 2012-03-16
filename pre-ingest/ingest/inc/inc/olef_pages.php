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
$arrTaxonsF = sortPageFiles($arrTaxonsF);  // IMPORTANT PRE SORT

$pattern  = "<nameString>";
$patternE = "</nameString><namebankID>";    // ONLY TAXONS WITH NAMEBANKID

for ($i=0;$i<$nTaxonsF;$i++)
{   
    // TAXON FILE LADEN & TAXONS HOLEN
    $before = file_get_contents($arrTaxonsF[$i]);
    
    // LEERE NAMEBANKIDS WEG
    $after = str_ireplace(
            array("\t","\r",
                "<namebankID></namebankID>",
                "<namebankID>\n</namebankID>","<namebankID>\n\n</namebankID>",
                "<namebankID>\n </namebankID>","<namebankID>\n\n </namebankID>",
                "<namebankID>\n  </namebankID>","<namebankID>\n\n  </namebankID>"),
            "",$before);
    
    // NAMEBANK ID ELEMENTS IN GLEICHE ZEILE BRINGEN WIE nameString
    $after = str_ireplace(
            array("</nameString>\n<namebankID>","</nameString>\n\n<namebankID>",
                "</nameString>\n <namebankID>","</nameString>\n\n <namebankID>",
                "</nameString>\n  <namebankID>","</nameString>\n\n  <namebankID>"),
            $patternE."\n",
            $after);

    // MEHRFACHE LEERZEILEN WEG
    $after = str_ireplace(array("\n\n\n\n","\n\n\n","\n\n"),"\n",$after);

    file_put_contents($arrTaxonsF[$i], $after);

    
    // HOLEN DER TAXONS AUS AUFBEREITUNG MIT FILTER (NUR JENE MIT NAMEBANKID)
    $arrLines = file_get_content_filtered($arrTaxonsF[$i],array($patternE),"<!",true,true);
    $nLines   = count($arrLines);
    
    for ($j=0;$j<$nLines;$j++)
    {
        $arrLines[$j] = substr($arrLines[$j],0,stripos($arrLines[$j],"</nameString>"));
    }
    
    reset($arrLines);
            
    $arrTaxons[($i+1)] = "".str_ireplace(array($pattern,$patternE,"</nameString>"),"",
            implode(_TRENNER,$arrLines));

    unset($arrLines);
    unset($before);
    unset($after);
    
    /*
     <results>
      <allNames>
     * 
     * 
  </entity>
  <entity>
  <nameString>Oleaceae</nameString>
  <namebankID>459706</namebankID>
  </entity>
  <entity>
  <nameString>Orchidales</nameString>

  <namebankID>467838</namebankID>
  </entity>
  <entity>
  <nameString>Oxalidaceae</nameString>
  <namebankID>456438</namebankID>
  </entity>
  <entity>
     * 
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
$arrTiffs = sortPageFiles($arrTiffs);  // IMPORTANT PRE SORT


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
$containerElement = $containerElement->appendChild($domDoc->createElement("olef:".$arrNewNodes[0])); 

// ADD files
$containerElement = $containerElement->appendChild($domDoc->createElement("olef:".$arrNewNodes[1]));  


// ***********************************************************************
// SCHLEIFE UEBER ALLE TIFFS UND ALLE HINZUZUFUEGENDEN ATTRIBUTE PRO SEITE
// ***********************************************************************

for ($curTiff=0;$curTiff<$nTiffs;$curTiff++)
{
    // $domDoc->getElementsByTagName('files')->item(0);         // START ELEMENT WIEDER AUF FILE
    
    $curParent    = $containerElement;
    $arrPageInfos = getPageInfoFromFile($arrTiffs[$curTiff],$curTiff+1);
    
    for ($curNode=2;$curNode<$nNewNodes;$curNode++)             // VON FILE BIS TAXON
    {
        $curNodeName  = $arrNewNodes[$curNode];
        
        if ($curNodeName!='taxon')                                      // TAXON VERWALTET SICH SELBST
        $node = $domDoc->createElement("olef:".$curNodeName,"\n");      // NO VALUE HERE
        
        switch($curNodeName)
        {
            case 'reference':
             $node->setAttribute("type", "path");
             $node->nodeValue = $arrTiffs[$curTiff];          
             $curParent->appendChild($node);                     // WIRD NICHT ZU PARENTELEMENT
            break;
        
            case 'page':                                         // TYP DER PAGE GEM. FSG
             if ((is_array($arrPageInfos))&&($arrPageInfos[2]!=""))  
                $node->setAttribute("pageType",$arrPageInfos[2]);
             else                       
                $node->setAttribute("pageType",_DEFAULT_PAGETYPE);
                
             $curParent = $curParent->appendChild($node);
            break;

            case 'name':

              // KEIN NAME NODE WENN PAGE LEER
              // MEHRERE NAME NODES WENN MEHRERE PAGES
              // !!! MEHRERE PAGE ELEMENT STATT MEHRERE NAME
                
              for ($i=3;$i<7;$i++)      // SUPPORT FUER 4 SEITEN PRO FILE
              {
                  if ((is_array($arrPageInfos))&&(array_key_exists($i, $arrPageInfos))&&($arrPageInfos[$i]!=""))
                  {
                      if ($i>3) $node  = $domDoc->createElement("olef:".$curNodeName,"\n");

                      $node->nodeValue = $arrPageInfos[$i];
                      $curParent->appendChild($node);          // WIRD NICHT ZU PARENTELEMENT
                  }
              }
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
                            $node = $domDoc->createElement("olef:".$curNodeName,"\n");  // taxon node
                            $curTaxonNode = $curParent->appendChild($node);

                            $node = $domDoc->createElement("dwc:scientificName",$curTaxon);  // taxon string
                            $node->setAttribute("xmlns:dwc", "http://rs.tdwg.org/dwc/terms/");

                            $curTaxonNode->appendChild($node);
                        }
                    }
                }
                         // DA LETZTER NEWNODE ZURUECK ZUM ELEMENT CONTAINER
                break;
            
            default:
              $curParent = $curParent->appendChild($node);          // WIRD ZU NEUEM PARENT NODE
        }

       unset($node);

    }
}



// SPEICHERN MODIFIZIERTEN OLEF

if ($domDoc->save(_OLEF_FILE)>0) echo "OLEF saved, OLEF pages generated ... ok\n";
else                             echo _ERR." OLEF page elements could not be written!\n";


echo "\n\n</pre>\n";



?>
