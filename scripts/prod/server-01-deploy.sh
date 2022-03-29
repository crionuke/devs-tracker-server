#!/bin/bash

source ~/.profile
ssh devstracker@${DEVS_TRACKER_PROD_SERVER_01_IP} -p ${DEVS_TRACKER_PROD_SERVER_01_PORT} "source .profile; ./deploy.sh"
