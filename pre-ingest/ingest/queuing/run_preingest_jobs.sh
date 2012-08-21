#!/bin/sh
# *************************************************
# A. MEHRRATH PRE-INGEST JOB RUNNER [BASIC VERSION]
# W. KOLLER CODE FOR PARALLEL PROCESSING ADDED
# *************************************************
# RUN AND CLEANUP JOBS FROM _WORK_DIR (CONFIG.PHP)
# PARALLELISM THROUGH TIME DEPENDANT EXECUTION 
# DIRECTORY (SCIPTS MOVE TO).
# *************************************************
# TODOS: UMLEITUNG DER AUSGABE IN (ERR) LOGFILE
#        TESTING
# *************************************************
#
# CONFIG
rootDir="/mnt/nfs/dev/opt/pre-ingest/ingest"

# Enable monitor mode
set -m

# trap function for handling exiting childs
function childDone
{
    let processCount-=1
}

# Enable monitoring of childs
trap "childDone" SIGCHLD

# INIT
# process counting
processCount=0
# maximum processes limited to number of CPUs
maxProcesses=`cat /proc/cpuinfo | grep processor | wc -l`

# working dir
sourceDir="$rootDir/temp/"

# log file
now=`date +%Y_%m_%d_%H_%M_%S`
logDest="$rootDir/log/log_$now.log"

# queueing dirs
execDir="$rootDir/queuing/running/preparation_$now/"
archDir="$rootDir/queuing/archive/"

printf '\n*********************************************************************************************************\n'
printf "TRY TO GET AND EXECUTE SCRIPTS FROM $sourceDir IN $execDir.\n"
printf '*********************************************************************************************************\n'

# create execution directory
mkdir $execDir

# MOVE CP'S SCRIPTS TO INDIVIDUAL EXECUTION DIRECTORY (1 INVOKE ONLY)
find $sourceDir -maxdepth 2 -name '*.sh' -type f -exec mv {} $execDir ';'

# **********************************
# INVOKE SCRIPTS IN CURRENT $execDir
# optimized to allow parallel processing of all sh files within a single run
# **********************************
for file in ${execDir}*.sh
do
    sh $file &>> ${logDest} &
    let processCount+=1

    # prevent spawning to many processes
    while [ $processCount -ge $maxProcesses ]; do
        wait
    done
done

# wait for all processes to finish
while [ 1 ]; do
    wait
    if [ $? -eq 0 ]
    then
        break
    fi
done

# ARCHIVE & CLEANUP
find $execDir -maxdepth 1 -name '*.sh' -type f -exec mv {} $archDir ';'

# CLEAN EMPTY (30MIN) LOGS

find $rootDir/log/ -name '*.log' -type f -size 0    -exec rm -f {} ';'

find $rootDir/log/ -name '*.log' -type f -mtime +31 -exec rm -f {} ';'

find $archDir      -name '*.sh'  -type f -mtime +31 -exec rm -f {} ';'


# ETWAIGE ARTEFAKTE DROPPEN

rm -f $execDir'*.*'

rmdir $execDir

# -- end --

printf '\n******************** END ********************\n\n'

