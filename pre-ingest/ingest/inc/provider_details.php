<?php
// ********************************************
// ** FILE:    PROVIDER_DETAILS.PHP          **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
// REQUIRES: $arrProvider ...

$extraTag = "";

if ((!_SELF_ADMIN)&&($user_name!="admin"))   $extraTag = " DISABLED ";



echo "<div id='pd_div' style='z-index: 99; visibility: hidden; position: absolute; left: 100px; top: 60px;'>";

form('cp_details');
hidden("menu_nav",$menu_nav);
hidden("sub_action","save_cp_details");

echo "<table cellspacing=3 cellpadding=3 width=650 border=1 style='font: 12px verdana; color: #1A3B72; background-color: #E4EBF7;'>";
echo "<tr><th colspan=2 style=\"height: 33px; \">".
        icon("preferences.png","","","","",false)." my Preferences"._TR;

echo "<li>My Username"._TD."<b>".$arrProvider['user_name']."</b>";  
if ($arrProvider['is_admin']=='1') echo " (Admin)";    

lz(6);
echo "Password <input type='password' name='user_pwd' size='25' value=''>";
echo " <sup>*enter to change</sup> ";

echo _TR."Ingested Medias"._TD.
		((int)abfrage("select count(1) from ingests where ingest_status='success'"));
lz(3);
echo "<a href='#' onClick=\"popup_win('ih','"._SYSTEM."?menu_nav=ingest_history&user_name=".$arrProvider['user_name']."',400,500);\">";
icon("history_21.gif"," ingest history ...[click for details]"); echo " Ingest History</a>";

lz(3);
echo checkbox("queue_mode",$arrProvider['queue_mode'].$extraTag,"<b>Queue Mode</b>","","before","",false)." <sup>*queued time shifted processing</sup>"._TR;

// CONTENT HOME
echo "Content Home"._TD;        textfeld("user_content_home ".$extraTag,$arrProvider['user_content_home'],55);
lz(3);
if (is_readable(_CONTENT_ROOT.$arrProvider['user_content_home'])) 
		icon("green_16.png","Filesystem Check ok, readable..."); 
else    icon("failed_16.png","Filesystem Check failed no access possible..."); 
echo " <sup>*no slashes needed</sup> ";

// KUERZEL
echo _TR."Provider Token"._TD;  textfeld("user_content_id ".$extraTag,$arrProvider['user_content_id'],45);
echo " <sup>*defines your working directory</sup> ";

// CONFIGS
echo _TR."Ingest Config"._TD;   textfeld("user_config ".$extraTag,$arrProvider['user_config'],55,900);
echo _TR."Schema Mapping"._TD;  textfeld("user_config_smt ".$extraTag,$arrProvider['user_config_smt'],55,900);
echo " <sup>*Parameters</sup> ";

echo _TR."Working Dir"._TD._WORK_DIR.$arrProvider['user_content_id']."/"; lz(3);
if (is_readable(_WORK_DIR.$arrProvider['user_content_id']."/"))
		icon("green_16.png", "Filesystem Check ok, readable..."); 
else    icon("failed_16.png","Filesystem Check failed no access possible..."); 

lz();
icon("folder_16.png","Explore this Directory.","onClick=\"javascript: popup_win('cr','"._SYSTEM."?menu_nav=show_working_dir',1000,500);\"");


// ALL PROVIDERS ROOT
echo _TR."Content Root"._TD._CONTENT_ROOT; lz(3);
if (is_readable(_CONTENT_ROOT)) icon("green_16.png", "Filesystem Check ok, readable..."); 
else                            icon("failed_16.png","Filesystem Check failed no access possible..."); 

lz();
icon("folder_16.png","Explore this Directory.","onClick=\"javascript: popup_win('cr','"._SYSTEM."?menu_nav=show_content_root',1000,500);\"");


// CREATE USERS WORKDIRECTORY
@mkdir(_WORK_DIR.$arrProvider['user_content_id']);


// SHOW USERS CONTENT ROOT
$usr_croot = clean_path(_CONTENT_ROOT."/".$arrProvider['user_content_home']);

echo _TR."User Content"._TD.$usr_croot; lz(3);
if (is_readable($usr_croot)) icon("green_16.png", "Filesystem Check ok, readable..."); 
else                         icon("failed_16.png","Filesystem Check failed no access possible..."); 

lz();
icon("exclam_16.png","You are managing this directory.");
lz();
icon("folder_16.png","Explore this Directory.","onClick=\"javascript: popup_win('cr','"._SYSTEM."?menu_nav=show_user_content_root',1000,500);\"");




echo _TR."Metadata Webservice"._TD;  
textfeld("metadata_ws ".$extraTag,$arrProvider['metadata_ws'],62,900);
lz();
icon("exclam_16.png","Preferred setting. If set webservice is used before local file.");



echo "</td></tr><tr><td colspan=2>";
textarea("user_memo",$arrProvider['user_memo'],"","",
		"<br>My Personal Ingest Notes<br>","",true,"border: 1px solid black; width: 640px; height: 70px;"); 

echo "</td></tr><tr><td align=center colspan=2>
<input type=\"submit\" class=\"button\" value=\"Save\" style=\"width: 449px;\">
<input type=\"button\" class=\"button\" value=\"Close\" onClick=\"document.getElementById('pd_div').style.visibility = 'hidden';\" style=\"width: 189px;\">    ";

echo _TAB;

close_form();

echo "</div>";

?>