<?php
/**
 * this file contains all block implementations
 *
 */


function bhle_search_block_view_top_menue() {

    $block['subject'] = null;
    $block['#class'] = array('top-menue');
    $block['content']['#type'] = 'markup';

    $out = '<ul class="top-menu-list"><li class="top-search">';
    $out .= l(t('Search'), 'search/bhle/empty', array(
                'attributes' => array(
                	'title'=>t('Search'),
                	'class' => array('search-top-link'),
                    )
            )) . '</li><li class="top-browse">';
    $out .= l(t('Browse'), 'browse', array(
    			'attributes' => array(
                    'title'=>t('Browse'),
                    'class' => array('browse-top-link')
                    )
                 )) . '</li><li class="top-tutorial">';
    $out .= l(t('Tutorial'),'',  array(
    			'attributes' => array(
                        'title'=>t('Browse'),
                        'class' => array('tutorial-top-link', 'colorbox-inline'),
                ),
    			'query' => array(
                    'width'=>'960',
                    'height'=>'500',
                    'inline'=>'true',
    			),
    			'fragment' => 'tutorial-inline-content'
               )) . '</li></ul>';
    $block['content']['#markup'] = $out;

  return $block;
}

function bhle_search_block_view_advanced_facet($bhle_facet) {

    $block['subject'] = t('Facet list');
    $block['content']['#type'] = 'markup';
    $block['content']['#markup'] = '';
    if(is_array($bhle_facet)) {
        $block['content']['#markup'] .= '<p id="facet-list-grip-multiple" class="collapsing-multiple-grip">' . t('Collapse / expand all') . '</p>';
        $block['content']['#markup'] .= '<div class="facet-list collapsing-multiple">';
        foreach($bhle_facet['fields'] as $field_name => $field) {
            $block['content']['#markup'] .= '<div class="facet-item"><h3 class="collapsing-grip"><a href="#">' . $field['#title'] . '</a></h3>';
            $block['content']['#markup'] .= '<ul class="collapsing-content">';
            foreach($field as $key => $val) {
                if (is_numeric($key)) {
                    $source = 'advanced';
                    foreach($_SESSION['bhle_search']['conditions'] as $condition) {
                        if ($condition['field'] == str_replace('__facet', '', $field_name) &&
                        ($condition['term'] == $val['#label'] || bhle_search_solr_escape($condition['term']) == $val['#label'] || $condition['term'] == bhle_search_solr_escape($val['#label']))
                        ) {
                            $delete = TRUE;
                        }
                    }
                    if($delete) {
                        $params = array('query' => array('action' => 'removefacet', 'field' => $field_name, 'key' => $key, 'source' => $source, 'last' => md5(time())));
                        $block['content']['#markup'] .= '<li>' . l(t('REMOVE:'), 'search/bhle/empty', $params) . ' <span class="value">' . $val['#label'] . '</span></li>';
                    } elseif(!$val['#label']) {
                        $params = array('query' => array('action' => 'addfacet', 'field' => $field_name, 'key' => $key, 'source' => $source, 'last' => md5(time())));
                        $block['content']['#markup'] .= '<li><span class="value">' . t('Not set') . ':' . ' ' . $val['#count'] . '</span></li>'; // TODO: use theme_list
                    } else {
                        $params = array('query' => array('action' => 'addfacet', 'field' => $field_name, 'key' => $key, 'source' => $source, 'last' => md5(time())));
                        $block['content']['#markup'] .= '<li>' . l($val['#label'] . ':', 'search/bhle/empty', $params) . ' <span class="value">' . $val['#count'] . '</span></li>'; // TODO: use theme_list
                    }
                    unset($delete);
                }
            }
            $block['content']['#markup'] .= '</ul></div>';
        }
        $block['content']['#markup'] .= '</div>';
    }
    return $block;
}

// function     bhle_search_block_view_simple_facet($bhle_facet) {
//           $block['subject'] = t('Facet list');
//           $block['content']['#type'] = 'markup';
//           $block['content']['#markup'] = '';
//           if(is_array($bhle_facet)) {
//             $block['content']['#markup'] .= '<p id="facet-list-grip-multiple" class="collapsing-multiple-grip">' . t('Collapse / expand all') . '</p>';
//             $block['content']['#markup'] .= '<div class="facet-list collapsing-multiple">';
//             foreach($bhle_facet['fields'] as $field_name => $field) {
//               $block['content']['#markup'] .= '<div class="facet-item"><h3 class="collapsing-grip"><a href="#">' . $field['#title'] . '</a></h3>';
//               $block['content']['#markup'] .= '<ul class="collapsing-content">';
//               foreach($field as $key => $val) {
//                 if (is_numeric($key)) {
//                   $source = 'simple';
//                   if(!$val['#label']) {
//                     $params = array('query' => array('action' => 'addfacet', 'field' => $field_name, 'key' => $key, 'source' => $source, 'last' => md5(time())));
//                     $block['content']['#markup'] .= '<li><span class="value">' . t('Not set') . ':' . ' ' . $val['#count'] . '</span></li>'; // TODO: use theme_list
//                   } else {
//                     $params = array('query' => array('action' => 'addfacet', 'field' => $field_name, 'key' => $key, 'source' => $source, 'last' => md5(time())));
//                     $block['content']['#markup'] .= '<li>' . l($val['#label'] . ':', 'search/bhle/empty', $params) . ' <span class="value">' . $val['#count'] . '</span></li>'; // TODO: use theme_list
//                   }
//                   unset($delete);
//                 }
//               }
//               $block['content']['#markup'] .= '</ul></div>';
//             }
//             $block['content']['#markup'] .= '</div>';
//           }
//    return $block;
// }