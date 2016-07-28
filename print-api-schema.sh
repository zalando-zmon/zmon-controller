#!/bin/bash

find "database/zmon/20_api" -name '*.sql' \
                                   | sort \
                                   | xargs cat
