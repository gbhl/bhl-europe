// $Id: toolbar_hide.js,v 1.1.2.4 2010/11/13 21:30:07 joshthegeek Exp $

(function($){
if (Drupal.settings.toolbar_hide.default) {
  $(document).ready(function(){$('#toolbar').hide();$('body').addClass('tbr_menu_hidden');});
}
$(document).keypress(function(e) {
	var unicode=e.keyCode? e.keyCode : e.charCode;
	if (String.fromCharCode(unicode)==Drupal.settings.toolbar_hide.key){
	  $('#toolbar').slideToggle('fast');
	  $('body').toggleClass('tbr_menu_hidden');
	}
});
})(jQuery);
