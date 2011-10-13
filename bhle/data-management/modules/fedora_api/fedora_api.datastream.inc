<?php

include_once('fedora_api.item.inc');

class FedoraDatastream extends PropertyObject {
  private $_parent = NULL;
  public $dsID = '';
  //protected $_populated = FALSE; // If the getDatastream call has been made to populate this object's fields
  private $_dsLabel;
  private $_dsVersionID;
  private $_dsCreateDate;
  private $_dsState;
  private $_dsMIME;
  private $_dsFormatURI;
  private $_dsSize;
  private $_dsVersionable;
  private $_dsInfoType;
  private $_dsLocation;
  private $_dsLocatoinType;
  private $_dsChecksumType;
  private $_dsChecksum;
  
  /**
   *
   * @param type $parent FedoraItem that this item is a part of
   * @param type $dsID The Datastreams DSID
   * @param type $label Datastream's label, if creating a minimal datastream
   * @param type $mimeType The Datastream's MIME Type, if creating a minimal datastream
   */
  function __construct($parent, $dsID, $label = NULL, $mimeType = NULL) {
    $this->_parent = $parent;
    $this->_dsID = $dsID;
    $this->_populated = FALSE;
     if ($label == NULL && $mimeType == NULL) {
       $this->_refresh();
    }
    else {
      // Create a minimal datastream. Getters may invoke getDatastreamInfo to access other fields.
      $this->_dsLabel = $label;
      $this->_dsMIME = $mimeType;
    }
  }

  protected function get_dsID() {
    return $this->_dsID;
  }
  
  protected function get_content() {

    $resp = $this->parent->client->api->getDatastreamDissemination($this->parent->pid, $this->_dsID);
    if ($resp->code != 200) {
      throw new FedoraRestException("Error getting datastream contents $dsID . $resp->error");
    }
    return $resp->data;
  }
  
  protected function set_content($content) {
    $resp = $this->parent->client->api->modifyDatastream($this->parent->pid, $this->_dsID, NULL, $content);
    if ($resp->code != 200) {
      throw new FedoraRestException("Unable to set datastream contents. " . $resp->error);
    }
  }
  
  public function get_dsLabel() {
    return $this->_dsLabel;
  }
  
  protected function set_dsLabel($label) {
    $resp = $this->parent->client->api->modifyDatastream($this->parent->pid, $this->_dsID, NULL, NULL, array('dsLabel' => $label));
    if ($resp->code != 200) {
      throw new FedoraRestException("Unable to set label, $resp->error");
    }
    $this->_dsLabel = $label;
  }
  
  protected function get_dsVersionID() {
    
    return $this->_dsVersionID;
  }
  
  protected function get_dsCreateDate() {
    return $this->_dsCreateDate;
  }
  
  protected function get_dsState() {
    return $this->_dsState;
  }
  
  protected function set_dsState($state) {
    $state = strtoupper($state);
    if (!in_array($state, array('A', 'I', 'D'))) {
      throw new FedoraRestException("State must be one of 'A', 'I', or 'D'");
    }
    $resp = $this->parent->client->api->modifyDatastream($this->parent->pid, $this->_dsID, NULL, NULL, array('dsState' => $state));
    if ($resp->code != 200) {
      throw new FedoraRestException("Unable to set label, $resp->error");
    }
    $this->_dsState = $state;
  }
  
  public function get_dsMIME() {
    return $this->_dsMIME;
  }
  
  protected function get_dsFormatURI() {
    return $this->_dsFormatURI;
  }
  
  public function get_dsSize() {
    return $this->_dsSize;
  }
  
  protected function get_dsVersionable() {
    return $this->_dsVersionable;
  }
  
  protected function set_dsVersionable($versionable) {
    $versionable = $versionable == TRUE ? 'true' : 'false';
    $resp = $this->parent->client->api->modifyDatastream($this->parent->pid, $this->_dsID, NULL, NULL, array('versionable' => $versionable));
    if ($resp->code != 200) {
      throw new FedoraRestException("Unable to set dsVersionable, $resp->error");
    }
    $this->_dsVersionable = $versionable;
  }
  
  protected function get_dsInfoType() {
    return $this->_dsInfoType;
  }
  
  protected function get_dsLocation() {
    return $this->_dsLocation;
  }
  
  protected function get_dsLocationType() {
    return $this->_dsLocationType;
  }
  
  protected function get_dsChecksumType() {
    return $this->_dsChecksumType;
  }
  
  protected function get_dsChecksum() {
    return $this->_dsChecksum;
  }
  
  protected function get_parent() {
    return $this->_parent;
  }
  
  private function _decodeDatastreamXML($xml) {
    $ds_info = new SimpleXMLElement($xml);
    $this->_dsLabel = (string) $ds_info->dsLabel;
    $this->_dsVersionID = (string) $ds_info->dsVersionID;
    $this->_dsCreateDate = new DateTime((string) $ds_info->createdDate);
    $this->_dsState = (string) $ds_info->dsState;
    $this->_dsMIME = (string) $ds_info->dsMIME;
    $this->_dsFormatURI = (string) $ds_info->dsFormatURI;
    $this->_dsSize = (int) (string) $ds_info->dsSize;
    $this->_dsVersionable = (boolean) (string) $ds_info->dsVersionable;
    $this->_dsInfoType = (string) $ds_info->dsInfoType;
    $this->_dsLocation = (string) $ds_info->dsLocation;
    $this->_dsLocationType = (string) $ds_info->dsLocationType;
    $this->_dsChecksumType = (string) $ds_info->dsChecksumType;
    $this->_dsChecksum = (string) $ds_info->dsChecksum;
  }

  /**
   * Call out to Fedora to populate this Datastream object's fields.
   */
  protected function _refresh() {
    $resp = $this->_parent->client->api->getDatastream($this->_parent->pid, $this->_dsID);
    if ($resp->code == 200) {
      $this->_decodeDatastreamXML($resp->data);
      $this->_populated = TRUE;
    }
    else {
      throw new FedoraRestException("Unable to create datastream for $this->_dsID. $resp->error.");
    }
  }  
}