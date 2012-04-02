<?php
// ********************************************
// ** FILE:    UPLOAD_ANALYZE.PHP            **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    19.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

include_once(_SHARED."formlib.php");
include_once(_SHARED."imagelib.php");
include_once(_SHARED."pdf_tools.php");


// PFADE DECODIEREN
if (isset($analyzeDir))  $analyzeDir  = urldecode($analyzeDir);
if (isset($dirSelected)) $dirSelected = urldecode($dirSelected);


$strNoContent = _ERR."No Content found! Check your uploads and configuration.<br><br>\n";


if (!isset($analyzeDir))    die(_ERR." No Directory set.");
else                        $analyzeDir = clean_path($analyzeDir);

ob_start();

echo "<center><h2 style='margin-top: 5px ! important;'>Content Analyzer - <font color=green>activates new Content</font>
    <br><font color=blue size=2><b>From your home directory: ".$analyzeDir."/...</b>
    <br>For SERIALS check only the serials root folder!</font></h2>";

form('dir_details');
hidden("menu_nav",      $menu_nav);


if (_MODE=='production')
progressBar("Please wait, reading Files<br>to database...","processing.gif","margin-top: 55px; left: 350px;","visible",2);

echo invisible_html(1024*5);

@ob_end_flush();
@ob_flush();
@flush(); 
sleep(1);



// ************************************
// PRAESENTIERE 1 EBENE ZUM ANALYSIEREN
// ************************************
if ( (!isset($dirSelected)) || (!is_dir(clean_path($analyzeDir."/".$dirSelected))) ) 
{
    $arrDir = getDirectory($analyzeDir,array(),0,array('.', '..',_AIP_DIR ),0);
    $anzDir = count($arrDir);
    
    
    if ($anzDir>0)
    {
        hidden("sub_action",    "save_dir_details");
        hidden("analyzeDir",    urlencode($analyzeDir));

        echo "<table cellpadding=\"3\" cellspacing=\"3\" border=\"0\" style=\"border: 1px solid black;\">\n";        

        for ($i=0;$i<$anzDir;$i++)    
        {
            if (is_dir($arrDir[$i]))    
            { 
                $curRelativeDir = str_replace($analyzeDir,"",$arrDir[$i]);
                
                $myUrl = _SYSTEM."?menu_nav=".$menu_nav."&analyzeDir=".urlencode($analyzeDir)."&dirSelected=".$curRelativeDir;
                
                echo "<tr style=\"background-color: white;\" onMouseOver=\"this.style.backgroundColor='#ffffaa';\" onMouseOut=\"this.style.backgroundColor='white';\">
                    <td style=\"width: 550px;\"><a style=\"text-decoration: none;\" href=\"".$myUrl."\">";
                
                icon("folder_16.png");  echo " .".$curRelativeDir."</a><br>";
                
                echo _TD;
                
                $myJS = "onClick=\"javascript: if (this.checked) { if (!confirm('Do you really want to activate content at your root?')) { this.checked=false; } }    \"";
                
                if (((int)abfrage("select count(1) from content where content_root='".$arrDir[$i]."'",$db))==0)
                    checkbox("enable_".$i,0,"",$myJS,"","",true,$arrDir[$i]);
                else
                    icon("green_16.png", "Already under management, disable in management view only...");

                echo "</tr>\n";
            }
        }
        
        echo _TAB;
        nl();
        button("SAVE - your current activation selection","submit",900);
    }
    else
        echo $strNoContent;
    
   
    
    if (_MODE=='production') close_progressBar();
    
    nl();
}
else
{
	// --------------------------------- $analyzeDir LESEN ---------------------------------------
        if (!instr($analyzeDir,$dirSelected)) $analyzeDir = clean_path ($analyzeDir."/".$dirSelected);
	// -------------------------------------------------------------------------------------------
        $arrDir = array();
        if (!is_dir_empty($analyzeDir,true)) $arrDir[] = $analyzeDir;           // FALLS SELBST FILES ENTHAELT
	$arrDir   = array_merge($arrDir,getDirectory($analyzeDir,array(),0,
                array('.', '..',_AIP_DIR ),_ANALYZE_MAX_DEPTH));
        $anzDir   = count($arrDir);
	// -----------------------------------------------------------------------------------------

	// EINLESEN DER STRUKTUR IN DB
	$csvDir = implode(_TRENNER,$arrDir);
	$csvDir = str_replace(_CONTENT_ROOT,"",$csvDir);

	// mysql_select("update users set user_directory='".mysql_clean_string($csvDir)."' where user_id=".$user_id,$db);
	// $out2 = ob_get_contents();
	// @ob_end_clean();

        if (_MODE=='production') close_progressBar();


	if ($anzDir>0)
	{
		if ((!isset($curpage))||(!is_numeric($curpage))) $curpage = 1;            // _MAX_LISTLEN page
                
		hidden("sub_action",    "save_dir_details");
                hidden("curpage",       $curpage);
		hidden("analyzeDir",    urlencode(str_replace($dirSelected,"",$analyzeDir)));
                hidden("dirSelected",   urlencode($dirSelected));

                $anzPages           = ceil($anzDir/_MAX_LISTLEN);
		$cur_pages          = 0;                            // content page file counter
		$last_listed_dir    = "";
		$last_listed_dir_cb = "";

                $offset = _MAX_LISTLEN*($curpage-1);

                $maxidx = $offset+_MAX_LISTLEN;

                if ($maxidx>($anzDir-1)) $maxidx = $anzDir-1;       // seitenteil (letzte seite)

                // HEADERS
                echo "<b>".count($arrDir)." Element(s) found <font size=1>(Viewing ".($offset+1)."-".($maxidx+1).")</font><br>
			<font color=green>Please check the objects you want to manage and save.</font>"; nl();

                // UP TO $dirSelected SELECTION
                abs_l(-30,0); lz(2);
                icon("folder_32_up.png","Up to re-select starting folder...",
                        "onClick=\"document.location.href='".
                        _SYSTEM."?menu_nav=upload_analyze&analyzeDir=".urlencode(_USER_CONTENT_ROOT)."';\"");
                abs_e();
                
                // BLAETTERN
                if (_MODE=='development')   $minPages = 0;      // test
                else                        $minPages = 1;

                if ($anzPages > $minPages)
                {
                    abs_r(5,5,8); echo "Page(s): ";
                    
                    js_begin();
                    ?> 

                    function loadPage(pageno)
                    {
                     document.forms.dir_details.sub_action.value='';
                     document.forms.dir_details.curpage.value=pageno;
                     document.forms.dir_details.submit();
                    }

                    <?php

                    js_end();

                    for ($i=1;$i<=$anzPages;$i++)
                    {
                        if ($curpage<>$i)   button($i,"loadPage(".$i.");",26,22,"center");
                        else    // AKTUELLE PAGE 
                            { 
                                button($i."' style='cursor: pointer; width: 26px; height: 22px; background-color: #6DECFF; border: 2px solid black;",
                                "loadPage(".$i.");",26,22,"center"); 
                            }
                    }

                    abs_e();
                }
                
                


                // LIST
		echo "<table border=\"1\" class=\"contentlist\">
			<tr><th>PATH | INFO &nbsp; (target files/directories [filtered])</th>
                        <th width=70>Activate Content Root/File</th></tr>";

                // **********************************
                for ($i=$offset;$i<=($maxidx+1);$i++)
                // **********************************
		{
		   ob_start();
		   
		   $metadata  = false;
		   $isPDF     = false;
		   $isDir     = false;
		   
		   if ($i<($maxidx+1))
		   {
			if      (is_dir($arrDir[$i]))  $isDir=true;
			else if (isPDF($arrDir[$i]))   $isPDF=true;
		   }
		   
		   // PAGES LINE
		   if ((($cur_pages<>0)&&(($isDir)||($isPDF)))||($i==($maxidx+1))) {
			   
			   echo "<tr><td align=right>";
			   icon("picture.gif");
			   echo " Number of valid page objects for this (image files, scans, ...) </td><td align=center>".$cur_pages." p.";
			   echo "</td></tr>\n";
			   $cur_pages=0;
		   }

                   if ($i==($maxidx+1)) break(1);   // RAUS FALLS LETZTER SCHON VORIGER WAR

		   $isPageData = isPagedata($arrDir[$i]);

		   // PAGE DATA FILES NICHT LISTEN (UEBERSICHTLICHKEIT)
		   if (!$isPageData) 
		   {
			   // CONTENT LINE
			   echo "<tr "; 

			   if(($isDir)||($isPDF))               { echo "bgcolor=\"#E4EBF7\" "; }
			   else if (isMetadata($arrDir[$i]))    { echo "bgcolor=\"#F7F6AF\" "; $metadata=true; }

			   echo "><td ";

			   if ((!$isDir)&&(!$isPDF))              echo " style=\"color: #888888;\" ";
			   else                                   echo " style=\"font-weight: bolder; font-size: 16px;\" ";

			   // AUSGABE AKTUELLER ZEILE
			   echo ">".str_replace(_CONTENT_ROOT,"",$arrDir[$i])."</td><td align=center>";
			 
			   // ONLY DIRS AND PDFS CAN BE ACTIVATED
			   if ((($isDir)||($isPDF)))
			   {
				   // LOOK IF ALREADY ACTIVE (2 TEIL IST WEGEN PDFS DIRNAME EBENFALLS KEINE CHECKBOX MEHR)
				   if ((((int)abfrage("select count(1) from content where content_root='".$arrDir[$i]."'",$db))>0)||
					   (((int)abfrage("select count(1) from content where content_root='".dirname($arrDir[$i])."'",$db))>0))
					   icon("green_16.png", "Already under management, disable in management view only...");
				   else 
				   {
					   // NICHT AKTIVIERTE DIRS & PDFS 
					   
					   // FALLS DIRECTORY NICHT LEER ANBIETEN
					   if ((!$isPDF)&&(!is_dir_empty($arrDir[$i],true)))  // count only files
					   {
							checkbox("enable_".$i,0,"","","","",true,$arrDir[$i]);
							$last_listed_dir=$arrDir[$i];
							$last_listed_dir_cb="enable_".$i;
					   }
					   else if ($isPDF) 
					   {
						   checkbox("enable_".$i,0,"","","","",true,$arrDir[$i]);
						   echo "<br>".getNumPagesInPDF(array($arrDir[$i]))." p.";
						   
						   // FALLS VORIGES DIR DAS DIESES PDFS WAR DEAKTIVIERE DESSEN VORIGE CHECKBOX WIEDER
						   if (instr($arrDir[$i],$last_listed_dir)) 
						   {
							   js_command("myObj = document.forms.dir_details.".$last_listed_dir_cb."; 
								   if (IsElement(myObj)) { myObj.disabled = true; }");
						   }
					   }
				   }
			   }
			   else 
				   if ($metadata) icon("write12.gif");

			   echo "</td></tr>\n";
		   }
		   else $cur_pages++;
		   
		   @ob_end_flush();
		   @ob_flush();
		   @flush();        
		}
		
		echo "</table>\n";
		
		// echo "<font >".str_replace(_TRENNER,"<br>\n",$csvDir)."</font>";
		nl();
		button("SAVE - your current activation selection","submit",900);
	}
	else
		echo $strNoContent;

} // $dirSelected given

// CLOSE & REFRESH PARENT
close_ingest_popup("CLOSE - stop activating content");

close_form();

nl(2);

?>

</center>
