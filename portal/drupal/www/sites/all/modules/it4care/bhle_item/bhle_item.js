(function ($) {
  Drupal.behaviors.customIframeHeight = {
    attach: function (context, settings) {
    
      var bookReader = $('iframe.bookreader');
    
      function iframeHeight() {
        var windowHeight = $(window).height();
        var iframeOffset = bookReader.offset();
        var iframeHeightValue = windowHeight - iframeOffset.top;
        
        bookReader.css('height', iframeHeightValue + 'px');
        bookReader.parent().css('height', iframeHeightValue + 'px');
      }
      
      iframeHeight();
      
      $(window).resize(function() {
        iframeHeight();
      });

    }
  };

})(jQuery);