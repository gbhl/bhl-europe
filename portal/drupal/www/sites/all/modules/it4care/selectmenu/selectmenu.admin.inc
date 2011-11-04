<?php

/**
 * Admin form
 */
function selectmenu_admin_form($form_state) {
	$form = array();
	$form['selectmenu_enabled'] = array(
		'#type' => 'checkbox',
		'#title' => t('Enable jQuery UI seletmenu'),
		'#default_value' => variable_get('selectmenu_enabled', TRUE),
		'#description' => t('Use this option to toggle jQuery UI selectmenu on and off globally.'),
	);
	$form['selectmenu_form_id_exceptions'] = array(
		'#type' => 'textarea',
		'#title' => t('Form CSS IDs to ignore'),
		'#description' => t('Enter the CSS IDs of the forms to not enable jQuery UI selectmenu on. One per line.'),
		'#rows' => 3,
		'#cols' => 40,
		'#default_value' => variable_get('selectmenu_form_id_exceptions', ''),
	);
	$form['selectmenu_ignore_system_settings_forms'] = array(
		'#type' => 'checkbox',
		'#title' => t('Ignore system settings forms'),
		'#description' => t('Do not use jQuery UI selectmenu for Drupal system settings form.'),
		'#default_value' => variable_get('selectmenu_ignore_system_settings_forms', TRUE),
	);
	$form['selectmenu_ignore_overlay_forms'] = array(
		'#type' => 'checkbox',
		'#title' => t('Ignore forms inside Overlay'),
		'#description' => t('Do not use jQuery UI selectmenu for forms that are inside of the Drupal 7 Overlay.'),
		'#default_value' => variable_get('selectmenu_ignore_overlay_forms', TRUE),
	);
	$form['selectmenu_ignore_node_add_forms'] = array(
		'#type' => 'checkbox',
		'#title' => t('Ignore node add forms'),
		'#description' => t('Do not use jQuery UI selectmenu for Drupal node add forms.'),
		'#default_value' => variable_get('selectmenu_ignore_node_add_forms', TRUE),
	);
	$form['selectmenu_disable_for_admin_theme'] = array(
		'#type' => 'checkbox',
		'#title' => t('Disable selectmenu for admin theme'),
		'#description' => t('Do not use jQuery UI selectmenu for any page that is using the admin theme set in Appearance settings. Only check this box if the site theme is different from the admin theme. Otherwise selectmenu will be disabled throughout the site.'),
		'#default_value' => variable_get('selectmenu_disable_for_admin_theme', FALSE),
	);


	return system_settings_form($form);
}