<?php

/**
 * Views field handler class for treating durations.
 */
class SearchApiViewsHandlerFieldOptions extends SearchApiViewsHandlerField {

  /**
   * Specifies the options this handler uses.
   */
  public function option_definition() {
    $options = parent::option_definition();
    $options['format_name'] = array('default' => TRUE);
    return $options;
  }

  /**
   * Returns an option form for setting this handler's options.
   */
  public function options_form(&$form, &$form_state) {
    parent::options_form($form, $form_state);

    $form['format_name'] = array(
      '#title' => t('Use human-readable name'),
      '#type' => 'checkbox',
      '#description' => t("If this is checked, the values' names will be displayed instead of their internal identifiers."),
      '#default_value' => $this->options['format_name'],
      '#weight' => -5,
    );
  }

  /**
   * Helper function for rendering a single value.
   */
  protected function renderValue($value) {
    if ($this->options['format_name'] && isset($this->definition['options'][$value])) {
      $value = $this->definition['options'][$value];
    }
    return check_plain($value);
  }

}
