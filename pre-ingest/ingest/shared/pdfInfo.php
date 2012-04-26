<?php
/**
 * Class wrapping around a PDF for analyzing the properties of it using pdftk
 *
 * @author Wolfgang Koller
 * @since 2012-04-26
 */
class pdfInfo {
    private $m_pdftkInfo = array();
    private $m_numPages = -1;
    private $m_structure = null;
    
    /**
     * Prevent direct instantiation 
     */
    private function __construct( $p_pdftkOutput ) {
        // Read PDF-TK output and put it into a structured array
        foreach( $p_pdftkOutput as $line ) {
            $lineParts = explode(':', $line);
            if( count($lineParts) < 2 ) continue;
            
            $this->m_pdftkInfo[] = array( trim($lineParts[0]), trim($lineParts[1]) );
        }
    }

    /**
     * Return the number of pages for this pdf-object
     * @return int Number of pages 
     */
    public function getNumPages() {
        // Check if we already found the page-count
        if( $this->m_numPages >= 0 ) return $this->m_numPages;
        
        // Find NumerOfPages in pdftk-output
        $this->m_numPages = 0;
        foreach( $this->m_pdftkInfo as $info ) {
            if( $info[0] == 'NumberOfPages' ) {
                $this->m_numPages = $info[1];
                break;
            }
        }
        
        // Return number of pages
        return $this->m_numPages;
    }
    
    /**
     * Returns the structure of this PDF
     * @return boolean|array False on error, else an array containing the structure 
     */
    public function getStructure() {
        // Check if we already parsed the structure
        if( $this->m_structure != null ) return $this->m_structure;
        
        // Prepare structure variables
        $this->m_structure = array();
        $lastStructure = null;
        $currStructure = null;
        // Start parsing the structure
        for( $j = 0; $j < count($this->m_pdftkInfo); $j++ ) {
            // Receive current info/value pair
            $info = $this->m_pdftkInfo[$j][0];
            $value = $this->m_pdftkInfo[$j][1];

            // Check if a new section is starting, or this is the last info-line
            if( $info == 'PageLabelNewIndex' ) {
                // Structure is complete and new one is starting
                $lastStructure = $currStructure;
                $currStructure = array();
                
                // Check if we have a valid last entry
                if( $lastStructure != null && isset($lastStructure['PageLabelNewIndex']) ) {
                    // Finally merge the partial structure into the complete pdf-Structure
                    $this->m_structure = $this->m_structure + $this->buildStructurePart($lastStructure, $value - $lastStructure['PageLabelNewIndex']);
                }
            }
            
            // Remember info in current structure
            $currStructure[$info] = $value;
        }
        
        // Don't forget the last structure info block
        if($currStructure != null) {
            // Integrate final structure information
            $this->m_structure = $this->m_structure + $this->buildStructurePart($currStructure, $this->getNumPages() - $currStructure['PageLabelNewIndex'] + 1);
        }

        // Check if we were able to analyze the structure
        if( count($this->m_structure) <= 0 ) {
            $this->m_structure = false;
        }

        // Return PDF-Structure
        return $this->m_structure;
    }
    
    /**
     * Build a structured array based on PDF-structure information
     * @param array $p_structure Structure-Info from PDF-TK
     * @param int $p_pageCount Number of pages this structure is valid for
     * @return array Structured information
     */
    private function buildStructurePart( $p_structure, $p_pageCount ) {
        // Build structure information based on passed in info
        $pdfStructurePart = array();
        // Check the numbering style
        switch($p_structure['PageLabelNumStyle']) {
            case 'NoNumber':
                // Fill all pages in range using the static pagelabelprefix
                $pdfStructurePart = array_fill($p_structure['PageLabelNewIndex'], $p_pageCount, $p_structure['PageLabelPrefix']);
                break;
            case 'LowercaseLetters':
                break;
            case 'UppercaseLetters':
                break;
            case 'LowercaseRomanNumerals':
                break;
            case 'UppercaseRomanNumerals':
                break;
            case 'DecimalArabicNumerals':
                // Fill in the arabic numbers
                for( $i = 0; $i < $p_pageCount; $i++ ) {
                    $pdfStructurePart[$p_structure['PageLabelNewIndex'] + $i] = $p_structure['PageLabelStart'] + $i;
                }
                break;
            default:
                break;
        }
        
        // Return the structured array
        return $pdfStructurePart;
    }

    /**
     * Static access function for receiving an object of type pdfInfo
     * @param string $p_pdfName Full path to PDF
     * @return boolean|object Returns false on error, pdfInfo instance on success
     */
    public static function read( $p_pdfName ) {
        // Check if pdf exists
        if( !file_exists($p_pdfName) ) {
            return false;
        }
        
        // Prepare pdftk command
        $output = array();
        $return_val = -1;
        $pdftk_cmd = _PDFTK . ' ' . escapeshellarg($p_pdfName) . ' ' . _PDFTK_DATA;
        
        // Run pdftk command
        exec($pdftk_cmd, $output, $return_val);
        
        // Check if we got some output
        if( $return_val != 0 ) return false;
        
        // Now instantiate a pdfInfo object and return it
        return (new pdfInfo( $output ));
    }
}
