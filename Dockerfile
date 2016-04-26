FROM registry.opensource.zalan.do/stups/openjdk:8u77-b03-1-20

EXPOSE 8443

COPY zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar /zmon-controller.jar
COPY target/scm-source.json /

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) -jar /zmon-controller.jar
