FROM registry.opensource.zalan.do/stups/openjdk:latest

EXPOSE 8443

COPY zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar /zmon-controller.jar
COPY private-libs private-libs

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) $(appdynamics-agent) -cp /zmon-controller.jar -Dopentracing.provider=lightstep -Dloader.path=private-libs/ org.springframework.boot.loader.PropertiesLauncher
