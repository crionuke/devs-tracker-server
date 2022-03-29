#!/bin/bash

source ~/.profile
ssh root@${DEVS_TRACKER_PROD_DB_01_IP} -p ${DEVS_TRACKER_PROD_DB_01_PORT}
