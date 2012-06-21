<?php
// ********************************************
// ** FILE:    TESSERACT_CONFIG.PHP          **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    15.01.2012                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************

// SMALL TESSERACT STEERING

// *****************************************
// ADMINISTER LANGUAGE SUPPORT MAPPINGS HERE
// *****************************************

// 1. INSTALL LANGUAGE SUPPORT FILE IN TESSDATA DIRECTORY
// 2. ADD MAPPING 

// IMPORTANT:
// LANGUAGES WHICH ARE NOT MAPPED HERE ARE NOT AVAILABLE FOR THE OCR PROCESS.
// TERMS ARE CASE INSENSITIVE KEEP ALL LOWERCASE.
// TERMS START AND END WITH A COMMA ,

// FORMAT:
//  'XXX'=>'MT1,MT2,MT3,....',

// WHERE:  XXX ........... TESSERACT LANGUAGE ABREVIATION PARAMETER, A SHORT LANG ABREVIATION
//         MT1,...,MTn ... (METADATA) LANGUAGE TERMS WHICH SHOULD MAP TO OCR LANGUAGE XXX

$tesseractMappings = array (
    'eng'=>',english,us,uk,kingdom,common,england,engl,eng,en,',
    'deu'=>',deutsch,german,de,deu,deut,ge,germ,ger',
    'fra'=>',france,francaise,frenc,french,franz,fr,fra,fran,fre',
    'dan'=>',danish,dan,dk,',
    'spa'=>',spanish,espaniol,esp,es,span,espan,',
	'fin'=>',finnish,fin,fi,',
    'hun'=>',hungarian,hun,hu,',
    'ces'=>',czech,cz,ceska,ces',
    'por'=>',portuguese,por,pt,',
    'pol'=>',polish,pol,pl,',
    'ita'=>',italian,ita,it,',
    'nld'=>',dutch,dut,nl,nld,'
);


// DEFAULT LANGUAGE TO USE FOR ALL NOT MAPPED FOREIGN LANGUAGES


if (!defined("_TESSERACT_DEAULT_LANG")) define("_TESSERACT_DEAULT_LANG","eng");


?>
