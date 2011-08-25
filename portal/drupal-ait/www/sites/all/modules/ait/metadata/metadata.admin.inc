<?php

/**
 * metadata settings form
 * @return form array
 */
function metadata_settings($form, &$form_state) {

  $form['indexer'] = array(
    '#type' => 'fieldset',
    '#title' => t('Database schema'),
  );

  $form['indexer']['metadata_schema'] = array(
    '#type' => 'select',
    '#title' => t('Metadata field cataloge'),
    '#options' => metadata_get_schemas(),
    '#disabled' => true,
    '#default_value' => metadata_variable_get_application('metadata_schema'),
  );

  $form['functions'] = array(
    '#type' => 'fieldset',
    '#title' => t('External data'),
  );
  $form['functions']['download_solr_conf'] = array(
    '#type' => 'submit',
    '#value' => t('Download SOLR config directory.'),
  );

  /* TODO safety query before deleting everything
  $form['delete_all_records'] = array(
    '#type' => 'submit',
    '#value' => t('Delete all records in database (with fields)'),
  );
  */

  return $form;
}

function metadata_settings_submit($form, &$form_state) {
//   die("<pre>".var_export($form, true)."\n\n\n".var_export($form_state, true));
  $func = 'metadata_settings_submit__' .
          $form_state['clicked_button']['#parents'][0];
  if(function_exists($func)) {
    return $func($form, $form_state);
  } else {
    die("Go away there is no $func");
  }
}

/**
 * metadata settigs download_fields_xsl submit handler
 */
function metadata_settings_submit__download_fields_xsl($form, &$form_state) {
  $xsltxt = variable_get("metadata_solr_transform", null);
  if($xsltxt == null) {
    require_once(dirname(__FILE__)."/metadata_config_tools.inc");

    $data = metadata_read_ods_table(dirname(__FILE__)."/../additional/schema.ods", 1, 3);
    $xsldom = metadata_create_data_transformer($data);
  } else {
    $xsldom = new DOMDocument();
    $xsldom->loadXML($xsltxt);
  }
  header("Content-Type: text/xml; charset=UTF-8");
  header("Content-Disposition: attachment; filename=record2fields.xsl; modification-date=\"" . date("r") . '";');
  die($xsldom->saveXML());
}

/**
 * metadata settigs download_schema submit handler
 */
function metadata_settings_submit__download_schema($form, &$form_state) {
  require_once(dirname(__FILE__)."/metadata_config_tools.inc");

  $data = metadata_schema_data();
  $schema = metadata_create_schema($data);
  die( "<pre>".var_export($data, true)."</pre>");
  header("Content-Type: text/xml; charset=UTF-8");
  header("Content-Disposition: attachment; filename=schema.xml; modification-date=\"" . date("r") . '";');
  die($schema->saveXML());
}

/**
 * metadata settigs download_solr_conf submit handler
 */
function metadata_settings_submit__download_solr_conf($form, &$form_state) {
  require_once(dirname(__FILE__)."/metadata_config_tools.inc");

  $data = metadata_read_ods_table(dirname(__FILE__)."/../additional/schema.ods", 1, 3);
  $schema = metadata_create_schema($data);
  $mets_config = metadata_create_dataimport_config($data);

  $temp = tempnam(sys_get_temp_dir(), "solr_conf_");
  $zip = new ZipArchive();
  $zip->open($temp, ZIPARCHIVE::CREATE);
  $zip->addFromString("conf/schema.xml", $schema->saveXML());
  $zip->addFromString("conf/mets-config.xml", $mets_config->saveXML());
  foreach(array(
    'mapping-ISOLatin1Accent.txt',
    'protwords.txt',
    'solrconfig.xml',
    'spellings.txt',
    'stopwords.txt',
    'synonyms.txt',
    'xslt/example_atom.xsl',
    'xslt/example_rss.xsl',
    'xslt/example.xsl',
    'xslt/luke.xsl',
  ) as $fn) {
    $zip->addFromString("conf/$fn",
      file_get_contents(dirname(__FILE__)."/../additional/metadata_core/$fn"));
  }
  $zip->close();


  header("Content-Type: application/zip");
  header("Content-Disposition: attachment; filename=solr_config.zip; modification-date=\"" . date("r") . '";');

  readfile($temp);
  unlink($temp);
  die("");
}

/**
 * metadata settigs delete_all_records submit handler
 */
function metadata_settings_submit__delete_all_records($form, &$form_state) {

  $sql = 'SELECT nid FROM {node} n WHERE n.type = :type';
  // TODO: only delete application specific nodes
  $result = db_query($sql, array(':type' => 'bhle'));
  $nids = array();
  foreach ($result as $row) {
    $nids[] = $row->nid;
  }

  node_delete_multiple($nids);
}



/**
 * metadata user settings (stylesheets, etc.)
 */
function metadata_user_settings($form, &$form_state) {

  $form['view_properties'] = array(
    '#type' => 'fieldset',
    '#title' => t('View properties'),
  );
  $form['view_properties']['metadata_stylesheet'] = array(
    '#type' => 'select',
    '#title' => t('Node Stylesheet'),
    '#options' => metadata_get_stylesheets(),
    '#default_value' => metadata_variable_get_user('metadata_stylesheet'),
  );
  $form['view_properties']['metadata_resultlist_stylesheet'] = array(
    '#type' => 'select',
    '#title' => t('Resultlist Stylesheet'),
    '#options' => metadata_get_list_stylesheets(),
    '#default_value' => metadata_variable_get_user('metadata_result_list_stylesheet'),
  );
  $form['view_properties']['metadata_resulttable_stylesheet'] = array(
    '#type' => 'select',
    '#title' => t('Resulttable Stylesheet'),
    '#options' => metadata_get_table_stylesheets(),
    '#default_value' => metadata_variable_get_user('metadata_result_table_stylesheet'),
  );
  $form['download_properties'] = array(
    '#type' => 'fieldset',
    '#title' => t('Download properties'),
  );
  $form['download_properties']['metadata_download_stylesheet'] = array(
    '#type' => 'select',
    '#title' => t('Download Stylesheet'),
    '#options' => metadata_get_download_stylesheets(),
    '#default_value' => metadata_variable_get_user('metadata_download_stylesheet'),
  );
  $form['download_properties']['upload_file'] = array(
    '#type' => 'file',
    '#title' => t('Upload your own stylesheet: '),
  );
  $form['download_properties']['upload_button'] = array(
    '#type' => 'button',
    '#value' => t('Upload'),
    '#submit' => array('metadata_user_settings_xsl_upload'),
    '#executes_submit_callback' => TRUE,
  );
  $form['#attributes'] = array(
    'enctype' => "multipart/form-data",
  );
  $form['submit'] = array(
    '#type' => 'submit',
    '#value' => t('Save'),
  );

  return $form;
}


/**
 * metadata user settings submit handler
 */
function metadata_user_settings_submit($form, &$form_state) {
  // saving variables
  metadata_variable_set_user('metadata_stylesheet',             $form_state['values']['metadata_stylesheet']);
  metadata_variable_set_user('metadata_result_list_stylesheet', $form_state['values']['metadata_resultlist_stylesheet']);
  metadata_variable_set_user('metadata_download_stylesheet',    $form_state['values']['metadata_download_stylesheet']);
  //unsetting $_SESSION variables, so that the saved values take effect immediatly
  unset($_SESSION['metadata_stylesheet']);
  unset($_SESSION['metadata_result_list_stylesheet']);
  unset($_SESSION['metadata_download_stylesheet']);
}


function metadata_user_settings_xsl_upload($form, &$form_state) {
  global $user;

  $fileName = @$_FILES['files']['name']['upload_file'];
  if($fileName != "") {
    $filePath = $_FILES['files']['tmp_name']['upload_file'];
    if ($fileContent = file_get_contents($filePath)) {

      try {
        $xsl = new DOMDocument();
        if (!$xsl->loadXML($fileContent)) die('compiling stylesheet failed.');
        $xslt = new XSLTProcessor();
        $xslt->importStylesheet($xsl);
      } catch (PDOException $e) {
        die('compiling stylesheet failed.');
      }

      if (isset($user->data['metadata_download_stylesheets'])) $stylesheets = $user->data['metadata_download_stylesheets'];
      else $stylesheets = array();
      $stylesheets[$fileName] = $fileContent;
      metadata_variable_set_user('metadata_download_stylesheets', $stylesheets);
    }
    else {
      drupal_set_message(t("File import:"), "error");
    }
  }
}