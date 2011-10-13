/**
 *  @file
 *  This will pass the settings and initiate the Carousel.
 */
(function($) {
Drupal.behaviors.viewsJqfxCloudcarousel = {
  attach: function (context) {
    for (id in Drupal.settings.viewsJqfxCloudcarousel) {
      $('#' + id + ':not(.viewsJqfxCloudcarousel-processed)', context).addClass('viewsJqfxCloudcarousel-processed').each(function () {
      
        // Add the cloud_carousel class to our images for the plugin
        $('.views-jqfx-cloudcarousel img').addClass('cloudcarousel');
      
        // Get our settings
        var settings = Drupal.settings.viewsJqfxCloudcarousel[$(this).attr('id')];
        
        // Add the control and text containers to our settings
        settings.buttonLeft = $('#cloudcarousel-left-but');
        settings.buttonRight = $('#cloudcarousel-right-but');
        settings.altBox = $('#cloudcarousel-alt-text');
        settings.titleBox = $('#cloudcarousel-title-text');
        
        // Load 3d Cloud Carousel
        $(this).CloudCarousel(settings);
      });
    }
  }
}

})(jQuery);
