<?php

/**
 * Implements hook_views_data().
 */
function search_api_views_views_data() {
  $data = array();
  $entity_types = entity_get_info();
  foreach (search_api_index_load_multiple(FALSE) as $index) {
    // Base data
    $key = 'search_api_index_' . $index->machine_name;
    $table = &$data[$key];
    $entity_info = entity_get_info($index->entity_type);
    $table['table']['group'] = $entity_info['label'];
    $table['table']['base'] = array(
      'field' => 'search_api_id',
      'index' => $index->machine_name,
      'title' => $index->name,
      'help' => t('Use the %name search index for filtering and retrieving data.', array('%name' => $index->name)),
      'query class' => 'search_api_views_query',
      'entity type' => $index->entity_type,
      'skip entity load' => TRUE,
    );

    // Add all available fields
    // This is largely copied from _search_api_admin_get_fields().
    $max_depth = variable_get('search_api_views_max_fields_depth', 2);
    $orig_wrapper = $index->entityWrapper();
    $fields = empty($index->options['fields']) ? array() : $index->options['fields'];

    // A wrapper for a specific field name prefix, e.g. 'user:' mapped to the user wrapper
    $wrappers = array('' => $orig_wrapper);
    // Display names for the prefixes
    $prefix_names = array('' => '');
    // The list nesting level for entities with a certain prefix
    $nesting_levels = array('' => 0);

    $types = search_api_field_types();
    $types['options'] = t('Options');
    while ($wrappers) {
      foreach ($wrappers as $prefix => $wrapper) {
        $prefix_name = $prefix_names[$prefix];
        $depth = substr_count($prefix, ':');
        // Deal with lists of entities.
        $nesting_level = $nesting_levels[$prefix];
        $type_prefix = str_repeat('list<', $nesting_level);
        $type_suffix = str_repeat('>', $nesting_level);
        if ($nesting_level) {
          $info = $wrapper->info();
          // The real nesting level of the wrapper, not the accumulated one.
          $level = search_api_list_nesting_level($info['type']);
          for ($i = 0; $i < $level; ++$i) {
            $wrapper = $wrapper[0];
          }
        }
        // Now look at all properties.
        foreach ($wrapper as $property => $value) {
          $field = $prefix . $property;
          $key = _search_api_views_field_identifier($field, $table);
          $info = $value->info();
          // Some fields of contrib entities might have incomplete definitions.
          $info += array('label' => $field);
          $type = $type_prefix . $info['type'] . $type_suffix;
          $inner_type = search_api_extract_inner_type($info['type']);
          if ($inner_type == 'token') {
            $inner_type = 'string';
            $type = search_api_nest_type('string', $type);
          }

          if (isset($types[$inner_type]) || isset($entity_types[$inner_type])) {
            if (isset($types[$inner_type]) && $value->optionsList('view')) {
              $inner_type = 'options';
              $type = search_api_nest_type('options', $type);
            }
            // Add field handler.
            if ($prefix_name) {
              $table[$key]['group'] = $prefix_name;
            }
            $table[$key]['title'] = $info['label'];
            if ($key != $field) {
              $table[$key]['real field'] = $field;
            }
            $table[$key]['help'] = empty($info['description']) ? t('(No information available)') : $info['description'];
            $table[$key]['type'] = $type;
            $table[$key]['field']['handler'] = _search_api_views_field_handler($type, $inner_type);
            $table[$key]['field']['type'] = $type;
            if ($inner_type == 'options') {
              $table[$key]['field']['options'] = $value->optionsList('view');
            }

            // If field is indexed, also add additional handlers.
            if (!empty($fields[$field]['indexed'])) {
              // Discern between original and indexed type
              $table[$key]['field']['type'] = $table[$key]['type'];
              $table[$key]['type'] = $fields[$field]['type'];
              $table[$key] += _search_api_views_add_handlers($fields[$field], $value);
              if (!empty($table[$key]['sort'])) {
                $table[$key]['field']['click sortable'] = TRUE;
              }
            }
            unset($fields[$field]);
          }
          if (!isset($types[$inner_type]) && $depth < $max_depth) {
            // Visit this entity/struct in a later iteration.
            $field .= ':';
            $wrappers[$field] = $value;
            $prefix_names[$field] = $prefix_name ? $prefix_name . ' » ' . $info['label'] : $info['label'];
            $nesting_levels[$field] = search_api_list_nesting_level($type);
          }
        }
        unset($wrappers[$prefix]);
      }
    }

    // Add handlers for all indexed fields which weren't processed yet.
    foreach ($fields as $key => $field) {
      if (empty($field['indexed'])) {
        continue;
      }
      $tmp = $orig_wrapper;
      $group = '';
      $name = '';
      $parts = explode(':', $key);
      foreach ($parts as $i => $part) {
        if (!isset($tmp->$part)) {
          continue 2;
        }
        $tmp = $tmp->$part;
        $info = $tmp->info();
        $group = ($group ? $group . ' » ' . $name : ($name ? $name : ''));
        $name = $info['label'];
        if ($i < count($parts) - 1) {
          // Unwrap lists.
          $level = search_api_list_nesting_level($info['type']);
          for ($j = 0; $j < $level; ++$j) {
            $tmp = $tmp[0];
          }
        }
      }
      $id = _search_api_views_field_identifier($key, $table);
      if ($group) {
        $table[$id]['group'] = $group;
      }
      $table[$id]['title'] = $name;
      $table[$id]['help'] = empty($info['description']) ? NULL : $info['description'];
      $table[$id]['type'] = $field['type'];
      $table[$id] += _search_api_views_add_handlers($field, $tmp);
    }

    // Special handlers
    $table['search_api_id']['title'] = t('Entity ID');
    $table['search_api_id']['help'] = t("The entity's ID.");
    $table['search_api_id']['type'] = 'integer';
    $table['search_api_id']['sort']['handler'] = 'SearchApiViewsHandlerSort';

    $table['search_api_relevance']['group'] = t('Search');
    $table['search_api_relevance']['title'] = t('Relevance');
    $table['search_api_relevance']['help'] = t('The relevance of this search result with respect to the query.');
    $table['search_api_relevance']['type'] = 'decimal';
    $table['search_api_relevance']['field']['handler'] = _search_api_views_field_handler('decimal', 'decimal');
    $table['search_api_relevance']['field']['click sortable'] = TRUE;
    $table['search_api_relevance']['sort']['handler'] = 'SearchApiViewsHandlerSort';

    $table['search_api_excerpt']['group'] = t('Search');
    $table['search_api_excerpt']['title'] = t('Excerpt');
    $table['search_api_excerpt']['help'] = t('The search result excerpted to show found search terms.');
    $table['search_api_excerpt']['type'] = 'text';
    $table['search_api_excerpt']['field']['handler'] = 'SearchApiViewsHandlerField';

    $table['search_api_views_fulltext']['group'] = t('Search');
    $table['search_api_views_fulltext']['title'] = t('Fulltext search');
    $table['search_api_views_fulltext']['help'] = t('Search several or all fulltext fields at once.');
    $table['search_api_views_fulltext']['type'] = 'text';
    $table['search_api_views_fulltext']['filter']['handler'] = 'SearchApiViewsHandlerFilterFulltext';
    $table['search_api_views_fulltext']['argument']['handler'] = 'SearchApiViewsHandlerArgumentFulltext';

    $table['search_api_views_more_like_this']['group'] = t('Search');
    $table['search_api_views_more_like_this']['title'] = t('More like this');
    $table['search_api_views_more_like_this']['help'] = t('Find similar content.');
    $table['search_api_views_more_like_this']['type'] = 'text';
    $table['search_api_views_more_like_this']['argument']['handler'] = 'SearchApiViewsHandlerArgumentMoreLikeThis';
  }
  return $data;
}

/**
 * Helper function for creating valid Views field identifiers out of Search API field names.
 *
 * @return string
 *   A valid Views field identifier that isn't yet used as a key in $table.
 */
function _search_api_views_field_identifier($field, array $table) {
  $key = $base = preg_replace('/[^a-zA-Z0-9]+/S', '_', $field);
  $i = 0;
  while (isset($table[$key])) {
    $key = $base . '_' . ++$i;
  }
  return $key;
}

/**
 * Helper function for selecting a handler for a field of a specific type.
 */
function _search_api_views_field_handler($type, $inner_type) {
  switch ($inner_type) {
    case 'boolean':
      return 'SearchApiViewsHandlerFieldBoolean';
    case 'date':
      return 'SearchApiViewsHandlerFieldDate';
    case 'duration':
      return 'SearchApiViewsHandlerFieldDuration';
    case 'options':
      return 'SearchApiViewsHandlerFieldOptions';

    default:
      if (entity_get_info($inner_type)) {
        return 'SearchApiViewsHandlerFieldEntity';
      }
      return 'SearchApiViewsHandlerField';
  }
}

/**
 * Helper function that returns an array of handler definitions to add to a
 * views field definition.
 */
function _search_api_views_add_handlers($field, $wrapper) {
  $type = $field['type'];
  $inner_type = search_api_extract_inner_type($type);
  if ($inner_type == 'text') {
    return array(
      'argument' => array(
        'handler' => 'SearchApiViewsHandlerArgumentText',
      ),
      'filter' => array(
        'handler' => 'SearchApiViewsHandlerFilterText',
      ),
    );
  }

  $ret = array();

  if (empty($field['entity_type']) && $options = $wrapper->optionsList('view')) {
    $ret['filter']['handler'] = 'SearchApiViewsHandlerFilterOptions';
    $ret['filter']['options'] = $options;
  }
  elseif ($inner_type == 'boolean') {
    $ret['filter']['handler'] = 'SearchApiViewsHandlerFilterBoolean';
  }
  elseif ($inner_type == 'date') {
    $ret['filter']['handler'] = 'SearchApiViewsHandlerFilterDate';
  }
  else {
    $ret['filter']['handler'] = 'SearchApiViewsHandlerFilter';
  }

  $ret['argument']['handler'] = 'SearchApiViewsHandlerArgument';

  // We can only sort according to single-valued fields.
  if ($type == $inner_type) {
    $ret['sort']['handler'] = 'SearchApiViewsHandlerSort';
  }

  return $ret;
}

/**
 * Implements hook_views_plugins().
 */
function search_api_views_views_plugins() {
  $ret = array(
    'query' => array(
      'search_api_views_query' => array(
        'title' => t('Search API Query'),
        'help' => t('Query will be generated and run using the Search API.'),
        'handler' => 'SearchApiViewsQuery'
      ),
    ),
  );

  if (module_exists('search_api_facets')) {
    $ret['display']['search_api_views_facets_block'] = array(
      'title' => t('Facets block'),
      'help' => t('Display facets for this search as a block anywhere on the site.'),
      'handler' => 'SearchApiViewsFacetsBlockDisplay',
      'uses hook block' => TRUE,
      'use ajax' => FALSE,
      'use pager' => FALSE,
      'use more' => TRUE,
      'accept attachments' => TRUE,
      'admin' => t('Facets block'),
    );
  }

  return $ret;
}
