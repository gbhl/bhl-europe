<?php
/**
 * @file
 * Template file for BHLE item view
 *
 * The array $rendered
 * The object $item
 *
 */
//dpm($item);
unset($item->olef_scientific_name);
//dpm($rendered);
?>
<!-- bhle item template -->
<div id="tabs" class="biblio">

  <ul>
    <li class="first"><a href="#tabs-1" class="active biblio-summary"><?php print t('Summary'); ?></a></li>
    <li><a href="#tabs-2" class="active biblio-abstract"><?php print t('Abstract'); ?></a></li>
    <li><a href="#tabs-3" class="active biblio-mods"><?php print t('MODS'); ?></a></li>
<!--    <li><a href="#tabs-4" class="active biblio-bibtex"><?php print t('BibTeX'); ?></a></li> -->
    <li><a href="#tabs-5" class="active biblio-endnote"><?php print t('Endnote'); ?></a></li>
    <li><a href="#tabs-6" class="active biblio-mark21"><?php print t('Mark21'); ?></a></li>
    <li><a href="#tabs-7" class="active biblio-dc"><?php print t('DC'); ?></a></li>
<!--    <li><a href="#tabs-8" class="active biblio-references"><?php print t('References'); ?></a></li> --> 
  </ul>

  <div id="tabs-1" class="biblio-summary">
    <div class="download-button"><a href="#" title="<?php print t('Download Summary'); ?>"> <?php print t('Download Summary'); ?></a></div>

    <ul>
      <?php if ($rendered['dc_title']) { ?><li><label><?php print t('Title'); ?></label><p><?php print $rendered['dc_title']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_name']) { ?><li><label><?php print t('Author'); ?></label><p><?php print $rendered['mods_name']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_date_issued']) { ?><li><label><?php print t('Year'); ?></label><p><?php print $rendered['mods_date_issued']; ?></p></li><?php } ?>
      <?php if ($rendered['page_range']) { ?><li><label><?php print t('Page range'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['journal_title']) { ?><li><label><?php print t('Journal title'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['volume_title']) { ?><li><label><?php print t('Volume title'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['mods_publisher']) { ?><li><label><?php print t('Publisher'); ?></label><p><?php print $rendered['mods_publisher']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_origin']) { ?><li><label><?php print t('Place of publishing'); ?></label><p><?php print $rendered['mods_origin']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_language']) { ?><li><label><?php print t('Language of the text'); ?></label><p><?php print $rendered['mods_language']; ?></p></li><?php } ?>
      <?php if ($rendered['fgs_ownerId']) { ?><li><label><?php print t('Content provider'); ?></label><p><?php print $rendered['fgs_ownerId']; ?></p></li><?php } ?>
      <?php if ($rendered['grib']) { ?><li><label><?php print t('GRIB?'); ?></label><p>&nbsp;</p></li><?php } ?>
    </ul>
  </div>

  <div id="tabs-2" class="biblio-abstract">
    <div class="download-button"><a href="#" title="<?php print t('Download Abstract'); ?>"> <?php print t('Download Abstract'); ?></a></div>
    <p><?php print t('No data for '); ?><?php print t('Abstract'); ?></p>
  </div>

  <div id="tabs-3" class="biblio-mods">
    <div class="download-button"><a href="#" title="<?php print t('Download MODS'); ?>"> <?php print t('Download MODS'); ?></a></div>
    <ul>
      <?php if ($rendered['mods_title']) { ?><li><label><?php print t('Title'); ?></label><p><?php print $rendered['mods_title']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_name']) { ?><li><label><?php print t('Author'); ?></label><p><?php print $rendered['mods_name']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_date_issued']) { ?><li><label><?php print t('Year'); ?></label><p><?php print $rendered['mods_date_issued']; ?></p></li><?php } ?>
      <?php if ($rendered['page_range']) { ?><li><label><?php print t('Page range'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['journal_title']) { ?><li><label><?php print t('Journal title'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['volume_title']) { ?><li><label><?php print t('Volume title'); ?></label><p>&nbsp;</p></li><?php } ?>
      <?php if ($rendered['mods_publisher']) { ?><li><label><?php print t('Publisher'); ?></label><p><?php print $rendered['mods_publisher']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_origin']) { ?><li><label><?php print t('Place of publishing'); ?></label><p><?php print $rendered['mods_origin']; ?></p></li><?php } ?>
      <?php if ($rendered['mods_language']) { ?><li><label><?php print t('Language of the text'); ?></label><p><?php print $rendered['mods_language']; ?></p></li><?php } ?>
      <?php if ($rendered['fgs_ownerId']) { ?><li><label><?php print t('Content provider'); ?></label><p><?php print $rendered['fgs_ownerId']; ?></p></li><?php } ?>
      <?php if ($rendered['grib']) { ?><li><label><?php print t('GRIB?'); ?></label><p>&nbsp;</p></li><?php } ?>
    </ul>
  </div>

<!--
  <div id="tabs-4" class="biblio-bibtex">
    <div class="download-button"><a href="#" title="<?php print t('Download BibTeX'); ?>"> <?php print t('Download BibTeX'); ?></a></div>
    <p><?php print t('No data for '); ?>BibTeX</p>
  </div>
-->

  <div id="tabs-5" class="biblio-endnote">
    <div class="download-button"><a href="#" title="<?php print t('Download EndNote citations'); ?>"><?php print t('Download EndNote citations'); ?></a></div>
    <p><?php print t('No data for '); ?><?php print t('EndNote'); ?></p>
  </div>

  <div id="tabs-6" class="biblio-references">
    <div class="download-button"><a href="#" title="<?php print t('Download Mark21'); ?>"><?php print t('Download Mark21'); ?></a></div>
    <p><?php print t('No data for '); ?><?php print t('Mark21'); ?></p>
  </div>

  <div id="tabs-7" class="biblio-references">
    <div class="download-button"><a href="#" title="<?php print t('Download DC'); ?>"><?php print t('Download DC'); ?></a></div>
    <p><?php print t('No data for '); ?><?php print t('DC'); ?></p>
  </div>

<!--
  <div id="tabs-8" class="biblio-references">
    <div class="download-button"><a href="#" title="<?php print t('Download References'); ?>"><?php print t('Download References'); ?></a></div>
    <p><?php print t('No data for '); ?><?php print t('References'); ?></p>
  </div>
-->

</div>


<!-- /bhle item template -->
