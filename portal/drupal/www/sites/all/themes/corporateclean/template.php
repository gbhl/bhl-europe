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
drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/jquery.tools.min.js');

//Initialize slideshow using theme settings
$effect=theme_get_setting('feedtabs_effect','corporateclean');

   drupal_add_js('jQuery(document).ready(function($) {
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
  
  /*drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/jQRangeSlider.js');*/
  
  drupal_add_js(drupal_get_path('theme', 'corporateclean') . '/js/custom.js');
 
 
?>