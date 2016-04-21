FROM java:8-jre

MAINTAINER Alan van Dam <avandam@zilverline.com>

EXPOSE 8080
ADD target/sos-server-0.2.3-SNAPSHOT-jar-with-dependencies.jar /opt/sos/sos-server.jar
WORKDIR /opt/sos

CMD ["java", "-jar", "sos-server.jar"]
