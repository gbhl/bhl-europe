<?php
/**
 * Represents a connection to a Fedora-Commons Repository using the REST API
 */
class FedoraConnection {
  public $url;
  public $username;
  public $password;
  
  function __construct($url = NULL, $username = NULL, $password = NULL) {
    if (!empty($url)) {
      $this->url = $url;
    }
    else {
      $this->url = variable_get('fedora_server_url', 'http://localhost:8080/fedora');
    }
    
    if (!empty($username)) {
      $fedora_user = $username;
      $fedora_pass = $password;
    }
    else {
      $user_var = variable_get('fedora_user', '');
      if (!empty($user_var)) {
        $fedora_user = variable_get('fedora_user', '');
        $fedora_pass = variable_get('fedora_password', '');
      }
      elseif ($user->uid == 0) {
        $fedora_user = 'anonymous';
        $fedora_pass = 'anonymous';
      }
      else {
        $fedora_user = $user->name;
        $fedora_pass = $user->pass;
      }
    }
    $this->username = $fedora_user;
    $this->password = $fedora_pass;
    
    
  }
  
   /**
   * Inserts a username and password into a URL giving a format like "http://user:pass@domain.com/"
   * This is the format expected by PHP's parse_url function.
   * @return String The full URL to send a REST API call to.
   */
  function requestURL() {
    return (!empty($this->username) ? substr_replace($this->url, "$this->username" . (!empty($this->password) ? ":$this->password@" : '@'), strpos($this->url, '://') + 3, 0) : $this->url);
  }
  
  function getObject($pid) {
    return new FedoraItem($pid, $this);
  }
}