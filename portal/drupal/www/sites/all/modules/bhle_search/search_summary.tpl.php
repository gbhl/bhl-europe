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
		  	'search_conditions' =>
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


print ('<pre>' .var_dump($_SESSION['bhle_search']) .'</pre>');
// print ( var_dump($number_of_results) . var_dump($matches_for) .'</pre>');

?>
<div class="search-summary search-summary-<?php print $classes; ?>">
	<div class="number_of_results">
		<label><?php print $number_of_results['label']; ?>:</label><?php print $number_of_results['value']; ?>
	</div>
	<div class="matches_for">
		<label><?php print $matches_for['label']; ?>:</label>
		<ul>
		<?php foreach ($matches_for['search_conditions'] as $condition) : ?>
			<li>
				<?php if($condition['phrase']) :?><span class="exact">exact:</span><?php endif; ?>
				<span class="terms"><?php print $condition['term']; ?></span>
				<span class="count">(<?php print $condition['count']; ?>)</span>
				<span class="field"><?php print $condition['field']; ?></span>
				<span class="operator"><?php print $condition['operator']; ?></span>
			</li>
		<?php endforeach; ?>
		</ul>
	</div>
</div>