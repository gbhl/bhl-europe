<?php

/**
 * Views filter handler base class for handling all "normal" cases.
 */
class SearchApiViewsHandlerFilterDate extends SearchApiViewsHandlerFilter {

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
      '#description' => t('A date in any format understood by <a href="@doc-link">PHP</a>. For example, "@date1" or "@date2".',
          array('@doc-link' => 'http://php.net/manual/en/function.strtotime.php', '@date1' => format_date(REQUEST_TIME, 'custom', 'Y-m-d H:i:s'), '@date2' => 'now + 1 day')),
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
    $v = is_numeric($this->value) ? $this->value : strtotime($this->value, REQUEST_TIME);
    if ($v !== FALSE) {
      $this->query->condition($this->real_field, $v, $this->operator);
    }
  }

}
