// $Id: README.txt,v 1.1 2009/09/18 02:32:15 quicksketch Exp $

Hint provides a simple API for adding JavaScript "hint" text to text fields
and textareas. The "hint" is shown inside of the text field until the user
clicks or gives focus to the text field, then the hint text is cleared, allowing
the user to enter their own content.

 * Works with both textfields and password inputs.
 * Uses the "title" attribute for the hint text content.
 * Easily add a hint to any text field by using the #hint Form API property.

This module built by robots: http://www.lullabot.com
Written by Nathan Haug (quicksketch)

Usage
-----

1) Copy the texthint folder to the modules folder in your installation.

2) Enable the module using Administer -> Site building -> Modules
   (/admin/build/modules).

3) Visit the setting page to enable any default implementations at Administer ->
   Settings -> Texthint (admin/settings/texthint).

Bonus
-----

4) Use hook_form_alter() or your theme to modify existing forms. For example,
   the theme search form can be made to use Hint with the following code
   in your theme's template.php file.


/**
 * Theme override of theme_search_theme_form().
 */
function [your_theme_name]_search_theme_form($form) {
  // Replace the normal label with a hint.
  unset($form['search_theme_form']['#title']);
  $form['search_theme_form']['#hint'] = t('Search');
  return drupal_render($form);
}

   Texthint also provides a quick utility function to set the title as a hint,
   which can be useful if you know texthint is going to be available.

texthint_set_hint($form['search_theme_form'], t('Search'));

   Though generally just setting $element['#hint'] is a good idea, because it
   will not interfer with the site if texthint.module is disabled.

Support
-------

Please file any bugs with this module in the Hint issue queue on
Drupal.org. Please send any questions there also:
http://drupal.org/project/issues/texthint
