<?php
/**
* config
*/
$contentDir = "/home/wkoller/Desktop/fr-mnhn/main/";
$destinationDir = "/home/wkoller/Desktop/fr-mnhn/prepared/";
$structureDir = "/home/wkoller/Desktop/fr-mnhn/structure/";
$smtJar = 'smt-cli/smt-cli.jar';
$javaBin = '/usr/bin/java';

define('_MARC_NAMESPACE', 'http://www.loc.gov/MARC21/slim');

/**
 * program start
 */
require("excel_reader2.php");
require("AutoDOMDocument.php");

// Find all entries in the content dir
$dirHandle = dir($contentDir);
if( $dirHandle ) {
    while( ($entry = $dirHandle->read()) ) {
        if( $entry == "." || $entry == ".." ) continue;

        // Construct path and check for directory
        $path = $contentDir . $entry . '/';
        echo $path . "\n";
        if( is_dir($path) ) {
            // create output directory & handle content
            //mkdir( $destinationDir . $entry );
            handleEntry( $entry, $path );
        }
    }
}

/**
 * Handle an entry as serial
 * @param string $p_entry path to top-level directory
 */
function handleEntry( $p_name, $p_path ) {
    global $structureDir;
    global $destinationDir;
    global $javaBin;
    global $smtJar;
    
    // create structure dir path
    $currStructureDir = $structureDir . $p_name . '/';
    
    // Check if a structure entry exists
    if( !is_dir($currStructureDir) ) {
        echo "ERROR: No structure found for '$p_name'\n";
        return;
    }
    
    // setup file names
    $md_xmlFileName = $p_name . '_marc21.xml';
    
    // generate main processing dir
    $seriesDir = $destinationDir . $p_name . '/';
    $md_isoFile = $p_path . $p_name . '.iso';
    $md_xmlFile = $seriesDir . $p_name . '.xml';
    $md_seriesFile = $seriesDir . $md_xmlFileName;
    
    //check if series dir already exists, if yes skip it
    if(file_exists($seriesDir) ) {
        echo "WARNING: '$seriesDir' already exists, skipping processing!\n";
        return;
    }
    
    // create series dir and start processing
    mkdir($seriesDir);
    $md_seriesUpdated = false;
    
    // convert metadata
    system( $javaBin . ' -jar ' . $smtJar . ' -m c -cm 1 -if ' . $md_isoFile . ' -of ' . $md_xmlFile );
    system( $javaBin . ' -jar ' . $smtJar . ' -m x -if ' . $md_xmlFile . ' -of ' . $md_seriesFile . ' -map UNIMARCtoMARC21.xsl' );
    unlink($md_xmlFile);
    
    // Cycle through each entry and handle them
    $contentHandle = dir($p_path);
    while( $subEntry = $contentHandle->read() ) {
        if( $subEntry == "." || $subEntry == ".." ) continue;

        // Generate full sub-path and check if it is a directory
        $subPath = $p_path . $subEntry . '/';
        if( !is_dir($subPath) ) continue;
        echo "Found sub-content '$subPath'\n";
        
        // generate section dir name
        $subEntryComponents = explode('_', $subEntry);
        // remove last element
        unset($subEntryComponents[count($subEntryComponents) - 1]);
        // glue back together
        $sectionDir = $seriesDir . implode('_', $subEntryComponents) . '/';
        
        // generate volume dir & file name
        $volumeDir = $sectionDir . $subEntry . '/';
        $md_volumeFile = $volumeDir . $md_xmlFileName;
        
        // create directory in destination path
        $destinationPath = $volumeDir;
        mkdir( $destinationPath, 0777, true );
        
        // Construct structure file name
        $structureFile = $currStructureDir . $subEntry . '.xls';
        
        // Check if a structure file exists
        if(!file_exists($structureFile)) {
            echo "ERROR: No structural information found!\n";
            continue;
        }
        
        // Read structural excel file
        $fileInfo = array();
        $excelReader = new Spreadsheet_Excel_Reader($structureFile);
        $rowCount = $excelReader->rowcount();
        $sequence = 0;
        for( $row = 1; $row <= $rowCount; $row++ ) {
            $type = utf8_encode($excelReader->val($row, 1));
            $filename = utf8_encode($excelReader->val($row, 16));
            $element_type = utf8_encode($excelReader->val($row, 17));
            $title = utf8_encode($excelReader->val($row, 21));
            $authors = utf8_encode($excelReader->val($row, 22));
            $vol_part = utf8_encode($excelReader->val($row, 7));
            $num_part = utf8_encode($excelReader->val($row, 8));
            $notebib = utf8_encode($excelReader->val($row, 6));
            $edit_date = utf8_encode($excelReader->val($row, 12));
            
            // extract identifier from filename
            $pathInfo = pathinfo($filename);
            $identifier = $pathInfo['filename'];

            // handle different type entries
            switch( $type ) {
                // element numbering
                case "E":
                    $sequence++;
                    // check for extra info
                    $element_type_full = $element_type;
                    $element_type_parts = explode(' ', $element_type);
                    if( count($element_type_parts) > 1 ) {
                        // pages have a number suffix
                        $element_type = $element_type_parts[0];
                    }
                    echo "'$identifier' has type '$element_type'\n";
                    
                    // construct basic filename from identifier
                    $filename = str_replace('_', '-', $identifier);
                    $filename .= '_' . sprintf('%04d', $sequence);
                    $basename = $filename;
                    
                    switch( $element_type ) {
                        // cover
                        case 'couverture':
                        case 'coueverture':
                        case 'couveture':
                            $filename .= '_COVER';
                            break;
                        // page
                        case 'page':
                            $filename .= '_PAGE';
                            if( isset($element_type_parts[1]) ) {
                                $filename .= '_' . $element_type_parts[1];
                            }
                            break;
                        // blank
                        case 'non':
                            // non might have sub-types due to blank in name
                            switch( $element_type_full ) {
                                case 'non numérotée':
                                    $filename .= '_BLANK';
                                    break;
                                default:
                                    echo "ERROR: Unknown element-type '$element_type_full'\n";
                                    break;
                            
                            }
                            break;
                        // plate
                        case 'planche':
                            $filename .= '_PLATE';
                            if( isset($element_type_parts[1]) ) {
                                $filename .= '_' . $element_type_parts[1];
                            }
                            break;
                        default:
                            echo "ERROR: Unknown element-type '$element_type'\n";
                            break;
                    }
                    // Append final tif
                    $filename .= '.tif';
                    echo "Renaming to '$filename'\n";
                    
                    $fileInfo[$identifier] = array(
                        'filename' => $filename,
                        'basename' => $basename,
                        'structure' => false
                    );
                    
                    // copy & rename file to final version
                    copy($subPath . $subEntry . '_TIFF/' . $identifier . '.tif', $destinationPath . $filename);
                    
                    break;
                // table of contents
                case 'TOC':
                    if( empty($identifier) ) continue;
                    
                    echo "TOC: '$identifier' has type '$element_type'\n";
                    
                    $fileInfo[$identifier]['structure'] = true;
                    $fileInfo[$identifier]['title'] = $title;
                    $fileInfo[$identifier]['authors'] = $authors;
                    break;
                // section / volume information
                case 'S':
                    // check if we updated series before
                    if( !$md_seriesUpdated ) {
                        // update series metadata
                        $domSeries = new AutoDOMDocument(null, 'utf-8');
                        $domSeries->load($md_seriesFile);
                        // create XPath to search for title field
                        $domXPath = new DOMXPath($domSeries);
                        $titleFields = $domXPath->query("*/marc:datafield[@marc:tag='245']");
                        $titleField = $titleFields->item(0);
                        // add series info as remainder
                        $remainderField = $domSeries->appendChild($titleField, _MARC_NAMESPACE, 'subfield', $notebib);
                        $remainderField->setAttributeNS(_MARC_NAMESPACE, 'code', 'b');
                        // save back series metadata file
                        $domSeries->save($md_seriesFile);
                        
                        $md_seriesUpdated = true;
                    }
                    
                    // check section metadata
                    $md_sectionFile = $sectionDir . $md_xmlFileName;
                    if( !file_exists($md_sectionFile) ) {
                        // copy series metadata
                        copy($md_seriesFile, $md_sectionFile);
                        // load section metadata as DOMDocument
                        $domSection = new AutoDOMDocument(null, 'utf-8');
                        $domSection->load($md_sectionFile);
                        // create XPath to search for title field
                        $domXPath = new DOMXPath($domSection);
                        $titleFields = $domXPath->query("*/marc:datafield[@marc:tag='245']");
                        $titleField = $titleFields->item(0);
                        // create subfield & append it
                        $subfield = $domSection->appendChild($titleField, _MARC_NAMESPACE, 'subfield', $vol_part);
                        $subfield->setAttributeNS(_MARC_NAMESPACE, 'code', 'p');

                        // save updated document
                        $domSection->save($md_sectionFile);
                    }
                    // check volume metadata
                    if( !file_exists($md_volumeFile) ) {
                        // copy section metadata
                        copy($md_sectionFile, $md_volumeFile);
                        // load volume metadata as DOMDocument
                        $domVolume = new AutoDOMDocument(null, 'utf-8');
                        $domVolume->load($md_volumeFile);
                        // create XPath to search for title field
                        $domXPath = new DOMXPath($domVolume);
                        $titleFields = $domXPath->query("*/marc:datafield[@marc:tag='245']");
                        $titleField = $titleFields->item(0);
                        // create subfield & append it
                        $subfield = $domVolume->appendChild($titleField, _MARC_NAMESPACE, 'subfield', $num_part);
                        $subfield->setAttributeNS(_MARC_NAMESPACE, 'code', 'p');
                        
                        // find publication info & add date to it
                        $publicationFields = $domXPath->query("*/marc:datafield[@marc:tag='260']");
                        $publicationField = $publicationFields->item(0);
                        // create subfield & append it
                        $subfield = $domVolume->appendChild($publicationField, _MARC_NAMESPACE, 'subfield', $edit_date);
                        $subfield->setAttributeNS(_MARC_NAMESPACE, 'code', 'c');

                        // save updated document
                        $domVolume->save($md_volumeFile);
                    }
                    break;
            }
        }
        
        // move files into correct sub-folders
        $currDestinationPath = $destinationPath;
        foreach( $fileInfo as $identifier => $info ) {
            // check if we have to create a new sub-folder
            if( $info['structure'] ) {
                $currDestinationPath = $destinationPath . $info['basename'] . '/';
                mkdir($currDestinationPath);
                
                // create metadata for this article
                $md_articleFile = $currDestinationPath . $md_xmlFileName;
                // copy volume metadata
                copy($md_volumeFile, $md_articleFile);
                // load article metadata as DOMDocument
                $domArticle = new AutoDOMDocument(null, 'utf-8');
                $domArticle->load($md_articleFile);
                // create XPath to search for title field
                $domXPath = new DOMXPath($domArticle);
                $titleFields = $domXPath->query("*/marc:datafield[@marc:tag='245']");
                $titleField = $titleFields->item(0);
                $recordField = $titleField->parentNode;

                // find relevant title fields and copy them across to the host-entry tag
                $titleField_title = $domXPath->query("marc:subfield[@marc:code='a']", $titleField)->item(0);
                $titleField_remainders = $domXPath->query("marc:subfield[@marc:code='b']", $titleField);
                $titleField_parts = $domXPath->query("marc:subfield[@marc:code='p']", $titleField);
                // create new host entry field
                $hostEntry = $domArticle->appendChild($recordField, _MARC_NAMESPACE, 'datafield');
                $hostEntry->setAttributeNS(_MARC_NAMESPACE, 'tag', '773');

                // create title statement
                $hostEntry_title = $titleField_title->nodeValue;
                foreach( $titleField_remainders as $titleField_remainder ) {
                    $hostEntry_title .= ' ' . $titleField_remainder->nodeValue;
                }
                // append new information to host entry
                $hostEntry_title = $domArticle->appendChild($hostEntry, _MARC_NAMESPACE, 'subfield', $hostEntry_title);
                $hostEntry_title->setAttributeNS(_MARC_NAMESPACE, 'code', 't');
                $hostEntry_part = '';
                for( $l = 0; $l < $titleField_parts->length; $l++ ) {
                    $hostEntry_part .= ((empty($hostEntry_part)) ? '' : '; ');
                    $hostEntry_part .= $titleField_parts->item($l)->nodeValue;
                }
                $hostEntry_part = $domArticle->appendChild($hostEntry, _MARC_NAMESPACE, 'subfield', $hostEntry_part);
                $hostEntry_part->setAttributeNS(_MARC_NAMESPACE, 'code', 'g');
                
                // delete old title entries
                for( $l = 0; $l < $titleFields->length; $l++ ) {
                    $titleFields->item($l)->parentNode->removeChild($titleFields->item($l));
                }
                
                // create new title entry for article
                $titleEntry = $domArticle->appendChild($recordField, _MARC_NAMESPACE, 'datafield');
                $titleEntry->setAttributeNS(_MARC_NAMESPACE, 'tag', '245');
                $titleEntry_title = $domArticle->appendChild($titleEntry, _MARC_NAMESPACE, 'subfield', $info['title']);
                $titleEntry_title->setAttributeNS(_MARC_NAMESPACE, 'code', 'a');
                // create new author entry for article
                $authorEntry = $domArticle->appendChild($recordField, _MARC_NAMESPACE, 'datafield');
                $authorEntry->setAttributeNS(_MARC_NAMESPACE, 'tag', '100');
                $authorEntry_name = $domArticle->appendChild($authorEntry, _MARC_NAMESPACE, 'subfield', $info['authors']);
                $authorEntry_name->setAttributeNS(_MARC_NAMESPACE, 'code', 'a');
                
                // save updated document
                $domArticle->save($md_articleFile);
            }
            
            // move file to sub-folder
            if( $currDestinationPath != $destinationPath ) {
                rename($destinationPath . $info['filename'], $currDestinationPath . $info['filename']);
            }
        }
        
        //var_export($fileInfo);
    }
}
