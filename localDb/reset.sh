#!/bin/bash

./kill.sh
rm -rf devs-tracker-db
sleep 2
./up.sh
docker logs devs-tracker-db