(function ($) {
  Drupal.behaviors.customCheckboxes = {
    attach: function (context, settings) {

      var customCheckboxesLabel = $('#bhle-tagging-basket-download-form .form-checkboxes .form-item.form-type-checkbox label');
      
      customCheckboxesLabel.prev('input[type=checkbox]').css('display', 'none');
      
      customCheckboxesLabel.click(function() {
        if ($(this).hasClass('checked')) {
          $(this).removeClass('checked');
        }
        else {
          $(this).addClass('checked');
        } 
      });

    }
  };

})(jQuery);