#!/bin/bash

set -e

ERR_MSG=''

# 로그 출력
log() {
  local MSG="[$(date '+%Y-%m-%d %H:%M:%S')] $1"
  echo "$MSG"
}

log "===== Deployment script started ====="

trap 'echo "Error occurred: $ERR_MSG. Exiting deploy script."; exit 1' ERR

if sudo docker ps --filter "name=app-blue" --quiet | grep -E .; then
  echo "Blue down, Green Up "
  BEFORE_COMPOSE_COLOR="blue"
  AFTER_COMPOSE_COLOR="green"
else
  echo "Green down, Blue up"
  BEFORE_COMPOSE_COLOR="green"
  AFTER_COMPOSE_COLOR="blue"
fi

# 새로운 image pull 받기
docker compose -f docker-compose.yaml pull "app-${AFTER_COMPOSE_COLOR}" \
  || { echo "pull new image failed"; exit 1; }

# 새로운 서비스만 시작
docker compose -f docker-compose.yaml up -d --no-deps "app-${AFTER_COMPOSE_COLOR}" \
  || { echo "bring up new service failed"; exit 1; }

sleep 10
echo "Switched from $BEFORE_COMPOSE_COLOR to $AFTER_COMPOSE_COLOR."

# 새로운 컨테이너가 제대로 떴는지 확인
if docker ps --filter "name=app-${AFTER_COMPOSE_COLOR}" --quiet | grep -q .; then
    # reload nginx
    NGINX_ID=$(sudo docker ps --filter "name=nginx" --quiet)
    NGINX_CONFIG="/home/ubuntu/deploy/nginx.conf"

    echo "Switching Nginx upstream config..."
    if ! sed -i "s/app-${BEFORE_COMPOSE_COLOR}:8080/app-${AFTER_COMPOSE_COLOR}:8080/" "$NGINX_CONFIG"; then
          echo "Error occurred: Failed to update Nginx config. Exiting deploy script."
          exit 1
    fi

    echo "Reloading Nginx in Container"
    if ! docker exec "$NGINX_ID" nginx -s reload; then
      ERR_MSG='Failed to reload Nginx'
      exit 1
    fi

  # 이전 컨테이너 종료
  log "Stopping old stack ($BEFORE_COMPOSE_COLOR)..."
  docker stop "app-${BEFORE_COMPOSE_COLOR}" || true
else
  log "New container is NOT running; keeping previous stack. Inspect logs."
  exit 1
fi

ERR_MSG="image prune failed"
docker image prune -af || true

log "Deployment completed successfully."
log "===== Deployment script ended ====="

exit 0