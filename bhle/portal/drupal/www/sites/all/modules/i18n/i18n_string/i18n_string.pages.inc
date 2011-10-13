<?php

/**
 * @file
 * Internationalization (i18n) package - translatable strings reusable admin UI.
 *
 * @author Jose A. Reyero, 2007
 */

/**
 * Generate translate page from object
 */
function i18n_string_translate_page_object($object_type, $object_value, $langcode = NULL) {
  // Get base keys for all these strings. Object key may be multiple like for blocks (module, delta)
  $object = i18n_object($object_type, $object_value);
  $strings = $object->get_strings(array('empty' => TRUE));

  if (empty($langcode)) {
    drupal_set_title(t('Translate @name', array('@name' => i18n_object_info($object_type, 'title'))));
    return i18n_string_translate_page_overview($object, $strings);
  }
  else {
    drupal_set_title(t('Translate to @language', array('@language' => i18n_language_name($langcode))));
    return drupal_get_form('i18n_string_translate_page_form', $strings, $langcode);
  }
}

/**
 * Provide a core translation module like overview page for this object.
 */
function i18n_string_translate_page_overview($object, $strings) {
  include_once DRUPAL_ROOT . '/includes/language.inc';
  // Set the default item key, assume it's the first.
  $item_title = reset($strings);
  $header = array(t('Language'), t('Title'), t('Status'), t('Operations'));
  $source_language = variable_get_value('i18n_string_source_language');
  $rows = array();

  foreach (language_list() as $langcode => $language) {
    if ($langcode == $source_language) {
      $rows[] = array(
        $language->name . ' ' . t('(source)'),
        check_plain($item_title->get_string()),
        t('original'),
        l(t('edit'), $object->get_edit_path()),
      );
    }
    else {
      // Try to figure out if this item has any of its properties translated.
      $translated = FALSE;
      foreach ($strings as $i18nstring) {
        if ($i18nstring->get_translation($langcode)) {
          $translated = TRUE;
          break;
        }
      }
      // Translate the item that was requested to be displayed as title.
      $rows[] = array(
        $language->name,
        $item_title->format_translation($langcode, array('sanitize default' => TRUE)),
        $translated ? t('translated') : t('not translated'),
        l(t('translate'), $object->get_translate_path($langcode), array('query' => drupal_get_destination())),
      );
    }
  }

  $build['i18n_string_translation_overview'] = array(
    '#theme' => 'table',
    '#header' => $header,
    '#rows' => $rows,
  );

  return $build;
}

/**
 * Form builder callback for in-place string translation.
 */
function i18n_string_translate_page_form($form, &$form_state, $strings, $langcode) {
  $form = i18n_string_translate_page_form_base($form, $langcode);
  $form['strings'] += i18n_string_translate_page_form_strings($strings, $langcode);
  return $form;
}

/**
 * Create base form for string translation
 */
function i18n_string_translate_page_form_base($form, $langcode, $redirect = NULL) {
  $form['langcode'] = array(
    '#type' => 'value',
    '#value' => $langcode,
  );
  $form['strings'] = array(
    // Use a tree, so we can access the values easily.
    '#tree' => TRUE,
  );
  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => t('Save translation'),
  );
  if ($redirect) {
    $form['#redirect'] = array(
      $redirect,
    );
  }
  // Add explicit validate and submit hooks so this can be used from inside any form.
  $form['#validate'] = array('i18n_string_translate_page_form_validate');
  $form['#submit'] = array('i18n_string_translate_page_form_submit');
  return $form;  
}

/**
 * Create field elements for strings
 */
function i18n_string_translate_page_form_strings($strings, $langcode) {
  $formats = filter_formats();
  foreach ($strings as $item) {
    $disabled = FALSE;
    $description = '';
    // We may have a source or not. Maybe the format is disallowed for all.
    $source = $item->get_source();
    $format_id = $source ? $source->format : $item->format;
    if ($format_id) {
      $format = filter_format_load($format_id);
      $disabled = !i18n_string_translate_access($item);
      if ($disabled) {
        $description = t('This string uses the %name text format. You are not allowed to translate or edit texts with this format.', array('%name' => $format->name));
      }
      else {
        $description = '<h5>' . t('Text format: @name', array('@name' => $format->name)) . '</h5>' . theme('filter_tips', array('tips' => _filter_tips($format->format, FALSE)));
      }
    }
    // If we don't have a source we create it
    if (!$source && empty($disabled)) {
      $item->update(array('messages' => TRUE));
      $source = $item->get_source();
    }
    $default_value = $item->format_translation($langcode, array('langcode' => $langcode, 'sanitize' => FALSE, 'debug' => FALSE));
    $form[$item->get_name()] = array(
      '#title' => $item->get_title(),
      '#type' => 'textarea',
      '#default_value' => $default_value,
      '#disabled' => $disabled,
      '#description' => $description,
      '#i18n_string_format' => !empty($source) ? $source->format : 0,
      '#rows' => min(ceil(str_word_count($default_value) / 12), 10),
      '#parents' => array('strings', $item->get_name()),
    );
  }
  return $form; 
}

/**
 * Validation submission callback for in-place string translation.
 */
function i18n_string_translate_page_form_validate($form, &$form_state) {
  foreach ($form_state['values']['strings'] as $key => $value) {
    // We don't need to validate disabled form fields because those are already
    // validated by the FormAPI.
    if (empty($form['strings'][$key]['#i18n_string_format'])) {
      i18n_string_validate_submission("strings][$key", $value);
    }
  }
}

/**
 * Form submission callback for in-place string translation.
 */
function i18n_string_translate_page_form_submit($form, &$form_state) {
  foreach ($form_state['values']['strings'] as $key => $value) {
    list($textgroup, $context) = i18n_string_context(explode(':', $key));
    i18n_string_textgroup($textgroup)->update_translation($context, $form_state['values']['langcode'], $value);
  }
  drupal_set_message(t('Translations saved.'));
  if (isset($form['#redirect'])) {
    $form_state['redirect'] = $form['#redirect'];
  }
}

/**
 * String submission validation callback.
 */
function i18n_string_validate_submission($formkey, $value) {
  // Validation based on locale_translate_edit_form_validate.
  if (!locale_string_is_safe($value)) {
    form_set_error($formkey, t('The submitted string contains disallowed HTML: %string', array('%string' => $value)));
    watchdog('locale', 'Attempted submission of a translation string with disallowed HTML: %string', array('%string' => $value), WATCHDOG_WARNING);
  }
}

/**
 * Menu callback. Saves a string translation coming as POST data.
 */
function i18n_string_l10n_client_save_string() {
  global $user, $language;

  if (user_access('use on-page translation')) {
    $textgroup = !empty($_POST['textgroup']) ? $_POST['textgroup'] : 'default';
    // Default textgroup will be handled by l10n_client module
    if ($textgroup == 'default') {
      return l10n_client_save_string();
    }
    elseif (isset($_POST['source']) && isset($_POST['target']) && !empty($_POST['context']) && !empty($_POST['form_token']) && drupal_valid_token($_POST['form_token'], 'l10n_client_form')) {
      $name = $textgroup . ':' . $_POST['context'];
      $result = i18n_string_translation_update($name, $_POST['target'], $language->language, $_POST['source']);
      if ($result) {
        $message = theme('l10n_client_message', array('message' => t('Translation saved locally for user defined string.'), 'level' => WATCHDOG_INFO));
      }
      elseif ($result === FALSE) {
        $message = theme('l10n_client_message', array('message' => t('Not saved due to insufficient permissions.')));     
      }
      else {
        $message = theme('l10n_client_message', array('message' => t('Not saved due to source string missing.')));
      }
    }
    else {
      $message = theme('l10n_client_message', array('message' => t('Not saved due to missing form values.')));
    }
    drupal_json_output($message);
    exit;
  }
}
