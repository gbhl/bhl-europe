#!/bin/bash
# *************************************************
# A. MEHRRATH PRE-INGEST JOB RUNNER [BASIC VERSION]
# W. KOLLER CODE FOR PARALLEL PROCESSING ADDED
# *************************************************
# RUN AND CLEANUP JOBS FROM _WORK_DIR (CONFIG.PHP)
# PARALLELISM THROUGH TIME DEPENDANT EXECUTION 
# DIRECTORY (SCIPTS MOVE TO).
# *************************************************

# ******
# CONFIG
# ******
rootDir="/mnt/nfs/dev/opt/pre-ingest/ingest"

# *********
# FUNCTIONS
# *********
# trap function for handling exiting childs
function childDone
{
    let processCount-=1
}

# ****
# INIT
# ****
# log file
now=`date +"%Y%m%d-%H%M"`
logDest="$rootDir/log/$now.log"

# redirect complete output to log file
exec &> $logDest 

# process counting
processCount=0
# maximum processes limited to number of CPUs
maxProcesses=`cat /proc/cpuinfo | grep processor | wc -l`

# working dir
sourceDir="$rootDir/temp/"


# queueing dirs
runDir="preparation_$now/"
execDir="$rootDir/queuing/running/$runDir"
archDir="$rootDir/queuing/archive/$runDir"

printf '\n*********************************************************************************************************\n'
printf "TRY TO GET AND EXECUTE SCRIPTS FROM $sourceDir IN $execDir.\n"
printf '*********************************************************************************************************\n'

# create execution & archive directory
mkdir $execDir
mkdir $archDir

# MOVE CP'S SCRIPTS TO INDIVIDUAL EXECUTION DIRECTORY (1 INVOKE ONLY)
find $sourceDir -maxdepth 2 -name '*.sh' -type f -exec mv {} $execDir ';'

# **********************************
# INVOKE SCRIPTS IN CURRENT $execDir
# optimized to allow parallel processing of all sh files within a single run
# **********************************
# Enable monitoring of childs
set -m
trap "childDone" SIGCHLD

# cycle through all scripts waiting to be executed
for file in ${execDir}*.sh
do
    # run script, once it is done move to archive dir
    bash $file && mv $file $archDir &
    let processCount+=1

    echo "Executing $file ($processCount / $maxProcesses)"

    # prevent spawning to many processes
    while [ $processCount -ge $maxProcesses ]; do
        wait
    done
done

# wait for all processes to finish
while [ $processCount -gt 0 ]; do
    wait
done

# disable monitoring of childs
trap - SIGCHLD

# ************************
# CLEAN EMPTY (30MIN) LOGS
# ************************
#find $rootDir/log/ -name '*.log' -type f -size 0    -exec rm -f {} ';'
#find $rootDir/log/ -name '*.log' -type f -mtime +31 -exec rm -f {} ';'
#find $archDir      -name '*.sh'  -type f -mtime +31 -exec rm -f {} ';'


# **************
# DROP ARTEFACTS
# **************
rm -f $execDir'*.*'
rmdir $execDir

# -- end --
printf '\n******************** END ********************\n\n'
