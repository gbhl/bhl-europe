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

       $( "#tabs" ).tabs();   
       
       $("#results-view-type span").bind("click", function() {
         $("#results").toggleClass("list catalog");
         return false;
       });
       
       $("#results-view-type span").toggle(function () {
         $(this ).text("' . t('Catalog') . '"); 
       }, function () {
         $(this ).text("' . t('List') . '"); 
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
drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/custom.js');
 
 
?>