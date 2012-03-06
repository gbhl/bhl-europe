<?php

/*
 * MANIPULATIVE DATABASE OPERATIONS AFTER POSTS
 */


// *****************
//   SAVE ACCOUNT 
// *****************
if($sub_action=="save_cp_details")
{
    // NEUEN ODER BESTEHENDEN WERT UPDATEN (ERSPART DYNAMISCHES SQL)       
    if (!isset($user_content_home)) $user_content_home = $arrProvider['user_content_home'];
    if (!isset($user_content_id))   $user_content_id   = $arrProvider['user_content_id'];
    if (!isset($user_config))       $user_config       = $arrProvider['user_config'];
    if (!isset($user_config_smt))   $user_config_smt   = $arrProvider['user_config_smt'];
    if ((!isset($queue_mode))||(!is_numeric($queue_mode)))    $queue_mode = 0;
    if (!isset($user_memo))         $user_memo         = $arrProvider['user_memo'];
    
    $query  = "update users set 
        user_content_home='".$user_content_home."', 
        user_content_id='".$user_content_id."', 
        user_config='".$user_config."', 
        user_config_smt='".$user_config_smt."',
        queue_mode=".$queue_mode.",
        user_memo='".$user_memo."',
        metadata_ws='".$metadata_ws."' 
        where user_id=".$user_id;
    
    // BESTAETIGUNG UM UMSETZEN EVTL. DADURCH GEAENDERTER REALTIME USER VARIABLEN
    if (mysql_select($query,$db)>0) { 
        $endmsg = "Account data successfully updated!";
        $arrProvider['user_content_home'] = $user_content_home;
        $arrProvider['user_content_id']   = $user_content_id;
        $arrProvider['user_config']       = $user_config;
        $arrProvider['user_config_smt']   = $user_config_smt;
        $arrProvider['queue_mode']        = $queue_mode;
        $arrProvider['user_memo']         = $user_memo;
        $arrProvider['metadata_ws']       = $metadata_ws;
    }

    // *****************
    // PASSWORD CHANGE 
    // *****************
    if ((isset($user_pwd))&&($user_pwd!=""))
    {
        $query = "update users set user_pwd=password('".$user_pwd."') where user_id=".$user_id;

        if (mysql_select($query,$db)>0) $endmsg .= " Password Updated.";
    }
}



// *******************************
//   SAVE NEW CONTENT TO MANAGE
// *******************************
if($sub_action=="save_dir_details")
{
    $inserted = 0;
    $query    = "insert into content (content_id,content_root,content_name,
        content_atime,content_ctime,content_size,content_pages) values ";
    
    // CHECKBOXEN LIEFERN PER VAR.NAMEN DIE ZU AKTIVIERENDEN VERZEICHNISSE
    $arrKeys = array_keys($_POST);

    $anzKeys = count($arrKeys);

    // echo_pre($_POST);    
    
    for ($i=0;$i<$anzKeys;$i++)
    {
        
     if (instr($arrKeys[$i],"enable_"))
     {
         /* if (!is_dir($arrKeys[$i]))     // checkbox value mismatch correction
         {
            if (instr($arrKeys[$i],"_PDF"))   $arrKeys[$i] = str_replace("_PDF",".PDF",$arrKeys[$i]);
            if (instr($arrKeys[$i],"_pdf"))   $arrKeys[$i] = str_replace("_pdf",".pdf",$arrKeys[$i]);
           
           [enable_2376] => /mnt/nfs/upload/providers/testdata/spices_prepared/medicinischpharm00luer
           [enable_5386] => /mnt/nfs/upload/providers/testdata/spices_prepared/floradecatalunya01cade* 
         }*/
         
         $isPDF = isPDF($_POST[$arrKeys[$i]]);
         $isDir = is_dir($_POST[$arrKeys[$i]]);
         
         if (($isDir)||($isPDF))
         {
             $nextPK = nextPK("content",$db,"content_id");

             // FILE OR DIR CREATE TIME FOR SORTING
             // $ctime = @filectime($arrKeys[$i]);
             
             $ctime = @filectime($_POST[$arrKeys[$i]]);
             
             if ($ctime=="") $ctime = time();
             
             $ctime = date("Y-m-d H:i:s",$ctime);
             
             // SIZE ESTIMATION (DIR|FILE)
             // if isPDF($arrKeys[$i]))
             // $fsize = getFileSize()
             if ($isDir) $fsize = get_dir_size($_POST[$arrKeys[$i]]);
             if ($isPDF) $fsize = filesize ($_POST[$arrKeys[$i]]);
             
             // CONTENT PAGES
             // PAGES & SIZE
             if ($isPDF)
             {
                include_once(_SHARED."pdf_tools.php");
                $cpages = (int) getNumPagesInPDF(array($_POST[$arrKeys[$i]]));
             }
             else  // BILDDATEN SELBEN TYPS IM ROOT ZAEHLEN (NICHT REKURSIV!)
             {
                $cpages = (int) count(getContentFiles($_POST[$arrKeys[$i]],'pagedata'));
             }
             
             if ($isPDF) { $croot = dirname($_POST[$arrKeys[$i]]); $cname=basename($_POST[$arrKeys[$i]]); }
             else        { 
                 $croot = $_POST[$arrKeys[$i]];
                 $cname = str_replace(urldecode($analyzeDir),'',$_POST[$arrKeys[$i]]);
                 $cname = str_replace(_CONTENT_ROOT,'',$cname);
             }

             $inserted += mysql_select($query." (".$nextPK.",'".$croot."','".$cname."',now(),'".$ctime."',".$fsize.",".$cpages.")",$db);
         }
     }
    }
    
    if ($inserted>0) $endmsg .= $inserted." Structures for Ingest Management prepared successfully.";
    
    // if (mysql_select($query,$db)>0) $endmsg = "Account data successfully updated!";
}



// *********************************
// ***** SAVE_INGEST_SETTINGS ******
// *********************************
if ($sub_action=="save_ingest_settings")
{
   // SAVE CONTENT ALIASE 
   // WERTE ALLER POST VARS DIE MIT CONTENT_ALIAS BEGINNEN    
    
    $arrKeys = array_keys($_POST);
    $anzKeys = count($arrKeys);
    
    for ($i=0;$i<$anzKeys;$i++)
    {
         if (instr($arrKeys[$i],"content_alias"))
         {
                $curContentID    = substr($arrKeys[$i],strrpos($arrKeys[$i],"_")+1);
                $curContentAlias = $_POST[$arrKeys[$i]];

                $query = "update content set content_alias='".
                        mysql_clean_string($curContentAlias)."' where content_id=".$curContentID;

                mysql_select($query,$db);
         }
         
         if (instr($arrKeys[$i],"content_ipr"))
         {
                $curContentID  = substr($arrKeys[$i],strrpos($arrKeys[$i],"_")+1);
                $curContentIPR = $_POST[$arrKeys[$i]];

                $query = "update content set content_ipr='".
                        mysql_clean_string($curContentIPR)."' where content_id=".$curContentID;

                mysql_select($query,$db);
         }
         
         if (instr($arrKeys[$i],"content_type"))
         {
                $curContentID   = substr($arrKeys[$i],strrpos($arrKeys[$i],"_")+1);
                $curContentType = $_POST[$arrKeys[$i]];

                $query = "update content set content_type='".
                        mysql_clean_string($curContentType)."' where content_id=".$curContentID;

                mysql_select($query,$db);
         }

    }
    
}



// *************************************
// ***** DROP CONTENT (FROM MGMT) ******
// *************************************
if ($sub_action == "drop_content") 
{
    if (isset($destDir))     // FROM INIT.PHP
    {
        // DELETE CONTENT MANAGMENT DATA ROW
        mysql_select("delete from content where content_id=" . $content_id, $db);

        // DELETE QUEUE SCRIPT FILE
        @unlink($curQueueFile);

        rrmdir($destDir);   // AIP DIR DROP

        $endmsg .= "Content " . $content_id . " removed from management. Queue/Workdir/"._AIP_DIR." cleaned up.";

        unset($content_id);
    }
}


// RESET INGEST TO NOT INGESTED VIA DELETE OF CONTROLFILES
if($sub_action=="reset_ingest")
{
    
    @unlink(clean_path($destDir."/")._FEDORA_CF_FINISHED);
    @unlink(clean_path($destDir."/")._FEDORA_CF_READY);
    
    // !!! ALLE NICHT OLEF XML AUCH LOESCHEN HIER
    
    $endmsg .= "Selected content no longer marked as -ingest ready- and no longer marked as -ingested-.";
}


?>
