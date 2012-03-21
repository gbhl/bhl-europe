<?php

require_once 'Stomp.php';
require_once("Stomp/Message/Map.php");

class MessageHelper {
	
	protected $_brokerUrl = null;
	
	protected $_client = null;
	protected $_topic = "/topic/preingest";
	
	public function __construct ($brokerUrl) {
		$this->_brokerUrl = $brokerUrl;
		$this->_client = new Stomp($this->_brokerUrl);
		$this->clientId = "preingest";
		$this->_client->connect();
	}

	public function informIngest ($guid, $uri) {
		$this->_client->send($this->_topic, $this->transformToMapMessage($guid, $uri), array('persistent'=>'true'));
	}
	
	private function transformToMapMessage ($guid, $uri) {
		$body = array("GUID"=>$guid, "URI"=>$uri);
		$header = array();
		$header['transformation'] = 'jms-map-json';
		$mapMessage = new StompMessageMap($body, $header);
		return $mapMessage;
	}
	
	public function subscribe() {
		$this->_client->subscribe($this->_topic);
	}
	
	public function unsubscribe() {
		$this->_client->unsubscribe($this->_topic);
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
