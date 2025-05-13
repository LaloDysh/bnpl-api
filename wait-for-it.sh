TIMEOUT=60
QUIET=0
HOST=""
PORT=""
VERBOSE=0

POSTGRES_USER=${POSTGRES_USER}
POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
POSTGRES_DB=${POSTGRES_DB}

timestamp() {
  date +"%Y-%m-%d %H:%M:%S"
}

log() {
  if [ "$QUIET" -eq 0 ]; then
    echo "$(timestamp) $*"
  fi
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

check_connection() {
  if command_exists nc; then
    if [ "$VERBOSE" -eq 1 ]; then
      log "Trying connection with nc -z $HOST $PORT"
    fi
    nc -z "$HOST" "$PORT" > /dev/null 2>&1
    return $?
  elif command_exists telnet; then
    if [ "$VERBOSE" -eq 1 ]; then
      log "Trying connection with telnet $HOST $PORT"
    fi
    echo quit | telnet "$HOST" "$PORT" > /dev/null 2>&1
    return $?
  else
    if [ "$VERBOSE" -eq 1 ]; then
      log "Trying connection with /dev/tcp/$HOST/$PORT"
    fi
    (echo > "/dev/tcp/$HOST/$PORT") >/dev/null 2>&1
    return $?
  fi
}

wait_for() {
  log "Waiting for PostgreSQL to be ready at $HOST:$PORT..."
  count=1
  start_ts=$(date +%s)
  
  while true; do
    if check_connection; then
      end_ts=$(date +%s)
      diff=$((end_ts - start_ts))
      log "PostgreSQL is available at $HOST:$PORT after $diff seconds!"
      sleep 2
      
      if command_exists psql; then
        log "Testing PostgreSQL connection with psql..."
        if PGPASSWORD=$POSTGRES_PASSWORD psql -h "$HOST" -U "$POSTGRES_USER" -d "$POSTGRES_DB" -c "SELECT 1" > /dev/null 2>&1; then
          log "PostgreSQL connection established successfully"
        else
          log "Warning! Can't connect with psql yet, but port is open"
        fi
      fi
      
      if [ $# -gt 0 ]; then
        log "Executing command: $*"
        exec "$@"
      fi
      return 0
    fi
    
    end_ts=$(date +%s)
    diff=$((end_ts - start_ts))
    
    if [ $diff -gt $TIMEOUT ]; then
      log "Timeout after waiting $TIMEOUT seconds for $HOST:$PORT"
      log "Diagnostic information:"
      
      if command_exists ping; then
        log "Trying to ping $HOST..."
        ping -c 1 "$HOST"
      fi
      
      log "Docker network status:"
      if command_exists docker; then
        docker network ls
        docker ps
      fi
      
      return 1
    fi
    
    log "Waiting for PostgreSQL... ($count/$TIMEOUT)"
    count=$((count+1))
    sleep 1
  done
}

while [ $# -gt 0 ]; do
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
    -v)
      VERBOSE=1
      shift 1
      ;;
    -u)
      POSTGRES_USER="$2"
      shift 2
      ;;
    -p)
      POSTGRES_PASSWORD="$2"
      shift 2
      ;;
    -d)
      POSTGRES_DB="$2"
      shift 2
      ;;
    -- )
      shift
      break
      ;;
    *)
      break
      ;;
  esac
done

if [ "$HOST" = "" -o "$PORT" = "" ]; then
  log "Error: you must provide a host and port to test (host:port)"
  exit 1
fi

if [ "$VERBOSE" -eq 1 ]; then
  log "Container network information:"
  ip addr show 2>/dev/null || true
fi

wait_for "$@"