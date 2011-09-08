<?php


/**
 * user configuration form for advanced search
 * lets the user select which metadata fields appear in search forms
 * @param <type> $form
 * @param <type> $form_state
 * @param <type> $type - type of settings ('application','user','session')
 * @return form array
 */
function advanced_search_settings_user_form($form, &$form_state) {

  $fields = advanced_search_get_and_set_fields_from_solr_schema();
  $selfields = metadata_variable_get_user('advanced_search_selectable_fields');

  $form['advanced_search_result_view_type'] = array(
    '#title' => t('Default result view type'),
    '#type' => 'select',
    '#options' => array('list' => t('List'), 'table' => t('Table')),
    '#default_value' => metadata_variable_get_user('advanced_search_result_view_type'),
  );
  $form['advanced_search_result_list_size'] = array(
    '#type' => 'select',
    '#title' => t('Number of results in result-list'),
    '#options' => drupal_map_assoc(array(5,10,15,20,30,50,100)), // TODO load from anywhere
    '#default_value' => metadata_variable_get_user('advanced_search_result_list_size'),
  );
  $form['advanced_search_result_table_size'] = array(
    '#type' => 'select',
    '#title' => t('Number of results in result-table'),
    '#options' => drupal_map_assoc(array('2x3','3x4','4x5')), // TODO load from anywhere
    '#default_value' => metadata_variable_get_user('advanced_search_result_table_size'),
  );
  $form['advanced_search_field_set_type'] = array(
    '#title' => t('metadata fields for advanced search'),
    '#type' => 'radios',
    '#options' => array('all' => t('all metadata fields'), 'selected' => t('only selected metadata fields:')),
    '#default_value' => metadata_variable_get_user('advanced_search_field_set_type'),
  );
  $form['advanced_search_selectable_fields'] = array(
    '#type' => 'checkboxes',
    '#options' => $fields,
    '#default_value' =>  $selfields,
    '#prefix' => '<div style="max-height:300px; overflow:scroll; TEST">',
    '#suffix' => '</div>',
    '#attributes' => array ('onclick' => 'document.getElementById("edit-advanced-search-field-set-type-selected").checked = true;')
  );
  $form['reset'] = array(
    '#type' => 'submit',
    '#value' => 'reset to defaults',
    '#submit' => array('advanced_search_settings_user_reset'),
  );
  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => 'save',
    '#submit' => array('advanced_search_settings_user_form_submit_user'),
  );

  return $form;
}


/**
 * user configuration submit handler for advanced search
 * @param <type> $form
 * @param <type> $form_state
 */
function advanced_search_settings_user_reset($form, &$form_state) {

  // saving variables
  metadata_variable_unset_user('advanced_search_result_view_type');
  metadata_variable_unset_user('advanced_search_result_list_size');
  metadata_variable_unset_user('advanced_search_result_table_size');
  metadata_variable_unset_user('advanced_search_field_set_type');
  metadata_variable_unset_user('advanced_search_selectable_fields');
  //unsetting $_SESSION variables, so that the saved values take effect immediatly
  unset($_SESSION['advanced_search_result_view_type']);
  unset($_SESSION['advanced_search_result_list_size']);
  unset($_SESSION['advanced_search_result_table_size']);
  unset($_SESSION['advanced_search_field_set_type']);
  unset($_SESSION['advanced_search_selectable_fields']);
}


/**
 * user configuration submit handler for advanced search
 * @param <type> $form
 * @param <type> $form_state
 */
function advanced_search_settings_user_form_submit_user($form, &$form_state) {

  // saving variables
  metadata_variable_set_user('advanced_search_result_view_type',  $form_state['values']['advanced_search_result_view_type']);
  metadata_variable_set_user('advanced_search_result_list_size',  $form_state['values']['advanced_search_result_list_size']);
  metadata_variable_set_user('advanced_search_result_table_size', $form_state['values']['advanced_search_result_table_size']);
  metadata_variable_set_user('advanced_search_field_set_type',    $form_state['values']['advanced_search_field_set_type']);
  metadata_variable_set_user('advanced_search_selectable_fields', $form_state['values']['advanced_search_selectable_fields']);
  //unsetting $_SESSION variables, so that the saved values take effect immediatly
  unset($_SESSION['advanced_search_result_view_type']);
  unset($_SESSION['advanced_search_result_list_size']);
  unset($_SESSION['advanced_search_result_table_size']);
  unset($_SESSION['advanced_search_field_set_type']);
  unset($_SESSION['advanced_search_selectable_fields']);
}


/**
 * user configuration form for advanced search
 * lets the user select which metadata fields appear in search forms
 * @param <type> $form
 * @param <type> $form_state
 * @param <type> $type - type of settings ('application','user','session')
 * @return form array
 */
function advanced_search_settings_application_form($form, &$form_state) {

  $fields = advanced_search_get_and_set_fields_from_solr_schema();
  $selfields = metadata_variable_get_application('advanced_search_selectable_fields');

  $form['advanced_search_result_view_type'] = array(
    '#title' => t('Default result view type'),
    '#type' => 'select',
    '#options' => array('list' => t('List'), 'table' => t('Table')),
    '#default_value' => metadata_variable_get_application('advanced_search_result_view_type'),
  );
  $form['advanced_search_result_list_size'] = array(
    '#type' => 'select',
    '#title' => t('Number of results in result-list'),
    '#options' => drupal_map_assoc(array(5,10,15,20,30,50,100)), // TODO load from anywhere
    '#default_value' => metadata_variable_get_application('advanced_search_result_list_size'),
  );
  $form['advanced_search_result_table_size'] = array(
    '#type' => 'select',
    '#title' => t('Number of results in result-table'),
    '#options' => drupal_map_assoc(array('2x3','3x4','4x5')), // TODO load from anywhere
    '#default_value' => metadata_variable_get_application('advanced_search_result_table_size'),
  );
  $form['advanced_search_field_set_type'] = array(
    '#title' => t('metadata fields for advanced search'),
    '#type' => 'radios',
    '#options' => array('all' => t('all metadata fields'), 'selected' => t('only selected metadata fields:')),
    '#default_value' => metadata_variable_get_application('advanced_search_field_set_type'),
  );
  $form['advanced_search_selectable_fields'] = array(
    '#type' => 'checkboxes',
    '#options' => $fields,
    '#default_value' =>  $selfields,
    '#prefix' => '<div style="max-height:300px; overflow:scroll; TEST">',
    '#suffix' => '</div>',
    '#attributes' => array ('onclick' => 'document.getElementById("edit-advanced-search-field-set-type-selected").checked = true;')
  );
  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => 'save',
    '#submit' => array('advanced_search_settings_application_form_submit'),
  );

  return $form;
}



/**
 * application configuration submit handler for advanced search
 * @param <type> $form
 * @param <type> $form_state
 */
function advanced_search_settings_application_form_submit($form, &$form_state) {

  // saving variables
  metadata_variable_set_application('advanced_search_result_view_type',  $form_state['values']['advanced_search_result_view_type']);
  metadata_variable_set_application('advanced_search_result_list_size',  $form_state['values']['advanced_search_result_list_size']);
  metadata_variable_set_application('advanced_search_result_table_size', $form_state['values']['advanced_search_result_table_size']);
  metadata_variable_set_application('advanced_search_field_set_type',    $form_state['values']['advanced_search_field_set_type']);
  metadata_variable_set_application('advanced_search_selectable_fields', $form_state['values']['advanced_search_selectable_fields']);


}

