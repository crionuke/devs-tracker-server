#!/bin/bash

source ~/.profile
ssh devstracker@${DEVS_TRACKER_PROD_SERVER_02_IP} -p ${DEVS_TRACKER_PROD_SERVER_02_PORT} "source .profile; ./deploy.sh"
