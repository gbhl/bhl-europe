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
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Material type:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li><a href="#" title=""><?php print t('Unknown: '); ?></a> <span class="value"></span></li>
                   <li><a href="#" title=""><?php print t('Journal: '); ?></a> <span class="value"></span></li>
                   <li><a href="#" title=""><?php print t('Volume: '); ?></a> <span class="value"></span></li>
                   <li><a href="#" title=""><?php print t('Article: '); ?></a> <span class="value"></span></li>
                   <li><a href="#" title=""><?php print t('Monograph: '); ?></a> <span class="value"></span></li>
               </ul>
  </div>

  <div class="author-name">
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Author name:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li><?php print t('Unknown: '); ?></li>
               </ul>
  </div>

  <div class="year">
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Year:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

  <div class="languages">
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Languages:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

    <div class="content-providers">
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Content providers:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

    <div class="scientific-names">
		<h3 class="collapsing-grip"><a href="#" title=""><?php print t('Scientific names:'); ?></a> <span class="value"></span></h3>
                <ul class="collapsing-content">
                   <li></li>
               </ul>
  </div>

</div>