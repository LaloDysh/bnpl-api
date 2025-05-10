#!/bin/sh
# wait-for-it.sh: Script mejorado para esperar a que un servicio esté disponible

TIMEOUT=60
QUIET=0
HOST=""
PORT=""
VERBOSE=0

timestamp() {
  date +"%Y-%m-%d %H:%M:%S"
}

log() {
  echo "$(timestamp) $*"
}

command_exists() {
  command -v "$1" >/dev/null 2>&1
}

check_connection() {
  if command_exists nc; then
    if [ "$VERBOSE" -eq 1 ]; then
      log "Intentando conexión con nc -z $HOST $PORT"
    fi
    nc -z "$HOST" "$PORT" > /dev/null 2>&1
    return $?
  elif command_exists telnet; then
    if [ "$VERBOSE" -eq 1 ]; then
      log "Intentando conexión con telnet $HOST $PORT"
    fi
    echo quit | telnet "$HOST" "$PORT" > /dev/null 2>&1
    return $?
  else
    if [ "$VERBOSE" -eq 1 ]; then
      log "Intentando conexión con /dev/tcp/$HOST/$PORT"
    fi
    (echo > "/dev/tcp/$HOST/$PORT") >/dev/null 2>&1
    return $?
  fi
}

wait_for() {
  log "Esperando a que PostgreSQL esté listo en $HOST:$PORT..."
  
  count=1
  start_ts=$(date +%s)
  
  while true; do
    if check_connection; then
      end_ts=$(date +%s)
      diff=$(( end_ts - start_ts ))
      log "¡PostgreSQL está disponible en $HOST:$PORT después de $diff segundos!"
      
      # Espera adicional de 2 segundos para asegurar que PostgreSQL esté completamente listo
      sleep 2
      
      # Prueba si PostgreSQL realmente está listo para aceptar conexiones
      if command_exists psql; then
        log "Probando conexión a PostgreSQL con psql..."
        if PGPASSWORD=$POSTGRES_PASSWORD psql -h $HOST -U $POSTGRES_USER -d $POSTGRES_DB -c "SELECT 1" > /dev/null 2>&1; then
          log "Conexión a PostgreSQL establecida correctamente"
        else
          log "¡Alerta! Todavía no se puede conectar con psql, pero el puerto está abierto"
        fi
      fi
      
      if [ $# -gt 0 ]; then
        log "Ejecutando comando: $*"
        exec "$@"
      fi
      return 0
    fi
    
    end_ts=$(date +%s)
    diff=$(( end_ts - start_ts ))
    
    if [ $diff -gt $TIMEOUT ]; then
      log "Timeout después de esperar $TIMEOUT segundos por $HOST:$PORT"
      # Información adicional para diagnóstico
      log "Información de diagnóstico:"
      if command_exists ping; then
        log "Intentando ping a $HOST..."
        ping -c 1 $HOST
      fi
      log "Estado de la red Docker:"
      if command_exists docker; then
        docker network ls
        docker ps
      fi
      return 1
    fi
    
    log "Esperando a PostgreSQL... ($count/$TIMEOUT)"
    count=$((count+1))
    sleep 1
  done
}

# Procesar argumentos
POSTGRES_USER="postgres"
POSTGRES_PASSWORD="postgres"
POSTGRES_DB="bnpl"

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
  log "Error: debes proporcionar un host y puerto para probar (host:puerto)"
  exit 1
fi

export POSTGRES_USER
export POSTGRES_PASSWORD
export POSTGRES_DB

# Mostrar información de red para diagnóstico
log "Información de red del contenedor:"
ip addr show

wait_for "$@"