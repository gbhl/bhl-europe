<?php
/**
 * CKEditor - The text editor for the Internet - http://ckeditor.com
 * Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
 *
 * == BEGIN LICENSE ==
 *
 * Licensed under the terms of any of the following licenses at your
 * choice:
 *
 *  - GNU General Public License Version 2 or later (the "GPL")
 *    http://www.gnu.org/licenses/gpl.html
 *
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 *
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 *
 * == END LICENSE ==
 *
 * @file
 * CKEditor Module for Drupal 7.x
 *
 * This module allows Drupal to replace textarea fields with CKEditor.
 *
 * This HTML text editor brings to the web many of the powerful functionalities
 * of known desktop editors like Word. It's really  lightweight and doesn't
 * require any kind of installation on the client computer.
 */

/**
 * Hook to register CKEditor plugin - it would appear in plugins list on profile setting page
 */
function hook_ckeditor_plugin() {
    return array(
        'plugin_name' => array(
            // Name of the plugin used to write it
            'name' => 'plugin_name',
            // Description of plugin - it would be displayed in plugins managment of profile settings
            'desc' => t('Description of plugin'),
            // The full path to the CKEditor plugin directory, with trailing slash.
            'path' => drupal_get_path('module', 'my_module') . '/plugin_dir/',
        )
    );
}

?>
