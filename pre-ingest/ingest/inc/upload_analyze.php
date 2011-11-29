<?php
// ********************************************
// ** FILE:    UPLOAD_ANALYZE.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

include_once(_SHARED."formlib.php");
include_once(_SHARED."imagelib.php");
include_once(_SHARED."dir_tools.php");


if (!isset($analyzeDir)) die(_ERR." No Directory set.");

$analyzeDir = str_replace('//','/',urldecode($analyzeDir));



ob_start();

echo "<h2>Content Analyzer - <font color=green>activate new Content</font><br><font color=blue size=2><b>Checking ".$analyzeDir."...</b></font></h2>";

progressBar("Please wait, reading Files<br>to database...","processing.gif","margin-top: 55px; left: 20px;","visible",2);

echo invisible_html(1024*5);

ob_end_flush();
ob_flush();
flush(); 
sleep(2);

// --------------------------------------------------------------

// echo_pre(getDirectory($analyzeDir));

$arrDir = getDirectory($analyzeDir,array());
    
// echo_pre($arrDir);       <-- wird nie angezeigt das buffer drop !

$csvDir = implode(_TRENNER,$arrDir);
$csvDir = str_replace(_CONTENT_ROOT,"",$csvDir);

mysql_select("update users set user_directory='".($csvDir)."' where user_id=".$user_id,$db);

$out2 = ob_get_contents();

ob_end_clean();

close_progressBar();

$anzDir = count($arrDir);

form('dir_details');
 
if ($anzDir>0)
{
   
    echo "<b>".count($arrDir)." Elements found.<br>
        <font color=green>Please check the objects you want to manage.</font>";
    nl();
   
    hidden("menu_nav",$menu_nav);
    hidden("sub_action","save_dir_details");
    hidden("analyzeDir",urlencode($analyzeDir));
    
    echo "<table border=1 width=980 style=\"margin-left: 2px; border-color: green; background-color: white; font: 11px courier new;\">
        <tr><th>PATH | INFO</th><th width=70>Activate Content Root/File</th></tr>";
    
    $cur_pages = 0;
    
    for ($i=0;$i<$anzDir;$i++)
    {
       $metadata  = false;
       $isPDF    = false;
       $isDir    = false;
       
       if (is_dir($arrDir[$i]))     $isDir=true;
       else if (isPDF($arrDir[$i])) $isPDF=true;
       
       // PAGES LINE
       if (($cur_pages<>0)&&(($isDir)||($isPDF))) {
           
           echo "<tr><td align=right>";
           icon("picture.gif");
           echo " Number of valid page objects for this (image files, scans, ...) </td><td align=center>".$cur_pages;;
           echo "</tr></td>\n";
           $cur_pages=0;
       }
       
       $isPageData = isPagedata($arrDir[$i]);
       
       $isAIPdir   = false;
       if (instr($arrDir[$i],"/"._AIP_DIR,true))    $isAIPdir = true;
       
       // PAGE DATA FILES NICHT LISTEN (UEBERSICHTLICHKEIT)
       if (!$isPageData) 
       {
           // CONTENT LINE
           echo "<tr "; 

           if(($isDir)||($isPDF))                 echo "bgcolor=\"#E4EBF7\" ";
           else if (isMetadata($arrDir[$i]))    { echo "bgcolor=\"#F7F6AF\" "; $metadata=true; }

           echo "><td ";

           if ($isAIPdir)                          echo " style=\"color: blue;\" ";
           else if ((!$isDir)&&(!$isPDF))          echo " style=\"color: #888888;\" ";
           else                                    echo " style=\"font-weight: bolder; font-size: 16px;\" ";

           echo ">".str_replace(_CONTENT_ROOT,"",$arrDir[$i])."</td><td align=center>";

           // ONLY DIRS AND PDFS CAN BE ACTIVATED BUT NO AIP DIRS
           if ((($isDir)||($isPDF))&&(!$isAIPdir))
           {
               // LOOK IF ALREADY ACTIVE
               if (((int)abfrage("select count(*) from content where content_root='".$arrDir[$i]."'",$db))>0)
                   icon("green_16.png", "Already under management, disable in management view only...");
               else
                   checkbox("enable_".$i,0,"","","","",true,$arrDir[$i]);      
           }
           else 
               if ($metadata) icon("write12.gif");

           echo "</td></tr>\n";
       }
       else $cur_pages++;
    }
    
    echo "</table>";
    
    // echo "<font >".str_replace(_TRENNER,"<br>\n",$csvDir)."</font>";
    button("save selection","submit",200);

}
else
    echo "<font color=red>No Content found!</font>";

lz(2);
close_button(2,200);

close_form();


?>
