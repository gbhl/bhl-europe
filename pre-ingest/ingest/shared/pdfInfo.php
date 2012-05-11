<?php
/**
 * Class wrapping around a PDF for analyzing the properties of it using pdftk
 *
 * @author Wolfgang Koller
 * @since 2012-04-26
 */
class pdfInfo {
    private $m_pdftkInfo = null;
    private $m_pdfFontsInfo = null;
    private $m_numPages = -1;
    private $m_structure = null;
    private $m_bHasText = null;
    private $m_pdfName = "";

    /**
     * Used from static constructor function
     * @param string $p_pdfName Name of PDF file
     */
    private function __construct( $p_pdfName ) {
        $this->m_pdfName = $p_pdfName;

        // Check if we have a cached information for this PDF
        $result = mysql_select( "SELECT * FROM `pdf_info` WHERE `file_name` = '" . mysql_escape_string($this->m_pdfName) . "'" );
        if( mysql_num_rows($result) > 0 ) {
            $row = mysql_fetch_array($result, MYSQL_ASSOC);
            
            $this->m_pdfFontsInfo = unserialize($row['pdfFonts_info']);
            $this->m_pdftkInfo = unserialize($row['pdftk_info']);
        }
        // if not run our analyzing tools to fetch all the info
        else {
            $this->runPDFfonts();
            $this->runPDFtk();
            
            // Store the tool output in the database to cache it
            mysql_select( "
                INSERT INTO `pdf_info`
                ( `file_name`, `pdfFonts_info`, `pdftk_info` )
                values
                (
                '" . mysql_escape_string($this->m_pdfName) . "', 
                '" . mysql_escape_string(serialize($this->m_pdfFontsInfo)) . "',
                '" . mysql_escape_string(serialize($this->m_pdftkInfo)) . "'
                )"  );
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
     * Returns true if the PDF has embedded text
     * @return bool True if PDF has embedded text, false otherwise 
     */
    public function hasText() {
        // Check if we already know about the text
        if( $this->m_bHasText != null ) return $this->m_bHasText;

        // Check if we have at least one font (pdffonts outputs two header lines)
        if( count($this->m_pdfFontsInfo) > 2 ) {
            $this->m_bHasText = true;
        }
        else {
            $this->m_bHasText = false;
        }
        
        return $this->m_bHasText;
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
        
        // Now instantiate a pdfInfo object and return it
        return (new pdfInfo($p_pdfName));
    }
    
    /**
     * Run pdftk and extract the information from it
     * @return boolean true on success, false on error
     */
    private function runPDFtk() {
        // Check if we already extracted the info
        if( $this->m_pdftkInfo != null ) return true;
        $this->m_pdftkInfo = array();
        
        // Prepare pdftk command
        $pdftkOutput = array();
        $return_val = -1;
        $pdftk_cmd = _PDFTK . ' ' . escapeshellarg($this->m_pdfName) . ' ' . _PDFTK_DATA;
        // Run pdftk command
        exec($pdftk_cmd, $pdftkOutput, $return_val);
        // Check if command executed successfully
        if( $return_val != 0 ) return false;

        // Read PDF-TK output and put it into a structured array
        foreach( $pdftkOutput as $line ) {
            $lineParts = explode(':', $line);
            if( count($lineParts) < 2 ) continue;
            
            $this->m_pdftkInfo[] = array( trim($lineParts[0]), trim($lineParts[1]) );
        }
        
        return true;
    }
    
    /**
     * Run pdffonts and extract the information from it
     * @return boolean true on success, false on error
     */
    private function runPDFfonts() {
        // Check if we already extracted the info
        if( $this->m_pdfFontsInfo != null ) return true;
        $this->m_pdfFontsInfo = array();
        
        // Run pdffonts command to check if PDF has embedded text
        $return_val = -1;
        $pdffonts_cmd = _PDFFONTS . ' ' . escapeshellarg($this->m_pdfName);
        // Run pdftk command
        exec($pdffonts_cmd, $this->m_pdfFontsInfo, $return_val);
        // Check if we got some output
        if( $return_val != 0 ) return false;
                
        return true;
    }
}
