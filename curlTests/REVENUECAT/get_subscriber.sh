#!/bin/bash

curl --request GET \
  --url "https://api.revenuecat.com/v1/subscribers/${REVENUE_CAT_USER}" \
  --header "Authorization: Bearer ${API_REVENUE_CAT_SECRET_KEY}" \
  --header 'Content-Type: application/json' \
  --header 'X-Platform: ios'