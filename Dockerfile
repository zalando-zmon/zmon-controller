FROM registry.opensource.zalan.do/stups/openjdk:latest

EXPOSE 8443 8778

RUN mkdir -p /app/lib
RUN curl -o /app/lib/jolokia-jvm-1.6.2-agent.jar -v https://repo1.maven.org/maven2/org/jolokia/jolokia-jvm/1.6.2/jolokia-jvm-1.6.2-agent.jar
ENV JOLOKIA_AGENT="-javaagent:/app/lib/jolokia-jvm-1.6.2-agent.jar=port=8778,host=0.0.0.0"

COPY zalando-apis /zalando-apis

COPY zmon-controller-app/target/zmon-controller-1.0.1-SNAPSHOT.jar /zmon-controller.jar

CMD java $JAVA_OPTS $(java-dynamic-memory-opts) $JOLOKIA_AGENT -jar /zmon-controller.jar