<?php

require_once('fedora_api.raw.inc');
require_once('fedora_api.item.inc');
require_once('FedoraRestException.php');
/**
 * Wrapper for Fedora Commons API functions that don't operate on existing objects.
 */
class FedoraClient {
  
  public $connection;
  protected $_populated = FALSE;
  function __construct($connection = NULL) {
    if (empty($connection)) {
      $connection = new FedoraConnection();
    }
    
    $this->connection = $connection;
    $this->api = new FedoraAPI($this->connection);
  }
  
  /**
   * Ingest a new object, with either an FOXML file, an FOXML string or neither for a blank object.
   * @param string $foxml_file
   * @param string $foxml_string
   * @param array $options
   * @return FedoraItem the newly-created item.
   */
  function ingest($foxml_file = NULL, $foxml_string = NULL, 
          $options = array('pid' => NULL, 'new' => NULL, 'label' => NULL, 'format' => NULL, 'encoding' => NULL,
                                                     'namespace' => NULL, 'ownerId' => NULL, 'logMessage' => NULL,)) {
    
    $response = $this->api->ingest($foxml_file, $foxml_string, !empty($options['pid']) ? $options['pid'] : NULL, 
            !empty($options['new']) ? $options['new'] : NULL, !empty($options['label']) ? $options['label'] : NULL,
            !empty($options['format']) ? $options['format'] : NULL, !empty($options['encoding']) ? $options['encoding'] : NULL,
            !empty($options['namespace']) ? $options['namespace'] : NULL, !empty($options['ownerId']) ? $options['ownerId'] : NULL, 
            !empty($options['logMessage']) ? $options['logMessage'] : NULL);
    
    if ($response->code != 201) {
      throw new FedoraRestException($response->error);
    }
    return new FedoraItem($response->data);
    
  }
  
  /**
   * Wrapper for Fedora's findObjects API function.
   * @param string $terms Search terms, e.g. "changeme:*"
   * @param string $query Detailed search query, e.g. "pid=demo:12"
   * @param int $max_results
   * @param array $display_fields 
   * @return array The result set with the specified fields populated
   */
  function findObjects($terms = NULL, $query = NULL, $max_results = 50, 
          $display_fields = array('pid','label', 'state', 'ownerId', 'cDate', 'mDate', 'dcmDate', 
              'title', 'creator', 'subject', 'description', 'publisher', 'contributor', 'date', 'type', 
              'format', 'identifier', 'source', 'language', 'relation', 'coverage', 'rights')) {

    // pid needs to be part of the displayFields so we can construct the results array.  
    if (!in_array('pid', $display_fields)) {
      $display_fields[] = 'pid';
    }
    $resp = $this->api->findObjects($terms, $query, $max_results, 'xml', $display_fields);
    if ($resp->code != 200) {
      throw new FedoraRestException($resp->error);
    }
    $results = new SimpleXMLElement($resp->data);
    $resultset = array();
    foreach ($results->resultList->objectFields as $obj) {
      $pid = (string) $obj->pid;
      $resultset[$pid] = array();

      foreach ($obj->children() as $field) {
        $field_name = $field->getName();
        if ($field_name != 'pid') {
          $resultset[$pid][$field_name] = (string) $field;
        }
      }
    }
    return $resultset;
  }
  
  /**
   * Simple wrapper for findObjects that returns TRUE if an object with the 
   * specified pid exists in the repository.
   * @param string $pid
   * @return boolean
   */
  public function objectExists($pid) {
    $result = $this->findObjects(NULL, "pid=$pid");
    return count($result) == 1;
  }
}