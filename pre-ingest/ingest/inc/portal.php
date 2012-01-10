<?php
// ********************************************
// ** FILE:    PORTAL.PHP                    **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

?>

<ul id="navigation">

<li class="home"><a href="index.php"><span>Home/Login</span></a></li>

<li class="search"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Registration</span></a></li>
       
<li class="photos"><a href="#"  onCLick="popup_win('help', '<?php echo _SYSTEM; ?>?menu_nav=help', 800,600);"><span>Help</span></a></li>

<li class="rssfeed"><a href="#" onCLick="popup_win('about','<?php echo _SYSTEM; ?>?menu_nav=about',400,500);"><span>About</span></a></li>        
        
<li class="contact"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Contact</span></a></li>

</ul>





<div id="container" >
	
	<a id="logo" href="#">BHL - Europe Central Ingestion Portal</a>
	<br>
	
	<!-- BEGIN Menu -->
	<ul id="nav">
		<li><a href="#">News</a>
			<ul>
				<li><a href="#">No news provided at the moment.</a>
				</li>
			</ul>
		</li>
	
		<li><a href="#">Statistics</a>
			<ul>
				<li><a href="#">Content</a>
					<ul>
						<li><a href="#">...</a></li>
					</ul>
				</li>
				<li><a href="#">Server</a>
					<ul>
						<li><a href="#">...</a></li>
					</ul>
				</li>

				<li><a href="#">Database</a>
					<ul>
						<li><a href="#">...</a></li>
					</ul>
				</li>

				<li><a href="#">Provider</a>
					<ul>
						<li><a href="#">...</a></li>
					</ul>
				</li>
	
			</ul>
		</li>
	
		<li><a href="#">Content Provider</a>
			<ul>
				<li><a href="#">Delivery Format Request</a></li>
				<li><a href="#">XML/File Format Inquiry</a></li>
			</ul>
		</li>

		<li><a href="#"><font color=#888888>Administration</font></a>
			<ul>
				<li><a target='_blank' href="#">Master Login</a></li>
				<li><a target='_blank' href="https://bhl.wikispaces.com/">BHL WIKI</a></li>
				<li><a target='_blank' href="http://groups.google.com/group/bhle-tech?hl=en-GB">BHLE Google Group</a></li>
				<li><a target='_blank' href="https://github.com/bhle">GitHub</a></li>
			</ul>
		</li>

	
	</ul>

	<!-- END Menu -->
	<div id="content">	    	 
		<h1>Online Services</h1>
		<p>Welcome to the Central Content Ingestion Portal for Upload Management and transparent Ingestion.</p>

		<br>
		<h1>Member Login</h2>
                <form method="post" action="index.php" style="margin: 0px 0px 0px 0px;">
                <input type="hidden"   name="menu_nav"  value="ingest_list">
		<input type="text"     name="user_name" size=30><br>
		<input type="password" name="user_pwd"  size=30 style="margin-top: 2px;"><br>
		<input type="image"    name="submit"    src='img/button_login_green.png' style='margin-top: 2px;'>
                </form>
	</div>
		  
</div>

<div id=logodiv>

<a href='http://www.bhl-europe.eu/' target='_blank'><img src="img/logo.png" width=277 height=92 border=0></a>

</div>



<script type="text/javascript" src="js/mootools-core-1.4.1-full-compat.js"></script>

<!-- Load the MenuMatic Class -->
<script src="js/MenuMatic_0.68.3.js" type="text/javascript" charset="utf-8"></script>

<!-- Create a MenuMatic Instance -->
<script type="text/javascript" >
	window.addEvent('domready', function() {			
		var myMenu = new MenuMatic({ orientation:'vertical' });			
	});

/* ********************** */

</script>


	


<script type="text/javascript" src="js/jquery-1.6.4.js"></script>

<script type="text/javascript">


// modifications by mehrrath a.


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

// ------------------------------------------

?>