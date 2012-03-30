<?php

require_once 'Stomp.php';
require_once("Stomp/Message/Map.php");
require_once 'Thread.php' ;

class MessageHelper {
	
	protected $_brokerUrl = null;
	
	protected $_client = null;
	protected $_clientId = "preingest";
	protected $_queue = "/queue/preingest";
	protected $_topic = "/topic/ingest";
	
	protected $_subscriber_thread;
	
	public function __construct ($brokerUrl) {
		$this->_brokerUrl = $brokerUrl;
		$this->_client = new Stomp($this->_brokerUrl);
	}

	/**
	 * Send a message containing GUID and URI to ActiveMQ queue ('preingest' by default) to indicate this SIP is ready to ingest.
	 */
	public function informIngest ($guid, $uri) {
		$this->_client->connect();
		$this->_client->send($this->_queue, $this->transformToMapMessage($guid, $uri), array('persistent'=>'true'));
		$this->_client->disconnect();
	}
	
	/**
	 * Transform a message to Map, receiver will get a JSON-array message
	 */
	private function transformToMapMessage ($guid, $uri) {
		$body = array("GUID"=>$guid, "URI"=>$uri);
		$header = array();
		$header['transformation'] = 'jms-map-json';
		$mapMessage = new StompMessageMap($body, $header);
		return $mapMessage;
	}
	
	/**
	 * create a thread to subscribe to a topic ('ingest' by default), and pass the name of a function as a callback function (this function should have an argument as the message)
	 */
	public function subscribe($function, $topic = null) {
		if( ! Thread::available() ) {
			echo "Threads not supported\n";
			return null;
		}
		
		if ($topic === null) {
			$topic = $this->_topic;
		}
		
		function receive($topic, $function) {
			$this->clientId = $this->_clientId;
			$this->_client->connect();
			$this->_client->subscribe($topic);
		
			while ($_subscriber_thread -> isAlive()) {
				$msg = $this->_client->readFrame();
				if ( $msg != null) {
					call_user_func($function, $msg);
					$this->_client->ack($msg);
				}
				
				sleep(1);
			}
			
			$this->_client->unsubscribe($topic);
			$this->_client->disconnect();
		}
		
		$_subscriber_thread = new Thread(array($this, 'receive'));
		$_subscriber_thread -> start($topic, $function);
	}
	
	/**
	 * simply kill the thread if it exists
	 */
	public function unsubscribe() {
		if ($this->_subscriber_thread != null){
			$this->_subscriber_thread -> stop();
		}
	}
	
	/**
	 * reveive messages in loop and trigger the callback function
	 */
	public function receive($topic, $function) {
		$this->clientId = $this->_clientId;
		$this->_client->connect();
		$this->_client->subscribe($topic);
	
		while (true) {
			$msg = $this->_client->readFrame();
			if ( $msg != null) {
				call_user_func($function, $msg);
				$this->_client->ack($msg);
			}
			
			sleep(1);
		}
		
		$this->_client->unsubscribe($topic);
		$this->_client->disconnect();
	}
}

?>
