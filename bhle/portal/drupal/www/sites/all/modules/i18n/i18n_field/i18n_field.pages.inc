<?php
/**
 * Translation page for fields.
 */

/**
 * Field translation page
 * 
 * We need to translate field and field instance.
 */
function i18n_field_page_translate($instance, $langcode = NULL) {
  module_load_include('inc', 'i18n_string', 'i18n_string.pages');
  if (!$langcode) {
    // Overview page will be the regular one
    return i18n_string_translate_page_object('field_instance', $instance);
  }
  else {
    drupal_set_title(t('Translate to @language', array('@language' => i18n_language_name($langcode))));
    return drupal_get_form('i18n_field_page_translate_form', $instance, $langcode);
  }
}

/**
 * Field instance and field translation in a single form on two different fieldsets.
 */
function i18n_field_page_translate_form($form, &$form_state, $instance, $langcode) {
  $form = i18n_string_translate_page_form_base($form, $langcode);
  $object = i18n_object('field_instance', $instance);
  $form['#redirect'] = array($object->get_translate_path($langcode));
  // Field instance
  $form['strings']['instance'] = array(
    '#type' => 'fieldset',
    '#title' => t('Field instance'),
    '#collapsible' => TRUE,
  );
  $form['strings']['instance'] += i18n_string_translate_page_form_strings($object->get_strings(array('empty' => TRUE)), $langcode);
  // Field settings
  $object = i18n_object('field', $instance);
  $form['strings']['field'] = array(
    '#type' => 'fieldset',
    '#title' => t('Field settings'),
    '#collapsible' => TRUE, '#collapsed' => TRUE,
  );
  $form['strings']['field'] += i18n_string_translate_page_form_strings($object->get_strings(array('empty' => TRUE)), $langcode); 
  return $form;
}