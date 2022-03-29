#!/bin/bash
set -e

VALUES=$1

if [ -z "$VALUES" ]; then
  echo "USAGE: $0 <values>"
  exit 1
fi

helm upgrade devs-tracker ./chart \
  --install \
  --create-namespace \
  --namespace devs-tracker \
  -f values/$VALUES.yaml