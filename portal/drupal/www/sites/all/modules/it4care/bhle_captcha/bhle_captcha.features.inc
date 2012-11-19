<?php
/**
 * @file
 * bhle_captcha.features.inc
 */

/**
 * Implements hook_ctools_plugin_api().
 */
function bhle_captcha_ctools_plugin_api() {
  list($module, $api) = func_get_args();
  if ($module == "captcha" && $api == "captcha") {
    return array("version" => "1");
  }
  list($module, $api) = func_get_args();
  if ($module == "strongarm" && $api == "strongarm") {
    return array("version" => "1");
  }
}
