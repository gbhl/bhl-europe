<?php

/**
 * Displays a search page.
 *
 * @param $id
 *   The search page's ID.
 * @param $keys
 *   The keys to search for.
 */
function search_api_page_view($id, $keys = NULL) {
  $page = search_api_page_load((int) $id);
  if (!$page) {
    return MENU_NOT_FOUND;
  }

  $ret['form'] = drupal_get_form('search_api_page_search_form', $page, $keys);

  if ($keys) {
    $results = search_api_page_search_execute($page, $keys);

    // If spellcheck results are returned then add them to the render array.
    if (isset($results['search_api_spellcheck'])) {
      $ret['search_api_spellcheck']['#theme'] = 'search_api_spellcheck';
      $ret['search_api_spellcheck']['#spellcheck'] = $results['search_api_spellcheck'];
      // Let the theme function know where the key is stored by passing its arg
      // number. We can work this out from the number of args in the page path.
      $ret['search_api_spellcheck']['#options'] = array(
        'arg' => array(count(arg(NULL, $page->path))),
      );
    }

    $ret['results']['#theme'] = 'search_api_page_results';
    $ret['results']['#index'] = search_api_index_load($page->index_id);
    $ret['results']['#results'] = $results;
    $ret['results']['#view_mode'] = isset($page->options['view_mode']) ? $page->options['view_mode'] : 'search_api_page_result';
    $ret['results']['#keys'] = $keys;

    if ($results['result count'] > $page->options['per_page']) {
      pager_default_initialize($results['result count'], $page->options['per_page']);
      $ret['pager']['#theme'] = 'pager';
      $ret['pager']['#quantity'] = 9;
    }

    if (!empty($results['ignored'])) {
      drupal_set_message(t('The following search keys are too short or too common and were therefore ignored: "@list".', array('@list' => implode(t('", "'), $results['ignored']))), 'warning');
    }
    if (!empty($results['warnings'])) {
      foreach ($results['warnings'] as $warning) {
        drupal_set_message($warning, 'warning');
      }
    }
  }

  return $ret;
}

/**
 * Executes a search.
 *
 * @param stdClass $page
 *   The page for which a search should be executed.
 * @param $keys
 *   The keywords to search for.
 *
 * @return array
 *   The search results as returned by SearchApiQueryInterface::execute().
 */
function search_api_page_search_execute(stdClass $page, $keys) {
  $limit = $page->options['per_page'];
  $offset = pager_find_page() * $limit;
  $options = array(
    'search id' => 'search_api_page:' . $page->path,
    'parse mode' => $page->options['mode'],
  );

  if (!empty($page->options['search_api_spellcheck'])) {
    $options['search_api_spellcheck'] = TRUE;
  }

  $query = search_api_query($page->index_id, $options)
    ->keys($keys)
    ->range($offset, $limit);
  if (!empty($page->options['fields'])) {
    $query->fields($page->options['fields']);
  }
  return $query->execute();
}

/**
 * Function for preprocessing the variables for the search_api_page_results
 * theme.
 *
 * @param array $variables
 *   An associative array containing:
 *   - index: The index this search was executed on.
 *   - results: An array of search results, as returned by
 *     SearchApiQueryInterface::execute().
 *   - keys: The keywords of the executed search.
 */
function template_preprocess_search_api_page_results(array &$variables) {
  if (!empty($variables['results']['results'])) {
    $variables['entities'] = entity_load($variables['index']->entity_type, array_keys($variables['results']['results']));
  }
}

/**
 * Theme function for displaying search results.
 *
 * @param array $variables
 *   An associative array containing:
 *   - index: The index this search was executed on.
 *   - results: An array of search results, as returned by
 *     SearchApiQueryInterface::execute().
 *   - entities: The loaded entities for all results, in an array keyed by ID.
 *   - "view_mode": The view mode to use for displaying the individual results,
 *     or the special mode "search_api_page_result" to use the theme function
 *     of the same name.
 *   - keys: The keywords of the executed search.
 */
function theme_search_api_page_results(array $variables) {
  drupal_add_css(drupal_get_path('module', 'search_api_page') . '/search_api_page.css');

  $index = $variables['index'];
  $results = $variables['results'];
  $entities = $variables['entities'];
  $keys = $variables['keys'];

  $output = '<p class="search-performance">' . format_plural($results['result count'],
      'The search found 1 result in @sec seconds.',
      'The search found @count results in @sec seconds.',
      array('@sec' => round($results['performance']['complete'], 3))) . '</p>';

  if (!$results['result count']) {
    $output .= "\n<h2>" . t('Your search yielded no results') . "</h2>\n";
    return $output;
  }

  $output .= "\n<h2>" . t('Search results') . "</h2>\n";

  if ($variables['view_mode'] == 'search_api_page_result') {
    entity_prepare_view($index->entity_type, $entities);
    $output .= '<ol class="search-results">';
    foreach ($results['results'] as $item) {
      $output .= '<li class="search-result">' . theme('search_api_page_result', array('index' => $index, 'result' => $item, 'entity' => isset($entities[$item['id']]) ? $entities[$item['id']] : NULL, 'keys' => $keys)) . '</li>';
    }
    $output .= '</ol>';
  }
  else {
    $output .= '<div class="search-results">';
    $render = entity_view($index->entity_type, $entities, $variables['view_mode']);
    $output .= render($render);
    $output .= '</div>';
  }

  return $output;
}

/**
 * Theme function for displaying search results.
 *
 * @param array $variables
 *   An associative array containing:
 *   - index: The index this search was executed on.
 *   - result: One item of the search results, an array containing the keys
 *     'id' and 'score'.
 *   - entity: The loaded entity corresponding to the result.
 *   - keys: The keywords of the executed search.
 */
function theme_search_api_page_result(array $variables) {
  $index = $variables['index'];
  $id = $variables['result']['id'];
  $entity = $variables['entity'];

  $wrapper = entity_metadata_wrapper($index->entity_type, $entity);

  $url = entity_uri($index->entity_type, $entity);
  $name = entity_label($index->entity_type, $entity);

  if ($index->entity_type == 'file') {
    $url = array(
      'path' => file_create_url($url),
      'options' => array(),
    );
  }

  if (!empty($variables['result']['excerpt'])) {
    $text = $variables['result']['excerpt'];
  }
  else {
    $fields = $index->options['fields'];
    $fields = array_intersect_key($fields, drupal_map_assoc($index->getFulltextFields()));
    $fields = search_api_extract_fields($wrapper, $fields);
    $text = '';
    $length = 0;
    foreach ($fields as $field) {
      if (search_api_is_list_type($field['type']) || !isset($field['value'])) {
        continue;
      }
      $val_length = drupal_strlen($field['value']);
      if ($val_length > $length) {
        $text = $field['value'];
        $length = $val_length;
      }
    }
    if ($text && function_exists('text_summary')) {
      $text = text_summary($text);
    }
  }

  $output = '<h3>' . ($url ? l($name, $url['path'], $url['options']) : check_plain($name)) . "</h3>\n";
  if ($text) {
    $output .= $text;
  }

  return $output;
}
