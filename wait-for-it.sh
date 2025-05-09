#!/bin/sh
# wait-for-it.sh: A simple script to wait for a service to be available on a host:port
# Usage: ./wait-for-it.sh host:port [-t timeout] [-- command args]

TIMEOUT=15
QUIET=0

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

echoerr() {
  if [ "$QUIET" -ne 1 ]; then echo "$@" 1>&2; fi
}

usage() {
  cat << USAGE >&2
Usage:
  $0 host:port [-t timeout] [-- command args]
  -t TIMEOUT     Timeout in seconds (default: 15)
  -q             Quiet mode
  -- COMMAND     Command with args to run after the wait
USAGE
  exit 1
}

wait_for() {
  start_ts=$(date +%s)
  while :
  do
    if command_exists nc; then
      nc -z "$HOST" "$PORT" > /dev/null 2>&1
    else
      (echo > /dev/tcp/"$HOST"/"$PORT") >/dev/null 2>&1
    fi
    result=$?
    
    if [ $result -eq 0 ] ; then
      if [ $# -gt 0 ] ; then
        exec "$@"
      fi
      exit 0
    fi
    
    end_ts=$(date +%s)
    diff=$(( end_ts - start_ts ))
    
    if [ $diff -gt "$TIMEOUT" ]; then
      echoerr "$0: Timeout after waiting $TIMEOUT seconds for $HOST:$PORT"
      exit 1
    fi
    
    sleep 1
  done
}

while [ $# -gt 0 ]
do
  case "$1" in
    *:* )
    HOST=$(echo "$1" | cut -d : -f 1)
    PORT=$(echo "$1" | cut -d : -f 2)
    shift 1
    ;;
    -t)
    TIMEOUT="$2"
    shift 2
    ;;
    -q)
    QUIET=1
    shift 1
    ;;
    --)
    shift
    break
    ;;
    --help)
    usage
    ;;
    *)
    echoerr "Unknown argument: $1"
    usage
    ;;
  esac
done

if [ "$HOST" = "" -o "$PORT" = "" ]; then
  echoerr "Error: you need to provide a host and port to test."
  usage
fi

wait_for "$@"
