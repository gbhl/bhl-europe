<?php
/**
 * @file
 * Variable API module. Definition for Drupal core variables
 */

/**
 * Implements hook_variable_info().
 */
function locale_variable_info($options) {
  // This variable will be altered by translation module. See translation.variable.inc
  $variables['language_content_type_[node_type]'] = array(
    'type' => 'multiple',
    'title' => t('Multilingual support'),
    'repeat' => array(
      'type' => 'enable',
       // Set options here so translation module can add some more 
      'options' => array(t('Disabled'), t('Enabled')),
    ),
    'description' => t('Enable multilingual support for this content type. If enabled, a language selection field will be added to the editing form, allowing you to select from one of the <a href="!languages">enabled languages</a>. If disabled, new posts are saved with the default language. Existing content will not be affected by changing this option.', array('!languages' => url('admin/config/regional/language', $options)), $options),
    'group' => 'node_type_settings',
  );
  return $variables;
}

/**
 * Implements hook_variable_type_info().
 */
function locale_variable_type_info() {
  // Language code
  $type['language'] = array(
    'title' => t('Language'),
    'options callback' => 'locale_variable_options_language',
    'type' => 'select',
  );
  return $type;
}

/**
 * Callback for all languages
 */
function locale_variable_options_language($variable, $options) {
  return locale_language_list('name', TRUE);
}


