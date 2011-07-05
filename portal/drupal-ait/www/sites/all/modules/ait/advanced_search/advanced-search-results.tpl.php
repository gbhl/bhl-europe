<?php

/**
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

  <?php print $pager; ?>
  <?php switch ($view_type) :  case 'list':    // list view ?>
      <ol class="search-results <?php print $module; ?>-results advanced-search-results-list">
        <?php foreach ($search_results as $search_result) :?>
          <?php print $search_result; ?>
        <?php endforeach; ?>
      </ol>
      <?php break; ?>
    <?php case 'table' :    // table view ?>
      <table class="advanced-search-results-table">
        <?php for ($i=0; $i<$size['y']; $i++) : ?>
          <tr>
            <?php for ($j=0; $j<$size['x']; $j++) : ?>
              <td class="advanced-search-results-table-cell">
                <div>
                  <?php $index = $i*$size['y'] + $j ?>
                  <?php if (isset($search_results[$index])) : ?>
                    <?php print $search_results[$index]; ?>
                  <?php else : ?>
                    <?php print '&nbsp;'; ?>
                  <?php endif; ?>
                </div>
              </td>
            <?php endfor; ?>
          </tr>
        <?php endfor; ?>
      </table>
      <?php break; ?>
    <?php default : ?>
      <p>wrong view type: <?php print $view_type?></p>
  <?php endswitch; ?>
  <?php print $pager; ?>

<?php else : ?>

  <h2><?php print t('Your search yielded no results');?></h2>
  <?php print search_help('search#noresults', drupal_help_arg()); ?>

<?php endif; ?>




