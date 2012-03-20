<?php

/*
 * MISC. TEST SCRIPT
 * 
 * http://localhost/index.php?menu_nav=test
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


?>
