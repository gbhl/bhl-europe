<?php
/**
 * @file
 * Variable information
 */

/**
 * Implements hook_variable_info().
 */
function i18n_string_variable_info($options = array()) {
  $variables['i18n_string_translate_langcode_[language]'] = array(
    'type' => 'multiple_language',
    'title' => t('Enable translation for language'),
    'multiple values' => array('type' => 'boolean'),
    'group' => 'i18n',
  );
  $variables['i18n_string_allowed_formats'] = array(
    'title' => t('Translatable text formats'),
    'options callback' => 'i18n_string_variable_format_list',
    'type' => 'options',
    'default callback' => 'i18n_string_variable_format_default',
    'access' => 'administer filters',
    'description' => t('Only the strings that have the text formats selected will be allowed by the translation system. All the others will be deleted next time the strings are refreshed.'),
  );
  $variables['i18n_string_source_language'] = array(
    'title' => t('Source language'),
    'type' => 'language',
    'default callback' => 'i18n_string_source_language',
    'description' => t('Language that will be used as the source language for string translations. The default is the site default language.'),
  );
  $variables['i18n_string_debug'] = array(
    'type' => 'enable',
    'title' => t('Debug string translation', array(), $options),
    'default' => 0,
    'group' => 'debug',
  );
  return $variables;
}

/**
 * Options callback, format list
 */
function i18n_string_variable_format_list() {
  $list = array();
  // As the user has administer filters permissions we get a full list here
  foreach (filter_formats() as $fid => $format) {
    $list[$fid] = $format->name;
  }
  return $list;
}

/**
 * Allowed formats default value
 */
function i18n_string_variable_format_default() {
  return array(filter_fallback_format());
}