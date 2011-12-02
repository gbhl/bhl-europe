<?php

/**
* @file
* Default theme implementation for displaying the biblio of solr queries.
*
* Available variables:
*
*/

?>

<div id="tabs" class="biblio">

<ul>
  <li class="first"><a href="#tabs-1" class="active biblio-summary"><?php print t('Summary'); ?></a></li>
  <li><a href="#tabs-2" class="active biblio-abstract"><?php print t('Abstract'); ?></a></li>
  <li><a href="#tabs-3" class="active biblio-mods"><?php print t('MODS'); ?></a></li>
  <li><a href="#tabs-4" class="active biblio-bibtex"><?php print t('BibTeX'); ?></a></li>
  <li><a href="#tabs-5" class="active biblio-endnote"><?php print t('Endnote'); ?></a></li>
  <li><a href="#tabs-6" class="active biblio-references"><?php print t('References'); ?></a></li>
</ul>

<div id="tabs-1" class="biblio-summary">
  <div class="download-button"><a href="#" title="<?php print t('Download Summary'); ?>"> <?php print t('Download Summary'); ?></a></div>
  <ul>
    <li><label><?php print t('Title'); ?></label> <?php print $biblio_title; ?></li>
    <li><label><?php print t('Author'); ?></label> <?php print $biblio_author; ?></li>
    <li><label><?php print t('Year'); ?></label> <?php print $biblio_year; ?></li>
    <li><label><?php print t('page range'); ?></label> <?php print $biblio_page_range; ?></li>
    <li><label><?php print t('Journal title'); ?></label> <?php print $biblio_journal_title; ?></li>
    <li><label><?php print t('Volume title'); ?></label> <?php print $biblio_volume_title; ?></li>
    <li><label><?php print t('Publisher'); ?></label> <?php print $biblio_publisher; ?></li>
    <li><label><?php print t('Place of publishing'); ?></label> <?php print $biblio_place_of_publishing; ?></li>
    <li><label><?php print t('Language of the text'); ?></label> <?php print $biblio_language; ?></li>
    <li><label><?php print t('Content provider'); ?></label> <?php print $biblio_content_provider; ?></li>
    <li><label><?php print t('GRIB?'); ?></label> <?php print $biblio_grib; ?></li>
  </ul> 
</div>

<div id="tabs-2" class="biblio-abstract">
  <div class="download-button"><a href="#" title="<?php print t('Download Abstract'); ?>"> <?php print t('Download Abstract'); ?></a></div>
  <?php print $biblio_abstract; ?>
</div>

<div id="tabs-3" class="biblio-mods">
  <div class="download-button"><a href="#" title="<?php print t('Download MODS'); ?>"> <?php print t('Download MODS'); ?></a></div>
  <?php print $biblio_mods; ?>
</div>

<div id="tabs-4" class="biblio-bibtex">
  <div class="download-button"><a href="#" title="<?php print t('Download BibTeX'); ?>"> <?php print t('Download BibTeX'); ?></a></div>
  <?php print $biblio_bibtex; ?>
</div>

<div id="tabs-5" class="biblio-endnote">
  <div class="download-button"><a href="#" title="<?php print t('Download EndNote citations'); ?>"> <?php print t('Download EndNote citations'); ?></a></div>
  <?php print $biblio_endnote; ?>
</div>

<div id="tabs-6" class="biblio-references">
  <div class="download-button"><a href="#" title="<?php print t('Download References'); ?>">  <?php print t('Download References'); ?></a></div>
  <?php print $biblio_references; ?>
</div>

</div>
