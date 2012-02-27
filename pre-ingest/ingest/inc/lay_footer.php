<?php
// ********************************************
// ** FILE:    LAY_FOOTER.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



if ($menu_nav=='file_tree')
{
    include_once("file_tree.php");
}
else if (($menu_nav=='portal')||($menu_nav=='ingest_list'))
{
?>

<script type="text/javascript" src="js/jquery/jquery-1.6.4.js"></script>

<script type="text/javascript" src="js/jquery/ui/jquery.ui.core.js"></script>
 
<script type="text/javascript" src="js/jquery/ui/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.position.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.autocomplete.js"></script>
 
<script type="text/javascript" src="js/jquery/ui/jquery.ui.mouse.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.button.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.draggable.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.resizable.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.dialog.js"></script>
<script type="text/javascript" src="js/jquery/ui/jquery.ui.effects.core.js"></script>

<script type="text/javascript" src="js/jquery.tools.tooltips.js"></script>


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


<?php

if ($menu_nav=='ingest_list')
{
    $a1 = "popup_win('au','"._SYSTEM."?menu_nav=upload_analyze&analyzeDir=".urlencode(_USER_CONTENT_ROOT)."',1000,500);";
    $a2 = "document.location.href='"._SYSTEM."?menu_nav=ingest_list';";
    
    $a3 = "popup_win('mu','"._SYSTEM."?menu_nav=show_user_dir',1000,500);";
    $a4 = "popup_win('il','"._SYSTEM."?menu_nav=ingest_log',1000,500);";

    $a5 = "document.forms.form_ingest_manager.submit();";
?>


$( "button1", ".demo" ).button( {icons: { primary: "ui-icon-arrowrefresh-1-w" }});
$( "button2", ".demo" ).button( {icons: { primary: "ui-icon-arrowrefresh-1-w" }});
$( "button3", ".demo" ).button( {icons: { primary: "ui-icon-zoomin" }});
$( "button4", ".demo" ).button( {icons: { primary: "ui-icon-zoomin" }});
$( "button5", ".demo" ).button( {icons: { primary: "ui-icon-check" }});

$( "button1", ".demo" ).click(function() { <?php echo $a1; ?> });
$( "button2", ".demo" ).click(function() { <?php echo $a2; ?>  });
$( "button3", ".demo" ).click(function() { <?php echo $a3; ?>  });
$( "button4", ".demo" ).click(function() { <?php echo $a4; ?>  });
$( "button5", ".demo" ).click(function() { <?php echo $a5; ?>  });


// ********************
// ***** TOOLTIPS *****
// ********************
$("#mytable img[title]").tooltip({

// each trashcan image works as a trigger
tip: '#tooltip',

// custom positioning
position: 'bottom center',

// move tooltip a little bit to the right
offset: [10, 0],

// there is no delay when the mouse is moved away from the trigger
delay: 0
});



//  $(document).ready(function() {

$("#ar a[title]").tooltip({

position: 'bottom center',

// tweak the position
offset: [10, -30],

// use the "slide" effect
effect: 'slide',  opacity: 1

// add dynamic plugin with optional configuration for bottom edge
}).dynamic({ bottom: { direction: 'down', bounce: true } });

// });



<?php

}

?>

})(jQuery);  //  abschluss

</script>

<?php

if ($menu_nav=='portal') $endJS = " 
    document.forms.frm_login.user_name.focus();
";

}



if (!isset($noheader))
{
    // JS_ALERT
    if ((isset($endmsg))&&($endmsg!="")) js_alert($endmsg);

    // JS CODE
    if ((isset($endJS))&&($endJS!="")) js_command("\n".$endJS."\n");
    
?>

</BODY>
</HTML>

<?php

}

?>