<?php

/**
 * Views argument handler class for handling all non-fulltext types.
 */
class SearchApiViewsHandlerArgument extends views_handler_argument {

  /**
   * The associated views query object.
   *
   * @var SearchApiViewsQuery
   */
  public $query;

  /**
   * Determine if the argument can generate a breadcrumb
   *
   * @return boolean
   */
  // @todo Change and implement set_breadcrumb()?
  public function uses_breadcrumb() {
    return FALSE;
  }

  /**
   * Provide a list of default behaviors for this argument if the argument
   * is not present.
   *
   * Override this method to provide additional (or fewer) default behaviors.
   */
  public function default_actions($which = NULL) {
    $defaults = array(
      'ignore' => array(
        'title' => t('Display all values'),
        'method' => 'default_ignore',
        'breadcrumb' => TRUE, // generate a breadcrumb to here
      ),
      'not found' => array(
        'title' => t('Hide view / Page not found (404)'),
        'method' => 'default_not_found',
        'hard fail' => TRUE, // This is a hard fail condition
      ),
      'empty' => array(
        'title' => t('Display empty text'),
        'method' => 'default_empty',
        'breadcrumb' => TRUE, // generate a breadcrumb to here
      ),
      'default' => array(
        'title' => t('Provide default argument'),
        'method' => 'default_default',
        'form method' => 'default_argument_form',
        'has default argument' => TRUE,
        'default only' => TRUE, // this can only be used for missing argument, not validation failure
      ),
    );

    if ($which) {
      if (!empty($defaults[$which])) {
        return $defaults[$which];
      }
    }
    else {
      return $defaults;
    }
  }

  /**
   * Set up the query for this argument.
   *
   * The argument sent may be found at $this->argument.
   */
  // @todo Provide options to select the operator, instead of always using '='?
  public function query($group_by = FALSE) {
    $this->query->condition($this->real_field, $this->argument);
  }

  /**
   * Get the title this argument will assign the view, given the argument.
   *
   * This usually needs to be overridden to provide a proper title.
   */
  public function title() {
    return t('Search @field for "@arg"', array('@field' => $this->definition['title'], '@arg' => $this->argument));
  }

}
