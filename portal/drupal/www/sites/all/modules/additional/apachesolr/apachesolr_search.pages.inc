<?php

/**
 * @file
 *   Provides the page callback for user defined search pages.
 */

/**
 * Returns search results on user defined search pages.
 */
function apachesolr_search_user_defined_search_page($page_id, $keys = '', $value) {

  $search_page = apachesolr_search_page_load($page_id);
  $search_type = $search_page->settings['apachesolr_search_search_type'];
  // Replace dynamic path with current path
  $search_page->search_path = str_replace('%', $value, $search_page->search_path);
  // Replace the path with the value
  $search_page->search_path = str_replace('%', $value, $search_page->search_path);
  // What to do when we have an initial empty search
  $empty_search_behavior = $search_page->settings['apachesolr_search_browse'];

  // Manual filters
  $filters = isset($search_page->settings['fq']) ? $search_page->settings['fq'] : array();
  // If the manual filter has a % in it, replace it with $value
  $filters = str_replace('%', $value, $filters);
  // Set our search type filter
  if (!empty($search_type) && !empty($value) && $search_type != 'custom') {
    $filters[] = $search_type . ':' . $value;
  }
  // We may also have filters added by facet API module. The 'f'
  // is determined by constant FacetapiAdapter::FILTER_KEY. Hard
  // coded here to avoid extra class loading.
  $conditions = array();
  if (!empty($_GET['f']) && is_array($_GET['f'])) {
    if (module_exists('facetapi')) {
      $conditions['f'] = $_GET['f'];
    }
  }

  $solrsort = isset($_GET['solrsort']) ? $_GET['solrsort'] : '';

  $params = array();
  try {
    $solr = apachesolr_get_solr($search_page->env_id);
    // Default parameters
    $params = array(
      'fq' => $filters,
      'rows' => $search_page->settings['apachesolr_search_per_page'],
    );

    if (empty($search_page->settings['apachesolr_search_spellcheck'])) {
      $params['spellcheck'] = 'false';
    }
    else {
      $params['spellcheck'] = 'true';
    }
    // Empty text Behavior
    if (!$keys && empty($conditions) && ($empty_search_behavior == 'browse' || $empty_search_behavior == 'blocks')) {
      // Pass empty search behavior as string on to apachesolr_search_search_page()
      $results = apachesolr_search_run('apachesolr', $params, '', $search_page->search_path, 0, $solr);

      if ($empty_search_behavior == 'browse') {
        // Hide sidebar blocks for content-area browsing instead.
        apachesolr_suppress_blocks(TRUE);
      }
      $build_results = apachesolr_search_page_browse($empty_search_behavior);
    }
    // Full text behavior
    elseif ($keys || !empty($conditions) || $empty_search_behavior == 'results') {
      $params['q'] = $keys;

      $results = apachesolr_search_run('apachesolr', $params, $solrsort, $search_page->search_path, pager_find_page(), $solr);

      // Adds search results to the render array.
      $build_results = array(
        '#theme' => 'search_results',
        '#results' => $results,
        '#module' => 'apachesolr_search',
      );
    }
  }
  catch (Exception $e) {
    watchdog('Apache Solr', nl2br(check_plain($e->getMessage())), NULL, WATCHDOG_ERROR);
    apachesolr_failure(t('Solr search'), $keys);
  }

  // Initiate our build array
  $build = array();
  if (isset($search_page->settings['apachesolr_search_search_box']) && $search_page->settings['apachesolr_search_search_box']) {
    // Adds the search form to the page.
    $build['search_form'] = drupal_get_form('apachesolr_search_user_defined_search_form', $search_page, $keys);
  }
  if (isset($build_results)) {
    // Adds the search results to the page
    $build['search_results'] = $build_results;
  }
  return $build;
}

/**
 * Search for placed on user defined search pages.
 */
function apachesolr_search_user_defined_search_form($form, &$form_state, $search_page, $keys = '') {
  // Loads the core Search CSS file, use the core search module's classes.
  drupal_add_css(drupal_get_path('module', 'search') . '/search.css');

  $form = array();

  $form['#id'] = 'search-form';
  $form['#attributes']['class'][] = 'search-form';

  $form['#search_page'] = $search_page;

  $form['basic'] = array(
    '#type' => 'container',
    '#attributes' => array('class' => array('container-inline')),
  );
  $form['basic']['keys'] = array(
    '#type' => 'textfield',
    '#title' => t('Enter terms'),
    '#default_value' => $keys,
    '#size' => 20,
    '#maxlength' => 255,
  );
  $form['basic']['submit'] = array(
    '#type' => 'submit',
    '#value' => t('Search'),
  );

  // Set our solr environment for easy retrieval
  // TODO: this should be generic and not duplicated
  $apachesolr_has_searched = apachesolr_has_searched();
  $query = apachesolr_current_query();

  if ($apachesolr_has_searched) {
    $query = apachesolr_current_query();
    $searcher = $query->getSearcher();
    // We use the presence of filter query params as a flag for the retain filters checkbox.
    $fq = $query->getParam('fq');
  }


  if ($apachesolr_has_searched && ($response = apachesolr_static_response_cache($searcher))) {
    $form['basic']['get'] = array(
      '#type' => 'hidden',
      '#default_value' => json_encode(array_diff_key($_GET, array('q' => 1, 'page' => 1, 'solrsort' => 1, 'retain-filters' => 1))),
    );

    if ($fq) {
      $form['basic']['retain-filters'] = array(
        '#type' => 'checkbox',
        '#title' => t('Retain current filters'),
        '#default_value' => (int) isset($_GET['retain-filters']),
      );
    }

    // Get spellchecker suggestions into an array.
    if (isset($response->spellcheck->suggestions) && $response->spellcheck->suggestions) {
      $suggestions = get_object_vars($response->spellcheck->suggestions);
      if ($suggestions) {
        // Get the original query and replace words.

        foreach ($suggestions as $word => $value) {
          $replacements[$word] = $value->suggestion[0];
        }
        $new_keywords = strtr($query->getParam('q'), $replacements);

        // Show only if suggestion is different than current query.
        if ($query->getParam('q') != $new_keywords) {
          $form['suggestion'] = array(
            '#theme' => 'apachesolr_search_suggestions',
            '#links' => array(l($new_keywords, $query->getPath($new_keywords))),
          );
        }
      }
    }
  }

  return $form;
}

/**
 * Processes apachesolr_search_user_defined_search_form submissions.
 */
function apachesolr_search_user_defined_search_form_submit(&$form, &$form_state) {
  $search_page = $form['#search_page'];
  $redirect = $search_page->search_path;
  if (strlen($form_state['values']['keys'])) {
    $redirect .= '/' . $form_state['values']['keys'];
  }

  $get = json_decode($form_state['values']['get'], TRUE);

  if (!empty($form_state['values']['retain-filters'])) {
    // Add our saved value
    $get['retain-filters'] = '1';
    // Add the query values into the redirect.
    $form_state['redirect'] = array($redirect, array('query' => $get));
  }
  else {
    // Redirects to path set in configuration.
    $form_state['redirect'] = $redirect;
  }
}
