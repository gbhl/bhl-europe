<?php
/**
 * Search for a given name and return the bibliography for it
 * @param string $name Name to search for
 * @return object 
 */
function _bhle_webservice_searchForName($name) {
    $solr = apachesolr_get_solr(variable_get('bhle_search_solr_core'));
    $query = 'olef_scientific_name:' . bhle_search_solr_escape($name);
    
    try {
        $responseObj = $solr->search($query, array());
        
        return (object)array(
            'numFound' => $responseObj->response->numFound,
            'docs' => $responseObj->response->docs
        );
    }
    catch( Exception $e ) {
        return (object)array( 'error' => $result = $e->getMessage());
    }
}
