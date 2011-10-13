<?php

/**
 * Views field handler class for treating durations.
 */
class SearchApiViewsHandlerFieldEntity extends SearchApiViewsHandlerField {

  /**
   * @var string
   */
  protected $entity_type;

  /**
   * Initialize this field handler.
   */
  public function init(&$view, &$options) {
    parent::init($view, $options);

    $this->entity_type = search_api_extract_inner_type($this->definition['type']);
  }

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
      '#title' => t('Display label instead of ID'),
      '#type' => 'checkbox',
      '#description' => t("If this is checked, the entities' labels will be displayed instead of their numeric identifiers."),
      '#default_value' => $this->options['format_name'],
      '#weight' => -5,
    );
  }

  /**
   * Render a value as a link to the entity, if applicable.
   */
  protected function renderLink($value, $values) {
    $render = $this->renderValue($value, $values);
    if (!$this->options['link_to_entity'] || !is_numeric($value)) {
      return $render;
    }
    $entity = entity_load($this->entity_type, array($value));
    if (!isset($entity[$value])) {
      return $render;
    }
    $entity = $entity[$value];
    $url = entity_uri($this->entity_type, $entity);
    if ($this->entity_type == 'file') {
      $url = array(
        'path' => file_create_url($url),
        'options' => array(),
      );
    }
    return l($render, $url['path'], array('html' => TRUE) + $url['options']);
  }

  /**
   * Helper function for rendering a single value.
   */
  protected function renderValue($value) {
    if ($this->options['format_name']) {
      $value = (int) $value;
      $entity = entity_load($this->entity_type, array($value));
      if (isset($entity[$value])) {
        $entity = $entity[$value];
        $label = entity_label($this->entity_type, $entity);
        if ($label) {
          return check_plain($label);
        }
      }
    }
    return check_plain($value);
  }

}
