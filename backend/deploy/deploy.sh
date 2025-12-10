#!/bin/bash

set -e

COMPOSE_FILE="/home/ubuntu/backend/docker/docker-compose.dev.yaml"
NGINX_CONFIG="/home/ubuntu/backend/deploy/nginx.conf"
MAX_RETRY=30
RETRY_INTERVAL=2

ERR_MSG=''

# 로그 출력
log() {
  local MSG="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$MSG"
}

cleanup() {
  local exit_code=$?
  if [[ $exit_code -ne 0 ]]; then
    log "ERROR: 배포 중 오류 발생 (exit code: $exit_code)"
  fi
}

trap cleanup EXIT
trap 'echo "Error occurred: $ERR_MSG"; exit 1' ERR

log "===== Deployment script started ====="

if docker ps --filter "name=app-blue" --quiet | grep -E .; then
  echo "Blue down, Green Up "
  BEFORE_COMPOSE_COLOR="blue"
  AFTER_COMPOSE_COLOR="green"
else
  echo "Green down, Blue up"
  BEFORE_COMPOSE_COLOR="green"
  AFTER_COMPOSE_COLOR="blue"
fi

# 새로운 image pull 받기
ERR_MSG="이미지 pull 중 오류 발생"
docker compose -f "$COMPOSE_FILE" pull "app-${AFTER_COMPOSE_COLOR}" \
  || { echo "pull new image failed"; exit 1; }

# 새로운 서비스만 시작
ERR_MSG="새로운 서비스(app-${AFTER_COMPOSE_COLOR}) 시작 실패"
docker compose -f "$COMPOSE_FILE" up -d --no-deps "app-${AFTER_COMPOSE_COLOR}" \
  || { echo "bring up new service failed"; exit 1; }

# Running Check
ERR_MSG="Running Check 실패"
log "Waiting for app-${AFTER_COMPOSE_COLOR} to be healthy..."
for ((i=1; i<=MAX_RETRY; i++)); do
  if docker ps --filter "name=app-${AFTER_COMPOSE_COLOR}" --filter "status=running" --quiet | grep -q .; then
      log "Container is running (attempt $i/$MAX_RETRY)"
      break
  fi

  if [[ $i -eq $MAX_RETRY ]]; then
    log "ERROR: Health check failed after $MAX_RETRY attempts"
    exit 1
  fi
  sleep $RETRY_INTERVAL
done

# Nginx Reload
ERR_MSG="Nginx Container 조회 실패"
NGINX_ID=$(docker ps --filter "name=nginx" --quiet)
if [[ -z "$NGINX_ID" ]]; then
  log "ERROR: Nginx container not found"
  exit 1
fi

log "Updating Nginx upstream config..."
ERR_MSG="Nginx 설정 업데이트 실패"
sed -i "s/app-${BEFORE_COMPOSE_COLOR}:8080/app-${AFTER_COMPOSE_COLOR}:8080/" "$NGINX_CONFIG"

log "Reloading Nginx..."
ERR_MSG="Nginx reload 실패"
docker exec "$NGINX_ID" nginx -s reload

# 이전 컨테이너 종료
log "Stopping old container (app-${BEFORE_COMPOSE_COLOR})..."
ERR_MSG="이전 컨테이너 종료 실패"
docker compose -f "$COMPOSE_FILE" stop "app-${BEFORE_COMPOSE_COLOR}" || true

# 이미지 정리
log "Cleaning up old images..."
ERR_MSG="docker image 정리 실패"
docker image prune -af || true

log "Deployment completed successfully."
log "===== Deployment script ended ====="

exit 0