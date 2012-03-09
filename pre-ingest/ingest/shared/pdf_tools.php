<?php
// ***************************************
// ** FILE:    PDF.PHP                  **
// ** PURPOSE: TA ORACLE SYSTEM VIEW    **
// ** DATE:    25.10.2007               **
// ** AUTHOR:  ANDREAS MEHRRATH         **
// ***************************************



// *****************************************************
function button_pdf($abs_file_name,$orientation="right")
// *****************************************************
{
    if ($orientation=="right") abs_r(10,0,10);

    icon(_SHARED_IMG."pdf.gif","Download PDF...","onClick=\"javascript: document.location='"._SHARED_URL."file_download.php?srcFile=".urlencode($abs_file_name)."';\"");

    if ($orientation=="right") abs_e();
}


// **************************************************
function getNumPagesInPDF(array $arguments = array()) 
// **************************************************
{
    @list($PDFPath) = $arguments;
    $stream = @fopen($PDFPath, "r");

    $PDFContent = @fread($stream, filesize($PDFPath));

    if (!$stream || !$PDFContent)
    {
        @fclose($stream);
        return false;
    }
        

    $firstValue = 0;
    $secondValue = 0;
    if (preg_match("/\/N\s+([0-9]+)/", $PDFContent, $matches)) {
        $firstValue = $matches[1];
    }

    if (preg_match_all("/\/Count\s+([0-9]+)/s", $PDFContent, $matches)) {
        $secondValue = max($matches[1]);
    }

    @fclose($stream);

    return (($secondValue != 0) ? $secondValue : max($firstValue, $secondValue));
}



// ***********************************
function php2pdf($url)
// ***********************************
{

 $tempDir = _SHARED."html2pdf/temp/";
 $success = false;

 


 // *******************************
 // 1. wget .php --> .html
 // *******************************
 
 @unlink($tempDir."temp.html");

 system("/usr/bin/wget --quiet --tries=1 --timeout=60 --output-document=".$tempDir."temp.html \"".$url."\"");


 // error
 if (!file_exists($tempDir."temp.html") )
	 return $success;




 // *******************************
 // 2. html2pdf    .html --> ps/pdf
 // *******************************
 require_once(_SHARED."html2pdf/HTML_ToPDF.php");


 // full path to the file to be converted
 $htmlFile = $tempDir.'temp.html';


 // the default domain for images that use a relative path
 // (you'll need to change the paths in the test.html page 
 // to an image on your server)
 $defaultDomain = 'www.mindcatch.at';


 // full path to the PDF we are creating
 $pdfFile = $tempDir.'temp.pdf';


 // remove old one, just to make sure we are making it afresh
 @unlink($pdfFile);

 
 // instnatiate the class with our variables
 // $pdf =& new HTML_ToPDF($htmlFile, $defaultDomain, $pdfFile);
 $pdf = new HTML_ToPDF($htmlFile, $defaultDomain, $pdfFile);

 // set headers/footers
 // $pdf->setHeader('color', 'blue');
 // $pdf->setFooter('left',  'Generated by HTML_ToPDF');
 // $pdf->setFooter('right', '$D');

 $result = $pdf->convert();				// returns filename of pdf in  basename($result) 

 // check if the result was an error
 if (PEAR::isError($result)) 
	{
     echo "<font color=red>".$result->getMessage()."</font>";
	 return $success;											// false
	}
 else 
	{
     $success = true; 
	}


 // echo "PDF file created successfully: $result";
 // echo "<br />Click <a href='"._SHARED_URL."html2pdf/temp/temp.pdf'>here</a> to view the PDF file.";

 return $success;

}


?>