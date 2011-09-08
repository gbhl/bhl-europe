<?php
// $Id: comment.tpl.php,v 1.4.2.3 2011/02/06 22:47:17 andregriffin Exp $
?>
<article class="<?php print $classes . ' ' . $zebra; ?>"<?php print $attributes; ?>>
  
  <header>
    <?php print $picture ?>
    
    <span class="submitted"><?php print $author; ?> - <?php print $created; ?></span>

    <?php if ($new): ?>
      <span class="new"><?php print $new ?></span>
    <?php endif; ?>

    <?php print render($title_prefix); ?>
    <h3<?php print $title_attributes; ?>><?php print $title ?></h3>
    <?php print render($title_suffix); ?>
  </header>

  <div class="content"<?php print $content_attributes; ?>>
    <?php hide($content['links']); print render($content); ?>
    <?php if ($signature): ?>
    <div class="user-signature clearfix">
      <?php print $signature ?>
    </div>
    <?php endif; ?>
  </div>

  <?php if (!empty($content['links'])): ?>
    <footer>
      <?php print render($content['links']) ?>
    </footer>
  <?php endif; ?>

</article> <!-- /.comment -->
