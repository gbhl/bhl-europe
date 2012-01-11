<?php
// ********************************************
// ** FILE:    LAY_HEADER.PHP                **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************



?>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" >
<head runat="server" >
<title><?php echo _APP_NAME." | v."._APP_VERSION; ?></title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="css/reset-min.css" type="text/css" />
<link rel="stylesheet" href="css/portal.css"    type="text/css" media="all" charset="utf-8" />
<link rel="stylesheet" href="css/ingest.css"    type="text/css" media="all" charset="utf-8" />
<?php

// NUR BEIM PORTAL MENU MATIC
if ($menu_nav=="portal")
echo "<link rel=\"stylesheet\" href=\"css/MenuMatic.css\" type=\"text/css\" media=\"screen\" charset=\"utf-8\" />\n";

// BEIM PORTAL UND DIV. ANDEREN SEITEN UPPER MENU
if (in_array($menu_nav,array("portal","ingest_list","admin"))) 
echo "<link rel=\"stylesheet\" href=\"css/uppermenu.css\" type=\"text/css\" media=\"screen\" charset=\"utf-8\" />\n";

if ($menu_nav=="portal") {
?>
<!--[if lt IE 7]>
	<link rel="stylesheet" href="css/MenuMatic-ie6.css" type="text/css" media="screen" charset="utf-8" />
<![endif]-->

<?php } ?>

<script type="text/javascript" src="<?php echo _SHARED_URL; ?>js/tools.js"></script>
</head>
<body>

<?php

// UPPER MENU

if ($menu_nav=='ingest_list')
{
?>

<ul id="navigation">
        <li class="home"><a href="#" onClick="switch_visibility(document.getElementById('pd_div'));"><span>my Account</span></a></li>
        <li class="photos"><a href="#"  onCLick="popup_win('help', '<?php echo _SYSTEM; ?>?menu_nav=help', 800,600);"><span>Help</span></a></li>
        <li class="rssfeed"><a href="#" onCLick="popup_win('about','<?php echo _SYSTEM; ?>?menu_nav=about',420,540);"><span>About</span></a></li>        
	<li class="contact"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Contact</span></a></li>
	<li class="about"><a href="#" onClick="nachfrage('Terminate current ingest session?','<?php echo _SYSTEM."?menu_nav=logout"; ?>');"><span>Logout</span></a></li>        
</ul>

<?php    

}

if ($menu_nav=='portal')
{
?>

<ul id="navigation">
    <li class="home"><a href="index.php"><span>Home/Login</span></a></li>
    <li class="search"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Registration</span></a></li>
    <li class="photos"><a href="#"  onCLick="popup_win('help', '<?php echo _SYSTEM; ?>?menu_nav=help', 800,600);"><span>Help</span></a></li>
    <li class="rssfeed"><a href="#" onCLick="popup_win('about','<?php echo _SYSTEM; ?>?menu_nav=about',420,540);"><span>About</span></a></li>        
    <li class="contact"><a href="mailto:<?php echo _CONTACT_EMAIL; ?>"><span>Contact</span></a></li>
</ul>

<?php

}

?>