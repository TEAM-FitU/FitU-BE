#!/bin/bash
set -e

DOMAIN="fitu-backend.duckdns.org"
EMAIL="yhsdeveloper8746@gmail.com"

echo "====== HTTPS 및 DuckDNS 설정 스크립트 시작 ======"

# EB 환경 변수에서 토큰 가져옴
TOKEN=$(/opt/elasticbeanstalk/bin/get-config environment -k DUCKDNS_TOKEN)

if [ -z "$TOKEN" ]; then
    echo "오류: DUCKDNS_TOKEN 환경 변수를 찾을 수 없습니다."
    exit 1
fi

# DuckDNS IP 업데이트
echo ">>> DuckDNS IP 업데이트 시도..."
curl -s "https://www.duckdns.org/update?domains=${DOMAIN%%.duckdns.org*}&token=${TOKEN}&ip="
echo ""

# DNS 전파 확인을 위해 잠시 대기
echo ">>> DNS 전파를 위해 30초 대기..."
sleep 60

if [ ! -d "/etc/letsencrypt/live/$DOMAIN" ]; then
  echo ">>> Let's Encrypt 인증서 새로 발급 시도..."
  certbot --nginx -d $DOMAIN --non-interactive --agree-tos --email $EMAIL --redirect
else
  echo ">>> Let's Encrypt 인증서 갱신 시도..."
  certbot renew --quiet
fi

echo ">>> Cron 작업 설정..."
echo "*/5 * * * * root curl -s \"https://www.duckdns.org/update?domains=${DOMAIN%%.duckdns.org*}&token=${TOKEN}&ip=\"" > /etc/cron.d/duckdns-update
echo "0 1,13 * * * root certbot renew --quiet" > /etc/cron.d/certbot-renew

echo "====== 스크립트 성공적으로 완료 ======"