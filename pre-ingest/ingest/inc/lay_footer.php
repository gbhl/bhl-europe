<?php
// ********************************************
// ** FILE:    LAY_FOOTER.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



if (($menu_nav=='portal')||($menu_nav=='ingest_list'))
{
?>

<script type="text/javascript" src="js/jquery-1.6.4.js"></script>

<script type="text/javascript">

// MODIFICATIONS BY MEHRRATH A.

// TO RUN TOGETHER WITH MOOTOOLS !
jQuery.noConflict();

// $(function() {
(function($) 
{
	var d=300;
	$('#navigation a').each(function(){
		$(this).stop().animate({
			'marginTop':'-80px'
		},d+=150);
	});

	$('#navigation > li').hover(
	function () {
		$('a',$(this)).stop().animate({
			'marginTop':'-2px'
		},200);
	},
	function () {
		$('a',$(this)).stop().animate({
			'marginTop':'-80px'
		},200);
	}
);
})(jQuery);  //  abschluss
</script>

<?php
}


if ((isset($endmsg))&&($endmsg!="")) js_alert($endmsg);

?>

</BODY>
</HTML>
