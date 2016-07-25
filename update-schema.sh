#!/bin/bash

export PGPASSWORD=postgres

psql -h localhost -p 38088 -U postgres -d local_zmon_db -c "DROP SCHEMA zzm_api CASCADE;"

find "database/zmon/20_api" -name '*.sql' \
                                   | sort \
                                   | xargs cat \
                                   | psql -h localhost -p 38088 -U postgres -d local_zmon_db
