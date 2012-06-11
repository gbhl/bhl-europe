(function ($) {
  Drupal.behaviors.customSlider = {
    attach: function() {

      var min = $("input#edit-slider-filter-min");
      var max = $("input#edit-slider-filter-max");

      if (!min.length || !max.length) {
        return;
      }
          
      var init_min = ('' == min.val()) ? 1600 : min.val();
      var init_max = ('' == max.val()) ? 2050 : max.val();

      min.val(init_min);
      max.val(init_max);

      min.parents('div.views-widget:not(.ajax-processed)').addClass('ajax-processed').before(
        $('<div></div>').slider({
             range: true,
             min:  1600,     // Adjust slider min and max to the range 
             max:  2050,     // of the exposed filter.
             step: 10,
             values: [init_min, init_max],

             slide: function(event, ui){
                min.val(ui.values[0]);
                $('a.ui-slider-min').text(ui.values[0]);
                max.val(ui.values[1]);
                $('a.ui-slider-max').text(ui.values[1]);
             },

      			 stop: function(event, ui){
      			  	$(this).parents('form').find('.ctools-auto-submit-click').click();
      			 },             
        })
      );      // Add .hide() before the ';' to remove the input elements altogether.

      $('a.ui-slider-min').text(init_min);
      $('a.ui-slider-max').text(init_max);
    }
  };
})(jQuery);

(function ($) {
  Drupal.behaviors.customSliderContentTop = {
    attach: function (context, settings) {
    
      $('.not-front #content_top').once('customSliderContentTop', function () {
        
        var contentTop = $(this);
        
        contentTop.after('<span class="slider-content-top" title="' + Drupal.t('Close') + '"></span>');
        
        contentTop.next().click(function() {
          contentTop.slideToggle('slow', function() {
            if (contentTop.is(':hidden')) {
              contentTop.next().attr('title', Drupal.t('Expand'));
            } else {
              contentTop.next().attr('title', Drupal.t('Close'));  
            }
          });
        });
        
      });
      
      $('a.external-link').attr('target', '_blank');

    }
  };

})(jQuery);