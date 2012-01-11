<?php
// ********************************************
// ** FILE:    SELFTEST.PHP                  **
// ** PURPOSE: BHLE INGESTION & PREPARATION  **
// ** DATE:    05.11.2011                    **
// ** AUTHOR:  ANDREAS MEHRRATH              **
// ********************************************




echo " *** "._APP_NAME." Selftest START *** ok\n";


// DATABASE TEST

if (is_numeric(abfrage("select count(*) from users")))  		"Database Test ok.\n";
else									"Database Setup incorrect.\n";


// FILESYSTEM TESTS

@mkdir(_WORK_DIR);

if (is_writable(_WORK_DIR)) "Working Directory exists and is writeable.\n";
else                        "Working Directory not writeable or create fails.\n";


echo " *** "._APP_NAME." Selftest END *** ok\n";


?>