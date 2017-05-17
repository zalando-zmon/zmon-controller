#!/bin/bash

if [ ! -f zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar ]; then
	./mvnw clean install -DskipTests
fi

echo 'Make sure the provided Vagrant-Box is up and all services are running.'

export ZMON_JWT_SECRET=28PI9q068f2qCbT38hnGX279Wei5YU5n
export SPRING_PROFILES_ACTIVE=github     # use GitHub auth
export SERVER_PORT=8444
export ZMON_OAUTH2_SSO_CLIENT_ID=3a115b9b22f5e5d59c60
export ZMON_OAUTH2_SSO_CLIENT_SECRET=4935052ba99afedbb7d6bc186715742db904e393
export ZMON_AUTHORITIES_SIMPLE_ADMINS=*  # everybody is admin!
export REDIS_PORT=38086                  # use Redis in Vagrant box
export POSTGRES_PASSWORD="Uhaf5AekUhaf5Aek"
export POSTGRES_URL=jdbc:postgresql://localhost:38088/local_zmon_db
export ZMON_SCHEDULER_URL=http://localhost:38085
export ZMON_EVENTLOG_URL=http://localhost:38081
export ZMON_SIGNUP_GITHUB_ALLOWED_USERS=*
export ZMON_SIGNUP_GITHUB_ALLOWED_ORGAS=*

java -Xmx240m -jar zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar
