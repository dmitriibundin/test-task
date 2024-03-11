#!/usr/bin/env bash

curl -X POST \
  -H "X-User-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "phoneId": 1
  }' \
  localhost:8080/reserve
