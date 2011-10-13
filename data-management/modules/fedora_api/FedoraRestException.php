<?php

/**
 * Wrapper for HTTP responses from Fedora Commons's REST API.
 */
class FedoraRestException extends Exception {
  function __construct($message, $code = NULL, $previous = NULL) {
    parent::__construct($message);
  }
  
}
