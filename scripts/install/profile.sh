#!/bin/bash

echo "export DO_API_TOKEN=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_SERVER_PORT=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_SPRING_DATASOURCE_URL=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_SPRING_DATASOURCE_USERNAME=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_SPRING_DATASOURCE_PASSWORD=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_LOGGING_LEVEL_COM_CRIONUKE_DEVSTRACKER=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_VALIDATE_REQUESTS=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_FREE_TRACKERS_LIMIT=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_MAX_TRACKERS_LIMIT=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_API_FIREBASE_CREDENTIALS_JSON=" >> /home/devstracker/.profile
echo "export DEVS_TRACKER_API_REVENUE_CAT_SECRET_KEY=" >> /home/devstracker/.profile
