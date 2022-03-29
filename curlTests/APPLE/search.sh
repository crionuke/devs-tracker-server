#!/bin/bash

curl -X GET "https://itunes.apple.com/search?term=kirill+byvshev&country=us&media=software&entity=software&attribute=softwareDeveloper&limit=200" | jq
