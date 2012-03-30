<?php
// ********************************************
// ** FILE:    FILE_TREE.PHP                 **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.01.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
/*
 * http://forum.jquery.com/topic/jquery-file-tree-how-to-add-function-when-file-is-clicked
 * http://www.abeautifulsite.net/blog/2008/03/jquery-file-tree/#demo
 * 
Parameter 	Description 	Default Value
root 	root folder to display 	/
script 	location of the serverside AJAX file to use 	jqueryFileTree.php
folderEvent 	event to trigger expand/collapse 	click
expandSpeed 	Speed at which to expand branches (in milliseconds); use -1 for no animation 	500
collapseSpeed 	Speed at which to collapse branches (in milliseconds); use -1 for no animation 	500
expandEasing 	Easing function to use on expand 	None
collapseEasing 	Easing function to use on collapse 	None
multiFolder 	Whether or not to limit the browser to one subfolder at a time 	true
loadMessage 	Message to display while initial tree loads (can be HTML) 	Loading…
 * 
 */

$showTime = true;

if (  (isset($root)) && (isset($start_root)) && (instr($root,_CONTENT_ROOT,true,true)) )
{

// AJAX REQUERST LIEFERT VAR DIR MIT

if (array_key_exists('dir', $_POST))
{
    $_POST['dir'] = urldecode($_POST['dir']);

    if( file_exists($root . $_POST['dir']) ) {
            $files = scandir($root . $_POST['dir']);
            natcasesort($files);
            if( count($files) > 2 ) { /* The 2 accounts for . and .. */
                    echo "<ul class=\"jqueryFileTree\" style=\"display: none;\">";
                    // All dirs
                    foreach( $files as $file ) {
                            if( file_exists($root . $_POST['dir'] . $file) && $file != '.' && $file != '..' && is_dir($root . $_POST['dir'] . $file) ) {
                                    echo "<li class=\"directory collapsed\"><a href=\"#\" rel=\"" . htmlentities($_POST['dir'] . $file) . "/\">" . htmlentities($file) ."</a></li>";
                            }
                    }
                    // All files
                    foreach( $files as $file ) {
                            if( file_exists($root . $_POST['dir'] . $file) && $file != '.' && $file != '..' && !is_dir($root . $_POST['dir'] . $file) ) {
                                    $ext = preg_replace('/^.*\./', '', $file);
                                    echo "<li class=\"file ext_$ext\"><a href=\"#\" rel=\"" . htmlentities($_POST['dir'] . $file) . "\">" . htmlentities($file);
                                    
                                    echo "</a></li>";
                                    
                                    if ($showTime)
                                        echo " <p style='margin-top: -20px; margin-left: 380px; margin-bottom: 0px; font-size: 11px; color: #ccc;'>".
                                            date("d.m H:i",filemtime($root . $_POST['dir'] . $file))."</p>\n";
                            }
                    }
                    echo "</ul>\n";	
            }
    }
}
else    // INITIALER AUFBAU MIT SOFORTIGEM AJAX REQUEST
{

    ?>
<script type="text/javascript" src="js/jquery/jquery-1.6.4.js"></script>

<script type="text/javascript" src="js/jqueryFileTree/jqueryFileTree.js"></script>

<link href="js/jqueryFileTree/jqueryFileTree.css" rel="stylesheet" type="text/css" media="screen" />

<style>

.example {
     /*   float: left; 
        align: center;*/
        margin: 5px;
}

.cont1 {
        width: 530px;
        height: 400px;
        border-top: solid 1px #BBB;
        border-left: solid 1px #BBB;
        border-bottom: solid 1px #FFF;
        border-right: solid 1px #FFF;
        background-color: #fff;
        overflow: auto;
        padding: 1px;
	opacity: 0.75;
	-moz-opacity: 0.75;
	filter: alpha(opacity=75);    
        color: #000;
}

</style>
<?php
 
    // WICHTIG: START_ROOT MUSS IMMER MITGEGEBEN WERDEN
    js_begin();
?>

function openFile(file) 
{
    window.location.href = '<?php echo _SYSTEM."?noheader=1&menu_nav=file_download&srcFile=".urlencode($root); ?>' + file;
}

// alert(file + '\n\nView only.');

$(document).ready( function() 
{
    $('#fileTreeContainer').fileTree({ root: '/', script: '<?php echo _SYSTEM."?menu_nav=file_tree&start_root=".urlencode($start_root)."&noheader=1"; ?>' }, function(file) { 
            openFile(file);
    });
});

<?php
js_end();

?>

<div class="example">
<h3><?php echo "/ ... ".substr(str_replace(_CONTENT_ROOT,"",$root),-42); ?></h3>

<div id="fileTreeContainer" class="cont1"></div>
</div>


<?php

}
}       // ISSET ROOT
else
    echo _ERR."Security problematic, connection details etc. logged..."


?>
