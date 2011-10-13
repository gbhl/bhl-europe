<?php

/**
 * Description of PropertyObject
 *
 * From example code at http://www.php.net/manual/en/language.oop5.properties.php#98267
 */
abstract class PropertyObject {
  private $_construct = FALSE;
  protected $_populated = TRUE;
  protected function _refresh() {
    
  }
  
  public function __get($name) {
    $var_name = "_$name";
    if ((!property_exists($this, $var_name) || $this->$var_name == NULL) && !$this->_populated) {
      $this->_refresh();
    }
    if (method_exists($this, ($method = 'get_'.$name))) {
      return $this->$method();
    }
    else return;
  }
  
  public function __isset($name) {
    if (method_exists($this, ($method = 'isset_'.$name))){
      return $this->$method();
    }
    else return;
  }
  
  public function __set($name, $value) {
    if (method_exists($this, ($method = 'set_'.$name))) {
      $this->$method($value);
    }
    else {
      $this->$name = $value;
    }
  }
  
  public function __unset($name) {
    if (method_exists($this, ($method = 'unset_'.$name))){
      $this->$method();
    }
  }
}
