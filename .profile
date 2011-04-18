# /etc/profile: system-wide .profile file for the Bourne shell (sh(1))
# and Bourne compatible shells (bash(1), ksh(1), ash(1), ...).

if [ "`id -u`" -eq 0 ]; then
  PATH="/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
else
  PATH="/usr/local/bin:/usr/bin:/bin:/usr/games"
fi

if [ "$PS1" ]; then
  if [ "$BASH" ]; then
    PS1='\u@\h:\w\$ '
  else
    if [ "`id -u`" -eq 0 ]; then
      PS1='# '
    else
      PS1='$ '
    fi
  fi
fi

export PATH

umask 022

export M2_HOME=/opt/apache-maven-3.0.3
export ANT_HOME=/opt/apache-ant-1.8.1
##export JAVA_HOME=/usr/lib/jvm/java-6-openjdk
export JAVA_HOME=/mnt/nfs-demeter/dev/jdk1.6.0_24
export FEDORA_HOME=/mnt/nfs-demeter/dev/opt/archivalStorage/fedora
export SOLR_HOME=/mnt/nfs-demeter/dev/solr

export KAKADU_HOME=/mnt/nfs-demeter/dev/opt/portal/adore-djatoka-1.1/bin/Linux-x86-64
export DJAKOTA_HOME=/mnt/nfs-demeter/dev/opt/portal/adore-djatoka-1.1

export JAVA_OPTS="-Djava.library.path=/usr/lib -Djava.awt.headless=true -Dfile.encoding=UTF-8 -server -Xms1536m -Xmx1536m -XX:NewSize=256m -XX:MaxNewSize=256m -XX:PermSize=256m -XX:MaxPermSize=256m -XX:+DisableExplicitGC -Djavax.net.ssl.trustStore=$FEDORA_HOME/server/truststore  -Djavax.net.ssl.trustStorePassword=tomcat -Dsolr.data.dir=/mnt/nfs-demeter/dev/data/solr -Dkakadu.home=$KAKADU_HOME"


export PATH=$JAVA_HOME/bin:$PATH:$FEDORA_HOME/server/bin:$FEDORA_HOME/client/bin:$ANT_HOME/bin:$KAKADU_HOME:$M2_HOME/bin

# added by sprogerb@ait.co.at
alias ll='ls -al'
alias ls='ls --color=yes'
