###HOW TO USE BOOTMEUP-Branch

####Build

```
git clone https://github.com/zalando/zmon-controller.git

cd zmon-controller

git checkout bootMeUp

./mvnw clean install
```

####Run

Make sure the provided Vagrant-Box is up and all services are running.

```

java -Dspring.profiles.active=github -jar zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar

```
