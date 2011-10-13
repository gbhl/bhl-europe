<?php

/**
 * Views field handler base class for this module, handling all fields that
 * don't need any special treatment and don't have special options.
 *
 * Handles lists automatically.
 */
class SearchApiViewsHandlerField extends views_handler_field {

  /**
   * Array of fields that should additionally be added to the query.
   *
   * @var array
   */
  protected $additional_fields = array();

  /**
   * The associated views query object.
   *
   * @var SearchApiViewsQuery
   */
  public $query;

  /**
   * Whether this field displays a list type or not.
   *
   * @var boolean
   */
  protected $is_list_type = FALSE;

  /**
   * Initialize this field handler.
   */
  public function init(&$view, &$options) {
    parent::init($view, $options);

    $this->is_list_type = empty($this->definition['type']) ? FALSE : search_api_is_list_type($this->definition['type']);
  }

  /**
   * Specifies the options this handler uses.
   */
  public function option_definition() {
    $options = parent::option_definition();
    $options['link_to_entity'] = array('default' => FALSE);
    $options['list']['contains']['mode'] = array('default' => 'collapse');
    $options['list']['contains']['glue'] = array('default' => ', ');
    return $options;
  }

  /**
   * Provide "Link to entity" option, and list handling options if applicable.
   */
  public function options_form(&$form, &$form_state) {
    parent::options_form($form, $form_state);
    if ($this->is_list_type) {
      $form['list']['mode'] = array(
        '#type' => 'select',
        '#title' => t('List handling'),
        '#options' => array(
          'collapse' => t('Concatenate values using the list seperator'),
          'first' => t('Show first (if present)'),
          'count' => t('Show item count'),
        ),
        '#default_value' => $this->options['list']['mode'],
      );
      $form['list']['glue'] = array(
        '#type' => 'textfield',
        '#title' => t('List seperator'),
        '#default_value' => $this->options['list']['glue'],
      );
    }
    $form['link_to_entity'] = array(
      '#type' => 'checkbox',
      '#title' => t('Link this field to its entity'),
      '#description' => t('This will override any other link you have set.'),
      '#default_value' => $this->options['link_to_entity'],
    );
  }

  /**
   * Called to add the field to a query.
   */
  public function query() {
    // Add the field.
    $this->field_alias = $this->query->addField($this->real_field);
    $this->addAdditionalFields();
  }

  /**
   * Add 'additional' fields to the query.
   *
   * @param $fields
   *   An array of fields. The key is an identifier used to later find the
   *   field alias used. The value is either a string in which case it's
   *   assumed to be a field on this handler's table; or it's an array in the
   *   form of
   *   @code array('table' => $tablename, 'field' => $fieldname) @endcode
   */
  public function addAdditionalFields($fields = NULL) {
    if (!isset($fields)) {
      // notice check
      if (empty($this->additional_fields)) {
        return;
      }
      $fields = $this->additional_fields;
    }
    if (!empty($fields) && is_array($fields)) {
      foreach ($fields as $identifier => $info) {
        $this->aliases[$identifier] = $this->query->addField(is_array($info) ? $info['field'] : $info);
      }
    }
  }

  /**
   * Called to determine what to tell the clicksorter.
   */
  public function click_sort($order) {
    $this->query->sort($this->real_field, $order);
  }

  /**
   * Render the field.
   *
   * @param $values
   *   The values retrieved from the database.
   */
  public function render($values) {
    $value = $this->get_value($values);
    if (is_array($value)) {
      return $this->renderArray($value, $values);
    }
    return $this->renderLink($value, $values);
  }

  /**
   * Render a list of values.
   */
  protected function renderArray($value, $values) {
    if (isset($this->options['list']['mode'])) {
      if ($this->options['list']['mode'] == 'first') {
        $value = count($value) ? array_shift($value) : NULL;
        if (is_array($value)) {
          return $this->renderLink($value, $values);
        }
        elseif (isset($value)) {
          return $this->renderLink($value, $values);
        }
        return NULL;
      }
      if ($this->options['list']['mode'] == 'count') {
        return count($value);
      }
    }
    $vs = array();
    foreach ($value as $v) {
      $vs[] = is_array($v) ? $this->renderArray($v, $values) : $this->renderLink($v, $values);
    }
    $glue = isset($this->options['list']['glue']) ? $this->options['list']['glue'] : ', ';
    return implode($glue, $vs);
  }

  /**
   * Render a value as a link to the entity, if applicable.
   */
  protected function renderLink($value, $values) {
    $render = $this->renderValue($value, $values);
    if (!$this->options['link_to_entity']) {
      return $render;
    }
    $type = isset($values->search_api_views_entity_type) ? $values->search_api_views_entity_type : $this->query->getIndex()->entity_type;
    $entity = $values->entity;
    // $values->entity can either be the fully loaded entity, or just its ID.
    if (!is_object($entity)) {
      $entity = entity_load($type, array($entity));
      if ($entity) {
        $entity = reset($entity);
      }
    }
    if (is_object($entity) && ($url = entity_uri($type, $entity))) {
      return l($render, $url['path'], array('html' => TRUE) + $url['options']);
    }
    return $render;
  }

  /**
   * Helper function for rendering a single value. Might be overridden by child
   * classes to provide more specific rendering.
   */
  protected function renderValue($value) {
    return $value;
  }

}
