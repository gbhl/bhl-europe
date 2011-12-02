/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

/**
 * @file Plugin for inserting images from Drupal media module
 */
( function() {
    CKEDITOR.plugins.add( 'media',
    {
        // Wrap Drupal plugin in a proxy plugin.
        init: function(editor)
        {
            var pluginCommand = {
                exec: function (editor) {
                    var data = {
                        format: 'html',
                        node: null,
                        content: ''
                    };
                    var selection = editor.getSelection();

                    if (selection) {
                        data.node = selection.getSelectedElement();
                        if (data.node) {
                            data.node = data.node.$;
                        }
                        if (selection.getType() == CKEDITOR.SELECTION_TEXT) {
                            if (CKEDITOR.env.ie) {
                                data.content = selection.getNative().createRange().text;
                            }
                            else {
                                data.content = selection.getNative().toString();
                            }
                        }
                        else if (data.node) {
                            // content is supposed to contain the "outerHTML".
                            data.content = data.node.parentNode.innerHTML;
                        }
                    }
                    Drupal.settings.ckeditor.plugins['media'].invoke(data, Drupal.settings.ckeditor.plugins['media'], editor.name);
                }
            };
            editor.addCommand( 'media', pluginCommand );

            editor.ui.addButton( 'Media',
            {
                label: 'Add media',
                command: 'media',
                icon: this.path + 'images/icon.gif'
            });
        }
    });

} )();


