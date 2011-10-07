<?php
/**
 * @file
 * Variable information
 */

/**
 * Implements hook_variable_info()
 */
function i18n_node_variable_info($options = array()) {
  $variables['i18n_hide_translation_links'] = array(
    'type' => 'boolean',
    'title' => t('Hide content translation links', array(), $options),
    'description' => t('Hide the links to translations in content body and teasers. If you choose this option, switching language will only be available from the language switcher block.', array(), $options),
    'default' => 0,
    'group' => 'i18n',
  );
  $variables['i18n_translation_switch'] = array(
    'type' => 'boolean',
    'title' => t('Switch interface for translating', array(), $options),
    'default' => 0,
    'group' => 'i18n',
    'description' => t('Switch interface language to fit node language when creating or editing a translation. If not checked the interface language will be independent from node language.', array(), $options),
  );
  $variables['i18n_node_options_[node_type]'] = array(
    'type' => 'multiple',
    'title' => t('Extended language options', array(), $options),
    'repeat' => array(
      'type' => 'options',
      'options' => array(
        'current' => t('Set current language as default for new content.', array(), $options),
        'required' => t('Require language (Do not allow Language Neutral).', array(), $options),
        'lock' => t('Lock language (Cannot be changed).', array(), $options),
      ),
    ),
    'group' => 'i18n',
  );
  $variables['i18n_node_extended_[node_type]'] = array(
    'type' => 'multiple',
    'title' => t('Extended language support'),
    'repeat' => array(
      'type' => 'select',
      'options callback' => 'i18n_node_variable_extended_options',
      'default' => I18N_LANGUAGE_ENABLED,
    ),
    'description' => t('If enabled, all defined languages will be allowed for this content type in addition to only enabled ones. This is useful to have more languages for content than for the interface.', array(), $options),
    'group' => 'i18n',
  );
  return $variables;
}

/**
 * Options callback for i18n_node_extended_
 */
function i18n_node_variable_extended_options($variable, $options) {
  return array(
    I18N_LANGUAGE_ENABLED => t('Normal - All enabled languages will be allowed.', array(), $options),
    I18N_LANGUAGE_EXTENDED => t('Extended - All defined languages will be allowed.', array(), $options),
    I18N_LANGUAGE_EXTENDED | I18N_LANGUAGE_HIDDEN => t('Extended, but not displayed - All defined languages will be allowed for input, but not displayed in links.', array(), $options),
  );  
}