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
<link rel="stylesheet" href="css/portal.css"    type="text/css" media="all"    charset="utf-8" />
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
