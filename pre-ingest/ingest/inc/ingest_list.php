<?php
// ********************************************
// ** FILE:    INGEST_LIST.PHP               **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// CONTENT PROVIDER'S VIEW TO NOT INGESTED CONTENT

include_once(_SHARED."formlib.php");
include_once(_SHARED."imagelib.php");

?>

<ul id="navigation">
        <li class="home"><a href="#" onClick="switch_visibility(document.getElementById('pd_div'));"><span>my Account</span></a></li>
       
        <li class="photos"><a href="#"  onCLick="popup_win('help', '<?php echo _SYSTEM; ?>?menu_nav=help', 800,600);"><span>Help</span></a></li>

        <li class="rssfeed"><a href="#" onCLick="popup_win('about','<?php echo _SYSTEM; ?>?menu_nav=about',420,540);"><span>About</span></a></li>        
        
	<li class="contact"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Contact</span></a></li>

	<li class="about"><a href="#" onClick="nachfrage('Terminate current ingest session?','<?php echo _SYSTEM."?menu_nav=logout"; ?>');"><span>Logout</span></a></li>        
</ul>

<?php



// ADMIN ACCOUNT
include("provider_details.php");


// ADMIN MY FILES
include("content_list.php");



// UPPER MENU

?>

<script type="text/javascript" src="js/jquery-1.6.4.js"></script>

<script type="text/javascript">


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


<div id=logodiv>

<a href='http://www.bhl-europe.eu/' target='_blank'><img src="img/logo.png" width=277 height=92 border=0></a>

</div>
