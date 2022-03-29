#!/bin/bash

curl -v -X POST -H "Content-Type: application/json" -H "Authorization: Bearer AnonymousUser0001" \
    -d "{ \"countries\": [ \"us\", \"ru\" ], \"term\": \"kirill\" }" \
    http://localhost:8080/devstracker/v1/developers/search
