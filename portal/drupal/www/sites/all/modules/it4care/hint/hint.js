// $Id: hint.js,v 1.4 2009/12/20 02:05:12 quicksketch Exp $
(function ($) {

/**
 * The Drupal behavior to add the $.hint() behavior to elements.
 */
Drupal.behaviors.hint = {};
Drupal.behaviors.hint.attach = function(content) {
  // Even though it's unlikely that another class name would be used, we ensure
  // that the behavior uses the default class names used in the module.
  jQuery('input.hint-enabled:not(input.hint)', content).hint({
    hintClass: 'hint',
    triggerClass: 'hint-enabled'
  });
};

/**
 * The jQuery method $.hint().
 *
 * This method can be used on any text field or password element and is not
 * Drupal-specific. Any elements using hint must have a "title" attribute,
 * which will be used as the hint.
 */
jQuery.fn.hint = function(options) {
  var opts = jQuery.extend(false, jQuery.fn.hint.defaults, options);

  $(this).find('input').andSelf().filter('[type=text], [type=password]').each(function() {
    if (this.title) {
      var attributes = opts.keepAttributes;
      var $element = $(this);
      var $placeholder = $('<input type="textfield" value="" />');

      // Set the attributes on our placeholder.
      for (var key in attributes) {
        var attribute = attributes[key];
        $placeholder.attr(attribute, $element.get(0)[attribute]);
      }
      $placeholder.val($element.get(0).title);
      $placeholder.get(0).autocomplete = false;
      $placeholder.removeClass(opts.triggerClass).addClass(opts.hintClass);
      $element.after($placeholder);
      if ($element.val() == '') {
        $element.hide();
      }
      else {
        $placeholder.hide();
      }

      $placeholder.focus(function() {
        $placeholder.hide();
        $element.show().get(0).focus();
      });
      $element.blur(function() {
        if (this.value == '') {
          $element.hide();
          $placeholder.show();
        }
      });
    }
  });
};

jQuery.fn.hint.defaults = {
  // The class given the textfield containing the hint.
  hintClass: 'hint',

  // A class that will trigger the hint if $.hint() is used on multiple
  // elements or the entire page.
  triggerClass: 'hint-enabled',

  // A list of attributes that will be copied to the placeholder element.
  // Usually this list will be sufficient, but a special list may be specified
  // if needing to keep custom or obscure attributes.
  keepAttributes: ['style', 'className', 'title', 'size']
};

})(jQuery);
