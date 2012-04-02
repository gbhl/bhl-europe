<?php
// ********************************************
// ** FILE:    SERIAL_KERNEL.PHP             **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    27.03.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************
/*
 * 
 * Using the Pre-Ingest tool is should be possible to parse & process structured data (into an AIP).

Structured content refers to periodicals like serials. (but not only).
A detailed description of how the content is structured can be found in the 
 * file submission guidelines which are available here: https://bhl.wikispaces.com/file/view/BHL-Europe_File+Submission+Guidelines.pdf

Relations between Serial / Section / Volume / Article level should be 
 * maintained using both the OLEF parentGUID field as well as the RELS-EXT description worked out by Zheng.
Each level of information (Serial / Section / etc.) has to contain one metadata 
 * file which will be converted to OLEF and then be included into the METS file.

The output for periodicals of the Pre-Ingest tool should be, like for monographs, an AIP package which can be ingested into fedora.

 * content contains more than one level of information.
**********************************
They are:
- Serial
- Section
- Volume
- Article

For a detailed explanation on how to distinguish between the levels please see the File 
 * Submission Guideline which is available on the wiki: https://bhl.wikispaces.com/file/view/BHL-Europe_File+Submission+Guidelines.pdf


Maintain relations between serial levels
 * **********************************
mehrrath is assigned 
Assign someone to this issue  Clear assignee  mehrrath mehrrath   akohlbecker Andreas 

 *Relations between each serial level should be maintained using the parentGUID within the 
 * OLEF data as well as the RELS-EXT definition in the final METS output.

OLEF see: http://www.bhl-europe.eu/bhl-schema/v0.3/
RELS-EXT see: http://groups.google.com/group/bhle-tech/browse_thread/thread/39c896271f0cbef4/cd4bb048a0950bc1?hl=de&lnk=gst&q=rels


Sorting of articles / chapters
**********************************
Milestone: PI Final   
MilestoneFilter milestones  OpenClosed PI FinalCurrent Due in about 20 hours


EditSorting of articles and chapters should be kept according to the specification found in the file submission guidelines. Therefor ordering is done using directory names for articles.

Keep in mind that in case of "volumes" and "monographs with chapters" ordering might include files on the current folder and files from the sub-folder.

This is all well defined (including examples) in the file submission guideline: 
https://bhl.wikispaces.com/file/view/BHL-Europe_File+Submission+Guidelines.pdf

 * For defining the content model (which is relevant for the final METS output) see issue #385

* 
*/

$arrIgnoreDirs = array('.', '..','.aip','.AIP');


if (_MODE=='production') 
progressBar("Please wait operation in progress...", "processing.gif","margin-top: 75px; left: 350px;", "visible", 2);




// SERIAL LEVELS ANALYSIEREN

$arrSerialLevels = array("Serial","Section","Volume","Article");
$nSerialLevels   = count($arrSerialLevels);

$arrAnalyzedDirs = array();

$nMarked = 0;
$nSent   = 0;


$sLevel  = 0;

include("serial_kernel_step.php");

$L1Directory = getDirectory($contentDir, "", 0, $arrIgnoreDirs, 0);     // serial root
$L1n         = count($L1Directory);

for ($l1=0;$l1<$L1n;$l1++)
{
    $sLevel = 1;
    
    if (is_dir($L1Directory[$l1]))      // FUER JEDE section 
    {
        include("serial_kernel_step.php");
        
        $L2Directory = getDirectory($L1Directory[$l1], "", 0, $arrIgnoreDirs, 0); 
        $L2n         = count($L2Directory);

        for ($l2=0;$l2<$L2n;$l2++)
        {
            $sLevel = 2;

            if (is_dir($L2Directory[$l2]))  // FUER JEDES volume 
            {
                include("serial_kernel_step.php");

                $L3Directory = getDirectory($L2Directory[$l2], "", 0, $arrIgnoreDirs, 0); 
                $L3n         = count($L3Directory);

                for ($l3=0;$l3<$L3n;$l3++)
                {
                    $sLevel      = 3;

                    if (is_dir($L3Directory[$l3]))      // FUER JEDEN artikel
                    {
                        include("serial_kernel_step.php");
           
                    }
                }
            }
        }
    }
}

if (_MODE=='production') close_progressBar();




// ** GET_METS POST NOTES **

if ($nMarked > 0)   $endmsg .= $nMarked." AIP(s) marked as ready for ingest for Fedora. ";
if ($nSent   > 0)   $endmsg .= $nSent." ActiveMQ ingest message(s) sent.";


?>
