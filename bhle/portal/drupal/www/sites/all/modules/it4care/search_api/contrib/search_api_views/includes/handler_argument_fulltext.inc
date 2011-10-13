<?php

/**
 * Views argument handler class for handling fulltext fields.
 */
class SearchApiViewsHandlerArgumentFulltext extends SearchApiViewsHandlerArgumentText {

  /**
   * Specify the options this filter uses.
   */
  public function option_definition() {
    $options = parent::option_definition();
    $options['fields'] = array('default' => array());
    return $options;
  }

  /**
   * Extend the options form a bit.
   */
  public function options_form(array &$form, array &$form_state) {
    parent::options_form($form, $form_state);

    $index = search_api_index_load(substr($this->table, 17));
    if (!empty($index->options['fields'])) {
      $fields = array();
      $f = $index->options['fields'];
      foreach ($index->getFulltextFields() as $name) {
        $fields[$name] = $f[$name]['name'];
      }
    }
    if (!empty($fields)) {
      $form['fields'] = array(
        '#type' => 'select',
        '#title' => t('Searched fields'),
        '#description' => t('Select the fields that will be searched. If no fields are selected, all available fulltext fields will be searched.'),
        '#options' => $fields,
        '#size' => min(4, count($fields)),
        '#multiple' => TRUE,
        '#default_value' => $this->options['fields'],
      );
    }
    else {
      $form['fields'] = array(
        '#type' => 'value',
        '#value' => array(),
      );
    }
  }

  /**
   * Set up the query for this argument.
   *
   * The argument sent may be found at $this->argument.
   */
  public function query($group_by = FALSE) {
    if ($this->options['fields']) {
      $this->query->fields($this->options['fields']);
    }

    $old = $this->query->getOriginalKeys();
    $this->query->keys($this->argument);
    if ($old) {
      $keys = &$this->query->getKeys();
      if (is_array($keys)) {
        $keys[] = $old;
      }
      elseif (is_array($old)) {
        // We don't support such nonsense.
      }
      else {
        $keys = "($old) ($keys)";
      }
    }
  }

}
