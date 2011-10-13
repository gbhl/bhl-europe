<?php

/**
 * @file
 * Helper functions for string administration.
 */

// Load locale library
include_once './includes/locale.inc';

/**
 * Form callback. Refresh textgroups.
 */
function i18n_string_admin_refresh_form() {
  module_load_include('inc', 'i18n_string');
  // Select textgroup/s. Just the ones that have a 'refresh callback'
  $groups = array();
  foreach (i18n_string_group_info() as $name => $info) {
    $groups[$name] = $info['title'];
  }
  $form['groups'] = array(
    '#type' => 'checkboxes',
    '#title' => t('Select text groups'),
    '#options' => $groups,
    '#description' => t('If a text group is no showing up here it means this feature is not implemented for it.'),
  );
  $form['delete'] = array(
    '#type' => 'checkbox',
    '#title' => t('Clean up left over strings.'),
    '#default_value' => TRUE,
  );
  $form['refresh'] = array(
    '#type' => 'submit',
    '#value' => t('Refresh strings'),
    '#suffix' => '<p>' . t('This will create all the missing strings for the selected text groups.') . '</p>',
  );
  return $form;
}

/**
 * Form submission.
 */
function i18n_string_admin_refresh_form_submit($form, &$form_state) {
  $op = isset($form_state['values']['op']) ? $form_state['values']['op'] : '';
  $groups = array_filter($form_state['values']['groups']);
  $group_names = module_invoke_all('locale', 'groups');
  if ($op == t('Refresh strings') && $groups) {
    $batch = i18n_string_refresh_batch($groups, $form_state['values']['delete']);
    batch_set($batch);
  }
}


/**
 * Refresh all user defined strings for a given text group.
 *
 * @param $group
 *   Text group to refresh
 * @param $delete
 *   Optional, delete existing (but not refresed, strings and translations)
 * @return Boolean
 *   True if the strings have been refreshed successfully. False otherwise.
 */
function i18n_string_refresh_group($group, $delete = FALSE) {
  $result = FALSE;
  
  // Compile all strings for this group
  if ($strings = i18n_string_group_string_list($group)) {
    i18n_string_refresh_string_list($strings);
    $result = TRUE;
  }
  // Invoke refresh hook
  $result = $result || module_invoke_all('i18n_string_refresh', $group);

  // Now delete all source strings that were not refreshed (they don't have a row in i18n_string)
  if ($result && $delete) {
    i18n_string_refresh_cleanup($group);
  }
  return $result;
}

/**
 * Clean up left over strings for text group
 */
function i18n_string_refresh_cleanup($group) {
  $lids = db_select('locales_source', 's')
    ->fields('s', array('lid'))
    ->condition('textgroup', $group)
    ->condition('version', 0)
    ->execute()
    ->fetchCol();
  if ($lids) {
    drupal_set_message(t('Performing cleanup for text group %textgroup, deleting @count left over strings.', array('%textgroup' => $group, '@count' => count($lids))));
    db_delete('locales_target')->condition('lid', $lids)->execute();
    db_delete('locales_source')->condition('lid', $lids)->execute();
    db_delete('i18n_string')->condition('lid', $lids)->execute();
    return count($lids);
  }
  else {
    return 0;
  }
}

/**
 * Prepare group for refreshing, reset version, count strings
 */
function i18n_string_refresh_reset($group) {
  // Mark data on locales_source setting version = 0
  db_update('locales_source')
    ->fields(array('version' => 0))
    ->condition('textgroup', $group)
    ->execute();
  return (int)db_query("SELECT COUNT(*) FROM {locales_source} WHERE textgroup = :textgroup", array(':textgroup' => $group))->fetchField();
}

/**
 * Refresh string list
 */
function i18n_string_refresh_string_list($strings) {
  $count = 0;
  foreach ($strings as $textgroup => $group_strings) {
    foreach ($group_strings as $type => $type_strings) {
      foreach ($type_strings as $id => $object_strings) {
        foreach ($object_strings as $key => $string) {
          if (is_array($string)) {
            $format = isset($string['format']) ? $string['format'] : NULL;
            $string = $string['string'];
          }
          else {
            $format = NULL;
          }
          i18n_string_update(array($textgroup, $type, $id, $key), $string, array('format' => $format));
          $count++;
        }
      }
    }
  }
  return $count;
}

/**
 * Create batch for refreshing strings
 *
 * @param $groups
 *   Array of text groups to refresh
 * @param $delete
 *   Optional, delete existing (but not refresed, strings and translations)
 */
function i18n_string_refresh_batch($groups, $delete = FALSE) {
  $operations = array();
  foreach ($groups as $group) {
    $operations[] = array('_i18n_string_batch_refresh_prepare', array($group));
    // First try to find string list
    $operations[] = array('_i18n_string_batch_refresh_list', array($group));
    // Then invoke refresh callback
    $operations[] = array('_i18n_string_batch_refresh_callback', array($group));
    if ($delete) {
      $operations[] = array('_i18n_string_batch_refresh_cleanup', array($group));
    }
    // Output group summary
    $operations[] = array('_i18n_string_batch_refresh_summary', array($group));
  }
  $batch = array(
    'operations'    => $operations,
    'title'         => t('Refreshing user defined strings'),
    'init_message'  => t('Starting string refresh'),
    'error_message' => t('Error refreshing user defined strings'),
    'file' => drupal_get_path('module', 'i18n_string') . '/i18n_string.admin.inc',
  );
  return $batch;
}

/**
 * Refresh strings for enabled modules
 */
function i18n_string_refresh_enabled_modules($modules) {
  // Check if any of the modules has strings to update
  $count = 0;
  foreach ($modules as $module) {
    if ($strings = i18n_string_module_string_list($module)) {
      $count += i18n_string_refresh_string_list($strings);
     
    }
    // Call module refresh if exists
    module_invoke($module, 'i18n_string_refresh', 'all');
  }
  if ($count) {
    drupal_set_message(format_plural($count, 'Refreshed one string for enabled modules.', 'Refreshed @count strings for the enabled modules.'));
  }
}

/**
 * Purge uninstalled modules.
 */
function i18n_string_refresh_uninstalled_modules($modules) {
  foreach ($modules as $module) {
    // If the module defines any textgroup, purge all strings.
    module_load_include('i18n.inc', $module);
    if ($string_info = module_invoke($module, 'i18n_string_info')) {
      foreach ($string_info as $group => $group_info) {
        i18n_string_refresh_reset($group);
        i18n_string_refresh_cleanup($group);
      }
    }
  }
}

/**
 * Prepare group for refreshing
 */
function _i18n_string_batch_refresh_prepare($group, &$context) {
  $context['results'][$group] = array(
    'count' => i18n_string_refresh_reset($group),
    'result' => FALSE,
  );
}

/**
 * Batch operation: Refresh string list for group
 */
function _i18n_string_batch_refresh_list($group, &$context) {
  $count = 0;
  if ($strings = i18n_string_group_string_list($group)) {
    $count = i18n_string_refresh_string_list($strings);
    $context['results'][$group]['result'] = TRUE;
  }
  $context['results'][$group]['refresh'] = $count;
}

/**
 * Batch operation: Invoke i18n_string_refresh
 */
function _i18n_string_batch_refresh_callback($group, &$context) {
  $result = module_invoke_all('i18n_string_refresh', $group);
  $count = $result ? array_sum($result) : 0;
  $context['results'][$group]['refresh'] += $count;
  if ($count) {
    $context['results'][$group]['result'] = TRUE;
  }
}

/**
 * Batch callback, delete old strings
 */
function _i18n_string_batch_refresh_cleanup($group, &$context) {
  if (!empty($context['results'][$group]['result'])) {
    $context['results'][$group]['deleted'] = i18n_string_refresh_cleanup($group);
  }
}

/**
 * Batch operations: Print refresh summary for group
 */
function _i18n_string_batch_refresh_summary($group, &$context) {
  if ($context['results'][$group]['result']) {
    drupal_set_message(t("Successfully refreshed @count strings for %group", array('@count' => $context['results'][$group]['refresh'], '%group' => i18n_string_group_info($group, 'title'))));
    if (!empty($context['results'][$group]['deleted'])) {
      drupal_set_message(t('Deleted @count left over strings.', array('@count' => $context['results'][$group]['deleted'])));
    }
  }
  else {
    drupal_set_message(t("Cannot refresh strings for %group.", array('%group' => i18n_string_group_info($group, 'title'))), 'warning');
  }
}

/**
 * Get all strings for a text group
 */
function i18n_string_group_string_list($group) {
  // Add strings provided by all modules on hook_string_list().
  // Note that i18n_string module itself will also collect all strings for this group's objects
  $strings = module_invoke_all('i18n_string_list', $group);
  // Invoke hook_i18n_string_list_TEXTGROUP_alter()
  drupal_alter('i18n_string_list_' . $group, $strings);
  return $strings;
}

/**
 * Get all strings from a module.
 */
function i18n_string_module_string_list($module) {
  $strings = array();
  // Try loading i18n.inc for the module and some library functions.
  module_load_include('i18n.inc', $module);
  module_load_include('i18n.inc', 'i18n_string');
  // If the module defines any textgroup, get all strings for this group
  if ($groups = module_invoke($module, 'i18n_string_info')) {
    foreach ($groups as $group) {
      $strings = i18n_string_array_merge($strings, i18n_string_group_string_list($group));
    }   
  }
  else {
    $groups = array();
  }
  // The module may implement i18n_string_list()
  if ($string_list = module_invoke($module, 'i18n_string_list', 'all')) {
    foreach ($string_list as $group => $group_strings) {
      if (!in_array($group, $groups)) {
        $strings[$group] = $group_strings;
      }
    }
  }
  // If the module defines any object that has strings in another textgroup
  if ($object_types = module_invoke($module, 'i18n_object_info')) {
    foreach ($object_types as $type => $type_info) {
      if (($group = i18n_string_object_info($type, 'textgroup')) && !in_array($group, $groups)) {
        if ($group_strings = i18n_string_object_type_string_list($type)) {
          $strings = i18n_string_array_merge($strings, $group_strings);
        }
      }
    }
  }
  return $strings;
}

/**
 * User interface for string editing.
 */
function i18n_string_locale_translate_edit_form($form, &$form_state, $lid) {
  // Fetch source string, if possible.
  $source = db_query('SELECT source, context, textgroup, location FROM {locales_source} WHERE lid = :lid', array(':lid' => $lid))->fetchObject();
  if (!$source) {
    drupal_set_message(t('String not found.'), 'error');
    drupal_goto('admin/config/regional/translate/translate');
  }

  // Add original text to the top and some values for form altering.
  $form['original'] = array(
    '#type'  => 'item',
    '#title' => t('Original text'),
    '#markup' => check_plain(wordwrap($source->source, 0)),
  );
  if (!empty($source->context)) {
    $form['context'] = array(
      '#type' => 'item',
      '#title' => t('Context'),
      '#markup' => check_plain($source->context),
    );
  }
  $form['lid'] = array(
    '#type'  => 'value',
    '#value' => $lid
  );
  $form['textgroup'] = array(
    '#type'  => 'value',
    '#value' => $source->textgroup,
  );
  $form['location'] = array(
    '#type'  => 'value',
    '#value' => $source->location
  );

  // Include default form controls with empty values for all languages.
  // This ensures that the languages are always in the same order in forms.
  $languages = language_list();

  // We don't need the default language value, that value is in $source.
  $omit = $source->textgroup == 'default' ? 'en' : i18n_string_source_language();
  unset($languages[($omit)]);
  $form['translations'] = array('#tree' => TRUE);
  // Approximate the number of rows to use in the default textarea.
  $rows = min(ceil(str_word_count($source->source) / 12), 10);
  foreach ($languages as $langcode => $language) {
    $form['translations'][$langcode] = array(
      '#type' => 'textarea',
      '#title' => t($language->name),
      '#rows' => $rows,
      '#default_value' => '',
    );
  }

  // Fetch translations and fill in default values in the form.
  $result = db_query("SELECT DISTINCT translation, language FROM {locales_target} WHERE lid = :lid AND language <> :omit", array(':lid' => $lid, ':omit' => $omit));
  foreach ($result as $translation) {
    $form['translations'][$translation->language]['#default_value'] = $translation->translation;
  }

  $form['actions'] = array('#type' => 'actions');
  $form['actions']['submit'] = array('#type' => 'submit', '#value' => t('Save translations'));
  
  // Restrict filter permissions and handle validation and submission for i18n strings
  $context = db_select('i18n_string', 'i18ns')
    ->fields('i18ns')
    ->condition('lid', $form['lid']['#value'])
    ->execute()
    ->fetchObject();
  if ($source->textgroup != 'default' && $context) {
    $form['i18n_string_context'] = array('#type' => 'value', '#value' => $context);
    if ($context->format) {
      $formats = filter_formats();
      $format = $formats[$context->format];
      $disabled = !filter_access($format);
      if ($disabled) {
        drupal_set_message(t('This string uses the %name text format. You are not allowed to translate or edit texts with this format.', array('%name' => $format->name)), 'warning');
      }
      foreach (element_children($form['translations']) as $langcode) {
        $form['translations'][$langcode]['#disabled'] = $disabled;
      }
      $form['translations']['format_help'] = array(
        '#type' => 'markup',
        '#markup' => '<h5>' . t('Text format: @name', array('@name' => $format->name)) . '</h5>' . theme('filter_tips', array('tips' => _filter_tips($context->format, FALSE))),
      );
      $form['submit']['#disabled'] = $disabled;
    }
  }
  return $form;
}

/**
 * Process string editing form validations.
 *
 * If it is an allowed format, skip default validation, the text will be filtered later
 */
function i18n_string_locale_translate_edit_form_validate($form, &$form_state) {
  if (empty($form_state['values']['i18n_string_context']) || empty($form_state['values']['i18n_string_context']->format)) {
    // If not text format use regular validation for all strings
    $copy_state = $form_state;
    $copy_state['values']['textgroup'] = 'default';
    module_load_include('admin.inc', 'locale');
    locale_translate_edit_form_validate($form, $copy_state);
  }
  elseif (!i18n_string_translate_access($form_state['values']['i18n_string_context'])) {
    form_set_error('translations', t('You are not allowed to translate or edit texts with this text format.'));
  }
}

/**
 * Process string editing form submissions.
 *
 * Mark translations as current.
 */
function i18n_string_locale_translate_edit_form_submit($form, &$form_state) {
  // Invoke locale submission.
  module_load_include('admin.inc', 'locale');
  locale_translate_edit_form_submit($form, $form_state);
  $lid = $form_state['values']['lid'];
  foreach ($form_state['values']['translations'] as $key => $value) {
    if (!empty($value)) {
      // An update has been made, so we assume the translation is now current.
      db_update('locales_target')
        ->fields(array('i18n_status' => I18N_STRING_STATUS_CURRENT))
        ->condition('lid', $lid)
        ->condition('language', $key)
        ->execute();
    }
  }
}
