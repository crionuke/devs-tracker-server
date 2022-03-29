#!/bin/bash
set -e

doctl registry login
docker build -t registry.digitalocean.com/crionuke/devs-tracker/devs-tracker-server:1.0.0 .
docker push registry.digitalocean.com/crionuke/devs-tracker/devs-tracker-server:1.0.0

echo Finished!