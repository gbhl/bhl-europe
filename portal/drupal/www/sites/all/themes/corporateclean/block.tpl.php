<div id="<?php print $block_html_id; ?>" class="<?php print $classes; ?>"<?php print $attributes; ?>>

<?php print render($title_prefix); ?>
<?php if ($block->subject): ?>
<h2<?php print $title_attributes; ?>><span><?php print $block->subject ?></span></h2>
<?php endif;?>
<?php print render($title_suffix); ?>

<div class="content"<?php print $content_attributes; ?>>
<div class="content-inner">
<?php print $content ?>
</div>
</div>

</div>