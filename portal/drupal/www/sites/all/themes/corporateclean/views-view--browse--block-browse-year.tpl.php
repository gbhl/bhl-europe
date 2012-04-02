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

<?php
  //  jandvorak - add class when slider delta is less than 100
  $min_num = isset($_REQUEST['slider-filter']['min']) ? intval($_REQUEST['slider-filter']['min']) : '0';
  $max_num = isset($_REQUEST['slider-filter']['max']) ? intval($_REQUEST['slider-filter']['max']) : '0';
  $classes .= ( $max_num - $min_num <= 100 && $min_num>0 && $max_num>0) ? ' use-range' : '';
?>

<div class="<?php print $classes; ?>">

    <?php if ($exposed): ?>
      <div class="view-filters">
        <?php print $exposed; ?>

        <div id="userange_button" class="userange-button">
        <?php
        
          $min = isset($_REQUEST['slider-filter']['min']) ? $_REQUEST['slider-filter']['min'] : '_MIN_';
          $max = isset($_REQUEST['slider-filter']['max']) ? $_REQUEST['slider-filter']['max'] : '_MAX_';
          
          // jandvorak - generate Use the range button link by slider request values
          //$label = '[_MIN_ TO _MAX_]';
          $label = '['.$min.' TO '.$max.']';
          
          $key = $label;
          $params = array('query' => array('action' => 'addfacet', 'field' => 'mods_date_issued', 'key' => urlencode($key), 'source' => browse, 'last' => md5(time())));
          print '' . l(t('Use the interval'), 'search/bhle/empty', $params) . '';
        ?>  
          <span class="disallow-button"><?php print t('Use the interval'); ?></span>
        </div>   
      </div>   
      
    <?php endif; ?>

    <div class="view-content">
      <?php if ($rows): ?>
        <?php print $rows; ?>
      <?php endif; ?>
    </div>

</div>
  
<?php /* class view */ ?>