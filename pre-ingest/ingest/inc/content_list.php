<?php
// ********************************************
// ** FILE:    CONTENT_LIST.PHP              **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

nl();

$tabCols = 6;

?>

<div id="tooltip" class="tooltip">
</div>


<style>

th { border-left: 1px solid white; border-bottom: 1px solid black; }

</style>

<?php

form("form_ingest_manager");
hidden("menu_nav",$menu_nav);
hidden("sub_action","save_ingest_settings");

echo "<table id=\"mytable\" class=\"contenttable\"><tr><td colspan=\"".($tabCols-1)."\" align=\"left\">";

?>

<div id="buttonbar" style="margin-top: 5px; margin-bottom: 5px;" class="demo">

<button1 id="button1" >Reload & Analyze My Current Uploads</button1>
<button2 id="button2" >Refresh View</button2>
<button3 id="button3" >View My Analyzed Uploads</button3>
<button4 id="button4" >View My Ingest Log</button4>
<button5 id="button5" >Save My Changes</button5>

</div>

</td><td nowrap align="center" style="border: none; background-color: #C1DBFF;"><div id="ar">
    <a href="#" title="Switch between queue and realtime processing for small medias. See the age of the view and the duration to the next refresh." onClick="switch_visibility(document.getElementById('pd_div'));">
<font style="font: 9px arial; color: black;">

<?php

if(_QUEUE_MODE) 
{
    echo "Queuing <b>ON</b>";
}
else
{
    echo "Realtime Processing <b>ON</b>";
}

?>
</a>
    
<br> Last Refresh: <?php echo date("H:i"); ?> / Next in: 

<input type="text" size="3" value="" name="srest" style="width: 25px; font: 9px arial; border: none; background-color: transparent;">


</font></div>
</td></tr>
<tr><th>Upload &<br>Activation Time<?php

icon("down_18.png");

?></th><th>Content Destination &AMP;<br>Name</th><th>Content Type &AMP;<br>Ingest Status</th>
<th>my <font color="#4444ff">Alias</font>/<font color="#44ff44">IPR<sup>*</sup></font></th>
<th>Pages &<br>Sizes</th><th style="font: 10px arial !important;">
Worksteps: META|IMG<br>OCR/PDF|TAXON|INGEST<sup>*</sup></th></tr>     

<tr><td colspan="<?php echo $tabCols; ?>">&nbsp;</td></tr>

<?php 

// GET MANAGED CONTENT

$query = "select content_id, date_format(content_ctime,'%Y.%m.%d'), 
    date_format(content_atime,'%Y.%m.%d<br>%H:%i'), 
    content_root, content_type, content_name, content_alias, 
    content_status, content_size, content_pages, content_last_succ_step, content_ipr      
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
        $isPDF = isPDF($line[5]);

        // TIMES
        echo "<tr style=\"border-bottom: 1px solid #888888;\"><td>";
        
        // TRY GET & DISPLAY MEDIA PREVIEW
        echo "<table border=0 cellmargin=0 cellpadding=0><tr><td>";
        include("inc/mediathumb.php");
        echo "</td><td valign=middle align=center><font size=1>";
        // DATES
        echo $line[1]."<br><font color=\"#aaaaaa\">".$line[2]."</font></font>";
        echo _TAB;
        
        
        echo _TD;
        
        // CONTENT DEST
        echo str_replace("eingabefeld","cname",textfeld("content_name_".$line[0]." readonly ",
                $line[5],20,98,"","",false,false));
        // echo "<font color=#aaaaaa>...</font><font style=\"color: black; font-weight: bold; \">".substr(,-40)."</font>";
        lz();
        
        $myAction = "popup_win('ft','"._SYSTEM."?menu_nav=file_tree&start_root=".urlencode(clean_path(str_replace(_CONTENT_ROOT,"",$line[3]))."/")."',560,440);";
        
        icon("folder_32.png","Explore the media directory, check & open media files.<br><br>".str_replace(_CONTENT_ROOT,
        "",$line[3]),"onClick=\"javascript: ".$myAction."\"");

        echo _TD;
        
        // CONTENT TYPE 
        arr_dropdown($arrEnumCTypes,"content_type_".$line[0]."' style='background-color: #eeeeff; width:120px;",$line[4],1,"","",true);
        nl();

        // INGEST STATUS
        arr_dropdown($arrEnumCStatus,"content_status_".$line[0]."' disabled style='margin-top: 3px; background-color: #eeffee; width:120px;",$line[7],1,"","",true);

        echo "</td><td align=center>";

        // ALIAS
        textfeld("content_alias_".$line[0]." style=\"border: 1px solid #4D86CD; text-align: center; width: 150px; color: #4D86CD; font-weight: bold;\" ",$line[6],19,98,"","",false); 
        nl();
        
        // IPR INFO
        if ($line[11]=='') 
            $line[11] = abfrage("select default_ipr from "._USER_TAB." where user_id=".$user_id,$db);
                
        arr_dropdown($arrIPR,"content_ipr_".$line[0]."' style='border: 2px solid #11FF0B; margin-top: 3px; width: 150px;",$line[11],1,"","",false);
        
        // textfeld("content_ipr_".$line[0]." style=\"margin-top: 4px; border: 1px solid #11FF0B; text-align: center; width: 150px; font-weight: normal;\" ",$line[11],19,254,"","",false);

        // PAGES AND SIZE
        if (($line['content_pages']=="")||($line['content_pages']==0))
            $line['content_pages'] = "n.a.";

        echo "</td><td align=center><b>".$line['content_pages']." p.</b><br>".
        number_format(round($line['content_size']/1024/1024,2),1)."MB</td>";

        echo "\n<td nowrap style=\"border-bottom: 1px solid blue;\">";

        // CONTENT ACTIONS
        include("content_list_buttons.php");

        echo "</td></tr>\n";

        $line = mysql_fetch_array($result);
    }
    
    echo _TAB;
    

    js_begin();
    ?>

    var rest = <?php echo _GUI_REFRESH/1000; ?>;

    // ****************
    function showRest()
    // ****************
    {
     if (rest>0) { 
         rest = rest-1;
         document.forms.form_ingest_manager.srest.value = rest + 's';
         t1 = setTimeout("showRest()",1000);
     }
     return true;
    }
    
    // ******************
    function reloadList()
    // ******************
    {
        window.location.href = window.location.href;
    }

    showRest();
    
    t2 = window.setTimeout("reloadList()",<?php echo _GUI_REFRESH; ?>);

    <?php
    js_end();

}
// NO CONTENT PREPARED
else
{
    echo _TAB;
    
    nl(3);
    echo "<b>You have currently no Content under Management.</b><br><br>
Please analyze your content uploads first and check your account details in <b>my Preferences</b> above.\n";
    
}

?>
