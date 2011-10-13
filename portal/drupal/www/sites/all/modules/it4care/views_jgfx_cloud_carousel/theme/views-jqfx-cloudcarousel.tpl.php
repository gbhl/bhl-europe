<?php
/**
 *  @file
 *  Outputs the view.
 *
 */
?>

<div id="views-jqfx-cloudcarousel-<?php print $id; ?>" class="views-jqfx-cloudcarousel-container">

  <div id="views-jqfx-cloudcarousel-images-<?php print $id; ?>" class=<?php print $classes; ?>>
    <?php foreach ($images as $image): ?>
      <?php print $image ."\n"; ?>
    <?php endforeach; ?>
    <div id="cloudcarousel-title-text" class="cloudcarousel-title-text"></div>
    <div id="cloudcarousel-alt-text" class="cloudcarousel-alt-text"></div>
    <div id="cloudcarousel-left-but" class="cloudcarousel-left-but"></div>
    <div id="cloudcarousel-right-but" class="cloudcarousel-right-but"></div>
  </div>

</div>

