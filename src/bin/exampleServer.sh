#!/bin/sh
#
if [ -z "$ZABBIXJ_HOME" -o ! -d "$ZABBIXJ_HOME" ] ; then
  ## resolve links - $0 may be a link to webc's home
  PRG="$0"
  progname=`basename "$0"`

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
    else
    PRG=`dirname "$PRG"`"/$link"
    fi
  done

  ZABBIXJ_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  ZABBIXJ_HOME=`cd "$ZABBIXJ_HOME" && pwd`
fi

ZABBIXJ_LIB="${ZABBIXJ_HOME}/lib"

CLASSPATH=${ZABBIXJ_HOME}/dist/quigley-zabbixj-1.0.0.jar
CLASSPATH=${CLASSPATH}:${ZABBIXJ_LIB}/slf4j-1.5.6/slf4j-api-1.5.6.jar
CLASSPATH=${CLASSPATH}:${ZABBIXJ_LIB}/slf4j-1.5.6/slf4j-log4j12-1.5.6.jar
CLASSPATH=${CLASSPATH}:${ZABBIXJ_LIB}/log4j-1.2.15/log4j-1.2.15.jar
CLASSPATH=${CLASSPATH}:${ZABBIXJ_HOME}/dist/

java -cp ${CLASSPATH} com.quigley.zabbixj.example.ExampleServer $*