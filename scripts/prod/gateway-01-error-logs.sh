#!/bin/bash

source ~/.profile
ssh root@${DEVS_TRACKER_PROD_GATEWAY_01_IP} -p ${DEVS_TRACKER_PROD_GATEWAY_01_PORT} "tail -f /var/log/nginx/error.log"
