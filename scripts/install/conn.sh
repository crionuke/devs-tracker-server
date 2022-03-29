#!/bin/bash

sudo -u postgres PGPASSWORD=$PGPASSWORD psql -U devstracker -h $(ip route | grep eth1 | awk '{print $9}')
