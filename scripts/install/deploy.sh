#!/bin/bash

PRIVATE_IP=$(ip route | grep eth1 | awk '{print $9}')

docker login -u $DO_API_TOKEN -p $DO_API_TOKEN registry.digitalocean.com
docker pull registry.digitalocean.com/crionuke/devs-tracker/devs-tracker-server:1.0.0
docker kill devs-tracker-server
docker rm devs-tracker-server
cat ~/.profile
docker run --name devs-tracker-server -d --restart=unless-stopped \
    -p ${PRIVATE_IP}:${DEVS_TRACKER_SERVER_PORT}:${DEVS_TRACKER_SERVER_PORT} \
    -e "SERVER_PORT=${DEVS_TRACKER_SERVER_PORT}" \
    -e "SPRING_DATASOURCE_URL=${DEVS_TRACKER_SPRING_DATASOURCE_URL}" \
    -e "SPRING_DATASOURCE_USERNAME=${DEVS_TRACKER_SPRING_DATASOURCE_USERNAME}" \
    -e "SPRING_DATASOURCE_PASSWORD=${DEVS_TRACKER_SPRING_DATASOURCE_PASSWORD}" \
    -e "SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=${DEVS_TRACKER_SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE}" \
    -e "LOGGING_LEVEL_COM_CRIONUKE_DEVSTRACKER=${DEVS_TRACKER_LOGGING_LEVEL_COM_CRIONUKE_DEVSTRACKER}" \
    -e "DEVS_TRACKER_VALIDATE_REQUESTS=${DEVS_TRACKER_VALIDATE_REQUESTS}" \
    -e "DEVS_TRACKER_FREE_TRACKERS_LIMIT=${DEVS_TRACKER_FREE_TRACKERS_LIMIT}" \
    -e "DEVS_TRACKER_MAX_TRACKERS_LIMIT=${DEVS_TRACKER_MAX_TRACKERS_LIMIT}" \
    -e "API_FIREBASE_CREDENTIALS_JSON=${DEVS_TRACKER_API_FIREBASE_CREDENTIALS_JSON}" \
    -e "API_REVENUE_CAT_SECRET_KEY=${DEVS_TRACKER_API_REVENUE_CAT_SECRET_KEY}" \
    registry.digitalocean.com/crionuke/devs-tracker/devs-tracker-server:1.0.0
docker ps
docker logs -f devs-tracker-server