<?php

/**
 * @file
 * Hooks provided by the Search API module.
 */

/**
 * @addtogroup hooks
 * @{
 */

/**
 * Defines one or more search service classes a module offers.
 *
 * Note: The ids should be valid PHP identifiers.
 *
 * @see hook_search_api_service_info_alter()
 *
 * @return array
 *   An associative array of search service classes, keyed by a unique
 *   identifier and containing associative arrays with the following keys:
 *   - name: The service class' translated name.
 *   - description: A translated string to be shown to administrators when
 *     selecting a service class. Should contain all peculiarities of the
 *     service class, like field type support, supported features (like facets),
 *     the "direct" parse mode and other specific things to keep in mind.
 *   - class: The service class, which has to implement the
 *     SearchApiServiceInterface interface.
 */
function hook_search_api_service_info() {
  $services['example_some'] = array(
    'name' => t('Some Service'),
    'description' => t('Service for some search engine.'),
    'class' => 'SomeServiceClass',
    // Unknown keys can later be read by the object for additional information.
    'init args' => array('foo' => 'Foo', 'bar' => 42),
  );
  $services['example_other'] = array(
    'name' => t('Other Service'),
    'description' => t('Service for another search engine.'),
    'class' => 'OtherServiceClass',
  );

  return $services;
}

/**
 * Alter the Search API service info.
 *
 * Modules may implement this hook to alter the information that defines Search
 * API service. All properties that are available in
 * hook_search_api_service_info() can be altered here.
 *
 * @see hook_search_api_service_info()
 *
 * @param array $service_info
 *   The Search API service info array, keyed by service id.
 */
function hook_search_api_service_info_alter(array &$service_info) {
  foreach ($service_info as $id => $info) {
    $service_info[$id]['class'] = 'MyProxyServiceClass';
    $service_info[$id]['example_original_class'] = $info['class'];
  }
}

/**
 * Registers one or more callbacks that can be called at index time to add
 * additional data to the indexed items (e.g. comments or attachments to nodes),
 * alter the data in other forms or remove items from the array.
 *
 * Data-alter callbacks (which are called "Data alterations" in the UI) are
 * classes implementing the SearchApiAlterCallbackInterface interface.
 *
 * @see SearchApiAlterCallbackInterface
 *
 * @return array
 *   An associative array keyed by the callback IDs and containing arrays with
 *   the following keys:
 *   - name: The name to display for this callback.
 *   - description: A short description of what the callback does.
 *   - class: The callback class.
 *   - weight: (optional) Defines the order in which callbacks are displayed
 *     (and, therefore, invoked) by default. Defaults to 0.
 */
function hook_search_api_alter_callback_info() {
  $callbacks['example_random_alter'] = array(
    'name' => t('Random alteration'),
    'description' => t('Alters all passed item data completely randomly.'),
    'class' => 'ExampleRandomAlter',
    'weight' => 100,
  );
  $callbacks['example_add_comments'] = array(
    'name' => t('Add comments'),
    'description' => t('For nodes and similar entities, adds comments.'),
    'class' => 'ExampleAddComments',
  );

  return $callbacks;
}

/**
 * Registers one or more processors. These are classes implementing the
 * SearchApiProcessorInterface interface which can be used at index and search
 * time to pre-process item data or the search query, and at search time to
 * post-process the returned search results.
 *
 * @see SearchApiProcessorInterface
 *
 * @return array
 *   An associative array keyed by the processor id and containing arrays
 *   with the following keys:
 *   - name: The name to display for this processor.
 *   - description: A short description of what the processor does at each
 *     phase.
 *   - class: The processor class, which has to implement the
 *     SearchApiProcessorInterface interface.
 *   - weight: (optional) Defines the order in which processors are displayed
 *     (and, therefore, invoked) by default. Defaults to 0.
 */
function hook_search_api_processor_info() {
  $callbacks['example_processor'] = array(
    'name' => t('Example processor'),
    'description' => t('Pre- and post-processes data in really cool ways.'),
    'class' => 'ExampleSearchApiProcessor',
    'weight' => -1,
  );
  $callbacks['example_processor_minimal'] = array(
    'name' => t('Example processor 2'),
    'description' => t('Processor with minimal description.'),
    'class' => 'ExampleSearchApiProcessor2',
  );

  return $callbacks;
}

/**
 * Allows you to log or alter the items that are indexed.
 *
 * Please be aware that generally preventing the indexing of certain items is
 * deprecated. This is better done with data alterations, which can easily be
 * configured and only added to indexes where this behaviour is wanted.
 * If your module will use this hook to reject certain items from indexing,
 * please document this clearly to avoid confusion.
 *
 * @param array $items
 *   The entities that will be indexed (before calling any data alterations).
 * @param SearchApiIndex $index
 *   The search index on which items will be indexed.
 */
function hook_search_api_index_items_alter(array &$items, SearchApiIndex $index) {
  foreach ($items as $id => $item) {
    if ($id % 5 == 0) {
      unset($items[$id]);
    }
  }
  example_store_indexed_entity_ids($index->entity_type, array_keys($items));
}

/**
 * Lets modules alter a search query before executing it.
 *
 * @param SearchApiQueryInterface $query
 *   The SearchApiQueryInterface object representing the search query.
 */
function hook_search_api_query_alter(SearchApiQueryInterface $query) {
  $info = entity_get_info($index->entity_type);
  $query->condition($info['entity keys']['id'], 0, '!=');
}

/**
 * Act on search servers when they are loaded.
 *
 * @param array $servers
 *   An array of loaded SearchApiServer objects.
 */
function hook_search_api_server_load(array $servers) {
  foreach ($servers as $server) {
    db_insert('example_search_server_access')
      ->fields(array(
        'server' => $server->machine_name,
        'access_time' => REQUEST_TIME,
      ))
      ->execute();
  }
}

/**
 * A new search server was created.
 *
 * @param SearchApiServer $server
 *   The new server.
 */
function hook_search_api_server_insert(SearchApiServer $server) {
  db_insert('example_search_server')
    ->fields(array(
      'server' => $server->machine_name,
      'insert_time' => REQUEST_TIME,
    ))
    ->execute();
}

/**
 * A search server was edited, enabled or disabled.
 *
 * @param SearchApiServer $server
 *   The edited server.
 */
function hook_search_api_server_update(SearchApiServer $server) {
  if ($server->name != $server->original->name) {
    db_insert('example_search_server_name_update')
      ->fields(array(
        'server' => $server->machine_name,
        'update_time' => REQUEST_TIME,
      ))
      ->execute();
  }
}

/**
 * A search server was deleted.
 *
 * @param SearchApiServer $server
 *   The deleted server.
 */
function hook_search_api_server_delete(SearchApiServer $server) {
  db_insert('example_search_server_update')
    ->fields(array(
      'server' => $server->machine_name,
      'update_time' => REQUEST_TIME,
    ))
    ->execute();
  db_delete('example_search_server')
    ->condition('server', $server->machine_name)
    ->execute();
}

/**
* Define default search servers.
*
* @return array
*   An array of default search servers, keyed by machine names.
*
* @see hook_default_search_api_server_alter()
*/
function hook_default_search_api_server() {
  $defaults['main'] = entity_create('search_api_server', array(
    'name' => 'Main server',
    'machine_name' => 'main',// Must be same as the used array key.
    // Other properties ...
  ));
  return $defaults;
}

/**
* Alter default search servers.
*
* @param array $defaults
*   An array of default search servers, keyed by machine names.
*
* @see hook_default_search_api_server()
*/
function hook_default_search_api_server_alter(array &$defaults) {
  $defaults['main']->name = 'Customized main server';
}

/**
 * Act on search indexes when they are loaded.
 *
 * @param array $indexes
 *   An array of loaded SearchApiIndex objects.
 */
function hook_search_api_index_load(array $indexes) {
  foreach ($indexes as $index) {
    db_insert('example_search_index_access')
      ->fields(array(
        'index' => $index->machine_name,
        'access_time' => REQUEST_TIME,
      ))
      ->execute();
  }
}

/**
 * A new search index was created.
 *
 * @param SearchApiIndex $index
 *   The new index.
 */
function hook_search_api_index_insert(SearchApiIndex $index) {
  db_insert('example_search_index')
    ->fields(array(
      'index' => $index->machine_name,
      'insert_time' => REQUEST_TIME,
    ))
    ->execute();
}

/**
 * A search index was edited in any way.
 *
 * @param SearchApiIndex $index
 *   The edited index.
 */
function hook_search_api_index_update(SearchApiIndex $index) {
  if ($index->name != $index->original->name) {
    db_insert('example_search_index_name_update')
      ->fields(array(
        'index' => $index->machine_name,
        'update_time' => REQUEST_TIME,
      ))
      ->execute();
  }
}

/**
 * A search index was scheduled for reindexing
 *
 * @param SearchApiIndex $index
 *   The edited index.
 * @param $clear
 *   Boolean indicating whether the index was also cleared.
 */
function hook_search_api_index_reindex(SearchApiIndex $index, $clear = FALSE) {
  db_insert('example_search_index_reindexed')
    ->fields(array(
      'index' => $index->id,
      'update_time' => REQUEST_TIME,
    ))
    ->execute();
}

/**
 * A search index was deleted.
 *
 * @param SearchApiIndex $index
 *   The deleted index.
 */
function hook_search_api_index_delete(SearchApiIndex $index) {
  db_insert('example_search_index_update')
    ->fields(array(
      'index' => $index->machine_name,
      'update_time' => REQUEST_TIME,
    ))
    ->execute();
  db_delete('example_search_index')
    ->condition('index', $index->machine_name)
    ->execute();
}

/**
* Define default search indexes.
*
* @return array
*   An array of default search indexes, keyed by machine names.
*
* @see hook_default_search_api_index_alter()
*/
function hook_default_search_api_index() {
  $defaults['main'] = entity_create('search_api_index', array(
    'name' => 'Main index',
    'machine_name' => 'main',// Must be same as the used array key.
    // Other properties ...
  ));
  return $defaults;
}

/**
* Alter default search indexes.
*
* @param array $defaults
*   An array of default search indexes, keyed by machine names.
*
* @see hook_default_search_api_index()
*/
function hook_default_search_api_index_alter(array &$defaults) {
  $defaults['main']->name = 'Customized main index';
}

/**
 * @} End of "addtogroup hooks".
 */
