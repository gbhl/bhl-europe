<?php

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


// *****************
//   SAVE ACCOUNT 
// *****************
if($sub_action=="save_cp_details")
{
    $query  = "update users set 
        user_content_home='".$user_content_home."', 
        user_content_id='".$user_content_id."', 
        user_config='".$user_config."', 
        user_config_smt='".$user_config_smt."'  
        where user_id=".$user_id;

    if (mysql_select($query,$db)>0) $endmsg = "Account data successfully updated!";

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
    include_once(_SHARED."dir_tools.php");
    
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
             $ctime = @filectime($arrKeys[$i]);
             if ($ctime=="") time();
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
                $cpages = (int) count(getPageFiles($_POST[$arrKeys[$i]]));
             }

             $inserted += mysql_select($query." (".$nextPK.",'".$_POST[$arrKeys[$i]]."','".
                     str_replace(_CONTENT_ROOT,'',$_POST[$arrKeys[$i]])."',now(),'".$ctime.
                     "',".$fsize.",".$cpages.")",$db);
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
    }
    
    // SPEICHERN DER CONTENT TYPS UND INGEST STATUS !!! hier
    
}



// *************************************
// ***** DROP CONTENT (FROM MGMT) ******
// *************************************
if ($sub_action=="drop_content")
{
   if (is_numeric($content_id))
       mysql_select("delete from content where content_id=".$content_id,$db);
}




?>
