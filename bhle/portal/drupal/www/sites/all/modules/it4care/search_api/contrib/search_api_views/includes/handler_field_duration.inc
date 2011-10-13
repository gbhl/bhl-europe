<?php

/**
 * Views field handler class for treating durations.
 */
class SearchApiViewsHandlerFieldDuration extends SearchApiViewsHandlerField {

  /**
   * Specifies the options this handler uses.
   */
  public function option_definition() {
    $options = parent::option_definition();
    $options['format_interval'] = array('default' => TRUE);
    return $options;
  }

  /**
   * Returns an option form for setting this handler's options.
   */
  public function options_form(array &$form, array &$form_state) {
    parent::options_form($form, $form_state);

    $form['format_interval'] = array(
      '#title' => t('Format as interval'),
      '#type' => 'checkbox',
      '#default_value' => $this->options['format_interval'],
      '#weight' => -5,
    );
  }

  /**
   * Helper function for rendering a single value.
   */
  protected function renderValue($value) {
    if ($this->options['format_interval']) {
      return format_interval($value);
    }
    return check_plain($value);
  }

}
