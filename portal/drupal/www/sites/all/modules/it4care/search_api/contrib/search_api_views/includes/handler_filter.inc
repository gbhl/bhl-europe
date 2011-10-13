<?php

/**
 * Views filter handler base class for handling all "normal" cases.
 */
class SearchApiViewsHandlerFilter extends views_handler_filter {

  /**
   * The value to filter for.
   *
   * @var mixed
   */
  public $value;

  /**
   * The operator used for filtering.
   *
   * @var string
   */
  public $operator;

  /**
   * The associated views query object.
   *
   * @var SearchApiViewsQuery
   */
  public $query;

  /**
   * Provide a list of options for the operator form.
   */
  public function operator_options() {
    return array(
      '<' => t('Is smaller than'),
      '<=' => t('Is smaller than or equal to'),
      '=' => t('Is equal to'),
      '<>' => t('Is not equal to'),
      '>=' => t('Is greater than or equal to'),
      '>' => t('Is greater than'),
    );
  }

  /**
   * Provide a form for setting the filter value.
   */
  public function value_form(&$form, &$form_state) {
    while (is_array($this->value)) {
      $this->value = $this->value ? array_shift($this->value) : NULL;
    }
    $form['value'] = array(
      '#type' => 'textfield',
      '#title' => empty($form_state['exposed']) ? t('Value') : '',
      '#size' => 30,
      '#default_value' => isset($this->value) ? $this->value : '',
    );
  }

  /**
   * Add this filter to the query.
   */
  public function query() {
    while (is_array($this->value)) {
      $this->value = $this->value ? array_shift($this->value) : NULL;
    }
    if ($this->value) {
      $this->query->condition($this->real_field, $this->value, $this->operator);
    }
  }

}
