# ***********************************************
# A. MEHRRATH PRE-INGEST JOB RUNNER [BASIC]

# Run and cleanup jobs in _WORK_DIR (config.php)

# TODOS: umleitung der ausgabe in (err) logfile
# ***********************************************

# find <workdir> -name '*.sh' -maxdepth 1 -type f -exec sh {} ';'

# INVOKE 

find /var/www/ingest/temp -name '*.sh' -maxdepth 1 -type f -exec sh {} ';'

# CLEANUP

find /var/www/ingest/temp -name '*.sh' -maxdepth 1 -type f -exec rm -f {} ';'

