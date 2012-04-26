/**
Copyright (c) 2011, Museum of Natural History Vienna
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Museum of Natural History Vienna nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Museum of Natural History Vienna BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

var bhle_search = {

    exact_phrase: function( p_exact ) {
        // WARNING: attr is pre jQuery 1.6 - if there is ever an upgrade we have to switch to "prop"
        jQuery( 'input[name^="exact_phrase_"]' ).attr( 'checked', p_exact.attr('checked') );
    },

    or_not_operators: function () {
        // WARNING: live is pre jQuery 1.6 - if there is ever an upgrade we have to switch to "jQuery(document).on(…"
        jQuery('.operator-or, .operator-not').live('click', function(){
          var $input        = jQuery('.form-item input.form-text', jQuery(this).parent());

          // If the field is filled with placeholder text, empty it first
          if ( $input.val() == jQuery('.fieldset-wrapper label:first').text())
            $input.val('');

          // Select operator by class
          var operator      = '';
          if ( jQuery(this).attr('class').indexOf('operator-or') >= 0 )
            operator = ' OR ';
          if ( jQuery(this).attr('class').indexOf('operator-not') >= 0 )
            operator = ' NOT ';

          // Do operator insertion
          var insertPos = ( $input.cursorPosition() == 0 ) ? $input.val().length : $input.cursorPosition() ;
          $input.val( $input.val().substring(0, insertPos) + operator + $input.val().substring(insertPos) );
          $input.focus();

          return false;
        });
    },

    save_query_dialog: function () {
    	jQuery('#edit-save--2').click(function(event){
    			jQuery(this).colorbox(
    					{
    						html: '<h3>Please enter a name for the query to be stored:</h3>'
    							+ '<form><input id="save-query-dialog-name" name="name" type="text" size="30" maxlength="30" />'
    							+ '<br /><input id="save-query-dialog-ok" type="button" value="OK"></form>',
    							onComplete: function(){
    								// Callback that fires right after loaded content is displayed.
    								jQuery('#save-query-dialog-ok').click(function(event){
    									// set the chosen name
    									name = jQuery('#save-query-dialog-name').val();
    									jQuery('input[name="save_query_name"]').val(name);

    									// simulate submit button:
    									// <input id="edit-save" class="form-submit" type="submit" value="Save Query" name="op" tabindex="9">
    									localizedValue = jQuery('#edit-save--2').val();
    									jQuery('#edit-save--2').after('<input type="hidden" name="op" value="' + localizedValue +'"/>');
    									jQuery('#search-form--2').submit();
    								})
    							}
    					}
    			); // [END colorbox]
    	}) // [END event handler]
    }
}

jQuery(document).ready( bhle_search.or_not_operators );
jQuery(document).ready( bhle_search.save_query_dialog );


// jQuery method for getting input cursor position
new function($) {
    $.fn.cursorPosition = function() {
        var pos = 0;
        var input = $(this).get(0);
        // IE Support
        if (document.selection) {
            input.focus();
            var sel = document.selection.createRange();
            var selLen = document.selection.createRange().text.length;
            sel.moveStart('character', -input.value.length);
            pos = sel.text.length - selLen;
        }
        // Firefox support
        else if (input.selectionStart || input.selectionStart == '0')
            pos = input.selectionStart;

        return pos;
    }
} (jQuery);