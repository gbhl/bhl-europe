<?php
/**
 * @file
 * Default implementation of the visitors overview report template.
 *
 * Available variables:
 * - $data_url: complete url with params to get selected report.
 */
foreach($data_url as $data) {
?>
<h2><?php print $data['title']; ?></h2>
<div class="widgetIframe"><iframe width="100%" height="350" <?php print 'src="' . $data['url']; ?>" scrolling="no" frameborder="0" marginheight="0" marginwidth="0"></iframe></div>
<?php } ?>
