#!/bin/sh
# *************************************************
# A. MEHRRATH PRE-INGEST JOB RUNNER [BASIC VERSION]
# *************************************************
# RUN AND CLEANUP JOBS FROM _WORK_DIR (CONFIG.PHP)
# PARALLELISM THROUGH TIME DEPENDANT EXECUTION 
# DIRECTORY (SCIPTS MOVE TO).
# *************************************************
# TODOS: UMLEITUNG DER AUSGABE IN (ERR) LOGFILE
#        TESTING
# *************************************************
#
# */30 * * * * sh /var/www/ingest/queuing/run_preingest_jobs.sh  > /dev/null 2>&1
#
# find <workdir> -maxdepth 1 -name '*.sh' -type f -exec sh {} ';'

# INITS

now=`date +%Y_%m_%d_%H_%M_%S`

rootDir="/var/www/ingest"

# _WORK_DIR (CONFIG.PHP)
sourceDir="$rootDir/temp/"

# LOG
logDest="$rootDir/log/log_$now.log"

execDir="$rootDir/queuing/running/preparation_$now/"
archDir="$rootDir/queuing/archive/"

printf '\n*********************************************************************************************************\n'
printf "TRY TO GET AND EXECUTE SCRIPTS FROM $sourceDir IN $execDir.\n"
printf '*********************************************************************************************************\n'

mkdir $execDir

# MOVE CP'S SCRIPTS TO INDIVIDUAL EXECUTION DIRECTORY (1 INVOKE ONLY)

find $sourceDir -maxdepth 2 -name '*.sh' -type f -exec mv {} $execDir ';'


# **********************************
# INVOKE SCRIPTS IN CURRENT $execDir
# **********************************
find $execDir -maxdepth 1 -name '*.sh' -type f -exec sh {} ';' > $logDest


# ARCHIVE & CLEANUP

find $execDir -maxdepth 1 -name '*.sh' -type f -exec mv {} $archDir ';'

# CLEAN EMPTY (30MIN) LOGS

find $rootDir/log/ -name '*.log' -type f -size 0 -exec rm -f {} ';'

# ETWAIGE ARTEFAKTE DROPPEN

rm -f $execDir'*.*'

rmdir $execDir

# -- end --

printf '\n******************** END ********************\n\n'

