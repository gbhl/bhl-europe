<?php
/**
 * @file
 * Template file for BHLE block view
 *
 * The object $item
 * The string $bid
 * 
 *
 */
//dpm($rendered, 'rendered');
?>

<p id="search-step-back-link">
  <a href="/search/bhle" title="step back">STEP BACK</a>
</p>
<div class="biblio-context-panel">
  <div class="collection-id">
    <p>
      <label>Collection:</label> <a href="/collection/nbn" title="Collection"><?php print $rendered['mods_record_content_source']; ?></a>
    </p>
    <p>
      <label>ID:</label> <?php print $bid; ?>
    </p>
  </div>
  <div class="content-preview">
    <img src="<?php print $rendered['thumbnail']; ?>" alt="">
  </div>
  <div class="biblio-read-links-block">
    <div class="biblio-read-years">
      <a href="/period/1670">1670</a>
      <a href="/period/1910">1910</a>
      <a href="/period/1930">1930</a>
      <a href="/period/1960">1960</a>
    </div>
    <div class="biblio-read-link">
      <?php print $rendered['read_more_link']; ?>
      <div class="biblio-download-title"><?php print t('Download as format') . ':<br />'; ?></div>
      <div class="biblio-action-buttons">
        <?php print $rendered['dl_link_pdf']; ?>
        <?php print $rendered['dl_link_ocr']; ?>
        <?php print $rendered['dl_link_jp2']; ?>
      </div>
    </div>
  </div>
</div>