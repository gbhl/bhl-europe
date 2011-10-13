<?php
/**
 * @file
 * Defines the View Style Plugins for the Views jQFX module.
 */

/**
 * Implements hook_views_plugins().
 */
function views_jqfx_views_plugins() {
  return array(
    'style' => array(
      'jqfx' => array(
        'title' => t('jQFX'),
        'help' => t('Applies jQuery effects to views output.'),
        'handler' => 'views_jqfx_plugin_style_jqfx',
        'uses options' => TRUE,
        'uses row plugin' => TRUE,
        'uses_grouping' => FALSE,
        'type' => 'normal',
        'parent' => 'list',
        'path' => drupal_get_path('module', 'views_jqfx'),
        'theme' => 'views_jqfx',
        'theme path' => drupal_get_path('module', 'views_jqfx'),
        'theme file' => 'views_jqfx.theme.inc',
      ),
    ),
  );
}
