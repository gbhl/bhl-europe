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
global $language;
//dpm($item);
?>

<p id="search-step-back-link">
  <a href="/search/bhle" title="<?php print t('step back'); ?>"><?php print t('STEP BACK'); ?></a>
</p>
<div class="biblio-context-panel">
  <div class="collection-id">
    <p>
      <label><?php print t('Collection'); ?>:</label> <a href="<?php print 'http://www.bhle.eu/en/partners'; ?>" target="_blank" title="<?php print t('Collection'); ?>"><?php print $rendered['mods_record_content_source']; ?></a>
    </p>
    <p>
      <label>ID:</label> <?php print $bid; ?>
    </p>
  </div>
  <div class="content-preview content-type-<?php print $item->contentType[0]; ?>">
    <?php
      if(!$item->childs) {
        print '<img src="' . $rendered['thumbnail'] . '" alt="">';
      }
    ?>
  </div>
  <div class="biblio-read-links-block">
    <?php if ($rendered['child_links']): ?>
      <div class="biblio-read-years">
        <ul>
    <?php foreach ($rendered['child_links'] as $rendered_link): ?>
        <li><?php print $rendered_link; ?></li>
    <?php endforeach; ?>
        </ul>
      </div>
    <?php else: ?>
    <div class="biblio-read-link">
      <?php print $rendered['read_more_link']; ?>
      <div class="biblio-download-title"><?php print t('Download as format') . ':<br />'; ?></div>
      <div class="biblio-action-buttons">
        <?php print $rendered['dl_link_pdf']; ?>
        <?php print $rendered['dl_link_ocr']; ?>
        <?php print $rendered['dl_link_jp2']; ?>
      </div>
    </div>
    <?php endif; ?>
  </div>
</div>