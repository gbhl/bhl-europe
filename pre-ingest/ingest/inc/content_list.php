<?php
// ********************************************
// ** FILE:    CONTENT_LIST.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

form("form_ingest_manager");
hidden("menu_nav",$menu_nav);
hidden("sub_action","save_ingest_settings");

echo "<center>";
echo "<table border=1 width=1000 style='font: 11px verdana; background-color: white; margin-top: 33px;'>
<tr><td colspan=7 align=center>";

button("Reload & Analyze My Current Uploads",
        "popup_win('au','"._SYSTEM."?menu_nav=upload_analyze&analyzeDir=".urlencode(str_replace('//','/',_CONTENT_ROOT."/".$arrProvider['user_content_home']))."',1000,500);",990,32,'center'); 
nl();

button("View My Ingest Log",
        "popup_win('il','"._SYSTEM."?menu_nav=ingest_log',1000,500);",200,32,'center');

button("View My Uploads",
        "popup_win('mu','"._SYSTEM."?menu_nav=show_user_dir',1000,500);",200,32,'center');

button("Refresh Ingest List","document.location.href='"._SYSTEM."?menu_nav=ingest_list';",200,32,'center'); 

button("Save My Changes","submit",390,32,'center');

?>

<tr><th>Upload &<br>Activation Time<?php

icon("down_18.png");

?></th><th>Content Destination</th><th>Content Type</th>
<th>Ingest Status</th><th>Alias/Name<sup>*</sup></th>
<th>Pages &<br>Sizes</th><th style="font: 10px arial !important;">
Worksteps: META|IMG<br>OCR/PDF|TAXON|INGEST<sup>*</sup></th></tr>     

<?php 

// GET MANAGED CONTENT

$query = "select content_id, content_ctime, content_atime, 
    content_root, content_type, content_name, content_alias, 
    content_status, content_size, content_pages, content_last_succ_step     
    from content 
    where content_root like '%".$arrProvider['user_content_home']."%' 
        order by content_ctime desc, content_atime desc ";

$result = mysql_select($query);
$nrows  = mysql_num_rows($result);

if ($nrows>0)
{
    $line = mysql_fetch_array($result);
    
    while ($line)
    {
        $arrIngestDetails = get_ingest_details("",$line[0]);    // ALLE INGEST DETAILS ZUM CONTENT
        // kommt spaeter in verwendung wenn ingest vorgang initierbar!!!
        
        $isPDF = isPDF($line[5]);

        // TIMES
        echo "<tr><td>".$line[1]."<br><font color=\"#aaaaaa\">".$line[2]."</font>"._TD;
        
        // CONTENT DEST.
        echo "<font color=#aaaaaa>"._CONTENT_ROOT."</font>
            <font style=\"color: black; font-weight: bold; text-shadow: #555 3px 2px 4px;\">".str_replace(_CONTENT_ROOT,"",$line[3])."</font>"._TD;
        
        // CONTENT TYPE 
        arr_dropdown($arrEnumCTypes,'content_type',$line[4],1,"","",true);
        echo _TD;
        
        // INGEST STATUS
        arr_dropdown($arrEnumCStatus,'content_status',$line[7],1,"","",true);
        
        echo _TD;

        // ALIAS / NAME
        textfeld("content_alias_".$line[0]." style=\"border: 1px solid red; text-align: center; width: 120px; font-weight: bold;\" ",$line[6],19,98,"","",false); nl();
        textfeld("content_name_".$line[0]."  style=\"border: 1px solid #aaa; text-align: center; width: 120px; margin-top: 4px;\" readonly ",$line[5],20,98,"","",false);
        

        // PAGES AND SIZE
        if (($line['content_pages']=="")||($line['content_pages']==0))
            $line['content_pages'] = "n.a.";
        
        echo "</td><td align=center><b>".$line['content_pages']."</b><br>".
                round($line['content_size']/1024/1024,2)."MB</td>";
        
        echo "\n<td nowrap>";
        
        // CONTENT ACTIONS
        include("content_list_buttons.php");
        
        echo "</td></tr>\n";
        
        
        $line = mysql_fetch_array($result);
    }
    
    echo _TAB;

}
// NO CONTENT PREPARED
else
{
    echo _TAB;
    
    nl(3);
    echo "<center><b>You have currently no Content under Management.</b>
<br>        <br>
Please analyze your content uploads first and check your account details in <b>my Account</b> above.

</center>";
}



/*

for ($i=0;$i<$nrows;$i++)
{
    echo _TR." relative path not found"._TD." .. "._TD." .. "._TD." .. "._TD." .. "._TD." .. "._TD;

    // OPERATIONS
    checkbox("ingest_do_ocr");
    checkbox("ingest_do_taxon");
    checkbox("ingest_do_sm");

    echo " "._TR;
}
*/


// button($value, $js_action, $width, $height, $textalign, $id, $echome, $tabindex)

// echo _HOME;
// echo "<a href=\""._SYSTEM."?menu_nav=admin\" >test</a>";


    
?>
