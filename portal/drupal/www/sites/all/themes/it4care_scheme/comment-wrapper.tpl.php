<?php
// $Id: comment-wrapper.tpl.php,v 1.1.2.1 2011/01/27 21:06:12 andregriffin Exp $
?>
<section id="comments" class="<?php print $classes; ?>"<?php print $attributes; ?>>
  <?php if ($content['comments'] && $node->type != 'forum'): ?>
    <?php print render($title_prefix); ?>
    <h2 class="title"><?php print t('Comments'); ?></h2>
    <?php print render($title_suffix); ?>
  <?php endif; ?>

  <?php print render($content['comments']); ?>

  <?php if ($content['comment_form']): ?>
    <section id="comment-form">
      <h2 class="title"><?php print t('Add new comment'); ?></h2>
      <?php print render($content['comment_form']); ?>
    </section> <!-- /#comment-form -->
  <?php endif; ?>
</section> <!-- /#comments -->
