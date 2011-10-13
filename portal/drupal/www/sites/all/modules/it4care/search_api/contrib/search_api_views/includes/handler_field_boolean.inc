<?php

/**
 * Views field handler class for treating booleans.
 */
class SearchApiViewsHandlerFieldBoolean extends SearchApiViewsHandlerField {

  /**
   * Specifies the options this handler uses.
   */
  public function option_definition() {
    $options = parent::option_definition();
    unset($options['alter']['strip_tags']);
    $options['format']['true'] = array('default' => '1');
    $options['format']['false'] = array('default' => '0');
    $options['format']['html'] = array('default' => FALSE);
    return $options;
  }

  /**
   * Returns an option form for setting this handler's options.
   */
  public function options_form(array &$form, array &$form_state) {
    parent::options_form($form, $form_state);

    unset($form['alter']['strip_tags']);
    $form['format']['#weight'] = -5;
    $form['format']['true'] = array(
      '#title' => t('"True" text'),
      '#type' => 'textfield',
      '#description' => t('The text to display for true values.'),
      '#default_value' => $this->options['format']['true'],
    );
    $form['format']['false'] = array(
      '#title' => t('"False" text'),
      '#type' => 'textfield',
      '#description' => t('The text to display for false values.'),
      '#default_value' => $this->options['format']['false'],
    );
    $form['format']['html'] = array(
      '#title' => t('Use HTML'),
      '#type' => 'checkbox',
      '#description' => t('Check this if you used HTML in the display texts.'),
      '#default_value' => $this->options['format']['html'],
    );
  }

  /**
   * Helper function for rendering a single value.
   */
  protected function renderValue($value) {
    $value = $value ? $this->options['format']['true'] : $this->options['format']['false'];
    return $this->options['format']['html'] ? $value : check_plain($value);
  }

}
