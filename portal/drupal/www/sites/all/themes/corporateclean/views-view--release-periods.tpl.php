<?php
/**
 * @file views-view.tpl.php
 * Main view template
 *
 * Variables available:
 * - $classes_array: An array of classes determined in
 *   template_preprocess_views_view(). Default classes are:
 *     .view
 *     .view-[css_name]
 *     .view-id-[view_name]
 *     .view-display-id-[display_name]
 *     .view-dom-id-[dom_id]
 * - $classes: A string version of $classes_array for use in the class attribute
 * - $css_name: A css-safe version of the view name.
 * - $css_class: The user-specified classes names, if any
 * - $header: The view header
 * - $footer: The view footer
 * - $rows: The results of the view query, if any
 * - $empty: The empty text to display if the view is empty
 * - $pager: The pager next/prev links to display, if any
 * - $exposed: Exposed widget form/info to display
 * - $feed_icon: Feed icon to display, if any
 * - $more: A link to view more, if any
 *
 * @ingroup views_templates
 */
?>
<div id="view-release-periods-page">
<div class="<?php print $classes; ?>">
  
    <div class="view-header">
      <h2><span><?php print t('Browse by Year'); ?></span></h2>
    </div>

  <div class="view-release-period-content-wrapper">
  <div class="view-release-period-content">
    <?php if ($exposed): ?>
      <div class="view-filters">
        <?php print $exposed; ?>
      </div>   
      
    <?php endif; ?>

    <div class="view-content">
      <?php if ($rows): ?>
        <?php print $rows; ?>
      <?php endif; ?>
    </div>
  </div>

</div> 
</div> 
</div> 

<?php /* class view */ ?>