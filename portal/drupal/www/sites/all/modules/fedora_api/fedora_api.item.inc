<?php

/**
 * An wrapper for Fedora's raw API calls to present Fedora items as manageable objects.
 */
require_once('PropertyObject.inc');

class FedoraItem extends PropertyObject {
  public $pid = NULL; // The $pid of the fedora object represented by an instance of this class.
  private $objectProfile = NULL;
  private $_datastreams = NULL;
  public $client = NULL;
  private static $instantiated_pids = array();
  
  
  /**
   * Create an object to represent an item in the Fedora repository.
   * Throws a SOAPException if the PID is not in the repository.
   *
   * @param string $pid
   * @param FedoraConnection $connection
   * @return FedoraItem
   */
  function __construct($pid, $client = NULL) {
    if ($client) {
      $this->client = $client;
    }
    else {
      // Make a default client using Drupal's variables.
      $this->client = new FedoraClient();
    }
    $this->pid = $pid;
    if (isset(FedoraItem::$instantiated_pids[$pid])) {
      $this->client =& FedoraItem::$instantiated_pids[$pid]->client;
      $this->objectProfile =& FedoraItem::$instantiated_pids[$pid]->objectProfile;
      $this->_datastreams =& FedoraItem::$instantiated_pids[$pid]->_datastreams;
      //$datastreams_doc =& FedoraItem::$instantiated_pids[$pid]->datastreams_list;
    }
    else { 
      $this->_refreshObjectProfile();
      FedoraItem::$instantiated_pids[$pid]=&$this;
    }
  }
 
  public function delete() {
    $this->state = 'D';
  }
  
  public function purge() {
    $this->client->api->purgeObject($this->pid);
  }
  
  /**
   * Add a new datastream.  Wrapper for the addDatastream Fedora API-M method.
   * @param type $dsID
   * @param string $file_path
   * @param string $ds_string
   * @param type $params 
   */
  public function addDatastream($dsID, $file_path = NULL, $ds_string = NULL, $params = array('altIDs' => NULL, 'dsLabel' => NULL, 'dsState' => NULL, 'formatURI' => NULL,
                                                                                                                                             'checksumType' => NULL, 'checksum' => NULL, 'mimeType' => NULL,
                                                                                                                                             'logMessage' => NULL)) {
    
    $response = $this->client->api->addDatastream($this->pid, $dsID, $file_path, $ds_string, $params);
    if ($response->code != 201) {
      throw new FedoraRestException($response->error);
    }
    $this->_refreshDatastreams();
    return $this->datastreams[$dsID];
  }
  
  /**
   * Add a relationship string to this object's RELS-EXT.
   * does not support rels-int yet.
   * @param string $relationship
   * @param <type> $object
   */
  public function add_relationship($relationship, $object, $namespaceURI = 'info:fedora/fedora-system:def/relations-external#') {
    $ds_list = $this->get_datastreams();   
    if (empty($ds_list['RELS-EXT'])) {
		$dsString = '        <rdf:RDF xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#">
          <rdf:Description rdf:about="info:fedora/'. $this->pid . '">
          </rdf:Description>
        </rdf:RDF>';
      $this->addDatastream('RELS-EXT', NULL, $dsString, 
		array('dsLabel'=>'Fedora object-to-object relationship metadata', 'mimeType'=>'text/xml', 'controlGroup' => 'X'));
    }
    $relsext = $this->client->api->getDatastreamDissemination($this->pid,'RELS-EXT')->data;
    if (substr($object, 0, 12) != 'info:fedora/') {
      $object = "info:fedora/$object";
    }
    $relsextxml = new DomDocument();    
    $relsextxml->loadXML($relsext);
    $description = $relsextxml->getElementsByTagNameNS('http://www.w3.org/1999/02/22-rdf-syntax-ns#', 'Description');
    if ($description->length == 0) {
      $description = $relsextxml->getElementsByTagNameNS('http://www.w3.org/1999/02/22-rdf-syntax-ns#', 'description');
    }
    $description=$description->item(0);
    // Create the new relationship node.
    $newrel = $relsextxml->createElementNS($namespaceURI, $relationship);
    $newrel->setAttribute('rdf:resource', $object);
    $description->appendChild($newrel);
    $this->client->api->modifyDatastream( $this->pid, 'RELS-EXT', NULL, $relsextxml->saveXML(), 
			array('dsLabel'=>"Fedora Object-to-Object Relationship Metadata", 'mimeType'=>'text/xml'));
  }
  
  /**
   * Returns an associative array of this object's datastreams. Results look like this:
   *
   *  'DC' =>
   *    array
   *      'label' => string 'Dublin Core Record for this object' (length=34)
   *      'MIMEType' => string 'text/xml' (length=8)
   *  'RELS-EXT' =>
   *    array
   *      'label' => string 'RDF Statements about this object' (length=32)
   *      'MIMEType' => string 'application/rdf+xml' (length=19)
   *
   * @return array
   */
  function get_datastreams() {
    
    if ($this->_datastreams != NULL) {
      return $this->_datastreams;
    }
    
    $result = $this->client->api->listDatastreams($this->pid);
	if ($result->code != 200) {
      throw new FedoraRestException($result->error);
    }
    $datastreams_doc = new SimpleXMLElement($result->data);
    $ds_list = array();
    if ($datastreams_doc != FALSE) {

      foreach ($datastreams_doc->datastream as $ds) {
        foreach($ds->attributes() as $field => $value) {
          switch ($field) {
            case 'dsid':
              $dsid = (string) $value;
              break;            
            case 'label':
              $dslabel = (string) $value;
              break;
            case 'mimeType':
              $dsmime = (string) $value;
              break;
          }
        }
        
        if (!empty($dsid) && !empty($dsmime) && $dslabel !== NULL) {
          $ds_list[$dsid] = new FedoraDatastream($this, $dsid, $dslabel, $dsmime);
        }
      }
    }
    
    $this->_datastreams = $ds_list;
    return $ds_list;
  }
  
  public function get_label() {
    return (string) $this->objectProfile->objLabel;
  }
  
  protected function set_label($label) {
    $this->modifyObject($label);
  }
  
  public function get_ownerId() {
    return  (string) $this->objectProfile->objOwnerId;
  }
  
  protected function set_ownerId($ownerId) {
    $this->modifyObject($this->label, $ownerId);
  }
  
  public function get_state() {
    return (string) $this->objectProfile->objState;
  }
  
  protected function set_state($state) {
    $this->modifyObject(NULL, NULL, $state);
  }
  
  public function get_pid() {
    return $this->pid;
  }
  
  public function get_createDate() {
    return new DateTime((string) $this->objectProfile->objCreateDate);
  }
  
  public function get_lastModDate() {
    return  new DateTime((string) $this->objectProfile->objLastModDate);
  }
  
  public function get_models() {
    $models = array();
    foreach ($this->objectProfile->objModels->model as $model) {
      $models[] = (string) $model;
    }
    return $models;
  }
  
  private function _refreshDatastreams() {
    $this->_datastreams = NULL;
  }
  
  private function _refreshObjectProfile() {
    $response = $this->client->api->getObjectProfile($this->pid);

    if ($response->code == 200) {
      $this->objectProfile = new SimpleXMLElement($response->data);
      $this->_datastreams = $this->get_datastreams();
    } else {
      $this->objectProfile = '';
      $this->_datastreams = array();
    }
  }
  
  /**
   * Wrapper for Fedora's modifyObject REST call. Updates the local objectProfile after each call.
   * @param type $label
   * @param type $ownerId
   * @param type $state One of 'A' for active, 'D' for deleted, or 'I' for inactive 
   * @param type $logMessage 
   */
  public function modifyObject($label = NULL, $ownerId = NULL, $state = NULL, $logMessage = NULL) {
    $response = $this->client->api->modifyObject($this->pid, $label, $ownerId, $state, $logMessage);
    if ($response->code == 200) {
      $this->_refreshObjectProfile();
      return TRUE;
    }
    else {
      return FALSE;
    }
  }
  
  static function createItem( ) {
    $this->client->api->ingest();
  }
}
