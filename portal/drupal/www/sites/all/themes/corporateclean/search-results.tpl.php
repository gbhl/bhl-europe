<?php

/**
 * @file
 * Default theme implementation for displaying search results.
 *
 * This template collects each invocation of theme_search_result(). This and
 * the child template are dependent to one another sharing the markup for
 * definition lists.
 *
 * Note that modules may implement their own search type and theme function
 * completely bypassing this template.
 *
 * Available variables:
 * - $search_results: All results as it is rendered through
 *   search-result.tpl.php
 * - $module: The machine-readable name of the module (tab) being searched, such
 *   as "node" or "user".
 *
 *
 * @see template_preprocess_search_results()
 */
?>
<?php if ($search_results) : ?>
  <h2 class="search-result-title wcag"><?php print t('Search results');?></h2>
  
  <p id="results-view-type" class="radius gradient-vertical-grey"><?php print t('View search results as:'); ?> <span><?php print t('TABLE'); ?></span></p>     
  <div id="results" class="list">   
  
    <ol class="search-results <?php print $module; ?>-results">
      <?php print $search_results; ?>
    </ol>
    
    <?php print $pager; ?>

  </div>

<?php else : ?>
  <h2><?php print t('Your search yielded no results');?></h2>
  <?php print search_help('search#noresults', drupal_help_arg()); ?>
<?php endif; ?>
