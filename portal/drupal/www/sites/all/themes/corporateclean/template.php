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