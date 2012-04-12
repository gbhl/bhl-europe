<?php

/**
* @file
* Default theme implementation for displaying the search summary of solr queries.
*
* Available variables:
*
*  - $number_of_results (array)
*  		e.g.:  'label' => string 'Number of results'
*		  	   'value' => int 22
*
*  - $matches_for (array)
*  		e.g.:
*  			'label' => string 'Matches for'
		  	'info' =>
			    array
			      0 =>
			        array
			          'term' => string 'origin'
			          'field' => string 'mods_title'
			          'field_label'=> string 'Title'
			          'operator' => string 'OR'
			          'phrase' => boolean true
			          'count' => int 9999
			      1 => ...
*/


//print ('<pre>' .var_dump($_SESSION['bhle_search']) .'</pre>');
//print ('<pre>' . var_dump($number_of_results).'</pre>');
//print ('<h6>$matches_for</h6><pre>' .  var_dump($matches_for) .'</pre>');
//print ('<h6>$search_summary</h6><pre>' .  var_dump($DEBUG) .'</pre>');

?>

<div id="search_type"><?php print t((isset($matches_for) ? 'Advanced' : 'Simple') . ' search'); ?></div>
<p id="edit-search"><a href="#" title="<?php print t('Edit search query'); ?>"><?php print t('EDIT SEARCH'); ?></a></p>

<div class="search-summary search-summary-<?php print $classes; ?>">
	<div class="number_of_results">
		<h3 class="heading"><?php print $number_of_results['label']; ?>: <span class="value"><?php print $number_of_results['value']; ?></span></h3>
	</div>
<?php if(isset($matches_for)) :?>
	<div class="matches_for">
		<h3 class="heading collapsing-grip expanded"><?php print $matches_for['label']; ?>:</h3>
		<ul id="searched-querry" class="collapsing-content">
		<?php $i=1; foreach ($matches_for['info'] as $info) : ?>
			<li>
				<?php if($info['exact_phrase'] == 1) :?><span class="exact"><?php print t('exact'); ?>:</span><?php endif; ?>
				<span class="terms"><?php print $info['term']; ?></span>
				<?php if(isset($info['term_expanded'])) : ?>
					<span class="terms_expanded"> <?php print t('expanded to'); ?>: <?php print $info['term_expanded']; ?></span>
				<?php endif; ?>
				<span class="count">(<?php print $info['count']; ?>)</span>
				<span class="field"><?php print $info['field_label']; ?></span>
				<?php if(++$i <= count($matches_for['info'])) : ?>
				<span class="operator"><?php print $info['operator']; ?></span>
				<?php endif; ?>
			</li>
		<?php endforeach; ?>
		</ul>
	</div>
<?php endif; ?>
</div>