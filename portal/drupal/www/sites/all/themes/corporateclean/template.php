<?php
/**
 * Return a themed breadcrumb trail.
 *
 * @param $breadcrumb
 *   An array containing the breadcrumb links.
 * @return
 *   A string containing the breadcrumb output.
 */

/**
* Add javascript files for jquery plugins.
*/                                       

//Initialize slideshow using theme settings
$effect=theme_get_setting('feedtabs_effect','corporateclean');

drupal_add_library('system', 'ui.tabs');  

drupal_add_js('jQuery(document).ready(function($) {

    $(".page-search-bhle.one-sidebar #content_preface").hide();
    $("#edit-search a").addClass("expand");

    $("#edit-search a").click(function(){
      if ($(this).hasClass("expand")) {
        $(".page-search-bhle.one-sidebar #content_preface").slideDown(900);
        $(this).removeClass("expand");
      } else {
        $(".page-search-bhle.one-sidebar #content_preface").slideUp(900);
        $(this).addClass("expand");
      }
    });

    $(".collapsing-grip").click(function(){
      var header=$(this);
      var par = $(this).next(".collapsing-content");
      if (header.hasClass("expanded")) {
        par.slideUp(600);
        header.removeClass("expanded");
      } else {
        par.slideDown(600);
        header.addClass("expanded");
      }
    });

    $(".collapsing-multiple-grip").click(function(){
      var header=$(this);
      var parheader = $(".collapsing-multiple .collapsing-grip");
      var par = $(".collapsing-multiple .collapsing-content");
      if (header.hasClass("expanded-all")) {
        par.slideUp(600);
        parheader.removeClass("expanded");        
        header.removeClass("expanded-all");
      } else {
        par.slideDown(600);
        parheader.addClass("expanded");
        header.addClass("expanded-all");
      }
    });


       $( "#tabs" ).tabs();   
       
       $("#results-sort-box #results-list-type-box #results-view-type span").bind("click", function() {
         $("#search-results").toggleClass("list catalog");
         return false;
       });
       
       $("#results-view-type span").toggle(function () {
         $(this ).text("' . t('TABLE') . '"); 
       }, function () {
         $(this ).text("' . t('LIST') . '"); 
       });       
      
       $("#accordion").tabs("div.block-aggregator",{
            tabs: "h2", 
            initialIndex:2,
            event:"mouseover",
            effect: "'.$effect.'"
        });

  });',
   
  array('type' => 'inline', 'scope' => 'header', 'weight' => 5)
);
   
drupal_add_library('system', 'ui.slider');
//drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/custom.js');
 
function corporateclean_form_alter(&$form, &$form_state, $form_id) {
  if ($form_id == 'search_block_form') {
  
    unset($form['search_block_form']['#title']);
	
    $form['search_block_form']['#title_display'] = 'invisible';
  	$form_default = t('simple search all');
    $form['search_block_form']['#default_value'] = $form_default;

 	  $form['search_block_form']['#attributes'] = array('tabindex' => 0, 'onblur' => "if (this.value == '') {this.value = '{$form_default}';}", 'onfocus' => "if (this.value == '{$form_default}') {this.value = '';}" );
  }
} 

function corporateclean_page_alter(&$page) {
  // This assumes everything being output in the "content" page region.

  // Logged in
  if (!empty($page['content']['system_main']['content']['search_form'])) {
    unset($page['content']['system_main']['content']['search_form']);
  }

  // Not logged in
  if (!empty($page['content']['system_main']['search_form'])) {
    unset($page['content']['system_main']['search_form']);
  }
}

/**
 * Override or insert variables into the page template.
 */
function corporateclean_preprocess_page(&$vars) {
  if (arg(0) == 'bhle-read') {
    $vars['title'] = FALSE;
  }
}

/**
 * Override or insert variables into the html template.
 */
function corporateclean_preprocess_html(&$vars) {
  // Set IE7 stylesheet
  $theme_path = base_path() . path_to_theme();
  $vars['ie7_styles'] = '<link type="text/css" rel="stylesheet" media="all" href="' . $theme_path . '/ie7-fixes.css" />' . "\n";
}

function corporateclean_js_alter(&$javascript) {
  if ($_GET['q'] == 'node/22503') {
    unset($javascript['sites/all/modules/it4care/selectmenu/js/jquery.ui.selectmenu/jquery.ui.selectmenu.js']);
    unset($javascript['sites/all/modules/it4care/selectmenu/js/selectmenu.js']);
  }
}

/**
 * Override default theme implementation to present the source of the feed.
 */
function corporateclean_preprocess_aggregator_feed_source(&$vars) {
  unset($vars['source_icon']); // Disable feed icon
}

/**
 * Returns HTML for a button form element.
 *
 * @param $variables
 *   An associative array containing:
 *   - element: An associative array containing the properties of the element.
 *     Properties used: #attributes, #button_type, #name, #value.
 *
 * @ingroup themeable
 * 
 * Override input type="submit" -> type="button"  
 */
function corporateclean_button($variables) {
  $element = $variables['element'];
  
  $type = strtolower($element['#button_type']);
  switch ($type) {
    case 'reset':
    case 'button':
      break;
    default:
      $type = 'submit';
  }
  $element['#attributes']['type'] = $type;
  
  element_set_attributes($element, array('id', 'name', 'value'));

  $element['#attributes']['class'][] = 'form-' . $element['#button_type'];
  if (!empty($element['#attributes']['disabled'])) {
    $element['#attributes']['class'][] = 'form-button-disabled';
  }

  return '<input' . drupal_attributes($element['#attributes']) . ' />';
}

?>