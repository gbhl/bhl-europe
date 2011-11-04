Drupal.behaviors.selectmenu = {
  attach: function(context, settings) {
    (function ($) {
      var form = null;

      $('select').each(function(context){
        form = $(this).parents('form');
        var apply_selectmenu = true;

        // Skip multi-row selects
        if ($(this).attr('multiple')) {
          apply_selectmenu = false;
        }

        // If this form's ID isn't part of the exception list
        if (!in_array(form.attr('id'), Drupal.settings.selectmenu.form_id_exceptions)) {
          if (Drupal.settings.selectmenu.ignore_system_settings_forms
                      && (form.attr('id').indexOf('system-') === 0
                            || $(this).parents('body.page-admin').length > 0
                )
            ) {
            apply_selectmenu = false;
          }

          // Check for Overlay forms
          if (Drupal.settings.selectmenu.ignore_overlay_forms) {
            if ($(this).parents('body.overlay').length > 0) {
              apply_selectmenu = false;
            }
          }

          // Check for Node add forms
          if (Drupal.settings.selectmenu.ignore_node_add_forms) {
            if ($(this).parents('body.page-node-add').length > 0) {
              apply_selectmenu = false;
            }
          }
          
          // If no ignore criteria met, carry out skinning select element
          if (apply_selectmenu) {
            $(this).selectmenu();
          }
        }
      });

      function in_array (needle, haystack, argStrict) {
        // Checks if the given value exists in the array  
        // 
        // version: 1107.2516
        // discuss at: http://phpjs.org/functions/in_array
        // +   original by: Kevin van Zonneveld (http://kevin.vanzonneveld.net)
        // +   improved by: vlado houba
        // +   input by: Billy
        // +   bugfixed by: Brett Zamir (http://brett-zamir.me)
        // *     example 1: in_array('van', ['Kevin', 'van', 'Zonneveld']);
        // *     returns 1: true
        // *     example 2: in_array('vlado', {0: 'Kevin', vlado: 'van', 1: 'Zonneveld'});
        // *     returns 2: false
        // *     example 3: in_array(1, ['1', '2', '3']);
        // *     returns 3: true
        // *     example 3: in_array(1, ['1', '2', '3'], false);
        // *     returns 3: true
        // *     example 4: in_array(1, ['1', '2', '3'], true);
        // *     returns 4: false
        var key = '',
          strict = !! argStrict;

        if (strict) {
          for (key in haystack) {
            if (haystack[key] === needle) {
                return true;
            }
          }
        } else {
          for (key in haystack) {
            if (haystack[key] == needle) {
                return true;
            }
          }
        }
        return false;
      }
    })(jQuery);
  }
};

(function ($) {
  $(document).ready(function($){

  });
})(jQuery);