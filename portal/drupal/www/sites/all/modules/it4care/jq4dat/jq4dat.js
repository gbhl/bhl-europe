/**
 * @file
 *    Sample javascript/jQuery code for "jQuery for Designers & Themers".
 *
 * @author
 *    Bevan Rudge: @BevanR, Drupal.geek.nz
 */

/**
 * Hacks the user-register form at user/register.
 *
 * Drupal will call this on page-ready, and every time HTML gets added to the DOM.
 * It's useful to think of DOM as "live manipulable HTML which does not persist
 * between a page refresh".
 *
 * @param context: The new DOM, so that event callbacks can be binded to it.
 *    Note the context parameter is used when binding an event to an element, but
 *    not when manipulating (adding/removing changing) HTML/DOM.  This is a rule-
 *    of-thumb guide only and for proper use you need a jQuery/AJAX developer, and
 *    is out of the scope of this code and sample.
 */
Drupal.behaviors.jq4datHackUserRegister = {};
Drupal.behaviors.jq4datHackUserRegister.attach = function(context) {
  var $ = jQuery;

  // On page load, fade out the email form element.
  $('#user-register-form .form-item-mail', context).fadeOut();

  // On focus (when the form element gets the focus – i.e. gets cicked on)...
  $('#user-register-form #edit-name', context).focus(function() {
    // slide down the email address field.
    $('#user-register-form .form-item-mail').slideDown();
  });

  // On blur (when focus is lost – i.e. something else gets clicked on)...
  $('#user-register-form #edit-name', context).blur(function() {
    // get the value of the username input element.
    var name = $('#user-register-form #edit-name').val();

    // Check that it is not empty.
    if (name) {
      // Create some HTML.  This is not the correct way way.
      // var markup = '<div id="foobar" class="message"><strong>Hey,</strong> nice username! But is <em>' + name + '</em> really your name?</div>';

      // We should use Drupal's javascript theme layer to create the HTML.
      // @see Drupal.theme.prototype.jq4datName below.
      var markup = Drupal.theme('jq4datName', name);

      // Now append that HTML at the end of the inside of the username field.
      // @see visualjquery.com for other manipulation functions like appendTo(), before(), after()...
      $('#user-register-form .form-item-name').append(markup);

      // Now select the <div> we just added (#foobar) and add another class to it.
      // We can also modify it's style directly here with a javascript object like:
      // { property: 'value, property: 'value }
      $('#foobar').addClass('warning').css({
        display: 'inline',
        color: 'red' // Omit the last comma for IE6!
      });
    }
  });
};

/**
 * Declares a theme callback that can be overridden (See the example below).
 *
 * @param name.
 * @return HTML string.
 */
Drupal.theme.prototype.jq4datName = function(name) {
  // This like a drupal theme function.
  return 'Is <em>' + name + '</em> really your name?';
};

/**
 * Overrides the theme function called 'jq4datName' (Defined above).
 *
 * @param name.
 * @return HTML string or jQuery DOM object.
 */
Drupal.theme.jq4datName = function(name) {
  // This like a drupal theme function.
  var markup = '<div id="foobar" class="message"><strong>Hey,</strong> nice username! But is <em>' + name + '</em> really your name?</div>';

  // We could just return markup at this point. We could also DOMify it and return
  // the DOM object instead.
  markup = jQuery(markup);

  // We can also bind events to it's DOM. The 'click' event often only works on
  // links or buttons.  'mousedown' is similar to 'click'.
  markup.mousedown(function(e) {
    // I didn't demo jQuery UI effects in the session, but here is a demo of the
    // 'explode' effect.  @see http://jqueryui.com/demos/effect/ for more.
    // You need to jquery_ui_add(array('effects.bounce')) for each effects library
    // you want to use.  @see http://drupal.org/project/jquery_ui
    // @see jq4dat_preprocess_page() in template.php.
    markup.effect('explode');
  });

  // You can bind multiple events like this.  Most of the time you don't need to
  // worry about the e (event) parameter.
  markup.bind('mouseenter mouseleave', function(e) {
    markup.effect('bounce');
  });

  // Don't forget to return either DOM or HTML string though.
  return markup;
};

/**
 * This is an anonymous function that gets executed on page-ready, but won't ever
 * get called again like Drupal.behaviors functions do when new DOM is added.
 */
jQuery(function() {
  // Make the children of #sidebar-left (the .block elements) sortable.
  // You need to jquery_ui_add(array('ui.sortable')) for jQuery UI library you
  // want to use. @see http://drupal.org/project/jquery_ui
  // @see jq4dat_preprocess_page() in template.php.
  jQuery('.region').sortable({
    // These parameters are all optional, and are documented
    // @see http://jqueryui.com/demos/sortable/
    cursor: 'crosshair',
    handle: 'h2' // Omit the last comma for IE6!
  });

  // Note that the reordered state is lost as soon as the user navigates away from
  // the page.  To save and restore it you need the skills of an AJAX developer,
  // but would essentially provide sortable() with a callback function that would;
  //
  //   * be invoked on sortable events such as when a user drops a sortable item
  //   * get the new order from Sortable (see Sortable's documentation)
  //   * submit it to the server with $.post() or similar
  //   * a Drupal module's menu callback handler would save it to the database
  //   * The server would also need to restore this order when it themes the HTML
  //     in Drupal or renders AHAH to the page with jQuery.
  //
  // Incidentally I wrote a jQuery plugin library that does all of this with an
  // arbitrary number of columns.  It's a bit like iGoogle.
  // @see http://civicactions.com/blog/2009/feb/22/jquerydashboard_plugin
});
