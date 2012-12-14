<?php
/**
* config
*/
$contentDir = "/home/wkoller/Desktop/fr-mnhn/main/";
$destinationDir = "/home/wkoller/Desktop/fr-mnhn/prepared/";
$structureDir = "/home/wkoller/Desktop/fr-mnhn/structure/";

/**
 * program start 
 */
require("excel_reader2.php");

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
            mkdir( $destinationDir . $entry );
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
    
    // create structure dir path
    $currStructureDir = $structureDir . $p_name . '/';
    
    // Check if a structure entry exists
    if( !is_dir($currStructureDir) ) {
        echo "ERROR: No structure found for '$p_name'\n";
        return;
    }
    
    // Cycle through each entry and handle them
    $contentHandle = dir($p_path);
    while( $subEntry = $contentHandle->read() ) {
        if( $subEntry == "." || $subEntry == ".." ) continue;

        // Generate full sub-path and check if it is a directory
        $subPath = $p_path . $subEntry . '/';
        if( !is_dir($subPath) ) continue;
        echo "Found sub-content '$subPath'\n";
        
        // create directory in destination path
        $destinationPath = $destinationDir . $p_name . '/' . $subEntry . '/';
        mkdir( $destinationPath );
        
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
            $type = $excelReader->val($row, 1);
            $filename = $excelReader->val($row, 16);
            $element_type = $excelReader->val($row, 17);
            $title = $excelReader->val($row, 21);
            $authors = $excelReader->val($row, 22);
            
            // extract identifier from filename
            $pathInfo = pathinfo($filename);
            $identifier = $pathInfo['filename'];

            // handle different type entries
            switch( $type ) {
                // element numbering
                case "E":
                    $sequence++;
                    echo "'$identifier' has type '$element_type'\n";
                    // check for extra info
                    $element_type_parts = explode(' ', $element_type);
                    if( count($element_type_parts) > 1 ) {
                        $element_type = $element_type_parts[0];
                    }
                    
                    // construct basic filename from identifier
                    $filename = str_replace('_', '-', $identifier);
                    $filename .= '_' . sprintf('%04d', $sequence);
                    $basename = $filename;
                    
                    switch( $element_type ) {
                        // cover
                        case 'couverture':
                            $filename .= '_COVER';
                            break;
                        // page
                        case 'page':
                            $filename .= '_PAGE';
                            if( isset($element_type_parts[1]) ) {
                                $filename .= '_' . $element_type_parts[1];
                            }
                            break;
                        case 'non numérotée':
                            $filename .= '_BLANK';
                            break;
                        default:
                            echo "ERROR: Unknown element-type!";
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
            }
        }
        
        // move files into correct sub-folders
        $currDestinationPath = $destinationPath;
        foreach( $fileInfo as $identifier => $info ) {
            // check if we have to create a new sub-folder
            if( $info['structure'] ) {
                $currDestinationPath = $destinationPath . $info['basename'] . '/';
                mkdir($currDestinationPath);
            }
            
            // move file to sub-folder
            if( $currDestinationPath != $destinationPath ) {
                rename($destinationPath . $info['filename'], $currDestinationPath . $info['filename']);
            }
        }
        
        var_export($fileInfo);
    }
}
