<?php

/**
 * Displays a form for selecting the indexed fields for which range facets 
 * should be created.
 *
 * @param SearchApiIndex $index
 *   The index for which to display the settings.
 */
function search_api_ranges_index_select(array $form, array &$form_state, SearchApiIndex $index) {
  module_load_include('admin.inc', 'search_api');
  drupal_set_title(search_api_admin_item_title($index));
  $form_state['index'] = $index;

  if (!$index->server() || !$index->server()->supportsFeature('search_api_facets')) {
    // We can't offer any range facets, but maybe the user can delete old range facet data.
    drupal_set_message(t("The server this index currently lies on doesn't support range facets. " .
        'To use range facets, you will have to move this index to a server supporting this feature.'), 'error');
    if (search_api_range_load_multiple(FALSE, array('index_id' => $index->machine_name))) {
      $form['description'] = array(
        '#type' => 'item',
        '#title' => t('Delete range facet settings'),
        '#description' => t("If you won't use range facets with this index anymore, you can delete all range facet data associated with it. " .
            "This will delete all settings for range facets on this index. Range facets on other indexes won't be influenced."),
      );
      $form['button'] = array(
        '#type' => 'submit',
        '#value' => t('Delete range facet settings'),
        '#submit' => array('search_api_ranges_index_select_submit_delete'),
      );
    }
    return $form;
  }

  $form['description'] = array(
    '#type' => 'item',
    '#title' => t('Select the available range facet blocks'),
    '#description' => t('<p>All enabled blocks will be shown, along with their names, on the block administration page. ' .
        'For your own convenience, choose unique names. ' .
        'By cloning blocks, you can have multiple range facet blocks with seperate configurations for a single index field.</p>' .
        "<p>When you won't be using a disabled range facet anymore, you can delete all its settings permanently, reducing database size. " .
        "This won't influence other range facets. You can enable it again later, but without the previously stored settings.</p>" .
        "<p>* Checkboxes with an asterisk denote overridden range facet configurations. These cannot be deleted, but only be reset to their defaults.</p>"),
  );
  $form['ranges'] = array(
    '#tree' => TRUE,
    '#theme' => 'search_api_ranges_form_table',
    '#table_header' => array(
      t('Enabled'),
      t('Field'),
      t('Type'),
      t('Name'),
      t('Delete data'),
    ),
    '#table_empty' => t('There are currently no fields for which range facets can be displayed.'),
  );

  if (!empty($index->options['fields'])) {
    $ranges = array();
    if ($disabled = empty($index->enabled)) {
      drupal_set_message('Since this index is at the moment disabled, no range facet blocks can be activated.', 'warning');
    }
    $show_status = FALSE;
    foreach (search_api_range_load_multiple(FALSE, array('index_id' => $index->machine_name)) as $range) {
      $ranges[$range->field][] = $range;
      if ($range->status & ENTITY_IN_CODE) {
        $show_status = TRUE;
      }
    }
    if ($show_status) {
      $form['ranges']['#table_header'] = array(
        t('Enabled'),
        t('Status'),
        t('Field'),
        t('Type'),
        t('Name'),
        t('Delete data'),
      );
      $empty_status = ' ';
    }
    $types = search_api_field_types();
    foreach ($index->options['fields'] as $key => $field) {
      if (!$field['indexed']) {
        continue;
      }
      // skip fulltext or multi-value
      if ($field['type'] == 'text' || $field['type'] == 'string' || strpos($field['type'], 'list<') !== FALSE) {
        continue;
      }
      $type = search_api_extract_inner_type($field['type']);
      $type = isset($types[$type]) ? $types[$type] : $type;
      if (empty($ranges[$key])) {
        $ranges[$key][] = new SearchApiRange(array(
          'index_id' => $index->machine_name,
          'field' => $key,
          'name' => t('!field', array('!field' => $field['name'])),
          'enabled' => 0,
          'status' => 0,
        ));
      }
      foreach ($ranges[$key] as $i => $range) {
        $k = $i ? "$key-$i" : $key;
        $form['ranges'][$k]['range'] = array(
          '#type' => 'value',
          '#value' => $range,
        );
        $form['ranges'][$k]['enabled'] = array(
          '#type' => 'checkbox',
          '#default_value' => $range->enabled,
          '#disabled' => $disabled,
        );
        if ($show_status) {
          $form['ranges'][$k]['status']['#markup'] = $range->status ? theme('entity_status', array('status' => $range->status)) : $empty_status;
        }
        $form['ranges'][$k]['field'] = array(
          '#markup' => $field['name'],
        );
        $form['ranges'][$k]['type'] = array(
          '#markup' => $type,
        );
        $form['ranges'][$k]['name'] = array(
          '#type' => 'textfield',
          '#maxlength' => max(strlen($range->name), 80),
          '#default_value' => $range->name,
        );
        if (($range->enabled && $range->status != ENTITY_OVERRIDDEN) || empty($range->delta) || !($range->status & ENTITY_CUSTOM)) {
          $form['ranges'][$k]['delete'] = array(
            '#markup' => '&nbsp;',
          );
        }
        else {
          $form['ranges'][$k]['delete'] = array(
            '#type' => 'checkbox',
            '#default_value' => FALSE,
          );
          if ($range->status & ENTITY_IN_CODE) {
            $form['ranges'][$k]['delete']['#title'] = '*';
          }
        }
      }
    }
  }

  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => t('Save settings'),
  );

  return $form;
}

/**
 * Validation callback for search_api_ranges_index_select().
 */
function search_api_ranges_index_select_validate(array $form, array &$form_state) {
  $warn = FALSE;
  foreach ($form_state['values']['ranges'] as $key => $v) {
    if (!empty($v['range']->delta) || $v['enabled']) {
      // -> This setting will be stored.
      if (!$v['name']) {
        form_set_error("ranges][$key][name", t("You can't set an empty name."));
      }
      elseif (strlen($v['name']) > 80) {
        form_set_error("ranges][$key][name", t('Names cannot be longer than 80 characters, but "@name" is @count characters long.',
            array('@name' => $v['name'], '@count' => strlen($v['name']))));
      }
    }
    elseif ($v['range']->name != $v['name']) {
      $warn = TRUE;
    }
  }
  if ($warn) {
    drupal_set_message(t('Note that changed names of range facet blocks that are disabled and have no settings yet, are not saved.'), 'warning');
  }
}

/**
 * Submit callback for search_api_ranges_index_select().
 */
function search_api_ranges_index_select_submit(array $form, array &$form_state) {
  $index = $form_state['index'];
  $count = 0;
  $delete_count = 0;
  $reset_count = 0;

  $counts = array();
  foreach ($form_state['values']['ranges'] as $key => $v) {
    $field = $v['range']->field;
    $c = $counts[$field] = (isset($counts[$field]) ? $counts[$field] + 1 : 1);
    if (empty($v['enabled']) && empty($v['range']->delta)) {
      // Field is not stored, what use would cloning or deleting be?
      continue;
    }
    if ((empty($v['enabled'])) || $v['range']->status == ENTITY_OVERRIDDEN) {
      // This is the only case in which a "delete" makes sense.
      if (!empty($v['delete'])) {
        $v['range']->delete();
        $v['range']->status == ENTITY_OVERRIDDEN ? ++$reset_count : ++$delete_count;
        continue;
      }
    }
    $f = $v['range'];
    $update = $f->enabled != $v['enabled'] || $f->name != $v['name'];
    if ($update) {
      $f = clone $f;
      $f->enabled = $v['enabled'];
      $f->name = $v['name'];
      $f->save();
      ++$count;
    }
  }

  if ($delete_count) {
    drupal_set_message(format_plural($delete_count, 'The settings of 1 range facet block were successfully deleted.', 'The settings of @count range facet blocks were successfully deleted.'));
  }
  if ($count) {
    drupal_set_message(format_plural($count, '1 range facet block was successfully updated.', '@count range facet blocks were successfully updated.'));
  }
  if ($reset_count) {
    drupal_set_message(format_plural($reset_count, '1 range facet configuration was reset.', '@count range facet configurations were reset.'));
  }
  if (!($count + $delete_count + $reset_count)) {
    drupal_set_message(t('No values were changed.'));
  }
}

/**
 * Submit callback for search_api_ranges_index_select(), when clicking on
 * "Delete range facet settings".
 */
function search_api_ranges_index_select_submit_delete(array $form, array &$form_state) {
  $index = $form_state['index'];
  $ret = search_api_range_delete_multiple(array('index_id' => $index->machine_name));
  if ($ret) {
    drupal_set_message(t('All range facet data stored for this index was deleted.'));
  }
  else {
    drupal_set_message(t('There was no data to delete.'), 'warning');
  }
  $form_state['redirect'] = 'admin/config/search/search_api/index/' . $index->machine_name;
}

/**
 * Theming function for rendering a form as a table.
 *
 * @param array $variables
 *   An array of variables to use, containing only one entry:
 *   - element: The sub-form to render as a table.
 *
 * @return string
 *   HTML displaying the specified sub-form as a table.
 */
function theme_search_api_ranges_form_table(array $variables) {
  $form = $variables['element'];

  $rows = array();
  foreach (element_children($form) as $id) {
    $row = array();
    foreach (element_children($form[$id]) as $field) {
      if ($cell = render($form[$id][$field])) {
        $row[] = $cell;
      }
    }
    $rows[] = $row;
  }

  $vars['rows'] = $rows;
  if (isset($form['#table_header'])) {
    $vars['header'] = $form['#table_header'];
  }
  if (isset($form['#table_empty'])) {
    $vars['empty'] = $form['#table_empty'];
  }
  return theme('table', $vars);
}
