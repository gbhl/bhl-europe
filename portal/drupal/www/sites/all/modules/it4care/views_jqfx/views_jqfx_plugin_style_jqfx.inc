<?php
/**
 * @file
 * Contains the list style plugin.
 */

/**
 * Style plugin to render each item of an ordered or unordered list.
 *
 * @ingroup views_style_plugins
 */
class views_jqfx_plugin_style_jqfx extends views_plugin_style_list {
  // Set default options
  function option_definition() {
    $options = parent::option_definition();
    // Load all include files from views jqfx addons.
    module_load_all_includes('views_jqfx.inc');
    // call every module using hook_views_jqfx_option_definition and merge
    // it's return value with the other default options.
    return array_merge($options, module_invoke_all('views_jqfx_option_definition'));
  }
  // Build the settings form for the view.
  function options_form(&$form, &$form_state) {
    // load up all views jqfx modules functions.
    module_load_all_includes('views_jqfx.inc');
    parent::options_form($form, $form_state);
    /**
     * Slides
     */
    $form['slides_header'] = array(
      '#markup' => '<h2>' . t('jQFX Settings') . '</h2>',
    );
    // Get all jqfx types.
    $modules = module_invoke_all('views_jqfx_jqfx_types');
    if ($modules) {
      $form['jqfx_type'] = array(
        '#type' => 'select',
        '#title' => t('FX Type'),
        '#options' => $modules,
        '#default_value' => $this->options['jqfx_type'],
      );
      $arguments = array(
        &$form,
        &$form_state,
        &$this,
      );
      foreach (module_implements('views_jqfx_jqfx_type_form') as $module) {
        ctools_include('dependent');
        $form[$module] = array(
          '#type' => 'fieldset',
          '#title' => check_plain(t('!module options', array('!module' => $modules[$module]))),
          '#collapsible' => TRUE,
          '#attributes' => array('class' => array($module)),
          '#process' => array('ctools_dependent_process'),
          '#dependency' => array(':input[name="style_options[jqfx_type]"]' => array($module)),
        );
        $function = $module . '_views_jqfx_jqfx_type_form';
        call_user_func_array($function, $arguments);
      }
    }
    else {
      $form['enable_module'] = array(
        '#markup' => t('There is no Views jQFX plugin enabled. Go to the !modules and enable a Views jQFX plugin module. For example Views jQFX ImageFlow.', array('!modules' => l(t('Modules Page', array(), array('langcode' => 'de')), 'admin/build/modules')), array('langcode' => 'en')),
      );
    }
  }
  function options_validate(&$form, &$form_state) {
    module_load_all_includes('views_jqfx.inc');
    foreach (module_implements('views_jqfx_options_form_validate') as $module) {
      $function = $module . '_views_jqfx_options_form_validate';
      call_user_func_array($function, array(&$form, &$form_state, &$this));
    }
  }
  function options_submit(&$form, &$form_state) {
    module_load_all_includes('views_jqfx.inc');
    foreach (module_implements('views_jqfx_options_form_submit') as $module) {
      $function = $module . '_views_jqfx_options_form_submit';
      call_user_func_array($function, array($form, &$form_state));
    }
  }
}
