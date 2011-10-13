<?php
/**
 * @file
 * The theme system, which controls the output of views jQFX.
 *
 * This just adds a wrapper div to the output.
 */

/**
 * The current element of the output.
 *
 * @ingroup themeable
 */
function theme_views_jqfx_main_section($variables) {
  $attributes['id'] = "{$variables['plugin']}_main_{$variables['id']}";
  $attributes['class'] = array("{$variables['plugin']}_main views", "jqfx_main");
  $attributes = drupal_attributes($attributes);

  return "<div$attributes>{$variables['hidden_elements']}</div>";
}
