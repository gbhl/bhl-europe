#!/bin/bash
. $HOME/.profile
#
# small script which checks if access is still processing content, if not it restarts it
#

#
# CONFIG
#
ACCESS_URL=
ACCESS_USER=
ACCESS_PASSWORD=
ACCESS_PATH=
# OPTIONAL CONFIG
FORCE_TIMEOUT=240

# fetch the status
ACCESS_STATUS=`curl ${ACCESS_URL} -u ${ACCESS_USER}:${ACCESS_PASSWORD} | grep 'lastJobExecutionStatus' | grep -i 'start'`

# check if jobs are running
if [ -z "${ACCESS_STATUS}" ]
then
    echo "No more jobs running - restarting access!"

    # stop access component (force it after timeout)
    ${ACCESS_PATH}/bin/catalina.sh stop ${FORCE_TIMEOUT} -force

    # start access again
    ${ACCESS_PATH}/bin/catalina.sh start
fi
