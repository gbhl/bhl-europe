<?php
/*
 * the querystore makes heavy use of the field api, here are some very useful links:
 *
 *  - INTRODUCTION: http://ado.io/blog/drupal-7/working-programmatically-with-fields-in-drupal-7
 *  - Field API: http://api.drupal.org/api/drupal/modules!field!field.module/group/field/7
 *  - Field API glossary: https://drupal.org/node/443540
 *  - Field API tutorial: http://drupal.org/node/707832
 *  - Formatting fields: http://www.computerminds.co.uk/articles/creating-new-field-formatters-drupal-7
 *  - read from and write to fields: http://drupal.org/node/1047600#comment-4923198
 *
 */

define('BHLE_SEARCH_FIELD_QUERYSTORE', 'user_querystore');

/**
* Implements hook_field_formatter_info().
*/
function bhle_search_field_formatter_info() {
    return array(
        // the key must be unique, so it's best to prefix with your module's name.
    	'bhle_search_stored_query' => array(
          	'label' => t('Stored query from text_long field'),
          	'field types' => array('text_long')
         ),
    );
}

/**
* Implements hook_field_formatter_view().
*
*/
function bhle_search_field_formatter_view($entity_type, $entity, $field, $instance, $langcode, $items, $display) {

    $header = array(
        t('Name'),
        t('Query'),
        array(
              'data' => t('Operations'),
              'colspan' => 2,
        ),
    );
    $rows = array();

    foreach ($items as $delta => $item) {

        $stored_query_info = unserialize($item['value']);

        //TODO put into theme -------------
        $query = t('expand search') . ': ' . ($stored_query_info['data']['expand_search'] == 0 ? t('no') : t('yes') );
	    $query .= ' | ' . t('exact word / phrase') . ': ' .( $stored_query_info['data']['exact_phrase'] == 0 ? t('no') : t('yes') );
        $query .= '<ul class="query">';
        $stored_query_info['data'];
        foreach($stored_query_info['data']['conditions'] as $condition){
            $query_row = "";
            $query_row .= human_readable_field_name($condition['field']) . ": " . $condition['term'];
            if($condition['exact_phrase'] != null) {
                $query_row .= ' ('. t('exact') . ')';
            }
            $query .= '<li>' . check_plain($query_row) . '</li>';
        }
        $query .= '</ul>';
        // --------------------------------

        $rows[] = array (
            'data' => array(
                check_plain($stored_query_info['name']),
                $query,
                l(t('Execute query'), 'bhle_search/querystore/'.$stored_query_info['name'].'/execute'),
                l(t('Delete query'), 'bhle_search/querystore/'.$stored_query_info['name'].'/delete'),
    //          l(t('Load query'), 'bhle_search/querystore/'.$stored_query_info['name'].'/load'),
            )
        );

        $elements[$delta] = array(
        	'#markup' => $html,
            );
    }

    $build['querystore_table'] = array(
        '#theme' => 'table',
        '#header' => $header,
        '#rows' => $rows,
    );

    return array($build);
}

/**
 * Creates nessecary fields
 *
 * TODO move into module update and install script
 *
 */
function bhle_search_check_fields() {

//     bhle_search_drop_fields();

    if(!field_info_field(BHLE_SEARCH_FIELD_QUERYSTORE)) {

       field_create_field(array(
            'field_name' => BHLE_SEARCH_FIELD_QUERYSTORE,
            'type' => 'text_long',
            'cardinality' => FIELD_CARDINALITY_UNLIMITED,
       		'locked' => TRUE,
            'settings' => array(),
       		'entity_types' => array('user'),
       ));

        field_create_instance(array(
            'field_name' => BHLE_SEARCH_FIELD_QUERYSTORE,
            'entity_type' => 'user',
            'bundle' => 'user',
            'label' => t("Query store"),
        	'description' => t("The user's saved queries"),
            'display' => array(
        		'default' => array(
        		    // The type of the display formatter (defined above in bhle_search_field_formatter_info())
        			'type'=> 'bhle_search_stored_query',
        			// The name of the module which implements the display formatter.
        			'module' => 'bhle_search',
        		),
            ),
        ));
    }
}

/**
 * Helper function for development
 */
function bhle_search_drop_fields() {
    field_delete_field(BHLE_SEARCH_FIELD_QUERYSTORE);
    field_cron();
}

/**
 * create or update saved query
 *
 * @param unknown_type $data
 * @param unknown_type $name
 */
function bhle_search_querystore_save($data, $name){
    global $user;

    // normalize $name
    $name = preg_replace("/[^\w\s]/", "_", $name);

    $obj = entity_metadata_wrapper('user', $user);
//     $fieldName = BHLE_SEARCH_FIELD_QUERYSTORE;

    $queryStore = $obj->user_querystore->value();

    // create array to be stored
    $new_query = array('name'=>$name, 'data'=>$data);
    $new_query_serialized = serialize($new_query);

    $key = bhle_search_querystore_find_key($queryStore, $name);
    if($key !== false){
        $queryStore[$key] = $new_query_serialized;
    } else {
        $queryStore[] = $new_query_serialized;
    }

    // save changes
    $obj->user_querystore = $queryStore;
    $obj->save();
}

/**
 * Delete stored query
 *
 * @param unknown_type $name
 */
function bhle_search_querystore_delete($name){
    global $user;

    $obj = entity_metadata_wrapper('user', $user);
    $queryStore = $obj->user_querystore->value();

    $key = bhle_search_querystore_find_key($queryStore, $name);
    if($key !== false){
        unset($queryStore[$key]);
    }

    // save changes
    $obj->user_querystore = $queryStore;
    $obj->save();
}

/**
 * load query data from store
 *
 * @param unknown_type $name
 */
function bhle_search_querystore_get($name){
    global $user;

    $obj = entity_metadata_wrapper('user', $user);
    $queryStore = $obj->user_querystore->value();

    $key = bhle_search_querystore_find_key($queryStore, $name);
    if($key !== false){
        $storedquery = unserialize($queryStore[$key]);
        return $storedquery['data'];
    }

}


/**
 * returns the array key of the stored query with thew given $name.
 * If fhe query is not found FALSE is returned.
 *
 * @param array $querystore_field the BHLE_SEARCH_FIELD_QUERYSTORE field
 * @param unknown_type $name name uder which the query is stored
 */
function bhle_search_querystore_find_key($querystore, $name){

    foreach($querystore as $key=>$value){
        $stored_query = unserialize($value);
        if($stored_query['name'] == $name){
            return $key;
        }
    }
    return false;
}

/**
 * API callback executed as menu callback
 *
 * @param unknown_type $goto_path
 * @param unknown_type $action
 * @param unknown_type $name
 */
function bhle_search_querystore_action($goto_path, $action, $name){

    switch ($action){
        case "delete":
            bhle_search_querystore_delete($name);
            break;
        case "execute":
            $query_data = bhle_search_querystore_get($name);
            if($query_data){
                $_SESSION['bhle_search']['conditions'] = $query_data['conditions'];
                $_SESSION['bhle_search']['expand_search'] = $query_data['expand_search'];
                $_SESSION['bhle_search']['exact_phrase'] = $query_data['exact_phrase'];
                $_SESSION['bhle_search']['simple_search'] = $query_data['simple_search'];
            }
            break;
    }

    drupal_goto($goto_path);
}
