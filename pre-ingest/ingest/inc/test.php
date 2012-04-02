<?php

/*
 * MISC. TEST SCRIPT
 * 
 * http://localhost/index.php?menu_nav=test&content_id=3
 */


$arrToSort = array(
"asklfjasdlfj_0324_PAGE_011.tif",
"asklfjasdlfj_314_PAGE_01.tif",
"asklfjasdlfj_0301_PAGE_2.tif"
);

$arrToSort = array(
"asklfjasdlfj_.tif",
"asklfjasfj_.tif",
"asklfjasj_.tif"
);


echo_pre(
sortPageFiles($arrToSort) // ,$sortBy='sequence'
        );






$arrTiffs = getContentFiles($contentDir, 'single_suffix', true,'.tif'); 
$nTiffs   = count($arrTiffs);

echo _TABLE;

echo_pre($arrTiffs);


// TIFFS VON DATENBANK HOLEN 
$query = "select content_pages_tiff from content where content_id=" . $content_id;
$arrTiffs = explode(_TRENNER, abfrage($query, $db));


echo _TD;

echo_pre($arrTiffs);

echo _TAB;



?>
