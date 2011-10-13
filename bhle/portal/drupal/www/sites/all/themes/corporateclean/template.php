<?php
/**
 * Return a themed breadcrumb trail.
 *
 * @param $breadcrumb
 *   An array containing the breadcrumb links.
 * @return
 *   A string containing the breadcrumb output.
 */
function corporateclean_breadcrumb($variables){
  $breadcrumb = $variables['breadcrumb'];
  if (!empty($breadcrumb)) {
    $breadcrumb[] = drupal_get_title();
    return '<div class="breadcrumb">' . implode(' <span class="breadcrumb-separator">/</span> ', $breadcrumb) . '</div>';
  }
}


/**
 * Implementation of hook_theme
 * Define themes to make style
 */    
/*
function corporateclean_theme($existing, $type, $theme, $path) {
  $base = array(
    'arguments' => array('form' => NULL),
    'render element' => 'form',
    'path' => drupal_get_path('theme', 'corporateclean') . '/templates/forms',
  );

  return array(
    'advanced_search_block_form_simple' => $base + array(
      'template' => 'advanced_search_block_form_simple',
    ),
  );
}
*/
/**
 * Preprocessor for advanced_search_block_form_simple theme
 */
 /*
function corporateclean_preprocess_advanced_search_block_form_simple(&$variables) {
 $variables['query']['#default_value'] = t('test');
}
 */
/*
function corporateclean_form_alter(&$form, &$form_state, $form_id) {


  if ($form_id == 'search_block_form') {
  
    unset($form['search_block_form']['#title']);
	
    $form['search_block_form']['#title_display'] = 'invisible';
	$form_default = t('Search');
    $form['search_block_form']['#default_value'] = $form_default;
    $form['actions']['submit'] = array('#type' => 'image_button', '#src' => base_path() . path_to_theme() . '/images/search-button.png');

 	$form['search_block_form']['#attributes'] = array('onblur' => "if (this.value == '') {this.value = '{$form_default}';}", 'onfocus' => "if (this.value == '{$form_default}') {this.value = '';}" );

  } 
  if ($form_id == 'advanced_search_block_form_simple') {

    unset($form['advanced-search-block-form-simple']['#title']);


    $form['advanced-search-block-form-simple']['#title_display'] = 'invisible';
    $form_default = t('simple search all');
    $form['advanced-search-block-form-simple']['#default_value'] = $form_default;

    $form['actions']['submit'] = array('#type' => 'image_button', '#src' => base_path() . path_to_theme() . '/images/btn-search.pngg');

    $form['advanced-search-block-form-simple']['#attributes'] = array('onblur' => "if (this.value == '') {this.value = '{$form_default}';}", 'onfocus' => "if (this.value == '{$form_default}') {this.value = '';}" );

  }
}
*/


/**
 * Add javascript files for page--front jquery slideshow.
 */
drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/../../../../misc/jquery.once.js'); 
drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/jquery.tools.min.js');

//Initialize slideshow using theme settings
$effect=theme_get_setting('feedtabs_effect','corporateclean');

drupal_add_js(
  '
  // jQuery.noConflict()
   jQuery(document).ready(function($) {  
     $("#accordion").once("#accordion").tabs("div.content",{
	     tabs: "h2", 
	     initialIndex:2,
	     event:"mouseover",
	     effect: "'.$effect.'"
     });
   });',
array('type' => 'inline', 'scope' => 'header', 'weight' => 5)
);
 
function corporateclean_form_alter(&$form, &$form_state, $form_id) {
  if ($form_id == 'advanced_search_block_form_simple') {
  // Add a checkbox to toggle the breadcrumb trail.
  $form_default = t('simple search all');
  $form['query'] = array(
    '#type' => 'textfield',
    '#title' => t('search in:'),
    '#title_display' => 'before',
    '#size' => 15,
    '#default_value' => $form_default,
//    '#attributes' => array('title' => t('Enter the terms you wish to search for.')),
    '#attributes' => array('onblur' => "if (this.value == '') {this.value = '{$form_default}';}", 'onfocus' => "if (this.value == '{$form_default}') {this.value = '';}" ),
  );
  
  $form['submit'] = array(
    '#type' => 'image_button', 
    '#src' => base_path() . path_to_theme() . '/images/btn-search.png'
  );
  
  };
}    

  
?>