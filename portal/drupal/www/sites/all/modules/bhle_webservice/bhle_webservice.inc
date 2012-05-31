<?php
/**
 * Search for a given name and return the bibliography for it
 * @param string $name Name to search for
 * @return object 
 */
function _bhle_webservice_searchForName($name, $format = 'mods') {
    $solr = apachesolr_get_solr(variable_get('bhle_search_solr_core'));
    $query = 'olef_scientific_name:' . bhle_search_solr_escape($name);
    $format = strtolower($format);
    
    try {
        $responseObj = $solr->search($query, array());
        
        $docs = array();
        
        // Cycle through found documents and prepare response for them
        foreach( $responseObj->response->docs as $doc ) {
            $PID_components =  explode( '-', $doc->PID);
            
            // Check for valid PID
            if( count($PID_components) <= 1 ) continue;
            // Extractr "pure" PID
            $PID = $PID_components[1];

            // Create correct URL and fetch it
            $url = variable_get('bhle_environment', BHLE_ENVIRONMENT) . '/' . str_replace('%bid', $PID, BHLE_MODS_URI);
            $mods_content = drupal_http_request($url)->data;
            
            // Store mods-content as result
            $docs[] = $mods_content;
        }
        
        return (object)array(
            'numFound' => $responseObj->response->numFound,
            'docs' => $responseObj->response->docs
        );
    }
    catch( Exception $e ) {
        return (object)array( 'error' => $result = $e->getMessage());
    }
}
