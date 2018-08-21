FROM registry.opensource.zalan.do/stups/openjdk:latest

EXPOSE 8443

COPY zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar /zmon-controller.jar
COPY jvm.java.security /jvm.java.security

CMD java -Djava.security.properties=jvm.java.security $JAVA_OPTS $(java-dynamic-memory-opts) $(appdynamics-agent) -jar /zmon-controller.jar
