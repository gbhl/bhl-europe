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


//print ('<pre>' .var_dump($_SESSION['bhle_search']) .'</pre>');
//print ('<pre>' . var_dump($number_of_results).'</pre>');
// print ('<h6>$matches_for</h6><pre>' .  var_dump($matches_for) .'</pre>');
// print ('<h6>$search_summary</h6><pre>' .  var_dump($DEBUG) .'</pre>');

?>

<div id="search_type"><?php print(isset($matches_for) ? "Advanced" : "Simple" );?> search</div>
<p id="search-step-back-link"><a href="/search/bhle" title="step back"><?php print t('STEP BACK'); ?></a></p>

<div class="search-summary search-summary-<?php print $classes; ?>">
	<div class="number_of_results">
		<h3 class="heading"><?php print $number_of_results['label']; ?>: <span class="value"><?php print $number_of_results['value']; ?></span></h3>
		<p id="save-query-link"><a href="#" title="Save query"><?php print t('Save query'); ?></a></p>
	</div>
<?php if(isset($matches_for)) :?>
	<div class="matches_for">
		<h3 class="heading collapsing-grip expanded"><?php print $matches_for['label']; ?>:</h3>
		<ul id="searched-querry" class="collapsing-content">
		<?php foreach ($matches_for['search_conditions'] as $condition) : ?>
			<li>
				<?php if($condition['exact_phrase'] == 1) :?><span class="exact">exact:</span><?php endif; ?>
				<span class="terms"><?php print $condition['term']; ?></span>
				<span class="count">(<?php print $condition['count']; ?>)</span>
				<span class="field"><?php print $condition['field_label']; ?></span>
				<span class="operator"><?php print $condition['operator']; ?></span>
			</li>
		<?php endforeach; ?>
		</ul>
	</div>
<?php endif; ?>
</div>