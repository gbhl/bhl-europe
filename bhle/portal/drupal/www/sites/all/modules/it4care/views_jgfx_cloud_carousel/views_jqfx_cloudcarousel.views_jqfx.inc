<?php

/**
 * @file
 * The UI and menu includes.
 *
 * Provides the default options array and generates and
 * validates the settings form.
 */

/**
 * Implements hook_views_jqfx_jqfx_types().
 */
function views_jqfx_cloudcarousel_views_jqfx_jqfx_types() {
  $options = array(
    'views_jqfx_cloudcarousel' => t('Cloud Carousel'),
  );
  return $options;
}

/**
 * Implements hook_views_jqfx_option_definition().
 */
function views_jqfx_cloudcarousel_views_jqfx_option_definition() {
  // Set our default options.
  $options['views_jqfx_cloudcarousel'] = array(
    'contains' => array(
      'style'              => array('default' => 'basic'),
      'auto_rotate'        => array('default' => 'no'),
      'auto_rotate_delay'  => array('default' => '5000'),
      'bring_to_front'     => array('default' => TRUE),
      'fps'                => array('default' => '30'),
      'min_scale'          => array('default' => '0.5'),
      'mouse_wheel'        => array('default' => TRUE),
      'open_colorbox'      => array('default' => FALSE),
      'refl_height'        => array('default' => '56'),
      'refl_gap'           => array('default' => '2'),
      'refl_opacity'       => array('default' => '0.5'),
      'speed'              => array('default' => '0.15'),
      'x_pos'              => array('default' => '300'),
      'y_pos'              => array('default' => '150'),
      'x_radius'           => array('default' => '265'),
      'y_radius'           => array('default' => '35'),
    ),
  );
  return $options;
}

/**
 * Implements hook_views_jqfx_views_jqfx_type_form().
 */
function views_jqfx_cloudcarousel_views_jqfx_jqfx_type_form(&$form, &$form_state, &$view) {
  ctools_include('dependent');
  // Alerts the user if the library is not installed
  if (!file_exists('sites/all/libraries/cloud-carousel/cloud-carousel.js')) {
    $form['views_jqfx_cloudcarousel']['no_cloudcarousel_js'] = array(
      '#markup' => '<div style="color: red">' . t('You need to download the 3d Cloud Carousel plugin and copy it to sites/all/libraries/cloud-carousel. You can find the plugin at !url.', array('!url' => l(t('http://www.professorcloud.com/mainsite/carousel.htm', array(), array('langcode' => 'en')), 'http://www.professorcloud.com/mainsite/carousel.htm', array('attributes' => array('target' => '_blank')))), array('langcode' => 'en')) . '</div>',
    );
  }
  $form['views_jqfx_cloudcarousel']['style'] = array(
    '#type' => 'select',
    '#title' => t('Style'),
    '#description' => t("Select a theme option. For a custom theme it is probably easiest to chose 'basic' and override these settings using your own css"),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['style'],
    '#options' => array(
      'basic' => t('Basic'),
      'sail' => t('Sail'),
    ),
  );
  $form['views_jqfx_cloudcarousel']['auto_rotate'] = array(
    '#type' => 'select',
    '#title' => t('Auto rotation'),
    '#description' => t("Turn on auto-rotation of the carousel using either 'left' or 'right' as the value. The carousel will rotate between items automatically. The auto-rotation stops when the user hovers over the carousel area, and starts again when the mouse is moved off."),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['auto_rotate'],
    '#options' => array(
      'no' => t('Disabled'),
      'left' => t('Left'),
      'right' => t('Right'),
    ),
  );
  $form['views_jqfx_cloudcarousel']['auto_rotate_delay'] = array(
    '#type' => 'textfield',
    '#title' => t('Auto rotate delay'),
    '#description' => t('Delay in milliseconds between each rotation in auto-rotate mode. A minimum value of 1000 (i.e. one second) is recommended.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['auto_rotate_delay'],
    '#process' => array('ctools_dependent_process'),
    '#dependency' => array('edit-style-options-views-jqfx-cloudcarousel-auto-rotate' => array('yes')),
  );
  $form['views_jqfx_cloudcarousel']['bring_to_front'] = array(
    '#type' => 'select',
    '#title' => t('Bring to front'),
    '#description' => t('If true, moves the item clicked on to the front.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['bring_to_front'],
    '#options' => array(
      FALSE => t('Disabled'),
      TRUE => t('Enabled'),
    ),
  );
  $form['views_jqfx_cloudcarousel']['fps'] = array(
    '#type' => 'textfield',
    '#title' => t('FPS'),
    '#description' => t("This is the approximate frame rate of the carousel in frames per second. The higher the number, the faster and smoother the carousel movement will appear. However, frame rates that are too high can make the user's browser feel sluggish, especially if they have an under powered machine. The default setting of 30 is a good tradeoff between speed and performance."),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['fps'],
  );
  $form['views_jqfx_cloudcarousel']['min_scale'] = array(
    '#type' => 'textfield',
    '#title' => t('Image scale'),
    '#description' => t('The minimum scale appled to the furthest item. The item at the front has a scale of 1. To make items in the distance one quarter of the size, minScale would be 0.25.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['min_scale'],
  );
  $form['views_jqfx_cloudcarousel']['mouse_wheel'] = array(
    '#type' => 'select',
    '#title' => t('Mouse wheel'),
    '#description' => t('If set to true, this will enable mouse wheel support for the carousel. You will need to include this mouse wheel plugin first: !url.', array('!url' => l(t('http://plugins.jquery.com/project/mousewheel', array(), array('langcode' => 'en')), 'http://plugins.jquery.com/project/mousewheel', array('attributes' => array('target' => '_blank')))), array('langcode' => 'en')),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['mouse_wheel'],
    '#options' => array(
      FALSE => t('Disabled'),
      TRUE => t('Enabled'),
    ),
  );
  if (!file_exists('sites/all/libraries/cloud-carousel/cloud-carousel.min.js')) {
    $form['views_jqfx_cloudcarousel']['no_mousewheel_js'] = array(
      '#markup' => '<div style="color: red">' . t('You need to download the Mouse Wheel plugin and copy it to sites/all/libraries/jquery.mousewheel. You can find the plugin at !url.', array('!url' => l(t('http://plugins.jquery.com/project/mousewheel', array(), array('langcode' => 'en')), 'http://plugins.jquery.com/project/mousewheel', array('attributes' => array('target' => '_blank')))), array('langcode' => 'en')) . '</div>',
      '#process' => array('ctools_dependent_process'),
      '#dependency' => array('edit-style-options-views-jqfx-cloudcarousel-mouse-wheel' => array(TRUE)),
    );
  }
  $form['views_jqfx_cloudcarousel']['open_colorbox'] = array(
    '#type' => 'select',
    '#title' => t('Colorbox'),
    '#description' => t('If enabled, this will open image links in a Colorbox Lightbox. You must have the colorbox module installed to use this option: !url.', array('!url' => l(t('Project colorbox', array(), array('langcode' => 'en')), 'http://drupal.org/project/colorbox', array('attributes' => array('target' => '_blank')))), array('langcode' => 'en')),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['open_colorbox'],
    '#options' => array(
      FALSE => t('Disabled'),
      TRUE => t('Enabled'),
    ),
  );
  if (!module_exists('colorbox')) {
    $form['views_jqfx_cloudcarousel']['no_colorbox_module'] = array(
      '#markup' => '<div style="color: red">' . t('You need to install the colorbox module to use this option.') . '</div>',
      '#process' => array('ctools_dependent_process'),
      '#dependency' => array('edit-style-options-views-jqfx-cloudcarousel-open-colorbox' => array(TRUE)),
    );
  }
  $form['views_jqfx_cloudcarousel']['refl_height'] = array(
    '#type' => 'textfield',
    '#title' => t('Reflection height'),
    '#description' => t('Height of the auto-reflection in pixels, assuming applied to the item at the front. The reflection will scale automatically. A value of 0 means that no auto-reflection will appear.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['refl_height'],
  );
  $form['views_jqfx_cloudcarousel']['refl_gap'] = array(
    '#type' => 'textfield',
    '#title' => t('Reflection gap'),
    '#description' => t('Amount of vertical space in pixels between image and reflection, assuming applied to the item at the front. Gap will scale automatically.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['refl_gap'],
  );
  $form['views_jqfx_cloudcarousel']['refl_opacity'] = array(
    '#type' => 'textfield',
    '#title' => t('Reflection opacity'),
    '#description' => t('Specifies how transparent the reflection is. 0 is invisible, 1 is totally opaque.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['refl_opacity'],
  );
  $form['views_jqfx_cloudcarousel']['speed'] = array(
    '#type' => 'textfield',
    '#title' => t('Speed'),
    '#description' => t('This value represents the speed at which the carousel rotates between items. Good values are around 0.1 ~ 0.3. A value of one will instantly move from one item to the next without any rotation animation. Values should be greater than zero and less than one.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['speed'],
  );
  $form['views_jqfx_cloudcarousel']['x_pos'] = array(
    '#type' => 'textfield',
    '#title' => t('X position'),
    '#description' => t('Horizontal position of the circle centre relative to the container. You would normally set this to half the width of the container.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['x_pos'],
  );
  $form['views_jqfx_cloudcarousel']['y_pos'] = array(
    '#type' => 'textfield',
    '#title' => t('Y position'),
    '#description' => t('Vertical position of the circle centre relative to the container. You would normally set this to around half the height of container.'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['y_pos'],
  );
  $form['views_jqfx_cloudcarousel']['x_radius'] = array(
    '#type' => 'textfield',
    '#title' => t('X radius'),
    '#description' => t('Half-width of the circle that items travel around. Defaults to "Width of container / 2.3."'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['x_radius'],
  );
  $form['views_jqfx_cloudcarousel']['y_radius'] = array(
    '#type' => 'textfield',
    '#title' => t('Y radius'),
    '#description' => t('Half-height of the circle that items travel around. By playing around with this value, you can alter the amount of "tilt". Defaults to "Height of container / 6."'),
    '#default_value' => $view->options['views_jqfx_cloudcarousel']['y_radius'],
  );
}
