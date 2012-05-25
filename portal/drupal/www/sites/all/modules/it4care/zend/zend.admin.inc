<?php

/**
 * @file
 * Administration pages for the Zend Framework.
 */

/**
 * Administration settings for the Zend Framework.
 */
function zend_admin($form, &$form_state) {
  $path = zend_get_path();
  $zend_status = $path ? 'ok' : 'error';
  $zend_version = $path ? Zend_Version::VERSION : t('Not found');
  $form['zend_path'] = array(
    '#type' => 'textfield',
    '#title' => t('Installation Path'),
    '#description' => t('The path to the Zend Framework. If the libraries module is installed and the framework is in the libraries directory or the framework is in the PHP include path it will be automatically detected. The current installed version is: %version', array('%version' => $zend_version)),
    '#default_value' => zend_get_path(),
    '#attributes' => array('class' => array($zend_status)),
  );
  return system_settings_form($form);
}
