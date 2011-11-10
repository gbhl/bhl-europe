<div id="<?php print $block_html_id; ?>" class="<?php print $classes; ?>"<?php print $attributes; ?>>

  <?php print render($title_prefix); ?>
  <?php if ($block->subject): ?>
    <h2<?php print $title_attributes; ?>><?php print $block->subject ?></h2>
  <?php endif;?>
  <?php print render($title_suffix); ?>

  <div class="content"<?php print $content_attributes; ?>>
    <p class="advanced-search"><a class="advanced-search-link" href="search/bhle" title="<?php print t('Advanced search');?>"><?php print t('Advanced search');?></a></p>
    <?php print $content ?>
  </div>

</div>
  
<div class="simple-search-links">
  <p class="advanced-buttons"><a class="browse-link" href="/browse" title="Browse">Browse</a> <a class="tutorial-link colorbox-load init-colorbox-load-processed-processed cboxElement" href="/tutorial?width=900&amp;height=500" title="Tutorial">Tutorial</a></p>
</div>