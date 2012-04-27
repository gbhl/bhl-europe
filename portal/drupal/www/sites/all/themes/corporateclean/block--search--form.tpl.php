<div id="<?php print $block_html_id; ?>" class="<?php print $classes; ?>"<?php print $attributes; ?>>

  <?php print render($title_prefix); ?>
  <?php if ($block->subject): ?>
    <h2<?php print $title_attributes; ?>><?php print $block->subject ?></h2>
  <?php endif;?>
  <?php print render($title_suffix); ?>

  <div class="content"<?php print $content_attributes; ?>>
    <p class="advanced-search"><?php print l(t('Advanced search'), 'search/bhle', array('attributes' => array('class' => array('advanced-search-link'), 'title' => t('Advanced search')))); ?></p>
    <?php print $content ?>
  </div>

</div>
  
<div class="simple-search-links">
  <p class="advanced-buttons"><?php print l(t('Browse'), 'browse', array('attributes' => array('class' => array('browse-link'), 'title' => t('Browse')))); ?> <?php print l(t('Tutorial'), 'browse', array('attributes' => array('class' => array('tutorial-link', 'colorbox-inline'), 'title' => t('Tutorial')), 'query' => array('inline' => 'true', 'width' => '500', 'height' => '500'), 'fragment' => 'tutorial-inline-content')); ?></p>
</div>