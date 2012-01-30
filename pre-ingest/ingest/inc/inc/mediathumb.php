<?php

/*
 *  TRY TO CREATE A MEDIA THUMB
 */

$thumbPage= 2;

$thumbIMG = clean_path($line[3] . "/". _AIP_DIR."/"._THUMB_FN);
$thumbURL = getReverseLookupURL($thumbIMG);        


if (!file_exists($thumbIMG)) 
{
    if (!$isPDF)
    {
        @mkdir(dirname($thumbIMG));
        
        $arrCF = getContentFiles($line[3], 'pagedata', false);

        sort($arrCF);

        $myCmd = _IMG_MAGICK_CONVERT . " -resize "._THUMB_SIZE." \"" . $arrCF[$thumbPage] . "\" \"" . $thumbIMG."\"";
        $myCmd = exec_prepare($myCmd);
        @exec($myCmd);

        $myCmd = str_replace("convert","composite",_IMG_MAGICK_CONVERT) . " -gravity center \"".$thumbIMG."\" \""._USR_ABS._THUMB_BGRD."\" \"".$thumbIMG."\" ";
        $myCmd = exec_prepare($myCmd);
        @exec($myCmd);
    }
    else
        $pdfURL = _REVERSE_LOOKUP_URL.clean_path($arrProvider['user_content_home']."/".
str_replace($arrProvider['user_content_home'],"",str_replace(_CONTENT_ROOT,"",$line[3]))."/".$line[5]);

}


// THUMB EXISTS
if (file_exists($thumbIMG))          echo "<img border='0' src='".$thumbURL."' alt='Preview...' title='Preview...' width='45'>\n";
else if (($isPDF)&&(isset($pdfURL))) echo "<a href=\"".$pdfURL."\" target=_blank>".icon("pdf_thumb.png","PDF source...","","","",false)."</a>";
else                                 icon("no_preview.png","No preview available...");


?>
