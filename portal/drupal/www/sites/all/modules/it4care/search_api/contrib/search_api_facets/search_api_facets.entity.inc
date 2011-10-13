<?php

/**
 * Class representing a search facet.
 */
class SearchApiFacet extends Entity {

  /* Database values that will be set when object is loaded: */

  /**
   * @var integer
   */
  public $id = 0;

  /**
   * @var string
   */
  public $delta = '';

  /**
   * @var string
   */
  public $index_id = '';

  /**
   * @var string
   */
  public $field = '';

  /**
   * @var string
   */
  public $name = '';

  /**
   * @var integer
   */
  public $enabled = 1;

  /**
   * @var array
   */
  public $options = array();

  /**
   * Constructor as a helper to the parent constructor.
   */
  public function __construct(array $values = array()) {
    parent::__construct($values, 'search_api_facet');
    // Always set default options.
    // Since at this point, $this->options might still be a serialized string,
    // this has to be done a bit hack-ish.
    if (!is_array($this->options)) {
      if ($this->options) {
        $this->options = unserialize($this->options);
        $was_serialized = TRUE;
      }
      else {
        $this->options = array();
      }
    }
    $this->options += array(
      'limit'             => 10,
      'display_more_link' => FALSE,
      'more_limit'        => 10,
      'min_count'         => 1,
      'sort'              => 'count',
      'missing'           => FALSE,
      'show_active'       => TRUE,
      'default_true'      => TRUE,
      'ids_list'          => array(),
      'type'              => '',
    );
    if (empty($this->options['field_name'])) {
      $index = search_api_index_load($this->index_id);
      $this->options['field_name'] = isset($index->options['fields'][$this->field]['name']) ? $index->options['fields'][$this->field]['name'] : $this->field;
    }
    if (isset($was_serialized)) {
      $this->options = serialize($this->options);
    }
  }

  /**
   * Saves this facet.
   * If $this->delta is empty, it will be computed automatically. In this case,
   * or if $this->is_new is set, the facet will be inserted. Otherwise, it will
   * be updated.
   */
  public function save() {
    if (empty($this->delta)) {
      // The facet should be newly created if save() is invoked with $this->delta being empty.
      $this->id = NULL;

      // Construct a delta using index id and field.
      // We have to ensure that it's both unique and at most 32 characters long,
      // leading to a bit of complexity.
      $delta = substr($this->index_id, 0, 20) . '_' . preg_replace('/\W+/', '_', $this->field);
      $base = $delta = substr($delta, 0, 32);
      $i = 0;
      while (search_api_facet_load($delta)) {
        $suffix = '_' . ++$i;
        $delta = substr($base, 0, 32 - strlen($suffix)) . $suffix;
      }
      $this->delta = $delta;
    }

    return parent::save();
  }

}
