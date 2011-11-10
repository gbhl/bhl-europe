<?php

/**
 * @file
 * Default theme implementation for displaying a single search result.
 *
 * This template renders a single search result and is collected into
 * search-results.tpl.php. This and the parent template are
 * dependent to one another sharing the markup for definition lists.
 *
 * Available variables:
 * - $url: URL of the result.
 * - $title: Title of the result.
 * - $snippet: A small preview of the result. Does not apply to user searches.
 * - $info: String of all the meta information ready for print. Does not apply
 *   to user searches.
 * - $info_split: Contains same data as $info, split into a keyed array.
 * - $module: The machine-readable name of the module (tab) being searched, such
 *   as "node" or "user".
 * - $title_prefix (array): An array containing additional output populated by
 *   modules, intended to be displayed in front of the main title tag that
 *   appears in the template.
 * - $title_suffix (array): An array containing additional output populated by
 *   modules, intended to be displayed after the main title tag that appears in
 *   the template.
 *
 * Default keys within $info_split:
 * - $info_split['type']: Node type (or item type string supplied by module).
 * - $info_split['user']: Author of the node linked to users profile. Depends
 *   on permission.
 * - $info_split['date']: Last update of the node. Short formatted.
 * - $info_split['comment']: Number of comments output as "% comments", %
 *   being the count. Depends on comment.module.
 *
 * Other variables:
 * - $classes_array: Array of HTML class attribute values. It is flattened
 *   into a string within the variable $classes.
 * - $title_attributes_array: Array of HTML attributes for the title. It is
 *   flattened into a string within the variable $title_attributes.
 * - $content_attributes_array: Array of HTML attributes for the content. It is
 *   flattened into a string within the variable $content_attributes.
 *
 * Since $info_split is keyed, a direct print of the item is possible.
 * This array does not apply to user searches so it is recommended to check
 * for its existence before printing. The default keys of 'type', 'user' and
 * 'date' always exist for node searches. Modules may provide other data.
 * @code
 *   <?php if (isset($info_split['comment'])) : ?>
 *     <span class="info-comment">
 *       <?php print $info_split['comment']; ?>
 *     </span>
 *   <?php endif; ?>
 * @endcode
 *
 * To check for all available data within $info_split, use the code below.
 * @code
 *   <?php print '<pre>'. check_plain(print_r($info_split, 1)) .'</pre>'; ?>
 * @endcode
 *
 * @see template_preprocess()
 * @see template_preprocess_search_result()
 * @see template_process()
 */
?>
<li class="<?php print $classes; ?>"<?php print $attributes; ?>><!-- PID=<?php print $PID; ?>  -->
  <div class="content-type-logo radius gradient-vertical-grey"><p class="label"><?php print t($content_type) ?></p></div>
  <?php print render($title_prefix); ?>
  <h3 class="result-title"<?php print $title_attributes; ?>>
    <a href="<?php print $url; ?>"><?php print $title; ?></a>
    <span class="gradient-overflow">&nbsp;</span></h3>
  <?php print render($title_suffix); ?>
  <div class="search-snippet-info">
    <?php if ($snippet) : ?>
      <p class="search-snippet"<?php print $content_attributes; ?>><?php print $snippet; ?></p>
    <?php endif; ?>
    <?php if ($info_split) : ?>
      <ul class="search-info-list">
      <?php foreach($info_split as $key => $value) : ?>
        <li class="search-info-item metadata-<?php print $key ?>">
        <a href="#" title="<?php print t($key) ?>" class="content-<?php print $key ?>"><?php print $value ?></a><span class="gradient-overflow">&nbsp;</span></li>
      <?php endforeach; ?>
      </ul>
    <?php endif; ?>
  </div>
  <div class="result-actions">
      <p class="result-buttons view-read radius gradient-vertical-grey">
      	<a title="View record" href="/fedora/objects/<?php print(urlencode($PID)); url() ?>/datastreams/MODS/content" class="link-view first">View record</a>
      	<a title="Read <?php print t($content_type) ?>" href="/fedora/objects/<?php print(urlencode($PID)); ?>/methods/bhle-service%3AbookSdef/bookreader?ui=full#page/1/mode/1up" class="link-read last">Read <?php print t($content_type) ?></a>
      </p>
  </div>
</li>