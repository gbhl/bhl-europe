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

  <div id="results">

    <div id="results-sort-box">

      <div id="results-number-box">
        <div id="results-number-label">
          <?php print t('Items per page:'); ?> <span><?php print t('all'); ?></span></a>                            
        </div>
        <ul id="results-number">
          <li><a href="?per_list=5">5</a></li>
          <li><a href="?per_list=10">10</a></li>
          <li><a href="?per_list=15">15</a></li>
          <li><a href="?per_list=20">20</a></li>
          <li><a href="?per_list=25">25</a></li>
          <li><a href="?per_list=30">30</a></li>
          <li class="selected"><a href="?per_list="><?php print t('all'); ?></a></li>
        </ul>
      </div>

      <div id="results-list-type-box">
        <p id="results-view-type" class="radius gradient-vertical-grey"><?php print t('View results as:'); ?> <span><?php print t('LIST'); ?></span></p>
       </ul>
      </div>

    </div>
  
    <?php print $pager; ?>

    <ol id="search-results" class="search-results <?php print $module; ?>-results catalog">
      <?php print $search_results; ?>
    </ol>

    <?php print $pager; ?>
  </div>

<?php else : ?>
  <h2><?php print t('Your search yielded no results');?></h2>
  <?php print search_help('search#noresults', drupal_help_arg()); ?>
<?php endif; ?>
