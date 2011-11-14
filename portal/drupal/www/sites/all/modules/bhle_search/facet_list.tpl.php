<?php

/**
* @file
* Default theme implementation for displaying the facet list of solr queries.
*
* Available variables:
*
*/

?>

<p id="facet-list-grip-multiple" class="collapsing-multiple-grip"><?php print t('Collapse / expand all'); ?></p>

<div class="facet-list collapsing-multiple">

  <div class="content-types">
		<label class="collapsing-grip"><?php print t('Material type:'); ?></label>
                <ul class="collapsing-content">
                   <li><?php print t('Unknown: '); ?></li>
                   <li><?php print t('Journal: '); ?></li>
                   <li><?php print t('Volume: '); ?><</li>
                   <li><?php print t('Article: '); ?></li>
                   <li><?php print t('Monograph: '); ?></li>
               </ul>
  </div>

  <div class="author-name">
		<label class="collapsing-grip"><?php print t('Author name:'); ?></label>
                <ul class="collapsing-content">
                   <li><?php print t('Unknown: '); ?></li>
               </ul>
  </div>

  <div class="year">
		<label class="collapsing-grip"><?php print t('Year:'); ?></label>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

  <div class="languages">
		<label class="collapsing-grip"><?php print t('Languages:'); ?></label>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

    <div class="content-providers">
		<label class="collapsing-grip"><?php print t('Content providers:'); ?></label>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

    <div class="scientific-names">
		<label class="collapsing-grip"><?php print t('Scientific names:'); ?></label>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

</div>