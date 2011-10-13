<?php
/**
 * @file custom Search API Page search form
 * @see mt_core.module's implementation of hook_theme()
 *
 * Variables contained in $form. What we need is:
 *
 * $form['form']['keys_1']    The textfield containing the search query
 * $form['form']['submit_1']  Submit button
 */
?>
<?php print drupal_render($form['text-range']); ?>
<div class="yui3-g">
  <div class="yui3-u range-box range-box-left"><?php print drupal_render($form['range-from']); ?></div>
  <div class="yui3-u range-slider-box"><?php print drupal_render($form['range-slider']); ?></div>
  <div class="yui3-u range-box range-box-right"><?php print drupal_render($form['range-to']); ?></div>
</div>
<?php print drupal_render($form['submit']); ?>
<?php print drupal_render_children($form); ?>
