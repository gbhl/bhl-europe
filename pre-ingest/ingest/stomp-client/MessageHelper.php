<?php

require_once 'Stomp.php';
require_once("Stomp/Message/Map.php");

class MessageHelper {
	
	protected $_brokerUrl = null;
	
	protected $_client = null;
	protected $_outbound_topic = "/topic/preingest";
	protected $_inbound_topic = "/topic/ingest";
	
	public function __construct ($brokerUrl) {
		$this->_brokerUrl = $brokerUrl;
		$this->_client = new Stomp($this->_brokerUrl);
		$this->clientId = "preingest";
		$this->_client->connect();
	}

	public function informIngest ($guid, $uri) {
		$this->_client->send($this->_outbound_topic, $this->transformToMapMessage($guid, $uri), array('persistent'=>'true'));
	}
	
	private function transformToMapMessage ($guid, $uri) {
		$body = array("GUID"=>$guid, "URI"=>$uri);
		$header = array();
		$header['transformation'] = 'jms-map-json';
		$mapMessage = new StompMessageMap($body, $header);
		return $mapMessage;
	}
	
	public function subscribe() {
		$this->_client->subscribe($this->_inbound_topic);
	}
	
	public function subscribe($topic) {
		$this->_client->subscribe($topic);
	}
	
	public function unsubscribe() {
		$this->_client->unsubscribe($this->_inbound_topic);
	}
	
	public function unsubscribe($topic) {
		$this->_client->unsubscribe($topic);
	}
	
	public function receive() {
		$msg = $this->_client->readFrame();

		if ( $msg != null) {
			$this->_client->ack($msg);
			return $msg->body;
		} else {
			echo "Failed to receive a message\n";
			return null;
		}
	}
	
	public function __destruct() {
		$this->_client->disconnect();
	}
}

?>
