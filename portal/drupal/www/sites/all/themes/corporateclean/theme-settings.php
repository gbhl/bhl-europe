<?php
/**
 * Implements hook_form_FORM_ID_alter().
 *
 * @param $form
 *   The form.
 * @param $form_state
 *   The form state.
 */
function corporateclean_form_system_theme_settings_alter(&$form, &$form_state) {

  $form['mtt_settings'] = array(
    '#type' => 'fieldset',
    '#title' => t('Corporate Clean Theme Settings'),
    '#collapsible' => FALSE,
	'#collapsed' => FALSE,
  );

  $form['mtt_settings']['breadcrumb'] = array(
    '#type' => 'fieldset',
    '#title' => t('Breadcrumb'),
    '#collapsible' => TRUE,
	'#collapsed' => FALSE,
  );
  
  $form['mtt_settings']['breadcrumb']['breadcrumb_display'] = array(
    '#type' => 'checkbox',
    '#title' => t('Show breadcrumb'),
  	'#description'   => t('Use the checkbox to enable or disable Breadcrumb.'),
	'#default_value' => theme_get_setting('breadcrumb_display','corporateclean'),
    '#collapsible' => TRUE,
	'#collapsed' => FALSE,
  );
  
  $form['mtt_settings']['feed-tabs'] = array(
    '#type' => 'fieldset',
    '#title' => t('Front Page Feed Tabs'),
    '#collapsible' => TRUE,
	'#collapsed' => FALSE,
  );
  
  $form['mtt_settings']['feed-tabs']['feedtabs_according'] = array(
    '#type' => 'checkbox',
    '#title' => t('Show according tabs'),
	'#default_value' => theme_get_setting('feedtabs_according','corporateclean'),
  );
  
  $form['mtt_settings']['feed-tabs']['feedtabs_effect'] = array(
    '#type' => 'select',
    '#title' => t('Effects'),
  	'#description'   => t('From the drop-down menu, select the tabs effect you prefer.'),
	'#default_value' => theme_get_setting('feedtabs_effect','corporateclean'),
    '#options' => array(
		'horizontal' => t('horizontal'),
    ),
  );
    
}
