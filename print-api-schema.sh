#!/bin/bash

echo "CREATE SCHEMA zzm_api_os_01;"
echo "SET search_path TO zzm_api_os_01, zzm_data, public;"

find "database/zmon/20_api" -name '*.sql' \
                                   | sort \
                                   | xargs cat
