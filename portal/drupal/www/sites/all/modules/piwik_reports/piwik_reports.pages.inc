<?php

/**
 * @file
 * Reports callback for Piwik Reports
 */

/**
 * Report menu links
 */
function piwik_reports_menu_block_page() {
  $item = menu_get_item();
  if ($content = system_admin_menu_block($item)) {
    $output = theme('admin_block_content', array('content' => $content));
  }
  else {
    $output = '<p>' . t('You do not have any reports.') . '</p>';
  }

  return $output;
}

/**
 * Return the form where to select the report period.
 */
function piwik_reports_dateselect_form() {

  $period = array(
    0 => t('Today'),
    1 => t('Yesterday'),
    2 => t('Last week'),
    3 => t('Last month'),
    4 => t('Last year'),
  );

  $form['#attributes'] = array('class' => 'search-form');
  $form['basic'] = array(
    '#type' => 'fieldset',
    '#title' => t('Filter time period'),
  );
  $form['basic']['inline'] = array(
    '#prefix' => '<div class="container-inline">',
    '#suffix' => '</div>',
  );
  $form['basic']['inline']['period'] = array(
    '#type' => 'select',
    '#title' => '',
    '#default_value' => isset($_SESSION['piwik_reports_period']) ? $_SESSION['piwik_reports_period'] : 0,
    '#options' => $period,
  );
  $form['basic']['inline']['submit'] = array(
    '#type' => 'submit',
    '#value' => t('Filter'),
    '#weight' => 15,
  );
  return $form;
}

/**
 * Return the form where to select the site.
 */
function piwik_reports_siteselect_form($form = array(), $piwik_auth) {
  $options = array();
  $sites = piwik_reports_get_sites($piwik_auth);
  if (!isset($_SESSION['piwik_reports_site']) || $_SESSION['piwik_reports_site'] == '') $_SESSION['piwik_reports_site'] = $sites[0]['idsite'];
  $allowed_keys = explode(',', variable_get('piwik_reports_allowed_sites', ''));
  foreach ($sites as $site) {
    if (empty($allowed_keys[0]) || in_array($site['idsite'], $allowed_keys)) {
      $options[$site['idsite']] = $site['name'];
    }
  }
  if (count($options) > 1) {
    $form['basic']['inline']['site'] = array(
      '#type' => 'select',
      '#title' => '',
      '#default_value' => $_SESSION['piwik_reports_site'],
      '#options' => $options,
    );
  }
  elseif (count($options) == 1) {
    $form['basic']['inline']['site'] = array(
      '#type' => 'hidden',
      '#value' => $_SESSION['piwik_reports_site'],
    );
  }
  else {
    $_SESSION['piwik_reports_site'] = '';
  }
  return $form;
}


/**
 * Return the $form that handles piwik reports
 *
 * @param $form
 * @param $form_state
 *   see form api.
 * @param $report
 *   The report name.
 *
 * @return
 */
function piwik_reports_reports($form, &$form_state, $report) {
  global $user;

  $piwik_auth = (variable_get('piwik_reports_token_auth', '') ? variable_get('piwik_reports_token_auth', '') : $user->data['piwik_reports_token_auth']);
  $piwik_url = piwik_reports_get_url();
  $form = piwik_reports_dateselect_form();
  $form = piwik_reports_siteselect_form($form, $piwik_auth);
  $piwik_site_id = $_SESSION['piwik_reports_site'];
  $period = isset($_SESSION['piwik_reports_period']) ? $_SESSION['piwik_reports_period'] : 0;

  if ($period == 1) {
    // Special handling for "yesterday" = 1.
    // The yesterday date value is required.
    $date = _piwik_reports_select_period($period);
  }
  else {
    // Otherwise it returns the today date value.
    $date = _piwik_reports_select_period(0);
  }
  $period_name = _piwik_reports_get_period_name($period);

  // Create an array of URL parameters for easier maintenance.
  $data_params[0] = array();
  $data_params[0]['idSite'] = $piwik_site_id;
  $data_params[0]['date'] = $date;
  $data_params[0]['period'] = $period_name;
  $data_params[0]['disableLink'] = 1;
  $data_params[0]['module'] = 'Widgetize';
  $data_params[0]['action'] = 'iframe';
  $data_params[0]['disableLink'] = 1;
  $data_params[0]['widget'] = 1;
 // $data_params[0]['loading'] = t('Loading data...');
  if (!empty($piwik_auth)) {
    $data_params[0]['token_auth'] = $piwik_auth;
  }

  switch ($report) {
    case 'visitors_overview':
      $title[0] = t('Visits trend');
      $data_params[0]['columns[]'] = 'nb_visits';
      $data_params[0]['moduleToWidgetize'] = 'VisitsSummary';
      $data_params[0]['actionToWidgetize'] = 'getEvolutionGraph';
    break;
    case 'visitors_times':
      $title[0] = t('Visits by local time');
      $data_params[0]['moduleToWidgetize'] = 'VisitTime';
      $data_params[0]['actionToWidgetize'] = 'getVisitInformationPerLocalTime';
    break;
    case 'visitors_settings':
      $data_params[0]['filter_limit'] = 6;

      $data_params[1] = $data_params[0];
      $data_params[2] = $data_params[0];
      $data_params[3] = $data_params[0];
      // Browser families
      $title[0] = t('Browser families');
      $data_params[0]['moduleToWidgetize'] = 'UserSettings';
      $data_params[0]['actionToWidgetize'] = 'getBrowserType';
      // Screen resolutions
      $title[1] = t('Screen resolution');
      $data_params[1]['moduleToWidgetize'] = 'UserSettings';
      $data_params[1]['actionToWidgetize'] = 'getConfiguration';
      // Operating systems
      $title[2] = t('Operating system');
      $data_params[2]['moduleToWidgetize'] = 'UserSettings';
      $data_params[2]['actionToWidgetize'] = 'getOS';
      // Client configurations
      $title[3] = t('Client configuration');
      $data_params[3]['moduleToWidgetize'] = 'UserSettings';
      $data_params[3]['actionToWidgetize'] = 'getResolution';
    break;
    case 'visitors_locations':
      $title[0] = t('Visitors countries');
      $data_params[0]['moduleToWidgetize'] = 'UserCountry';
      $data_params[0]['actionToWidgetize'] = 'getCountry';
      $data_params[0]['filter_limit'] = 15;
    break;
    case 'custom_variables':
      $title[0] = t('Custom variables');
      $data_params[0]['moduleToWidgetize'] = 'CustomVariables';
      $data_params[0]['actionToWidgetize'] = 'getCustomVariables';
      $data_params[0]['filter_limit'] = 15;
    break;
    case 'actions_pageurls':
      $title[0] = t('Page visits');
      $data_params[0]['moduleToWidgetize'] = 'Actions';
      $data_params[0]['actionToWidgetize'] = 'getPageUrls';
      $data_params[0]['filter_limit'] = 15;
    break;
    case 'actions_outlinks':
      $title[0] = t('');
      $data_params[0]['moduleToWidgetize'] = 'Actions';
      $data_params[0]['actionToWidgetize'] = 'getOutlinks';
      $data_params[0]['filter_limit'] = 15;
    break;
    case 'actions_downloads':
      $title[0] = t('');
      $data_params[0]['moduleToWidgetize'] = 'Actions';
      $data_params[0]['actionToWidgetize'] = 'getDownloads';
      $data_params[0]['filter_limit'] = 15;
    break;
    case 'referers_evolution':
      $title[0] = t('');
      $data_params[0]['moduleToWidgetize'] = 'Referers';
      $data_params[0]['actionToWidgetize'] = 'getRefererType';
    break;
    case 'referers_search':
      $data_params[1] = $data_params[0];

      $title[0] = t('Search engines');
      $data_params[0]['moduleToWidgetize'] = 'Referers';
      $data_params[0]['actionToWidgetize'] = 'getSearchEngines';

      $title[1] = t('Keywords');
      $data_params[1]['moduleToWidgetize'] = 'Referers';
      $data_params[1]['actionToWidgetize'] = 'getKeywords';
    break;
    case 'referers_websites':
      $title[0] = t('');
      $data_params[0]['moduleToWidgetize'] = 'Referers';
      $data_params[0]['actionToWidgetize'] = 'getWebsites';
    break;
  }
  // Build the data URL with all params and urlencode it.
  foreach ($data_params as $key => $data) {
    $theme_args['data_url'][] = array('url' => $piwik_url . 'index.php?' . drupal_http_build_query($data), 'title' => $title[$key], );
  }
  // Render HTML code.
  $output = theme('piwik_reports', $theme_args);
  $form['content'] = array(
    '#type' => 'item',
    '#markup' => $output,
  );
  return $form;
}

/**
 * Reports form submit function
 */
function piwik_reports_reports_submit($form, &$form_state) {
  $_SESSION['piwik_reports_period'] = $form_state['values']['period'];
  $_SESSION['piwik_reports_site'] = $form_state['values']['site'];
}

/**
 * Helper function that return the starting and ending dates according to the
 * selected period.
 *
 * @param $period
 *    selected period
 * @return
 *    array with starting and ending date
 */
function _piwik_reports_select_period($period) {
  switch ($period) {
    case 0:
      $date = date("Y-m-d");
      break;

    case 1:
      $d = mktime(0, 0, 0, date("m"), date("d") - 1, date("Y"));
      $date = date("Y-m-d", $d);
      break;

    case 2:
      $d = mktime(0, 0, 0, date("m"), date("d") - 7, date("Y"));
      $date = date("Y-m-d", $d);
      break;

    case 3:
      $d = mktime(0, 0, 0, date("m") - 1, date("d"), date("Y"));
      $date = date("Y-m-d", $d);
      break;

    case 4:
      $d = mktime(0, 0, 0, date("m"), date("d"), date("Y") - 1);
      $date = date("Y-m-d", $d);
      break;
  }
  return $date;
}

/**
 * Helper function that return the name of the selected period.
 *
 * @param $period
 *    selected period
 * @return
 *    name
 */
function _piwik_reports_get_period_name($period) {
  // Possible periods are day, week, month, year.
  switch ($period) {
    case 0:
      $p = "day";
      break;

    case 1:
      $p = "day";
      break;

    case 2:
      $p = "week";
      break;

    case 3:
      $p = "month";
      break;

    case 4:
      $p = "year";
      break;
  }
  return $p;
}
