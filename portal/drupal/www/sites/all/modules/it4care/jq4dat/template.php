<?php
/**
 * @file
 *    Custom theme's template.php file.
 */

/**
 * Preprocess page.tpl.php variables.
 */
function jq4dat_preprocess_page(&$vars) {
  // Sortable requires Draggable and Droppable, but Drupal handles these dependencies.
  drupal_add_library('system', 'ui.sortable');

  // Add libraries for some jQuery UIs effects.
  drupal_add_library('system', 'effects.bounce');
  drupal_add_library('system', 'effects.explode');
}
