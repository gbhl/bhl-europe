<?php

/**
 * @file
 * Theme & preprocess functions for the Views jQFX: Cloud Carousel module.
 */

function template_preprocess_views_jqfx_cloudcarousel(&$vars) {
  // Initialize our $images array.
  $vars['images'] = array();

  // Strip the images from the $rows created by the original view query.
  foreach ($vars['rows'] as $item) {
    // For our images we'll look for items enclosed in anchor tags first.
    if (preg_match('@(<a.*?img.*?</a>)@i', $item, $matches)) {
      $image = $matches[1];
    }
    // If no anchor tags we'll just look for images.
    elseif (preg_match('@(<\s*img\s+[^>]*>)@i', $item, $matches)) {
      $image = $matches[1];
    }
    // If we have no images then we have no Carousel.
    else {$images = NULL;}
    // Add the image to our image array to display.
    $vars['images'][] = $image;
  }

  $options = $vars['options']['views_jqfx_cloudcarousel'];

  // Add the module css
  drupal_add_css((drupal_get_path('module', 'views_jqfx_cloudcarousel')) . '/theme/styles/' . $options['style'] . '/views_jqfx_cloudcarousel_' . $options['style'] . '.css');

  _views_jqfx_cloudcarousel_add_js($options, 'views-jqfx-cloudcarousel-images-' . $vars['id']);

}

function _views_jqfx_cloudcarousel_add_js($options, $id) {

  // Find the path to our plugin.
  $path = 'sites/all/libraries';

  // Add the plugin JS.
  drupal_add_js($path . '/cloud-carousel/cloud-carousel.js');

  // If the mousewheel option is true add its javascript
  if ($options['mouse_wheel']) {
    drupal_add_js($path . '/jquery.mousewheel/jquery.mousewheel.js');
  }

  // Get the javascript settings.
  $settings = array(
    'autoRotate'       => $options['auto_rotate'],
    'autoRotateDelay'  => (int) $options['auto_rotate_delay'],
    'bringToFront'     => (bool) $options['bring_to_front'],
    'FPS'              => (int) $options['fps'],
    'minScale'         => (float) $options['min_scale'],
    'mouseWheel'       => (bool) $options['mouse_wheel'],
    'openColorbox'     => (bool) $options['open_colorbox'],
    'reflHeight'       => (int) $options['refl_height'],
    'reflGap'          => (int) $options['refl_gap'],
    'reflOpacity'      => (float) $options['refl_opacity'],
    'speed'            => (float) $options['speed'],
    'xPos'             => (int) $options['x_pos'],
    'yPos'             => (int) $options['y_pos'],
    'xRadius'          => (int) $options['x_radius'],
    'yRadius'          => (int) $options['y_radius'],
  );

  // Add the settings array.
  drupal_add_js(array('viewsJqfxCloudcarousel' => array($id => $settings)), 'setting');

  // Add the module javascript
  $drupal_path = drupal_get_path('module', 'views_jqfx_cloudcarousel');
  drupal_add_js($drupal_path . '/js/views_jqfx_cloudcarousel.js');
}
