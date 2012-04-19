<?php
/**
 * @file
 * Template file for BHLE item view
 *
 * The array $rendered
 * The object $item
 * 
 * Summary, Abstract, MODS, Endnote, Bibtex  
 *
 */
unset($item->olef_scientific_name);
dpm($item);
//dpm($rendered);
?>
<!-- bhle item template -->
<div id="tabs" class="biblio">

  <ul>
    <li class="first"><a href="#tabs-1" class="active biblio-summary"><?php print t('Summary'); ?></a></li>
    <?php if ($rendered['abstract']) { ?><li><a href="#tabs-2" class="active biblio-abstract"><?php print t('Abstract'); ?></a></li><?php } ?>
    <li><a href="#tabs-3" class="active biblio-mods"><?php print t('MODS'); ?></a></li>
    <li><a href="#tabs-4" class="active biblio-endnote"><?php print t('Endnote'); ?></a></li>
    <li><a href="#tabs-5" class="active biblio-bibtex"><?php print t('Bibtex'); ?></a></li>
    <li><a href="#tabs-6" class="active biblio-olef"><?php print t('OLEF'); ?></a></li>
  </ul>

  <div id="tabs-1" class="biblio-summary">
    <?php if ($rendered['dl_link_summary']) { print '<div class="download-button">' . $rendered['dl_link_summary'] . '</div>'; } ?>
    <?php if ($rendered['meta_summary_source']) { print $rendered['meta_summary_source']; } ?>
  </div>

  <?php if ($rendered['abstract']) { ?>
  <div id="tabs-2" class="biblio-abstract">
    <?php if ($rendered['dl_link_abstract']) { print '<div class="download-button">' . $rendered['dl_link_abstract'] . '</div>'; } ?>
    <?php if ($rendered['abstract']) { print $rendered['abstract']; } ?>
  </div>
  <?php } ?>

  <div id="tabs-3" class="biblio-mods">
    <?php if ($rendered['dl_link_mods']) { print '<div class="download-button">' . $rendered['dl_link_mods'] . '</div>'; } ?>
    <?php if ($rendered['meta_mods_source']) { print '<pre>' . $rendered['meta_mods_source'] . '</pre>'; } ?>
  </div>

  <div id="tabs-4" class="biblio-endnote">
    <?php if ($rendered['dl_link_endnote']) { print '<div class="download-button">' . $rendered['dl_link_endnote'] . '</div>'; } ?>
    <?php if ($rendered['meta_endnote_source']) { print '<pre>' . $rendered['meta_endnote_source'] . '</pre>'; } ?>
  </div>

  <div id="tabs-5" class="biblio-bibtex">
    <?php if ($rendered['dl_link_bibtex']) { print '<div class="download-button">' . $rendered['dl_link_bibtex'] . '</div>'; } ?>
    <?php if ($rendered['meta_bibtex_source']) { print '<pre>' . $rendered['meta_bibtex_source'] . '</pre>'; } ?>
  </div>

  <div id="tabs-6" class="biblio-oleft">
    <?php if ($rendered['dl_link_olef']) { print '<div class="download-button">' . $rendered['dl_link_olef'] . '</div>'; } ?>
    <?php if ($rendered['meta_olef_source']) { print '<pre>' . $rendered['meta_olef_source'] . '</pre>'; } ?>
  </div>
  
</div>


<!-- /bhle item template -->
